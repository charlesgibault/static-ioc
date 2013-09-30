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

import java.util.Collection;
import java.util.LinkedList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.staticioc.model.Property;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Commonly used methods for configuration parsing
 * @author charles
 *
 */
public class ParserHelper implements ParserConstants
{
	protected static final Logger logger = LoggerFactory.getLogger(ParserHelper.class);

	/**
	 * Builds a value property
	 * @param propertyName name of the Property to build
	 * @param value of the Property to build
	 * @return the built Property if value is not null, null otherwise
	 */
	public static Property getVal(String propertyName, String value) {
		if ( value == null) { return null; }
		return new Property( propertyName, value, null );
	}

	/**
	 * Builds a ref property
	 * @param propertyName name of the Property to build
	 * @param ref of the Property to build
	 * @return the built Property if ref is not null, null otherwise
	 */
	public  static Property getRef(String propertyName, String ref) {
		if ( ref == null) { return null; }
		return new Property( propertyName, null, ref );
	}

	/**
	 * Handle value, ref and idref attribute of an XML node and builds the corresponding property
	 * @param propName name of the Property to build
	 * @param node to analyze
	 * @return the built Property, if value, ref or idref is found, null otherwise
	 */
	public static Property handleValueRefAttributes(final String propName, final Node node)
	{
		Property prop=null;
		final NamedNodeMap propAttributes = node.getAttributes();
		
		final Node valueNode = propAttributes.getNamedItem(VALUE);
		final String value = (valueNode != null ) ? valueNode.getNodeValue() : null;

		final Node refNode = propAttributes.getNamedItem(REF);
		final Node idRefNode = propAttributes.getNamedItem(IDREF);
		final String ref = refNode != null ? refNode.getNodeValue() : (idRefNode != null ? idRefNode.getNodeValue() : null);

		if ( value != null || ref != null ) // Simple property : value or reference
		{
			prop  = new Property( propName, value, ref);
			
			// Handle type specified as an attribute
			if( value != null )
			{
				final Node typeNode = propAttributes.getNamedItem(TYPE);
				if( typeNode != null)
				{
					prop.setType( typeNode.getNodeValue() );
				}
			}
		}
		return prop;
	}
	
	/**
	 * Extract text value for nodes like <value>text</value>
	 * @param node
	 * @return the content of the first subnode of a node, expressed as a String, or null otherwise
	 */
	public  static String extractFirstChildValue(final Node node) {
		final Node valueSubNode = node.getFirstChild();

		if ( valueSubNode == null ) { return null; }
		return valueSubNode.getNodeValue();
	}

	/**
	 * Go through a node list a extract the first matching node given its name
	 * @param nodes
	 * @param name
	 * @return the first matching node given in the NodeList, or null if not matching Node is found
	 */
	public static Node extractFirstNodeByName(final NodeList nodes, final String name, final String namespacePrefix) {
		if(nodes != null && name != null )
		{	
			for( int n = 0 ; n<nodes.getLength() ; ++n )
			{
				final Node node = nodes.item( n );

				if ( match(name, node.getNodeName(), namespacePrefix) )
				{
					return node;
				}
			}
		}

		return null;
	}

	/**
	 * Go through a node list a extract the first matching node given its name
	 * @param nodes
	 * @param name
	 * @return the first matching node given in the NodeList, or null if not matching Node is found
	 */
	public  static Collection<Node> extractNodesByName(final NodeList nodes, final String name, final String namespacePrefix) {
		Collection<Node> result = new LinkedList<Node>();
		
		if(nodes != null && name != null )
		{	
			for( int n = 0 ; n<nodes.getLength() ; ++n )
			{
				final Node node = nodes.item( n );

				if ( match(name, node.getNodeName(), namespacePrefix) )
				{
					result.add( node );
				}
			}
		}

		return result;
	}
	
	/**
	 * Parse a NodeList for XML namespace declaration attributes and build a mapping associating each namespace with its prefix
	 * 
	 * @param nodes to analyze
	 * @return a Map<namespace URL, namespace prefix> for each namespace definition attached to any nodes in the passed NodeList
	 */
	public static Map<String, String> extractNamespacePrefix( final NodeList nodes )
	{
		final Map<String, String> namespacePrefix = new ConcurrentHashMap<String, String> ();

		for( int i = 0 ; i < nodes.getLength() ; ++i)
		{
			Node node = nodes.item(i);
			NamedNodeMap attributes = node.getAttributes();

			for( int a = 0 ; a < attributes.getLength(); ++a )
			{
				final Node attribute = attributes.item(a);
				final String attributeValue = attribute.getNodeName();

				if( attributeValue != null && attributeValue.startsWith(XML_NAMESPACE_DEF) ) // its a namespace declaration
				{
					final String schemaUrl = attribute.getNodeValue();					
					String prefix  = "";
					
					if( attributeValue.contains(":") ) // prefix
					{
						prefix = attributeValue.split(":")[1];
					}
					
					namespacePrefix.put(schemaUrl, prefix);
				}
			}
		}
		
		return namespacePrefix;
	}
	
	/**
	 * Build full name of an element given the prefix of its namespace
	 * @param prefix of the namespace the element belongs to
	 * @param name of the element
	 * @return exact name of the element as it is expected in present document
	*/
	public static String prefixedName(String prefix, String name)
	{
		if ( StringUtils.isEmpty(prefix) )
		{
			return name;
		}
		
		StringBuilder sb = new StringBuilder(prefix);
		sb.append( XML_NAMESPACE_DELIMITER );
		sb.append(name);
		return sb.toString();
	} 
	
	/**
	 * Check whether a node has the expected name after prefix resolution
	 * 
	 * @param expectedName unprefixed expected name 
	 * @param nodeName full name of the element to compare
	 * @param prefix of the namespace the expected element belongs to
	 * @return true if nodeName is equals to expectedName after prefix resolution
	 */
	public static boolean match( String expectedName, String nodeName, String prefix)
	{
		if ( expectedName == null)
		{
			return StringUtils.isEmpty(prefix) && nodeName == null;
		}
		
		if ( StringUtils.isEmpty(prefix) )
		{
			return expectedName.equals(nodeName);
		}
		
		if( nodeName == null || nodeName.length() != expectedName.length() + prefix.length() +1 )
		{
			return false;
		}
		
		return nodeName.startsWith(prefix)
			&& nodeName.endsWith(expectedName)
			&& nodeName.charAt( prefix.length() ) == XML_NAMESPACE_DELIMITER;
	}
}
