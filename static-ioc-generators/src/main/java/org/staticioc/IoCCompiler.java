/**
 * 
 */
package org.staticioc;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;

import org.staticioc.generator.CodeGenerator;
import org.xml.sax.SAXException;

/**
 * @author Charles Gibault
 * @date 11 sept. 2012
 */
public interface IoCCompiler
{
	public void compile( CodeGenerator generator, String outputPath, Map<String, List<String>> inputOutputMapping )  throws SAXException, IOException, ParserConfigurationException;
	public void compile( CodeGenerator generator, String outputPath, Map<String, List<String>> inputOutputMapping, String fileExtensionOverride )  throws SAXException, IOException, ParserConfigurationException;
}
