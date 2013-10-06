package org.staticioc.parser.namespace.gwt;

interface GwtNamespaceConstants {
	String NAMESPACE_URI="http://com.googlecode.static-ioc/schema/gwt";
	
	String FACTORY_BEAN="com.google.gwt.core.client.GWT";
	String CLASS_SUFFIX = ".class";
	String CREATE="create";
	String MESSAGES="messages";

	String SERVICE="service";
	String ASYNC_SERVICE_SUFFIX = "Async";
	
	String EVENT_BUS="eventBus";
	String DEFAULT_EVENT_BUS_CLASS = "com.google.web.bindery.event.shared.EventBus";
	String DEFAULT_EVENT_BUS_INTERFACE = "com.google.gwt.event.shared.SimpleEventBus";

	String HISTORY_MAPPER="historyMapper";
	String ACTIVITY_MANAGER="activityManager";
	String ACTIVITY_MANAGER_CLASS="com.google.gwt.activity.shared.ActivityManager";
	String ACTIVITY_MAPPER="activityMapper";

	String TYPE="type";
	String INTERFACE="interface";
	
	
}
