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
	private static final String ARG_TARGET_MAPPING = "t";

	private static final Logger logger  = LoggerFactory.getLogger(StaticIoCCommandLineCompiler.class);
	
	private static final String USAGE = "StaticIoCCommandLineCompiler -"+ ARG_CODE_GENERATOR + " <org.staticioc.javaCodeGenerator> -"
																+ ARG_OUTPUT_PATH+" </output/path> -"
																+ ARG_TARGET_MAPPING + " <org.demo.GeneratedFile:src/main/resources/context.xml>";
	
	private static final HelpFormatter formatter = new HelpFormatter();
	
	@SuppressWarnings( "static-access" )
	private static Options buildOptions()
	{
		Option help = new Option( ARG_HELP, "help", false, "print this message" );
		
		Option codeGenerator = OptionBuilder.withArgName( "package.codeGeneratorClass" )
				.hasArg()
				.withDescription(  "Fully qualified classname for the code generator" )
				.withLongOpt( "generator" )
				.create( ARG_CODE_GENERATOR );
		
		Option outputPath = OptionBuilder.withArgName( "path/to/output/folder" )
				.hasArg()
				.withDescription(  "Path were IoC source code will be generated" )
				.withLongOpt( "output-path" )
				.create( ARG_OUTPUT_PATH );
		
		Option targetMapping = OptionBuilder.withArgName( "package.name.target1:source1,source2;target2:source1,..." )
				.hasArg()
				.withDescription(  "Collection of mappings < target file -> List< source file > >" )
				.withLongOpt( "target-map" )
				.create( ARG_TARGET_MAPPING );
			
		Option fileExtension = OptionBuilder.withArgName( ".abc" )
				.hasArg()
				.withDescription(  "Generated file extension override (Optional)" )
				.withLongOpt( "file-extension" )
				.create( ARG_FILE_EXTENSION );
		
		Options options = new Options();
		options.addOption( help );
		options.addOption( codeGenerator );
		options.addOption( outputPath );
		options.addOption( targetMapping );
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
		
		if( line.hasOption( ARG_CODE_GENERATOR ) ) { // Output Path
			codeGeneratorClassName = line.getOptionValue( ARG_CODE_GENERATOR );
			logger.debug( "Using Code Generator {}", codeGeneratorClassName );
		}
		else
		{
			formatter.printHelp( USAGE, options );
			throw new IllegalArgumentException("Missing code generator");
		}

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
