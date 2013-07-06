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
