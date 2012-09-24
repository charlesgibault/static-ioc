/**
 *  Copyright (C) 2012 Charles Gibault
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
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

public class Bean
{
	public enum Type { SIMPLE, LIST, SET, PROPERTIES, MAP };
	public enum Scope { SINGLETON, PROTOTYPE };
	
	public Bean(){}
	public Bean( String name, String className)
	{
		this.name = name;
		this.className = className;
	}
	
	public Bean( String name, String className, boolean anonymous)
	{
		this.name = name;
		this.className = className;
		this.anonymous = anonymous;
	}
	
	/**
	 * Kind of copy constructor to construct a bean from a parent
	 * @param name
	 * @param parent
	 * @param anonymous
	 */
	public Bean( String name, Bean parent, boolean anonymous)
	{
		this.name = name;
		this.className = parent.className;
		this.type = parent.type;
		this.scope = parent.scope;
		
		this.anonymous = anonymous; // not inherited, as with abstract attribute
		
		for( Property prop : parent.properties)
		{
			this.properties.add( prop.clone() );
		}
		
		for( Property prop : parent.constructorArgs)
		{
			this.constructorArgs.add( prop.clone() );
		}
	}
		
	private String name;
	private String className;
	private Type type = Type.SIMPLE;
	private Scope scope = Scope.SINGLETON;
	
	private boolean anonymous = false;
	private boolean abstractBean = false;

	private Collection<Property> properties = new HashSet<Property>();
	private Set<Property> constructorArgs = new LinkedHashSet<Property>();
	
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
		Bean other = (Bean) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}
	
	@Override
	public String toString() {
		return "Bean [name=" + name + ", className=" + className + ", type="
				+ type + ", anonymous=" + anonymous + ", abstractBean="
				+ abstractBean + ", properties=" + properties
				+ ", constructorArgs=" + constructorArgs + "]";
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getClassName() {
		return className;
	}
	public void setClassName(String className) {
		this.className = className;
	}
	public Collection<Property> getProperties() {
		return properties;
	}
	public Set<Property> getConstructorArgs() {
		return constructorArgs;
	}
	public boolean isAnonymous() {
		return anonymous;
	}
	public void setAnonymous(boolean anonymous) {
		this.anonymous = anonymous;
	}	
	public boolean isAbstract() {
		return abstractBean;
	}
	public void setAbstract(boolean abstractBean) {
		this.abstractBean = abstractBean;
	}
	public Type getType() {
		return type;
	}
	public void setType(Type type) {
		this.type = type;
	}
	public Scope getScope() {
		return scope;
	}
	public void setScope(Scope scope) {
		this.scope = scope;
	}
}
