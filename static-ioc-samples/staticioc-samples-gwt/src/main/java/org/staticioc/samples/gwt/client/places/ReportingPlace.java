package org.staticioc.samples.gwt.client.places;

import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceTokenizer;

public class ReportingPlace extends Place
{
	private String name;

	public ReportingPlace(String token) {
		this.name = token;
	}

	public String getName() {
		return name;
	}

	public static class Tokenizer implements PlaceTokenizer<ReportingPlace> {
		@Override
		public String getToken(ReportingPlace place) {
			return place.getName();
		}

		@Override
		public ReportingPlace getPlace(String token) {
			return new ReportingPlace(token);
		}
	}

}
