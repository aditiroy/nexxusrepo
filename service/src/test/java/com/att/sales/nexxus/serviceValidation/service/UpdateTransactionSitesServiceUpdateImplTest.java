package com.att.sales.nexxus.serviceValidation.service;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyMap;
import static org.mockito.Mockito.anyString;

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
import com.att.sales.nexxus.serviceValidation.model.UpdateTransSitesServiceUpdateRequest;
import com.att.sales.nexxus.serviceValidation.model.UpdateTransSitesServiceUpdateResponse;
import com.att.sales.nexxus.util.HttpRestClient;
import com.att.sales.nexxus.util.RestClientUtil;
@ExtendWith(MockitoExtension.class)
public class UpdateTransactionSitesServiceUpdateImplTest {

	@InjectMocks
	UpdateTransactionSitesServiceUpdateImpl updateTransSiteServiceUpdateImpl;

	@Mock
	RestClientUtil restClient;

	@Mock
	Environment env;
	
	@Mock
	private HttpRestClient httpRest;
	
	private Map<String, Object> paramMap = new HashMap<String, Object>();

	@BeforeEach
	public void initializeServiceMetaData() {
		Map<String, Object> requestParams = new HashMap<>();
		requestParams.put(ServiceMetaData.OFFER, "AVPN");
		requestParams.put(ServiceMetaData.REST_REQUEST_START_TIME, System.currentTimeMillis());
		requestParams.put(ServiceMetaData.VERSION, "1.0");
		requestParams.put(ServiceMetaData.METHOD, "TestMethod");
		requestParams.put(ServiceMetaData.URI, "Testuri");
		ServiceMetaData.add(requestParams);
		
		paramMap.put("requestMetaDataMap", new HashMap<String, Object>());
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testUpdateTransSiteServiceUpdate() throws SalesBusinessException {
		Mockito.when(env.getProperty("myPrice.updateTransactionSiteServiceUpdate")).thenReturn(
				"https://custompricingdev2.att.com/rest/v7/commerceDocumentsOraclecpqo_bmClone_2Transaction/{transactionId}/actions/sitesServiceUpdate");
		UpdateTransSitesServiceUpdateResponse response = new UpdateTransSitesServiceUpdateResponse();
		Long transId = 1L;
		String transResponse = new Object().toString();
		UpdateTransSitesServiceUpdateRequest request = new UpdateTransSitesServiceUpdateRequest();
		Mockito.when(httpRest.callHttpRestClient(any(), any(), any(), any(), any(), any()))
				.thenReturn(transResponse);
		Mockito.when(restClient.processResult(anyString(), any(Class.class))).thenReturn(response);

		updateTransSiteServiceUpdateImpl.sitesServiceUpdate(request, transId, paramMap);
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testWithExceptionScenario() throws SalesBusinessException {
		Mockito.when(env.getProperty("myPrice.updateTransactionSiteServiceUpdate")).thenReturn(
				"https://custompricingdev2.att.com/rest/v7/commerceDocumentsOraclecpqo_bmClone_2Transaction/{transactionId}/actions/sitesServiceUpdate");
		Long transId = 1L;
		UpdateTransSitesServiceUpdateRequest request = new UpdateTransSitesServiceUpdateRequest();
		Mockito.when(httpRest.callHttpRestClient(any(), any(), any(), any(), any(), any()))
				.thenThrow(SalesBusinessException.class);

		updateTransSiteServiceUpdateImpl.sitesServiceUpdate(request, transId, paramMap);
	}

}
