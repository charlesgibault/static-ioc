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
package org.staticioc.samples.gwt.client.presenter;

import java.util.List;

import org.staticioc.samples.gwt.client.Messages;
import org.staticioc.samples.gwt.client.places.EditPlace;
import org.staticioc.samples.gwt.client.service.ContactsServiceAsync;
import org.staticioc.samples.gwt.client.view.MessagePopUpView;
import org.staticioc.samples.gwt.client.view.ReportingView;
import org.staticioc.samples.gwt.shared.model.Contact;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AcceptsOneWidget;

public class ContactsReportingPresenterImpl  extends AbstractActivity implements ReportingView.Presenter<Contact>, MessagePopUpView.Presenter {

	private ReportingView<Contact> reportingView;
	private MessagePopUpView errorView;
	private ContactsServiceAsync contactsService;
	private Messages messages;
	private EventBus eventBus;
	private PlaceController placeController;

	@Override
	public void display(AcceptsOneWidget container) {
		container.setWidget( reportingView.asWidget() );
		fetchContacts();
	}
	
	@Override
	public void start(AcceptsOneWidget containerWidget, EventBus arg1) {		
		display(containerWidget);
	}
	
	public void fetchContacts() {
		contactsService.retrieveContacts( new AsyncCallback<List<Contact>>() {
			public void onFailure(Throwable caught) {
				displayError( messages.errorRetrievingContactsMessage() );
			}

			@Override
			public void onSuccess(List<Contact> contacts) {
				reportingView.setModel(contacts);
			}
		});

	}
	
	@Override
	public void setState(Place place)
	{
		//Nothing to do:  we're stateless
	}
	
	@Override
	public void onOkButtonClicked() {
		errorView.hide();
	}

	@Override
	public void onGoBack() {
		placeController.goTo(new EditPlace("contacts") );
	}
	
	protected void displayError(String message)
	{
		errorView.setModel(message);
		errorView.show();
	}

	public void setReportingView(ReportingView<Contact> reportingView) {
		this.reportingView = reportingView;
	}


	public void setErrorView(MessagePopUpView errorView) {
		this.errorView = errorView;
	}

	public void setContactsService(ContactsServiceAsync contactsService) {
		this.contactsService = contactsService;
	}

	public void setMessages(Messages messages) {
		this.messages = messages;
	}

	public void setEventBus(EventBus eventBus) {
		this.eventBus = eventBus;
	}
	
	public void setPlaceController(PlaceController placeController) {
		this.placeController = placeController;
	}
}
