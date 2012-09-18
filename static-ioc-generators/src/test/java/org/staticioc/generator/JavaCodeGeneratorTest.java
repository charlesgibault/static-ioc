package org.staticioc.generator;

import static org.junit.Assert.assertEquals;

import org.junit.Assert;
import org.junit.Test;
import org.staticioc.generator.CodeGenerator;
import org.staticioc.generator.JavaCodeGenerator;
import org.staticioc.model.Bean;
import org.staticioc.model.Property;

/**
 * Unit test for testing the Java Code Generator
 */
public class JavaCodeGeneratorTest
{
	CodeGenerator generator = new JavaCodeGenerator();
	StringBuilder result = new StringBuilder();
	
	public JavaCodeGeneratorTest()
	{
		generator.setOutput(result);
	}
	
	private String normalizeCode( String code)
	{
		return code.replaceAll("\\s+", " ").trim();
	}
	
	/**
	 * Test that paths for generated code matches with code package
	 */
	@Test
	public void testGetFilePath()
	{
		assertEquals("Incorrect path for class with package", "org/static/ioc/testClass", generator.getFilePath( "org.static.ioc.testClass" ) );
		assertEquals("Incorrect path for class without package", "testClass", generator.getFilePath( "testClass" ) );
		assertEquals("Incorrect path for empty class", "", generator.getFilePath( "" ) );
		assertEquals("Incorrect path for null class", "", generator.getFilePath( null ) );
	}
	
	/**
	 * Test that paths for generated code matches with code package
	 */
	@Test
	public void testGetPackageName()
	{
		assertEquals("Incorrect package for class with package", "org.static.ioc", generator.getPackageName( "org.static.ioc.testClass" ) );
		assertEquals("Incorrect package for class without package", "", generator.getPackageName( "testClass" ) );
		assertEquals("Incorrect package for empty class", "", generator.getPackageName( "" ) );
		assertEquals("Incorrect package for null class", "", generator.getPackageName( null ) );
	}

	/**
	 * Test that paths for generated code matches with code package
	 */
	@Test
	public void testgetClassName()
	{
		assertEquals("Incorrect class name for class with package", "testClass", generator.getClassName( "org.static.ioc.testClass" ) );
		assertEquals("Incorrect class name for class without package", "testClass", generator.getClassName( "testClass" ) );
		assertEquals("Incorrect class name for empty class", "", generator.getClassName( "" ) );
		assertEquals("Incorrect class name for null class", "", generator.getClassName( null ) );
	}

	/**
	 * Test that generated java file will end by .java
	 */
	@Test
	public void testgetDefaultFileExtension()
	{
		assertEquals("Incorrect file extension", ".java", generator.getDefaultSourceFileExtension() );
	}
	
	/**
	 * Simple class structure test
	 */
	@Test
	public void testSimpleClassDefinition()
	{
		final String packageName = "org.staticioc.test";
		final String className = "TestClass";

		Bean bean = new Bean("bean", "org.staticioc.model.Bean");
		Property property = new Property( "name", "value", null);
		bean.getProperties().add(property);
		
		generator.initPackage( packageName );
		
		generator.initClass(className);

		generator.declareBean(bean);
		
		generator.initConstructor( className );	
		
		generator.instantiateBean(bean);
		generator.declareProperty(bean, property);
		
		generator.closeConstructor( className );
		
		generator.initDestructor( className );
		generator.closeDestructor( className );
		
		generator.closeClass(className);
		
		generator.closePackage(packageName);
	
		final String expectedResult = normalizeCode( "package " + packageName + ";\n" 
					+ "public class " + className + " {\n"
					+ "public final org.staticioc.model.Bean bean;\n"
					+ "public " + className + "() {\n"
					+ "bean = new org.staticioc.model.Bean();\n"
					+ "bean.setName( \"value\" );\n"
					+"}\n"
					+ "public void destroyContext() { }\n"
					+ "}" );
		
		Assert.assertEquals( "Incorrect class definition", expectedResult, normalizeCode( result.toString() ) );
	}
	
	/**
	 * Anonymous beans visibility tests
	 */
	@Test
	public void testAnonymousBeansDeclaration()
	{
		Bean bean = new Bean("bean", "org.staticioc.model.Bean", false);		
		generator.declareBean(bean);
		
		Bean anonymousBean = new Bean("anonymousBean", "org.staticioc.model.Bean", true);
		generator.declareBean(anonymousBean);

		Assert.assertEquals( "Incorrect bean definition", normalizeCode(
					"public final org.staticioc.model.Bean bean;\n"
				+  	"private final org.staticioc.model.Bean anonymousBean;\n"),
				 	normalizeCode( result.toString() ) );
	}

	/**
	 * Check that constructor arguments are properly declared
	 */
	@Test
	public void testConstructorArgsDeclaration()
	{
		Bean bean = new Bean("bean", "org.staticioc.model.Bean");

		Property arg1 = new Property( "name", "value", null);
		Property arg2 = new Property( "className", "java.util.LinkedList<String>", null);
		
		bean.getConstructorArgs().add(arg1);
		bean.getConstructorArgs().add(arg2);
		
		generator.instantiateBean(bean);
		
		Assert.assertEquals( "Incorrect bean with constructor args instantation", normalizeCode(
				"bean = new org.staticioc.model.Bean(\"value\", \"java.util.LinkedList<String>\");\n" ),
			 	normalizeCode( result.toString() ) );
	}

	/**
	 * Test that String / Integer / Double / true/false / typed values conversion are properly working
	 */
	@Test
	public void testValueConversions()
	{
		Bean bean = new Bean("bean", "org.staticioc.model.Bean");
		
		Property textValue = new Property( "name", "value", null);
		generator.declareProperty(bean, textValue);
		
		Property intValue = new Property( "name", "3", null);
		generator.declareProperty(bean, intValue);
		
		Property intAsTextValue = new Property( "name", "3", null);
		intAsTextValue.setType( "String");
		generator.declareProperty(bean, intAsTextValue);
		
		Property fpValue = new Property( "name", "3.0", null);
		Property floatValue = new Property( "name", "3.0f", null);
		Property doubleValue = new Property( "name", "3.0d", null);
		generator.declareProperty(bean, fpValue);
		generator.declareProperty(bean, floatValue);
		generator.declareProperty(bean, doubleValue);
		
		Property booleanPositiveValue = new Property( "name", "TruE", null);
		Property booleanNegativeValue = new Property( "name", "FALSE", null);
		generator.declareProperty(bean, booleanPositiveValue);
		generator.declareProperty(bean, booleanNegativeValue);
	
		final String expectedResult = normalizeCode(	"bean.setName( \"value\" );\n"
													+ 	"bean.setName( 3 );\n"
													+ 	"bean.setName(  new String( 3 ) );\n"
													+ 	"bean.setName( 3.0 );\n"
													+ 	"bean.setName( 3.0f );\n"
													+ 	"bean.setName( 3.0d );\n"
													+ 	"bean.setName( true );\n"
													+ 	"bean.setName( false );\n" );
	
		Assert.assertEquals( "Incorrect class definition", expectedResult, normalizeCode( result.toString() ) );
	}
	
	/**
	 * Test that various Java Collections are properly defined
	 */
	@Test
	public void testListDeclaration()
	{
		Property prop1 = new Property( "name", "nameValue", null);
		Property prop2 = new Property( "id", null, "idRef" );
		
		Bean bean = new Bean("listBean", null);
		bean.setType( Bean.Type.LIST);
		
		generator.instantiateBean( bean );
		generator.declareProperty(bean, prop1);
		generator.declareProperty(bean, prop2);
		
		final String expectedResult = normalizeCode(	"listBean = new java.util.LinkedList();\n"
													+	"listBean.add( \"nameValue\" );\n"
													+ 	"listBean.add( idRef );\n" );
		
		Assert.assertEquals( "Incorrect collection definition", expectedResult, normalizeCode( result.toString() ) );
	}
	
	/**
	 * Test that various Java Collections are properly defined
	 */
	@Test
	public void testSetDeclaration()
	{
		Property prop1 = new Property( "name", "nameValue", null);
		Property prop2 = new Property( "id", null, "idRef" );
		
		Bean bean = new Bean("setBean", null);
		bean.setType( Bean.Type.SET);
	
		generator.instantiateBean( bean );
		generator.declareProperty(bean, prop1);
		generator.declareProperty(bean, prop2);
		
		final String expectedResult = normalizeCode(	"setBean = new java.util.HashSet();\n"
				+	"setBean.add( \"nameValue\" );\n"
				+ 	"setBean.add( idRef );\n" );

		Assert.assertEquals( "Incorrect collection definition", expectedResult, normalizeCode( result.toString() ) );
	}
	
	/**
	 * Test that various Java Collections are properly defined
	 */
	@Test
	public void testMapDeclaration()
	{
		Property prop1 = new Property( "name", "nameValue", null);
		Property prop2 = new Property( "id", null, "idRef" );
		
		Bean bean = new Bean("mapBean", null);
		bean.setType( Bean.Type.MAP);
		
		generator.instantiateBean( bean );
		generator.declareProperty(bean, prop1);
		generator.declareProperty(bean, prop2);
		
		final String expectedResult = normalizeCode(	"mapBean = new java.util.HashMap();\n"
				+	"mapBean.put( \"name\", \"nameValue\" );\n"
				+ 	"mapBean.put( \"id\", idRef );\n" );

		Assert.assertEquals( "Incorrect collection definition", expectedResult, normalizeCode( result.toString() ) );
	}
	
	/**
	 * Test that various Java Collections are properly defined
	 */
	@Test
	public void testPropertiesDeclaration()
	{
		Property prop1 = new Property( "name", "nameValue", null);
		Property prop2 = new Property( "id", null, "idRef" );
		
		Bean bean = new Bean("propBean", null);
		bean.setType( Bean.Type.PROPERTIES);
		
		generator.instantiateBean( bean );
		generator.declareProperty(bean, prop1);
		generator.declareProperty(bean, prop2);
		
		final String expectedResult = normalizeCode(	"propBean = new java.util.Properties();\n"
				+	"propBean.setProperty( \"name\", \"nameValue\" );\n"
				+ 	"propBean.setProperty( \"id\", idRef );\n" );

		Assert.assertEquals( "Incorrect collection definition", expectedResult, normalizeCode( result.toString() ) );
	}
	
}
