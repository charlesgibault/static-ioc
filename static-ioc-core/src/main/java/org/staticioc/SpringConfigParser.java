/**
 *  Copyright (C) 2012 Charles Gibault
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
import org.staticioc.model.*;
import org.staticioc.model.Bean.Scope;
import org.staticioc.parser.*;
import org.staticioc.parser.plugins.*;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class SpringConfigParser extends AbstractSpringConfigParser
{
	private final DocumentBuilderFactory dbf;
	private final DocumentBuilder db;
	private final XPathFactory xPathFactory; 
	
	private final XPathExpression xBeans;
	private final XPathExpression xProps;
	private final XPathExpression xImports;
	
	//TODO put that into a PluginChain
	private final Collection<AttributesParserPlugin> attributesParserPlugins = new LinkedList<AttributesParserPlugin>();
	private final Map<String, NodeParserPlugin> nodesParserPlugins = new ConcurrentHashMap<String, NodeParserPlugin>();	
	
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
		
		try
		{
			final XPath xPathBeans  = xPathFactory.newXPath();
			xBeans = xPathBeans.compile(XPATH_BEAN);

			final XPath xPathProperties  = xPathFactory.newXPath();
			xProps = xPathProperties.compile(XPATH_PROPERTY);

			final XPath xPathImports  = xPathFactory.newXPath();
			xImports = xPathImports.compile(XPATH_IMPORT);
		}
		catch( XPathExpressionException e )
		{
			throw new ParserConfigurationException( "Error compiling XPaths :" + e.getMessage() );
		}
		
		// Load plugins
		AttributesParserPlugin springPPluging = new SpringPParserPlugin();
		springPPluging.setBeanContainer(this);
		attributesParserPlugins.add( springPPluging );
		
		NodeParserPlugin listPlugin = new ListPlugin();
		NodeParserPlugin setPlugin = new SetPlugin();
		NodeParserPlugin mapPlugin = new MapPlugin();
		NodeParserPlugin propertiesPlugin = new PropertiesPlugin();
		
		listPlugin.setBeanContainer(this);
		setPlugin.setBeanContainer(this);
		mapPlugin.setBeanContainer(this);
		propertiesPlugin.setBeanContainer(this);
		
		nodesParserPlugins.put(listPlugin.getSupportedNode(), listPlugin);
		nodesParserPlugins.put(setPlugin.getSupportedNode(), setPlugin);
		nodesParserPlugins.put(mapPlugin.getSupportedNode(), mapPlugin);
		nodesParserPlugins.put(propertiesPlugin.getSupportedNode(), propertiesPlugin);
	}
	
	protected String populateBeansMap( final Node beanNode ) throws XPathExpressionException
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
			id = generateAnonymousBeanId(); 
			isAnonymous = true;
		}
			
		// Check if this node has a parent
		final Node parentNode = beanAttributes.getNamedItem(PARENT);
		
		Bean bean = null;
		
		if (parentNode != null )
		{
			final String parentName = parentNode.getNodeValue();
			
			// Test direct resolution (parent already registered)
			final Bean parentBean = getBean( parentName );
			
			if( parentBean == null) // parent not known yet
			{
				registerParent( new ParentDependency( parentName, id, alias, isAnonymous,  beanNode) );
				return id;
			}
			else
			{
				// Perform bean copy and continues
				bean = duplicateBean( id, alias, parentBean, isAnonymous );
			}
		}
		
		return processBeanNode(beanNode, beanAttributes, id, alias, isAnonymous, bean);
	}

	@Override
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
		
		// read all kind of properties
		
		// Specific attributes plugin
		for( AttributesParserPlugin plugin: attributesParserPlugins )
		{
			plugin.handleAttributes(bean, beanAttributes);
		}
		
		// Enrich bean with constructor args
		handleConstructorArgs( bean, beanNode);
						
		// Handle properties
		final NodeList propsRef = (NodeList) xProps.evaluate(beanNode, XPathConstants.NODESET);
		handleBeanProperties(bean, propsRef);
		
		// register Bean in Map
		register( bean );
		
		return id;
	}
	
	/**
	 * Parse the XML <bean/> nodes's children for constructor arguments and enrich the Bean object accordingly
	 * @param bean
	 * @param beanNode
	 * @throws XPathExpressionException 
	 */
	protected void handleConstructorArgs( final Bean bean, final Node beanNode ) throws XPathExpressionException
	{
		final Collection<Node> constArgNodes = ParserHelper.extractNodesByName(beanNode.getChildNodes(), CONSTRUCTOR_ARGS);
		
		final Property[] args = new Property[ constArgNodes.size() ];
		
		int autoIndex=0;
		
		for ( Node node : constArgNodes )
		{
			final NamedNodeMap constAttributes = node.getAttributes();
			
			// Retrieve index
			final Node indexNode = constAttributes.getNamedItem(INDEX);
			final String indexValue = ( indexNode != null )? indexNode.getNodeValue() : null;
			
			try
			{
				final int index = ( indexNode != null )? Integer.valueOf( indexValue ):autoIndex++;
				final String argumentPropName = CONSTRUCTOR_ARGS + index;
				
				//handle case where value is set as an attribute 
				Property argumentProp = handleValueRefAttributes(argumentPropName, node );
				
				if( argumentProp == null) // Argument defined as a sub node
				{
					final Node argumentNode = node.getFirstChild();
					if( argumentNode == null)
					{
						logger.warn("Ignoring constructor argument {} with neither attribute not node value for bean {}", index, bean.getId());
						continue;
					}
				
					argumentProp = handleSubProp( argumentNode, argumentPropName );
				}
				
				if (argumentProp != null)
				{
					args[index] = argumentProp; // collect arguments in disorder (potentially)

					final Node typeNode = constAttributes.getNamedItem(TYPE);
					if( typeNode != null)
					{
						argumentProp.setType( typeNode.getNodeValue() );
					}
				}
			}
			catch(NumberFormatException e)
			{
				logger.warn( "Cannot parse constructor argument index {}", (indexValue!=null)?indexValue:autoIndex );
			}
			catch( IndexOutOfBoundsException e )
			{
				logger.warn( "Out of bound constructor argument index {}. Should be < {}" , (indexValue!=null)?indexValue:autoIndex, constArgNodes.size() );
			}

		}
		
		// Now reorder them.
		for( Property prop : args)
		{
			if( prop != null)
			{
				logger.debug("Constructor arg : {}", prop.toString());
				addOrReplaceProperty( prop, bean.getConstructorArgs() );
			}
		}
	}
	
	protected Property handleValueRefAttributes(final String propName, final Node node)
	{
		Property prop=null;
		final NamedNodeMap propAttributes = node.getAttributes();
		
		final Node valueNode = propAttributes.getNamedItem(VALUE);
		final String value = (valueNode != null ) ? valueNode.getNodeValue() : null;

		final Node refNode = propAttributes.getNamedItem(REF);
		final Node idRefNode = propAttributes.getNamedItem(IDREF);
		final String ref = (refNode != null ) ? refNode.getNodeValue() : ((idRefNode != null ) ? idRefNode.getNodeValue() : null);

		if ( value != null || ref != null ) // Simple property : value or reference
		{
			prop  = new Property( propName, value, ref);
			
			// Handle type specified as an attribute
			if( value != null )
			{
				final Node typeNode = propAttributes.getNamedItem(TYPE);
				if( typeNode != null)
				{
					prop.setType( typeNode.getNodeValue() );
				}
			}
		}
		return prop;
	}
	
	protected void handleBeanProperties( final Bean bean, final NodeList propsRef)
			 throws XPathExpressionException
	{
		for( int p = 0 ; p< propsRef.getLength() ; ++p )
		{
			final Node node = propsRef.item( p );
			final NamedNodeMap propAttributes = node.getAttributes();
			
			final Node nameNode = propAttributes.getNamedItem(NAME);
			
			if(nameNode != null ) // Ignore properties with no name
			{
				final String propName = nameNode.getNodeValue();
				Property prop = handleValueRefAttributes(propName, node );
				
				if ( prop != null ) // Simple property : value or reference
				{
					addOrReplaceProperty(prop, bean.getProperties() );
				}
				else
				{
					handleSubProps( bean, propName, node.getChildNodes());				
				}
			}
		}
	}
	
	/**
	 * Handle all bean's properties nodes
	 * @param bean : reference to the bean owning this attribute
	 * @param propName name of the property being inspected
	 * @param propChilds attributes of the property node
	 * @throws XPathExpressionException
	 */
	@Override
	public void handleSubProps( final Bean bean, final String propName, final NodeList propChilds) throws XPathExpressionException
	{
		for (int sp = 0 ; sp < propChilds.getLength() ; ++sp )
		{
			final Node spNode = propChilds.item( sp );
			
			Property prop = handleSubProp(  spNode, propName );
			if (prop != null )
			{
				addOrReplaceProperty(prop, bean.getProperties() );
			}
		}
	}
	
	/**
	 * Handle the following node : bean | ref | idref | list | set | map | props | value | null
	 * @param spNode is the XML node representing the properties
	 * @param propName is the name of the property for the parent bean
	 * @return a Property representing the appropriate object association for parent bean
	 * @throws XPathExpressionException
	 */
	@Override
	public Property handleSubProp( final Node spNode, final String propName )
			 throws XPathExpressionException
	{
		final String spNodeName = spNode.getNodeName();
		
		if( nodesParserPlugins.containsKey( spNodeName) )
		{
			NodeParserPlugin plugin = nodesParserPlugins.get(spNodeName);
			return plugin.handleProperty(spNode, propName);
		}
		else if ( spNodeName.equals( REF ) || spNodeName.equals( IDREF ) ) // Look at children named ref / idref
		{
			final NamedNodeMap spNodeAttributes = spNode.getAttributes();
			final Node refNode = spNodeAttributes.getNamedItem(BEAN);
		
			if( refNode != null) // handle <ref bean="">
			{
				return ParserHelper.getRef( propName, refNode.getNodeValue() );
			} 
			else //Handle <ref>value</ref>
			{
				return ParserHelper.getRef( propName, ParserHelper.extractFirstChildValue(spNode) );
			}
		}
		else if ( spNodeName.equals( VALUE ) ) // Look at children named value
		{						
			final Property res =  ParserHelper.getVal( propName, ParserHelper.extractFirstChildValue(spNode) ); 
			
			// Handle type specified as an attribute
			final NamedNodeMap spNodeAttributes = spNode.getAttributes();
			final Node typeNode = spNodeAttributes.getNamedItem(TYPE);
			if( typeNode != null)
			{
				res.setType( typeNode.getNodeValue() );
			}

			return res;
		}
		else if  ( spNodeName.equals( BEAN ) ) // anonymous bean
		{
			// recursively create bean
			final String subBeanName = populateBeansMap( spNode );
			
			if (subBeanName != null) // Wire this bean as a reference
			{
				return ParserHelper.getRef( propName, subBeanName );
			}
		}
		else if  ( spNodeName.equals( NULL ) ) // null value
		{
			return new Property( propName, null, null );
		}
		return null;
	}

	public Map<String, Bean> load( Collection<String> configurationFiles) throws SAXException, IOException
	{
		final Set<String> loadedContext = new HashSet<String>();

		// Start by loading each configuration file without resolving beans
		for( String config : configurationFiles )
		{
			load( config, loadedContext, false );
		}

		// Now resolve every beans
		try
		{
			resolveParentDefinition();
			resolvePrototypeBeans();
		}
		catch( XPathExpressionException e)
		{
			logger.error( "Error parsing files" + configurationFiles.toString(), e );
		}

		return getBeans();
	}

	
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
	protected Map<String, Bean> load( String configurationFile, Set<String> loadedContext, boolean resolveBean) throws SAXException, IOException
	{
		// Start by keeping track of loaded file (after normalizing its name)
		configurationFile = FilenameUtils.concat( FilenameUtils.getFullPath( configurationFile ), FilenameUtils.getName( configurationFile ) );
		loadedContext.add( configurationFile);
		
		final Document confFileDom = db.parse( configurationFile );
		
		try
		{
			final NodeList importsRef = (NodeList) xImports.evaluate(confFileDom, XPathConstants.NODESET);
			final NodeList beansRef = (NodeList) xBeans.evaluate(confFileDom, XPathConstants.NODESET);
			// TODO plug special namespace plugins here

			// Start by recursively resolve all imports
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
						final String fullPath = FilenameUtils.getFullPath( configurationFile ) ; 
						importedFileName = FilenameUtils.concat(fullPath, importedFileName);
					}
					
					// Check for infinite loops
					if ( !loadedContext.contains( importedFileName ) )
					{
						SpringConfigParser subParser = new SpringConfigParser();
						Map<String,Bean> importedBeans = subParser.load( importedFileName, loadedContext, false );
						
						register( importedBeans );
						register( subParser ); // handle parents (in case of cross file parent/child relationship), prototype listing and reference tracking
					}
				}
			}
			
			// the parse bean definition 
			for( int i = 0 ; i< beansRef.getLength() ; ++i )
			{
				populateBeansMap( beansRef.item( i ) );
			}
			
			// resolve parent definition that couldn't be resolved in the first pass.
			if( resolveBean)
			{
				resolveParentDefinition();
				resolvePrototypeBeans();
			}
			
			// Finally resolve alias references
			resolveAliasReference();
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
			for( String beanName : getBeans().keySet() )
			{
				logger.debug( "Registered bean {}", beanName);
			}
		}
	
		return getBeans();
	}


	//TODO add more control on target class : inheritance, lifecycle
	//TODO add (partial) support for PropertyPlaceHolders
	//TODO add support for init-method and a init() method
	//TODO add support for depends-on (in init-method)
	//TODO add support for destroy-method and a teardDown() method ?	
	//TODO refactor this class : split it between Delegate that are responsible for handling a particular type of element?
}
