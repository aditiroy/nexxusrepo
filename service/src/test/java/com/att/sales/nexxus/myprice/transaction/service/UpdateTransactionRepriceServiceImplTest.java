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
import com.att.sales.nexxus.util.RestClientUtil;

@ExtendWith(MockitoExtension.class)

public class UpdateTransactionRepriceServiceImplTest {
	
	@InjectMocks
	UpdateTransactionRepriceServiceImpl updateTransactionRepriceServiceImpl;
	
	@Mock
	private Environment env;
	
	@Mock
	private RestClientUtil restClient;

	@Mock
	private HttpRestClient httpRest;
	
	@Test
	public void testUpdateTransactionRepriceService() {
		LinkedHashMap<String, Object> requestMap = new LinkedHashMap<String, Object>();
		requestMap.put(StringConstants.MY_PRICE_TRANS_ID, "1111111");
		try {
			Mockito.when(env.getProperty("myprice.updateTransactionReprice")).thenReturn("https://custompricingst2.att.com/rest/v7/commerceDocumentsOraclecpqo_bmClone_2Transaction/{transactionId}/actions/reprice_q");
			String transResponse = new Object().toString();
			Mockito.when(httpRest.callHttpRestClient(anyString(), any(), anyMap(), anyString(), anyMap(), anyString()))
			.thenReturn(transResponse);
			Mockito.when(restClient.processResult(anyString(), any()))
			.thenReturn("test");
			updateTransactionRepriceServiceImpl.updateTransactionRepriceService(requestMap);
		} catch (SalesBusinessException e) {
			// TODO Auto-generated catch block
			e.getMessage();
		}
	}
	
	@Test
	public void testException() {
		try {
			LinkedHashMap<String, Object> requestMap = new LinkedHashMap<String, Object>();
			requestMap.put(StringConstants.MY_PRICE_TRANS_ID, "1111111");
			Mockito.when(env.getProperty("myprice.updateTransactionReprice")).thenReturn("https://custompricingst2.att.com/rest/v7/commerceDocumentsOraclecpqo_bmClone_2Transaction/{transactionId}/actions/reprice_q");
			Mockito.when(httpRest.callHttpRestClient(anyString(), any(), anyMap(), anyString(), anyMap(), anyString()))
			.thenThrow(SalesBusinessException.class);
			updateTransactionRepriceServiceImpl.updateTransactionRepriceService(requestMap);
		} catch (SalesBusinessException e) {
			// TODO Auto-generated catch block
			e.getMessage();
		}
	}


}
