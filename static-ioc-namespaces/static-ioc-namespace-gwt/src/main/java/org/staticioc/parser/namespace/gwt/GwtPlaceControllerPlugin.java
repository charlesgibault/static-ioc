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

import java.util.HashSet;
import java.util.Set;

import javax.xml.xpath.XPathExpressionException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.staticioc.dependency.RunTimeDependency;
import org.staticioc.model.Bean;
import org.staticioc.model.Property;
import org.staticioc.parser.ParserHelper;
import org.w3c.dom.Node;

public class GwtPlaceControllerPlugin extends AbstractGwtNodeSupport 
{
	private static final Logger logger = LoggerFactory.getLogger( GwtPlaceControllerPlugin.class );
			
	@Override
	public String getUnprefixedSupportedNode()
	{
		return PLACE_CONTROLLER;
	}

	@Override
	public Property handleNode( final Node node, final String propName ) throws XPathExpressionException
	{
		String className = ParserHelper.extractAttributeValueAsString(CLASS,  node.getAttributes(), null);
		
		Bean gwtBean = createBean( node, PLACE_CONTROLLER, className != null ? className : PLACE_CONTROLLER_CLASS, null, null);
		
		if( gwtBean == null)
		{
			return null;
		}

		String eventBus = ParserHelper.extractAttributeValueAsString(EVENT_BUS,  node.getAttributes(), EVENT_BUS);

		beanParser.getBeanContainer().addOrReplaceProperty( ParserHelper.buildContructorArgument(0, null, eventBus) , gwtBean.getConstructorArgs() );

		final Set<String> beanDependencies = new HashSet<String>();
		beanDependencies.add(eventBus);
		
		RunTimeDependency dependency = new RunTimeDependency( gwtBean.getId(), beanDependencies);
		logger.debug( "Adding runtime dependency {}", dependency );
		beanParser.getBeanContainer().registerRunTimeDependency( dependency );
		
		// register Bean in Map
		beanParser.getBeanContainer().register( gwtBean  );

		// Wire this bean as a reference
		return ParserHelper.getRef( propName, gwtBean.getId() );
	}
}
