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

import org.staticioc.container.BeanContainer;
import org.staticioc.model.*;

/**
 * Unit test for SpringConfigParser
 */
public class SpringConfigParserSimpleBeanTest extends AbstractTestSpringParser
{
	private final static String TEST_CONTEXT = "src/test/resources/SpringConfigParserTest-SimpleBean.xml";

	public SpringConfigParserSimpleBeanTest() throws Exception 
	{
		super( TEST_CONTEXT );
	}
	
	protected SpringConfigParserSimpleBeanTest(String context) throws Exception 
	{
		super( context );
	}
	
	/**
	 * Simple Bean loading validation : personBean
	 * 
	 */
	@Test
	public void testSimpleBeanLoading()
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
		
		// Property checks
		final Map<String, Property> properties = mapProperties( bean.getProperties() );
		
		checkProperty(properties, "name", 		"personName", 	null );
		checkProperty(properties, "username", 	"root", 		null );
		checkProperty(properties, "address", 	"address 1",	null );
		checkProperty(properties, "country", 	null, 			"country" );
		checkProperty(properties, "countryRefBean", 	null, 	"country" );
		checkProperty(properties, "countryRefLocal", 	null, 	"country" );
		checkProperty(properties, "birthCountry",null, 			"country" );
		checkProperty(properties, "age", 		"28", 			null );
		checkProperty(properties, "reference", 	null, 			"number10House" );
		checkProperty(properties, "nullProp", 	null, 			null );

		assertNull("Property from innerBean shouldn not be seen here", properties.get("innerBeanOnly"));
	}
	
	/**
	 * Specific check innerBean declaration
	 */
	@Test
	public void testInnerBeanLoading()
	{
		// Retrieve personBean and test properties
		Bean bean = loadedBeans.get("personBean");

		Assert.assertNotNull( bean );
		// Property checks
		final Map<String, Property> properties = mapProperties( bean.getProperties() );
				
		Property innerBeanRefProp = properties.get("innerBean");
		assertNotNull("InnerBean property missing ", innerBeanRefProp); 
		assertNotNull("InnerBean property is not a reference ", innerBeanRefProp.getRef() ); 
		
		Bean innerBean = loadedBeans.get(  innerBeanRefProp.getRef() );
		assertNotNull("InnerBean not found", innerBean);
		assertEquals("Bean class not properly set", "test.innerBean", innerBean.getClassName() );
		assertTrue("Inner Bean should be anonymous", innerBean.isAnonymous() );
		
		// Inner bean should have a prototype scope
		//assertEquals("Inner Bean scope should be prototype", Bean.Scope.PROTOTYPE, bean.getScope() );
		
		// Property checks
		final Map<String, Property> innerProperties = mapProperties( innerBean.getProperties() );
		checkProperty(innerProperties, "innerBeanOnly", "Embedded property", 	null );
		
		assertNull("Property from outer Bean shouldn not be seen here", innerProperties.get("age"));
	}
	
	/**
	 * Simple Bean loading validation : personBean
	 * 
	 */
	@Test
	public void testAliasBeanLoading()
	{
		// Retrieve personBean and test properties
		Bean bean1a = loadedBeans.get("personBean");
		Bean bean1b = loadedBeans.get("myPersonBean");
		
		Bean bean2a = loadedBeans.get("country");
		Bean bean2b = loadedBeans.get("myCountry");
		
		Bean bean = loadedBeans.get("personBean");
		
		Assert.assertNotNull( bean1a );
		Assert.assertNull( bean1b ); // alias bean should not be referenced
		Assert.assertNotNull( bean2a );
		Assert.assertNull( bean2b ); // alias bean should not be referenced
		
		Assert.assertNotNull( bean );
		
		// Property checks
		final Map<String, Property> properties = mapProperties( bean.getProperties() );
		checkProperty(properties, "namedCountry", 	null, "country" ); // Reference to myCountry should be resolved to bean's id country 
	}

	/**
	 * Anonymous bean loading
	 */
	@Test
	public void testAnonymousBean()
	{
		boolean anonymousBeanFound = false;
		
		for( String name : loadedBeans.keySet() )
		{
			if( name.startsWith( BeanContainer.ANONYMOUS_BEAN_PREFIX) )
			{
				final Bean bean = loadedBeans.get(name);

				// Our test set only contains one that has class "test.AnonymousPerson"
				if( ! "test.AnonymousPerson".equals( bean.getClassName()) )
				{
					continue;
				}

				anonymousBeanFound = true;

				// Bean definition checks
				assertTrue("Named bean not declared as anonymous" , bean.isAnonymous() );
				assertEquals("Bean type not properly set", Bean.Type.SIMPLE , bean.getType() );
				assertFalse("Real bean wrongly considered as abstract" , bean.isAbstract() );
				assertTrue("Constructor args found were not expected", bean.getConstructorArgs().isEmpty() );
				
				// Property checks
				final Map<String, Property> properties = mapProperties( bean.getProperties() );
				
				checkProperty(properties, "name", 	"Tony", null );
				checkProperty(properties, "age", 	"53", 	null );
				checkProperty(properties, "house", 	null, 	"number10House" );
			}
		}
		
		assertTrue("No anonymous bean found", anonymousBeanFound);
	}

	/**
	 * Bean with spring p:prop notation loading
	 * <bean class="test.House" name="number10House" p:name="10 Downing Street" p:p-ref="personBean" p:person-ref="personBean"/>
	 */
	@Test
	public void testSpringPNotation()
	{
		// Retrieve personBean and test properties
		Bean bean = loadedBeans.get("number10House");
		Assert.assertNotNull( bean );
		
		// Bean definition checks
		assertEquals("Bean id not properly set", "number10House", bean.getId() );
		assertEquals("Bean class not properly set", "test.House", bean.getClassName() );
		assertEquals("Bean type not properly set", Bean.Type.SIMPLE , bean.getType() );
		assertFalse("Named bean wrongly considered as anonymous" , bean.isAnonymous() );
		assertFalse("Real bean wrongly considered as abstract" , bean.isAbstract() );
		assertTrue("Constructor args found were not expected", bean.getConstructorArgs().isEmpty() );

		// Property checks
		final Map<String, Property> properties = mapProperties( bean.getProperties() );

		checkProperty(properties, "name", 		"10 Downing Street", 	null );
		checkProperty(properties, "p",  		null, 	"personBean" );
		checkProperty(properties, "person",  	null, 	"personBean" );
	}

	/**
	 * Bean with constructor arguments loading
	 */
	@Test
	public void testConstructorArgs()
	{
		// Retrieve personBean and test properties
		Bean bean = loadedBeans.get("country");
		Assert.assertNotNull( bean );
		
		// Bean definition checks
		assertEquals("Bean id not properly set", "country", bean.getId() );
		assertEquals("Bean alias not properly set", "myCountry", bean.getAlias() );
		assertEquals("Bean class not properly set", "test.Country", bean.getClassName() );
		assertEquals("Bean type not properly set", Bean.Type.SIMPLE , bean.getType() );
		assertFalse("Named bean wrongly considered as anonymous" , bean.isAnonymous() );
		assertFalse("Real bean wrongly considered as abstract" , bean.isAbstract() );
		assertEquals( "Constructor args not found were expected", 2, bean.getConstructorArgs().size() );
		
		// Property checks
		final Map<String, Property> constructorArgs = mapProperties( bean.getConstructorArgs() );
		
		checkProperty(constructorArgs, SpringConfigParser.CONSTRUCTOR_ARGS + "0", 		"42", 	null );
		checkProperty(constructorArgs, SpringConfigParser.CONSTRUCTOR_ARGS + "1",  		"7500000", null, "java.util.Integer" );
	}
}
