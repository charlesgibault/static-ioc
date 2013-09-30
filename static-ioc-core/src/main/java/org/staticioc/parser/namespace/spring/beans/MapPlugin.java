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
package org.staticioc.parser.namespace.spring.beans;

import javax.xml.xpath.XPathExpressionException;

import org.staticioc.model.Bean;
import org.staticioc.model.CollectionBean;
import org.staticioc.model.Property;
import org.staticioc.parser.AbstractNodeSupportPlugin;
import org.staticioc.parser.ParserHelper;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class MapPlugin extends AbstractNodeSupportPlugin
{
	@Override
	public String getUnprefixedSupportedNode()
	{
		return MAP;
	}

	@Override
	public Property handleNode( final Node node, final String propName ) throws XPathExpressionException
	{
		// create an anonymous bean of appropriate collection type
		final String beanId = beanParser.getBeanContainer().generateAnonymousBeanId();
		final Bean collecBean = new CollectionBean( beanId, Bean.Type.MAP.toString(), Bean.Type.MAP );
		handleMap( collecBean, node.getChildNodes() );

		// register Bean in Map
		beanParser.getBeanContainer().register( collecBean  );

		// Wire this bean as a reference
		return ParserHelper.getRef( propName, beanId );
	}
	
	/**
	 * Handle a map definition
	 * @param collecBean (anonymous) bean representing the collection
	 * @param entries list of XML node representing the Map
	 * @throws XPathExpressionException
	 */
	private void handleMap( final Bean collecBean, final NodeList entries ) throws XPathExpressionException
	{		
		for (int e = 0 ; e < entries.getLength() ; ++e )
		{
			final Node entry = entries.item( e );
						
			if( ! ParserHelper.match( ENTRY, entry.getNodeName(), prefix) ) // ignore non <entry> nodes
			{
				continue;
			}
			
			final NamedNodeMap entryAttributes = entry.getAttributes();
			
			// entry has a 'key' attribute or a 'key-ref' attribute or an inner entry
			final Node keyNode = entryAttributes.getNamedItem(KEY);
			
			String keyValue=null;
			boolean isKeyRef = false;
			
			if(keyNode != null)
			{
				keyValue = keyNode.getNodeValue();
			}
			else
			{
				final Node keyRefNode = entryAttributes.getNamedItem(KEY_REF);
			
				if(keyRefNode != null)
				{
					keyValue = keyRefNode.getNodeValue();
					isKeyRef = true;
				}
				else
				{
					final Node keyDef = ParserHelper.extractFirstNodeByName( entry.getChildNodes(), KEY, prefix);
					
					if(keyDef != null)
					{
						for (int kc = 0 ; kc < keyDef.getChildNodes().getLength() ; ++kc )
						{
							final Node kcNode = keyDef.getChildNodes().item( kc );
							final String keyPropName = collecBean.getId() + "_key";
							
							Property keyRefAsProp = beanParser.handleNode( kcNode, keyPropName );
							if (keyRefAsProp != null)
							{
								if( keyRefAsProp.getValue() != null )
								{
									keyValue = keyRefAsProp.getValue();
									break;
								}
								else if( keyRefAsProp.getRef() != null )
								{
									keyValue = keyRefAsProp.getRef();
									isKeyRef = true;
									break;
								}
							}
						}
					}
				}
			}			
			
			// entry has a 'value' attribute or a 'value-ref' attribute or an inner entry.
			final Node valueNode = entryAttributes.getNamedItem(VALUE);
			
			String value = null;
			String refValue = null;
			
			if(valueNode != null)
			{
				value = valueNode.getNodeValue();
			}
			else
			{
				final Node valueRefNode = entryAttributes.getNamedItem(VALUE_REF);
			
				if(valueRefNode != null)
				{
					refValue = valueRefNode.getNodeValue();
				}
				else
				{
					for (int v = 0 ; v < entry.getChildNodes().getLength() ; ++v )
					{
						final Node vNode = entry.getChildNodes().item( v );
						final String valuePropName = collecBean.getId() + "_value";
						
						Property keyValueAsProp = beanParser.handleNode( vNode, valuePropName );
						if (keyValueAsProp != null)
						{
							if( keyValueAsProp.getValue() != null )
							{
								value = keyValueAsProp.getValue();
								break;
							}
							else if( keyValueAsProp.getRef() != null )
							{
								refValue = keyValueAsProp.getRef();
								break;
							}
						}
					}
				}
			}
			
			if ( keyValue != null)
			{
				Property prop  = new Property( keyValue, value, refValue);
				prop.setKeyRef( isKeyRef );
			
				beanParser.getBeanContainer().addOrReplaceProperty(prop, collecBean.getProperties() );
			}
		}
	}
}
