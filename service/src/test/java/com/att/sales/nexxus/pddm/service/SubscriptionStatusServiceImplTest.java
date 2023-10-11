package com.att.sales.nexxus.pddm.service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.env.Environment;

import com.att.sales.framework.model.ServiceMetaData;
import com.att.sales.nexxus.dao.repository.NxPddmFileAuditStatusRepository;
import com.att.sales.nexxus.model.PddmSubscriptionStatusRequest;
import com.att.sales.nexxus.util.DME2RestClient;
import com.att.sales.pddm.dao.NxUDFDetailsFileAuditStatus;
import com.fasterxml.jackson.databind.ObjectMapper;

@ExtendWith(MockitoExtension.class)
public class SubscriptionStatusServiceImplTest {

	@InjectMocks
	SubscriptionStatusServiceImpl test;
	
	@Mock
	private DME2RestClient dmeClient;
	
	@Mock
	private Environment env;
	
	@Mock
	ObjectMapper mapper;
	
	@Mock
	PddmSubscriptionStatusRequest pddmSubscriptionStatusRequest;

	@Mock
	NxPddmFileAuditStatusRepository nxPddmFileAuditStatusRepo;
	
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
	public void test() {
		try {
			BigDecimal fileId = new BigDecimal(111);
			NxUDFDetailsFileAuditStatus value = new NxUDFDetailsFileAuditStatus();
			value.setFileId(fileId);
			value.setStatusType("Failure");
			Mockito.when(nxPddmFileAuditStatusRepo.findByFileId(fileId)).thenReturn(value);
			
			String reasonCode="WF.ERR.001";
			test.setPddmSubscriptionStatus(fileId, reasonCode);
   		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			test.callingPddmSubscriptionStatus(pddmSubscriptionStatusRequest);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	

}
