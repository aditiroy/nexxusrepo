package com.att.sales.nexxus.rest.handlers;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.junit.jupiter.MockitoExtension;

import com.att.sales.framework.exception.SalesBusinessException;
import com.att.sales.nexxus.constant.CustomJsonConstants;
import com.att.sales.nexxus.constant.MyPriceConstants;
import com.att.sales.nexxus.dao.repository.NxLookupDataRepository;
import com.att.sales.nexxus.myprice.transaction.service.MyPriceTransactionUtil;
import com.att.sales.nexxus.util.NexxusJsonUtility;
import com.att.sales.nexxus.util.RestClientUtil;

@ExtendWith(MockitoExtension.class)
public class ConfigSolutionRestHandlerTest {
	
	@Spy
	@InjectMocks
	private ConfigSolutionRestHandler configSolutionRestHandler;
	
	@Mock
	private RestClientUtil restClient;
	
	@Mock
	private MyPriceTransactionUtil myPriceTransactionUtil;
	
	@Mock
	private CustomJsonProcessingUtil customJsonProcessingUtil;
	
	@Mock
	private NxLookupDataRepository nxLookupDataRepository;
	
	@Mock
	private NexxusJsonUtility nexxusJsonUtility;
	
	
	@Test
	public void processTest() {
		Map<String, Object> inputParamMap=new HashMap<String, Object>();
		inputParamMap.put(MyPriceConstants.PRODUCT_TYPE, MyPriceConstants.SOURCE_PD);
		String inputDesign="{}";
		inputParamMap.put(CustomJsonConstants.IS_CRITERIA_REQUIRED, true);
		inputParamMap.put(MyPriceConstants.OFFER_NAME, "AVPN");
		inputParamMap.put(CustomJsonConstants.BS_ID, 1234);
		inputParamMap.put(CustomJsonConstants.DOCUMENT_ID, 67776);
		Map<String, Object> responseMap=new HashMap<String, Object>();
		responseMap.put(MyPriceConstants.RESPONSE_STATUS, true);
		String s="{}";
		when(((ConfigRestHandler)configSolutionRestHandler).getCustomJsonProcessingUtil().createJsonString(any(),any())).thenReturn(s);
		configSolutionRestHandler.process(inputParamMap, inputDesign);
	}
	
	@Test
	public void processTestError() throws SalesBusinessException {
		Map<String, Object> inputParamMap=new HashMap<String, Object>();
		inputParamMap.put(MyPriceConstants.PRODUCT_TYPE, MyPriceConstants.SOURCE_PD);
		String inputDesign="{}";
		inputParamMap.put(CustomJsonConstants.IS_CRITERIA_REQUIRED, true);
		inputParamMap.put(MyPriceConstants.OFFER_NAME, "AVPN");
		inputParamMap.put(CustomJsonConstants.BS_ID, 1234);
		inputParamMap.put(CustomJsonConstants.DOCUMENT_ID, 67776);
		Map<String, Object> responseMap=new HashMap<String, Object>();
		responseMap.put(MyPriceConstants.RESPONSE_STATUS, true);
		String s="{}";
		when(((ConfigRestHandler)configSolutionRestHandler).getCustomJsonProcessingUtil().createJsonString(any(),any())).thenReturn(s);
		doThrow(new SalesBusinessException("")).doNothing().when(((ConfigRestHandler)configSolutionRestHandler)).triggerRestClient(any(), any(), any(), any(), any());
		configSolutionRestHandler.process(inputParamMap, inputDesign);
	}
	
	@Test
	public void processTestError1() throws SalesBusinessException  {
		Map<String, Object> inputParamMap=new HashMap<String, Object>();
		inputParamMap.put(MyPriceConstants.PRODUCT_TYPE, MyPriceConstants.SOURCE_PD);
		String inputDesign="{}";
		inputParamMap.put(CustomJsonConstants.IS_CRITERIA_REQUIRED, true);
		inputParamMap.put(MyPriceConstants.OFFER_NAME, "AVPN");
		inputParamMap.put(CustomJsonConstants.BS_ID, 1234);
		inputParamMap.put(CustomJsonConstants.DOCUMENT_ID, 67776);
		Map<String, Object> responseMap=new HashMap<String, Object>();
		responseMap.put(MyPriceConstants.RESPONSE_STATUS, true);
		String s="{}";
		when(((ConfigRestHandler)configSolutionRestHandler).getCustomJsonProcessingUtil().createJsonString(any(),any())).thenReturn(s);
		configSolutionRestHandler.process(null, inputDesign);
	}
	
	@Test
	public void initTest() {
		configSolutionRestHandler.init();
	}

}
