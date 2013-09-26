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

import org.staticioc.model.Bean;
import org.w3c.dom.Node;

/**
 * Interface for plugins that parse special attributes on a Bean node
 * @author charles
 */
public interface NodeParserPlugin
{
	/**
	 * Handle a specific node to enrich the passed bean
	 * @param bean Bean to enrich
	 * @param node full Node to parse
	 */
	void handleNode( final Bean bean, final Node node ) throws XPathExpressionException;
	
	/**
	 * A reference to the bean parser context
	 * @param parser
	 */
	void setBeanParser(BeanParser parser);

	/**
	 * Declare the prefix for this namespace in the current document
	 * @param prefix
	 */
	void setPrefix(String prefix);
}
