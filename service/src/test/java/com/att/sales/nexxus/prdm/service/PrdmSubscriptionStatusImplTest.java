package com.att.sales.nexxus.prdm.service;


import java.io.IOException;
import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.junit.Before;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.env.Environment;

import com.att.sales.framework.exception.SalesBusinessException;
import com.att.sales.framework.model.Message;
import com.att.sales.framework.model.ServiceMetaData;
import com.att.sales.nexxus.common.CommonConstants;
import com.att.sales.nexxus.dao.repository.NxPrdmFileAuditStatusRepository;
import com.att.sales.nexxus.model.PrdmSubscriptionStatusRequest;
import com.att.sales.nexxus.util.DME2RestClient;
import com.att.sales.prdm.dao.NxRatePlanFileAuditData;
import com.fasterxml.jackson.databind.ObjectMapper;

@ExtendWith(MockitoExtension.class)
public class PrdmSubscriptionStatusImplTest {


	@InjectMocks
	PrdmSubscriptionStatusImpl prdmSubscriptionStatusImpl;
	
	@Mock
	private DME2RestClient dmeClient;
	
	@Mock
	private Environment env;
	
	@Mock
	ObjectMapper mapper;
	
	@Mock
	PrdmSubscriptionStatusRequest prdmSubscriptionStatusRequest;
	
	@Mock
	NxPrdmFileAuditStatusRepository nxPrdmFileAuditStatusRepository;
	
	@BeforeEach
	public void initializeServiceMetaData() {
		Map<String, Object> requestParams = new HashMap<>();
		requestParams.put(ServiceMetaData.OFFER, "AVPN");
		requestParams.put(ServiceMetaData.REST_REQUEST_START_TIME, System.currentTimeMillis());
		requestParams.put(ServiceMetaData.VERSION, "1.0");
		requestParams.put(ServiceMetaData.METHOD, "TestMethod");
		requestParams.put(ServiceMetaData.URI, "Testuri");
		ServiceMetaData.add(requestParams);
	}


	@Test
	public void test() throws SalesBusinessException, IOException {
		try {
			BigDecimal fileId = new BigDecimal(111);
			NxRatePlanFileAuditData value = new NxRatePlanFileAuditData();
			value.setFileId(fileId);
			value.setStatusType("Failure");
			Mockito.when(nxPrdmFileAuditStatusRepository.findByFileId(fileId)).thenReturn(value);
			
			String domainObject = null;
			String client = "nexxus";
			String msApplication = "nexxus";
			BigDecimal salesPddmId = null;

			String reasonCode="WF.ERR.001";
			prdmSubscriptionStatusRequest.setClient(client);
			prdmSubscriptionStatusRequest.setDomainObject(domainObject);
			prdmSubscriptionStatusRequest.setMsApplication(msApplication);
			prdmSubscriptionStatusRequest.setSalesPddmId(salesPddmId);
			prdmSubscriptionStatusRequest.setStatus(value.getStatus());
			prdmSubscriptionStatusRequest.setStatusType(value.getStatusType());
			prdmSubscriptionStatusRequest.setTimeStamp(new Date());
			prdmSubscriptionStatusRequest.setTransactionId(value.getTransactionId());
			prdmSubscriptionStatusRequest.setReasonCode(reasonCode);
			if (!value.getStatusType().equalsIgnoreCase(CommonConstants.SUCCESS)) {
				Message reasonMsg = new Message();
				prdmSubscriptionStatusRequest.setReason(reasonMsg.getDescription());
			}
			prdmSubscriptionStatusImpl.setPrdmSubscriptionStatus(fileId, reasonCode);
   		} catch (Exception e) {
			e.printStackTrace();
		}
		prdmSubscriptionStatusImpl.callingPrdmSubscriptionStatus(prdmSubscriptionStatusRequest);
		
	}
	

}
