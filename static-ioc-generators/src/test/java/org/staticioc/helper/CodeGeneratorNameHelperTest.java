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
package org.staticioc.helper;

import junit.framework.Assert;

import org.junit.Test;

public class CodeGeneratorNameHelperTest {
	/**
	 * Check a correct target language
	 */
	@Test
	public void testGetGeneratorClass()
	{
		Assert.assertEquals("Wrong target Code generator", "org.staticioc.generator.JavaCodeGenerator", CodeGeneratorNameHelper.getGeneratorClass( "JAVA") );
		Assert.assertEquals("Wrong target Code generator", "org.staticioc.generator.JavaCodeGenerator", CodeGeneratorNameHelper.getGeneratorClass( "java") );
	}
	
	/**
	 * Check a non existing target language
	 */
	@Test(expected = IllegalArgumentException.class) 
	public void testNonExistingGeneratorClass()
	{
		CodeGeneratorNameHelper.getGeneratorClass( "esperanto");
	}
	
	/**
	 * Check a non supported target language
	 */
	@Test(expected = IllegalArgumentException.class) 
	public void testUnsupportedGeneratorClass()
	{
		CodeGeneratorNameHelper.getGeneratorClass( "cpp");
	}
}
