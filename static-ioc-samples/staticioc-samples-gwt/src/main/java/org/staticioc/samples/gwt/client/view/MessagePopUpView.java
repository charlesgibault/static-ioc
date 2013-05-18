package org.staticioc.samples.gwt.client.view;

import com.google.gwt.user.client.ui.Widget;

/**
 * Pure view here: no link to Model specificities and Presenter access through interface contract only
 * @author charles
 * @param <T> Type to display in this view
 */
public interface MessagePopUpView {

  public interface Presenter extends org.staticioc.samples.gwt.client.presenter.Presenter {
    void onOkButtonClicked();
  }
  
  void setPresenter(Presenter presenter);
  void setModel(String message);

  void show();
  void hide();
  Widget asWidget();
}
