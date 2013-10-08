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

import static org.junit.Assert.*;

import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.junit.Test;
import org.staticioc.parser.ParserConstants;
import org.staticioc.parser.ParserHelper;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

public class ParserHelperNamespaceTest {

	private final static String TEST_CONTEXT_DEFAULT = "src/test/resources/SpringConfigParserTest-SimpleBean.xml";
	private final static String TEST_CONTEXT_BEAN = "src/test/resources/SpringConfigParserTest-PrefixedBean.xml";
	
	private final DocumentBuilderFactory dbf;
	private final DocumentBuilder db;
	private final XPathExpression xBeanRoot;

	public ParserHelperNamespaceTest() throws ParserConfigurationException, XPathExpressionException
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
		
		XPathFactory xPathFactory = XPathFactory.newInstance();
		final XPath xPathBeanRoot  = xPathFactory.newXPath();
		xBeanRoot = xPathBeanRoot.compile(ParserConstants.XPATH_BEANS_NODE);
	}

	@Test
	public void testDefaultNamespacePrefixMapping() throws Exception
	{
		final Document confFileDom = db.parse( TEST_CONTEXT_DEFAULT );
		NodeList beansRoot = (NodeList) xBeanRoot.evaluate(confFileDom, XPathConstants.NODESET);
		
		Map<String, String> result = ParserHelper.extractNamespacePrefix(beansRoot);
		
		assertEquals( "Default namespace not loaded properly", "", result.get("http://www.springframework.org/schema/beans") );
		assertEquals( "Spring p namespace not loaded properly", "p", result.get("http://www.springframework.org/schema/p") );
		assertEquals( "XML schema namespace not loaded properly", "xsi", result.get("http://www.w3.org/2001/XMLSchema-instance") );
	}

	@Test
	public void testBeanNamespacePrefixMapping() throws Exception
	{
		final Document confFileDom = db.parse( TEST_CONTEXT_BEAN );
		NodeList beansRoot = (NodeList) xBeanRoot.evaluate(confFileDom, XPathConstants.NODESET);

		Map<String, String> result = ParserHelper.extractNamespacePrefix(beansRoot);
		
		assertEquals( "Default namespace not loaded properly", "bean", result.get("http://www.springframework.org/schema/beans") );
		assertEquals( "Spring p namespace not loaded properly", "d", result.get("http://www.springframework.org/schema/p") );
		assertEquals( "XML schema namespace not loaded properly", "xsi", result.get("http://www.w3.org/2001/XMLSchema-instance") );
	}
}
