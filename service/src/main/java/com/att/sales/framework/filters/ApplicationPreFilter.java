package com.att.sales.framework.filters;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.att.sales.framework.model.ServiceMetaData;
import com.att.sales.nexxus.common.CommonConstants;

public class ApplicationPreFilter implements Filter {

	/**
	 * The Logger for this class.
	 */
	private static final Logger log = LoggerFactory.getLogger(ApplicationPreFilter.class);

	@Override

	public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain)
			throws IOException, ServletException {

		log.info("> doFilter");

		HttpServletRequest request = (HttpServletRequest) req;
		String headerValue = request.getHeader("Authorization");

		
		Map <String, Object> requestMetaDataHash = new HashMap<>();

		String offer = request.getHeader("OFFER");
		String filename = request.getHeader(CommonConstants.FILENAME);
		String transactionId = request.getHeader("TransactionId");
		String method=(String)ServiceMetaData.getRequestMetaData().get(ServiceMetaData.METHOD);
		String uri=(String)ServiceMetaData.getRequestMetaData().get(ServiceMetaData.URI);
		String version=(String)ServiceMetaData.getRequestMetaData().get(ServiceMetaData.VERSION);
		String attuid=request.getHeader("attuid");
		// Initialize Thread Local
		// Pass the variables which you need to add in Thread Local
		// Access the Threadlocal variable across the application using
		// Map<String,Object> map=ServiceMetaData.getRequestMetaData()
		
		
		requestMetaDataHash.put("OFFER", offer);
		requestMetaDataHash.put("TransactionId", transactionId);
		requestMetaDataHash.put(CommonConstants.FILENAME, filename);
		requestMetaDataHash.put("ATTUID", attuid);
		// Change this to add more filter parameters. SERVICEID should match the id
				// attribute of service_definition xml file
				// eg: <sales-service id="GET:/domainobject/servicenamenoun:v1:actionType">
		requestMetaDataHash.put(ServiceMetaData.SERVICE_FILTER,
				method.concat(":").concat(uri).concat(":").concat(version));		
		 HttpServletResponse response = (HttpServletResponse) resp;
		ServiceMetaData.add(requestMetaDataHash);

		chain.doFilter(request, response);
	
	
	}


	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		log.info("creating {}", ApplicationPreFilter.class.getName());
		
	}

	@Override
	public void destroy() {
		log.info("destroying {}", ApplicationPreFilter.class.getName());
		
	}
}