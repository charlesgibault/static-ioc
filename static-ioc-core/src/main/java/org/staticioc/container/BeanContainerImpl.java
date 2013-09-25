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
package org.staticioc.container;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import javax.xml.xpath.XPathExpressionException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.staticioc.dependency.DefinitionDependency;
import org.staticioc.dependency.DependencyManager;
import org.staticioc.dependency.ResolvedDependencyCallback;
import org.staticioc.dependency.RunTimeDependency;
import org.staticioc.model.Bean;
import org.staticioc.model.Property;
import org.staticioc.model.Bean.Scope;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

public class BeanContainerImpl implements ExtendedBeanContainer
{
	protected static final Logger logger = LoggerFactory.getLogger(BeanContainerImpl.class);
	
	private static int anonymousBeanIdentifier = 0;

	// Map a bean's id to the actual instanciated beans 
	private final Map<String,Bean> beans = new ConcurrentHashMap<String,Bean>();
	
	// Map a bean's name (alias) to the actual registerd beans 
	private final Map<String,Bean> aliases = new HashMap<String,Bean>();

	private final DependencyManager<DefinitionDependency> parentDependencyManager = new DependencyManager<DefinitionDependency>();
	private final DependencyManager<RunTimeDependency> runTimeDependencyManager = new DependencyManager<RunTimeDependency>();
	
	// Track prototypes Beans separately 
	private final Set<String> prototypeBeans = new LinkedHashSet<String>();
	
	// Listing all Properties referencing a Bean
	private final List<Property> propertyReferences = new LinkedList<Property>();
	
	/**
	 * @return a unique generated Bean id to use for anonymous Beans' declaration
	 */
	@Override
	public String generateAnonymousBeanId() {
		return ANONYMOUS_BEAN_PREFIX + (++anonymousBeanIdentifier);
	}
	
	/**
	 * @return a unique generated Bean id to use for prototype Beans' declaration
	 */
	protected String generatePrototypeBeanId( String beanName) {
		return PROTOTYPE_BEAN_PREFIX + beanName + (++anonymousBeanIdentifier);
	}
	

	/**
	 * Add or replace a Property in a bean's property set
	 * Also updates a the Map of Property with a bean ref.
	 * 
	 * @param prop
	 * @param set
	 */
	@Override
	public void addOrReplaceProperty(final Property prop, final Collection<Property> set) {
		if( ! set.add( prop ) )
		{
			set.remove(prop);
			set.add(prop);
		}
		
		// Build a Map< Bean ref name -> Property> (used for Prototype resolution)
		registerPropertyReference( prop);
	}
	
	@Override
	public Bean duplicateBean( final String id, final String alias, final Bean parent, final boolean isAnonymous)
	{
		final Bean duplicatedBean = new Bean( id, parent, isAnonymous );
		duplicatedBean.setAlias(alias);
		
		 //Make sure inherited properties are tracked
		registerPropertiesReferences( duplicatedBean );
		return duplicatedBean;
	}
	
	private void registerPropertyReference( final Property prop )
	{
		if( prop.getRef() != null )
		{
			propertyReferences.add( prop );
		}
	}
	
	private void registerPropertiesReferences( final Bean bean)
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
	@Override
	public void register(final Bean bean) {	
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
	@Override
	public void register(final Map<String, Bean> beans) {	
		for( final Bean bean : beans.values() )
		{
			register(bean);
		}
	}
	
	/**
	 * Register a bean -> parent dependency
	 * @param dependency to be registered
	 */
	@Override
	public void registerParent( DefinitionDependency dependency ) {
		parentDependencyManager.register(dependency);
	}
	

	/**
	 * Register a bean -> parent dependency
	 * @param dependency to be registered
	 */
	@Override
	public void registerRunTimeDependency( RunTimeDependency dependency ) {
		runTimeDependencyManager.register(dependency);
	}
	
	/**
	 * Register a parser's into this parser
	 * Merge parent and runtime dependencies, prototypeBeans and propertyReferences
	 * @param bean to be registered
	 */
	@Override
	public void register( final ExtendedBeanContainer container)
	{
		if( logger.isDebugEnabled())
		{
			logger.debug( "Adding {} parent dependencies", container.getParentDependencyManager() );
			logger.debug( "Adding {} runtime dependencies", container.getRunTimeDependencyManager() );
			logger.debug( "Adding {} prototypes definitions", container.getPrototypeBeans() );
			logger.debug( "Adding {} Bean reference tracking", container.getPropertyReferences() );
			logger.debug( "Adding {} Bean alias dictionary", container.getAliases() );
		}

		parentDependencyManager.registerAll(container.getParentDependencyManager());
		runTimeDependencyManager.registerAll(container.getRunTimeDependencyManager());
		prototypeBeans.addAll( container.getPrototypeBeans() );
		propertyReferences.addAll( container.getPropertyReferences() );
		aliases.putAll( container.getAliases() );
	}	
	
	/**
	 * Resolve a bean by Name
	 * @param name
	 * @return
	 */
	@Override
	public Bean getBean( final String id)
	{
		final Bean bean = beans.get(id);
		return (bean != null)? bean : aliases.get(id);
	}
	
	@Override
	public Map<String, Bean > getBeans()
	{
		return beans;
	}

	/**
	 * Resolve all parent definitions
	 * @param callback to use to process a Bean
	 */
	@Override
	public void resolveParentDefinition(final ResolvedBeanCallback callback)
	{
		parentDependencyManager.resolveAllDependencies(this, new ResolvedDependencyCallback<DefinitionDependency> ()
		{
			@Override
			public void onResolvedDependency(DefinitionDependency dependency, SimpleBeanContainer container)
			{
				final String parentBeanId = dependency.getParentId();
				final Bean parentBean = container.getBean(parentBeanId);
				
				// Perform bean copy and continue
				final Bean bean = duplicateBean( dependency.getId(), dependency.getAlias(), parentBean, dependency.isAnonymous() );
				final Node beanNode = dependency.getNode();
				final NamedNodeMap beanAttributes = beanNode.getAttributes();
				
				try {
					callback.resolve( bean, beanNode, beanAttributes, dependency.isAnonymous() );
				} catch (XPathExpressionException e) {
					logger.error( "Error processing Bean " + bean.getId(), e );
				}
			}
				
		} );
	}
	
	@Override
	public LinkedHashSet<String> getOrderedBeanIds()
	{
		return runTimeDependencyManager.resolveBeansOrder(beans.keySet(), this);
	}
	
	@Override
	public LinkedList<Bean> getOrderedBeans()
	{
		LinkedList<Bean> orderedBeans = new LinkedList<Bean>();
		LinkedHashSet<String> orderedIds = getOrderedBeanIds();
		
		for ( String id : orderedIds)
		{
			orderedBeans.add( getBean(id) );
		}
		
		return orderedBeans;
	}
	
	@Override
	public void resolveReferences()
	{
		logger.debug( "Resolving alias for {} beans", beans.size() );

		for( Property prop : propertyReferences )
		{
			// Resolve aliases
			Bean aliasBean = aliases.get( prop.getRef() );
			if( aliasBean != null )
			{
				logger.debug( "Resolved alias {}", prop.getRef() );
				prop.setRef( aliasBean.getId() );
			}
			
			// Resolve Prototypes
			if( prototypeBeans.contains( prop.getRef() ) )
			{
				injectPrototype( prop );
			}
		}
		
	}

	private void injectPrototype( Property prop )
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
		}
	}
	
	@Override
	public
	DependencyManager<DefinitionDependency> getParentDependencyManager() {
		return parentDependencyManager;
	}

	@Override
	public
	DependencyManager<RunTimeDependency> getRunTimeDependencyManager() {
		return runTimeDependencyManager;
	}

	@Override
	public
	Set<String> getPrototypeBeans() {
		return prototypeBeans;
	}

	@Override
	public
	List<Property> getPropertyReferences() {
		return propertyReferences;
	}

	@Override
	public
	Map<String, Bean> getAliases() {
		return aliases;
	}
}