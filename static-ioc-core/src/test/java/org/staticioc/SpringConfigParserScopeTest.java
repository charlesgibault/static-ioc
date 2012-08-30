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
public class SpringConfigParserScopeTest extends AbstractSpringParserTest {
	private final static String TEST_CONTEXT = "src/test/resources/SpringConfigParserTest-Scope.xml";

	public SpringConfigParserScopeTest() throws Exception {
		super(TEST_CONTEXT);		
	}

	/**
	 * Simple singleton loading test
	 */
	@Test
	public void testSingletonBean()
	{
		// Retrieve personBean and test properties
		final Bean userBean1 = loadedBeans.get("userBean1");
		final Bean userBean2 = loadedBeans.get("userBean2");
		final Bean singletonBean = loadedBeans.get("singleton");

		Assert.assertNotNull( userBean1 );
		Assert.assertNotNull( userBean2 );
		Assert.assertNotNull( singletonBean );		
		
		// Bean definition checks
		assertEquals("Bean id not properly set", "singleton", singletonBean.getName() );
		assertEquals("Bean class not properly set", "test.Singleton", singletonBean.getClassName() );
		assertFalse("Real bean wrongly considered as abstract" , singletonBean.isAbstract() );
		assertEquals("Bean not a singleton" , Bean.Scope.SINGLETON, singletonBean.getScope() );

		// Property checks
		final Map<String, Property> userBean1Properties = mapProperties( userBean1.getProperties() );
		final Map<String, Property> userBean2Properties = mapProperties( userBean2.getProperties() );

		checkProperty(userBean1Properties, "singletonBean", null, 	singletonBean.getName() );
		checkProperty(userBean2Properties, "singletonBean", null, 	singletonBean.getName() );		
	}
	
	/**
	 * Simple prototype loading test
	 */
	@Test
	public void testPrototypeBean()
	{
		// Retrieve personBean and test properties
		final Bean userBean1 = loadedBeans.get("userBean1");
		final Bean userBean2 = loadedBeans.get("userBean2");
		final Bean prototypeBean = loadedBeans.get("prototype");

		Assert.assertNotNull( userBean1 );
		Assert.assertNotNull( userBean2 );
		Assert.assertNotNull( prototypeBean );		

		// Prototype bean definition checks
		assertEquals("Bean id not properly set", "prototype", prototypeBean.getName() );
		assertEquals("Bean class not properly set", "test.Prototype", prototypeBean.getClassName() );
		assertFalse("Real bean wrongly considered as abstract" , prototypeBean.isAbstract() );
		assertEquals("Bean is not a prototype" , Bean.Scope.PROTOTYPE, prototypeBean.getScope() );

		// Property checks
		final Map<String, Property> userBean1Properties = mapProperties( userBean1.getProperties() );
		final Map<String, Property> userBean2Properties = mapProperties( userBean2.getProperties() );

		final Property protoRef1 = userBean1Properties.get("prototypeBean");
		final Property protoRef2 = userBean2Properties.get("prototypeBean");
		
		Assert.assertNotNull( protoRef1.getRef() );
		Assert.assertNotNull( protoRef2.getRef() );
		Assert.assertFalse( "Different reference to prototype bean expected", protoRef1.getRef().equals( protoRef2.getRef() ) );

		final Bean injectedPrototypeBean1 = loadedBeans.get( protoRef1.getRef() );
		final Bean injectedPrototypeBean2 = loadedBeans.get( protoRef2.getRef() );
		
		Assert.assertNotNull( injectedPrototypeBean1 );
		Assert.assertNotNull( injectedPrototypeBean2 );
		
		assertEquals("Bean class not properly set", "test.Prototype", injectedPrototypeBean1.getClassName() );
		assertEquals("Bean class not properly set", "test.Prototype", injectedPrototypeBean2.getClassName() );
		
		assertFalse("Real bean wrongly considered as abstract" , injectedPrototypeBean1.isAbstract() );
		assertFalse("Real bean wrongly considered as abstract" , injectedPrototypeBean2.isAbstract() );
		
		assertEquals("Injected bean shouldn't be a prototype anymore" , Bean.Scope.SINGLETON, injectedPrototypeBean1.getScope() );
		assertEquals("Injected bean shouldn't be a prototype anymore" , Bean.Scope.SINGLETON, injectedPrototypeBean2.getScope() );
		
		// Property checks
		final Map<String, Property> protoBean1Properties = mapProperties( injectedPrototypeBean1.getProperties() );
		final Map<String, Property> protoBean2Properties = mapProperties( injectedPrototypeBean2.getProperties() );

		checkProperty(protoBean1Properties, "type", "prototype bean - everyone gets a different instance", null, null );
		checkProperty(protoBean2Properties, "type", "prototype bean - everyone gets a different instance", null, null );		
	}
	
	/**
	 * Simple prototype loading test
	 */
	@Test
	public void testOldScopeDefinition()
	{
		final Bean oldSingleton = loadedBeans.get("oldSingleton");
		final Bean oldPrototype = loadedBeans.get("oldPrototype");
		
		Assert.assertNotNull( oldSingleton );
		Assert.assertNotNull( oldPrototype );
		
		assertEquals("Bean is not a singleton" , Bean.Scope.SINGLETON, oldSingleton.getScope() );
		assertEquals("Bean is not a prototype" , Bean.Scope.PROTOTYPE, oldPrototype.getScope() );
	}
	
	/**
	 * Simple prototype inheritance test
	 */
	@Test
	public void testNonScopeInheritance()
	{
		final Bean prototypeChild = loadedBeans.get("prototypeChild");
		Assert.assertNotNull( prototypeChild );
		assertEquals("Bean is a prototype" , Bean.Scope.SINGLETON, prototypeChild.getScope() );
	}
	
	/**
	 * Complex prototype loading test : inheritance in the middle
	 */
	@Test
	public void testInheritedPrototypeProperty()
	{
		// Retrieve personBean and test properties
		final Bean userBean3 = loadedBeans.get("userBean3");
		final Bean userBean4 = loadedBeans.get("userBean4");
		Assert.assertNotNull( userBean3 );
		Assert.assertNotNull( userBean4 );
		
		// Property checks
		final Map<String, Property> userBean3Properties = mapProperties( userBean3.getProperties() );
		final Property protoRef3 = userBean3Properties.get("prototypeBean");

		final Map<String, Property> userBean4Properties = mapProperties( userBean4.getProperties() );
		final Property protoRef4 = userBean4Properties.get("prototypeBean");
		
		Assert.assertNotNull( protoRef3.getRef() );
		Assert.assertNotNull( protoRef4.getRef() );
		Assert.assertFalse( "Different reference to prototype bean expected", "prototype".equals( protoRef3.getRef() ) );
		Assert.assertFalse( "Different reference to prototype bean expected", "prototype".equals( protoRef4.getRef() ) );

		final Bean injectedPrototypeBean3 = loadedBeans.get( protoRef3.getRef() );
		final Bean injectedPrototypeBean4 = loadedBeans.get( protoRef4.getRef() );
		
		Assert.assertNotNull( injectedPrototypeBean3 );
		Assert.assertNotNull( injectedPrototypeBean4 );
		
		assertEquals("Bean class not properly set", "test.Prototype", injectedPrototypeBean3.getClassName() );
		assertEquals("Bean class not properly set", "test.Prototype", injectedPrototypeBean4.getClassName() );
		
		assertFalse("Real bean wrongly considered as abstract" , injectedPrototypeBean3.isAbstract() );
		assertFalse("Real bean wrongly considered as abstract" , injectedPrototypeBean4.isAbstract() );
		
		assertEquals("Injected bean shouldn't be a prototype anymore" , Bean.Scope.SINGLETON, injectedPrototypeBean3.getScope() );
		assertEquals("Injected bean shouldn't be a prototype anymore" , Bean.Scope.SINGLETON, injectedPrototypeBean4.getScope() );
		
		// Property checks
		final Map<String, Property> protoBean3Properties = mapProperties( injectedPrototypeBean3.getProperties() );
		final Map<String, Property> protoBean4Properties = mapProperties( injectedPrototypeBean4.getProperties() );

		checkProperty(protoBean3Properties, "type", "prototype bean - everyone gets a different instance", null, null );
		checkProperty(protoBean4Properties, "type", "prototype bean - everyone gets a different instance", null, null );
	}
}
