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

import org.staticioc.container.BeanContainer;
import org.staticioc.dependency.DefinitionDependency;
import org.staticioc.dependency.FactoryBeanDependency;
import org.staticioc.dependency.ResolvedBeanCallback;
import org.staticioc.dependency.RunTimeDependency;
import org.staticioc.model.Bean;
import org.staticioc.model.Property;
import org.staticioc.model.Bean.Scope;
import org.staticioc.parser.AbstractNodeSupportPlugin;
import org.staticioc.parser.NodeParserPlugin;
import org.staticioc.parser.ParserHelper;

import org.apache.commons.lang3.tuple.Pair;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

public class BeanPlugin extends AbstractNodeSupportPlugin  implements ResolvedBeanCallback
{
	protected static final Logger logger = LoggerFactory.getLogger(BeanPlugin.class);
		
	@Override
	public String getUnprefixedSupportedNode()
	{
		return BEAN;
	}

	@Override
	public Property handleNode(Node node, String propName) throws XPathExpressionException {

		// recursively create bean
		final String subBeanName = createBean( node );

		if (subBeanName != null) // Wire this bean as a reference
		{
			return ParserHelper.getRef( propName, subBeanName );
		}
		
		return null;
	}

	/**
	 * Parse a <bean/> node, read id/name/alias attributes to define its id, then load other attributes and return its id
	 * 
	 * @param beanNode XML <bean/> node to process
	 * @return the id of the created Bean
	 * @throws XPathExpressionException
	 */
	protected String createBean( final Node beanNode ) throws XPathExpressionException
	{
		if (beanNode == null) // if Bean doesn't exist, there's nothing to do
		{
			return null;
		}

		Pair<String, String> ids = ParserHelper.extractBeanId(beanNode);

		String id = ids.getLeft();
		final String alias = ids.getRight();

		boolean isAnonymous = false;
		if( id == null )
		{
			id = beanParser.getBeanContainer().generateAnonymousBeanId(); 
			isAnonymous = true;
		}

		// Check if this node has a parent
		final NamedNodeMap beanAttributes = beanNode.getAttributes();

		Bean bean = null;
		final String parentName = ParserHelper.extractAttributeValueAsString(PARENT, beanAttributes, null);

		if (parentName != null )
		{
			// Test direct resolution (parent already registered)
			final Bean parentBean = beanParser.getBeanContainer().getBean( parentName );

			if( parentBean == null) // parent not known yet
			{
				beanParser.getBeanContainer().registerParent( new DefinitionDependency( parentName, id, alias, isAnonymous,  beanNode, this) );
				return id;
			}
			else
			{
				// Perform bean copy and continues
				bean = beanParser.getBeanContainer().duplicateBean( id, alias, parentBean, isAnonymous );
			}
		}

		// Perform actual node content processing
		return processBeanNodeAttributes(beanNode, beanAttributes, id, alias, isAnonymous, bean, beanParser.getBeanContainer());
	}
	
	/**
	 * Process a <bean/> node's attributes (all but id attribute).
	 * 
	 * @param beanNode
	 * @param beanAttributes
	 * @param id
	 * @param alias
	 * @param isAnonymous
	 * @param bean
	 * @param container the BeanContainer to use
	 * @return the id of the processed Bean
	 * @throws XPathExpressionException
	 */
	protected String processBeanNodeAttributes(final Node beanNode, final NamedNodeMap beanAttributes, final String id, final String alias, boolean isAnonymous, Bean bean, BeanContainer container) throws XPathExpressionException
	{
		String className = ParserHelper.extractAttributeValueAsString(CLASS, beanAttributes, (bean != null)? bean.getClassName() : null);
		boolean abstractBean = ParserHelper.extractAttributeValueAsBoolean( ABSTRACT, beanAttributes, Boolean.FALSE );


		final Node scopeNode = beanAttributes.getNamedItem(SCOPE);
		final Node singletonNode = beanAttributes.getNamedItem(SINGLETON);

		Scope scope = Scope.SINGLETON;
		if( scopeNode != null && PROTOTYPE.equalsIgnoreCase( scopeNode.getNodeValue() ) 
				|| singletonNode != null && !Boolean.valueOf( singletonNode.getNodeValue() ) )
		{
			scope = Scope.PROTOTYPE;
		}

		logger.debug( "id {} scope {} ", id, scope );

		//Handle Factory Bean/Method here
		String factoryBean= ParserHelper.extractAttributeValueAsString(FACTORY_BEAN, beanAttributes, (bean != null)? bean.getFactoryBean() : null );
		String factoryMethod= ParserHelper.extractAttributeValueAsString(FACTORY_METHOD, beanAttributes, (bean != null)? bean.getFactoryMethod() : null );

		// Handle init/destroy methods here:
		String initMethod= ParserHelper.extractAttributeValueAsString(INIT_METHOD, beanAttributes, (bean != null)? bean.getInitMethod() : null );
		String destroyMethod= ParserHelper.extractAttributeValueAsString(DESTROY_METHOD, beanAttributes, (bean != null)? bean.getDestroyMethod() : null );

		// Class is mandatory for non abstract beans
		if( className == null && !abstractBean)
		{
			logger.warn( "No class defined for bean {}. Skipping", id );
			return null;
		}

		if( bean == null )
		{
			bean = new Bean(id, className, isAnonymous );
			bean.setAlias(alias);
		}
		else
		{
			bean.setClassName(className); // possible override of the parent's class
			bean.setAlias(alias);
		}

		bean.setAbstract( abstractBean );
		bean.setScope( scope );

		bean.setFactoryBean(factoryBean);
		bean.setFactoryMethod(factoryMethod);

		if( factoryBean != null)
		{
			final RunTimeDependency factoryBeanDependency = new FactoryBeanDependency( id, factoryBean); // Factory-bean can refer to framework classes and thus must be "soft" 
			container.registerRunTimeDependency( factoryBeanDependency );
		}

		bean.setInitMethod(initMethod);
		bean.setDestroyMethod(destroyMethod);

		
		// Specific attributes plugin
		for( NodeParserPlugin plugin: beanParser.getNodeParserPlugins() )
		{
			plugin.handleNode(bean, beanNode);
		}

		// register Bean in Map
		container.register( bean );

		return id;
	}

	/**
	 * Important: do not use the beanParser.getContainer() here as the points at the initial BeanContainer used when processing the <bean/> node in the first place
	 * but the container passed in parameter as this is the aggregated/up-to-date (in case of <import/> directive) BeanContainer.
	 */
	@Override
	public String onResolve(Bean bean, Node beanNode, NamedNodeMap beanAttributes, boolean isAnonymous, BeanContainer container) throws XPathExpressionException
	{
		logger.debug("Resolved Bean {}", bean.getId() );
		return processBeanNodeAttributes( beanNode, beanAttributes, bean.getId(), bean.getAlias(), isAnonymous, bean, container );
	}
}
