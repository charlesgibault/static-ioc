package org.staticioc;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
import org.staticioc.model.Bean;
import org.staticioc.model.Property;

/**
 * Abstract class to host useful utilities methods for unit testing Bean / Property loading
 */
public abstract class AbstractSpringParserTest
{
	protected final SpringConfigParser parser;
	protected final Map<String, Bean> loadedBeans;
	
	public AbstractSpringParserTest(final String[] testContexts) throws Exception 
	{
		parser = new SpringConfigParser();
		loadedBeans = parser.load( Arrays.asList(testContexts) ) ;
	}
	
	public AbstractSpringParserTest(final String testContext) throws Exception 
	{
		parser = new SpringConfigParser();
		loadedBeans = parser.load( testContext );
	}
	
	protected Map<String, Property> mapProperties( Collection<Property> properties )
	{
		Map<String, Property> result = new HashMap<String, Property>();
		
		for( Property prop : properties)
		{
			result.put( prop.getName(), prop );
		}
		
		return result;
	}
	
	protected void checkProperty(final Map<String, Property> properties, final String name, final String val, final String ref)
	{
		checkProperty( properties.get(name), name, val, ref);
	}
	
	protected void checkProperty(final Map<String, Property> properties, final String name, final String val, final String ref, final String type)
	{
		checkProperty( properties.get(name), name, val, ref, type);
	}
	
	protected void checkProperty(final Property prop, final String name, final String val, final String ref)
	{
		Assert.assertNotNull( "Property is null " + name, prop);
		if( name != null)
		{
			assertEquals("Property name not loaded properly", name, prop.getName() );
		}
		assertEquals("Property value not loaded properly", val, prop.getValue() );
		assertEquals("Property reference not loaded properly", ref, prop.getRef() );
	}
	
	protected void checkProperty(final Property prop, final String name, final String val, final String ref, final String type)
	{
		checkProperty( prop, name, val, ref);
		assertEquals("Property type not matching",type, prop.getType() );
	}
	
}
