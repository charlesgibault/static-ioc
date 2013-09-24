package org.staticioc.parser;

import java.util.List;

public interface NamespaceParser
{
	/**
	 * @return the URI implemented by this namespace parser
	 */
	String getNameSpaceUri();
	
	/**
	 * @return a list of ordered NodeParserPlugin elements to load elements for this namespace
	 */
	List<NodeParserPlugin> getNodeParserPlugins();
	
	/**
	 * @return a list of ordered NodeSupportPlugin elements to load elements for this namespace
	 */
	List<NodeSupportPlugin> getNodeSupportPlugins();
	
	/**
	 * A reference to the bean container
	 * @param container
	 */
	void setBeanContainer(BeanParser container);
	
	/**
	 * Declare the prefix for this namespace in the current document
	 * @param prefix
	 */
	void setPrefix(String prefix);

}
