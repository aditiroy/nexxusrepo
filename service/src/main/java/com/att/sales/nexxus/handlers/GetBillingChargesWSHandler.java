package com.att.sales.nexxus.handlers;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.att.cio.commonheader.v3.WSEndUserToken;
import com.att.cio.commonheader.v3.WSEnterpriseLogging;
import com.att.cio.commonheader.v3.WSHeader;
import com.att.ims.dwweb.billingcharges.v1.BillingChargesEntity;
import com.att.ims.dwweb.billingcharges.v1.GetBillingChargesInfoRequest;
import com.att.ims.dwweb.billingcharges.v1.GetBillingChargesInfoResponse;
import com.att.ims.dwweb.billingcharges.v1.ObjectFactory;
import com.att.sales.framework.exception.SalesBusinessException;
import com.att.sales.nexxus.common.MessageConstants;
import com.att.sales.nexxus.constant.MyPriceConstants;
import com.att.sales.nexxus.rome.model.GetBillingChargesResponse;
import com.att.sales.nexxus.ws.utility.SoapWSHandler;
import com.att.sales.nexxus.ws.utility.WSProcessingService;

/**
 * The Class GetBillingChargesWSHandler.
 */
@Component
public class GetBillingChargesWSHandler implements MessageConstants {

	/** The Constant log. */
	private static final Logger log = LoggerFactory.getLogger(GetBillingChargesWSHandler.class);

	@Autowired
	@Qualifier("getBillingChargesWSClientUtility")
	private SoapWSHandler getBillingChargesWSClientUtility;

	@Autowired
	private WSProcessingService wsProcessingService;

	/**
	 * To call GetBillingChargesInfo web-service published at DW application.
	 *
	 * @param requestMap the request map
	 * @return Map<String, Object>
	 * @throws SalesBusinessException the sales business exception
	 */
	public GetBillingChargesResponse initiateGetOptyInfoWebService(Map<String, Object> requestMap)
			throws SalesBusinessException {
		ObjectFactory objectFactory = new ObjectFactory();
		GetBillingChargesInfoRequest getBillingChargesInfoRequest = objectFactory.createGetBillingChargesInfoRequest();
		GetBillingChargesInfoResponse getBillingChargesInfoResponse = null;
		log.info("Entered initiateGetOptyInfoWebService() method");
		log.info("Creating Header details...");
			// createHeaderDetails(getBillingChargesInfoRequest);
		prepareRequestBody(requestMap, getBillingChargesInfoRequest);
		log.info("requestMap...:::: {}",requestMap);
		log.info("getBillingChargesInfoRequest...:::: {}",getBillingChargesInfoRequest.toString());
		try {
			getBillingChargesWSClientUtility.setWsName(MyPriceConstants.GET_BILLING_CHARGES_WS);

			getBillingChargesInfoResponse = wsProcessingService.initiateWebService(getBillingChargesInfoRequest,
					getBillingChargesWSClientUtility, requestMap, GetBillingChargesInfoResponse.class);

		} catch (Exception e) {
			log.error("Exception occurred in initiateGetOptyInfoWebService : " + e.getMessage());
			throw new SalesBusinessException(MessageConstants.STATUS_NOT_SUCCESS);
		}
		log.info("Ended initiateGetOptyInfoWebService() method");
		return processResponse(getBillingChargesInfoResponse, requestMap);
	}

	/**
	 * To prepare the request body for the web-service call.
	 *
	 * @param requestMap                   the request map
	 * @param getBillingChargesInfoRequest the billing charges info request
	 */
	protected void prepareRequestBody(Map<String, Object> requestMap,
			GetBillingChargesInfoRequest getBillingChargesInfoRequest) {
		getBillingChargesInfoRequest.setKeyFieldID((String) requestMap.get("keyFieldID"));
		getBillingChargesInfoRequest.setBillDate((String) requestMap.get("billDate"));
		getBillingChargesInfoRequest.setRequestType((String) requestMap.get("requestType"));
		getBillingChargesInfoRequest.setRefNB((String) requestMap.get("refNB"));
		getBillingChargesInfoRequest.setMcnNB((String) requestMap.get("mcnNB"));
		getBillingChargesInfoRequest.setSvID((String) requestMap.get("svID"));
		getBillingChargesInfoRequest.setL3AcctNB("");
		getBillingChargesInfoRequest.setPortNB("");
		getBillingChargesInfoRequest.setCircuitNB("");
		getBillingChargesInfoRequest.setParntAcctNB("");
		getBillingChargesInfoRequest.setSubAcctNB("");
		getBillingChargesInfoRequest.setRouterNB("");
	}

	/**
	 * To process the response received from web-service call.
	 *
	 * @param getBillingChargesInfoResponse the billing charges info response
	 * @param requestMap                    the request map
	 * @return Map<String, Object>
	 * @throws SalesBusinessException the sales business exception
	 */
	public GetBillingChargesResponse processResponse(GetBillingChargesInfoResponse getBillingChargesInfoResponse,
			Map<String, Object> requestMap) throws SalesBusinessException {
		log.info("Inside processResponse...");

		GetBillingChargesResponse resp = new GetBillingChargesResponse();
		BillingChargesEntity billingChargesEntity = getBillingChargesInfoResponse.getBillingChargesData()
				.getBillingChargesEntity().get(0);

		resp.setMessage(billingChargesEntity.getMessage());
		resp.setResponseCode(billingChargesEntity.getResponseCode());
		resp.setKeyFieldID((String) requestMap.get("keyFieldID"));

		return resp;

	}

	/**
	 * Method to create header details for change request.
	 *
	 * @param getBillingChargesInfoRequest the billing charges info request
	 * @throws SalesBusinessException the sales business exception
	 */
	private void createHeaderDetails(GetBillingChargesInfoRequest getBillingChargesInfoRequest)
			throws SalesBusinessException {
		WSHeader wsHeader = new WSHeader();
		try {
			wsHeader.setWSResponseMessageExpiration(30000L);

			WSEndUserToken wsEndUserToken = new WSEndUserToken();
			// wsEndUserToken.setToken(token);
			// wsEndUserToken.setTokenType(tokenType);
			wsHeader.setWSEndUserToken(wsEndUserToken);

			WSEnterpriseLogging wsEnterpriseLogging = new WSEnterpriseLogging();
//			wsEnterpriseLogging.setApplicationID(applicationId);
			// wsEnterpriseLogging.setLoggingKey(loggingKey);
			wsHeader.setWSEnterpriseLogging(wsEnterpriseLogging);

			getBillingChargesInfoRequest.setWSHeader(wsHeader);
		} catch (RuntimeException e) {
			log.error("Exception during header preparation", e);
			throw new SalesBusinessException(e.getMessage());
		}
	}

}
