package org.staticioc.parser;

import java.util.Collection;
import java.util.LinkedList;

import org.staticioc.model.Property;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Commonly used methods for configuration parsing
 * @author charles
 *
 */
public class ParserHelper
{
	public static Property getVal(String propertyName, String value) {
		if ( value == null) { return null; }
		return new Property( propertyName, value, null );
	}

	public  static Property getRef(String propertyName, String ref) {
		if ( ref == null) { return null; }
		return new Property( propertyName, null, ref );
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
	public  static Collection<Node> extractNodesByName(final NodeList nodes, final String name) {
		Collection<Node> result = new LinkedList<Node>();

		if(nodes != null && name != null )
		{	
			for( int n = 0 ; n<nodes.getLength() ; ++n )
			{
				final Node node = nodes.item( n );

				if ( name.equals(node.getNodeName()) )
				{
					result.add( node );
				}
			}
		}

		return result;
	}
}
