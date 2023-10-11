package com.att.sales.nexxus.pddm.service;

import com.att.sales.framework.exception.SalesBusinessException;
import com.att.sales.nexxus.model.ProductDataLoadRequest;

/**
 * The Interface NexxusUDFDetails.
 */
public interface NexxusUDFDetails {
	
	/**
	 * Put nexxus UDF details.
	 *
	 * @param productDataLoadRequest the product data load request
	 * @throws SalesBusinessException the sales business exception
	 */
	public void putNexxusUDFDetails(ProductDataLoadRequest productDataLoadRequest) throws SalesBusinessException;

}
