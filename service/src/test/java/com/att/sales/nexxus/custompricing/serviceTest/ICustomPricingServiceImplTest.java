package com.att.sales.nexxus.custompricing.serviceTest;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyMap;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.env.Environment;
import org.springframework.test.util.ReflectionTestUtils;

import com.att.sales.framework.model.ServiceMetaData;
import com.att.sales.framework.service.BaseServiceImpl;
import com.att.sales.nexxus.common.CommonConstants;
import com.att.sales.nexxus.constant.MyPriceConstants;
import com.att.sales.nexxus.custompricing.model.ActionDeterminant;
import com.att.sales.nexxus.custompricing.model.AllIncPrice;
import com.att.sales.nexxus.custompricing.model.CustomPricingRequest;
import com.att.sales.nexxus.custompricing.model.CustomPricingResponse;
import com.att.sales.nexxus.custompricing.model.Solution;
import com.att.sales.nexxus.custompricing.model.SolutionDeterminant;
import com.att.sales.nexxus.custompricing.service.ICustomPricingService;
import com.att.sales.nexxus.custompricing.service.ICustomPricingServiceImpl;
import com.att.sales.nexxus.dao.model.NxDesign;
import com.att.sales.nexxus.dao.model.NxMpDeal;
import com.att.sales.nexxus.dao.model.NxMpPriceDetails;
import com.att.sales.nexxus.dao.model.NxMpSiteDictionary;
import com.att.sales.nexxus.dao.model.solution.NxSolutionDetail;
import com.att.sales.nexxus.dao.repository.NxDesignRepository;
import com.att.sales.nexxus.dao.repository.NxMpDealRepository;
import com.att.sales.nexxus.dao.repository.NxMpPriceDetailsRepository;
import com.att.sales.nexxus.dao.repository.NxMpSiteDictionaryRepository;
import com.att.sales.nexxus.dao.repository.NxSolutionDetailsRepository;
import com.att.sales.nexxus.myprice.transaction.dao.service.NxMpRepositoryService;
import com.att.sales.nexxus.myprice.transaction.model.AseodReqRatesResponse;
import com.att.sales.nexxus.myprice.transaction.service.Aseod3PAReqRatesHelperService;
import com.att.sales.nexxus.myprice.transaction.service.AseodReqRatesServiceImpl;
import com.att.sales.nexxus.myprice.transaction.service.MyPriceTransactionUtil;
import com.att.sales.nexxus.util.HttpRestClient;
import com.att.sales.nexxus.util.RestClientUtil;

@ExtendWith(MockitoExtension.class)
public class ICustomPricingServiceImplTest {
	
	@InjectMocks
	ICustomPricingServiceImpl iCustomPricingServiceImpl;
	
	@Mock
	AseodReqRatesServiceImpl aseodReqRatesServiceImpl;
	
	@Mock
	ICustomPricingService iCustomPricingService;
	
	@Mock
	NxMpDealRepository nxMpDealRepository;
	
	@InjectMocks
	MyPriceConstants myPriceConstants;
	
	@InjectMocks
	CommonConstants commonConstants;
	
	@Mock
	NxSolutionDetailsRepository repository;
	
	@Mock
	NxDesignRepository nxDesignRepository;

	@Mock
	NxMpPriceDetailsRepository nxMpPriceDetailsRepository;
	
	@Mock
	
	NxMpRepositoryService nxMpRepositoryService;
	
	@Mock
	NxMpSiteDictionaryRepository nxMpSiteDicRepo;
	
	@Mock
	Environment env;
	
	@Mock
	MyPriceTransactionUtil myPriceTransactionUtil;
	
	@Mock
	Properties props;
	
	@Mock
	RestClientUtil restClient;
	
	@Mock
	HttpRestClient httpRest;
	
	@InjectMocks
	BaseServiceImpl baseServiceImpl;
	
	@Mock
	private Aseod3PAReqRatesHelperService aseod3PAReqRatesHelperService;
	
	@BeforeEach
	public void initializeServiceMetaData() {
	Map<String, Object> requestParams = new HashMap<>();
		requestParams.put(ServiceMetaData.OFFER, "ADE");
		requestParams.put(ServiceMetaData.REST_REQUEST_START_TIME, System.currentTimeMillis());
		requestParams.put(ServiceMetaData.VERSION, "1.0");
		requestParams.put(ServiceMetaData.METHOD, "TestMethod");
		requestParams.put(ServiceMetaData.URI, "Testuri");
		ReflectionTestUtils.setField(myPriceTransactionUtil, "httpProxyHost", "pxyapp.proxy.att.com");
		ReflectionTestUtils.setField(myPriceTransactionUtil, "httpProxyPort", "8080");
		ReflectionTestUtils.setField(myPriceTransactionUtil, "proxyUser", "m12568");	
		ReflectionTestUtils.setField(myPriceTransactionUtil, "proxyPassword", "Apple2019");
		
		
		
		ServiceMetaData.add(requestParams);
	}

	@Test
	public void testCustomPricingService() {
		
		CustomPricingRequest request = new CustomPricingRequest();
		CustomPricingResponse response = new CustomPricingResponse();
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
		//iCustomPricingService.getCutomPricing(request);
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
		nxMpDealList.add(nxMpDeal);
		Mockito.when(nxMpDealRepository.findBydealIDVersnIdRevId(request.getSolution().getDealId(), request.getSolution().getVersionNumber(), 
				request.getSolution().getRevisionNumber(),new HashSet<String>(Arrays.asList(MyPriceConstants.DEAL_STATUS_APPROVED, CommonConstants.APPROVED)))).thenReturn(nxMpDealList);	
		Mockito.when(nxMpDealRepository.findBydealIDVersnId(request.getSolution().getDealId(),request.getSolution().getVersionNumber(), 
				new HashSet<String>(Arrays.asList(MyPriceConstants.DEAL_STATUS_APPROVED, CommonConstants.APPROVED)))).thenReturn(nxMpDealList);
		NxSolutionDetail nxSolndetail = new NxSolutionDetail();
		nxSolndetail.setActiveYn("Y");
		nxSolndetail.setFlowType("AUTO");
		Mockito.when(repository.findByNxSolutionId(nxMpDealList.get(0).getSolutionId())).thenReturn(nxSolndetail);
		List<NxDesign> nxDesigns = new ArrayList<>();
		NxDesign nxDesign = new NxDesign();
		nxDesign.setAsrItemId("");
		nxDesigns.add(nxDesign);
		Mockito.when(nxDesignRepository.findByNxSolutionDetail(nxSolndetail)).thenReturn(nxDesigns);
		
		Long nxTxnId = nxMpDeal.getNxTxnId();
		List<NxMpPriceDetails> nxMpPriceDetails = new ArrayList<>();
		NxMpPriceDetails nxMpPriceDetail = new NxMpPriceDetails();
		nxMpPriceDetail.setComponentType("Port");
		nxMpPriceDetail.setComponentId(8922174L);
		nxMpPriceDetails.add(nxMpPriceDetail);
		Mockito.when(nxMpPriceDetailsRepository.findByNxDesignIdAndNxTxnId(nxDesign.getNxDesignId(), nxTxnId)).thenReturn(nxMpPriceDetails);
		//iCustomPricingServiceImpl.getCutomPricing(request);
		baseServiceImpl.setSuccessResponse(response);
		
		
	}
	
	@Test
	public void testFMOCustomPricingService() {
		
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
		//iCustomPricingService.getCutomPricing(request);
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
		nxMpDeal.setOfferId("0");
		nxMpDeal.setTransactionId("0");
		nxMpDeal.setNxTxnId(551L);
		nxMpDealList.add(nxMpDeal);
		Mockito.when(nxMpDealRepository.findBydealIDVersnIdRevId(request.getSolution().getDealId(), request.getSolution().getVersionNumber(), 
				request.getSolution().getRevisionNumber(),new HashSet<String>(Arrays.asList(MyPriceConstants.DEAL_STATUS_APPROVED, CommonConstants.APPROVED)))).thenReturn(nxMpDealList);	
		Mockito.when(nxMpDealRepository.findBydealIDVersnId(request.getSolution().getDealId(),request.getSolution().getVersionNumber(), 
				new HashSet<String>(Arrays.asList(MyPriceConstants.DEAL_STATUS_APPROVED, CommonConstants.APPROVED)))).thenReturn(nxMpDealList);
		NxSolutionDetail nxSolndetail = new NxSolutionDetail();
		nxSolndetail.setActiveYn("Y");
		nxSolndetail.setFlowType("FMO");
		Mockito.when(repository.findByNxSolutionId(nxMpDealList.get(0).getSolutionId())).thenReturn(nxSolndetail);
		List<NxDesign> nxDesigns = new ArrayList<>();
		NxDesign nxDesign = new NxDesign();
		nxDesign.setAsrItemId("");
		nxDesigns.add(nxDesign);
		Mockito.when(nxDesignRepository.findByNxSolutionDetail(nxSolndetail)).thenReturn(nxDesigns);
		
		Long nxTxnId = nxMpDeal.getNxTxnId();
		List<NxMpPriceDetails> nxMpPriceDetails = new ArrayList<>();
		NxMpPriceDetails nxMpPriceDetail = new NxMpPriceDetails();
		nxMpPriceDetail.setComponentType("Port");
		nxMpPriceDetail.setComponentId(8922174L);
		nxMpPriceDetails.add(nxMpPriceDetail);
		Mockito.when(nxMpPriceDetailsRepository.findByNxDesignIdAndNxTxnId(nxDesign.getNxDesignId(), nxTxnId)).thenReturn(nxMpPriceDetails);
		String offerId = nxMpDeal.getOfferId();
		Mockito.when(nxMpRepositoryService.getOfferIdByOfferName(nxMpDeal.getOfferId())).thenReturn(offerId);
		//iCustomPricingServiceImpl.getCutomPricing(request);
	
	}
	
	@Test
	public void testINRCustomPricingService() {
		
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
		//iCustomPricingService.getCutomPricing(request);
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
		nxMpDeal.setOfferId("ADE");
		nxMpDeal.setTransactionId("0");
		nxMpDeal.setNxTxnId(128L);
		nxMpDealList.add(nxMpDeal);
		Mockito.when(nxMpDealRepository.findBydealIDVersnIdRevId(request.getSolution().getDealId(), request.getSolution().getVersionNumber(), 
				request.getSolution().getRevisionNumber(),new HashSet<String>(Arrays.asList(MyPriceConstants.DEAL_STATUS_APPROVED, CommonConstants.APPROVED)))).thenReturn(nxMpDealList);	
		Mockito.when(nxMpDealRepository.findBydealIDVersnId(request.getSolution().getDealId(),request.getSolution().getVersionNumber(), 
				new HashSet<String>(Arrays.asList(MyPriceConstants.DEAL_STATUS_APPROVED, CommonConstants.APPROVED)))).thenReturn(nxMpDealList);
		NxSolutionDetail nxSolndetail = new NxSolutionDetail();
		nxSolndetail.setActiveYn("Y");
		nxSolndetail.setFlowType("INR");
		Mockito.when(repository.findByNxSolutionId(nxMpDealList.get(0).getSolutionId())).thenReturn(nxSolndetail);
		List<NxDesign> nxDesigns = new ArrayList<>();
		NxDesign nxDesign = new NxDesign();
		nxDesign.setAsrItemId("");
		nxDesigns.add(nxDesign);
		Mockito.when(nxDesignRepository.findByNxSolutionDetail(nxSolndetail)).thenReturn(nxDesigns);
		
		Long nxTxnId = nxMpDeal.getNxTxnId();
		List<NxMpPriceDetails> nxMpPriceDetails = new ArrayList<>();
		NxMpPriceDetails nxMpPriceDetail = new NxMpPriceDetails();
		nxMpPriceDetail.setComponentType("Port");
		nxMpPriceDetail.setComponentId(8922174L);
		nxMpPriceDetail.setBeid("beid");
		nxMpPriceDetails.add(nxMpPriceDetail);
		Mockito.when(nxMpPriceDetailsRepository.findByNxDesignIdAndNxTxnId(nxDesign.getNxDesignId(), nxTxnId)).thenReturn(nxMpPriceDetails);
		Mockito.when(nxMpPriceDetailsRepository.findByNxTxnId(nxMpDeal.getNxTxnId())).thenReturn(nxMpPriceDetails);
		String offerId = nxMpDeal.getOfferId();
		Mockito.when(nxMpRepositoryService.getOfferIdByOfferName(nxMpDeal.getOfferId())).thenReturn(offerId);
		NxMpSiteDictionary nxMpSiteDic = new NxMpSiteDictionary();
		nxMpSiteDic.setSiteAddress("{ \"siteAddress\" :  [ {\r\n" + 
				"  \"nxSiteId\" : \"\",\r\n" + 
				"  \"country\" : null,\r\n" + 
				"  \"address\" : \"null null null null null null\",\r\n" + 
				"  \"globalLocationId\" : \"123456\",\r\n" + 
				"  \"city\" : null,\r\n" + 
				"  \"postalCode\" : null,\r\n" + 
				"  \"regionFranchiseStatus\" : null,\r\n" + 
				"  \"addressLine\" : \"null null\",\r\n" + 
				"  \"buildingClli\" : null,\r\n" + 
				"  \"addressMatchCode\" : \"NA\",\r\n" + 
				"  \"swcCLLI\" : null,\r\n" + 
				"  \"name\" : \"testSiteName\",\r\n" + 
				"  \"MatchStatus\" : \"NA\",\r\n" + 
				"  \"state\" : null,\r\n" + 
				"  \"validationStatus\" : \"NA\"\r\n" + 
				"} ]}");
		Mockito.when(nxMpSiteDicRepo.findByNxTxnId(nxMpDeal.getNxTxnId())).thenReturn(nxMpSiteDic);
		//iCustomPricingServiceImpl.getCutomPricing(request);
	}
	
	@Test
	public void testASENoDCustomPricingService() throws Exception {
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
		solution.setExternalKey("6511142");
		solution.setProductNumber(null);
		actionDeterminantsList.add(actionDeterminant);
		request.setActionDeterminants(actionDeterminantsList);
		request.setSolution(solution);
		iCustomPricingService.getCutomPricing(request);
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
		nxMpDeal.setOfferId("ASENoD");
		nxMpDeal.setTransactionId("0");
		nxMpDeal.setNxTxnId(128L);
		nxMpDealList.add(nxMpDeal);
		
		/*
		 * when(this.env.getProperty("myprice.aseodReqRatesArrContainer")).thenReturn(
		 * "https://custompricingdev2.att.com/rest/v7/commerceDocumentsOraclecpqo_bmClone_2Transaction/{transactionId}/wl_int_ASEoD_ReqRatesArrContainer_q"
		 * ); when(this.env.getProperty("myprice.username")).thenReturn("salestest");
		 * when(this.env.getProperty("myprice.password")).thenReturn("SalesTest1!");
		 * Proxy proxy = null; AseodReqRatesResponse response = new
		 * AseodReqRatesResponse(); ServiceResponse serviceResponse = new
		 * ServiceResponse();; String uri =
		 * "https://custompricingdev2.att.com/rest/v7/commerceDocumentsOraclecpqo_bmClone_2Transaction/{transactionId}/wl_int_ASEoD_ReqRatesArrContainer_q";
		 * String transResponse ="success"; Map<String, Object> queryParameters = new
		 * HashMap<>(); Map<String, String> headers = new HashMap<>();
		 * queryParameters.put("httpProxyHost", "pxyapp.proxy.att.com");
		 * queryParameters.put("httpProxyPort", "8080");
		 * queryParameters.put("proxyUser", "m12568"); queryParameters.put
		 * ("proxyPassword", "Apple2019");
		 * Mockito.when(myPriceTransactionUtil.getProxy()).thenReturn(proxy);
		 */
		NxSolutionDetail nxSolndetail = new NxSolutionDetail();
		nxSolndetail.setActiveYn("Y");
		nxSolndetail.setFlowType("INR");
		Mockito.when(nxMpDealRepository.findBydealIDVersnId(request.getSolution().getDealId(),request.getSolution().getVersionNumber(), 
				new HashSet<String>(Arrays.asList(MyPriceConstants.DEAL_STATUS_APPROVED, CommonConstants.APPROVED)))).thenReturn(nxMpDealList);
		Mockito.when(repository.findByNxSolutionId(nxMpDealList.get(0).getSolutionId())).thenReturn(nxSolndetail);
		List<AllIncPrice> allIncPriceList = new ArrayList<>();
		AllIncPrice allIncPrice = new AllIncPrice();
		allIncPrice.setNetRate("0");
		allIncPrice.setCosType("PCB");
		allIncPrice.setPbi("");
		allIncPrice.setUsocId("");
		allIncPriceList.add(allIncPrice);
		String transactionId = nxMpDeal.getTransactionId();
		AseodReqRatesResponse aseodReqRatesResponse = null;
		/*
		 * Mockito.when(aseodReqRatesServiceImpl.aseodReqRates(transactionId)).
		 * thenReturn(aseodReqRatesResponse); iCustomPricingServiceImpl = new
		 * ICustomPricingServiceImpl(); ICustomPricingServiceImpl spy =
		 * PowerMockito.spy(iCustomPricingServiceImpl);
		 * PowerMockito.doReturn(allIncPriceList).when(spy
		 * ,METHODNAME,offerId,transactionId );
		 */
		
		AseodReqRatesResponse response = new AseodReqRatesResponse(); 
		 
		when(this.env.getProperty("myprice.aseodReqRatesArrContainer")).thenReturn("https://custompricingdev2.att.com/rest/v7/commerceDocumentsOraclecpqo_bmClone_2Transaction/{transactionId}/wl_int_ASEoD_ReqRatesArrContainer_q");
		when(this.env.getProperty("myprice.username")).thenReturn("salestest");
		when(this.env.getProperty("myprice.password")).thenReturn("SalesTest1!");
		String transResponse = new Object().toString();
		Mockito.when(httpRest.callHttpRestClient(anyString(), any(), anyMap(), anyString(), anyMap(), anyString()))
				.thenReturn(transResponse);
		Mockito.when(restClient.processResult(anyString(), any(Class.class))).thenReturn(response);
		Mockito.when(aseodReqRatesServiceImpl.aseodReqRates(transactionId)).thenReturn(aseodReqRatesResponse);
		iCustomPricingServiceImpl.getCutomPricing(request);
		
		
	}
	
	
	@Test
	public void testCustomPricingServiceAseNod() {
		
		CustomPricingRequest request = new CustomPricingRequest();
		CustomPricingResponse response = new CustomPricingResponse();
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
		//iCustomPricingService.getCutomPricing(request);
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
		nxMpDeal.setOfferId("ASENOD");
		nxMpDeal.setTransactionId("0");
		nxMpDeal.setNxTxnId(551L);
		nxMpDealList.add(nxMpDeal);
		Mockito.when(nxMpDealRepository.findBydealIDVersnIdRevId(request.getSolution().getDealId(), request.getSolution().getVersionNumber(), 
				request.getSolution().getRevisionNumber(),new HashSet<String>(Arrays.asList(MyPriceConstants.DEAL_STATUS_APPROVED, CommonConstants.APPROVED)))).thenReturn(nxMpDealList);	
		Mockito.when(nxMpDealRepository.findBydealIDVersnId(request.getSolution().getDealId(),request.getSolution().getVersionNumber(), 
				new HashSet<String>(Arrays.asList(MyPriceConstants.DEAL_STATUS_APPROVED, CommonConstants.APPROVED)))).thenReturn(nxMpDealList);
		NxSolutionDetail nxSolndetail = new NxSolutionDetail();
		nxSolndetail.setActiveYn("Y");
		nxSolndetail.setFlowType("AUTO");
		Mockito.when(repository.findByNxSolutionId(nxMpDealList.get(0).getSolutionId())).thenReturn(nxSolndetail);
		List<NxDesign> nxDesigns = new ArrayList<>();
		NxDesign nxDesign = new NxDesign();
		nxDesign.setAsrItemId("");
		nxDesigns.add(nxDesign);
		Mockito.when(nxDesignRepository.findByNxSolutionDetail(nxSolndetail)).thenReturn(nxDesigns);
		
		Long nxTxnId = nxMpDeal.getNxTxnId();
		List<NxMpPriceDetails> nxMpPriceDetails = new ArrayList<>();
		NxMpPriceDetails nxMpPriceDetail = new NxMpPriceDetails();
		nxMpPriceDetail.setComponentType("Port");
		nxMpPriceDetail.setComponentId(8922174L);
		nxMpPriceDetails.add(nxMpPriceDetail);
		Mockito.when(nxMpPriceDetailsRepository.findByNxDesignIdAndNxTxnId(nxDesign.getNxDesignId(), nxTxnId)).thenReturn(nxMpPriceDetails);
		//iCustomPricingServiceImpl.getCutomPricing(request);
	}
	
	@Test
	public void testCustomPricingServiceAseNodSpecialConstruction() {
		
		CustomPricingRequest request = new CustomPricingRequest();
		CustomPricingResponse response = new CustomPricingResponse();
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
		//iCustomPricingService.getCutomPricing(request);
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
		nxMpDeal.setOfferId("ASENOD");
		nxMpDeal.setTransactionId("0");
		nxMpDeal.setNxTxnId(551L);
		nxMpDeal.setContractPricingScope("specialConstruction");
		nxMpDealList.add(nxMpDeal);
		Mockito.when(nxMpDealRepository.findBydealIDVersnIdRevId(request.getSolution().getDealId(), request.getSolution().getVersionNumber(), 
				request.getSolution().getRevisionNumber(),new HashSet<String>(Arrays.asList(MyPriceConstants.DEAL_STATUS_APPROVED, CommonConstants.APPROVED)))).thenReturn(nxMpDealList);	
		Mockito.when(nxMpDealRepository.findBydealIDVersnId(request.getSolution().getDealId(),request.getSolution().getVersionNumber(), 
				new HashSet<String>(Arrays.asList(MyPriceConstants.DEAL_STATUS_APPROVED, CommonConstants.APPROVED)))).thenReturn(nxMpDealList);
		NxSolutionDetail nxSolndetail = new NxSolutionDetail();
		nxSolndetail.setActiveYn("Y");
		nxSolndetail.setFlowType("AUTO");
		Mockito.when(repository.findByNxSolutionId(nxMpDealList.get(0).getSolutionId())).thenReturn(nxSolndetail);
		List<NxDesign> nxDesigns = new ArrayList<>();
		NxDesign nxDesign = new NxDesign();
		nxDesign.setAsrItemId("");
		nxDesigns.add(nxDesign);
		Mockito.when(nxDesignRepository.findByNxSolutionDetail(nxSolndetail)).thenReturn(nxDesigns);
		
		Long nxTxnId = nxMpDeal.getNxTxnId();
		List<NxMpPriceDetails> nxMpPriceDetails = new ArrayList<>();
		NxMpPriceDetails nxMpPriceDetail = new NxMpPriceDetails();
		nxMpPriceDetail.setComponentType("Port");
		nxMpPriceDetail.setComponentId(8922174L);
		nxMpPriceDetails.add(nxMpPriceDetail);
		Mockito.when(nxMpPriceDetailsRepository.findByNxDesignIdAndNxTxnId(nxDesign.getNxDesignId(), nxTxnId)).thenReturn(nxMpPriceDetails);
		//iCustomPricingServiceImpl.getCutomPricing(request);
	}

	
}
