package org.staticioc.samples.gwt.client.service;

import java.util.List;

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
	List<Contact> retrieveContacts();
  
  /**
   * add a contact 
   * @param contact
   * @return updated contacts listing
   */
  List<Contact> addContact(Contact contact);

  /**
   * Removes a contact 
   * @param contact
   * @return updated contacts listing
   */
  List<Contact> deleteContact(Contact contact);  
}
