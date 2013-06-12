package org.staticioc.samples.gwt.client.view;

import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.*;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.cellview.client.HasKeyboardSelectionPolicy.KeyboardSelectionPolicy;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.SelectionChangeEvent;
import com.google.gwt.view.client.SingleSelectionModel;

import org.staticioc.samples.gwt.client.Messages;
import org.staticioc.samples.gwt.shared.model.Contact;

public class ContactsViewImpl extends Composite implements EditableListView<Contact>
{
	@UiTemplate("EditableListView.ui.xml")
	interface ContactsViewUiBinder extends UiBinder<Widget, ContactsViewImpl> {}
	private static ContactsViewUiBinder uiBinder = GWT.create(ContactsViewUiBinder.class);

	@UiField TextBox firstNameValue;
	@UiField TextBox lastNameValue;
	@UiField TextBox emailValue;

	@UiField(provided = true) CellTable<Contact> contactsTable;
	@UiField Button addButton;
	@UiField Button deleteButton;

	private Presenter<Contact> presenter;
	private Messages messages;
	private Contact selectedItem;

	public ContactsViewImpl()
	{	
		buildCellTable();
		initWidget( uiBinder.createAndBindUi(this));		 
	}

	@UiHandler("addButton")
	void onAddButtonClicked(ClickEvent event) {
		Contact contact= new Contact(firstNameValue.getValue(), lastNameValue.getValue(), emailValue.getValue());
		presenter.onAddButtonClicked(contact);
	}

	@UiHandler("deleteButton")
	void onDeleteButtonClicked(ClickEvent event) {
		if ( selectedItem != null) {
			presenter.onDeleteButtonClicked(selectedItem);//TODO retrieve selected item
		}
	}

	@Override
	public void resetUserInput() {
		firstNameValue.setValue("");
		lastNameValue.setValue("");
		emailValue.setValue("");
	}

	@Override
	public void setModel(List<Contact> entries) {
		contactsTable.setRowData(entries);
	}

	@Override
	public void setPresenter(Presenter<Contact> presenter) {
		this.presenter = presenter;
	}


	private void buildCellTable()
	{
		contactsTable = new CellTable<Contact>();
		contactsTable.setTableLayoutFixed(false);
		contactsTable.setFocus(false);
		contactsTable.setPageSize(10);

		// Adding columns
		TextColumn<Contact> firstNameColumn = new TextColumn<Contact>() {
			@Override
			public String getValue(Contact contact) {
				return contact.getFirstName();
			}
		};
		TextColumn<Contact> lastNameColumn = new TextColumn<Contact>() {
			@Override
			public String getValue(Contact contact) {
				return contact.getLastName();
			}
		};
		TextColumn<Contact> emailColumn = new TextColumn<Contact>() {
			@Override
			public String getValue(Contact contact) {
				return contact.getEmail();
			}
		};

		contactsTable.addColumn(firstNameColumn, "First Name");
		contactsTable.addColumn(lastNameColumn, "Last Name");
		contactsTable.addColumn(emailColumn, "Email");
		
		// Set-up selected contact selection model
		final SingleSelectionModel<Contact> selectionModel = new SingleSelectionModel<Contact>();
		contactsTable.setKeyboardSelectionPolicy(KeyboardSelectionPolicy.ENABLED);
		contactsTable.setSelectionModel(selectionModel);

		selectionModel.addSelectionChangeHandler(new SelectionChangeEvent.Handler() {
			public void onSelectionChange(SelectionChangeEvent event) {
				Contact selected = selectionModel.getSelectedObject();
				selectedItem=selected;
			}
		});
	}

	// Would go in an afterPropertiesSet() method when mechanism is available in staticioc
	private void translate()
	{
		contactsTable.getColumn(0).setDataStoreName(messages.firstNameField());
		contactsTable.getColumn(1).setDataStoreName(messages.lastNameField());
		contactsTable.getColumn(2).setDataStoreName(messages.emailField());
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

