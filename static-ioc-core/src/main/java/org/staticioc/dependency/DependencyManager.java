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
package org.staticioc.dependency;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import javax.xml.xpath.XPathExpressionException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.staticioc.model.AcknowledgeableBeanContainer;
import org.staticioc.model.Bean;
import org.staticioc.model.BeanContainer;

public class DependencyManager<T extends Dependency>
{
	protected static final Logger logger = LoggerFactory.getLogger(DependencyManager.class);
			
	// Map a bean
	private final Map<String, T > dependencyMap = new ConcurrentHashMap<String, T>();

	public void register( T dependency)
	{
		if( logger.isDebugEnabled())
		{
			logger.debug( "Adding {}", dependency );
		}
		
		dependencyMap.put( dependency.getId(), dependency );
	}

	public void registerAll( DependencyManager<T> dependencyManager)
	{
		registerAll( dependencyManager.dependencyMap );
	}

	
	public void registerAll( Map<String, T > dependencies)
	{
		dependencyMap.putAll( dependencies );
	}
	
	/**
	 * Resolve all registered beans dependencies and call the passed callback function for each bean once its dependencies have been resolved
	 * @param container
	 * @param callback
	 * @throws XPathExpressionException
	 */
	public void resolveAllDependencies(BeanContainer container, ResolvedDependencyCallback<T> callback)
	{
		final Set<String> visitedParents = new HashSet<String>();
		
		for( String parentName : dependencyMap.keySet() )
		{
			resolveBean( parentName, container, visitedParents, callback );
		}
	}
	
	/**
	 * Provide an ordered Set of Strings so that each Bean's id is listed after all its dependencies' id
	 * @param beans
	 * @param container
	 * @param callback
	 * @throws XPathExpressionException
	 */
	public LinkedHashSet<String> resolveBeansOrder(Set<String> beans, BeanContainer container)
	{
		final Set<String> visitedParents = new HashSet<String>();
		final LinkedHashSet<String> result = new LinkedHashSet<String>();
		final AcknowledgeableBeanContainer ackContainer = new AcknowledgeableBeanContainer( container );
		
		// First: add all beans that have no dependencies
		for( String id : container.getBeans().keySet() )
		{
			if ( !dependencyMap.keySet().contains( id ) )
			{
				result.add(id);
				ackContainer.acknowledge(id);
			}
		}

		// Then add all beans that have dependencies in the proper order		
		for( String parentName :  dependencyMap.keySet() )
			{
			resolveBean( parentName, ackContainer, visitedParents, new ResolvedDependencyCallback<T> ()
				{
					@Override
					public void onResolvedDependency(T dependency,
							BeanContainer container)
					{
						result.add(dependency.getId());
						ackContainer.acknowledge(dependency.getId());
					}
						
				} );
		}
		
		return result;
	}
	
	/**
	 * Resolve dependencies using a depth first search (Cormen et al. / Tarjan)
	 * 
	 * @param parentName
	 * @throws XPathExpressionException 
	 */
	protected void resolveBean( final String name, BeanContainer container, final Set<String> visitedBeans, ResolvedDependencyCallback<T> callback )
	{
		// Grab dependency
		final T dependency = dependencyMap.get(name);
		
		if( dependency == null )
		{
			logger.warn( "Unresolved dependency on Bean {}. Ignoring", name );
			return;
		}
		
		// Test direct resolution (parent already resolved)
		for( String targetId : dependency.getTargetIds())
		{
			Bean parentBean =  container.getBean( targetId );

			if( parentBean == null) // parent cannot be resolved directly -> Do a depth first approach
			{
				// Maintain a set of visited parent to avoid infinite loops in cycles.
				// This works because our depth first approach visits each node only once O(N) complexity.
				visitedBeans.add( name );

				if ( !visitedBeans.contains( targetId ))
				{
					resolveBean( targetId, container, visitedBeans, callback );
					parentBean = container.getBean( targetId );

					if( parentBean == null ) // Dependency couldn't be resolved still (due to cycle)
					{
						// Check if bean exist in the container
						if( dependency.isStrict() )
						{
							logger.warn( "Unresolved dependency on bean {}. Ignoring", name );
							return;
						}
					}
				}
				else
				{			
					logger.warn( "Cycle detected with bean {}", name );
					return;
				}
			}
		}
		
		// Callback ( T dependency)
		callback.onResolvedDependency(dependency, container);
		
		//TODO remove from parentDependencyMap ?
	}

	@Override
	public String toString() {
		return "DependencyManager [dependencyMap=" + dependencyMap + "]";
	}
}
