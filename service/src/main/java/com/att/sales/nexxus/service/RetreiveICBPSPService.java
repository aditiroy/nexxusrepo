package com.att.sales.nexxus.service;

import com.att.sales.framework.model.ServiceResponse;
import com.att.sales.nexxus.reteriveicb.model.RetreiveICBPSPRequest;
import com.fasterxml.jackson.core.JsonProcessingException;

/**
 * The Interface RetreiveICBPSPService.
 */
public interface RetreiveICBPSPService {
	
	/**
	 * Retreive ICBPSP.
	 *
	 * @param request the request
	 * @return the retreive ICBPSP response
	 * @throws JsonProcessingException 
	 */
	ServiceResponse retreiveICBPSP(RetreiveICBPSPRequest request) ;
}
