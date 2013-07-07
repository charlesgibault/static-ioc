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
package org.staticioc.samples.gwt.client.events;

import org.staticioc.samples.gwt.client.events.handler.ContactChangeEventHandler;
import org.staticioc.samples.gwt.shared.model.Contact;

import com.google.gwt.event.shared.GwtEvent;

public class ContactChangeEvent extends GwtEvent<ContactChangeEventHandler>
{
	public enum Action {CREATE, DELETE};

	private final Contact contact;
	private final Action action;
	
	public static Type<ContactChangeEventHandler> TYPE = new Type<ContactChangeEventHandler>();
	
	public ContactChangeEvent( final Contact contact, final Action action)
	{
		this.contact = contact;
		this.action = action;
	}
	
	@Override
	protected void dispatch(ContactChangeEventHandler handler) {
		handler.onContactChanged(this);
		
	}

	@Override
	public com.google.gwt.event.shared.GwtEvent.Type<ContactChangeEventHandler> getAssociatedType() {
		return TYPE;
	}

	public Contact getContact() {
		return contact;
	}

	public Action getAction() {
		return action;
	}
}
