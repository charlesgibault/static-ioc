package org.staticioc.parser.namespace.spring.p;

import org.staticioc.parser.AbstractNameSpaceParser;
import org.staticioc.parser.NodeParserPlugin;

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
