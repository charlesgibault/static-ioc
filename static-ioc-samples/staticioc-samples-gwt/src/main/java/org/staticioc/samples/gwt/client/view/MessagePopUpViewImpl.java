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

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.*;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

public class MessagePopUpViewImpl extends DialogBox implements MessagePopUpView {

	@UiTemplate("MessagePopUpView.ui.xml")
	interface MessagePopUpViewUiBinder extends UiBinder<Widget, MessagePopUpViewImpl>  {}
	private static MessagePopUpViewUiBinder uiBinder = GWT.create(MessagePopUpViewUiBinder.class);

	@UiField Button okButton;
	@UiField Label message;
	
	private Presenter presenter;

	public MessagePopUpViewImpl() {
		setWidget( uiBinder.createAndBindUi(this));
	}

	@UiHandler("okButton")
	void onOkButtonClicked(ClickEvent event) {
		if (presenter != null) {
			presenter.onOkButtonClicked();
		}
	}
	
	@Override
	public void setModel(String model) {
		message.setText(model);
	}

	@Override
	public void setPresenter(Presenter presenter) {
		this.presenter = presenter;
	}

	@Override
	public Widget asWidget() {
		return this;
	}
}

