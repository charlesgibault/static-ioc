package org.staticioc.parser.plugins;

import javax.xml.xpath.XPathExpressionException;

import org.staticioc.model.Bean;
import org.staticioc.model.CollectionBean;
import org.staticioc.model.Property;
import org.staticioc.parser.BeanContainer;
import org.staticioc.parser.NodeParserPlugin;
import org.staticioc.parser.ParserConstants;
import org.staticioc.parser.ParserHelper;
import org.w3c.dom.Node;

public class ListPlugin implements NodeParserPlugin, ParserConstants
{
	protected BeanContainer container;

	public String getSupportedNode()
	{
		return LIST;
	}

	@Override
	public Property handleProperty( final Node node, final String propName ) throws XPathExpressionException
	{
		// create an anonymous bean of appropriate collection type
		final String beanId = container.generateAnonymousBeanId();
		final Bean collecBean = new CollectionBean( beanId, Bean.Type.LIST.toString(), Bean.Type.LIST );
		container.handleSubProps( collecBean, "add", node.getChildNodes() ); // recursively set it's property

		// register Bean in Map
		container.register( collecBean  );

		// Wire this bean as a reference
		return ParserHelper.getRef( propName, beanId );
	}

	@Override
	public void setBeanContainer(BeanContainer container)
	{
		this.container = container;
	}
}
