package org.staticioc.generator;

public abstract class AbstractCodeGenerator implements CodeGenerator
{
	private final StringBuilder builder;
	
	/**
	 * Default constructor
	 * @param builder StringBuilder that will contain generated code
	 */
	AbstractCodeGenerator( final StringBuilder builder )
	{
		if (builder != null)
		{
			this.builder = builder;
		}
		else
		{
			this.builder = new StringBuilder();
		}
			
	}

	public final StringBuilder getBuilder() {
		return builder;
	}
}
