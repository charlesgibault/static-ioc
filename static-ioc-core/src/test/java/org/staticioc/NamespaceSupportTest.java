package org.staticioc;

import static org.junit.Assert.*;

import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.junit.Test;
import org.w3c.dom.Document;

public class NamespaceSupportTest {

	private final static String TEST_CONTEXT_DEFAULT = "src/test/resources/SpringConfigParserTest-SimpleBean.xml";
	private final static String TEST_CONTEXT_BEAN = "src/test/resources/SpringConfigParserTest-PrefixedBean.xml";
	
	private final DocumentBuilderFactory dbf;
	private final DocumentBuilder db;

	public NamespaceSupportTest() throws ParserConfigurationException
	{
		// Init DocumentBuilderFactory
		dbf = DocumentBuilderFactory.newInstance();

		dbf.setNamespaceAware(false);
		dbf.setValidating( false );
		dbf.setFeature("http://xml.org/sax/features/external-general-entities",  false );
		dbf.setFeature("http://xml.org/sax/features/external-parameter-entities",  false );
		dbf.setFeature("http://apache.org/xml/features/nonvalidating/load-dtd-grammar",  false );
		dbf.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd",  false );
		dbf.setFeature("http://xml.org/sax/features/use-entity-resolver2", false);

		db = dbf.newDocumentBuilder();
	}

	@Test
	public void testDefaultNamespacePrefixMapping() throws Exception
	{
		final Document confFileDom = db.parse( TEST_CONTEXT_DEFAULT );
		SpringConfigParser parser = new SpringConfigParser();

		Map<String, String> result = parser.extractNamespacePrefix(confFileDom);
		
		assertEquals( "Default namespace not loaded properly", "", result.get("http://www.springframework.org/schema/beans") );
		assertEquals( "Spring p namespace not loaded properly", "p", result.get("http://www.springframework.org/schema/p") );
		assertEquals( "XML schema namespace not loaded properly", "xsi", result.get("http://www.w3.org/2001/XMLSchema-instance") );
	}

	@Test
	public void testBeanNamespacePrefixMapping() throws Exception
	{
		final Document confFileDom = db.parse( TEST_CONTEXT_BEAN );
		SpringConfigParser parser = new SpringConfigParser();

		Map<String, String> result = parser.extractNamespacePrefix(confFileDom);
		
		assertEquals( "Default namespace not loaded properly", "bean", result.get("http://www.springframework.org/schema/beans") );
		assertEquals( "Spring p namespace not loaded properly", "d", result.get("http://www.springframework.org/schema/p") );
		assertEquals( "XML schema namespace not loaded properly", "xsi", result.get("http://www.w3.org/2001/XMLSchema-instance") );
	}
}
