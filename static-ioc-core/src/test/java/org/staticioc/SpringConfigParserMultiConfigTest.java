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

import java.util.Map;
import org.staticioc.model.*;

/**
 * Unit test for SpringConfigParser to test parent and prototype resolution when configuration are loaded together (without import)
 * TODO implement the test 
 */
public class SpringConfigParserMultiConfigTest extends AbstractSpringParserTest {
	private final static String[] TEST_CONTEXT = {
		"src/test/resources/SpringConfigParserTest-MultiConfig.xml",
		"src/test/resources/SpringConfigParserTest-SimpleBean.xml",
		"src/test/resources/SpringConfigParserTest-Scope.xml"
		};
	
	public SpringConfigParserMultiConfigTest() throws Exception {
		super(TEST_CONTEXT);		
	}

	/**
	 * Multiple files loaded at once, with parent and scope dependencies
	 * 
	 */
	@Test
	public void testExternalScopedBeanLoading()
	{
		final Bean userBean7 = loadedBeans.get("userBean7");
		final Bean userBean2 = loadedBeans.get("userBean2");
		final Bean prototypeBean = loadedBeans.get("prototype");
		
		Assert.assertNotNull( userBean7 );
		Assert.assertNotNull( userBean2 );
		Assert.assertNotNull( prototypeBean );		

		// Prototype bean definition checks
		assertEquals("Bean id not properly set", "prototype", prototypeBean.getName() );
		assertEquals("Bean is not a prototype" , Bean.Scope.PROTOTYPE, prototypeBean.getScope() );

		// Property checks
		final Map<String, Property> userBean7Properties = mapProperties( userBean7.getProperties() );
		final Map<String, Property> userBean2Properties = mapProperties( userBean2.getProperties() );

		final Property protoRef7 = userBean7Properties.get("prototypeBean");
		final Property protoRef2 = userBean2Properties.get("prototypeBean");
		
		Assert.assertNotNull( protoRef7.getRef() );
		Assert.assertNotNull( protoRef2.getRef() );
		Assert.assertFalse( "Different reference to prototype bean expected", protoRef7.getRef().equals( protoRef2.getRef() ) );

		final Bean injectedPrototypeBean7 = loadedBeans.get( protoRef7.getRef() );
		final Bean injectedPrototypeBean2 = loadedBeans.get( protoRef2.getRef() );
		
		Assert.assertNotNull( injectedPrototypeBean7 );
		Assert.assertNotNull( injectedPrototypeBean2 );
		
		assertEquals("Bean class not properly set", "test.Prototype", injectedPrototypeBean7.getClassName() );
		assertEquals("Bean class not properly set", "test.Prototype", injectedPrototypeBean2.getClassName() );
		
		assertFalse("Real bean wrongly considered as abstract" , injectedPrototypeBean7.isAbstract() );
		assertFalse("Real bean wrongly considered as abstract" , injectedPrototypeBean2.isAbstract() );
		
		assertEquals("Injected bean shouldn't be a prototype anymore" , Bean.Scope.SINGLETON, injectedPrototypeBean7.getScope() );
		assertEquals("Injected bean shouldn't be a prototype anymore" , Bean.Scope.SINGLETON, injectedPrototypeBean2.getScope() );
		
		// Property checks
		final Map<String, Property> protoBean1Properties = mapProperties( injectedPrototypeBean7.getProperties() );
		final Map<String, Property> protoBean2Properties = mapProperties( injectedPrototypeBean2.getProperties() );

		checkProperty(protoBean1Properties, "type", "prototype bean - everyone gets a different instance", null, null );
		checkProperty(protoBean2Properties, "type", "prototype bean - everyone gets a different instance", null, null );	
	}
	
	/**
	 * Multiple files loaded at once, with parent and scope dependencies
	 * 
	 */
	@Test
	public void testParentImportedBeanLoading()
	{
		// Retrieve personBean and test properties
		final Bean foreignChildBean = loadedBeans.get("foreignChildBean");

		Assert.assertNotNull( foreignChildBean );
		
		// Bean definition checks
		assertEquals("Bean id not properly set", "foreignChildBean", foreignChildBean.getName() );
		assertEquals("Bean class not properly set", "test.Person", foreignChildBean.getClassName() );		
		assertFalse("Real bean wrongly considered as abstract" , foreignChildBean.isAbstract() );
		
		// Property checks
		final Map<String, Property> properties = mapProperties( foreignChildBean.getProperties() );
				
		checkProperty(properties, "name", 		"personName", 	null );
		checkProperty(properties, "username", 	"root", 		null );
	}
}
