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
