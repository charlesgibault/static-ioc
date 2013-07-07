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
package org.staticioc.samples.coord;

import org.staticioc.samples.services.CustomerService;
import org.staticioc.samples.services.RemoteService;

/**
 * ExampleCoordinator
 * Example of coordinator that only depends on service defintion interface and
 * that is completely uncorrelated from actual implementation classes
 */
public class ExampleCoordinator
{
	RemoteService remoteService; // I don't know who implement this nor don't I care
	CustomerService customerService; // Same here
	
	public void performAction( String clientId, String message )
	{
		customerService.call(clientId);
		remoteService.sendMessage(message);
	}

	public void setRemoteService(RemoteService remoteService) {
		this.remoteService = remoteService;
	}

	public void setCustomerService(CustomerService customerService) {
		this.customerService = customerService;
	}
}
