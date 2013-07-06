package org.staticioc.dependency;

import java.util.Set;

public class RunTimeDependency extends Dependency
{
	public RunTimeDependency(String id, String targetId) {
		super(id, targetId);
		
	}
	
	public RunTimeDependency(String id, Set<String> targetIds) {
		super(id, targetIds);
		
	}
}
