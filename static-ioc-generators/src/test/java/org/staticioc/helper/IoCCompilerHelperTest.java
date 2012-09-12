/**
 * 
 */
package org.staticioc.helper;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author Charles Gibault
 * @date 12 sept. 2012
 */
public class IoCCompilerHelperTest
{
	/**
	 * Check a correct path
	 */
	@Test
	public void testCheckOutputPathExisting()
	{
		Assert.fail();
	}

	/**
	 * Check an incorrect path
	 */
	@Test
	public void testCheckOutputPathNonExisting()
	{
		Assert.fail();
	}

	/**
	 * Try to load the Java Code generator
	 */
	@Test
	public void testGetCodeGenerator()
	{
		Assert.fail();
	}
	
	/**
	 * Try to load a non existing Code generator
	 */
	@Test
	public void testCodeGeneratorNotFound()
	{
		Assert.fail();
	}
	
	/**
	 * Try to load a class that doesn't implement CodeGenerator
	 */
	@Test
	public void testCodeGeneratorNotImplemented()
	{
		Assert.fail();
	}
	

	/**
	 * Try to load a simple - single file mapping
	 */
	@Test
	public void testGetTargetMappingSingleFile()
	{
		Assert.fail();
	}
	
	/**
	 * Try to load a complex - multi-target - multiple files per target
	 */
	@Test
	public void testGetTargetMappingMultiTarget()
	{
		Assert.fail();
	}

	/**
	 * Try to load a malformatted mapping expression
	 */
	@Test
	public void testGetTargetMappingMalFormatted()
	{
		Assert.fail();
	}
}
