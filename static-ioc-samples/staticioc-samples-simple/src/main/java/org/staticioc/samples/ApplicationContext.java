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