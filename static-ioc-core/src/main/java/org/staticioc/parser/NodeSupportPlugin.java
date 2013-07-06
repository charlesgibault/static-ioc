package org.staticioc.parser;

import javax.xml.xpath.XPathExpressionException;

import org.staticioc.model.Property;
import org.w3c.dom.Node;

public interface NodeSupportPlugin
{
	/**
	 * Define which XML keyword this plugin supports
	 * @return name of the supported type
	 */
	String getSupportedNode();
	
	/**
	 * Process a special node type and encapsulate the result as a Property
	 * @param node Node to process
	 * @param name of the property representing the content of the node 
	 * @return a Property encapsulating the loaded node
	 * @throws XPathExpressionException
	 */
	Property handleNode( final Node node, final String name ) throws XPathExpressionException;
	
	/**
	 * A reference to the bean container
	 * @param container
	 */
	void setBeanContainer(BeanParser container);
}
