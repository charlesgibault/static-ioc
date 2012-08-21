package org.staticioc.model;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

public class CollectionBean extends Bean
{
	private List<Property> properties = new LinkedList<Property>();

	public Collection<Property> getProperties() {
		return properties;
	}

	public CollectionBean( Type type) {
		super();
		setType( type );
		setAnonymous(true);
	}
	
	public CollectionBean(String name, String className, Type type) {
		super(name, className, true);
		setType( type );
	}

	public CollectionBean(String name, String className, boolean anonymous, Type type) {
		super(name, className, anonymous);
		setType( type );
	}
}
