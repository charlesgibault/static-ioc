package org.staticioc.parser.plugins;


import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;

import org.staticioc.model.Bean;
import org.staticioc.model.Property;
import org.staticioc.parser.BeanContainer;
import org.staticioc.parser.NodeParserPlugin;
import org.staticioc.parser.ParserConstants;
import org.staticioc.parser.ParserHelper;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Handle property element
 * TODO migrate to a NodeSupportPlugin?
 */
public class BeanPropertiesParserPlugin implements NodeParserPlugin, ParserConstants
{
	protected BeanContainer container;
	private XPathExpression xProps;

	@Override
	public void handleNode(Bean bean, Node node) throws XPathExpressionException
	{
		final NodeList propsRef = (NodeList) getXProps().evaluate(node, XPathConstants.NODESET);
		handleBeanProperties(bean, propsRef);

	}

	protected void handleBeanProperties( final Bean bean, final NodeList propsRef)
			throws XPathExpressionException
			{
		for( int p = 0 ; p< propsRef.getLength() ; ++p )
		{
			final Node node = propsRef.item( p );
			final NamedNodeMap propAttributes = node.getAttributes();

			final Node nameNode = propAttributes.getNamedItem(NAME);

			if(nameNode != null ) // Ignore properties with no name
			{
				final String propName = nameNode.getNodeValue();
				Property prop = ParserHelper.handleValueRefAttributes(propName, node );

				if ( prop != null ) // Simple property : value or reference
				{
					container.addOrReplaceProperty(prop, bean.getProperties() );
				}
				else
				{
					container.handleNodes( bean, propName, node.getChildNodes());				
				}
			}
		}
	}
	
	private XPathExpression getXProps() throws XPathExpressionException
	{
		if( xProps == null)
		{
			synchronized(this)
			{
				if( xProps == null)
				{
					final XPath xPathProperties  = container.getXPathFactory().newXPath();
					xProps = xPathProperties.compile(XPATH_PROPERTY);
				}
			}
		}
		return xProps;
	}

	@Override
	public void setBeanContainer(BeanContainer container)
	{
		this.container = container;
	}
}