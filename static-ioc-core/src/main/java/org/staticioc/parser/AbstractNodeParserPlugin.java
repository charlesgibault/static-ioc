package org.staticioc.parser;


public abstract class AbstractNodeParserPlugin implements ParserConstants,  NodeParserPlugin
{
	protected BeanParser beanParser;
	protected String prefix = "";
	
	@Override
	public void setBeanParser(BeanParser parser)
	{
		this.beanParser = parser;
	}

	@Override
	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}
}
