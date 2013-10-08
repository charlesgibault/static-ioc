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
package org.staticioc.parser;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;

public class NamespaceHelperTest
{
	/**
	 * Try to load a valid expression
	 */
	@Test
	public void testGetNamespacePlugin()
	{
		// Single parameter :
		List<NamespaceParser> result = NamespaceHelper.getNamespacePlugins("org.staticioc.parser.namespace.spring.beans.SpringBeansNameSpaceParser");
		Assert.assertEquals( "Expected plugin count not matched", 1, result.size() );
	}

	/**
	 * Try to load a valid expression
	 */
	@Test
	public void testGetNamespacePlugins()
	{
		// Multiple plugins as parameters :
		List<NamespaceParser> result = NamespaceHelper.getNamespacePlugins("org.staticioc.parser.namespace.spring.beans.SpringBeansNameSpaceParser,org.staticioc.parser.namespace.spring.p.SpringPNameSpaceParser");
		Assert.assertEquals( "Expected plugin count not matched", 2, result.size() );
	}
	
	/**
	 * Try to load an unexisting namespacePlugin
	 */
	@Test(expected = IllegalArgumentException.class) 
	public void testGetNamespacePluginsNotFound()
	{
		NamespaceHelper.getNamespacePlugins("unexistent.class");
	}
	
	/**
	 * Try to load a class that doesn't implement NamespaceParser interface
	 */
	@Test(expected = IllegalArgumentException.class) 
	public void testGetNamespacePluginsInterfaceNotImplemented()
	{
		NamespaceHelper.getNamespacePlugins("java.util.List");
	}
	
	/**
	 * Try to load a malformatted namespace expressions
	 */
	@Test
	public void testGetNamespacePluginsMalFormatted()
	{
		// Null parameter :
		List<NamespaceParser> result = NamespaceHelper.getNamespacePlugins((String)null);
		Assert.assertEquals( "More elements than expected", 0, result.size() );
		
		// Null parameter :
		result = NamespaceHelper.getNamespacePlugins((List<String>)null);
		Assert.assertEquals( "More elements than expected", 0, result.size() );
		
		// Empty plugin list :
		result = NamespaceHelper.getNamespacePlugins("");
		Assert.assertEquals( "More elements than expected", 0, result.size() );
		
		// Empty plugin list :
		result = NamespaceHelper.getNamespacePlugins(",");
		Assert.assertEquals( "More elements than expected", 0, result.size() );
		
		// Empty plugin list :
		result = NamespaceHelper.getNamespacePlugins(",,");
		Assert.assertEquals( "More elements than expected", 0, result.size() );
	}

}
