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
