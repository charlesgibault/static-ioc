package org.staticioc;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import org.xml.sax.SAXException;
import javax.xml.parsers.ParserConfigurationException;

public class StaticIocRunner
{
	private static final Logger logger  = LoggerFactory.getLogger(StaticIocRunner.class);
	
	/**
	 * @param args
	 */
	public static void main(String[] args)
	{
		SpringStaticFactoryGenerator springStaticFactoryGenerator;
		
		// Parameters
		String configFile = "C:/Charles/workspace/staticioc/src/test/resources/test.xml"; //TODO handle list of files
		String targetClass = "GeneratedBeanContext"; //TODO parse package information ? (language dependent)
		String targetPath = "C:/Charles/workspace/staticioc/target/"; //Source root
		String targetFileExt = ".java"; //Source root
		
		// Computed values
		String outputPath = targetPath + targetClass + targetFileExt; //TODO handle file extension (language dependent)
		
		try
		{
			springStaticFactoryGenerator = new SpringStaticFactoryGenerator( configFile );
			springStaticFactoryGenerator.setGeneratedClassName( targetClass );
			
			StringBuilder output = springStaticFactoryGenerator.generate();
			
			writeToFile( output.toString(), outputPath);
			
			logger.info( output.toString() );
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
	
	protected static void writeToFile( final String content, final  String path)
	{
		FileWriter fileWriter = null;
        try
        {
            
            File newTextFile = new File(path);
            fileWriter = new FileWriter(newTextFile);
            fileWriter.write(content);
            fileWriter.close();
        }
        catch (IOException ex)
        {
        	logger.error("Error writing file " + path, ex);
        }
        finally
        {
            try
            {
                if ( fileWriter != null )
                {
                	fileWriter.close();
                }
            }
            catch (IOException e)
            {
            	logger.error("Error writing file " + path, e);
            }
        }
	}
}
