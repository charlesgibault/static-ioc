package org.staticioc.samples.gwt.client.service;

import java.util.Collection;

import org.staticioc.samples.gwt.shared.model.Contact;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/**
 * The client side stub for the RPC service.
 */
@RemoteServiceRelativePath("contact")
public interface ContactsService extends RemoteService 
{  
  /**
   * Retrieve all known contacts
   * @return contacts listing
   */
  Collection<Contact> retrieveContacts();
  
  /**
   * add a contact 
   * @param contact
   * @return updated contacts listing
   */
  Collection<Contact> addContact(Contact contact);

  /**
   * Removes a contact 
   * @param contact
   * @return updated contacts listing
   */
  Collection<Contact> deleteContact(Contact contact);  
}
