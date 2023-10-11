package com.att.sales.nexxus.rateletter.serviceTest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.junit.jupiter.MockitoExtension;

import com.att.sales.framework.exception.SalesBusinessException;
import com.att.sales.framework.model.ServiceMetaData;
import com.att.sales.framework.service.BaseServiceImpl;
import com.att.sales.nexxus.dao.model.NxMpDeal;
import com.att.sales.nexxus.dao.model.NxMpPriceDetails;
import com.att.sales.nexxus.dao.model.solution.NxSolutionDetail;
import com.att.sales.nexxus.dao.repository.NxMpDealRepository;
import com.att.sales.nexxus.dao.repository.NxMpPriceDetailsRepository;
import com.att.sales.nexxus.dao.repository.NxSolutionDetailsRepository;
import com.att.sales.nexxus.dmaap.mr.util.DmaapPublishEventsService;
import com.att.sales.nexxus.rateletter.model.RateLetterStatusRequest;
import com.att.sales.nexxus.rateletter.model.RateLetterStatusResponse;
import com.att.sales.nexxus.rateletter.service.RateLetterStatusImpl;

@ExtendWith(MockitoExtension.class)
public class RateLetterStatusImplTest {

	@InjectMocks
	RateLetterStatusImpl rateLetterStatusImpl;

	@InjectMocks
	BaseServiceImpl baseServiceImpl;

	@Mock
	NxMpDealRepository nxMpDealRepository;

	@Mock
	NxSolutionDetailsRepository repository;

	@Mock
	DmaapPublishEventsService dmaapPublishEventsService;

	@Mock
	NxMpPriceDetailsRepository nxMpPriceDetailsRepository;

	@BeforeEach
	public void initializeServiceMetaData() {
		Map<String, Object> requestParams = new HashMap<>();
		requestParams.put(ServiceMetaData.OFFER, "AVPN");
		requestParams.put(ServiceMetaData.REST_REQUEST_START_TIME, System.currentTimeMillis());
		requestParams.put(ServiceMetaData.VERSION, "1.0");
		requestParams.put(ServiceMetaData.METHOD, "TestMethod");
		requestParams.put(ServiceMetaData.URI, "Testuri");
		ServiceMetaData.add(requestParams);
	}

	@Test
	public void rateLetterStatusTest() throws SalesBusinessException {

		RateLetterStatusRequest request = new RateLetterStatusRequest();

		RateLetterStatusResponse response = new RateLetterStatusResponse();

		request.setOptyId("1");
		request.setDealVersion("D1");
		request.setDealStatus("Y");
		request.setDealRevisionNumber("101");
		request.setDealId("101");
		request.setCustomerName("TestSingh");
		request.setCpqId("101");
		request.setDealStatus("APPROVED");
		request.setQuoteType("firm");
		NxMpDeal nxMpDeal = new NxMpDeal();
		NxMpDeal newNxMPDeal = new NxMpDeal();
		Mockito.when(nxMpDealRepository.findByTransactionId("1")).thenReturn(nxMpDeal);
		NxSolutionDetail nxSolutionDetail = new NxSolutionDetail();
		Mockito.when(repository.save(nxSolutionDetail)).thenReturn(nxSolutionDetail);
		List<NxMpPriceDetails> nxMpPriceDetailList = new ArrayList<>();
		Mockito.when(nxMpPriceDetailsRepository.findByNxTxnId(nxMpDeal.getNxTxnId())).thenReturn(nxMpPriceDetailList);
		NxMpPriceDetails nxMpPriceDetails = new NxMpPriceDetails();
		nxMpPriceDetails.setBeid("beid");
		nxMpPriceDetails.setComponentType("port");
		nxMpPriceDetailList.add(nxMpPriceDetails);
		rateLetterStatusImpl.rateLetterStatus(request);
		baseServiceImpl.setSuccessResponse(response);
		rateLetterStatusImpl.triggerDmaapEvent(request);
		rateLetterStatusImpl.updateDeal(nxMpDeal, newNxMPDeal);

	}

	@Test
	public void testRateLetterStatus() throws SalesBusinessException {
		RateLetterStatusRequest request = new RateLetterStatusRequest();

		RateLetterStatusResponse response = new RateLetterStatusResponse();

		request.setOptyId("1");
		request.setDealVersion("D1");
		request.setDealStatus("Y");
		request.setDealRevisionNumber("101");
		request.setDealId("101");
		request.setCustomerName("TestSingh");
		request.setCpqId("101");
		request.setDealStatus("APPROVED");
		request.setQuoteType("firm");
		NxMpDeal nxMpDeal = new NxMpDeal();
		NxMpDeal newNxMPDeal = new NxMpDeal();
		List<NxMpDeal> nxMpDeals = new ArrayList<>();
		nxMpDeals.add(nxMpDeal);
		String cpqId = request.getCpqId();
		Mockito.when(nxMpDealRepository.findAllByTransactionId(cpqId)).thenReturn(nxMpDeals);
		rateLetterStatusImpl.rateLetterStatus(request);
	}

	@Test
	public void triggerDmaapEventTest() {
		RateLetterStatusRequest request = new RateLetterStatusRequest();
		request.setCpqId("1");
		rateLetterStatusImpl.triggerDmaapEvent(request, true);
	}
}
