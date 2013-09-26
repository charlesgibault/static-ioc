package org.staticioc.parser;

import static org.junit.Assert.*;

import org.junit.Test;

public class ParserHelperTest
{
	@Test
	public void testPrefixMatching()
	{
		assertTrue("Incorrect property matching", ParserHelper.match("property", "bean:property", "bean") );
		assertTrue("Incorrect property matching with empty name", ParserHelper.match("", "bean:", "bean") );
		assertTrue("Incorrect property matching with empty prefix", ParserHelper.match("ref", "ref", "") );
		assertTrue("Incorrect property matching with null prefix", ParserHelper.match("ref", "ref", null) );
		
		assertFalse("Incorrect property matching: wrong character", ParserHelper.match("properti", "bean:property", "bean") );
		assertFalse("Incorrect property matching: wrong character", ParserHelper.match("property", "bean:properti", "bean") );
		assertFalse("Incorrect property matching: wrong delimiter", ParserHelper.match("property", "beanxproperti", "bean") );
		assertFalse("Incorrect property matching: wrong length", 	ParserHelper.match("ref", "bean:property", "bean") );
		
		assertTrue("Incorrect handling of both cases null", ParserHelper.match(null, null, "") );
		assertTrue("Incorrect handling of both cases null", ParserHelper.match(null, null, null) );
		assertFalse("Incorrect handling of null expected name", ParserHelper.match(null, "bean:property", "bean") );
		assertFalse("Incorrect handling of null node name", ParserHelper.match("property", null, "bean") );
	}

	@Test
	public void testPrefixedName()
	{
		assertEquals("Incorrect property prefixing", "bean:property", ParserHelper.prefixedName("bean", "property" ) );
		assertEquals("Incorrect property prefixing", "property", ParserHelper.prefixedName( "", "property" ) );
		assertEquals("Incorrect property prefixing", "property", ParserHelper.prefixedName( null, "property" ) );
	}
}
