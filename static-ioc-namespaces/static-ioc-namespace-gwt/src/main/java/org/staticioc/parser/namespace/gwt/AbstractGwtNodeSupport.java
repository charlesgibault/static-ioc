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

import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.staticioc.model.Bean;
import org.staticioc.parser.AbstractNodeSupportPlugin;
import org.staticioc.parser.ParserHelper;
import org.w3c.dom.Node;

abstract class AbstractGwtNodeSupport extends AbstractNodeSupportPlugin implements GwtNamespaceConstants
{
	private static final Logger logger = LoggerFactory.getLogger( AbstractGwtNodeSupport.class );

	
	/**
	 * Create a Bean with basic attributes loaded from the Node's attribute 
	 * 
	 * @param node to parse
	 * @param defaultClass to define if no <class/> attribute is found
	 * @return a Bean whose id, alias, anonymous state have been read from the Node's attribute with factory bean/method set on Gwt.create()
	 */
	protected Bean createBean(final Node node, final String defaultId, final String defaultClass)
	{
		return createBean( node, defaultId, defaultClass, GwtNamespaceConstants.FACTORY_BEAN, CREATE);
	}

	/**
	 * Create a Bean with basic attributes loaded from the Node's attribute 
	 * 
	 * @param node to parse
	 * @param defaultClass to define if no <class/> attribute is found
	 * @param factoryBean to use
	 * @param factoryMethod to use
	 * @return a Bean whose id, alias, anonymous state have been read from the Node's attribute, with defined factory bean/method 
	 */
	protected Bean createBean(final Node node, final String defaultId, final String defaultClass, final String factoryBean, final String factoryMethod)
	{
		if (node == null) // if Bean doesn't exist, there's nothing to do
		{
			return null;
		}

		Pair<String, String> ids = ParserHelper.extractBeanId(node);

		String id = ids.getLeft() != null ? ids.getLeft() : defaultId ;
		final String alias = ids.getRight();

		boolean isAnonymous = false;
		if( id == null )
		{
			id = beanParser.getBeanContainer().generateAnonymousBeanId(); 
			isAnonymous = true;
		}

		//grab its property : 
		String className = ParserHelper.extractAttributeValueAsString(CLASS, node.getAttributes(), defaultClass);

		if( className == null)
		{
			logger.warn("Couldn't determine class for Bean {}", id);
			return null;
		}

		Bean gwtBean = new Bean(id, className, isAnonymous);
		gwtBean.setAlias(alias);
		gwtBean.setFactoryBean(factoryBean);
		gwtBean.setFactoryMethod(factoryMethod);

		return gwtBean;
	}

	/**
	 * Setup first constructor argument of the Bean
	 * 
	 * @param gwtBean whose first constructor argument is to be set
	 * @param value to be passed as first argument to the contructor
	 */
	protected void defineGwtCreateStubInterface( final Bean gwtBean, final String value)
	{
		// Setup constructor args
		beanParser.getBeanContainer().addOrReplaceProperty( ParserHelper.buildContructorArgument(0, value + CLASS_SUFFIX, null) , gwtBean.getConstructorArgs() );
	}

}
