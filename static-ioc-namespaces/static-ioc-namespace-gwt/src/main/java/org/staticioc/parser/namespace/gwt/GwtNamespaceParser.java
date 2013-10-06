package org.staticioc.parser.namespace.gwt;

import org.staticioc.parser.AbstractNameSpaceParser;
import org.staticioc.parser.NodeSupportPlugin;


public class GwtNamespaceParser extends AbstractNameSpaceParser implements GwtNamespaceConstants
{
	@Override
	public String getNameSpaceUri() {
		return NAMESPACE_URI;
	}

	public GwtNamespaceParser() {
		NodeSupportPlugin gwtCreatePlugin = new GwtCreatePlugin();
		NodeSupportPlugin gwtMessagesPlugin = new GwtMessagesPlugin();
		NodeSupportPlugin gwtServicePlugin = new GwtServicePlugin();
		NodeSupportPlugin gwtEventBusPlugin = new GwtEventBusPlugin();
		NodeSupportPlugin gwtHistoryMapperPlugin = new GwtHistoryMapperPlugin();
		NodeSupportPlugin gwtActivityMapperPlugin = new GwtActivityManagerPlugin();		
		
		nodeSupportPlugins.add( gwtCreatePlugin );
		nodeSupportPlugins.add( gwtMessagesPlugin );
		nodeSupportPlugins.add( gwtServicePlugin );
		nodeSupportPlugins.add( gwtEventBusPlugin );
		nodeSupportPlugins.add( gwtHistoryMapperPlugin );
		nodeSupportPlugins.add( gwtActivityMapperPlugin );
	}
}
