package org.staticioc.container;

import java.util.Map;

import org.staticioc.model.Bean;

public interface SimpleBeanContainer
{
	/**
	 * Retrieve a Bean using its id
	 * @param id of the Bean to retrieve
	 * @return the Bean whose id match (or null if Bean doesn't exists)
	 */
	Bean getBean( final String id);
	
	/**
	 * @return all known Beans in a <bean id, Bean> Map
	 */
	Map<String, Bean > getBeans();

}
