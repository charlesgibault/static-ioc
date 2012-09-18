package org.staticioc;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

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
	
	public enum TargetCode { JAVA };
	
	private final static int INIT_BUFFER_SIZE=4096;
		
	private String commentHeader = "This code has been generated using Static IoC framework (http://code.google.com/p/static-ioc/). DO NOT EDIT MANUALLY HAS CHANGES MAY BE OVERRIDEN";
		
	private boolean ignoreUnresolvedRefs = true;
		
	//	private TargetCode targetCode = TargetCode.JAVA;
	//TODO move to helper ?
	/**
	 * Instantiate the proper code generator given target language. Bind StringBuilder that will hold generated result 
	 * @param res
	 * @return true if init was successful, false otherwise
	
	protected boolean initCodeGenerator( StringBuilder res )
	{
		switch ( targetCode)
		{
		case JAVA:
			codeGenerator = new JavaCodeGenerator( res );
			
			break;
		default:
			logger.error( "Unsupported target : {}", targetCode );
			return false;
		}
		return true;
	}*/	
	
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
	public void compile( final CodeGenerator generator, final String outputPath, final Map< String, List< String >> inputOutputMapping, String fileExtensionOverride ) throws SAXException,
	IOException, ParserConfigurationException
	{
		// Check if a valid extension override was provided
		fileExtensionOverride = (fileExtensionOverride == null) ? generator.getDefaultSourceFileExtension() : fileExtensionOverride;
		
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
		Map<String, Bean> beanClassMap = springConfigParser.load( configurationFiles );

		codeGenerator.comment(Level.HEADER, commentHeader );

		codeGenerator.initPackage( generatedPackageName );		
		codeGenerator.initClass( generatedClassName );
		
		// declare beans
		codeGenerator.comment(Level.CLASS, "Bean definition" );
		for ( String beanName: beanClassMap.keySet() )
		{
			final Bean bean = beanClassMap.get(beanName);
			
			// Ignore abstract beans and prototypes
			if ( isHidden(bean) ){ continue; }
						
			codeGenerator.declareBean( bean );
		}

		res.append("\n");
		codeGenerator.comment(Level.CLASS, "Constructor of the Factory" );
		codeGenerator.initConstructor( generatedClassName );
		
		// Instantiate beans
		codeGenerator.comment(Level.METHOD, "Instanciating beans" );			
		
		for ( String beanName: beanClassMap.keySet() )
		{
			final Bean bean = beanClassMap.get(beanName);
			
			// Ignore abstract beans and prototypes
			if ( isHidden(bean) ){ continue; }
						
			codeGenerator.instantiateBean( bean );
		}
		
		res.append("\n");
		
		// Set properties
		for ( String beanName: beanClassMap.keySet() )
		{
			final Bean bean = beanClassMap.get(beanName);
			
			// Ignore abstract beans and beans with no properties
			if ( isHidden(bean) || bean.getProperties().isEmpty() ){ continue; }
			
			codeGenerator.comment(Level.METHOD, "Setting up bean " + beanName );			
			for( Property prop : bean.getProperties() )
			{
				// Check that the reference exists and is not abstract
				if( prop.getRef() != null )
				{
					final Bean refBean = beanClassMap.get( prop.getRef() );
					
					if( refBean == null )
					{
						logger.warn( "Reference {} does not exist for Bean {}", prop.getRef(), bean.getName() );
						
						if( isIgnoreUnresolvedRefs() )
						{
							continue;
						}
						
					}
					else if ( refBean.isAbstract() )
					{
						logger.warn( "Reference {} is abstract for Bean {}", prop.getRef(), bean.getName() );
						continue;
					}
				}
				
				codeGenerator.declareProperty( bean, prop );
			}
			res.append("\n");
		}
		
		codeGenerator.closeConstructor( generatedClassName );
		
		codeGenerator.initDestructor( generatedClassName );
		//TODO build destructor : free beans in their reverse order of creation
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
