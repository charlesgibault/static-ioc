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

		final NamedNodeMap beanAttributes = beanNode.getAttributes();

		// use Id if defined, name otherwise (if defined), auto-generated bean name otherwise.
		final Node idNode = beanAttributes.getNamedItem(ID);
		final Node bNameNode = beanAttributes.getNamedItem(NAME);

		String id = (idNode != null )? idNode.getNodeValue() : (bNameNode != null )? bNameNode.getNodeValue() : null;
		final String alias = ( idNode != null  && bNameNode != null)? bNameNode.getNodeValue(): null;

		boolean isAnonymous = false;
		if( id == null )
		{
			id = beanParser.getBeanContainer().generateAnonymousBeanId(); 
			isAnonymous = true;
		}

		// Check if this node has a parent
		final Node parentNode = beanAttributes.getNamedItem(PARENT);

		Bean bean = null;

		if (parentNode != null )
		{
			final String parentName = parentNode.getNodeValue();

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
		String className = (bean != null)? bean.getClassName() : null;
		boolean abstractBean = false;

		final Node classNode = beanAttributes.getNamedItem(CLASS);
		if ( classNode != null )
		{
			className = classNode.getNodeValue();
		}

		final Node abstractNode = beanAttributes.getNamedItem( ABSTRACT );
		if( abstractNode != null )
		{
			abstractBean = Boolean.valueOf( abstractNode.getNodeValue() );
		}

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
		String factoryBean=(bean != null)? bean.getFactoryBean() : null;
		String factoryMethod=(bean != null)? bean.getFactoryMethod() : null;

		final Node factoryBeanNode = beanAttributes.getNamedItem(FACTORY_BEAN);
		final Node factoryMethodNode = beanAttributes.getNamedItem(FACTORY_METHOD);
		if ( factoryBeanNode != null )
		{
			factoryBean = factoryBeanNode.getNodeValue();
		}
		if ( factoryMethodNode != null )
		{
			factoryMethod = factoryMethodNode.getNodeValue();
		}

		// Handle init/destroy methods here:
		String initMethod=(bean != null)? bean.getInitMethod() : null;
		String destroyMethod=(bean != null)? bean.getDestroyMethod() : null;

		final Node initMethodBeanNode = beanAttributes.getNamedItem(INIT_METHOD);
		final Node destroyMethodNode = beanAttributes.getNamedItem(DESTROY_METHOD);
		if ( initMethodBeanNode != null )
		{
			initMethod = initMethodBeanNode.getNodeValue();
		}
		if ( destroyMethodNode != null )
		{
			destroyMethod = destroyMethodNode.getNodeValue();
		}

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