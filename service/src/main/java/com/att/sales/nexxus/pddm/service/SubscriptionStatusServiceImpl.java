package com.att.sales.nexxus.pddm.service;

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
import com.att.sales.nexxus.dao.repository.NxPddmFileAuditStatusRepository;
import com.att.sales.nexxus.model.PddmSubscriptionStatusRequest;
import com.att.sales.nexxus.model.PddmSubscriptionStatusResponse;
import com.att.sales.nexxus.util.DME2RestClient;
import com.att.sales.pddm.dao.NxUDFDetailsFileAuditStatus;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * The Class SubscriptionStatusServiceImpl.
 *
 * @author RudreshWaladaunki
 */
@Service
public class SubscriptionStatusServiceImpl {
	
	/** The log. */
	private static Logger log = LoggerFactory.getLogger(SubscriptionStatusServiceImpl.class);
	
	/** The dme client. */
	@Autowired
	private DME2RestClient dmeClient;
	
	/** The env. */
	@Autowired
	private Environment env;
	
	/** The mapper. */
	@Autowired
	private ObjectMapper mapper;
	
	/** The pddm subscription status request. */
	@Autowired
	private PddmSubscriptionStatusRequest pddmSubscriptionStatusRequest;

	/** The nx pddm file audit status repo. */
	@Autowired
	private NxPddmFileAuditStatusRepository nxPddmFileAuditStatusRepo;

	/**
	 * Sets the pddm subscription status.
	 *
	 * @param fileId the file id
	 * @param reasonCode the reason code
	 * @throws SalesBusinessException the sales business exception
	 */
	public void setPddmSubscriptionStatus(BigDecimal fileId, String reasonCode) throws SalesBusinessException{
		NxUDFDetailsFileAuditStatus nxUDFDetailsFileAuditStatus = nxPddmFileAuditStatusRepo.findByFileId(fileId);

		String domainObject = null;
		String client = "nexxus";
		String msApplication = "nexxus";
		BigDecimal salesPddmId = null;

		if (null != nxUDFDetailsFileAuditStatus) {

			pddmSubscriptionStatusRequest.setClient(client);
			pddmSubscriptionStatusRequest.setDomainObject(domainObject);
			pddmSubscriptionStatusRequest.setMsApplication(msApplication);
			pddmSubscriptionStatusRequest.setSalesPddmId(salesPddmId);
			pddmSubscriptionStatusRequest.setStatus(nxUDFDetailsFileAuditStatus.getStatus());
			pddmSubscriptionStatusRequest.setStatusType(nxUDFDetailsFileAuditStatus.getStatusType());
			pddmSubscriptionStatusRequest.setTimeStamp(new Date());
			pddmSubscriptionStatusRequest.setTransactionId(nxUDFDetailsFileAuditStatus.getTransactionId());

			if (!nxUDFDetailsFileAuditStatus.getStatusType().equalsIgnoreCase(CommonConstants.SUCCESS)) {
				Message reasonMsg = MessageResourcesUtil.getMessageMapping().get(reasonCode);
				pddmSubscriptionStatusRequest.setReason(reasonMsg.getDescription());
				pddmSubscriptionStatusRequest.setReasonCode(reasonCode);
			}
			// DME2 call to PDDM for status
			try {
				callingPddmSubscriptionStatus(pddmSubscriptionStatusRequest);
			} catch (IOException e) {
				log.error("Exception occured during PDDM STATUS CALL", e);
				throw new SalesBusinessException(e.getMessage());
				
			}
		}

	}

	/**
	 * Calling pddm subscription status.
	 *
	 * @param request the request
	 * @return the pddm subscription status response
	 * @throws SalesBusinessException the sales business exception
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public PddmSubscriptionStatusResponse callingPddmSubscriptionStatus(PddmSubscriptionStatusRequest request)
			throws SalesBusinessException, IOException {

		log.info("Inside callingPddmSubscriptionStatus method");

		Map<String, Object> requestMap = new HashMap<>();
		requestMap.put("JSON", request);

		Map<String, String> requestHeaders = new HashMap<>();
		requestHeaders.put("Accept", "application/json");

		String uri = env.getProperty("productRules.uri");
		String subContext = env.getProperty("pddmStatus.subContext");
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

		PddmSubscriptionStatusResponse responseObj = mapper.readValue(response, PddmSubscriptionStatusResponse.class);
		log.info("The response is {}", response);

		return responseObj;
	}

}
