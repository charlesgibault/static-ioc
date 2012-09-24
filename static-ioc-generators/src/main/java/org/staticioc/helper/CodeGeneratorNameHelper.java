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
