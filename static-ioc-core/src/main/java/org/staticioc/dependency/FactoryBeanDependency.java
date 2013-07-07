package org.staticioc.dependency;

public class FactoryBeanDependency extends RunTimeDependency
{
	public FactoryBeanDependency(String id, String targetId) {
		super(id, targetId);
		setStrict(false);
	}
}
