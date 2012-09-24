package org.staticioc;

/**
 * Static IoC - Compile XML based inversion of control configuration file into a single init class, for many languages.
 * Project Home : http://code.google.com/p/static-ioc/
 * 
 *   Copyright 2012 Charles Gibault
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.staticioc.generator.CodeGenerator;
import org.staticioc.helper.CodeGeneratorNameHelper;
import org.staticioc.helper.IoCCompilerHelper;

import org.slf4j.impl.StaticLoggerBinder;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Convert IoC configuration file into source code that can be part of the compile process
 * 
 * @goal generate
 * @phase generate-sources
 * @requiresDependencyResolution compile
 * @author <a href="https://code.google.com/p/static-ioc/">Charles Gibault</a>
 * @version $Id$
 */
public class StaticIocMojo
    extends AbstractMojo
{
	 /**
     * Optional override of the output file extension
     *
     * @parameter expression="${staticioc.outputFileExtension}"
     */
    private String outputFileExtension = null;
	
    /**
     * Name of the target language for generated source
     * @parameter expression="${staticioc.targetLanguage}"
     */
    private String targetLanguage = null;
    
    /**
     * Fully qualified name of the Code generator to use
     * @parameter expression="${staticioc.generator}"
     */
    private String generator = null;
    
    /**
     * Location of the generated file. By default : put into the src directory
     * @parameter expression="${staticioc.outputPath}"  default-value="${project.src.directory}"
     */
    private File outputPath;

    /**
     * Target mapping configuration
     * <targetMapping>
  	 *   <target1>source,source2,source3</target1>
  	 *   <target2>source4</target2>
	 * </targetMapping>
     * 
     * @parameter expression="${staticioc.targetMapping}"
     * @required
     */
    private Map<String,String> targetMapping;
    
    
    /**
     * Actual plugin execution
     */
    public void execute() throws MojoExecutionException, MojoFailureException 
    {
    	//Have the slf4j and maven logs communicate
    	StaticLoggerBinder.getSingleton().setMavenLog( getLog() );
    	
        File f = outputPath;
        //TODO check if we throw an error instead 
        if ( !f.exists() )
        {
            f.mkdirs();
        }

        IoCCompiler springStaticFactoryGenerator = new SpringStaticFactoryGenerator();
        CodeGenerator generator = getCodeGenerator();
        
        try
        {
        	springStaticFactoryGenerator.compile( generator, outputPath.getAbsolutePath(), getTargetMapping(), outputFileExtension );
        }
        catch ( Exception e )
        {
            throw new MojoExecutionException ( "Error generating static ioc", e );
        }
    }
    
    private CodeGenerator getCodeGenerator() throws MojoFailureException 
	{
    	if( generator == null && targetLanguage != null )
    	{		
    		getLog().debug( "Using Target language " + targetLanguage );
    		generator = CodeGeneratorNameHelper.getGeneratorClass( targetLanguage );
    	}
    	
    	if (generator == null)
    	{
    		 throw new MojoFailureException ( "Missing target language (<targetLanguage>) or code generator (<generator>) property. You must specify at least one option" );
    	}
		
		return IoCCompilerHelper.getCodeGenerator( generator );
	}
	
    private Map< String,List<String> > getTargetMapping()
	{
    	Map< String,List<String> > result = new HashMap< String, List<String> >();
    	
    	for( String target : targetMapping.keySet() )
    	{
    		List<String> sources = IoCCompilerHelper.extractSourcesList( targetMapping.get( target ) );
    		
    		if( !sources.isEmpty() )
    		{
    			result.put( target, sources);
    		}
    	}
    	
    	return result;
	}
}
