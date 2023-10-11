package com.att.sales.nexxus.pddm.service;

import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.junit.jupiter.MockitoExtension;

import com.att.sales.framework.model.ServiceMetaData;
import com.att.sales.nexxus.dao.repository.NxPddmFileAuditStatusRepository;

@ExtendWith(MockitoExtension.class)
public class UDFAuditDataServiceImplTest {
	
	@InjectMocks
	UDFAuditDataServiceImpl test;
	
	@Mock
	NxPddmFileAuditStatusRepository nxPddmFileAuditStatusRepository;
	
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
		test.setAuditData("zipFileName", "status", "statusType");
	
	}

}
