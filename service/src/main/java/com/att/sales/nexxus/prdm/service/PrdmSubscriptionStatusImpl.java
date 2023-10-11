package com.att.sales.nexxus.prdm.service;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import com.att.sales.framework.exception.SalesBusinessException;
import com.att.sales.framework.model.Message;
import com.att.sales.framework.util.MessageResourcesUtil;
import com.att.sales.nexxus.common.CommonConstants;
import com.att.sales.nexxus.dao.repository.NxPrdmFileAuditStatusRepository;
import com.att.sales.nexxus.model.PrdmSubscriptionStatusRequest;
import com.att.sales.nexxus.model.PrdmSubscriptionStatusResponse;
import com.att.sales.nexxus.util.DME2RestClient;
import com.att.sales.prdm.dao.NxRatePlanFileAuditData;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * The Class PrdmSubscriptionStatusImpl.
 *
 * @author DevChouhan
 */
@Service
public class PrdmSubscriptionStatusImpl {
	
	/** The log. */
	private static Logger log = LoggerFactory.getLogger(PrdmSubscriptionStatusImpl.class);
	
	/** The dme client. */
	@Autowired
	private DME2RestClient dmeClient;
	
	/** The env. */
	@Autowired
	private Environment env;
	
	/** The mapper. */
	@Autowired
	private ObjectMapper mapper;
	
	/** The prdm subscription status request. */
	@Autowired
	private PrdmSubscriptionStatusRequest prdmSubscriptionStatusRequest;
	
	/** The audit status repository. */
	@Autowired
	private NxPrdmFileAuditStatusRepository auditStatusRepository;
	
	/**
	 * Sets the prdm subscription status.
	 *
	 * @param fileId the file id
	 * @param reasonCode the reason code
	 * @throws SalesBusinessException the sales business exception
	 */
	public void setPrdmSubscriptionStatus(BigDecimal fileId, String reasonCode) throws SalesBusinessException{
		NxRatePlanFileAuditData nxRatePlanFileAuditStatus = auditStatusRepository.findByFileId(fileId);

		String domainObject = null;
		String client = "nexxus";
		String msApplication = "nexxus";
		BigDecimal salesPddmId = null;

		if (null != nxRatePlanFileAuditStatus) {

			prdmSubscriptionStatusRequest.setClient(client);
			prdmSubscriptionStatusRequest.setDomainObject(domainObject);
			prdmSubscriptionStatusRequest.setMsApplication(msApplication);
			prdmSubscriptionStatusRequest.setSalesPddmId(salesPddmId);
			prdmSubscriptionStatusRequest.setStatus(nxRatePlanFileAuditStatus.getStatus());
			prdmSubscriptionStatusRequest.setStatusType(nxRatePlanFileAuditStatus.getStatusType());
			prdmSubscriptionStatusRequest.setTimeStamp(new Date());
			prdmSubscriptionStatusRequest.setTransactionId(nxRatePlanFileAuditStatus.getTransactionId());
			prdmSubscriptionStatusRequest.setReasonCode(reasonCode);

			if (!nxRatePlanFileAuditStatus.getStatusType().equalsIgnoreCase(CommonConstants.SUCCESS)) {
				Message reasonMsg = MessageResourcesUtil.getMessageMapping().get(reasonCode);
				prdmSubscriptionStatusRequest.setReason(reasonMsg.getDescription());
			}
			// DME2 call to PRDM for status
			/*try {
				callingPrdmSubscriptionStatus(prdmSubscriptionStatusRequest);
			} catch (IOException e) {
				log.error("Exception occured during PDDM STATUS CALL", e);
				throw new SalesBusinessException(e.getMessage());
				
			}*/
		}

	}
	
	/**
	 * Calling prdm subscription status.
	 *
	 * @param request the request
	 * @return the prdm subscription status response
	 * @throws SalesBusinessException the sales business exception
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public PrdmSubscriptionStatusResponse callingPrdmSubscriptionStatus(PrdmSubscriptionStatusRequest request)
			throws SalesBusinessException, IOException {

		log.info("Inside callingPddmSubscriptionStatus method");

		Map<String, Object> requestMap = new HashMap<>();
		requestMap.put("JSON", request);

		Map<String, String> requestHeaders = new HashMap<>();
		requestHeaders.put("Accept", "application/json");

		String uri = env.getProperty("");
		String subContext = env.getProperty("");
		String aftLatitude = env.getProperty("dme2.latitude");
		String aftLongitude = env.getProperty("dme2.longitude");
		String aftEnvironment = env.getProperty("dme2.aftenv");
		String userName = env.getProperty("dme2.authid");
		String password = env.getProperty("dme2.authpassword");
		log.info("Calling Pddm microservice Subscription status");

		String requestPayLoad = mapper.writeValueAsString(request);
		log.info("The request now is {}", requestPayLoad);

		String response = dmeClient.callDme2Client(uri, subContext, requestPayLoad, aftLatitude, aftLongitude,
				aftEnvironment, userName, password, requestHeaders);

		PrdmSubscriptionStatusResponse responseObj = mapper.readValue(response, PrdmSubscriptionStatusResponse.class);
		log.info("The response is {}", response);

		return responseObj;
	}

	

}
