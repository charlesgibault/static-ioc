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

