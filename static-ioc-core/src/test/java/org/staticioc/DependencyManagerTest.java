package org.staticioc;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;

import org.junit.Test;
import org.staticioc.dependency.DependencyManager;
import org.staticioc.dependency.RunTimeDependency;
import org.staticioc.model.Bean;
import org.staticioc.model.BeanContainer;

public class DependencyManagerTest
{
	
	
	@Test
	public void testRunTimeDependencyResolution()
	{
		DependencyManager<RunTimeDependency> manager = new DependencyManager<RunTimeDependency>();
		
		Bean bean1 = new Bean("bean1", "pkg");
		Bean bean2 = new Bean("bean2", "pkg");
		Bean bean3 = new Bean("bean3", "pkg");
		Bean bean4 = new Bean("bean4", "pkg");
		Bean bean5 = new Bean("bean5", "pkg");
		
		final Map<String, Bean> beans = new LinkedHashMap<String, Bean>();
		beans.put(bean1.getId(), bean1);
		beans.put(bean2.getId(), bean2);
		beans.put(bean3.getId(), bean3);
		beans.put(bean4.getId(), bean4);
		beans.put(bean5.getId(), bean5);
		
		BeanContainer container = new BeanContainer()
		{
			@Override
			public Bean getBean(String id) {
				return beans.get(id);
			}

			@Override
			public Map<String, Bean> getBeans() {
				return beans;
			}
			
		};
		
		// No dependency declared for the moment : bean are expected to come out in the same order as they were inserted in the LinkedHashMap
		LinkedHashSet<String> resultNoDependencies = manager.resolveAllBeans(beans.keySet(), container);
		assertEquals("Incorrect bean order when no dependency are present", "[bean1, bean2, bean3, bean4, bean5]", resultNoDependencies.toString() );
		
		// Now add dependencies
		// 5 -> 1
		// 3 -> 1, 2
		// 1 -> 4
		// Expected order: 2, 4, 1, 3, 5
		RunTimeDependency dependency5_1 = new RunTimeDependency("bean5", "bean1");
		RunTimeDependency dependency3_12 = new RunTimeDependency("bean3", new LinkedHashSet<String>( Arrays.asList(new String[] {  "bean1", "bean2" } ) ) );
		RunTimeDependency dependency1_4 = new RunTimeDependency("bean1", "bean4");
		
		manager.register(dependency5_1);
		manager.register(dependency3_12);
		manager.register(dependency1_4);
		
		LinkedHashSet<String> resultWithDependencies = manager.resolveAllBeans(beans.keySet(), container);
		assertEquals("Incorrect bean order when dependencies are present", "[bean2, bean4, bean1, bean3, bean5]", resultWithDependencies.toString() );
	}

}
