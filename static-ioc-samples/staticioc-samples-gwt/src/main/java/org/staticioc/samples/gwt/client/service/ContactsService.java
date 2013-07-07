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
