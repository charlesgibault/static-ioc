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

import com.google.gwt.user.client.ui.Widget;

import java.util.List;

/**
 * Pure view here: no link to Model specificities and Presenter access through interface contract only
 * @author charles
 * @param <T> Type to display in this view
 */
public interface EditableListView<T> {

  public interface Presenter<T> extends org.staticioc.samples.gwt.client.presenter.Presenter {
    void onAddButtonClicked(T selectedModel);
    void onDeleteButtonClicked(T selectedModel);
    void onReportingButtonClicked();
  }
  
  void setPresenter(Presenter<T> presenter);
  void setModel(List<T> models);
  void resetUserInput();

  Widget asWidget();
}
