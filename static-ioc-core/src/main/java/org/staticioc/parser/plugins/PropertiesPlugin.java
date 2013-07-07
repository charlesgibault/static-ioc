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
import org.staticioc.model.CollectionBean;
import org.staticioc.model.Property;
import org.staticioc.parser.BeanParser;
import org.staticioc.parser.NodeSupportPlugin;
import org.staticioc.parser.ParserConstants;
import org.staticioc.parser.ParserHelper;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class PropertiesPlugin implements NodeSupportPlugin, ParserConstants
{
	protected BeanParser container;

	public String getSupportedNode()
	{
		return PROPS;
	}

	@Override
	public Property handleNode( final Node node, final String propName ) throws XPathExpressionException
	{
		// create an anonymous bean of appropriate collection type
		final String beanId = container.generateAnonymousBeanId();
		final Bean collecBean = new CollectionBean( beanId, Bean.Type.PROPERTIES.toString(), Bean.Type.PROPERTIES );
		handleProperties( collecBean, node.getChildNodes() );
		
		// register Bean in Map
		container.register( collecBean  );

		// Wire this bean as a reference
		return ParserHelper.getRef( propName, beanId );
	}
	
	/**
	 * Handle properties node (which is a kind of Map<String,String>)
	 * @param collecBean
	 * @param entries
	 */
	protected void handleProperties( final Bean collecBean, final NodeList entries )
	{
		for (int e = 0 ; e < entries.getLength() ; ++e )
		{
			final Node entry = entries.item( e );
			final String entryNodeName = entry.getNodeName();
			
			if( !entryNodeName.equals( PROP ) ) // ignore non <entry> nodes
			{
				continue;
			}
			
			// Prop has a key attribute and an extractFirstChildValue
			final NamedNodeMap entryAttributes = entry.getAttributes();
			final Node keyNode = entryAttributes.getNamedItem(KEY);
			
			if ( keyNode != null)
			{
				final String value = ParserHelper.extractFirstChildValue( entry );
				Property prop = ParserHelper.getVal( keyNode.getNodeValue(), value);
				
				if( prop != null )
				{
					container.addOrReplaceProperty(prop, collecBean.getProperties() );
				}
			}
		}
	}
	
	@Override
	public void setBeanContainer(BeanParser container)
	{
		this.container = container;
	}
}
