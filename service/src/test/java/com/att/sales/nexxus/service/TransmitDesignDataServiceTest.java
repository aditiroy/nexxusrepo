package com.att.sales.nexxus.service;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyList;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import com.att.sales.framework.exception.SalesBusinessException;
import com.att.sales.framework.model.ServiceMetaData;
import com.att.sales.nexxus.constant.CommonConstants;
import com.att.sales.nexxus.constant.TDDConstants;
import com.att.sales.nexxus.dao.model.NxDesign;
import com.att.sales.nexxus.dao.model.NxDesignAudit;
import com.att.sales.nexxus.dao.model.NxDesignDetails;
import com.att.sales.nexxus.dao.model.NxLookupData;
import com.att.sales.nexxus.dao.model.NxMpDeal;
import com.att.sales.nexxus.dao.model.NxUdfMapping;
import com.att.sales.nexxus.dao.model.solution.NxSolutionDetail;
import com.att.sales.nexxus.dao.repository.NxDesignAuditRepository;
import com.att.sales.nexxus.dao.repository.NxDesignRepository;
import com.att.sales.nexxus.dao.repository.NxMpDealRepository;
import com.att.sales.nexxus.dao.repository.NxSolutionDetailsRepository;
import com.att.sales.nexxus.dmaap.mr.util.DmaapPublishEventsServiceImpl;
import com.att.sales.nexxus.ped.dmaap.model.NxPEDStatusDMaap;
import com.att.sales.nexxus.reteriveicb.model.Component;
import com.att.sales.nexxus.reteriveicb.model.UDFBaseData;
import com.att.sales.nexxus.transmitdesigndata.model.CircuitDetails;
import com.att.sales.nexxus.transmitdesigndata.model.EndpointDetails;
import com.att.sales.nexxus.transmitdesigndata.model.PortDetails;
import com.att.sales.nexxus.transmitdesigndata.model.SolutionStatus;
import com.att.sales.nexxus.transmitdesigndata.model.TransmitDesignDataRequest;
import com.att.sales.nexxus.util.DME2RestClient;
import com.att.sales.nexxus.util.JacksonUtil;
import com.att.sales.nexxus.util.JsonPathUtil;
import com.att.sales.nexxus.util.NexxusJsonUtility;
import com.fasterxml.jackson.databind.ObjectMapper;

@ExtendWith(MockitoExtension.class)
public class TransmitDesignDataServiceTest {

	@Spy
	@InjectMocks
	private TransmitDesignDataService transmitDesignDataService;
	
	@Mock
	private JsonPathUtil jsonPathUtil;
	
	@Mock
	private DmaapPublishEventsServiceImpl dmaapPublishEventsServiceImpl;
	
	@Mock
	private MailServiceImpl mailService;
	
	@Mock
	private DME2RestClient dME2RestClient;
	
	@Mock
	private TransmitDesignDataRepoService tddRepositoryService;
	
	@Mock
	private ObjectMapper mapper;
	
	@Mock
	private NxDesignAuditRepository nxDesignAuditRepository;
	
	@Mock
	private NxMpDealRepository nxMpDealRepository;
	
	@Mock
	private NexxusJsonUtility nexxusJsonUtility;
	
	@Mock
	private NxSolutionDetailsRepository nxSolutionDetailsRepository;
	@Mock
	private NxDesignRepository nxDesignRepository;
	Boolean isDesignChange=false;
	
	@BeforeAll
	public static void init() {
		Map<String, Object> map = new HashMap<>();
		map.put(ServiceMetaData.REST_REQUEST_START_TIME, System.currentTimeMillis());
		ServiceMetaData.add(map);
	}
	
	@AfterAll
	public static void afterClass() {
		ServiceMetaData.getThreadLocal().remove();
	}
	@Test
	@Disabled
	public void executeTestEror() {
		TransmitDesignDataRequest request=new TransmitDesignDataRequest();
		this.prepareRequestForASE(request);
		List<Object[]> nxSol = new ArrayList<>();
		when(nxSolutionDetailsRepository.findNxSolutionByExternalKey(anyLong())).thenReturn(nxSol);
		//when(tddRepositoryService.findByExternalKey(any())).thenReturn(getSolutionDtlsLstForASE());
		transmitDesignDataService.execute(request);
	}
	@Test
	@Disabled
	public void executeTestASE() {
		TransmitDesignDataRequest request=new TransmitDesignDataRequest();
		this.prepareRequestForASE(request);
		transmitDesignDataService.isDesignChange=true;
		//when(tddRepositoryService.findByExternalKey(any())).thenReturn(getSolutionDtlsLstForASE());
		List<Object[]> nxSol = new ArrayList<>();
		Object[] sol = new Object[3];
		sol[0] = 22l;
		sol[1] = "N";
		sol[2] = "user";
		nxSol.add(sol);
		when(nxSolutionDetailsRepository.findNxSolutionByExternalKey(anyLong())).thenReturn(nxSol);
		List<Object> asr = new ArrayList<>();
		asr.add("ASR1");
		when(nexxusJsonUtility.getValueLst(any(), anyString())).thenReturn(asr);
		List<NxSolutionDetail> solDetail = getSolutionDtlsLstForASE();
		when(nxDesignRepository.findByNxSolutionIdAndAsrItemId(anyLong(), anyList())).thenReturn(solDetail.get(0).getNxDesign());
		Map<Long,Map<String,NxUdfMapping>> udfMappingData=new HashMap<>();
		Map<String,NxUdfMapping> intMap=new HashMap<>(); 
		NxUdfMapping b=new NxUdfMapping();
		b.setComponentId(12l);
		b.setComponentType("port");
		b.setNxUdfMappingId(1l);
		b.setOfferId(2l);
		b.setOfferName("ASE");
		b.setRuleSet("TT");
		b.setUdfId(1l);
		b.setUdfAttributeDatasetName("Text");
		intMap.put("estimatedInterval", b);
		udfMappingData.put(30l, intMap);
		when(tddRepositoryService.getUdfDataMap(any(),any())).thenReturn(udfMappingData);
		List<NxMpDeal> dealData=new ArrayList<>();
		NxMpDeal ndeal=new NxMpDeal();
		ndeal.setActiveYN("Y");
		ndeal.setDealID("1");
		ndeal.setVersion("DD");
		dealData.add(ndeal);
		when(nxMpDealRepository.findBySolutionId(any())).thenReturn(dealData);
		
		NxDesignAudit nxDesignAudit=new NxDesignAudit();
		nxDesignAudit.setData("{\"solution\":{\"solutionDeterminants\":{\"icbInd\":null,\"solutionType\":\"NS\",\"mvlInd\":null},"
				+ "\"bulkInd\":null,\"offers\":[{\"bundleCode\":\"ASE\",\"site\":[],\"offerId\":\"103\"}],\"leadDesignID\":null,"
				+ "\"marketStrata\":\"BNS\",\"cancellationReason\":null,\"pricerDSolutionId\":null,\"automationInd\":\"N\","
				+ "\"erateInd\":null,\"layer\":\"Wholesale\",\"solutionStatus\":\"N\"},\"actionDeterminants"
				+ "\":[{\"component\":[\"Design\",\"Price\",\"ASE\"],\"activity\":\"UpdateDesign\"}]}");
		when(nxDesignAuditRepository.findByNxRefIdAndTransaction(any(),any())).thenReturn(nxDesignAudit);
		transmitDesignDataService.execute(request);
	}
	
	@Test
	@Disabled
	public void executeTestADE() {
		TransmitDesignDataRequest request=new TransmitDesignDataRequest();
		this.prepareRequestForADE(request);
		Map<Long,Map<String,NxUdfMapping>> udfMappingData=new HashMap<>();
		Map<String,NxUdfMapping> intMap=new HashMap<>(); 
		NxUdfMapping b=new NxUdfMapping();
		b.setComponentId(12l);
		b.setComponentType("port");
		b.setNxUdfMappingId(1l);
		b.setOfferId(2l);
		b.setOfferName("ASE");
		b.setRuleSet("TT");
		b.setUdfId(1l);
		b.setUdfAttributeDatasetName("Text");
		intMap.put("estimatedInterval", b);
		udfMappingData.put(1210l, intMap);
		when(tddRepositoryService.getUdfDataMap(any(),any())).thenReturn(udfMappingData);
		NxDesignAudit nxDesignAudit=new NxDesignAudit();
		nxDesignAudit.setData("{\"solution\":{\"solutionDeterminants\":{\"icbInd\":null,\"solutionType\":\"NS\",\"mvlInd\":null},"
				+ "\"bulkInd\":null,\"offers\":[{\"bundleCode\":\"ASE\",\"site\":[],\"offerId\":\"103\"}],\"leadDesignID\":null,"
				+ "\"marketStrata\":\"BNS\",\"cancellationReason\":null,\"pricerDSolutionId\":null,\"automationInd\":\"N\","
				+ "\"erateInd\":null,\"layer\":\"Wholesale\",\"solutionStatus\":\"N\"},\"actionDeterminants"
				+ "\":[{\"component\":[\"Design\",\"Price\",\"ASE\"],\"activity\":\"UpdateDesign\"}]}");
		when(nxDesignAuditRepository.findByNxRefIdAndTransaction(any(),any())).thenReturn(nxDesignAudit);
		//when(tddRepositoryService.findByExternalKey(any())).thenReturn(getSolutionDtlsLstForADE());
		List<Object[]> nxSol = new ArrayList<>();
		Object[] sol = new Object[3];
		sol[0] = 22l;
		sol[1] = "N";
		sol[2] = "user";
		nxSol.add(sol);
		when(nxSolutionDetailsRepository.findNxSolutionByExternalKey(anyLong())).thenReturn(nxSol);
		List<Object> asr = new ArrayList<>();
		asr.add("AD78789");
		when(nexxusJsonUtility.getValueLst(any(), anyString())).thenReturn(asr);
		List<NxSolutionDetail> solDetail = getSolutionDtlsLstForADE();
		when(nxDesignRepository.findByNxSolutionIdAndAsrItemId(anyLong(), anyList())).thenReturn(solDetail.get(0).getNxDesign());
		List<NxMpDeal> dealData=new ArrayList<>();
		NxMpDeal ndeal=new NxMpDeal();
		ndeal.setActiveYN("Y");
		ndeal.setDealID("1");
		ndeal.setVersion("DD");
		dealData.add(ndeal);
		when(nxMpDealRepository.findBySolutionId(any())).thenReturn(dealData);
		transmitDesignDataService.execute(request);
	}
	public void prepareRequestForASE(TransmitDesignDataRequest request) {
		List<SolutionStatus> solutionStatus=new ArrayList<>();
		SolutionStatus sl=new SolutionStatus();
		sl.setNxSolutionId("123");
		sl.setOpportunityId("ABC");
		sl.setResponseType("Solution");
		sl.setSolutionCancellationReason("Test");
		sl.setSolutionId(1);
		sl.setStatusCode("D");
		sl.setStatusDescription("GFH");
		List<PortDetails> portDetails=new ArrayList<>();
		PortDetails pd=new PortDetails();
		pd.setAsrItemId("54634");
		pd.setEstimatedInterval(1);
		pd.setNotes("UUUU");
		pd.setNssManagerATTUID("S");
		pd.setNssManagerFirstName("GH");
		pd.setNssManagerLastName("YU");
		pd.setPreliminaryServingPlanURL("jk");
		pd.setStatusCode("MR");
		pd.setStatusDescription("kkjj");
		portDetails.add(pd);
		sl.setPortDetails(portDetails);
		solutionStatus.add(sl);
		request.setSolutionStatus(solutionStatus);
	} 
	private List<NxSolutionDetail>  getSolutionDtlsLstForASE(){
		List<NxSolutionDetail> nxSolutionDetail=new ArrayList<>();
		NxSolutionDetail nxs=new NxSolutionDetail();
		nxs.setCreatedDate(new Date());
		nxs.setCreatedUser("ABC");
		nxs.setExternalKey(1l);
		nxs.setNxSolutionId(2l);
		List<NxDesign> nxDesign=new ArrayList<>();
		NxDesign nd=new NxDesign();
		nd.setNxDesignId(1l);
		nd.setAsrItemId("54634");
		nd.setNxSolutionDetail(nxs);
		List<NxDesignDetails> nxDesignDetails = new ArrayList<>();
		NxDesignDetails nxd=new NxDesignDetails();
		nxd.setCreatedDate(new Date());
		nxd.setNxDesignId(3l);
		nxd.setNxDesign(nd);
		nxd.setDesignData(getSite().toJSONString());
		nxDesignDetails.add(nxd);
		nd.setNxDesignDetails(nxDesignDetails);
		nxDesign.add(nd);
		nxs.setNxDesign(nxDesign);
		nxSolutionDetail.add(nxs);
		return nxSolutionDetail;
	}
	
	

	public void prepareRequestForADE(TransmitDesignDataRequest request) {
		List<SolutionStatus> solutionStatus=new ArrayList<>();
		SolutionStatus sl=new SolutionStatus();
		sl.setNxSolutionId("123");
		sl.setOpportunityId("ABC");
		sl.setResponseType("Circuit");
		sl.setSolutionCancellationReason("Test");
		sl.setSolutionId(1);
		sl.setStatusCode("CL");
		sl.setStatusDescription("GFH");
		List<CircuitDetails> circuitDetails =new ArrayList<>();
		CircuitDetails cd=new CircuitDetails();
		cd.setAsrItemId("AD78789");
		cd.setCircuitCancellationReason("FF");
		cd.setConfirmedInterval(1);
		cd.setEstimatedInterval(1);
		cd.setNotes("LL");
		cd.setNssManagerATTUID("S");
		cd.setNssManagerFirstName("GH");
		cd.setNssManagerLastName("YU");
		cd.setPreliminaryServingPlanURL("jk");
		List<EndpointDetails> endpointDetails=new ArrayList<>();
		EndpointDetails e=new EndpointDetails();
		e.setAlternateSWCCLLI("ss");
		e.setCommonLanguageFacilityId("hh");
		e.setEdgelessDesignIndicator("l");
		e.setEndpointType("A");
		endpointDetails.add(e);
		EndpointDetails e1=new EndpointDetails();
		e1.setAlternateSWCCLLI("ss");
		e1.setCommonLanguageFacilityId("hh");
		e1.setEdgelessDesignIndicator("l");
		e1.setEndpointType("Z");
		endpointDetails.add(e1);
		cd.setEndpointDetails(endpointDetails);
		circuitDetails.add(cd);
		sl.setCircuitDetails(circuitDetails);
		solutionStatus.add(sl);
		request.setSolutionStatus(solutionStatus);
	} 
	
	private List<NxSolutionDetail>  getSolutionDtlsLstForADE(){
		List<NxSolutionDetail> nxSolutionDetail=new ArrayList<>();
		NxSolutionDetail nxs=new NxSolutionDetail();
		nxs.setCreatedDate(new Date());
		nxs.setCreatedUser("ABC");
		nxs.setExternalKey(1l);
		nxs.setNxSolutionId(2l);
		List<NxDesign> nxDesign=new ArrayList<>();
		NxDesign nd=new NxDesign();
		nd.setNxDesignId(1l);
		nd.setAsrItemId("AD78789");
		nd.setNxSolutionDetail(nxs);
		List<NxDesignDetails> nxDesignDetails = new ArrayList<>();
		NxDesignDetails nxd=new NxDesignDetails();
		nxd.setCreatedDate(new Date());
		nxd.setNxDesignId(3l);
		nxd.setNxDesign(nd);
		nxd.setDesignData(getCircuit().toJSONString());
		nxDesignDetails.add(nxd);
		nd.setNxDesignDetails(nxDesignDetails);
		nxDesign.add(nd);
		nxs.setNxDesign(nxDesign);
		nxSolutionDetail.add(nxs);
		return nxSolutionDetail;
	}
	
	@Test
	public void createRequestForOrchTest() {
		SolutionStatus sl=new SolutionStatus();
		sl.setNxSolutionId("123");
		sl.setOpportunityId("ABC");
		sl.setResponseType("Solution");
		sl.setSolutionCancellationReason("Test");
		sl.setSolutionId(1);
		sl.setStatusCode("D");
		sl.setStatusDescription("GFH");
		NxSolutionDetail nxs=new NxSolutionDetail();
		nxs.setCreatedDate(new Date());
		nxs.setCreatedUser("ABC");
		nxs.setExternalKey(1l);
		nxs.setNxSolutionId(2l);
		transmitDesignDataService.createRequestForOrch(sl, nxs);
	}
	
	
	private JSONObject getSite() {
		
		JSONObject site=JacksonUtil.toJsonObject("{\"siteId\":9032336,\"npanxx\":1254,\"address1\":\"fdsa\",\"city\":\"qwr\",\"state\":\"ewrqw\","
				+ "\"saLecName\":null,\"speedId\":null,\"saLecSwClli\":null,\"popClli\":null,\"country\":\"wqrewq\",\"macdType\":"
				+ "\"take\",\"macdActivity\":null,\"fromInventory\":null,\"dualSiteId\":null,\"onNetCheck\":null,\"ethernetVendor\":null,"
				+ "\"designSiteOfferPort\":[{\"designStatus\":\"D\",\"typeOfInventory\":null,\"milesResult\":null,\"securityDesignDetails\":"
				+ "null,\"macdActivityType\":null,\"component\":[{\"componentCodeId\":10,\"fromInvYN\":null,\"logicalChannelPvcID\":null,"
				+ "\"eteVpnKey\":null,\"diversityGroupId\":null,\"componentCodeType\":\"connection\",\"componentId\":9195162,\"externalField\""
				+ ":null,\"parentComponentId\":null,\"externalKeyRef\":null,\"mvlInd\":null,\"userEnteredVpn\":null,\"siteId\":null,"
				+ "\"logicalChannelId\":null,\"designDetails\":null,\"routeTargets\":null,\"references\":null},{\"componentCodeId\":50,"
				+ "\"fromInvYN\":null,\"logicalChannelPvcID\":null,\"eteVpnKey\":null,\"diversityGroupId\":null,\"componentCodeType\":"
				+ "\"Cos\",\"componentId\":null,\"externalField\":null,\"parentComponentId\":null,\"externalKeyRef\":null,\"mvlInd\":null,"
				+ "\"userEnteredVpn\":null,\"siteId\":null,\"logicalChannelId\":null,\"designDetails\":[{\"udfAttributeId\":[1024724],"
				+ "\"udfId\":20014,\"readOnly\":null,\"udfAttributeText\":null,\"defaultUdfAttributeId\":null,\"lovAttributeIdList\":null,"
				+ "\"udfValue\":null},{\"udfAttributeId\":[53644],\"udfId\":20210,\"readOnly\":null,\"udfAttributeText\":null,"
				+ "\"defaultUdfAttributeId\":null,\"lovAttributeIdList\":null,\"udfValue\":null},{\"udfAttributeId\":[30200],"
				+ "\"udfId\":20013,\"readOnly\":null,\"udfAttributeText\":null,\"defaultUdfAttributeId\":null,\"lovAttributeIdList"
				+ "\":null,\"udfValue\":null},{\"udfAttributeId\":[30263],\"udfId\":20226,\"readOnly\":null,\"udfAttributeText"
				+ "\":null,\"defaultUdfAttributeId\":null,\"lovAttributeIdList\":null,\"udfValue\":null},{\"udfAttributeId\":[55065],"
				+ "\"udfId\":1000048,\"readOnly\":null,\"udfAttributeText\":null,\"defaultUdfAttributeId\":null,\"lovAttributeIdList"
				+ "\":null,\"udfValue\":null}],\"routeTargets\":null,\"references\":null},{\"componentCodeId\":30,\"fromInvYN\":null,"
				+ "\"logicalChannelPvcID\":null,\"eteVpnKey\":null,\"diversityGroupId\":null,\"componentCodeType\":\"Port\","
				+ "\"componentId\":null,\"externalField\":null,\"parentComponentId\":null,\"externalKeyRef\":null,\"mvlInd\":null,"
				+ "\"userEnteredVpn\":null,\"siteId\":null,\"logicalChannelId\":null,\"designDetails\":[{\"udfAttributeId\":null,"
				+ "\"udfId\":200046,\"readOnly\":null,\"udfAttributeText\":[\"8790\"],\"defaultUdfAttributeId\":null,\"lovAttributeIdList"
				+ "\":null,\"udfValue\":null},{\"udfAttributeId\":null,\"udfId\":200057,\"readOnly\":null,\"udfAttributeText\":[\"VTVT\"],"
				+ "\"defaultUdfAttributeId\":null,\"lovAttributeIdList\":null,\"udfValue\":null}],\"routeTargets\":null,\"references\":null}],"
				+ "\"routerDetails\":null,\"logicalChannelDetail\":null,\"customDesign\":null,\"accessPricingAQ\":null,\"aggregateBilling\":null,"
				+ "\"portValidationMessage\":null,\"voiceFeatureDetail\":null}],\"priceDetails\":{\"componentDetails\":[{\"componentCodeId\":1324,"
				+ "\"componentCodeType\":\"dfas\",\"componentId\":2131,\"componentType\":null,\"componentParentId\":123,\"componentAttributes\":"
				+ "[{\"componentFieldName\":\"we\",\"componentFieldValue\":\"wer\"}],\"priceAttributes\":[{\"productRateId\":123156,\"beid\":"
				+ "\"awf\",\"rateDescription\":\"adwf\",\"priceCatalog\":\"awfa\",\"localListPrice\":21.0,\"targetListPrice\":23.0,\"priceType\":"
				+ "\"awf\",\"priceUnit\":\"wadf\",\"frequency\":\"asdf\",\"monthlySurcharge\":23,\"discount\":86.0,\"discountId\":\"dfa\",\"quantity"
				+ "\":34.0,\"localNetPrice\":4345.0,\"targetNetPrice\":54.0,\"localTotalPrice\":564.0,\"targetTotalPrice\":464.0,\"localCurrency\":"
				+ "\"ewaf\",\"targetCurrency\":\"dsaf\",\"rdsPriceType\":\"adfa\",\"priceName\":\"sfr\",\"typeOfInventory\":\"fwadf\",\"priceInUSD\":"
				+ "\"sad\",\"priceScenarioId\":8745,\"rategroup\":\"adsfad\",\"externalBillingSystem\":\"adwf\",\"ratePlanId\":4556,\"priceCompType\":"
				+ "\"fdsaf\",\"pvcId\":\"adfs\",\"chargeCodeId\":\"das\"}],\"scpPriceMessages\":null}],\"priceMessage\":null},\"address\":null,\"postalCode"
				+ "\":null,\"siteName\":null,\"swcClli\":\"retw\",\"customerLocationClli\":\"wafraw\",\"active\":null,\"emc\":\"ewqtre\",\"carrierHotel\":"
				+ "\"wetre\",\"attComments\":\"gffd\",\"newBuilding\":\"awerewq\",\"customerReference\":\"ryt\",\"asrItemId\":\"54634\",\"lataCode\":"
				+ "\"36543\",\"zipCode\":\"qwrqw\",\"address2\":\"werqw\",\"room\":\"qwerwq\",\"floor\":\"qwfq\",\"building\":\"qwfe\",\"siteComment\":"
				+ "\"ery\",\"lconFirstName\":null,\"lconLastName\":null,\"lconPhone\":null,\"lconEmail\":null,\"endPointSiteIdentifier\":\"wewqw\","
				+ "\"buildingClli\":\"rtr\",\"regionCode\":\"retW\",\"activityType\":\"reyttry\",\"cancellationReason\":\"ert\",\"product\":null,"
				+ "\"quantity\":null,\"nssEngagement\":\"dsaf\",\"designStatus\":null,\"multiGigeIndicator\":\"qwewq\",\"alias\":\"ewr\",\"macdActionType"
				+ "\":null,\"lconDetails\":[{\"lconType\":null,\"lconFirstName\":\"er\",\"lconLastName\":\"ewtr\",\"lconPhone\":\"etw\","
				+ "\"lconEmail\":\"wetr\"}],\"ethernetVendoe\":null}");
		
		return site;
	}
	
	private JSONObject getCircuit() {
		
		JSONObject circuit=JacksonUtil.toJsonObject("{\"component\":[{\"diversityGroupId\":null,\"componentId\":null,\"references\":null,"
				+ "\"logicalChannelPvcID\":null,\"externalKeyRef\":null,\"fromInvYN\":null,\"mvlInd\":null,"
				+ "\"routeTargets\":null,\"externalField\":null,\"parentComponentId\":null,\"userEnteredVpn\":null,"
				+ "\"componentCodeId\":30,\"componentCodeType\":\"Port\",\"siteId\":null,\"eteVpnKey\":null,\"logicalChannelId"
				+ "\":null,\"designDetails\":[{\"udfAttributeId\":null,\"defaultUdfAttributeId\":null,\"lovAttributeIdList\":null,"
				+ "\"readOnly\":null,\"udfValue\":null,\"udfAttributeText\":[\"2\"],\"udfId\":200046},{\"udfAttributeId\":null,"
				+ "\"defaultUdfAttributeId\":null,\"lovAttributeIdList\":null,\"readOnly\":null,\"udfValue\":null,\"udfAttributeText"
				+ "\":[\"yy\"],\"udfId\":200057},{\"udfAttributeId\":null,\"defaultUdfAttributeId\":null,\"lovAttributeIdList\":null,"
				+ "\"readOnly\":null,\"udfValue\":null,\"udfAttributeText\":[\"1\"],\"udfId\":200047}]},{\"diversityGroupId\":null,"
				+ "\"componentId\":10175,\"references\":null,\"logicalChannelPvcID\":null,\"externalKeyRef\":null,\"fromInvYN\":null,"
				+ "\"mvlInd\":null,\"routeTargets\":null,\"externalField\":null,\"parentComponentId\":null,\"userEnteredVpn\":null,"
				+ "\"componentCodeId\":1210,\"componentCodeType\":\"Circuit\",\"siteId\":null,\"eteVpnKey\":null,\"logicalChannelId\":null,"
				+ "\"designDetails\":[{\"udfAttributeId\":[],\"defaultUdfAttributeId\":null,\"lovAttributeIdList\":null,\"readOnly"
				+ "\":null,\"udfValue\":null,\"udfAttributeText\":[\"FL\"],\"udfId\":200118},{\"udfAttributeId\":[301788],\"defaultUdfAttributeId"
				+ "\":null,\"lovAttributeIdList\":null,\"readOnly\":null,\"udfValue\":null,\"udfAttributeText\":[],\"udfId\":200174},{"
				+ "\"udfAttributeId\":[301770],\"defaultUdfAttributeId\":null,\"lovAttributeIdList\":null,\"readOnly\":null,\"udfValue"
				+ "\":null,\"udfAttributeText\":[],\"udfId\":200163},{\"udfAttributeId\":[301773],\"defaultUdfAttributeId\":null,"
				+ "\"lovAttributeIdList\":null,\"readOnly\":null,\"udfValue\":null,\"udfAttributeText\":[],\"udfId\":200164},{"
				+ "\"udfAttributeId\":[30605],\"defaultUdfAttributeId\":null,\"lovAttributeIdList\":null,\"readOnly\":null,\"udfValue"
				+ "\":null,\"udfAttributeText\":[],\"udfId\":200167},{\"udfAttributeId\":[30604],\"defaultUdfAttributeId\":null,"
				+ "\"lovAttributeIdList\":null,\"readOnly\":null,\"udfValue\":null,\"udfAttributeText\":[],\"udfId\":200168},{"
				+ "\"udfAttributeId\":[301784],\"defaultUdfAttributeId\":null,\"lovAttributeIdList\":null,\"readOnly\":null,"
				+ "\"udfValue\":null,\"udfAttributeText\":[],\"udfId\":200169},{\"udfAttributeId\":[30604],\"defaultUdfAttributeId"
				+ "\":null,\"lovAttributeIdList\":null,\"readOnly\":null,\"udfValue\":null,\"udfAttributeText\":[],\"udfId\":200170},{"
				+ "\"udfAttributeId\":[301820],\"defaultUdfAttributeId\":null,\"lovAttributeIdList\":null,\"readOnly\":null,\"udfValue\":null,"
				+ "\"udfAttributeText\":[],\"udfId\":200176},{\"udfAttributeId\":[301845],\"defaultUdfAttributeId\":null,\"lovAttributeIdList"
				+ "\":null,\"readOnly\":null,\"udfValue\":null,\"udfAttributeText\":[],\"udfId\":200216},{\"udfAttributeId\":[30604],"
				+ "\"defaultUdfAttributeId\":null,\"lovAttributeIdList\":null,\"readOnly\":null,\"udfValue\":null,\"udfAttributeText"
				+ "\":[],\"udfId\":200184},{\"udfAttributeId\":[30604],\"defaultUdfAttributeId\":null,\"lovAttributeIdList\":null,"
				+ "\"readOnly\":null,\"udfValue\":null,\"udfAttributeText\":[],\"udfId\":200185},{\"udfAttributeId\":[301890],"
				+ "\"defaultUdfAttributeId\":null,\"lovAttributeIdList\":null,\"readOnly\":null,\"udfValue\":null,\"udfAttributeText\":[],"
				+ "\"udfId\":200217},{\"udfAttributeId\":[],\"defaultUdfAttributeId\":null,\"lovAttributeIdList\":null,\"readOnly\":null,"
				+ "\"udfValue\":null,\"udfAttributeText\":[\"Click to Select\"],\"udfId\":200210},{\"udfAttributeId\":[],\"defaultUdfAttributeId"
				+ "\":null,\"lovAttributeIdList\":null,\"readOnly\":null,\"udfValue\":null,\"udfAttributeText\":[\"AD78789\"],\"udfId\":200162},{"
				+ "\"udfAttributeId\":[302215],\"defaultUdfAttributeId\":null,\"lovAttributeIdList\":null,\"readOnly\":null,\"udfValue\":null,"
				+ "\"udfAttributeText\":[\"R\"],\"udfId\":200193},{\"udfAttributeId\":[],\"defaultUdfAttributeId\":null,\"lovAttributeIdList"
				+ "\":null,\"readOnly\":null,\"udfValue\":null,\"udfAttributeText\":[\"8759\"],\"udfId\":200192},{\"udfAttributeId\":[],"
				+ "\"defaultUdfAttributeId\":null,\"lovAttributeIdList\":null,\"readOnly\":null,\"udfValue\":null,\"udfAttributeText"
				+ "\":[\"8432\"],\"udfId\":200158},{\"udfAttributeId\":[30604],\"defaultUdfAttributeId\":null,\"lovAttributeIdList"
				+ "\":null,\"readOnly\":null,\"udfValue\":null,\"udfAttributeText\":[],\"udfId\":200183},{\"udfAttributeId\":[],"
				+ "\"defaultUdfAttributeId\":null,\"lovAttributeIdList\":null,\"readOnly\":null,\"udfValue\":null,\"udfAttributeText"
				+ "\":[\"0\"],\"udfId\":20781}]},{\"diversityGroupId\":null,\"componentId\":13052,\"references\":[{\"referenceType\":"
				+ "\"Site\",\"referenceId\":5678}],\"logicalChannelPvcID\":null,\"externalKeyRef\":null,\"fromInvYN\":null,\"mvlInd"
				+ "\":null,\"routeTargets\":null,\"externalField\":null,\"parentComponentId\":10175,\"userEnteredVpn\":null,\"componentCodeId"
				+ "\":1220,\"componentCodeType\":\"Endpoint\",\"siteId\":null,\"eteVpnKey\":null,\"logicalChannelId\":null,\"designDetails"
				+ "\":[{\"udfAttributeId\":[301829],\"defaultUdfAttributeId\":null,\"lovAttributeIdList\":null,\"readOnly\":null,\"udfValue"
				+ "\":null,\"udfAttributeText\":[],\"udfId\":200179},{\"udfAttributeId\":[],\"defaultUdfAttributeId\":null,\"lovAttributeIdList"
				+ "\":null,\"readOnly\":null,\"udfValue\":null,\"udfAttributeText\":[\"A\"],\"udfId\":21033},{\"udfAttributeId\":[301848],"
				+ "\"defaultUdfAttributeId\":null,\"lovAttributeIdList\":null,\"readOnly\":null,\"udfValue\":null,\"udfAttributeText\":[],"
				+ "\"udfId\":200205},{\"udfAttributeId\":[30604],\"defaultUdfAttributeId\":null,\"lovAttributeIdList\":null,\"readOnly"
				+ "\":null,\"udfValue\":null,\"udfAttributeText\":[],\"udfId\":200212},{\"udfAttributeId\":[30604],\"defaultUdfAttributeId"
				+ "\":null,\"lovAttributeIdList\":null,\"readOnly\":null,\"udfValue\":null,\"udfAttributeText\":[],\"udfId\":200213},{"
				+ "\"udfAttributeId\":null,\"defaultUdfAttributeId\":null,\"lovAttributeIdList\":null,\"readOnly\":null,\"udfValue\":null,"
				+ "\"udfAttributeText\":[\"UU\"],\"udfId\":200218},{\"udfAttributeId\":[301845],\"defaultUdfAttributeId\":null,\"lovAttributeIdList"
				+ "\":null,\"readOnly\":null,\"udfValue\":null,\"udfAttributeText\":[],\"udfId\":200216},{\"udfAttributeId\":[],\"defaultUdfAttributeId"
				+ "\":null,\"lovAttributeIdList\":null,\"readOnly\":null,\"udfValue\":null,\"udfAttributeText\":[\"GREEN\"],\"udfId\":21036},{"
				+ "\"udfAttributeId\":[],\"defaultUdfAttributeId\":null,\"lovAttributeIdList\":null,\"readOnly\":null,\"udfValue\":null,"
				+ "\"udfAttributeText\":[\"8759\"],\"udfId\":200192},{\"udfAttributeId\":[301820],\"defaultUdfAttributeId\":null,\"lovAttributeIdList"
				+ "\":null,\"readOnly\":null,\"udfValue\":null,\"udfAttributeText\":[],\"udfId\":200176},{\"udfAttributeId\":[301784],"
				+ "\"defaultUdfAttributeId\":null,\"lovAttributeIdList\":null,\"readOnly\":null,\"udfValue\":null,\"udfAttributeText\":[],"
				+ "\"udfId\":200169},{\"udfAttributeId\":[],\"defaultUdfAttributeId\":null,\"lovAttributeIdList\":null,\"readOnly\":null,"
				+ "\"udfValue\":null,\"udfAttributeText\":[\"ORLDFLMA\"],\"udfId\":200045},{\"udfAttributeId\":null,\"defaultUdfAttributeId\":null,"
				+ "\"lovAttributeIdList\":null,\"readOnly\":null,\"udfValue\":null,\"udfAttributeText\":[\"ty\"],\"udfId\":20184},{\"udfAttributeId"
				+ "\":null,\"defaultUdfAttributeId\":null,\"lovAttributeIdList\":null,\"readOnly\":null,\"udfValue\":null,\"udfAttributeText\":["
				+ "\"UY\"],\"udfId\":21136}]},{\"diversityGroupId\":null,\"componentId\":13053,\"references\":[{\"referenceType\":\"Site\","
				+ "\"referenceId\":1234}],\"logicalChannelPvcID\":null,\"externalKeyRef\":null,\"fromInvYN\":null,\"mvlInd\":null,\"routeTargets"
				+ "\":null,\"externalField\":null,\"parentComponentId\":10175,\"userEnteredVpn\":null,\"componentCodeId\":1220,\"componentCodeType"
				+ "\":\"Endpoint\",\"siteId\":null,\"eteVpnKey\":null,\"logicalChannelId\":null,\"designDetails\":[{\"udfAttributeId\":[],"
				+ "\"defaultUdfAttributeId\":null,\"lovAttributeIdList\":null,\"readOnly\":null,\"udfValue\":null,\"udfAttributeText\":["
				+ "\"YELLOW\"],\"udfId\":21037},{\"udfAttributeId\":[],\"defaultUdfAttributeId\":null,\"lovAttributeIdList\":null,\"readOnly"
				+ "\":null,\"udfValue\":null,\"udfAttributeText\":[\"Z\"],\"udfId\":21034},{\"udfAttributeId\":[],\"defaultUdfAttributeId"
				+ "\":null,\"lovAttributeIdList\":null,\"readOnly\":null,\"udfValue\":null,\"udfAttributeText\":[\"8759\"],\"udfId\":200192},{"
				+ "\"udfAttributeId\":[301820],\"defaultUdfAttributeId\":null,\"lovAttributeIdList\":null,\"readOnly\":null,\"udfValue\":null,"
				+ "\"udfAttributeText\":[],\"udfId\":200176},{\"udfAttributeId\":[301784],\"defaultUdfAttributeId\":null,\"lovAttributeIdList"
				+ "\":null,\"readOnly\":null,\"udfValue\":null,\"udfAttributeText\":[],\"udfId\":200169},{\"udfAttributeId\":[],\"defaultUdfAttributeId"
				+ "\":null,\"lovAttributeIdList\":null,\"readOnly\":null,\"udfValue\":null,\"udfAttributeText\":[\"KSSMFLXA\"],\"udfId\":200045},{"
				+ "\"udfAttributeId\":null,\"defaultUdfAttributeId\":null,\"lovAttributeIdList\":null,\"readOnly\":null,\"udfValue\":null,"
				+ "\"udfAttributeText\":[\"IOU\"],\"udfId\":20184},{\"udfAttributeId\":null,\"defaultUdfAttributeId\":null,\"lovAttributeIdList"
				+ "\":null,\"readOnly\":null,\"udfValue\":null,\"udfAttributeText\":[\"OUIP\"],\"udfId\":21136}]}],\"site\":[{\"country\":"
				+ "\"wqrewq\",\"zipCode\":\"qwrqw\",\"postalCode\":null,\"speedId\":null,\"lconLastName\":null,\"lataCode\":\"36543\","
				+ "\"siteComment\":\"ery\",\"building\":\"qwfe\",\"asrItemId\":\"54634\",\"fromInventory\":null,\"buildingClli\":\"rtr\","
				+ "\"endPointSiteIdentifier\":\"wewqw\",\"multiGigeIndicator\":\"qwewq\",\"customerReference\":\"ryt\",\"state\":\"ewrqw\",\"npanxx"
				+ "\":1254,\"saLecSwClli\":null,\"active\":null,\"lconDetails\":[{\"lconType\":null,\"lconLastName\":\"ewtr\",\"lconFirstName\":\"er\","
				+ "\"lconPhone\":\"etw\",\"lconEmail\":\"wetr\"}],\"macdActionType\":null,\"macdActivity\":null,\"priceDetails\":{\"componentDetails\":[{"
				+ "\"componentType\":null,\"componentId\":2131,\"componentAttributes\":[{\"componentFieldName\":\"we\",\"componentFieldValue\":\"wer\"}],"
				+ "\"componentCodeId\":1324,\"componentCodeType\":\"dfas\",\"componentParentId\":123,\"priceAttributes\":[{\"priceScenarioId\":8745,"
				+ "\"priceCatalog\":\"awfa\",\"localNetPrice\":4345.0,\"rategroup\":\"adsfad\",\"ratePlanId\":4556,\"priceInUSD\":\"sad\",\"monthlySurcharge"
				+ "\":23,\"discount\":86.0,\"targetListPrice\":23.0,\"localCurrency\":\"ewaf\",\"chargeCodeId\":\"das\",\"frequency\":\"asdf\",\"rdsPriceType"
				+ "\":\"adfa\",\"localTotalPrice\":564.0,\"productRateId\":123156,\"targetNetPrice\":54.0,\"priceUnit\":\"wadf\",\"quantity\":34.0,"
				+ "\"targetCurrency\":\"dsaf\",\"priceCompType\":\"fdsaf\",\"priceType\":\"awf\",\"typeOfInventory\":\"fwadf\",\"rateDescription\":\"adwf\","
				+ "\"externalBillingSystem\":\"adwf\",\"beid\":\"awf\",\"targetTotalPrice\":464.0,\"pvcId\":\"adfs\",\"discountId\":\"dfa\",\"priceName\":"
				+ "\"sfr\",\"localListPrice\":21.0}],\"scpPriceMessages\":null}],\"priceMessage\":null},\"designStatus\":null,\"emc\":\"ewqtre\","
				+ "\"customerLocationClli\":\"IOU\",\"city\":\"qwr\",\"designSiteOfferPort\":[{\"aggregateBilling\":null,\"typeOfInventory\":null,"
				+ "\"voiceFeatureDetail\":null,\"macdActivityType\":null,\"milesResult\":null,\"securityDesignDetails\":null,\"component\":[{"
				+ "\"diversityGroupId\":null,\"componentId\":9195162,\"references\":null,\"logicalChannelPvcID\":null,\"externalKeyRef\":null,"
				+ "\"fromInvYN\":null,\"mvlInd\":null,\"routeTargets\":null,\"externalField\":null,\"parentComponentId\":null,\"userEnteredVpn\":null,"
				+ "\"componentCodeId\":10,\"componentCodeType\":\"connection\",\"siteId\":null,\"eteVpnKey\":null,\"logicalChannelId\":null,"
				+ "\"designDetails\":null},{\"diversityGroupId\":null,\"componentId\":null,\"references\":null,\"logicalChannelPvcID\":null,"
				+ "\"externalKeyRef\":null,\"fromInvYN\":null,\"mvlInd\":null,\"routeTargets\":null,\"externalField\":null,\"parentComponentId\":null,"
				+ "\"userEnteredVpn\":null,\"componentCodeId\":50,\"componentCodeType\":\"Cos\",\"siteId\":null,\"eteVpnKey\":null,\"logicalChannelId"
				+ "\":null,\"designDetails\":[{\"udfAttributeId\":[1024724],\"defaultUdfAttributeId\":null,\"lovAttributeIdList\":null,\"readOnly\":null,"
				+ "\"udfValue\":null,\"udfAttributeText\":null,\"udfId\":20014},{\"udfAttributeId\":[53644],\"defaultUdfAttributeId\":null,"
				+ "\"lovAttributeIdList\":null,\"readOnly\":null,\"udfValue\":null,\"udfAttributeText\":null,\"udfId\":20210},{\"udfAttributeId\":[30200],"
				+ "\"defaultUdfAttributeId\":null,\"lovAttributeIdList\":null,\"readOnly\":null,\"udfValue\":null,\"udfAttributeText\":null,\"udfId\":20013},{"
				+ "\"udfAttributeId\":[30263],\"defaultUdfAttributeId\":null,\"lovAttributeIdList\":null,\"readOnly\":null,\"udfValue\":null,\"udfAttributeText\":null,"
				+ "\"udfId\":20226},{\"udfAttributeId\":[55065],\"defaultUdfAttributeId\":null,\"lovAttributeIdList\":null,\"readOnly\":null,\"udfValue\":null,"
				+ "\"udfAttributeText\":null,\"udfId\":1000048}]},{\"diversityGroupId\":null,\"componentId\":null,\"references\":null,\"logicalChannelPvcID\":null,"
				+ "\"externalKeyRef\":null,\"fromInvYN\":null,\"mvlInd\":null,\"routeTargets\":null,\"externalField\":null,\"parentComponentId\":null,\"userEnteredVpn\":null,"
				+ "\"componentCodeId\":30,\"componentCodeType\":\"Port\",\"siteId\":null,\"eteVpnKey\":null,\"logicalChannelId\":null,\"designDetails\":[{\"udfAttributeId\":null,"
				+ "\"defaultUdfAttributeId\":null,\"lovAttributeIdList\":null,\"readOnly\":null,\"udfValue\":null,\"udfAttributeText\":[\"908\"],\"udfId\":200046},{"
				+ "\"udfAttributeId\":null,\"defaultUdfAttributeId\":null,\"lovAttributeIdList\":null,\"readOnly\":null,\"udfValue\":null,\"udfAttributeText\":[\"IIIII\"],"
				+ "\"udfId\":200057}]}],\"portValidationMessage\":null,\"accessPricingAQ\":null,\"designStatus\":\"D\",\"logicalChannelDetail\":null,"
				+ "\"routerDetails\":null,\"customDesign\":null}],\"siteName\":null,\"nssEngagement\":\"dsaf\",\"regionCode\":\"retW\",\"dualSiteId\":null,\"swcClli\":"
				+ "\"retw\",\"attComments\":\"gffd\",\"newBuilding\":\"awerewq\",\"alias\":\"ewr\",\"floor\":\"qwfq\",\"saLecName\":null,\"lconEmail\":null,\"product\":null,"
				+ "\"address\":null,\"quantity\":null,\"address2\":\"werqw\",\"cancellationReason\":\"ert\",\"address1\":\"fdsa\",\"onNetCheck\":null,\"lconFirstName\":null,"
				+ "\"lconPhone\":null,\"room\":\"qwerwq\",\"carrierHotel\":\"wetre\",\"ethernetVendor\":null,\"siteId\":1234,\"popClli\":null,\"macdType\":\"take\","
				+ "\"activityType\":\"reyttry\",\"ethernetVendoe\":null},{\"country\":\"wqrewq\",\"zipCode\":\"qwrqw\",\"postalCode\":null,\"speedId\":null,"
				+ "\"lconLastName\":null,\"lataCode\":\"36543\",\"siteComment\":\"ery\",\"building\":\"qwfe\",\"asrItemId\":\"54634\",\"fromInventory\":null,"
				+ "\"buildingClli\":\"rtr\",\"endPointSiteIdentifier\":\"wewqw\",\"multiGigeIndicator\":\"qwewq\",\"customerReference\":\"ryt\",\"state\":"
				+ "\"ewrqw\",\"npanxx\":1254,\"saLecSwClli\":null,\"active\":null,\"lconDetails\":[{\"lconType\":null,\"lconLastName\":\"ewtr\",\"lconFirstName\":"
				+ "\"er\",\"lconPhone\":\"etw\",\"lconEmail\":\"wetr\"}],\"macdActionType\":null,\"macdActivity\":null,\"priceDetails\":{\"componentDetails\":[{\"componentType\":null,"
				+ "\"componentId\":2131,\"componentAttributes\":[{\"componentFieldName\":\"we\",\"componentFieldValue\":\"wer\"}],\"componentCodeId\":1324,"
				+ "\"componentCodeType\":\"dfas\",\"componentParentId\":123,\"priceAttributes\":[{\"priceScenarioId\":8745,\"priceCatalog\":\"awfa\",\"localNetPrice"
				+ "\":4345.0,\"rategroup\":\"adsfad\",\"ratePlanId\":4556,\"priceInUSD\":\"sad\",\"monthlySurcharge\":23,\"discount\":86.0,\"targetListPrice\":23.0,"
				+ "\"localCurrency\":\"ewaf\",\"chargeCodeId\":\"das\",\"frequency\":\"asdf\",\"rdsPriceType\":\"adfa\",\"localTotalPrice\":564.0,\"productRateId"
				+ "\":123156,\"targetNetPrice\":54.0,\"priceUnit\":\"wadf\",\"quantity\":34.0,\"targetCurrency\":\"dsaf\",\"priceCompType\":\"fdsaf\",\"priceType"
				+ "\":\"awf\",\"typeOfInventory\":\"fwadf\",\"rateDescription\":\"adwf\",\"externalBillingSystem\":\"adwf\",\"beid\":\"awf\",\"targetTotalPrice"
				+ "\":464.0,\"pvcId\":\"adfs\",\"discountId\":\"dfa\",\"priceName\":\"sfr\",\"localListPrice\":21.0}],\"scpPriceMessages\":null}],\"priceMessage"
				+ "\":null},\"designStatus\":null,\"emc\":\"ewqtre\",\"customerLocationClli\":\"te\",\"city\":\"qwr\",\"designSiteOfferPort\":[{\"aggregateBilling"
				+ "\":null,\"typeOfInventory\":null,\"voiceFeatureDetail\":null,\"macdActivityType\":null,\"milesResult\":null,\"securityDesignDetails\":null,"
				+ "\"component\":[{\"diversityGroupId\":null,\"componentId\":9195162,\"references\":null,\"logicalChannelPvcID\":null,\"externalKeyRef\":null,"
				+ "\"fromInvYN\":null,\"mvlInd\":null,\"routeTargets\":null,\"externalField\":null,\"parentComponentId\":null,\"userEnteredVpn\":null,"
				+ "\"componentCodeId\":10,\"componentCodeType\":\"connection\",\"siteId\":null,\"eteVpnKey\":null,\"logicalChannelId\":null,\"designDetails"
				+ "\":null},{\"diversityGroupId\":null,\"componentId\":null,\"references\":null,\"logicalChannelPvcID\":null,\"externalKeyRef\":null,\"fromInvYN"
				+ "\":null,\"mvlInd\":null,\"routeTargets\":null,\"externalField\":null,\"parentComponentId\":null,\"userEnteredVpn\":null,\"componentCodeId\":50,"
				+ "\"componentCodeType\":\"Cos\",\"siteId\":null,\"eteVpnKey\":null,\"logicalChannelId\":null,\"designDetails\":[{\"udfAttributeId\":[1024724],"
				+ "\"defaultUdfAttributeId\":null,\"lovAttributeIdList\":null,\"readOnly\":null,\"udfValue\":null,\"udfAttributeText\":null,\"udfId\":20014},{"
				+ "\"udfAttributeId\":[53644],\"defaultUdfAttributeId\":null,\"lovAttributeIdList\":null,\"readOnly\":null,\"udfValue\":null,\"udfAttributeText"
				+ "\":null,\"udfId\":20210},{\"udfAttributeId\":[30200],\"defaultUdfAttributeId\":null,\"lovAttributeIdList\":null,\"readOnly\":null,\"udfValue"
				+ "\":null,\"udfAttributeText\":null,\"udfId\":20013},{\"udfAttributeId\":[30263],\"defaultUdfAttributeId\":null,\"lovAttributeIdList\":null,"
				+ "\"readOnly\":null,\"udfValue\":null,\"udfAttributeText\":null,\"udfId\":20226},{\"udfAttributeId\":[55065],\"defaultUdfAttributeId\":null,"
				+ "\"lovAttributeIdList\":null,\"readOnly\":null,\"udfValue\":null,\"udfAttributeText\":null,\"udfId\":1000048}]},{\"diversityGroupId\":null,"
				+ "\"componentId\":null,\"references\":null,\"logicalChannelPvcID\":null,\"externalKeyRef\":null,\"fromInvYN\":null,\"mvlInd\":null,\"routeTargets\":null,"
				+ "\"externalField\":null,\"parentComponentId\":null,\"userEnteredVpn\":null,\"componentCodeId\":30,\"componentCodeType\":\"Port\",\"siteId\":null,\"eteVpnKey\":null,"
				+ "\"logicalChannelId\":null,\"designDetails\":[{\"udfAttributeId\":null,\"defaultUdfAttributeId\":null,\"lovAttributeIdList\":null,\"readOnly\":null,\"udfValue\":null,"
				+ "\"udfAttributeText\":[\"908\"],\"udfId\":200046},{\"udfAttributeId\":null,\"defaultUdfAttributeId\":null,\"lovAttributeIdList\":null,\"readOnly\":null,\"udfValue\":null,"
				+ "\"udfAttributeText\":[\"IIIII\"],\"udfId\":200057}]}],\"portValidationMessage\":null,\"accessPricingAQ\":null,\"designStatus\":\"D\",\"logicalChannelDetail\":null,"
				+ "\"routerDetails\":null,\"customDesign\":null}],\"siteName\":null,\"nssEngagement\":\"dsaf\",\"regionCode\":\"retW\",\"dualSiteId\":null,\"swcClli\":\"retw\","
				+ "\"attComments\":\"gffd\",\"newBuilding\":\"awerewq\",\"alias\":\"ewr\",\"floor\":\"qwfq\",\"saLecName\":null,\"lconEmail\":null,\"product\":null,\"address\":null,"
				+ "\"quantity\":null,\"address2\":\"werqw\",\"cancellationReason\":\"ert\",\"address1\":\"fdsa\",\"onNetCheck\":null,\"lconFirstName\":null,\"lconPhone\":null,"
				+ "\"room\":\"qwerwq\",\"carrierHotel\":\"wetre\",\"ethernetVendor\":null,\"siteId\":5678,\"popClli\":null,\"macdType\":\"take\","
				+ "\"activityType\":\"reyttry\",\"ethernetVendoe\":null}]}");
		return circuit;
	}
	
	@Test
	public void createActionDeterminantsTest() {
		transmitDesignDataService.createActionDeterminants("W", Arrays.asList("ww"));
	}
	
	@Test
	public void getEndPointSiteBlockTest() {
		transmitDesignDataService.getEndPointSiteBlock(getCircuit(), 5678l);
	}
	
	
	@Test
	public void convertPedToUdfFormatTest1() {
		Map<Long,Map<String,NxUdfMapping>> udfMappingData=new HashMap<>();
		Map<String,NxUdfMapping> intMap=new HashMap<>(); 
		NxUdfMapping b=new NxUdfMapping();
		b.setComponentId(12l);
		b.setComponentType("port");
		b.setNxUdfMappingId(1l);
		b.setOfferId(2l);
		b.setOfferName("ASE");
		b.setRuleSet("TT");
		b.setUdfId(1l);
		b.setUdfAttributeDatasetName("ID");
		intMap.put("estimatedInterval", b);
		udfMappingData.put(30l, intMap);
		NxLookupData lookupdata=new NxLookupData();
		lookupdata.setItemId("RR");
		when(tddRepositoryService.findTopByDatasetNameAndDescription(any(),any())).thenReturn(lookupdata);
		transmitDesignDataService.convertPedToUdfFormat(udfMappingData, 30l, "estimatedInterval", "TT");
	}
	
	@Test
	public void convertPedToUdfFormatTest2() {
		Map<Long,Map<String,NxUdfMapping>> udfMappingData=new HashMap<>();
		Map<String,NxUdfMapping> intMap=new HashMap<>(); 
		NxUdfMapping b=new NxUdfMapping();
		b.setComponentId(12l);
		b.setComponentType("port");
		b.setNxUdfMappingId(1l);
		b.setOfferId(2l);
		b.setOfferName("ASE");
		b.setRuleSet("TT");
		b.setUdfId(1l);
		b.setUdfAttributeDatasetName("ID");
		intMap.put("estimatedInterval", b);
		udfMappingData.put(30l, intMap);
		NxLookupData lookupdata=new NxLookupData();
		lookupdata.setItemId("RR");
		when(tddRepositoryService.getUdfAttrIdFromSalesTbl(any(),any(),any(),any())).thenReturn(1l);
		transmitDesignDataService.convertPedToUdfFormat(udfMappingData, 30l, "estimatedInterval", "TT");
	}
	
	
	@Test
	public void callOrchCustomPricingOrderFlowTest() throws SalesBusinessException{
		JSONObject request=new JSONObject();
		String offerName="ASE";
		NxDesign nxDesign=new NxDesign();
		transmitDesignDataService.callOrchCustomPricingOrderFlow(request, offerName, nxDesign);
	}
	
	@Test
	public void addDesignByUdfIdTest1() {
		transmitDesignDataService.addDesignByUdfId("11", "1234", "GHH", CommonConstants.TEXT, new Object(), "");
	}
	
	@Test
	public void addDesignByUdfIdTest2() {
		transmitDesignDataService.addDesignByUdfId("11", "1234", "123", CommonConstants.ID, new Object(), "");
	}
	
	@Test
	public void updateDesignByUdfId1() {
		List<UDFBaseData> designDetailList=new ArrayList<>();
		UDFBaseData u =new UDFBaseData();
		u.setUdfId(1);
		u.setUdfAttributeText(new ArrayList<>(Arrays.asList("tt")));
		designDetailList.add(u);
		transmitDesignDataService.updateDesignByUdfId("11", "1234", CommonConstants.TEXT, new Object(),"", designDetailList,"");
	}
	
	@Test
	public void updateDesignByUdfId2() {
		List<UDFBaseData> designDetailList=new ArrayList<>();
		UDFBaseData u =new UDFBaseData();
		u.setUdfId(1);
		u.setUdfAttributeId(new ArrayList<>(Arrays.asList(1l)));
		designDetailList.add(u);
		transmitDesignDataService.updateDesignByUdfId("11", "1234", CommonConstants.ID, new Object(),"", designDetailList,"");
	}
	
	@Test
	public void addComponentBlockTest() {
		List<Component> components=new ArrayList<>();
		Component c=new Component();
		components.add(c);
		String componentCodeId="122";
		String componentCodeType="TT";
		transmitDesignDataService.addComponentBlock(components, componentCodeId, componentCodeType);
	}
	
	@Test
	public void setDesignChangeStatusTest1() {
		List<UDFBaseData> designDetailList=new ArrayList<>();
		UDFBaseData u =new UDFBaseData();
		u.setUdfId(1);
		u.setUdfAttributeText(new ArrayList<>(Arrays.asList("tt")));
		designDetailList.add(u);
		transmitDesignDataService.setDesignChangeStatus(designDetailList, CommonConstants.TEXT, "1");
	}
	
	@Test
	public void setDesignChangeStatusTest2() {
		List<UDFBaseData> designDetailList=new ArrayList<>();
		UDFBaseData u =new UDFBaseData();
		u.setUdfId(1);
		u.setUdfAttributeId(new ArrayList<>(Arrays.asList(1l)));
		designDetailList.add(u);
		transmitDesignDataService.setDesignChangeStatus(designDetailList, CommonConstants.ID, "2");
	}
	
	@Test
	public void getPedStatusDiscriptionTest() {
		Map<String,NxLookupData> pedStatusMap =new HashMap<>();
		NxLookupData n=new NxLookupData();
		n.setDescription("gg");
		pedStatusMap.put("D", n);
		transmitDesignDataService.getPedStatusDiscription("D", pedStatusMap);
	}
	
	@Test
	public void updateActionDeterminantsTest() {
		JSONObject baseRequest=new JSONObject();
		transmitDesignDataService.updateActionDeterminants(baseRequest);
	}
	
	@Test
	public void checkKmzMapLinkCreatePedDmaapDesignForADE() {
		NxPEDStatusDMaap pedDmaapReqObj=new NxPEDStatusDMaap();
		pedDmaapReqObj.setKmzMapLink(null);
		pedDmaapReqObj.setEventType("Circuit");

		CircuitDetails pedCircuitDetails = new CircuitDetails();
		Map<String,Object> inputMap =new HashMap<>() ;
		inputMap.put(TDDConstants.RESPONSE_TYPE,"Solution");
		transmitDesignDataService.createPedDmaapDesignForADE(pedDmaapReqObj,pedCircuitDetails,inputMap);
		inputMap.put(TDDConstants.SOLUTION_LEVEL_STATUS, null);
		transmitDesignDataService.createPedDmaapDesignForADE(pedDmaapReqObj,pedCircuitDetails,inputMap);
		
		inputMap.put(TDDConstants.SOLUTION_LEVEL_STATUS, "D");
		transmitDesignDataService.createPedDmaapDesignForADE(pedDmaapReqObj,pedCircuitDetails,inputMap);
	
		inputMap.put(TDDConstants.SOLUTION_LEVEL_STATUS, "");
		transmitDesignDataService.createPedDmaapDesignForADE(pedDmaapReqObj,pedCircuitDetails,inputMap);

		inputMap.put(TDDConstants.CIRCUIT_LEVEL_STATUS, "D");
		inputMap.put(TDDConstants.RESPONSE_TYPE,"Circuit");
		inputMap.put(TDDConstants.TDD_KMZ_MAP_LINK,"linkValue");
		transmitDesignDataService.createPedDmaapDesignForADE(pedDmaapReqObj,pedCircuitDetails,inputMap);

		inputMap.put(TDDConstants.CIRCUIT_LEVEL_STATUS, "S");
		transmitDesignDataService.createPedDmaapDesignForADE(pedDmaapReqObj,pedCircuitDetails,inputMap);
		
	}

	@Test
	public void createSiteForADE() throws ParseException {
		Map<String,Object> inputMap=new HashMap<>();
		inputMap.put(TDDConstants.A_SITE_ID,7658L);
		inputMap.put(TDDConstants.Z_SITE_ID,68678L);
		inputMap.put(TDDConstants.ASR_ITEM_ID,"AD15689");
		inputMap.put(TDDConstants.CIRCUIT_LEVEL_STATUS,"C");
		inputMap.put(TDDConstants.CIRCUIT_LEVEL_CANCEL_REASON,"business error");
		String circuitData="{ \"component\": [ { \"componentCodeId\": 10, \"componentCodeType\": \"Connection\", \"componentId\": 9278505, \"designDetails\": [ { \"udfAttributeId\": [ 30595 ], \"udfId\": 20030, \"udfAttributeText\": [ \"Primary\" ] }, { \"udfAttributeId\": [ 30515 ], \"udfId\": 20017, \"udfAttributeText\": [ \"AVPN\" ] } ] } ] }";
	
		JSONParser parser = new JSONParser();
		JSONObject reqCircuitData=(JSONObject) parser.parse(circuitData);
		
		JSONObject result=transmitDesignDataService.createSiteForADE(inputMap,reqCircuitData);
		assertEquals("AD15689", (String)result.get("asrItemId"));
	}
	
	@Test
	public void testcreateDppRequestForADE() {
		ReflectionTestUtils.setField(transmitDesignDataService, "isDppTriggerADE", "Y");
		NxSolutionDetail dbSolutionDtls=new NxSolutionDetail() ;
		dbSolutionDtls.setNxSolutionId(1L);
		NxDesignDetails nxDesignDetails= new NxDesignDetails();
		nxDesignDetails.setDesignData("{\"designVersion\":1,\"designModifiedInd\":null,\"cancellationReason\":null,\"specialConstructionHandling\":null,\"nssEngagement\":null,\"typeOfInventory\":null,\"accessCarrierNameAbbreviation\":null,\"component\":[{\"diversityGroupId\":null,\"componentId\":52655,\"references\":[],\"logicalChannelPvcID\":null,\"externalKeyRef\":null,\"fromInvYN\":null,\"mvlInd\":null,\"routeTargets\":null,\"externalField\":null,\"parentComponentId\":null,\"userEnteredVpn\":null,\"componentCodeId\":1210,\"componentCodeType\":\"Circuit\",\"siteId\":null,\"eteVpnKey\":null,\"logicalChannelId\":null,\"siteObj\":null,\"designDetails\":[{\"udfAttributeId\":[30605],\"defaultUdfAttributeId\":null,\"lovAttributeIdList\":null,\"readOnly\":null,\"udfValue\":null,\"udfAttributeText\":[\"N\\/A\"],\"udfId\":22211},{\"udfAttributeId\":[30605],\"defaultUdfAttributeId\":null,\"lovAttributeIdList\":null,\"readOnly\":null,\"udfValue\":null,\"udfAttributeText\":[\"OKGS\"],\"udfId\":22212},{\"udfAttributeId\":[30605],\"defaultUdfAttributeId\":null,\"lovAttributeIdList\":null,\"readOnly\":null,\"udfValue\":null,\"udfAttributeText\":[\"100\"],\"udfId\":22213},{\"udfAttributeId\":[30604],\"defaultUdfAttributeId\":null,\"lovAttributeIdList\":null,\"readOnly\":null,\"udfValue\":null,\"udfAttributeText\":[\"OK--\"],\"udfId\":22214},{\"udfAttributeId\":[343047],\"defaultUdfAttributeId\":null,\"lovAttributeIdList\":null,\"readOnly\":null,\"udfValue\":null,\"udfAttributeText\":[\"Interstate\\/Intrastate USOC\"],\"udfId\":21085},{\"udfAttributeId\":[301770],\"defaultUdfAttributeId\":null,\"lovAttributeIdList\":null,\"readOnly\":null,\"udfValue\":null,\"udfAttributeText\":[\"Add\"],\"udfId\":200163},{\"udfAttributeId\":[301820],\"defaultUdfAttributeId\":null,\"lovAttributeIdList\":null,\"readOnly\":null,\"udfValue\":null,\"udfAttributeText\":[\"Interstate (FCC) Access (Interstate)\"],\"udfId\":200176},{\"udfAttributeId\":[30604],\"defaultUdfAttributeId\":null,\"lovAttributeIdList\":null,\"readOnly\":null,\"udfValue\":null,\"udfAttributeText\":[\"No\"],\"udfId\":200168},{\"udfAttributeId\":[301794],\"defaultUdfAttributeId\":null,\"lovAttributeIdList\":null,\"readOnly\":null,\"udfValue\":null,\"udfAttributeText\":[\"OTU2 (10 Gbps)\"],\"udfId\":200174},{\"udfAttributeId\":[30604],\"defaultUdfAttributeId\":null,\"lovAttributeIdList\":null,\"readOnly\":null,\"udfValue\":null,\"udfAttributeText\":[\"No\"],\"udfId\":200184},{\"udfAttributeId\":[301784],\"defaultUdfAttributeId\":null,\"lovAttributeIdList\":null,\"readOnly\":null,\"udfValue\":null,\"udfAttributeText\":[\"12 Months\"],\"udfId\":200169},{\"udfAttributeId\":[30604],\"defaultUdfAttributeId\":null,\"lovAttributeIdList\":null,\"readOnly\":null,\"udfValue\":null,\"udfAttributeText\":[\"No\"],\"udfId\":200170},{\"udfAttributeId\":[30604],\"defaultUdfAttributeId\":null,\"lovAttributeIdList\":null,\"readOnly\":null,\"udfValue\":null,\"udfAttributeText\":[\"No\"],\"udfId\":200180},{\"udfAttributeId\":[301773],\"defaultUdfAttributeId\":null,\"lovAttributeIdList\":null,\"readOnly\":null,\"udfValue\":null,\"udfAttributeText\":[\"Newstart Circuit\"],\"udfId\":200164},{\"udfAttributeId\":[30604],\"defaultUdfAttributeId\":null,\"lovAttributeIdList\":null,\"readOnly\":null,\"udfValue\":null,\"udfAttributeText\":[\"No\"],\"udfId\":200167},{\"udfAttributeId\":[30604],\"defaultUdfAttributeId\":null,\"lovAttributeIdList\":null,\"readOnly\":null,\"udfValue\":null,\"udfAttributeText\":[\"No\"],\"udfId\":200185},{\"udfAttributeId\":[],\"defaultUdfAttributeId\":null,\"lovAttributeIdList\":null,\"readOnly\":null,\"udfValue\":null,\"udfAttributeText\":[\"NC\"],\"udfId\":200118},{\"udfAttributeId\":[],\"defaultUdfAttributeId\":null,\"lovAttributeIdList\":null,\"readOnly\":null,\"udfValue\":null,\"udfAttributeText\":[\"1\"],\"udfId\":200172},{\"udfAttributeId\":[],\"defaultUdfAttributeId\":null,\"lovAttributeIdList\":null,\"readOnly\":null,\"udfValue\":null,\"udfAttributeText\":[\"3\"],\"udfId\":200198},{\"udfAttributeId\":[301845],\"defaultUdfAttributeId\":null,\"lovAttributeIdList\":null,\"readOnly\":null,\"udfValue\":null,\"udfAttributeText\":[\"Y\"],\"udfId\":200216},{\"udfAttributeId\":[],\"defaultUdfAttributeId\":null,\"lovAttributeIdList\":null,\"readOnly\":null,\"udfValue\":null,\"udfAttributeText\":[\"AD3136566\"],\"udfId\":200162},{\"udfAttributeId\":[],\"defaultUdfAttributeId\":null,\"lovAttributeIdList\":null,\"readOnly\":null,\"udfValue\":null,\"udfAttributeText\":[\"6\"],\"udfId\":20781},{\"udfAttributeId\":[302220],\"defaultUdfAttributeId\":null,\"lovAttributeIdList\":null,\"readOnly\":null,\"udfValue\":null,\"udfAttributeText\":[\"Y\"],\"udfId\":200193},{\"udfAttributeId\":[],\"defaultUdfAttributeId\":null,\"lovAttributeIdList\":null,\"readOnly\":null,\"udfValue\":null,\"udfAttributeText\":[\"32358\"],\"udfId\":200192}]},{\"diversityGroupId\":null,\"componentId\":98413,\"references\":[{\"referenceType\":\"Site\",\"referenceId\":99995150542}],\"logicalChannelPvcID\":null,\"externalKeyRef\":null,\"fromInvYN\":null,\"mvlInd\":null,\"routeTargets\":null,\"externalField\":null,\"parentComponentId\":52655,\"userEnteredVpn\":null,\"componentCodeId\":1220,\"componentCodeType\":\"Endpoint\",\"siteId\":null,\"eteVpnKey\":null,\"logicalChannelId\":null,\"siteObj\":{\"country\":\"US\",\"zipCode\":\"60613\",\"diverseFromReferenceInfo\":null,\"swcCertification\":null,\"postalCode\":null,\"speedId\":null,\"siteNpanxx\":null,\"lconLastName\":null,\"lataCode\":\"422\",\"siteComment\":null,\"specialConstructionPaymentUrl\":null,\"certificationStatus\":null,\"_endPointRef\":21033,\"building\":null,\"asrItemId\":null,\"numOfCopperRepeaters\":null,\"fromInventory\":null,\"buildingClli\":null,\"endPointSiteIdentifier\":\"A\",\"multiGigeIndicator\":null,\"customerReference\":null,\"state\":\"NC\",\"specialConstructionHandlingNotes\":null,\"opticalTerminatingNetworkCarrierFacilityAssignment\":null,\"npanxx\":\"889988\",\"designVersion\":null,\"globalLocationId\":null,\"thirdPartyInd\":null,\"saLecSwClli\":null,\"active\":null,\"accessCarrierNameAbbreviation\":null,\"lconDetails\":[{\"lconType\":null,\"lconLastName\":null,\"lconFirstName\":null,\"lconPhone\":null,\"lconEmail\":null}],\"specialConstructionCharge\":null,\"interDepartmentMeetPointChecklistURL\":null,\"inventoryNumOfPairs\":null,\"macdActionType\":null,\"macdActivity\":null,\"priceDetails\":null,\"designStatus\":null,\"emc\":null,\"customerLocationClli\":\"CHCJILLFH00\",\"collocationCarrierFacilityAssignment\":null,\"city\":\"CHGO\",\"designSiteOfferPort\":null,\"numberRemoteTerminals\":null,\"jurisdiction\":null,\"siteName\":\"L2Z\",\"nssEngagement\":null,\"taskClli\":null,\"regionCode\":\"Y\",\"independentCarrierCompanyLATA\":null,\"dualSiteId\":null,\"swcClli\":\"CHCGILLW\",\"attComments\":null,\"newBuilding\":null,\"design\":null,\"alias\":\"L2Z\",\"floor\":null,\"saLecName\":null,\"lconEmail\":null,\"loopLength\":null,\"independentCarrierCompanyName\":null,\"nxSiteId\":451421,\"product\":null,\"address\":null,\"quantity\":null,\"designModifiedInd\":null,\"referenceOfferId\":null,\"address2\":null,\"cancellationReason\":null,\"address1\":\"1946 W IRVING PARK RD\",\"assetInvestmentSheetIndicator\":null,\"specialConstructionHandling\":null,\"onNetCheck\":null,\"lconFirstName\":null,\"lconPhone\":null,\"room\":null,\"carrierHotel\":null,\"ethernetVendor\":null,\"siteId\":99995150542,\"popClli\":null,\"macdType\":null,\"activityType\":null},\"designDetails\":[{\"udfAttributeId\":[30604],\"defaultUdfAttributeId\":null,\"lovAttributeIdList\":null,\"readOnly\":null,\"udfValue\":null,\"udfAttributeText\":[\"Interstate\\/Intrastate USOC\"],\"udfId\":22210},{\"udfAttributeId\":[],\"defaultUdfAttributeId\":null,\"lovAttributeIdList\":null,\"readOnly\":null,\"udfValue\":null,\"udfAttributeText\":[\"02OTF.202\"],\"udfId\":20187},{\"udfAttributeId\":[30604],\"defaultUdfAttributeId\":null,\"lovAttributeIdList\":null,\"readOnly\":null,\"udfValue\":null,\"udfAttributeText\":[\"No\"],\"udfId\":200212},{\"udfAttributeId\":[301794],\"defaultUdfAttributeId\":null,\"lovAttributeIdList\":null,\"readOnly\":null,\"udfValue\":null,\"udfAttributeText\":[\"OTU2 (10 Gbps)\"],\"udfId\":200174},{\"udfAttributeId\":[301845],\"defaultUdfAttributeId\":null,\"lovAttributeIdList\":null,\"readOnly\":null,\"udfValue\":null,\"udfAttributeText\":[\"Y\"],\"udfId\":200216},{\"udfAttributeId\":[304240],\"defaultUdfAttributeId\":null,\"lovAttributeIdList\":null,\"readOnly\":null,\"udfValue\":null,\"udfAttributeText\":[null],\"udfId\":200208},{\"udfAttributeId\":[30604],\"defaultUdfAttributeId\":null,\"lovAttributeIdList\":null,\"readOnly\":null,\"udfValue\":null,\"udfAttributeText\":[\"No\"],\"udfId\":200213},{\"udfAttributeId\":[301833],\"defaultUdfAttributeId\":null,\"lovAttributeIdList\":null,\"readOnly\":null,\"udfValue\":null,\"udfAttributeText\":[\"1310 - SR1 Single Mode Fiber (SMF)\"],\"udfId\":200179},{\"udfAttributeId\":[301806],\"defaultUdfAttributeId\":null,\"lovAttributeIdList\":null,\"readOnly\":null,\"udfValue\":null,\"udfAttributeText\":[\"IR \\/ IF End-User Customer\"],\"udfId\":200175},{\"udfAttributeId\":[301848],\"defaultUdfAttributeId\":null,\"lovAttributeIdList\":null,\"readOnly\":null,\"udfValue\":null,\"udfAttributeText\":[\"IF\"],\"udfId\":200205},{\"udfAttributeId\":[30604],\"defaultUdfAttributeId\":null,\"lovAttributeIdList\":null,\"readOnly\":null,\"udfValue\":null,\"udfAttributeText\":[\"No\"],\"udfId\":200218},{\"udfAttributeId\":[301823],\"defaultUdfAttributeId\":null,\"lovAttributeIdList\":null,\"readOnly\":null,\"udfValue\":null,\"udfAttributeText\":[\"Unprotected\"],\"udfId\":200178},{\"udfAttributeId\":[301890],\"defaultUdfAttributeId\":null,\"lovAttributeIdList\":null,\"readOnly\":null,\"udfValue\":null,\"udfAttributeText\":[\"End-User Customer\"],\"udfId\":200217},{\"udfAttributeId\":[605334],\"defaultUdfAttributeId\":null,\"lovAttributeIdList\":null,\"readOnly\":null,\"udfValue\":null,\"udfAttributeText\":[\"Red\"],\"udfId\":21960},{\"udfAttributeId\":[301476],\"defaultUdfAttributeId\":null,\"lovAttributeIdList\":null,\"readOnly\":null,\"udfValue\":null,\"udfAttributeText\":[\"TRUE\"],\"udfId\":21962},{\"udfAttributeId\":[],\"defaultUdfAttributeId\":null,\"lovAttributeIdList\":null,\"readOnly\":null,\"udfValue\":null,\"udfAttributeText\":[\"1.35\"],\"udfId\":21961},{\"udfAttributeId\":[],\"defaultUdfAttributeId\":null,\"lovAttributeIdList\":null,\"readOnly\":null,\"udfValue\":null,\"udfAttributeText\":[\"A\"],\"udfId\":21033},{\"udfAttributeId\":[],\"defaultUdfAttributeId\":null,\"lovAttributeIdList\":null,\"readOnly\":null,\"udfValue\":null,\"udfAttributeText\":[\"NC\"],\"udfId\":200118},{\"udfAttributeId\":[],\"defaultUdfAttributeId\":null,\"lovAttributeIdList\":null,\"readOnly\":null,\"udfValue\":null,\"udfAttributeText\":[\"Tier 1\"],\"udfId\":1000360},{\"udfAttributeId\":[],\"defaultUdfAttributeId\":null,\"lovAttributeIdList\":null,\"readOnly\":null,\"udfValue\":null,\"udfAttributeText\":[\"GREEN\"],\"udfId\":21036},{\"udfAttributeId\":null,\"defaultUdfAttributeId\":null,\"lovAttributeIdList\":null,\"readOnly\":null,\"udfValue\":null,\"udfAttributeText\":[\"CHCJILLFH00\"],\"udfId\":200160},{\"udfAttributeId\":[],\"defaultUdfAttributeId\":null,\"lovAttributeIdList\":null,\"readOnly\":null,\"udfValue\":null,\"udfAttributeText\":[\"CHCGILLW\"],\"udfId\":200045},{\"udfAttributeId\":[],\"defaultUdfAttributeId\":null,\"lovAttributeIdList\":null,\"readOnly\":null,\"udfValue\":null,\"udfAttributeText\":[\"32358\"],\"udfId\":200192},{\"udfAttributeId\":[301820],\"defaultUdfAttributeId\":null,\"lovAttributeIdList\":null,\"readOnly\":null,\"udfValue\":null,\"udfAttributeText\":[\"Interstate (FCC) Access (Interstate)\"],\"udfId\":200176},{\"udfAttributeId\":[301784],\"defaultUdfAttributeId\":null,\"lovAttributeIdList\":null,\"readOnly\":null,\"udfValue\":null,\"udfAttributeText\":[\"12 Months\"],\"udfId\":200169}]},{\"diversityGroupId\":null,\"componentId\":98414,\"references\":[{\"referenceType\":\"Site\",\"referenceId\":99995150544}],\"logicalChannelPvcID\":null,\"externalKeyRef\":null,\"fromInvYN\":null,\"mvlInd\":null,\"routeTargets\":null,\"externalField\":null,\"parentComponentId\":52655,\"userEnteredVpn\":null,\"componentCodeId\":1220,\"componentCodeType\":\"Endpoint\",\"siteId\":null,\"eteVpnKey\":null,\"logicalChannelId\":null,\"siteObj\":{\"country\":\"US\",\"zipCode\":\"60605\",\"diverseFromReferenceInfo\":null,\"swcCertification\":\"Certified with Caveats\",\"postalCode\":null,\"speedId\":null,\"siteNpanxx\":null,\"lconLastName\":null,\"lataCode\":\"434\",\"siteComment\":null,\"specialConstructionPaymentUrl\":null,\"certificationStatus\":null,\"_endPointRef\":21034,\"building\":null,\"asrItemId\":\"AD3136566\",\"numOfCopperRepeaters\":null,\"fromInventory\":null,\"buildingClli\":null,\"endPointSiteIdentifier\":\"Z\",\"multiGigeIndicator\":null,\"customerReference\":null,\"state\":\"SC\",\"specialConstructionHandlingNotes\":null,\"opticalTerminatingNetworkCarrierFacilityAssignment\":null,\"npanxx\":\"889988\",\"designVersion\":\"1\",\"globalLocationId\":\"00010L70GG\",\"thirdPartyInd\":null,\"saLecSwClli\":null,\"active\":null,\"accessCarrierNameAbbreviation\":null,\"lconDetails\":[{\"lconType\":null,\"lconLastName\":null,\"lconFirstName\":null,\"lconPhone\":null,\"lconEmail\":null}],\"specialConstructionCharge\":null,\"interDepartmentMeetPointChecklistURL\":null,\"inventoryNumOfPairs\":null,\"macdActionType\":null,\"macdActivity\":\"Newstart Circuit\",\"priceDetails\":null,\"designStatus\":\"N\",\"emc\":null,\"customerLocationClli\":\"CHCGILWBHAM\",\"collocationCarrierFacilityAssignment\":null,\"city\":\"CHGO\",\"designSiteOfferPort\":null,\"numberRemoteTerminals\":null,\"jurisdiction\":\"Interstate (FCC) Access (Interstate)\",\"siteName\":\"L2A\",\"nssEngagement\":null,\"taskClli\":null,\"regionCode\":\"Y\",\"independentCarrierCompanyLATA\":null,\"dualSiteId\":null,\"swcClli\":\"CHCGILWB\",\"attComments\":null,\"newBuilding\":null,\"design\":null,\"alias\":\"L2A\",\"floor\":null,\"saLecName\":null,\"lconEmail\":null,\"loopLength\":null,\"independentCarrierCompanyName\":null,\"nxSiteId\":451420,\"product\":null,\"address\":null,\"quantity\":\"1\",\"designModifiedInd\":null,\"referenceOfferId\":null,\"address2\":null,\"cancellationReason\":null,\"address1\":\"315 E ROBINSON ST\",\"assetInvestmentSheetIndicator\":null,\"specialConstructionHandling\":null,\"onNetCheck\":null,\"lconFirstName\":null,\"lconPhone\":null,\"room\":null,\"carrierHotel\":null,\"ethernetVendor\":null,\"siteId\":99995150544,\"popClli\":null,\"macdType\":\"Add\",\"activityType\":\"New Request\"},\"designDetails\":[{\"udfAttributeId\":[30604],\"defaultUdfAttributeId\":null,\"lovAttributeIdList\":null,\"readOnly\":null,\"udfValue\":null,\"udfAttributeText\":[\"Interstate\\/Intrastate USOC\"],\"udfId\":22210},{\"udfAttributeId\":[],\"defaultUdfAttributeId\":null,\"lovAttributeIdList\":null,\"readOnly\":null,\"udfValue\":null,\"udfAttributeText\":[\"02OTF.202\"],\"udfId\":20187},{\"udfAttributeId\":[30604],\"defaultUdfAttributeId\":null,\"lovAttributeIdList\":null,\"readOnly\":null,\"udfValue\":null,\"udfAttributeText\":[\"No\"],\"udfId\":200212},{\"udfAttributeId\":[301794],\"defaultUdfAttributeId\":null,\"lovAttributeIdList\":null,\"readOnly\":null,\"udfValue\":null,\"udfAttributeText\":[\"OTU2 (10 Gbps)\"],\"udfId\":200174},{\"udfAttributeId\":[301845],\"defaultUdfAttributeId\":null,\"lovAttributeIdList\":null,\"readOnly\":null,\"udfValue\":null,\"udfAttributeText\":[\"Y\"],\"udfId\":200216},{\"udfAttributeId\":[304240],\"defaultUdfAttributeId\":null,\"lovAttributeIdList\":null,\"readOnly\":null,\"udfValue\":null,\"udfAttributeText\":[null],\"udfId\":200208},{\"udfAttributeId\":[30604],\"defaultUdfAttributeId\":null,\"lovAttributeIdList\":null,\"readOnly\":null,\"udfValue\":null,\"udfAttributeText\":[\"No\"],\"udfId\":200213},{\"udfAttributeId\":[301833],\"defaultUdfAttributeId\":null,\"lovAttributeIdList\":null,\"readOnly\":null,\"udfValue\":null,\"udfAttributeText\":[\"1310 - SR1 Single Mode Fiber (SMF)\"],\"udfId\":200179},{\"udfAttributeId\":[301806],\"defaultUdfAttributeId\":null,\"lovAttributeIdList\":null,\"readOnly\":null,\"udfValue\":null,\"udfAttributeText\":[\"IR \\/ IF End-User Customer\"],\"udfId\":200175},{\"udfAttributeId\":[301848],\"defaultUdfAttributeId\":null,\"lovAttributeIdList\":null,\"readOnly\":null,\"udfValue\":null,\"udfAttributeText\":[\"IF\"],\"udfId\":200205},{\"udfAttributeId\":[30604],\"defaultUdfAttributeId\":null,\"lovAttributeIdList\":null,\"readOnly\":null,\"udfValue\":null,\"udfAttributeText\":[\"No\"],\"udfId\":200218},{\"udfAttributeId\":[301823],\"defaultUdfAttributeId\":null,\"lovAttributeIdList\":null,\"readOnly\":null,\"udfValue\":null,\"udfAttributeText\":[\"Unprotected\"],\"udfId\":200178},{\"udfAttributeId\":[301890],\"defaultUdfAttributeId\":null,\"lovAttributeIdList\":null,\"readOnly\":null,\"udfValue\":null,\"udfAttributeText\":[\"End-User Customer\"],\"udfId\":200217},{\"udfAttributeId\":[],\"defaultUdfAttributeId\":null,\"lovAttributeIdList\":null,\"readOnly\":null,\"udfValue\":null,\"udfAttributeText\":[\"Z\"],\"udfId\":21034},{\"udfAttributeId\":[605334],\"defaultUdfAttributeId\":null,\"lovAttributeIdList\":null,\"readOnly\":null,\"udfValue\":null,\"udfAttributeText\":[\"Red\"],\"udfId\":21960},{\"udfAttributeId\":[605339],\"defaultUdfAttributeId\":null,\"lovAttributeIdList\":null,\"readOnly\":null,\"udfValue\":null,\"udfAttributeText\":[\"FALSE\"],\"udfId\":21962},{\"udfAttributeId\":[],\"defaultUdfAttributeId\":null,\"lovAttributeIdList\":null,\"readOnly\":null,\"udfValue\":null,\"udfAttributeText\":[\"0.62\"],\"udfId\":21961},{\"udfAttributeId\":[],\"defaultUdfAttributeId\":null,\"lovAttributeIdList\":null,\"readOnly\":null,\"udfValue\":null,\"udfAttributeText\":[\"SC\"],\"udfId\":200118},{\"udfAttributeId\":[],\"defaultUdfAttributeId\":null,\"lovAttributeIdList\":null,\"readOnly\":null,\"udfValue\":null,\"udfAttributeText\":[\"Tier 3\"],\"udfId\":1000360},{\"udfAttributeId\":[],\"defaultUdfAttributeId\":null,\"lovAttributeIdList\":null,\"readOnly\":null,\"udfValue\":null,\"udfAttributeText\":[\"YELLOW\"],\"udfId\":21037},{\"udfAttributeId\":null,\"defaultUdfAttributeId\":null,\"lovAttributeIdList\":null,\"readOnly\":null,\"udfValue\":null,\"udfAttributeText\":[\"CHCGILWBHAM\"],\"udfId\":200160},{\"udfAttributeId\":[],\"defaultUdfAttributeId\":null,\"lovAttributeIdList\":null,\"readOnly\":null,\"udfValue\":null,\"udfAttributeText\":[\"CHCGILWB\"],\"udfId\":200045},{\"udfAttributeId\":[],\"defaultUdfAttributeId\":null,\"lovAttributeIdList\":null,\"readOnly\":null,\"udfValue\":null,\"udfAttributeText\":[\"32358\"],\"udfId\":200192},{\"udfAttributeId\":[301820],\"defaultUdfAttributeId\":null,\"lovAttributeIdList\":null,\"readOnly\":null,\"udfValue\":null,\"udfAttributeText\":[\"Interstate (FCC) Access (Interstate)\"],\"udfId\":200176},{\"udfAttributeId\":[301784],\"defaultUdfAttributeId\":null,\"lovAttributeIdList\":null,\"readOnly\":null,\"udfValue\":null,\"udfAttributeText\":[\"12 Months\"],\"udfId\":200169}]}],\"specialConstructionCharge\":null,\"site\":[{\"country\":\"US\",\"zipCode\":\"60613\",\"diverseFromReferenceInfo\":null,\"swcCertification\":null,\"postalCode\":null,\"speedId\":null,\"siteNpanxx\":null,\"lconLastName\":null,\"lataCode\":\"422\",\"siteComment\":null,\"specialConstructionPaymentUrl\":null,\"certificationStatus\":null,\"_endPointRef\":21033,\"building\":null,\"asrItemId\":null,\"numOfCopperRepeaters\":null,\"fromInventory\":null,\"buildingClli\":null,\"endPointSiteIdentifier\":\"A\",\"multiGigeIndicator\":null,\"customerReference\":null,\"state\":\"NC\",\"specialConstructionHandlingNotes\":null,\"opticalTerminatingNetworkCarrierFacilityAssignment\":null,\"npanxx\":\"889988\",\"designVersion\":null,\"globalLocationId\":null,\"thirdPartyInd\":null,\"saLecSwClli\":null,\"active\":null,\"accessCarrierNameAbbreviation\":null,\"lconDetails\":[{\"lconType\":null,\"lconLastName\":null,\"lconFirstName\":null,\"lconPhone\":null,\"lconEmail\":null}],\"specialConstructionCharge\":null,\"interDepartmentMeetPointChecklistURL\":null,\"inventoryNumOfPairs\":null,\"macdActionType\":null,\"macdActivity\":null,\"priceDetails\":null,\"designStatus\":null,\"emc\":null,\"customerLocationClli\":\"CHCJILLFH00\",\"collocationCarrierFacilityAssignment\":null,\"city\":\"CHGO\",\"designSiteOfferPort\":null,\"numberRemoteTerminals\":null,\"jurisdiction\":null,\"siteName\":\"L2Z\",\"nssEngagement\":null,\"taskClli\":null,\"regionCode\":\"Y\",\"independentCarrierCompanyLATA\":null,\"dualSiteId\":null,\"swcClli\":\"CHCGILLW\",\"attComments\":null,\"newBuilding\":null,\"design\":null,\"alias\":\"L2Z\",\"floor\":null,\"saLecName\":null,\"lconEmail\":null,\"loopLength\":null,\"independentCarrierCompanyName\":null,\"nxSiteId\":451421,\"product\":null,\"address\":null,\"quantity\":null,\"designModifiedInd\":null,\"referenceOfferId\":null,\"address2\":null,\"cancellationReason\":null,\"address1\":\"1946 W IRVING PARK RD\",\"assetInvestmentSheetIndicator\":null,\"specialConstructionHandling\":null,\"onNetCheck\":null,\"lconFirstName\":null,\"lconPhone\":null,\"room\":null,\"carrierHotel\":null,\"ethernetVendor\":null,\"siteId\":99995150542,\"popClli\":null,\"macdType\":null,\"activityType\":null},{\"country\":\"US\",\"zipCode\":\"60605\",\"diverseFromReferenceInfo\":null,\"swcCertification\":\"Certified with Caveats\",\"postalCode\":null,\"speedId\":null,\"siteNpanxx\":null,\"lconLastName\":null,\"lataCode\":\"434\",\"siteComment\":null,\"specialConstructionPaymentUrl\":null,\"certificationStatus\":null,\"_endPointRef\":21034,\"building\":null,\"asrItemId\":\"AD3136566\",\"numOfCopperRepeaters\":null,\"fromInventory\":null,\"buildingClli\":null,\"endPointSiteIdentifier\":\"Z\",\"multiGigeIndicator\":null,\"customerReference\":null,\"state\":\"SC\",\"specialConstructionHandlingNotes\":null,\"opticalTerminatingNetworkCarrierFacilityAssignment\":null,\"npanxx\":\"889988\",\"designVersion\":\"1\",\"globalLocationId\":\"00010L70GG\",\"thirdPartyInd\":null,\"saLecSwClli\":null,\"active\":null,\"accessCarrierNameAbbreviation\":null,\"lconDetails\":[{\"lconType\":null,\"lconLastName\":null,\"lconFirstName\":null,\"lconPhone\":null,\"lconEmail\":null}],\"specialConstructionCharge\":null,\"interDepartmentMeetPointChecklistURL\":null,\"inventoryNumOfPairs\":null,\"macdActionType\":null,\"macdActivity\":\"Newstart Circuit\",\"priceDetails\":null,\"designStatus\":\"N\",\"emc\":null,\"customerLocationClli\":\"CHCGILWBHAM\",\"collocationCarrierFacilityAssignment\":null,\"city\":\"CHGO\",\"designSiteOfferPort\":null,\"numberRemoteTerminals\":null,\"jurisdiction\":\"Interstate (FCC) Access (Interstate)\",\"siteName\":\"L2A\",\"nssEngagement\":null,\"taskClli\":null,\"regionCode\":\"Y\",\"independentCarrierCompanyLATA\":null,\"dualSiteId\":null,\"swcClli\":\"CHCGILWB\",\"attComments\":null,\"newBuilding\":null,\"design\":null,\"alias\":\"L2A\",\"floor\":null,\"saLecName\":null,\"lconEmail\":null,\"loopLength\":null,\"independentCarrierCompanyName\":null,\"nxSiteId\":451420,\"product\":null,\"address\":null,\"quantity\":\"1\",\"designModifiedInd\":null,\"referenceOfferId\":null,\"address2\":null,\"cancellationReason\":null,\"address1\":\"315 E ROBINSON ST\",\"assetInvestmentSheetIndicator\":null,\"specialConstructionHandling\":null,\"onNetCheck\":null,\"lconFirstName\":null,\"lconPhone\":null,\"room\":null,\"carrierHotel\":null,\"ethernetVendor\":null,\"siteId\":99995150544,\"popClli\":null,\"macdType\":\"Add\",\"activityType\":\"New Request\"}],\"designStatus\":\"N\",\"priceDetails\":{\"componentDetails\":[{\"componentType\":\"Circuit\",\"componentId\":52655,\"componentAttributes\":[],\"componentCodeId\":null,\"componentCodeType\":null,\"componentParentId\":2449960,\"priceAttributes\":[{\"country\":null,\"priceScenarioId\":99999832866,\"requestedRate\":14.4,\"ratePlanId\":null,\"discount\":null,\"requestedMRCDiscPercentage\":null,\"localCurrency\":\"USD\",\"frequency\":\"NRC\",\"rdsPriceType\":null,\"localTotalPrice\":60.0,\"requestedNRCRate\":null,\"productRateId\":0,\"targetNetPrice\":60.0,\"requestedNRCDiscPercentage\":null,\"priceUnit\":\"Administrative Charge - Per Order\",\"secondaryKeys\":null,\"targetCurrency\":\"USD\",\"typeOfInventory\":null,\"icbDesiredDiscPerc\":null,\"rateDescription\":null,\"requestedDiscount\":76.0,\"beid\":\"EYXCX-ORCMX\",\"targetTotalPrice\":null,\"componentParentId\":null,\"discountId\":null,\"priceName\":\"Administrative Charge  - NRC\",\"uniqueId\":null,\"priceGroup\":null,\"priceCatalog\":\"Standard Pricing\",\"localNetPrice\":60.0,\"priceInUSD\":\"60\",\"monthlySurcharge\":null,\"targetListPrice\":60.0,\"chargeCodeId\":null,\"priceModifiedInd\":\"Y\",\"nrcBeid\":null,\"term\":12,\"reqPriceType\":null,\"referencePortId\":null,\"componentType\":null,\"quantity\":\"1\",\"priceCompType\":null,\"lineItemId\":null,\"priceType\":\"CIRCUIT\",\"rateGroup\":\"Administrative Charge\",\"externalBillingSystem\":null,\"requestedMRCRate\":null,\"pvcId\":null,\"elementType\":null,\"localListPrice\":60.0},{\"country\":null,\"priceScenarioId\":99999832865,\"requestedRate\":60.0,\"ratePlanId\":null,\"discount\":null,\"requestedMRCDiscPercentage\":null,\"localCurrency\":\"USD\",\"frequency\":\"NRC\",\"rdsPriceType\":null,\"localTotalPrice\":60.0,\"requestedNRCRate\":null,\"productRateId\":0,\"targetNetPrice\":60.0,\"requestedNRCDiscPercentage\":null,\"priceUnit\":\"Administrative Charge - Per Order\",\"secondaryKeys\":null,\"targetCurrency\":\"USD\",\"typeOfInventory\":null,\"icbDesiredDiscPerc\":null,\"rateDescription\":null,\"requestedDiscount\":0.0,\"beid\":\"EYXCX-ORCMX\",\"targetTotalPrice\":null,\"componentParentId\":null,\"discountId\":null,\"priceName\":\"Administrative Charge  - NRC\",\"uniqueId\":null,\"priceGroup\":null,\"priceCatalog\":\"Standard Pricing\",\"localNetPrice\":60.0,\"priceInUSD\":\"60\",\"monthlySurcharge\":null,\"targetListPrice\":60.0,\"chargeCodeId\":null,\"priceModifiedInd\":null,\"nrcBeid\":null,\"term\":12,\"reqPriceType\":null,\"referencePortId\":null,\"componentType\":null,\"quantity\":\"1\",\"priceCompType\":null,\"lineItemId\":null,\"priceType\":\"CIRCUIT\",\"rateGroup\":\"Administrative Charge\",\"externalBillingSystem\":null,\"requestedMRCRate\":null,\"pvcId\":null,\"elementType\":null,\"localListPrice\":60.0},{\"country\":null,\"priceScenarioId\":99999832864,\"requestedRate\":60.0,\"ratePlanId\":null,\"discount\":null,\"requestedMRCDiscPercentage\":null,\"localCurrency\":\"USD\",\"frequency\":\"NRC\",\"rdsPriceType\":null,\"localTotalPrice\":60.0,\"requestedNRCRate\":null,\"productRateId\":0,\"targetNetPrice\":60.0,\"requestedNRCDiscPercentage\":null,\"priceUnit\":\"Administrative Charge - Per Order\",\"secondaryKeys\":null,\"targetCurrency\":\"USD\",\"typeOfInventory\":null,\"icbDesiredDiscPerc\":null,\"rateDescription\":null,\"requestedDiscount\":0.0,\"beid\":\"EYXCX-ORCMX\",\"targetTotalPrice\":null,\"componentParentId\":null,\"discountId\":null,\"priceName\":\"Administrative Charge  - NRC\",\"uniqueId\":null,\"priceGroup\":null,\"priceCatalog\":\"Standard Pricing\",\"localNetPrice\":60.0,\"priceInUSD\":\"60\",\"monthlySurcharge\":null,\"targetListPrice\":60.0,\"chargeCodeId\":null,\"priceModifiedInd\":null,\"nrcBeid\":null,\"term\":12,\"reqPriceType\":null,\"referencePortId\":null,\"componentType\":null,\"quantity\":\"1\",\"priceCompType\":null,\"lineItemId\":null,\"priceType\":\"CIRCUIT\",\"rateGroup\":\"Administrative Charge\",\"externalBillingSystem\":null,\"requestedMRCRate\":null,\"pvcId\":null,\"elementType\":null,\"localListPrice\":60.0},{\"country\":null,\"priceScenarioId\":99999832866,\"requestedRate\":144.0,\"ratePlanId\":null,\"discount\":null,\"requestedMRCDiscPercentage\":null,\"localCurrency\":\"USD\",\"frequency\":\"NRC\",\"rdsPriceType\":null,\"localTotalPrice\":600.0,\"requestedNRCRate\":null,\"productRateId\":0,\"targetNetPrice\":600.0,\"requestedNRCDiscPercentage\":null,\"priceUnit\":\"Design Central Office Connection Charge - Per Circuit\",\"secondaryKeys\":null,\"targetCurrency\":\"USD\",\"typeOfInventory\":null,\"icbDesiredDiscPerc\":null,\"rateDescription\":null,\"requestedDiscount\":76.0,\"beid\":\"EYXCX-NRBCL\",\"targetTotalPrice\":null,\"componentParentId\":null,\"discountId\":null,\"priceName\":\"Design Central Office Connection Charge  - NRC\",\"uniqueId\":null,\"priceGroup\":null,\"priceCatalog\":\"Standard Pricing\",\"localNetPrice\":600.0,\"priceInUSD\":\"600\",\"monthlySurcharge\":null,\"targetListPrice\":600.0,\"chargeCodeId\":null,\"priceModifiedInd\":\"Y\",\"nrcBeid\":null,\"term\":12,\"reqPriceType\":null,\"referencePortId\":null,\"componentType\":null,\"quantity\":\"1\",\"priceCompType\":null,\"lineItemId\":null,\"priceType\":\"CIRCUIT\",\"rateGroup\":\"Design Central Office Connection Charge\",\"externalBillingSystem\":null,\"requestedMRCRate\":null,\"pvcId\":null,\"elementType\":null,\"localListPrice\":600.0},{\"country\":null,\"priceScenarioId\":99999832865,\"requestedRate\":0.0,\"ratePlanId\":null,\"discount\":null,\"requestedMRCDiscPercentage\":null,\"localCurrency\":\"USD\",\"frequency\":\"NRC\",\"rdsPriceType\":null,\"localTotalPrice\":600.0,\"requestedNRCRate\":null,\"productRateId\":0,\"targetNetPrice\":600.0,\"requestedNRCDiscPercentage\":null,\"priceUnit\":\"Design Central Office Connection Charge - Per Circuit\",\"secondaryKeys\":null,\"targetCurrency\":\"USD\",\"typeOfInventory\":null,\"icbDesiredDiscPerc\":null,\"rateDescription\":null,\"requestedDiscount\":0.0,\"beid\":\"EYXCX-NRBCL\",\"targetTotalPrice\":null,\"componentParentId\":null,\"discountId\":null,\"priceName\":\"Design Central Office Connection Charge  - NRC\",\"uniqueId\":null,\"priceGroup\":null,\"priceCatalog\":\"Standard Pricing\",\"localNetPrice\":600.0,\"priceInUSD\":\"600\",\"monthlySurcharge\":null,\"targetListPrice\":600.0,\"chargeCodeId\":null,\"priceModifiedInd\":null,\"nrcBeid\":null,\"term\":12,\"reqPriceType\":null,\"referencePortId\":null,\"componentType\":null,\"quantity\":\"1\",\"priceCompType\":null,\"lineItemId\":null,\"priceType\":\"CIRCUIT\",\"rateGroup\":\"Design Central Office Connection Charge\",\"externalBillingSystem\":null,\"requestedMRCRate\":null,\"pvcId\":null,\"elementType\":null,\"localListPrice\":600.0},{\"country\":null,\"priceScenarioId\":99999832864,\"requestedRate\":0.0,\"ratePlanId\":null,\"discount\":null,\"requestedMRCDiscPercentage\":null,\"localCurrency\":\"USD\",\"frequency\":\"NRC\",\"rdsPriceType\":null,\"localTotalPrice\":600.0,\"requestedNRCRate\":null,\"productRateId\":0,\"targetNetPrice\":600.0,\"requestedNRCDiscPercentage\":null,\"priceUnit\":\"Design Central Office Connection Charge - Per Circuit\",\"secondaryKeys\":null,\"targetCurrency\":\"USD\",\"typeOfInventory\":null,\"icbDesiredDiscPerc\":null,\"rateDescription\":null,\"requestedDiscount\":0.0,\"beid\":\"EYXCX-NRBCL\",\"targetTotalPrice\":null,\"componentParentId\":null,\"discountId\":null,\"priceName\":\"Design Central Office Connection Charge  - NRC\",\"uniqueId\":null,\"priceGroup\":null,\"priceCatalog\":\"Standard Pricing\",\"localNetPrice\":600.0,\"priceInUSD\":\"600\",\"monthlySurcharge\":null,\"targetListPrice\":600.0,\"chargeCodeId\":null,\"priceModifiedInd\":null,\"nrcBeid\":null,\"term\":12,\"reqPriceType\":null,\"referencePortId\":null,\"componentType\":null,\"quantity\":\"1\",\"priceCompType\":null,\"lineItemId\":null,\"priceType\":\"CIRCUIT\",\"rateGroup\":\"Design Central Office Connection Charge\",\"externalBillingSystem\":null,\"requestedMRCRate\":null,\"pvcId\":null,\"elementType\":null,\"localListPrice\":600.0}],\"scpPriceMessages\":null},{\"componentType\":\"Endpoint\",\"componentId\":98413,\"componentAttributes\":[],\"componentCodeId\":null,\"componentCodeType\":null,\"componentParentId\":52655,\"priceAttributes\":[{\"country\":null,\"priceScenarioId\":99999832866,\"requestedRate\":360.0,\"ratePlanId\":null,\"discount\":null,\"requestedMRCDiscPercentage\":null,\"localCurrency\":\"USD\",\"frequency\":\"NRC\",\"rdsPriceType\":null,\"localTotalPrice\":1500.0,\"requestedNRCRate\":null,\"productRateId\":0,\"targetNetPrice\":1500.0,\"requestedNRCDiscPercentage\":null,\"priceUnit\":\"Customer Connection Charge\",\"secondaryKeys\":null,\"targetCurrency\":\"USD\",\"typeOfInventory\":null,\"icbDesiredDiscPerc\":null,\"rateDescription\":null,\"requestedDiscount\":76.0,\"beid\":\"EYXCX-NRBBL\",\"targetTotalPrice\":null,\"componentParentId\":null,\"discountId\":null,\"priceName\":\"Customer Connection Charge  - NRC\",\"uniqueId\":null,\"priceGroup\":null,\"priceCatalog\":\"Standard Pricing\",\"localNetPrice\":1500.0,\"priceInUSD\":\"1500\",\"monthlySurcharge\":null,\"targetListPrice\":1500.0,\"chargeCodeId\":null,\"priceModifiedInd\":\"Y\",\"nrcBeid\":null,\"term\":12,\"reqPriceType\":null,\"referencePortId\":null,\"componentType\":null,\"quantity\":\"1\",\"priceCompType\":null,\"lineItemId\":null,\"priceType\":\"PORT_CONNECTION\",\"rateGroup\":\"Customer Connection Charge\",\"externalBillingSystem\":null,\"requestedMRCRate\":null,\"pvcId\":null,\"elementType\":null,\"localListPrice\":1500.0},{\"country\":null,\"priceScenarioId\":99999832865,\"requestedRate\":0.0,\"ratePlanId\":null,\"discount\":null,\"requestedMRCDiscPercentage\":null,\"localCurrency\":\"USD\",\"frequency\":\"NRC\",\"rdsPriceType\":null,\"localTotalPrice\":1500.0,\"requestedNRCRate\":null,\"productRateId\":0,\"targetNetPrice\":1500.0,\"requestedNRCDiscPercentage\":null,\"priceUnit\":\"Customer Connection Charge\",\"secondaryKeys\":null,\"targetCurrency\":\"USD\",\"typeOfInventory\":null,\"icbDesiredDiscPerc\":null,\"rateDescription\":null,\"requestedDiscount\":0.0,\"beid\":\"EYXCX-NRBBL\",\"targetTotalPrice\":null,\"componentParentId\":null,\"discountId\":null,\"priceName\":\"Customer Connection Charge  - NRC\",\"uniqueId\":null,\"priceGroup\":null,\"priceCatalog\":\"Standard Pricing\",\"localNetPrice\":1500.0,\"priceInUSD\":\"1500\",\"monthlySurcharge\":null,\"targetListPrice\":1500.0,\"chargeCodeId\":null,\"priceModifiedInd\":null,\"nrcBeid\":null,\"term\":12,\"reqPriceType\":null,\"referencePortId\":null,\"componentType\":null,\"quantity\":\"1\",\"priceCompType\":null,\"lineItemId\":null,\"priceType\":\"PORT_CONNECTION\",\"rateGroup\":\"Customer Connection Charge\",\"externalBillingSystem\":null,\"requestedMRCRate\":null,\"pvcId\":null,\"elementType\":null,\"localListPrice\":1500.0},{\"country\":null,\"priceScenarioId\":99999832864,\"requestedRate\":0.0,\"ratePlanId\":null,\"discount\":null,\"requestedMRCDiscPercentage\":null,\"localCurrency\":\"USD\",\"frequency\":\"NRC\",\"rdsPriceType\":null,\"localTotalPrice\":1500.0,\"requestedNRCRate\":null,\"productRateId\":0,\"targetNetPrice\":1500.0,\"requestedNRCDiscPercentage\":null,\"priceUnit\":\"Customer Connection Charge\",\"secondaryKeys\":null,\"targetCurrency\":\"USD\",\"typeOfInventory\":null,\"icbDesiredDiscPerc\":null,\"rateDescription\":null,\"requestedDiscount\":0.0,\"beid\":\"EYXCX-NRBBL\",\"targetTotalPrice\":null,\"componentParentId\":null,\"discountId\":null,\"priceName\":\"Customer Connection Charge  - NRC\",\"uniqueId\":null,\"priceGroup\":null,\"priceCatalog\":\"Standard Pricing\",\"localNetPrice\":1500.0,\"priceInUSD\":\"1500\",\"monthlySurcharge\":null,\"targetListPrice\":1500.0,\"chargeCodeId\":null,\"priceModifiedInd\":null,\"nrcBeid\":null,\"term\":12,\"reqPriceType\":null,\"referencePortId\":null,\"componentType\":null,\"quantity\":\"1\",\"priceCompType\":null,\"lineItemId\":null,\"priceType\":\"PORT_CONNECTION\",\"rateGroup\":\"Customer Connection Charge\",\"externalBillingSystem\":null,\"requestedMRCRate\":null,\"pvcId\":null,\"elementType\":null,\"localListPrice\":1500.0},{\"country\":null,\"priceScenarioId\":99999832866,\"requestedRate\":258.5,\"ratePlanId\":null,\"discount\":null,\"requestedMRCDiscPercentage\":null,\"localCurrency\":\"USD\",\"frequency\":\"MRC\",\"rdsPriceType\":null,\"localTotalPrice\":12925.0,\"requestedNRCRate\":null,\"productRateId\":0,\"targetNetPrice\":12925.0,\"requestedNRCDiscPercentage\":null,\"priceUnit\":\"Port Connection\",\"secondaryKeys\":null,\"targetCurrency\":\"USD\",\"typeOfInventory\":null,\"icbDesiredDiscPerc\":null,\"rateDescription\":null,\"requestedDiscount\":98.0,\"beid\":\"EYXCX-EYFOX\",\"targetTotalPrice\":null,\"componentParentId\":null,\"discountId\":null,\"priceName\":\"Port Connection Type - OTU2 \\/ OTU2e (10Gbps) - 12M\",\"uniqueId\":null,\"priceGroup\":null,\"priceCatalog\":\"Standard Pricing\",\"localNetPrice\":12925.0,\"priceInUSD\":\"12925\",\"monthlySurcharge\":null,\"targetListPrice\":12925.0,\"chargeCodeId\":null,\"priceModifiedInd\":\"Y\",\"nrcBeid\":null,\"term\":12,\"reqPriceType\":null,\"referencePortId\":null,\"componentType\":null,\"quantity\":\"1\",\"priceCompType\":null,\"lineItemId\":null,\"priceType\":\"PORT_CONNECTION\",\"rateGroup\":\"Port Connection\",\"externalBillingSystem\":null,\"requestedMRCRate\":null,\"pvcId\":null,\"elementType\":null,\"localListPrice\":12925.0},{\"country\":null,\"priceScenarioId\":99999832865,\"requestedRate\":0.0,\"ratePlanId\":null,\"discount\":null,\"requestedMRCDiscPercentage\":null,\"localCurrency\":\"USD\",\"frequency\":\"MRC\",\"rdsPriceType\":null,\"localTotalPrice\":12925.0,\"requestedNRCRate\":null,\"productRateId\":0,\"targetNetPrice\":12925.0,\"requestedNRCDiscPercentage\":null,\"priceUnit\":\"Port Connection\",\"secondaryKeys\":null,\"targetCurrency\":\"USD\",\"typeOfInventory\":null,\"icbDesiredDiscPerc\":null,\"rateDescription\":null,\"requestedDiscount\":0.0,\"beid\":\"EYXCX-EYFOX\",\"targetTotalPrice\":null,\"componentParentId\":null,\"discountId\":null,\"priceName\":\"Port Connection Type - OTU2 \\/ OTU2e (10Gbps) - 12M\",\"uniqueId\":null,\"priceGroup\":null,\"priceCatalog\":\"Standard Pricing\",\"localNetPrice\":12925.0,\"priceInUSD\":\"12925\",\"monthlySurcharge\":null,\"targetListPrice\":12925.0,\"chargeCodeId\":null,\"priceModifiedInd\":null,\"nrcBeid\":null,\"term\":12,\"reqPriceType\":null,\"referencePortId\":null,\"componentType\":null,\"quantity\":\"1\",\"priceCompType\":null,\"lineItemId\":null,\"priceType\":\"PORT_CONNECTION\",\"rateGroup\":\"Port Connection\",\"externalBillingSystem\":null,\"requestedMRCRate\":null,\"pvcId\":null,\"elementType\":null,\"localListPrice\":12925.0},{\"country\":null,\"priceScenarioId\":99999832864,\"requestedRate\":0.0,\"ratePlanId\":null,\"discount\":null,\"requestedMRCDiscPercentage\":null,\"localCurrency\":\"USD\",\"frequency\":\"MRC\",\"rdsPriceType\":null,\"localTotalPrice\":12925.0,\"requestedNRCRate\":null,\"productRateId\":0,\"targetNetPrice\":12925.0,\"requestedNRCDiscPercentage\":null,\"priceUnit\":\"Port Connection\",\"secondaryKeys\":null,\"targetCurrency\":\"USD\",\"typeOfInventory\":null,\"icbDesiredDiscPerc\":null,\"rateDescription\":null,\"requestedDiscount\":0.0,\"beid\":\"EYXCX-EYFOX\",\"targetTotalPrice\":null,\"componentParentId\":null,\"discountId\":null,\"priceName\":\"Port Connection Type - OTU2 \\/ OTU2e (10Gbps) - 12M\",\"uniqueId\":null,\"priceGroup\":null,\"priceCatalog\":\"Standard Pricing\",\"localNetPrice\":12925.0,\"priceInUSD\":\"12925\",\"monthlySurcharge\":null,\"targetListPrice\":12925.0,\"chargeCodeId\":null,\"priceModifiedInd\":null,\"nrcBeid\":null,\"term\":12,\"reqPriceType\":null,\"referencePortId\":null,\"componentType\":null,\"quantity\":\"1\",\"priceCompType\":null,\"lineItemId\":null,\"priceType\":\"PORT_CONNECTION\",\"rateGroup\":\"Port Connection\",\"externalBillingSystem\":null,\"requestedMRCRate\":null,\"pvcId\":null,\"elementType\":null,\"localListPrice\":12925.0}],\"scpPriceMessages\":null},{\"componentType\":\"Endpoint\",\"componentId\":98414,\"componentAttributes\":[],\"componentCodeId\":null,\"componentCodeType\":null,\"componentParentId\":52655,\"priceAttributes\":[{\"country\":null,\"priceScenarioId\":99999832866,\"requestedRate\":360.0,\"ratePlanId\":null,\"discount\":null,\"requestedMRCDiscPercentage\":null,\"localCurrency\":\"USD\",\"frequency\":\"NRC\",\"rdsPriceType\":null,\"localTotalPrice\":1500.0,\"requestedNRCRate\":null,\"productRateId\":0,\"targetNetPrice\":1500.0,\"requestedNRCDiscPercentage\":null,\"priceUnit\":\"Customer Connection Charge\",\"secondaryKeys\":null,\"targetCurrency\":\"USD\",\"typeOfInventory\":null,\"icbDesiredDiscPerc\":null,\"rateDescription\":null,\"requestedDiscount\":76.0,\"beid\":\"EYXCX-NRBBL\",\"targetTotalPrice\":null,\"componentParentId\":null,\"discountId\":null,\"priceName\":\"Customer Connection Charge  - NRC\",\"uniqueId\":null,\"priceGroup\":null,\"priceCatalog\":\"Standard Pricing\",\"localNetPrice\":1500.0,\"priceInUSD\":\"1500\",\"monthlySurcharge\":null,\"targetListPrice\":1500.0,\"chargeCodeId\":null,\"priceModifiedInd\":\"Y\",\"nrcBeid\":null,\"term\":12,\"reqPriceType\":null,\"referencePortId\":null,\"componentType\":null,\"quantity\":\"1\",\"priceCompType\":null,\"lineItemId\":null,\"priceType\":\"PORT_CONNECTION\",\"rateGroup\":\"Customer Connection Charge\",\"externalBillingSystem\":null,\"requestedMRCRate\":null,\"pvcId\":null,\"elementType\":null,\"localListPrice\":1500.0},{\"country\":null,\"priceScenarioId\":99999832865,\"requestedRate\":0.0,\"ratePlanId\":null,\"discount\":null,\"requestedMRCDiscPercentage\":null,\"localCurrency\":\"USD\",\"frequency\":\"NRC\",\"rdsPriceType\":null,\"localTotalPrice\":1500.0,\"requestedNRCRate\":null,\"productRateId\":0,\"targetNetPrice\":1500.0,\"requestedNRCDiscPercentage\":null,\"priceUnit\":\"Customer Connection Charge\",\"secondaryKeys\":null,\"targetCurrency\":\"USD\",\"typeOfInventory\":null,\"icbDesiredDiscPerc\":null,\"rateDescription\":null,\"requestedDiscount\":0.0,\"beid\":\"EYXCX-NRBBL\",\"targetTotalPrice\":null,\"componentParentId\":null,\"discountId\":null,\"priceName\":\"Customer Connection Charge  - NRC\",\"uniqueId\":null,\"priceGroup\":null,\"priceCatalog\":\"Standard Pricing\",\"localNetPrice\":1500.0,\"priceInUSD\":\"1500\",\"monthlySurcharge\":null,\"targetListPrice\":1500.0,\"chargeCodeId\":null,\"priceModifiedInd\":null,\"nrcBeid\":null,\"term\":12,\"reqPriceType\":null,\"referencePortId\":null,\"componentType\":null,\"quantity\":\"1\",\"priceCompType\":null,\"lineItemId\":null,\"priceType\":\"PORT_CONNECTION\",\"rateGroup\":\"Customer Connection Charge\",\"externalBillingSystem\":null,\"requestedMRCRate\":null,\"pvcId\":null,\"elementType\":null,\"localListPrice\":1500.0},{\"country\":null,\"priceScenarioId\":99999832864,\"requestedRate\":0.0,\"ratePlanId\":null,\"discount\":null,\"requestedMRCDiscPercentage\":null,\"localCurrency\":\"USD\",\"frequency\":\"NRC\",\"rdsPriceType\":null,\"localTotalPrice\":1500.0,\"requestedNRCRate\":null,\"productRateId\":0,\"targetNetPrice\":1500.0,\"requestedNRCDiscPercentage\":null,\"priceUnit\":\"Customer Connection Charge\",\"secondaryKeys\":null,\"targetCurrency\":\"USD\",\"typeOfInventory\":null,\"icbDesiredDiscPerc\":null,\"rateDescription\":null,\"requestedDiscount\":0.0,\"beid\":\"EYXCX-NRBBL\",\"targetTotalPrice\":null,\"componentParentId\":null,\"discountId\":null,\"priceName\":\"Customer Connection Charge  - NRC\",\"uniqueId\":null,\"priceGroup\":null,\"priceCatalog\":\"Standard Pricing\",\"localNetPrice\":1500.0,\"priceInUSD\":\"1500\",\"monthlySurcharge\":null,\"targetListPrice\":1500.0,\"chargeCodeId\":null,\"priceModifiedInd\":null,\"nrcBeid\":null,\"term\":12,\"reqPriceType\":null,\"referencePortId\":null,\"componentType\":null,\"quantity\":\"1\",\"priceCompType\":null,\"lineItemId\":null,\"priceType\":\"PORT_CONNECTION\",\"rateGroup\":\"Customer Connection Charge\",\"externalBillingSystem\":null,\"requestedMRCRate\":null,\"pvcId\":null,\"elementType\":null,\"localListPrice\":1500.0},{\"country\":null,\"priceScenarioId\":99999832866,\"requestedRate\":258.5,\"ratePlanId\":null,\"discount\":null,\"requestedMRCDiscPercentage\":null,\"localCurrency\":\"USD\",\"frequency\":\"MRC\",\"rdsPriceType\":null,\"localTotalPrice\":12925.0,\"requestedNRCRate\":null,\"productRateId\":0,\"targetNetPrice\":12925.0,\"requestedNRCDiscPercentage\":null,\"priceUnit\":\"Port Connection\",\"secondaryKeys\":null,\"targetCurrency\":\"USD\",\"typeOfInventory\":null,\"icbDesiredDiscPerc\":null,\"rateDescription\":null,\"requestedDiscount\":98.0,\"beid\":\"EYXCX-EYFOX\",\"targetTotalPrice\":null,\"componentParentId\":null,\"discountId\":null,\"priceName\":\"Port Connection Type - OTU2 \\/ OTU2e (10Gbps) - 12M\",\"uniqueId\":null,\"priceGroup\":null,\"priceCatalog\":\"Standard Pricing\",\"localNetPrice\":12925.0,\"priceInUSD\":\"12925\",\"monthlySurcharge\":null,\"targetListPrice\":12925.0,\"chargeCodeId\":null,\"priceModifiedInd\":\"Y\",\"nrcBeid\":null,\"term\":12,\"reqPriceType\":null,\"referencePortId\":null,\"componentType\":null,\"quantity\":\"1\",\"priceCompType\":null,\"lineItemId\":null,\"priceType\":\"PORT_CONNECTION\",\"rateGroup\":\"Port Connection\",\"externalBillingSystem\":null,\"requestedMRCRate\":null,\"pvcId\":null,\"elementType\":null,\"localListPrice\":12925.0},{\"country\":null,\"priceScenarioId\":99999832865,\"requestedRate\":0.0,\"ratePlanId\":null,\"discount\":null,\"requestedMRCDiscPercentage\":null,\"localCurrency\":\"USD\",\"frequency\":\"MRC\",\"rdsPriceType\":null,\"localTotalPrice\":12925.0,\"requestedNRCRate\":null,\"productRateId\":0,\"targetNetPrice\":12925.0,\"requestedNRCDiscPercentage\":null,\"priceUnit\":\"Port Connection\",\"secondaryKeys\":null,\"targetCurrency\":\"USD\",\"typeOfInventory\":null,\"icbDesiredDiscPerc\":null,\"rateDescription\":null,\"requestedDiscount\":0.0,\"beid\":\"EYXCX-EYFOX\",\"targetTotalPrice\":null,\"componentParentId\":null,\"discountId\":null,\"priceName\":\"Port Connection Type - OTU2 \\/ OTU2e (10Gbps) - 12M\",\"uniqueId\":null,\"priceGroup\":null,\"priceCatalog\":\"Standard Pricing\",\"localNetPrice\":12925.0,\"priceInUSD\":\"12925\",\"monthlySurcharge\":null,\"targetListPrice\":12925.0,\"chargeCodeId\":null,\"priceModifiedInd\":null,\"nrcBeid\":null,\"term\":12,\"reqPriceType\":null,\"referencePortId\":null,\"componentType\":null,\"quantity\":\"1\",\"priceCompType\":null,\"lineItemId\":null,\"priceType\":\"PORT_CONNECTION\",\"rateGroup\":\"Port Connection\",\"externalBillingSystem\":null,\"requestedMRCRate\":null,\"pvcId\":null,\"elementType\":null,\"localListPrice\":12925.0},{\"country\":null,\"priceScenarioId\":99999832864,\"requestedRate\":0.0,\"ratePlanId\":null,\"discount\":null,\"requestedMRCDiscPercentage\":null,\"localCurrency\":\"USD\",\"frequency\":\"MRC\",\"rdsPriceType\":null,\"localTotalPrice\":12925.0,\"requestedNRCRate\":null,\"productRateId\":0,\"targetNetPrice\":12925.0,\"requestedNRCDiscPercentage\":null,\"priceUnit\":\"Port Connection\",\"secondaryKeys\":null,\"targetCurrency\":\"USD\",\"typeOfInventory\":null,\"icbDesiredDiscPerc\":null,\"rateDescription\":null,\"requestedDiscount\":0.0,\"beid\":\"EYXCX-EYFOX\",\"targetTotalPrice\":null,\"componentParentId\":null,\"discountId\":null,\"priceName\":\"Port Connection Type - OTU2 \\/ OTU2e (10Gbps) - 12M\",\"uniqueId\":null,\"priceGroup\":null,\"priceCatalog\":\"Standard Pricing\",\"localNetPrice\":12925.0,\"priceInUSD\":\"12925\",\"monthlySurcharge\":null,\"targetListPrice\":12925.0,\"chargeCodeId\":null,\"priceModifiedInd\":null,\"nrcBeid\":null,\"term\":12,\"reqPriceType\":null,\"referencePortId\":null,\"componentType\":null,\"quantity\":\"1\",\"priceCompType\":null,\"lineItemId\":null,\"priceType\":\"PORT_CONNECTION\",\"rateGroup\":\"Port Connection\",\"externalBillingSystem\":null,\"requestedMRCRate\":null,\"pvcId\":null,\"elementType\":null,\"localListPrice\":12925.0}],\"scpPriceMessages\":null}],\"priceMessage\":null},\"purchaseOrderNumber\":null,"
				+ "\"icsc\":null,\"specialConstructionHandlingNotes\":null}");
		Map<String,Object> inputMap = new HashMap();
		inputMap.put(TDDConstants.SOLUTION_LEVEL_STATUS, "C");
		inputMap.put(TDDConstants.SOLUTION_LEVEL_CANCEL_REASON,"Business logic");
		NxDesignAudit nxDesignAudit=new NxDesignAudit();
		nxDesignAudit.setData("{\"solution\":{\"solutionDeterminants\":{\"icbInd\":null,\"solutionType\":\"NS\",\"mvlInd\":null},\"bulkInd\":\"N\",\"bundleCode\":\"ADE\",\"offers\":[{\"site\":[],\"offerId\":\"120\"}],\"leadDesignID\":8887778,\"marketStrata\":\"Retail\",\"cancellationReason\":null,\"pricerDSolutionId\":8887778,\"automationInd\":\"N\",\"erateInd\":\"N\",\"layer\":\"Retail\",\"solutionStatus\":\"N\"},\"actionDeterminants"
				+ "\":[{\"component\":[\"Design\",\"Price\",\"ADE\"],\"activity\":\"UpdateDesign\"}]}");
		when(nxDesignAuditRepository.findByNxRefIdAndTransaction(any(),any())).thenReturn(nxDesignAudit);
		
		transmitDesignDataService.createDppRequestForADE(dbSolutionDtls,nxDesignDetails,inputMap);

	}
	
	@Test
	public void testCreateDppRequestForASE() {
		ReflectionTestUtils.setField(transmitDesignDataService, "isDppTriggerASE", "Y");
		NxSolutionDetail dbSolutionDtls=new NxSolutionDetail() ;
		dbSolutionDtls.setNxSolutionId(1L);
		NxDesignDetails nxDesignDetails= new NxDesignDetails();
		nxDesignDetails.setDesignData("{\"nxSiteId\":451430,\"siteId\":99988935477,\"npanxx\":\"310827\",\"address1\":\"5353 GROSVENOR\",\"city\":\"ORLANDO\",\"state\":\"FL\",\"saLecName\":null,\"speedId\":null,\"saLecSwClli\":null,\"popClli\":null,\"country\":null,\"macdType\":\"Change\",\"macdActivity\":\"Change CIR UNI;Change Port Connection Speed;\",\"fromInventory\":null,\"dualSiteId\":null,\"onNetCheck\":null,\"ethernetVendor\":null,\"designSiteOfferPort\":[{\"designStatus\":null,\"typeOfInventory\":\"To\",\"milesResult\":null,\"securityDesignDetails\":null,\"macdActivityType\":null,\"component\":[{\"componentCodeId\":10,\"fromInvYN\":null,\"logicalChannelPvcID\":null,\"eteVpnKey\":null,\"diversityGroupId\":null,\"componentCodeType\":\"Connection\",\"componentId\":9278459,\"externalField\":null,\"parentComponentId\":null,\"externalKeyRef\":null,\"mvlInd\":null,\"userEnteredVpn\":null,\"siteId\":null,\"logicalChannelId\":null,\"designDetails\":[{\"udfAttributeId\":[30595],\"udfId\":20030,\"readOnly\":null,\"udfAttributeText\":[\"Primary\"],\"defaultUdfAttributeId\":null,\"lovAttributeIdList\":null,\"udfValue\":null},{\"udfAttributeId\":[30515],\"udfId\":20017,\"readOnly\":null,\"udfAttributeText\":[\"AVPN\"],\"defaultUdfAttributeId\":null,\"lovAttributeIdList\":null,\"udfValue\":null}],\"routeTargets\":null,\"references\":null,\"siteObj\":null},{\"componentCodeId\":30,\"fromInvYN\":null,\"logicalChannelPvcID\":null,\"eteVpnKey\":null,\"diversityGroupId\":null,\"componentCodeType\":\"Port\",\"componentId\":null,\"externalField\":null,\"parentComponentId\":null,\"externalKeyRef\":null,\"mvlInd\":null,\"userEnteredVpn\":null,\"siteId\":null,\"logicalChannelId\":null,\"designDetails\":[{\"udfAttributeId\":[30604],\"udfId\":22019,\"readOnly\":null,\"udfAttributeText\":[\"N\"],\"defaultUdfAttributeId\":null,\"lovAttributeIdList\":null,\"udfValue\":null},{\"udfAttributeId\":[30604],\"udfId\":22020,\"readOnly\":null,\"udfAttributeText\":[\"N\"],\"defaultUdfAttributeId\":null,\"lovAttributeIdList\":null,\"udfValue\":null},{\"udfAttributeId\":[30604],\"udfId\":22021,\"readOnly\":null,\"udfAttributeText\":[\"N\"],\"defaultUdfAttributeId\":null,\"lovAttributeIdList\":null,\"udfValue\":null},{\"udfAttributeId\":[30604],\"udfId\":200075,\"readOnly\":null,\"udfAttributeText\":[\"N\"],\"defaultUdfAttributeId\":null,\"lovAttributeIdList\":null,\"udfValue\":null},{\"udfAttributeId\":[30604],\"udfId\":200079,\"readOnly\":null,\"udfAttributeText\":[\"N\"],\"defaultUdfAttributeId\":null,\"lovAttributeIdList\":null,\"udfValue\":null},{\"udfAttributeId\":[300302],\"udfId\":20013,\"readOnly\":null,\"udfAttributeText\":[\"Real-Time (Profile 5)\"],\"defaultUdfAttributeId\":null,\"lovAttributeIdList\":null,\"udfValue\":null},{\"udfAttributeId\":null,\"udfId\":200007,\"readOnly\":null,\"udfAttributeText\":[\"15/KRFN/117212/FL\"],\"defaultUdfAttributeId\":null,\"lovAttributeIdList\":null,\"udfValue\":null},{\"udfAttributeId\":[300248],\"udfId\":200006,\"readOnly\":null,\"udfAttributeText\":[\"LC/VLAN-level\"],\"defaultUdfAttributeId\":null,\"lovAttributeIdList\":null,\"udfValue\":null},{\"udfAttributeId\":[301507],\"udfId\":200134,\"readOnly\":null,\"udfAttributeText\":[\"9STATES\"],\"defaultUdfAttributeId\":null,\"lovAttributeIdList\":null,\"udfValue\":null},{\"udfAttributeId\":[58219],\"udfId\":200133,\"readOnly\":null,\"udfAttributeText\":[\"5000 Mb\"],\"defaultUdfAttributeId\":null,\"lovAttributeIdList\":null,\"udfValue\":null},{\"udfAttributeId\":[30605],\"udfId\":200011,\"readOnly\":null,\"udfAttributeText\":[\"Yes\"],\"defaultUdfAttributeId\":null,\"lovAttributeIdList\":null,\"udfValue\":null},{\"udfAttributeId\":[605334],\"udfId\":21960,\"readOnly\":null,\"udfAttributeText\":[\"Red\"],\"defaultUdfAttributeId\":null,\"lovAttributeIdList\":null,\"udfValue\":null},{\"udfAttributeId\":null,\"udfId\":20169,\"readOnly\":null,\"udfAttributeText\":[\"R00002035890\"],\"defaultUdfAttributeId\":null,\"lovAttributeIdList\":null,\"udfValue\":null},{\"udfAttributeId\":[300260,300258],\"udfId\":200010,\"readOnly\":null,\"udfAttributeText\":[\"Change Port Connection Speed\"],\"defaultUdfAttributeId\":null,\"lovAttributeIdList\":null,\"udfValue\":null},{\"udfAttributeId\":null,\"udfId\":21961,\"readOnly\":null,\"udfAttributeText\":[\"99\"],\"defaultUdfAttributeId\":null,\"lovAttributeIdList\":null,\"udfValue\":null},{\"udfAttributeId\":[30604],\"udfId\":200009,\"readOnly\":null,\"udfAttributeText\":[\"N\"],\"defaultUdfAttributeId\":null,\"lovAttributeIdList\":null,\"udfValue\":null},{\"udfAttributeId\":[605339],\"udfId\":21962,\"readOnly\":null,\"udfAttributeText\":[\"FALSE\"],\"defaultUdfAttributeId\":null,\"lovAttributeIdList\":null,\"udfValue\":null},{\"udfAttributeId\":[301473],\"udfId\":20171,\"readOnly\":null,\"udfAttributeText\":[\"Fiber\"],\"defaultUdfAttributeId\":null,\"lovAttributeIdList\":null,\"udfValue\":null},{\"udfAttributeId\":[300251],\"udfId\":200008,\"readOnly\":null,\"udfAttributeText\":[\"Change\"],\"defaultUdfAttributeId\":null,\"lovAttributeIdList\":null,\"udfValue\":null},{\"udfAttributeId\":[300273],\"udfId\":200015,\"readOnly\":null,\"udfAttributeText\":[\"IN-SERVICE\"],\"defaultUdfAttributeId\":null,\"lovAttributeIdList\":null,\"udfValue\":null},{\"udfAttributeId\":[301577],\"udfId\":200143,\"readOnly\":null,\"udfAttributeText\":[\"KSE5\"],\"defaultUdfAttributeId\":null,\"lovAttributeIdList\":null,\"udfValue\":null},{\"udfAttributeId\":[301475],\"udfId\":20173,\"readOnly\":null,\"udfAttributeText\":[\"SGOS\"],\"defaultUdfAttributeId\":null,\"lovAttributeIdList\":null,\"udfValue\":null},{\"udfAttributeId\":null,\"udfId\":200014,\"readOnly\":null,\"udfAttributeText\":[\"2020-09-25 00:00:00.0\"],\"defaultUdfAttributeId\":null,\"lovAttributeIdList\":null,\"udfValue\":null},{\"udfAttributeId\":[30605],\"udfId\":200013,\"readOnly\":null,\"udfAttributeText\":[\"Yes\"],\"defaultUdfAttributeId\":null,\"lovAttributeIdList\":null,\"udfValue\":null},{\"udfAttributeId\":[30605],\"udfId\":200012,\"readOnly\":null,\"udfAttributeText\":[\"Yes\"],\"defaultUdfAttributeId\":null,\"lovAttributeIdList\":null,\"udfValue\":null},{\"udfAttributeId\":[300277],\"udfId\":200018,\"readOnly\":null,\"udfAttributeText\":[\"Existing\"],\"defaultUdfAttributeId\":null,\"lovAttributeIdList\":null,\"udfValue\":null},{\"udfAttributeId\":null,\"udfId\":200017,\"readOnly\":null,\"udfAttributeText\":[\"Recap\"],\"defaultUdfAttributeId\":null,\"lovAttributeIdList\":null,\"udfValue\":null},{\"udfAttributeId\":[604699],\"udfId\":200145,\"readOnly\":null,\"udfAttributeText\":[\"COMPLEX_MACDIII\"],\"defaultUdfAttributeId\":null,\"lovAttributeIdList\":null,\"udfValue\":null},{\"udfAttributeId\":[301475],\"udfId\":200022,\"readOnly\":null,\"udfAttributeText\":[\"SGOS\"],\"defaultUdfAttributeId\":null,\"lovAttributeIdList\":null,\"udfValue\":null},{\"udfAttributeId\":[300295],\"udfId\":200021,\"readOnly\":null,\"udfAttributeText\":[\"10 Gb\"],\"defaultUdfAttributeId\":null,\"lovAttributeIdList\":null,\"udfValue\":null},{\"udfAttributeId\":[300284],\"udfId\":200020,\"readOnly\":null,\"udfAttributeText\":[\"IR/IF End-User Customer\"],\"defaultUdfAttributeId\":null,\"lovAttributeIdList\":null,\"udfValue\":null},{\"udfAttributeId\":null,\"udfId\":20184,\"readOnly\":null,\"udfAttributeText\":[\"ORLDFLMA\"],\"defaultUdfAttributeId\":null,\"lovAttributeIdList\":null,\"udfValue\":null},{\"udfAttributeId\":[30604],\"udfId\":200027,\"readOnly\":null,\"udfAttributeText\":[\"N\"],\"defaultUdfAttributeId\":null,\"lovAttributeIdList\":null,\"udfValue\":null},{\"udfAttributeId\":null,\"udfId\":20187,\"readOnly\":null,\"udfAttributeText\":[\"02LNF.A02\"],\"defaultUdfAttributeId\":null,\"lovAttributeIdList\":null,\"udfValue\":null},{\"udfAttributeId\":[74933],\"udfId\":200031,\"readOnly\":null,\"udfAttributeText\":[\"250 Mb\"],\"defaultUdfAttributeId\":null,\"lovAttributeIdList\":null,\"udfValue\":null},{\"udfAttributeId\":[300367],\"udfId\":200035,\"readOnly\":null,\"udfAttributeText\":[\"Inside\"],\"defaultUdfAttributeId\":null,\"lovAttributeIdList\":null,\"udfValue\":null},{\"udfAttributeId\":[300365],\"udfId\":200033,\"readOnly\":null,\"udfAttributeText\":[\"10 G\"],\"defaultUdfAttributeId\":null,\"lovAttributeIdList\":null,\"udfValue\":null},{\"udfAttributeId\":null,\"udfId\":200032,\"readOnly\":null,\"udfAttributeText\":[\"0\"],\"defaultUdfAttributeId\":null,\"lovAttributeIdList\":null,\"udfValue\":null},{\"udfAttributeId\":[302216],\"udfId\":21860,\"readOnly\":null,\"udfAttributeText\":[\"Certified\"],\"defaultUdfAttributeId\":null,\"lovAttributeIdList\":null,\"udfValue\":null},{\"udfAttributeId\":[302216],\"udfId\":21861,\"readOnly\":null,\"udfAttributeText\":[\"Certified\"],\"defaultUdfAttributeId\":null,\"lovAttributeIdList\":null,\"udfValue\":null},{\"udfAttributeId\":[300373],\"udfId\":200037,\"readOnly\":null,\"udfAttributeText\":[\"AC Single\"],\"defaultUdfAttributeId\":null,\"lovAttributeIdList\":null,\"udfValue\":null},{\"udfAttributeId\":[55065],\"udfId\":21862,\"readOnly\":null,\"udfAttributeText\":[\"N\"],\"defaultUdfAttributeId\":null,\"lovAttributeIdList\":null,\"udfValue\":null},{\"udfAttributeId\":[300371],\"udfId\":200036,\"readOnly\":null,\"udfAttributeText\":[\"Wall\"],\"defaultUdfAttributeId\":null,\"lovAttributeIdList\":null,\"udfValue\":null},{\"udfAttributeId\":[300377],\"udfId\":200040,\"readOnly\":null,\"udfAttributeText\":[\"02LNF.A02, 10G Base LR SMF Optical\"],\"defaultUdfAttributeId\":null,\"lovAttributeIdList\":null,\"udfValue\":null},{\"udfAttributeId\":null,\"udfId\":200047,\"readOnly\":null,\"udfAttributeText\":[\"10\"],\"defaultUdfAttributeId\":null,\"lovAttributeIdList\":null,\"udfValue\":null},{\"udfAttributeId\":null,\"udfId\":200046,\"readOnly\":null,\"udfAttributeText\":[\"10\"],\"defaultUdfAttributeId\":null,\"lovAttributeIdList\":null,\"udfValue\":null},{\"udfAttributeId\":null,\"udfId\":200045,\"readOnly\":null,\"udfAttributeText\":[\"ORLDFLMA\"],\"defaultUdfAttributeId\":null,\"lovAttributeIdList\":null,\"udfValue\":null},{\"udfAttributeId\":[30604],\"udfId\":200048,\"readOnly\":null,\"udfAttributeText\":[\"N\"],\"defaultUdfAttributeId\":null,\"lovAttributeIdList\":null,\"udfValue\":null},{\"udfAttributeId\":[55064],\"udfId\":200059,\"readOnly\":null,\"udfAttributeText\":[\"Y\"],\"defaultUdfAttributeId\":null,\"lovAttributeIdList\":null,\"udfValue\":null},{\"udfAttributeId\":[300401],\"udfId\":200058,\"readOnly\":null,\"udfAttributeText\":[\"IF\"],\"defaultUdfAttributeId\":null,\"lovAttributeIdList\":null,\"udfValue\":null},{\"udfAttributeId\":[30604],\"udfId\":200060,\"readOnly\":null,\"udfAttributeText\":[\"N\"],\"defaultUdfAttributeId\":null,\"lovAttributeIdList\":null,\"udfValue\":null}],\"routeTargets\":null,\"references\":null,\"siteObj\":null}],\"routerDetails\":null,\"logicalChannelDetail\":null,\"customDesign\":null,\"accessPricingAQ\":null,\"aggregateBilling\":null,\"portValidationMessage\":null,\"voiceFeatureDetail\":null}],\"priceDetails\":{\"componentDetails\":[{\"componentCodeId\":null,\"componentCodeType\":null,\"componentId\":99988935477,\"componentType\":\"Site\",\"componentParentId\":6902072,\"componentAttributes\":[{\"componentFieldName\":\"Country\",\"componentFieldValue\":\"US\"}],\"priceAttributes\":[],\"scpPriceMessages\":null},{\"componentCodeId\":null,\"componentCodeType\":null,\"componentId\":9278459,\"componentType\":\"Port\",\"componentParentId\":99988935477,\"componentAttributes\":[{\"componentFieldName\":\"Country\",\"componentFieldValue\":\"US\"}],\"priceAttributes\":[{\"productRateId\":0,\"beid\":\"OEMXG\",\"rateDescription\":null,\"priceCatalog\":\"Standard Pricing\",\"localListPrice\":8000.0,\"targetListPrice\":8000.0,\"icbDesiredDiscPerc\":null,\"priceType\":\"PORT\",\"priceUnit\":\"Month\",\"frequency\":\"MRC\",\"monthlySurcharge\":null,\"discount\":null,\"discountId\":null,\"quantity\":\"1\",\"localNetPrice\":8000.0,\"targetNetPrice\":8000.0,\"localTotalPrice\":8000.0,\"targetTotalPrice\":null,\"localCurrency\":\"USD\",\"targetCurrency\":\"USD\",\"rdsPriceType\":\"Port\",\"priceName\":\"OEM_Port - 10 Gb - 12\",\"typeOfInventory\":null,\"priceInUSD\":\"8000\",\"priceScenarioId\":99999904706,\"rateGroup\":\"OEM_Port\",\"externalBillingSystem\":null,\"ratePlanId\":null,\"priceCompType\":\"ASE\",\"pvcId\":null,\"chargeCodeId\":null,\"requestedNRCRate\":null,\"requestedNRCDiscPercentage\":null,\"requestedMRCRate\":null,\"requestedMRCDiscPercentage\":null,\"priceModifiedInd\":null,\"requestedDiscount\":null,\"requestedRate\":null,\"term\":null,\"priceGroup\":null,\"referencePortId\":null,\"componentType\":null,\"componentParentId\":null,\"country\":null,\"secondaryKeys\":null,\"lineItemId\":null,\"nrcBeid\":null,\"uniqueId\":null,\"elementType\":null,\"reqPriceType\":null},{\"productRateId\":0,\"beid\":\"OEMXG\",\"rateDescription\":null,\"priceCatalog\":\"Standard Pricing\",\"localListPrice\":15750.0,\"targetListPrice\":15750.0,\"icbDesiredDiscPerc\":null,\"priceType\":\"PORT\",\"priceUnit\":\"Month\",\"frequency\":\"NRC\",\"monthlySurcharge\":null,\"discount\":null,\"discountId\":null,\"quantity\":\"1\",\"localNetPrice\":15750.0,\"targetNetPrice\":15750.0,\"localTotalPrice\":15750.0,\"targetTotalPrice\":null,\"localCurrency\":\"USD\",\"targetCurrency\":\"USD\",\"rdsPriceType\":\"Port\",\"priceName\":\"OEM_Port - 10 Gb - NRC\",\"typeOfInventory\":null,\"priceInUSD\":\"15750\",\"priceScenarioId\":99999904706,\"rateGroup\":\"OEM_Port\",\"externalBillingSystem\":null,\"ratePlanId\":null,\"priceCompType\":\"ASE\",\"pvcId\":null,\"chargeCodeId\":null,\"requestedNRCRate\":null,\"requestedNRCDiscPercentage\":null,\"requestedMRCRate\":null,\"requestedMRCDiscPercentage\":null,\"priceModifiedInd\":null,\"requestedDiscount\":null,\"requestedRate\":null,\"term\":null,\"priceGroup\":null,\"referencePortId\":null,\"componentType\":null,\"componentParentId\":null,\"country\":null,\"secondaryKeys\":null,\"lineItemId\":null,\"nrcBeid\":null,\"uniqueId\":null,\"elementType\":null,\"reqPriceType\":null},{\"productRateId\":0,\"beid\":\"OEM5T\",\"rateDescription\":null,\"priceCatalog\":\"Standard Pricing\",\"localListPrice\":150.0,\"targetListPrice\":150.0,\"icbDesiredDiscPerc\":null,\"priceType\":\"COS\",\"priceUnit\":\"Month\",\"frequency\":\"NRC\",\"monthlySurcharge\":null,\"discount\":null,\"discountId\":null,\"quantity\":\"1\",\"localNetPrice\":150.0,\"targetNetPrice\":150.0,\"localTotalPrice\":150.0,\"targetTotalPrice\":null,\"localCurrency\":\"USD\",\"targetCurrency\":\"USD\",\"rdsPriceType\":\"Cos\",\"priceName\":\"OEM_RealTime - 5000 Mb - NRC\",\"typeOfInventory\":null,\"priceInUSD\":\"150\",\"priceScenarioId\":99999904706,\"rateGroup\":\"OEM_RealTime\",\"externalBillingSystem\":null,\"ratePlanId\":null,\"priceCompType\":\"ASE\",\"pvcId\":null,\"chargeCodeId\":null,\"requestedNRCRate\":null,\"requestedNRCDiscPercentage\":null,\"requestedMRCRate\":null,\"requestedMRCDiscPercentage\":null,\"priceModifiedInd\":null,\"requestedDiscount\":null,\"requestedRate\":null,\"term\":null,\"priceGroup\":null,\"referencePortId\":null,\"componentType\":null,\"componentParentId\":null,\"country\":null,\"secondaryKeys\":null,\"lineItemId\":null,\"nrcBeid\":null,\"uniqueId\":null,\"elementType\":null,\"reqPriceType\":null},{\"productRateId\":0,\"beid\":\"OEM5T\",\"rateDescription\":null,\"priceCatalog\":\"Standard Pricing\",\"localListPrice\":9487.0,\"targetListPrice\":9487.0,\"icbDesiredDiscPerc\":null,\"priceType\":\"COS\",\"priceUnit\":\"Month\",\"frequency\":\"MRC\",\"monthlySurcharge\":null,\"discount\":null,\"discountId\":null,\"quantity\":\"1\",\"localNetPrice\":9487.0,\"targetNetPrice\":9487.0,\"localTotalPrice\":9487.0,\"targetTotalPrice\":null,\"localCurrency\":\"USD\",\"targetCurrency\":\"USD\",\"rdsPriceType\":\"Cos\",\"priceName\":\"OEM_RealTime - 5000 Mb - 12\",\"typeOfInventory\":null,\"priceInUSD\":\"9487\",\"priceScenarioId\":99999904706,\"rateGroup\":\"OEM_RealTime\",\"externalBillingSystem\":null,\"ratePlanId\":null,\"priceCompType\":\"ASE\",\"pvcId\":null,\"chargeCodeId\":null,\"requestedNRCRate\":null,\"requestedNRCDiscPercentage\":null,\"requestedMRCRate\":null,\"requestedMRCDiscPercentage\":null,\"priceModifiedInd\":null,\"requestedDiscount\":null,\"requestedRate\":null,\"term\":null,\"priceGroup\":null,\"referencePortId\":null,\"componentType\":null,\"componentParentId\":null,\"country\":null,\"secondaryKeys\":null,\"lineItemId\":null,\"nrcBeid\":null,\"uniqueId\":null,\"elementType\":null,\"reqPriceType\":null}],\"scpPriceMessages\":null}],\"priceMessage\":null},\"address\":null,\"postalCode\":null,\"siteName\":\"loc1\",\"swcClli\":null,\"customerLocationClli\":null,\"active\":null,\"emc\":null,\"carrierHotel\":null,\"attComments\":null,\"newBuilding\":null,\"customerReference\":null,\"asrItemId\":\"R00002035890\",\"lataCode\":\"458\",\"zipCode\":\"36740\",\"address2\":null,\"room\":null,\"floor\":null,\"building\":null,\"siteComment\":null,\"lconFirstName\":null,\"lconLastName\":null,\"lconPhone\":null,\"lconEmail\":null,\"endPointSiteIdentifier\":\"A\",\"buildingClli\":null,\"regionCode\":\"Y\",\"activityType\":null,\"cancellationReason\":null,\"product\":null,\"quantity\":\"1\",\"nssEngagement\":null,\"designStatus\":\"N\",\"multiGigeIndicator\":null,\"alias\":\"loc1\",\"macdActionType\":null,\"lconDetails\":[{\"lconType\":null,\"lconFirstName\":null,\"lconLastName\":null,\"lconPhone\":null,\"lconEmail\":null}],\"globalLocationId\":null,\"jurisdiction\":\"FCC\",\"certificationStatus\":null,\"designModifiedInd\":null,\"independentCarrierCompanyLATA\":null,\"independentCarrierCompanyName\":null,\"interDepartmentMeetPointChecklistURL\":null,\"diverseFromReferenceInfo\":null,\"opticalTerminatingNetworkCarrierFacilityAssignment\":null,\"collocationCarrierFacilityAssignment\":null,\"assetInvestmentSheetIndicator\":null,\"swcCertification\":\"Certified\",\"designVersion\":1,\"specialConstructionPaymentUrl\":null,\"thirdPartyInd\":null,\"accessCarrierNameAbbreviation\":null,\"design\":null,\"loopLength\":null,\"numOfCopperRepeaters\":null,\"inventoryNumOfPairs\":null,\"taskClli\":null,\"numberRemoteTerminals\":null,\"referenceOfferId\":null,\"siteNpanxx\":null,\"specialConstructionCharge\":null,"
				+ "\"specialConstructionHandling\":null,\"specialConstructionHandlingNotes\":null,\"_endPointRef\":null}");
		Map<String,Object> inputMap = new HashMap<>();
		NxDesignAudit nxDesignAudit=new NxDesignAudit();
		nxDesignAudit.setData("{\"solution\":{\"solutionDeterminants\":{\"icbInd\":null,\"solutionType\":\"NS\",\"mvlInd\":null},\"bulkInd\":\"N\",\"bundleCode\":\"ASE\",\"offers\":[{\"site\":[],\"offerId\":\"103\"}],\"leadDesignID\":6902072,\"marketStrata\":\"Retail\",\"cancellationReason\":null,\"pricerDSolutionId\":6902072,\"automationInd\":\"N\",\"erateInd\":\"N\",\"layer\":\"Retail\",\"solutionStatus\":\"N\"},\"actionDeterminants\":[{\"component\":[\"Design\",\"Price\",\"ASE\"],"
				+ "\"activity\":\"UpdateDesign\"}]}");
		transmitDesignDataService.createDppRequestForASE(dbSolutionDtls,nxDesignDetails, inputMap) ;
	}

	public void getSolutionDetailByIdTest() {
		when(tddRepositoryService.findByExternalKey(any())).thenReturn(getSolutionDtlsLstForASE());
		transmitDesignDataService.getSolutionDetailById(1l);
	}
	
	@Test
	public void getDesignDetailsByAsrIdTest() {
		transmitDesignDataService.getDesignDetailsByAsrId(getSolutionDtlsLstForADE().get(0).getNxDesign(), "AD78789");
	}
	
}

