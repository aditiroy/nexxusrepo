package com.att.sales.nexxus.myprice.transaction.service;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.anyMap;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.doNothing;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.env.Environment;

import com.att.sales.framework.exception.SalesBusinessException;
import com.att.sales.nexxus.constant.MyPriceConstants;
import com.att.sales.nexxus.dao.model.NxMpDeal;
import com.att.sales.nexxus.dao.repository.NxMpDealRepository;
import com.att.sales.nexxus.dao.repository.NxMpDesignDocumentRepository;
import com.att.sales.nexxus.myprice.transaction.model.RemoveTransactionLineResponse;
import com.att.sales.nexxus.util.HttpRestClient;
import com.att.sales.nexxus.util.RestClientUtil;

@ExtendWith(MockitoExtension.class)

public class RemoveTransactionServiceLineImplTest {
	
	@InjectMocks
	private RemoveTransactionServiceLineImpl removeTransactionServiceLineImpl;
	
	@Mock
	private NxMpDealRepository nxMpDealRepository;
	
	@Mock
	private Environment env;
	
	@Mock
	private RestClientUtil restClient;
	
	@Mock
	HttpRestClient httpRest;
	
	private Map<String, Object> requestMap;
	
	@Mock
	private NxMpDesignDocumentRepository nxMpDesignDocumentRepository;
	
	@BeforeEach
	public void init() {
		requestMap = new HashMap<String, Object>();
		requestMap.put(MyPriceConstants.MP_TRANSACTION_ID, "1111111");
		requestMap.put(MyPriceConstants.NX_DESIGN_ID, 1111L);
		requestMap.put(MyPriceConstants.DOCUMENT_ID, new HashSet<Long>() {{add(1L);}});
	}
	
	@Test
	public void testRemoveTransactionLine() {
		 
		try {
			Mockito.when(env.getProperty("myprice.removeTransactionLine")).thenReturn("https://custompricingdev2.att.com/rest/v7/commerceDocumentsOraclecpqo_bmClone_2Transaction/{transactionId}/actions/_remove_transactionLine");
			String transResponse = "{\"quoteUrl\" : \"quoteUrl\"}";
			Mockito.when(httpRest.callHttpRestClient(anyString(), any(), anyMap(), anyString(), anyMap(), anyString()))
			.thenReturn(transResponse);
			Mockito.when(restClient.processResult(anyString(), any()))
			.thenReturn(new RemoveTransactionLineResponse());
			Mockito.when(nxMpDealRepository.findByTransactionId(anyString())).thenReturn(new NxMpDeal());
			doNothing().when(nxMpDesignDocumentRepository).updateActiveYNByTxnId(any(), anyLong(), anyLong());
			removeTransactionServiceLineImpl.removeTransactionLine(requestMap);
			
		} catch (SalesBusinessException e) {
			e.getMessage();
		}
	}
	
	@Test
	public void testException() {
		try {
			
			Mockito.when(env.getProperty("myprice.removeTransactionLine")).thenReturn("https://custompricingdev2.att.com/rest/v7/commerceDocumentsOraclecpqo_bmClone_2Transaction/{transactionId}/actions/_remove_transactionLine");
			Mockito.when(httpRest.callHttpRestClient(anyString(), any(), anyMap(), anyString(), anyMap(), anyString()))
			.thenThrow(SalesBusinessException.class);
			removeTransactionServiceLineImpl.removeTransactionLine(requestMap);
		} catch (SalesBusinessException e) {
			e.getMessage();
		}
	}

}
