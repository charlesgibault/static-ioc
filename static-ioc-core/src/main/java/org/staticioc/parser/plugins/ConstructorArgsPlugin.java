package org.staticioc.parser.plugins;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import javax.xml.xpath.XPathExpressionException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.staticioc.dependency.RunTimeDependency;
import org.staticioc.model.Bean;
import org.staticioc.model.Property;
import org.staticioc.parser.BeanParser;
import org.staticioc.parser.NodeParserPlugin;
import org.staticioc.parser.ParserConstants;
import org.staticioc.parser.ParserHelper;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

public class ConstructorArgsPlugin implements NodeParserPlugin, ParserConstants
{
	protected static final Logger logger = LoggerFactory.getLogger(ConstructorArgsPlugin.class);
	
	protected BeanParser container;
	
	/**
	 * Parse the XML <bean/> nodes's children for constructor arguments and enrich the Bean object accordingly
	 * @param bean
	 * @param beanNode
	 * @throws XPathExpressionException 
	 */
	@Override
	public void handleNode( final Bean bean, final Node beanNode ) throws XPathExpressionException
	{
		final Collection<Node> constArgNodes = ParserHelper.extractNodesByName(beanNode.getChildNodes(), CONSTRUCTOR_ARGS);
		
		final Property[] args = new Property[ constArgNodes.size() ];
		
		int autoIndex=0;
		
		for ( Node node : constArgNodes )
		{
			final NamedNodeMap constAttributes = node.getAttributes();
			
			// Retrieve index
			final Node indexNode = constAttributes.getNamedItem(INDEX);
			final String indexValue = ( indexNode != null )? indexNode.getNodeValue() : null;
			
			try
			{
				final int index = ( indexNode != null )? Integer.valueOf( indexValue ):autoIndex++;
				final String argumentPropName = CONSTRUCTOR_ARGS + index;
				
				//handle case where value is set as an attribute 
				Property argumentProp = ParserHelper.handleValueRefAttributes(argumentPropName, node );
				
				if( argumentProp == null) // Argument defined as a sub node
				{
					final Node argumentNode = node.getFirstChild();
					if( argumentNode == null)
					{
						logger.warn("Ignoring constructor argument {} with neither attribute not node value for bean {}", index, bean.getId());
						continue;
					}
				
					argumentProp = container.handleNode( argumentNode, argumentPropName );
				}
				
				if (argumentProp != null)
				{
					args[index] = argumentProp; // collect arguments in disorder (potentially)

					final Node typeNode = constAttributes.getNamedItem(TYPE);
					if( typeNode != null)
					{
						argumentProp.setType( typeNode.getNodeValue() );
					}
				}
			}
			catch(NumberFormatException e)
			{
				logger.warn( "Cannot parse constructor argument index {}", (indexValue!=null)?indexValue:autoIndex );
			}
			catch( IndexOutOfBoundsException e )
			{
				logger.warn( "Out of bound constructor argument index {}. Should be < {}" , (indexValue!=null)?indexValue:autoIndex, constArgNodes.size() );
			}

		}
		
		final Set<String> beanDependencies = new HashSet<String>();
		
		// Now reorder them.
		for( Property prop : args)
		{
			if( prop != null)
			{
				logger.debug("Constructor arg : {}", prop.toString());
				container.addOrReplaceProperty( prop, bean.getConstructorArgs() );
				
				if( prop.getRef() != null )
				{
					beanDependencies.add( prop.getRef() );
				}
			}
		}
		
		if( ! beanDependencies.isEmpty() )
		{
			RunTimeDependency dependency = new RunTimeDependency( bean.getId(), beanDependencies);
			logger.debug( "Adding runtime dependency {}", dependency );
			container.registerRunTimeDependency( dependency );
		}
	}

	@Override
	public void setBeanContainer(BeanParser container)
	{
		this.container = container;
	}
}
