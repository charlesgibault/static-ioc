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
package org.staticioc.dependency;

import javax.xml.xpath.XPathExpressionException;

import org.staticioc.container.BeanContainer;
import org.staticioc.model.Bean;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

/**
 * Callback interface used during Bean dependency resolution
 */
public interface ResolvedBeanCallback
{
	/**
	 * Callback method when a new Bean is resolved
	 * @param bean newly resolved Bean
	 * @param beanNode XML node matching this new Bean
	 * @param beanAttributes Map of the Bean's attributes 
	 * @param isAnonymous whether the bean is anonymous or not
	 * @param container is the current aggregated BeanContainer used for Bean resolution
	 *        (in case of import, it may not be the same as the one used when registring the dependency)
	 * @return the id of the Bean after resolution
	 * @throws XPathExpressionException
	 */
	String onResolve(Bean bean, Node beanNode, NamedNodeMap beanAttributes, boolean isAnonymous, BeanContainer container ) throws XPathExpressionException;
}
