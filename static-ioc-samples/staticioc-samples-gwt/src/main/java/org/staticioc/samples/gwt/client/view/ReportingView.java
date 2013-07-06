package org.staticioc.samples.gwt.client.view;

import com.google.gwt.user.client.ui.Widget;

import java.util.List;

/**
 * Pure view here: no link to Model specificities and Presenter access through interface contract only
 * @author charles
 * @param <T> Type to display in this view
 */
public interface ReportingView<T> {

  public interface Presenter<T> extends org.staticioc.samples.gwt.client.presenter.Presenter {
    void onGoBack();
  }
  
  void setPresenter(Presenter<T> presenter);
  void setModel(List<T> models);

  Widget asWidget();
}
