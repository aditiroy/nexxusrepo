package com.att.sales.nexxus.rest.handlers;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
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
import com.att.sales.nexxus.myprice.transaction.service.ConfigAndUpdateRestUtilInr;
import com.att.sales.nexxus.myprice.transaction.service.ConfigAndUpdateRestUtilPd;
import com.att.sales.nexxus.myprice.transaction.service.MyPriceTransactionUtil;
import com.att.sales.nexxus.myprice.transaction.service.RestCommonUtil;
import com.att.sales.nexxus.util.NexxusJsonUtility;
import com.att.sales.nexxus.util.RestClientUtil;

@ExtendWith(MockitoExtension.class)
public class ConfigDesignUpdateRestHandlerTest {
	
	@Spy
	@InjectMocks
	private ConfigDesignUpdateRestHandler configDesignUpdateRestHandler;
	
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
	
	@Mock
	private ConfigAndUpdateRestUtilInr configAndUpdateRestUtilInr;
	
	@Mock
	private ConfigAndUpdateRestUtilPd configAndUpdateRestUtilPd;
	
	@Mock
	private RestCommonUtil restCommonUtil;
	

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
		when(((ConfigRestHandler)configDesignUpdateRestHandler).getCustomJsonProcessingUtil().createJsonString(any(),any())).thenReturn(s);
		configDesignUpdateRestHandler.process(inputParamMap, inputDesign);
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
		when(((ConfigRestHandler)configDesignUpdateRestHandler).getCustomJsonProcessingUtil().createJsonString(any(),any())).thenReturn(s);
		doThrow(new SalesBusinessException("")).doNothing().when(((ConfigRestHandler)configDesignUpdateRestHandler)).triggerRestClient(any(), any(), any(), any(), any());
		configDesignUpdateRestHandler.process(inputParamMap, inputDesign);
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
		when(((ConfigRestHandler)configDesignUpdateRestHandler).getCustomJsonProcessingUtil().createJsonString(any(),any())).thenReturn(s);
		configDesignUpdateRestHandler.process(null, inputDesign);
	}
	
	@Test
	public void initTest() {
		configDesignUpdateRestHandler.init();
	}
	
	@Test
	public void processConfigReqDataFromCustomRulesTest() {
		Map<String, Object> inputParamMap=new HashMap<String, Object>();
		inputParamMap.put(MyPriceConstants.PRODUCT_TYPE, MyPriceConstants.SOURCE_INR);
		String inputDesign="{}";
		configDesignUpdateRestHandler.processConfigReqDataFromCustomRules(inputParamMap, inputDesign);
	}
	
	@Test
	public void processConfigDesignUpdateResponseTest() {
		Map<String, Object> inputParamMap=new HashMap<String, Object>();
		inputParamMap.put(MyPriceConstants.PRODUCT_TYPE, MyPriceConstants.SOURCE_INR);
		inputParamMap.put(MyPriceConstants.OFFER_NAME, MyPriceConstants.AVPN);
		LinkedHashMap<String,String> errorMessageMap=new LinkedHashMap<String, String>();
		errorMessageMap.put("Default_Error", "Error");
		inputParamMap.put(CustomJsonConstants.CONFIG_ERROR_MSG, errorMessageMap);
		
		 Map<String, Object> responseMap=new HashMap<String, Object>();
		 responseMap.put(MyPriceConstants.RESPONSE_DATA,"{\r\n" + 
		 		"    \"cacheInstanceId\": \"2HcDryT95YbyuGAnqo19h6dvCL2Wx4EQ6s4wr523fDxfHkk5r7WqYFHa8JQBtxdl~211882285\",\r\n" + 
		 		"    \"configData\": {\r\n" + 
		 		"	\"siteConfigurationError_pf\": \"[{\\\"nxSiteId\\\":\"10003\",\"Error\":\\\"POPCLLIName DUMMY NAME is not valid\\\"}]\",\r\n" + 
		 		"	\"part_custom_field3\": \"\"\r\n" + 
		 		"	}\r\n" + 
		 		"}	");
		 responseMap.put(MyPriceConstants.RESPONSE_STATUS,true);
		
		String inputDesign="{}";
		Object s="[{\"nxSiteId\":\"10003\",\"Error\":\"POPCLLIName DUMMY NAME is not valid\"},{\"nxSiteId\":\"234\",\"Error\":\"terer valid\"}]";
		when(((ConfigRestHandler)configDesignUpdateRestHandler).getNexxusJsonUtility().getValue(any(),any())).thenReturn(s);
		configDesignUpdateRestHandler.processConfigDesignUpdateResponse(inputParamMap, responseMap, inputDesign);
	}
	
	@Test
	public void processConfigDesignUpdateResponseTest2() {
		Map<String, Object> inputParamMap=new HashMap<String, Object>();
		inputParamMap.put(MyPriceConstants.PRODUCT_TYPE, MyPriceConstants.SOURCE_INR);
		inputParamMap.put(MyPriceConstants.OFFER_NAME, MyPriceConstants.AVPN);
		inputParamMap.put(CustomJsonConstants.ELEMENT_TYPE, new HashSet<String>(Arrays.asList("Port")));
		
		LinkedHashMap<String,String> errorMessageMap=new LinkedHashMap<String, String>();
		errorMessageMap.put("Default_Error", "Error");
		inputParamMap.put(CustomJsonConstants.CONFIG_ERROR_MSG, errorMessageMap);
		
		 Map<String, Object> responseMap=new HashMap<String, Object>();
		 responseMap.put(MyPriceConstants.RESPONSE_DATA,"{\r\n" + 
		 		"    \"cacheInstanceId\": \"2HcDryT95YbyuGAnqo19h6dvCL2Wx4EQ6s4wr523fDxfHkk5r7WqYFHa8JQBtxdl~211882285\",\r\n" + 
		 		"    \"configData\": {\r\n" + 
		 		"	\"siteConfigurationError_pf\": \"[{\\\"nxSiteId\\\":\"10003\",\"Error\":\\\"POPCLLIName DUMMY NAME is not valid\\\"}]\",\r\n" + 
		 		"	\"part_custom_field3\": \"\"\r\n" + 
		 		"	}\r\n" + 
		 		"}	");
		 responseMap.put(MyPriceConstants.RESPONSE_STATUS,true);
		
		String inputDesign="{}";
		Object s="[{\"nxSiteId\":\"10003\",\"Error\":\"POPCLLIName DUMMY NAME is not valid\"},{\"nxSiteId\":\"234\",\"Error\":\"terer valid\"}]";
		when(((ConfigRestHandler)configDesignUpdateRestHandler).getNexxusJsonUtility().getValue(any(),any())).thenReturn(s);
		configDesignUpdateRestHandler.processConfigDesignUpdateResponse(inputParamMap, responseMap, inputDesign);
	}
	
	@Test
	public void processConfigDesignUpdateResponseTest3() {
		Map<String, Object> inputParamMap=new HashMap<String, Object>();
		inputParamMap.put(MyPriceConstants.PRODUCT_TYPE, MyPriceConstants.SOURCE_INR);
		inputParamMap.put(MyPriceConstants.OFFER_NAME, "EPLSWAN");
		LinkedHashMap<String,String> errorMessageMap=new LinkedHashMap<String, String>();
		errorMessageMap.put("Default_Error", "Error");
		inputParamMap.put(CustomJsonConstants.CONFIG_ERROR_MSG, errorMessageMap);
		
		 Map<String, Object> responseMap=new HashMap<String, Object>();
		 responseMap.put(MyPriceConstants.RESPONSE_DATA,"{\r\n" + 
		 		"    \"cacheInstanceId\": \"2HcDryT95YbyuGAnqo19h6dvCL2Wx4EQ6s4wr523fDxfHkk5r7WqYFHa8JQBtxdl~211882285\",\r\n" + 
		 		"    \"configData\": {\r\n" + 
		 		"	\"items\": [{\r\n" + 
		 		"	\"part_custom_field3\": \"ABC\"\r\n" + 
		 		"	}],\r\n" + 
		 		"	\"siteConfigurationError_pf\": \"[{\\\"nxSiteId\\\":\"10003\",\"Error\":\\\"POPCLLIName DUMMY NAME is not valid\\\"}]\",\r\n" + 
		 		"	\r\n" + 
		 		"	}\r\n" + 
		 		"}	");
		 responseMap.put(MyPriceConstants.RESPONSE_STATUS,true);
		
		String inputDesign="{}";
		Object s="[{\"nxSiteId\":\"10003\",\"Error\":\"POPCLLIName DUMMY NAME is not valid\"},{\"nxSiteId\":\"234\",\"Error\":\"terer valid\"}]";
		when(((ConfigRestHandler)configDesignUpdateRestHandler).getNexxusJsonUtility().getValue(any(),any())).thenReturn(s);
		configDesignUpdateRestHandler.processConfigDesignUpdateResponse(inputParamMap, responseMap, inputDesign);
	}
	
	@Test
	public void processConfigDesignUpdateResponseTest4() {
		Map<String, Object> inputParamMap=new HashMap<String, Object>();
		inputParamMap.put(MyPriceConstants.PRODUCT_TYPE, MyPriceConstants.SOURCE_INR);
		inputParamMap.put(MyPriceConstants.OFFER_NAME, "EPLSWAN");
		LinkedHashMap<String,String> errorMessageMap=new LinkedHashMap<String, String>();
		errorMessageMap.put("Default_Error", "Error");
		inputParamMap.put(CustomJsonConstants.CONFIG_ERROR_MSG, errorMessageMap);
		
		 Map<String, Object> responseMap=new HashMap<String, Object>();
		 responseMap.put(MyPriceConstants.RESPONSE_DATA,"{\r\n" + 
		 		"    \"cacheInstanceId\": \"2HcDryT95YbyuGAnqo19h6dvCL2Wx4EQ6s4wr523fDxfHkk5r7WqYFHa8JQBtxdl~211882285\",\r\n" + 
		 		"    \"configData\": {\r\n" + 
		 		"	\"items\": [{\r\n" + 
		 		"	\"part_custom_field3\": \"ABC\"\r\n" + 
		 		"	}],\r\n" + 
		 		"	\"siteConfigurationError_pf\": \"[{\\\"nxSiteId\\\":\"10003\",\"Error\":\\\"POPCLLIName DUMMY NAME is not valid\\\"}]\",\r\n" + 
		 		"	\r\n" + 
		 		"	}\r\n" + 
		 		"}	");
		 responseMap.put(MyPriceConstants.RESPONSE_STATUS,true);
		
		String inputDesign="{}";
		Object s="[{\"nxSiteId\":\"10003\",\"Error\":\"POPCLLIName DUMMY NAME is not valid\"},{\"nxSiteId\":\"234\",\"Error\":\"terer valid\"}]";
		when(((ConfigRestHandler)configDesignUpdateRestHandler).getNexxusJsonUtility().getValue(any(),any())).thenReturn(s);
		List<Object> dd=new ArrayList<Object>();
		dd.add("sss");
		when(((ConfigRestHandler)configDesignUpdateRestHandler).getNexxusJsonUtility().getValueLst(any(),any())).thenReturn(dd);
		configDesignUpdateRestHandler.processConfigDesignUpdateResponse(inputParamMap, responseMap, inputDesign);
	}
	
	@Test
	public void processConfigDesignUpdateResponseTest5() {
		Map<String, Object> inputParamMap=new HashMap<String, Object>();
		inputParamMap.put(MyPriceConstants.PRODUCT_TYPE, MyPriceConstants.SOURCE_PD);
		inputParamMap.put(MyPriceConstants.OFFER_NAME, MyPriceConstants.ADE_OFFER_NAME);
		LinkedHashMap<String,String> errorMessageMap=new LinkedHashMap<String, String>();
		errorMessageMap.put("Default_Error", "Error");
		inputParamMap.put(CustomJsonConstants.CONFIG_ERROR_MSG, errorMessageMap);
		
		 Map<String, Object> responseMap=new HashMap<String, Object>();
		 responseMap.put(MyPriceConstants.RESPONSE_DATA,"{\r\n" + 
		 		"    \"cacheInstanceId\": \"2HcDryT95YbyuGAnqo19h6dvCL2Wx4EQ6s4wr523fDxfHkk5r7WqYFHa8JQBtxdl~211882285\",\r\n" + 
		 		"    \"configData\": {\r\n" + 
		 		"	\"siteConfigurationError_pf\": \"[{\\\"nxSiteId\\\":\"10003\",\"Error\":\\\"POPCLLIName DUMMY NAME is not valid\\\"}]\",\r\n" + 
		 		"	\"part_custom_field3\": \"\"\r\n" + 
		 		"	}\r\n" + 
		 		"}	");
		 responseMap.put(MyPriceConstants.RESPONSE_STATUS,true);
		
		String inputDesign="{}";
		Object s="[{\"nxSiteId\":\"10003\",\"Error\":\"POPCLLIName DUMMY NAME is not valid\"},{\"nxSiteId\":\"234\",\"Error\":\"terer valid\"}]";
		when(((ConfigRestHandler)configDesignUpdateRestHandler).getNexxusJsonUtility().getValue(any(),any())).thenReturn(s);
		configDesignUpdateRestHandler.processConfigDesignUpdateResponse(inputParamMap, responseMap, inputDesign);
	}
	
	@Test
	public void compareReqAndRespElementTypeTest() {
		boolean t=true;
		when(((ConfigRestHandler)configDesignUpdateRestHandler).getRestCommonUtil().listEqualsIgnoreOrder(any(),any())).thenReturn(t);
		configDesignUpdateRestHandler.compareReqAndRespElementType(new HashSet<String>(Arrays.asList("a")), 
				new HashSet<String>(Arrays.asList("a")));
	}
	

}
