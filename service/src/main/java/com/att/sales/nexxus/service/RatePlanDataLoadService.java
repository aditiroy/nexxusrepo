package com.att.sales.nexxus.service;

import com.att.sales.framework.exception.SalesBusinessException;
import com.att.sales.nexxus.model.ProductDataLoadRequest;

/**
 * The Interface RatePlanDataLoadService.
 */
public interface RatePlanDataLoadService {
	
	/**
	 * Put rate plan data load.
	 *
	 * @param productDataLoadRequest the product data load request
	 * @throws SalesBusinessException the sales business exception
	 */
	public void putRatePlanDataLoad(ProductDataLoadRequest productDataLoadRequest) throws SalesBusinessException;

}
