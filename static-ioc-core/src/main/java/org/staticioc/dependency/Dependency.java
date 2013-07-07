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
package org.staticioc.dependency;

import java.util.LinkedHashSet;
import java.util.Set;

public abstract class Dependency
{
	/// Source bean having the dependency
	private final String id;
	
	/// Set of beans whom the bean id depends on
	private final Set<String> targetIds;
	
	/// boolean determining whether the Dependency is strict or not
	private boolean strict=true;

	protected Dependency(String id, String targetId) {
		this.id = id;
		this.targetIds = new LinkedHashSet<String>();
		this.targetIds.add(targetId);
	}
	
	protected Dependency(String id, Set<String> targetIds) {
		this.id = id;
		this.targetIds = targetIds;
	}
	
	public String getId() {
		return id;
	}

	public Set<String> getTargetIds() {
		return targetIds;
	}

	public boolean isStrict() {
		return strict;
	}

	protected void setStrict(boolean strict) {
		this.strict = strict;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result
				+ ((targetIds == null) ? 0 : targetIds.hashCode());
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
		Dependency other = (Dependency) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (targetIds == null) {
			if (other.targetIds != null)
				return false;
		} else if (!targetIds.equals(other.targetIds))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Dependency [id=" + id + ", targetIds=" + targetIds + "]";
	}
}
