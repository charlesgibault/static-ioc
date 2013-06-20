package org.staticioc.parser;

public interface ParserConstants {
	static final String BEAN_PROPERTY_PREFIX = "p:";
	static final String BEAN_PROPERTY_REF_SUFFIX = "-ref";
	static final String XPATH_BEAN = "/beans/bean";
	static final String XPATH_PROPERTY = "property[@name]";
	static final String XPATH_IMPORT = "/beans/import[@resource]";
	static final String BEAN = "bean";
	static final String REF = "ref";
	static final String ID = "id";
	static final String IDREF = "idref";
	static final String CLASS = "class";
	static final String NAME = "name";
	static final String VALUE = "value";
	static final String TYPE = "type";
	static final String ABSTRACT = "abstract";
	static final String PARENT = "parent";
	static final String SCOPE = "scope";
	static final String PROTOTYPE = "prototype";
	static final String SINGLETON = "singleton";
	static final String FACTORY_BEAN = "factory-bean";
	static final String FACTORY_METHOD = "factory-method";
	static final String INIT_METHOD = "init-method";
	static final String DESTROY_METHOD = "destroy-method";
	static final String MAP = "map";
	static final String LIST = "list";
	static final String SET = "set";
	static final String PROPS = "props";
	static final String ENTRY = "entry";
	static final String KEY = "key";
	static final String KEY_REF = "key-ref";
	static final String VALUE_REF = "value-ref";
	static final String PROP = "prop";
	static final String CONSTRUCTOR_ARGS = "constructor-arg";
	static final String INDEX = "index";
	static final String NULL = "null";
	static final String RESOURCE = "resource";
}
