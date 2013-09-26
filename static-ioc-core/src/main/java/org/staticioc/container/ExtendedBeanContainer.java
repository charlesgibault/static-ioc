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
	 * A ResolvedBeanCallback will be called after each parent Bean resolution
	 * 
	 * @param callback to call after each parent Bean resolution
	 */
	void resolveParentDefinition(ResolvedBeanCallback callback);
	
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
