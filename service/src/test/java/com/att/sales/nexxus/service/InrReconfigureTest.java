package com.att.sales.nexxus.service;

import static org.junit.Assert.assertFalse;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyBoolean;
import static org.mockito.Mockito.anyList;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.anyMap;
import static org.mockito.Mockito.anySet;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.junit.jupiter.MockitoExtension;

import com.att.sales.framework.model.ServiceResponse;
import com.att.sales.framework.model.Status;
import com.att.sales.nexxus.constant.MyPriceConstants;
import com.att.sales.nexxus.constant.StringConstants;
import com.att.sales.nexxus.dao.model.NxAccessPricingData;
import com.att.sales.nexxus.dao.model.NxMpDeal;
import com.att.sales.nexxus.dao.model.NxRequestDetails;
import com.att.sales.nexxus.dao.model.solution.NxSolutionDetail;
import com.att.sales.nexxus.dao.repository.NxMpDealRepository;
import com.att.sales.nexxus.dao.repository.NxRequestDetailsRepository;
import com.att.sales.nexxus.fallout.model.FalloutDetailsRequest;
import com.att.sales.nexxus.model.UpdateTransactionOverrideRequest;
import com.att.sales.nexxus.myprice.transaction.model.CreateTransactionResponse;
import com.att.sales.nexxus.myprice.transaction.service.CopyTransactionServiceImpl;
import com.att.sales.nexxus.myprice.transaction.service.MyPriceTransactionUtil;

@ExtendWith(MockitoExtension.class)
public class InrReconfigureTest {

	@InjectMocks
	private InrReconfigure inrReconfigure;

	@Mock
	private MyPriceTransactionUtil myPriceTransactionUtil;
	
	@Mock
	private NxRequestDetailsRepository nxRequestDetailsRepository;
	
	@Mock 
	private CopyTransactionServiceImpl copyTransactionServiceImpl;
	
	@Mock
	private NxMpDealRepository nxMpDealRepository;
	
	@Mock
	private UpdateTransactionOverrideImpl updateTransactionOverrideImpl;
	
	@Mock
	private ProcessINRtoMP processINRtoMP;
	
	@Mock
	private MailServiceImpl mailServiceImpl;
	
	@Test
	public void testisNewLocationAdded() {
		List<NxRequestDetails> nxRequestDetailsList = new ArrayList<>();
		NxRequestDetails nxRequestDetails1 = new NxRequestDetails();
		nxRequestDetails1.setStatus(90L);
		nxRequestDetails1.setProduct("AVPN");
		nxRequestDetailsList.add(nxRequestDetails1);
		when(nxRequestDetailsRepository.findbyNSolutionIdAndActiveYnAndNxRequestGrpIds(any(),anyString(),anyList()))
		.thenReturn(nxRequestDetailsList);
		boolean result=inrReconfigure.isNewLocationAdded(new ArrayList<>(), new NxSolutionDetail());
		assertFalse(result);

		NxRequestDetails nxRequestDetails2 = new NxRequestDetails();
		nxRequestDetails2.setStatus(30L);
		nxRequestDetails2.setProduct("AVPN");
		nxRequestDetailsList.add(nxRequestDetails1);
		when(nxRequestDetailsRepository.findbyNSolutionIdAndActiveYnAndNxRequestGrpIds(any(),anyString(),anyList()))
		.thenReturn(nxRequestDetailsList);
		boolean result1=inrReconfigure.isNewLocationAdded(new ArrayList<>(), new NxSolutionDetail());
		assertFalse(result1);

	}

	@Test 
	public void testreconfigure() {
		Map<String, Object> paramMap= new HashMap<>();
		paramMap.put(MyPriceConstants.NX_AUDIT_ID, 67L);
		paramMap.put("createTransactionResponse", new CreateTransactionResponse());
		paramMap.put(MyPriceConstants.RESPONSE_STATUS, true);
		doNothing().when(processINRtoMP).process(any(),anyList(),anyMap(),anyMap(),anyString(),any(),any(),anyBoolean());
		doNothing().when(myPriceTransactionUtil).updateNxMpDealMpInd(anyString(),anyLong());
		doNothing().when(myPriceTransactionUtil).updateNxDesignAuditStatus(anyString(),anyString(),anyLong());
		doNothing().when(myPriceTransactionUtil).updateMpDealStatusByNxTxnId(anyString(), anyLong());
		NxMpDeal nxMpDeal=new NxMpDeal();
		when(nxMpDealRepository.findByNxTxnId(any())).thenReturn(nxMpDeal);
		FalloutDetailsRequest request = new FalloutDetailsRequest(); 
		inrReconfigure.reconfigure(paramMap, new CreateTransactionResponse(), new NxMpDeal(), new NxSolutionDetail(),
				new ArrayList<Long>(), new HashMap<>(), request);
	}
	
	@Test
	public void testcopyTransaction() {
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put(StringConstants.NEW_MY_PRICE_DEAL,new NxMpDeal());
		paramMap.put("CALL_CLEAN_SAVE",StringConstants.CONSTANT_Y);
		CreateTransactionResponse createTransactionResponse = new CreateTransactionResponse();
		createTransactionResponse.setNxTransacId(1L);
		createTransactionResponse.setMyPriceTransacId("1");
		doNothing().when(myPriceTransactionUtil).createTransactionResponse(any(), any(), anyString());
		Map<String, Object> updateResponse= new HashMap<String, Object>();
		updateResponse.put(MyPriceConstants.RESPONSE_STATUS,true);
		when(myPriceTransactionUtil.callUpdateCleanSave(any(),anyMap(),anyList())).thenReturn(updateResponse);
		UpdateTransactionOverrideRequest overrideRequest= new UpdateTransactionOverrideRequest();
		when(myPriceTransactionUtil.prepareUpdateTransactionOverrideRequest(any(),anyMap(),anySet(),any(),anyString())).thenReturn(overrideRequest);
		Status status=new Status();
		status.setCode("200");
		ServiceResponse overrideRes= new ServiceResponse(status);
		when(updateTransactionOverrideImpl.updateTransactionOverride(any())).thenReturn(null);
		FalloutDetailsRequest request = new FalloutDetailsRequest(); 
		inrReconfigure.copyTransaction(paramMap, createTransactionResponse, new NxMpDeal(), new ArrayList<Long>(), request);
		
		updateResponse.put(MyPriceConstants.RESPONSE_STATUS,false);
		when(myPriceTransactionUtil.callUpdateCleanSave(any(),anyMap(),anyList())).thenReturn(updateResponse);
		inrReconfigure.copyTransaction(paramMap, createTransactionResponse, new NxMpDeal(), new ArrayList<Long>(), request);

		paramMap.put("CALL_CLEAN_SAVE",StringConstants.CONSTANT_N);
		inrReconfigure.copyTransaction(paramMap, createTransactionResponse, new NxMpDeal(), new ArrayList<Long>(), request);
	}
}
