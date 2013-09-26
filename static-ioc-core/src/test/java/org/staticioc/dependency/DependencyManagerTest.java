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
package org.staticioc.dependency;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;

import org.junit.Test;
import org.staticioc.container.SimpleBeanContainer;
import org.staticioc.dependency.DependencyManager;
import org.staticioc.dependency.RunTimeDependency;
import org.staticioc.model.Bean;

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
		
		SimpleBeanContainer container = new SimpleBeanContainer()
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
		LinkedHashSet<String> resultNoDependencies = manager.resolveBeansOrder(beans.keySet(), container);
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
		
		LinkedHashSet<String> resultWithDependencies = manager.resolveBeansOrder(beans.keySet(), container);
		assertEquals("Incorrect bean order when dependencies are present", "[bean2, bean4, bean1, bean3, bean5]", resultWithDependencies.toString() );
	}

}
