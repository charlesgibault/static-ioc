package org.staticioc.samples.gwt.client.presenter;

import com.google.gwt.activity.shared.Activity;
import com.google.gwt.place.shared.Place;
import com.google.gwt.user.client.ui.AcceptsOneWidget;

public interface Presenter extends Activity
{
  void display(final AcceptsOneWidget container);
  void setState(Place place);
}
