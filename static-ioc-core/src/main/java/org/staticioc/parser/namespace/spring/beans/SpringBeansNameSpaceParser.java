package org.staticioc.parser.namespace.spring.beans;

import org.staticioc.parser.AbstractNameSpaceParser;
import org.staticioc.parser.NodeParserPlugin;
import org.staticioc.parser.NodeSupportPlugin;

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
		NodeSupportPlugin beanPlugin = new BeanPlugin();
		NodeSupportPlugin refPlugin = new RefPlugin();
		NodeSupportPlugin idrefPlugin = new IdRefPlugin();
		NodeSupportPlugin valuePlugin = new ValuePlugin();
		NodeSupportPlugin nullPlugin = new NullPlugin();
		
		nodeSupportPlugins.add( beanPlugin );
		nodeSupportPlugins.add( listPlugin );
		nodeSupportPlugins.add( setPlugin );
		nodeSupportPlugins.add( mapPlugin );
		nodeSupportPlugins.add( propertiesPlugin );
		nodeSupportPlugins.add( refPlugin );
		nodeSupportPlugins.add( idrefPlugin );
		nodeSupportPlugins.add( valuePlugin );
		nodeSupportPlugins.add( nullPlugin );
		
	}

	@Override
	public String getNameSpaceUri() {
		return "http://www.springframework.org/schema/beans";
	}
}
