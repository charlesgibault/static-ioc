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
/**
 * 
 */
package org.staticioc.helper;

import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.junit.Assert;
import org.junit.Test;
import org.staticioc.generator.CodeGenerator;
import org.staticioc.generator.JavaCodeGenerator;

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
		IoCCompilerHelper.checkOutputPath( FileUtils.getTempDirectoryPath() );
	}

	/**
	 * Check an incorrect path
	 */
	@Test(expected = IllegalArgumentException.class) 
	public void testCheckOutputPathNonExisting()
	{
		IoCCompilerHelper.checkOutputPath("/nonexisting/path/");
	}

	/**
	 * Try to load the Java Code generator
	 */
	@Test
	public void testGetCodeGenerator()
	{
		CodeGenerator generator = IoCCompilerHelper.getCodeGenerator("org.staticioc.generator.JavaCodeGenerator");
		Assert.assertTrue("Incorrect class instantiated", generator instanceof JavaCodeGenerator );
	}
	
	/**
	 * Try to load a non existing Code generator
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testCodeGeneratorNotFound()
	{
		IoCCompilerHelper.getCodeGenerator("non.existing.class");
	}
	
	/**
	 * Try to load a class that doesn't implement CodeGenerator
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testCodeGeneratorNotImplemented()
	{
		IoCCompilerHelper.getCodeGenerator("java.util.List");
	}
	

	/**
	 * Try to load a simple - single file mapping
	 */
	@Test
	public void testGetTargetMappingSingleFile()
	{
		Map<String, List<String>> result = IoCCompilerHelper.getTargetMapping("package.name.target1:source1");
		
		Assert.assertEquals( "More elements than expected", 1, result.keySet().size() );
	
		List<String> sources = result.get("package.name.target1");
		Assert.assertEquals( "More elements than expected", 1, sources.size() );
		Assert.assertEquals( "Incorrect value", "source1", sources.get(0) );
	}
	
	/**
	 * Try to load a complex - multi-target - multiple files per target
	 * Note: expected format "package.name.target1:source1,source2;target2:source1,..."
	 */
	@Test
	public void testGetTargetMappingMultiTarget()
	{
		Map<String, List<String>> result = IoCCompilerHelper.getTargetMapping("package.name.target1:source1,source2;package.name.target2:source3");
		
		Assert.assertEquals( "More elements than expected", 2, result.keySet().size() );
	
		List<String> sources1 = result.get("package.name.target1");
		Assert.assertEquals( "More elements than expected", 2, sources1.size() );
		Assert.assertEquals( "Incorrect value", "source1", sources1.get(0) );
		Assert.assertEquals( "Incorrect value", "source2", sources1.get(1) );
		
		List<String> sources2 = result.get("package.name.target2");
		Assert.assertEquals( "More elements than expected", 1, sources2.size() );
		Assert.assertEquals( "Incorrect value", "source3", sources2.get(0) );
	}

	/**
	 * Try to load a malformatted mapping expression
	 */
	@Test
	public void testGetTargetMappingMalFormatted()
	{
		// Empty mapping :
		Map<String, List<String>> result = IoCCompilerHelper.getTargetMapping("");
		Assert.assertEquals( "More elements than expected", 0, result.keySet().size() );
		
		// missing source after :
		result = IoCCompilerHelper.getTargetMapping("p:;p2:s2");
		Assert.assertEquals( "More elements than expected", 1, result.keySet().size() );
		
		// missing mapping between ;;
		result = IoCCompilerHelper.getTargetMapping("p:s;;");
		Assert.assertEquals( "More elements than expected", 1, result.keySet().size() );
		
		// missing target before :
		result = IoCCompilerHelper.getTargetMapping(":s;");
		Assert.assertEquals( "More elements than expected", 0, result.keySet().size() );
		
		// missing target before :
		result = IoCCompilerHelper.getTargetMapping("s;");
		Assert.assertEquals( "More elements than expected", 0, result.keySet().size() );
		
		// : as last character
		result = IoCCompilerHelper.getTargetMapping("test:;target:");
		Assert.assertEquals( "More elements than expected", 0, result.keySet().size() );
	}
}
