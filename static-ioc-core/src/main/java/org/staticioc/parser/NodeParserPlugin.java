package org.staticioc.parser;

import javax.xml.xpath.XPathExpressionException;

import org.staticioc.model.Property;
import org.w3c.dom.Node;

public interface NodeParserPlugin
{
	/**
	 * Define which XML keyword this plugin supports
	 * @return name of the supported type
	 */
	String getSupportedNode();
	
	/**
	 * 
	 * @param spNode
	 * @param propName
	 * @return a property representing this special type
	 */
	Property handleProperty( final Node node, final String propName ) throws XPathExpressionException;
	
	/**
	 * A reference to the bean container
	 * @param container
	 */
	void setBeanContainer(BeanContainer container);
}
