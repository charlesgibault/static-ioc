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
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;

import org.staticioc.samples.gwt.client.Messages;
import org.staticioc.samples.gwt.shared.model.Contact;

public class ContactsReportingViewImpl extends Composite implements ReportingView<Contact>
{
	@UiTemplate("ReportingView.ui.xml")
	interface ContactsReportingViewUiBinder extends UiBinder<Widget, ContactsReportingViewImpl> {}
	private static ContactsReportingViewUiBinder uiBinder = GWT.create(ContactsReportingViewUiBinder.class);

	@UiField(provided = true) CellTable<Contact> contactsTable;

	@UiField Button backButton;
	
	private Presenter<Contact> presenter;
	private Messages messages;

	public ContactsReportingViewImpl()
	{	
		buildCellTable();
		initWidget( uiBinder.createAndBindUi(this));		 
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
	}

	public void afterPropertiesSet()
	{
		// Add  columns to Contact Table
		TextColumn<Contact> emailColumn = new TextColumn<Contact>() {
			@Override
			public String getValue(Contact contact) {
				return contact.getEmail();
			}
		};
		
		contactsTable.addColumn(emailColumn, messages.emailField() );
	}
	
	@UiHandler("backButton")
	void onBackButtonClicked(ClickEvent event) {
		//Navigate to place reporting
		presenter.onGoBack();
	}

	public void setMessages(Messages messages) {
		this.messages = messages;
	}

	@Override
	public Widget asWidget() {
		return this;
	}
}

