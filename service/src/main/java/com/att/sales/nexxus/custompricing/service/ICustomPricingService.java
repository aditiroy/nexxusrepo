package com.att.sales.nexxus.custompricing.service;

import org.json.JSONException;

import com.att.sales.framework.exception.SalesBusinessException;
import com.att.sales.framework.model.ServiceResponse;
import com.att.sales.nexxus.custompricing.model.CustomPricingRequest;
import com.att.sales.nexxus.custompricing.model.CustomPricingResponse;

public interface ICustomPricingService {

	ServiceResponse getCutomPricing(CustomPricingRequest request) throws JSONException, SalesBusinessException;
	
}
 