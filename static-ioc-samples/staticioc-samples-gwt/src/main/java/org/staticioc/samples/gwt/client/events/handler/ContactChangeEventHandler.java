package org.staticioc.samples.gwt.client.events.handler;

import org.staticioc.samples.gwt.client.events.ContactChangeEvent;

import com.google.gwt.event.shared.EventHandler;

public interface ContactChangeEventHandler extends EventHandler {
    void onContactChanged(ContactChangeEvent issueResolvedEvent);
}