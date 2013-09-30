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

import org.w3c.dom.Node;

public class DefinitionDependency extends Dependency
{	
	private final String alias;
	private final boolean isAnonymous;
	private final Node node;
	private ResolvedBeanCallback resolvedBeanCallback;
	
	public DefinitionDependency(String parentId, String id, String alias, boolean isAnonymous, Node parentNode, ResolvedBeanCallback callback)
	{
		super(id, parentId);
		this.alias = alias;
		this.isAnonymous = isAnonymous;
		this.node = parentNode;
		this.resolvedBeanCallback = callback;
	}
	
	public String getParentId() {
		return getTargetIds().iterator().next();
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

	public ResolvedBeanCallback getResolvedBeanCallback() {
		return resolvedBeanCallback;
	}

	@Override
	public String toString() {
		return "DefinitionDependency [parentId=" + getParentId() + ", id=" + getId() + ", alias=" + alias
				+ ", isAnonymous=" + isAnonymous + ", hasCallback=" +  (resolvedBeanCallback!= null) + "]";
	}
}
