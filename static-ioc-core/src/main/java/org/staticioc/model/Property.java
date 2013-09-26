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

public class Property implements Cloneable
{
	public Property(){}
	
	public Property( final String name, final String value, final String ref)
	{
		this(name, value, ref, null);
	}
	
	public Property( final String name, final String value, final String ref, final String type )
	{
		this.name = name;
		this.value = value;
		this.ref = ref;
		this.type = type;
	}
	
	private String name;
	private String value;
	private String ref;
	private String type; //optional type for a value
	
	/**
	 * Hack to handle Map entries properly
	 * Indicate whether name of the property is in fact a bean reference (for map collections)
	 */
	boolean keyRef = false; 
	
	@Override
	protected Property clone()
    {
		return new Property( name, value, ref, type );
    }
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Property other = (Property) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}
	
	@Override
	public String toString() {
		return "Property [name=" + name + ", value=" + value + ", ref=" + ref
				+ ", type=" + type + ", keyRef=" + keyRef + "]";
	}

	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	public String getRef() {
		return ref;
	}
	public void setRef(String ref) {
		this.ref = ref;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public boolean isKeyRef() {
		return keyRef;
	}

	public void setKeyRef(boolean keyRef) {
		this.keyRef = keyRef;
	}
}
