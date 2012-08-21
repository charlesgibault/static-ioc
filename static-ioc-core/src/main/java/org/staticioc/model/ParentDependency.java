package org.staticioc.model;

import org.w3c.dom.Node;

public class ParentDependency
{
	private final String parentName;
	private final String name;
	private final boolean isAnonymous;
	private final Node node;
	
	public ParentDependency(String parentName, String name,
			boolean isAnonymous, Node parentNode) {
		super();
		this.parentName = parentName;
		this.name = name;
		this.isAnonymous = isAnonymous;
		this.node = parentNode;
	}
	
	public String getParentName() {
		return parentName;
	}
	public String getName() {
		return name;
	}
	public boolean isAnonymous() {
		return isAnonymous;
	}
	public Node getNode() {
		return node;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ParentDependency other = (ParentDependency) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "ParentDependency [parentName=" + parentName + ", name=" + name
				+ ", isAnonymous=" + isAnonymous + "]";
	}
}
