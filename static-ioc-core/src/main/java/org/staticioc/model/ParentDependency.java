/**
 *  Copyright (C) 2012 Charles Gibault
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
package org.staticioc.model;

import org.w3c.dom.Node;

public class ParentDependency
{
	private final String parentId;
	private final String id;
	private final String alias;
	private final boolean isAnonymous;
	private final Node node;
	
	public ParentDependency(String parentId, String id, String alias,
			boolean isAnonymous, Node parentNode) {
		super();
		this.parentId = parentId;
		this.id = id;
		this.alias = alias;
		this.isAnonymous = isAnonymous;
		this.node = parentNode;
	}
	
	public String getParentId() {
		return parentId;
	}
	public String getId() {
		return id;
	}
	public boolean isAnonymous() {
		return isAnonymous;
	}
	public Node getNode() {
		return node;
	}
	public String getAlias() {
		return alias;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
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
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "ParentDependency [parentId=" + parentId + ", id=" + id + ", alias=" + alias
				+ ", isAnonymous=" + isAnonymous + "]";
	}
}
