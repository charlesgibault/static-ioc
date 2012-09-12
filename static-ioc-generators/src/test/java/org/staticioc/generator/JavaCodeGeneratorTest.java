package org.staticioc.generator;

import static org.junit.Assert.assertEquals;

import org.junit.Assert;
import org.junit.Test;
import org.staticioc.generator.CodeGenerator;
import org.staticioc.generator.JavaCodeGenerator;

/**
 * Unit test for testing the Java Code Generator
 */
public class JavaCodeGeneratorTest
{
	CodeGenerator generator = new JavaCodeGenerator();
	
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
		Assert.fail();
	}
	
	/**
	 * Anonymous and prototype beans tests
	 */
	@Test
	public void testAnonymousBeansDeclaration()
	{
		Assert.fail();
	}

	/**
	 * Check that constructor arguments are properly declared
	 */
	@Test
	public void testConstructorArgsDeclaration()
	{
		Assert.fail();
	}

	/**
	 * Test that String / Integer / Double conversion are properly working
	 */
	@Test
	public void testValueConversions()
	{
		Assert.fail();
	}
	
	/**
	 * Test that various Java Collections are properly defined
	 */
	@Test
	public void testCollectionDeclaration()
	{
		Assert.fail();
	}
	
	
}
