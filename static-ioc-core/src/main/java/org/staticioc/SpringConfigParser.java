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
import org.staticioc.container.ResolvedBeanCallback;
import org.staticioc.dependency.DefinitionDependency;
import org.staticioc.dependency.FactoryBeanDependency;
import org.staticioc.dependency.RunTimeDependency;
import org.staticioc.model.Bean;
import org.staticioc.model.Property;
import org.staticioc.model.Bean.Scope;
import org.staticioc.parser.*;
import org.staticioc.parser.namespace.*;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

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

	protected Map<String, String> extractNamespacePrefix( final Document confFileDom ) throws XPathExpressionException
	{
		final Map<String, String> namespacePrefix = new ConcurrentHashMap<String, String> ();

		final NodeList beansRoot = (NodeList) xBeanRoot.evaluate(confFileDom, XPathConstants.NODESET);

		for( int i = 0 ; i < beansRoot.getLength() ; ++i)
		{
			Node beansNode = beansRoot.item(i);
			NamedNodeMap attributes = beansNode.getAttributes();

			for( int a = 0 ; a < attributes.getLength(); ++a )
			{
				final Node attribute = attributes.item(a);
				final String attributeValue = attribute.getNodeName();

				if( attributeValue != null && attributeValue.startsWith(XML_NAMESPACE_DEF) ) // its a namespace declaration
				{
					final String schemaUrl = attribute.getNodeValue();					
					String prefix  = "";
					
					if( attributeValue.contains(":") ) // prefix
					{
						prefix = attributeValue.split(":")[1];
					}
					
					logger.debug( "schemaUrl {} has prefix {} ", schemaUrl, prefix );
					namespacePrefix.put(schemaUrl, prefix);
				}
			}
		}
		
		return namespacePrefix;
	}

	protected String createBean( final Node beanNode ) throws XPathExpressionException
	{
		if (beanNode == null) // if Bean doesn't exist, there's nothing to do
		{
			return null;
		}

		final NamedNodeMap beanAttributes = beanNode.getAttributes();

		// use Id if defined, name otherwise (if defined), auto-generated bean name otherwise.
		final Node idNode = beanAttributes.getNamedItem(ID);
		final Node bNameNode = beanAttributes.getNamedItem(NAME);

		String id = (idNode != null )? idNode.getNodeValue() : (bNameNode != null )? bNameNode.getNodeValue() : null;
		final String alias = ( idNode != null  && bNameNode != null)? bNameNode.getNodeValue(): null;

		boolean isAnonymous = false;
		if( id == null )
		{
			id = beanContainer.generateAnonymousBeanId(); 
			isAnonymous = true;
		}

		// Check if this node has a parent
		final Node parentNode = beanAttributes.getNamedItem(PARENT);

		Bean bean = null;

		if (parentNode != null )
		{
			final String parentName = parentNode.getNodeValue();

			// Test direct resolution (parent already registered)
			final Bean parentBean = beanContainer.getBean( parentName );

			if( parentBean == null) // parent not known yet
			{
				beanContainer.registerParent( new DefinitionDependency( parentName, id, alias, isAnonymous,  beanNode) );
				return id;
			}
			else
			{
				// Perform bean copy and continues
				bean = beanContainer.duplicateBean( id, alias, parentBean, isAnonymous );
			}
		}

		// Perform actual node content processing
		return processBeanNode(beanNode, beanAttributes, id, alias, isAnonymous, bean);
	}
	
	/**
	 * Process a <bean/> node's content.
	 */
	protected String processBeanNode(final Node beanNode, final NamedNodeMap beanAttributes, final String id, final String alias, boolean isAnonymous, Bean bean) throws XPathExpressionException
	{
		String className = (bean != null)? bean.getClassName() : null;
		boolean abstractBean = false;

		final Node classNode = beanAttributes.getNamedItem(CLASS);
		if ( classNode != null )
		{
			className = classNode.getNodeValue();
		}

		final Node abstractNode = beanAttributes.getNamedItem( ABSTRACT );
		if( abstractNode != null )
		{
			abstractBean = Boolean.valueOf( abstractNode.getNodeValue() );
		}

		final Node scopeNode = beanAttributes.getNamedItem(SCOPE);
		final Node singletonNode = beanAttributes.getNamedItem(SINGLETON);

		Scope scope = Scope.SINGLETON;
		if( scopeNode != null && PROTOTYPE.equalsIgnoreCase( scopeNode.getNodeValue() ) 
				|| singletonNode != null && !Boolean.valueOf( singletonNode.getNodeValue() ) )
		{
			scope = Scope.PROTOTYPE;
		}

		logger.debug( "id {} scope {} ", id, scope );

		//Handle Factory Bean/Method here
		String factoryBean=(bean != null)? bean.getFactoryBean() : null;
		String factoryMethod=(bean != null)? bean.getFactoryMethod() : null;

		final Node factoryBeanNode = beanAttributes.getNamedItem(FACTORY_BEAN);
		final Node factoryMethodNode = beanAttributes.getNamedItem(FACTORY_METHOD);
		if ( factoryBeanNode != null )
		{
			factoryBean = factoryBeanNode.getNodeValue();
		}
		if ( factoryMethodNode != null )
		{
			factoryMethod = factoryMethodNode.getNodeValue();
		}

		// Handle init/destroy methods here:
		String initMethod=(bean != null)? bean.getInitMethod() : null;
		String destroyMethod=(bean != null)? bean.getDestroyMethod() : null;

		final Node initMethodBeanNode = beanAttributes.getNamedItem(INIT_METHOD);
		final Node destroyMethodNode = beanAttributes.getNamedItem(DESTROY_METHOD);
		if ( initMethodBeanNode != null )
		{
			initMethod = initMethodBeanNode.getNodeValue();
		}
		if ( destroyMethodNode != null )
		{
			destroyMethod = destroyMethodNode.getNodeValue();
		}

		// Class is mandatory for non abstract beans
		if( className == null && !abstractBean)
		{
			logger.warn( "No class defined for bean {}. Skipping", id );
			return null;
		}

		if( bean == null )
		{
			bean = new Bean(id, className, isAnonymous );
			bean.setAlias(alias);
		}
		else
		{
			bean.setClassName(className); // possible override of the parent's class
			bean.setAlias(alias);
		}

		bean.setAbstract( abstractBean );
		bean.setScope( scope );

		bean.setFactoryBean(factoryBean);
		bean.setFactoryMethod(factoryMethod);

		if( factoryBean != null)
		{
			final RunTimeDependency factoryBeanDependency = new FactoryBeanDependency( id, factoryBean); // Factory-bean can refer to framework classes and thus must be "soft" 
			beanContainer.registerRunTimeDependency( factoryBeanDependency );
		}

		bean.setInitMethod(initMethod);
		bean.setDestroyMethod(destroyMethod);

		// Specific attributes plugin
		for( NodeParserPlugin plugin: nodeParserPlugins )
		{
			plugin.handleNode(bean, beanNode);
		}

		// register Bean in Map
		beanContainer.register( bean );

		return id;
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
			if (prop != null )
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
	public Property handleNode( final Node node, final String propName )
			throws XPathExpressionException
			{
		final String spNodeName = node.getNodeName();
		
		if( nodesSupportPlugins.containsKey( spNodeName) )
		{
			NodeSupportPlugin plugin = nodesSupportPlugins.get(spNodeName);
			return plugin.handleNode(node, propName);
		}
		else if ( ParserHelper.match( REF, spNodeName, prefix) || ParserHelper.match( IDREF, spNodeName, prefix) ) // Look at children named ref / idref
		{
			final NamedNodeMap spNodeAttributes = node.getAttributes();
			final Node refNode = spNodeAttributes.getNamedItem(BEAN);

			if( refNode != null) // handle <ref bean="">
			{
				return ParserHelper.getRef( propName, refNode.getNodeValue() );
			} 
			else //Handle <ref>value</ref>
			{
				return ParserHelper.getRef( propName, ParserHelper.extractFirstChildValue(node) );
			}
		}
		else if ( ParserHelper.match( VALUE, spNodeName, prefix) ) // Look at children named value
		{						
			final Property res =  ParserHelper.getVal( propName, ParserHelper.extractFirstChildValue(node) ); 

			// Handle type specified as an attribute
			final NamedNodeMap spNodeAttributes = node.getAttributes();
			final Node typeNode = spNodeAttributes.getNamedItem(TYPE);
			if( typeNode != null)
			{
				res.setType( typeNode.getNodeValue() );
			}

			return res;
		}
		else if  ( ParserHelper.match( BEAN, spNodeName, prefix) ) // anonymous bean
		{
			// recursively create bean
			final String subBeanName = createBean( node );

			if (subBeanName != null) // Wire this bean as a reference
			{
				return ParserHelper.getRef( propName, subBeanName );
			}
		}
		else if  ( ParserHelper.match( NULL, spNodeName, prefix) ) // null value
		{
			return new Property( propName, null, null );
		}
		return null;
	}

	/**
	 * Load a set of configuration files
	 * @param configurationFiles
	 * @return
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
		resolveParentDefinition();
		beanContainer.resolveReferences();


		return beanContainer.getBeans();
	}


	/**
	 * Load a single configuration file
	 * @param configurationFile
	 * @return
	 * @throws SAXException
	 * @throws IOException
	 */
	public Map<String, Bean> load( String configurationFile) throws SAXException, IOException
	{
		return load( configurationFile, new HashSet<String>(), true );
	}

	/**
	 * 
	 * @param configurationFile
	 * @param loadedContext Set to track all loaded context to avoid infinite looping should configuration files be incorrects
	 * @param resolveBean
	 * @return
	 * @throws SAXException
	 * @throws IOException
	 */
	protected Map<String, Bean> load( String entryFile, Set<String> loadedContext, boolean resolveBean) throws SAXException, IOException
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
			Map<String, String> namespaces = extractNamespacePrefix(confFileDom);
		
			
			// TODO make loading flexible enough to have a namespace plugin mechanism here
			SpringBeansNameSpaceParser springBeansParser = new SpringBeansNameSpaceParser();
			springBeansParser.setBeanParser(this);

			SpringPNameSpaceParser springPNameSpaceParser = new SpringPNameSpaceParser();
			springPNameSpaceParser.setBeanParser(this);

			if( namespaces.containsKey( springBeansParser.getNameSpaceUri() ) )
			{
				prefix = namespaces.get( springBeansParser.getNameSpaceUri() );
				springBeansParser.setPrefix( prefix );
				
				logger.debug("Namespace {}. Adding {} node parser plugins", springBeansParser.getNameSpaceUri(), springBeansParser.getNodeParserPlugins().size() );
				
				nodeParserPlugins.addAll( springBeansParser.getNodeParserPlugins() );
				
				for( NodeSupportPlugin plugin : springBeansParser.getNodeSupportPlugins() )
				{
					final String supportedNode = plugin.getSupportedNode();
					logger.debug("Namespace {}. Adding node support plugin for {} nodes", springBeansParser.getNameSpaceUri(), supportedNode );
					
					nodesSupportPlugins.put(supportedNode, plugin);
				}
			}
			else
			{
				logger.warn( "Main beans namespace ({}) is not included in {}", springBeansParser.getNameSpaceUri(), configurationFile );
			}
			
			if( namespaces.containsKey( springPNameSpaceParser.getNameSpaceUri() ) )
			{
				springPNameSpaceParser.setPrefix( namespaces.get( springPNameSpaceParser.getNameSpaceUri() ) );

				logger.debug("Namespace {}. Adding {} node parser plugins", springPNameSpaceParser.getNameSpaceUri(), springPNameSpaceParser.getNodeParserPlugins().size() );
				
				nodeParserPlugins.addAll( springPNameSpaceParser.getNodeParserPlugins() );

				for( NodeSupportPlugin plugin : springPNameSpaceParser.getNodeSupportPlugins() )
				{
					final String supportedNode = plugin.getSupportedNode();
					logger.debug("Namespace {}. Adding node support plugin for {} nodes", springBeansParser.getNameSpaceUri(), supportedNode );
					
					nodesSupportPlugins.put(supportedNode, plugin);
				}
			}
		
		
			final NodeList importsRef = (NodeList) xImports.evaluate(confFileDom, XPathConstants.NODESET);
			final NodeList beansRef = (NodeList) xBeans.evaluate(confFileDom, XPathConstants.NODESET);
			
			// Start by recursively resolve all imports
			resolveImports(importsRef, loadedContext, fullPath);

			// the parse bean definition 
			for( int i = 0 ; i< beansRef.getLength() ; ++i )
			{
				createBean( beansRef.item( i ) );
			}

			// resolve parent definition that couldn't be resolved in the first pass.
			if( resolveBean)
			{
				resolveParentDefinition();
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
	
	protected void resolveParentDefinition()
	{
		beanContainer.resolveParentDefinition(
				new ResolvedBeanCallback()
				{
					public String resolve(Bean bean, Node beanNode, NamedNodeMap beanAttributes, boolean isAnonymous ) throws XPathExpressionException
					{
						return processBeanNode(beanNode, beanAttributes, bean.getId(), bean.getAlias(), isAnonymous, bean);
					}
				} );
	}

	@Override
	public void addOrReplaceProperty(final Property prop, final Collection<Property> set) {
		beanContainer.addOrReplaceProperty( prop, set);
	}

	@Override
	public BeanContainer getBeanContainer() {
		return beanContainer;
	}

	//TODO add (partial) support for PropertyPlaceHolders
}
