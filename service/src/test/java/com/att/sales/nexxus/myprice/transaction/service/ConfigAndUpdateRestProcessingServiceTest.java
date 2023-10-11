package com.att.sales.nexxus.myprice.transaction.service;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Qualifier;

import com.att.sales.framework.model.ServiceMetaData;
import com.att.sales.nexxus.constant.CustomJsonConstants;
import com.att.sales.nexxus.constant.InrConstants;
import com.att.sales.nexxus.constant.MyPriceConstants;
import com.att.sales.nexxus.rest.handlers.ConfigRestHandler;
import com.att.sales.nexxus.service.NxMyPriceRepositoryServce;

@ExtendWith(MockitoExtension.class)

public class ConfigAndUpdateRestProcessingServiceTest {
	
	@Spy
	@InjectMocks
	private ConfigAndUpdateRestProcessingService configAndUpdateRestProcessingService;
	
	
	@Mock
	@Qualifier("configSolutionRestHandler")
	private ConfigRestHandler configSolutionRestHandler;
	
	@Mock
	@Qualifier("configDesignUpdateRestHandler")
	private ConfigRestHandler configDesignUpdateRestHandler;
	
	@Mock
	@Qualifier("configInSystemRestHandler")
	private ConfigRestHandler configInSystemRestHandler;
	
	
	@Mock
	@Qualifier("configAddTransactionRestHandler")
	private ConfigRestHandler configAddTransactionRestHandler;
	
	@Mock
	private NxMyPriceRepositoryServce nxMyPriceRepositoryServce;
	
	@Mock
	private ConfigAndUpdateRestUtilPd configAndUpdateRestUtilPd;
	
	@BeforeAll
	public static void init() {
		Map<String, Object> map = new HashMap<>();
		map.put(ServiceMetaData.REST_REQUEST_START_TIME, System.currentTimeMillis());
		
		
		ServiceMetaData.add(map);
	}

	@AfterAll
	public static void afterClass() {
		ServiceMetaData.getThreadLocal().remove();
	}
	
	
	@Test
	public void callMpConfigAndUpdateTest() {
		Map<String, Object> requestMetaDataMap=new HashMap<String, Object>();
		Map<String, Object> inputParamMap=new HashMap<String, Object>();
		inputParamMap.put(InrConstants.REQUEST_META_DATA_KEY, requestMetaDataMap);
		inputParamMap.put(MyPriceConstants.PRODUCT_TYPE, MyPriceConstants.SOURCE_PD);
		String inputDesign="{}";
		inputParamMap.put(CustomJsonConstants.IS_CRITERIA_REQUIRED, true);
		inputParamMap.put(MyPriceConstants.OFFER_NAME, "AVPN");
		inputParamMap.put(CustomJsonConstants.BS_ID, 1234);
		inputParamMap.put(CustomJsonConstants.DOCUMENT_ID, 67776);
		Map<String, Object> responseMap=new HashMap<String, Object>();
		responseMap.put(MyPriceConstants.RESPONSE_STATUS, true);
		when(configSolutionRestHandler.process(any(),any())).thenReturn(responseMap);
		responseMap.put(MyPriceConstants.RESPONSE_STATUS, true);
		when(configDesignUpdateRestHandler.process(any(),any())).thenReturn(responseMap);
		responseMap.put(MyPriceConstants.RESPONSE_STATUS, true);
		when(configInSystemRestHandler.process(any(),any())).thenReturn(responseMap);
		responseMap.put(MyPriceConstants.RESPONSE_STATUS, true);
		when(configAddTransactionRestHandler.process(any(),any())).thenReturn(responseMap);
		configAndUpdateRestProcessingService.callMpConfigAndUpdate(inputParamMap, inputDesign);
	}
	
	@Test
	public void callMpConfigAndUpdateError1Test() {
		Map<String, Object> inputParamMap=new HashMap<String, Object>();
		String inputDesign="{}";
		inputParamMap.put(MyPriceConstants.PRODUCT_TYPE, "INR");
		inputParamMap.put(CustomJsonConstants.IS_CRITERIA_REQUIRED, true);
		inputParamMap.put(MyPriceConstants.OFFER_NAME, "AVPN");
		inputParamMap.put(CustomJsonConstants.BS_ID, 1234);
		inputParamMap.put(CustomJsonConstants.DOCUMENT_ID, 67776);
		Map<String, Object> responseMap=new HashMap<String, Object>();
		responseMap.put(MyPriceConstants.RESPONSE_STATUS, false);
		when(configSolutionRestHandler.process(any(),any())).thenReturn(responseMap);
		configAndUpdateRestProcessingService.callMpConfigAndUpdate(inputParamMap, inputDesign);
	}
	
	@Test
	public void callMpConfigAndUpdateError2Test() {
		Map<String, Object> inputParamMap=new HashMap<String, Object>();
		String inputDesign="{}";
		inputParamMap.put(MyPriceConstants.PRODUCT_TYPE, "INR");
		inputParamMap.put(CustomJsonConstants.IS_CRITERIA_REQUIRED, true);
		inputParamMap.put(MyPriceConstants.OFFER_NAME, "AVPN");
		inputParamMap.put(CustomJsonConstants.BS_ID, 1234);
		inputParamMap.put(CustomJsonConstants.DOCUMENT_ID, 67776);
		Map<String, Object> responseMap=new HashMap<String, Object>();
		responseMap.put(MyPriceConstants.RESPONSE_STATUS, true);
		when(configSolutionRestHandler.process(any(),any())).thenReturn(responseMap);
		responseMap.put(MyPriceConstants.RESPONSE_STATUS, true);
		when(configInSystemRestHandler.process(any(),any())).thenReturn(responseMap);
		Map<String, Object> responseMap1=new HashMap<String, Object>();
		responseMap1.put(MyPriceConstants.RESPONSE_STATUS, false);
		when(configDesignUpdateRestHandler.process(any(),any())).thenReturn(responseMap1);
		configAndUpdateRestProcessingService.callMpConfigAndUpdate(inputParamMap, inputDesign);
	}
	
	@Test
	public void callMpConfigAndUpdateError3Test() {
		Map<String, Object> inputParamMap=new HashMap<String, Object>();
		String inputDesign="{}";
		inputParamMap.put(MyPriceConstants.PRODUCT_TYPE, "INR");
		inputParamMap.put(CustomJsonConstants.IS_CRITERIA_REQUIRED, true);
		inputParamMap.put(MyPriceConstants.OFFER_NAME, "AVPN");
		inputParamMap.put(CustomJsonConstants.BS_ID, 1234);
		inputParamMap.put(CustomJsonConstants.DOCUMENT_ID, 67776);
		Map<String, Object> responseMap=new HashMap<String, Object>();
		responseMap.put(MyPriceConstants.RESPONSE_STATUS, true);
		when(configSolutionRestHandler.process(any(),any())).thenReturn(responseMap);
		Map<String, Object> responseMap1=new HashMap<String, Object>();
		responseMap1.put(MyPriceConstants.RESPONSE_STATUS, false);
		when(configInSystemRestHandler.process(any(),any())).thenReturn(responseMap1);
		configAndUpdateRestProcessingService.callMpConfigAndUpdate(inputParamMap, inputDesign);
	}
	
	@Test
	public void callMpConfigAndUpdateError4Test() {
		Map<String, Object> inputParamMap=new HashMap<String, Object>();
		String inputDesign="{}";
		inputParamMap.put(MyPriceConstants.PRODUCT_TYPE, "INR");
		inputParamMap.put(CustomJsonConstants.IS_CRITERIA_REQUIRED, true);
		inputParamMap.put(MyPriceConstants.OFFER_NAME, "AVPN");
		inputParamMap.put(CustomJsonConstants.BS_ID, 1234);
		inputParamMap.put(CustomJsonConstants.DOCUMENT_ID, 67776);
		Map<String, Object> responseMap=new HashMap<String, Object>();
		responseMap.put(MyPriceConstants.RESPONSE_STATUS, true);
		when(configSolutionRestHandler.process(any(),any())).thenReturn(responseMap);
		responseMap.put(MyPriceConstants.RESPONSE_STATUS, true);
		when(configDesignUpdateRestHandler.process(any(),any())).thenReturn(responseMap);
		responseMap.put(MyPriceConstants.RESPONSE_STATUS, true);
		when(configInSystemRestHandler.process(any(),any())).thenReturn(responseMap);
		responseMap.put(MyPriceConstants.RESPONSE_STATUS, true);
		when(configAddTransactionRestHandler.process(any(),any())).thenReturn(responseMap);
		Map<String, Object> responseMap1=new HashMap<String, Object>();
		responseMap1.put(MyPriceConstants.RESPONSE_STATUS, false);
		responseMap1.put(CustomJsonConstants.REST_RESPONSE_ERROR,new HashSet<String>(Arrays.asList("Error")));
		when(configAddTransactionRestHandler.process(any(),any())).thenReturn(responseMap1);
		configAndUpdateRestProcessingService.callMpConfigAndUpdate(inputParamMap, inputDesign);
	}

	@Test
	public void callMpConfigAndUpdateError5Test() {
		Map<String, Object> inputParamMap=new HashMap<String, Object>();
		String inputDesign="{}";
		inputParamMap.put(MyPriceConstants.PRODUCT_TYPE, "INR");
		inputParamMap.put(CustomJsonConstants.IS_CRITERIA_REQUIRED, true);
		inputParamMap.put(MyPriceConstants.OFFER_NAME, "AVPN");
		inputParamMap.put(CustomJsonConstants.BS_ID, 1234);
		inputParamMap.put(CustomJsonConstants.DOCUMENT_ID, 67776);
		Map<String, Object> responseMap=new HashMap<String, Object>();
		responseMap.put(MyPriceConstants.RESPONSE_STATUS, true);
		when(configSolutionRestHandler.process(any(),any())).thenReturn(responseMap);
		responseMap.put(MyPriceConstants.RESPONSE_STATUS, true);
		when(configInSystemRestHandler.process(any(),any())).thenReturn(responseMap);
		Map<String, Object> responseMap1=new HashMap<String, Object>();
		responseMap1.put(CustomJsonConstants.CONFIG_BOM_ERROR, true);
		responseMap1.put(MyPriceConstants.RESPONSE_STATUS, false);
		when(configDesignUpdateRestHandler.process(any(),any())).thenReturn(responseMap1);
		configAndUpdateRestProcessingService.callMpConfigAndUpdate(inputParamMap, inputDesign);
	}
	
	@Test
	public void callMpConfigAndUpdateError6Test() {
		Map<String, Object> inputParamMap=new HashMap<String, Object>();
		String inputDesign="{}";
		inputParamMap.put(MyPriceConstants.PRODUCT_TYPE, "INR");
		inputParamMap.put(CustomJsonConstants.IS_CRITERIA_REQUIRED, true);
		inputParamMap.put(MyPriceConstants.OFFER_NAME, "AVPN");
		inputParamMap.put(CustomJsonConstants.BS_ID, 1234);
		inputParamMap.put(CustomJsonConstants.DOCUMENT_ID, 67776);
		Map<String, Object> responseMap=new HashMap<String, Object>();
		responseMap.put(MyPriceConstants.RESPONSE_STATUS, true);
		when(configSolutionRestHandler.process(any(),any())).thenReturn(responseMap);
		responseMap.put(MyPriceConstants.RESPONSE_STATUS, true);
		when(configInSystemRestHandler.process(any(),any())).thenReturn(responseMap);
		Map<String, Object> responseMap1=new HashMap<String, Object>();
		responseMap1.put(CustomJsonConstants.SITE_CONFIG_ERROR, true);
		responseMap1.put(MyPriceConstants.RESPONSE_STATUS, false);
		when(configDesignUpdateRestHandler.process(any(),any())).thenReturn(responseMap1);
		configAndUpdateRestProcessingService.callMpConfigAndUpdate(inputParamMap, inputDesign);
	}
	
}
