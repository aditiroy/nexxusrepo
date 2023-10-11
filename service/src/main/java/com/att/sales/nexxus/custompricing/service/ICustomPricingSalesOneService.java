package com.att.sales.nexxus.custompricing.service;

import com.att.sales.framework.model.ServiceResponse;
import com.att.sales.nexxus.custompricing.model.CustomPricingRequest;
import com.att.sales.nexxus.custompricing.model.CustomPricingResponse;
import com.att.sales.nexxus.custompricing.model.GetCustomPricingResponse;

public interface ICustomPricingSalesOneService {

	ServiceResponse getCustomPricingSalesOne(CustomPricingRequest request);
	
}
 