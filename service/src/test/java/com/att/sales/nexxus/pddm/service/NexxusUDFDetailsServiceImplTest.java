package com.att.sales.nexxus.pddm.service;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

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

import com.att.sales.framework.exception.SalesBusinessException;
import com.att.sales.framework.model.ServiceMetaData;
import com.att.sales.nexxus.common.CommonConstants;
import com.att.sales.nexxus.constant.MessageConstants;
import com.att.sales.nexxus.helper.FileReaderHelper;
import com.att.sales.nexxus.model.ProductDataLoadRequest;

@ExtendWith(MockitoExtension.class)
public class NexxusUDFDetailsServiceImplTest {

	@Mock
	public FileReaderHelper fileReaderHelper;
	
	@Mock
	public SubscriptionStatusServiceImpl subscriptionStatusServiceImpl;
	
	@Mock
	public UDFAuditDataServiceImpl auditDataServiceImpl;
	
	@InjectMocks
	NexxusUDFDetailsServiceImpl test;

	@Mock
	ProductDataLoadRequest productDataLoadRequest;
	
	@Mock
	Environment env;

	Map<String, Object> requestParams = new HashMap<>();

	@BeforeEach
	public void initializeServiceMetaData() {
		requestParams.put(ServiceMetaData.OFFER, "AVPN");
		requestParams.put(ServiceMetaData.REST_REQUEST_START_TIME, System.currentTimeMillis());
		requestParams.put(ServiceMetaData.VERSION, "1.0");
		requestParams.put(ServiceMetaData.METHOD, "TestMethod");
		requestParams.put(ServiceMetaData.URI, "Testuri");
		ServiceMetaData.add(requestParams);
	}
	
	@Test
	public void test() throws SalesBusinessException {
		requestParams.put(CommonConstants.FILENAME, "TEST_FILE");
		requestParams.put("TransactionId", "13453");
		ServiceMetaData.add(requestParams);
		test.putNexxusUDFDetails(productDataLoadRequest);

		test.loadDataFromArchToWrkTables();

		String message = CommonConstants.ROLLBACK;
		test.loadDataFromWorkingToActualTables(message);

		message = CommonConstants.ACTIVATE;
    	when(env.getProperty(Mockito.anyString())).thenReturn("envProperty");
    	test.loadDataFromWorkingToActualTables(message);
}

	@Test
	public void testPutNexxusUDFDetailsNegative() throws SalesBusinessException {
		requestParams.put(CommonConstants.FILENAME, "testfile");
		ServiceMetaData.add(requestParams);
		test.putNexxusUDFDetails(productDataLoadRequest);
	}
	@Test
	public void testLoadDataFromArchToWrkTablesNegative() throws SalesBusinessException {
		doThrow(new SalesBusinessException()).when(fileReaderHelper).dataProcessing(Mockito.anyString());
		String reasonCode=test.loadDataFromArchToWrkTables();
		assertEquals(MessageConstants.FILE_PROCESSING_DELAYED, reasonCode);
	}
	
	@Test
	public void testloadDataFromWorkingToActualTablesNegative() throws SalesBusinessException {
		doThrow(new SalesBusinessException()).when(fileReaderHelper).dataRollBack(Mockito.anyString());
		String message = CommonConstants.ROLLBACK;
		test.loadDataFromWorkingToActualTables(message);
	}


}
