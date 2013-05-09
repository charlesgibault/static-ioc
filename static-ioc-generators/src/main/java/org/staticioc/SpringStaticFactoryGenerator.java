/**
 *  Copyright (C) 2012 Charles Gibault
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
package org.staticioc;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.NavigableMap;
import java.util.NavigableSet;
import java.util.TreeSet;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;
import org.xml.sax.SAXException;

import org.staticioc.generator.CodeGenerator;
import org.staticioc.generator.CodeGenerator.Level;
import org.staticioc.model.*;
import org.staticioc.model.Bean.Scope;

public class SpringStaticFactoryGenerator implements IoCCompiler
{
	private static final Logger logger  = LoggerFactory.getLogger(SpringStaticFactoryGenerator.class);
	
	private final static int INIT_BUFFER_SIZE = 4096;
		
	private String commentHeader = "This code has been generated using Static IoC framework (http://code.google.com/p/static-ioc/). DO NOT EDIT MANUALLY HAS CHANGES MAY BE OVERRIDEN";
		
	private boolean ignoreUnresolvedRefs = true;
	
	/**
	 * Ignore abstract beans and prototypes
	 * @param bean
	 * @return
	 */
	protected boolean isHidden( final Bean bean )
	{
		return bean.isAbstract() || bean.getScope().equals( Scope.PROTOTYPE );
	}

	@Override
	public void compile( CodeGenerator generator, String outputPath, Map< String, List< String >> inputOutputMapping ) throws SAXException,
			IOException, ParserConfigurationException
	{
		compile( generator, outputPath, inputOutputMapping, generator.getDefaultSourceFileExtension() );
	}
	
	@Override
	public void compile( final CodeGenerator generator, String outputPath, final Map< String, List< String >> inputOutputMapping, String fileExtensionOverride ) throws SAXException,
	IOException, ParserConfigurationException
	{
		// Check if a valid extension override was provided
		fileExtensionOverride = (fileExtensionOverride == null) ? generator.getDefaultSourceFileExtension() : fileExtensionOverride;
		
		// Check if outputPath actually ends with a trailing / or not
		outputPath = ( outputPath.endsWith(File.separator) ) ? outputPath : outputPath + File.separator;
		
		//Build complete output file path:
		for( final String targetClass : inputOutputMapping.keySet() )
		{
			String targetFile = outputPath + generator.getFilePath( targetClass ) + fileExtensionOverride;

			//Load all configuration files for this target
			logger.info( "Generating code for {}", targetClass );
			
			String generatedClassName = generator.getClassName( targetClass );
			String generatedPackageName = generator.getPackageName( targetClass );
			StringBuilder generatedCode = generate( generator, generatedPackageName, generatedClassName, inputOutputMapping.get(targetClass) );
			
			// Generate resulting file
			logger.info( "Writing {} to {}", targetClass, targetFile );
			FileUtils.writeStringToFile( new File(targetFile), generatedCode.toString() );
		}
	}

	/**
	 * Entry point for the service : generate code matching setup configuration file.
	 * @return
	 * @throws XPathExpressionException
	 * @throws IOException 
	 * @throws SAXException 
	 * @throws ParserConfigurationException 
	 */
	public StringBuilder generate(final CodeGenerator codeGenerator, final String generatedPackageName, final String generatedClassName, final List<String> configurationFiles )  throws SAXException, IOException, ParserConfigurationException
	{
		final StringBuilder res = new StringBuilder( INIT_BUFFER_SIZE );
		codeGenerator.setOutput( res );
		
		SpringConfigParser springConfigParser = new SpringConfigParser();
		NavigableMap<String, Bean> beanClassMap = springConfigParser.load( configurationFiles );

		codeGenerator.comment(Level.HEADER, commentHeader );

		codeGenerator.initPackage( generatedPackageName );		
		codeGenerator.initClass( generatedClassName );
		
		final NavigableSet<Bean> orderedBean = new TreeSet<Bean>( beanClassMap.values() );
		
		// declare beans
		codeGenerator.comment(Level.CLASS, "Bean definition" );
		for ( final Bean bean: orderedBean )
		{
			// Ignore abstract beans and prototypes
			if ( isHidden(bean) ){ continue; }
						
			codeGenerator.declareBean( bean );
		}

		res.append("\n");
		codeGenerator.comment(Level.CLASS, "Constructor of the Factory" );
		codeGenerator.initConstructor( generatedClassName );
		
		// Instantiate beans
		codeGenerator.comment(Level.METHOD, "Instanciating beans" );			
		
		for ( final Bean bean: orderedBean )
		{
			// Ignore abstract beans and prototypes
			if ( isHidden(bean) ){ continue; }
			
			if( bean.getFactoryBean()!=null )
			{
				codeGenerator.instantiateBeanWithFactory( bean );
			}
			else
			{
				codeGenerator.instantiateBean( bean );
			}
		}
		
		res.append("\n");
		
		// Set properties
		for ( final Bean bean: orderedBean )
		{
			// Ignore abstract beans and beans with no properties
			if ( isHidden(bean) || bean.getProperties().isEmpty() ){ continue; }
			
			codeGenerator.comment(Level.METHOD, "Setting up bean " + bean.getId() );			
			for( Property prop : bean.getProperties() )
			{
				// Check that the reference exists and is not abstract
				if( prop.getRef() != null )
				{
					final Bean refBean = beanClassMap.get( prop.getRef() );
					
					if( refBean == null )
					{
						logger.warn( "Reference {} does not exist for Bean {}", prop.getRef(), bean.getId() );
						
						if( isIgnoreUnresolvedRefs() )
						{
							continue;
						}
						
					}
					else if ( refBean.isAbstract() )
					{
						logger.warn( "Reference {} is abstract for Bean {}", prop.getRef(), bean.getId() );
						continue;
					}
				}
				
				codeGenerator.declareProperty( bean, prop );
			}
			res.append("\n");
		}
		
		codeGenerator.closeConstructor( generatedClassName );
		res.append("\n");
		res.append("\n");
		
		codeGenerator.initDestructor( generatedClassName );
		
		//Destructor : free beans in their reverse order of creation
		for ( final Bean bean: orderedBean.descendingSet() )
		{
			// Ignore abstract beans and prototypes that were not instanciated
			if ( isHidden(bean) ){ continue; }
			codeGenerator.deleteBean( bean );
		}
		
		
		codeGenerator.closeDestructor( generatedClassName );

		codeGenerator.closeClass( generatedClassName );
		codeGenerator.closePackage( generatedPackageName );

		return res;
	}

	public boolean isIgnoreUnresolvedRefs() {
		return ignoreUnresolvedRefs;
	}

	public void setIgnoreUnresolvedRefs(boolean ignoreUnresolvedRefs) {
		this.ignoreUnresolvedRefs = ignoreUnresolvedRefs;
	}
}
