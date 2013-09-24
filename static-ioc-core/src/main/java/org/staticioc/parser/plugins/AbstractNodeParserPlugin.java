package org.staticioc.parser.plugins;

import org.staticioc.parser.BeanParser;
import org.staticioc.parser.NodeParserPlugin;
import org.staticioc.parser.ParserConstants;

public abstract class AbstractNodeParserPlugin implements ParserConstants,  NodeParserPlugin
{
	protected BeanParser container;
	protected String prefix = "";
	
	@Override
	public void setBeanContainer(BeanParser container)
	{
		this.container = container;
	}

	@Override
	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}
}
