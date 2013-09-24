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

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import org.staticioc.model.*;

/**
 * Unit test for SpringConfigParser
 */
public class SpringConfigParserCollectionTest extends AbstractSpringParserTest
{
	private final static String TEST_CONTEXT = "src/test/resources/SpringConfigParserTest-Collection.xml";
	private final Bean collectBean;
	final Map<String, Property> collectProperties;
			
	public SpringConfigParserCollectionTest() throws Exception 
	{
		this( TEST_CONTEXT );
	}
	
	protected SpringConfigParserCollectionTest(String context) throws Exception 
	{
		super( context );
		collectBean = loadedBeans.get("collectBean");
		
		Assert.assertNotNull( "collectBean not found", collectBean);
		assertEquals("Bean class not properly set", "test.Collection", collectBean.getClassName() );
		
		collectProperties = mapProperties( collectBean.getProperties() );
	}
	
	/**
	 * Collection loading test
	*/
	@Test
    public void testListBeanLoading()
    {
		Property listProp = collectProperties.get("lists");
		Assert.assertNotNull( "lists property not found", listProp);
		
		Bean listBean = loadedBeans.get(  listProp.getRef() );
		Assert.assertNotNull( "lists content not found", listBean);
		
		assertEquals("List content type is not correct", Bean.Type.LIST, listBean.getType() );
		assertTrue("List Bean is not anonymous", listBean.isAnonymous() );
		
		Collection<Property> properties  = listBean.getProperties();
		assertEquals("List content length incorrect", 5, properties.size() );
		
		Iterator<Property> it = properties.iterator();
		checkProperty( it.next(), null, "1", null);
		checkProperty( it.next(), null, "2", null);
		checkProperty( it.next(), null, null, "personBean");
		checkProperty( it.next(), null, null, "country");
		
		String refBean = it.next().getRef();
		Assert.assertNotNull( "Empty reference found", refBean);
		
		Bean listedBean = loadedBeans.get( refBean );
		Assert.assertNotNull( "Referenced bean not found", listBean);
		
		assertEquals("Bean class not properly set", "test.Person", listedBean.getClassName() );
		assertEquals("Bean property count is incorrect", 3, listedBean.getProperties().size() );
    }
	
	/**
	 * Collection loading test
	*/
	@Test
    public void testSetBeanLoading()
    {
		Property setProp = collectProperties.get("sets");
		Assert.assertNotNull( "sets property not found", setProp);
		
		Bean setBean = loadedBeans.get(  setProp.getRef() );
		Assert.assertNotNull( "sets content not found", setBean);
		
		assertEquals("Set content type is not correct", Bean.Type.SET, setBean.getType() );
		assertTrue("Set Bean is not anonymous", setBean.isAnonymous() );
		
		Collection<Property> properties  = setBean.getProperties();
		assertEquals("Set content length incorrect", 3, properties.size() );
    }
	
	/**
	 * Collection loading test
	*/
	@Test
    public void testPropertiesBeanLoading()
    {
		Property propsProp = collectProperties.get("props");
		Assert.assertNotNull( "props property not found", propsProp);
		
		Bean propBean = loadedBeans.get(  propsProp.getRef() );
		Assert.assertNotNull( "props content not found", propBean);
		
		assertEquals("Props content type is not correct", Bean.Type.PROPERTIES, propBean.getType() );
		assertTrue("Props Bean is not anonymous", propBean.isAnonymous() );
		
		Collection<Property> properties  = propBean.getProperties();
		assertEquals("Prop content length incorrect", 3, properties.size() );
		
		// Check content
		Map<String, Property> mappedProps = mapProperties( properties );
		checkProperty(mappedProps, "admin", 		"admin@nospam.com", 	null );
		checkProperty(mappedProps, "support", 		"support@nospam.com", 	null );
		checkProperty(mappedProps, "test", 		"test@nospam.com", 	null );
    }
	
	/**
	 * Collection loading test
	*/
	@Test
    public void testMapBeanLoading()
    {
		Property mapProp = collectProperties.get("maps");
		Assert.assertNotNull( "maps property not found", mapProp);
		
		Bean mapBean = loadedBeans.get(  mapProp.getRef() );
		Assert.assertNotNull( "Maps content not found", mapBean);
		
		assertEquals("Maps content type is not correct", Bean.Type.MAP, mapBean.getType() );
		assertTrue("Map Bean is not anonymous", mapBean.isAnonymous() );
		
		Collection<Property> properties  = mapBean.getProperties();
		assertEquals("Prop content length incorrect", 9, properties.size() );

		// Check content
		Map<String, Property> mappedProps = mapProperties( properties );
		checkProperty(mappedProps, "Key 1", 		"1", 	null );
		checkProperty(mappedProps, "Key 2", 		"42", 	null );
		Property prop = mappedProps.get("Key 2");
		Assert.assertTrue("Map key should be a reference", prop.isKeyRef() );
		
		checkProperty(mappedProps, "Key 3", 		null, 	"personBean" );
		checkProperty(mappedProps, "Key 4", 		"test", null 	 );
		
		prop = mappedProps.get("Key 5");
		Assert.assertNotNull("Reference shouldn't be null", prop.getRef() );
		Assert.assertTrue( "Reference should be anonymous", prop.getRef().startsWith( SpringConfigParser.ANONYMOUS_BEAN_PREFIX) );
		
		prop = mappedProps.get("Key 6");
		Assert.assertNotNull("Reference shouldn't be null", prop.getRef() );
		Assert.assertTrue( "Reference should be anonymous", prop.getRef().startsWith( SpringConfigParser.ANONYMOUS_BEAN_PREFIX) );
			
		checkProperty(mappedProps, "Key 7", 		"2", 	null );
		//checkProperty(mappedProps, "bean_?", 		"woah", 	null ); //keyref = true
		checkProperty(mappedProps, "Key 9", 		null, 	null );
    }
}
