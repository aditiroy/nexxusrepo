package com.att.sales.nexxus.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyList;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.anyMap;
import static org.mockito.Mockito.anySet;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doNothing;
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

import org.junit.Before;
import org.junit.Ignore;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.stubbing.Answer;
import org.springframework.test.util.ReflectionTestUtils;

import com.att.sales.nexxus.admin.model.UploadEthTokenRequest;
import com.att.sales.nexxus.constant.CustomJsonConstants;
import com.att.sales.nexxus.constant.MyPriceConstants;
import com.att.sales.nexxus.constant.StringConstants;
import com.att.sales.nexxus.dao.model.NxAccessPricingData;
import com.att.sales.nexxus.dao.model.NxDesignAudit;
import com.att.sales.nexxus.dao.model.NxInrDesign;
import com.att.sales.nexxus.dao.model.NxInrDesignDetails;
import com.att.sales.nexxus.dao.model.NxLookupData;
import com.att.sales.nexxus.dao.model.NxMpDeal;
import com.att.sales.nexxus.dao.model.NxOutputFileModel;
import com.att.sales.nexxus.dao.model.NxRequestDetails;
import com.att.sales.nexxus.dao.model.NxValidationRules;
import com.att.sales.nexxus.dao.model.solution.NxSolutionDetail;
import com.att.sales.nexxus.dao.repository.NxAccessPricingDataRepository;
import com.att.sales.nexxus.dao.repository.NxDesignAuditRepository;
import com.att.sales.nexxus.dao.repository.NxDesignRepository;
import com.att.sales.nexxus.dao.repository.NxInrDesignRepository;
import com.att.sales.nexxus.dao.repository.NxLookupDataRepository;
import com.att.sales.nexxus.dao.repository.NxMpDealRepository;
import com.att.sales.nexxus.dao.repository.NxMpSiteDictionaryRepository;
import com.att.sales.nexxus.dao.repository.NxMpSolutionDetailsRepository;
import com.att.sales.nexxus.dao.repository.NxOutputFileRepository;
import com.att.sales.nexxus.dao.repository.NxRequestDetailsRepository;
import com.att.sales.nexxus.dao.repository.NxSolutionSiteRepository;
import com.att.sales.nexxus.dao.repository.NxValidationRulesRepository;
import com.att.sales.nexxus.fallout.service.FalloutDetailsImpl;
import com.att.sales.nexxus.model.AccessPricingUiRequest;
import com.att.sales.nexxus.model.RestErrors;
import com.att.sales.nexxus.myprice.transaction.dao.service.NxMpRepositoryService;
import com.att.sales.nexxus.myprice.transaction.model.CreateTransactionResponse;
import com.att.sales.nexxus.myprice.transaction.service.ConfigAndUpdatePricingUtilInr;
import com.att.sales.nexxus.myprice.transaction.service.ConfigAndUpdateProcessingIglooService;
import com.att.sales.nexxus.myprice.transaction.service.ConfigAndUpdateProcessingInrService;
import com.att.sales.nexxus.myprice.transaction.service.ConfigAndUpdateRestProcessingService;
import com.att.sales.nexxus.myprice.transaction.service.ConfigDesignHelperService;
import com.att.sales.nexxus.myprice.transaction.service.IUpdateTransactionImpl;
import com.att.sales.nexxus.myprice.transaction.service.MyPriceTransactionUtil;
import com.att.sales.nexxus.util.JsonPathUtil;
import com.att.sales.nexxus.util.NexxusJsonUtility;
import com.att.sales.nexxus.util.RestClientUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;


@ExtendWith(MockitoExtension.class)
public class ProcessINRtoMPTest {

	@InjectMocks
	private ProcessINRtoMP processINRtoMP;
	
	@Mock
	private SubmitToMyPriceService submitToMyPriceService;

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
	private RestClientUtil restClient;

	@Mock
	private NxDesignAuditRepository nxDesignAuditRepository;

	@Mock
	private MyPriceTransactionUtil myPriceTransactionUtil;

	@Mock
	private IUpdateTransactionImpl iUpdateTransactionImpl;

	@Mock
	private NxMpSolutionDetailsRepository nxMpSolutionDetailsRepository;

	@Mock
	private NxMpSiteDictionaryRepository nxMpSiteDictionaryRepository;

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
	private ConfigAndUpdateProcessingInrService configAndUpdateProcessingInrService;

	@Mock
	private InrQualifyService inrQualifyService;

	@Mock
	private ConfigAndUpdateProcessingIglooService configAndUpdateProcessingIglooService;

	@Mock
	private ConfigDesignHelperService configDesignHelperService;

	@Mock
	private MailServiceImpl mailServiceImpl;

	@Mock
	NxLookupDataRepository nxLookupDataRepository;

	@Mock
	private NexxusService nexxusService;

	@Mock
	private FalloutDetailsImpl falloutDetailsImpl;
	
	@Mock
	private ObjectNode jsonNode;
	
	@Mock
	private ConfigAndUpdatePricingUtilInr configAndUpdatePricingUtilInr;

	@Mock
	private NxAccessPricingDataRepository nxAccessPricingDataRepository;
	
	@Mock
	private ConfigAndUpdateRestProcessingService configAndUpdateRestProcessingService;

	NxSolutionDetail nxSolutionDetail;
	List<Long> nxRequestGrpId;
	Map<String, Object> requestMetaDataMap;
	Map<String, Object> configUpdateResMap;
	String source;
	CreateTransactionResponse createTransactionResponse;
	List<NxAccessPricingData> nxAccessPricingDatas;
	String prodName;
	boolean isReconfigure;

	@BeforeEach
	public void initData() {
		nxSolutionDetail = new NxSolutionDetail();
		nxSolutionDetail.setNxSolutionId(1L);
		nxRequestGrpId = new ArrayList<>();
		nxRequestGrpId.add(1L);
		requestMetaDataMap = new HashMap<>();
		requestMetaDataMap.put("NX_MP_STATUS_IND", "S");
		source = "INR";
		createTransactionResponse = new CreateTransactionResponse();
		createTransactionResponse.setNxTransacId(1L);
		createTransactionResponse.setMyPriceTransacId("35436");
		nxAccessPricingDatas = new ArrayList<>();

		NxAccessPricingData nxAccessPricingData = new NxAccessPricingData();
		nxAccessPricingData.setAccessBandwidth(20);
		nxAccessPricingData.setEthToken("ethToken");
		nxAccessPricingData.setIglooQuoteId("38.987676");
		nxAccessPricingData.setIncludeYn("Y");
		nxAccessPricingData.setLocationYn("New");
		nxAccessPricingData.setMrc("22.67");
		nxAccessPricingData.setNrc("45.8888");
		nxAccessPricingData.setAccessType("access");
		nxAccessPricingData.setNxAccessPriceId(new Long(2l));
		nxAccessPricingData.setReqContractTerm("contractTerm");
		nxAccessPricingData.setReqCountry("US");
		nxAccessPricingData.setLocationYn("New");
		nxAccessPricingData.setIncludeYn("Y");
		nxAccessPricingData.setIntermediateJson(
				"{\"reqStreetAddress\":\"8665 CYPRESS WATERS BLVD\",\"reqCity\":\"Irving\",\"reqState\":\"TX\",\"reqZipCode\":\"75063\",\"country\":\"United States\",\"address\":\"8665 CYPRESS WATERS BLVD\",\"city\":\"IRVING\",\"state\":\"TX\",\"zipCode\":\"75063-7337\",\"reqAccessBandwidth\":\"20\",\"reqVendor\":\"SBC\",\"reqIlecSwc\":\"DLLSTXNO\",\"attEthPop\":\"DLLSTXTL\",\"service\":\"AVPN-ETH\",\"bandwidth\":\"20\",\"accessArch\":\"Switched\",\"physicalInterface\":\"100BaseTX Electrical\",\"mrc\":\"906.00\",\"discountMonthlyRecurringPrice\":\"688.56\",\"nonRecurringCharge\":\"1630.00\",\"ethToken\":\"ETH1005W7J\",\"iglooQuoteID\":\"121566.001\",\"flowType\":\"IGL\",\"nxSiteId\":443326}");
		nxAccessPricingDatas.add(nxAccessPricingData);
		isReconfigure = false;
		prodName = "AVPN";
		ReflectionTestUtils.setField(processINRtoMP, "documentId", "577757");
	}
	@Disabled
	@Test
	public void testProcessINR() throws JsonProcessingException {
		List<NxRequestDetails> nxRequestDetailList = new ArrayList<>();
		NxRequestDetails nxRequestDetails = new NxRequestDetails();
		nxRequestDetails.setNxRequestGroupId(1L);
		nxRequestDetails.setNxReqId(1L);
		nxRequestDetails.setNxRequestGroupName(MyPriceConstants.ACCESS_GROUP);
		nxRequestDetails.setProduct("AVPN");
		List<NxOutputFileModel> nxOutputFiles =new ArrayList<>();
		NxOutputFileModel nxOutputFileModel = new NxOutputFileModel();
		nxOutputFileModel.setMpOutputJson("{ \"beginBillMonth\": \"August 2019\", \"service\": \"AVPN\", \"searchCriteriaValue\": \"518912795\", \"customerName\": \"ALLWEILER AS\", \"searchCriteria\": \"DunsNumber\", \"accountDetails\": [ { \"currency\": \"NOK\", \"custName\": \"Allweiler AS\", \"site\": [ { \"country\": \"NO\", \"city\": \"HVALSTAD\", \"address\": \"NYE VAKAAS VEI 4\", \"siteId\": \"90922336\", \"custPostalcode\": \"1395\", \"design\": [ { \"siteName\": \"3036816\", \"accessCarrier\": \"SAREAQ\", \"portType\": \"ETH\", \"technology\": \"Ethernet (Gateway Interconnect/ESP ETH Shared)\", \"portSpeed\": \"2000\", \"accessBandwidth\": \"2000 Kbps\", \"priceDetails\": [ { \"priceType\": \"PORTBEID\", \"beid\": \"19617\", \"quantity\": \"1\", \"localListPrice\": \"2630\", \"actualPrice\": \"815.3\", \"secondaryKey\": \"#Intl#MPLS Port#Flat Rate#2M#Enet#Enet#VPN Transport Connection#per port#19617#18030\", \"elementType\": \"Port\", \"uniqueId\": \"#MPLS Port#MPLS Port - 2M#ENET#2Mbps#ENET#VPN Transport#Connection#Each\" }  ], \"accessSecondaryKey\": \"#Intl#Norway#NO#AVPN#N/A#N/A\" } ], \"FALLOUTMATCHINGID\": \"0000000001/AVPNPricingInventory/Body/AccountDetails/AVPNService/AVPNInventoryDetails/SiteDetails/FALLOUTMATCHINGID\", \"secondaryKey\": \"#Intl#AVPN#Norway#NO#EMEA\" } ] } ], \"flowType\": \"INR\" }");
		nxOutputFiles.add(nxOutputFileModel);
		nxRequestDetails.setNxOutputFiles(nxOutputFiles);
		nxRequestDetailList.add(nxRequestDetails);
		when(nxRequestDetailsRepository.findByNxSolutionId(Mockito.anyLong())).thenReturn(nxRequestDetailList);
		when(falloutDetailsImpl.isServiceAccessGroup(nxRequestDetailList)).thenReturn(false);
		List<NxLookupData> nxLookupLst= new ArrayList<>();
		NxLookupData nxLookupData = new NxLookupData();
		nxLookupData.setItemId("2");
		nxLookupData.setCriteria("AVPN");
		nxLookupLst.add(nxLookupData);
		when(nxLookupDataRepository.findByDatasetName(any())).thenReturn(nxLookupLst);
		when(nxRequestDetailsRepository.findbyNxSolutionDetailAndNxRequestGroupIdAndActiveYn(Mockito.any(),
				Mockito.anyLong(), Mockito.anyString())).thenReturn(nxRequestDetailList);
		List<NxValidationRules> nxValidationCircuitBlockRules= new ArrayList<>();
		NxValidationRules nxValidationRule1 = new NxValidationRules();
		nxValidationRule1.setActive("Y");
		nxValidationRule1.setName("AVPN");
		nxValidationRule1.setValue("circuitId:nxSiteId");
		nxValidationRule1.setSubData("design");
		nxValidationRule1.setSubDataPath("$.design[*]");
		nxValidationRule1.setDataPath("$.accountDetails..site[*]");
		nxValidationRule1.setOffer("AVPN");
		nxValidationCircuitBlockRules.add(nxValidationRule1);
		
		NxValidationRules nxValidationRule2 = new NxValidationRules();
		nxValidationRule2.setActive("Y");
		nxValidationRule2.setName("AVPN");
		nxValidationRule2.setValue("circuitId:nxSiteId");
		nxValidationRule2.setSubData("design");
		nxValidationRule2.setSubDataPath("$.design[*]");
		nxValidationRule2.setDataPath("$.accountDetails..site[*]");
		nxValidationRule2.setOffer("AVPN");
		nxValidationRule2.setValidationGroup("DESIGN_BLOCK");
		nxValidationCircuitBlockRules.add(nxValidationRule2);
		
		when(nxValidationRulesRepository.findByValidationGroupAndOfferAndActiveAndFlowType(Mockito.anyString(),
				Mockito.anyString(), Mockito.anyString(),Mockito.anyString())).thenReturn(nxValidationCircuitBlockRules);
		when(nxValidationRulesRepository.findByValidationGroupAndOfferAndActiveAndFlowTypeAndName(Mockito.anyString(),
				Mockito.anyString(), Mockito.anyString(),Mockito.anyString(),Mockito.anyString())).thenReturn(nxValidationCircuitBlockRules);
		List<NxDesignAudit> nxDesignAudits = new ArrayList<>();
		NxDesignAudit nxDesignAudit = new NxDesignAudit();
		nxDesignAudit.setStatus("AVPN");
		nxDesignAudit.setData("[BREC872247ATI, DNEC511653ATI, IUEC652168ATI, IUEC711110ATI, MFEC996431ATI, IUEC868037ATI, MLEC876761ATI, BBEC532695ATI, IZEC545788ATI, IUEC763349ATI, IUEC733475ATI, DNEC509257ATI, IUEC558973ATI, IUEC849915ATI, DHEC129464811ATI, BKEC861711ATI, IZEC519863ATI, DHEC127539811ATI, IUEC642943ATI, IUEC767741ATI, IUEC838821ATI]");
		nxDesignAudit.setNxSubRefId("1");
		nxDesignAudits.add(nxDesignAudit);
		when(nxDesignAuditRepository.findByNxSubRefIdAndTransaction(anyList(),anyString())).thenReturn(nxDesignAudits);
		List<NxLookupData> nxLookupDataList2 = new ArrayList<>();
		when(nxLookupDataRepository.findByItemIdsAndDatasetAndCriteriaAndDesc
				(anySet(),anyString(),anyString(),anyString())).thenReturn(nxLookupDataList2);

	
		List<Object> circuits = new ArrayList<>();
		circuits.add("{ \"country\": \"NO\", \"city\": \"HVALSTAD\", \"address\": \"NYE VAKAAS VEI 4\", \"siteId\": \"90922336\", \"custPostalcode\": \"1395\", \"design\": [ { \"siteName\": \"3036816\", \"accessCarrier\": \"SAREAQ\", \"portType\": \"ETH\", \"technology\": \"Ethernet (Gateway Interconnect/ESP ETH Shared)\", \"portSpeed\": \"2000\", \"accessBandwidth\": \"2000 Kbps\", \"priceDetails\": [ { \"priceType\": \"PORTBEID\", \"beid\": \"19617\", \"quantity\": \"1\", \"localListPrice\": \"2630\", \"actualPrice\": \"815.3\", \"secondaryKey\": \"#Intl#MPLS Port#Flat Rate#2M#Enet#Enet#VPN Transport Connection#per port#19617#18030\", \"elementType\": \"Port\", \"uniqueId\": \"#MPLS Port#MPLS Port - 2M#ENET#2Mbps#ENET#VPN Transport#Connection#Each\" } ], \"accessSecondaryKey\": \"#Intl#Norway#NO#AVPN#N/A#N/A\" } ], \"FALLOUTMATCHINGID\": \"0000000001/AVPNPricingInventory/Body/AccountDetails/AVPNService/AVPNInventoryDetails/SiteDetails/FALLOUTMATCHINGID\", \"secondaryKey\": \"#Intl#AVPN#Norway#NO#EMEA\" } ");
		when(inrQualifyService.getCircuits(any(),any())).thenReturn(circuits);
		when(mapper.valueToTree(any())).thenReturn(jsonNode);
		when(submitToMyPriceService.skipCktProcessing(any(),any(),any())).thenReturn(false);
		when(mapper.writeValueAsString(any())).thenReturn("data");
		when(nexxusJsonUtility.getValue(any(),any())).thenReturn("cktId");
		when(jsonNode.path(anyString())).thenReturn(jsonNode);
		when(jsonNode.isMissingNode()).thenReturn(false);
		Map<String, Object> respMap = new HashMap<>();
		respMap.put(MyPriceConstants.RESPONSE_STATUS,true);
		respMap.put(MyPriceConstants.CONFIG_SOLN_DESIGN_RESPONSE, true);
		when(configDesignHelperService.processConfigDesignResponse(any())).thenReturn(respMap);
		List<Object[]> nxInrDesigns = new ArrayList<>();
		Object obj[] = new Object[2] ;
		obj[0]="567";
		obj[1]="IVEC990793ATI";
		nxInrDesigns.add(obj);
		when(nxInrDesignRepository.findDesignByNxSolutionId(anyLong(),anyList(),anyString(),anyList())).thenReturn(nxInrDesigns);
		processINRtoMP.process(nxSolutionDetail, nxRequestGrpId, requestMetaDataMap, configUpdateResMap, source, createTransactionResponse, prodName, isReconfigure);
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void testProcessIGLOO() {
		NxMpDeal nxMpDeal=new NxMpDeal();
		when(nxMpDealRepository.findByNxTxnId(any())).thenReturn(nxMpDeal);
		Map<String, Object> paramMap=mock(Map.class);
		when(paramMap.containsKey(MyPriceConstants.CONFIG_SOLN_DESIGN_RESPONSE)).thenReturn(true);
		when(paramMap.get(MyPriceConstants.CONFIG_SOLN_DESIGN_RESPONSE)).thenReturn(true);
		Map<String, Object> respMap = new HashMap<>();
		respMap.put(MyPriceConstants.RESPONSE_STATUS,true);
		respMap.put(MyPriceConstants.CONFIG_SOLN_DESIGN_RESPONSE, true);
		when(configDesignHelperService.processConfigDesignResponse(any())).thenReturn(respMap);
		processINRtoMP.process(nxSolutionDetail, null, requestMetaDataMap, configUpdateResMap, source, createTransactionResponse, prodName, isReconfigure);
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void testProcessIGLOOElse() {
		NxMpDeal nxMpDeal=new NxMpDeal();
		when(nxMpDealRepository.findByNxTxnId(any())).thenReturn(nxMpDeal);
		Map<String, Object> paramMap=mock(Map.class);
		when(paramMap.containsKey(MyPriceConstants.CONFIG_SOLN_DESIGN_RESPONSE)).thenReturn(true);
		when(paramMap.get(MyPriceConstants.CONFIG_SOLN_DESIGN_RESPONSE)).thenReturn(false);
		Map<String, Object> respMap = new HashMap<>();
		respMap.put(MyPriceConstants.RESPONSE_STATUS,false);
		respMap.put(MyPriceConstants.CONFIG_SOLN_DESIGN_RESPONSE, false);
		when(configDesignHelperService.processConfigDesignResponse(any())).thenReturn(respMap);
		processINRtoMP.process(nxSolutionDetail, null, requestMetaDataMap, configUpdateResMap, source, createTransactionResponse, prodName, isReconfigure);
	}
	
	@Test
	public void testGetServiceAccessGroupId() {
		List<NxRequestDetails> nxRequestDetailList = new ArrayList<>();
		NxRequestDetails nxRequestDetails = new NxRequestDetails();
		nxRequestDetails.setNxRequestGroupId(1L);
		nxRequestDetails.setNxRequestGroupName(MyPriceConstants.ACCESS_GROUP);
		nxRequestDetails.setEdfAckId("4547");
		nxRequestDetailList.add(nxRequestDetails);
		when(nxRequestDetailsRepository.findByNxSolutionId(any())).thenReturn(nxRequestDetailList);
		when(falloutDetailsImpl.isServiceAccessGroup(any())).thenReturn(true);
		processINRtoMP.getServiceAccessGroupId(1L);
	}

	@Test
	public void testGetDataSetElse() {
		when(nxLookupDataRepository.findByDatasetName(any())).thenReturn(new ArrayList<>());
		processINRtoMP.getDataSet("datasetName");
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void testfindInrRestRequests() {
		List<NxRequestDetails> nxRequestDetailList = new ArrayList<>();
		NxRequestDetails nxRequestDetails = new NxRequestDetails();
		nxRequestDetails.setProduct("AVPN");
		nxRequestDetails.setNxReqId(1L);
		nxRequestDetailList.add(nxRequestDetails);
		Map<Long, String> restRequests = new HashMap<>();
		List<NxLookupData> nxLookupDataList1 = new ArrayList<>();
		NxLookupData nxLookupData= new NxLookupData();
		nxLookupDataList1.add(nxLookupData);
		when(nxLookupDataRepository.findByItemIdsAndDatasetAndCriteriaAndDesc
				(anySet(),anyString(),anyString(),anyString())).thenReturn(nxLookupDataList1);
		Map<Long, String>  result=processINRtoMP.findInrRestRequests(nxRequestDetailList,restRequests);
		String response=result.get(1L);
		assertEquals("Y", response);
		
		List<NxLookupData> nxLookupDataList2 = new ArrayList<>();
		when(nxLookupDataRepository.findByItemIdsAndDatasetAndCriteriaAndDesc
				(anySet(),anyString(),anyString(),anyString())).thenReturn(nxLookupDataList2);
		Map<Long, String>  result2=processINRtoMP.findInrRestRequests(nxRequestDetailList,restRequests);
		String response2=result2.get(1L);
		assertEquals("N", response2);
	}

	@Disabled
	@Test
	public void createIglooData() {
		List<NxAccessPricingData> nxAccessPricingDataList = new ArrayList<>();
		NxAccessPricingData nxAccessPricingData = new NxAccessPricingData();
		nxAccessPricingData.setIntermediateJson("{\"reqStreetAddress\":\"1 Aerojet Way North\",\"reqCity\":\"Las Vegas\",\"reqState\":\"NV\",\"reqZipCode\":\"89030\",\"country\":\"United States\",\"address\":\"1 AEROJET WAY\",\"city\":\"NORTH LAS VEGAS\",\"state\":\"NV\",\"zipCode\":\"89030-3318\",\"reqAccessBandwidth\":\"100\",\"reqVendor\":\"COX COMMUNICATIONS\",\"attEthPop\":\"LSVGNV02\",\"service\":\"AVPN-ETH\",\"bandwidth\":\" \",\"accessArch\":\"Switched\",\"physicalInterface\":\"100BaseTX Electrical\",\"mrc\":\"1970.00\",\"discountMonthlyRecurringPrice\":\"1970.00\",\"nonRecurringCharge\":\"500.00\",\"ethToken\":\"ETH3043376\",\"iglooQuoteID\":\"4055274.001\",\"flowType\":\"IGL\",\"nxSiteId\":15}");
		nxAccessPricingData.setConsolidation_criteria("Switched$100$100BaseTX Electrical$Y");
		nxAccessPricingData.setLocationYn("New");
		nxAccessPricingDataList.add(nxAccessPricingData);
		NxAccessPricingData nxAccessPricingData2 = new NxAccessPricingData();
		nxAccessPricingData2.setIntermediateJson("{\"reqStreetAddress\":\"3 WORLD FINANCIAL CTR\",\"reqCity\":\"New York\",\"reqState\":\"NY\",\"reqZipCode\":\"10281\",\"country\":\"United States\",\"address\":\"3 WORLD FINANCIAL CTR\",\"city\":\"NEW YORK\",\"state\":\"NY\",\"zipCode\":\"10281-1013\",\"reqAccessBandwidth\":\"100\",\"reqVendor\":\"VERIZON\",\"reqIlecSwc\":\"NYCMNYWS\",\"attEthPop\":\"NYCMNY54\",\"service\":\"AVPN-ETH\",\"bandwidth\":\" \",\"accessArch\":\"Switched\",\"physicalInterface\":\"100BaseTX Electrical\",\"mrc\":\"2778.00\",\"nrc\":\"268.00\",\"discountMonthlyRecurringPrice\":\"2778.00\",\"nonRecurringCharge\":\"1230.00\",\"ethToken\":\"ETH3029320\",\"iglooQuoteID\":\"4043128.001\",\"flowType\":\"IGL\",\"nxSiteId\":13}");
		nxAccessPricingData2.setConsolidation_criteria("Switched$100$100BaseTX Electrical$Y");
		nxAccessPricingData2.setLocationYn("New");
		nxAccessPricingDataList.add(nxAccessPricingData2);
		when(nxMpDealRepository.getCountryCodeByCountryIsoCode(anyString())).thenReturn("US");
		List<NxLookupData> lookUp = new ArrayList<>();
		NxLookupData nxLookupData = new NxLookupData();
		nxLookupData.setItemId("2");
		lookUp.add(nxLookupData);
		when(nxLookupDataRepository.findByDatasetName(any())).thenReturn(lookUp);
		nxAccessPricingDataList.add(nxAccessPricingData);
		processINRtoMP.createIglooData(nxAccessPricingDataList);
		List<NxAccessPricingData> nxAccessPricingDataList2 = new ArrayList<>();
		NxAccessPricingData nxAccessPricingData3 = new NxAccessPricingData();
		nxAccessPricingData3.setIntermediateJson("{\"custAddr1\":\"Aarhus\",\"city\":\"Aarhus\",\"custPostalcode\":\"2100\",\"geoAddr\":\"Aarhus Nord, 8200, Aarhus N, Aarhus, Midtjylland\",\"country\":\"DENMARK\",\"alternateCurrency\":\"USD\",\"supplierName\":\"Tele Denmark\",\"clli\":\"ARHSDKAC\",\"monthlyCostLocal\":\"0.0\",\"oneTimeCostLocal\":\"0.0\",\"currency\":\"DKK\",\"accessBandwidth\":\"10000\",\"technology\":\"IP Access(IP Access/Internet Access)\",\"serialNumber\":\"1006651_DK_AVPN_-_BB_2022325_3646462\",\"nodeName\":\"Aarhus\",\"speed\":\"10000\",\"service\":\"AVPN - BB\",\"monthlyPriceUSD\":\"222.0\",\"oneTimePriceUSD\":\"111.0\",\"flowType\":\"IGL\",\"nxSiteId\":137}");
		nxAccessPricingData3.setConsolidation_criteria("IP Access(IP Access/Internet Access)$10000$AVPN - BB$N");
		nxAccessPricingData3.setLocationYn("Existing");
		nxAccessPricingDataList2.add(nxAccessPricingData3);
		when(nxMpDealRepository.getCountryCodeByCountryIsoCode(anyString())).thenReturn("DN");
		processINRtoMP.createIglooData(nxAccessPricingDataList2);
	}
	
	@Test
	public void testIsRESTEnabled() {
		Map<String, Object> requestParam = new HashMap<>();
		requestParam.put(MyPriceConstants.FLOW_TYPE,"INR");
		List<NxLookupData> restProductDetailsone = new ArrayList<>();
		NxLookupData nxLookupDataOne = new NxLookupData();
		nxLookupDataOne.setCriteria("Y");
		restProductDetailsone.add(nxLookupDataOne);
		when(nxLookupDataRepository.findByDatasetNameAndItemId(anyString(),anyString())).thenReturn(restProductDetailsone);
		boolean result1=processINRtoMP.isRESTEnabled(requestParam);
		assertTrue(result1);
		List<NxLookupData> restProductDetailsTwo = new ArrayList<>();
		NxLookupData nxLookupData = new NxLookupData();
		nxLookupData.setCriteria("N");
		restProductDetailsTwo.add(nxLookupData);
		when(nxLookupDataRepository.findByDatasetNameAndItemId(anyString(),anyString())).thenReturn(restProductDetailsTwo);
		boolean result=processINRtoMP.isRESTEnabled(requestParam);
		assertFalse(result);
	}

	@Test
	public void testGetProduct() {
		Map<String,List<String>> productInfoMap = new HashMap<>();
		productInfoMap.put("ADI", Arrays.asList("MIS/PNT"));
		when(configAndUpdatePricingUtilInr.getConfigProdutMapFromLookup(anyString())).thenReturn(productInfoMap);
		String result=processINRtoMP.getProduct("ADI");
		assertEquals("ADI", result);
	}
	
	@Test
	public void testIsDesignProcessed() {
		String nxKeyId="57657$US";
		Long nxSiteId=57L;
		Long groupId=768L;
		Map<Long, List<Map<String, List<Long>>>> grpIdNxKeyIdNxSiteIds=new HashMap<Long, List<Map<String,List<Long>>>>();
		List<Map<String, List<Long>>> nxKeyIdNXSiteIdList = new ArrayList<>();
		Map<String, List<Long>> nxKeyIdNXSiteIdMap= new HashMap<>();
		List<Long> siteIdList= new ArrayList<>();
		siteIdList.add(nxSiteId);
		nxKeyIdNXSiteIdMap.put(nxKeyId, siteIdList);
		nxKeyIdNXSiteIdList.add(nxKeyIdNXSiteIdMap);
		grpIdNxKeyIdNxSiteIds.put(groupId, nxKeyIdNXSiteIdList);
		boolean result=processINRtoMP.isDesignProcessed(grpIdNxKeyIdNxSiteIds,nxKeyId,nxSiteId, groupId);
		assertFalse(result);

		boolean result1=processINRtoMP.isDesignProcessed(grpIdNxKeyIdNxSiteIds,nxKeyId,67L, groupId);
		assertTrue(result1);
		
		boolean result2=processINRtoMP.isDesignProcessed(grpIdNxKeyIdNxSiteIds,"34$US",67L, groupId);
		assertTrue(result2);
		
		Map<Long, List<Map<String, List<Long>>>> grpIdNxKeyIdNxSiteIdsSecond=new HashMap<Long, List<Map<String,List<Long>>>>();
		List<Map<String, List<Long>>> nxKeyIdNXSiteIdListSecond = new ArrayList<>();
		Map<String, List<Long>> nxKeyIdNXSiteIdMapSecond= new HashMap<>();
		nxKeyIdNXSiteIdMapSecond.put("57$US", Arrays.asList(57L));
		nxKeyIdNXSiteIdListSecond.add(nxKeyIdNXSiteIdMap);
		grpIdNxKeyIdNxSiteIdsSecond.put(groupId, nxKeyIdNXSiteIdList);
		boolean result3=processINRtoMP.isDesignProcessed(grpIdNxKeyIdNxSiteIds,"454$US",56L, 78L);
		assertTrue(result3);
	}
	
	@Test
	public void testGetErrorDetails() {
		Map<String, Object> requestMap= new HashMap<>();
		requestMap.put(MyPriceConstants.RESPONSE_STATUS, false);
		requestMap.put(CustomJsonConstants.ERROR_WS_NAME, "Add_to_txn");
		requestMap.put(CustomJsonConstants.REST_RESPONSE_ERROR, "Communication Error");
		requestMap.put(CustomJsonConstants.REST_RESPONSE_ERROR, "site not configured");
		requestMap.put(CustomJsonConstants.CONFIG_BOM_ERROR_DATA, "Bom error");
		requestMap.put(CustomJsonConstants.SITE_CONFIG_ERROR_MAP, "site config error");
		processINRtoMP.getErrorDetails(requestMap);
	}
	
	@Test
	public void testPrepareFailedCkt() {
		List<NxValidationRules> restErrRules= new ArrayList<>();
		NxValidationRules nxValidationRules = new NxValidationRules();
		nxValidationRules.setValue("circuitId:nxSiteId");
		nxValidationRules.setName("Ethernet");
		nxValidationRules.setValidationGroup("REST_ERROR");
		nxValidationRules.setDataPath("$..['CktId']:$..[?(@.CktId=='<REPLACE>')]..nxSiteId");
		nxValidationRules.setOffer("DOMESTIC DEDICATED ACCESS");
		nxValidationRules.setActive("Y");
		nxValidationRules.setFlowType("INR");
		restErrRules.add(nxValidationRules);
		String product="Ethernet";
		String requestProductcd="DOMESTIC DEDICATED ACCESS";
		String design="";
		List<Object> datas= new ArrayList<>();
		datas.add("57657");
		List<NxLookupData> usageProdList= new ArrayList<>();
		NxLookupData nxLookupData = new NxLookupData();
		nxLookupData.setDescription("VTNS,BVoIP");
		usageProdList.add(nxLookupData);
		when(nexxusJsonUtility.getValueLst(any(),anyString())).thenReturn(datas);
		Map<String, List<RestErrors>> designErr= new HashMap<>();
		List<RestErrors> restErrorList= new ArrayList<>();
		RestErrors restErrors = new RestErrors();
		restErrors.setCircuitId("57657");
		restErrors.setMessages("circuit not configured");
		restErrorList.add(restErrors);
		Map<String, Object> response= new HashMap<>();
		Set<String> error=new HashSet<String>();
		error.add("error");
		response.put(CustomJsonConstants.REST_RESPONSE_ERROR, error);
		response.put(CustomJsonConstants.CONFIG_BOM_ERROR_DATA, error);
		response.put(MyPriceConstants.PRODUCT_TYPE, "INR");
		Map<String,Set<String>> siteConfigErrorMap=new HashMap<String, Set<String>>();
		siteConfigErrorMap.put("567", error);
		response.put(CustomJsonConstants.SITE_CONFIG_ERROR_MAP, siteConfigErrorMap);
		processINRtoMP.prepareFailedCkt(response, product, design, restErrRules, designErr,usageProdList,requestProductcd);

		List<NxValidationRules> restErrRules1= new ArrayList<>();
		NxValidationRules nxValidationRules1 = new NxValidationRules();
		nxValidationRules1.setValue("circuitId:nxSiteId");
		nxValidationRules1.setName("AVPN");
		nxValidationRules1.setValidationGroup("REST_ERROR");
		nxValidationRules1.setDataPath("$..design..['circuitId', 'nxSiteId']");
		nxValidationRules1.setOffer("AVPN");
		nxValidationRules1.setActive("Y");
		nxValidationRules1.setFlowType("INR");
		restErrRules1.add(nxValidationRules);
		String product1="AVPN";
		List<Object> dataList= new ArrayList<>();
		LinkedHashMap<String, Object> dataMap= new LinkedHashMap<>();
		dataMap.put("circuitId", "57657");
		dataList.add(dataMap);
		when(nexxusJsonUtility.getValueLst(any(),anyString())).thenReturn(dataList);
		designErr.put("AVPN", restErrorList);
		processINRtoMP.prepareFailedCkt(response, product1, design, restErrRules1, designErr,usageProdList,product1);

	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void testProcessIGLOORestFlow() {
		NxMpDeal nxMpDeal=new NxMpDeal();
		when(nxMpDealRepository.findByNxTxnId(any())).thenReturn(nxMpDeal);
		Map<String, Object> paramMap=mock(Map.class);
		when(paramMap.containsKey(MyPriceConstants.CONFIG_SOLN_DESIGN_RESPONSE)).thenReturn(true);
		when(nxAccessPricingDataRepository.findByNxSolIdAndIncludeIndAndMpStatus(anyLong())).thenReturn(nxAccessPricingDatas);
		List<NxLookupData> restProductDetailsone = new ArrayList<>();
		NxLookupData nxLookupDataOne = new NxLookupData();
		nxLookupDataOne.setCriteria("Y");
		restProductDetailsone.add(nxLookupDataOne);
		when(nxLookupDataRepository.findByDatasetNameAndItemId(anyString(),anyString())).thenReturn(restProductDetailsone);
		List<NxLookupData> lookUp = new ArrayList<>();
		NxLookupData nxLookupData = new NxLookupData();
		nxLookupData.setItemId("2");
		lookUp.add(nxLookupData);
		when(nxLookupDataRepository.findByDatasetName(any())).thenReturn(lookUp);
		doNothing().when(configAndUpdateRestProcessingService).callMpConfigAndUpdate(anyMap(),anyString());
		when(nxAccessPricingDataRepository.getNxAccessPricingData(anyLong())).thenReturn(new NxAccessPricingData());
		processINRtoMP.process(nxSolutionDetail, null, requestMetaDataMap, configUpdateResMap, source, createTransactionResponse, prodName, isReconfigure);
	}
	
	
	@Test
	public void testSaveNxInrDesign() throws IOException {
		NxSolutionDetail nxSolutionDetail = new NxSolutionDetail();
		nxSolutionDetail.setNxSolutionId(67L);
		ObjectMapper realmapper = new ObjectMapper();
		JsonNode design = realmapper.readTree("{\"siteName\": \"2667781\", \"accessCarrier\": \"AT&T\", \"circuitId\": \"BBEC586813ATI\"}");
		Map<String, Object> paramMap=new HashMap<>(); 
		paramMap.put(MyPriceConstants.NX_REQ_ID, 56L);
		paramMap.put(MyPriceConstants.PRODUCT_NAME,"AVPN");
		paramMap.put(MyPriceConstants.NX_KEY_ID,"4646$US");
		paramMap.put(MyPriceConstants.MP_REST_DESIGN_LIMIT,2);
		Map<String, NxInrDesign> designDataMap= new HashMap<>();
		NxInrDesign nxInrDesign = new NxInrDesign();
		List<NxInrDesignDetails> nxInrDesignDetailList=new ArrayList<>();
		NxInrDesignDetails nxInrDesignDetails = new NxInrDesignDetails();
		nxInrDesignDetails.setProduct("AVPN");
		nxInrDesignDetailList.add(nxInrDesignDetails);
		nxInrDesign.setNxInrDesignDetails(nxInrDesignDetailList);
		nxInrDesign.setStatus("RS");
		designDataMap.put("4646$US", nxInrDesign);
		List<JsonNode> designsList = new ArrayList<>();
		designsList.add(design);
		when(mapper.readValue(anyString(),any(TypeReference.class))).thenReturn(designsList);
		processINRtoMP.saveNxInrDesign(nxSolutionDetail,design,paramMap,designDataMap);

		paramMap.put(MyPriceConstants.SAVE_MP_OUTPUT_JSON_OBJECT_NODE,StringConstants.CONSTANT_Y);
		processINRtoMP.saveNxInrDesign(nxSolutionDetail,design,paramMap,designDataMap);

		paramMap.put("IS_RECONFIGURE", "inrReconfigure");
		paramMap.put("NX_MP_STATUS_IND", StringConstants.CONSTANT_Y);
		processINRtoMP.saveNxInrDesign(nxSolutionDetail,design,paramMap,designDataMap);
	}

	@Test
	public void testProcessINRRestFlowFail() {
		List<NxRequestDetails> nxRequestDetailList = new ArrayList<>();
		NxRequestDetails nxRequestDetails = new NxRequestDetails();
		nxRequestDetails.setNxRequestGroupId(1L);
		nxRequestDetails.setNxReqId(1L);
		nxRequestDetails.setProduct("AVPN");
		List<NxOutputFileModel> nxOutputFiles =new ArrayList<>();
		NxOutputFileModel nxOutputFileModel = new NxOutputFileModel();
		nxOutputFileModel.setMpOutputJson("{ \"beginBillMonth\": \"August 2019\", \"service\": \"AVPN\", \"searchCriteriaValue\": \"518912795\", \"customerName\": \"ALLWEILER AS\", \"searchCriteria\": \"DunsNumber\", \"accountDetails\": [ { \"currency\": \"NOK\", \"custName\": \"Allweiler AS\", \"site\": [ { \"country\": \"NO\", \"city\": \"HVALSTAD\", \"address\": \"NYE VAKAAS VEI 4\", \"siteId\": \"90922336\", \"custPostalcode\": \"1395\", \"design\": [ { \"siteName\": \"3036816\", \"accessCarrier\": \"SAREAQ\", \"portType\": \"ETH\", \"technology\": \"Ethernet (Gateway Interconnect/ESP ETH Shared)\", \"portSpeed\": \"2000\", \"accessBandwidth\": \"2000 Kbps\", \"priceDetails\": [ { \"priceType\": \"PORTBEID\", \"beid\": \"19617\", \"quantity\": \"1\", \"localListPrice\": \"2630\", \"actualPrice\": \"815.3\", \"secondaryKey\": \"#Intl#MPLS Port#Flat Rate#2M#Enet#Enet#VPN Transport Connection#per port#19617#18030\", \"elementType\": \"Port\", \"uniqueId\": \"#MPLS Port#MPLS Port - 2M#ENET#2Mbps#ENET#VPN Transport#Connection#Each\" }  ], \"accessSecondaryKey\": \"#Intl#Norway#NO#AVPN#N/A#N/A\" } ], \"FALLOUTMATCHINGID\": \"0000000001/AVPNPricingInventory/Body/AccountDetails/AVPNService/AVPNInventoryDetails/SiteDetails/FALLOUTMATCHINGID\", \"secondaryKey\": \"#Intl#AVPN#Norway#NO#EMEA\" } ] } ], \"flowType\": \"INR\" }");
		nxOutputFiles.add(nxOutputFileModel);
		nxRequestDetails.setNxOutputFiles(nxOutputFiles);
		nxRequestDetailList.add(nxRequestDetails);
		when(nxRequestDetailsRepository.findByNxSolutionId(Mockito.anyLong())).thenReturn(nxRequestDetailList);
		List<NxLookupData> nxLookupLst= new ArrayList<>();
		NxLookupData nxLookupData = new NxLookupData();
		nxLookupData.setItemId("2");
		nxLookupData.setCriteria("A");
		nxLookupLst.add(nxLookupData);
		when(nxLookupDataRepository.findByDatasetName(any())).thenReturn(nxLookupLst);
		when(nxRequestDetailsRepository.findbyNxSolutionDetailAndNxRequestGroupIdAndActiveYn(Mockito.any(),
				Mockito.anyLong(), Mockito.anyString())).thenReturn(nxRequestDetailList);
		when(nxLookupDataRepository.findByItemIdsAndDatasetAndCriteriaAndDesc
				(anySet(),anyString(),anyString(),anyString())).thenReturn(nxLookupLst);
		List<Object[]> nxInrDesigns = new ArrayList<>();
		Object obj[] = new Object[2] ;
		obj[0]="567";
		obj[1]="IVEC990793ATI";
		nxInrDesigns.add(obj);
		when(nxInrDesignRepository.findDesignByNxSolutionId(anyLong(),anyList(),anyString(),anyList())).thenReturn(nxInrDesigns);
		NxInrDesign nxInrDesign = new NxInrDesign();
		List<NxInrDesignDetails> nxInrDesignDetailList= new ArrayList<>();
		NxInrDesignDetails nxInrDesignDetails = new NxInrDesignDetails();
		nxInrDesignDetails.setStatus(MyPriceConstants.REST_API_NOT_INVOKED);
		nxInrDesignDetailList.add(nxInrDesignDetails);
		nxInrDesignDetails.setProduct("AVPN");
		nxInrDesignDetails.setSubProduct("LocalAccess");
		nxInrDesign.setNxInrDesignDetails(nxInrDesignDetailList);
		when(nxInrDesignRepository.findByNxSolutionIdAndNxInrDesignIdAndActiveYN(anyLong(),anyLong(),anyString())).thenReturn(nxInrDesign);
		//fail scenario
		doAnswer(new Answer() {
			   public Object answer(InvocationOnMock invocation) {
				Map<String,Object> requestMap=invocation.getArgument(0, Map.class);
				   requestMap.put(MyPriceConstants.RESPONSE_STATUS, false);
				   requestMap.put(CustomJsonConstants.CONFIG_BOM_ERROR,true);
				   requestMap.put(CustomJsonConstants.SITE_CONFIG_ERROR, true);
				return null;
			   }})
			 .when(configAndUpdateRestProcessingService).callMpConfigAndUpdate(anyMap(),anyString());
		processINRtoMP.process(nxSolutionDetail, nxRequestGrpId, requestMetaDataMap, configUpdateResMap, source, createTransactionResponse, prodName, isReconfigure);
	}
	

	@Test
	public void testProcessINRRestFlowSuccess() {
		List<NxRequestDetails> nxRequestDetailList = new ArrayList<>();
		NxRequestDetails nxRequestDetails = new NxRequestDetails();
		nxRequestDetails.setNxRequestGroupId(1L);
		nxRequestDetails.setNxReqId(1L);
		nxRequestDetails.setProduct("AVPN");
		List<NxOutputFileModel> nxOutputFiles =new ArrayList<>();
		NxOutputFileModel nxOutputFileModel = new NxOutputFileModel();
		nxOutputFileModel.setMpOutputJson("{ \"beginBillMonth\": \"August 2019\", \"service\": \"AVPN\", \"searchCriteriaValue\": \"518912795\", \"customerName\": \"ALLWEILER AS\", \"searchCriteria\": \"DunsNumber\", \"accountDetails\": [ { \"currency\": \"NOK\", \"custName\": \"Allweiler AS\", \"site\": [ { \"country\": \"NO\", \"city\": \"HVALSTAD\", \"address\": \"NYE VAKAAS VEI 4\", \"siteId\": \"90922336\", \"custPostalcode\": \"1395\", \"design\": [ { \"siteName\": \"3036816\", \"accessCarrier\": \"SAREAQ\", \"portType\": \"ETH\", \"technology\": \"Ethernet (Gateway Interconnect/ESP ETH Shared)\", \"portSpeed\": \"2000\", \"accessBandwidth\": \"2000 Kbps\", \"priceDetails\": [ { \"priceType\": \"PORTBEID\", \"beid\": \"19617\", \"quantity\": \"1\", \"localListPrice\": \"2630\", \"actualPrice\": \"815.3\", \"secondaryKey\": \"#Intl#MPLS Port#Flat Rate#2M#Enet#Enet#VPN Transport Connection#per port#19617#18030\", \"elementType\": \"Port\", \"uniqueId\": \"#MPLS Port#MPLS Port - 2M#ENET#2Mbps#ENET#VPN Transport#Connection#Each\" }  ], \"accessSecondaryKey\": \"#Intl#Norway#NO#AVPN#N/A#N/A\" } ], \"FALLOUTMATCHINGID\": \"0000000001/AVPNPricingInventory/Body/AccountDetails/AVPNService/AVPNInventoryDetails/SiteDetails/FALLOUTMATCHINGID\", \"secondaryKey\": \"#Intl#AVPN#Norway#NO#EMEA\" } ] } ], \"flowType\": \"INR\" }");
		nxOutputFiles.add(nxOutputFileModel);
		nxRequestDetails.setNxOutputFiles(nxOutputFiles);
		nxRequestDetailList.add(nxRequestDetails);
		when(nxRequestDetailsRepository.findByNxSolutionId(Mockito.anyLong())).thenReturn(nxRequestDetailList);
		List<NxLookupData> nxLookupLst= new ArrayList<>();
		NxLookupData nxLookupData = new NxLookupData();
		nxLookupData.setItemId("2");
		nxLookupData.setCriteria("A");
		nxLookupLst.add(nxLookupData);
		when(nxLookupDataRepository.findByDatasetName(any())).thenReturn(nxLookupLst);
		when(nxRequestDetailsRepository.findbyNxSolutionDetailAndNxRequestGroupIdAndActiveYn(Mockito.any(),
				Mockito.anyLong(), Mockito.anyString())).thenReturn(nxRequestDetailList);
		when(nxLookupDataRepository.findByItemIdsAndDatasetAndCriteriaAndDesc
				(anySet(),anyString(),anyString(),anyString())).thenReturn(nxLookupLst);
		List<Object[]> nxInrDesigns = new ArrayList<>();
		Object obj[] = new Object[2] ;
		obj[0]="567";
		obj[1]="IVEC990793ATI";
		nxInrDesigns.add(obj);
		when(nxInrDesignRepository.findDesignByNxSolutionId(anyLong(),anyList(),anyString(),anyList())).thenReturn(nxInrDesigns);
		NxInrDesign nxInrDesign = new NxInrDesign();
		List<NxInrDesignDetails> nxInrDesignDetailList= new ArrayList<>();
		NxInrDesignDetails nxInrDesignDetails = new NxInrDesignDetails();
		nxInrDesignDetails.setStatus(MyPriceConstants.REST_API_NOT_INVOKED);
		nxInrDesignDetailList.add(nxInrDesignDetails);
		nxInrDesignDetails.setProduct("AVPN");
		nxInrDesignDetails.setSubProduct("LocalAccess");
		nxInrDesign.setNxInrDesignDetails(nxInrDesignDetailList);
		when(nxInrDesignRepository.findByNxSolutionIdAndNxInrDesignIdAndActiveYN(anyLong(),anyLong(),anyString())).thenReturn(nxInrDesign);
		doAnswer(new Answer() {
			   public Object answer(InvocationOnMock invocation) {
				Map<String,Object> requestMap=invocation.getArgument(0, Map.class);
				   requestMap.put(MyPriceConstants.RESPONSE_STATUS, true);
				return null;
			   }})
			 .when(configAndUpdateRestProcessingService).callMpConfigAndUpdate(anyMap(),anyString());
		processINRtoMP.process(nxSolutionDetail, nxRequestGrpId, requestMetaDataMap, configUpdateResMap, source, createTransactionResponse, prodName, isReconfigure);
	}
	
}
