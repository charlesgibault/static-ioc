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
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

import org.apache.commons.lang.StringUtils;

public class Bean implements Comparable<Bean>
{
	public enum Type { SIMPLE, LIST, SET, PROPERTIES, MAP };
	public enum Scope { SINGLETON, PROTOTYPE };
	
	private String id;
	private String alias=null;
	private String className;
	private Type type = Type.SIMPLE;
	private Scope scope = Scope.SINGLETON;
	
	private boolean anonymous = false;
	private boolean isAbstract = false;

	private Collection<Property> properties = new HashSet<Property>();
	private Set<Property> constructorArgs = new LinkedHashSet<Property>();
	
	private String factoryBean=null;
	private String factoryMethod=null;
	
	private String initMethod=null;
	private String destroyMethod=null;
	
	public Bean(){}
	public Bean( String id, String className)
	{
		this.id = id;
		this.className = className;
	}
	
	public Bean( String id, String className, boolean anonymous)
	{
		this.id = id;
		this.className = className;
		this.anonymous = anonymous;
	}
	
	/**
	 * Kind of copy constructor to construct a bean from a parent
	 * @param name
	 * @param parent
	 * @param anonymous
	 */
	public Bean( String id, Bean parent, boolean anonymous)
	{
		this.id = id;
		this.className = parent.className;
		this.type = parent.type;
		this.scope = parent.scope;
		this.factoryBean=parent.factoryBean;
		this.factoryMethod=parent.factoryMethod;
		this.initMethod=parent.initMethod;
		this.destroyMethod=parent.destroyMethod;
		
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
	
	/**
	 * Construct a bean that is to be created by a factoryBean
	 * @param id
	 * @param className
	 * @param factoryBean
	 * @param factoryMethod
	 */
	public Bean( String id, String className, String factoryBean, String factoryMethod)
	{
		this.id = id;
		this.className = className;
		this.factoryBean = factoryBean;
		this.factoryMethod = factoryMethod;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
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
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}
	
	@Override
	public int compareTo(Bean bean) // Alphanumerical sorting using Bean Id
	{
		if( StringUtils.equals(id, bean.id) )
		{
			return 0;
		}
		
		if( id == null)
		{
			return -1;
		}
				
		return id.compareTo(bean.id);
	}
	@Override
	public String toString() {
		return "Bean [id=" + id + ", alias=" + alias + ", className=" + className
				+ ", type=" + type + ", anonymous=" + anonymous
				+ ", abstract=" + isAbstract
				+ ", factoryBean=" + factoryBean + ", factoryMethod=" + factoryMethod
				+ ", initMethod=" + initMethod + ", destroyMethod=" + destroyMethod
				+ ", properties=" + properties
				+ ", constructorArgs=" + constructorArgs + "]";
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
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
		return isAbstract;
	}
	public void setAbstract(boolean abstractBean) {
		this.isAbstract = abstractBean;
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
	public String getAlias() {
		return alias;
	}
	public void setAlias(String alias) {
		this.alias = alias;
	}
	public String getFactoryBean() {
		return factoryBean;
	}
	public void setFactoryBean(String factoryBean) {
		this.factoryBean = factoryBean;
	}
	public String getFactoryMethod() {
		return factoryMethod;
	}
	public void setFactoryMethod(String factoryMethod) {
		this.factoryMethod = factoryMethod;
	}
	public String getInitMethod() {
		return initMethod;
	}
	public void setInitMethod(String initMethod) {
		this.initMethod = initMethod;
	}
	public String getDestroyMethod() {
		return destroyMethod;
	}
	public void setDestroyMethod(String destroyMethod) {
		this.destroyMethod = destroyMethod;
	}
}
