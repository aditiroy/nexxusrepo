package com.att.sales.nexxus.myprice.transaction.service;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.doNothing;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;


import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import com.att.sales.framework.exception.SalesBusinessException;
import com.att.sales.framework.model.ServiceResponse;
import com.att.sales.nexxus.constant.TDDConstants;
import com.att.sales.nexxus.dao.model.NxMpDeal;
import com.att.sales.nexxus.dao.model.solution.NxSolutionDetail;
import com.att.sales.nexxus.dao.repository.NxMpDealRepository;

@ExtendWith(MockitoExtension.class)

public class AutomationFlowHelperServiceTest {
	
	@InjectMocks
	AutomationFlowHelperService automationFlowHelperService;
	
	@Mock
	private NxMpDealRepository nxMpDealRepository;
	
	@Mock
	private UpdateTransactionQualifyService updateTransactionQualifyService;
	
	@Mock
	private UpdateTransactionRepriceServiceImpl updateTransactionRepriceService;
	
	@Mock
	private UpdateTransactionPriceScore updateTransactionPriceScore;
	
	@Mock
	private ServiceResponse response;
	
	@Mock
	private UpdateTransactionSubmitToApproval updateTransactionSubmitToApproval;
	
	@Mock
	private UpdateTransactionPrintDocServiceImpl updateTransactionPrintDocService; 
	
	@Mock
	private GenerateRateLetter generateRateLetter;
	
	private LinkedHashMap<String, Object> requestMap;
	
	private List<NxMpDeal> nxMpDeals;
	
	@Mock
	private GetTransactionLineServiceImpl getTransactionLineService;
	
	@BeforeEach
	public void init() {
		requestMap = new LinkedHashMap<String, Object>();
		requestMap.put(TDDConstants.SOLUTION_DATA, new NxSolutionDetail());
		nxMpDeals = new ArrayList<NxMpDeal>();
		NxMpDeal deal = new NxMpDeal();
		deal.setTransactionId("111111");
		nxMpDeals.add(deal);
	}
	
	@Test
	@Disabled
	public void testProcess() throws SalesBusinessException {
		Mockito.when(nxMpDealRepository.findBySolutionIdAndActiveYN(anyLong(), anyString())).thenReturn(nxMpDeals);
		Mockito.when(updateTransactionQualifyService.updateTransactionQualifyService(any())).thenReturn(response);
		Mockito.when(updateTransactionRepriceService.updateTransactionRepriceService(any())).thenReturn(response);
		Mockito.when(updateTransactionPriceScore.updateTransactionPriceScore(any())).thenReturn(response);
		Mockito.when(updateTransactionSubmitToApproval.updateTransactionSubmitToApproval(any())).thenReturn(true);
		Mockito.when(updateTransactionPrintDocService.updateTransactionPrintDocService(any())).thenReturn(response);
		Mockito.when(generateRateLetter.generateRateLetter(any())).thenReturn(response);
		automationFlowHelperService.process(requestMap);
	}
	
	@Test
	@Disabled
	public void testProcessElse() throws SalesBusinessException {
		Mockito.when(nxMpDealRepository.findBySolutionIdAndActiveYN(anyLong(), anyString())).thenReturn(nxMpDeals);
		Mockito.when(updateTransactionQualifyService.updateTransactionQualifyService(any())).thenReturn(response);
		Mockito.when(updateTransactionRepriceService.updateTransactionRepriceService(any())).thenReturn(response);
		Mockito.when(updateTransactionPriceScore.updateTransactionPriceScore(any())).thenReturn(response);
		Mockito.when(updateTransactionSubmitToApproval.updateTransactionSubmitToApproval(any())).thenReturn(false);
		doNothing().when(getTransactionLineService).publishDmaapForAutomationFlow(any(), anyString());
		automationFlowHelperService.process(requestMap);
	}
	
	@Test
	public void testProcessException() throws SalesBusinessException {
		Mockito.when(nxMpDealRepository.findBySolutionIdAndActiveYN(anyLong(), anyString())).thenReturn(nxMpDeals);
		Mockito.when(updateTransactionQualifyService.updateTransactionQualifyService(any())).thenThrow(Exception.class);
		doNothing().when(getTransactionLineService).publishDmaapForAutomationFlow(any(), anyString());
		automationFlowHelperService.process(requestMap);
	}

}
