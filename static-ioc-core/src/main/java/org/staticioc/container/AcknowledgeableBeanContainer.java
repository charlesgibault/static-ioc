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

import java.util.HashMap;
import java.util.Map;

import org.staticioc.model.Bean;

public class AcknowledgeableBeanContainer implements SimpleBeanContainer
{
	private final SimpleBeanContainer instance;
	private final Map<String, Bean> knownBeans = new HashMap<String, Bean>();
	
	public AcknowledgeableBeanContainer( SimpleBeanContainer instance )
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
	
	public void acknowledge(String id)
	{
		Bean bean = instance.getBean(id);
		
		if( bean != null )
		{
			knownBeans.put(id, bean);
		}
	}
}
