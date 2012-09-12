/**
 * 
 */
package org.staticioc.helper;

import java.io.File;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.staticioc.generator.CodeGenerator;

/**
 * @author Charles Gibault
 * @date 12 sept. 2012
 */
public class IoCCompilerHelper
{
	private static final Logger logger  = LoggerFactory.getLogger(IoCCompilerHelper.class);
	
	public static void checkOutputPath( String outputPath)
	{		
		//Check that path exist
		File ofile = new File(outputPath);
		
		if( !ofile.exists()  )
		{
			throw new IllegalArgumentException("Output path " + outputPath + " does not exist");
		}
		else if (!ofile.canWrite() )
		{
			throw new IllegalArgumentException("Output path " + outputPath + " is not writable");
		}
	}
	
	public static CodeGenerator getCodeGenerator( String codeGeneratorClassName )
	{
		try
		{
			return (CodeGenerator) Class.forName( codeGeneratorClassName ).newInstance();
		}
		catch( ClassNotFoundException e)
		{
			throw new IllegalArgumentException("Code generator class not found on current classpath", e);
		}
		catch( Exception e)
		{
			throw new IllegalArgumentException("Code generator class could not be instanciated. Does it has a 0 args constructor and does it implements org.staticioc.generator.CodeGenerator interface?", e);
		}
	}
	
	public static Map< String,List<String> > getTargetMapping( String targetMappingAsText )
	{		
		//Parse targetMapping definition and build associated map
		Map< String,List<String> > targetMapping= new HashMap< String,List<String> >();
		
		//Expected format "package.name.target1:source1,source2;target2:source1,..."
		String[] targetMappings = targetMappingAsText.split( ";" );
		
		for( String targetExp : targetMappings )
		{
			String trimmedMapping = targetExp.trim();
			
			if( trimmedMapping.isEmpty() )
			{
				logger.warn( "Ignoring empty mapping {}", targetExp );
				continue;
			}
			
			int targetSourceSeparator = trimmedMapping.indexOf( ":" );
			
			if( targetSourceSeparator < 0 )
			{
				logger.warn( "Mapping format non recognized. Ignoring {}. Expected target:source1,source2...", trimmedMapping );
				continue;
			}
			
			String target = trimmedMapping.substring( 0, targetSourceSeparator );
			String[] sources = trimmedMapping.substring( targetSourceSeparator ).split( "," );
			
			List<String> sourcesList = new LinkedList<String>();
			
			for (String source : sources)
			{
				//TODO Check that source exists here ?
				String trimmedSource = source.trim();
				
				File file = new File(trimmedSource);
				
				if( !file.exists()  )
				{
					throw new IllegalArgumentException("Source file " + trimmedSource + " does not exist");
				}
				else if (!file.canRead() )
				{
					throw new IllegalArgumentException("Source file " + trimmedSource + " is not readable");
				}
				
				sourcesList.add(trimmedSource);				
			}
			
			targetMapping.put( target, sourcesList );
			
		}
		
		return targetMapping;
	}
}
