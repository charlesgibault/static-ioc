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
	
	public static List<String> extractSourcesList(String sourcesListAsString)
	{
		String[] sources = sourcesListAsString.trim().split( "," );
		
		List<String> sourcesList = new LinkedList<String>();
		
		for (String source : sources)
		{
			String trimmedSource = source.trim();
			
			if( !trimmedSource.isEmpty() )
			{
				sourcesList.add(trimmedSource);				
			}
		}
		
		return sourcesList;
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
			
			int targetSourceSeparator = trimmedMapping.indexOf( ':' );
			
			if( targetSourceSeparator < 0 )
			{
				logger.warn( "Mapping format non recognized. Ignoring {}. Expected target:source1,source2...", trimmedMapping );
				continue;
			}
			
			String target = trimmedMapping.substring( 0, targetSourceSeparator );
			String sourcesAsString = trimmedMapping.substring( targetSourceSeparator + 1 );
			
			List<String> sourcesList = extractSourcesList( sourcesAsString );
			
			if( !sourcesList.isEmpty() && !target.isEmpty() )
			{
				targetMapping.put( target, sourcesList );
			}
			
		}
		
		return targetMapping;
	}
}
