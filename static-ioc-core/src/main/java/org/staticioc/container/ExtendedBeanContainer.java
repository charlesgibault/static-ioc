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

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.staticioc.dependency.DefinitionDependency;
import org.staticioc.dependency.DependencyManager;
import org.staticioc.dependency.RunTimeDependency;
import org.staticioc.model.Bean;
import org.staticioc.model.Property;

public interface ExtendedBeanContainer extends BeanContainer {
	
	/**
	 * Register another ExtendedBeanContainer's content into this ExtendedBeanContainer
	 * Merge parent and runtime dependencies, prototypeBeans, aliases and propertyReferences
	 * @param container whose content is to be register in the current container
	 */
	void register( ExtendedBeanContainer container);
	
	/**
	 * Resolve aliases and prototype references once for all
	 */
	void resolveReferences();

	/**
	 * Resolve all parents dependencies and fill in their definition.
	 */
	void resolveParentDefinition();
	
	/**
	 * @return the Bean definition dependencies manager
	 */
	DependencyManager<DefinitionDependency> getParentDependencyManager();
	
	/**
	 * @return the Bean runtime dependencies manager
	 */
	DependencyManager<RunTimeDependency> getRunTimeDependencyManager();
	
	/**
	 * @return the Set of ids of currently tracked prototyped Beans
	 */
	Set<String> getPrototypeBeans();
	
	/**
	 * @return the List of all Properties that a referencing a Bean (ie: value=null && ref!=null)
	 */
	List<Property> getPropertyReferences();
	
	/**
	 * @return A Map< alias name, target Bean>
	 */
	Map<String, Bean> getAliases();
}
