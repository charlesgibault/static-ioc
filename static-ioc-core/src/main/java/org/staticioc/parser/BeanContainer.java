package org.staticioc.parser;

import java.util.Collection;
import java.util.Map;

import javax.xml.xpath.XPathExpressionException;

import org.staticioc.AbstractSpringConfigParser;
import org.staticioc.model.Bean;
import org.staticioc.model.ParentDependency;
import org.staticioc.model.Property;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public interface BeanContainer
{
	/**
	 * Update a collection of properties while registering potential references in the container (for prototype resolution)
	 * @param prop the Property to add
	 * @param properties Collection of existing properties
	 */
	void addOrReplaceProperty(final Property prop, final Collection<Property> properties);
		
	/**
	 * Handle a bean's sub property
	 * @param bean the current bean
	 * @param propName the current property name
	 * @param propChilds then children property that the container must resolve
	 * @throws XPathExpressionException
	 */
	void handleSubProps( final Bean bean, final String propName, final NodeList propChilds) throws XPathExpressionException;

	/**
	 * Handle a <property> sub-node
	 * @param spNode
	 * @param propName
	 * @return
	 * @throws XPathExpressionException
	 */
	Property handleSubProp( final Node subNode, final String propName ) throws XPathExpressionException;
	
	/**
	 * 
	 * @return a GUID for an anonymous bean
	 */
	String generateAnonymousBeanId();
	
	/**
	 * Register a set of beans in the bean map
	 * @param beans
	 */
	void register(final Bean bean);

	/**
	 * Register a set of beans in the bean map
	 * @param beans
	 */
	void register(final Map<String, Bean> beans) ;
	
	/**
	 * Register a bean in the bean map
	 * @param bean to be registered
	 */
	void registerParent( ParentDependency parent );
	
	/**
	 * Register a parser's into this parser
	 * Merge parent dependencies, prototypeBeans and propertyReferencesMap
	 * @param bean to be registered
	 */
	void register( final AbstractSpringConfigParser parser);
}
