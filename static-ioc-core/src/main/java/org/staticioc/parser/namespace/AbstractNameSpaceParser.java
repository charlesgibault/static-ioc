package org.staticioc.parser.namespace;

import java.util.LinkedList;
import java.util.List;

import org.staticioc.parser.BeanParser;
import org.staticioc.parser.NamespaceParser;
import org.staticioc.parser.NodeParserPlugin;
import org.staticioc.parser.NodeSupportPlugin;

public abstract class AbstractNameSpaceParser implements NamespaceParser
{
	protected final List<NodeSupportPlugin> nodeSupportPlugins = new LinkedList<NodeSupportPlugin>();
	protected final List<NodeParserPlugin> nodeParserPlugins = new LinkedList<NodeParserPlugin>();
	protected String prefix="";
	
	@Override
	public List<NodeParserPlugin> getNodeParserPlugins() {
		return nodeParserPlugins;
	}

	@Override
	public List<NodeSupportPlugin> getNodeSupportPlugins() {
		return nodeSupportPlugins;
	}

	@Override
	public void setBeanParser(BeanParser beanParser) {
		for( NodeParserPlugin plugin : nodeParserPlugins)
		{
			plugin.setBeanParser(beanParser);
		}
		
		for( NodeSupportPlugin plugin : nodeSupportPlugins)
		{
			plugin.setBeanContainer(beanParser);
		}

	}

	@Override
	public void setPrefix(String prefix) {
		this.prefix = prefix;
		for( NodeParserPlugin plugin : nodeParserPlugins)
		{
			plugin.setPrefix(prefix);
		}
		
		for( NodeSupportPlugin plugin : nodeSupportPlugins)
		{
			plugin.setPrefix(prefix);
		}
	}
}
