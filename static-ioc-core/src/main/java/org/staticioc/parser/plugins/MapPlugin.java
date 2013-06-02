package org.staticioc.parser.plugins;

import javax.xml.xpath.XPathExpressionException;

import org.staticioc.model.Bean;
import org.staticioc.model.CollectionBean;
import org.staticioc.model.Property;
import org.staticioc.parser.BeanContainer;
import org.staticioc.parser.NodeSupportPlugin;
import org.staticioc.parser.ParserConstants;
import org.staticioc.parser.ParserHelper;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class MapPlugin implements NodeSupportPlugin, ParserConstants
{
	protected BeanContainer container;

	public String getSupportedNode()
	{
		return MAP;
	}

	@Override
	public Property handleNode( final Node node, final String propName ) throws XPathExpressionException
	{
		// create an anonymous bean of appropriate collection type
		final String beanId = container.generateAnonymousBeanId();
		final Bean collecBean = new CollectionBean( beanId, Bean.Type.MAP.toString(), Bean.Type.MAP );
		handleMap( collecBean, node.getChildNodes() );

		// register Bean in Map
		container.register( collecBean  );

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
			final String entryNodeName = entry.getNodeName();
			
			if( !entryNodeName.equals( ENTRY ) ) // ignore non <entry> nodes
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
					final Node keyDef = ParserHelper.extractFirstNodeByName( entry.getChildNodes(), KEY);
					
					if(keyDef != null)
					{
						for (int kc = 0 ; kc < keyDef.getChildNodes().getLength() ; ++kc )
						{
							final Node kcNode = keyDef.getChildNodes().item( kc );
							final String keyPropName = collecBean.getId() + "_key";
							
							Property keyRefAsProp = container.handleNode( kcNode, keyPropName );
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
						
						Property keyValueAsProp = container.handleNode( vNode, valuePropName );
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
			
				container.addOrReplaceProperty(prop, collecBean.getProperties() );
			}
		}
	}

	@Override
	public void setBeanContainer(BeanContainer container)
	{
		this.container = container;
	}
}
