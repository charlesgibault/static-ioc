package org.staticioc.model;

import java.util.Map;

public interface BeanContainer
{
	Bean getBean( final String id);
	
	public Map<String, Bean > getBeans();
}
