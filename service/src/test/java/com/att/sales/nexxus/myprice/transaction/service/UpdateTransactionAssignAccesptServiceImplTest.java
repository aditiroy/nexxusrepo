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
import com.att.sales.framework.model.ServiceResponse;
import com.att.sales.nexxus.constant.StringConstants;
import com.att.sales.nexxus.util.HttpRestClient;
import com.att.sales.nexxus.util.RestClientUtil;

@ExtendWith(MockitoExtension.class)

public class UpdateTransactionAssignAccesptServiceImplTest {

	@InjectMocks
	UpdateTransactionAssignAccesptServiceImpl updateTransactionAssignAccesptServiceImpl;
	
	@Mock
	RestClientUtil restClient;

	@Mock
	Environment env;

	@Mock
	HttpRestClient httpRest;
	
	@Test
	public void updateTransactionassignAccesptService() throws SalesBusinessException {
		try {
			String uri = "https://custompricingst2.att.com/rest/v7/commerceDocumentsOraclecpqo_bmClone_2Transaction/{transactionId}/actions/assignAccept";
			Mockito.when(env.getProperty("myprice.updateTransactionAssignAccept")).thenReturn(uri);
			Mockito.when(env.getProperty("myprice.username")).thenReturn("");
			Mockito.when(env.getProperty("myprice.password")).thenReturn("");
			String assignAcceptResponse = "{}";
			LinkedHashMap<String, Object> requestMap = new LinkedHashMap<>();
			requestMap.put(StringConstants.MY_PRICE_TRANS_ID, "12345");
			Mockito.when(httpRest.callHttpRestClient(anyString(), any(), anyMap(), anyString(), anyMap(), anyString())).thenReturn(assignAcceptResponse);
			ServiceResponse response = new ServiceResponse();
			Mockito.when(restClient.processResult(assignAcceptResponse, ServiceResponse.class)).thenReturn(response);
			updateTransactionAssignAccesptServiceImpl.updateTransactionassignAccesptService(requestMap);
		} catch (Exception e) {
			e.getMessage();
		}
	}

	
	@SuppressWarnings("unchecked")
	@Test
	public void testException() throws SalesBusinessException {
		try {
			String uri = "https://custompricingst2.att.com/rest/v7/commerceDocumentsOraclecpqo_bmClone_2Transaction/{transactionId}/actions/assignAccept";
			Mockito.when(env.getProperty("myprice.updateTransactionAssignAccept")).thenReturn(uri);
			Mockito.when(env.getProperty("myprice.username")).thenReturn("");
			Mockito.when(env.getProperty("myprice.password")).thenReturn("");
			//String assignAcceptResponse = "{}";
			LinkedHashMap<String, Object> requestMap = new LinkedHashMap<>();
			requestMap.put(StringConstants.MY_PRICE_TRANS_ID, "12345");
			Mockito.when(httpRest.callHttpRestClient(anyString(), any(), anyMap(), anyString(), anyMap(), anyString())).thenThrow(SalesBusinessException.class);

			//ServiceResponse response = new ServiceResponse();
			//Mockito.when(restClient.processResult(assignAcceptResponse, ServiceResponse.class)).thenReturn(response);
			updateTransactionAssignAccesptServiceImpl.updateTransactionassignAccesptService(requestMap);
		} catch (Exception e) {
			e.getMessage();
		}
	}

}
