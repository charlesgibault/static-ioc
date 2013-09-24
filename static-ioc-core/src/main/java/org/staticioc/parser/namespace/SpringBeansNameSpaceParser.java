package org.staticioc.parser.namespace;

import org.staticioc.parser.NodeParserPlugin;
import org.staticioc.parser.NodeSupportPlugin;
import org.staticioc.parser.plugins.BeanPropertiesParserPlugin;
import org.staticioc.parser.plugins.ConstructorArgsPlugin;
import org.staticioc.parser.plugins.ListPlugin;
import org.staticioc.parser.plugins.MapPlugin;
import org.staticioc.parser.plugins.PropertiesPlugin;
import org.staticioc.parser.plugins.SetPlugin;;

public class SpringBeansNameSpaceParser extends AbstractNameSpaceParser
{	
	public SpringBeansNameSpaceParser()
	{
		NodeParserPlugin constructorArgsPlugin = new ConstructorArgsPlugin();
		NodeParserPlugin beanPropertiesPlugin = new BeanPropertiesParserPlugin();

		nodeParserPlugins.add( constructorArgsPlugin );
		nodeParserPlugins.add( beanPropertiesPlugin );


		NodeSupportPlugin listPlugin = new ListPlugin();
		NodeSupportPlugin setPlugin = new SetPlugin();
		NodeSupportPlugin mapPlugin = new MapPlugin();
		NodeSupportPlugin propertiesPlugin = new PropertiesPlugin();

		nodeSupportPlugins.add( listPlugin );
		nodeSupportPlugins.add( setPlugin );
		nodeSupportPlugins.add( mapPlugin );
		nodeSupportPlugins.add( propertiesPlugin );
	}

	@Override
	public String getNameSpaceUri() {
		return "http://www.springframework.org/schema/beans";
	}
}
