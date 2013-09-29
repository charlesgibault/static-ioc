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
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.Map;

import org.staticioc.dependency.DefinitionDependency;
import org.staticioc.dependency.RunTimeDependency;
import org.staticioc.model.Bean;
import org.staticioc.model.Property;

public interface BeanContainer extends SimpleBeanContainer
{
	/**
	 * Prefix used for declaring all anonymous Beans
	 */
	String ANONYMOUS_BEAN_PREFIX = "bean_";
	
	/**
	 * Prefix used for declaring all instances of prototype Beans
	 */
	String PROTOTYPE_BEAN_PREFIX = "prototyped_";
	
	/**
	 * Duplicate a Bean's definition, changing id, alias and anonymous status on the fly
	 * @param id of the duplicated Bean
	 * @param alias of the duplicated Bean
	 * @param source Bean to be duplicated
	 * @param isAnonymous whether the duplicated Bean should be anonymous or not
	 * @return the newly duplicated Bean
	 */
	Bean duplicateBean( String id, String alias, Bean source, boolean isAnonymous);
	
	/**
	 * 
	 * @return a GUID for an anonymous bean
	 */
	String generateAnonymousBeanId();
	
	/**
	 * Register a Bean in the container
	 * @param bean
	 */
	void register(final Bean bean);

	/**
	 * Register a set of Beans in the container
	 * @param beans
	 */
	void register(final Map<String, Bean> beans) ;
	
	/**
	 * Register a bean -> parent dependency
	 * @param dependency to be registered
	 */
	void registerParent( DefinitionDependency dependency );
	
	/**
	 * Register a bean -> run dependency
	 * @param dependency to be registered
	 */
	void registerRunTimeDependency(RunTimeDependency dependency);

	/**
	 * Add or replace a Property in a bean's property set
	 * Also updates a the Map of Property with a bean reference.
	 * 
	 * @param prop
	 * @param set
	 */
	void addOrReplaceProperty(Property prop, Collection<Property> set);
	
	/**
	 * Resolve beans dependencies to order them so that for each Bean, all its dependencies are ordered before it
	 * @return an ordered Set of Bean's ids
	 */
	LinkedHashSet<String> getOrderedBeanIds();
	
	/**
	 * Resolve beans dependencies to order them so that for each Bean, all its dependencies are ordered before it
	 * @return an ordered Set of Beans
	 */
	LinkedList<Bean> getOrderedBeans();
}
