package com.att.sales.nexxus.rome.service;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.att.sales.framework.exception.SalesBusinessException;
import com.att.sales.framework.service.BaseServiceImpl;
import com.att.sales.nexxus.handlers.GetBillingChargesWSHandler;
import com.att.sales.nexxus.rome.model.ABSDWGetBillingChargesRequest;
import com.att.sales.nexxus.rome.model.GetBillingChargesResponse;

/**
 * The Class GetBillingChargesServiceImpl.
 */
@Service
public class GetBillingChargesServiceImpl extends BaseServiceImpl {

	/** The logger. */
	private static Logger logger = LoggerFactory.getLogger(GetBillingChargesServiceImpl.class);

	@Autowired
	private GetBillingChargesWSHandler getBillingChargesWSHandler;

	/**
	 * Retrieve billing charges.
	 *
	 * @param request the request
	 * @return the gets the billChargesResp
	 * @throws SalesBusinessException the sales business exception
	 */
	public GetBillingChargesResponse performGetBillingCharges(ABSDWGetBillingChargesRequest request)
			throws SalesBusinessException {
		GetBillingChargesResponse billChargesResp = null;
		logger.info("Entered performGetBillingCharges() method");
		Map<String, Object> requestMap = new HashMap<>();
		requestMap.put("billDate", request.getBillDate());
		requestMap.put("keyFieldID", request.getKeyFieldID());
		requestMap.put("requestType", request.getRequestType());
		requestMap.put("refNB", request.getRefNB());
		requestMap.put("mcnNB", request.getMcnNB());	
		requestMap.put("svID", request.getSvID());
		
		
		try {
			logger.info("Ended performGetBillingCharges() method");
			billChargesResp = getBillingChargesWSHandler.initiateGetOptyInfoWebService(requestMap);
			setSuccessResponse(billChargesResp);
		} catch (Exception ex) {
			ex.printStackTrace();
			logger.error("Exception during GetBillingChargesServiceImpl : performGetBillingCharges call", ex);
			throw new SalesBusinessException("GetBillingChargesServiceImpl");
		}
		return billChargesResp;
	}
}
