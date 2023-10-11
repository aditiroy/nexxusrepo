/**
 * 
 */
package com.att.sales.nexxus.myprice.transaction.service;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyInt;
import static org.mockito.Mockito.anyMap;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.doNothing;

import java.util.ArrayList;
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
import com.att.sales.nexxus.dao.model.NxMpDeal;
import com.att.sales.nexxus.dao.model.solution.NxSolutionDetail;
import com.att.sales.nexxus.dao.repository.NxMpDealRepository;
import com.att.sales.nexxus.dao.repository.SalesMsDao;
import com.att.sales.nexxus.myprice.transaction.model.CreateTransactionResponse;
import com.att.sales.nexxus.reteriveicb.model.Offer;
import com.att.sales.nexxus.reteriveicb.model.RetreiveICBPSPRequest;
import com.att.sales.nexxus.reteriveicb.model.Solution;
import com.att.sales.nexxus.util.HttpRestClient;
import com.att.sales.nexxus.util.RestClientUtil;

/**
 * @author ShruthiCJ
 *
 */
@ExtendWith(MockitoExtension.class)

public class CreateTransactionServiceImplTest {

	@InjectMocks
	private CreateTransactionServiceImpl createTransactionServiceImpl;

	@Mock
	private NxMpDealRepository nxMpDealRepository;
	
	@Mock
	private Environment env;
	
	@Mock
	private RestClientUtil restClient;
	
	@Mock
	private MyPriceTransactionUtil myPriceTransactionUtil;
	
	@Mock
	private Map<String, String> headers;

	@Mock
	private SalesMsDao salesMsDao;
	
	private RetreiveICBPSPRequest retreiveICBPSPRequest;
	
	private NxSolutionDetail nxSolutionDetail;
	
	@Mock
	private HttpRestClient httpRest;
	
	@BeforeEach
	public void init() {
		retreiveICBPSPRequest = new RetreiveICBPSPRequest();
		Solution solution = new Solution();
		solution.setPriceScenarioId(616161L);
		List<Offer> offers = new ArrayList<Offer>();
		Offer offer = new Offer();
		offer.setOfferId("6");
		offers.add(offer);
		solution.setOffers(offers);
		retreiveICBPSPRequest.setSolution(solution);
		nxSolutionDetail = new NxSolutionDetail();
		nxSolutionDetail.setNxSolutionId(1010L);
	}
	
	@Test
	public void testCreateTransaction() throws SalesBusinessException {
		Mockito.when(salesMsDao.getOfferNameByOfferId(anyInt())).thenReturn("ASE");
		String transResponse = "{\"nxTransacId\":11, \"dealID\": \"1234\"}";
		Mockito.when(httpRest.callHttpRestClient(anyString(), any(), anyMap(), anyString(), anyMap(), anyString()))
		.thenReturn(transResponse);
		Mockito.when(restClient.processResult(anyString(), any())).thenReturn( new CreateTransactionResponse());
		NxMpDeal deal = new NxMpDeal();
		Mockito.when(nxMpDealRepository.save(any(NxMpDeal.class))).thenReturn(deal);
		doNothing().when(myPriceTransactionUtil).sendDmaapEvents(any(), any(), anyString(), anyMap());
		createTransactionServiceImpl.createTransaction(retreiveICBPSPRequest, nxSolutionDetail, null, null);
	}
	
	@Test
	public void testCreateTransactionElse() throws SalesBusinessException {
		Mockito.when(salesMsDao.getOfferNameByOfferId(anyInt())).thenReturn("ASE");
		String transResponse = new Object().toString();
		Mockito.when(httpRest.callHttpRestClient(anyString(), any(), anyMap(), anyString(), anyMap(), anyString()))
		.thenReturn(transResponse);
		NxMpDeal deal = new NxMpDeal();
		Mockito.when(nxMpDealRepository.save(any(NxMpDeal.class))).thenReturn(deal);
		doNothing().when(myPriceTransactionUtil).sendDmaapEvents(any(), any(), anyString(), anyMap());
		createTransactionServiceImpl.createTransaction(retreiveICBPSPRequest, nxSolutionDetail, null, null);
	}
	
	@Test
	public void testCreateTransactionExce() throws SalesBusinessException {
		Mockito.when(salesMsDao.getOfferNameByOfferId(anyInt())).thenReturn("ASE");
		Mockito.when(httpRest.callHttpRestClient(anyString(), any(), anyMap(), anyString(), anyMap(), anyString()))
		.thenThrow(SalesBusinessException.class);
		NxMpDeal deal = new NxMpDeal();
		Mockito.when(nxMpDealRepository.save(any(NxMpDeal.class))).thenReturn(deal);
		doNothing().when(myPriceTransactionUtil).sendDmaapEvents(any(), any(), anyString(), anyMap());
		createTransactionServiceImpl.createTransaction(retreiveICBPSPRequest, nxSolutionDetail, null, null);
	}
	
	@Test
	public void testCallCreateTrans() throws SalesBusinessException {
		String transResponse = "{\"nxTransacId\":11, \"dealID\": \"1234\"}";
		Mockito.when(httpRest.callHttpRestClient(anyString(), any(), anyMap(), anyString(), anyMap(), anyString()))
		.thenReturn(transResponse);
		createTransactionServiceImpl.callCreateTrans();
	}

}
