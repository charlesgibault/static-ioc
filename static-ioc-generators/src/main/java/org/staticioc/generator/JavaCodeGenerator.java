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

import org.apache.commons.lang.WordUtils;
import org.staticioc.model.*;

public class JavaCodeGenerator extends AbstractCodeGenerator
{
	private final static String SETTER_PREFIX="set";
		
	protected String getSetterName( final Property property )
	{
		return SETTER_PREFIX + WordUtils.capitalize( property.getName() );
	}
	
	protected String getAssignMethod( final Bean bean, final Property property )
	{
		switch ( bean.getType() )
		{
			case LIST:
			case SET:
				return "add( ";
			case MAP:
				if( property.isKeyRef() )
				{
					return "put( " + property.getName() + ", ";
				}
				else
				{
					return "put( \"" + property.getName() + "\", ";
				}			
				
			case PROPERTIES:
				return "setProperty( \"" + property.getName() + "\", ";
			default:
					
				return getSetterName(property) + "( ";
		}
	}
	
	@Override
	public void comment( Level level, String comment )
	{
		switch ( level )
		{
		case HEADER:
			getBuilder().append( "/** ").append(comment).append(" */\n");
			break;
			
		case CLASS:
			getBuilder().append( "\t/**\n\t * ").append(comment).append("\n*/\t\n");
			break;
			
		case METHOD:
			getBuilder().append( "\t\t// ").append(comment).append("\n");
			break;
		}
	}
		
	@Override
	public void initClass( String className )
	{
		getBuilder().append( "@SuppressWarnings(\"rawtypes\")\n").append("public class ").append( className ).append( "\n{\n" );
	}
	

	@Override
	public void closeClass(String className)
	{		
		getBuilder().append("\n}");
	}

	@Override
	public void initConstructor( String className )
	{
		getBuilder().append("\t@SuppressWarnings(\"unchecked\")\n").append("\tpublic ").append(className).append("()\n\t{\n" );	
	}
	
	@Override
	public void closeConstructor(String className)
	{		
		getBuilder().append( "\n\t}" );
	}
	
	@Override
	public void declareBean( Bean bean )
	{
		if ( bean.isAnonymous() )
		{
			getBuilder().append("\tprivate ");
		}
		else
		{
			getBuilder().append("\tpublic ");
		}
			
		getBuilder().append( getBeanClass( bean ) ).append(" ").append( bean.getId() ).append( ";\n" );
	}
	
	protected String getBeanClass( Bean bean )
	{
		switch ( bean.getType() ) //TODO replace with thread safe concurrent collection for list/set/properties ?
		{
			case LIST:
				return "java.util.LinkedList";
			case SET:
				return "java.util.HashSet";
			case MAP:
				return "java.util.ConcurrentHashMap";
			case PROPERTIES:
				return "java.util.Properties";
			default:
				return bean.getClassName();
		}
	}
	
	@Override
	public void instantiateBean( Bean bean )
	{
		getBuilder().append("\t\t").append( bean.getId() ).append( " = new " ).append( getBeanClass( bean ) ).append("(");
		appendConstructorArgs(bean);
		getBuilder().append(");\n");
	}
	
	@Override
	public void instantiateBeanWithFactory( Bean bean )
	{
		getBuilder().append("\t\t").append( bean.getId() ).append( " = " ).append( bean.getFactoryBean() ).append(".").append(getFactoryMethod(bean)).append("(");		
		appendConstructorArgs(bean);
		getBuilder().append(");\n");
	}
	
	@Override
	public void declareProperty( Bean bean, final Property property )
	{
		getBuilder().append( "\t\t").append( bean.getId() ).append("." ).append( getAssignMethod( bean, property ) );
		appendPropertyValue( property );
		getBuilder().append( " );\n" );	
	}
	
	@Override
	protected String getPackageSeparator()
	{
		return ".";
	}

	@Override
	public String getDefaultSourceFileExtension()
	{
		return ".java";
	}

	@Override
	public void initPackage( String packageDef )
	{
		if (packageDef != null && !packageDef.isEmpty() )
		{
			getBuilder().append("package ").append( packageDef ).append( ";\n");
		}
	}

	@Override
	public void closePackage( String packageDef )
	{
		//Nothing to do in Java
	}

	@Override
	public void initDestructor( String className )
	{
		getBuilder().append("\tpublic void destroyContext()\n\t{\n" );
	}
	
	@Override
	public void closeDestructor( String className )
	{
		getBuilder().append( "\n\t}" );	
	}

	@Override
	public void deleteBean( Bean bean )
	{
		getBuilder().append( "\t" ).append( bean.getId() ).append(" = null;");
	}
}
