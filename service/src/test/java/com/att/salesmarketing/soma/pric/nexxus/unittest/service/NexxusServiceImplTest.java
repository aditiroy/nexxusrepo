package com.att.salesmarketing.soma.pric.nexxus.unittest.service;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import org.apache.commons.collections.CollectionUtils;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.env.Environment;

import com.att.sales.framework.exception.SalesBusinessException;
import com.att.sales.framework.model.ServiceMetaData;
import com.att.sales.framework.model.ServiceResponse;
import com.att.sales.framework.model.Status;
import com.att.sales.nexxus.accesspricing.model.AccessPricingResponse;
import com.att.sales.nexxus.common.CommonConstants;
import com.att.sales.nexxus.common.MessageConstants;
import com.att.sales.nexxus.dao.model.NexxusSolutionDetailUIModel;
import com.att.sales.nexxus.dao.model.NxRequestDetails;
import com.att.sales.nexxus.dao.model.solution.NxSolutionDetail;
import com.att.sales.nexxus.dao.repository.NxAccessPricingDataRepository;
import com.att.sales.nexxus.dao.repository.NxRequestDetailsRepository;
import com.att.sales.nexxus.dao.repository.NxSolutionDetailsRepository;
import com.att.sales.nexxus.edf.model.ManageBillingPriceInvDataRequest;
import com.att.sales.nexxus.edf.model.ManageBillingPriceInvDataResponse;
import com.att.sales.nexxus.edf.model.ManageBillingPriceInventoryDataRequest;
import com.att.sales.nexxus.edf.model.ManageBillingPriceInventoryDataResponse.InnerManageBillingPriceInventoryDataResponse;
import com.att.sales.nexxus.edf.model.PriceInventoryDataRequest;
import com.att.sales.nexxus.edf.model.products;
import com.att.sales.nexxus.handlers.GetOptyInfoWSHandler;
import com.att.sales.nexxus.helper.FileReaderHelper;
import com.att.sales.nexxus.model.APUiResponse;
import com.att.sales.nexxus.model.GUIResponse;
import com.att.sales.nexxus.model.ProductDataLoadRequest;
import com.att.sales.nexxus.reteriveicb.model.NexxusTestRequest;
import com.att.sales.nexxus.reteriveicb.model.NexxusTestResponse;
import com.att.sales.nexxus.rome.model.GetBillingChargesRequest;
import com.att.sales.nexxus.rome.model.GetBillingChargesResponse;
import com.att.sales.nexxus.rome.model.GetOptyRequest;
import com.att.sales.nexxus.rome.model.GetOptyResponse;
import com.att.sales.nexxus.rome.service.GetBillingChargesServiceImpl;
import com.att.sales.nexxus.rome.service.GetOptyInfoServiceImpl;
import com.att.sales.nexxus.service.AccessPricingService;
import com.att.sales.nexxus.service.NexxusServiceImpl;
import com.att.sales.nexxus.util.AuditUtil;
import com.att.sales.nexxus.util.DME2RestClient;
import com.att.sales.util.StringUtils;
import com.fasterxml.jackson.databind.JsonNode;

/*
 * chandan(ck218y)
 */
@ExtendWith(MockitoExtension.class)
public class NexxusServiceImplTest {

	@InjectMocks
	NexxusServiceImpl service = new NexxusServiceImpl();

	@Mock
	ProductDataLoadRequest productDataLoadRequest;

	//@Mock
	//MultipartBody multipart;
	@Mock
	com.fasterxml.jackson.databind.ObjectMapper nexxusObjectMapper = new com.fasterxml.jackson.databind.ObjectMapper();

	@Mock
	DME2RestClient dme;

	@Mock
	private GetBillingChargesServiceImpl getBillingChargesServiceImpl;

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
	JsonNode jsonNode;

	@Mock
	AuditUtil auditUtil;

	@BeforeEach
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

	}
@Disabled
	@Test
	public void testputUploadASENexxusFile() throws SalesBusinessException, IOException {
		SalesBusinessException exc = new SalesBusinessException(MessageConstants.PROCESS_ERROR_CODE);
		String string = "etc/process/";
		//productDataLoadRequest.setMultipartBody(multipart);
		//Mockito.when(productDataLoadRequest.getMultipartBody()).thenReturn(multipart);
		Mockito.when(env.getProperty(Mockito.anyString())).thenReturn(string);
		// test.putUploadASENexxusFile(productDataLoadRequest);
		service.putUploadASENexxusFile(productDataLoadRequest);

	}

	/*
	 * @Test public void testPutProductDataLoad() throws SalesBusinessException {
	 * ProductDataLoadRequest dataLoadRequest = new ProductDataLoadRequest();
	 * dataLoadRequest.setMultipartBody(multipart);
	 * service.putProductDataLoad(productDataLoadRequest); }
	 */

	// @Test
	public void testTransformTestData() {
		NexxusTestRequest request = new NexxusTestRequest();
		request.setSolutionId(12345L);
		request.setSolutionName("solutionName");
		NexxusTestResponse resp = new NexxusTestResponse();
		resp.setSolutionId(12345L);

		service.transformTestData(request);
	}

	@Test
	public void testSetDesignDataLoadData() throws SalesBusinessException {
		String string = "string";
		service.setDesignDataLoadData(string);
	}

	@Disabled
	@Test
	public void testFetchNexxusSolutionByUserIdForFilterElse() throws SalesBusinessException {
		LinkedHashMap<String, Object> queryParams = new LinkedHashMap<>();
		String userId = "";
		String optyId = "";
		queryParams.put("userId", userId);
		queryParams.put("filter", "584");
		queryParams.put("ROW_FETCH_COUNT", 200L);
		queryParams.put("fetchBatchIndex", 1);
		String[] flowTypes = { "INR", "FMO", "iglooQuote" };
		queryParams.put("flowType", Arrays.asList(flowTypes));
		when(em.createNamedQuery(any(), eq(NexxusSolutionDetailUIModel.class)))
				.thenReturn(typedQueryNexxusSolutionDetailUIModel);
		NexxusSolutionDetailUIModel nexxusSolnDetailUIModel = new NexxusSolutionDetailUIModel();
		nexxusSolnDetailUIModel.setCreatedUser("aa316k");
		nexxusSolnDetailUIModel.setCustomerName("Akash");
		nexxusSolnDetailUIModel.setNxSolutionId(584L);
		List<NexxusSolutionDetailUIModel> resultList = null;
		when(typedQueryNexxusSolutionDetailUIModel.getResultList()).thenReturn(resultList);
		service.fetchNexxusSolutionsByUserId(queryParams);

	}

	/*
	 * @Test public void testBillingPriceInventoryData() { String uri=""; String
	 * subContext=""; String requestPayLoad=""; String aftLatitude=""; String
	 * aftLongitude=""; String aftEnvironment=""; String userName=""; String
	 * password=""; ManageBillingPriceInventoryDataResponse response=new
	 * ManageBillingPriceInventoryDataResponse(); ManageBillingPriceInvDataRequest
	 * inventoryrequest=new ManageBillingPriceInvDataRequest();
	 * inventryRequest.setBill_Month(new Date());
	 * inventryRequest.setCustomer_name("abc"); Report_Key report=new Report_Key();
	 * report.setDuns_number("Duns_number");
	 * report.setL3_sub_acct_id("l3_sub_acct_id");
	 * report.setL4_acct_id("l4_acct_id");
	 * report.setL5_master_acct_id("l5_master_acct_id"); List<Report_Key>
	 * Report_key=new ArrayList<>(); Report_key.add(report);
	 * inventoryrequest.setRepo //Mockito.when(dme.callDme2Client(uri, subContext,
	 * requestPayLoad, aftLatitude, aftLongitude, aftEnvironment, userName,
	 * password)); service.getBillingPriceInventryData(inventryRequest); }
	 */
	@Test
	public void testPutUploadASENexxusFile() throws IOException, SalesBusinessException {
		ProductDataLoadRequest productDataLoadRequest = new ProductDataLoadRequest();
		String fileName = "FILENAME";
		// Mockito.when(productDataLoadRequest.getMultipartBody()).thenReturn(multipart);
		service.putUploadASENexxusFile(productDataLoadRequest);
	}

	@Test
	public void testFetchNexxusSolutionByUserId() throws SalesBusinessException {
		LinkedHashMap<String, Object> queryParams = new LinkedHashMap<>();
		String userId = "";
		String optyId = "";
		queryParams.put("userId", userId);
		queryParams.put("externalId", "1234");
		queryParams.put("optyId", optyId);
		queryParams.put("ROW_FETCH_COUNT", 200L);
		queryParams.put("fetchBatchIndex", 1);
		String[] flowTypes = { "INR", "FMO", "iglooQuote" };
		queryParams.put("flowType", Arrays.asList(flowTypes));
		when(em.createNamedQuery(any(), eq(NexxusSolutionDetailUIModel.class)))
				.thenReturn(typedQueryNexxusSolutionDetailUIModel);
		NexxusSolutionDetailUIModel nexxusSolnDetailUIModel = new NexxusSolutionDetailUIModel();
		nexxusSolnDetailUIModel.setCreatedUser("aa316k");
		nexxusSolnDetailUIModel.setCustomerName("Akash");
		nexxusSolnDetailUIModel.setNxSolutionId(584L);
		List<NexxusSolutionDetailUIModel> resultList = Arrays.asList(nexxusSolnDetailUIModel);
		when(typedQueryNexxusSolutionDetailUIModel.getResultList()).thenReturn(resultList);
		service.fetchNexxusSolutionsByUserId(queryParams);

	}

	@Test
	public void testFetchNexxusSolutionByUserIdIf() throws SalesBusinessException {
		LinkedHashMap<String, Object> queryParams = new LinkedHashMap<>();
		// String userId = "";
		String optyId = "";
		String externalId = "";
		queryParams.put("userId", "ec006e");
		queryParams.put("externalId", externalId);
		queryParams.put("optyId", optyId);
		queryParams.put("ROW_FETCH_COUNT", 200L);
		queryParams.put("fetchBatchIndex", 1);
		String[] flowTypes = { "INR", "FMO", "iglooQuote" };
		queryParams.put("flowType", Arrays.asList(flowTypes));
		when(em.createNamedQuery(any(), eq(NexxusSolutionDetailUIModel.class)))
				.thenReturn(typedQueryNexxusSolutionDetailUIModel);
		NexxusSolutionDetailUIModel nexxusSolnDetailUIModel = new NexxusSolutionDetailUIModel();
		nexxusSolnDetailUIModel.setCreatedUser("aa316k");
		nexxusSolnDetailUIModel.setCustomerName("Akash");
		nexxusSolnDetailUIModel.setNxSolutionId(584L);
		List<NexxusSolutionDetailUIModel> resultList = Arrays.asList(nexxusSolnDetailUIModel);
		when(typedQueryNexxusSolutionDetailUIModel.getResultList()).thenReturn(resultList);
		service.fetchNexxusSolutionsByUserId(queryParams);

	}

	@Test
	public void testFetchNexxusSolutionByUserIdIfElse() throws SalesBusinessException {
		LinkedHashMap<String, Object> queryParams = new LinkedHashMap<>();
		// String userId = "";
		String optyId = "";
		String externalId = "";

		queryParams.put("userId", "ec006e");
		queryParams.put("externalId", externalId);
		queryParams.put("optyId", "63736");
		queryParams.put("ROW_FETCH_COUNT", 200L);
		queryParams.put("fetchBatchIndex", 1);
		String[] flowTypes = { "INR", "FMO", "iglooQuote" };
		queryParams.put("flowType", Arrays.asList(flowTypes));

		when(em.createNamedQuery(any(), eq(NexxusSolutionDetailUIModel.class)))
				.thenReturn(typedQueryNexxusSolutionDetailUIModel);
		NexxusSolutionDetailUIModel nexxusSolnDetailUIModel = new NexxusSolutionDetailUIModel();
		nexxusSolnDetailUIModel.setCreatedUser("aa316k");
		nexxusSolnDetailUIModel.setCustomerName("Akash");
		nexxusSolnDetailUIModel.setNxSolutionId(584L);
		List<NexxusSolutionDetailUIModel> resultList = Arrays.asList(nexxusSolnDetailUIModel);
		when(typedQueryNexxusSolutionDetailUIModel.getResultList()).thenReturn(resultList);
		service.fetchNexxusSolutionsByUserId(queryParams);

	}

	@Test
	public void testFetchNexxusSolutionByUserIdIfElse1() throws SalesBusinessException {
		LinkedHashMap<String, Object> queryParams = new LinkedHashMap<>();
		String userId = "";
		String optyId = "";
		String externalId = "";
		queryParams.put("userId", userId);
		queryParams.put("externalId", externalId);
		queryParams.put("optyId", optyId);
		queryParams.put("nxId", "63736");
		queryParams.put("ROW_FETCH_COUNT", 200L);
		queryParams.put("fetchBatchIndex", 1);
		String[] flowTypes = { "INR", "FMO", "iglooQuote" };
		queryParams.put("flowType", Arrays.asList(flowTypes));
		when(em.createNamedQuery(any(), eq(NexxusSolutionDetailUIModel.class)))
				.thenReturn(typedQueryNexxusSolutionDetailUIModel);
		NexxusSolutionDetailUIModel nexxusSolnDetailUIModel = new NexxusSolutionDetailUIModel();
		nexxusSolnDetailUIModel.setCreatedUser("aa316k");
		nexxusSolnDetailUIModel.setCustomerName("Akash");
		nexxusSolnDetailUIModel.setNxSolutionId(584L);
		List<NexxusSolutionDetailUIModel> resultList = Arrays.asList(nexxusSolnDetailUIModel);

		when(typedQueryNexxusSolutionDetailUIModel.getResultList()).thenReturn(resultList);
		service.fetchNexxusSolutionsByUserId(queryParams);

	}

	@Test
	public void testFetchNexxusSolutionByUserIdSuccess() throws SalesBusinessException {
		LinkedHashMap<String, Object> queryParams = new LinkedHashMap<>();
		String userId = "";
		String optyId = "";
		String externalId = "";
		queryParams.put("userId", userId);
		queryParams.put("externalId", externalId);
		queryParams.put("optyId", optyId);
		queryParams.put("nxId", "63736");
		when(em.createNamedQuery(any(), eq(NexxusSolutionDetailUIModel.class)))
				.thenReturn(typedQueryNexxusSolutionDetailUIModel);
		NexxusSolutionDetailUIModel nexxusSolnDetailUIModel = new NexxusSolutionDetailUIModel();
		nexxusSolnDetailUIModel.setCreatedUser("aa316k");
		nexxusSolnDetailUIModel.setCustomerName("Akash");
		nexxusSolnDetailUIModel.setNxSolutionId(584L);
		List<NexxusSolutionDetailUIModel> resultList = null;

		when(typedQueryNexxusSolutionDetailUIModel.getResultList()).thenReturn(resultList);
		service.fetchNexxusSolutionsByUserId(queryParams);

	}

//	@Ignore
//	@Test
//	public void testFetchNexxusSolutionByUserIdForFilter() throws SalesBusinessException {
//		LinkedHashMap<String, Object> queryParams = new LinkedHashMap<>();
//		String userId = "";
//		String optyId = "";
//		queryParams.put("userId", userId);
//		queryParams.put("filter", "584");
//		queryParams.put("ROW_FETCH_COUNT", 200L);
//		queryParams.put("fetchBatchIndex", 1);
//		String[] flowTypes = {"INR","FMO","iglooQuote"};
//		queryParams.put("flowType", Arrays.asList(flowTypes));
//		when(em.createNamedQuery(any(), eq(NexxusSolutionDetailUIModel.class)))
//				.thenReturn(typedQueryNexxusSolutionDetailUIModel);
//		NexxusSolutionDetailUIModel nexxusSolnDetailUIModel = new NexxusSolutionDetailUIModel();
//		nexxusSolnDetailUIModel.setCreatedUser("aa316k");
//		nexxusSolnDetailUIModel.setCustomerName("Akash");
//		nexxusSolnDetailUIModel.setNxSolutionId(584L);
//		List<NexxusSolutionDetailUIModel> resultList = Arrays.asList(nexxusSolnDetailUIModel);
//		when(typedQueryNexxusSolutionDetailUIModel.getResultList()).thenReturn(resultList);
//		service.fetchNexxusSolutionsByUserId(queryParams);
//
//	}

//	@Test(expected = SalesBusinessException.class)
//	public void testGetBillingPriceInventorydata() throws Exception {
//		ManageBillingPriceInvDataRequest inventoryrequest = new ManageBillingPriceInvDataRequest();
//		PriceInventoryDataRequest req = new PriceInventoryDataRequest();
//		req.setBillMonth("536gd");
//		req.setCustomerName("STARK");
//		req.setDunsNumber("64738");
//		req.setGlobalUtlDunsNbr("globalUtlDunsNbr");
//		req.setL3SubAcctId("l3SubAcctId");
//		req.setL4AcctId("l4AcctId");
//		req.setL5MasterAcctId("l5MasterAcctId");
//		req.setMcn("mcn");
//		req.setProduct("AVPN");
//		List<PriceInventoryDataRequest> inventoryList = new ArrayList<>();
//		inventoryList.add(req);
//		inventoryrequest.setInventoryList(inventoryList);
//		ManageBillingPriceInventoryDataResponse resp = new ManageBillingPriceInventoryDataResponse();
//		InnerManageBillingPriceInventoryDataResponse innerResponse = new InnerManageBillingPriceInventoryDataResponse();
//		innerResponse.setRequestId("763287sgdh");
//		resp.setManageBillingPriceInventoryDataResponse(innerResponse);
//		List<ManageBillingPriceInventoryDataResponse> edfResponseList = new ArrayList<>();
//		edfResponseList.add(resp);
//		ManageBillDataInv inventryRequest = new ManageBillDataInv();
//		 ManageBillingPriceInventoryDataRequest invDataRequest=new  ManageBillingPriceInventoryDataRequest();
//		 invDataRequest.setBill_Month("bill_Month");
//		 invDataRequest.setCustomer_name("customer_name");
//		 invDataRequest.setProduct("AVPN");
//		 Report_Key report=new Report_Key();
//		 report.setDuns_number("duns_number");
//		 report.setL3_sub_acct_id("l3_sub_acct_id");
//		 report.setL4_acct_id("l4_acct_id");
//		 report.setL5_master_acct_id("l5_master_acct_id");
//		 List<Report_Key> reportList=new ArrayList<>();
//		 reportList.add(report);
//		 invDataRequest.setReport_key(reportList);
//		inventryRequest.setManageBillingPriceInventoryDataRequest(invDataRequest);
//		String status = "success";
//		List<NxRequestDetails> detailsList=new ArrayList<>();
//		NxRequestDetails details = new NxRequestDetails();
//		details.setCreatedDate(new Date());
//		details.setCpniApprover("cpniApprover");
//		details.setEdfAckId("edfAckId");
//		details.setFlowType("INR");
//		details.setNxReqDesc("nxReqDesc");
//		details.setProduct("AVPN");
//		details.setUser("ec006e");
//		details.setAcctCriteria("acctCriteria");
//		details.setStatus(new Long(200));
//		detailsList.add(details);
//		// Mockito.when(resp.getManageBillingPriceInventoryDataResponse().getStatus()).thenReturn(status);
//		Mockito.when(dme.getBillingPriceInventryUri(Mockito.anyObject())).thenReturn(resp);
//
////		service.getBillingPriceInventryData(inventoryrequest);
//
//	}
//	

//	@Test
//	public void testGetInternal() throws Exception {
//		service.getInternalTest();
//	}

	@Test
	public void testSaveInventoryPricingRequestData() throws SalesBusinessException {
		ManageBillingPriceInvDataResponse resp = new ManageBillingPriceInvDataResponse();
		ManageBillingPriceInvDataRequest inventoryrequest = new ManageBillingPriceInvDataRequest();
		List<NxRequestDetails> nxRequestDetails = new ArrayList<>();
		NxRequestDetails details = new NxRequestDetails();
		inventoryrequest.setNxSolutionId("12345");
		inventoryrequest.setAttuid("ec006e");
		inventoryrequest.setOptyId("675589");
		details.setDmaapMsg("dmaapMsg");
		nxRequestDetails.add(details);
		GetOptyRequest optyRequest = new GetOptyRequest();
		optyRequest.setAction("createSolution");
		optyRequest.setAttuid("ec006e");
		optyRequest.setOptyId("675589");
		GetOptyResponse optyResponse = new GetOptyResponse();
		NxSolutionDetail solution = new NxSolutionDetail();
		solution.setNxSolutionId(new Long(2l));
		List<NxSolutionDetail> solutionlist = new ArrayList<>();
		solutionlist.add(solution);
		Mockito.when(repo.saveAll(nxRequestDetails)).thenReturn(nxRequestDetails);
		Mockito.when(optyInfoServiceImpl.performGetOptyInfo(Mockito.any())).thenReturn(optyResponse);
		Mockito.when(solutionRepo.findByNxSolutionId(Mockito.anyLong())).thenReturn(solutionlist.get(0));
		try {
			service.saveInventoryPricingRequestData(inventoryrequest, nxRequestDetails, resp, false);
		} catch (Exception e) {

		}

	}

	@Test
	public void testSaveInventoryPricingRequestDataforIf() throws SalesBusinessException {
		ManageBillingPriceInvDataResponse resp = new ManageBillingPriceInvDataResponse();
		ManageBillingPriceInvDataRequest inventoryrequest = new ManageBillingPriceInvDataRequest();
		List<NxRequestDetails> nxRequestDetails = new ArrayList<>();
		NxRequestDetails details = new NxRequestDetails();
		inventoryrequest.setNxSolutionId("");
		inventoryrequest.setAttuid("ec006e");
		inventoryrequest.setOptyId("675589");
		details.setDmaapMsg("dmaapMsg");
		nxRequestDetails.add(details);
		GetOptyRequest optyRequest = new GetOptyRequest();
		optyRequest.setAction("createSolution");
		optyRequest.setAttuid("ec006e");
		optyRequest.setOptyId("675589");
		optyRequest.setHrId("ec006e");
		optyRequest.setNxSolutionId(new Long(2));
		optyRequest.setSolutionDescription("description");
		GetOptyResponse optyResponse = new GetOptyResponse();
		optyResponse.setNxSolutionId(new Long(2));
		Mockito.when(repo.saveAll(nxRequestDetails)).thenReturn(nxRequestDetails);
		Mockito.when(optyInfoServiceImpl.performGetOptyInfo(Mockito.any())).thenReturn(optyResponse);
		try {
			service.saveInventoryPricingRequestData(inventoryrequest, nxRequestDetails, resp, false);
		} catch (Exception e) {

		}
	}

	@Disabled
	@Test
	public void testSaveInventoryPricingRequestDataforElse() throws SalesBusinessException {
		ManageBillingPriceInvDataResponse resp = new ManageBillingPriceInvDataResponse();
		ManageBillingPriceInvDataRequest inventoryrequest = new ManageBillingPriceInvDataRequest();
		List<NxRequestDetails> nxRequestDetails = new ArrayList<>();
		NxRequestDetails details = new NxRequestDetails();
		inventoryrequest.setNxSolutionId("");
		inventoryrequest.setAttuid("ec006e");
		inventoryrequest.setOptyId("675589");
		details.setDmaapMsg("dmaapMsg");
		nxRequestDetails.add(details);
		GetOptyRequest optyRequest = new GetOptyRequest();
		optyRequest.setAction("createSolution");
		optyRequest.setAttuid("ec006e");
		optyRequest.setOptyId("675589");
		optyRequest.setHrId("ec006e");
		optyRequest.setNxSolutionId(new Long(2));
		optyRequest.setSolutionDescription("description");
		GetOptyResponse optyResponse = new GetOptyResponse();
		optyResponse.setNxSolutionId(null);
		Status status = new Status();
		status.setCode("200");
		optyResponse.setStatus(status);
		Mockito.when(repo.saveAll(nxRequestDetails)).thenReturn(nxRequestDetails);
		Mockito.when(optyInfoServiceImpl.performGetOptyInfo(Mockito.any())).thenReturn(optyResponse);
//		service.saveInventoryPricingRequestData(inventoryrequest, nxRequestDetails, resp, false);

	}

	/*
	 * @Test public void testSaveInventoryPricingRequestDataIf() throws
	 * SalesBusinessException { ManageBillingPriceInvDataResponse resp = new
	 * ManageBillingPriceInvDataResponse(); ManageBillingPriceInvDataRequest
	 * inventoryrequest=new ManageBillingPriceInvDataRequest();
	 * List<NxRequestDetails> nxRequestDetails = new ArrayList<>(); NxRequestDetails
	 * details = new NxRequestDetails(); inventoryrequest.setNxSolutionId(null);
	 * inventoryrequest.setAttuid("ec006e"); inventoryrequest.setOptyId("675589");
	 * details.setDmaapMsg("dmaapMsg"); nxRequestDetails.add(details);
	 * GetOptyRequest optyRequest = new GetOptyRequest();
	 * optyRequest.setAction("createSolution"); optyRequest.setAttuid("ec006e");
	 * optyRequest.setOptyId("675589"); optyRequest.setNxSolutionId(new Long(2l));
	 * GetOptyResponse optyResponse=new GetOptyResponse();
	 * optyResponse.setCustomerName("STARK"); optyResponse.setDunsNumber("789");
	 * optyResponse.setOptyId("675589"); optyResponse.setNxSolutionId(new Long(2l));
	 * 
	 * //Mockito.when(repo.save(nxRequestDetails)).thenReturn(nxRequestDetails);
	 * Mockito.when(optyInfoServiceImpl.performGetOptyInfo(optyRequest)).thenReturn(
	 * optyResponse);
	 * //doReturn(optyResponse).when(optyInfoServiceImpl).performGetOptyInfo(
	 * optyRequest);
	 * //Mockito.when(optyResponse.getNxSolutionId()).thenReturn(Mockito.anyLong());
	 * service.saveInventoryPricingRequestData(inventoryrequest, nxRequestDetails,
	 * resp);
	 * 
	 * }
	 */
	/*
	 * @Test public void testEqueals() { Object obj=new Object(); obj=true;
	 * service.equals(obj); }
	 */

	@Test
	public void testRetrieveBillingCharges() throws Exception {
		GetBillingChargesRequest inventoryrequest = new GetBillingChargesRequest();
		List<products> reqProductList = new ArrayList<products>();
		products p = new products();
		p.setActive("Y");
		p.setProduct("AVPN");
		p.setProductType("SERVICE_GROUP");
		reqProductList.add(p);

		inventoryrequest.setProducts(reqProductList);
		inventoryrequest.setNxSolutionId("12345");
		inventoryrequest.setAttuid("12345");
		inventoryrequest.setBeginBillMonth("202111");
		inventoryrequest.setCpniApprover("approver");
		JSONObject obj = new JSONObject("{\"searchCriteria\": \"MCN\", \"mcn\": \"808810,808810\"}");
		inventoryrequest.setManageBillingPriceInventoryDataRequest(obj);

		PriceInventoryDataRequest req = new PriceInventoryDataRequest();

		req.setBillMonth("536gd");
		req.setMcn("mcn");
		req.setProduct("AVPN");
		List<PriceInventoryDataRequest> inventoryList = new ArrayList<>();
		inventoryList.add(req);
		GetBillingChargesResponse resp = new GetBillingChargesResponse();
		resp.setResponseCode("0000");
		InnerManageBillingPriceInventoryDataResponse innerResponse = new InnerManageBillingPriceInventoryDataResponse();
		innerResponse.setRequestId("763287sgdh");

		ManageBillingPriceInventoryDataRequest invDataRequest = new ManageBillingPriceInventoryDataRequest();
		invDataRequest.setBill_Month("bill_Month");
		invDataRequest.setCustomer_name("customer_name");
		invDataRequest.setProduct("AVPN");

		List<NxRequestDetails> detailsList = new ArrayList<>();
		NxRequestDetails details = new NxRequestDetails();
		details.setCreatedDate(new Date());
		details.setCpniApprover("cpniApprover");
		details.setEdfAckId("edfAckId");
		details.setFlowType("INR");
		details.setNxReqDesc("nxReqDesc");
		details.setProduct("AVPN");
		details.setUser("ec006e");
		details.setAcctCriteria("acctCriteria");
		details.setStatus(new Long(200));
		detailsList.add(details);
		Mockito.when(getBillingChargesServiceImpl.performGetBillingCharges(Mockito.any())).thenReturn(resp);
		Mockito.doNothing().when(auditUtil).addActionToNxUiAudit(anyLong(), anyString(), anyString(), anyString(),
				anyLong(), anyString(), anyLong(),anyString());
		Mockito.when(nexxusObjectMapper.writeValueAsString(Mockito.any()))
				.thenReturn("{\"searchCriteria\": \"MCN\", \"mcn\": \"808810,808810\"}");

		GetOptyResponse optyResponse = new GetOptyResponse();
		NxSolutionDetail solution = new NxSolutionDetail();
		solution.setNxSolutionId(new Long(2l));
		List<NxSolutionDetail> solutionlist = new ArrayList<>();
		solutionlist.add(solution);
		Mockito.when(optyInfoServiceImpl.performGetOptyInfo(Mockito.any())).thenReturn(optyResponse);
		Mockito.when(solutionRepo.findByNxSolutionId(Mockito.anyLong())).thenReturn(solutionlist.get(0));
		try {
			service.retrieveBillingCharges(inventoryrequest);
		} catch (Exception e) {

		}

	}

}
