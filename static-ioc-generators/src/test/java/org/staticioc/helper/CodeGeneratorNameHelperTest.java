package org.staticioc.helper;

import junit.framework.Assert;

import org.junit.Test;

public class CodeGeneratorNameHelperTest {
	/**
	 * Check a correct target language
	 */
	@Test
	public void testGetGeneratorClass()
	{
		Assert.assertEquals("Wrong target Code generator", "org.staticioc.generator.JavaCodeGenerator", CodeGeneratorNameHelper.getGeneratorClass( "JAVA") );
		Assert.assertEquals("Wrong target Code generator", "org.staticioc.generator.JavaCodeGenerator", CodeGeneratorNameHelper.getGeneratorClass( "java") );
	}
	
	/**
	 * Check a non existing target language
	 */
	@Test(expected = IllegalArgumentException.class) 
	public void testNonExistingGeneratorClass()
	{
		CodeGeneratorNameHelper.getGeneratorClass( "esperanto");
	}
	
	/**
	 * Check a non supported target language
	 */
	@Test(expected = IllegalArgumentException.class) 
	public void testUnsupportedGeneratorClass()
	{
		CodeGeneratorNameHelper.getGeneratorClass( "cpp");
	}
}
