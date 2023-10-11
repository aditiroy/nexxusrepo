package com.att.sales.nexxus.fallout.service;

import com.att.sales.framework.exception.SalesBusinessException;
import com.att.sales.framework.model.ServiceResponse;
import com.att.sales.nexxus.fallout.model.FalloutDetailsRequest;
import com.att.sales.nexxus.fallout.model.FalloutDetailsResponse;

/**
 * The Interface ActionService.
 */
public interface ActionService {
	
	public ServiceResponse copyAction(FalloutDetailsRequest request) throws SalesBusinessException; 
	
	public ServiceResponse submitToMyprice(FalloutDetailsRequest request) throws SalesBusinessException;
		
	public ServiceResponse retriggerRequest(FalloutDetailsRequest request) throws SalesBusinessException;

}
