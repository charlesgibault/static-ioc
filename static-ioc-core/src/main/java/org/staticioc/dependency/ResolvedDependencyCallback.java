package org.staticioc.dependency;

import org.staticioc.model.BeanContainer;

public interface ResolvedDependencyCallback<T extends Dependency>
{
	void onResolvedDependency( T dependency, BeanContainer container );
}
