package org.staticioc.samples.gwt.server;

import java.util.List;

import org.staticioc.samples.gwt.shared.model.Contact;

public class ContactsDatabase
{
	private List<Contact> contacts;

	public List<Contact> getContacts() {
		return contacts;
	}

	public void setContacts(List<Contact> contacts) {
		this.contacts = contacts;
	}
}
