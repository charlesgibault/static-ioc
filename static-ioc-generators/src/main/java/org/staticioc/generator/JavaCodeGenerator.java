package org.staticioc.generator;

import org.apache.commons.lang.WordUtils;
import org.staticioc.model.*;

public class JavaCodeGenerator extends AbstractCodeGenerator
{
	private final static String SETTER_PREFIX="set";
	
	public JavaCodeGenerator( StringBuilder builder )
	{
		super(builder);
	}
	
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
		getBuilder().append( "public class ").append( className ).append( "\n{\n" );
	}
	

	@Override
	public void closeClass(String className)
	{		
		getBuilder().append("\n}");
	}

	@Override
	public void initConstructor( String className )
	{
		getBuilder().append("\tpublic ").append(className).append("()\n\t{\n" );	
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
			getBuilder().append("\tprivate final ");
		}
		else
		{
			getBuilder().append("\tpublic final ");
		}
			
		getBuilder().append( getBeanClass( bean ) ).append(" ").append( bean.getName() ).append( ";\n" );
	}
	
	protected String getBeanClass( Bean bean )
	{
		switch ( bean.getType() )
		{
			case LIST:
				return getListClass();
			case SET:
				return getSetClass();
			case MAP:
				return getMapClass();
			case PROPERTIES:
				return getPropertiesClass();
			default:
				return bean.getClassName();
		}
	}
	
	@Override
	public void instantiateBean( Bean bean )
	{
		getBuilder().append("\t\t").append( bean.getName() ).append( " = new " ).append( getBeanClass( bean ) ).append("(");
		
		// handle constructor args
		boolean isFirstArg = true;
		for( Property prop : bean.getConstructorArgs())
		{
			// handle parameter separation
			if( !isFirstArg ) { getBuilder().append( ", " ); }
			isFirstArg = false;
			
			appendPropertyValue( prop );
		}
		
		getBuilder().append(");\n");
	}
	
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
			else
			{
				try
				{
					Double.parseDouble( property.getValue() );
					getBuilder().append( property.getValue() );	

				} catch (NumberFormatException nfe) {
					// Not a number -> add quotes around the value
					getBuilder().append('"').append( property.getValue() ).append('"');	
				}
			}

		}
		else // ref == null, value == null --> <null/> value
		{
			getBuilder().append( getNull() );
		}
	}

	@Override
	public void declareProperty( Bean bean, final Property property )
	{
		getBuilder().append( "\t\t").append( bean.getName() ).append("." ).append( getAssignMethod( bean, property ) );
		appendPropertyValue( property );
		getBuilder().append( " );\n" );	
	}
	
	
	
	/* (non-Javadoc)
	 * @see org.staticioc.generator.CodeGenerator#getFilePath(java.lang.String)
	 */
	@Override
	public String getFilePath( String fullClassName )
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void initPackage( String packageDef )
	{
		getBuilder().append("package ").append( packageDef ).append( "\n");
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
		getBuilder().append( "\t" ).append( bean.getName() ).append(" = null;");
	}

	protected String getListClass() {
		return "java.util.LinkedList";
	}

	protected String getSetClass() {
		return "java.util.HashSet";
	}

	protected String getMapClass() {
		return "java.util.HashMap";
	}

	protected String getPropertiesClass() {
		return "java.util.Properties";
	}

	protected String getNull() {
		return "null";
	}
}
