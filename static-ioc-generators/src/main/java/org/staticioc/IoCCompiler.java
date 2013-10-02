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
package org.staticioc;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;

import org.staticioc.generator.CodeGenerator;
import org.staticioc.parser.NamespaceParser;
import org.xml.sax.SAXException;

/**
 * @author Charles Gibault
 */
public interface IoCCompiler
{
	/**
	 * Load a Set of context configuration files and generate a BeanFactory class that instanciates the declared Beans for each input/output mapping
	 * using the default file extension provided by the chosen CodeGenerator
	 * 
	 * @param generator  CodeGenerator object to use to generate the target BeanFactory source code
	 * @param outputPath were the BeanFactory classes will be written 
	 * @param inputOutputMapping Map< Target BeanFactory class name, List of configuration files to load>
	 * @throws SAXException
	 * @throws IOException
	 * @throws ParserConfigurationException
	 */
	void compile( CodeGenerator generator, String outputPath, Map<String, List<String>> inputOutputMapping )  throws SAXException, IOException, ParserConfigurationException;
	
	/**
	 * Load a Set of context configuration files and generate a BeanFactory class that instanciates the declared Beans for each input/output mapping
	 * using the provided file extension.
	 * 
	 * @param generator  CodeGenerator object to use to generate the target BeanFactory source code
	 * @param outputPath were the BeanFactory classes will be written 
	 * @param inputOutputMapping Map< Target BeanFactory class name, List of configuration files to load>
	 * @param fileExtensionOverride file extension to use for generated classes
	 * @throws SAXException
	 * @throws IOException
	 * @throws ParserConfigurationException
	 */
	void compile( CodeGenerator generator, String outputPath, Map<String, List<String>> inputOutputMapping, String fileExtensionOverride )  throws SAXException, IOException, ParserConfigurationException;

	/**
	 * Load a Set of context configuration files and generate a BeanFactory class that instanciates the declared Beans for each input/output mapping
	 * using the provided file extension.
	 * 
	 * @param generator  CodeGenerator object to use to generate the target BeanFactory source code
	 * @param outputPath were the BeanFactory classes will be written 
	 * @param inputOutputMapping Map< Target BeanFactory class name, List of configuration files to load>
	 * @param fileExtensionOverride file extension to use for generated classes
	 * @param namespacePlugins List<NamespaceParser> of extra namespace support plugins to use in addition to default's Spring Bean and Spring p
	 * @throws SAXException
	 * @throws IOException
	 * @throws ParserConfigurationException
	 */
	void compile( CodeGenerator generator, String outputPath, Map<String, List<String>> inputOutputMapping, String fileExtensionOverride, List<NamespaceParser> namespacePlugins )  throws SAXException, IOException, ParserConfigurationException;

}
