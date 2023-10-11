/**
 * 
 */
package com.att.sales.nexxus.myprice.transaction.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyInt;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import com.att.sales.framework.exception.SalesBusinessException;
import com.att.sales.framework.model.ServiceMetaData;
import com.att.sales.framework.model.ServiceResponse;
import com.att.sales.framework.model.Status;
import com.att.sales.nexxus.common.CommonConstants;
import com.att.sales.nexxus.common.MessageConstants;
import com.att.sales.nexxus.constant.InrConstants;
import com.att.sales.nexxus.constant.MyPriceConstants;
import com.att.sales.nexxus.constant.StringConstants;
import com.att.sales.nexxus.constant.TDDConstants;
import com.att.sales.nexxus.dao.model.NxDesign;
import com.att.sales.nexxus.dao.model.NxDesignAudit;
import com.att.sales.nexxus.dao.model.NxLookupData;
import com.att.sales.nexxus.dao.model.NxMpDeal;
import com.att.sales.nexxus.dao.model.NxMpDesignDocument;
import com.att.sales.nexxus.dao.model.NxMpSiteDictionary;
import com.att.sales.nexxus.dao.model.NxRequestDetails;
import com.att.sales.nexxus.dao.model.solution.NxSolutionDetail;
import com.att.sales.nexxus.dao.repository.NxDesignAuditRepository;
import com.att.sales.nexxus.dao.repository.NxDesignRepository;
import com.att.sales.nexxus.dao.repository.NxMpDealRepository;
import com.att.sales.nexxus.dao.repository.NxMpDesignDocumentRepository;
import com.att.sales.nexxus.dao.repository.NxMpPriceDetailsRepository;
import com.att.sales.nexxus.dao.repository.NxMpSiteDictionaryRepository;
import com.att.sales.nexxus.dao.repository.NxRequestDetailsRepository;
import com.att.sales.nexxus.dao.repository.NxSolutionDetailsRepository;
import com.att.sales.nexxus.dao.repository.SalesMsDao;
import com.att.sales.nexxus.dmaap.mr.util.DmaapPublishEventsService;
import com.att.sales.nexxus.handlers.ConfigureDesignWSHandler;
import com.att.sales.nexxus.handlers.ConfigureSolnAndProductWSHandler;
import com.att.sales.nexxus.model.UpdateTransactionOverrideRequest;
import com.att.sales.nexxus.myprice.transaction.model.ConfigureSolnAndProductResponse;
import com.att.sales.nexxus.myprice.transaction.model.CreateTransactionResponse;
import com.att.sales.nexxus.reteriveicb.model.Circuit;
import com.att.sales.nexxus.reteriveicb.model.Component;
import com.att.sales.nexxus.reteriveicb.model.Offer;
import com.att.sales.nexxus.reteriveicb.model.Port;
import com.att.sales.nexxus.reteriveicb.model.References;
import com.att.sales.nexxus.reteriveicb.model.RetreiveICBPSPRequest;
import com.att.sales.nexxus.reteriveicb.model.Site;
import com.att.sales.nexxus.reteriveicb.model.Solution;
import com.att.sales.nexxus.reteriveicb.model.UDFBaseData;
import com.att.sales.nexxus.service.NxMyPriceRepositoryServce;
import com.att.sales.nexxus.service.PedSnsdServiceUtil;
import com.att.sales.nexxus.service.ProcessPDtoMPRestUtil;
import com.att.sales.nexxus.service.SubmitToMyPriceService;
import com.att.sales.nexxus.service.UpdateTransactionOverrideImpl;
import com.att.sales.nexxus.util.JsonPathUtil;
import com.att.sales.nexxus.util.NexxusJsonUtility;
import com.att.sales.nexxus.util.RestClientUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * @author ShruthiCJ
 *
 */
@ExtendWith(MockitoExtension.class)

public class MyPriceTransactionUtilTest {
	@Spy
	@InjectMocks
	MyPriceTransactionUtil myPriceTransactionUtil;

	@Mock
	private SubmitToMyPriceService submitToMyPriceService;

	@Mock
	private UpdateTransactionCleanSaveFMO updateTransactionCleanSaveFMO;

	@Mock
	private CreateTransactionService createTransactionService;

	@Mock
	private IUpdateTransaction iUpdateTransaction;

	@Mock
	private ConfigureSolnAndProductWSHandler configureSolnAndProductWSHandler;

	@Mock
	private ConfigureDesignWSHandler configureDesignWSHandler;

	@Mock
	private UpdateTransactionPricingFMOServiceImpl updateTransactionPricingFMOServiceImpl;

	@Mock
	private UpdateTransactionPricingServiceImpl updateTransactionPricingServiceImpl;

	@Mock
	private NxDesignAuditRepository nxDesignAuditRepository;

	@Mock
	private NxMpSiteDictionaryRepository nxMpSiteDictionaryRepository;

	@Mock
	private PedSnsdServiceUtil pedSnsdServiceUtil;

	@Mock
	private JsonPathUtil jsonPathUtil;

	@Mock
	private CopyTransactionServiceImpl copyTransactionServiceImpl;

	@Mock
	private UpdateTransactionOverrideImpl updateTransactionOverrideImpl;

	@Mock
	private NxMpDealRepository nxMpDealRepository;

	@Mock
	private SalesMsDao salesMsDao;

	@Mock
	private RemoveTransactionServiceLineImpl removeTransactionServiceLineImpl;

	@Mock
	private NxMpDesignDocumentRepository designDocumentRepo;

	@Mock
	private ObjectMapper mapper;

	@Mock
	private NxDesignRepository nxDesignRepository;

	@Mock
	private NxMpSiteDictionaryRepository siteRepo;

	@Mock
	private EntityManager em;

	@Mock
	private NxSolutionDetailsRepository nxSolutionDetailsRepository;

	@Mock
	private DmaapPublishEventsService dmaapPublishEventsService;

	@Mock
	private NxMpPriceDetailsRepository priceDetailsRepo;

	@Mock
	private NexxusJsonUtility nexxusJsonUtility;

	@Mock
	private RestClientUtil restClient;

	@Mock
	private NxMyPriceRepositoryServce nxMyPriceRepositoryServce;

	@Mock
	private NxRequestDetailsRepository nxRequestDetailsRepository;

	@Mock
	private ProcessPDtoMPRestUtil processPDtoMPRestUtil;
	
	private ObjectMapper realMapper = new ObjectMapper();
	
	@Mock
	private NxDesignAudit designAudit;
	
	@Mock
	private UpdateTxnSiteUploadServiceImpl updateTxnSiteUploadServiceImpl;

	@Test
	public void saveNxDesignAuditTest() {
		RetreiveICBPSPRequest retreiveICBPSPRequest = new RetreiveICBPSPRequest();
		Map<String, Object> requestParams = new HashMap<>();
		requestParams.put("k", "V");
		ServiceMetaData.add(requestParams);
		myPriceTransactionUtil.saveNxDesignAudit(null, null, retreiveICBPSPRequest, null, null);
		ServiceMetaData.getThreadLocal().remove();
	}
	
	@Test
	public void saveNxDesignAudit() {
		Map<String, Object> requestParams = new HashMap<>();
		requestParams.put("k", "V");
		ServiceMetaData.add(requestParams);
		Mockito.when(nxDesignAuditRepository.save(any(NxDesignAudit.class))).thenReturn(designAudit);
		myPriceTransactionUtil.saveNxDesignAudit(null, null);
		ServiceMetaData.getThreadLocal().remove();
	}

	@Test
	public void updateNxDesignAuditStatusTest() {
		myPriceTransactionUtil.updateNxDesignAuditStatus(null, null, 1L);
	}

	@Test
	public void updateNxMpDealMpIndTest() {
		myPriceTransactionUtil.updateNxMpDealMpInd(null, null);
	}

	@Test
	public void updateMpDealStatusByNxTxnIdTest() {
		myPriceTransactionUtil.updateMpDealStatusByNxTxnId(null, null);
	}

	@Test
	public void createAndUpdateTranscTest() {
		RetreiveICBPSPRequest retreiveICBPSPRequest = new RetreiveICBPSPRequest();
		Solution solution = new Solution();
		solution.setPriceScenarioId(1L);
		retreiveICBPSPRequest.setSolution(solution);
		NxSolutionDetail nxSolutionDetail = new NxSolutionDetail();
		Map<String, Object> result = new HashMap<>();
		result.put(MyPriceConstants.FLOW_TYPE, "fmo");
		Long nxTxnId = 1L;
		Map<String, Object> response = new HashMap<>();
		when(createTransactionService.createTransaction(any(), any(), any(), any())).thenReturn(response);
		NxDesignAudit nxDesignAudit = new NxDesignAudit();
		nxDesignAudit.setNxAuditId(1L);
		when(nxDesignAuditRepository.findByNxRefIdAndTransaction(any(), any())).thenReturn(nxDesignAudit);
		doNothing().when(myPriceTransactionUtil).updateNxDesignAuditStatus(any(), any(), anyLong());
		myPriceTransactionUtil.createAndUpdateTransc(retreiveICBPSPRequest, nxSolutionDetail, result, nxTxnId);

		doReturn(1L).when(myPriceTransactionUtil).saveNxDesignAudit(any(), any(), any(), any(), any());
		myPriceTransactionUtil.createAndUpdateTransc(retreiveICBPSPRequest, nxSolutionDetail, result, null);

		CreateTransactionResponse createTransactionResponse = new CreateTransactionResponse();
		createTransactionResponse.setSuccess(true);
		response.put("createTransactionRes", createTransactionResponse);
		Map<String, Object> updateResponse = new HashMap<>();
		doReturn(updateResponse).when(myPriceTransactionUtil).callUpdateCleanSave(any(), any(), any(), any(), any());
		myPriceTransactionUtil.createAndUpdateTransc(retreiveICBPSPRequest, nxSolutionDetail, result, nxTxnId);

		updateResponse.put(MyPriceConstants.RESPONSE_STATUS, true);
		myPriceTransactionUtil.createAndUpdateTransc(retreiveICBPSPRequest, nxSolutionDetail, result, null);
	}

	@Test
	public void callUpdateCleanSaveTest() throws SalesBusinessException {
		RetreiveICBPSPRequest retreiveICBPSPRequest = new RetreiveICBPSPRequest();
		CreateTransactionResponse createTransactionResponse = new CreateTransactionResponse();
		Long nxAuditId = 1L;
		NxSolutionDetail nxSolutionDetail = new NxSolutionDetail();
		Map<String, Object> result = new HashMap<>();
		result.put(MyPriceConstants.FLOW_TYPE, MyPriceConstants.SOURCE_FMO);
		Map<String, Object> updateResponse = new HashMap<String, Object>();
		when(updateTransactionCleanSaveFMO.updateTransactionCleanSave(any(), any(), any())).thenReturn(updateResponse);
		doNothing().when(myPriceTransactionUtil).sendDmaapEvents(any(), any(), any(), any());
		myPriceTransactionUtil.callUpdateCleanSave(retreiveICBPSPRequest, createTransactionResponse, nxAuditId,
				nxSolutionDetail, result);

		updateResponse.put(MyPriceConstants.RESPONSE_STATUS, true);
		myPriceTransactionUtil.callUpdateCleanSave(retreiveICBPSPRequest, createTransactionResponse, nxAuditId,
				nxSolutionDetail, result);

		result.put(MyPriceConstants.FLOW_TYPE, "notFmo");
		when(iUpdateTransaction.updateTransactionCleanSave(any(), any(), any())).thenReturn(updateResponse);
		myPriceTransactionUtil.callUpdateCleanSave(retreiveICBPSPRequest, createTransactionResponse, nxAuditId,
				nxSolutionDetail, result);
		
		SalesBusinessException salesBusinessException = new SalesBusinessException(MessageConstants.INVALID_OPTYID_OR_HRID);
		when(iUpdateTransaction.updateTransactionCleanSave(any(), any(), any())).thenThrow(salesBusinessException);
		myPriceTransactionUtil.callUpdateCleanSave(retreiveICBPSPRequest, createTransactionResponse, nxAuditId,
				nxSolutionDetail, result);
	}

	@Test
	public void configAndUpdatePricingTest() {
		CreateTransactionResponse response = new CreateTransactionResponse();
		ObjectNode siteJson = realMapper.createObjectNode();
		NxDesign nxDesign = new NxDesign();
		Map<String, Object> paramMap = new HashMap<>();
		String flowType = "fmo";
		Map<String, Object> requestMetaDataMap = new HashMap<>();
		paramMap.put(InrConstants.REQUEST_META_DATA_KEY, requestMetaDataMap);
		paramMap.put(StringConstants.TRANSACTION_TYPE, StringConstants.TRANSACTION_TYPE_NEW);
		Map<String, Object> responseMap = new HashMap<>();
		doReturn(responseMap).when(myPriceTransactionUtil).callConfigureSolutionProduct(any());
		responseMap.put(MyPriceConstants.RESPONSE_STATUS, true);
		doReturn(responseMap).when(myPriceTransactionUtil).callConfigureDesign(any());
		myPriceTransactionUtil.configAndUpdatePricing(response, siteJson, nxDesign, paramMap, flowType);
		
		paramMap.put(StringConstants.TRANSACTION_TYPE, StringConstants.TRANSACTION_TYPE_EXISTING);
		paramMap.put("IS_RECONFIGURE", StringConstants.CONSTANT_N);
		doReturn(true).when(myPriceTransactionUtil).removeTransactionServiceLineImpl(any());
		siteJson.put("designStatus", StringConstants.DESIGN_NEW);
		doReturn(responseMap).when(myPriceTransactionUtil).callUpdatePricing(any());
		myPriceTransactionUtil.configAndUpdatePricing(response, siteJson, nxDesign, paramMap, flowType);
		
		siteJson.put("designStatus", StringConstants.DESIGN_CANCEL);
		doNothing().when(myPriceTransactionUtil).deleteSiteFromSiteDic(any(), any());
		paramMap.put(MyPriceConstants.CURRENT_NX_SITE_ID, new ArrayList<Long>());
		myPriceTransactionUtil.configAndUpdatePricing(response, siteJson, nxDesign, paramMap, flowType);
		
		siteJson.put("designStatus", StringConstants.DESIGN_UPDATE);
		myPriceTransactionUtil.configAndUpdatePricing(response, siteJson, nxDesign, paramMap, flowType);
		
		siteJson.put("designModifiedInd", StringConstants.CONSTANT_N);
		myPriceTransactionUtil.configAndUpdatePricing(response, siteJson, nxDesign, paramMap, flowType);
	}
	
	@Test
	public void callConfigureSolutionProductTest() throws SalesBusinessException {
		Map<String, Object> designMap = new HashMap<>();
		doNothing().when(myPriceTransactionUtil).copyTime(any(), any());
		doNothing().when(myPriceTransactionUtil).prepareResponseMap(any(), any());
		myPriceTransactionUtil.callConfigureSolutionProduct(designMap);
		
		ConfigureSolnAndProductResponse cspResponse = new ConfigureSolnAndProductResponse();
		when(configureSolnAndProductWSHandler.initiateConfigSolnAndProdWebService(any())).thenReturn(cspResponse);
		myPriceTransactionUtil.callConfigureSolutionProduct(designMap);
		
		SalesBusinessException salesBusinessException = new SalesBusinessException("message");
		when(configureSolnAndProductWSHandler.initiateConfigSolnAndProdWebService(any())).thenThrow(salesBusinessException);
		myPriceTransactionUtil.callConfigureSolutionProduct(designMap);
	}
	
	@Test
	public void callConfigureDesignTest() throws SalesBusinessException {
		Map<String, Object> designMap = new HashMap<>();
		when(configureDesignWSHandler.initiateConfigDesignWebService(any())).thenReturn(false);
		doNothing().when(myPriceTransactionUtil).prepareResponseMap(any(), any());
		doNothing().when(myPriceTransactionUtil).copyTime(any(), any());
		myPriceTransactionUtil.callConfigureDesign(designMap);
		
		when(configureDesignWSHandler.initiateConfigDesignWebService(any())).thenReturn(true);
		myPriceTransactionUtil.callConfigureDesign(designMap);
		
		SalesBusinessException salesBusinessException = new SalesBusinessException("message");
		when(configureDesignWSHandler.initiateConfigDesignWebService(any())).thenThrow(salesBusinessException);
		myPriceTransactionUtil.callConfigureDesign(designMap);
	}
	
	@Test
	public void callUpdatePricingTest() throws SalesBusinessException {
		Map<String, Object> designMap = new HashMap<>();
		doNothing().when(myPriceTransactionUtil).prepareResponseMap(any(), any());
		myPriceTransactionUtil.callUpdatePricing(designMap);
		
		designMap.put(MyPriceConstants.SOURCE, MyPriceConstants.SOURCE_FMO);
		designMap.put(MyPriceConstants.RESPONSE_STATUS, true);
		myPriceTransactionUtil.callUpdatePricing(designMap);
		
		designMap.remove(MyPriceConstants.SOURCE);
		SalesBusinessException salesBusinessException = new SalesBusinessException("message");
		when(updateTransactionPricingServiceImpl.updateTransactionPricingRequest(any())).thenThrow(salesBusinessException);
		myPriceTransactionUtil.callUpdatePricing(designMap);
	}
	
	@Test
	public void removeTransactionServiceLineImplTest() throws SalesBusinessException {
		Map<String, Object> designMap = new HashMap<>();
		List<NxMpDesignDocument> productLineIds = new ArrayList<>();
		when(designDocumentRepo.getMpProductLineIdByNxTxnIdAndNxDesignId(any(), any())).thenReturn(productLineIds);
		NxMpDesignDocument designDoc = new NxMpDesignDocument();
		productLineIds.add(designDoc);
		designDoc.setMpSolutionId("1");
		designDoc.setMpDocumentNumber(1L);
		designDoc.setMpProductLineId("1");
		myPriceTransactionUtil.removeTransactionServiceLineImpl(designMap);
		
		SalesBusinessException salesBusinessException = new SalesBusinessException("message");
		when(removeTransactionServiceLineImpl.removeTransactionLine(any())).thenThrow(salesBusinessException);
		myPriceTransactionUtil.removeTransactionServiceLineImpl(designMap);
	}
	
	@Test
	public void myPriceHeadersTest() {
		Map<String, String> res = myPriceTransactionUtil.myPriceHeaders();
		assertNotNull(res);
	}
	
	@Test
	public void getValuesFromRequestTest() {
		Set<Object> res = myPriceTransactionUtil.getValuesFromRequest(null, null);
		assertNull(res);
		
		when(jsonPathUtil.search(any(), any(), any())).thenCallRealMethod();
		String request = "{\r\n" + 
				"    \"site\": [{\r\n" + 
				"            \"siteId\": \"1\"\r\n" + 
				"        }\r\n" + 
				"    ]\r\n" + 
				"}";
		String path = "$..siteId";
		res = myPriceTransactionUtil.getValuesFromRequest(request, path);
		assertNotNull(res);
		
		path = "$..notExist";
		res = myPriceTransactionUtil.getValuesFromRequest(request, path);
		assertNull(res);
	}
	
	@Test
	public void reconfigureScenariosTest() {
		RetreiveICBPSPRequest retreiveICBPSPRequest = new RetreiveICBPSPRequest();
		Solution solution = new Solution();
		List<Offer> offers = new ArrayList<>();
		Offer offer = new Offer();
		offers.add(offer);
		solution.setOffers(offers);
		retreiveICBPSPRequest.setSolution(solution);
		List<NxMpDeal> nxMpDeal = new ArrayList<>();
		Map<String, Object> paramMap = new HashMap<>();
		offer.setOfferId("103");
		when(salesMsDao.getOfferNameByOfferId(anyInt())).thenReturn(StringConstants.OFFERNAME_ASE);
		doNothing().when(myPriceTransactionUtil).processReconfigure(any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any(),any());
		myPriceTransactionUtil.reconfigureScenarios(retreiveICBPSPRequest, nxMpDeal, paramMap);
		
		when(salesMsDao.getOfferNameByOfferId(anyInt())).thenReturn(StringConstants.OFFERNAME_ADE);
		myPriceTransactionUtil.reconfigureScenarios(retreiveICBPSPRequest, nxMpDeal, paramMap);
	}
	
	@Test
	public void reconfigureScenariosIpneTest() {
		RetreiveICBPSPRequest retreiveICBPSPRequest = new RetreiveICBPSPRequest();
		Solution solution = new Solution();
		List<Offer> offers = new ArrayList<>();
		Offer offer = new Offer();
		offers.add(offer);
		solution.setOffers(offers);
		solution.setSourceName("IPNE");
		retreiveICBPSPRequest.setSolution(solution);
		List<NxMpDeal> nxMpDeal = new ArrayList<>();
		Map<String, Object> paramMap = new HashMap<>();
		offer.setOfferId("210");
		when(salesMsDao.getOfferNameByOfferId(anyInt())).thenReturn(StringConstants.OFFERNAME_EPLSWAN);
		doNothing().when(myPriceTransactionUtil).processReconfigure(any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any(),any());
		myPriceTransactionUtil.reconfigureScenarios(retreiveICBPSPRequest, nxMpDeal, paramMap);
		
		when(salesMsDao.getOfferNameByOfferId(anyInt())).thenReturn(StringConstants.OFFERNAME_EPLSWAN);
		myPriceTransactionUtil.reconfigureScenarios(retreiveICBPSPRequest, nxMpDeal, paramMap);
	}
	
	@Test
	public void reconfigureScenariosIpneMutipleOfferTest() {
		RetreiveICBPSPRequest retreiveICBPSPRequest = new RetreiveICBPSPRequest();
		Solution solution = new Solution();
		List<Offer> offers = new ArrayList<>();
		Offer offerAde = new Offer();
		Offer offerIpne = new Offer();
		offers.add(offerAde);
		offers.add(offerIpne);
		solution.setOffers(offers);
		solution.setSourceName("IPNE");
		retreiveICBPSPRequest.setSolution(solution);
		List<NxMpDeal> nxMpDeal = new ArrayList<>();
		Map<String, Object> paramMap = new HashMap<>();
		offerIpne.setOfferId("210");
		offerAde.setOfferId("120");
		when(salesMsDao.getOfferNameByOfferId(anyInt())).thenReturn(StringConstants.OFFERNAME_EPLSWAN);
		doNothing().when(myPriceTransactionUtil).processReconfigure(any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any(),any());
		myPriceTransactionUtil.reconfigureScenarios(retreiveICBPSPRequest, nxMpDeal, paramMap);
		
		when(salesMsDao.getOfferNameByOfferId(anyInt())).thenReturn(StringConstants.OFFERNAME_EPLSWAN);
		myPriceTransactionUtil.reconfigureScenarios(retreiveICBPSPRequest, nxMpDeal, paramMap);
	}
	@Test
	public void processReconfigureTest() {
		List<NxMpDeal> nxMpDeal = new ArrayList<>();
		NxMpDeal nxMpDealElement = new NxMpDeal();
		nxMpDealElement.setSolutionId(1L);
		nxMpDeal.add(nxMpDealElement);
		Map<String, Object> paramMap = new HashMap<>();
		doReturn(true).when(myPriceTransactionUtil).checkDesignStatusForR(any());
		doReturn(true).when(myPriceTransactionUtil).checkDesignStatus(any(), any());
		doReturn(false).when(myPriceTransactionUtil).checkPriceStatus(any());
		List<NxSolutionDetail> nxSolutionDetail = new ArrayList<>();
		NxSolutionDetail nxSolutionDetailElement = new NxSolutionDetail();
		nxSolutionDetail.add(nxSolutionDetailElement);
		when(nxSolutionDetailsRepository.findByNxSolutionId(anyLong())).thenReturn(nxSolutionDetail.get(0));
		NxMpDeal deal = new NxMpDeal();
		doReturn(deal).when(myPriceTransactionUtil).getNxMpDeal(any(), any());
		doNothing().when(myPriceTransactionUtil).createTransactionResponse(any(), any(), any());
		RetreiveICBPSPRequest retreiveICBPSPRequestObj = new RetreiveICBPSPRequest();
		Solution solution=new Solution();
		solution.setPriceScenarioId(1L);
		retreiveICBPSPRequestObj.setSolution(solution);
		myPriceTransactionUtil.processReconfigure(null, null, null, null, null, nxMpDeal, paramMap, null, null, null, null,retreiveICBPSPRequestObj);
		
		doReturn(false).when(myPriceTransactionUtil).checkDesignStatusForR(any());
		doReturn(false).when(myPriceTransactionUtil).checkDesignStatus(any(), any());
		doReturn(true).when(myPriceTransactionUtil).checkPriceStatus(any());
		paramMap.put(StringConstants.NEW_MY_PRICE_DEAL, deal);
		paramMap.put("CALL_CLEAN_SAVE", StringConstants.CONSTANT_Y);
		Map<String, Object> updateResponse = new HashMap<>();
		doReturn(updateResponse).when(myPriceTransactionUtil).callUpdateCleanSave(any(), any(), any(), any(), any());
		updateResponse.put(MyPriceConstants.RESPONSE_STATUS, false);
		myPriceTransactionUtil.processReconfigure(null, null, null, null, null, nxMpDeal, paramMap, null, null, null, null,retreiveICBPSPRequestObj);
		
		updateResponse.put(MyPriceConstants.RESPONSE_STATUS, true);
		doReturn(null).when(myPriceTransactionUtil).prepareUpdateTransactionOverrideRequest(any(), any(), any(), any(), any());
		ServiceResponse overrideRes = new ServiceResponse();
		Status status = new Status();
		status.setCode(CommonConstants.FAILURE_STATUS);
		overrideRes.setStatus(status);
		when(updateTransactionOverrideImpl.updateTransactionOverride(any())).thenReturn(overrideRes);
		myPriceTransactionUtil.processReconfigure(null, null, null, null, null, nxMpDeal, paramMap, null, null, null, null,retreiveICBPSPRequestObj);
		
		status.setCode(CommonConstants.SUCCESS_STATUS);
		myPriceTransactionUtil.processReconfigure(null, null, null, null, null, nxMpDeal, paramMap, null, null, null, null,retreiveICBPSPRequestObj);
		
		paramMap.remove(StringConstants.NEW_MY_PRICE_DEAL);
		myPriceTransactionUtil.processReconfigure(null, null, null, null, null, nxMpDeal, paramMap, null, null, null, null,retreiveICBPSPRequestObj);
	}
	
	@Test
	public void createTransactionResponseTest() {
		CreateTransactionResponse createTransactionResponse = new CreateTransactionResponse();
		NxMpDeal nxMpDeal = new NxMpDeal();
		myPriceTransactionUtil.createTransactionResponse(createTransactionResponse, nxMpDeal, null);
		
		myPriceTransactionUtil.createTransactionResponse(createTransactionResponse, nxMpDeal, "offerName");
	}
	
	@Test
	public void checkDesignStatusTest() {
		boolean res = myPriceTransactionUtil.checkDesignStatus(null, null);
		assertFalse(res);
		
		Set<Object> set = new HashSet<>();
		set.add(StringConstants.CONSTANT_Y);
		res = myPriceTransactionUtil.checkDesignStatus(set, null);
		assertTrue(res);
	}
	
	@Test
	public void checkDesignStatusForRTest() {
		boolean res = myPriceTransactionUtil.checkDesignStatusForR(null);
		assertFalse(res);
		
		Set<Object> designStatusIndicators = new HashSet<>();
		designStatusIndicators.add(StringConstants.DESIGN_REOPEN);
		res = myPriceTransactionUtil.checkDesignStatusForR(designStatusIndicators);
		assertTrue(res);
	}
	
	@Test
	public void checkPriceStatusTest() {
		boolean res = myPriceTransactionUtil.checkPriceStatus(null);
		assertFalse(res);
		
		Set<Object> asePriceModifiedIds = new HashSet<>();
		asePriceModifiedIds.add(StringConstants.CONSTANT_Y);
		res = myPriceTransactionUtil.checkPriceStatus(asePriceModifiedIds);
		assertTrue(res);
	}
	
	@Test
	public void getSiteDictionaryTest() throws JSONException {
		NxMpDeal deal = new NxMpDeal();
		Set<Object> designStatusIndicators = new HashSet<>();
		designStatusIndicators.add(StringConstants.DESIGN_NEW);
		Offer offer = new Offer();
		List<Site> siteList = new ArrayList<>();
		Site site = new Site();
		siteList.add(site);
		offer.setSite(siteList);
		String offerName = StringConstants.OFFERNAME_ADE;
		NxMpSiteDictionary siteData = new NxMpSiteDictionary();
		when(siteRepo.findByNxTxnId(any())).thenReturn(siteData);
		Set<Object> adeNewSiteIds = new HashSet<>();
		doReturn(adeNewSiteIds).when(myPriceTransactionUtil).getValuesFromRequest(any(), any());
		site.setDesignStatus(StringConstants.DESIGN_NEW);
		ReflectionTestUtils.setField(myPriceTransactionUtil, "mapper", realMapper);
		doReturn(1L).when(myPriceTransactionUtil).getNxSiteId();
		String siteAddress = "{\r\n" + 
				"    \"siteAddress\": [{\r\n" + 
				"            \"addressLine\": \"1 2\"\r\n" + 
				"        }\r\n" + 
				"    ]\r\n" + 
				"}";
		siteData.setSiteAddress(siteAddress);
		myPriceTransactionUtil.getSiteDictionary(deal, designStatusIndicators, offer, offerName);
		
		siteData.setSiteAddress("{}");
		myPriceTransactionUtil.getSiteDictionary(deal, designStatusIndicators, offer, offerName);
		
		designStatusIndicators.clear();
		designStatusIndicators.add(StringConstants.DESIGN_CANCEL);
		offerName = StringConstants.OFFERNAME_ASE;
		site.setDesignStatus(StringConstants.DESIGN_CANCEL);
		siteData.setSiteAddress(siteAddress);
		site.setAddress1("1");
		site.setAddress2("2");
		myPriceTransactionUtil.getSiteDictionary(deal, designStatusIndicators, offer, offerName);
		
		offerName = StringConstants.OFFERNAME_ADE;
		site.setSiteId(1L);
		List<Circuit> circuitList = new ArrayList<>();
		Circuit circuit = new Circuit();
		circuitList.add(circuit);
		offer.setCircuit(circuitList);
		circuit.setDesignStatus(StringConstants.DESIGN_CANCEL);
		List<Component> componentList = new ArrayList<>();
		Component component = new Component();
		component.setComponentCodeId(1220L);
		componentList.add(component);
		circuit.setComponent(componentList);
		List<References> referencesList = new ArrayList<>();
		References references = new References();
		references.setReferenceId(1L);
		referencesList.add(references);
		component.setReferences(referencesList);
		myPriceTransactionUtil.getSiteDictionary(deal, designStatusIndicators, offer, offerName);
		
		designStatusIndicators.clear();
		designStatusIndicators.add(StringConstants.DESIGN_UPDATE);
		Map<Long, Map<String, String>> clliMap = new HashMap<>();
		doReturn(clliMap).when(myPriceTransactionUtil).getClliValuesforAllSites(any());
		offerName = StringConstants.OFFERNAME_ASE;
		site.setDesignStatus(StringConstants.DESIGN_UPDATE);
		doNothing().when(myPriceTransactionUtil).getUpdatedSiteJson(any(), any(), any(),any());
		myPriceTransactionUtil.getSiteDictionary(deal, designStatusIndicators, offer, offerName);
		
		offerName = StringConstants.OFFERNAME_ADE;
		circuit.setDesignStatus(StringConstants.DESIGN_UPDATE);
		myPriceTransactionUtil.getSiteDictionary(deal, designStatusIndicators, offer, offerName);
	}
	
	@Test
	public void getSiteDictionaryForIpneTest() throws JSONException {
		RetreiveICBPSPRequest retreiveICBPSPRequest = new RetreiveICBPSPRequest();
		NxSolutionDetail solutionData = new NxSolutionDetail();
		Map<String, Object> paramMap = new HashMap<>();
		paramMap.put(StringConstants.TRANSACTION_TYPE, StringConstants.TRANSACTION_TYPE_NEW);
		Solution solution = new Solution();
		retreiveICBPSPRequest.setSolution(solution);
		List<Offer> offers = new ArrayList<>();
		solution.setOffers(offers);
		Offer offerAde = new Offer();
		offerAde.setOfferId("120");
		offers.add(offerAde);
		
		NxMpDeal deal = new NxMpDeal();
		Set<Object> designStatusIndicators = new HashSet<>();
		designStatusIndicators.add(StringConstants.DESIGN_NEW);
		Offer offer = new Offer();
		List<Site> siteList = new ArrayList<>();
		Site site = new Site();
		siteList.add(site);
		offerAde.setSite(siteList);
		List<Long> nxSiteIdList= new ArrayList<>();
		String offerName = StringConstants.OFFERNAME_ADE;
		NxMpSiteDictionary siteData = new NxMpSiteDictionary();
		when(siteRepo.findByNxTxnId(any())).thenReturn(siteData);
		Set<Object> adeNewSiteIds = new HashSet<>();
		doReturn(adeNewSiteIds).when(myPriceTransactionUtil).getValuesFromRequest(any(), any());
		site.setDesignStatus(StringConstants.DESIGN_NEW);
		ReflectionTestUtils.setField(myPriceTransactionUtil, "mapper", realMapper);
		doReturn(1L).when(myPriceTransactionUtil).getNxSiteId();
		String siteAddress = "{\r\n" + 
				"    \"siteAddress\": [{\r\n" + 
				"            \"addressLine\": \"1 2\"\r\n" + 
				"        }\r\n" + 
				"    ]\r\n" + 
				"}"; 
		siteData.setSiteAddress(siteAddress);
		myPriceTransactionUtil.getSiteDictionaryForIpne(deal, designStatusIndicators, offer, offerName,retreiveICBPSPRequest,nxSiteIdList);
		
		siteData.setSiteAddress("{}");
		myPriceTransactionUtil.getSiteDictionaryForIpne(deal, designStatusIndicators, offer, offerName,retreiveICBPSPRequest,nxSiteIdList);
		
		designStatusIndicators.clear();
		designStatusIndicators.add(StringConstants.DESIGN_CANCEL);
		offerName = StringConstants.OFFERNAME_ASE;
		site.setDesignStatus(StringConstants.DESIGN_CANCEL);
		siteData.setSiteAddress(siteAddress);
		site.setAddress1("1");
		site.setAddress2("2");
		myPriceTransactionUtil.getSiteDictionaryForIpne(deal, designStatusIndicators, offer, offerName,retreiveICBPSPRequest,nxSiteIdList);
		
		offerName = StringConstants.OFFERNAME_ADE;
		site.setSiteId(1L);
		List<Circuit> circuitList = new ArrayList<>();
		Circuit circuit = new Circuit();
		circuitList.add(circuit);
		offerAde.setCircuit(circuitList);
		circuit.setDesignStatus(StringConstants.DESIGN_CANCEL);
		List<Component> componentList = new ArrayList<>();
		Component component = new Component();
		component.setComponentCodeId(1220L);
		componentList.add(component);
		circuit.setComponent(componentList);
		List<References> referencesList = new ArrayList<>();
		References references = new References();
		references.setReferenceId(1L);
		referencesList.add(references);
		component.setReferences(referencesList);
		myPriceTransactionUtil.getSiteDictionaryForIpne(deal, designStatusIndicators, offer, offerName,retreiveICBPSPRequest,nxSiteIdList);
		
		designStatusIndicators.clear();
		designStatusIndicators.add(StringConstants.DESIGN_UPDATE);
		Map<Long, Map<String, String>> clliMap = new HashMap<>();
		doReturn(clliMap).when(myPriceTransactionUtil).getClliValuesforAllSites(any());
		offerName = StringConstants.OFFERNAME_EPLSWAN;
		site.setDesignStatus(StringConstants.DESIGN_UPDATE);
		doNothing().when(myPriceTransactionUtil).getUpdatedSiteJson(any(), any(), any(),any());
		myPriceTransactionUtil.getSiteDictionaryForIpne(deal, designStatusIndicators, offer, offerName,retreiveICBPSPRequest,nxSiteIdList);
		
		offerName = StringConstants.OFFERNAME_ADE;
		circuit.setDesignStatus(StringConstants.DESIGN_UPDATE);
		myPriceTransactionUtil.getSiteDictionaryForIpne(deal, designStatusIndicators, offer, offerName,retreiveICBPSPRequest,nxSiteIdList);
	}
	
	@Test
	public void getUpdatedSiteJsonTest() throws JSONException {
		Site site = new Site();
		site.setSiteId(1L);
		JSONObject siteDict = new JSONObject();
		siteDict.put("pricerdSiteId", 1L);
		Map<Long, Map<String, String>> clliMap = new HashMap<>();
		Map<String, String> currentClliMap = new HashMap<>();
		clliMap.put(1L, currentClliMap);
		myPriceTransactionUtil.getUpdatedSiteJson(site, siteDict, clliMap,"ASE");
		
		currentClliMap.put("swcLLi", "swcLLi");
		currentClliMap.put("buildingClli", "buildingClli");
		myPriceTransactionUtil.getUpdatedSiteJson(site, siteDict, clliMap,"ADE");
	}
	
	@Test
	public void getClliValuesforAllSitesTest() {
		Offer offer = new Offer();
		offer.setOfferId("103");
		List<Site> sites = new ArrayList<>();
		offer.setSite(sites);
		Site currentSite = new Site();
		sites.add(currentSite);
		List<Port> ports = new ArrayList<>();
		currentSite.setDesignSiteOfferPort(ports);
		Port port = new Port();
		ports.add(port);
		List<Component> components = new ArrayList<>();
		port.setComponent(components);
		Component component = new Component();
		components.add(component);
		component.setComponentCodeType("Port");
		List<UDFBaseData> udfList = new ArrayList<>();
		component.setDesignDetails(udfList);
		Map<String, String> getCLLiValues = new HashMap<>();
		doReturn(getCLLiValues).when(myPriceTransactionUtil).getCLLiValues(any());
		myPriceTransactionUtil.getClliValuesforAllSites(offer);
		
		offer.setOfferId("120");
		List<Circuit> circuits = new ArrayList<>();
		offer.setCircuit(circuits);
		Circuit circuit = new Circuit();
		circuits.add(circuit);
		circuit.setComponent(components);
		component.setComponentCodeId(1220L);
		List<References> references = new ArrayList<>();
		component.setReferences(references);
		References reference = new References();
		references.add(reference);
		reference.setReferenceType("Site");
		currentSite.setSiteId(1L);
		reference.setReferenceId(1L);
		myPriceTransactionUtil.getClliValuesforAllSites(offer);
	}
	
	@Test
	public void getCLLiValuesTest() {
		List<UDFBaseData> udfList = new ArrayList<>();
		UDFBaseData udf = new UDFBaseData();
		udf.setUdfId(200045);
		udf.setUdfAttributeText(Arrays.asList("text"));
		UDFBaseData udf1 = new UDFBaseData();
		udf1.setUdfId(20184);
		udf1.setUdfAttributeText(Arrays.asList("text1"));
		udfList.add(udf);
		udfList.add(udf1);
		myPriceTransactionUtil.getCLLiValues(udfList);
	}
	
	@Test
	public void getNxSiteIdTest() {
		Query query = mock(Query.class);
		when(em.createNativeQuery(any())).thenReturn(query);
		BigDecimal id = BigDecimal.valueOf(1L);
		when(query.getSingleResult()).thenReturn(id);
		long res = myPriceTransactionUtil.getNxSiteId();
		assertEquals(1L, res);
	}
	
	@Test
	public void getNxMpDealTest() {
		List<NxMpDeal> nxMpDeal = new ArrayList<>();
		Set<Object> asePrcieScenarioIds = new HashSet<>();
		asePrcieScenarioIds.add("1");
		NxMpDeal nxMpDealElement = new NxMpDeal();
		nxMpDealElement.setPriceScenarioId(1L);
		nxMpDeal.add(nxMpDealElement);
		NxMpDeal res = myPriceTransactionUtil.getNxMpDeal(nxMpDeal, asePrcieScenarioIds);
		assertSame(nxMpDealElement, res);
	}
	
	@Test
	public void prepareUpdateTransactionOverrideRequestTest() throws JsonProcessingException, JSONException {
		NxMpDeal newDeal = new NxMpDeal();
		Map<String, Object> paramMap = new HashMap<>();
		Set<Object> designStatusIndicators = new HashSet<>();
		Offer offer = null;
		String offerName = null;
		paramMap.put("IS_RECONFIGURE", StringConstants.CONSTANT_N);
		String siteJson = "siteJson";
		doReturn(siteJson).when(myPriceTransactionUtil).getUpdatedSiteForRC(any(), any(), any());
		NxMpSiteDictionary siteData = new NxMpSiteDictionary();
		siteData.setSiteJson("siteJson");
		when(siteRepo.findByNxTxnId(any())).thenReturn(siteData);
		myPriceTransactionUtil.prepareUpdateTransactionOverrideRequest(newDeal, paramMap, designStatusIndicators, offer, offerName);
		
		paramMap.put("IS_RECONFIGURE", "inrReconfigure");
		paramMap.put("iglooCount", 1l);
		doReturn(siteJson).when(myPriceTransactionUtil).getSiteDictionary(any(), any(), any(), any());
		List<NxRequestDetails> nxRequestDetails = new ArrayList<>();
		NxRequestDetails nxRequestDetailsElement = new NxRequestDetails();
		nxRequestDetails.add(nxRequestDetailsElement);
		nxRequestDetailsElement.setNxRequestGroupId(1L);
		when(nxRequestDetailsRepository.findbyNxSolutionIdAndActiveYnAndStatus(any(), any(), any())).thenReturn(nxRequestDetails);
		when(submitToMyPriceService.getSiteAddress(any(), any(), any())).thenReturn(siteJson);
		myPriceTransactionUtil.prepareUpdateTransactionOverrideRequest(newDeal, paramMap, designStatusIndicators, offer, offerName);
		
		JsonProcessingException jsonProcessingException = mock(JsonProcessingException.class);
		when(submitToMyPriceService.persistNxMpSiteDictionary(any(), any(), any())).thenThrow(jsonProcessingException);
		myPriceTransactionUtil.prepareUpdateTransactionOverrideRequest(newDeal, paramMap, designStatusIndicators, offer, offerName);
	}
	
	@Test
	public void prepareUpdateTransactionOverrideRequestForIpneTest() throws JsonProcessingException, JSONException {
		RetreiveICBPSPRequest retreiveICBPSPRequest = new RetreiveICBPSPRequest();
		Solution solution = new Solution();
		retreiveICBPSPRequest.setSolution(solution);
		NxMpDeal newDeal = new NxMpDeal();
		Map<String, Object> paramMap = new HashMap<>();
		Set<Object> designStatusIndicators = new HashSet<>();
		Offer offer = null;
		String offerName = null;
		paramMap.put("IS_RECONFIGURE", StringConstants.CONSTANT_N);
		String siteJson = "siteJson";
		doReturn(siteJson).when(myPriceTransactionUtil).getUpdatedSiteForRCForIpne(any(), any(), any(),any(),any(),any());
		NxMpSiteDictionary siteData = new NxMpSiteDictionary();
		siteData.setSiteJson("siteJson");
		when(siteRepo.findByNxTxnId(any())).thenReturn(siteData);
		myPriceTransactionUtil.prepareUpdateTransactionOverrideRequestForIpne(newDeal, paramMap, designStatusIndicators, offer, offerName,retreiveICBPSPRequest);
		
		paramMap.put("IS_RECONFIGURE", "inrReconfigure");
		paramMap.put("iglooCount", 1l);
		doReturn(siteJson).when(myPriceTransactionUtil).getSiteDictionaryForIpne(any(), any(), any(), any(), any(),any());
		List<NxRequestDetails> nxRequestDetails = new ArrayList<>();
		NxRequestDetails nxRequestDetailsElement = new NxRequestDetails();
		nxRequestDetails.add(nxRequestDetailsElement);
		nxRequestDetailsElement.setNxRequestGroupId(1L);
		when(nxRequestDetailsRepository.findbyNxSolutionIdAndActiveYnAndStatus(any(), any(), any())).thenReturn(nxRequestDetails);
		when(submitToMyPriceService.getSiteAddress(any(), any(), any())).thenReturn(siteJson);
		myPriceTransactionUtil.prepareUpdateTransactionOverrideRequestForIpne(newDeal, paramMap, designStatusIndicators, offer, offerName,retreiveICBPSPRequest);
		
		JsonProcessingException jsonProcessingException = mock(JsonProcessingException.class);
		when(submitToMyPriceService.persistNxMpSiteDictionary(any(), any(), any())).thenThrow(jsonProcessingException);
		myPriceTransactionUtil.prepareUpdateTransactionOverrideRequestForIpne(newDeal, paramMap, designStatusIndicators, offer, offerName,retreiveICBPSPRequest);
	}
	
	@Test
	public void processMutliPriceScenarioTest() {
		RetreiveICBPSPRequest retreiveICBPSPRequest = new RetreiveICBPSPRequest();
		NxSolutionDetail solutionData = new NxSolutionDetail();
		Map<String, Object> paramMap = new HashMap<>();
		paramMap.put(StringConstants.TRANSACTION_TYPE, StringConstants.TRANSACTION_TYPE_NEW);
		NxMpDeal newDeal = new NxMpDeal();
		when(nxMpDealRepository.findByNxTxnId(any())).thenReturn(newDeal);
		Solution solution = new Solution();
		retreiveICBPSPRequest.setSolution(solution);
		List<Offer> offers = new ArrayList<>();
		solution.setOffers(offers);
		Offer offer = new Offer();
		offer.setOfferId("103");
		offers.add(offer);
		String offerName = StringConstants.OFFERNAME_ASE;
		when(salesMsDao.getOfferNameByOfferId(anyInt())).thenReturn(offerName);
		Set<Object> priceScenarioIds = new HashSet<>();
		doReturn(priceScenarioIds).when(myPriceTransactionUtil).getValuesFromRequest(any(), any());
		priceScenarioIds.add("1");
		Map<String, Object> result = new HashMap<String, Object>();
		when(copyTransactionServiceImpl.processMutliPriceScenario(any())).thenReturn(result);
		result.put(StringConstants.STATUS, true);
		result.put(StringConstants.NEW_NX_TXN_OBJECT, newDeal);
		doNothing().when(myPriceTransactionUtil).sendDmaapEvents(any(), any(), any(), any());
		result.put(StringConstants.NEW_NX_TXN_ID, 1L);
		Long nxAuditId = 1L;
		doReturn(nxAuditId).when(myPriceTransactionUtil).saveNxDesignAudit(any(), any(), any(), any(), any());
		UpdateTransactionOverrideRequest utOverrideReq = new UpdateTransactionOverrideRequest();
		doReturn(utOverrideReq).when(myPriceTransactionUtil).prepareUpdateTransactionOverrideRequest(any(), any(), any(), any(), any());
		ServiceResponse overrideRes = new ServiceResponse();
		when(updateTransactionOverrideImpl.updateTransactionOverride(any())).thenReturn(overrideRes);
		Status status = new Status();
		overrideRes.setStatus(status);
		status.setCode(CommonConstants.FAILURE_STATUS);
		myPriceTransactionUtil.processMutliPriceScenario(retreiveICBPSPRequest, solutionData, paramMap);
		
		offerName = StringConstants.OFFERNAME_ADE;
		when(salesMsDao.getOfferNameByOfferId(anyInt())).thenReturn(offerName);
		myPriceTransactionUtil.processMutliPriceScenario(retreiveICBPSPRequest, solutionData, paramMap);
		
		paramMap.remove(StringConstants.TRANSACTION_TYPE);
		paramMap.put(StringConstants.CALLPEDFORDESIGNUPDATE, true);
		myPriceTransactionUtil.processMutliPriceScenario(retreiveICBPSPRequest, solutionData, paramMap);
		
		paramMap.put(StringConstants.CALLPEDFORDESIGNUPDATE, false);
		myPriceTransactionUtil.processMutliPriceScenario(retreiveICBPSPRequest, solutionData, paramMap);
		
		paramMap.put(StringConstants.TRANSACTION_TYPE, StringConstants.TRANSACTION_TYPE_NEW);
		result.put(StringConstants.STATUS, false);
		myPriceTransactionUtil.processMutliPriceScenario(retreiveICBPSPRequest, solutionData, paramMap);
		
		result.put(StringConstants.STATUS, true);
		status.setCode(CommonConstants.SUCCESS_STATUS);
		when(nxMpDealRepository.findByNxTxnIdAndPriceScenarioIdAndActiveYN(any(), any(), any())).thenReturn(newDeal);
		newDeal.setSolutionId(1L);
		when(processPDtoMPRestUtil.isRESTEnabled(any(), any())).thenReturn(false);
		offer.setPriceUpdate("priceUpdate");
		solution.setContractTerm(1L);
		offerName = StringConstants.OFFERNAME_ASE;
		when(salesMsDao.getOfferNameByOfferId(anyInt())).thenReturn(offerName);
		List<Site> sites = new ArrayList<>();
		offer.setSite(sites);
		Site site = new Site();
		sites.add(site);
		when(nxDesignRepository.findByAsrItemIdAndNxSolutionId(any(), anyLong())).thenReturn(1L);
		Map<String, Object> pricingStatus = new HashMap<String, Object>();
		doReturn(pricingStatus).when(myPriceTransactionUtil).callUpdatePricing(any());
		pricingStatus.put(MyPriceConstants.RESPONSE_STATUS, false);
		doReturn(null).when(myPriceTransactionUtil).getNxSolutionDetails(any());
		myPriceTransactionUtil.processMutliPriceScenario(retreiveICBPSPRequest, solutionData, paramMap);
		
		pricingStatus.put(MyPriceConstants.RESPONSE_STATUS, true);
		myPriceTransactionUtil.processMutliPriceScenario(retreiveICBPSPRequest, solutionData, paramMap);
		
		when(processPDtoMPRestUtil.isRESTEnabled(any(), any())).thenReturn(true);
		when(processPDtoMPRestUtil.callUpdatePricing(any(), any())).thenReturn(pricingStatus);
		myPriceTransactionUtil.processMutliPriceScenario(retreiveICBPSPRequest, solutionData, paramMap);
		
		offerName = StringConstants.OFFERNAME_ADE;
		when(salesMsDao.getOfferNameByOfferId(anyInt())).thenReturn(offerName);
		List<Circuit> circuits = new ArrayList<>();
		Circuit circuit = new Circuit();
		circuits.add(circuit);
		offer.setCircuit(circuits);
		when(pedSnsdServiceUtil.findAdeAsrItemId(any())).thenReturn("asrItemId");
		when(processPDtoMPRestUtil.isRESTEnabled(any(), any())).thenReturn(false);
		ReflectionTestUtils.setField(myPriceTransactionUtil, "mapper", realMapper);
		pricingStatus.put(MyPriceConstants.RESPONSE_STATUS, false);
		myPriceTransactionUtil.processMutliPriceScenario(retreiveICBPSPRequest, solutionData, paramMap);
		
		when(processPDtoMPRestUtil.isRESTEnabled(any(), any())).thenReturn(true);
		myPriceTransactionUtil.processMutliPriceScenario(retreiveICBPSPRequest, solutionData, paramMap);
		
		circuit.setDesignStatus("C");
		myPriceTransactionUtil.processMutliPriceScenario(retreiveICBPSPRequest, solutionData, paramMap);
	}
	
	@Test
	public void deleteSiteFromSiteDicTest() {
		NxMpSiteDictionary nxMpSiteDictionary = new NxMpSiteDictionary();
		String siteAddress = "{\r\n" + 
				"    \"siteAddress\": [{\r\n" + 
				"            \"nxSiteId\": 1\r\n" + 
				"        }\r\n" + 
				"    ]\r\n" + 
				"}";
		List<Long> nxSiteIds = Arrays.asList(1L);
		nxMpSiteDictionary.setSiteAddress(siteAddress);
		when(nxMpSiteDictionaryRepository.findByNxTxnId(any())).thenReturn(nxMpSiteDictionary);
		myPriceTransactionUtil.deleteSiteFromSiteDic(null, nxSiteIds);
	}
	
	@Test
	public void getNxSolutionDetailsTest() {
		NxMpDeal nxMpDeal = new NxMpDeal();
		nxMpDeal.setSolutionId(1L);
		doReturn(nxMpDeal).when(myPriceTransactionUtil).getNxMpDeal(any());
		List<NxSolutionDetail> nxSolutionDetail = Arrays.asList(new NxSolutionDetail());
		when(nxSolutionDetailsRepository.findByNxSolutionId(anyLong())).thenReturn(nxSolutionDetail.get(0));
		myPriceTransactionUtil.getNxSolutionDetails(null);
	}
	
	@Test
	public void sendDmaapEventsTest() {
		NxSolutionDetail solution = new NxSolutionDetail();
		solution.setNxSolutionId(1L);
		solution.setExternalKey(1L);
		String status = "TDD_TRIGGER";
		myPriceTransactionUtil.sendDmaapEvents(null, solution, status);
		
		NxMpDeal deal = new NxMpDeal();
		deal.setTransactionId("1");
		myPriceTransactionUtil.sendDmaapEvents(deal, solution, status);
	}
	
	@Test
	public void sendDmaapEvents1Test() {
		NxSolutionDetail solution = new NxSolutionDetail();
		solution.setNxSolutionId(1L);
		solution.setExternalKey(1L);
		String status = "SUBMITTED";
		Map<String, Object> apiResponse = new HashMap<>();
		apiResponse.put(MyPriceConstants.MYPRICE_DESIGN, MyPriceConstants.MYPRICE_DESIGN);
		apiResponse.put(MyPriceConstants.NX_AUDIT_ID, 1L);
		doNothing().when(myPriceTransactionUtil).updateNxDesignAuditStatus(any(), any(), anyLong());
		myPriceTransactionUtil.sendDmaapEvents(null, solution, status, apiResponse);
		
		NxMpDeal deal = new NxMpDeal();
		deal.setTransactionId("1");
		status = CommonConstants.FAILED;
		doNothing().when(myPriceTransactionUtil).updateNxMpDealMpInd(any(), any());
		Map<String,String> allAsrSiteIdMap = new HashMap<>();
		apiResponse.put(MyPriceConstants.NX_SITE_ID_ASR_ITEM_ID_MAP, allAsrSiteIdMap);
		apiResponse.put(MyPriceConstants.RESPONSE_DATA, "{}");
		allAsrSiteIdMap.put("1", "1");
		apiResponse.put(MyPriceConstants.RESPONSE_CODE, 40);
		apiResponse.put(MyPriceConstants.RESPONSE_MSG, "message");
		myPriceTransactionUtil.sendDmaapEvents(deal, solution, status, apiResponse);
		
		apiResponse.put(MyPriceConstants.MYPRICE_DESIGN, "not" + MyPriceConstants.MYPRICE_DESIGN);
		myPriceTransactionUtil.sendDmaapEvents(deal, solution, status, apiResponse);
	}
	
	@Test
	public void getUpdatedSiteForRCTest() throws JSONException {
		String offerName = StringConstants.OFFERNAME_ASE;
		Offer offer = new Offer();
		List<Site> sites = new ArrayList<>();
		offer.setSite(sites);
		Site site = new Site();
		sites.add(site);
		site.setAddress1("1");
		site.setAddress2("2");
		NxMpDeal deal = new NxMpDeal();
		NxMpSiteDictionary siteData = new NxMpSiteDictionary();
		when(siteRepo.findByNxTxnId(any())).thenReturn(siteData);
		String siteAddress = "{\r\n" + 
				"    \"siteAddress\": [{\r\n" + 
				"            \"nxSiteId\": 1,\r\n" + 
				"			\"addressLine\": \"1 2\"\r\n" + 
				"        },\r\n" + 
				"		{\r\n" + 
				"            \"nxSiteId\": 2,\r\n" + 
				"			\"address\": \"1 2\"\r\n" + 
				"        }\r\n" + 
				"    ]\r\n" + 
				"}";
		siteData.setSiteAddress(siteAddress);
		myPriceTransactionUtil.getUpdatedSiteForRC(offerName, offer, deal);
		
		offerName = StringConstants.OFFERNAME_ADE;
		site.setSiteId(1L);
		List<Circuit> circuits = new ArrayList<>();
		offer.setCircuit(circuits);
		Circuit circuit = new Circuit();
		circuits.add(circuit);
		List<Component> components = new ArrayList<>();
		circuit.setComponent(components);
		Component component = new Component();
		component.setComponentCodeId(1220L);
		components.add(component);
		List<References> referencesList = new ArrayList<>();
		component.setReferences(referencesList);
		References references = new References();
		references.setReferenceId(1L);
		referencesList.add(references);
		site.setAddress("1 2");
		myPriceTransactionUtil.getUpdatedSiteForRC(offerName, offer, deal);
		offerName = StringConstants.OFFERNAME_EPLSWAN;
		myPriceTransactionUtil.getUpdatedSiteForRC(offerName, offer, deal);
	}
	
	@Test
	public void getUpdatedSiteForRCForIpneTest() throws JSONException {
		RetreiveICBPSPRequest retreiveICBPSPRequest = new RetreiveICBPSPRequest();
		NxSolutionDetail solutionData = new NxSolutionDetail();
		Solution solution = new Solution();
		retreiveICBPSPRequest.setSolution(solution);
		String offerName = StringConstants.OFFERNAME_ADE;
		Offer offer = new Offer();
		List<Site> sites = new ArrayList<>();
		offer.setSite(sites);
		Site site = new Site();
		sites.add(site);
		site.setAddress1("1");
		site.setAddress2("2");
		List<Offer> offers = new ArrayList<>();
		offers.add(offer);
		solution.setOffers(offers);
		List<Long> nxSiteIdList= new ArrayList<>();
		Map<String, Object> paramMap = new HashMap<>();
		paramMap.put("IS_RECONFIGURE", StringConstants.CONSTANT_N);
		NxMpDeal deal = new NxMpDeal();
		NxMpSiteDictionary siteData = new NxMpSiteDictionary();
		when(siteRepo.findByNxTxnId(any())).thenReturn(siteData);
		String siteAddress = "{\r\n" + 
				"    \"siteAddress\": [{\r\n" + 
				"            \"nxSiteId\": 1,\r\n" + 
				"			\"addressLine\": \"1 2\"\r\n" + 
				"        },\r\n" + 
				"		{\r\n" + 
				"            \"nxSiteId\": 2,\r\n" + 
				"			\"address\": \"1 2\"\r\n" + 
				"        }\r\n" + 
				"    ]\r\n" + 
				"}";
		siteData.setSiteAddress(siteAddress);
		site.setSiteId(1L);
		List<Circuit> circuits = new ArrayList<>();
		offer.setCircuit(circuits);
		Circuit circuit = new Circuit();
		circuits.add(circuit);
		List<Component> components = new ArrayList<>();
		circuit.setComponent(components);
		Component component = new Component();
		component.setComponentCodeId(1220L);
		components.add(component);
		List<References> referencesList = new ArrayList<>();
		component.setReferences(referencesList);
		References references = new References();
		references.setReferenceId(1L);
		referencesList.add(references);
		site.setAddress("1 2");
		myPriceTransactionUtil.getUpdatedSiteForRCForIpne(offerName, offer, deal,retreiveICBPSPRequest,paramMap,nxSiteIdList);
	}
	
	@Test
	public void getAutomationFlowIndTest() {
		Solution solution = new Solution();
		solution.setAutomationInd("Y");
		List<Offer> offers = new ArrayList<>();
		solution.setOffers(offers);
		Offer offer = new Offer();
		offer.setOfferId(TDDConstants.ASE_OFFER_ID);
		offers.add(offer);
		List<Site> sites = new ArrayList<>();
		offer.setSite(sites);
		Site site = new Site();
		sites.add(site);
		site.setAddress1("1");
		site.setAddress2("2");
		List<Circuit> circuits = new ArrayList<>();
		offer.setCircuit(circuits);
		Circuit circuit = new Circuit();
		circuits.add(circuit);
		doReturn("Y").when(myPriceTransactionUtil).getNssEngagementValue(any(), any(), any());
		myPriceTransactionUtil.getAutomationFlowInd(solution);
		
		offer.setOfferId(TDDConstants.ADE_OFFER_ID);
		doReturn("N").when(myPriceTransactionUtil).getNssEngagementValue(any(), any(), any());
		myPriceTransactionUtil.getAutomationFlowInd(solution);
	}
	
	@Test
	public void getNssEngagementValueTest() {
		String jsonPath = "1||2";
		doReturn(null).when(myPriceTransactionUtil).processOrCondition(any(), any(), any());
		myPriceTransactionUtil.getNssEngagementValue(null, jsonPath, null);
		
		jsonPath = "1";
		doReturn(null).when(myPriceTransactionUtil).getItemValueUsingJsonPath(any(), any(), any());
		myPriceTransactionUtil.getNssEngagementValue(null, jsonPath, null);
	}
	
	@Test
	public void processOrConditionTest() {
		List<String> pathList = new ArrayList<>();
		myPriceTransactionUtil.processOrCondition(null, pathList, null);
		
		pathList.add("path");
		doReturn("itemValue").when(myPriceTransactionUtil).getItemValueUsingJsonPath(any(), any(), any());
		myPriceTransactionUtil.processOrCondition(null, pathList, null);
	}
	
	@Test
	public void getItemValueUsingJsonPathTest() {
		when(nexxusJsonUtility.getValue(any(), any())).thenReturn("value");
		doReturn("res").when(myPriceTransactionUtil).getDataFromNxLookUp(any(), any());
		myPriceTransactionUtil.getItemValueUsingJsonPath(null, null, null);
		
		when(nexxusJsonUtility.getValue(any(), any())).thenReturn(null);
		myPriceTransactionUtil.getItemValueUsingJsonPath(null, null, null);
	}
	
	@Test
	public void getDataFromNxLookUpTest() {
		Map<String,NxLookupData> resultMap = new HashMap<>();
		NxLookupData data = new NxLookupData();
		String input = "input";
		resultMap.put(input, data);
		when(nxMyPriceRepositoryServce.getLookupDataByItemId(any())).thenReturn(resultMap);
		myPriceTransactionUtil.getDataFromNxLookUp(input, "dataset");
		
		myPriceTransactionUtil.getDataFromNxLookUp(null, null);
	}
	
	@Test
	public void saveDesignDataTest() {
		ReflectionTestUtils.setField(myPriceTransactionUtil, "mapper", realMapper);
		RetreiveICBPSPRequest retreiveICBPSPRequest = new RetreiveICBPSPRequest();
		Solution solution = new Solution();
		retreiveICBPSPRequest.setSolution(solution);
		List<Offer> offers = new ArrayList<>();
		Offer offer = new Offer();
		offers.add(offer);
		solution.setOffers(offers);
		List<Site> siteList = new ArrayList<>();
		Site site = new Site();
		siteList.add(site);
		offer.setSite(siteList);
		offer.setOfferId("103");
		when(salesMsDao.getOfferNameByOfferId(anyInt())).thenReturn("");
		when(nexxusJsonUtility.getValue(any(), any())).thenReturn("value");
		Map<String, Object> paramMap = new HashMap<>();
		myPriceTransactionUtil.saveDesignData(retreiveICBPSPRequest, null, null, paramMap);
	}
	
	@Test
	public void isLastDesignTest() {
		List<String> inputList = Arrays.asList("1", "2");
		boolean res = myPriceTransactionUtil.isLastDesign(1, inputList);
		assertTrue(res);
		
		res = myPriceTransactionUtil.isLastDesign(0, inputList);
		assertFalse(res);
	}
	
	@Test
	public void getProxyTest() {
		myPriceTransactionUtil.getProxy();
	}
	
	@Test
	public void callUpdateCleanSave1Test() {
		myPriceTransactionUtil.callUpdateCleanSave(null, null, null);
	}
}
