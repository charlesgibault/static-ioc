package org.staticioc.samples.gwt.client.view;

import com.google.gwt.user.client.ui.Widget;

import java.util.Collection;

/**
 * Pure view here: no link to Model specificities and Presenter access through interface contract only
 * @author charles
 * @param <T> Type to display in this view
 */
public interface EditableListView<T> {

  public interface Presenter<T> extends org.staticioc.samples.gwt.client.presenter.Presenter {
    void onAddButtonClicked(T selectedModel);
    void onDeleteButtonClicked(T selectedModel);
  }
  
  void setPresenter(Presenter<T> presenter);
  void setModel(Collection<T> models);
  void resetUserInput();

  Widget asWidget();
}
