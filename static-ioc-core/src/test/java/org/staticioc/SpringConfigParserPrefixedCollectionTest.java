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
package org.staticioc;

/**
 * Unit test for SpringConfigParser
 */
public class SpringConfigParserPrefixedCollectionTest extends SpringConfigParserCollectionTest
{
	private final static String TEST_CONTEXT = "src/test/resources/SpringConfigParserTest-PrefixedCollection.xml";

	public SpringConfigParserPrefixedCollectionTest() throws Exception 
	{
		super( TEST_CONTEXT );
	}
}
