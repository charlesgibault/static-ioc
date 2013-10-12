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
package org.staticioc.samples.gwt.client;

import org.staticioc.samples.gwt.client.places.EditPlace;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceHistoryHandler;
import com.google.gwt.user.client.ui.RootPanel;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class SampleGwtApplication implements EntryPoint {
	/**
	 * Load our application context once and for all
	 */
	private final ApplicationContext context = new ApplicationContext();
	private Place defaultPlace = new EditPlace("contacts");

	/**
	 * This is the entry point method. Very simple thanks to MVP + IoC couple
	 */
	public void onModuleLoad() {	
	    PlaceHistoryHandler historyHandler = new PlaceHistoryHandler(context.historyMapper);
        historyHandler.register(context.placeController, context.eventBus, defaultPlace);

    	RootPanel.get("gwtAppContainer").add(context.appWidget);
        
	    // Goes to the place represented on URL else default place
	    historyHandler.handleCurrentHistory();

	}
}
