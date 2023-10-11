/**
 * 
 */
package com.att.sales.nexxus.accesspricing.service;

import com.att.sales.framework.exception.SalesBusinessException;
import com.att.sales.framework.model.ServiceResponse;
import com.att.sales.nexxus.model.APUiResponse;
import com.att.sales.nexxus.model.AccessPricingUiRequest;

/**
 * The Interface IglooAccessPricingService.
 *
 * @author RudreshWaladaunki
 */
public interface IglooAccessPricingService {

	/**
	 * Gets the access pricing.
	 *
	 * @param request the request
	 * @return the access pricing
	 * @throws SalesBusinessException the sales business exception
	 */
	public ServiceResponse getAccessPricing(AccessPricingUiRequest request) throws SalesBusinessException;

}
