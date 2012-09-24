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

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.staticioc.generator.CodeGenerator;
import org.staticioc.helper.CodeGeneratorNameHelper;
import org.staticioc.helper.IoCCompilerHelper;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.xml.sax.SAXException;
import javax.xml.parsers.ParserConfigurationException;

public class StaticIoCCommandLineCompiler
{
	private static final String ARG_FILE_EXTENSION = "x";
	private static final String ARG_OUTPUT_PATH = "o";
	private static final String ARG_HELP = "h";
	private static final String ARG_CODE_GENERATOR = "g";
	private static final String ARG_TARGET_LANGUAGE = "L";
	private static final String ARG_TARGET_MAPPING = "t";

	private static final Logger logger  = LoggerFactory.getLogger(StaticIoCCommandLineCompiler.class);
	
	private static final String USAGE = "StaticIoCCommandLineCompiler -"+ ARG_TARGET_LANGUAGE + " <Java> -"
																+ ARG_OUTPUT_PATH + " </output/path> -"
																+ ARG_TARGET_MAPPING + " <org.demo.GeneratedFile:src/main/resources/context.xml>";
	
	private static final HelpFormatter formatter = new HelpFormatter();
	
	@SuppressWarnings( "static-access" )
	private static Options buildOptions()
	{
		Option help = new Option( ARG_HELP, "help", false, "print this message" );
		
		Option codeGenerator = OptionBuilder.withArgName( "package.codeGeneratorClass" )
				.hasArg()
				.withDescription(  "Fully qualified classname for the code generator (optional, overrides target language)" )
				.withLongOpt( "generator" )
				.create( ARG_CODE_GENERATOR );
		
		Option targetLanguage = OptionBuilder.withArgName( "Java" )
				.hasArg()
				.withDescription(  "Name of the target language" )
				.withLongOpt( "target-language" )
				.create( ARG_TARGET_LANGUAGE );
		
		Option outputPath = OptionBuilder.withArgName( "path/to/output/folder" )
				.hasArg()
				.withDescription(  "Path were IoC source code will be generated" )
				.withLongOpt( "output-path" )
				.create( ARG_OUTPUT_PATH );
		
		Option targetMapping = OptionBuilder.withArgName( "package.name.target1:source1,source2;target2:source1,..." )
				.hasArg()
				.withDescription(  "Collection of mappings < target file -> List< source file > >" )
				.withLongOpt( "target-mapping" )
				.create( ARG_TARGET_MAPPING );
			
		Option fileExtension = OptionBuilder.withArgName( ".abc" )
				.hasArg()
				.withDescription(  "Generated file extension override (Optional)" )
				.withLongOpt( "output-file-extension" )
				.create( ARG_FILE_EXTENSION );
		
		Options options = new Options();
		options.addOption( help );
		options.addOption( targetLanguage );
		options.addOption( outputPath );
		options.addOption( targetMapping );
		options.addOption( codeGenerator );
		options.addOption( fileExtension );
		
		return options;
	}
	
	private static String getOutputPath( CommandLine line, Options options )
	{
		String outputPath = null;
		
		if( line.hasOption( ARG_OUTPUT_PATH ) ) { // Output Path
			outputPath = line.getOptionValue( ARG_OUTPUT_PATH );
			logger.debug( "Using Output path {}", outputPath );
		}
		else
		{
			formatter.printHelp( USAGE, options );
			throw new IllegalArgumentException("Missing output path");
		}
		
		IoCCompilerHelper.checkOutputPath( outputPath );
		
		return outputPath;
	}
	
	private static CodeGenerator getCodeGenerator( CommandLine line, Options options )
	{
		String codeGeneratorClassName = null;
		
		if( line.hasOption( ARG_CODE_GENERATOR ) ) {
			codeGeneratorClassName = line.getOptionValue( ARG_CODE_GENERATOR );
		}
		else if (line.hasOption( ARG_TARGET_LANGUAGE ) )
		{
			String targetLang = line.getOptionValue( ARG_TARGET_LANGUAGE ); 
					
			logger.debug( "Using Target language {}", targetLang );
			codeGeneratorClassName = CodeGeneratorNameHelper.getGeneratorClass( targetLang );
		}
		else
		{
			formatter.printHelp( USAGE, options );
			throw new IllegalArgumentException("Missing target language (" + ARG_TARGET_LANGUAGE + ") or code generator (" + ARG_CODE_GENERATOR + "). At least one is required");
		}

		logger.debug( "Using Code Generator {}", codeGeneratorClassName );
		
		try
		{
			return IoCCompilerHelper.getCodeGenerator( codeGeneratorClassName );
		}
		catch ( RuntimeException e)
		{
			formatter.printHelp( USAGE, options );
			throw e;
		}
	}
	
	private static Map< String,List<String> > getTargetMapping( CommandLine line, Options options )
	{
		String targetMappingAsText = null;
		
		if( line.hasOption( ARG_TARGET_MAPPING ) ) { // Output Path
			targetMappingAsText = line.getOptionValue( ARG_TARGET_MAPPING );
			logger.debug( "Using target mapping {}", targetMappingAsText );
		}
		else
		{
			formatter.printHelp( USAGE, options );
			throw new IllegalArgumentException("Missing target mapping definition");
		}
		
		try
		{
			return IoCCompilerHelper.getTargetMapping( targetMappingAsText );
		}
		catch ( RuntimeException e)
		{
			formatter.printHelp( USAGE, options );
			throw e;
		}
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args)
	{
		Options options = buildOptions();
		CommandLineParser parser = new GnuParser();
		
		Map< String,List<String> > targetMapping;
		CodeGenerator codeGenerator;
		String outputPath;
		String fileExtension = null;
	
		try {
	        // parse the command line arguments
	        CommandLine line = parser.parse( options, args );
	        
	        // Help first
	        if( line.hasOption( ARG_HELP ) ) {
	        	formatter.printHelp( USAGE, options );
	        	return;
	        }

	        if( line.hasOption( ARG_FILE_EXTENSION ) ) { // Optional file extension override
	        	fileExtension = line.getOptionValue( ARG_FILE_EXTENSION );
	        	logger.debug( "Using file extension override {}", fileExtension );
	        }
	        
	        outputPath = getOutputPath(line, options);
	        codeGenerator = getCodeGenerator( line, options);
	        targetMapping = getTargetMapping( line, options);
	    }
	    catch( ParseException exp ) {
	        // oops, something went wrong
	        logger.error( "Parsing failed.  Reason: {}", exp.getMessage() );
	        formatter.printHelp( USAGE, options );
	        throw new IllegalArgumentException(exp);
	    }
		
		// Now instantiate and call the IocCompiler
		IoCCompiler springStaticFactoryGenerator = new SpringStaticFactoryGenerator();
	
		try
		{			
			springStaticFactoryGenerator.compile( codeGenerator, outputPath, targetMapping, fileExtension );
		}
		catch ( ParserConfigurationException e )
		{
			logger.error( "Error setting up XML Context", e);
		}
		catch ( IOException e )
		{
			logger.error( "Error reading configuration file", e);
		}
		catch ( SAXException e )
		{
			logger.error( "Error parsing configuration file", e);
		}	
	}
}
