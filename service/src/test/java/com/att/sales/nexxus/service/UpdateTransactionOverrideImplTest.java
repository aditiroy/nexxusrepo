package com.att.sales.nexxus.service;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.anyMap;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.doNothing;

import java.util.HashMap;
import java.util.Map;

import org.junit.BeforeClass;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.env.Environment;
import org.springframework.test.util.ReflectionTestUtils;

import com.att.sales.framework.exception.SalesBusinessException;
import com.att.sales.framework.model.ServiceMetaData;
import com.att.sales.framework.model.ServiceResponse;
import com.att.sales.nexxus.common.CommonConstants;
import com.att.sales.nexxus.constant.MyPriceConstants;
import com.att.sales.nexxus.dao.model.NxMpDeal;
import com.att.sales.nexxus.dao.repository.NxMpDealRepository;
import com.att.sales.nexxus.model.UpdateTransactionOverrideRequest;
import com.att.sales.nexxus.myprice.transaction.service.MyPriceTransactionUtil;
import com.att.sales.nexxus.util.HttpRestClient;
import com.att.sales.nexxus.util.RestClientUtil;

@ExtendWith(MockitoExtension.class)
public class UpdateTransactionOverrideImplTest {

	@Spy
	@InjectMocks
	private UpdateTransactionOverrideImpl updateTransactionOverrideImpl;

	@Mock
	RestClientUtil restClient;
	
	@Mock
	MyPriceTransactionUtil myPriceTransactionUtil;
	
	@Mock
	Environment env;
	
	@Mock
	NxMpDealRepository nxMpDealRepository;
	
	@Mock
	Map<String, Object> result;
	
	@Mock
	MyPriceConstants myPriceConstants;
	
	@Mock
	private HttpRestClient httpRest;
	
	public static final String RESPONSE_DATA = "data";

	@BeforeAll
	public static void init() {
		Map<String, Object> requestParams = new HashMap<>();
		requestParams.put(ServiceMetaData.OFFER, "AVPN");
		requestParams.put(ServiceMetaData.REST_REQUEST_START_TIME, System.currentTimeMillis());
		requestParams.put(ServiceMetaData.VERSION, "1.0");
		requestParams.put(ServiceMetaData.METHOD, "TestMethod");
		requestParams.put(ServiceMetaData.URI, "TestUri");
		ServiceMetaData.add(requestParams);
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testUpdateTransactionOverride() throws SalesBusinessException {
		ReflectionTestUtils.setField(updateTransactionOverrideImpl, "uri",
				"https://custompricingdev2.att.com/rest/v7/commerceDocumentsOraclecpqo_bmClone_2Transaction/{TransactionId}/actions/cleanSave_t");
		ServiceResponse response = new ServiceResponse();
		UpdateTransactionOverrideRequest request = new UpdateTransactionOverrideRequest();
		request.setMyPriceTransId("12333");
		request.setTransType("transType");
		request.setNxAuditId(1L);
		Mockito.when(env.getProperty("myPrice.updateTransactionCleanSaveRequest")).thenReturn("https://custompricingdev2.att.com/rest/v7/commerceDocumentsOraclecpqo_bmClone_2Transaction/{TransactionId}/actions/cleanSave_t");
		String transResponse = new Object().toString();
		Mockito.when(httpRest.callHttpRestClient(anyString(), any(), anyMap(), anyString(), anyMap(), anyString())).thenReturn(transResponse);
		Mockito.when(restClient.processResult(anyString(), any(Class.class))).thenReturn(response);
		NxMpDeal nxMpDeal = new NxMpDeal();
		Mockito.when(nxMpDealRepository.findByTransactionId(request.getMyPriceTransId())).thenReturn(nxMpDeal);
		doNothing().when(myPriceTransactionUtil).updateNxDesignAuditStatus(anyString(), anyString(),anyLong());
		//doNothing().when(myPriceTransactionUtil).sendDmaapEvents(nxMpDeal, myPriceTransactionUtil.getNxSolutionDetails(nxMpDeal.getNxTxnId()), CommonConstants.FAILED, result);
		updateTransactionOverrideImpl.updateTransactionOverride(request);
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testWithExceptionScenario() throws SalesBusinessException  {
		try {
			UpdateTransactionOverrideRequest request = new UpdateTransactionOverrideRequest();
			request.setMyPriceTransId("12333");
			request.setTransType("transType");
			request.setNxAuditId(1L);
			Mockito.when(env.getProperty("myPrice.updateTransactionCleanSaveRequest")).thenReturn("https://custompricingdev2.att.com/rest/v7/commerceDocumentsOraclecpqo_bmClone_2Transaction/{TransactionId}/actions/cleanSave_t");
			Mockito.when(httpRest.callHttpRestClient(anyString(), any(), anyMap(), anyString(), anyMap(), anyString()))
							.thenThrow(SalesBusinessException.class);
			NxMpDeal nxMpDeal = new NxMpDeal();
			Mockito.when(nxMpDealRepository.findByTransactionId(request.getMyPriceTransId())).thenReturn(nxMpDeal);
			doNothing().when(myPriceTransactionUtil).updateNxDesignAuditStatus(anyString(), anyString(),anyLong());
			UpdateTransactionOverrideRequest requestDetails = new UpdateTransactionOverrideRequest();
			requestDetails.setMyPriceTransId("myPriceTransId");
			Mockito.when(result.get(myPriceConstants.RESPONSE_CODE)).thenReturn(requestDetails);
			updateTransactionOverrideImpl.updateTransactionOverride(request);
		 }catch(Exception e) {
			 e.printStackTrace();
		 }
	}
	
	@Test
	public void testUpdateTransactionOverrideService() throws SalesBusinessException {
		ReflectionTestUtils.setField(updateTransactionOverrideImpl, "uri",
				"https://custompricingdev2.att.com/rest/v7/commerceDocumentsOraclecpqo_bmClone_2Transaction/{TransactionId}/actions/cleanSave_t");
		ServiceResponse response = new ServiceResponse();
		UpdateTransactionOverrideRequest request = new UpdateTransactionOverrideRequest();
		request.setMyPriceTransId("12333");
		request.setTransType("transType");
		request.setNxAuditId(1L);
		String transResponse = new Object().toString();
		Mockito.when(env.getProperty("myPrice.updateTransactionCleanSaveRequest")).thenReturn("https://custompricingdev2.att.com/rest/v7/commerceDocumentsOraclecpqo_bmClone_2Transaction/{TransactionId}/actions/cleanSave_t");
		Mockito.when(httpRest.callHttpRestClient(anyString(), any(), anyMap(), anyString(), anyMap(), anyString()))
				.thenReturn(transResponse);
		Mockito.when(restClient.processResult(anyString(), any(Class.class))).thenReturn(response);
		NxMpDeal nxMpDeal = new NxMpDeal();
		Mockito.when(nxMpDealRepository.findByTransactionId(request.getMyPriceTransId())).thenReturn(nxMpDeal);
		doNothing().when(myPriceTransactionUtil).updateNxDesignAuditStatus(anyString(), anyString(),anyLong());
		//doNothing().when(myPriceTransactionUtil).sendDmaapEvents(nxMpDeal, myPriceTransactionUtil.getNxSolutionDetails(nxMpDeal.getNxTxnId()), CommonConstants.FAILED, result);
		updateTransactionOverrideImpl.updateTransactionOverride(request);
	}
}
