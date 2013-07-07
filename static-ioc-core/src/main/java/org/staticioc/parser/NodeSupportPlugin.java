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
package org.staticioc.parser;

import javax.xml.xpath.XPathExpressionException;

import org.staticioc.model.Property;
import org.w3c.dom.Node;

public interface NodeSupportPlugin
{
	/**
	 * Define which XML keyword this plugin supports
	 * @return name of the supported type
	 */
	String getSupportedNode();
	
	/**
	 * Process a special node type and encapsulate the result as a Property
	 * @param node Node to process
	 * @param name of the property representing the content of the node 
	 * @return a Property encapsulating the loaded node
	 * @throws XPathExpressionException
	 */
	Property handleNode( final Node node, final String name ) throws XPathExpressionException;
	
	/**
	 * A reference to the bean container
	 * @param container
	 */
	void setBeanContainer(BeanParser container);
}
