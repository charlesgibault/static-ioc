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
package org.staticioc.parser.plugins;


import javax.xml.xpath.XPathExpressionException;

import org.staticioc.model.Bean;
import org.staticioc.model.Property;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

/**
 * Handle property written using Spring p: syntactic sugar 
 */
public class SpringPParserPlugin extends AbstractNodeParserPlugin
{
	protected static final String BEAN_PROPERTY_REF_SUFFIX = "-ref";
	
	protected String beanPropertyPrefix;
	
	public SpringPParserPlugin()
	{
		prefix="p";
		setPrefix(prefix);
	}
	
	@Override
	public void handleNode(Bean bean, Node node) throws XPathExpressionException
	{
		final NamedNodeMap attributes = node.getAttributes();
		handleAttributes(bean, attributes);
	}

	protected void handleAttributes(Bean bean, NamedNodeMap beanAttributes) {
		// Handle Spring p:* properties
		for ( int a=0 ; a < beanAttributes.getLength() ; ++a )
		{
			Node beanAttr = beanAttributes.item( a ); 
			if ( beanAttr.getNodeName().startsWith( beanPropertyPrefix ) )
			{
				String propertyName = beanAttr.getNodeName().substring( beanPropertyPrefix.length() );
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
				beanParser.addOrReplaceProperty(prop, bean.getProperties() );
			}
		}
	}

	@Override
	public void setPrefix(String prefix) {
		super.setPrefix(prefix);
		beanPropertyPrefix = prefix + XML_NAMESPACE_DELIMITER;		
	}
}
