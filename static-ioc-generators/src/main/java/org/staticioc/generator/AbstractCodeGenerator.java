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
package org.staticioc.generator;

public abstract class AbstractCodeGenerator implements CodeGenerator
{
	private StringBuilder builder;
	
	public final StringBuilder getBuilder() {
		return builder;
	}
	
	public void setOutput( StringBuilder builder)
	{
		this.builder = builder;
	}
	
	/**
	 * @return the character that separates package element and class name in a fully qualified class name.
	 * eg : "." for java, ":" for Cpp
	 */
	protected abstract String getPackageSeparator();
	
	@Override
	public String getFilePath( final String fullClassName )
	{
		if( fullClassName == null)
		{
			return "";
		}
		
		return fullClassName.replaceAll( "\\" + getPackageSeparator(), "/" );
	}

	@Override
	public String getClassName( String fullClassName )
	{
		if( fullClassName == null)
		{
			return "";
		}
		
		int lastPackageIndex = 1 + fullClassName.lastIndexOf( getPackageSeparator() );

		if( lastPackageIndex < 1 )
		{
			return fullClassName;
		}
		
		return fullClassName.substring( lastPackageIndex );
	}

	@Override
	public String getPackageName( String fullClassName )
	{
		if( fullClassName == null)
		{
			return "";
		}
		
		int lastPackageIndex = fullClassName.lastIndexOf( getPackageSeparator() );

		if( lastPackageIndex < 0 )
		{
			return "";
		}
		
		return fullClassName.substring( 0, lastPackageIndex );
	}
}
