package com.att.sales.nexxus.myprice.transaction.service;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyMap;
import static org.mockito.Mockito.anyString;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.env.Environment;

import com.att.sales.framework.exception.SalesBusinessException;
import com.att.sales.framework.model.ServiceMetaData;
import com.att.sales.nexxus.constant.StringConstants;
import com.att.sales.nexxus.util.HttpRestClient;

@ExtendWith(MockitoExtension.class)

public class UpdateTransactionPrintDocServiceImplTest {

	@Spy
	@InjectMocks
	private UpdateTransactionPrintDocServiceImpl updateTransactionPrintDocServiceImpl;

	@Mock
	Environment env;
	
	@Mock
	HttpRestClient httpRest;

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
	public void updateTransactionPrintDocServiceTest() throws SalesBusinessException {
		try {
			String uri = "https://custompricingst2.att.com/rest/v7/commerceDocumentsOraclecpqo_bmClone_2Transaction/{transactionId}/actions/_print_document";
			Mockito.when(env.getProperty("myprice.updateTransactionPrintDocument")).thenReturn(uri);
			Mockito.when(env.getProperty("myprice.username")).thenReturn("");
			Mockito.when(env.getProperty("myprice.password")).thenReturn("");
			LinkedHashMap<String, Object> requestMap = new LinkedHashMap<>();
			requestMap.put(StringConstants.MY_PRICE_TRANS_ID, "12345");
			String responseInstring = new Object().toString();
			Mockito.when(httpRest.callHttpRestClient(anyString(), any(), anyMap(), anyString(), anyMap(), anyString())).thenReturn(responseInstring);

			updateTransactionPrintDocServiceImpl.updateTransactionPrintDocService(requestMap);
		} catch (SalesBusinessException e) {
			e.getMessage();
		}
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testException() throws SalesBusinessException {
		try {
			String uri = "https://custompricingst2.att.com/rest/v7/commerceDocumentsOraclecpqo_bmClone_2Transaction/{transactionId}/actions/_print_document";
			Mockito.when(env.getProperty("myprice.updateTransactionPrintDocument")).thenReturn(uri);
			Mockito.when(env.getProperty("myprice.username")).thenReturn("");
			Mockito.when(env.getProperty("myprice.password")).thenReturn("");
			LinkedHashMap<String, Object> requestMap = new LinkedHashMap<>();
			requestMap.put(StringConstants.MY_PRICE_TRANS_ID, "12345");
			Mockito.when(httpRest.callHttpRestClient(anyString(), any(), anyMap(), anyString(), anyMap(), anyString())).thenThrow(SalesBusinessException.class);
			updateTransactionPrintDocServiceImpl.updateTransactionPrintDocService(requestMap);
		} catch (SalesBusinessException e) {
			e.getMessage();
		}
	}
}
