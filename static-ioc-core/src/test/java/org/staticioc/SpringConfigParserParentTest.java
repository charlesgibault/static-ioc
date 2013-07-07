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

import org.junit.Assert;
import org.junit.Test;
import static org.junit.Assert.*;

import java.util.Map;
import org.staticioc.model.*;

/**
 * Unit test for SpringConfigParser to test parent resolution and import
 * loading. Note : a cyclic dependency is present in the TEST_CONTEXT
 */
public class SpringConfigParserParentTest extends AbstractSpringParserTest {
	private final static String TEST_CONTEXT = "src/test/resources/SpringConfigParserTest-Parent.xml";

	public SpringConfigParserParentTest() throws Exception {
		super(TEST_CONTEXT);		
	}

	/**
	 * Parent loading test
	 */
	@Test
	public void testSimpleParentBeanLoading()
	{
		// Retrieve personBean and test properties
		final Bean parentBean = loadedBeans.get("beanParent");
		final Bean abstractBean = loadedBeans.get("abstractBean");
		final Bean realBean = loadedBeans.get("realBean");

		Assert.assertNotNull( parentBean );
		Assert.assertNotNull( abstractBean );
		Assert.assertNotNull( realBean );
				
		// Bean definition checks
		assertEquals("Bean id not properly set", "beanParent", parentBean.getId() );
		assertEquals("Bean id not properly set", "abstractBean", abstractBean.getId() );
		assertEquals("Bean id not properly set", "realBean", realBean.getId() );

		assertEquals("Bean class not properly set", "test.Bean", parentBean.getClassName() );
		assertEquals("Bean class not properly set", "test.AnotherBean", abstractBean.getClassName() );
		assertEquals("Bean class not properly set", "test.AnotherBean", realBean.getClassName() );

		assertFalse("Real bean wrongly considered as abstract" , parentBean.isAbstract() );
		assertTrue("Abstract bean wrongly considered as real" , abstractBean.isAbstract() );
		assertFalse("Real bean wrongly considered as abstract" , realBean.isAbstract() );

		assertEquals( "Constructor args not found were expected", 2, parentBean.getConstructorArgs().size() );
		assertEquals( "Constructor args not found were expected", 2, abstractBean.getConstructorArgs().size() );
		assertEquals( "Constructor args not found were expected", 3, realBean.getConstructorArgs().size() );

		// Property checks
		final Map<String, Property> parentProperties = mapProperties( parentBean.getProperties() );
		final Map<String, Property> abstractProperties = mapProperties( abstractBean.getProperties() );
		final Map<String, Property> realProperties = mapProperties( realBean.getProperties() );

		checkProperty(parentProperties, "nation", 	"import", 	null );
		checkProperty(abstractProperties, "nation", "import", 	null );
		checkProperty(realProperties, "nation", 	"reality", 	null );

		Assert.assertNull( parentProperties.get("address") );
		checkProperty(abstractProperties, "address", "Somewhere over the rainbow", 	null );
		checkProperty(realProperties, "address", 	"Somewhere over the rainbow", 	null );

		Assert.assertNull( parentProperties.get("type") );
		Assert.assertNull( abstractProperties.get("type") );
		checkProperty(realProperties, "type", "Cold hard concrete bean", 	null );	

		// Constructor arguments inheritance checks
		final Map<String, Property> constructorArgsParent = mapProperties( parentBean.getConstructorArgs() );
		final Map<String, Property> constructorArgsAbstract = mapProperties( abstractBean.getConstructorArgs() );
		final Map<String, Property> constructorArgsReal = mapProperties( realBean.getConstructorArgs() );

		checkProperty(constructorArgsParent, SpringConfigParser.CONSTRUCTOR_ARGS + "0", "00", 	null );
		checkProperty(constructorArgsParent, SpringConfigParser.CONSTRUCTOR_ARGS + "1", "10", null, "java.util.Integer" );

		checkProperty(constructorArgsAbstract, SpringConfigParser.CONSTRUCTOR_ARGS + "0", "00", 	null );
		checkProperty(constructorArgsAbstract, SpringConfigParser.CONSTRUCTOR_ARGS + "1", "10", null, "java.util.Integer" );

		checkProperty(constructorArgsReal, SpringConfigParser.CONSTRUCTOR_ARGS + "0", "30", null, "java.util.Integer" );
		checkProperty(constructorArgsReal, SpringConfigParser.CONSTRUCTOR_ARGS + "1", "40", null, null );
		checkProperty(constructorArgsReal, SpringConfigParser.CONSTRUCTOR_ARGS + "2", "45", null );
	}

	/**
	 * Collection loading test
	 */
	@Test
	public void testCyclicParentBeanLoading() {
		Assert.assertNull( loadedBeans.get("cycleChildren") );
		Assert.assertNull( loadedBeans.get("cycleParent") );
		Assert.assertNull( loadedBeans.get("cycleGrandParent") );		
	}

	/**
	 * Imported bean loading test
	 */
	@Test
	public void testSimpleImportBeanLoading() {
		Assert.assertNotNull( loadedBeans.get("children") ); // Existence test. More detailed test below
	}

	/**
	 * imported bean loading test with cross file parent dependencies
	 * 
	 */
	@Test
	public void testParentImportedBeanLoading()
	{
		// Retrieve personBean and test properties
		final Bean parentBean = loadedBeans.get("parent");
		final Bean grandParentBean = loadedBeans.get("grandParent");
		final Bean childrenBean = loadedBeans.get("children");

		Assert.assertNotNull( parentBean );
		Assert.assertNotNull( grandParentBean );
		Assert.assertNotNull( childrenBean );
		
		// Bean definition checks
		assertEquals("Bean id not properly set", "parent", parentBean.getId() );
		assertEquals("Bean id not properly set", "grandParent", grandParentBean.getId() );
		assertEquals("Bean id not properly set", "children", childrenBean.getId() );

		assertEquals("Bean class not properly set", "parent.Bean", parentBean.getClassName() );
		assertEquals("Bean class not properly set", "test.AnotherBean", grandParentBean.getClassName() );
		assertEquals("Bean class not properly set", "children.Bean", childrenBean.getClassName() );

		assertFalse("Real bean wrongly considered as abstract" , parentBean.isAbstract() );
		assertFalse("Real bean wrongly considered as abstract" , grandParentBean.isAbstract() );
		assertFalse("Real bean wrongly considered as abstract" , childrenBean.isAbstract() );

		assertEquals( "Constructor args not found were expected", 3, parentBean.getConstructorArgs().size() );
		assertEquals( "Constructor args not found were expected", 3, grandParentBean.getConstructorArgs().size() );
		assertEquals( "Constructor args not found were expected", 3, childrenBean.getConstructorArgs().size() );
	}
	
	/**
	 * imported bean loading test with cross file parent dependencies
	 * 
	 */
	@Test
	public void testNamedParentRef()
	{
		final Bean namedParentBean = loadedBeans.get("namedParentBean");
		final Bean namedImportedParentBean = loadedBeans.get("namedImportedParentBean");
		
		Assert.assertNotNull( namedParentBean );
		Assert.assertNotNull( namedImportedParentBean );
		
		assertEquals("Bean class not properly set", "test.Bean", namedParentBean.getClassName() );
		assertEquals("Bean class not properly set", "parent.Bean", namedImportedParentBean.getClassName() );
	}
}
