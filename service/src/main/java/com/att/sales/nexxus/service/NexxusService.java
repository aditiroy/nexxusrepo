package com.att.sales.nexxus.service;

import java.util.LinkedHashMap;

import com.att.sales.framework.exception.SalesBusinessException;
import com.att.sales.framework.model.ServiceResponse;
import com.att.sales.nexxus.edf.model.ManageBillingPriceInvDataRequest;
import com.att.sales.nexxus.reteriveicb.model.NexxusTestRequest;
import com.att.sales.nexxus.reteriveicb.model.NexxusTestResponse;
import com.att.sales.nexxus.rome.model.GetBillingChargesRequest;

/**
 * The Interface NexxusService.
 *
 * @author sw088d
 */
public interface NexxusService {
	
	/**
	 * Transform test data.
	 *
	 * @param request the request
	 * @return the nexxus test response
	 */
	ServiceResponse transformTestData(NexxusTestRequest request);
	
	/**
	 * Gets the billing price inventry data.
	 *
	 * @param inventoryrequest the inventoryrequest
	 * @return the billing price inventry data
	 * @throws SalesBusinessException the sales business exception
	 */
	ServiceResponse getBillingPriceInventryData(
			ManageBillingPriceInvDataRequest inventoryrequest) throws SalesBusinessException;

	/**
	 * This method fetches the list of solutions by user Id along with the account
	 * searches associated with each solution.
	 * 
	 * @param queryParams query parameters passed in the request url
	 * @return Object of type NexxusSolutionDetailUiModelResponse
	 * @throws SalesBusinessException 
	 */
	public ServiceResponse fetchNexxusSolutionsByUserId(LinkedHashMap<String, Object> queryParams) throws SalesBusinessException;
	
	public void updateNxSolution(Long nxSolutionId);

	/**
	 * Gets the billing price inventry data.
	 *
	 * @param inventoryrequest the inventoryrequest
	 * @return the billing price inventry data
	 * @throws SalesBusinessException the sales business exception
	 */
	ServiceResponse retrieveBillingCharges(GetBillingChargesRequest inventoryrequest) throws SalesBusinessException;
	
}
