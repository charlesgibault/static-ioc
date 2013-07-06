package org.staticioc.samples.gwt.client.places;

import com.google.gwt.place.shared.PlaceHistoryMapper;
import com.google.gwt.place.shared.WithTokenizers;

@WithTokenizers({ReportingPlace.Tokenizer.class, EditPlace.Tokenizer.class})
public interface AppPlaceHistoryMapper extends PlaceHistoryMapper { }
