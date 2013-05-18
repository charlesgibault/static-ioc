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

import org.staticioc.model.Bean;
import org.staticioc.model.Property;

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
	
	/** 
	 * Overridable extra logic that analyze a String value and determines whether it should be quoted
	 * or not when declaring property
	 * @param value
	 * @return true if quotes must be used
	 */
	protected boolean useQuotes( String value )
	{
		if( value.endsWith(getClassExtension()) ) // Handle class type without quotes 
		{
			return false;
		}
		else
		{
			try
			{
				Double.parseDouble( value );
				return false;

			} catch (NumberFormatException nfe) {
				return true;
			}
		}
	}
	
	/**
	 * Append a property's value to the generated source
	 * @param property
	 */
	protected void appendPropertyValue( final Property property )
	{
		if ( property == null )
		{
			getBuilder().append( getNull() );
		}
		else if ( property.getRef() != null )
		{
			getBuilder().append( property.getRef() );	
		}
		else if ( property.getValue() != null ) // Value case :
		{
			if( property.getType() != null )				
			{
				getBuilder().append( "new " ).append(  property.getType() ).append("( ").append( property.getValue() ).append( " )");
			}
			else if( "true".equalsIgnoreCase( property.getValue() ) ) // Must be careful with char encoding here, so using equalsIgnoreCase
			{
				getBuilder().append( getTrue() );	
			}
			else if( "false".equalsIgnoreCase( property.getValue() ) ) // Must be careful with char encoding here, so using equalsIgnoreCase
			{
				getBuilder().append( getFalse() );	
			}
			else if( useQuotes( property.getValue() ) )
			{
				getBuilder().append('"').append( property.getValue() ).append('"');	
			}
			else
			{
				getBuilder().append( property.getValue() );	
			}

		}
		else // ref == null, value == null --> <null/> value
		{
			getBuilder().append( getNull() );
		}
	}
	
	protected void appendConstructorArgs(Bean bean)
	{
		// handle constructor args
		boolean isFirstArg = true;
		for( Property prop : bean.getConstructorArgs())
		{
			// handle parameter separation
			if( !isFirstArg ) { getBuilder().append( getSeparator() ); }
			isFirstArg = false;
			
			appendPropertyValue( prop );
		}
	}
	
	protected String getFactoryMethod(Bean bean)
	{
		return bean.getFactoryMethod()!=null?bean.getFactoryMethod():getDefaultFactoryMethod();
	}
	
	protected String getNull() {
		return "null";
	}
	
	protected String getSeparator()
	{
		return ", ";
	}
	
	protected String getDefaultFactoryMethod()
	{
		return "create";
	}
	
	protected String getClassExtension()
	{
		return ".class";
	}
	
	protected String getTrue()
	{
		return "true";
	}

	protected String getFalse()
	{
		return "false";
	}
}
