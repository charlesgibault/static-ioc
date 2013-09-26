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

import org.apache.commons.lang.StringUtils;
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
	public static Property getVal(String propertyName, String value) {
		if ( value == null) { return null; }
		return new Property( propertyName, value, null );
	}

	public  static Property getRef(String propertyName, String ref) {
		if ( ref == null) { return null; }
		return new Property( propertyName, null, ref );
	}

	public static Property handleValueRefAttributes(final String propName, final Node node)
	{
		Property prop=null;
		final NamedNodeMap propAttributes = node.getAttributes();
		
		final Node valueNode = propAttributes.getNamedItem(VALUE);
		final String value = (valueNode != null ) ? valueNode.getNodeValue() : null;

		final Node refNode = propAttributes.getNamedItem(REF);
		final Node idRefNode = propAttributes.getNamedItem(IDREF);
		final String ref = (refNode != null ) ? refNode.getNodeValue() : ((idRefNode != null ) ? idRefNode.getNodeValue() : null);

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
	 * @return
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
	 * @return
	 */
	public static Node extractFirstNodeByName(final NodeList nodes, final String name) {
		if(nodes != null && name != null )
		{	
			for( int n = 0 ; n<nodes.getLength() ; ++n )
			{
				final Node node = nodes.item( n );

				if ( name.equals(node.getNodeName()) )
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
	 * @return
	 */
	public  static Collection<Node> extractNodesByName(final NodeList nodes, final String name, final String namespacePrefix) {
		Collection<Node> result = new LinkedList<Node>();
		
		if(nodes != null && name != null )
		{
			final String fullName = prefixedName(namespacePrefix, name);
			
			for( int n = 0 ; n<nodes.getLength() ; ++n )
			{
				final Node node = nodes.item( n );

				if ( fullName.equals(node.getNodeName()) )
				{
					result.add( node );
				}
			}
		}

		return result;
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
