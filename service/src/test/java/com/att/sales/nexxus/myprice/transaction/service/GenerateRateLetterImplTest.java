package com.att.sales.nexxus.myprice.transaction.service;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyMap;
import static org.mockito.Mockito.anyString;

import java.util.LinkedHashMap;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.env.Environment;

import com.att.sales.framework.exception.SalesBusinessException;
import com.att.sales.nexxus.constant.StringConstants;
import com.att.sales.nexxus.util.HttpRestClient;

@ExtendWith(MockitoExtension.class)

public class GenerateRateLetterImplTest {

	@InjectMocks
	GenerateRateLetterImpl generateRateLetterImpl;

	@Mock
	Environment env;
	
	@Mock
	HttpRestClient httpRest;
	
	@Test
	public void generateRateLetter() throws SalesBusinessException {
		try {
			String uri = "https://custompricingst2.att.com/rest/v7/commerceDocumentsOraclecpqo_bmClone_2Transaction/{transactionId}/actions/approveRateLetter_q";
			Mockito.when(env.getProperty("myprice.generateRateLetter")).thenReturn(uri);
			Mockito.when(env.getProperty("myprice.username")).thenReturn("");
			Mockito.when(env.getProperty("myprice.password")).thenReturn("");
			String glResponse = "{}";
			LinkedHashMap<String, Object> requestMap = new LinkedHashMap<>();
			requestMap.put(StringConstants.MY_PRICE_TRANS_ID, "12345");
			Mockito.when(httpRest.callHttpRestClient(anyString(), any(), anyMap(), anyString(), anyMap(), anyString())).thenReturn(glResponse);

			//ServiceResponse response = new ServiceResponse();
			//Mockito.when(restClient.processResult(approveRLResponse, ServiceResponse.class)).thenReturn(response);
			generateRateLetterImpl.generateRateLetter(requestMap);
		} catch (Exception e) {
			e.getMessage();
		}
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void testException() throws SalesBusinessException {
		try {
			String uri = "https://custompricingdev2.att.com/rest/v7/commerceDocumentsOraclecpqo_bmClone_2Transaction/{transactionId}/actions/rl_wlAutoApprovalRateLetter_q";
			Mockito.when(env.getProperty("myprice.generateRateLetter")).thenReturn(uri);
			Mockito.when(env.getProperty("myprice.username")).thenReturn("");
			Mockito.when(env.getProperty("myprice.password")).thenReturn("");
			LinkedHashMap<String, Object> requestMap = new LinkedHashMap<>();
			requestMap.put(StringConstants.MY_PRICE_TRANS_ID, "12345");
			Mockito.when(httpRest.callHttpRestClient(anyString(), any(), anyMap(), anyString(), anyMap(), anyString())).thenThrow(SalesBusinessException.class);

			//ServiceResponse response = new ServiceResponse();
			//Mockito.when(restClient.processResult(approveRLResponse, ServiceResponse.class)).thenReturn(response);
			generateRateLetterImpl.generateRateLetter(requestMap);
		} catch (Exception e) {
			e.getMessage();
		}
	}

}
