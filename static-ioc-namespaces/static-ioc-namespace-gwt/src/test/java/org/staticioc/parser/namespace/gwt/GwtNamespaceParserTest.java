package org.staticioc.parser.namespace.gwt;
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


import java.util.Iterator;

import org.junit.Assert;
import org.junit.Test;
import static org.junit.Assert.*;

import org.staticioc.AbstractTestSpringParser;
import org.staticioc.model.*;
import org.staticioc.parser.ParserConstants;

/**
 * Unit test for SpringConfigParser
 */
public class GwtNamespaceParserTest extends AbstractTestSpringParser
{
	private static final String GWT = "com.google.gwt.core.client.GWT";
	private final static String TEST_CONTEXT = "src/test/resources/gwtContext-test.xml";

	public GwtNamespaceParserTest() throws Exception 
	{	
		this( TEST_CONTEXT );
	}
	
	protected  GwtNamespaceParserTest(String context) throws Exception 
	{	
		super( context, new GwtNamespaceParser() );
	}

	/**
	 * Test <gwt:message nodes/> nodes parsing
	 */
	@Test
	public void testMessages()
	{
		// Retrieve personBean and test properties
		Bean messages = loadedBeans.get("messages");
		Bean messagesAdmin = loadedBeans.get("messagesAdmin");

		Assert.assertNotNull( messages );
		Assert.assertNotNull( messagesAdmin );
		
		// Bean definition checks
		assertEquals("Bean id not properly set", "messages", messages.getId() );
		assertEquals("Bean id not properly set", "messagesAdmin", messagesAdmin.getId() );
		
		assertEquals("Bean class not properly set", "org.staticioc.samples.gwt.client.Messages", messages.getClassName() );
		assertEquals("Bean class not properly set", "org.staticioc.samples.gwt.client.AdminMessages", messagesAdmin.getClassName() );
		
		assertEquals("Bean type not properly set", Bean.Type.SIMPLE , messages.getType() );
		assertEquals("Bean type not properly set", Bean.Type.SIMPLE , messagesAdmin.getType() );
		
		assertFalse("Named bean wrongly considered as anonymous" , messages.isAnonymous() );
		assertFalse("Named bean wrongly considered as anonymous" , messagesAdmin.isAnonymous() );
		
		assertFalse("Real bean wrongly considered as abstract" , messages.isAbstract() );
		assertFalse("Real bean wrongly considered as abstract" , messagesAdmin.isAbstract() );
		
		//Factory-Bean on GWT.create()
		assertEquals("Factory bean not properly set", GWT , messages.getFactoryBean() );
		assertEquals("Factory bean not properly set", GWT , messagesAdmin.getFactoryBean() );
		
		assertEquals("Factory method not properly set", "create" , messages.getFactoryMethod() );		
		assertEquals("Factory method not properly set", "create" , messagesAdmin.getFactoryMethod() );		
		
		assertEquals("Constructor args not found were expected", messages.getConstructorArgs().size(), 1 );
		assertEquals("Constructor args not found were expected", messagesAdmin.getConstructorArgs().size(), 1 );

		Property constructorArgs = messages.getConstructorArgs().iterator().next();
		checkProperty( constructorArgs, ParserConstants.CONSTRUCTOR_ARGS + "0", "org.staticioc.samples.gwt.client.Messages.class", null);
		
		constructorArgs = messagesAdmin.getConstructorArgs().iterator().next();
		checkProperty( constructorArgs, ParserConstants.CONSTRUCTOR_ARGS + "0", "org.staticioc.samples.gwt.client.AdminMessages.class", null);
		
		assertTrue("Propertes found were none expected", messages.getProperties().isEmpty() );
		assertTrue("Propertes found were none expected", messagesAdmin.getProperties().isEmpty() );
		
		// Check for mis-formatted nodes
		Assert.assertNull( loadedBeans.get("missingClassMessage") );
	}

	/**
	 * Test <gwt:service/> nodes parsing
	 */
	@Test
	public void testService()
	{
		// Retrieve personBean and test properties
		Bean contactsService = loadedBeans.get("contactsService");

		Assert.assertNotNull( contactsService );

		// Bean definition checks
		assertEquals("Bean id not properly set", "contactsService", contactsService.getId() );
		assertEquals("Bean class not properly set", "org.staticioc.samples.gwt.client.service.ContactsServiceAsync", contactsService.getClassName() );
		assertEquals("Bean type not properly set", Bean.Type.SIMPLE , contactsService.getType() );
		assertFalse("Named bean wrongly considered as anonymous" , contactsService.isAnonymous() );
		assertFalse("Real bean wrongly considered as abstract" , contactsService.isAbstract() );

		//Factory-Bean on GWT.create()
		assertEquals("Factory bean not properly set", GWT , contactsService.getFactoryBean() );
		assertEquals("Factory method not properly set", "create" , contactsService.getFactoryMethod() );		
		assertEquals("Constructor args not found were expected", contactsService.getConstructorArgs().size(), 1 );

		Property constructorArgs = contactsService.getConstructorArgs().iterator().next();
		checkProperty( constructorArgs, ParserConstants.CONSTRUCTOR_ARGS + "0", "org.staticioc.samples.gwt.client.service.ContactsService.class", null);

		assertTrue("Propertes found were none expected", contactsService.getProperties().isEmpty() );
		
		// Check for mis-formatted nodes
		Assert.assertNull( loadedBeans.get("missingInterfaceCService") );
	}

	/**
	 * Test <gwt:eventBus/> nodes parsing
	 */
	@Test
	public void testEventBus()
	{
		// Retrieve personBean and test properties
		Bean eventBus = loadedBeans.get("eventBus");
		Bean legacyEventBus = loadedBeans.get("legacyEventBus");

		Assert.assertNotNull( eventBus );
		Assert.assertNotNull( legacyEventBus );

		// Bean definition checks
		assertEquals("Bean id not properly set", "eventBus", eventBus.getId() );
		assertEquals("Bean id not properly set", "legacyEventBus", legacyEventBus.getId() );

		assertEquals("Bean class not properly set", "com.google.web.bindery.event.shared.EventBus", eventBus.getClassName() );
		assertEquals("Bean class not properly set", "com.google.gwt.event.shared.EventBus", legacyEventBus.getClassName() );

		assertEquals("Bean type not properly set", Bean.Type.SIMPLE , eventBus.getType() );
		assertEquals("Bean type not properly set", Bean.Type.SIMPLE , legacyEventBus.getType() );

		assertFalse("Named bean wrongly considered as anonymous" , eventBus.isAnonymous() );
		assertFalse("Named bean wrongly considered as anonymous" , legacyEventBus.isAnonymous() );

		assertFalse("Real bean wrongly considered as abstract" , eventBus.isAbstract() );
		assertFalse("Real bean wrongly considered as abstract" , legacyEventBus.isAbstract() );

		//Factory-Bean on GWT.create()
		assertEquals("Factory bean not properly set", GWT , eventBus.getFactoryBean() );
		assertEquals("Factory bean not properly set", GWT , legacyEventBus.getFactoryBean() );

		assertEquals("Factory method not properly set", "create" , eventBus.getFactoryMethod() );		
		assertEquals("Factory method not properly set", "create" , legacyEventBus.getFactoryMethod() );		

		assertEquals("Constructor args not found were expected", eventBus.getConstructorArgs().size(), 1 );
		assertEquals("Constructor args not found were expected", legacyEventBus.getConstructorArgs().size(), 1 );

		Property constructorArgs = eventBus.getConstructorArgs().iterator().next();
		checkProperty( constructorArgs, ParserConstants.CONSTRUCTOR_ARGS + "0", "com.google.gwt.event.shared.SimpleEventBus.class", null);

		constructorArgs = legacyEventBus.getConstructorArgs().iterator().next();
		checkProperty( constructorArgs, ParserConstants.CONSTRUCTOR_ARGS + "0", "com.google.gwt.event.shared.testing.CountingEventBus.class", null);

		assertTrue("Propertes found were none expected", eventBus.getProperties().isEmpty() );
		assertTrue("Propertes found were none expected", legacyEventBus.getProperties().isEmpty() );
	}

	/**
	 * Test <gwt:historyMapper/> nodes parsing
	 */
	@Test
	public void testHistoryMapper()
	{
		// Retrieve personBean and test properties
		Bean historyMapper = loadedBeans.get("historyMapper");
		Bean anotherHistoryMapper = loadedBeans.get("anotherHistoryMapper");

		Assert.assertNotNull( historyMapper );
		Assert.assertNotNull( anotherHistoryMapper );

		// Bean definition checks
		assertEquals("Bean id not properly set", "historyMapper", historyMapper.getId() );
		assertEquals("Bean id not properly set", "anotherHistoryMapper", anotherHistoryMapper.getId() );

		assertEquals("Bean class not properly set", "org.staticioc.samples.gwt.client.places.AppPlaceHistoryMapper", historyMapper.getClassName() );
		assertEquals("Bean class not properly set", "org.staticioc.samples.gwt.client.places.AnotherAppPlaceHistoryMapper", anotherHistoryMapper.getClassName() );

		assertEquals("Bean type not properly set", Bean.Type.SIMPLE , historyMapper.getType() );
		assertEquals("Bean type not properly set", Bean.Type.SIMPLE , anotherHistoryMapper.getType() );

		assertFalse("Named bean wrongly considered as anonymous" , historyMapper.isAnonymous() );
		assertFalse("Named bean wrongly considered as anonymous" , anotherHistoryMapper.isAnonymous() );

		assertFalse("Real bean wrongly considered as abstract" , historyMapper.isAbstract() );
		assertFalse("Real bean wrongly considered as abstract" , anotherHistoryMapper.isAbstract() );

		//Factory-Bean on GWT.create()
		assertEquals("Factory bean not properly set", GWT , historyMapper.getFactoryBean() );
		assertEquals("Factory bean not properly set", GWT , anotherHistoryMapper.getFactoryBean() );

		assertEquals("Factory method not properly set", "create" , historyMapper.getFactoryMethod() );		
		assertEquals("Factory method not properly set", "create" , anotherHistoryMapper.getFactoryMethod() );		

		assertEquals("Constructor args not found were expected", historyMapper.getConstructorArgs().size(), 1 );
		assertEquals("Constructor args not found were expected", anotherHistoryMapper.getConstructorArgs().size(), 1 );

		Property constructorArgs = historyMapper.getConstructorArgs().iterator().next();
		checkProperty( constructorArgs, ParserConstants.CONSTRUCTOR_ARGS + "0", "org.staticioc.samples.gwt.client.places.AppPlaceHistoryMapper.class", null);

		constructorArgs = anotherHistoryMapper.getConstructorArgs().iterator().next();
		checkProperty( constructorArgs, ParserConstants.CONSTRUCTOR_ARGS + "0", "org.staticioc.samples.gwt.client.places.AnotherAppPlaceHistoryMapper.class", null);

		assertTrue("Propertes found were none expected", historyMapper.getProperties().isEmpty() );
		assertTrue("Propertes found were none expected", anotherHistoryMapper.getProperties().isEmpty() );
		
		// Check for mis-formatted nodes
		Assert.assertNull( loadedBeans.get("missingClassHistoryMapper") );
	}
	
	/**
	 * Test <gwt:activityManager/> nodes parsing
	 */
	@Test
	public void testActivityManager()
	{
		// Retrieve personBean and test properties
		Bean activityManager = loadedBeans.get("activityManager");
		Bean anotherActivityManager = loadedBeans.get("anotherActivityManager");

		Assert.assertNotNull( activityManager );
		Assert.assertNotNull( anotherActivityManager );

		// Bean definition checks
		assertEquals("Bean id not properly set", "activityManager", activityManager.getId() );
		assertEquals("Bean id not properly set", "anotherActivityManager", anotherActivityManager.getId() );

		assertEquals("Bean class not properly set", "com.google.gwt.activity.shared.ActivityManager", activityManager.getClassName() );
		assertEquals("Bean class not properly set", "com.google.gwt.activity.shared.ActivityManager", anotherActivityManager.getClassName() );

		assertEquals("Bean type not properly set", Bean.Type.SIMPLE , activityManager.getType() );
		assertEquals("Bean type not properly set", Bean.Type.SIMPLE , anotherActivityManager.getType() );

		assertFalse("Named bean wrongly considered as anonymous" , activityManager.isAnonymous() );
		assertFalse("Named bean wrongly considered as anonymous" , anotherActivityManager.isAnonymous() );

		assertFalse("Real bean wrongly considered as abstract" , activityManager.isAbstract() );
		assertFalse("Real bean wrongly considered as abstract" , anotherActivityManager.isAbstract() );

		assertEquals("Constructor args not found were expected", activityManager.getConstructorArgs().size(), 2 );
		assertEquals("Constructor args not found were expected", anotherActivityManager.getConstructorArgs().size(), 2 );

		Iterator<Property> it =  activityManager.getConstructorArgs().iterator();
		Property constructorArgs = it.next();
		checkProperty( constructorArgs, ParserConstants.CONSTRUCTOR_ARGS + "0", null, "activityMapper");
		
		constructorArgs = it.next();
		checkProperty( constructorArgs, ParserConstants.CONSTRUCTOR_ARGS + "1", null, "eventBus");

		it =  anotherActivityManager.getConstructorArgs().iterator();
		constructorArgs = it.next();
		checkProperty( constructorArgs, ParserConstants.CONSTRUCTOR_ARGS + "0", null, "anotherActivityMapper");
		
		constructorArgs = it.next();
		checkProperty( constructorArgs, ParserConstants.CONSTRUCTOR_ARGS + "1", null, "legacyEventBus");

		assertTrue("Propertes found were none expected", activityManager.getProperties().isEmpty() );
		assertTrue("Propertes found were none expected", anotherActivityManager.getProperties().isEmpty() );
		
		
		//TODO check that a RunTimeDependency has been set on the activity manager's arguments
	}


	/**
	 * Test <gwt:create/> nodes parsing
	 */
	@Test
	public void testCreate()
	{
		// Retrieve personBean and test properties
		Bean createBean = loadedBeans.get("instance");

		Assert.assertNotNull( createBean );

		// Bean definition checks
		assertEquals("Bean id not properly set", "instance", createBean.getId() );
		assertEquals("Bean class not properly set", "test.stubbedInterface", createBean.getClassName() );
		assertEquals("Bean type not properly set", Bean.Type.SIMPLE , createBean.getType() );
		assertFalse("Named bean wrongly considered as anonymous" , createBean.isAnonymous() );
		assertFalse("Real bean wrongly considered as abstract" , createBean.isAbstract() );

		//Factory-Bean on GWT.create()
		assertEquals("Factory bean not properly set", GWT , createBean.getFactoryBean() );
		assertEquals("Factory method not properly set", "create" , createBean.getFactoryMethod() );		
		assertEquals("Constructor args not found were expected", createBean.getConstructorArgs().size(), 1 );

		Property constructorArgs = createBean.getConstructorArgs().iterator().next();
		checkProperty( constructorArgs, ParserConstants.CONSTRUCTOR_ARGS + "0", "test.interface.class", null);

		assertTrue("Propertes found were none expected", createBean.getProperties().isEmpty() );
		
		// Check for mis-formatted nodes
		Assert.assertNull( loadedBeans.get("missingClassGwtCreate") );
		Assert.assertNull( loadedBeans.get("missingInterfaceGwtCreate") );
		Assert.assertNull( loadedBeans.get("missingClassAndInterfaceGwtCreate") );
		
	}
}
