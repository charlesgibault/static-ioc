package org.staticioc.samples.gwt.client.view;

import java.util.LinkedList;
import java.util.Collection;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.*;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import org.staticioc.samples.gwt.client.Messages;
import org.staticioc.samples.gwt.shared.model.Contact;

public class ContactsViewImpl extends Composite implements EditableListView<Contact>
{
	@UiTemplate("EditableListView.ui.xml")
	interface ContactsViewUiBinder extends UiBinder<Widget, ContactsViewImpl> {}
	private static ContactsViewUiBinder uiBinder = GWT.create(ContactsViewUiBinder.class);

	@UiField Label firstName; 
	@UiField Label lastName;
	@UiField Label email;
	
	@UiField TextBox firstNameValue;
	@UiField TextBox lastNameValue;
	@UiField TextBox emailValue;
	
	@UiField FlexTable contactsTable;
	@UiField Button addButton;
	@UiField Button deleteButton;

	private Presenter<Contact> presenter;
	private Messages messages;
	private Collection<Contact> contacts = new LinkedList<Contact>();

	public ContactsViewImpl() {
		initWidget( uiBinder.createAndBindUi(this));
	}

	private void translate()
	{
		firstName.setText(messages.firstNameField());
		lastName.setText(messages.lastNameField());
		email.setText(messages.emailField());
		addButton.setText(messages.addButton());
		deleteButton.setText(messages.removeButton());
	}
	
	@UiHandler("addButton")
	void onAddButtonClicked(ClickEvent event) {
		if (presenter != null) {
			Contact contact= new Contact(firstNameValue.getValue(), lastNameValue.getValue(), emailValue.getValue());
			presenter.onAddButtonClicked(contact);
		}
	}

	@UiHandler("deleteButton")
	void onDeleteButtonClicked(ClickEvent event) {
		if (presenter != null) {
			presenter.onDeleteButtonClicked(null);//TODO retrieve selected item
		}
	}
	
	@Override
	public void resetUserInput() {
		firstNameValue.setValue("");
		lastNameValue.setValue("");
		emailValue.setValue("");
	}

	@Override
	public void setModel(Collection<Contact> entries) {
		this.contacts = entries;

		for( Contact contact : contacts)
		{
			// TODO Display contacts			
		}
	}

	@Override
	public void setPresenter(Presenter<Contact> presenter) {
		this.presenter = presenter;
	}

	public Messages getMessages() {
		return messages;
	}

	public void setMessages(Messages messages) {
		this.messages = messages;
		translate();
	}

	@Override
	public Widget asWidget() {
		return this;
	}
}

