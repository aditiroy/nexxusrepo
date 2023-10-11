package com.att.sales.nexxus.common;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import javax.ws.rs.core.UriBuilder;

import org.apache.commons.lang.StringUtils;

import com.att.aft.dme2.api.DME2RestfulHandler;
import com.att.aft.dme2.logging.Logger;
import com.att.aft.dme2.logging.LoggerFactory;
import com.att.aft.dme2.request.DME2Payload;
import com.att.aft.dme2.request.DME2TextPayload;

/**
 * The Class DME2Utility.
*
 * @author Lijo Manickathan John( Sales)
 * 
 * 
 *         DME2 Client which gets called for GET,POST,PUT,PATCH and DELETE
 *         requests The latitude and longitude configurations need to be passed
 *         for the ACTIVE cluster. When ACTIVE CLUSTER goes down, the request
 *         will route to the DR cluster having the same route offer. As long as
 *         we specify the right latitude and longitude of ACTIVE cluster in DME2
 *         request ,all requests will go to the ACTIVE cluster and not to DR
 *         cluster
 */

public class DME2TestUtility {

	private static Logger log = LoggerFactory.getLogger(DME2TestUtility.class);

	/**
	 * Factory Method : POST ,PUT or DELETE request with BODY
	 * 
	 * @param String
	 *            env,String latitude,String longitude,String uri,String
	 *            subContext,String method,Map requestHeaders,String
	 *            requestPayload,String userId,String password
	 * 
	 * 
	 * @return String
	 * @throws Exception
	 * 
	 * @inheritDoc Returns the response
	 */

	public String processRequest(String env, String latitude, String longitude, String uri, String subContext,
			String method, Map<String, String> queryParameters, Map<String, String> requestHeaders,
			String requestPayload, String userId, String password, boolean async) throws Exception {

		if (!StringUtils.isEmpty(requestPayload)) {

			requestHeaders.put("Content-Length", Integer.toString(requestPayload.length()));

		} else if (requestPayload == null) {
			requestPayload = "";
		}
		if (queryParameters == null) {

			queryParameters = new HashMap<String, String>();
		} else {

			UriBuilder builder = UriBuilder.fromPath(subContext);

			for (java.util.Map.Entry<String, String> entry : queryParameters.entrySet()) {
				builder.queryParam(entry.getKey(), entry.getValue());
			}

			URI subcontextURI = builder.build();

			subContext = subcontextURI.toString();

			queryParameters = new HashMap<String, String>();

		}

		System.setProperty("AFT_ENVIRONMENT", env);
		System.setProperty("AFT_LATITUDE", latitude);
		System.setProperty("AFT_LONGITUDE", longitude);
	    System.setProperty("AFT_DME2_PARSE_FAULT", "false");
	    System.setProperty("AFT_DME2_LEGACY_SEMANTIC_VERSION", "true");
		
				
		DME2Payload payload = new DME2TextPayload(requestPayload);
		
				
		
		String response = DME2RestfulHandler.callService(uri, 480000, method, subContext, queryParameters,
				requestHeaders, payload, userId, password).getBody().toString();
		
		System.out.println("Response is"+response);
	

		return response;

	}

	/**
	 * Factory Method : POST ,PUT or DELETE request with BODY
	 * 
	 * @param String
	 *            env,String latitude,String longitude,String uri,String
	 *            subContext,String method,Map requestHeaders,String
	 *            requestPayload,String userId,String password
	 * 
	 * 
	 * @return String
	 * @throws Exception
	 * 
	 * @inheritDoc Returns the response
	 */

	public String processRequest(String env, String latitude, String longitude, String uri, String subContext,
			String method, Map<String, String> queryParameters, Map<String, String> requestHeaders,
			String requestPayload, String userId, String password, boolean async, Integer timeout) throws Exception {

		if (!StringUtils.isEmpty(requestPayload)) {

			requestHeaders.put("Content-Length", Integer.toString(requestPayload.length()));

		} else if (requestPayload == null) {
			requestPayload = "";
		}
		if (queryParameters == null) {

			queryParameters = new HashMap<String, String>();
		} else {

			UriBuilder builder = UriBuilder.fromPath(subContext);

			for (java.util.Map.Entry<String, String> entry : queryParameters.entrySet()) {
				builder.queryParam(entry.getKey(), entry.getValue());
			}

			URI subcontextURI = builder.build();

			subContext = subcontextURI.toString();

			queryParameters = new HashMap<String, String>();

		}

		System.setProperty("AFT_ENVIRONMENT", env);
		System.setProperty("AFT_LATITUDE", latitude);
		System.setProperty("AFT_LONGITUDE", longitude);
		DME2Payload payload = new DME2TextPayload(requestPayload);

		
		String response = DME2RestfulHandler.callService(uri, timeout, method, subContext, queryParameters,
				requestHeaders, payload, userId, password).getBody().toString();
		if (StringUtils.isEmpty(response)) {

			
			throw new RuntimeException("DME2 Response is empty");
		}

		return response;

	}

	

}
