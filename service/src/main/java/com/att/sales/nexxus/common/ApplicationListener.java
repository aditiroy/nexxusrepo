/**
 * Added by Lijo Manickathan John
  */
package com.att.sales.nexxus.common;



import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



/**
 * The listener interface for receiving application events.
 * The class that is interested in processing a application
 * event implements this interface, and the object created
 * with that class is registered with a component using the
 * component's <code>addApplicationListener<code> method. When
 * the application event occurs, that object's appropriate
 * method is invoked.
 *
 * @author Lijo Manickathan John
 */
public class ApplicationListener implements ServletContextListener {

	/**
	 * The Constant LOG.
	 * 
	 * @inheritDoc Logger.
	 */
	private static final Logger LOG = LoggerFactory.getLogger(ApplicationListener.class);

	/**
	 * Instantiates a new common api initializer.
	 * 
	 * @inheritDoc Default constructor.
	 */
	public ApplicationListener() {
		if (LOG.isDebugEnabled()) {
			LOG.debug("ApplicationListener : Intstantiated - app startup");
		}
	}

	/**
	 * Context destroyed.
	 *
	 * @param sce the sce
	 * @inheritDoc This method is contextDestroyed.
	 */
	
	public void contextDestroyed(ServletContextEvent sce) {
		if (LOG.isDebugEnabled()) {
			LOG.debug("Context Destruction Invoked");
		}
		
	}
	
	/**
	 * Context initialized.
	 * 
	 * @param context
	 *            the context
	 * @inheritDoc This method for initialising the context with different
	 *             integrationMappings
	 *             ,messageResourceMappings,validationRulesMappings.
	 */
	public void contextInitialized(ServletContextEvent context) {

		try {
		
			if (LOG.isDebugEnabled()) {
				LOG.debug("Loading the common config properties");
			}
				
		
			
		} catch (RuntimeException exp) {
			LOG.error("Exception in ApplicationListener", exp);
		}
	}
	
	
	
	

}
