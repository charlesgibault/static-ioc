package org.staticioc.samples.gwt.client.places;

import org.staticioc.samples.gwt.client.presenter.Presenter;

import com.google.gwt.activity.shared.Activity;
import com.google.gwt.activity.shared.ActivityMapper;
import com.google.gwt.place.shared.Place;

public class AppActivityMapper implements ActivityMapper {
	private Presenter reportingActivity;// Note: Presenter interface extends Activity
	private Presenter editionActivity;

	@Override
	public Activity getActivity(Place place) {
		if (place instanceof ReportingPlace)
		{
			reportingActivity.setState( place );
			return reportingActivity;
		}
		else if (place instanceof EditPlace)
		{
			editionActivity.setState( place );
			return editionActivity;
		}
		return null;
	}

	public void setReportingActivity(Presenter reportingActivity) {
		this.reportingActivity = reportingActivity;
	}

	public void setEditionActivity(Presenter editionActivity) {
		this.editionActivity = editionActivity;
	}
}
