package org.staticioc.parser.plugins;


import org.staticioc.model.Bean;
import org.staticioc.model.Property;
import org.staticioc.parser.AttributesParserPlugin;
import org.staticioc.parser.BeanContainer;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

/**
 * Handle property written using Spring p: syntactic sugar 
 */
public class SpringPParserPlugin implements AttributesParserPlugin
{
	protected static final String BEAN_PROPERTY_PREFIX = "p:";
	protected static final String BEAN_PROPERTY_REF_SUFFIX = "-ref";
	protected BeanContainer container;

	@Override
	public void handleAttributes(Bean bean, NamedNodeMap beanAttributes) {
		// Handle Spring p:* properties
		for ( int a=0 ; a < beanAttributes.getLength() ; ++a )
		{
			Node beanAttr = beanAttributes.item( a ); 
			if ( beanAttr.getNodeName().startsWith( BEAN_PROPERTY_PREFIX ) )
			{
				String propertyName = beanAttr.getNodeName().substring( BEAN_PROPERTY_PREFIX.length() );
				String ref = null;
				String value = null;

				// Check if value is a reference
				if ( propertyName.endsWith( BEAN_PROPERTY_REF_SUFFIX) )
				{
					propertyName = propertyName.substring(0, propertyName.length() - BEAN_PROPERTY_REF_SUFFIX.length() );
					ref = beanAttr.getNodeValue();
				}
				else // Value is a value
				{
					value = beanAttr.getNodeValue();
				}

				Property prop = new Property( propertyName, value, ref );
				container.addOrReplaceProperty(prop, bean.getProperties() );
			}
		}
	}

	@Override
	public void setBeanContainer(BeanContainer container)
	{
		this.container = container;
	}
}
