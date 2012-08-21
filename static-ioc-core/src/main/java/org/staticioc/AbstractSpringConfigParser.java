package org.staticioc;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import javax.xml.xpath.XPathExpressionException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.staticioc.model.Bean;
import org.staticioc.model.ParentDependency;
import org.staticioc.model.Property;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public abstract class AbstractSpringConfigParser {

	protected static final Logger logger = LoggerFactory.getLogger(SpringConfigParser.class);
	protected static final String BEAN_PROPERTY_PREFIX = "p:";
	protected static final String BEAN_PROPERTY_REF_SUFFIX = "-ref";
	protected static final String XPATH_BEAN = "/beans/bean";
	protected static final String XPATH_PROPERTY = "property[@name]";
	protected static final String XPATH_IMPORT = "/beans/import[@resource]";
	protected static final String BEAN = "bean";
	protected static final String REF = "ref";
	protected static final String ID = "id";
	protected static final String IDREF = "idref";
	protected static final String CLASS = "class";
	protected static final String NAME = "name";
	protected static final String VALUE = "value";
	protected static final String TYPE = "type";
	protected static final String ABSTRACT = "abstract";
	protected static final String PARENT = "parent";
	protected static final String SCOPE = "scope";
	protected static final String MAP = "map";
	protected static final String LIST = "list";
	protected static final String SET = "set";
	protected static final String PROPS = "props";
	protected static final String ENTRY = "entry";
	protected static final String KEY = "key";
	protected static final String KEY_REF = "key-ref";
	protected static final String VALUE_REF = "value-ref";
	protected static final String PROP = "prop";
	public static final String CONSTRUCTOR_ARGS = "constructor-arg";
	protected static final String INDEX = "index";
	protected static final String NULL = "null";
	protected static final String RESOURCE = "resource";
	
	public static final String ANONYMOUS_BEAN_PREFIX = "bean_";
	private static int anonymousBeanIdentifier = 0;

	private final Map<String,Bean> beanClassMap = new ConcurrentHashMap<String,Bean>();
	private final Map<String, ParentDependency > parentDependencyMap = new ConcurrentHashMap<String, ParentDependency>();

	/**
	 * 
	 * @return
	 */
	protected String generateAnonymousBeanId() {
		return ANONYMOUS_BEAN_PREFIX + (++anonymousBeanIdentifier);
	}

	protected void addOrReplaceProperty(final Property prop, final Collection<Property> set) {
		if( ! set.add( prop ) )
		{
			set.remove(prop);
			set.add(prop);
		}
	}

	/**
	 * Register a bean in the bean map
	 * @param bean to be registered
	 */
	protected void register(final Bean bean) {	
		if( logger.isDebugEnabled())
		{
			logger.debug( "Adding {}", bean );
		}
		
		beanClassMap.put( bean.getName(), bean);
	}

	/**
	 * Register a set of beans in the bean map
	 * @param beans
	 */
	protected void register(final Map<String, Bean> beans) {	
		if( logger.isDebugEnabled())
		{
			logger.debug( "Adding {}", beans );
		}
		
		beanClassMap.putAll(beans);
	}
	
	/**
	 * Register a bean in the bean map
	 * @param bean to be registered
	 */
	protected void registerParent( ParentDependency parent ) {	
		if( logger.isDebugEnabled())
		{
			logger.debug( "Adding {}", parent );
		}
		
		parentDependencyMap.put( parent.getName(), parent );
	}
	
	/**
	 * Register a bean in the bean map
	 * @param bean to be registered
	 */
	protected void registerParents( final AbstractSpringConfigParser parser) {	
		if( logger.isDebugEnabled())
		{
			logger.debug( "Adding {}", parser.parentDependencyMap );
		}
		
		parentDependencyMap.putAll( parser.parentDependencyMap );
	}
	
	/**
	 * Resolve a bean by Name
	 * @param name
	 * @return
	 */
	protected Bean getBeanByName( final String name)
	{
		return beanClassMap.get(name);
	}
	
	protected Map<String, Bean > getBeans()
	{
		return beanClassMap;
	}

	protected Property getVal(String propertyName, String value) {
		if ( value == null) { return null; }
		return new Property( propertyName, value, null );
	}

	protected Property getRef(String propertyName, String ref) {
		if ( ref == null) { return null; }
		return new Property( propertyName, null, ref );
	}

	/**
	 * Extract text value for nodes like <value>text</value>
	 * @param node
	 * @return
	 */
	protected String extractFirstChildValue(final Node node) {
		final Node valueSubNode = node.getFirstChild();
		
		if ( valueSubNode == null ) { return null; }
		return valueSubNode.getNodeValue();
	}

	/**
	 * Go through a node list a extract the first matching node given its name
	 * @param nodes
	 * @param name
	 * @return
	 */
	protected Node extractFirstNodeByName(final NodeList nodes, final String name) {
		if(nodes != null && name != null )
		{	
			for( int n = 0 ; n<nodes.getLength() ; ++n )
			{
				final Node node = nodes.item( n );
	
				if ( name.equals(node.getNodeName()) )
				{
					return node;
				}
			}
		}
		
		return null;
	}

	/**
	 * Go through a node list a extract the first matching node given its name
	 * @param nodes
	 * @param name
	 * @return
	 */
	protected Collection<Node> extractNodesByName(final NodeList nodes, final String name) {
		Collection<Node> result = new LinkedList<Node>();
		
		if(nodes != null && name != null )
		{	
			for( int n = 0 ; n<nodes.getLength() ; ++n )
			{
				final Node node = nodes.item( n );
	
				if ( name.equals(node.getNodeName()) )
				{
					result.add( node );
				}
			}
		}
		
		return result;
	}
	
	protected void resolveParentDefinition() throws XPathExpressionException
	{
		final Set<String> visitedParents = new HashSet<String>();
		
		for( String parentName : parentDependencyMap.keySet() )
		{
			resolveParentBean( parentName, visitedParents );
		}
	}
	
	/**
	 * Resolve parent bean dependencies using a depth first search (Cormen et al. / Tarjan)
	 * 
	 * @param parentName
	 * @throws XPathExpressionException 
	 */
	protected void resolveParentBean( final String name, final Set<String> visitedBeans ) throws XPathExpressionException
	{
		// Grab dependency
		final ParentDependency dependency = parentDependencyMap.get(name);
		
		if( dependency == null )
		{
			logger.warn( "Unresolved parent bean {}. Ignoring", name );
			return;
		}
		
		// Test direct resolution (parent already registered)
		Bean parentBean = getBeanByName( dependency.getParentName() );
		
		if( parentBean == null) // parent cannot be resolved directly -> Do a depth first approach
		{
			// Maintain a set of visited parent to avoid infinite loops in cycles.
			// This works because our depth first approach visits each node only once O(N) complexity.
			visitedBeans.add( name );
			
			if ( !visitedBeans.contains( dependency.getParentName() ))
			{
				resolveParentBean( dependency.getParentName(), visitedBeans );
				parentBean = getBeanByName( dependency.getParentName() );
				
				if( parentBean == null ) // Parent couldn't be resolved still (due to cycle)
				{
					logger.warn( "Unresolved parent bean {}. Ignoring", name );
					return;
				}
			}
			else
			{
			
				logger.warn( "Cycle detected with bean {}", name );
				return;
			}
		}
		
		// Perform bean copy and continue
		final Bean bean = new Bean( dependency.getName(), parentBean, dependency.isAnonymous() );
		final Node beanNode = dependency.getNode();
		final NamedNodeMap beanAttributes = beanNode.getAttributes();
		
		processBeanNode(beanNode, beanAttributes, bean.getName(), dependency.isAnonymous(), bean);			
	}
	
	abstract protected String processBeanNode(final Node beanNode, final NamedNodeMap beanAttributes, final String id, boolean isAnonymous, Bean bean) throws XPathExpressionException;
}