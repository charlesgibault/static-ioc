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
package org.staticioc.model;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

public class CollectionBean extends Bean
{
	private final List<Property> properties = new LinkedList<Property>();

	public Collection<Property> getProperties() {
		return properties;
	}

	public CollectionBean( Type type) {	
		this( null, null, true, type);
	}
	
	public CollectionBean(String name, String className, Type type) {
		this(name, className, true, type);
	}

	public CollectionBean(String name, String className, boolean anonymous, Type type) {
		super(name, className, anonymous);
		setType( type );
	}
}
