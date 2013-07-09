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
package org.staticioc.parser;

public interface ParserConstants
{
	String BEAN_PROPERTY_PREFIX = "p:";
	String BEAN_PROPERTY_REF_SUFFIX = "-ref";
	String XPATH_BEAN = "/beans/bean";
	String XPATH_PROPERTY = "property[@name]";
	String XPATH_IMPORT = "/beans/import[@resource]";
	String BEAN = "bean";
	String REF = "ref";
	String ID = "id";
	String IDREF = "idref";
	String CLASS = "class";
	String NAME = "name";
	String VALUE = "value";
	String TYPE = "type";
	String ABSTRACT = "abstract";
	String PARENT = "parent";
	String SCOPE = "scope";
	String PROTOTYPE = "prototype";
	String SINGLETON = "singleton";
	String FACTORY_BEAN = "factory-bean";
	String FACTORY_METHOD = "factory-method";
	String INIT_METHOD = "init-method";
	String DESTROY_METHOD = "destroy-method";
	String MAP = "map";
	String LIST = "list";
	String SET = "set";
	String PROPS = "props";
	String ENTRY = "entry";
	String KEY = "key";
	String KEY_REF = "key-ref";
	String VALUE_REF = "value-ref";
	String PROP = "prop";
	String CONSTRUCTOR_ARGS = "constructor-arg";
	String INDEX = "index";
	String NULL = "null";
	String RESOURCE = "resource";
}
