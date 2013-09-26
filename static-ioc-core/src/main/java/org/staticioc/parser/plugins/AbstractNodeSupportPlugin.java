package org.staticioc.parser.plugins;

import org.staticioc.parser.BeanParser;
import org.staticioc.parser.NodeSupportPlugin;
import org.staticioc.parser.ParserConstants;
import org.staticioc.parser.ParserHelper;

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
