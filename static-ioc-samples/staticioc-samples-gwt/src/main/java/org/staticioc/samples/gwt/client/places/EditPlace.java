package org.staticioc.samples.gwt.client.places;

import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceTokenizer;

public class EditPlace extends Place
{
	private String name;

	public EditPlace(String token) {
		this.name = token;
	}

	public String getName() {
		return name;
	}

	public static class Tokenizer implements PlaceTokenizer<EditPlace> {
		@Override
		public String getToken(EditPlace place) {
			return place.getName();
		}

		@Override
		public EditPlace getPlace(String token) {
			return new EditPlace(token);
		}
	}
}
