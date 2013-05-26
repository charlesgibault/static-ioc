package org.staticioc.parser;

import org.staticioc.model.Bean;
import org.w3c.dom.NamedNodeMap;

/**
 * Interface for plugins that parse special attributes on a Bean node
 * @author charles
 */
public interface AttributesParserPlugin
{
	/**
	 * Handle a bean's node attributes
	 * @param bean
	 * @param beanAttributes
	 */
	void handleAttributes(Bean bean, NamedNodeMap beanAttributes);
	
	/**
	 * A reference to the bean container
	 * @param container
	 */
	void setBeanContainer(BeanContainer container);
}
