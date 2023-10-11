package com.att.sales.framework.filters;

import java.util.HashSet;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;


@Configuration
public class FrameworkFilterConfiguration {
	
	private static final Logger log = LoggerFactory.getLogger(FrameworkFilterConfiguration.class);
	
	@Bean
	public FilterRegistrationBean corsFilter() {
		log.info("> corsFilter entered");
		FilterRegistrationBean registration = new FilterRegistrationBean();
		SimpleCORSFilter filter = new SimpleCORSFilter();

		registration.setFilter(filter);
		registration.setOrder(Ordered.HIGHEST_PRECEDENCE);
		registration.addUrlPatterns("/nexxus/*");
		//registration.setUrlPatterns(getPatternUrls());
		//registration.setServletNames(getServletNames());

		log.info("> corsFilter exit");
		return registration;
	}
	
	@Bean
	public FilterRegistrationBean mSPreFilter() {
		
		log.info("> mSPreFilter entered");

		FilterRegistrationBean registration = new FilterRegistrationBean();
		MSPreFilter filter = new MSPreFilter();
		registration.setFilter(filter);
		registration.setOrder(Ordered.HIGHEST_PRECEDENCE + 10);
		registration.addUrlPatterns("/nexxus/*");
		//registration.setServletNames(getServletNames());
		log.info("> mSPreFilter exit");
		return registration;
	}
	
	@Bean
	public FilterRegistrationBean applicationPreFilter() {
		
		log.info("> ApplicationPreFilter entered");

		FilterRegistrationBean registration = new FilterRegistrationBean();
		ApplicationPreFilter filter = new ApplicationPreFilter();
		registration.setFilter(filter);
		registration.setOrder(Ordered.HIGHEST_PRECEDENCE + 20);
		registration.addUrlPatterns("/nexxus/*");
		//registration.setUrlPatterns(getPatternUrls());
		//registration.setServletNames(getServletNames());
		log.info("> ApplicationPreFilter exit");
		return registration;
	}

	@Bean
	public FilterRegistrationBean actuatorFilter() {
		
		log.info("> ApplicationPreFilter entered");

		FilterRegistrationBean registration = new FilterRegistrationBean();
		registration.setFilter(new ActuatorFilter());
		registration.addUrlPatterns("/actuator/*");
		log.info("> ApplicationPreFilter exit");
		return registration;
	}
	

	public static Set<String> getPatternUrls() {

		Set<String> patternUrls = new HashSet<String>();
		patternUrls.add("/rest/*");
		patternUrls.add("/restservices/*");

		String patternUrl = System.getProperty("patternUrls");
		if (patternUrl != null) {
			String[] userDefinedPatternUrls = patternUrl.split(",");
			for (int i = 0; i < userDefinedPatternUrls.length; i++) {
				patternUrls.add(userDefinedPatternUrls[i]);
			}
		}

		return patternUrls;
	}

	public static Set<String> getServletNames() {

		Set<String> servletNames = new HashSet<String>();
		servletNames.add("m2EInvokerServlet");
		servletNames.add("CXFServlet");
		servletNames.add("RestletServlet");

		String servletName = System.getProperty("servletNames");
		if (servletName != null) {
			String[] userDefinedServletNames = servletName.split(",");
			for (int i = 0; i < userDefinedServletNames.length; i++) {
				servletNames.add(userDefinedServletNames[i]);
			}
		}

		return servletNames;
	}
}
