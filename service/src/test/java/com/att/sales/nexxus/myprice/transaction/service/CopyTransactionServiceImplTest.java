/**
 * 
 */
package com.att.sales.nexxus.myprice.transaction.service;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.anyMap;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.doNothing;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
import com.att.sales.nexxus.constant.StringConstants;
import com.att.sales.nexxus.dao.model.NxMpDeal;
import com.att.sales.nexxus.dao.model.NxMpDesignDocument;
import com.att.sales.nexxus.dao.model.NxMpPriceDetails;
import com.att.sales.nexxus.dao.model.NxMpSiteDictionary;
import com.att.sales.nexxus.dao.model.NxMpSolutionDetails;
import com.att.sales.nexxus.dao.model.solution.NxSolutionDetail;
import com.att.sales.nexxus.dao.repository.NxMpDealRepository;
import com.att.sales.nexxus.dao.repository.NxMpDesignDocumentRepository;
import com.att.sales.nexxus.dao.repository.NxMpPriceDetailsRepository;
import com.att.sales.nexxus.dao.repository.NxMpSiteDictionaryRepository;
import com.att.sales.nexxus.dao.repository.NxMpSolutionDetailsRepository;
import com.att.sales.nexxus.fallout.model.FalloutDetailsRequest;
import com.att.sales.nexxus.myprice.transaction.model.Documents;
import com.att.sales.nexxus.myprice.transaction.model.DocumentsWrapper;
import com.att.sales.nexxus.util.HttpRestClient;
import com.att.sales.nexxus.util.RestClientUtil;

/**
 * @author ShruthiCJ
 *
 */
@ExtendWith(MockitoExtension.class)

public class CopyTransactionServiceImplTest {
	
	@InjectMocks
	private CopyTransactionServiceImpl copyTransactionServiceImpl;
	
	@Mock
	private MyPriceTransactionUtil myPriceTransactionUtil;
	
	@Mock
	private RestClientUtil restClient;
	
	@Mock
	private NxMpDealRepository nxMpDealRepository;
	
	@Mock
	private NxMpSiteDictionaryRepository nxMpSiteDictionaryRepository;
	
	@Mock
	private NxMpSolutionDetailsRepository nxMpSolutionDetailsRepository;
	
	@Mock
	private NxMpDesignDocumentRepository nxMpDesignDocumentRepository;
	
	@Mock
	private NxMpPriceDetailsRepository nxMpPriceDetailsRepository;
	
	@Mock
	private Environment env;
	
	private NxMpDeal nxMpdeal;
	
	private NxMpSiteDictionary nxMpSiteDictionary;
	
	private NxMpSolutionDetails nxMpSolutionDetails;
	
	private List<NxMpDesignDocument>  nxMpDesignDocumentList;
	
	private List<NxMpPriceDetails> nxMpPriceDetailList;
	
	private Map<String, Object> requestMap;
	
	private DocumentsWrapper documentsWrapper;
	
	@Mock
	private HttpRestClient httpRest;
	
	@BeforeEach
	public void init() {
		requestMap = new HashMap<String, Object>();
		requestMap.put(StringConstants.MY_PRICE_TRANS_ID, "1");
		requestMap.put(StringConstants.PRICE_SCENARIO_ID, "11");
		documentsWrapper = new DocumentsWrapper();
		Documents documents = new Documents();
		documents.setDealID("1234");
		documents.setRevision("1");
		documentsWrapper.setDocuments(documents);
		nxMpdeal = new NxMpDeal();
		nxMpdeal.setDealID("1234");
		nxMpdeal.setNxTxnId(101L);
		nxMpSiteDictionary = new NxMpSiteDictionary();
		nxMpSiteDictionary.setNxTxnId(101L);
		nxMpSolutionDetails = new NxMpSolutionDetails();
		nxMpSolutionDetails.setNxTxnId(101l);
		nxMpDesignDocumentList = new ArrayList<>();
		NxMpDesignDocument design = new NxMpDesignDocument();
		design.setNxTxnId(101l);
		nxMpDesignDocumentList.add(design);
		nxMpPriceDetailList = new ArrayList<>();
		NxMpPriceDetails np = new NxMpPriceDetails();
		np.setNxTxnId(101l);
		nxMpPriceDetailList.add(np);
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void testProcessMutliPriceScenario() throws SalesBusinessException {
		requestMap.put(MyPriceConstants.RESPONSE_CODE, 200);
		Mockito.when(env.getProperty("myprice.copyTransactionRequest")).thenReturn("https://custompricingst.att.com/rest/v7/commerceDocumentsOraclecpqo_bmClone_2Transaction/{transactionId}/actions/_copy_transaction");
		String result = "{\"documents\": {\"rd_requestID_q\": \"1234\", \"rd_revisionNumber_q\":\"1\"}}";
		Mockito.when(httpRest.callHttpRestClient(anyString(), any(), anyMap(), anyString(), anyMap(), anyString()))
		.thenReturn(result);
		Mockito.when(restClient.processResult(anyString(), any())).thenReturn(documentsWrapper);
		Mockito.when(nxMpDealRepository.findByTransactionId(anyString())).thenReturn(nxMpdeal);
		Mockito.when(nxMpSiteDictionaryRepository.findByNxTxnId(anyLong())).thenReturn(nxMpSiteDictionary);
		Mockito.when(nxMpSolutionDetailsRepository.findByNxTxnId(anyLong())).thenReturn(nxMpSolutionDetails);
		Mockito.when(nxMpDesignDocumentRepository.findByNxTxnId(anyLong())).thenReturn(nxMpDesignDocumentList);
		Mockito.when(nxMpPriceDetailsRepository.findByNxTxnId(anyLong())).thenReturn(nxMpPriceDetailList);
		Mockito.when(nxMpDealRepository.saveAndFlush(any(NxMpDeal.class))).thenReturn(nxMpdeal);
		Mockito.when(nxMpSiteDictionaryRepository.save(any(NxMpSiteDictionary.class))).thenReturn(nxMpSiteDictionary);
		Mockito.when(nxMpSolutionDetailsRepository.save(any(NxMpSolutionDetails.class))).thenReturn(nxMpSolutionDetails);
		Mockito.when(nxMpDesignDocumentRepository.save(any())).thenReturn(nxMpDesignDocumentList);
		Mockito.when(nxMpPriceDetailsRepository.save(any())).thenReturn(nxMpPriceDetailList);
		Mockito.when(nxMpDealRepository.findMaxRevisionBySolutoinId(anyLong(),anyString(),anyString(),anyString())).thenReturn(0);
		Mockito.when(nxMpDealRepository.findMaxVersionBySolutoinId(anyLong(),anyString())).thenReturn(0);
		Mockito.when(env.getProperty(anyString())).thenReturn("test");
		Mockito.when(restClient.callRestApi(anyString(),anyString(),anyString(),anyMap(),anyMap(), anyString())).thenReturn("test");
		Mockito.when(restClient.processResult(anyString(), any())).thenReturn(null);
		copyTransactionServiceImpl.processMutliPriceScenario(requestMap);
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testCopyTransaction() throws SalesBusinessException {
		requestMap.put(MyPriceConstants.RESPONSE_CODE, 200);
		requestMap.put(StringConstants.FLOW_TYPE_INR, "pricerd");
		requestMap.put(StringConstants.PRICE_SCENARIO_ID, 11l);
		requestMap.put(StringConstants.SOLUTIONDETAILS, new NxSolutionDetail());
		requestMap.put(StringConstants.ADD_PRODDUCT_IN_EXSTING_TXN, "addProductInExistingTxn");
		Mockito.when(env.getProperty("myprice.copyTransactionRequest")).thenReturn("https://custompricingst.att.com/rest/v7/commerceDocumentsOraclecpqo_bmClone_2Transaction/{transactionId}/actions/_copy_transaction");
		String result = "{\"documents\": {\"rd_requestID_q\": \"1234\", \"rd_revisionNumber_q\":\"1\"}}";
		Mockito.when(httpRest.callHttpRestClient(anyString(), any(), anyMap(), anyString(), anyMap(), anyString()))
		.thenReturn(result);
		Mockito.when(restClient.processResult(anyString(), any())).thenReturn(documentsWrapper);
		List<NxMpDeal> deals = new ArrayList<>();
		deals.add(nxMpdeal);
		Mockito.when(nxMpDealRepository.findByMpTransactionId(anyString())).thenReturn(deals);
		Mockito.when(nxMpSiteDictionaryRepository.findByNxTxnId(anyLong())).thenReturn(nxMpSiteDictionary);
		Mockito.when(nxMpSolutionDetailsRepository.findByNxTxnId(anyLong())).thenReturn(nxMpSolutionDetails);
		Mockito.when(nxMpDesignDocumentRepository.findByNxTxnId(anyLong())).thenReturn(nxMpDesignDocumentList);
		Mockito.when(nxMpPriceDetailsRepository.findByNxTxnId(anyLong())).thenReturn(nxMpPriceDetailList);
		Mockito.when(nxMpDealRepository.saveAndFlush(any(NxMpDeal.class))).thenReturn(nxMpdeal);
		Mockito.when(nxMpSiteDictionaryRepository.save(any(NxMpSiteDictionary.class))).thenReturn(nxMpSiteDictionary);
		Mockito.when(nxMpSolutionDetailsRepository.save(any(NxMpSolutionDetails.class))).thenReturn(nxMpSolutionDetails);
		Mockito.when(nxMpDesignDocumentRepository.save(any())).thenReturn(nxMpDesignDocumentList);
		Mockito.when(nxMpPriceDetailsRepository.save(any())).thenReturn(nxMpPriceDetailList);
		Mockito.when(myPriceTransactionUtil.saveNxDesignAudit(anyLong(), anyString(), any(), anyString(), anyString())).thenReturn(1l);
		doNothing().when(myPriceTransactionUtil).sendDmaapEvents(any(), any() , anyString(), anyMap());
		FalloutDetailsRequest request = new FalloutDetailsRequest(); 
		copyTransactionServiceImpl.copyTransaction(requestMap, request);
		
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void testCopyTransactionElse() throws SalesBusinessException {
		requestMap.put(MyPriceConstants.RESPONSE_CODE, 500);
		requestMap.put(StringConstants.FLOW_TYPE_INR, "pricerd");
		requestMap.put(StringConstants.PRICE_SCENARIO_ID, 11l);
		Mockito.when(env.getProperty("myprice.copyTransactionRequest")).thenReturn("https://custompricingst.att.com/rest/v7/commerceDocumentsOraclecpqo_bmClone_2Transaction/{transactionId}/actions/_copy_transaction");
		String result = "{\"documents\": {\"rd_requestID_q\": \"1234\", \"rd_revisionNumber_q\":\"1\"}}";
		Mockito.when(httpRest.callHttpRestClient(anyString(), any(), anyMap(), anyString(), anyMap(), anyString()))
		.thenReturn(result);
		Mockito.when(restClient.processResult(anyString(), any())).thenReturn(documentsWrapper);
		doNothing().when(myPriceTransactionUtil).sendDmaapEvents(any(), any() , anyString(), anyMap());
		List<NxMpDeal> nxMpDeals=new ArrayList<>();
		NxMpDeal nxMpDeal = new NxMpDeal();
		nxMpDeals.add(nxMpDeal);
		Mockito.when(nxMpDealRepository.findByMpTransactionId(anyString())).thenReturn(nxMpDeals);
		FalloutDetailsRequest request = new FalloutDetailsRequest(); 
		copyTransactionServiceImpl.copyTransaction(requestMap, request);
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testCallCopyTransExcep() throws SalesBusinessException {
		Mockito.when(env.getProperty("myprice.copyTransactionRequest")).thenReturn("https://custompricingst.att.com/rest/v7/commerceDocumentsOraclecpqo_bmClone_2Transaction/{transactionId}/actions/_copy_transaction");
		Mockito.when(httpRest.callHttpRestClient(anyString(), any(), anyMap(), anyString(), anyMap(), anyString())).thenThrow(SalesBusinessException.class);
		FalloutDetailsRequest request = new FalloutDetailsRequest(); 
		copyTransactionServiceImpl.callCopyTrans(requestMap, "{}",new NxMpDeal(), request);
	}
}
