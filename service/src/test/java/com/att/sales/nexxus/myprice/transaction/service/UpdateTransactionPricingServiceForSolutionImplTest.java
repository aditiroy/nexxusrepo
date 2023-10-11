package com.att.sales.nexxus.myprice.transaction.service;

import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyMap;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.env.Environment;

import com.att.sales.framework.exception.SalesBusinessException;
import com.att.sales.nexxus.constant.MyPriceConstants;
import com.att.sales.nexxus.dao.model.NxDesign;
import com.att.sales.nexxus.dao.model.NxDesignDetails;
import com.att.sales.nexxus.dao.model.NxMpDesignDocument;
import com.att.sales.nexxus.dao.model.NxMpPriceDetails;
import com.att.sales.nexxus.dao.repository.NxDesignDetailsRepository;
import com.att.sales.nexxus.dao.repository.NxMpDesignDocumentRepository;
import com.att.sales.nexxus.dao.repository.NxMpPriceDetailsRepository;
import com.att.sales.nexxus.handlers.ConfigureDesignWSHandler;
import com.att.sales.nexxus.myprice.transaction.model.UpdateTransactionLineItem;
import com.att.sales.nexxus.myprice.transaction.model.UpdateTransactionPricingRequest;
import com.att.sales.nexxus.myprice.transaction.model.UpdateTransactionPricingResponse;
import com.att.sales.nexxus.myprice.transaction.service.UpdateTransactionPricingServiceForSolutionImpl.PassingParam;
import com.att.sales.nexxus.reteriveicb.model.ComponentDetails;
import com.att.sales.nexxus.reteriveicb.model.PriceAttributes;
import com.att.sales.nexxus.reteriveicb.model.PriceDetails;
import com.att.sales.nexxus.util.HttpRestClient;
import com.att.sales.nexxus.util.RestClientUtil;

@ExtendWith(MockitoExtension.class)

public class UpdateTransactionPricingServiceForSolutionImplTest {
	@Spy
	@InjectMocks
	private UpdateTransactionPricingServiceForSolutionImpl updateTransactionPricingServiceForSolutionImpl;
	@Mock
	private Environment env;
	@Mock
	private RestClientUtil restClient;
	@Mock
	private NxMpDesignDocumentRepository nxMpDesignDocumentRepository;
	@Mock
	private ConfigureDesignWSHandler configureDesignWSHandler;
	@Mock
	private NxMpPriceDetailsRepository nxMpPriceDetailsRepository;
	@Mock
	private NxDesignDetailsRepository nxDesignDetailsRepository;
	
	@Mock
	private HttpRestClient httpRest;

	@Test
	public void updateTransactionPricingRequestTest() throws SalesBusinessException {
		PassingParam passingParam = updateTransactionPricingServiceForSolutionImpl.new PassingParam();
		doReturn(passingParam).when(updateTransactionPricingServiceForSolutionImpl).getPassingParam();
		Map<String, Object> designMap = new HashMap<>();
		Map<String, Object> apiResponse = new HashMap<String, Object>();
		UpdateTransactionPricingRequest request = new UpdateTransactionPricingRequest();
		doReturn(request).when(updateTransactionPricingServiceForSolutionImpl).getRequest(any());
		doReturn(apiResponse).when(updateTransactionPricingServiceForSolutionImpl)
				.callUpdateTransactionPricingRequestApi(any(), any());

		updateTransactionPricingServiceForSolutionImpl.updateTransactionPricingRequest(designMap);

		UpdateTransactionPricingResponse response = new UpdateTransactionPricingResponse();
		designMap.put(MyPriceConstants.MP_TRANSACTION_ID, MyPriceConstants.MP_TRANSACTION_ID);
		designMap.put(MyPriceConstants.OFFER_TYPE, "ADE");
		designMap.put(MyPriceConstants.NX_SOLIUTION_ID, 1L);
		designMap.put(MyPriceConstants.NX_TRANSACTION_ID, 1L);
		designMap.put(MyPriceConstants.CONTRACT_TERM, "1");

		apiResponse.put(MyPriceConstants.RESPONSE_CODE, 200);
		apiResponse.put(MyPriceConstants.RESPONSE_DATA, MyPriceConstants.RESPONSE_DATA);
		when(restClient.processResult(any(), any())).thenReturn(response);
//		doReturn(request).when(restClient).processResult(any(), any());
//		doReturn(null).when(updateTransactionPricingServiceForSolutionImpl).setSuccessResponse(any());
		updateTransactionPricingServiceForSolutionImpl.updateTransactionPricingRequest(designMap);

		designMap.put(MyPriceConstants.OFFER_TYPE, "ASE");
		designMap.put(MyPriceConstants.PRICE_UPDATE, "Y");
		apiResponse.put(MyPriceConstants.RESPONSE_DATA, null);
		updateTransactionPricingServiceForSolutionImpl.updateTransactionPricingRequest(designMap);

		doReturn(null).when(updateTransactionPricingServiceForSolutionImpl).getRequest(any());
		updateTransactionPricingServiceForSolutionImpl.updateTransactionPricingRequest(designMap);

		designMap.put(MyPriceConstants.OFFER_TYPE, "ADE");
		designMap.put(MyPriceConstants.CONTRACT_TERM, "a");
		updateTransactionPricingServiceForSolutionImpl.updateTransactionPricingRequest(designMap);
		
		designMap.put(MyPriceConstants.CONTRACT_TERM, "1");
		passingParam.getUnmatchingAsr().add("1");
		updateTransactionPricingServiceForSolutionImpl.updateTransactionPricingRequest(designMap);
	}

	@Test
	public void getRequestTest() {
		PassingParam passingParam = updateTransactionPricingServiceForSolutionImpl.new PassingParam();
		NxDesignDetails nxDesignDetails = new NxDesignDetails();
		NxDesign nxDesign = new NxDesign();
		nxDesign.setNxDesignId(1L);
		nxDesignDetails.setNxDesign(nxDesign);
		List<NxDesignDetails> nxDesignDetailsList = Arrays.asList(nxDesignDetails);
		when(nxDesignDetailsRepository.findDesignDetailsaByNxSolutionId(any())).thenReturn(nxDesignDetailsList);
		NxMpDesignDocument nxMpDesignDocument = new NxMpDesignDocument();
		List<NxMpDesignDocument> nxMpDesignDocumentList = Arrays.asList(nxMpDesignDocument);
		when(nxMpDesignDocumentRepository.findByNxTxnIdAndNxDesignIdIn(any(), any()))
				.thenReturn(nxMpDesignDocumentList);
		NxMpPriceDetails nxMpPriceDetails = new NxMpPriceDetails();
		List<NxMpPriceDetails> nxMpPriceDetailsList = Arrays.asList(nxMpPriceDetails);
		when(nxMpPriceDetailsRepository.findByNxTxnId(any())).thenReturn(nxMpPriceDetailsList);
		doReturn(passingParam).when(updateTransactionPricingServiceForSolutionImpl).processSite(any());
		when(nxMpPriceDetailsRepository.saveAll(passingParam.getToBeSavedNxMpPriceDetails())).thenReturn(null);
		
		updateTransactionPricingServiceForSolutionImpl.getRequest(passingParam);
		
		passingParam.setNxDesignIdList(Arrays.asList(1L));
		when(nxDesignDetailsRepository.findByNxDesignIdIn(any())).thenReturn(nxDesignDetailsList);
		PassingParam passingParamReturn = updateTransactionPricingServiceForSolutionImpl.new PassingParam();
		passingParamReturn.getItemMap().put("1", new UpdateTransactionLineItem());
		doReturn(passingParamReturn).when(updateTransactionPricingServiceForSolutionImpl).processSite(any());
		updateTransactionPricingServiceForSolutionImpl.getRequest(passingParam);
	}
	
	@Test
	public void processSiteTest() {
		PassingParam passingParam = updateTransactionPricingServiceForSolutionImpl.new PassingParam();
		NxDesignDetails nxDesignDetails = new NxDesignDetails();
		String designData = "{\"thirdPartyInd\": \"Y\"}";
		nxDesignDetails.setDesignData(designData);
		List<NxDesignDetails> nxDesignDetailsList = Arrays.asList(nxDesignDetails);
		passingParam.setNxDesignDetailsList(nxDesignDetailsList);
		passingParam.setFlowType("ASE");
		doNothing().when(updateTransactionPricingServiceForSolutionImpl).processPriceDetails(any(), any(), any(), any());
		
		updateTransactionPricingServiceForSolutionImpl.processSite(passingParam);
		
		passingParam.setFlowType("ASENOD");
		updateTransactionPricingServiceForSolutionImpl.processSite(passingParam);
		
		passingParam.setFlowType("ADE");
		designData = "{}";
		nxDesignDetails.setDesignData(designData);
		updateTransactionPricingServiceForSolutionImpl.processSite(passingParam);
	}
	
	@Test
	public void processPriceDetailsTest() {
		NxDesignDetails nxDesignDetails = new NxDesignDetails();
		NxDesign nxDesign = new NxDesign();
		nxDesign.setNxDesignId(1L);
		nxDesign.setAsrItemId("1");
		nxDesignDetails.setNxDesign(nxDesign);
		Map<String, String> usocIdMap = new HashMap<>();
		PassingParam passingParam = updateTransactionPricingServiceForSolutionImpl.new PassingParam();
		passingParam.setPriceScenarioId(1L);
		
		updateTransactionPricingServiceForSolutionImpl.processPriceDetails(passingParam, nxDesignDetails, null, usocIdMap);
		
		ComponentDetails componentDetails = new ComponentDetails();
		PriceDetails priceDetails = new PriceDetails();
		priceDetails.setComponentDetails(Arrays.asList(componentDetails));
		PriceAttributes priceAttributes = new PriceAttributes();
		priceAttributes.setBeid("beid");
		priceAttributes.setPriceScenarioId(1L);
		PriceAttributes priceAttributes1 = new PriceAttributes();
		priceAttributes1.setBeid("beid");
		priceAttributes1.setPriceScenarioId(1L);
		PriceAttributes priceAttributes2 = new PriceAttributes();
		priceAttributes2.setBeid("beid1");
		priceAttributes2.setPriceScenarioId(1L);
		PriceAttributes priceAttributes3 = new PriceAttributes();
		priceAttributes3.setBeid("beid1");
		priceAttributes3.setPriceScenarioId(1L);
		componentDetails.setPriceAttributes(Arrays.asList(priceAttributes, priceAttributes1, priceAttributes2, priceAttributes3));
		usocIdMap.put("beid", "beidTransfered");
		passingParam.setFlowType("ASE");
		Map<String, NxMpDesignDocument> nxMpDesignDocumentAseMap = new HashMap<>();
		NxMpDesignDocument nxMpDesignDocument = new NxMpDesignDocument();
		nxMpDesignDocument.setMpDocumentNumber(1L);
		nxMpDesignDocumentAseMap.put("1_beidTransfered", nxMpDesignDocument);
		NxMpDesignDocument nxMpDesignDocument1 = new NxMpDesignDocument();
		nxMpDesignDocument1.setMpDocumentNumber(2L);
		nxMpDesignDocumentAseMap.put("1_beid1", nxMpDesignDocument1);
		passingParam.setNxMpDesignDocumentAseMap(nxMpDesignDocumentAseMap);
		priceAttributes.setFrequency("MRC");
		priceAttributes.setRequestedRate(1.0F);
		priceAttributes1.setFrequency("NRC");
		priceAttributes1.setLocalListPrice(1.0);
		priceAttributes2.setFrequency("NRC");
		priceAttributes2.setRequestedDiscount(0.5F);
		priceAttributes2.setLocalListPrice(1.0);
		priceAttributes3.setFrequency("MRC");
		priceAttributes3.setRequestedRate(1.0F);
		
		doNothing().when(updateTransactionPricingServiceForSolutionImpl).updateToBeSavedNxMpPriceDetails(any(), any(), any(), any());
		
		updateTransactionPricingServiceForSolutionImpl.processPriceDetails(passingParam, nxDesignDetails, priceDetails, usocIdMap);
		
		passingParam.setFlowType("ASENOD");
		componentDetails.setPriceAttributes(Arrays.asList(priceAttributes));
		updateTransactionPricingServiceForSolutionImpl.processPriceDetails(passingParam, nxDesignDetails, priceDetails, usocIdMap);
		
		passingParam.getItemMap().clear();
		passingParam.setFlowType("ADE");
		componentDetails.setComponentId(1L);
		Map<String, NxMpDesignDocument> nxMpDesignDocumentAdeMap = new HashMap<>();
		nxMpDesignDocumentAdeMap.put("1_beidTransfered_1", nxMpDesignDocument);
		nxMpDesignDocumentAdeMap.put("1_beid1_1", nxMpDesignDocument1);
		passingParam.setNxMpDesignDocumentAdeMap(nxMpDesignDocumentAdeMap);
		priceAttributes.setTerm(1L);
		componentDetails.setPriceAttributes(Arrays.asList(priceAttributes, priceAttributes2));
		updateTransactionPricingServiceForSolutionImpl.processPriceDetails(passingParam, nxDesignDetails, priceDetails, usocIdMap);
		
		nxMpDesignDocumentAdeMap.clear();
		updateTransactionPricingServiceForSolutionImpl.processPriceDetails(passingParam, nxDesignDetails, priceDetails, usocIdMap);
	}
	
	@Test
	public void updateToBeSavedNxMpPriceDetailsTest() {
		PassingParam passingParam = updateTransactionPricingServiceForSolutionImpl.new PassingParam();
		passingParam.setTransactionUpdate("reconfigure");
		Map<String, NxMpPriceDetails> nxMpPriceDetailsMap = new HashMap<>();
		NxMpPriceDetails nxMpPriceDetails = new NxMpPriceDetails();
		nxMpPriceDetailsMap.put("1_MRC", nxMpPriceDetails);
		passingParam.setNxMpPriceDetailsMap(nxMpPriceDetailsMap);
		NxMpDesignDocument nxMpDesignDocument = new NxMpDesignDocument();
		nxMpDesignDocument.setMpDocumentNumber(1L);
		PriceAttributes priceAttributes = new PriceAttributes();
		priceAttributes.setFrequency("MRC");
		ComponentDetails componentDetails = new ComponentDetails();
		
		updateTransactionPricingServiceForSolutionImpl.updateToBeSavedNxMpPriceDetails(passingParam, nxMpDesignDocument, priceAttributes, componentDetails);
		
		nxMpPriceDetailsMap.clear();
		updateTransactionPricingServiceForSolutionImpl.updateToBeSavedNxMpPriceDetails(passingParam, nxMpDesignDocument, priceAttributes, componentDetails);
		
		passingParam.setTransactionUpdate(null);
		priceAttributes.setFrequency("NRC");
		updateTransactionPricingServiceForSolutionImpl.updateToBeSavedNxMpPriceDetails(passingParam, nxMpDesignDocument, priceAttributes, componentDetails);
	}
	
	@Test
	public void callUpdateTransactionPricingRequestApiTest() throws SalesBusinessException {
		String myPriceTxnId = "myPriceTxnId";
		UpdateTransactionPricingRequest request = new UpdateTransactionPricingRequest();
		when(env.getProperty(any())).thenReturn("");
		String res = "ddg";
		Mockito.when(httpRest.callHttpRestClient(anyString(), any(), anyMap(), anyString(), anyMap(), anyString()))
		.thenReturn(res);
		
		updateTransactionPricingServiceForSolutionImpl.callUpdateTransactionPricingRequestApi(myPriceTxnId, request);
		
		Mockito.when(httpRest.callHttpRestClient(anyString(), any(), anyMap(), anyString(), anyMap(), anyString()))
		.thenThrow(new SalesBusinessException());
		updateTransactionPricingServiceForSolutionImpl.callUpdateTransactionPricingRequestApi(myPriceTxnId, request);
	}
	
	@Test
	public void getPassingParamTest() {
		assertNotNull(updateTransactionPricingServiceForSolutionImpl.getPassingParam());
	}
}
