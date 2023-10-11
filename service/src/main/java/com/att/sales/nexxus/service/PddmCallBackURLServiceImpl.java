package com.att.sales.nexxus.service;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import com.att.aft.dme2.internal.jackson.JsonParseException;
import com.att.aft.dme2.internal.jackson.map.JsonMappingException;
import com.att.sales.framework.exception.SalesBusinessException;

import com.att.sales.nexxus.model.ProductRuleRequest;
import com.att.sales.nexxus.model.ProductRuleResponse;
import com.att.sales.nexxus.util.DME2RestClient;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * The Class PddmCallBackURLServiceImpl.
 */
@Service
public class PddmCallBackURLServiceImpl {
	

	/** The log. */
	private static Logger log = LoggerFactory.getLogger(PddmCallBackURLServiceImpl.class);

	/** The dme client. */
	@Autowired
	private DME2RestClient dmeClient;
	
	
	/** The env. */
	@Autowired
	private Environment env;
	
	/** The mapper. */
	@Autowired
	private ObjectMapper mapper;
	
	/**
	 * Gets the p roduct rules from pddm.
	 *
	 * @param request the request
	 * @return the p roduct rules from pddm
	 * @throws SalesBusinessException the sales business exception
	 * @throws JsonParseException the json parse exception
	 * @throws JsonMappingException the json mapping exception
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	/*
	 * DME2 call for pddm
	 * 
	 * @param request 
	 * @return responseObj
	 * @throws SalesBusinessException
	 * @throws JsonParseException
	 * @throws JsonMappingException
	 * @throws IOException
	 * 
	 */
	public ProductRuleResponse getPRoductRulesFromPddm(ProductRuleRequest request)
			throws SalesBusinessException, JsonParseException, JsonMappingException, IOException {
		log.info("inside getPricingScenarioDetailsFromMS method");

		Map<String, Object> requestMap = new HashMap<>();
		requestMap.put("JSON", request);
		
		
		Map<String, String> requestHeaders = new HashMap<>();
	
		requestHeaders.put("Accept", "application/json");
		
		String uri = env.getProperty("productRules.uri");
		String subContext = env.getProperty("productRules.subContext");
		String aftLatitude = env.getProperty("dme2.latitude");
		String aftLongitude = env.getProperty("dme2.longitude");
		String aftEnvironment = env.getProperty("dme2.aftenv");
		String userName = env.getProperty("dme2.authid");
		String password = env.getProperty("dme2.authpassword");
		log.info("Calling Pddm microservice with Dmaap Data");
		
		
		
		String requestPayLoad = mapper.writeValueAsString(request);
log.info("The request now is {}", requestPayLoad);
		
		
		String response = dmeClient.callDme2Client(uri, subContext, requestPayLoad, aftLatitude, aftLongitude, aftEnvironment,
				userName, password, requestHeaders);
		
		ProductRuleResponse responseObj = mapper.readValue(response,
				ProductRuleResponse.class);
		log.info("The response is {}", response);
		
		return responseObj;
	}

}
