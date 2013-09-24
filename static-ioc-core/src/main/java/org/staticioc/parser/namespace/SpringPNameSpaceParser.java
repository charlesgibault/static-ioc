package org.staticioc.parser.namespace;

import org.staticioc.parser.NodeParserPlugin;
import org.staticioc.parser.plugins.SpringPParserPlugin;

public class SpringPNameSpaceParser extends AbstractNameSpaceParser
{	
	public SpringPNameSpaceParser() {
		NodeParserPlugin springPPluging = new SpringPParserPlugin();
		nodeParserPlugins.add( springPPluging );
	}

	@Override
	public String getNameSpaceUri() {
		return "http://www.springframework.org/schema/p";
	}
}
