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
public class SpringConfigParserFactoryBeanTest extends AbstractSpringParserTest
{
	private final static String TEST_CONTEXT = "src/test/resources/SpringConfigParserTest-FactoryBean.xml";

	public SpringConfigParserFactoryBeanTest() throws Exception 
	{
		super( TEST_CONTEXT );
	}
	
	/**
	 * Factory bean loading and ordering check
	 */
	@Test
	public void testFactoryBeanLoading()
	{
		// Retrieve personBean and test properties
		Bean product = loadedBeans.get("product");
		Bean subProduct = loadedBeans.get("subProduct");
		Bean rpcService = loadedBeans.get("rpcService");
		Bean productFactory = loadedBeans.get("productFactory");
		
		Assert.assertNotNull( product );
		Assert.assertNotNull( subProduct );
		Assert.assertNotNull( rpcService );
		Assert.assertNotNull( productFactory );
		
		// Bean definition checks
		assertEquals("Bean id not properly set", "product", product.getId() );
		assertEquals("Bean class not properly set", "test.Product", product.getClassName() );
		assertEquals("Bean type not properly set", Bean.Type.SIMPLE , product.getType() );
		assertEquals("Factory bean not properly set", "productFactory" , product.getFactoryBean() );
		assertEquals("Factory method not properly set", "createProduct" , product.getFactoryMethod() );		
		assertTrue("Constructor args found were not expected", product.getConstructorArgs().isEmpty() );

		assertEquals("Bean id not properly set", "subProduct", subProduct.getId() );
		assertEquals("Bean class not properly set", "test.SubProduct", subProduct.getClassName() );
		assertEquals("Bean type not properly set", Bean.Type.SIMPLE , subProduct.getType() );
		assertEquals("Factory bean not properly set", "productFactory" , subProduct.getFactoryBean() );
		assertEquals("Factory method not properly set", "createProduct" , subProduct.getFactoryMethod() );		
		assertTrue("Constructor args found were not expected", subProduct.getConstructorArgs().isEmpty() );
				
		assertEquals("Bean id not properly set", "rpcService", rpcService.getId() );
		assertEquals("Bean class not properly set", "test.RpcService", rpcService.getClassName() );
		assertEquals("Bean type not properly set", Bean.Type.SIMPLE , rpcService.getType() );
		assertEquals("Factory bean not properly set", "GWT" , rpcService.getFactoryBean() );
		assertEquals("Factory method not properly set", "create" , rpcService.getFactoryMethod() );		
		assertEquals("Constructor args not found were expected", rpcService.getConstructorArgs().size(), 1 );
	
		assertEquals("Bean id not properly set", "productFactory", productFactory.getId() );
		assertEquals("Bean class not properly set", "test.ProductFactory", productFactory.getClassName() );
		assertEquals("Bean type not properly set", Bean.Type.SIMPLE , productFactory.getType() );
		assertNull("Factory bean found were not expected" , productFactory.getFactoryBean() );
		assertNull("Factory method found were not expected", productFactory.getFactoryMethod() );		
		assertTrue("Constructor args found were not expected", productFactory.getConstructorArgs().isEmpty() );
		
		// Ordering checks
		assertEquals("Factory Bean expected to be inited before its product", 1, product.compareTo(productFactory) );
		assertEquals("Factory Bean expected to be inited before its product", -1, productFactory.compareTo(product) );
		
		assertEquals("Factory Bean expected to be inited before its product", 1, subProduct.compareTo(productFactory) );
		assertEquals("Factory Bean expected to be inited before its product", -1, productFactory.compareTo(subProduct) );
		
		assertTrue("Alphabetical ordering not respected when no dependency is found", subProduct.compareTo(product) > 0 );
		assertTrue("Alphabetical ordering not respected when no dependency is found", product.compareTo(subProduct) < 0 );
			
		assertTrue("Alphabetical ordering not respected when no dependency is found", product.compareTo(rpcService) < 0 );
		assertTrue("Alphabetical ordering not respected when no dependency is found", rpcService.compareTo(product) > 0);
		
		assertTrue("Alphabetical ordering not respected when no dependency is found", productFactory.compareTo(rpcService) < 0);
		assertTrue("Alphabetical ordering not respected when no dependency is found", rpcService.compareTo(productFactory) > 0);
	}
}
