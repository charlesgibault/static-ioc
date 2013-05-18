package org.staticioc.samples.gwt.client;

import org.staticioc.samples.gwt.client.view.EditableListView.Presenter;
import org.staticioc.samples.gwt.shared.model.Contact;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.user.client.ui.RootPanel;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class SampleGwtApplication implements EntryPoint {
	/**
	 * Load our application context once and for all
	 */
	private final ApplicationContext context = new ApplicationContext();

	private Presenter<Contact> presenter = context.contactsPresenter;
	/**
	 * This is the entry point method. Very simple thanks to MVP + IoC couple
	 */
	public void onModuleLoad() {
		// Add the nameField and sendButton to the RootPanel
		// Use RootPanel.get() to get the entire body element
		presenter.display( RootPanel.get("gwtAppContainer") );
	}
}
