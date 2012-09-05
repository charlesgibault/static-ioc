package org.staticioc;

import java.io.IOException;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;
import org.xml.sax.SAXException;

import org.staticioc.generator.CodeGenerator;
import org.staticioc.generator.JavaCodeGenerator;
import org.staticioc.model.*;
import org.staticioc.model.Bean.Scope;

public class SpringStaticFactoryGenerator
{
	private static final Logger logger  = LoggerFactory.getLogger(SpringStaticFactoryGenerator.class);
	
	public enum TargetCode { JAVA };
	
	private final static int INIT_BUFFER_SIZE=4096;
		
	private String generatedClassName = "GeneratedBeanContext";
	private final String configurationFile;
	
	private CodeGenerator codeGenerator;
	private TargetCode targetCode = TargetCode.JAVA;
	
	private boolean ignoreUnresolvedRefs = true;
	
	/**
	 * Constructor : init XML parser properly
	 * 
	 * @param configFile
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 * @throws IOException
	 * @throws XPathExpressionException
	 */
	public SpringStaticFactoryGenerator( final String configFile )
	{
		configurationFile = configFile;
	}
	
	/**
	 * Instantiate the proper code generator given target language. Bind StringBuilder that will hold generated result 
	 * @param res
	 * @return true if init was successful, false otherwise
	 */
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
	}	
	
	/**
	 * Ignore abstract beans and prototypes
	 * @param bean
	 * @return
	 */
	protected boolean isHidden( final Bean bean )
	{
		return bean.isAbstract() || bean.getScope().equals( Scope.PROTOTYPE );
	}
	
	/**
	 * Entry point for the service : generate code matching setup configuration file.
	 * @return
	 * @throws XPathExpressionException
	 * @throws IOException 
	 * @throws SAXException 
	 * @throws ParserConfigurationException 
	 */
	public StringBuilder generate()  throws SAXException, IOException, ParserConfigurationException
	{
		final StringBuilder res = new StringBuilder( INIT_BUFFER_SIZE );
		initCodeGenerator(res);
		
		SpringConfigParser springConfigParser = new SpringConfigParser();
		Map<String, Bean> beanClassMap = springConfigParser.load( configurationFile );
		
		codeGenerator.initClass( generatedClassName );
		
		// declare beans
		for ( String beanName: beanClassMap.keySet() )
		{
			final Bean bean = beanClassMap.get(beanName);
			
			// Ignore abstract beans and prototypes
			if ( isHidden(bean) ){ continue; }
						
			codeGenerator.declareBean( bean );
		}

		res.append("\n");
		codeGenerator.initConstructor( generatedClassName );
		
		// Instantiate beans
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
			
			codeGenerator.comment("Setting up bean " + beanName );			
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
		codeGenerator.closeClass( generatedClassName );
		return res;
	}

	public void setGeneratedClassName(String generatedClassName) {
		this.generatedClassName = generatedClassName;
	}

	public boolean isIgnoreUnresolvedRefs() {
		return ignoreUnresolvedRefs;
	}

	public void setIgnoreUnresolvedRefs(boolean ignoreUnresolvedRefs) {
		this.ignoreUnresolvedRefs = ignoreUnresolvedRefs;
	}

	public String getGeneratedClassName() {
		return generatedClassName;
	}

	public String getConfigurationFile() {
		return configurationFile;
	}
	
	// TODO clean up code generator interface / define abstract class ?
}
