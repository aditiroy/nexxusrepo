package com.att.sales.nexxus.myprice.transaction.service;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.anyMap;
import static org.mockito.Mockito.anyString;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.Query;

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
import com.att.sales.nexxus.dao.model.NxMpSiteDictionary;
import com.att.sales.nexxus.dao.model.NxMpSolutionDetails;
import com.att.sales.nexxus.dao.repository.NxMpSiteDictionaryRepository;
import com.att.sales.nexxus.dao.repository.NxMpSolutionDetailsRepository;
import com.att.sales.nexxus.dao.repository.NxSolutionDetailsRepository;
import com.att.sales.nexxus.handlers.GetOptyInfoWSHandler;
import com.att.sales.nexxus.myprice.transaction.model.CreateTransactionResponse;
import com.att.sales.nexxus.reteriveicb.model.Offer;
import com.att.sales.nexxus.reteriveicb.model.RetreiveICBPSPRequest;
import com.att.sales.nexxus.reteriveicb.model.Site;
import com.att.sales.nexxus.reteriveicb.model.Solution;
import com.att.sales.nexxus.rome.model.GetOptyResponse;
import com.att.sales.nexxus.util.HttpRestClient;
import com.att.sales.nexxus.util.RestClientUtil;

@ExtendWith(MockitoExtension.class)

public class UpdateTransactionCleanSaveFMOTest {
	
	@InjectMocks
	UpdateTransactionCleanSaveFMO updateTransactionCleanSaveFMO;
	
	private RetreiveICBPSPRequest retreiveICBPSPRequest;
	
	private CreateTransactionResponse createTransactionResponse;
	
	private Map<String,Object> paramMap;
	
	@Mock
	private GetOptyInfoWSHandler getOptyInfoWSHandler;
	
	@Mock
	private EntityManager em;
	
	@Mock
	private Query query;
	
	@Mock
	private NxMpSiteDictionaryRepository nxMpSiteDictionaryRepository;
	
	@Mock
	private NxMpSolutionDetailsRepository nxMpSolutionDetailRepository;
	
	@Mock
	private Environment env;
	
	@Mock
	private RestClientUtil restClient;

	@Mock
	private UpdateTxnSiteUploadServiceImpl updateTxnSiteUploadServiceImpl;
	
	private Map<String, Object> response;
	
	@Mock
	private NxSolutionDetailsRepository nxSolutionDetailsRepository;
	
	@Mock
	private HttpRestClient httpRest;
	
	@BeforeEach 
	public void init() {
		retreiveICBPSPRequest = new RetreiveICBPSPRequest();
		Solution solution = new Solution();
		solution.setOptyId("1RDDF3");
		solution.setErateInd("Y");
		List<Offer> offers = new ArrayList<>();
		Offer offer = new Offer();
		List<Site> sites = new ArrayList<>();
		Site site = new Site();
		site.setNxSiteId(101l);
		sites.add(site);
		offer.setSite(sites);
		offers.add(offer);
		solution.setOffers(offers);
		retreiveICBPSPRequest.setSolution(solution);
		createTransactionResponse = new CreateTransactionResponse();
		createTransactionResponse.setMyPriceTransacId("101010");
		paramMap = new HashMap<String,Object> ();
		paramMap.put(MyPriceConstants.FLOW_TYPE, MyPriceConstants.SOURCE_FMO);
		
		response = new HashMap<String, Object>();
		response.put(MyPriceConstants.RESPONSE_CODE, 200);
		response.put(MyPriceConstants.RESPONSE_MSG, "Success");
	}
	
	@Test
	public void testUpdateTransactionCleanSave() throws SalesBusinessException {
		
		Mockito.when(getOptyInfoWSHandler.initiateGetOptyInfoWebService(any())).thenReturn(new GetOptyResponse());
		Mockito.when(em.createNativeQuery(anyString())).thenReturn(query);
		Mockito.when(nxMpSiteDictionaryRepository.findByNxTxnId(anyLong())).thenReturn(null);
		Mockito.when(nxMpSiteDictionaryRepository.save(any(NxMpSiteDictionary.class))).thenReturn(new NxMpSiteDictionary());
		Mockito.when(nxMpSolutionDetailRepository.findByNxTxnId(anyLong())).thenReturn(null);
		Mockito.when(nxMpSolutionDetailRepository.save(any(NxMpSolutionDetails.class))).thenReturn(new NxMpSolutionDetails());
		Mockito.when(nxSolutionDetailsRepository.findByExternalKey(anyLong())).thenReturn(null);
		Mockito.when(em.createNamedQuery(anyString())).thenReturn(query);
		BigDecimal b = new BigDecimal(11);
		Mockito.when(query.getSingleResult()).thenReturn(b);
		Mockito.when(env.getProperty("myPrice.updateTransactionCleanSaveRequest")).thenReturn("https://custompricingst.att.com/rest/v8/commerceDocumentsOraclecpqo_bmClone_2Transaction/{transactionId}/actions/cleanSave");
		String res = new Object().toString();
		Mockito.when(httpRest.callHttpRestClient(anyString(), any(), anyMap(), anyString(), anyMap(), anyString()))
		.thenReturn(res);
		retreiveICBPSPRequest.getSolution().setExternalKey(1234L);
		updateTransactionCleanSaveFMO.updateTransactionCleanSave(retreiveICBPSPRequest, createTransactionResponse, paramMap);
		
		
	}

}
