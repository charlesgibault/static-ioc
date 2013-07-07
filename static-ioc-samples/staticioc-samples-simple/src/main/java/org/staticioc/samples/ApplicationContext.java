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
/** This code has been generated using Static IoC framework (http://code.google.com/p/static-ioc/). DO NOT EDIT MANUALLY HAS CHANGES MAY BE OVERRIDEN */
package org.staticioc.samples;
@SuppressWarnings("all")
public class ApplicationContext
{
	/**
	 * Bean definition
*/	
	public org.staticioc.samples.coord.ExampleCoordinator coordinator;
	public org.staticioc.samples.services.impl.RemoteServiceImpl remoteService;
	public org.staticioc.samples.services.impl.CustomerServiceImpl customerService;

	/**
	 * Constructor of the Factory
*/	
	public ApplicationContext()
	{
		// Instanciating beans
		coordinator = new org.staticioc.samples.coord.ExampleCoordinator();
		remoteService = new org.staticioc.samples.services.impl.RemoteServiceImpl();
		customerService = new org.staticioc.samples.services.impl.CustomerServiceImpl();

		// Setting up bean coordinator
		coordinator.setRemoteService( remoteService );
		coordinator.setCustomerService( customerService );


	}

	public void destroyContext()
	{
		customerService = null;
		remoteService = null;
		coordinator = null;

	}
}