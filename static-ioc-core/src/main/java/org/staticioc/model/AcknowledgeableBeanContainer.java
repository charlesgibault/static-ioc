package org.staticioc.model;

import java.util.HashMap;
import java.util.Map;

public class AcknowledgeableBeanContainer implements BeanContainer
{
	private final BeanContainer instance;
	private Map<String, Bean> knownBeans = new HashMap<String, Bean>();
	
	public AcknowledgeableBeanContainer( BeanContainer instance )
	{
		this.instance = instance;
	}
	
	@Override
	public Bean getBean(String id) {
		if( knownBeans.containsKey(id))
		{
			return knownBeans.get(id);
		}
		return null;
	}

	@Override
	public Map<String, Bean> getBeans() {
		return knownBeans;
	}

	public BeanContainer getInstance() {
		return instance;
	}
	
	public void acknowledge(String id)
	{
		Bean bean = instance.getBean(id);
		
		if( bean != null )
		{
			knownBeans.put(id, bean);
		}
	}
}
