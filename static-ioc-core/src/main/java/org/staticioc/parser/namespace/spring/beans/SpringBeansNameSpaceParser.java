/**
 *  Copyright (C) 2013 Charles Gibault
 *
 *  Static IoC - Compile XML based inversion of control configuration file into a single init class, for many languages.
 *  Project Home : http://code.google.com/p/static-ioc/
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
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
