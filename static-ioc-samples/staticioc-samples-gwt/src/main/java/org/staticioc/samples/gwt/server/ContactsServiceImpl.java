package org.staticioc.samples.gwt.server;

import java.util.List;

import org.staticioc.samples.gwt.client.service.ContactsService;
import org.staticioc.samples.gwt.shared.model.Contact;
import org.staticioc.samples.gwt.shared.validator.FieldVerifier;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

/**
 * The server side implementation of the RPC service.
 */
@SuppressWarnings("serial")
public class ContactsServiceImpl extends RemoteServiceServlet implements
ContactsService
{	
	ApplicationContext applicationContext = new ApplicationContext();
	List<Contact> contacts = applicationContext.defaultContactDatabase.getContacts();
	
	@Override
	public List<Contact> retrieveContacts() {
		return contacts;
	}

	@Override
	public List<Contact> addContact(Contact contact)
	{
		// Verify that the input is valid.
		if (!FieldVerifier.isValidName(contact.getFirstName())) {
			// If the input is not valid, throw an IllegalArgumentException back to
			// the client.
			throw new IllegalArgumentException(
					"First Name must be at least 4 characters long");
		}

		contacts.add(contact);
		return contacts;
	}

	@Override
	public List<Contact> deleteContact(Contact contact) {
		contacts.remove(contact);
		return contacts;
	}
}
