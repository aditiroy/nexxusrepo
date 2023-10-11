package com.att.sales.nexxus.custompricing.serviceTest;

import static org.mockito.Mockito.any;

import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.doNothing;

import java.util.ArrayList;
import java.util.Date;
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

import com.att.abs.ecrm.opty.v2.OptyInfoResponse;
import com.att.sales.framework.exception.SalesBusinessException;
import com.att.sales.framework.model.ServiceMetaData;
import com.att.sales.framework.model.ServiceResponse;
import com.att.sales.nexxus.custompricing.model.ActionDeterminant;
import com.att.sales.nexxus.custompricing.model.CustomPricingRequest;
import com.att.sales.nexxus.custompricing.model.GetCustomPricingResponse;
import com.att.sales.nexxus.custompricing.model.Solution;
import com.att.sales.nexxus.custompricing.model.SolutionDeterminant;
import com.att.sales.nexxus.custompricing.service.ICustomPricingSalesOneServiceImpl;
import com.att.sales.nexxus.dao.model.NxMpDeal;
import com.att.sales.nexxus.dao.model.solution.NxSolutionDetail;
import com.att.sales.nexxus.dao.repository.NxMpDealRepository;
import com.att.sales.nexxus.dao.repository.NxSolutionDetailsRepository;
import com.att.sales.nexxus.handlers.GetOptyInfoWSHandler;
import com.att.sales.nexxus.myprice.transaction.model.GetTransactionLineItem;
import com.att.sales.nexxus.myprice.transaction.model.GetTransactionLineResponse;
import com.att.sales.nexxus.myprice.transaction.model.GetTransactionResponse;
import com.att.sales.nexxus.myprice.transaction.service.GetTransactionLineServiceImpl;
import com.att.sales.nexxus.myprice.transaction.service.GetTransactionServiceImpl;
import com.att.sales.nexxus.rome.model.GetOptyRequest;
import com.att.sales.nexxus.rome.model.GetOptyResponse;
import com.att.sales.nexxus.rome.service.GetOptyInfoServiceImpl;
import com.att.sales.nexxus.ws.utility.SoapWSHandler;
import com.att.sales.nexxus.ws.utility.WSProcessingService;


@ExtendWith(MockitoExtension.class)
public class ICustomPricingSalesOneServiceTest {
	
	@InjectMocks
	ICustomPricingSalesOneServiceImpl icustomPricingSalesOneServiceImpl;
	
	@Mock
	NxMpDealRepository nxMpDealRepository;
	
	@Mock
	NxSolutionDetailsRepository repository;
	
	@Mock
	GetOptyInfoServiceImpl getOptyInfoServiceImpl;
	
	@Mock
	ServiceResponse serviceResponse;
	
	@Mock
	GetOptyInfoWSHandler getOptyInfoWSHandler;
	
	
	@Mock
	SoapWSHandler getOptyInfoWSClientUtility;
	
	@Mock
	WSProcessingService wsProcessingService;
	
	@Mock
	GetOptyResponse response;
	
	@Mock
	GetTransactionLineServiceImpl getTransactionLineServiceImpl;
	
	@Mock
	GetTransactionLineResponse getTxnLineResponse;
	
	@Mock
	GetTransactionServiceImpl getTransactionServiceImpl;
	
	@Mock
	Environment env;
	 
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
	public void testgetCustomPricing() throws SalesBusinessException {
		CustomPricingRequest request = new CustomPricingRequest();
		List<ActionDeterminant> actionDeterminantsList = new ArrayList<>();
		ActionDeterminant actionDeterminant = new ActionDeterminant();
		actionDeterminant.setActivity("RateLetter");
		List<String> component = actionDeterminant.getComponent();
		Solution solution = new Solution();
		SolutionDeterminant solutionDeterminant = new SolutionDeterminant();
		solutionDeterminant.setSolutionType("NS");
		solution.setUserId(null);
		solution.setDealId("126124");
		solution.setVersionNumber("3");
		solution.setRevisionNumber("2");
		solution.setExternalKey("6511142");
		solution.setProductNumber(null);
		actionDeterminantsList.add(actionDeterminant);
		request.setActionDeterminants(actionDeterminantsList);
		request.setSolution(solution);
		request.setUserId("hk544h");
		GetCustomPricingResponse getCPSOresponse = new GetCustomPricingResponse();
		List<NxSolutionDetail> solutionDetailsList = new ArrayList<>();
		NxSolutionDetail nxSolndetail = new NxSolutionDetail();
		nxSolndetail.setActiveYn("Y");
		nxSolndetail.setFlowType("");
		solutionDetailsList.add(nxSolndetail);
		GetOptyRequest getOptyRequest = new GetOptyRequest();
		getOptyRequest.setAction("action");
		getOptyRequest.setSolutionId(2L);
		getOptyRequest.setOptyId("optyId");
		getOptyRequest.setSolutionDescription("solutionDescription");
		getOptyRequest.setAttuid("ec006e");
		Map<String, Object> requestMap = new HashMap<>();
		requestMap.put("optyId", getOptyRequest.getOptyId());
		requestMap.put("attuid",   getOptyRequest.getAttuid());
		requestMap.put("action", getOptyRequest.getAction());
		requestMap.put("nxSolutionId", getOptyRequest.getNxSolutionId());
		requestMap.put("solutionDescription", getOptyRequest.getSolutionDescription());
		GetOptyResponse getOptyDetails = new GetOptyResponse();
		getOptyDetails.setAddress1("address1");
		getOptyDetails.setAddress2("address2");
		getOptyDetails.setCity("city");
		getOptyDetails.setState("state");
		getOptyDetails.setCountry("country");
		getOptyDetails.setPostalCode("postalCode");
		doNothing().when(getOptyInfoWSClientUtility).setWsName(Mockito.anyString());
		Mockito.when(getOptyInfoWSHandler.processResponse(any(), any())).thenReturn(Mockito.any());
		Mockito.when(wsProcessingService.initiateWebService(any(), any(), requestMap,eq(OptyInfoResponse.class))).thenReturn(Mockito.any());
		Mockito.when(getOptyInfoWSHandler.initiateGetOptyInfoWebService(requestMap)).thenReturn(Mockito.any());
		Mockito.when(getOptyInfoServiceImpl.performGetOptyInfo(getOptyRequest)).thenReturn(getOptyDetails);
		List<NxMpDeal> nxMpDealList = new ArrayList<>();
		NxMpDeal nxMpDeal = new NxMpDeal();
		nxMpDeal.setDealID("126124");
		nxMpDeal.setRevision("2");
		nxMpDeal.setVersion("3");
		nxMpDeal.setDealStatus("APPROVED");
		nxMpDeal.setAction("myprice");
		nxMpDeal.setActiveYN("Y");
		nxMpDeal.setCreatedDate(new Date());
		nxMpDeal.setModifiedDate(new Date());
		nxMpDeal.setOfferId("ASE");
		nxMpDeal.setTransactionId("0");
		nxMpDeal.setNxTxnId(551L);
		nxMpDeal.setSolutionId(2L);
		nxMpDealList.add(nxMpDeal);
		Mockito.when(nxMpDealRepository.findBydealIDVersnIdRevId(request.getSolution().getDealId(),
				request.getSolution().getVersionNumber(),request.getSolution().getRevisionNumber())).thenReturn(nxMpDealList);			
		Long nxSolutionId = nxMpDealList.get(0).getSolutionId();
		Mockito.when(repository.findByNxSolutionId(nxSolutionId)).thenReturn(solutionDetailsList.get(0));
		String transactionId = nxMpDeal.getTransactionId();
		GetTransactionResponse getTxnResp = new GetTransactionResponse();
		getTxnResp.setCustomerFirstName("customerFirstName");
		getTxnResp.setCustomerLastName("customerLastName");
		getTxnResp.setCustomerCompanyName("customerCompanyName");
		getTxnResp.setCustomerPhone("customerPhone");
		GetTransactionLineResponse getTxnLineResp = new GetTransactionLineResponse();
		List<GetTransactionLineItem> items = new ArrayList<>();
		GetTransactionLineItem item = new GetTransactionLineItem();
		item.setUsocId(null);
		item.setDocumentNumber("524");
		item.setBomId("BOM_AVPNAVPN_HCFEO_10_BOM_3");
		item.setIsProductRow("true");
		item.setExtendedPriceMRC("0.0");
		item.setExtendedPriceNRC("0.0");
		items.add(item);
		
		getTxnLineResp.setItems(items);
		Mockito.when(getTransactionServiceImpl.getTransactionSalesOne(transactionId)).thenReturn(getTxnResp);
		Mockito.when(getTransactionLineServiceImpl.getTransactionLineSalesOne(transactionId)).thenReturn(getTxnLineResp);
		icustomPricingSalesOneServiceImpl.getCustomPricingSalesOne(request);
		
	}
	

	@Test
	public void getCustomPricing() throws SalesBusinessException {
		CustomPricingRequest request = new CustomPricingRequest();
		List<ActionDeterminant> actionDeterminantsList = new ArrayList<>();
		ActionDeterminant actionDeterminant = new ActionDeterminant();
		actionDeterminant.setActivity("RateLetter");
		Solution solution = new Solution();
		SolutionDeterminant solutionDeterminant = new SolutionDeterminant();
		solutionDeterminant.setSolutionType("NS");
		solution.setUserId(null);
		solution.setDealId("126124");
		solution.setVersionNumber("3");
		solution.setRevisionNumber("2");
		solution.setExternalKey("6511142");
		solution.setProductNumber(null);
		actionDeterminantsList.add(actionDeterminant);
		request.setActionDeterminants(actionDeterminantsList);
		request.setSolution(solution);
		request.setUserId("hk544h");
		GetCustomPricingResponse getCPSOresponse = new GetCustomPricingResponse();
		List<NxSolutionDetail> solutionDetailsList = new ArrayList<>();
		NxSolutionDetail nxSolndetail = new NxSolutionDetail();
		nxSolndetail.setActiveYn("Y");
		nxSolndetail.setFlowType("");
		solutionDetailsList.add(nxSolndetail);
		GetOptyRequest getOptyRequest = new GetOptyRequest();
		getOptyRequest.setAction("action");
		getOptyRequest.setSolutionId(2L);
		getOptyRequest.setOptyId("optyId");
		getOptyRequest.setSolutionDescription("solutionDescription");
		getOptyRequest.setAttuid("ec006e");
		Map<String, Object> requestMap = new HashMap<>();
		requestMap.put("optyId", getOptyRequest.getOptyId());
		requestMap.put("attuid",   getOptyRequest.getAttuid());
		requestMap.put("action", getOptyRequest.getAction());
		requestMap.put("nxSolutionId", getOptyRequest.getNxSolutionId());
		requestMap.put("solutionDescription", getOptyRequest.getSolutionDescription());
		GetOptyResponse getOptyDetails = new GetOptyResponse();
		doNothing().when(getOptyInfoWSClientUtility).setWsName(Mockito.anyString());
		Mockito.when(getOptyInfoWSHandler.processResponse(any(), any())).thenReturn(Mockito.any());
		Mockito.when(wsProcessingService.initiateWebService(any(), any(), requestMap,eq(OptyInfoResponse.class))).thenReturn(Mockito.any());
		Mockito.when(getOptyInfoWSHandler.initiateGetOptyInfoWebService(requestMap)).thenReturn(Mockito.any());
		Mockito.when(getOptyInfoServiceImpl.performGetOptyInfo(getOptyRequest)).thenReturn(getOptyDetails);
		List<NxMpDeal> nxMpDealList = new ArrayList<>();
		NxMpDeal nxMpDeal = new NxMpDeal();
		nxMpDeal.setDealID("126124");
		nxMpDeal.setRevision("2");
		nxMpDeal.setVersion("3");
		nxMpDeal.setDealStatus("APPROVED");
		nxMpDeal.setAction("myprice");
		nxMpDeal.setActiveYN("Y");
		nxMpDeal.setCreatedDate(new Date());
		nxMpDeal.setModifiedDate(new Date());
		nxMpDeal.setOfferId("ASE");
		nxMpDeal.setTransactionId("0");
		nxMpDeal.setNxTxnId(551L);
		nxMpDeal.setSolutionId(2L);
		nxMpDealList.add(nxMpDeal);
		Mockito.when(nxMpDealRepository.findBydealIDVersnIdRevId(request.getSolution().getDealId(),
				request.getSolution().getVersionNumber(),request.getSolution().getRevisionNumber())).thenReturn(nxMpDealList);			
		Long nxSolutionId = nxMpDealList.get(0).getSolutionId();
		Mockito.when(repository.findByNxSolutionId(nxSolutionId)).thenReturn(solutionDetailsList.get(0));
		String transactionId = nxMpDeal.getTransactionId();
		GetTransactionResponse getTxnResp = new GetTransactionResponse();
		getTxnResp.setCustomerFirstName("customerFirstName");
		getTxnResp.setCustomerLastName("customerLastName");
		getTxnResp.setCustomerCompanyName("customerCompanyName");
		getTxnResp.setCustomerPhone("customerPhone");
		GetTransactionLineResponse getTxnLineResp = new GetTransactionLineResponse();
		List<GetTransactionLineItem> items = new ArrayList<>();
		GetTransactionLineItem item = new GetTransactionLineItem();
		item.setUsocId(null);
		item.setDocumentNumber("524");
		item.setBomId("BOM_AVPNAVPN_HCFEO_10_BOM_3");
		item.setIsProductRow("true");
		item.setExtendedPriceMRC("0.0");
		item.setExtendedPriceNRC("0.0");
		items.add(item);
		
		getTxnLineResp.setItems(items);
		Mockito.when(getTransactionServiceImpl.getTransactionSalesOne(transactionId)).thenReturn(getTxnResp);
		Mockito.when(getTransactionLineServiceImpl.getTransactionLineSalesOne(transactionId)).thenReturn(getTxnLineResp);
		icustomPricingSalesOneServiceImpl.getCustomPricingSalesOne(request);
		
	}
	

	
}
