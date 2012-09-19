package org.staticioc.helper;

import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.staticioc.generator.CodeGenerator;
import org.staticioc.generator.JavaCodeGenerator;

public class CodeGeneratorNameHelper
{
	// A listing of translatable generated code target names (in the future) 
	public enum TargetCode { JAVA, C, CPP, CXX, OBJECTIVEC, OBJC, PYTHON, JYTHON, DOTNET };
	
	private static final Map<TargetCode, Class<? extends CodeGenerator>> codeTranslations = new ConcurrentHashMap<TargetCode, Class<? extends CodeGenerator>>();
	
	static 
	{
		codeTranslations.put(TargetCode.JAVA, JavaCodeGenerator.class);
	}
	
	public static String getGeneratorClass( String name)
	{
		if( name == null )
		{
			throw new IllegalArgumentException( "Incorrect generator name provided" );
		}
		
		TargetCode target = TargetCode.valueOf(name.trim().toUpperCase(Locale.ENGLISH) );
		
		if( ! codeTranslations.containsKey( target ) )
		{
			throw new IllegalArgumentException( "Unsupported target language");
		}
		
		return codeTranslations.get( target ).getName();
	}
}
