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

import java.util.Collection;

import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.staticioc.container.BeanContainer;
import org.staticioc.model.Bean;
import org.staticioc.model.Property;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public interface BeanParser
{
	/**
	 * Update a collection of properties while registering potential references in the container (for prototype resolution)
	 * @param prop the Property to add
	 * @param properties Collection of existing properties
	 */
	void addOrReplaceProperty(final Property prop, final Collection<Property> properties);
		
	/**
	 * Handle a bean's sub property
	 * @param bean the current bean
	 * @param propName the current property name
	 * @param propChilds then children property that the container must resolve
	 * @throws XPathExpressionException
	 */
	void handleNodes( final Bean bean, final String propName, final NodeList propChilds) throws XPathExpressionException;

	/**
	 * Handle a <property> sub-node
	 * @param spNode
	 * @param propName
	 * @return
	 * @throws XPathExpressionException
	 */
	Property handleNode( final Node subNode, final String propName ) throws XPathExpressionException;
	
	/**
	 * @return the BeanContainer associated with the current BeanParser
	 */
	BeanContainer getBeanContainer();
	
	/**
	 * @return an XPathFactory for node analysis
	 */
	XPathFactory getXPathFactory();
}
