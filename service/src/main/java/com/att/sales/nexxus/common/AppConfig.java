/**
 * Added by Lijo Manickathan John
  */
package com.att.sales.nexxus.common;


import javax.sql.DataSource;

import org.dozer.DozerBeanMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;




/**
 * The Class AppConfig.
 *
 * @author Lijo Manickathan John
 */
@Configuration
public class AppConfig {
	private static Logger log = LoggerFactory.getLogger(AppConfig.class);

	/**
	 * Application listener.
	 *
	 * @return the application listener
	 */
	@Bean
	public ApplicationListener applicationListener() {
		return new ApplicationListener();
	}
	
	/**
     * Dozer bean mapper.
     *
     * @return the dozer bean mapper
     */
    @Bean
    public DozerBeanMapper dozerBeanMapper() {
        return new DozerBeanMapper();
    }
    
	@Bean
	@ConfigurationProperties(prefix = "ds.usrp")
	public DataSource usrpDS() {
		try {
			return DataSourceBuilder.create().build();
		} catch (Exception e) {
			log.error("Exception in creating datasource", e);
			return null;
		}
	}
	
	@Bean(name="taskscheduler")
	public ThreadPoolTaskScheduler taskScheduler(){
	    ThreadPoolTaskScheduler threadPoolTaskScheduler = new ThreadPoolTaskScheduler();
	    threadPoolTaskScheduler.setPoolSize(3);
	    threadPoolTaskScheduler.setAwaitTerminationSeconds(60);
	    threadPoolTaskScheduler.setThreadNamePrefix("TASK_SCHEDULER-");
	    return threadPoolTaskScheduler;
	}
}
