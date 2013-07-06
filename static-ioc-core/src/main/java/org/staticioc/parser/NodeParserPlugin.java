package org.staticioc.parser;

import javax.xml.xpath.XPathExpressionException;

import org.staticioc.model.Bean;
import org.w3c.dom.Node;

/**
 * Interface for plugins that parse special attributes on a Bean node
 * @author charles
 */
public interface NodeParserPlugin
{
	/**
	 * Handle a specific node to enrich the passed bean
	 * @param bean Bean to enrich
	 * @param node full Node to parse
	 */
	void handleNode( final Bean bean, final Node node ) throws XPathExpressionException;
	
	/**
	 * A reference to the bean container
	 * @param container
	 */
	void setBeanContainer(BeanParser container);
}
