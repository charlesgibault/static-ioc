package org.staticioc.samples.gwt.client.presenter;

import java.util.List;

import org.staticioc.samples.gwt.client.Messages;
import org.staticioc.samples.gwt.client.service.ContactsServiceAsync;
import org.staticioc.samples.gwt.client.view.EditableListView;
import org.staticioc.samples.gwt.client.view.MessagePopUpView;
import org.staticioc.samples.gwt.shared.model.Contact;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HasWidgets;

public class ContactsPresenterImpl implements EditableListView.Presenter<Contact>, MessagePopUpView.Presenter {

	private EditableListView<Contact> contactsView;
	private MessagePopUpView errorView;
	private ContactsServiceAsync contactsService;
	private Messages messages;

	@Override
	public void display(HasWidgets container) {
		 container.clear();
		 container.add(contactsView.asWidget());
		 fetchContacts();
	}

	@Override
	public void onAddButtonClicked(final Contact contact) {
		// Display add Contact pop-up
		contactsService.addContact(contact, new AsyncCallback<List<Contact>>() {
			public void onFailure(Throwable caught) {
				displayError( messages.errorAddingContactMessage( (contact!=null)?contact.getLastName():"null") );
			}

			@Override
			public void onSuccess(List<Contact> contacts) {
				contactsView.setModel(contacts);
				contactsView.resetUserInput();
				// In a real application, we would probably send an Event there too.
			}
		});
	}

	@Override
	public void onDeleteButtonClicked(final Contact contact) {
		contactsService.deleteContact(contact, new AsyncCallback<List<Contact>>() {
			public void onFailure(Throwable caught) {
				displayError( messages.errorDeletingContactMessage( (contact!=null)?contact.getLastName():"null")  );
			}

			@Override
			public void onSuccess(List<Contact> contacts) {
				contactsView.setModel(contacts);
				// In a real application, we would probably send an Event there too.
			}
		});

	}
	
	public void fetchContacts() {
		contactsService.retrieveContacts( new AsyncCallback<List<Contact>>() {
			public void onFailure(Throwable caught) {
				displayError( messages.errorRetrievingContactsMessage() );
			}

			@Override
			public void onSuccess(List<Contact> contacts) {
				contactsView.setModel(contacts);
			}
		});

	}
	
	@Override
	public void onOkButtonClicked() {
		errorView.hide();
	}
	
	protected void displayError(String message)
	{
		errorView.setModel(message);
		errorView.show();
	}

	public void setContactsView(EditableListView<Contact> contactsView) {
		this.contactsView = contactsView;
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
}
