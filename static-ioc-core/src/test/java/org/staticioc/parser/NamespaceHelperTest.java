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
