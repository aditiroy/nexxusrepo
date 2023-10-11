package com.att.sales.nexxus.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyInt;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.anyMap;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.EntityManager;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.stubbing.Answer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.util.ReflectionTestUtils;

import com.att.sales.framework.exception.SalesBusinessException;
import com.att.sales.framework.model.ServiceMetaData;
import com.att.sales.nexxus.common.CommonConstants;
import com.att.sales.nexxus.constant.FmoConstants;
import com.att.sales.nexxus.constant.InrConstants;
import com.att.sales.nexxus.constant.MyPriceConstants;
import com.att.sales.nexxus.dao.model.NxAccessPricingData;
import com.att.sales.nexxus.dao.model.NxDesign;
import com.att.sales.nexxus.dao.model.NxDesignAudit;
import com.att.sales.nexxus.dao.model.NxDesignDetails;
import com.att.sales.nexxus.dao.model.NxInrDesign;
import com.att.sales.nexxus.dao.model.NxInrDesignDetails;
import com.att.sales.nexxus.dao.model.NxLookupData;
import com.att.sales.nexxus.dao.model.NxMpDeal;
import com.att.sales.nexxus.dao.model.NxMpSiteDictionary;
import com.att.sales.nexxus.dao.model.NxMpSolutionDetails;
import com.att.sales.nexxus.dao.model.NxOutputFileModel;
import com.att.sales.nexxus.dao.model.NxRequestDetails;
import com.att.sales.nexxus.dao.model.NxSolutionSite;
import com.att.sales.nexxus.dao.model.NxValidationRules;
import com.att.sales.nexxus.dao.model.solution.NxSolutionDetail;
import com.att.sales.nexxus.dao.repository.NxAccessPricingDataRepository;
import com.att.sales.nexxus.dao.repository.NxDesignAuditRepository;
import com.att.sales.nexxus.dao.repository.NxDesignDetailsRepository;
import com.att.sales.nexxus.dao.repository.NxDesignRepository;
import com.att.sales.nexxus.dao.repository.NxInrDesignDetailsRepository;
import com.att.sales.nexxus.dao.repository.NxInrDesignRepository;
import com.att.sales.nexxus.dao.repository.NxLookupDataRepository;
import com.att.sales.nexxus.dao.repository.NxMpDealRepository;
import com.att.sales.nexxus.dao.repository.NxMpDesignDocumentRepository;
import com.att.sales.nexxus.dao.repository.NxMpSiteDictionaryRepository;
import com.att.sales.nexxus.dao.repository.NxMpSolutionDetailsRepository;
import com.att.sales.nexxus.dao.repository.NxOutputFileRepository;
import com.att.sales.nexxus.dao.repository.NxRequestDetailsRepository;
import com.att.sales.nexxus.dao.repository.NxSolutionSiteRepository;
import com.att.sales.nexxus.dao.repository.NxTeamRepository;
import com.att.sales.nexxus.dao.repository.NxValidationRulesRepository;
import com.att.sales.nexxus.fallout.model.FalloutDetailsRequest;
import com.att.sales.nexxus.handlers.GetOptyInfoWSHandler;
import com.att.sales.nexxus.myprice.transaction.dao.service.NxMpRepositoryService;
import com.att.sales.nexxus.myprice.transaction.model.CreateTransactionResponse;
import com.att.sales.nexxus.myprice.transaction.model.GetTransactionLineItem;
import com.att.sales.nexxus.myprice.transaction.model.GetTransactionLineResponse;
import com.att.sales.nexxus.myprice.transaction.service.ConfigAndUpdatePricingUtilInr;
import com.att.sales.nexxus.myprice.transaction.service.ConfigAndUpdateProcessingFmoService;
import com.att.sales.nexxus.myprice.transaction.service.ConfigAndUpdateRestProcessingService;
import com.att.sales.nexxus.myprice.transaction.service.ConfigDesignHelperService;
import com.att.sales.nexxus.myprice.transaction.service.CreateTransactionService;
import com.att.sales.nexxus.myprice.transaction.service.GetTransactionLineServiceImpl;
import com.att.sales.nexxus.myprice.transaction.service.IUpdateTransactionImpl;
import com.att.sales.nexxus.myprice.transaction.service.MyPriceTransactionUtil;
import com.att.sales.nexxus.myprice.transaction.service.UpdateTransactionPricingFMORestServiceImpl;
import com.att.sales.nexxus.myprice.transaction.service.UpdateTransactionPricingFMOServiceImpl;
import com.att.sales.nexxus.myprice.transaction.service.UpdateTxnSiteUploadServiceImpl;
import com.att.sales.nexxus.rome.model.GetOptyResponse;
import com.att.sales.nexxus.userdetails.model.UserDetails;
import com.att.sales.nexxus.userdetails.model.UserDetailsResponse;
import com.att.sales.nexxus.userdetails.model.UserRole;
import com.att.sales.nexxus.userdetails.service.UserDetailsServiceImpl;
import com.att.sales.nexxus.util.JsonPathUtil;
import com.att.sales.nexxus.util.NexxusJsonUtility;
import com.att.sales.nexxus.util.RestClientUtil;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

@ExtendWith(MockitoExtension.class)
public class SubmitToMyPriceServiceTest {
	@Spy
	@InjectMocks
	private SubmitToMyPriceService submitToMyPriceService;

	@Mock
	private ProcessINRtoMP processINRtoMP;

	@Mock
	private EntityManager em;

	@Mock
	private NxMpRepositoryService nxMpRepositoryService;

	@Mock
	private NxSolutionSiteRepository nxSolutionSiteRepository;

	@Mock
	private NexxusJsonUtility nexxusJsonUtility;

	@Mock
	private NxMpDealRepository nxMpDealRepository;

	@Mock
	private NxRequestDetailsRepository nxRequestDetailsRepository;

	@Mock
	private CreateTransactionService createTransactionService;

	@Mock
	private RestClientUtil restClient;

	@Mock
	private NxDesignAuditRepository nxDesignAuditRepository;

	@Mock
	private MyPriceTransactionUtil myPriceTransactionUtil;

	@Mock
	private IUpdateTransactionImpl iUpdateTransactionImpl;

	@Mock
	private GetOptyInfoWSHandler getOptyInfoWSHandler;

	@Mock
	private NxMpSolutionDetailsRepository nxMpSolutionDetailsRepository;

	@Mock
	private NxMpSiteDictionaryRepository nxMpSiteDictionaryRepository;

	@Mock
	private UserDetailsServiceImpl userDetailsServiceImpl;

	@Mock
	private JsonPathUtil jsonPathUtil;

	@Mock
	private NxOutputFileRepository nxOutputFileRepository;

	@Mock
	private NxValidationRulesRepository nxValidationRulesRepository;

	@Mock
	private ObjectMapper mapper;

	@Mock
	private NxDesignRepository nxDesignRepository;

	@Mock
	private NxInrDesignRepository nxInrDesignRepository;

	@Mock
	private NxAccessPricingDataRepository nxAccessPricingDataRepository;

	@Mock
	private InrQualifyService inrQualifyService;

	@Mock
	private ConfigAndUpdateProcessingFmoService configAndUpdateProcessingFmoService;

	@Mock
	private ConfigAndUpdatePricingUtilInr configAndUpdatePricingUtilInr;

	@Mock
	private UpdateTransactionPricingFMOServiceImpl updateTransactionPricingFMOServiceImpl;

	@Mock
	private InrReconfigure inrReconfigure;

	@Mock
	private ConfigDesignHelperService configDesignHelperService;

	@Mock
	private MailServiceImpl mailServiceImpl;

	@Mock
	private NxLookupDataRepository nxLookupDataRepository;

	@Mock
	private ConfigAndUpdateRestProcessingService configAndUpdateRestProcessingService;

	@Mock
	private GetTransactionLineServiceImpl getTransactionLineServiceImpl;

	@Mock
	private NxMpDesignDocumentRepository nxMpDesignDocumentRepo;

	@Mock
	private UpdateTransactionPricingFMORestServiceImpl updatePricingRestFmo;

	@Mock
	private NxMyPriceRepositoryServce nxMyPriceRepositoryServce;

	@Mock
	private NxDesignDetailsRepository nxDesignDetailsRepository;
	
	@Mock
	private UpdateTxnSiteUploadServiceImpl updateTxnSiteUploadServiceImpl;
	
	@Mock
	private NxTeamRepository nxTeamRepository;
	
	@Mock
	private NxInrDesignDetailsRepository nxInrDesignDetailsRepository;

	
	private ObjectMapper realMapper = new ObjectMapper();

	@Test
	public void submitToMyPriceTest() {
		NxSolutionDetail nxSolutionDetail = new NxSolutionDetail();
		nxSolutionDetail.setNxSolutionId(1L);
		List<Long> nxRequestGrpId = Arrays.asList(2L, 3L);
		FalloutDetailsRequest request = new FalloutDetailsRequest();
		request.setDataIds(nxRequestGrpId);
		submitToMyPriceService.submitToMyPrice(nxSolutionDetail, request);

		NxRequestDetails nxRequestDetailsElement = new NxRequestDetails();
		List<NxRequestDetails> nxRequestDetails = Arrays.asList(nxRequestDetailsElement);
		when(nxRequestDetailsRepository.findbyNSolutionIdAndActiveYnAndNxRequestGrpIds(any(), any(), any()))
				.thenReturn(nxRequestDetails);

		NxAccessPricingData nxAccessPricingData = new NxAccessPricingData();
		List<NxAccessPricingData> nxAccessPricingDatas = Arrays.asList(nxAccessPricingData);
		when(nxAccessPricingDataRepository.findByNxSolIdAndIncludeIndAndMpStatus(any()))
				.thenReturn(nxAccessPricingDatas);

		NxMpDeal nxMpDeal = new NxMpDeal();
		List<NxMpDeal> deal = Arrays.asList(nxMpDeal);
		when(nxMpDealRepository.findBySolutionIdAndActiveYN(any(), any())).thenReturn(deal);
		
		nxMpDeal.setTransactionId("transactionId");
		nxMpDeal.setNxMpStatusInd("N");
		nxRequestDetailsElement.setNxRequestGroupId(1L);
		
		when(nxRequestDetailsRepository.findbyNxSolutionIdAndActiveYnAndStatus(any(), any(), any())).thenReturn(nxRequestDetails);
		
		NxDesignAudit nxDesignAuditElement = new NxDesignAudit();
		List<NxDesignAudit> nxDesignAudit = Arrays.asList(nxDesignAuditElement);
		when(nxDesignAuditRepository.findByNxRefId(any())).thenReturn(nxDesignAudit);
		
		when(nxDesignAuditRepository.findByNxRefIdAndTransactionAndStatus(any(), any(), any())).thenReturn(nxDesignAudit);
		
		request.setNxSolutionId(1L);
		request.setActionInd("appendToOtherDeal");
		request.setDealId("1234");
		request.setVersion("1");
		request.setRevision("0");
		//submitToMyPriceService.submitToMyPrice(nxSolutionDetail, request);
		
		nxMpDeal.setTransactionId(null);
		Map<String, Object> result = new HashMap<String, Object>();
		result.put(MyPriceConstants.RESPONSE_STATUS, false);
		doReturn(result).when(submitToMyPriceService).createTransaction(any(), any(), any(), any());
		//submitToMyPriceService.submitToMyPrice(nxSolutionDetail, request);
		
		when(nxRequestDetailsRepository.findbyNSolutionIdAndActiveYnAndNxRequestGrpIds(any(), any(), any()))
		.thenReturn(new ArrayList<>());
		//submitToMyPriceService.submitToMyPrice(nxSolutionDetail, request);
		
		result.put(MyPriceConstants.RESPONSE_STATUS, true);
		when(nxRequestDetailsRepository.findbyNSolutionIdAndActiveYnAndNxRequestGrpIds(any(), any(), any()))
		.thenReturn(nxRequestDetails);
		NxOutputFileModel nxOutputFileModelElement = new NxOutputFileModel();
		List<NxOutputFileModel> nxOutputFileModel = Arrays.asList(nxOutputFileModelElement);
		when(nxOutputFileRepository
						.findByNxReqId(any())).thenReturn(nxOutputFileModel);
		Map<String, Object> updateResponse = new HashMap<>();
		doReturn(updateResponse).when(submitToMyPriceService).callUpdateCleanSave(any(), any(), any(),any());
		//submitToMyPriceService.submitToMyPrice(nxSolutionDetail, request);
		
		updateResponse.put(MyPriceConstants.RESPONSE_STATUS, false);
		//submitToMyPriceService.submitToMyPrice(nxSolutionDetail, request);
		
		updateResponse.put(MyPriceConstants.RESPONSE_STATUS, true);
		CreateTransactionResponse createTransactionResponse = new CreateTransactionResponse();
		result.put("createTransactionRes", createTransactionResponse);
		ServiceMetaData.add(updateResponse);
		//submitToMyPriceService.submitToMyPrice(nxSolutionDetail, request);
	}
	
	@Test
	public void skipCktProcessingTest() {
		NxValidationRules nxValidationRules = new NxValidationRules();
		nxValidationRules.setValue("value");
		nxValidationRules.setDescription("description");
		List<NxValidationRules> skipCktRules = Arrays.asList(nxValidationRules);
		Map<String, Object> paramMap = new HashMap<>();
		when(nexxusJsonUtility.getValue(any(), any())).thenReturn(null, "cktObj");
		submitToMyPriceService.skipCktProcessing(skipCktRules, null, paramMap);
		
		when(nexxusJsonUtility.getValue(any(), any())).thenReturn(null, "cktObj");
		paramMap.put("skipCircuits", new HashSet<String>());
		submitToMyPriceService.skipCktProcessing(skipCktRules, null, paramMap);
	}
	
	@Test
	public void saveNxInrDesignTest() throws IOException {
		NxSolutionDetail nxSolutionDetail = new NxSolutionDetail();
		ObjectNode design = realMapper.createObjectNode();
		Map<String, Object> paramMap = new HashMap<>();
		Map<String, NxInrDesign> designDataMap = new HashMap<>();
		NxInrDesign nxInrDesign = new NxInrDesign();
		when(nxInrDesignRepository.findByNxSolutionIdAndCircuitIdAndActiveYNAndNxRequestGroupId(any(), any(), any(), any())).thenReturn(nxInrDesign);
		submitToMyPriceService.saveNxInrDesign(nxSolutionDetail, design, paramMap, designDataMap);
		
		nxInrDesign.getNxInrDesignDetails().clear();
		paramMap.put(MyPriceConstants.NX_REQ_ID, 1L);
		paramMap.put(MyPriceConstants.SUB_DATA, "subData");
		paramMap.put(MyPriceConstants.CIRCUIT_ID, "circuitId");
		paramMap.put(MyPriceConstants.NX_GROUP_ID, 1L);
		paramMap.put(MyPriceConstants.NX_GROUP_IDS, new HashSet<Long>());
		paramMap.put("IS_RECONFIGURE", "isReconfigure");
		paramMap.put("NX_MP_STATUS_IND", "nxMpStatusInd");
		paramMap.put(MyPriceConstants.SUB_PRODUCT, "subProduct");
		designDataMap.put("circuitId", nxInrDesign);
		submitToMyPriceService.saveNxInrDesign(nxSolutionDetail, design, paramMap, designDataMap);
		
		nxInrDesign.getNxInrDesignDetails().clear();
		paramMap.put("NX_MP_STATUS_IND", "Y");
		paramMap.put("IS_RECONFIGURE", "inrReconfigure");
		submitToMyPriceService.saveNxInrDesign(nxSolutionDetail, design, paramMap, designDataMap);
		
		nxInrDesign.getNxInrDesignDetails().clear();
		designDataMap.put("circuitId", null);
		submitToMyPriceService.saveNxInrDesign(nxSolutionDetail, design, paramMap, designDataMap);
		
		nxInrDesign.getNxInrDesignDetails().clear();
		designDataMap.put("circuitId", nxInrDesign);
		NxInrDesignDetails nxInrDesignDetails = new NxInrDesignDetails();
		nxInrDesign.addNxInrDesignDetails(nxInrDesignDetails);
		nxInrDesignDetails.setProduct("product");
		paramMap.put(MyPriceConstants.PRODUCT_NAME, "product");
		paramMap.put(MyPriceConstants.OFFER_NAME, MyPriceConstants.DDA_OFFER_NAME);
		String designData = "{\r\n" + 
				"    \"subData\": {\r\n" + 
				"        \"nxSiteId\": 1,\r\n" + 
				"		\"endPointType\": \"A\"\r\n" + 
				"    }\r\n" + 
				"}";
		nxInrDesignDetails.setDesignData(designData);
		design = (ObjectNode) realMapper.readTree(designData);
		submitToMyPriceService.saveNxInrDesign(nxSolutionDetail, design, paramMap, designDataMap);
		
		((ObjectNode) design.path("subData")).put("nxSiteId", 2);
		submitToMyPriceService.saveNxInrDesign(nxSolutionDetail, design, paramMap, designDataMap);
		
		design = realMapper.createObjectNode();
		submitToMyPriceService.saveNxInrDesign(nxSolutionDetail, design, paramMap, designDataMap);
		
		designData = "{\r\n" + 
				"    \"nxSiteId\": 1\r\n" + 
				"}";
		nxInrDesign.getNxInrDesignDetails().clear();
		paramMap.put(MyPriceConstants.OFFER_NAME, "ASE");
		nxInrDesignDetails = new NxInrDesignDetails();
		nxInrDesignDetails.setProduct("product");
		nxInrDesignDetails.setDesignData(designData);
		nxInrDesign.addNxInrDesignDetails(nxInrDesignDetails);
		design = (ObjectNode) realMapper.readTree(designData);
		submitToMyPriceService.saveNxInrDesign(nxSolutionDetail, design, paramMap, designDataMap);
		
		design.put("nxSiteId", 2);
		submitToMyPriceService.saveNxInrDesign(nxSolutionDetail, design, paramMap, designDataMap);
		
		
		designData = "{\r\n" + 
				"    \"subData\": [{\r\n" + 
				"        \"nxSiteId\": 1,\r\n" + 
				"		\"endPointType\": \"A\"\r\n" + 
				"    }]\r\n" + 
				"}";
		paramMap.put(MyPriceConstants.PRODUCT_NAME, "AVPN");
		nxInrDesign.getNxInrDesignDetails().clear();
		nxInrDesignDetails = new NxInrDesignDetails();
		nxInrDesignDetails.setProduct("AVPN");
		nxInrDesignDetails.setDesignData(designData);
		nxInrDesign.addNxInrDesignDetails(nxInrDesignDetails);
		design = (ObjectNode) realMapper.readTree(designData);
		submitToMyPriceService.saveNxInrDesign(nxSolutionDetail, design, paramMap, designDataMap);
		
		designData = "{\r\n" + 
				"    \"subData\": {\r\n" + 
				"        \"nxSiteId\": 1,\r\n" + 
				"		\"endPointType\": \"A\"\r\n" + 
				"    }\r\n" + 
				"}";
		paramMap.put(MyPriceConstants.PRODUCT_NAME, "product");
		nxInrDesign.getNxInrDesignDetails().clear();
		nxInrDesignDetails = new NxInrDesignDetails();
		nxInrDesignDetails.setProduct("product");
		nxInrDesignDetails.setDesignData(designData);
		nxInrDesign.addNxInrDesignDetails(nxInrDesignDetails);
		design = (ObjectNode) realMapper.readTree(designData);
		submitToMyPriceService.saveNxInrDesign(nxSolutionDetail, design, paramMap, designDataMap);
		
		((ObjectNode) design.path("subData")).put("nxSiteId", 2);
		submitToMyPriceService.saveNxInrDesign(nxSolutionDetail, design, paramMap, designDataMap);
		
		nxInrDesign.getNxInrDesignDetails().clear();
		nxInrDesignDetails = new NxInrDesignDetails();
		nxInrDesignDetails.setProduct("notMatch");
		nxInrDesign.addNxInrDesignDetails(nxInrDesignDetails);
		submitToMyPriceService.saveNxInrDesign(nxSolutionDetail, design, paramMap, designDataMap);
	}
	
	@Test
	public void callUpdateCleanSaveTest() throws SalesBusinessException, JsonProcessingException {
		Map<String, Object> result = new HashMap<>();
		result.put(MyPriceConstants.NX_AUDIT_ID, 1L);
		result.put(MyPriceConstants.SOURCE, MyPriceConstants.SOURCE_FMO);
		CreateTransactionResponse createTransactionResponse = new CreateTransactionResponse();
		result.put("createTransactionRes", createTransactionResponse);
		NxSolutionDetail nxSolutionDetail = new NxSolutionDetail();
		doReturn("siteAddressJson").when(submitToMyPriceService).getSiteAddress(any(), any(), any());
		nxSolutionDetail.setOptyId("optyId");
		nxSolutionDetail.setExternalKey(1234L);
		GetOptyResponse optyResp = new GetOptyResponse();
		when(getOptyInfoWSHandler.initiateGetOptyInfoWebService(any())).thenReturn(optyResp);
		UserDetailsResponse resp = new UserDetailsResponse();
		when(userDetailsServiceImpl.retreiveUserDetails(any())).thenReturn(resp);
		List<UserDetails> userDetails = new ArrayList<>();
		UserDetails userDetailsElement = new UserDetails();
		userDetails.add(userDetailsElement);
		resp.setUserDetails(userDetails);
		List<UserRole> userRole = new ArrayList<>();
		UserRole userRoleElement = new UserRole();
		userRole.add(userRoleElement);
		userDetailsElement.setUserRole(userRole);
		result.put(MyPriceConstants.PRODUCT_NAME, "product");
		result.put(MyPriceConstants.OUTPUT_JSON, "outputJson");
		List<NxValidationRules> nxValidationRules = new ArrayList<>();
		NxValidationRules nxValidationRule = new NxValidationRules();
		nxValidationRule.setName("customerTCompanyName");
		nxValidationRules.add(nxValidationRule);
		when(nxValidationRulesRepository
					.findByValidationGroupAndOfferAndActiveAndFlowType(any(), any(), any(), any())).thenReturn(nxValidationRules);
		Set<Object> data = new HashSet<>();
		data.add("data");
		doReturn(data).when(submitToMyPriceService).getValuesFromRequest(any(), any());
		NxMpSiteDictionary nxMpSiteDictionary = new NxMpSiteDictionary();
		nxMpSiteDictionary.setSiteJson("siteJson");
		doReturn(nxMpSiteDictionary).when(submitToMyPriceService).persistNxMpSiteDictionary(any(), any(), any());
		String solutionDataJsonBlock = "solutionDataJsonBlock";
		doReturn(solutionDataJsonBlock).when(submitToMyPriceService).getupdateTransactionCSRTJSON(any());
		Mockito.when(updateTxnSiteUploadServiceImpl.translateSiteJsonRemoveDuplicatedNxSiteId(anyString())).thenReturn("siteAddressJson");
		doNothing().when(submitToMyPriceService).persistNxSolutoinDetail(any(), any(), any(), any());
		Map<String, Object> updateResponse = new HashMap<String, Object>();
		updateResponse.put("code", 200);
		Mockito.when(nxTeamRepository.findByNxSolutionId(anyLong())).thenReturn(null);
		when(iUpdateTransactionImpl.callMsForCleanSaveTransaction(any(), any())).thenReturn(updateResponse);
		submitToMyPriceService.callUpdateCleanSave(nxSolutionDetail, result, null,new HashMap<>());
		
		result.remove(MyPriceConstants.SOURCE);
		nxValidationRule.setName("NoSuchField");
		updateResponse.put("code", 400);
		submitToMyPriceService.callUpdateCleanSave(nxSolutionDetail, result, null,new HashMap<>());
		
		SalesBusinessException e = new SalesBusinessException();
		e.setHttpErrorCode(400);
		when(iUpdateTransactionImpl.callMsForCleanSaveTransaction(any(), any())).thenThrow(e);
		submitToMyPriceService.callUpdateCleanSave(nxSolutionDetail, result, null, new HashMap<>());
		
		result.remove(MyPriceConstants.OUTPUT_JSON);
		JsonProcessingException jsonProcessingException = mock(JsonProcessingException.class);
		doThrow(jsonProcessingException).when(submitToMyPriceService).getupdateTransactionCSRTJSON(any());
		submitToMyPriceService.callUpdateCleanSave(nxSolutionDetail, result, null, new HashMap<>());
	}
	
	@Test
	public void getSiteAddressTest() throws JsonParseException, JsonMappingException, IOException {
		ReflectionTestUtils.setField(submitToMyPriceService, "mapper", realMapper);
		List<Long> nxRequestGrpId = new ArrayList<>();
		NxSolutionDetail nxSolutionDetail = new NxSolutionDetail();
		nxRequestGrpId.add(1L);
		List<NxSolutionSite> nxSolutionSite = new ArrayList<>();
		NxSolutionSite siteJson = new NxSolutionSite();
		nxSolutionSite.add(siteJson);
		String siteAddress = "[{\r\n" + 
				"        \"nxSiteId\": \"nxSiteId\",\r\n" + 
				"        \"nxSiteIdZ\": \"nxSiteIdZ\"\r\n" + 
				"    }, {\r\n" + 
				"        \"nxSiteId\": \"nxSiteId\",\r\n" + 
				"        \"nxSiteIdZ\": \"nxSiteIdZ\"\r\n" + 
				"    }\r\n" + 
				"]";
		siteJson.setSiteAddress(siteAddress);
		when(nxSolutionSiteRepository.findByNxSolutionIdAndNxRequestGroupIdAndActiveYN(any(), any(), any())).thenReturn(nxSolutionSite);
		List<Object> iglooSiteAddress = new ArrayList<>();
		doReturn(iglooSiteAddress).when(submitToMyPriceService).getIglooSiteAddress(any(),any());
		Map<String, Object> map= new HashMap<>();
		submitToMyPriceService.getSiteAddress(nxRequestGrpId, nxSolutionDetail, map);
		
		ReflectionTestUtils.setField(submitToMyPriceService, "mapper", mapper);
		when(mapper.readValue(siteAddress, List.class)).thenThrow(JsonParseException.class);
		submitToMyPriceService.getSiteAddress(nxRequestGrpId, nxSolutionDetail, map);
	}
	
	@Test
	public void getIglooSiteAddressTest() {
		Long solutionId = 1L;
		List<NxAccessPricingData> nxAccessPricingDatas = new ArrayList<>();
		NxAccessPricingData nxAccessPricingData = new NxAccessPricingData();
		nxAccessPricingDatas.add(nxAccessPricingData);
		when(nxAccessPricingDataRepository.findByNxSolIdAndMpStatusAndIncludeInd(any())).thenReturn(nxAccessPricingDatas);
		NxDesignAudit nxDesignAudit = new NxDesignAudit();
		when(nxDesignAuditRepository.findByNxRefIdAndTransaction(any(), any())).thenReturn(nxDesignAudit);
		nxDesignAudit.setData("1");
		String intermediateJson = "{}";
		nxAccessPricingData.setIntermediateJson(intermediateJson);
		Map<String, Map<String, Object>> processedSites = new LinkedHashMap<String, Map<String, Object>>();
		submitToMyPriceService.getIglooSiteAddress(solutionId,processedSites);
		
		nxAccessPricingData.setSiteRefId("siteRefId");
		nxAccessPricingDatas.add(nxAccessPricingData);
		when(nxMpDealRepository.getCountryCodeByCountryIsoCode(any())).thenReturn("US");
		when(nxDesignAuditRepository.findByNxRefIdAndTransaction(any(), any())).thenReturn(null);
		//submitToMyPriceService.getIglooSiteAddress(solutionId,processedSites);
	}
	
	@Test
	public void getupdateTransactionCSRTJSONTest() throws JsonProcessingException {
		Map<Object, Object> updateCleanTransactionCleanSaveRequestMap = new HashMap<>();
		updateCleanTransactionCleanSaveRequestMap.put("k", "v");
		submitToMyPriceService.getupdateTransactionCSRTJSON(updateCleanTransactionCleanSaveRequestMap);
	}
	
	@Test
	public void persistNxSolutoinDetailTest() {
		submitToMyPriceService.persistNxSolutoinDetail(null, null, null, null);
		
		NxMpSolutionDetails nxMpSolutionDetails = new NxMpSolutionDetails();
		when(nxMpSolutionDetailsRepository.findByNxTxnId(any())).thenReturn(nxMpSolutionDetails);
		submitToMyPriceService.persistNxSolutoinDetail(null, null, null, null);
	}
	
	@Test
	public void persistNxMpSiteDictionaryTest() throws JsonProcessingException {
		ReflectionTestUtils.setField(submitToMyPriceService, "mapper", realMapper);
		submitToMyPriceService.persistNxMpSiteDictionary(null, null, null);
		
		NxMpSiteDictionary nxMpSiteDictionary = new NxMpSiteDictionary();
		when(nxMpSiteDictionaryRepository.findByNxTxnId(any())).thenReturn(nxMpSiteDictionary);
		submitToMyPriceService.persistNxMpSiteDictionary(null, "", null);
	}
	
	@Test
	public void createTransactionTest() throws SalesBusinessException {
		Map<String, Object> response = new HashMap<String, Object>();
		when(createTransactionService.callCreateTrans()).thenReturn(response);
		response.put(MyPriceConstants.RESPONSE_CODE, CommonConstants.SUCCESS_CODE);
		response.put(MyPriceConstants.RESPONSE_DATA, MyPriceConstants.RESPONSE_DATA);
		CreateTransactionResponse createTransactionRes = new CreateTransactionResponse();
		when(restClient.processResult(any(), any())).thenReturn(createTransactionRes);
		NxMpDeal nxMpDeal = new NxMpDeal();
		nxMpDeal.setPriceScenarioId(1L);
		when(nxMpDealRepository.saveAndFlush(any(NxMpDeal.class))).thenReturn(nxMpDeal);
		when(nxMpDealRepository.save(any(NxMpDeal.class))).thenReturn(nxMpDeal);
		FalloutDetailsRequest request1 = new FalloutDetailsRequest();
		request1.setNxSolutionId(1L);
		request1.setActionInd("appendToOtherDeal");
		request1.setAction("copySolution");
//		submitToMyPriceService.createTransaction(null, null, null, request1);
		
		NxDesignAudit nxDesignAudit = new NxDesignAudit();
		nxDesignAudit.setNxAuditId(1L);
		when(nxDesignAuditRepository.findByNxRefIdAndTransaction(any(), any())).thenReturn(nxDesignAudit);
//		submitToMyPriceService.createTransaction(null, 1L, null,request1);
		
		response.remove(MyPriceConstants.RESPONSE_DATA);
		response.put(MyPriceConstants.RESPONSE_CODE, 400);
//		submitToMyPriceService.createTransaction(null, null, null,request1);
		
//		submitToMyPriceService.createTransaction(null, 1L, null,request1);
		
		when(createTransactionService.callCreateTrans()).thenThrow(SalesBusinessException.class);
//		submitToMyPriceService.createTransaction(null, null, null,request1);
		
//		submitToMyPriceService.createTransaction(null, 1L, null,request1);
	}
	
//	@Test
//	public void getValuesFromRequestTest() {
//		submitToMyPriceService.getValuesFromRequest(null, null);
//		
//		//submitToMyPriceService.getValuesFromRequest("", null);
//		
//		List<Object> results = new ArrayList<>();
//		results.add("1");
//		when(jsonPathUtil.search(any(), any(), any())).thenReturn(results);
//		//submitToMyPriceService.getValuesFromRequest("", null);
//	}
	
	@Test
	public void saveNxDesignTest() {
		Map<String, Object> paramMap = new HashMap<>();
		submitToMyPriceService.saveNxDesign(null, null, paramMap);
		
		paramMap.put(FmoConstants.PORT_ID, "portId");
		ObjectNode design = realMapper.createObjectNode();
		design.put("siteId", 1);
		submitToMyPriceService.saveNxDesign(null, design, paramMap);
		
		NxDesign nxDesign = new NxDesign();
		NxDesignDetails nxDesignDetails = new NxDesignDetails();
		nxDesign.addNxDesignDetails(nxDesignDetails);
		when(nxDesignRepository.findByAsrItemIdAndNxSolutionDetail(any(), any())).thenReturn(nxDesign);
		submitToMyPriceService.saveNxDesign(null, design, paramMap);
	}
	
	@Test
	public void submitFMOToMyPriceTest() {
		ReflectionTestUtils.setField(submitToMyPriceService, "mapper", realMapper);
		NxSolutionDetail nxSolutionDetail = new NxSolutionDetail();
		NxRequestDetails requestDetails = new NxRequestDetails();
		requestDetails.setStatus(30L);
		NxOutputFileModel model = new NxOutputFileModel();
		String outputData = "{\r\n" + 
				"    \"solution\": {\r\n" + 
				"        \"offers\": [{\r\n" + 
				"                \"offerId\": \"7\",\r\n" + 
				"                \"site\": [{\r\n" + 
				"                        \"isLineItemPicked\": \"Y\",\r\n" + 
				"                        \"siteId\": 1,\r\n" + 
				"                        \"design\": [{}\r\n" + 
				"                        ]\r\n" + 
				"                    }\r\n" + 
				"                ]\r\n" + 
				"            }\r\n" + 
				"        ]\r\n" + 
				"    }\r\n" + 
				"}";
		model.setMpOutputJson(outputData);
		Map<String, Object> requestMap = new HashMap<>();
		requestMap.put(FmoConstants.ERATE_IND, "Y");
		requestMap.put(InrConstants.REQUEST_META_DATA_KEY, InrConstants.REQUEST_META_DATA_KEY);
		String offerName = "BVoIP";
		when(nxMpRepositoryService.getOfferNameByOfferId(anyInt())).thenReturn(offerName);
		when(configAndUpdateProcessingFmoService.getBvoipOfferName(any())).thenReturn(offerName);
		doReturn(false).when(submitToMyPriceService).isRESTEnabled(any(), any());
		doReturn(null).when(submitToMyPriceService).saveNxDesignFMO(any(), any(), any());
		List<NxDesign> nxDesigns = new ArrayList<>();
		NxDesign nxDesign = new NxDesign();
		nxDesign.setBundleCd(offerName);
		nxDesigns.add(nxDesign);
		when(nxDesignRepository.findByNxSolutionDetail(any())).thenReturn(nxDesigns);
		NxMpDeal nxMpDeal = new NxMpDeal();
		when(configAndUpdateProcessingFmoService.getDealBySolutionId(any())).thenReturn(nxMpDeal);
		Map<String, Object> responseMap = new HashMap<String, Object>();
		responseMap.put(MyPriceConstants.RESPONSE_STATUS, true);
		when(configDesignHelperService.processConfigDesignResponse(any())).thenReturn(responseMap);
		
		//submitToMyPriceService.submitFMOToMyPrice(nxSolutionDetail, requestDetails, model, requestMap);
		
		responseMap.put(MyPriceConstants.RESPONSE_STATUS, false);
		//submitToMyPriceService.submitFMOToMyPrice(nxSolutionDetail, requestDetails, model, requestMap);
		
		responseMap.put(MyPriceConstants.MP_API_ERROR, true);
		//submitToMyPriceService.submitFMOToMyPrice(nxSolutionDetail, requestDetails, model, requestMap);
		
		ReflectionTestUtils.setField(submitToMyPriceService, "documentId", "1");
		requestDetails.setStatus(80L);
		doReturn(true).when(submitToMyPriceService).isRESTEnabled(any(), any());
		outputData = "{\r\n" + 
				"    \"solution\": {\r\n" + 
				"        \"offers\": [{\r\n" + 
				"                \"offerId\": \"7\",\r\n" + 
				"                \"site\": [{\r\n" + 
				"                        \"isLineItemPicked\": \"Y\",\r\n" + 
				"                        \"siteId\": 1,\r\n" + 
				"						\"nxKeyId\": \"nxKeyIdValue\",\r\n" + 
				"                        \"design\": [{}\r\n" + 
				"                        ]\r\n" + 
				"                    }\r\n" + 
				"                ]\r\n" + 
				"            }\r\n" + 
				"        ]\r\n" + 
				"    }\r\n" + 
				"}";
		model.setMpOutputJson(outputData);
		String designData = "{\r\n" + 
				"    \"site\": [{\r\n" + 
				"            \"isLineItemPicked\": \"Y\",\r\n" + 
				"            \"siteId\": 1,\r\n" + 
				"            \"design\": [{}\r\n" + 
				"            ]\r\n" + 
				"        }\r\n" + 
				"    ]\r\n" + 
				"}";
		NxDesignDetails nxDesignDetail = new NxDesignDetails();
		nxDesignDetail.setDesignData(designData);
		nxDesignDetail.setType("type");
		nxDesign.addNxDesignDetails(nxDesignDetail);
		when(nexxusJsonUtility.getValue(any(), any())).thenReturn("NOTUS");
		doNothing().when(submitToMyPriceService).processConfigSolDesignResponse(any(), any(), any(), any(), any(), any(), any());
		//submitToMyPriceService.submitFMOToMyPrice(nxSolutionDetail, requestDetails, model, requestMap);
		
		doAnswer(new Answer() {
			@Override
			public Object answer(InvocationOnMock invocation) throws Throwable {
				Map<String, Object> restRequest = (Map<String, Object>) invocation.getArguments()[0];
				restRequest.put(MyPriceConstants.RESPONSE_STATUS, false);
				return null;
			}
			
		}).when(configAndUpdateRestProcessingService).callMpConfigAndUpdate(any(), any());
		//submitToMyPriceService.submitFMOToMyPrice(nxSolutionDetail, requestDetails, model, requestMap);
		
		outputData = "{\r\n" + 
				"    \"solution\": {\r\n" + 
				"        \"offers\": [{\r\n" + 
				"                \"offerId\": \"7\",\r\n" + 
				"                \"site\": [{\r\n" + 
				"                        \"isLineItemPicked\": \"Y\",\r\n" + 
				"                        \"siteId\": 1,\r\n" + 
				"                        \"design\": [{\r\n" + 
				"                                \"nxKeyId\": \"nxKeyIdValue\",\r\n" + 
				"                                \"uniqueId\": \"uniqueIdValue\"\r\n" + 
				"                            }\r\n" + 
				"                        ]\r\n" + 
				"                    }\r\n" + 
				"                ]\r\n" + 
				"            }\r\n" + 
				"        ]\r\n" + 
				"    }\r\n" + 
				"}";
		model.setMpOutputJson(outputData);
		//submitToMyPriceService.submitFMOToMyPrice(nxSolutionDetail, requestDetails, model, requestMap);
	}
	
	@Test
	public void isRESTEnabledTest() {
		List<NxLookupData> restProductDetails = new ArrayList<>();
		when(nxLookupDataRepository
				.findByItemIdAndDatasetAndCriteriaAndDesc(any(), any(), any(), any())).thenReturn(restProductDetails);
		boolean res = submitToMyPriceService.isRESTEnabled(null, null);
		assertFalse(res);
		
		NxLookupData nxLookupData = new NxLookupData();
		restProductDetails.add(nxLookupData);
		res = submitToMyPriceService.isRESTEnabled(null, null);
		assertTrue(res);
	}
	
	@Test
	public void saveNxDesignFMOTest() throws IOException {
		NxSolutionDetail nxSolutionDetail = new NxSolutionDetail();
		Map<String, ObjectNode> nxKeyIdToData = new HashMap<>();
		Map<String, Object> paramMap = new HashMap<>();
		Map<String, NxDesignDetails> nxSiteIdUniqueIdToNxDesignDetailsMap = new HashMap<>();
		Map<String, List<NxDesignDetails>> nxSiteIdToNxDesignDetailsListMap = new HashMap<>();
		String data = "{\r\n" + 
				"    \"site\": [{\r\n" + 
				"            \"isLineItemPicked\": \"Y\",\r\n" + 
				"            \"siteId\": 1,\r\n" + 
				"			\"nxSiteId\": 2,\r\n" + 
				"            \"design\": [{}\r\n" + 
				"            ]\r\n" + 
				"        }\r\n" + 
				"    ]\r\n" + 
				"}";
		ObjectNode objectNode = (ObjectNode) realMapper.readTree(data);
		nxKeyIdToData.put("nxKeyId", objectNode);
		NxDesign nxDesign = new NxDesign();
		NxDesignDetails nxDesignDetails = new NxDesignDetails();
		nxDesign.addNxDesignDetails(nxDesignDetails);
		when(nxDesignRepository.findByAsrItemIdAndNxSolutionDetail(any(), any())).thenReturn(nxDesign);
		
		submitToMyPriceService.saveNxDesignFMO(nxSolutionDetail, nxKeyIdToData, paramMap, nxSiteIdUniqueIdToNxDesignDetailsMap, nxSiteIdToNxDesignDetailsListMap);
	}
	
	@Test
	public void findTypeFromNxKeyIdTest() {
		String nxKeyId = "1$US$A/B";
		String res = submitToMyPriceService.findTypeFromNxKeyId(nxKeyId);
		assertEquals("A/B", res);
	}
	
	@Test
	public void saveNxDesignFMOTest1() {
		JsonNode design = realMapper.createObjectNode();
		Map<String, Object> paramMap = new HashMap<>();
		paramMap.put(FmoConstants.SITE_ID, "1");
		submitToMyPriceService.saveNxDesignFMO(null, design, paramMap);
		
		NxDesign nxDesign = new NxDesign();
		NxDesignDetails nxDesignDetails = new NxDesignDetails();
		nxDesign.addNxDesignDetails(nxDesignDetails);
		when(nxDesignRepository.findBySiteIdAndNxSolutionDetail(any(), any())).thenReturn(nxDesign);
		submitToMyPriceService.saveNxDesignFMO(null, design, paramMap);
		
		doReturn(nxDesignDetails).when(submitToMyPriceService).getNxDesignDtllsByPortIdAndProductName(any(), any(), any());
		submitToMyPriceService.saveNxDesignFMO(null, design, paramMap);
		
		paramMap.clear();
		submitToMyPriceService.saveNxDesignFMO(null, design, paramMap);
	}
	
	@Test
	public void getNxDesignDtllsByPortIdAndProductNameTest() {
		List<NxDesignDetails> nxDesignDetailslst = new ArrayList<>();
		NxDesignDetails nxDesignDetails = new NxDesignDetails();
		nxDesignDetailslst.add(nxDesignDetails);
		nxDesignDetails.setComponentId("1");
		nxDesignDetails.setProductName("AVPN");
		NxDesignDetails res = submitToMyPriceService.getNxDesignDtllsByPortIdAndProductName("1", "AVPN", nxDesignDetailslst);
		assertSame(nxDesignDetails, res);
	}
	
	@Test
	public void persistDesignBasedSolutionCategoryTest() {
		Map<String, Object> paramMap = new HashMap<>();
		paramMap.put(MyPriceConstants.NX_REQ_ID, 1L);
		NxValidationRules nxValidationRule = new NxValidationRules();
		nxValidationRule.setName("productNameValue");
		List<NxOutputFileModel> nxOutputFileModels = new ArrayList<>();
		NxOutputFileModel nxOutputFileModel = new NxOutputFileModel();
		nxOutputFileModel.setMpOutputJson("{}");
		nxOutputFileModels.add(nxOutputFileModel);
		NxSolutionDetail nxSolutionDetail = new NxSolutionDetail();
		when(nexxusJsonUtility.getValue(any(), any())).thenReturn("circuitId");
		String subDataPath = "{\r\n" + 
				"    \"usageRule\": [{\r\n" + 
				"            \"ruleName\": \"Interstate\",\r\n" + 
				"            \"jsonDataPath\": \"$..usageDetails.[?(@.nxGeoCode== 'VTNS Interstate Voice' && @.nxVoiceTiming=='18/1')]###$..usageDetails.[?(@.nxGeoCode== 'VTNS Interstate Voice'  && @.nxVoiceTiming=='18/6')]###$..usageDetails.[?(@.nxGeoCode== 'VTNS Interstate Voice'  && @.nxVoiceTiming=='6/1')]###$..usageDetails.[?(@.nxGeoCode== 'VTNS Interstate Voice'  && @.nxVoiceTiming=='6/6')]\",\r\n" + 
				"            \"jsonarray\": \"usageDetails\"\r\n" + 
				"        }, {\r\n" + 
				"            \"ruleName\": \"Intrastate\",\r\n" + 
				"            \"jsonValidationPath\": \"$..usageDetails.[?(@.nxGeoCode== 'VTNS Intrastate Voice')].nxState\",\r\n" + 
				"            \"jsonValidationKey\": \"CURRENT_STATECODE\",\r\n" + 
				"            \"jsonDataPath\": \"$..usageDetails.[?(@.nxGeoCode== 'VTNS Intrastate Voice' && @.nxState == 'CURRENT_STATECODE' && @.nxVoiceTiming=='18/1')]### $..usageDetails.[?(@.nxGeoCode== 'VTNS Intrastate Voice' && @.nxState == 'CURRENT_STATECODE' && @.nxVoiceTiming=='18/6')]### $..usageDetails.[?(@.nxGeoCode== 'VTNS Intrastate Voice' && @.nxState == 'CURRENT_STATECODE' && @.nxVoiceTiming=='6/6')]\",\r\n" + 
				"            \"jsonarray\": \"usageDetails\"\r\n" + 
				"        }\r\n" + 
				"    ]\r\n" + 
				"}";
		nxValidationRule.setSubDataPath(subDataPath);
		Map<String, List<String>> productInfoMap = new HashMap<>();
		when(configAndUpdatePricingUtilInr.getConfigProdutMapFromLookup(any())).thenReturn(productInfoMap);
		List<String> productInfo = Arrays.asList("productNameValue");
		productInfoMap.put("productName", productInfo);
		List<Object> validationPathList = new ArrayList<>();
		when(inrQualifyService.getCircuits(any(), any())).thenReturn(validationPathList);
		validationPathList.add("{}");
		doReturn(false).when(submitToMyPriceService).skipCktProcessing(any(), any(), any());
		List<NxInrDesignDetails> resultList= new ArrayList<>();
		when(nxInrDesignDetailsRepository.findByNxSolutionIdAndUsagecategory(anyLong(),anyString(),anyString())).thenReturn(resultList);
		submitToMyPriceService.persistDesignBasedSolutionCategory(nxValidationRule, paramMap, nxOutputFileModels, nxSolutionDetail, null);
	}
	
	@Test
	public void processConfigSolDesignResponseTest() throws SalesBusinessException, IOException {
		ReflectionTestUtils.setField(submitToMyPriceService, "mypriceGetTranSactionLineRetryTimes", 3);
		ReflectionTestUtils.setField(submitToMyPriceService, "mapper", realMapper);
		Map<String, Object> requestMap = new HashMap<>();
		Map<String, Object> requestMetaDataMap = new HashMap<>();
		requestMap.put(InrConstants.REQUEST_META_DATA_KEY, requestMetaDataMap);
		Map<String, NxDesignDetails> nxSiteIdUniqueIdToNxDesignDetailsMap = new HashMap<>();
		NxDesignDetails nxDesignDetails = new NxDesignDetails();
		nxDesignDetails.setType("type");
		String designData = "{\r\n" + 
				"    \"site\": [{\r\n" + 
				"            \"isLineItemPicked\": \"Y\",\r\n" + 
				"            \"siteId\": 1,\r\n" + 
				"			\"nxSiteId\": \"nxSiteId\",\r\n" + 
				"            \"design\": [{}\r\n" + 
				"            ]\r\n" + 
				"        }\r\n" + 
				"    ]\r\n" + 
				"}";
		nxDesignDetails.setDesignData(designData);
		NxDesign nxDesgin = new NxDesign();
		nxDesgin.setNxDesignId(1L);
		nxDesgin.addNxDesignDetails(nxDesignDetails);
		nxSiteIdUniqueIdToNxDesignDetailsMap.put("nxSiteIdUniqueId", nxDesignDetails);
		Map<String, List<NxDesignDetails>> nxSiteIdToNxDesignDetailsListMap = new HashMap<>();
		List<NxDesignDetails> nxDesignDetailsList = new ArrayList<>();
		nxDesignDetailsList.add(nxDesignDetails);
		nxSiteIdToNxDesignDetailsListMap.put("nxSiteId", nxDesignDetailsList);
		Map<String, String> nxSiteIdUniqueIdToUsocIdMap = new HashMap<>();
		nxSiteIdUniqueIdToUsocIdMap.put("nxSiteIdUniqueId", "usocId");
		Map<String, String> nxSiteIdUniqueIdToPortIdMap = new HashMap<>();
		nxSiteIdUniqueIdToPortIdMap.put("nxSiteIdUniqueId", "portId");
		Long nxTransactionId = 1L;
		String mpTransactionId = "1";
		submitToMyPriceService.processConfigSolDesignResponse(requestMap, nxTransactionId, mpTransactionId,
				nxSiteIdUniqueIdToPortIdMap, nxSiteIdUniqueIdToUsocIdMap, nxSiteIdUniqueIdToNxDesignDetailsMap,
				nxSiteIdToNxDesignDetailsListMap);
		
		GetTransactionLineResponse response = new GetTransactionLineResponse();
		response.setLimit(1000L);
		when(getTransactionLineServiceImpl.getTransactionLineConfigDesignSolution(any(), any(), any(), anyMap())).thenReturn(response);
		response.setHasMore(false);
		List<GetTransactionLineItem> itemList = new ArrayList<>();
		GetTransactionLineItem transLineItem = new GetTransactionLineItem();
		transLineItem.setBomId("BOM_Solution");
		transLineItem.setDocumentNumber("2");
		GetTransactionLineItem transLineItem1 = new GetTransactionLineItem();
		transLineItem1.setDocumentNumber("3");
		transLineItem1.setBomId("BOM_BVoIP");
		transLineItem1.setParentLineitem("Solution");
		transLineItem1.setBomParentId("BOM_Solution");
		transLineItem1.setParentDocNumber("2");
		GetTransactionLineItem transLineItem2 = new GetTransactionLineItem();
		transLineItem2.setDocumentNumber("4");
		transLineItem2.setBomId("BOM_BVoIPBVOIP_PS_IPFR_C");
		transLineItem2.setParentLineitem("BVoIP");
		transLineItem2.setBomParentId("BOM_BVoIP");
		transLineItem2.setParentDocNumber("3");
		transLineItem2.setExternalKey("nxSiteId");
		transLineItem2.setUniqueIds("UniqueId");
		GetTransactionLineItem transLineItem3 = new GetTransactionLineItem();
		transLineItem3.setDocumentNumber("5");
		transLineItem3.setBomId("BOM_BVoIPBVOIP_PS_IPFR_C");
		transLineItem3.setParentLineitem("BVoIP");
		transLineItem3.setBomParentId("BOM_BVoIP");
		transLineItem3.setParentDocNumber("3");
		transLineItem3.setExternalKey("nxSiteId");
		transLineItem3.setUniqueIds("UniqueId1");
		itemList.add(transLineItem);
		itemList.add(transLineItem1);
		itemList.add(transLineItem2);
		itemList.add(transLineItem3);
		response.setItems(itemList);
		submitToMyPriceService.processConfigSolDesignResponse(requestMap, nxTransactionId, mpTransactionId,
				nxSiteIdUniqueIdToPortIdMap, nxSiteIdUniqueIdToUsocIdMap, nxSiteIdUniqueIdToNxDesignDetailsMap,
				nxSiteIdToNxDesignDetailsListMap);

		nxSiteIdUniqueIdToNxDesignDetailsMap.put("nxSiteIdUniqueId", nxDesignDetails);
		nxSiteIdUniqueIdToNxDesignDetailsMap.put("nxSiteId1UniqueId", nxDesignDetails);
		submitToMyPriceService.processConfigSolDesignResponse(requestMap, nxTransactionId, mpTransactionId,
				nxSiteIdUniqueIdToPortIdMap, nxSiteIdUniqueIdToUsocIdMap, nxSiteIdUniqueIdToNxDesignDetailsMap,
				nxSiteIdToNxDesignDetailsListMap);
		
		nxSiteIdUniqueIdToNxDesignDetailsMap.put("nxSiteIdUniqueId", nxDesignDetails);
		nxSiteIdUniqueIdToNxDesignDetailsMap.put("nxSiteId1UniqueId", nxDesignDetails);
		ReflectionTestUtils.setField(submitToMyPriceService, "mapper", mapper);
		when(mapper.readTree(anyString())).thenThrow(IOException.class);
		submitToMyPriceService.processConfigSolDesignResponse(requestMap, nxTransactionId, mpTransactionId,
				nxSiteIdUniqueIdToPortIdMap, nxSiteIdUniqueIdToUsocIdMap, nxSiteIdUniqueIdToNxDesignDetailsMap,
				nxSiteIdToNxDesignDetailsListMap);
		
		when(getTransactionLineServiceImpl.getTransactionLineConfigDesignSolution(any(), any(), any(), anyMap())).thenThrow(SalesBusinessException.class);
		submitToMyPriceService.processConfigSolDesignResponse(requestMap, nxTransactionId, mpTransactionId,
				nxSiteIdUniqueIdToPortIdMap, nxSiteIdUniqueIdToUsocIdMap, nxSiteIdUniqueIdToNxDesignDetailsMap,
				nxSiteIdToNxDesignDetailsListMap);
	
	}
	
	@Test
	public void getKeyTest() {
		Object res = submitToMyPriceService.getKey(null, null);
		assertNull(res);
	}
}
