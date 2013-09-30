package org.staticioc.parser;


public abstract class AbstractNodeSupportPlugin implements ParserConstants,  NodeSupportPlugin
{
	protected BeanParser beanParser;
	protected String prefix = "";
	
	@Override
	public void setBeanContainer(BeanParser beanParser)
	{
		this.beanParser = beanParser;
	}

	@Override
	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}
	
	@Override
	public final String getSupportedNode()
	{
		return ParserHelper.prefixedName(prefix, getUnprefixedSupportedNode() );
	}
	
	protected abstract String getUnprefixedSupportedNode();
}
