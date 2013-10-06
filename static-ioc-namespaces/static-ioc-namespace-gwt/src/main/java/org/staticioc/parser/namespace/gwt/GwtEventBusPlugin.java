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
package org.staticioc.parser.namespace.gwt;

import javax.xml.xpath.XPathExpressionException;

import org.staticioc.model.Bean;
import org.staticioc.model.Property;
import org.staticioc.parser.ParserHelper;
import org.w3c.dom.Node;

public class GwtEventBusPlugin extends AbstractGwtNodeSupport 
{			
	@Override
	public String getUnprefixedSupportedNode()
	{
		return EVENT_BUS;
	}
	
	@Override
	public Property handleNode( final Node node, final String propName ) throws XPathExpressionException
	{
		Bean gwtBean = createBean( node, EVENT_BUS, DEFAULT_EVENT_BUS_CLASS);
		
		if( gwtBean == null)
		{
			return null;
		}

		String targetInterface = ParserHelper.extractAttributeValueAsString(INTERFACE,  node.getAttributes(), DEFAULT_EVENT_BUS_INTERFACE);
		
		defineGwtCreateStubInterface( gwtBean, targetInterface );
		
		// register Bean in Map
		beanParser.getBeanContainer().register( gwtBean  );

		// Wire this bean as a reference
		return ParserHelper.getRef( propName, gwtBean.getId() );
	}
}
