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
	@UiField Button reportingButton;

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
	
	@UiHandler("reportingButton")
	void onReportingButtonClicked(ClickEvent event) {
		//Navigate to place reporting
		presenter.onReportingButtonClicked();
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

	public void afterPropertiesSet()
	{
		// Add  columns to Contact Table
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

		contactsTable.addColumn(firstNameColumn, messages.firstNameField() );
		contactsTable.addColumn(lastNameColumn, messages.lastNameField() );
		contactsTable.addColumn(emailColumn, messages.emailField() );
	}

	public void setMessages(Messages messages) {
		this.messages = messages;
	}

	@Override
	public Widget asWidget() {
		return this;
	}
}

