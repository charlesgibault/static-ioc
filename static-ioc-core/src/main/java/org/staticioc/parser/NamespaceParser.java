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

import java.util.List;

public interface NamespaceParser
{
	/**
	 * @return the URI implemented by this namespace parser
	 */
	String getNameSpaceUri();
	
	/**
	 * @return a list of ordered NodeParserPlugin elements to load elements for this namespace
	 */
	List<NodeParserPlugin> getNodeParserPlugins();
	
	/**
	 * @return a list of ordered NodeSupportPlugin elements to load elements for this namespace
	 */
	List<NodeSupportPlugin> getNodeSupportPlugins();
	
	/**
	 * A reference to the bean container
	 * @param container
	 */
	void setBeanParser(BeanParser container);
	
	/**
	 * Declare the prefix for this namespace in the current document
	 * @param prefix
	 */
	void setPrefix(String prefix);

}
