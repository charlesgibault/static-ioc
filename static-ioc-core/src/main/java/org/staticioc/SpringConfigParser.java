/**
 *  Copyright (C) 2013 Charles Gibault
 *
 *  Static IoC - Compile XML based inversion of control configuration file into a single init class, for many languages.
 *  Project Home : http://code.google.com/p/static-ioc/
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.staticioc;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.staticioc.container.BeanContainer;
import org.staticioc.container.ExtendedBeanContainer;
import org.staticioc.container.BeanContainerImpl;
import org.staticioc.model.Bean;
import org.staticioc.model.Property;
import org.staticioc.parser.*;
import org.staticioc.parser.namespace.spring.beans.SpringBeansNameSpaceParser;
import org.staticioc.parser.namespace.spring.p.SpringPNameSpaceParser;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

//TODO add (partial) support for PropertyPlaceHolders
public class SpringConfigParser implements ParserConstants, BeanParser
{
	protected static final Logger logger = LoggerFactory.getLogger(SpringConfigParser.class);
	
	private final DocumentBuilderFactory dbf;
	private final DocumentBuilder db;
	private final XPathFactory xPathFactory; 

	private final XPathExpression xBeans;
	private final XPathExpression xImports;
	private final XPathExpression xBeanRoot;

	// Plugin chains
	private final Collection<NodeParserPlugin> nodeParserPlugins = new LinkedList<NodeParserPlugin>();
	private final Map<String, NodeSupportPlugin> nodesSupportPlugins = new ConcurrentHashMap<String, NodeSupportPlugin>();
	private final ExtendedBeanContainer beanContainer;
	
	private final List<NamespaceParser> namespaceParsers; 
	
	// Prefix for the Spring beans namespace
	private String prefix = "";

	public SpringConfigParser() throws ParserConfigurationException
	{
		// Init DocumentBuilderFactory
		dbf = DocumentBuilderFactory.newInstance();

		dbf.setNamespaceAware(false);
		dbf.setValidating( false );
		dbf.setFeature("http://xml.org/sax/features/external-general-entities",  false );
		dbf.setFeature("http://xml.org/sax/features/external-parameter-entities",  false );
		dbf.setFeature("http://apache.org/xml/features/nonvalidating/load-dtd-grammar",  false );
		dbf.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd",  false );
		dbf.setFeature("http://xml.org/sax/features/use-entity-resolver2", false);

		db = dbf.newDocumentBuilder();

		xPathFactory = XPathFactory.newInstance();
		
		beanContainer = new BeanContainerImpl();

		namespaceParsers = new LinkedList<NamespaceParser>();
		addNamespaceParser(  new SpringBeansNameSpaceParser() );
		addNamespaceParser(  new SpringPNameSpaceParser() );
		
		try
		{
			final XPath xPathBeans  = xPathFactory.newXPath();
			xBeans = xPathBeans.compile(XPATH_BEAN);

			final XPath xPathImports  = xPathFactory.newXPath();
			xImports = xPathImports.compile(XPATH_IMPORT);

			final XPath xPathBeanRoot  = xPathFactory.newXPath();
			xBeanRoot = xPathBeanRoot.compile(XPATH_BEANS_NODE);
		}
		catch( XPathExpressionException e )
		{
			throw new ParserConfigurationException( "Error compiling XPaths :" + e.getMessage() );
		}
	}

	@Override
	public XPathFactory getXPathFactory() {
		return xPathFactory;
	}

	/**
	 * Handle a list of node 
	 * @param nodes List of nodes to process
	 * @throws XPathExpressionException
	 */
	protected void handleNodes( final NodeList nodes) throws XPathExpressionException
	{
		handleNodes( null, "", nodes);
	}
	
	/**
	 * Handle all bean's properties nodes
	 * @param bean : reference to the bean owning this attributes
	 * @param propName name of the property being inspected
	 * @param nodes list to process
	 * @throws XPathExpressionException
	 */
	@Override
	public void handleNodes( final Bean bean, final String propName, final NodeList nodes) throws XPathExpressionException
	{
		for (int sp = 0 ; sp < nodes.getLength() ; ++sp )
		{
			final Node spNode = nodes.item( sp );

			Property prop = handleNode(  spNode, propName );
			if (prop != null && bean != null)
			{
				beanContainer.addOrReplaceProperty(prop, bean.getProperties() );
			}
		}
	}

	/**
	 * Handle the following node :
	 * @param node is the XML node representing the properties
	 * @param propName is the name of the property for the parent bean
	 * @return a Property representing the appropriate object association for parent bean
	 * @throws XPathExpressionException
	 */
	@Override
	public Property handleNode( final Node node, final String propName ) throws XPathExpressionException
	{		
		if( nodesSupportPlugins.containsKey( node.getNodeName()) )
		{
			NodeSupportPlugin plugin = nodesSupportPlugins.get(node.getNodeName());
			return plugin.handleNode(node, propName);
		}
		
		logger.trace("Unsupported node type {}. Ignoring", node.getNodeName() );
		return null;
	}

	/**
	 * Load a set of configuration files
	 * @param configurationFiles
	 * @return a Map<Bean's Id, Bean>  containing all loaded Beans, fully resolved
	 * @throws SAXException
	 * @throws IOException
	 */
	public Map<String, Bean> load( Collection<String> configurationFiles) throws SAXException, IOException
	{
		final Set<String> loadedContext = new HashSet<String>();

		// Start by loading each configuration file without resolving beans
		for( String config : configurationFiles )
		{
			load( config, loadedContext, false );
		}

		// Now resolve every beans
		beanContainer.resolveParentDefinition();
		beanContainer.resolveReferences();

		return beanContainer.getBeans();
	}


	/**
	 * Load a single configuration file
	 * @param configurationFile
	 * @return a Map<Bean's Id, Bean>  containing all loaded Beans, fully resolved
	 * @throws SAXException
	 * @throws IOException
	 */
	public Map<String, Bean> load( String configurationFile) throws SAXException, IOException
	{
		return load( configurationFile, new HashSet<String>(), true );
	}

	/**
	 * Load a single configuration file with or without resolving Beans 
	 * @param entryFile to load
	 * @param loadedContext Set to track all loaded context to avoid infinite looping should configuration files be incorrects
	 * @param resolveBean
	 * @return a Map<Bean's Id, Bean>  containing all loaded Beans, resolved or not depending on resolveBean parameter value
	 * @throws SAXException
	 * @throws IOException
	 */
	private Map<String, Bean> load( String entryFile, Set<String> loadedContext, boolean resolveBean) throws SAXException, IOException
	{
		// Start by keeping track of loaded file (after normalizing its name)
		final String fullPath = FilenameUtils.getFullPath( entryFile ) ; 
		final String configurationFile = FilenameUtils.concat( fullPath, FilenameUtils.getName( entryFile ) );
		loadedContext.add( configurationFile);

		final Document confFileDom = db.parse( configurationFile );

		try
		{
			logger.debug( "Starting loading of {}", configurationFile);
			
			// Extract namespaces and load matching namespace plugins
			final NodeList beansRoot = (NodeList) xBeanRoot.evaluate(confFileDom, XPathConstants.NODESET);

			Map<String, String> namespaces = ParserHelper.extractNamespacePrefix(beansRoot);
			logger.debug( "XML namespaces prefix mapping: {}", namespaces);
			
			for( NamespaceParser namespaceParser : namespaceParsers)
			{
				if( namespaces.containsKey( namespaceParser.getNameSpaceUri() ) )
				{
					prefix = namespaces.get( namespaceParser.getNameSpaceUri() );
					namespaceParser.setPrefix( prefix );
					logger.debug("Namespace {}. Adding {} node parser plugins", namespaceParser.getNameSpaceUri(), namespaceParser.getNodeParserPlugins().size() );
					
					nodeParserPlugins.addAll( namespaceParser.getNodeParserPlugins() );
					
					for( NodeSupportPlugin plugin : namespaceParser.getNodeSupportPlugins() )
					{
						final String supportedNode = plugin.getSupportedNode();
						logger.debug("Namespace {}. Adding node support plugin for {} nodes", namespaceParser.getNameSpaceUri(), supportedNode );
						
						nodesSupportPlugins.put(supportedNode, plugin);
					}
				}
			}
				
			// Now start loading imports and beans
		
			final NodeList importsRef = (NodeList) xImports.evaluate(confFileDom, XPathConstants.NODESET);
			final NodeList beansRef = (NodeList) xBeans.evaluate(confFileDom, XPathConstants.NODESET);
			
			// Start by recursively resolve all imports
			resolveImports(importsRef, loadedContext, fullPath);

			// the parse bean definition 
			handleNodes( beansRef);

			// resolve parent definition that couldn't be resolved in the first pass.
			if( resolveBean)
			{
				beanContainer.resolveParentDefinition();
			}

			// Finally resolve alias references
			beanContainer.resolveReferences();
		}
		catch( XPathExpressionException e)
		{
			logger.error( "Error parsing file " + configurationFile, e );
		}
		catch( ParserConfigurationException e )
		{
			logger.error( "Unexpected error loading " + configurationFile, e );
		}

		if( logger.isDebugEnabled() )
		{
			for( String beanName : beanContainer.getBeans().keySet() )
			{
				logger.debug( "Registered bean {}", beanName);
			}
		}

		return beanContainer.getBeans();
	}

	/**
	 * Recursively resolves a list of import directive and enrich the loadedContext
	 * @param importsRef
	 * @param loadedContext
	 * @param fullPath
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 * @throws IOException
	 */
	protected void resolveImports(final NodeList importsRef, final Set<String> loadedContext, final String fullPath) throws ParserConfigurationException, SAXException, IOException
	{
		for( int i = 0 ; i< importsRef.getLength() ; ++i )
		{
			final Node importNode = importsRef.item( i );
			final NamedNodeMap ImportAttributes = importNode.getAttributes();
			final Node resourceNode = ImportAttributes.getNamedItem(RESOURCE);

			if( resourceNode != null)
			{
				String importedFileName = resourceNode.getNodeValue();
				final File importedFilePath = new File(importedFileName);

				if ( !importedFilePath.isAbsolute() ) // handle relative paths
				{
					importedFileName = FilenameUtils.concat(fullPath, importedFileName);
				}

				// Check for infinite loops
				if ( !loadedContext.contains( importedFileName ) )
				{
					SpringConfigParser subParser = new SpringConfigParser();
					logger.debug( "Importing {}", importedFileName);
					Map<String,Bean> importedBeans = subParser.load( importedFileName, loadedContext, false );

					beanContainer.register( importedBeans );
					beanContainer.register( subParser.beanContainer ); // handle parents (in case of cross file parent/child relationship), prototype listing and reference tracking
				}
			}
		}
	}
	
	@Override
	public BeanContainer getBeanContainer() {
		return beanContainer;
	}

	@Override
	public Collection<NodeParserPlugin> getNodeParserPlugins() {
		return nodeParserPlugins;
	}

	public void addNamespaceParsers( List<NamespaceParser> plugins )
	{
		for ( NamespaceParser plugin : plugins )
		{
			addNamespaceParser( plugin );
		}
	}
	
	public void addNamespaceParser( NamespaceParser plugin )
	{
		if( plugin != null)
		{
			namespaceParsers.add( plugin );
			plugin.setBeanParser( this );
		}
	}
}
