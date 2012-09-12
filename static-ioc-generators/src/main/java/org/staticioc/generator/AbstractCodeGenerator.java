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
