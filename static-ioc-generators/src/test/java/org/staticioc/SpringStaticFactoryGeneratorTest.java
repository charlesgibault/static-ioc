/**
 * Unit test for testing the SpringStaticFactoryGenerator which is responsible
 * for converting bean definition into language independent directives
 */
package org.staticioc;

import java.io.IOException;
import java.util.Arrays;

import javax.xml.parsers.ParserConfigurationException;

import org.junit.Assert;
import org.junit.Test;
import org.staticioc.generator.CodeGenerator;
import org.staticioc.generator.JavaCodeGenerator;
import org.xml.sax.SAXException;

public class SpringStaticFactoryGeneratorTest
{
	SpringStaticFactoryGenerator factoryGenerator = new SpringStaticFactoryGenerator();
	
	private String normalizeCode( String code)
	{
		return code.replaceAll("\\s+", " ").trim();
	}
	
	/**
	 * Simple context test
	 * @throws ParserConfigurationException 
	 * @throws IOException 
	 * @throws SAXException 
	 */
	@Test
	public void testSimpleContextDefinition() throws SAXException, IOException, ParserConfigurationException
	{
		final CodeGenerator codeGenerator = new JavaCodeGenerator();
		StringBuilder result = new StringBuilder();
		codeGenerator.setOutput(result);
		
		final String packageName = "org.staticioc.test";
		final String className = "TestClass";

		String[] contexts = {"src/test/resources/SpringStaticFactoryGeneratorTest-context.xml"};
		
		final String expectedResult = normalizeCode( 
				"/** This code has been generated using Static IoC framework (http://code.google.com/p/static-ioc/). DO NOT EDIT MANUALLY HAS CHANGES MAY BE OVERRIDEN */\n" +
				"package " + packageName + ";\n" 
				+ "public class " + className + " {\n"
				
				+ "/** * Bean definition */\n"
				+ "public final test.House number10House;\n"
				+ "public final test.Person personBean;\n"
				+ "public final test.Country country;\n"
				
				+ "/** * Constructor of the Factory */\n"
				+ "public " + className + "() {\n"
				
				+ "// Instanciating beans\n"
				+ "number10House = new test.House();\n"
				+ "personBean = new test.Person();\n"
				+ "country = new test.Country(42, new java.util.Integer( 7500000 ));\n"

				+ "// Setting up bean number10House\n"
				+ "number10House.setPerson( personBean );\n"
				+ "number10House.setName( \"10 Downing Street\" );\n"
				+ "number10House.setP( personBean );\n"

				+ "// Setting up bean personBean\n"
				+ "personBean.setUsername( \"root\" );\n"
				+ "personBean.setName( \"personName\" );\n"
				+ "personBean.setBirthCountry( country );\n"
				+ "personBean.setAge( 28 );\n"
				+ "personBean.setCountry( country );\n"
								
				+"}\n"
				+ "public void destroyContext() { }\n"
				+ "}" );
		
		result = factoryGenerator.generate(codeGenerator, packageName, className, Arrays.asList( contexts ) );
		
		Assert.assertEquals( "Incorrect generated result", expectedResult, normalizeCode( result.toString() ) );
	}
}
