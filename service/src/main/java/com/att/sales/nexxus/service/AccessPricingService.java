package com.att.sales.nexxus.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.att.sales.framework.exception.SalesBusinessException;
import com.att.sales.nexxus.inr.InrFactory;
import com.att.sales.nexxus.inr.InrJsonToIntermediateJson;
import com.att.sales.nexxus.inr.OutputJsonFallOutData;
import com.att.sales.nexxus.inr.OutputJsonService;
import com.fasterxml.jackson.databind.JsonNode;

/**
 * The Class AccessPricingService.
 */
@Component
public class AccessPricingService {
	
	/** The inr factory. */
	@Autowired
	private InrFactory inrFactory;
	
	/** The output json service. */
	@Autowired
	private OutputJsonService outputJsonService;

	/**
	 * Generate intermediate json.
	 *
	 * @param response the response
	 * @return the json node
	 * @throws SalesBusinessException the sales business exception
	 */
	public JsonNode generateIntermediateJson(JsonNode response) throws SalesBusinessException {
		InrJsonToIntermediateJson inrJsonToIntermediateJson = inrFactory.getInrJsonToIntermediateJson(response);
		return inrJsonToIntermediateJson.generate();
	}

	/**
	 * Generate output json.
	 *
	 * @param intermediateJson the intermediate json
	 * @return the output json fall out data
	 * @throws SalesBusinessException the sales business exception
	 */
	public OutputJsonFallOutData generateOutputJson(JsonNode intermediateJson) throws SalesBusinessException {
		return outputJsonService.getOutputData(intermediateJson, "INR");
	}
}
