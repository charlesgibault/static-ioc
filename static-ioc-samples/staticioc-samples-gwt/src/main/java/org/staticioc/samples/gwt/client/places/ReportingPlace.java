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
