package com.att.sales.nexxus.myprice.transaction.service;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyMap;
import static org.mockito.Mockito.anyString;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.env.Environment;

import com.att.sales.framework.exception.SalesBusinessException;
import com.att.sales.nexxus.constant.MyPriceConstants;
import com.att.sales.nexxus.dao.model.NxMpDeal;
import com.att.sales.nexxus.dao.model.solution.NxSolutionDetail;
import com.att.sales.nexxus.dao.repository.NxMpDealRepository;
import com.att.sales.nexxus.dao.repository.NxSolutionDetailsRepository;
import com.att.sales.nexxus.myprice.transaction.model.GetTransactionResponse;
import com.att.sales.nexxus.myprice.transaction.model.OriginalClonedTxId;
import com.att.sales.nexxus.myprice.transaction.model.Values;
import com.att.sales.nexxus.util.HttpRestClient;
import com.att.sales.nexxus.util.RestClientUtil;

@ExtendWith(MockitoExtension.class)

public class GetTransactionServiceImplTest {

	@InjectMocks
	GetTransactionServiceImpl getTransactionServiceImpl;

	@Mock
	RestClientUtil restClient;

	@Mock
	Environment env;

	@Mock
	NxMpDealRepository nxMpDealRepository;
	
	@Mock
	private NxSolutionDetailsRepository nxSolutionDetailsRepository;
	
	@Mock
	HttpRestClient httpRest;

	@Test
	public void getTransaction() throws SalesBusinessException {
		try {
			String uri = "https://custompricingdev2.att.com/rest/v7/commerceDocumentsOraclecpqo_bmClone_2Transaction/{transactionId}";
			Mockito.when(env.getProperty("myprice.getTransaction")).thenReturn(uri);
			Mockito.when(env.getProperty("myprice.username")).thenReturn("");
			Mockito.when(env.getProperty("myprice.password")).thenReturn("");
			String transResponse = new Object().toString();
			String myPriceTransId = "12345";
			Mockito.when(httpRest.callHttpRestClient(anyString(), any(), anyMap(), anyString(), anyMap(), anyString())).thenReturn(transResponse);
			GetTransactionResponse response = new GetTransactionResponse();
			Mockito.when(restClient.processResult(anyString(), any())).thenReturn(response);
			getTransactionServiceImpl.updateRateLetterDetails(response, myPriceTransId);
			getTransactionServiceImpl.getTransaction(myPriceTransId);
		} catch (Exception e) {
			e.getMessage();
		}
	}

	@SuppressWarnings("unchecked")
	@Test
	public void getTransactionException() throws SalesBusinessException {
		try {
			String uri = "https://custompricingdev2.att.com/rest/v7/commerceDocumentsOraclecpqo_bmClone_2Transaction/{transactionId}";
			Mockito.when(env.getProperty("myprice.getTransaction")).thenReturn(uri);
			Mockito.when(env.getProperty("myprice.username")).thenReturn("");
			Mockito.when(env.getProperty("myprice.password")).thenReturn("");
			String myPriceTransId = "12345";
			Mockito.when(httpRest.callHttpRestClient(anyString(), any(), anyMap(), anyString(), anyMap(), anyString())).thenThrow(SalesBusinessException.class);
			// GetTransactionResponse response = new GetTransactionResponse();
			// Mockito.when(restClient.processResult(transResponse,
			// GetTransactionResponse.class)).thenReturn(response);
			// getTransactionServiceImpl.updateRateLetterDetails(response,myPriceTransId);
			getTransactionServiceImpl.getTransaction(myPriceTransId);
		} catch (Exception e) {
			e.getMessage();
		}
	}

	@Test
	public void getTransactionSalesOne() throws SalesBusinessException {
		try {
			String uri = "https://custompricingdev2.att.com/rest/v7/commerceDocumentsOraclecpqo_bmClone_2Transaction/{transactionId}";
			Mockito.when(env.getProperty("myprice.getTransaction")).thenReturn(uri);
			Mockito.when(env.getProperty("myprice.username")).thenReturn("");
			Mockito.when(env.getProperty("myprice.password")).thenReturn("");
			String transResponse = "{}";
			String myPriceTransId = "12345";
			Mockito.when(httpRest.callHttpRestClient(anyString(), any(), anyMap(), anyString(), anyMap(), anyString())).thenReturn(transResponse);
			GetTransactionResponse response = new GetTransactionResponse();
			Mockito.when(restClient.processResult(anyString(), any())).thenReturn(response);
			getTransactionServiceImpl.getTransactionSalesOne(myPriceTransId);
		} catch (Exception e) {
			e.getMessage();
		}
	}

	@SuppressWarnings("unchecked")
	@Test
	public void getTransactionSalesOneException() throws SalesBusinessException {
		try {
			String uri = "https://custompricingdev2.att.com/rest/v7/commerceDocumentsOraclecpqo_bmClone_2Transaction/{transactionId}";
			Mockito.when(env.getProperty("myprice.getTransaction")).thenReturn(uri);
			Mockito.when(env.getProperty("myprice.username")).thenReturn("");
			Mockito.when(env.getProperty("myprice.password")).thenReturn("");
			String myPriceTransId = "12345";
			Mockito.when(httpRest.callHttpRestClient(anyString(), any(), anyMap(), anyString(), anyMap(), anyString())).thenThrow(SalesBusinessException.class);
			// GetTransactionResponse response = new GetTransactionResponse();
			// Mockito.when(restClient.processResult(transResponse,
			// GetTransactionResponse.class)).thenReturn(response);
			getTransactionServiceImpl.getTransactionSalesOne(myPriceTransId);
		} catch (Exception e) {
			e.getMessage();
		}
	}

	@Test
	public void updateRateLetterDetails() {
		GetTransactionResponse response = new GetTransactionResponse();
		Values value = new Values();
		value.setDisplayValue("preNSS_Certified");
		value.setValue("preNSS_Certified");
		response.setRlType(value);
		response.setAutoApproval(true);
		response.setOriginalClonedTxId("12345");
		String transactionId = "12345";
		NxMpDeal nxmpdeal = new NxMpDeal();
		nxmpdeal.setAction("PD_CLONE");
		List<NxMpDeal> nxmpdeals = new ArrayList<>();
		nxmpdeals.add(nxmpdeal);
		Mockito.when(nxMpDealRepository.getByTransactionIdAndNxTxnOrder(transactionId)).thenReturn(nxmpdeals);
		nxmpdeal.setRateLetterExpiresOn(response.getRateLetterExpiresOn());
		nxmpdeal.setQuoteUrl(response.getQuoteUrl());
		nxmpdeal.setRlType(MyPriceConstants.RL_TYPE_WITHOUT_CAVEAT);
		nxmpdeal.setRlType(response.getRlType().getValue());
		nxmpdeal.setAutoApproval("Approved Online");
		nxmpdeal.setAction("PD_CLONE");
		
		OriginalClonedTxId cloneTxnId = new OriginalClonedTxId();
		cloneTxnId.setAction("Clone");
		cloneTxnId.setSourceId("12345");
		Mockito.when(restClient.processResult(response.getOriginalClonedTxId(), OriginalClonedTxId.class)).thenReturn(cloneTxnId);
		NxMpDeal oldNxmpdeal = new NxMpDeal();
		oldNxmpdeal.setPriceScenarioId(12345L);
		Mockito.when(nxMpDealRepository.getByTransactionId(cloneTxnId.getSourceId())).thenReturn(oldNxmpdeal);
		nxmpdeal.setPriceScenarioId(oldNxmpdeal.getPriceScenarioId());
		nxmpdeal.setModifiedDate(new Date());
		Mockito.when(nxMpDealRepository.save(nxmpdeal)).thenReturn(nxmpdeal);
		getTransactionServiceImpl.updateRateLetterDetails(response, transactionId);
	}
	
	
	@Test
	public void updateRateLetterDetailsRlType() {
		GetTransactionResponse response = new GetTransactionResponse();
		Values value = new Values();
		value.setDisplayValue("preNSS_CertifiedwithCaveats");
		value.setValue("preNSS_CertifiedwithCaveats");
		response.setRlType(value);
		response.setAutoApproval(false);
		response.setOriginalClonedTxId("12345");
		String transactionId = "12345";
		NxMpDeal nxmpdeal = new NxMpDeal();
		List<NxMpDeal> nxmpdeals = new ArrayList<>();
		nxmpdeals.add(nxmpdeal);
		Mockito.when(nxMpDealRepository.getByTransactionIdAndNxTxnOrder(transactionId)).thenReturn(nxmpdeals);
		nxmpdeal.setRateLetterExpiresOn(response.getRateLetterExpiresOn());
		nxmpdeal.setQuoteUrl(response.getQuoteUrl());
		nxmpdeal.setRlType(MyPriceConstants.RL_TYPE_WITH_CAVEAT);
		nxmpdeal.setRlType(response.getRlType().getValue());
		nxmpdeal.setAutoApproval("ICB");
		nxmpdeal.setAction("PD_CLONE");
		
		OriginalClonedTxId cloneTxnId = new OriginalClonedTxId();
		cloneTxnId.setAction("Clone");
		cloneTxnId.setSourceId("12345");
		Mockito.when(restClient.processResult(response.getOriginalClonedTxId(), OriginalClonedTxId.class)).thenReturn(cloneTxnId);
		NxMpDeal oldNxmpdeal = new NxMpDeal();
		oldNxmpdeal.setPriceScenarioId(12345L);
		Mockito.when(nxMpDealRepository.getByTransactionId(cloneTxnId.getSourceId())).thenReturn(oldNxmpdeal);
		nxmpdeal.setPriceScenarioId(oldNxmpdeal.getPriceScenarioId());
		nxmpdeal.setModifiedDate(new Date());
		Mockito.when(nxMpDealRepository.save(nxmpdeal)).thenReturn(nxmpdeal);
		getTransactionServiceImpl.updateRateLetterDetails(response, transactionId);
	}
	
	
	@Test
	public void updateRateLetterDetailsAutoApproval() {
		GetTransactionResponse response = new GetTransactionResponse();
		Values value = new Values();
		value.setDisplayValue("preNSS_Certified1");
		value.setValue("preNSS_Certified1");
		response.setRlType(value);
		response.setOriginalClonedTxId("12345");
		String transactionId = "12345";
		NxMpDeal nxmpdeal = new NxMpDeal();
		List<NxMpDeal> nxmpdeals = new ArrayList<>();
		nxmpdeals.add(nxmpdeal);
		Mockito.when(nxMpDealRepository.getByTransactionIdAndNxTxnOrder(transactionId)).thenReturn(nxmpdeals);
		nxmpdeal.setRateLetterExpiresOn(response.getRateLetterExpiresOn());
		nxmpdeal.setQuoteUrl(response.getQuoteUrl());
		nxmpdeal.setRlType(response.getRlType().getValue());
		nxmpdeal.setAutoApproval("None");
		nxmpdeal.setAction("PD_CLONE");
		
		OriginalClonedTxId cloneTxnId = new OriginalClonedTxId();
		cloneTxnId.setAction("Clone");
		cloneTxnId.setSourceId("12345");
		Mockito.when(restClient.processResult(response.getOriginalClonedTxId(), OriginalClonedTxId.class)).thenReturn(cloneTxnId);
		NxMpDeal oldNxmpdeal = new NxMpDeal();
		oldNxmpdeal.setPriceScenarioId(12345L);
		Mockito.when(nxMpDealRepository.getByTransactionId(cloneTxnId.getSourceId())).thenReturn(oldNxmpdeal);
		nxmpdeal.setPriceScenarioId(oldNxmpdeal.getPriceScenarioId());
		nxmpdeal.setModifiedDate(new Date());
		Mockito.when(nxMpDealRepository.save(nxmpdeal)).thenReturn(nxmpdeal);
		getTransactionServiceImpl.updateRateLetterDetails(response, transactionId);
	}
	
	@Test
	public void updateRateLetterDetailsForContractPricingScope() {
		GetTransactionResponse response = new GetTransactionResponse();
		response.setCustomerCompanyName("customerCompanyName");
		response.setSaartAccountNumber("saartAccountNumber");
		Values value = new Values();
		value.setDisplayValue("preNSS_CertifiedwithCaveats");
		value.setValue("preNSS_CertifiedwithCaveats");
		response.setRlType(value);
		response.setAutoApproval(false);
		response.setOriginalClonedTxId("12345");
		Values value1 = new Values();
		value1.setDisplayValue("SPECIAL CONSTRUCTION (POST)");
		value1.setValue("specialConstruction");
		response.setContractPricingScope(value1);
		String transactionId = "12345";
		NxMpDeal nxmpdeal = new NxMpDeal();
		List<NxMpDeal> nxmpdeals = new ArrayList<>();
		nxmpdeals.add(nxmpdeal);
		Mockito.when(nxMpDealRepository.getByTransactionIdAndNxTxnOrder(transactionId)).thenReturn(nxmpdeals);
		nxmpdeal.setRateLetterExpiresOn(response.getRateLetterExpiresOn());
		nxmpdeal.setQuoteUrl(response.getQuoteUrl());
		nxmpdeal.setRlType(MyPriceConstants.RL_TYPE_WITH_CAVEAT);
		nxmpdeal.setRlType(response.getRlType().getValue());
		nxmpdeal.setAutoApproval("ICB");
		nxmpdeal.setAction("PD_CLONE");
		nxmpdeal.setSolutionId(1L);
		
		OriginalClonedTxId cloneTxnId = new OriginalClonedTxId();
		cloneTxnId.setAction("Clone");
		cloneTxnId.setSourceId("12345");
		Mockito.when(restClient.processResult(response.getOriginalClonedTxId(), OriginalClonedTxId.class)).thenReturn(cloneTxnId);
		NxMpDeal oldNxmpdeal = new NxMpDeal();
		oldNxmpdeal.setPriceScenarioId(12345L);
		Mockito.when(nxMpDealRepository.getByTransactionId(cloneTxnId.getSourceId())).thenReturn(oldNxmpdeal);
		nxmpdeal.setPriceScenarioId(oldNxmpdeal.getPriceScenarioId());
		nxmpdeal.setModifiedDate(new Date());
		Mockito.when(nxMpDealRepository.save(nxmpdeal)).thenReturn(nxmpdeal);
		NxSolutionDetail nxSolutionDetail = new NxSolutionDetail();
		Mockito.when(nxSolutionDetailsRepository.findByNxSolutionId(Mockito.anyLong())).thenReturn(nxSolutionDetail);
		getTransactionServiceImpl.updateRateLetterDetails(response, transactionId);
	}
	
}
