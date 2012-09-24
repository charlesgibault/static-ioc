/** This code has been generated using Static IoC framework (http://code.google.com/p/static-ioc/). DO NOT EDIT MANUALLY HAS CHANGES MAY BE OVERRIDEN */
package org.staticioc.samples;
public class ApplicationContext
{
	/**
	 * Bean definition
*/	
	public final org.staticioc.samples.coord.ExampleCoordinator coordinator;
	public final org.staticioc.samples.services.impl.RemoteServiceImpl remoteService;
	public final org.staticioc.samples.services.impl.CustomerServiceImpl customerService;

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


	}	public void destroyContext()
	{

	}
}