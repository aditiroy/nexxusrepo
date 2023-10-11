package com.att.sales.nexxus.util;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.Map;

import org.json.simple.JSONObject;
import org.junit.Ignore;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.env.Environment;

import com.att.sales.framework.exception.SalesBusinessException;
//import com.att.sales.framework.util.DME2Utility;
import com.att.sales.nexxus.edf.model.ManageBillDataInv;
import com.att.sales.nexxus.edf.model.ValidateAccountDataRequest;
import com.att.sales.nexxus.reteriveicb.model.RetreiveICBPSPRequest;
import com.att.sales.nexxus.serviceValidation.model.AddressValidationServiceQualificationRequest;
import com.fasterxml.jackson.databind.ObjectMapper;

@ExtendWith(MockitoExtension.class)
public class DME2RestClientTest {
	@Spy
	@InjectMocks
	DME2RestClient dme2RestClient;
	@Mock
	private ObjectMapper mapper;
	/*@Mock
	private DME2Utility wrapper;*/
	@Mock
	private Environment env;
	@Mock
	private RestClientUtil restClientUtil;
	
	@Mock
	private HttpRestClient httpClient;
	
	@Test
	public void callDme2ClientTest() throws Exception {
		String expected = "res";
		Map<String, String> requestHeaders = new HashMap<>();
		//when(wrapper.processRequest(any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), anyBoolean())).thenReturn(expected);
		String res = dme2RestClient.callDme2Client(null, null, null, null, null, null, null, null, requestHeaders);
		assertEquals(expected, res);
	}
	
	@Test
	public void callDme2ClientExceptionTest() throws Exception {
		Map<String, String> requestHeaders = new HashMap<>();
		//doThrow(Exception.class).when(wrapper).processRequest(any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), anyBoolean());
		dme2RestClient.callDme2Client(null, null, null, null, null, null, null, null, requestHeaders);
	}

	@Test
	public void callDme2Client1Test() throws Exception {
		String expected = "res";
		Map<String, String> requestHeaders = new HashMap<>();
		//when(wrapper.processRequest(any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), anyBoolean())).thenReturn(expected);
		String res = dme2RestClient.callDme2Client(null, null, null, null, null, null, null, null, requestHeaders, null, null);
		assertEquals(expected, res);
	}
	
	@Test
	public void callDme2Client1ExecptionTest() throws Exception {
		Map<String, String> requestHeaders = new HashMap<>();
		//doThrow(Exception.class).when(wrapper).processRequest(any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), anyBoolean());
		dme2RestClient.callDme2Client(null, null, null, null, null, null, null, null, requestHeaders, null, null);
	}
	/**
	@Test
	public void getPricingAccessTest() throws SalesBusinessException, JsonParseException, JsonMappingException, IOException {
		QuoteRequest quoteRequest = new QuoteRequest();
		doReturn("{}").when(dme2RestClient).callDme2Client(any(), any(), any(), any(), any(), any(), any(), any(), any());
		dme2RestClient.getPricingAccess(quoteRequest);
	}
	**/
	@Test
	public void getBillingPriceInventryUriTest() throws Exception {
		ManageBillDataInv manageBillDataInv = new ManageBillDataInv();
		doReturn("{}").when(httpClient).callHttpRestClient(any(),  any(), any(), any(), any(), any());
		dme2RestClient.getBillingPriceInventryUri(manageBillDataInv);
	}
	
	@Test
	public void getValidateAccontDataUriTest() throws Exception {
		ValidateAccountDataRequest validateAccountDataRequest = new ValidateAccountDataRequest();
		doReturn("{}").when(dme2RestClient).callDme2Client(any(), any(), any(), any(), any(), any(), any(), any(), any());
		dme2RestClient.getValidateAccontDataUri(validateAccountDataRequest);
	}
	
	@Disabled
	@Test
	public void getValidateAccontDataUriExceptionTest() throws Exception {
		//ValidateAccountDataRequest validateAccountDataRequest = new ValidateAccountDataRequest();
		//doThrow(Exception.class).when(dme2RestClient).callDme2Client(any(), any(), any(), any(), any(), any(), any(), any(), any());
		//dme2RestClient.getValidateAccontDataUri(validateAccountDataRequest);
	}
	/**
	@Test
	public void callMailNotificationDME2Test() throws SalesBusinessException {
		doReturn("{}").when(dme2RestClient).callDme2Client(any(), any(), any(), any(), any(), any(), any(), any(), any());
		dme2RestClient.callMailNotificationDME2("");
	}
    **/
	
	@Test
	public void callMailNotificationDME2ExceptionTest() throws SalesBusinessException {
		doThrow(Exception.class).when(dme2RestClient).callDme2Client(any(), any(), any(), any(), any(), any(), any(), any(), any());
		dme2RestClient.callMailNotificationDME2("");
	}
	/**
	@Test
	public void callOrchCustomPricingOrderFlowTest() throws SalesBusinessException {
		RetreiveICBPSPRequest request = new RetreiveICBPSPRequest();
		doReturn("{}").when(dme2RestClient).callDme2Client(any(), any(), any(), any(), any(), any(), any(), any(), any());
		dme2RestClient.callOrchCustomPricingOrderFlow(request, null);
	}
    **/
	
	@Test
	public void callOrchCustomPricingOrderFlowExceptionTest() throws SalesBusinessException {
		RetreiveICBPSPRequest request = new RetreiveICBPSPRequest();
		doThrow(Exception.class).when(dme2RestClient).callDme2Client(any(), any(), any(), any(), any(), any(), any(), any(), any());
		dme2RestClient.callOrchCustomPricingOrderFlow(request, null);
	}
	/**
	@Test
	public void callOrchCustomPricingOrderFlow1Test() throws SalesBusinessException {
		JSONObject request = new JSONObject();
		doReturn("{}").when(dme2RestClient).callDme2Client(any(), any(), any(), any(), any(), any(), any(), any(), any());
		dme2RestClient.callOrchCustomPricingOrderFlow(request, null);
	}
    **/
	
	@Test
	public void callOrchCustomPricingOrderFlow1ExceptionTest() throws SalesBusinessException {
		JSONObject request = new JSONObject();
		doThrow(Exception.class).when(dme2RestClient).callDme2Client(any(), any(), any(), any(), any(), any(), any(), any(), any());
		dme2RestClient.callOrchCustomPricingOrderFlow(request, null);
	}
	
	@Test
	public void callAVSQRequestTest() throws SalesBusinessException {
		AddressValidationServiceQualificationRequest request = new AddressValidationServiceQualificationRequest();
		doReturn("{}").when(dme2RestClient).callDme2Client(any(), any(), any(), any(), any(), any(), any(), any(), any());
		dme2RestClient.callAVSQRequest(request, new HashMap<>());
		
		doThrow(Exception.class).when(dme2RestClient).callDme2Client(any(), any(), any(), any(), any(), any(), any(), any(), any());
		dme2RestClient.callAVSQRequest(request, new HashMap<>());
	}
	
	@Test
	public void callIpeGetQualificationTest() throws SalesBusinessException {
		//Map<String, Object> queryParameters = new HashMap<>();
		//doReturn("{}").when(dme2RestClient).callDme2Client(any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any());
		//Map<String, Object> queryParameters = new HashMap<>();
		//Mockito.when(env.getProperty(anyString())).thenReturn("test");
		//doReturn("{}").when(restClientUtil).callMPRestClient(anyString(), anyString(), anyString(), anyMap(),anyMap());
		//dme2RestClient.callIpeGetQualification(queryParameters);
	}

}
