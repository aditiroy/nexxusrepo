package com.att.salesmarketing.soma.pric.nexxus.unittest.service;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import org.apache.commons.collections.CollectionUtils;
//import org.apache.cxf.jaxrs.ext.multipart.MultipartBody;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.env.Environment;

import com.att.sales.framework.exception.SalesBusinessException;
import com.att.sales.framework.model.ServiceMetaData;
import com.att.sales.framework.model.ServiceResponse;
import com.att.sales.nexxus.accesspricing.model.AccessPricingResponse;
import com.att.sales.nexxus.accesspricing.service.AccessPricingServiceImpl;
import com.att.sales.nexxus.admin.model.UploadEthTokenRequest;
import com.att.sales.nexxus.common.CommonConstants;
import com.att.sales.nexxus.constant.StringConstants;
import com.att.sales.nexxus.dao.model.NexxusSolutionDetailUIModel;
import com.att.sales.nexxus.dao.model.NxAccessPricingData;
import com.att.sales.nexxus.dao.model.NxDesignAudit;
import com.att.sales.nexxus.dao.model.NxLookupData;
import com.att.sales.nexxus.dao.model.NxUser;
import com.att.sales.nexxus.dao.model.solution.NxSolutionDetail;
import com.att.sales.nexxus.dao.model.solution.NxTeam;
import com.att.sales.nexxus.dao.repository.NxAccessPricingDataRepository;
import com.att.sales.nexxus.dao.repository.NxDesignAuditRepository;
import com.att.sales.nexxus.dao.repository.NxMpDealRepository;
import com.att.sales.nexxus.dao.repository.NxRequestDetailsRepository;
import com.att.sales.nexxus.dao.repository.NxSolutionDetailsRepository;
import com.att.sales.nexxus.dao.repository.NxTeamRepository;
import com.att.sales.nexxus.dao.repository.NxUserRepository;
import com.att.sales.nexxus.handlers.GetOptyInfoWSHandler;
import com.att.sales.nexxus.helper.FileReaderHelper;
import com.att.sales.nexxus.inr.OutputJsonFallOutData;
import com.att.sales.nexxus.model.APUiResponse;
import com.att.sales.nexxus.model.AccessPricingUiRequest;
import com.att.sales.nexxus.model.AccessPricingUiResponse;
import com.att.sales.nexxus.model.AccessSupplierList;
import com.att.sales.nexxus.model.AccessSupplierObject;
import com.att.sales.nexxus.model.GUIResponse;
import com.att.sales.nexxus.model.NodeObj;
import com.att.sales.nexxus.model.NodeObjectList;
import com.att.sales.nexxus.model.ProductDataLoadRequest;
import com.att.sales.nexxus.model.QueoteRequestList;
import com.att.sales.nexxus.model.QuoteDetails;
import com.att.sales.nexxus.model.QuoteRequest;
import com.att.sales.nexxus.model.QuoteResponse;
import com.att.sales.nexxus.output.entity.NxMisBean;
import com.att.sales.nexxus.output.entity.NxOutputBean;
import com.att.sales.nexxus.rome.model.GetOptyResponse;
import com.att.sales.nexxus.rome.service.GetOptyInfoServiceImpl;
import com.att.sales.nexxus.service.AccessPricingService;
import com.att.sales.nexxus.service.NxMyPriceRepositoryServce;
import com.att.sales.nexxus.userdetails.service.UserDetailsServiceImpl;
import com.att.sales.nexxus.userdetails.service.UserServiceImpl;
import com.att.sales.nexxus.util.DME2RestClient;
import com.att.sales.nexxus.util.JacksonUtil;
import com.att.sales.util.StringUtils;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;

@ExtendWith(MockitoExtension.class)
public class AccessPricingServiceImplTest {
	
	@InjectMocks
	AccessPricingServiceImpl service=new AccessPricingServiceImpl();
	
	@Mock
	ProductDataLoadRequest productDataLoadRequest;

	//@Mock
	//MultipartBody multipart;

	@Mock
	DME2RestClient dme;
	@Mock
	ServiceResponse response;
	@Mock
	NxSolutionDetailsRepository solutionRepo;

	@Mock
	CollectionUtils utils;
	@Mock
	StringUtils stringUtils;
	@Mock
	GetOptyResponse optyResponse;

	@Mock
	Environment env;
	@Mock
	EntityManager em;

	@Mock
	GetOptyInfoWSHandler getOptyInfoWSHandler;;
	@Mock
	GetOptyInfoServiceImpl optyInfoServiceImpl;

	@Mock
	TypedQuery<NexxusSolutionDetailUIModel> typedQueryNexxusSolutionDetailUIModel;

	@Mock
	NxAccessPricingDataRepository repository;

	@Mock
	GUIResponse quoteDetails;
	@Mock
	AccessPricingResponse iglooResp;

	@Mock
	APUiResponse apResp;

	@Mock
	FileReaderHelper fileReaderHelper;

	@Mock
	NxRequestDetailsRepository repo;

	@Mock
	JsonNode intermediateJson;

	@Mock
	AccessPricingService accessPricingService;
	
	@Mock
	UserDetailsServiceImpl adoptUserService;

	@Mock
	NxDesignAuditRepository nxDesignAuditRepository;
	
	@Mock
	NxTeamRepository nxTeamRepository;
	
	@Mock
	NxMpDealRepository nxMpDealRepository;
	
	@Mock
	JsonNode jsonNode;
	
	@Mock
	private UserServiceImpl userServiceImpl;

	@Mock
	private NxUserRepository nxUserRepository;
	
	private static Map<String, NxLookupData> nxLookupDataMap;
	
	@Mock
	private NxMyPriceRepositoryServce nxMyPriceRepositoryServce;
	
	@BeforeAll
	public static void init() {
		Map<String, Object> map = new HashMap<>();
		map.put(ServiceMetaData.OFFER, "2");
		map.put(ServiceMetaData.VERSION, "v2");
		map.put(ServiceMetaData.METHOD, "post");
		map.put(ServiceMetaData.URI, "hghg");
		map.put(ServiceMetaData.REST_REQUEST_START_TIME, System.currentTimeMillis());
		map.put(ServiceMetaData.SERVICE_FILTER, "AVPN");
		map.put(ServiceMetaData.SERVICEID, "SERVICEID");
		map.put(CommonConstants.FILENAME, "FILE_1");
		map.put(ServiceMetaData.XTRANSACTIONID, "transactionId");

		ServiceMetaData.add(map);
		
		nxLookupDataMap = new HashMap<String, NxLookupData> ();
		nxLookupDataMap.put("AVPN", new NxLookupData());

	}

	@Test	
	public void testGetAccessPricing() throws SalesBusinessException {
		AccessPricingUiRequest request=new AccessPricingUiRequest();
		request.setAction("save");
		request.setCountry("US");
		String s1="ETH7865";
		List<String> dqid=new ArrayList<>();
		dqid.add(s1);
		request.setDqId(dqid);
		request.setNxSolutionId(new Long(1234));  
		request.setQueryType("GET");
		QuoteDetails quoteDetails=new QuoteDetails();
		quoteDetails.setApId(new Long(12));
		quoteDetails.setIncludeYn("Y");
		quoteDetails.setLocationYn("New");
		List<QuoteDetails> quoteList=new ArrayList<>();
		quoteList.add(quoteDetails);
		request.setQuoteDetails(quoteList);
		request.setRequestType("13");
		/*List<QuoteResponse> list=new ArrayList<>();
		Mockito.when(iglooResp.getQuoteResponse()).thenReturn(list);*/
		service.getAccessPricing(request);
	}
	
	@Test	
	public void testGetAccessPricingAdd() throws SalesBusinessException, JsonParseException, JsonMappingException, IOException {
		AccessPricingUiRequest request=new AccessPricingUiRequest();
		request.setAction("saveIglooQuote");
		request.setCountry("US");
		String s1="ETH7865";
		List<String> dqid=new ArrayList<>();
		dqid.add(s1);
		request.setDqId(dqid);
		request.setNxSolutionId(new Long(1234));
		request.setQueryType("GET");
		QuoteDetails quoteDetails=new QuoteDetails();
		quoteDetails.setApId(new Long(12));
		quoteDetails.setIncludeYn("Y");
		quoteDetails.setLocationYn("New");
		List<QuoteDetails> quoteList=new ArrayList<>();
		quoteList.add(quoteDetails);
		request.setQuoteDetails(quoteList);
		request.setRequestType("13");
		List<QuoteResponse> list=new ArrayList<>();
		QuoteRequest quoteRequest=new QuoteRequest();
		QueoteRequestList requestList=new QueoteRequestList();
		requestList.setCountry("US");
		requestList.setDqId("dqId");
		requestList.setQueryType("GET");
		requestList.setRequestType("13");
		List<QueoteRequestList> queoteRequestLists=new ArrayList<>();
		queoteRequestLists.add(requestList);
		quoteRequest.setQuoteRequest(queoteRequestLists);
	
		//Mockito.when(dme.getPricingAccess(quoteRequest)).thenReturn(resposeWrapper);
		Mockito.when(iglooResp.getQuoteResponse()).thenReturn(list);
		try {
		service.getAccessPricing(request);
		}catch(Exception e) {
			
		}
	}
		
		@Test	
	public void testGetAccessPricingRetrive() throws SalesBusinessException {
		AccessPricingUiRequest request=new AccessPricingUiRequest();
		request.setAction("retrieveIglooQuote");
		request.setCountry("US");
		String s1="ETH7865";
		List<String> dqid=new ArrayList<>();
		dqid.add(s1);
		request.setDqId(dqid);
		request.setNxSolutionId(new Long(1234));
		request.setQueryType("GET");
		QuoteDetails quoteDetails=new QuoteDetails();
		quoteDetails.setApId(new Long(12));
		quoteDetails.setIncludeYn("Y");
		quoteDetails.setLocationYn("New");
		List<QuoteDetails> quoteList=new ArrayList<>();
		quoteList.add(quoteDetails);
		request.setQuoteDetails(quoteList);
		request.setRequestType("13");
		List<QuoteResponse> list=new ArrayList<>();
		Mockito.when(iglooResp.getQuoteResponse()).thenReturn(list);
		service.getAccessPricing(request);
	}
	@Test	
	public void testGetAccessPricingUpdate() throws SalesBusinessException {
		AccessPricingUiRequest request=new AccessPricingUiRequest();
		request.setAction("updateIglooQuote");
		request.setCountry("US");
		String s1="ETH7865";
		List<String> dqid=new ArrayList<>();
		dqid.add(s1);
		request.setDqId(dqid);
		request.setNxSolutionId(new Long(1234));
		request.setQueryType("GET");
		QuoteDetails quoteDetails=new QuoteDetails();
		quoteDetails.setApId(new Long(12));
		quoteDetails.setIncludeYn("Y");
		quoteDetails.setLocationYn("New");
		List<QuoteDetails> quoteList=new ArrayList<>();
		quoteList.add(quoteDetails);
		request.setQuoteDetails(quoteList);
		request.setRequestType("13");
		NxAccessPricingData nxAccessPricingData=new NxAccessPricingData();
		nxAccessPricingData.setEthToken("ethToken");
		nxAccessPricingData.setIglooQuoteId("38.987676");
		nxAccessPricingData.setIncludeYn("Y");
		nxAccessPricingData.setLocationYn("New");
		nxAccessPricingData.setMrc("1000");
		nxAccessPricingData.setNrc("888");
		nxAccessPricingData.setAccessType("access");
		nxAccessPricingData.setNxAccessPriceId(new Long(2l));
		nxAccessPricingData.setReqContractTerm("contractTerm");
		nxAccessPricingData.setReqCountry("US");
		nxAccessPricingData.setLocationYn("New");
		nxAccessPricingData.setIncludeYn("Y");
		nxAccessPricingData.setIntermediateJson("{\"reqStreetAddress\":\"100 N FRANKLIN ST\",\"reqCity\":\"MOBILE\",\"reqZipCode\":\"36602\",\"country\":\"United States\",\"state\":\"AL\",\"service\":\"AVPN-ETH\",\"accessArch\":\"access\",\"mrc\":\"535.80\",\"discountMonthlyRecurringPrice\":\"535.80\",\"nonRecurringCharge\":\"850.00\",\"ethToken\":\"ETH200F9YB\",\"flowType\":\"IGL\"}");

		//nxAccessPricingData.add(pricingData);
		List<QuoteResponse> list=new ArrayList<>();
		NxSolutionDetail solution=new NxSolutionDetail();
		solution.setNxSolutionId(new Long(2l));
		List<NxSolutionDetail> solutionlist=new ArrayList<>();
		solutionlist.add(solution);
		Mockito.when(iglooResp.getQuoteResponse()).thenReturn(list);
		Mockito.when(repository.getNxAccessPricingData(Mockito.anyLong())).thenReturn(nxAccessPricingData);
		Mockito.when(solutionRepo.findByNxSolutionId(Mockito.anyLong())).thenReturn(solutionlist.get(0));
		Mockito.when(nxMpDealRepository.getCountryCodeByCountryIsoCode(Mockito.anyString())).thenReturn("US");
//		service.getAccessPricing(request);
	}
	
	@Test	
	public void testGetAccessPricingDelete() throws SalesBusinessException {
		AccessPricingUiRequest request=new AccessPricingUiRequest();
		request.setAction("deleteIglooQuote");
		request.setCountry("US");
		String s1="ETH7865";
		List<String> dqid=new ArrayList<>();
		dqid.add(s1);
		request.setDqId(dqid);
		request.setNxSolutionId(new Long(1234));
		request.setQueryType("GET");
		QuoteDetails quoteDetails=new QuoteDetails();
		quoteDetails.setApId(new Long(12));
		quoteDetails.setIncludeYn("Y");
		quoteDetails.setLocationYn("New");
		List<QuoteDetails> quoteList=new ArrayList<>();
		quoteList.add(quoteDetails);
		request.setQuoteDetails(quoteList);
		request.setRequestType("13");
		List<NxAccessPricingData> pricingDataList=new ArrayList<>();
		NxAccessPricingData nxAccessPricingData=new NxAccessPricingData();
		nxAccessPricingData.setEthToken("ethToken");
		nxAccessPricingData.setIglooQuoteId("38.987676");
		nxAccessPricingData.setIncludeYn("Y");
		nxAccessPricingData.setLocationYn("New");
		nxAccessPricingData.setMrc("10000.898986543");
		nxAccessPricingData.setNrc("10000.898986543");
		nxAccessPricingData.setAccessType("access");
		nxAccessPricingData.setNxAccessPriceId(new Long(2l));
		nxAccessPricingData.setReqContractTerm("contractTerm");
		nxAccessPricingData.setReqCountry("US");
		nxAccessPricingData.setLocationYn("New");
		nxAccessPricingData.setIncludeYn("Y");
		pricingDataList.add(nxAccessPricingData);
		NxSolutionDetail solution=new NxSolutionDetail();
		solution.setNxSolutionId(new Long(2l));
		List<NxSolutionDetail> solutionlist=new ArrayList<>();
		solutionlist.add(solution);
		Mockito.when(repository.findByNxSolutionId(Mockito.anyLong())).thenReturn(pricingDataList);
		//nxAccessPricingData.add(pricingData);
		List<QuoteResponse> list=new ArrayList<>();
		Mockito.when(iglooResp.getQuoteResponse()).thenReturn(list);
		Mockito.when(solutionRepo.findByNxSolutionId(Mockito.anyLong())).thenReturn(solutionlist.get(0));
		//Mockito.when(repository.getNxAccessPricingData(Mockito.anyLong())).thenReturn(nxAccessPricingData);
		//Mockito.when(service.roundOff("10000.898986543")).thenReturn(Mockito.anyObject());
		
//		service.getAccessPricing(request);
	}
	
	@Test
	public void testSaveDataNewSolution() throws SalesBusinessException {
		APUiResponse response=new APUiResponse();
		NxSolutionDetail nxSolutionDetail = new NxSolutionDetail();
		String description = StringConstants.FLOW_TYPE_IGLOO_QUOTE +"_"+LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
		nxSolutionDetail.setFlowType(StringConstants.FLOW_TYPE_IGLOO_QUOTE);
		nxSolutionDetail.setCreatedUser("ec006e");
		nxSolutionDetail.setActiveYn("Y");
		nxSolutionDetail.setNxsDescription(description);
		Mockito.when(solutionRepo.save(nxSolutionDetail)).thenReturn(nxSolutionDetail);
		AccessPricingUiRequest request=new AccessPricingUiRequest();
		request.setNxSolutionId(nxSolutionDetail.getNxSolutionId());
		List<String> listDqid=new ArrayList<>();
		String s1="dqId1";
		listDqid.add(s1);
		request.setDqId(listDqid);
		QuoteResponse quoteResponse=new QuoteResponse();
		GUIResponse quoteDetails=new GUIResponse();
		quoteDetails.setCurrency("DKK");
		quoteDetails.setCity("Irving");
		quoteDetails.setCountry("United States");
		quoteDetails.setReqCity("Irving");
		quoteDetails.setReqState("TX");
		quoteDetails.setReqCountry("United States");
		quoteResponse.setQuoteDetails(quoteDetails);
		quoteResponse.setAccessBandwidth(1000);
		quoteResponse.setBandwidth(1000);
		List<AccessSupplierList> accessSupplierList = new ArrayList<>();
		AccessSupplierList accessSupplier = new AccessSupplierList();
		AccessSupplierObject accessSupplierObject = new AccessSupplierObject();
		List<NodeObjectList> nodeList = new ArrayList<>();
		NodeObjectList nodeObjectList = new NodeObjectList();
		NodeObj nodeObj = new NodeObj();
		nodeObj.setNodeName("Copenhagen");
		nodeObj.setClli("CPNHDKBA");
		nodeObjectList.setNodeObj(nodeObj);
		nodeList.add(nodeObjectList);
		accessSupplierObject.setNodeList(nodeList);
		accessSupplierObject.setSupplierName("CPNHDKBA");
		accessSupplier.setAccessSupplier(accessSupplierObject);
		accessSupplierList.add(accessSupplier);
		quoteResponse.setAccessSupplierList(accessSupplierList);
		quoteResponse.setService("OPT-E-WAN (Ethernet)");
		quoteResponse.setAccessBandwidth(200);
		quoteResponse.setAccessArch("SWITCHED");
		quoteResponse.setCustCity("Irving");
		quoteResponse.setCustState("TX");
		quoteResponse.setCustCountry("United States");
		quoteResponse.setCustAddr1("8665 CYPRESS WATERS BLVD");

		com.att.sales.nexxus.model.Status status = new com.att.sales.nexxus.model.Status();
		status.setCode("200");
		quoteResponse.setStatus(status);
		List<QuoteResponse> quoteResponseList=new ArrayList<>();
		quoteResponseList.add(quoteResponse);
		AccessPricingResponse iglooResp=new AccessPricingResponse();
		iglooResp.setQuoteResponse(quoteResponseList);
		AccessPricingUiResponse resp=new AccessPricingUiResponse(); 
		resp.setApId(new Long(3l));
		resp.setCity("US");
		resp.setCity("WASHINGATAN");
		resp.setDqId("ETH536");
		resp.setIglooQuoteId("38.098");
		resp.setIncludeYn("Y");
		resp.setLocationYn("New");
		resp.setMrc("1125");
		resp.setNrc("5645");
		List<AccessPricingUiResponse> responseList =new ArrayList<>();
		responseList.add(resp);
		NxAccessPricingData pricingData=new NxAccessPricingData();
		pricingData.setEthToken("ethToken");
		pricingData.setIglooQuoteId("38.987676");
		pricingData.setIncludeYn("Y");
		pricingData.setLocationYn("New");
		pricingData.setMrc("1000");
		pricingData.setNrc("8888");
		pricingData.setAccessType("access");
		pricingData.setNxAccessPriceId(new Long(2l));
		pricingData.setReqContractTerm("contractTerm");
		pricingData.setReqCountry("US");
		pricingData.setIntermediateJson(s1);
		List<NxAccessPricingData> pricingDataList=new ArrayList<>();
		pricingDataList.add(pricingData);
		NxOutputBean bean=new NxOutputBean();
		NxMisBean misbean=new NxMisBean();
		misbean.setCountry("US");
		List<NxMisBean> listBean=new ArrayList<>();
		listBean.add(misbean);
		bean.setNxAdiMisBean(listBean);
		OutputJsonFallOutData outputJsonFallOutData=new OutputJsonFallOutData(bean, s1, null, false, false);
		
/*		NxSolutionDetail solution=new NxSolutionDetail();
		solution.setNxSolutionId(new Long(2l));
		List<NxSolutionDetail> solutionlist=new ArrayList<>();
		solutionlist.add(solution);*/
		String interimJson="{  \"circuitId\": \"test\"}";
		JsonNode design = JacksonUtil.toJsonNode(interimJson);
		Mockito.when(nxMyPriceRepositoryServce.getLookupDataByItemId(Mockito.anyString())).thenReturn(nxLookupDataMap);
		when(jsonNode.path(any())).thenReturn(jsonNode);
		when(accessPricingService.generateIntermediateJson(Mockito.any())).thenReturn(design);
		when(accessPricingService.generateOutputJson(Mockito.any())).thenReturn(outputJsonFallOutData);
		//Mockito.when(solutionRepo.findByNxSolutionId(Mockito.anyLong())).thenReturn(solutionlist);
		//Mockito.when(iglooResp.getQuoteResponse()).thenReturn(quoteResponseList);
		//when(accessPricingService.generateIntermediateJson(any())).thenReturn(jsonNode);
		
		service.saveData(iglooResp, request, responseList, response, jsonNode);

		
	}
	
	@Test
	public void testSaveData() throws SalesBusinessException {
		APUiResponse response=new APUiResponse();
		AccessPricingUiRequest request=new AccessPricingUiRequest();
		NxSolutionDetail nxSolutionDetail = new NxSolutionDetail();
		String description = StringConstants.FLOW_TYPE_IGLOO_QUOTE +"_"+LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
		nxSolutionDetail.setFlowType(StringConstants.FLOW_TYPE_IGLOO_QUOTE);
		nxSolutionDetail.setCreatedUser("ec006e");
		nxSolutionDetail.setActiveYn("Y");
		nxSolutionDetail.setNxsDescription(description);
		Mockito.when(solutionRepo.save(nxSolutionDetail)).thenReturn(nxSolutionDetail); 
		request.setNxSolutionId(nxSolutionDetail.getNxSolutionId());
		List<String> listDqid=new ArrayList<>();
		String s1="dqId1";
		listDqid.add(s1);
		request.setDqId(listDqid);
		QuoteResponse quoteResponse=new QuoteResponse();
		GUIResponse quoteDetails=new GUIResponse();
		quoteDetails.setCurrency("DKK");
		quoteDetails.setCity("Irving");
		quoteDetails.setCountry("United States");
		quoteDetails.setReqCity("Irving");
		quoteDetails.setReqState("TX");
		quoteDetails.setReqCountry("United States");
		quoteResponse.setQuoteDetails(quoteDetails);
		quoteResponse.setAccessBandwidth(1000);
		quoteResponse.setBandwidth(1000);
		
		List<AccessSupplierList> accessSupplierList = new ArrayList<>();
		AccessSupplierList accessSupplier = new AccessSupplierList();
		AccessSupplierObject accessSupplierObject = new AccessSupplierObject();
		List<NodeObjectList> nodeList = new ArrayList<>();
		NodeObjectList nodeObjectList = new NodeObjectList();
		NodeObj nodeObj = new NodeObj();
		nodeObj.setNodeName("Copenhagen");
		nodeObj.setClli("CPNHDKBA");
		nodeObjectList.setNodeObj(nodeObj);
		nodeList.add(nodeObjectList);
		accessSupplierObject.setNodeList(nodeList);
		accessSupplierObject.setSupplierName("CPNHDKBA");
		accessSupplier.setAccessSupplier(accessSupplierObject);
		accessSupplierList.add(accessSupplier);
		quoteResponse.setAccessSupplierList(accessSupplierList);
		quoteResponse.setService("OPT-E-WAN (Ethernet)");
		quoteResponse.setAccessBandwidth(200);
		quoteResponse.setAccessArch("SWITCHED");
		quoteResponse.setCustCity("Irving");
		quoteResponse.setCustState("TX");
		quoteResponse.setCustCountry("United States");
		quoteResponse.setCustAddr1("8665 CYPRESS WATERS BLVD");

		com.att.sales.nexxus.model.Status status = new com.att.sales.nexxus.model.Status();
		status.setCode("200");
		quoteResponse.setStatus(status);
		List<QuoteResponse> quoteResponseList=new ArrayList<>();
		quoteResponseList.add(quoteResponse);
		AccessPricingResponse iglooResp=new AccessPricingResponse();
		iglooResp.setQuoteResponse(quoteResponseList);
		AccessPricingUiResponse resp=new AccessPricingUiResponse(); 
		resp.setApId(new Long(3l));
		resp.setCity("US");
		resp.setCity("WASHINGATAN");
		resp.setDqId("ETH536");
		resp.setIglooQuoteId("38.098");
		resp.setIncludeYn("Y");
		resp.setLocationYn("New");
		resp.setMrc("1125");
		resp.setNrc("5645");
		List<AccessPricingUiResponse> responseList =new ArrayList<>();
		responseList.add(resp);
		NxAccessPricingData pricingData=new NxAccessPricingData();
		pricingData.setEthToken("ethToken");
		pricingData.setIglooQuoteId("38.987676");
		pricingData.setIncludeYn("Y");
		pricingData.setLocationYn("New");
		pricingData.setMrc("1000");
		pricingData.setNrc("8888");
		pricingData.setAccessType("access");
		pricingData.setNxAccessPriceId(new Long(2l));
		pricingData.setReqContractTerm("contractTerm");
		pricingData.setReqCountry("US");
		pricingData.setIntermediateJson(s1);
		List<NxAccessPricingData> pricingDataList=new ArrayList<>();
		pricingDataList.add(pricingData);
		NxOutputBean bean=new NxOutputBean();
		NxMisBean misbean=new NxMisBean();
		misbean.setCountry("US");
		List<NxMisBean> listBean=new ArrayList<>();
		listBean.add(misbean);
		bean.setNxAdiMisBean(listBean);
		OutputJsonFallOutData outputJsonFallOutData=new OutputJsonFallOutData(bean, s1, null, false, false);
		
		NxSolutionDetail solution=new NxSolutionDetail();
		solution.setNxSolutionId(new Long(2l));
		List<NxSolutionDetail> solutionlist=new ArrayList<>();
		solutionlist.add(solution);
		String interimJson="{  \"circuitId\": \"test\"}";
		JsonNode design = JacksonUtil.toJsonNode(interimJson);
		Mockito.when(nxMyPriceRepositoryServce.getLookupDataByItemId(Mockito.anyString())).thenReturn(nxLookupDataMap);
		when(jsonNode.path(any())).thenReturn(jsonNode);
		when(accessPricingService.generateIntermediateJson(Mockito.any())).thenReturn(design);
		when(accessPricingService.generateOutputJson(Mockito.any())).thenReturn(outputJsonFallOutData);
		Mockito.when(solutionRepo.findByNxSolutionId(Mockito.anyLong())).thenReturn(solutionlist.get(0));
		Mockito.when(nxMpDealRepository.getCountryCodeByCountryIsoCode(Mockito.anyString())).thenReturn("US");

		//Mockito.when(iglooResp.getQuoteResponse()).thenReturn(quoteResponseList);
		//when(accessPricingService.generateIntermediateJson(any())).thenReturn(jsonNode);
		
		service.saveData(iglooResp, request, responseList, response, jsonNode);

		
	}
	
	@Test
	public void testSaveDataElseIf() throws SalesBusinessException {
		APUiResponse response=new APUiResponse();
		AccessPricingUiRequest request=new AccessPricingUiRequest();
		NxSolutionDetail nxSolutionDetail = new NxSolutionDetail();
		String description = StringConstants.FLOW_TYPE_IGLOO_QUOTE +"_"+LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
		nxSolutionDetail.setFlowType(StringConstants.FLOW_TYPE_IGLOO_QUOTE);
		nxSolutionDetail.setCreatedUser("ec006e");
		nxSolutionDetail.setActiveYn("Y");
		nxSolutionDetail.setNxsDescription(description);
		Mockito.when(solutionRepo.save(nxSolutionDetail)).thenReturn(nxSolutionDetail);
		request.setNxSolutionId(nxSolutionDetail.getNxSolutionId());
		List<String> listDqid=new ArrayList<>();
		String s1="dqId1";
		String interimjson="{  \"circuitId\": \"test\"}";
		listDqid.add(s1);
		request.setDqId(listDqid);
		QuoteResponse quoteResponse=new QuoteResponse();
		GUIResponse quoteDetails=null;
		quoteResponse.setQuoteDetails(quoteDetails);
		com.att.sales.nexxus.model.Status status = new com.att.sales.nexxus.model.Status();
		status.setCode("200");
		quoteResponse.setStatus(status);
		List<QuoteResponse> quoteResponseList=new ArrayList<>();
		quoteResponseList.add(quoteResponse);
		AccessPricingResponse iglooResp=new AccessPricingResponse();
		iglooResp.setQuoteResponse(quoteResponseList);
		AccessPricingUiResponse resp=new AccessPricingUiResponse(); 
		resp.setApId(new Long(3l));
		resp.setCity("US");
		resp.setCity("WASHINGATAN");
		resp.setDqId("ETH536");
		resp.setIglooQuoteId("38.098");
		resp.setIncludeYn("Y");
		resp.setLocationYn("New");
		resp.setMrc("1125");
		resp.setNrc("5645");
		List<AccessPricingUiResponse> responseList =new ArrayList<>();
		responseList.add(resp);
		NxAccessPricingData pricingData=new NxAccessPricingData();
		pricingData.setEthToken("ethToken");
		pricingData.setIglooQuoteId("38.987676");
		pricingData.setIncludeYn("Y");
		pricingData.setLocationYn("New");
		pricingData.setMrc("mrc");
		pricingData.setNrc("nrc");
		pricingData.setAccessType("access");
		pricingData.setNxAccessPriceId(new Long(2l));
		pricingData.setReqContractTerm("contractTerm");
		pricingData.setReqCountry("US");
		pricingData.setIntermediateJson(interimjson);
		List<NxAccessPricingData> pricingDataList=new ArrayList<>();
		pricingDataList.add(pricingData);
		NxOutputBean bean=new NxOutputBean();
		NxMisBean misbean=new NxMisBean();
		misbean.setCountry("US");
		List<NxMisBean> listBean=new ArrayList<>();
		listBean.add(misbean);
		bean.setNxAdiMisBean(listBean);
		OutputJsonFallOutData outputJsonFallOutData=new OutputJsonFallOutData(bean, s1, null, false, false);
		
		NxSolutionDetail solution=new NxSolutionDetail();
		solution.setNxSolutionId(new Long(2l));
		List<NxSolutionDetail> solutionlist=new ArrayList<>();
		solutionlist.add(solution);

		String interimJson="{  \"circuitId\": \"test\"}";
		JsonNode design = JacksonUtil.toJsonNode(interimJson);
		when(jsonNode.path(any())).thenReturn(jsonNode);
		when(accessPricingService.generateIntermediateJson(Mockito.any())).thenReturn(design);
		when(accessPricingService.generateOutputJson(Mockito.any())).thenReturn(outputJsonFallOutData);
		Mockito.when(solutionRepo.findByNxSolutionId(Mockito.anyLong())).thenReturn(solutionlist.get(0));
		Mockito.when(nxMpDealRepository.getCountryCodeByCountryIsoCode(Mockito.anyString())).thenReturn("US");
		Mockito.when(nxMyPriceRepositoryServce.getLookupDataByItemId(Mockito.anyString())).thenReturn(nxLookupDataMap);
		

		//Mockito.when(iglooResp.getQuoteResponse()).thenReturn(quoteResponseList);
		//when(accessPricingService.generateIntermediateJson(any())).thenReturn(jsonNode);
		
		service.saveData(iglooResp, request, responseList, response, jsonNode);
		
	}
	
	
	@Test
	public void testSaveDataElse() throws SalesBusinessException {
		APUiResponse response=new APUiResponse();
		
		AccessPricingUiRequest request=new AccessPricingUiRequest();
		request.setNxSolutionId(new Long(4l));
		List<String> listDqid=new ArrayList<>();
		String s1="dqId1";
		listDqid.add(s1);
		request.setDqId(listDqid);
		QuoteResponse quoteResponse=new QuoteResponse();
		GUIResponse quoteDetails=new GUIResponse();
		quoteDetails.setCity("wahsington");
		quoteDetails.setCountry("US");
		quoteResponse.setQuoteDetails(quoteDetails);
		com.att.sales.nexxus.model.Status status = new com.att.sales.nexxus.model.Status();
		status.setCode("400");
		quoteResponse.setStatus(status);
		List<QuoteResponse> quoteResponseList=new ArrayList<>();
		quoteResponseList.add(quoteResponse);
		AccessPricingResponse iglooResp=new AccessPricingResponse();
		iglooResp.setQuoteResponse(quoteResponseList);
		AccessPricingUiResponse resp=new AccessPricingUiResponse(); 
		resp.setApId(new Long(3l));
		resp.setCity("US");
		resp.setCity("WASHINGATAN");
		resp.setDqId("ETH536");
		resp.setIglooQuoteId("38.098");
		resp.setIncludeYn("Y");
		resp.setLocationYn("New");
		resp.setMrc("1125");
		resp.setNrc("5645");
		List<AccessPricingUiResponse> responseList =new ArrayList<>();
		responseList.add(resp);
		NxAccessPricingData pricingData=new NxAccessPricingData();
		pricingData.setEthToken("ethToken");
		pricingData.setIglooQuoteId("38.987676");
		pricingData.setIncludeYn("Y");
		pricingData.setLocationYn("New");
		pricingData.setMrc("1000");
		pricingData.setNrc("888");
		pricingData.setAccessType("access");
		pricingData.setNxAccessPriceId(new Long(2l));
		pricingData.setReqContractTerm("contractTerm");
		pricingData.setReqCountry("US");
		pricingData.setIntermediateJson(s1);
		List<NxAccessPricingData> pricingDataList=new ArrayList<>();
		pricingDataList.add(pricingData);
		NxSolutionDetail solution=new NxSolutionDetail();
		solution.setNxSolutionId(new Long(2l));
		List<NxSolutionDetail> solutionlist=new ArrayList<>();
		solutionlist.add(solution);
		when(jsonNode.path(any())).thenReturn(jsonNode);
		when(accessPricingService.generateIntermediateJson(Mockito.any())).thenReturn(intermediateJson);
		//Mockito.when(iglooResp.getQuoteResponse()).thenReturn(quoteResponseList);
		//when(accessPricingService.generateIntermediateJson(any())).thenReturn(jsonNode);
		Mockito.when(solutionRepo.findByNxSolutionId(Mockito.anyLong())).thenReturn(solutionlist.get(0));
		Mockito.when(nxMyPriceRepositoryServce.getLookupDataByItemId(Mockito.anyString())).thenReturn(nxLookupDataMap);
		service.saveData(iglooResp, request, responseList, response, jsonNode);
		
		
	} 
	
	
	@Test
	public void createNxTeamEntryTest() throws SalesBusinessException{
		NxSolutionDetail nxSolutionDetail=new NxSolutionDetail();
		nxSolutionDetail.setNxSolutionId(new Long(2l));
		try {
			NxUser nxUser= new NxUser();
			nxUser.setUserAttId("ec006e");
			nxUser.setEmail("email");
			nxUser.setFirstName("firstName");
			nxUser.setLastName("lastName");

			NxTeam nxTeam = new NxTeam();
			nxTeam.setAttuid(nxUser.getUserAttId());
			nxTeam.setEmail(nxUser.getEmail());
			nxTeam.setfName(nxUser.getFirstName());
			nxTeam.setlName(nxUser.getLastName());
			nxTeam.setIsPryMVG("y");
			Mockito.when(nxTeamRepository.save(nxTeam)).thenReturn(nxTeam);
			Mockito.when(userServiceImpl.getUserProfileName(anyString())).thenReturn("Admin");
			Mockito.when(nxUserRepository.findByUserAttId(anyString())).thenReturn(nxUser);
			service.createNxTeamEntry(nxSolutionDetail, "ec006e");
		
			Mockito.when(userServiceImpl.getUserProfileName(anyString())).thenReturn("NONE");
			service.createNxTeamEntry(nxSolutionDetail, "ec006e");
			service.createNxTeamEntry(nxSolutionDetail, "ec006e");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void storeFailedTokensIfErrorTest() {
		AccessPricingUiRequest request = new AccessPricingUiRequest();
		request.setNxSolutionId(new Long(2l));
		List<String> reqDqId = new ArrayList<>();
		reqDqId.add("ETH1005W7J");
		request.setBulkupload(true);
		List<UploadEthTokenRequest> bulkUploadTokens = new ArrayList<>();
		UploadEthTokenRequest uploadEthTokenRequest = new UploadEthTokenRequest();
		uploadEthTokenRequest.setSiteRefId("1");
		uploadEthTokenRequest.setQuoteId("ETH1005W7J");
		uploadEthTokenRequest.setPortStatus("New");
		bulkUploadTokens.add(uploadEthTokenRequest);
		request.setBulkUploadTokens(bulkUploadTokens);
		NxDesignAudit audit = new NxDesignAudit();
		audit.setNxRefId(request.getNxSolutionId());
		audit.setData("[{\"quoteId\":\"ETH123345\",\"siteRefId\":\"1\"},{\"quoteId\":\"ETH20044YV\",\"siteRefId\":\"2\"},{\"quoteId\":\"ETH10044PL\",\"siteRefId\":\"3\"},{\"quoteId\":\"ETH342534\",\"siteRefId\":\"4\"},{\"quoteId\":\"\",\"siteRefId\":null}]");
		audit.setTransaction("Ethernet Token Bulkupload");
		Mockito.when(nxDesignAuditRepository.findByNxRefIdAndTransaction(request.getNxSolutionId(),"Ethernet Token Bulkupload")).thenReturn(audit);
		Mockito.when(nxDesignAuditRepository.save(audit)).thenReturn(audit);
		service.storeFailedTokens(request, reqDqId, true);
	}
	
	@Test
	public void storeFailedTokensTest() {
		AccessPricingUiRequest request = new AccessPricingUiRequest();
		request.setNxSolutionId(new Long(2l));
		List<String> reqDqId = new ArrayList<>();
		reqDqId.add("ETH1005W7J");
		request.setBulkupload(true);
		List<UploadEthTokenRequest> bulkUploadTokens = new ArrayList<>();
		UploadEthTokenRequest uploadEthTokenRequest = new UploadEthTokenRequest();
		uploadEthTokenRequest.setSiteRefId("1");
		uploadEthTokenRequest.setQuoteId("ETH1005W7J");
		uploadEthTokenRequest.setPortStatus("New");
		bulkUploadTokens.add(uploadEthTokenRequest);
		request.setBulkUploadTokens(bulkUploadTokens);
		NxDesignAudit audit = new NxDesignAudit();
		audit.setNxRefId(request.getNxSolutionId());
		audit.setData("[{\"quoteId\":\"ETH123345\",\"siteRefId\":\"1\"},{\"quoteId\":\"ETH20044YV\",\"siteRefId\":\"2\"},{\"quoteId\":\"ETH10044PL\",\"siteRefId\":\"3\"},{\"quoteId\":\"ETH342534\",\"siteRefId\":\"4\"},{\"quoteId\":\"\",\"siteRefId\":null}]");
		audit.setTransaction("Ethernet Token Bulkupload");
		Mockito.when(nxDesignAuditRepository.findByNxRefIdAndTransaction(request.getNxSolutionId(),"Ethernet Token Bulkupload")).thenReturn(audit);
		Mockito.when(nxDesignAuditRepository.save(audit)).thenReturn(audit);
		service.storeFailedTokens(request, reqDqId, false);
	}
	
	@Test
	public void getUploadedTokenTest() {
		List<UploadEthTokenRequest> bulkUploadTokens = new ArrayList<>(); 
		UploadEthTokenRequest uploadEthTokenRequest = new UploadEthTokenRequest();
		uploadEthTokenRequest.setSiteRefId("1");
		uploadEthTokenRequest.setQuoteId("ETH3456734");
		uploadEthTokenRequest.setPortStatus("New");
		bulkUploadTokens.add(uploadEthTokenRequest);
		String quoteId = "ETH3456734";
		service.getUploadedToken(bulkUploadTokens, quoteId, null);
	}
	
	@Test
	public void roundOffTest() {
		try {
			service.roundOff("10000.898986543");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Test
	public void getDetailsDataTest() {
		AccessPricingUiRequest request = new AccessPricingUiRequest();
		List<AccessPricingUiResponse> responseList = new ArrayList<>();
		APUiResponse response = new APUiResponse();
		request.setNxSolutionId(new Long(2l));
		List<NxAccessPricingData> nxAccessPricingDataList = new ArrayList<>();
		Mockito.when(repository.findByNxSolutionId(request.getNxSolutionId())).thenReturn(nxAccessPricingDataList);
		service.getDetailsData(request, responseList, response);
	}
	
	@Test
	public void retrieveQuoteIdTest() {
		AccessPricingUiRequest request = new AccessPricingUiRequest();
		List<AccessPricingUiResponse> responseList = new ArrayList<>();
		APUiResponse response = new APUiResponse();
		request.setNxSolutionId(new Long(2l));
		List<NxAccessPricingData> nxAccessPricingDataList = new ArrayList<>();
		Mockito.when(repository.findByNxSolutionId(request.getNxSolutionId())).thenReturn(nxAccessPricingDataList);
		service.retrieveQuoteId(request, responseList, response);
	}

	@Test
	public void deleteQuoteIdTest() {
		AccessPricingUiRequest request = new AccessPricingUiRequest();
		List<String> quotes = new ArrayList<>();
		quotes.add("ETH1005W7J");
		List<String> iglooQuotes = new ArrayList<>();
		iglooQuotes.add("1031413_BR_GMIS-ETH_2032403_3656731");
		request.setDqId(quotes);
		request.setIglooQuoteId(iglooQuotes);
		request.setNxSolutionId(new Long(2l));
		List<AccessPricingUiResponse> responseList = new ArrayList<>();
		APUiResponse response = new APUiResponse();
		NxSolutionDetail nxSolutionDetail = new NxSolutionDetail();
		nxSolutionDetail.setNxSolutionId(new Long(2l));
		Mockito.when(solutionRepo.findByNxSolutionId(request.getNxSolutionId())).thenReturn(nxSolutionDetail);
		List<NxAccessPricingData> nxAccessPricingList=new ArrayList<>();
		nxAccessPricingList.add(new NxAccessPricingData());
		Mockito.when(repository.findByEthTokenAndNxSolutionId(anyString(), anyLong())).thenReturn(nxAccessPricingList);
		Mockito.when(repository.findByIglooQuoteIdAndNxSolutionId(anyString(), anyLong())).thenReturn(nxAccessPricingList);
		try {
			service.deleteQuoteId(request, responseList, response);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
/*	@Test
	public void updateQuoteId() {
		AccessPricingUiRequest request = new AccessPricingUiRequest(); 
		List<AccessPricingUiResponse> responseList = new ArrayList<>();
		APUiResponse response = new APUiResponse();
		service.updateQuoteId(request, responseList, response);
	}*/
	
	
	@Test
	public void testSaveDataBulkUpload() throws SalesBusinessException {
		APUiResponse response=new APUiResponse();
		
		NxSolutionDetail nxSolutionDetail = new NxSolutionDetail();
		String description = StringConstants.FLOW_TYPE_IGLOO_QUOTE +"_"+LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
		nxSolutionDetail.setFlowType(StringConstants.FLOW_TYPE_IGLOO_QUOTE);
		nxSolutionDetail.setCreatedUser("ec006e");
		nxSolutionDetail.setActiveYn("Y");
		nxSolutionDetail.setNxsDescription(description);
		Mockito.when(solutionRepo.save(nxSolutionDetail)).thenReturn(nxSolutionDetail);
		
		AccessPricingUiRequest request=new AccessPricingUiRequest();
		request.setNxSolutionId(nxSolutionDetail.getNxSolutionId());
		List<String> listDqid=new ArrayList<>();
		String s1="ETH1005W7J1";
		listDqid.add(s1);
		request.setDqId(listDqid);
		
		request.setBulkupload(true);
		List<UploadEthTokenRequest> bulkUploadTokens = new ArrayList<>();
		UploadEthTokenRequest uploadEthTokenRequest = new UploadEthTokenRequest();
		uploadEthTokenRequest.setSiteRefId("1");
		uploadEthTokenRequest.setQuoteId("ETH1005W7J");
		uploadEthTokenRequest.setPortStatus("New");
		bulkUploadTokens.add(uploadEthTokenRequest);
		request.setBulkUploadTokens(bulkUploadTokens);
		
		QuoteResponse quoteResponse=new QuoteResponse();
		GUIResponse quoteDetails=new GUIResponse();
		quoteDetails.setCurrency("DKK");
		quoteDetails.setCity("Irving");
		quoteDetails.setCountry("United States");
		quoteDetails.setReqCity("Irving");
		quoteDetails.setReqState("TX");
		quoteDetails.setReqCountry("United States");
		quoteResponse.setQuoteDetails(quoteDetails);
		quoteResponse.setBandwidth(1000);
		List<AccessSupplierList> accessSupplierList = new ArrayList<>();
		AccessSupplierList accessSupplier = new AccessSupplierList();
		AccessSupplierObject accessSupplierObject = new AccessSupplierObject();
		List<NodeObjectList> nodeList = new ArrayList<>();
		NodeObjectList nodeObjectList = new NodeObjectList();
		NodeObj nodeObj = new NodeObj();
		nodeObj.setNodeName("Copenhagen");
		nodeObj.setClli("CPNHDKBA");
		nodeObjectList.setNodeObj(nodeObj);
		nodeList.add(nodeObjectList);
		accessSupplierObject.setNodeList(nodeList);
		accessSupplierObject.setSupplierName("CPNHDKBA");
		accessSupplier.setAccessSupplier(accessSupplierObject);
		accessSupplierList.add(accessSupplier);
		quoteResponse.setAccessSupplierList(accessSupplierList);
		quoteResponse.setService("OPT-E-WAN (Ethernet)");
		quoteResponse.setBandwidth(200);
		quoteResponse.setAccessArch("SWITCHED");
		quoteResponse.setCustCity("Irving");
		quoteResponse.setCustState("TX");
		quoteResponse.setCustCountry("United States");
		quoteResponse.setCustAddr1("8665 CYPRESS WATERS BLVD");
		com.att.sales.nexxus.model.Status status = new com.att.sales.nexxus.model.Status();
		status.setCode("200");
		quoteResponse.setStatus(status);
		List<QuoteResponse> quoteResponseList=new ArrayList<>();
		quoteResponseList.add(quoteResponse);
		AccessPricingResponse iglooResp=new AccessPricingResponse();
		iglooResp.setQuoteResponse(quoteResponseList);
		AccessPricingUiResponse resp=new AccessPricingUiResponse(); 
		resp.setApId(new Long(3l));
		resp.setCountry("US");
		resp.setCity("Irving");
		resp.setDqId("ETH1005W7J");
		resp.setIglooQuoteId("38.098");
		resp.setIncludeYn("Y");
		resp.setLocationYn("New");
		resp.setMrc("1125");
		resp.setNrc("5645");
		List<AccessPricingUiResponse> responseList =new ArrayList<>();
		responseList.add(resp);
		NxAccessPricingData pricingData=new NxAccessPricingData();
		pricingData.setEthToken("ethToken");
		pricingData.setIglooQuoteId("38.987676");
		pricingData.setIncludeYn("Y");
		pricingData.setLocationYn("New");
		pricingData.setMrc("1000");
		pricingData.setNrc("8888");
		String accessArch = quoteResponseList.get(0).getAccessArch();
		if(null != accessArch) {
			quoteResponseList.get(0).getQuoteDetails().setAccessArch(accessArch.toUpperCase());
			pricingData.setAccessType(accessArch.toUpperCase());
		}
		pricingData.setNxAccessPriceId(new Long(2l));
		pricingData.setReqContractTerm("contractTerm");
		pricingData.setReqCity("Irving");
		pricingData.setReqState("TX");
		pricingData.setReqCountry("United States");
		pricingData.setReqStreetAddress("8665 CYPRESS WATERS BLVD");
		pricingData.setIntermediateJson("{\"reqStreetAddress\":\"8665 CYPRESS WATERS BLVD\",\"reqCity\":\"Irving\",\"reqState\":\"TX\",\"reqZipCode\":\"75063\",\"country\":\"United States\",\"address\":\"8665 CYPRESS WATERS BLVD\",\"city\":\"IRVING\",\"state\":\"TX\",\"zipCode\":\"75063-7337\",\"reqAccessBandwidth\":\"20\",\"reqVendor\":\"SBC\",\"reqIlecSwc\":\"DLLSTXNO\",\"attEthPop\":\"DLLSTXTL\",\"service\":\"AVPN-ETH\",\"bandwidth\":\"20\",\"accessArch\":\"Switched\",\"physicalInterface\":\"100BaseTX Electrical\",\"mrc\":\"906.00\",\"discountMonthlyRecurringPrice\":\"688.56\",\"nonRecurringCharge\":\"1630.00\",\"ethToken\":\"ETH1005W7J\",\"iglooQuoteID\":\"121566.001\",\"flowType\":\"IGL\",\"nxSiteId\":443326}");
		pricingData.setSiteRefId("1");
		pricingData.setLocationYn("Existing");
		pricingData.setSupplierName("EPLS");
		pricingData.setClli("CPNHDKBA");
		pricingData.setNodeName("Copenhagen");
		pricingData.setService("AVPN-ETH");
		List<NxAccessPricingData> pricingDataList=new ArrayList<>();
		pricingDataList.add(pricingData);
		NxOutputBean bean=new NxOutputBean();
		NxMisBean misbean=new NxMisBean();
		misbean.setCountry("US");
		List<NxMisBean> listBean=new ArrayList<>();
		listBean.add(misbean);
		bean.setNxAdiMisBean(listBean);
		OutputJsonFallOutData outputJsonFallOutData=new OutputJsonFallOutData(bean, "ETH1005W7J", null, false, false);
		
/*		NxSolutionDetail solution=new NxSolutionDetail();
		solution.setNxSolutionId(new Long(2l));
		List<NxSolutionDetail> solutionlist=new ArrayList<>();
		solutionlist.add(solution);*/
		Mockito.when(nxMyPriceRepositoryServce.getLookupDataByItemId(Mockito.anyString())).thenReturn(nxLookupDataMap);
		String interimJson="{  \"circuitId\": \"test\"}";
		JsonNode design = JacksonUtil.toJsonNode(interimJson);

		when(jsonNode.path(any())).thenReturn(jsonNode);
		when(accessPricingService.generateIntermediateJson(Mockito.any())).thenReturn(design);
		when(accessPricingService.generateOutputJson(Mockito.any())).thenReturn(outputJsonFallOutData);
		//Mockito.when(solutionRepo.findByNxSolutionId(Mockito.anyLong())).thenReturn(solutionlist);
		//Mockito.when(iglooResp.getQuoteResponse()).thenReturn(quoteResponseList);
		//when(accessPricingService.generateIntermediateJson(any())).thenReturn(jsonNode);
		
		service.saveData(iglooResp, request, responseList, response, jsonNode);

		
	}

	
	@Test
	public void testSaveDataElseIfBulkupload() throws SalesBusinessException {
		APUiResponse response=new APUiResponse();
		AccessPricingUiRequest request=new AccessPricingUiRequest();
		NxSolutionDetail nxSolutionDetail = new NxSolutionDetail();
		String description = StringConstants.FLOW_TYPE_IGLOO_QUOTE +"_"+LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
		nxSolutionDetail.setFlowType(StringConstants.FLOW_TYPE_IGLOO_QUOTE);
		nxSolutionDetail.setCreatedUser("ec006e");
		nxSolutionDetail.setActiveYn("Y");
		nxSolutionDetail.setNxsDescription(description);
		Mockito.when(solutionRepo.save(nxSolutionDetail)).thenReturn(nxSolutionDetail);
		request.setNxSolutionId(nxSolutionDetail.getNxSolutionId());
		List<String> listDqid=new ArrayList<>();
		String s1="dqId1";
		listDqid.add(s1);
		request.setDqId(listDqid);
		
		request.setBulkupload(true);
		List<UploadEthTokenRequest> bulkUploadTokens = new ArrayList<>();
		UploadEthTokenRequest uploadEthTokenRequest = new UploadEthTokenRequest();
		uploadEthTokenRequest.setSiteRefId("1");
		uploadEthTokenRequest.setQuoteId("ETH1005W7J");
		uploadEthTokenRequest.setPortStatus("Existing");
		bulkUploadTokens.add(uploadEthTokenRequest);
		request.setBulkUploadTokens(bulkUploadTokens);
		
		QuoteResponse quoteResponse=new QuoteResponse();
		GUIResponse quoteDetails=null;
		quoteResponse.setQuoteDetails(quoteDetails);
		quoteResponse.setBandwidth(200);
		com.att.sales.nexxus.model.Status status = new com.att.sales.nexxus.model.Status();
		status.setCode("200");
		quoteResponse.setStatus(status);
		List<QuoteResponse> quoteResponseList=new ArrayList<>();
		quoteResponseList.add(quoteResponse);
		AccessPricingResponse iglooResp=new AccessPricingResponse();
		iglooResp.setQuoteResponse(quoteResponseList);
		AccessPricingUiResponse resp=new AccessPricingUiResponse(); 
		resp.setApId(new Long(3l));
		resp.setCity("US");
		resp.setCity("WASHINGATAN");
		resp.setDqId("ETH536");
		resp.setIglooQuoteId("38.098");
		resp.setIncludeYn("Y");
		resp.setLocationYn("New");
		resp.setMrc("1125");
		resp.setNrc("5645");
		resp.setCustCountry("United States");
		List<AccessPricingUiResponse> responseList =new ArrayList<>();
		responseList.add(resp);
		NxAccessPricingData pricingData=new NxAccessPricingData();
		pricingData.setLocationYn("New");
		pricingData.setEthToken("ethToken");
		pricingData.setIglooQuoteId("38.987676");
		pricingData.setIncludeYn("Y");
		pricingData.setMrc("mrc");
		pricingData.setNrc("nrc");
		pricingData.setAccessType("access");
		pricingData.setNxAccessPriceId(new Long(2l));
		pricingData.setReqContractTerm("contractTerm");
		pricingData.setReqCountry("US");
		pricingData.setCustCountry("United States");
		
		if (java.util.Optional.ofNullable(pricingData.getCustCountry()).isPresent() && !java.util.Optional.ofNullable(pricingData.getCurrency()).isPresent()) {
			String currency = null;
			Mockito.when(nxMpDealRepository.getCurrencyCodeByCountryIsoCode(pricingData.getCustCountry())).thenReturn(currency);
			pricingData.setCurrency(currency);
		}
		pricingData.setIntermediateJson(s1);
		List<NxAccessPricingData> pricingDataList=new ArrayList<>();
		pricingDataList.add(pricingData);
		NxOutputBean bean=new NxOutputBean();
		NxMisBean misbean=new NxMisBean();
		misbean.setCountry("US");
		List<NxMisBean> listBean=new ArrayList<>();
		listBean.add(misbean);
		bean.setNxAdiMisBean(listBean);
		OutputJsonFallOutData outputJsonFallOutData=new OutputJsonFallOutData(bean, s1, null, false, false);
		
		NxSolutionDetail solution=new NxSolutionDetail();
		solution.setNxSolutionId(new Long(2l));
		List<NxSolutionDetail> solutionlist=new ArrayList<>();
		solutionlist.add(solution);
		String interimJson="{  \"circuitId\": \"test\"}";
		JsonNode design = JacksonUtil.toJsonNode(interimJson);
		when(jsonNode.path(any())).thenReturn(jsonNode);
		when(accessPricingService.generateIntermediateJson(Mockito.any())).thenReturn(design);
		when(accessPricingService.generateOutputJson(Mockito.any())).thenReturn(outputJsonFallOutData);
		Mockito.when(solutionRepo.findByNxSolutionId(Mockito.anyLong())).thenReturn(solutionlist.get(0));
		//Mockito.when(iglooResp.getQuoteResponse()).thenReturn(quoteResponseList);
		//when(accessPricingService.generateIntermediateJson(any())).thenReturn(jsonNode);
		Mockito.when(nxMyPriceRepositoryServce.getLookupDataByItemId(Mockito.anyString())).thenReturn(nxLookupDataMap);
		service.saveData(iglooResp, request, responseList, response, jsonNode);
		
	}
	

}
