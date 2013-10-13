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
package org.staticioc.parser.namespace.spring.beans;

import javax.xml.xpath.XPathExpressionException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.staticioc.model.Property;
import org.staticioc.parser.AbstractNodeSupportPlugin;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

public class AliasPlugin extends AbstractNodeSupportPlugin
{
	protected static final Logger logger = LoggerFactory.getLogger(AliasPlugin.class);
			
	@Override
	public String getUnprefixedSupportedNode()
	{
		return ALIAS;
	}

	@Override
	public Property handleNode( final Node node, final String propName ) throws XPathExpressionException
	{
		final NamedNodeMap spNodeAttributes = node.getAttributes();
		final Node aliasNode = spNodeAttributes.getNamedItem(ALIAS);
		final Node nameNode = spNodeAttributes.getNamedItem(NAME);

		if( aliasNode == null || nameNode == null) 
		{
			logger.warn("Missing alias or name argument for <alias/> node {}", node );
			return null;
		}
		
		beanParser.getBeanContainer().registerAlias( nameNode.getNodeValue(), aliasNode.getNodeValue() );
		
		return null;
	}
}
