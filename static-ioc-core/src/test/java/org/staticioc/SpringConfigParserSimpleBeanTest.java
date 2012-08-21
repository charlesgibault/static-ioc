package org.staticioc;

import org.junit.Assert;
import org.junit.Test;
import static org.junit.Assert.*;

import java.util.Map;
import org.staticioc.model.*;

/**
 * Unit test for SpringConfigParser
 */
public class SpringConfigParserSimpleBeanTest extends AbstractSpringParserTest
{
	private final static String TEST_CONTEXT = "src/test/resources/SpringConfigParserTest-SimpleBean.xml";

	public SpringConfigParserSimpleBeanTest() throws Exception 
	{
		super( TEST_CONTEXT );
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
		assertEquals("Bean id not properly set", "personBean", bean.getName() );
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
		checkProperty(properties, "birthCountry",null, 			"country" );
		checkProperty(properties, "age", 		"28", 			null );
		checkProperty(properties, "reference", 	null, 			"number10House" );
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
			if( name.startsWith( SpringConfigParser.ANONYMOUS_BEAN_PREFIX) )
			{
				anonymousBeanFound = true;
				final Bean bean = loadedBeans.get(name); // Our test set only contains one
				
				// Bean definition checks
				assertTrue("Named bean not declared as anonymous" , bean.isAnonymous() );
				assertEquals("Bean class not properly set", "test.AnonymousPerson", bean.getClassName() );
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
		assertEquals("Bean id not properly set", "number10House", bean.getName() );
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
		assertEquals("Bean id not properly set", "country", bean.getName() );
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
