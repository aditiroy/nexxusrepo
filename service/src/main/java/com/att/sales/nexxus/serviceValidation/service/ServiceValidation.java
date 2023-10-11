package com.att.sales.nexxus.serviceValidation.service;

import com.att.sales.framework.exception.SalesBusinessException;
import com.att.sales.framework.model.ServiceResponse;
import com.att.sales.nexxus.serviceValidation.model.ServiceValidationRequest;
import com.att.sales.nexxus.serviceValidation.model.ServiceValidationResponse;

public interface ServiceValidation {

	public ServiceResponse validateService(ServiceValidationRequest request) throws SalesBusinessException;

}
