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

/**
 * Static IoC - Compile XML based inversion of control configuration file into a single init class, for many languages.
 * Project Home : http://code.google.com/p/static-ioc/
 * 
 *   Copyright 2012 Charles Gibault
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.NavigableMap;
import java.util.Set;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;

import javax.xml.xpath.XPathExpressionException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.staticioc.model.Bean;
import org.staticioc.model.ParentDependency;
import org.staticioc.model.Property;
import org.staticioc.model.Bean.Scope;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public abstract class AbstractSpringConfigParser {

	protected static final Logger logger = LoggerFactory.getLogger(SpringConfigParser.class);
	protected static final String BEAN_PROPERTY_PREFIX = "p:";
	protected static final String BEAN_PROPERTY_REF_SUFFIX = "-ref";
	protected static final String XPATH_BEAN = "/beans/bean";
	protected static final String XPATH_PROPERTY = "property[@name]";
	protected static final String XPATH_IMPORT = "/beans/import[@resource]";
	protected static final String BEAN = "bean";
	protected static final String REF = "ref";
	protected static final String ID = "id";
	protected static final String IDREF = "idref";
	protected static final String CLASS = "class";
	protected static final String NAME = "name";
	protected static final String VALUE = "value";
	protected static final String TYPE = "type";
	protected static final String ABSTRACT = "abstract";
	protected static final String PARENT = "parent";
	protected static final String SCOPE = "scope";
	protected static final String PROTOTYPE = "prototype";
	protected static final String SINGLETON = "singleton";
	protected static final String FACTORY_BEAN = "factory-bean";
	protected static final String FACTORY_METHOD = "factory-method";
	protected static final String MAP = "map";
	protected static final String LIST = "list";
	protected static final String SET = "set";
	protected static final String PROPS = "props";
	protected static final String ENTRY = "entry";
	protected static final String KEY = "key";
	protected static final String KEY_REF = "key-ref";
	protected static final String VALUE_REF = "value-ref";
	protected static final String PROP = "prop";
	public static final String CONSTRUCTOR_ARGS = "constructor-arg";
	protected static final String INDEX = "index";
	protected static final String NULL = "null";
	protected static final String RESOURCE = "resource";
	
	public static final String ANONYMOUS_BEAN_PREFIX = "bean_";
	public static final String PROTOTYPE_BEAN_PREFIX = "prototyped_";
	private static int anonymousBeanIdentifier = 0;

	// Map a bean's id to the actual instanciated beans 
	private final NavigableMap<String,Bean> beans = new TreeMap<String,Bean>();
	
	// Map a bean's name (alias) to the actual registerd beans 
	private final Map<String,Bean> aliases = new HashMap<String,Bean>();
	
	// Map a bean
	private final Map<String, ParentDependency > parentDependencyMap = new ConcurrentHashMap<String, ParentDependency>();
	
	// Track prototypes Beans separately 
	private final Set<String> prototypeBeans = new LinkedHashSet<String>();
	
	// Map a bean name with a Collection of Properties referencing that Bean
	private final Map<String,Collection<Property>> propertyReferencesMap = new ConcurrentHashMap<String,Collection<Property>>();
	
	/**
	 * 
	 * @return
	 */
	protected String generateAnonymousBeanId() {
		return ANONYMOUS_BEAN_PREFIX + (++anonymousBeanIdentifier);
	}
	
	protected String generatePrototypeBeanId( String beanName) {
		return ANONYMOUS_BEAN_PREFIX + beanName + (++anonymousBeanIdentifier);
	}
	
	/**
	 * Add or replace a Property in a bean's property set
	 * Also updates a the Map of Property with a bean ref.
	 * 
	 * @param prop
	 * @param set
	 */
	protected void addOrReplaceProperty(final Property prop, final Collection<Property> set) {
		if( ! set.add( prop ) )
		{
			set.remove(prop);
			set.add(prop);
		}
		
		// Build a Map< Bean ref name -> Property> (used for Prototype resolution)
		registerPropertyReference( prop);
	}
	
	protected Bean duplicateBean( final String id, final String alias, final Bean parent, final boolean isAnonymous)
	{
		final Bean duplicatedBean = new Bean( id, parent, isAnonymous );
		duplicatedBean.setAlias(alias);
		
		 //Make sure inherited properties are tracked
		registerPropertiesReference( duplicatedBean );
		return duplicatedBean;
	}
	
	private void registerPropertyReference( final Property prop )
	{
		if( prop.getRef() != null )
		{
			Collection<Property> props = propertyReferencesMap.get( prop.getRef() );
			
			if (props == null )
			{
				props = new LinkedList<Property>();
				propertyReferencesMap.put( prop.getRef(), props );
			}
			
			props.add( prop );
		}
	}
	
	private void registerPropertiesReference( final Bean bean)
	{
		for( Property prop : bean.getProperties() )
		{
			registerPropertyReference( prop );
		}
		
		for( Property prop : bean.getConstructorArgs() )
		{
			registerPropertyReference( prop );
		}
	}

	/**
	 * Register a bean in the bean map
	 * @param bean to be registered
	 */
	protected void register(final Bean bean) {	
		if( logger.isDebugEnabled())
		{
			logger.debug( "Adding {}", bean );
		}
		
		beans.put( bean.getId(), bean );
		
		if( bean.getAlias() != null)
		{
			logger.debug( "Adding alias {} for {}", bean.getAlias(), bean.getId() );
			aliases.put( bean.getAlias(), bean );
		}
		
		if( bean.getScope().equals( Scope.PROTOTYPE ) )
		{
			logger.debug( "Referencing bean {} as prototype", bean );
			prototypeBeans.add( bean.getId() );
		}
	}

	/**
	 * Register a set of beans in the bean map
	 * @param beans
	 */
	protected void register(final Map<String, Bean> beans) {	
		for( final Bean bean : beans.values() )
		{
			register(bean);
		}
	}
	
	/**
	 * Register a bean in the bean map
	 * @param bean to be registered
	 */
	protected void registerParent( ParentDependency parent ) {	
		if( logger.isDebugEnabled())
		{
			logger.debug( "Adding {}", parent );
		}
		
		parentDependencyMap.put( parent.getId(), parent );
	}
	
	/**
	 * Register a parser's into this parser
	 * Merge parent dependencies, prototypeBeans and propertyReferencesMap
	 * @param bean to be registered
	 */
	protected void register( final AbstractSpringConfigParser parser) {	
		if( logger.isDebugEnabled())
		{
			logger.debug( "Adding {} parent dependencies", parser.parentDependencyMap );
			logger.debug( "Adding {} prototypes definitions", parser.prototypeBeans );
			logger.debug( "Adding {} Bean reference tracking", parser.propertyReferencesMap );
		}
		
		parentDependencyMap.putAll( parser.parentDependencyMap );
		prototypeBeans.addAll( parser.prototypeBeans );
		propertyReferencesMap.putAll( parser.propertyReferencesMap );
	}	
	
	/**
	 * Resolve a bean by Name
	 * @param name
	 * @return
	 */
	protected Bean getBean( final String id)
	{
		final Bean bean = beans.get(id);
		return (bean != null)? bean : aliases.get(id);
	}
	
	protected NavigableMap<String, Bean > getBeans()
	{
		return beans;
	}

	protected Property getVal(String propertyName, String value) {
		if ( value == null) { return null; }
		return new Property( propertyName, value, null );
	}

	protected Property getRef(String propertyName, String ref) {
		if ( ref == null) { return null; }
		return new Property( propertyName, null, ref );
	}

	/**
	 * Extract text value for nodes like <value>text</value>
	 * @param node
	 * @return
	 */
	protected String extractFirstChildValue(final Node node) {
		final Node valueSubNode = node.getFirstChild();
		
		if ( valueSubNode == null ) { return null; }
		return valueSubNode.getNodeValue();
	}

	/**
	 * Go through a node list a extract the first matching node given its name
	 * @param nodes
	 * @param name
	 * @return
	 */
	protected Node extractFirstNodeByName(final NodeList nodes, final String name) {
		if(nodes != null && name != null )
		{	
			for( int n = 0 ; n<nodes.getLength() ; ++n )
			{
				final Node node = nodes.item( n );
	
				if ( name.equals(node.getNodeName()) )
				{
					return node;
				}
			}
		}
		
		return null;
	}

	/**
	 * Go through a node list a extract the first matching node given its name
	 * @param nodes
	 * @param name
	 * @return
	 */
	protected Collection<Node> extractNodesByName(final NodeList nodes, final String name) {
		Collection<Node> result = new LinkedList<Node>();
		
		if(nodes != null && name != null )
		{	
			for( int n = 0 ; n<nodes.getLength() ; ++n )
			{
				final Node node = nodes.item( n );
	
				if ( name.equals(node.getNodeName()) )
				{
					result.add( node );
				}
			}
		}
		
		return result;
	}
	
	protected void resolveParentDefinition() throws XPathExpressionException
	{
		final Set<String> visitedParents = new HashSet<String>();
		
		for( String parentName : parentDependencyMap.keySet() )
		{
			resolveParentBean( parentName, visitedParents );
		}
	}
	
	/**
	 * Resolve parent bean dependencies using a depth first search (Cormen et al. / Tarjan)
	 * 
	 * @param parentName
	 * @throws XPathExpressionException 
	 */
	protected void resolveParentBean( final String name, final Set<String> visitedBeans ) throws XPathExpressionException
	{
		// Grab dependency
		final ParentDependency dependency = parentDependencyMap.get(name);
		
		if( dependency == null )
		{
			logger.warn( "Unresolved parent bean {}. Ignoring", name );
			return;
		}
		
		// Test direct resolution (parent already registered)
		Bean parentBean = getBean( dependency.getParentId() );
		
		if( parentBean == null) // parent cannot be resolved directly -> Do a depth first approach
		{
			// Maintain a set of visited parent to avoid infinite loops in cycles.
			// This works because our depth first approach visits each node only once O(N) complexity.
			visitedBeans.add( name );
			
			if ( !visitedBeans.contains( dependency.getParentId() ))
			{
				resolveParentBean( dependency.getParentId(), visitedBeans );
				parentBean = getBean( dependency.getParentId() );
				
				if( parentBean == null ) // Parent couldn't be resolved still (due to cycle)
				{
					logger.warn( "Unresolved parent bean {}. Ignoring", name );
					return;
				}
			}
			else
			{			
				logger.warn( "Cycle detected with bean {}", name );
				return;
			}
		}
		
		// Perform bean copy and continue
		final Bean bean = duplicateBean( dependency.getId(), dependency.getAlias(), parentBean, dependency.isAnonymous() );
		final Node beanNode = dependency.getNode();
		final NamedNodeMap beanAttributes = beanNode.getAttributes();
		//TODO remove from parentDependencyMap ?
		
		processBeanNode(beanNode, beanAttributes, bean.getId(), bean.getAlias(), dependency.isAnonymous(), bean);			
	}
	
	protected void resolveAliasReference()
	{
		logger.debug( "Resolving alias for {} beans", beans.size() );
		for( Bean bean : beans.values() )
		{
			for( Property prop : bean.getProperties() )
			{
				Bean aliasBean = aliases.get( prop.getRef() );
				if( aliasBean != null )
				{
					logger.debug( "Resolved alias {} for bean {}", prop.getRef(), bean.getId() );
					prop.setRef( aliasBean.getId() );
				}
			}
		}
	}
	
	protected void resolvePrototypeBeans()
	{
		for( String bean : propertyReferencesMap.keySet() )
		{
			if( prototypeBeans.contains(bean) )
			{
				logger.debug( "Resolving prototype bean {}", bean );	
				Collection<Property> props = propertyReferencesMap.get(  bean );
				
				for( Property prop : props )
				{
					injectPrototype( prop );
				}
				
				//clear prop mapping once for all
				props.clear();
			}
		}
	}

	private void injectPrototype( Property prop )
	{
		if ( prop.getRef() != null && prototypeBeans.contains( prop.getRef() ) )
		{
			Bean prototype = getBean( prop.getRef() );
			
			if( prototype != null)
			{
				//duplicate bean with a new identifier and scope it as a singleton
				Bean prototypedInstance = new Bean( generatePrototypeBeanId( prototype.getId() ), prototype, true );
				prototypedInstance.setScope( Scope.SINGLETON );

				// change reference name 
				prop.setRef( prototypedInstance.getId() );
				
				// register it
				register( prototypedInstance );
				
				// Update prop mapping 
				registerPropertyReference( prop );
			}
		}
	}

	abstract protected String processBeanNode(final Node beanNode, final NamedNodeMap beanAttributes, final String id, final String alias, boolean isAnonymous, Bean bean) throws XPathExpressionException;
}