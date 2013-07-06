package org.staticioc.samples.gwt.client;

import org.staticioc.samples.gwt.client.places.EditPlace;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceHistoryHandler;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.SimplePanel;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class SampleGwtApplication implements EntryPoint {
	/**
	 * Load our application context once and for all
	 */
	private final ApplicationContext context = new ApplicationContext();
	private SimplePanel appWidget = new SimplePanel();
	private Place defaultPlace = new EditPlace("contacts");

	/**
	 * This is the entry point method. Very simple thanks to MVP + IoC couple
	 */
	public void onModuleLoad() {
		// Use RootPanel.get() to get the entire body element
	    context.activityManager.setDisplay(appWidget);
		
	    PlaceHistoryHandler historyHandler = new PlaceHistoryHandler(context.historyMapper);
        historyHandler.register(context.placeController, context.eventBus, defaultPlace);

    	RootPanel.get("gwtAppContainer").add(appWidget);
        
	    // Goes to the place represented on URL else default place
	    historyHandler.handleCurrentHistory();

	}
}
