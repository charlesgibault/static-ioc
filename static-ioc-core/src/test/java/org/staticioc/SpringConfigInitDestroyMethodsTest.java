/**
 *  Copyright (C) 2012 Charles Gibault
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

import org.junit.Assert;
import org.junit.Test;
import static org.junit.Assert.*;

import org.staticioc.model.*;

/**
 * Unit test for SpringConfigParser
 */
public class SpringConfigInitDestroyMethodsTest extends AbstractSpringParserTest
{
	private final static String TEST_CONTEXT = "src/test/resources/SpringConfigParserTest-InitDestroyMethods.xml";

	public SpringConfigInitDestroyMethodsTest() throws Exception 
	{
		super( TEST_CONTEXT );
	}
	
	/**
	 * Simple Bean loading validation : nobodyBean
	 * 
	 */
	@Test
	public void testNoMethodsDeclared()
	{
		// Retrieve personBean and test properties
		Bean bean = loadedBeans.get("nobodyBean");
		
		Assert.assertNotNull( bean );
		
		// Bean definition checks
		assertEquals("Bean id not properly set", "nobodyBean", bean.getId() );
		assertEquals("Bean class not properly set", "test.Person", bean.getClassName() );
		assertEquals("Bean type not properly set", Bean.Type.SIMPLE , bean.getType() );
		assertFalse("Named bean wrongly considered as anonymous" , bean.isAnonymous() );
		assertFalse("Real bean wrongly considered as abstract" , bean.isAbstract() );
		assertTrue("Constructor args found were not expected", bean.getConstructorArgs().isEmpty() );
	
		assertNull("Bean wrongly has an init-method",bean.getInitMethod());
		assertNull("Bean wrongly has an destroy-method",bean.getDestroyMethod());
	}
	
	/**
	 * Simple Bean loading validation : personBean
	 * 
	 */
	@Test
	public void testMethodsDeclared()
	{
		// Retrieve personBean and test properties
		Bean bean = loadedBeans.get("personBean");
		
		Assert.assertNotNull( bean );
		
		// Bean definition checks
		assertEquals("Bean id not properly set", "personBean", bean.getId() );
		assertEquals("Bean class not properly set", "test.Person", bean.getClassName() );
		assertEquals("Bean type not properly set", Bean.Type.SIMPLE , bean.getType() );
		assertFalse("Named bean wrongly considered as anonymous" , bean.isAnonymous() );
		assertFalse("Real bean wrongly considered as abstract" , bean.isAbstract() );
		assertTrue("Constructor args found were not expected", bean.getConstructorArgs().isEmpty() );
		
		assertEquals("Bean has an incorrect init-method", "initIt", bean.getInitMethod());
		assertEquals("Bean has an incorrect destroy-method", "cleanUp", bean.getDestroyMethod());
	}
	
	/**
	 * Inheritance Bean loading validation : childBean
	 * 
	 */
	@Test
	public void testInheritance()
	{
		// Retrieve personBean and test properties
		Bean bean = loadedBeans.get("childBean");
		
		Assert.assertNotNull( bean );
		
		// Bean definition checks
		assertEquals("Bean id not properly set", "childBean", bean.getId() );
		assertEquals("Bean class not properly set", "test.Person", bean.getClassName() );
		assertEquals("Bean type not properly set", Bean.Type.SIMPLE , bean.getType() );
		assertFalse("Named bean wrongly considered as anonymous" , bean.isAnonymous() );
		assertFalse("Real bean wrongly considered as abstract" , bean.isAbstract() );
		assertTrue("Constructor args found were not expected", bean.getConstructorArgs().isEmpty() );
		
		assertEquals("Bean has an incorrect init-method", "initIt", bean.getInitMethod());
		assertEquals("Bean has an incorrect destroy-method", "cleanUp", bean.getDestroyMethod());
	}
}
