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


public abstract class AbstractNodeParserPlugin implements ParserConstants,  NodeParserPlugin
{
	protected BeanParser beanParser;
	protected String prefix = "";
	
	@Override
	public void setBeanParser(BeanParser parser)
	{
		this.beanParser = parser;
	}

	@Override
	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}
}
