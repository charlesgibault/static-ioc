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
package org.staticioc;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.parsers.ParserConfigurationException;
import org.xml.sax.SAXException;

import org.staticioc.generator.CodeGenerator;
import org.staticioc.generator.CodeGenerator.Level;
import org.staticioc.model.Bean;
import org.staticioc.model.Property;
import org.staticioc.model.Bean.Scope;

public class SpringStaticFactoryGenerator implements IoCCompiler
{
	private static final Logger logger  = LoggerFactory.getLogger(SpringStaticFactoryGenerator.class);
	
	private final static int INIT_BUFFER_SIZE = 4096;
	private final static String[] EMPTY_STRING_ARRAY = new String[]{};
		
	private final static String COMMENT_HEADER = "This code has been generated using Static IoC framework (http://code.google.com/p/static-ioc/). DO NOT EDIT MANUALLY HAS CHANGES MAY BE OVERRIDEN";
		
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
	public void compile( final CodeGenerator generator, String oPath, final Map< String, List< String >> inputOutputMapping, String fileExtOverride ) throws SAXException,
	IOException, ParserConfigurationException
	{
		// Check if a valid extension override was provided
		final String fileExtensionOverride = (fileExtOverride == null) ? generator.getDefaultSourceFileExtension() : fileExtOverride;
		
		// Check if outputPath actually ends with a trailing / or not
		final String outputPath = ( oPath.endsWith(File.separator) ) ? oPath : oPath + File.separator;
		
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
	 * @throws IOException 
	 * @throws SAXException 
	 * @throws ParserConfigurationException 
	 */
	public StringBuilder generate(final CodeGenerator codeGenerator, final String generatedPackageName, final String generatedClassName, final List<String> configurationFiles )  throws SAXException, IOException, ParserConfigurationException
	{
		final StringBuilder res = new StringBuilder( INIT_BUFFER_SIZE );
		codeGenerator.setOutput( res );
		
		SpringConfigParser springConfigParser = new SpringConfigParser();
		Map<String, Bean> beanClassMap = springConfigParser.load( configurationFiles );
		final LinkedList<Bean> orderedBean = springConfigParser.getBeanContainer().getOrderedBeans();

		// Track beans with a declared init-method and destroy-method attribute
		List<Bean> initRequiredBeans = new LinkedList<Bean>();
		List<Bean> destroyRequiredBeans = new LinkedList<Bean>();
		
		codeGenerator.comment(Level.HEADER, COMMENT_HEADER );

		codeGenerator.initPackage( generatedPackageName );		
		codeGenerator.initClass( generatedClassName );
		
		// declare beans
		codeGenerator.comment(Level.CLASS, "Bean definition" );
		for ( final Bean bean: orderedBean )
		{
			// Ignore abstract beans and prototypes
			if ( isHidden(bean) ){ continue; }
			
			if( bean.getInitMethod() != null )
			{
				initRequiredBeans.add(bean);
			}
			
			if( bean.getDestroyMethod() != null )
			{
				destroyRequiredBeans.add(bean);
			}
						
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
		
		// Call init-methods
		if( !initRequiredBeans.isEmpty() )
		{
			codeGenerator.comment(Level.METHOD, "Init methods calls" );	
		}
		
		for ( Bean bean : initRequiredBeans)
		{
			res.append("\t\t");
			codeGenerator.invokeMethod(bean, bean.getInitMethod(), EMPTY_STRING_ARRAY);
		}
		
		codeGenerator.closeConstructor( generatedClassName );
		res.append("\n");
		res.append("\n");
		
		codeGenerator.initDestructor( generatedClassName );
		
		// Call destroy-methods
		for ( Bean bean : destroyRequiredBeans)
		{
			res.append("\t\t");
			codeGenerator.invokeMethod(bean, bean.getDestroyMethod(), EMPTY_STRING_ARRAY);
		}
		
		//Destructor : free beans in their reverse order of creation
		Iterator<Bean> itr = orderedBean.descendingIterator();
		
		while(itr.hasNext()) {
			final Bean bean = itr.next();
		    
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
