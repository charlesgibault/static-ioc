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
package org.staticioc.parser;

import java.util.LinkedList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NamespaceHelper
{
	private static final Logger logger  = LoggerFactory.getLogger( NamespaceHelper.class );

	/**
	 * Instanciate new instances of NamespaceParser implementations using their classname
	 * 
	 * @param namespacePluginsAsText Comma separated list of NamespaceParser implementation names (package.classname) to instanciate 
	 * @return a list of NamespaceParser implementations
	 */
	public static List<NamespaceParser> getNamespacePlugins( String namespacePluginsAsText )
	{
		LinkedList<String> namespacePluginNames = new LinkedList<String> ();
		
		if( namespacePluginsAsText != null)
		{

			//Expected format "package1.namespacePlugin1,package2.namespacePlugin2,..."
			String[] namespacePlugins = namespacePluginsAsText.split( "," );

			for( String pluginName : namespacePlugins )
			{
				String trimmedPluginName = pluginName.trim();

				if( trimmedPluginName.isEmpty() )
				{
					logger.warn( "Ignoring empty namespace plugin name {}", pluginName );
					continue;
				}
				
				namespacePluginNames.add( trimmedPluginName );
			}
		}
		
		return getNamespacePlugins(namespacePluginNames);
	}
	
	/**
	 * Instanciate new instances of NamespaceParser implementations using their classname
	 * 
	 * @param namespacePluginNames List of NamespaceParser implementation names (package.classname) to instanciate 
	 * @return a list of NamespaceParser implementations
	 */
	public static List<NamespaceParser> getNamespacePlugins( List<String> namespacePluginNames )
	{
		List<NamespaceParser> plugins = new LinkedList<NamespaceParser> ();
		
		if( namespacePluginNames != null )
		{
			for ( String pluginName : namespacePluginNames)
			{
				logger.debug( "Loading extra NamespaceParser plugin {}", pluginName );
				plugins.add( getNamespacePluginClass(pluginName) );
				logger.info( "Loaded extra NamespaceParser plugin {}", pluginName );
			}
		}
		
		return plugins;
	}

	/**
	 * Instanciate a new instance of a NamespaceParser implementation using its classname
	 * 
	 * @param classname full name (package.classname) to instanciate
	 * @return a new instance of the NamespaceParser
	 */
	public static NamespaceParser getNamespacePluginClass( String classname)
	{
		try
		{
			return (NamespaceParser) Class.forName( classname ).newInstance();
		}
		catch( ClassNotFoundException e)
		{
			throw new IllegalArgumentException("NamespaceParser plugin class not found on current classpath", e);
		}
		catch( Exception e)
		{
			throw new IllegalArgumentException("NamespaceParser plugin could not be instanciated. Does it has a 0 args constructor and does it implements org.staticioc.parser.NamespaceParser interface?", e);
		}
	}
}
