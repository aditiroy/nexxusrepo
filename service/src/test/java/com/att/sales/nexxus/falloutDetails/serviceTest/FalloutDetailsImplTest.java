package com.att.sales.nexxus.falloutDetails.serviceTest;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyCollection;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.spy;

import java.io.IOException;
import java.io.Reader;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.att.sales.framework.exception.SalesBusinessException;
import com.att.sales.framework.model.ServiceMetaData;
import com.att.sales.framework.model.ServiceResponse;
import com.att.sales.framework.model.Status;
import com.att.sales.nexxus.common.CommonConstants;
import com.att.sales.nexxus.constant.MyPriceConstants;
import com.att.sales.nexxus.constant.StringConstants;
import com.att.sales.nexxus.dao.model.FailedBeidDetails;
import com.att.sales.nexxus.dao.model.NxAdminUserModel;
import com.att.sales.nexxus.dao.model.NxDesignAudit;
import com.att.sales.nexxus.dao.model.NxLookupData;
import com.att.sales.nexxus.dao.model.NxOutputFileModel;
import com.att.sales.nexxus.dao.model.NxRequestDetails;
import com.att.sales.nexxus.dao.model.NxRequestGroup;
import com.att.sales.nexxus.dao.model.NxSolutionSite;
import com.att.sales.nexxus.dao.model.NxUser;
import com.att.sales.nexxus.dao.model.solution.NxSolutionDetail;
import com.att.sales.nexxus.dao.model.solution.NxTeam;
import com.att.sales.nexxus.dao.repository.HybridRepositoryService;
import com.att.sales.nexxus.dao.repository.NxAccessPricingDataRepository;
import com.att.sales.nexxus.dao.repository.NxAdminUserRepository;
import com.att.sales.nexxus.dao.repository.NxDesignAuditRepository;
import com.att.sales.nexxus.dao.repository.NxLookupDataRepository;
import com.att.sales.nexxus.dao.repository.NxRequestDetailsRepository;
import com.att.sales.nexxus.dao.repository.NxRequestGroupRepository;
import com.att.sales.nexxus.dao.repository.NxSolutionDetailsRepository;
import com.att.sales.nexxus.dao.repository.NxSolutionSiteRepository;
import com.att.sales.nexxus.dao.repository.NxTeamRepository;
import com.att.sales.nexxus.dao.repository.NxUserRepository;
import com.att.sales.nexxus.edf.model.ManageBillDataInv;
import com.att.sales.nexxus.edf.model.ManageBillingPriceInventoryDataRequest;
import com.att.sales.nexxus.edf.model.ManageBillingPriceInventoryDataResponse;
import com.att.sales.nexxus.edf.model.ManageBillingPriceInventoryDataResponse.InnerManageBillingPriceInventoryDataResponse;
import com.att.sales.nexxus.edf.model.ValidateAccountDataRequest;
import com.att.sales.nexxus.edf.model.ValidateAccountDataResponse;
import com.att.sales.nexxus.fallout.model.AdminUserList;
import com.att.sales.nexxus.fallout.model.FalloutDetailsRequest;
import com.att.sales.nexxus.fallout.model.FalloutDetailsResponse;
import com.att.sales.nexxus.fallout.service.FalloutDetailsImpl;
import com.att.sales.nexxus.inr.InrFallOutData;
import com.att.sales.nexxus.rome.model.GetOptyResponse;
import com.att.sales.nexxus.rome.service.GetOptyInfoServiceImpl;
import com.att.sales.nexxus.service.FmoProcessingService;
import com.att.sales.nexxus.service.InrProcessingService;
import com.att.sales.nexxus.service.InrQualifyService;
import com.att.sales.nexxus.service.NexxusAIService;
import com.att.sales.nexxus.service.NexxusService;
import com.att.sales.nexxus.service.NxMyPriceRepositoryServce;
import com.att.sales.nexxus.service.SubmitToMyPriceService;
import com.att.sales.nexxus.userdetails.service.UserServiceImpl;
import com.att.sales.nexxus.util.DME2RestClient;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
/*
 * chandan(ck218y)
 */
@ExtendWith(MockitoExtension.class)
public class FalloutDetailsImplTest {

	@InjectMocks
	private FalloutDetailsImpl falloutDetailsImpl;

	@Mock
	private HybridRepositoryService hybridRepo;
	
	@Mock
	private NexxusAIService nexxusAIService;
	
	@Mock
	private InrProcessingService inrProcessingService;
	
	@Mock
	private FmoProcessingService fmoProcessingService;

	@Mock
	private ObjectMapper mapper;
	
	@Mock
	private Reader reader;

	@Mock
	private DME2RestClient dme;

	@Mock
	private NxRequestGroupRepository nxRequestGroupRepository;
	
	@Mock
	private NxSolutionDetailsRepository nxSolutionDetailsRepository;
	
	@Mock
	private NxRequestDetailsRepository nxRequestDetailsRepository;
	
	@Mock
	private NxAccessPricingDataRepository nxAccessPricingDataRepository;
	
	@Mock
	private SubmitToMyPriceService submitToMyPriceService;
	
	@Mock
	private NexxusService nexxusService;
	
	@Mock
	private InrQualifyService inrQualifyService;
	
	@Mock
	private NxSolutionSiteRepository nxSolutionSiteRepository;
	
	@Mock
	private NxLookupDataRepository nxLookupDataRepository;
	
	@Mock
	private NxAdminUserRepository nxAdminUserRepository;
	
	@Mock
	private NxMyPriceRepositoryServce nxMyPriceRepositoryServce;
	
	@Mock
	private NxTeamRepository nxTeamRepository;
	
	private static List<NxLookupData> copyStatusLookup;
	
	@Mock
	private GetOptyInfoServiceImpl optyInfoServiceImpl;

	@Mock
	private UserServiceImpl userServiceImpl;

	@Mock
	private NxUserRepository nxUserRepository;
	
	@Mock
	private NxDesignAuditRepository nxDesignAuditRepository;
	
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

		ServiceMetaData.add(map);
		
		copyStatusLookup = new ArrayList<>();
		NxLookupData nx = new NxLookupData();
		nx.setItemId("200");
		nx.setDescription("30");
		nx.setCriteria("30,90");
		copyStatusLookup.add(nx);
	}

	@Test
	public void testRetreivefallout() throws SalesBusinessException, SQLException, JsonParseException, JsonMappingException, IOException {
		FalloutDetailsRequest request = new FalloutDetailsRequest();
		request.setAction("retrieveFalloutDetails");
		request.setNxReqId(12345l);
		request.setProduct("AVPN");
		List<NxOutputFileModel> fileModels = new ArrayList<>();
		NxOutputFileModel value = new NxOutputFileModel();
		value.setId(12345L);
		value.setFallOutData("fallOutData");
		// falloutData.setCharacterStream(123456l);
		// value.setFalloutData(falloutData);
		fileModels.add(value);
		FalloutDetailsResponse falloutResp =new FalloutDetailsResponse(); 
		falloutResp.setNxSolutionId(new Long(2l));
		falloutResp.setNxReqId(new Long(5l));
		InrFallOutData fallOutData=new InrFallOutData();
		Set<String> set=new HashSet<>();
		String s1="beid";
		set.add(s1);
		fallOutData.setBeid(set);
		Set<Map<String,String>> setMap=new HashSet<>();
		Map<String,String> map=new HashMap<>();
		String s2="map";
		map.put(s2, s2);
		setMap.add(map);
		fallOutData.setQueryParameter(setMap);
		String value1 = fileModels.get(0).getFallOutData();
		Mockito.when(mapper.readValue(value1, InrFallOutData.class)).thenReturn(fallOutData);
		//NxOutputFileModel model=new NxOutputFileModel();
		Mockito.when(nexxusAIService.getNxPredictions(Mockito.anySet(), Mockito.anyString())).thenReturn(true);
		//String value1=fileModels.get(0).getFallOutData();
       // Mockito.when(fileModels.get(0).getFallOutData()).thenReturn(s1);
		//Mockito.when(mapper.readValue(Mockito.anyString(),InrFallOutData.class)).thenReturn(fallOutData);
		Mockito.when(hybridRepo.getNxOutputFileRepository(Mockito.anyLong())).thenReturn(fileModels);
		// Mockito.when(falloutData.getCharacterStream()).thenReturn(reader);
		Mockito.when(nxDesignAuditRepository.findByNxRefIdAndTransaction(anyLong(), anyString())).thenReturn(new NxDesignAudit());	
			falloutDetailsImpl.nexxusRequestActions(request);
			
	}
	
	@Test
	public void testRetreivefalloutIf() throws SalesBusinessException, SQLException {
		FalloutDetailsRequest request = new FalloutDetailsRequest();
		request.setAction("retrieveFalloutDetails");
		request.setNxReqId(12345l);
		List<NxOutputFileModel> fileModels = new ArrayList<>();
		NxOutputFileModel value = new NxOutputFileModel();
		value.setId(12345L);
		fileModels.add(value);
		FalloutDetailsResponse falloutResp =new FalloutDetailsResponse(); 
		falloutResp.setNxSolutionId(new Long(2l));
		falloutResp.setNxReqId(new Long(5l));
		InrFallOutData inr=new InrFallOutData();
		Set<String> set=new HashSet<>();
		String s1="beid";
		set.add(s1);
		inr.setBeid(set);
		Set<Map<String,String>> setMap=new HashSet<>();
		Map<String,String> map=new HashMap<>();
		String s2="map";
		map.put(s1, s2);
		setMap.add(map);
		inr.setQueryParameter(setMap);
		//NxOutputFileModel model=new NxOutputFileModel();
		
		//String value1=fileModels.get(0).getFallOutData();
       // Mockito.when(fileModels.get(0).getFallOutData()).thenReturn(s1);
		Mockito.when(hybridRepo.getNxOutputFileRepository(Mockito.anyLong())).thenReturn(fileModels);
		Mockito.when(nxDesignAuditRepository.findByNxRefIdAndTransaction(anyLong(), anyString())).thenReturn(new NxDesignAudit());
		// Mockito.when(falloutData.getCharacterStream()).thenReturn(reader);
				try {
			falloutDetailsImpl.nexxusRequestActions(request);
		} catch (Exception e) {

		}
	}


	@Test
	public void testRetreivefalloutElse() throws SalesBusinessException, SQLException, IOException {
		FalloutDetailsRequest request = new FalloutDetailsRequest();
		request.setAction("retriggerRequest");
		request.setNxReqId(12345l);
		List<NxOutputFileModel> fileModels = new ArrayList<>();
		NxOutputFileModel value = new NxOutputFileModel();
		value.setId(12345L);
		// falloutData.setCharacterStream(123456l);
		// value.setFalloutData(falloutData);
		fileModels.add(value);
		NxRequestDetails nxRequestDetails = new NxRequestDetails();
		nxRequestDetails.setFlowType("INR");
		nxRequestDetails.setAcctCriteria("criteria");
		nxRequestDetails.setCpniApprover("cpniApprover");
		nxRequestDetails.setCreatedDate(new Date());
		nxRequestDetails.setDmaapMsg("dmaapMsg");
		nxRequestDetails.setEdfAckId("edfAckId");
		nxRequestDetails.setFileName("fileName");
		nxRequestDetails.setNxOutputFiles(fileModels);
		nxRequestDetails.setNxReqId(new Long(2l));
		NxSolutionDetail nxSolutionDetail = new NxSolutionDetail();
		nxSolutionDetail.setActiveYn("Y");
		nxRequestDetails.setNxSolutionDetail(nxSolutionDetail);
		nxRequestDetails.setProduct("AVPN");
		nxRequestDetails.setStatus(new Long(200));
		nxRequestDetails.setUser("ec006e");
		Mockito.when(hybridRepo.getNxOutputFileRepository(Mockito.anyLong())).thenReturn(fileModels);
		Mockito.when(hybridRepo.getByRequestId(Mockito.anyLong())).thenReturn(nxRequestDetails);
		// Mockito.when(inrProcessingService.regenerateOutputJson(Mockito.anyObject()));
		doNothing().when(inrProcessingService).regenerateOutputJson(Mockito.any());
		// Mockito.when(falloutData.getCharacterStream()).thenReturn(reader);
		try {
			falloutDetailsImpl.nexxusRequestActions(request);
		} catch (Exception e) {

		}

	}

	@Test
	public void testRetreivefalloutElseCatch() throws SalesBusinessException, SQLException, IOException {
		FalloutDetailsRequest request = new FalloutDetailsRequest();
		request.setAction("retriggerRequest");
		request.setNxReqId(12345l);
		List<NxOutputFileModel> fileModels = new ArrayList<>();
		NxOutputFileModel value = new NxOutputFileModel();
		value.setId(12345L);
		// falloutData.setCharacterStream(123456l);
		// value.setFalloutData(falloutData);
		fileModels.add(value);
		NxRequestDetails nxRequestDetails = new NxRequestDetails();
		nxRequestDetails.setFlowType("FMO");
		nxRequestDetails.setAcctCriteria("criteria");
		nxRequestDetails.setCpniApprover("cpniApprover");
		nxRequestDetails.setCreatedDate(new Date());
		nxRequestDetails.setDmaapMsg("dmaapMsg");
		nxRequestDetails.setEdfAckId("edfAckId");
		nxRequestDetails.setFileName("fileName");
		nxRequestDetails.setNxReqId(new Long(2l));
		NxSolutionDetail nxSolutionDetail = new NxSolutionDetail();
		nxSolutionDetail.setActiveYn("Y");
		nxRequestDetails.setNxSolutionDetail(nxSolutionDetail);
		nxRequestDetails.setProduct("AVPN");
		nxRequestDetails.setStatus(new Long(200));
		nxRequestDetails.setUser("ec006e");
		Mockito.when(hybridRepo.getNxOutputFileRepository(Mockito.anyLong())).thenReturn(fileModels);
		Mockito.when(hybridRepo.getByRequestId(Mockito.anyLong())).thenReturn(nxRequestDetails);
		// Mockito.when(inrProcessingService.regenerateOutputJson(Mockito.anyObject()));
		doNothing().when(inrProcessingService).regenerateOutputJson(Mockito.any());
		// Mockito.when(falloutData.getCharacterStream()).thenReturn(reader);
		try {
			falloutDetailsImpl.nexxusRequestActions(request);
		} catch (Exception e) {

		}
	}

	@Test
	public void testRetreivefalloutElseCatchIf() throws Exception {
		FalloutDetailsRequest request = new FalloutDetailsRequest();
		request.setAction("retriggerInrRequest");
		request.setNxReqId(12345l);
		List<NxOutputFileModel> fileModels = new ArrayList<>();
		NxOutputFileModel value = new NxOutputFileModel();
		value.setId(12345L);
		// value.setFalloutData(falloutData);
		fileModels.add(value);
		NxRequestDetails nxRequestDetails = new NxRequestDetails();
		nxRequestDetails.setFlowType("FMO");
		nxRequestDetails.setAcctCriteria(
				"{\"manageBillingPriceInventoryDataRequest\":{\"Report_key\":[{\"L3_sub_acct_id\":\"SLWJ27\",\"L5_master_acct_id\":\"M00KENYB\",\"L4_acct_id\":\"ALHBA6\",\"Duns_number\":\"2\"},{\"L3_sub_acct_id\":\"SLWJ27\",\"L5_master_acct_id\":\"M00KENYB\",\"L4_acct_id\":\"ALHBA6\",\"Duns_number\":\"4\"},{\"L3_sub_acct_id\":\"SLWJ27\",\"L5_master_acct_id\":\"M00KENYB\",\"L4_acct_id\":\"ALHBA6\",\"Duns_number\":\"7\"}],\"Product\":\"AVPN\",\"Bill_Month\":\"052017\",\"Customer_name\":\"GERMAY\"}}");
		nxRequestDetails.setCpniApprover("cpniApprover");
		nxRequestDetails.setCreatedDate(new Date());
		nxRequestDetails.setDmaapMsg("dmaapMsg");
		nxRequestDetails.setEdfAckId("edfAckId");
		nxRequestDetails.setFileName("fileName");
		nxRequestDetails.setNxOutputFiles(fileModels);
		nxRequestDetails.setNxReqId(new Long(2l));
		NxSolutionDetail nxSolutionDetail = new NxSolutionDetail();
		nxSolutionDetail.setActiveYn("Y");
		nxRequestDetails.setNxSolutionDetail(nxSolutionDetail);
		nxRequestDetails.setProduct("AVPN");
		nxRequestDetails.setStatus(new Long(200));
		nxRequestDetails.setUser("ec006e");
		ManageBillDataInv manageBillingDataInv = new ManageBillDataInv();
		ManageBillingPriceInventoryDataRequest manageBillingPriceInventoryDataRequest = new ManageBillingPriceInventoryDataRequest();
		manageBillingPriceInventoryDataRequest.setBill_Month("bill_Month");

		manageBillingDataInv.setManageBillingPriceInventoryDataRequest(manageBillingPriceInventoryDataRequest);
		ManageBillingPriceInventoryDataResponse manageBillingPriceInventoryDataResponse = new ManageBillingPriceInventoryDataResponse();
		Status status = new Status();
		status.setCode("200");
		manageBillingPriceInventoryDataResponse.setStatus(status);
		InnerManageBillingPriceInventoryDataResponse billingPriceInventoryDataResponse = new InnerManageBillingPriceInventoryDataResponse();
		billingPriceInventoryDataResponse.setRequestId("requestId");
		manageBillingPriceInventoryDataResponse.setManageBillingPriceInventoryDataResponse(billingPriceInventoryDataResponse);
		Mockito.when(hybridRepo.getNxOutputFileRepository(Mockito.anyLong())).thenReturn(fileModels);
		Mockito.when(hybridRepo.getByRequestId(Mockito.anyLong())).thenReturn(nxRequestDetails);
		// Mockito.when(inrProcessingService.regenerateOutputJson(Mockito.anyObject()));
		doNothing().when(inrProcessingService).regenerateOutputJson(Mockito.any());
		Mockito.when(mapper.readValue(nxRequestDetails.getAcctCriteria(), ManageBillDataInv.class))
				.thenReturn(manageBillingDataInv);
		Mockito.when(dme.getBillingPriceInventryUri(Mockito.any()))
				.thenReturn(manageBillingPriceInventoryDataResponse);
		try {
			falloutDetailsImpl.nexxusRequestActions(request);
		} catch (Exception e) {

		}

	}

	@Test
	public void testRetreivefalloutElseCatchElseIf() throws SalesBusinessException, SQLException, IOException {
		FalloutDetailsRequest request = new FalloutDetailsRequest();
		request.setAction("updateFalloutList");
		request.setNxReqId(12345l);
		request.setNxSolutionId(12345l);
		request.setFailedBeids(new ArrayList<String>() {{
			add("beids");
		}});
		List<NxOutputFileModel> fileModels = new ArrayList<>();
		NxOutputFileModel value = new NxOutputFileModel();
		value.setId(12345L);
		// falloutData.setCharacterStream(123456l);
		// value.setFalloutData(falloutData);
		fileModels.add(value);
		NxRequestDetails nxRequestDetails = new NxRequestDetails();
		nxRequestDetails.setFlowType("FMO");
		nxRequestDetails.setAcctCriteria("criteria");
		nxRequestDetails.setCpniApprover("cpniApprover");
		nxRequestDetails.setCreatedDate(new Date());
		nxRequestDetails.setDmaapMsg("dmaapMsg");
		nxRequestDetails.setEdfAckId("edfAckId");
		nxRequestDetails.setFileName("fileName");
		nxRequestDetails.setNxReqId(new Long(2l));
		NxSolutionDetail nxSolutionDetail = new NxSolutionDetail();
		nxSolutionDetail.setActiveYn("Y");
		nxRequestDetails.setNxSolutionDetail(nxSolutionDetail);
		nxRequestDetails.setProduct("AVPN");
		nxRequestDetails.setStatus(new Long(200));
		nxRequestDetails.setUser("ec006e");
		doNothing().when(hybridRepo).saveFailedBeidDetails(new FailedBeidDetails());
		Mockito.when(hybridRepo.getNxOutputFileRepository(Mockito.anyLong())).thenReturn(fileModels);
		Mockito.when(hybridRepo.getByRequestId(Mockito.anyLong())).thenReturn(nxRequestDetails);
		// Mockito.when(inrProcessingService.regenerateOutputJson(Mockito.anyObject()));
		doNothing().when(inrProcessingService).regenerateOutputJson(Mockito.any());
		// Mockito.when(falloutData.getCharacterStream()).thenReturn(reader);
		String string = "failesBeids";
		Mockito.when(mapper.writeValueAsString(Mockito.anyMap())).thenReturn(string);
//		falloutDetailsImpl.nexxusRequestActions(request);

	}
	
	@Test
	public void testRetreivefalloutNotEmpty() throws SalesBusinessException, SQLException, IOException {
		FalloutDetailsRequest request = new FalloutDetailsRequest();
		request.setAction("updateFalloutList");
		request.setNxReqId(12345l);
		request.setNxSolutionId(12345l);
		List<NxOutputFileModel> fileModels = new ArrayList<>();
		NxOutputFileModel value = new NxOutputFileModel();
		value.setId(12345L);
		// falloutData.setCharacterStream(123456l);
		// value.setFalloutData(falloutData);
		fileModels.add(value);
		NxRequestDetails nxRequestDetails = new NxRequestDetails();
		nxRequestDetails.setFlowType("FMO");
		nxRequestDetails.setAcctCriteria("criteria");
		nxRequestDetails.setCpniApprover("cpniApprover");
		nxRequestDetails.setCreatedDate(new Date());
		nxRequestDetails.setDmaapMsg("dmaapMsg");
		nxRequestDetails.setEdfAckId("edfAckId");
		nxRequestDetails.setFileName("fileName");
		nxRequestDetails.setNxReqId(new Long(2l));
		List<NxSolutionDetail> nxSolDetailList=new ArrayList<>();
		NxSolutionDetail nxSolutionDetail = new NxSolutionDetail();
		nxSolutionDetail.setActiveYn("Y");
		nxSolutionDetail.setModifiedDate(new Date());
		nxSolDetailList.add(nxSolutionDetail);
		nxRequestDetails.setNxSolutionDetail(nxSolutionDetail);
		nxRequestDetails.setProduct("AVPN");
		nxRequestDetails.setStatus(new Long(200));
		nxRequestDetails.setUser("ec006e");
		Mockito.when(hybridRepo.getNxOutputFileRepository(Mockito.anyLong())).thenReturn(fileModels);
		Mockito.when(hybridRepo.getByRequestId(Mockito.anyLong())).thenReturn(nxRequestDetails);
		Mockito.when(hybridRepo.getNxSolutionDetailList(Mockito.anyLong())).thenReturn(nxSolDetailList);
		// Mockito.when(inrProcessingService.regenerateOutputJson(Mockito.anyObject()));
		doNothing().when(inrProcessingService).regenerateOutputJson(Mockito.any());
		// Mockito.when(falloutData.getCharacterStream()).thenReturn(reader);
		String string = "failesBeids";
		Mockito.when(mapper.writeValueAsString(Mockito.anyMap())).thenReturn(string);
//		falloutDetailsImpl.nexxusRequestActions(request);

	}
	
	
	@Test
	public void testRetreivefalloutElseCatchIfElse() throws Exception {
		FalloutDetailsRequest request = new FalloutDetailsRequest();
		request.setAction("retriggerInrRequest");
		request.setNxReqId(12345l);
		List<NxOutputFileModel> fileModels = new ArrayList<>();
		NxOutputFileModel value = new NxOutputFileModel();
		value.setId(12345L);
		// value.setFalloutData(falloutData);
		fileModels.add(value);
		NxRequestDetails nxRequestDetails = new NxRequestDetails();
		nxRequestDetails.setFlowType("FMO");
		nxRequestDetails.setAcctCriteria(
				"{\"manageBillingPriceInventoryDataRequest\":{\"Report_key\":[{\"L3_sub_acct_id\":\"SLWJ27\",\"L5_master_acct_id\":\"M00KENYB\",\"L4_acct_id\":\"ALHBA6\",\"Duns_number\":\"2\"},{\"L3_sub_acct_id\":\"SLWJ27\",\"L5_master_acct_id\":\"M00KENYB\",\"L4_acct_id\":\"ALHBA6\",\"Duns_number\":\"4\"},{\"L3_sub_acct_id\":\"SLWJ27\",\"L5_master_acct_id\":\"M00KENYB\",\"L4_acct_id\":\"ALHBA6\",\"Duns_number\":\"7\"}],\"Product\":\"AVPN\",\"Bill_Month\":\"052017\",\"Customer_name\":\"GERMAY\"}}");
		nxRequestDetails.setCpniApprover("cpniApprover");
		nxRequestDetails.setCreatedDate(new Date());
		nxRequestDetails.setDmaapMsg("dmaapMsg");
		nxRequestDetails.setEdfAckId("edfAckId");
		nxRequestDetails.setFileName("fileName");
		nxRequestDetails.setNxReqId(new Long(2l));
		NxSolutionDetail nxSolutionDetail = new NxSolutionDetail();
		nxSolutionDetail.setActiveYn("Y");
		nxRequestDetails.setNxSolutionDetail(nxSolutionDetail);
		nxRequestDetails.setProduct("AVPN");
		nxRequestDetails.setStatus(new Long(200));
		nxRequestDetails.setUser("ec006e");
		ManageBillDataInv manageBillingDataInv = new ManageBillDataInv();
		ManageBillingPriceInventoryDataRequest manageBillingPriceInventoryDataRequest = new ManageBillingPriceInventoryDataRequest();
		manageBillingPriceInventoryDataRequest.setBill_Month("bill_Month");

		//manageBillingDataInv.setManageBillingPriceInventoryDataRequest(manageBillingPriceInventoryDataRequest);
		ManageBillingPriceInventoryDataResponse manageBillingPriceInventoryDataResponse = new ManageBillingPriceInventoryDataResponse();
		Status status = new Status();
		status.setCode("200");
		manageBillingPriceInventoryDataResponse.setStatus(status);
		InnerManageBillingPriceInventoryDataResponse billingPriceInventoryDataResponse = new InnerManageBillingPriceInventoryDataResponse();
		billingPriceInventoryDataResponse.setRequestId("requestId");
		manageBillingPriceInventoryDataResponse.setManageBillingPriceInventoryDataResponse(billingPriceInventoryDataResponse);
		Mockito.when(hybridRepo.getNxOutputFileRepository(Mockito.anyLong())).thenReturn(fileModels);
		Mockito.when(hybridRepo.getByRequestId(Mockito.anyLong())).thenReturn(nxRequestDetails);
		// Mockito.when(inrProcessingService.regenerateOutputJson(Mockito.anyObject()));
		doNothing().when(inrProcessingService).regenerateOutputJson(Mockito.any());
		Mockito.when(mapper.readValue(nxRequestDetails.getAcctCriteria(), ManageBillDataInv.class))
				.thenReturn(manageBillingDataInv);
		/*Mockito.when(dme.getBillingPriceInventryUri(Mockito.anyObject()))
				.thenReturn(manageBillingPriceInventoryDataResponse);
*/		try {
			falloutDetailsImpl.nexxusRequestActions(request);
		} catch (Exception e) {

		}

	}
	
	@Test
	public void testFindReqWithNxSiteId() {
		List<NxRequestDetails> nxRequestDetail = new ArrayList<NxRequestDetails>();
		List<NxOutputFileModel> files = new ArrayList<NxOutputFileModel>();
		NxOutputFileModel nxOutputFileModel = new NxOutputFileModel();
		files.add(nxOutputFileModel);
		NxRequestDetails nxRequestDetails = new NxRequestDetails();
		nxRequestDetails.setNxOutputFiles(files);
		nxRequestDetail.add(nxRequestDetails);
		falloutDetailsImpl.findReqWithNxSiteId(nxRequestDetail);
	}
	
	@Test
	public void testIsServiceAccessGroup() {
		List<NxRequestDetails> nxRequestDetails = new ArrayList<NxRequestDetails>();
		NxRequestDetails nxRequestDetail = new NxRequestDetails();
		nxRequestDetail.setNxRequestGroupName("SERVICE_ACCESS_GROUP");
		nxRequestDetails.add(nxRequestDetail);
		NxRequestDetails nxRequestDetail1 = new NxRequestDetails();
		nxRequestDetail1.setNxRequestGroupName("ACCESS_GROUP");
		nxRequestDetails.add(nxRequestDetail1);
		falloutDetailsImpl.isServiceAccessGroup(nxRequestDetails);
	}
	
	@Test
	public void testSetNxGroupStatus() {
		List<NxRequestDetails> nxRequestDetails = new ArrayList<NxRequestDetails>();
		NxRequestDetails nxRequestDetail = new NxRequestDetails();
		nxRequestDetail.setNxRequestGroupName("SERVICE_ACCESS_GROUP");
		nxRequestDetails.add(nxRequestDetail);
		NxRequestGroup nxRequestGroup = new NxRequestGroup();
		Mockito.when(nxRequestGroupRepository.save(nxRequestGroup)).thenReturn(nxRequestGroup);
		falloutDetailsImpl.setNxGroupStatus(nxRequestDetails, nxRequestGroup);
		
	}
	
	@Test
	public void testSetNxGroupStatusFailure() {
		List<NxRequestDetails> nxRequestDetails = new ArrayList<NxRequestDetails>();
		NxRequestDetails nxRequestDetail = new NxRequestDetails();
		nxRequestDetail.setNxRequestGroupName("SERVICE_ACCESS_GROUP");
		nxRequestDetail.setStatus(40L);
		nxRequestDetails.add(nxRequestDetail);
		NxRequestDetails nxRequestDetail1 = new NxRequestDetails();
		nxRequestDetail1.setNxRequestGroupName("ACCESS_GROUP");
		nxRequestDetail1.setStatus(30L);
		nxRequestDetails.add(nxRequestDetail1);
		NxRequestGroup nxRequestGroup = new NxRequestGroup();
		Mockito.when(nxRequestGroupRepository.save(nxRequestGroup)).thenReturn(nxRequestGroup);
		falloutDetailsImpl.setNxGroupStatus(nxRequestDetails, nxRequestGroup);
		
	}
	
	@Test
	public void testSetNxGroupStatusWithAccess() {
		List<NxRequestDetails> nxRequestDetails = new ArrayList<NxRequestDetails>();
		NxRequestDetails nxRequestDetail = new NxRequestDetails();
		nxRequestDetail.setNxRequestGroupName("ACCESS_GROUP");
		nxRequestDetails.add(nxRequestDetail);
		NxRequestGroup nxRequestGroup = new NxRequestGroup();
		Mockito.when(nxRequestGroupRepository.save(nxRequestGroup)).thenReturn(nxRequestGroup);
		falloutDetailsImpl.setNxGroupStatus(nxRequestDetails, nxRequestGroup);
		
	}
	
	@Test
	public void testSetErrorResponse() {
		ServiceResponse response = new ServiceResponse();
		Map<String, Object> result = new HashMap<String, Object>();
		falloutDetailsImpl.setErrorResponse(response, result);
	}
	
	@Test
	public void testSetErrorResponseErrCode() {
		ServiceResponse response = new ServiceResponse();
		falloutDetailsImpl.setErrorResponse(response, "M00021");
	}
	
	@Test
	public void testGetGroupsForSoln() throws SalesBusinessException {
		FalloutDetailsRequest request = new FalloutDetailsRequest();
		request.setAction("getGroupsForSoln");
		request.setNxSolutionId(12345l);
		List<NxSolutionDetail> solution = new ArrayList<NxSolutionDetail>();
		NxSolutionDetail nxSolutionDetail = new NxSolutionDetail();
		nxSolutionDetail.setNxSolutionId(12345l);
		solution.add(nxSolutionDetail);
		Mockito.when(nxSolutionDetailsRepository.findByNxSolutionIdAndActiveYn(request.getNxSolutionId(), StringConstants.CONSTANT_Y)).thenReturn(solution);
		List<NxRequestDetails> nxRequestDetails = new ArrayList<NxRequestDetails>();
		NxRequestDetails nxRequestDetail = new NxRequestDetails();
		nxRequestDetail.setNxRequestGroupName("ACCESS_GROUP");
		nxRequestDetail.setNxReqId(1L);
		nxRequestDetails.add(nxRequestDetail);
		Mockito.when(nxRequestDetailsRepository.findByNxSolutionDetailAndActiveYnAndFlowType(solution.get(0), StringConstants.CONSTANT_Y, MyPriceConstants.FLOW_TYPES)).thenReturn(nxRequestDetails);
		List<Object[]> result = new ArrayList<Object[]>();
		Mockito.when(nxRequestGroupRepository.findByNxSolutionIdAndActive(request.getNxSolutionId(), StringConstants.CONSTANT_Y)).thenReturn(result);
		List<Object[]> iglooData = new ArrayList<Object[]>();
		Object[] res1 = new Object[2];
		BigDecimal d = new BigDecimal(2);
		res1[0] = d;
		res1[1] = d;
		iglooData.add(res1);
		Mockito.when(nxAccessPricingDataRepository.findByNxSolId(request.getNxSolutionId())).thenReturn(iglooData);
		falloutDetailsImpl.nexxusRequestActions(request);
	}
	
	@Test
	public void testGetInrRequest() throws SalesBusinessException {
		FalloutDetailsRequest request = new FalloutDetailsRequest();
		request.setAction("getInrRequest");
		request.setNxSolutionId(12345l);
		List<NxSolutionDetail> solution = new ArrayList<NxSolutionDetail>();
		NxSolutionDetail nxSolutionDetail = new NxSolutionDetail();
		nxSolutionDetail.setNxSolutionId(12345l);
		solution.add(nxSolutionDetail);
		Mockito.when(nxSolutionDetailsRepository.findByNxSolutionIdAndActiveYn(request.getNxSolutionId(), StringConstants.CONSTANT_Y)).thenReturn(solution);
		List<NxRequestDetails> nxRequestDetails = new ArrayList<NxRequestDetails>();
		NxRequestDetails nxRequestDetail = new NxRequestDetails();
		nxRequestDetail.setNxReqDesc("");
		nxRequestDetail.setNxReqId(1L);
		nxRequestDetails.add(nxRequestDetail);
		List<String> products=new ArrayList<>();
		Mockito.when(nxRequestDetailsRepository.findProductsByNxSolutionId(request.getNxSolutionId())).thenReturn(products);
		//Mockito.when(nxRequestDetailsRepository.findRequestByNxSolutionIdAndProduct(request.getNxSolutionId(),products.get(0))).thenReturn(nxRequestDetails);
		falloutDetailsImpl.nexxusRequestActions(request);
	}
	
	@Test
	public void testGetGroupsForSolnNeg() throws SalesBusinessException {
		FalloutDetailsRequest request = new FalloutDetailsRequest();
		request.setAction("getGroupsForSoln");
		request.setNxSolutionId(12345l);
		List<NxSolutionDetail> solution = new ArrayList<NxSolutionDetail>();
		Mockito.when(nxSolutionDetailsRepository.findByNxSolutionIdAndActiveYn(request.getNxSolutionId(), StringConstants.CONSTANT_Y)).thenReturn(solution);
		falloutDetailsImpl.nexxusRequestActions(request);
	}
	
	@Test
	public void testSubmitToMyPrice() throws SalesBusinessException {
		FalloutDetailsRequest request = new FalloutDetailsRequest();
		request.setAction("submitToMyPrice");
		request.setNxSolutionId(12345l);
		request.setReqDesc("INR");
		List<NxSolutionDetail> solution = new ArrayList<NxSolutionDetail>();
		NxSolutionDetail nxSolutionDetail = new NxSolutionDetail();
		nxSolutionDetail.setNxSolutionId(12345l);
		solution.add(nxSolutionDetail);
		Mockito.when(nxSolutionDetailsRepository.findByNxSolutionIdAndActiveYn(request.getNxSolutionId(), StringConstants.CONSTANT_Y)).thenReturn(solution);
		doNothing().when(nexxusService).updateNxSolution(solution.get(0).getNxSolutionId());
		Map<String, Object> response = new HashMap<String, Object>();
		response.put(MyPriceConstants.RESPONSE_STATUS, false);
		Mockito.when(submitToMyPriceService.submitToMyPrice(solution.get(0), request)).thenReturn(response);
//		falloutDetailsImpl.nexxusRequestActions(request);
	}
	
	@Test
	public void testSubmitToMyPriceNeg() throws SalesBusinessException {
		FalloutDetailsRequest request = new FalloutDetailsRequest();
		request.setAction("submitToMyPrice");
		request.setNxSolutionId(12345l);
		List<NxSolutionDetail> solution = new ArrayList<NxSolutionDetail>();
		Mockito.when(nxSolutionDetailsRepository.findByNxSolutionIdAndActiveYn(request.getNxSolutionId(), StringConstants.CONSTANT_Y)).thenReturn(solution);
		falloutDetailsImpl.nexxusRequestActions(request);
	}
	
	@Test
	public void testSubmitToMyPriceFmo() throws SalesBusinessException {
		FalloutDetailsRequest request = new FalloutDetailsRequest();
		request.setAction("submitToMyPrice");
		request.setNxSolutionId(12345l);
		request.setReqDesc("FMO");
		List<NxSolutionDetail> solution = new ArrayList<NxSolutionDetail>();
		NxSolutionDetail nxSolutionDetail = new NxSolutionDetail();
		nxSolutionDetail.setNxSolutionId(12345l);
		solution.add(nxSolutionDetail);
		Mockito.when(nxSolutionDetailsRepository.findByNxSolutionIdAndActiveYn(request.getNxSolutionId(), StringConstants.CONSTANT_Y)).thenReturn(solution);
		doNothing().when(nexxusService).updateNxSolution(solution.get(0).getNxSolutionId());
		NxRequestDetails nxRequestDetails = new NxRequestDetails(); 
		Mockito.when(nxRequestDetailsRepository.findByNxReqIdAndActiveYn(request.getNxReqId(), StringConstants.CONSTANT_Y)).thenReturn(nxRequestDetails);
		Map<String, Object> response = new HashMap<String, Object>();
		response.put(MyPriceConstants.RESPONSE_STATUS, false);
		//doNothing().when(submitToMyPriceService).submitFMOToMyPrice(solution.get(0), nxRequestDetails, new NxOutputFileModel(), response);
		falloutDetailsImpl.nexxusRequestActions(request);
	}
	
	@Test
	public void testRemovePricingINRNeg() throws SalesBusinessException {
		FalloutDetailsRequest request = new FalloutDetailsRequest();
		request.setAction("removePricingINR");
		request.setNxSolutionId(12345l);
		request.setNxReqId(2345l);
		Mockito.when(nxRequestDetailsRepository.getOne(request.getNxReqId())).thenReturn(null);
		falloutDetailsImpl.nexxusRequestActions(request);
	}
	
	@Test
	public void testRemovePricingINR() throws SalesBusinessException {
		FalloutDetailsRequest request = new FalloutDetailsRequest();
		request.setAction("removePricingINR");
		request.setNxSolutionId(12345l);
		request.setNxReqId(2345l);
		NxRequestDetails nxRequestDetail = new NxRequestDetails();
		nxRequestDetail.setNxSolutionDetail(new NxSolutionDetail());
		Mockito.when(nxRequestDetailsRepository.getOne(request.getNxReqId())).thenReturn(nxRequestDetail);
		
		doNothing().when(nxRequestDetailsRepository).inactiveReqDetails(request.getNxReqId());
		List<NxRequestDetails> nxRequestDetailList = new ArrayList<NxRequestDetails>();
		Mockito.when(nxRequestDetailsRepository.findRequestsByGroupId(nxRequestDetail.getNxRequestGroupId(), "Y")).thenReturn(nxRequestDetailList);
		NxRequestGroup nxRequestGroup = new NxRequestGroup();
		Mockito.when(nxRequestGroupRepository.getOne(nxRequestDetail.getNxRequestGroupId())).thenReturn(nxRequestGroup);
		List<Long> nxReqid=new ArrayList();
		nxReqid.add(nxRequestDetail.getNxReqId());
		doNothing().when(inrQualifyService).deleteDisQualifiedCkts(nxReqid);
		NxSolutionSite nxSolutionSite = new NxSolutionSite();
		Mockito.when(nxSolutionSiteRepository.findByNxSolutionIdAndNxRequestGroupIdAndActiveYNAndNxReqId(anyLong(), 
				anyLong(), anyString(), anyLong())).thenReturn(nxSolutionSite);
		doNothing().when(nxSolutionSiteRepository).delete(nxSolutionSite);
//		falloutDetailsImpl.nexxusRequestActions(request);
	}
	
	@Test
	public void testRemovePricingINRReq() throws SalesBusinessException {
		FalloutDetailsRequest request = new FalloutDetailsRequest();
		request.setAction("removePricingINR");
		request.setNxSolutionId(12345l);
		request.setNxReqId(2345l);
		NxRequestDetails nxRequestDetail = new NxRequestDetails();
		nxRequestDetail.setNxSolutionDetail(new NxSolutionDetail());
		Mockito.when(nxRequestDetailsRepository.getOne(request.getNxReqId())).thenReturn(nxRequestDetail);
		doNothing().when(nxRequestDetailsRepository).inactiveReqDetails(request.getNxReqId());
		List<NxRequestDetails> nxRequestDetailList = new ArrayList<NxRequestDetails>();
		nxRequestDetail.setProduct("AVPN");
		nxRequestDetail.setNxReqId(2345l);
		nxRequestDetail.setNxRequestGroupName("SERVICE_ACCESS_GROUP");
		nxRequestDetailList.add(nxRequestDetail);
		Mockito.when(nxRequestDetailsRepository.findRequestsByGroupId(nxRequestDetail.getNxRequestGroupId(), "Y")).thenReturn(nxRequestDetailList);
		NxLookupData lookupData = new NxLookupData();
		lookupData.setItemId("4");
		lookupData.setCriteria("AVPN");
		Mockito.when(nxLookupDataRepository.findByDatasetNameAndDescription(anyString(), anyString())).thenReturn(lookupData);
		NxRequestGroup nxRequestGroup = new NxRequestGroup();
		nxRequestGroup.setGroupId(3l);
		List<NxRequestGroup> grps = new ArrayList<NxRequestGroup>();
		Mockito.when(nxRequestGroupRepository.findByNxSolutionIdAndGroupIdAndActiveYn(anyLong(), anyLong(), anyString())).thenReturn(grps);
		Mockito.when(nxRequestGroupRepository.getOne(nxRequestDetail.getNxRequestGroupId())).thenReturn(nxRequestGroup);
		Mockito.when(nxRequestGroupRepository.saveAndFlush(any())).thenReturn(nxRequestGroup);
		List<Long> nxReqid=new ArrayList();
		nxReqid.add(nxRequestDetail.getNxReqId());
		doNothing().when(inrQualifyService).deleteDisQualifiedCkts(nxReqid);
		NxSolutionSite nxSolutionSite = new NxSolutionSite();
		Mockito.when(nxSolutionSiteRepository.findByNxSolutionIdAndNxRequestGroupIdAndActiveYNAndNxReqId(anyLong(), 
				anyLong(), anyString(), anyLong())).thenReturn(nxSolutionSite);
		doNothing().when(nxSolutionSiteRepository).delete(nxSolutionSite);
//		falloutDetailsImpl.nexxusRequestActions(request);
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void testRemovePricingINRReqElse() throws SalesBusinessException {
		FalloutDetailsRequest request = new FalloutDetailsRequest();
		request.setAction("removePricingINR");
		request.setNxSolutionId(12345l);
		request.setNxReqId(2345l);
		NxRequestDetails nxRequestDetail = new NxRequestDetails();
		nxRequestDetail.setNxSolutionDetail(new NxSolutionDetail());
		Mockito.when(nxRequestDetailsRepository.getOne(request.getNxReqId())).thenReturn(nxRequestDetail);
		doNothing().when(nxRequestDetailsRepository).inactiveReqDetails(request.getNxReqId());
		List<NxRequestDetails> nxRequestDetailList = new ArrayList<NxRequestDetails>();
		nxRequestDetail.setProduct("AVPN");
		nxRequestDetail.setNxReqId(2345l);
		nxRequestDetail.setNxRequestGroupName("SERVICE_ACCESS_GROUP");
		nxRequestDetailList.add(nxRequestDetail);
		Mockito.when(nxRequestDetailsRepository.findRequestsByGroupId(nxRequestDetail.getNxRequestGroupId(), "Y")).thenReturn(nxRequestDetailList);
		NxLookupData lookupData = new NxLookupData();
		lookupData.setItemId("4");
		lookupData.setCriteria("AVPN");
		Mockito.when(nxLookupDataRepository.findByDatasetNameAndDescription(anyString(), anyString())).thenReturn(lookupData);
		NxRequestGroup nxRequestGroup = new NxRequestGroup();
		nxRequestGroup.setGroupId(3l);
		List<NxRequestGroup> grps = new ArrayList<NxRequestGroup>();
		grps.add(nxRequestGroup);
		Mockito.when(nxRequestGroupRepository.findByNxSolutionIdAndGroupIdAndActiveYn(anyLong(), anyLong(), anyString())).thenReturn(grps);
		Mockito.when(nxRequestGroupRepository.getOne(nxRequestDetail.getNxRequestGroupId())).thenReturn(nxRequestGroup);
		Mockito.when(nxRequestGroupRepository.saveAndFlush(any())).thenReturn(nxRequestGroup);
		Mockito.when(nxRequestDetailsRepository.saveAll(anyCollection())).thenReturn(nxRequestDetailList);
		List<Long> nxReqid=new ArrayList();
		nxReqid.add(nxRequestDetail.getNxReqId());
		doNothing().when(inrQualifyService).deleteDisQualifiedCkts(nxReqid);
		NxSolutionSite nxSolutionSite = new NxSolutionSite();
		Mockito.when(nxSolutionSiteRepository.findByNxSolutionIdAndNxRequestGroupIdAndActiveYNAndNxReqId(anyLong(), 
				anyLong(), anyString(), anyLong())).thenReturn(nxSolutionSite);
		doNothing().when(nxSolutionSiteRepository).delete(nxSolutionSite);
//		falloutDetailsImpl.nexxusRequestActions(request);
	}
	
	@Test
	public void testGetNotQualifiedGrpNeg() throws SalesBusinessException {
		FalloutDetailsRequest request = new FalloutDetailsRequest();
		request.setAction("getNotQualifiedGrp");
		request.setNxSolutionId(12345l);
		request.setNxReqId(2345l);
		Mockito.when(nxSolutionDetailsRepository.findByNxSolutionIdAndActiveYn(request.getNxSolutionId(), StringConstants.CONSTANT_Y)).thenReturn(null);
		falloutDetailsImpl.nexxusRequestActions(request);
	}
	
	@Test
	public void testGetNotQualifiedGrp() throws SalesBusinessException {
		FalloutDetailsRequest request = new FalloutDetailsRequest();
		request.setAction("getNotQualifiedGrp");
		request.setNxSolutionId(12345l);
		request.setNxReqId(2345l);
		List<NxSolutionDetail> solution = new ArrayList<NxSolutionDetail>();
		solution.add(new NxSolutionDetail());
		Mockito.when(nxSolutionDetailsRepository.findByNxSolutionIdAndActiveYn(request.getNxSolutionId(), StringConstants.CONSTANT_Y)).thenReturn(solution);
		List<NxRequestGroup> nxRequestGroups = new ArrayList<>();
		Mockito.when(hybridRepo.findByNxSolutionIdAndStatus(request.getNxSolutionId(), MyPriceConstants.NOT_QUALIFIED)).thenReturn(nxRequestGroups);
		falloutDetailsImpl.nexxusRequestActions(request);
	}
	
	@Test
	public void testQualifyGroupNeg() throws SalesBusinessException {
		FalloutDetailsRequest request = new FalloutDetailsRequest();
		request.setAction("qualifyGroup");
		request.setNxSolutionId(12345l);
		request.setNxReqId(2345l);
		Mockito.when(nxSolutionDetailsRepository.findByNxSolutionIdAndActiveYn(request.getNxSolutionId(), StringConstants.CONSTANT_Y)).thenReturn(null);
//		falloutDetailsImpl.nexxusRequestActions(request);
	}
	
	@Test
	public void testQualifyGroup() throws SalesBusinessException {
		FalloutDetailsRequest request = new FalloutDetailsRequest();
		request.setAction("qualifyGroup");
		request.setNxSolutionId(12345l);
		request.setNxReqId(2345l);
		request.setDataIds(new ArrayList<Long>() {{
			add(1L);
			}});
		List<NxSolutionDetail> solution = new ArrayList<NxSolutionDetail>();
		solution.add(new NxSolutionDetail());
		Mockito.when(nxSolutionDetailsRepository.findByNxSolutionIdAndActiveYn(request.getNxSolutionId(), StringConstants.CONSTANT_Y)).thenReturn(solution);
		doNothing().when(nexxusService).updateNxSolution(solution.get(0).getNxSolutionId());
		List<NxRequestGroup> nxRequestGroups = new ArrayList<NxRequestGroup>();
		NxRequestGroup nxRequestGroup = new NxRequestGroup();
		Mockito.when(hybridRepo.findByNxRequestGroupId(1L)).thenReturn(nxRequestGroup);
		doNothing().when(hybridRepo).saveNxRequestGroups(nxRequestGroups);
		List<Object[]> result = new ArrayList<Object[]>();
		Object[] res = new Object[6];
		BigDecimal d = new BigDecimal(2);
		res[0] = "test";
		res[1] = d;
		res[2] = d;
		res[3] = d;
		res[4] = "test";
		res[5] = "test";
		result.add(res);
		Mockito.when(nxRequestGroupRepository.findByNxSolutionIdAndNxRequestGroupIdAndActive(request.getNxSolutionId(), request.getDataIds(), StringConstants.CONSTANT_Y)).thenReturn(result);
//		falloutDetailsImpl.nexxusRequestActions(request);
	}
	
	@Test
	public void testGetExsistingGroupListNeg() throws SalesBusinessException {
		FalloutDetailsRequest request = new FalloutDetailsRequest();
		request.setAction("getExsistingGroupList");
		request.setNxSolutionId(12345l);
		request.setNxReqId(2345l);
		Mockito.when(hybridRepo.findByNxSolutionId(request.getNxSolutionId())).thenReturn(null);
		falloutDetailsImpl.nexxusRequestActions(request);
	}
	
	@Test
	public void testGetExsistingGroupList() throws SalesBusinessException {
		FalloutDetailsRequest request = new FalloutDetailsRequest();
		request.setAction("getExsistingGroupList");
		request.setNxSolutionId(12345l);
		List<NxRequestGroup> nxRequestGroups = new ArrayList<NxRequestGroup>();
		NxRequestGroup nxRequestGroup = new NxRequestGroup();
		nxRequestGroup.setGroupId(1l);
		nxRequestGroup.setNxRequestGroupId(2l);
		nxRequestGroup.setNxSolutionId(12345l);
		nxRequestGroups.add(nxRequestGroup);
		Mockito.when(hybridRepo.findByNxSolutionId(request.getNxSolutionId())).thenReturn(nxRequestGroups);
		falloutDetailsImpl.nexxusRequestActions(request);
	}
	
	@Test
	public void testGetGroupIdForSolnNeg() throws SalesBusinessException {
		FalloutDetailsRequest request = new FalloutDetailsRequest();
		request.setAction("getGroupIdForSoln");
		request.setNxSolutionId(12345l);
		Mockito.when(nxSolutionDetailsRepository.findByNxSolutionIdAndActiveYn(request.getNxSolutionId(), StringConstants.CONSTANT_Y)).thenReturn(null);
		falloutDetailsImpl.nexxusRequestActions(request);
	}
	
	@Test
	public void testGetGroupIdForSolnList() throws SalesBusinessException {
		FalloutDetailsRequest request = new FalloutDetailsRequest();
		request.setAction("getGroupIdForSoln");
		request.setNxSolutionId(12345l);
		List<NxSolutionDetail> solution = new ArrayList<NxSolutionDetail>();
		solution.add(new NxSolutionDetail());
		Mockito.when(nxSolutionDetailsRepository.findByNxSolutionIdAndActiveYn(request.getNxSolutionId(), StringConstants.CONSTANT_Y)).thenReturn(solution);
		List<NxRequestGroup> nxRequestGroups = new ArrayList<NxRequestGroup>();
		NxRequestGroup nxRequestGroup = new NxRequestGroup();
		nxRequestGroup.setGroupId(1l);
		nxRequestGroup.setNxRequestGroupId(2l);
		nxRequestGroup.setNxSolutionId(12345l);
		nxRequestGroups.add(nxRequestGroup);
		Mockito.when(hybridRepo.findByNxSolutionId(request.getNxSolutionId())).thenReturn(nxRequestGroups);
		List<NxLookupData> nxLookupData = new ArrayList<NxLookupData>();
		nxLookupData.add(new NxLookupData());
		Mockito.when(nxLookupDataRepository.findByItemIdAndDatasetName(nxRequestGroup.getGroupId().toString(), MyPriceConstants.NX_REQ_GROUP_NAMES)).thenReturn(nxLookupData);
		falloutDetailsImpl.nexxusRequestActions(request);
	}
	
	@Test
	public void testUpdateGroupDescription() throws SalesBusinessException {
		FalloutDetailsRequest request = new FalloutDetailsRequest();
		request.setAction("updateGroupDescription");
		request.setNxRequestGroupId(12345l);
		NxRequestGroup nxRequestGroup = new NxRequestGroup();
		Mockito.when(hybridRepo.findByNxRequestGroupId(request.getNxRequestGroupId())).thenReturn(nxRequestGroup);
		doNothing().when(hybridRepo).saveNxRequestGroup(nxRequestGroup);
		doNothing().when(nexxusService).updateNxSolution(nxRequestGroup.getNxSolutionId());
//		falloutDetailsImpl.nexxusRequestActions(request);
	}
	
	@Test
	public void testSearchCriteria() throws SalesBusinessException {
		FalloutDetailsRequest request = new FalloutDetailsRequest();
		request.setAction("searchCriteria");
		request.setNxReqId(12345l);
		NxRequestDetails nxRequestDetails =  new NxRequestDetails();
		Mockito.when(hybridRepo.getByRequestId(request.getNxReqId())).thenReturn(nxRequestDetails);
		falloutDetailsImpl.nexxusRequestActions(request);
	}
	
	@Test
	public void testGetStatus() throws SalesBusinessException {
		FalloutDetailsRequest request = new FalloutDetailsRequest();
		request.setAction("getStatus");
		request.setNxSolutionId(12345l);
		request.setNxReqId(2345l);
		List<Object[]> result = new ArrayList<Object[]>();
		Object[] res = new Object[6];
		BigDecimal d = new BigDecimal(2);
		res[0] = "test";
		res[1] = d;
		res[2] = d;
		res[3] = d;
		res[4] = 'C';
		res[5] = "test";
		result.add(res);
		Mockito.when(hybridRepo.findByGetStatusAction(request)).thenReturn(result);
		falloutDetailsImpl.nexxusRequestActions(request);
	}
	/** The logger. */
	private static Logger logger = LoggerFactory.getLogger(FalloutDetailsImplTest.class);
	
	@Test
	public void testUpdateRequestDescription() throws SalesBusinessException {
		FalloutDetailsRequest request = new FalloutDetailsRequest();
		request.setAction("updateRequestDescription");
		request.setNxSolutionId(12345l);
		request.setNxReqId(2345l);
		NxRequestDetails nxRequestDetails = new NxRequestDetails();
		nxRequestDetails.setNxSolutionDetail(new NxSolutionDetail());
		Mockito.when(hybridRepo.getByRequestId(request.getNxReqId())).thenReturn(nxRequestDetails);
		doNothing().when(hybridRepo).saveNxRequestDetails(nxRequestDetails);
		doNothing().when(nexxusService).updateNxSolution(nxRequestDetails.getNxSolutionDetail().getNxSolutionId());
//		falloutDetailsImpl.nexxusRequestActions(request);
	}
	@Test
	public void testCustomerNameEdit() throws SalesBusinessException {
		logger.info("inside cusotmerNameEdit ");
		FalloutDetailsRequest request = new FalloutDetailsRequest();
		request.setAction("updateCustomerName");
		request.setNxSolutionId(140l);
		request.setReqDesc("Vijay B");
		
		NxSolutionDetail nxSolutionDetails=nxSolutionDetailsRepository.findByNxSolutionId(request.getNxSolutionId());
		//int count=nxSolutionDetails.size();
		Mockito.when(nxSolutionDetailsRepository.updateCustomerNameSolutionId(request.getReqDesc(), request.getNxSolutionId())).thenReturn(1);
//		FalloutDetailsResponse response = falloutDetailsImpl.nexxusRequestActions(request);
//		logger.info("response : "+response.getNxSolutionId());
	}
	@Test
	public void testCopyAccessNeg() throws SalesBusinessException {
		FalloutDetailsRequest request = new FalloutDetailsRequest();
		request.setAction("copyAccess");
		request.setNxSolutionId(12345l);
		request.setNxReqId(1l);
		NxRequestDetails nxRequestDetails = new NxRequestDetails();
		nxRequestDetails.setNxRequestGroupName("ACCESS_GROUP");
		Mockito.when(hybridRepo.getByRequestId(request.getNxReqId())).thenReturn(nxRequestDetails);
		List<NxSolutionDetail> solution = new ArrayList<NxSolutionDetail>();
		Mockito.when(nxSolutionDetailsRepository.findByNxSolutionId(request.getNxSolutionId())).thenReturn(solution.get(0));
		falloutDetailsImpl.nexxusRequestActions(request);
	}
	
	@Test
	public void testCopyAccessNewGrp() throws SalesBusinessException {
		FalloutDetailsRequest request = new FalloutDetailsRequest();
		request.setAction("copyAccess");
		request.setNxSolutionId(12345l);
		request.setNxReqId(1l);
		NxRequestDetails nxRequestDetails = new NxRequestDetails();
		nxRequestDetails.setNxRequestGroupName("ACCESS_GROUP");
		nxRequestDetails.setStatus(100L);
		List<NxOutputFileModel> nxOutputFileModels = new ArrayList<NxOutputFileModel>();
		nxOutputFileModels.add(new NxOutputFileModel());
		nxRequestDetails.setNxOutputFiles(nxOutputFileModels);
		Mockito.when(nxMyPriceRepositoryServce.getItemDescFromLookup(anyString(), anyString())).thenReturn(copyStatusLookup);
		Mockito.when(hybridRepo.getByRequestId(request.getNxReqId())).thenReturn(nxRequestDetails);
		List<NxSolutionDetail> solution = new ArrayList<NxSolutionDetail>();
		solution.add(new NxSolutionDetail());
		Mockito.when(nxSolutionDetailsRepository.findByNxSolutionId(request.getNxSolutionId())).thenReturn(solution.get(0));
		NxLookupData nxLookupData = new NxLookupData();
		nxLookupData.setItemId("1");
		Mockito.when(nxLookupDataRepository.findByDescriptionAndDatasetName(nxRequestDetails.getProduct(), MyPriceConstants.NX_REQ_GROUP_NAMES)).thenReturn(nxLookupData);
		doNothing().when(nexxusService).updateNxSolution(1l);	
		falloutDetailsImpl = spy(falloutDetailsImpl);
		doNothing().when(falloutDetailsImpl).updateNxOutputFileCdirData(any(), anyLong());
		falloutDetailsImpl.nexxusRequestActions(request);
	}
	
	@Test
	public void testCopyAccessExisting() throws SalesBusinessException {
		FalloutDetailsRequest request = new FalloutDetailsRequest();
		request.setAction("copyAccess");
		request.setNxSolutionId(12345l);
		request.setNxReqId(1l);
		request.setDataIds(new ArrayList<Long>() {{
			add(1L);
			}});
		NxRequestDetails nxRequestDetails = new NxRequestDetails();
		nxRequestDetails.setNxRequestGroupName("SERVICE_ACCESS_GROUP");
		nxRequestDetails.setStatus(90L);
		List<NxOutputFileModel> nxOutputFileModels = new ArrayList<NxOutputFileModel>();
		nxOutputFileModels.add(new NxOutputFileModel());
		nxRequestDetails.setNxOutputFiles(nxOutputFileModels);
		Mockito.when(hybridRepo.getByRequestId(request.getNxReqId())).thenReturn(nxRequestDetails);
		List<NxSolutionDetail> solution = new ArrayList<NxSolutionDetail>();
		solution.add(new NxSolutionDetail());
		Mockito.when(nxSolutionDetailsRepository.findByNxSolutionId(request.getNxSolutionId())).thenReturn(solution.get(0));
		Mockito.when(nxMyPriceRepositoryServce.getItemDescFromLookup(anyString(), anyString())).thenReturn(copyStatusLookup);
		List<NxRequestGroup> nxRequestGroups = new ArrayList<NxRequestGroup>();
		NxRequestGroup nxRequestGroup = new NxRequestGroup();
		nxRequestGroups.add(nxRequestGroup);
		Mockito.when(nxRequestGroupRepository.findByNxRequestGroupIdAndActiveYn(request.getDataIds(), StringConstants.CONSTANT_Y)).thenReturn(nxRequestGroups);
		List<NxRequestDetails> nxRequests = new ArrayList<NxRequestDetails>();
		nxRequests.add(new NxRequestDetails());
		Mockito.when(nxRequestDetailsRepository.findByEdfAckIdAndActiveYnAndNxSolutionDetailAndNxRequestGroupId(nxRequestDetails.getEdfAckId()
				, StringConstants.CONSTANT_Y, new NxSolutionDetail(), 1L)).thenReturn(nxRequests);
		List<NxRequestDetails> nrds =  new ArrayList<NxRequestDetails>();
		NxRequestDetails reqDetail = new NxRequestDetails();
		reqDetail.setNxRequestGroupName("ACCESS_GROUP");
		nrds.add(reqDetail);
		Mockito.when(nxRequestDetailsRepository.findRequestsByGroupId(nxRequestGroup.getNxRequestGroupId(), StringConstants.CONSTANT_Y)).thenReturn(nrds);
		NxLookupData lookupdata =  new NxLookupData();
		lookupdata.setItemId("1");
		Mockito.when(nxLookupDataRepository.findByDatasetNameAndDescription(MyPriceConstants.SERVICE_ACCESS_GROUP, nxRequestDetails.getProduct())).thenReturn(lookupdata);
		Mockito.when(nxRequestGroupRepository.findByNxSolutionIdAndGroupIdAndActiveYn(request.getNxSolutionId(), 1L, StringConstants.CONSTANT_Y)).thenReturn(null);
		Mockito.when(nxRequestGroupRepository.save(nxRequestGroup)).thenReturn(nxRequestGroup);
		falloutDetailsImpl = spy(falloutDetailsImpl);
		doNothing().when(falloutDetailsImpl).updateNxOutputFileCdirData(any(), anyLong());
		falloutDetailsImpl.nexxusRequestActions(request);
	}
	
	@Test
	public void testCopyAccessWithMerge() throws SalesBusinessException {
		FalloutDetailsRequest request = new FalloutDetailsRequest();
		request.setAction("copyAccess");
		request.setNxSolutionId(12345l);
		request.setNxReqId(1l);
		request.setDataIds(new ArrayList<Long>() {{
			add(1L);
			}});
		NxRequestDetails nxRequestDetails = new NxRequestDetails();
		nxRequestDetails.setNxRequestGroupName("SERVICE_ACCESS_GROUP");
		nxRequestDetails.setStatus(90L);
		List<NxOutputFileModel> nxOutputFileModels = new ArrayList<NxOutputFileModel>();
		nxOutputFileModels.add(new NxOutputFileModel());
		nxRequestDetails.setNxOutputFiles(nxOutputFileModels);
		Mockito.when(hybridRepo.getByRequestId(request.getNxReqId())).thenReturn(nxRequestDetails);
		Mockito.when(nxMyPriceRepositoryServce.getItemDescFromLookup(anyString(), anyString())).thenReturn(copyStatusLookup);
		List<NxSolutionDetail> solution = new ArrayList<NxSolutionDetail>();
		solution.add(new NxSolutionDetail());
		Mockito.when(nxSolutionDetailsRepository.findByNxSolutionId(request.getNxSolutionId())).thenReturn(solution.get(0));
		List<NxRequestGroup> nxRequestGroups = new ArrayList<NxRequestGroup>();
		NxRequestGroup nxRequestGroup = new NxRequestGroup();
		nxRequestGroups.add(nxRequestGroup);
		Mockito.when(nxRequestGroupRepository.findByNxRequestGroupIdAndActiveYn(request.getDataIds(), StringConstants.CONSTANT_Y)).thenReturn(nxRequestGroups);
		List<NxRequestDetails> nxRequests = new ArrayList<NxRequestDetails>();
		nxRequests.add(new NxRequestDetails());
		Mockito.when(nxRequestDetailsRepository.findByEdfAckIdAndActiveYnAndNxSolutionDetailAndNxRequestGroupId(nxRequestDetails.getEdfAckId()
				, StringConstants.CONSTANT_Y, new NxSolutionDetail(), 1L)).thenReturn(nxRequests);
		List<NxRequestDetails> nrds =  new ArrayList<NxRequestDetails>();
		NxRequestDetails reqDetail = new NxRequestDetails();
		reqDetail.setNxRequestGroupName("ACCESS_GROUP");
		nrds.add(reqDetail);
		Mockito.when(nxRequestDetailsRepository.findRequestsByGroupId(nxRequestGroup.getNxRequestGroupId(), StringConstants.CONSTANT_Y)).thenReturn(nrds);
		NxLookupData lookupdata =  new NxLookupData();
		lookupdata.setItemId("1");
		Mockito.when(nxLookupDataRepository.findByDatasetNameAndDescription(MyPriceConstants.SERVICE_ACCESS_GROUP, nxRequestDetails.getProduct())).thenReturn(lookupdata);
		List<NxRequestGroup> serviceAccessData = new ArrayList<NxRequestGroup>();
		serviceAccessData.add(nxRequestGroup);
		Mockito.when(nxRequestGroupRepository.findByNxSolutionIdAndGroupIdAndActiveYn(request.getNxSolutionId(), 1L, StringConstants.CONSTANT_Y)).thenReturn(serviceAccessData);
		doNothing().when(nxRequestGroupRepository).delete(nxRequestGroup);
		Mockito.when(nxRequestGroupRepository.save(nxRequestGroup)).thenReturn(nxRequestGroup);
		falloutDetailsImpl = spy(falloutDetailsImpl);
		doNothing().when(falloutDetailsImpl).updateNxOutputFileCdirData(any(), anyLong());
		falloutDetailsImpl.nexxusRequestActions(request);
	}
	
	@Test
	public void testRetriggerBulkUploadIndividualRequest() throws Exception {
		FalloutDetailsRequest request = new FalloutDetailsRequest();
		request.setAction("retriggerBulkUploadIndividualRequest");
		request.setNxSolutionId(12345l);
		request.setNxReqId(2345l);
		NxRequestDetails nxRequestDetails = new NxRequestDetails();
		nxRequestDetails.setNxSolutionDetail(new NxSolutionDetail());
		nxRequestDetails.setValidateAccountDataRequestJson("{\"accountNumberSet\":[\"1717860518801\",\"1717862922966\",\"1717868121264\",\"1717869457318\",\"1717872627386\",\"1717889516105\",\"1717890612701\",\"1717912811516\",\"1717918436652\",\"1717919668188\",\"1717920339188\",\"1717920352632\",\"1717921782765\",\"1717926361899\",\"1717927294128\",\"1717927376084\",\"1717927762025\",\"1717928459592\",\"1717929018324\",\"1717929185522\",\"1717929225824\",\"1717929548513\",\"1717929756767\",\"1717930130222\",\"1717930298145\",\"1717930309173\",\"1717930359536\",\"1717931215441\",\"1717931345313\",\"1717931350355\",\"1717932301961\",\"1717932505460\",\"1717932789001\",\"1717933642961\",\"1717933664205\",\"1717933755912\",\"1717934124001\",\"1717934276001\",\"1717935020673\",\"1717935514499\",\"1717936439424\",\"1717936598248\",\"1717936695559\",\"1717936700567\",\"1717937542648\",\"1717937777230\",\"1717937994903\",\"1717939587745\",\"1717939732685\",\"1717939793396\",\"1717939873420\",\"1717940059630\",\"1717940553227\",\"1717940841001\",\"1717941063001\",\"1717941358324\",\"1717941754235\",\"1717942037443\",\"1717942082796\",\"1717942769151\",\"1717942786222\",\"1717943857742\",\"1717944069740\",\"1717944072769\",\"1717944223921\",\"1717944864056\",\"1717945053579\",\"1717945095001\",\"1717945559001\",\"1717945664419\",\"1717945724899\",\"1717945792384\",\"1717945864042\",\"1717946003001\",\"1717946135210\",\"1717946141326\",\"1717946147349\",\"1717946928495\",\"1717947609696\",\"1717947633821\",\"1717947794001\",\"1717948052601\",\"1717948177001\",\"1717948341568\",\"1717948717235\",\"1717948765569\",\"1717949144291\",\"1717949178001\",\"1717949591547\",\"1717949648072\",\"1717949790817\",\"1717949977491\",\"1717950128548\",\"1717950285897\",\"1717950386001\",\"1717950443001\",\"1717950451001\",\"1717950656001\",\"1717950822001\",\"1717950956954\",\"1717951022507\",\"1717951273233\",\"1717951445202\",\"1717951642056\",\"1717951841926\",\"1717952106001\",\"1717952198001\",\"1717952789857\",\"1717952853747\",\"1717953142389\",\"1717953490001\",\"1717953529625\",\"1717953633001\",\"1717953644001\",\"1717953726459\",\"1717953800001\",\"1717954067001\",\"1717954170679\",\"1717954301139\",\"1717954387001\",\"1717954403515\",\"1717954633943\",\"1717954662001\",\"1717955010001\",\"1717955275851\",\"1717955529001\",\"1717955919738\",\"1717956008111\",\"1717956882001\",\"1717956922001\",\"1717957184285\",\"1717957537737\",\"1717957623068\",\"1717957711422\",\"1717957834103\",\"1717957952604\",\"1717958009067\",\"1717958228997\",\"1717958284163\",\"1717958358549\",\"1717959124682\",\"1717959162891\",\"1717959243268\",\"1717959330001\",\"1717959530552\",\"1717959608878\",\"1717959718191\",\"1717959844570\",\"1717959962451\",\"1717960106001\",\"1717960563473\",\"1717960792398\",\"1717960816467\",\"1717960987001\",\"1717961226685\",\"1717961379680\",\"1717961531794\",\"1717961577470\",\"1717961700760\",\"1717961889001\",\"1717961892467\",\"1717962821924\",\"1717963099775\",\"1717963331410\",\"1717963403712\",\"1717963949585\",\"1717963981710\",\"1717963999820\",\"1717964196767\",\"1717964261001\",\"1717964290001\",\"1717964313219\",\"1717964387404\",\"1717964400484\",\"1717964772756\",\"1717964925285\",\"1717965558410\",\"1717965562447\",\"1717965825718\",\"1717966032001\",\"1717966158001\",\"1717966498445\",\"1717966577617\",\"1717966584001\",\"1717966608727\",\"1717966897529\",\"1717967061017\",\"1717967082103\",\"1717967396907\",\"1717967563344\",\"1717967676700\",\"1717967700762\",\"1717967750001\",\"1717967821001\",\"1717967907001\",\"1717968282580\",\"1717968360801\",\"1717968371818\",\"1717968506162\",\"1717968683562\",\"1717968902079\",\"1717968953001\",\"1717969123784\",\"1717969401001\",\"1717969426001\",\"1717969451809\",\"1717969604204\",\"1717969669403\",\"1717969969313\",\"1717969997415\",\"1717970083576\",\"1717970906560\",\"1717970944627\",\"1717970953642\",\"1717971120111\",\"1717971188001\",\"1717971377001\",\"1717971443978\",\"1717971661538\",\"1717971681589\",\"1717971776845\",\"1717971827001\",\"1717971996001\",\"1717972081840\",\"1717972165150\",\"1717972300581\",\"1717972322001\",\"1717972326636\",\"1717972728636\",\"1717972806884\",\"1717973259001\",\"1717973293330\",\"1717973356556\",\"1717973594528\",\"1717974156001\",\"1717974182052\",\"1717974193001\",\"1717974364001\",\"1717974587088\",\"1717974633215\",\"1717974688001\",\"1717974740442\",\"1717975059001\",\"1717975249001\",\"1717975823001\",\"1717976028609\",\"1717976111001\",\"1717976201001\",\"1717976219001\",\"1717976608174\",\"1717976648241\",\"1717976772001\",\"1717977048406\",\"1717977420027\",\"1717977638001\",\"1717977661714\",\"1717977799001\",\"1717977949001\",\"1717978164001\",\"1717978205996\",\"1717978219046\",\"1717978237001\",\"1717978317001\",\"1717978371500\",\"1717978488001\",\"1717978581001\",\"1717978788523\",\"1717978792531\",\"1717978804001\",\"1717978852001\",\"1717978858001\",\"1717979009934\",\"1717979095001\",\"1717979271001\",\"1717979669001\",\"1717979671001\",\"1717979746001\",\"1717979751001\",\"1717979772001\",\"1717979781001\",\"1717979787001\",\"1717979804001\",\"1717980066374\",\"1717980147690\",\"1717980215877\",\"1717980298001\",\"1717980385600\",\"1717980410698\",\"1717980430750\",\"1717980754001\",\"1717980870183\",\"1717980963391\",\"1717981144059\",\"1717981347959\",\"1717981517598\",\"1717981532001\",\"1717981622929\",\"1717981769297\",\"1717981778316\",\"1717981866001\",\"1717982178001\",\"1717982275841\",\"1717982421273\",\"1717982736224\",\"1717982782001\",\"1717982911001\",\"1717983104296\",\"1717983218001\",\"1717983711001\",\"1717984002001\",\"1717984005001\",\"1717984161159\",\"1717984318734\",\"1717984430001\",\"1717984490001\",\"1717984900001\",\"1717984997001\",\"1717985428514\",\"1717985711001\",\"1717985733001\",\"1717986020001\",\"1717986207001\",\"1717986306001\",\"1717986334001\",\"1717986374001\",\"1717986390001\",\"1717986493001\",\"1717986774001\",\"1717987094001\",\"1717987482001\",\"1717987600001\",\"1717987603001\",\"1717987679001\",\"1717987880476\",\"1717988007001\",\"1717988116001\",\"1717988573365\",\"1717988643001\",\"1717988865001\",\"1717989051001\",\"1717989116001\",\"1717989322001\",\"1717989355512\",\"1717989477001\",\"1717990147001\",\"1717990680001\",\"1717990744001\",\"1717991006001\",\"1717991108001\",\"1717991636001\",\"1717991894001\",\"1717991913001\",\"1717991917001\",\"1717991997001\",\"1717992117001\",\"1717992409001\",\"1717992513001\",\"1717992695001\",\"1717992697001\",\"1717993377001\",\"1717993411001\",\"1717993896001\",\"1717994062001\",\"1717994082001\",\"1717994447001\",\"1717994467001\",\"1717994600001\",\"1717994634001\",\"1717994812597\",\"1717994819001\",\"1717994991170\",\"1717995032001\",\"1717995133001\",\"1717995134001\",\"1717995308001\",\"1717995447001\",\"1717995470001\",\"1717995743001\",\"1717996665001\",\"1717996677001\",\"1717996770001\",\"1717997002001\",\"1717997117001\",\"1717997126001\",\"1717997454001\",\"1717997484001\",\"1717997557001\",\"1717997617001\",\"1717997770001\",\"1717998064001\",\"1717998312006\",\"1717998464001\",\"1717998742001\",\"1717998809516\",\"1717998878728\",\"1717999027001\",\"1717999501001\",\"1717999574001\",\"1717999990001\",\"1718000082001\",\"1718000178001\",\"1718000233553\",\"1718000422001\",\"1718000554001\",\"1718001870001\",\"1718001986001\",\"1718002015001\",\"1718002033001\",\"1718002134513\",\"1718002281001\",\"1718002356001\",\"1718002516001\",\"1718002663001\",\"1718002677001\",\"1718002698163\",\"1718002801001\",\"1718003025001\",\"1718003442001\",\"1718003569001\",\"1718003580001\",\"1718003584001\",\"1718003886001\",\"1718003959001\",\"1718004258001\",\"1718005179001\",\"1718005439001\",\"1718005691984\",\"1718005919001\",\"1718006153001\",\"1718006239001\",\"1718006934001\",\"1718007011001\",\"1718007068001\",\"1718007146001\",\"1718007580001\",\"1718007738001\",\"1718008743001\",\"1718008755001\",\"1718009051001\",\"1718009453001\",\"1718018126001\",\"1718018254001\",\"1718019180001\",\"1718019210001\",\"1718019593001\",\"1718020325001\",\"1718024981001\",\"1718027610001\",\"1718028818001\",\"1718029057001\",\"1718029269001\",\"1718029457001\",\"1718030045001\",\"1718030365001\",\"1718030472001\",\"1718032154001\",\"1718032234001\",\"1718032288001\",\"1718032295001\",\"1718032345001\",\"1718032670001\",\"1718032771001\",\"1718032942001\",\"1718033016001\",\"1718033072001\",\"1718033153001\",\"1718033389001\",\"1718033412001\",\"1718033430001\",\"1718034484001\",\"1718034691001\",\"8310000088945\",\"8310000098293\",\"8310000572506\",\"8310000575927\",\"8310000632237\",\"8310000633642\",\"8310000663818\",\"8310000667895\",\"8310000668351\",\"8310000704803\",\"8310000706171\",\"8310000706659\",\"8310000706777\",\"8310000707416\",\"8310000709916\",\"8310000709951\",\"8310000710490\",\"8310000710548\",\"8310000711371\",\"8310000711621\",\"8310000711879\",\"8310000712111\",\"8310000712824\",\"8310000721196\",\"8310000721579\",\"8310000721973\",\"8310000723619\",\"8310000725243\",\"8310000729933\",\"8310000731851\",\"8310000735055\",\"8310000735420\",\"8310000735500\",\"8310000735586\",\"8310000736593\",\"8310000764577\",\"8310000767466\",\"8310000767630\",\"8310000767643\",\"8310000768650\",\"8310000769131\",\"8310000769817\",\"8310000770544\",\"8310000771024\",\"8310000771575\",\"8310000771819\",\"8310000772035\",\"8310000772467\",\"8310000775211\",\"8310000776538\",\"8310000776998\",\"8310000777371\",\"8310000777889\",\"8310000779203\",\"8310000782158\",\"8310000782214\",\"8310000785201\",\"8310000791431\",\"8310000791445\",\"8310000797228\",\"8310000800802\",\"8310000801089\",\"8310000801528\",\"8310000801757\",\"8310000802044\",\"8310000802072\",\"8310000802168\",\"8310000802261\",\"8310000802770\",\"8310000803406\",\"8310000803745\",\"8310000804204\",\"8310000804345\",\"8310000804554\",\"8310000804660\",\"8310000805149\",\"8310000813874\",\"8310000817423\",\"8310000817868\",\"8310000820950\",\"8310000821029\",\"8310000821607\",\"8310000823439\",\"8310000823852\",\"8310000824314\",\"8310000826291\",\"8310000826672\",\"8310000826851\",\"8310000827544\",\"8310000828994\",\"8310000830216\",\"8310000830320\",\"8310000830331\",\"8310000830414\",\"8310000830887\",\"8310000830987\",\"8310000831885\",\"8310000832161\",\"8310000832844\",\"8310000832857\",\"8310000833673\",\"8310000834013\",\"8310000834070\",\"8310000834168\",\"8310000834520\",\"8310000834895\",\"8310000835824\",\"8310000835940\",\"8310000836284\",\"8310000837109\",\"8310000837393\",\"8310000837922\",\"8310000838519\",\"8310000838674\",\"8310000839174\",\"8310000839396\",\"8310000839590\",\"8310000840102\",\"8310000840453\",\"8310000855021\",\"8310000856896\",\"8310000857290\",\"8310000857397\",\"8310000857887\",\"8310000858927\",\"8310000859419\",\"8310000859911\",\"8310000860316\",\"8310000860835\",\"8310000861893\",\"8310000863140\",\"8310000864121\",\"8310000915715\",\"8310000918860\",\"8310000919996\",\"8310001006420\",\"8310001008963\",\"8310001010601\",\"8310001015531\",\"8310001017059\",\"8310001147945\",\"8310001178292\",\"8310001226019\",\"8310001229700\",\"8310001230447\",\"8310001230762\",\"8310001230893\",\"8310001231272\",\"8310001231507\",\"8310001231691\",\"8310001232020\",\"8310001232931\",\"8310001235146\",\"8310001235351\",\"8310001235675\",\"8310001237326\",\"8310001239017\",\"8310001239532\",\"8310001240282\",\"8310001241032\",\"8310001241146\",\"8310001241561\",\"8310001242447\",\"8310001243865\",\"8310001243891\",\"8310001245006\",\"8310001263593\",\"8310001289982\",\"8310001290765\",\"8310001291627\",\"8310001292009\",\"8310001292082\",\"8310001292245\",\"8310001292389\",\"8310001293429\",\"8310001295664\",\"8310001296050\",\"8310001296250\",\"8310001296553\",\"8310001296758\",\"8310001297464\",\"8310001297987\",\"8310001298460\",\"8310001298582\",\"8310001298901\",\"8310001299568\",\"8310001299737\",\"8310001299821\",\"8310001300315\",\"8310001311359\",\"8310001311668\",\"8310001315853\",\"8310001323080\",\"8310001324768\",\"8310001344741\",\"8310001347987\",\"8310001349765\",\"8310001352494\",\"8310001359784\",\"8310001360282\",\"8310001366964\",\"8310001373109\",\"8310001373263\",\"8310001375030\",\"8310001375224\",\"8310001375240\",\"8310001375369\",\"8310001375559\",\"8310001375653\",\"8310001375781\",\"8310001375983\",\"8310001376391\",\"8310001376400\",\"8310001376754\",\"8310001377073\",\"8310001377337\",\"8310001377862\",\"8310001377990\",\"8310001378107\",\"8310001378516\",\"8310001378885\",\"8310001381273\",\"8310001381822\",\"8310001382115\",\"8310001385638\",\"8310001385947\",\"8310001386223\",\"8310001386605\",\"8310001387176\",\"8310001387268\",\"8310001387324\",\"8310001387392\",\"8310001387692\",\"8310001387846\",\"8310001387900\",\"8310001388265\",\"8310001388347\",\"8310001388596\",\"8310001388808\",\"8310001389084\",\"8310001389132\",\"8310001389432\",\"8310001389640\",\"8310001389827\",\"8310001389985\",\"8310001390212\",\"8310001390336\",\"8310001390388\",\"8310001390418\",\"8310001390524\",\"8310001405070\",\"8310001406131\",\"8310001408161\",\"8310001413415\",\"8310001415735\",\"8310001417805\",\"8310001420951\",\"8310001440913\",\"8310001445353\",\"8310001452635\",\"8310001455172\",\"8310001455301\",\"8310001457164\",\"8310001459334\",\"8310001462516\",\"8310001462554\",\"8310001462757\",\"8310001463484\",\"8310001464329\",\"8310001464388\",\"8310001464442\",\"8310001464756\",\"8310001465779\",\"8310001466041\",\"8310001484697\",\"8310001485011\",\"8310001485677\",\"8310001504739\",\"8310001505623\",\"8310001513802\",\"8310001513822\",\"8310001513851\",\"8310001513853\",\"8310001513917\",\"8310001514494\",\"8310001514825\",\"8310001516001\",\"8310001529642\",\"8310001535093\",\"8310001539460\",\"8310001547037\",\"8310001555649\",\"8310001562845\",\"8310001575580\",\"8310001577534\",\"8310001579694\",\"8310001580294\",\"8310001588485\",\"8310001591700\",\"8310001597638\",\"8310001599147\",\"8310001602318\",\"8310001605285\",\"8310001610193\",\"8310001621604\",\"8310001634640\",\"8310001643250\",\"8310001643599\",\"8310001650907\",\"8310001651698\",\"8310001652817\",\"8310001654403\",\"8310001657247\",\"8310001662203\",\"8310001665636\",\"8310001672077\",\"8310001676277\",\"8310001681170\",\"8310001682691\",\"8310001686888\",\"8310001694381\",\"8310001694960\",\"8310001697979\",\"8310001702872\",\"8310001705755\",\"8310001705812\",\"8310001706227\",\"8310001716566\",\"8310001720204\",\"8310001724628\",\"8310001725977\",\"8310001726359\",\"8310001739899\",\"8310001740258\",\"8310001746298\",\"8310001747316\",\"8310001753290\",\"8310001783246\",\"8310001783509\",\"8310001786562\",\"8310001795616\",\"8310001836906\",\"8310001844124\",\"8310001845456\",\"8310001849263\",\"8310001849992\",\"8310001851362\",\"8310001851586\",\"8310001865389\",\"8310001870607\",\"8310001877029\",\"8310001878659\",\"8310001906034\",\"8310001910330\",\"8310001912269\",\"8310001915270\",\"8310001929582\",\"8310001941384\",\"8310001941547\",\"8310001949846\",\"8310001950799\",\"8310001952110\",\"8310001955769\",\"8310001962685\",\"8310001974492\",\"8310001979344\",\"8310001979498\",\"8310001988891\",\"8310002001716\",\"8310002004102\",\"8310002006372\",\"8310002028096\",\"8310002038863\",\"8310002042505\",\"8310002055065\",\"8310002058741\",\"8310002063317\",\"8310002064797\",\"8310002078842\",\"8310002085645\",\"8310002092896\",\"8310002111737\",\"8310002121209\",\"8310002121273\",\"8310002129977\",\"8310002130272\",\"8310002147763\",\"8310002148397\",\"8310002149444\",\"8310002152015\",\"8310002160766\",\"8310002164911\",\"8310002166226\",\"8310002167455\",\"8310002171501\",\"8310002176302\",\"8310002199424\",\"8310002210214\",\"8310002214135\",\"8310002222530\",\"8310002227037\",\"8310002230614\",\"8310002237615\",\"8310002245954\",\"8310002271073\",\"8310002274593\",\"8310002297945\",\"8310002299268\",\"8310002302071\",\"8310002335594\",\"8310002341298\",\"8310002342519\",\"8310002342848\",\"8310002344432\",\"8310002352391\",\"8310002354359\",\"8310002370027\",\"8310002370540\",\"8310002372273\",\"8310002373548\",\"8310002373612\",\"8310002385961\",\"8310002399295\",\"8310002400137\",\"8310002415576\",\"8310002421534\",\"8310002423333\",\"8310002424660\",\"8310002425632\",\"8310002428156\",\"8310002431446\",\"8310002442560\",\"8310002442590\",\"8310002444993\",\"8310002459401\",\"8310002460402\",\"8310002460582\",\"8310002460934\",\"8310002462845\",\"8310002463079\",\"8310002464662\",\"8310002465410\",\"8310002473013\",\"8310002476895\",\"8310002477935\",\"8310002479468\",\"8310002482733\",\"8310002484148\",\"8310002488743\",\"8310002489669\",\"8310002493490\",\"8310002498278\",\"8310002498975\",\"8310002500710\",\"8310002501056\",\"8310002501118\",\"8310002501243\",\"8310002542830\",\"8310002545899\",\"8310002553031\",\"8310002558110\",\"8310002567294\",\"8310002576373\",\"8310002584400\",\"8310002587312\",\"8310002588846\",\"8310002590800\",\"8310002596437\",\"8310002596554\",\"8310002599366\",\"8310002614014\",\"8310002619759\",\"8310002627116\",\"8310002631060\",\"8310002631491\",\"8310002640678\",\"8310002640951\",\"8310002641390\",\"8310002642295\",\"8310002643012\",\"8310002643016\",\"8310002643155\",\"8310002649964\",\"8310002654059\",\"8310002660021\",\"8310002669572\",\"8310002673134\",\"8310002679816\",\"8310002681402\",\"8310002685675\",\"8310002691555\",\"8310002696965\",\"8310002699363\",\"8310002703804\",\"8310002705961\",\"8310002706148\",\"8310002710526\",\"8310002713711\",\"8310002730373\",\"8310002730830\",\"8310002737406\",\"8310002741446\",\"8310002786111\",\"8310002786766\",\"8310002787320\",\"8310002787447\",\"8310002787986\",\"8310002789209\",\"8310002789265\",\"8310002790074\",\"8310002790118\",\"8310002791100\",\"8310002792362\",\"8310002802733\",\"8310002810554\",\"8310002811859\",\"8310002819599\",\"8310002821399\",\"8310002824220\",\"8310002829895\",\"8310002830063\",\"8310002831184\",\"8310002840612\",\"8310002841107\",\"8310002850729\"],\"mcnSet\":[],\"product\":\"MIS/PNT\",\"billMonth\":\"082019\",\"beginBillMonth\":\"082019\"}");
		Mockito.when(hybridRepo.getByRequestId(request.getNxReqId())).thenReturn(nxRequestDetails);
		ValidateAccountDataRequest req = new ValidateAccountDataRequest();
		Mockito.when(mapper.readValue(nxRequestDetails.getValidateAccountDataRequestJson(), 
				ValidateAccountDataRequest.class)).thenReturn(req);
		ValidateAccountDataResponse edfResponse = new ValidateAccountDataResponse();
		edfResponse.setCorrelationId(110);
		Mockito.when(dme.getValidateAccontDataUri(req)).thenReturn(edfResponse);
		falloutDetailsImpl.nexxusRequestActions(request);
	}
	
	@Test
	public void testAdminUserListRetrieve() throws SalesBusinessException {
		FalloutDetailsRequest request = new FalloutDetailsRequest();
		request.setAction("adminUserListRetrieve");
		List<NxAdminUserModel> adminUserList = new ArrayList<NxAdminUserModel>();
		NxAdminUserModel model = new NxAdminUserModel();
		model.setId(1l);
		adminUserList.add(model);
		Mockito.when(nxAdminUserRepository.findAll()).thenReturn(adminUserList);
		falloutDetailsImpl.nexxusRequestActions(request);
	}
	
	@Test
	public void testAddAdminUser() throws Exception {
		FalloutDetailsRequest request = new FalloutDetailsRequest();
		request.setAction("addAdminUser");
		AdminUserList adminUserList = new AdminUserList();
		adminUserList.setRowId(1l);
		List<AdminUserList> list = new ArrayList<AdminUserList>();
		list.add(adminUserList);
		request.setAdminUserList(list);
		falloutDetailsImpl.nexxusRequestActions(request);
	}
	
	@Test
	public void testActivateDeActivateAdminUser() throws Exception {
		FalloutDetailsRequest request = new FalloutDetailsRequest();
		request.setAction("activateDeActivateAdminUser");
		AdminUserList adminUserList = new AdminUserList();
		adminUserList.setRowId(1l);
		List<AdminUserList> list = new ArrayList<AdminUserList>();
		list.add(adminUserList);
		request.setAdminUserList(list);
		Mockito.when(nxAdminUserRepository.updateActiveYn(anyString(), anyLong())).thenReturn(1);
		falloutDetailsImpl.nexxusRequestActions(request);
	}
	
	@Test
	public void testUpdateNxOutputFileCdirData() {
		NxSolutionDetail sol = new NxSolutionDetail();
		sol.setNxSolutionId(10l);
		NxRequestDetails newNxRequestDetails = new NxRequestDetails();
		newNxRequestDetails.setNxSolutionDetail(sol);
		newNxRequestDetails.setNxReqId(1010l);
		List<NxOutputFileModel> fileModels = new ArrayList<>();
		NxOutputFileModel value = new NxOutputFileModel();
		value.setId(12345L);
		value.setCdirData("{\"header\":[{\"nexxusSolutionId\":28751,\"nexxusRequestId\":18183,\"customerName\":\"\",\"searchCriteria\":\"\",\"searchValue\":\"\",\"opportunityID\":\"\",\"dunsNumber\":\"\",\"mcn\":\"\",\"mainAcctNumber\":\"\",\"l5MasterAcctId\":\"\",\"l4AcctId\":\"\",\"l3SubAcctId\":\"\",\"billMonth\":\"\",\"beginBillMonth\":\"\",\"rootTag\":\"InrDomCktResponse\"},{\"nexxusSolutionId\":28751,\"nexxusRequestId\":18183,\"customerName\":\"\",\"searchCriteria\":\"\",\"searchValue\":\"\",\"opportunityID\":\"\",\"dunsNumber\":\"\",\"mcn\":\"\",\"mainAcctNumber\":\"\",\"l5MasterAcctId\":\"\",\"l4AcctId\":\"\",\"l3SubAcctId\":\"\",\"billMonth\":\"\",\"beginBillMonth\":\"\",\"rootTag\":\"InrDomCktResponse1\"}],\"mainSheet\":[{\"rootTag\":\"InrDomCktResponse\",\"/InrDomCktResponse/v1:Header/v1:CustomerName\":\"NA\",\"/InrDomCktResponse/v1:Header/v1:DUNSNumber\":\"945155190\",\"/InrDomCktResponse/v1:Header/v1:BillMonth\":\"September 2019\",\"/InrDomCktResponse/v1:Header/v1:ProprietaryStatement\":\"AT&T Proprietary (Restricted) Only for use by authorized individuals or any above-designated team(s) within the AT&T companies and not for general distribution.\",\"/InrDomCktResponse/v1:Header/xmlns:v1\":\"http://edb.att.com/inr/v1\",\"/InrDomCktResponse/v1:Header/v1:Product\":\"DOMESTIC PL IOC\",\"/InrDomCktResponse/v1:Header/v1:CorrelationID\":\"263749957\",\"/InrDomCktResponse/Body/DomesticEthernetIOCInventory/CustomerAccountInfo/SOC\":\"3J\",\"/InrDomCktResponse/Body/DomesticEthernetIOCInventory/CustomerAccountInfo/MCN\":\"SK9699\",\"/InrDomCktResponse/Body/DomesticEthernetIOCInventory/CustomerAccountInfo/GRC\":\"EQU\",\"/InrDomCktResponse/Body/DomesticEthernetIOCInventory/CustomerAccountInfo/CustomerName\":\"AETNA\",\"/InrDomCktResponse/Body/DomesticEthernetIOCInventory/CustomerAccountInfo/DunsNumber\":\"945155190\",\"/InrDomCktResponse/Body/DomesticEthernetIOCInventory/CustomerAccountInfo/CustomerSubAccountInfo/CustomerCircuitInfo/PrimClsOfSvcCd\":\"HAPEE\",\"/InrDomCktResponse/Body/DomesticEthernetIOCInventory/CustomerAccountInfo/CustomerSubAccountInfo/CustomerCircuitInfo/ServiceIndicator\":\"PL\",\"/InrDomCktResponse/Body/DomesticEthernetIOCInventory/CustomerAccountInfo/CustomerSubAccountInfo/CustomerCircuitInfo/CktId\":\"BFEC558376   ATI\",\"/InrDomCktResponse/Body/DomesticEthernetIOCInventory/CustomerAccountInfo/CustomerSubAccountInfo/CustomerCircuitInfo/CatOfSvcCd\":\"OC48\",\"/InrDomCktResponse/Body/DomesticEthernetIOCInventory/CustomerAccountInfo/CustomerSubAccountInfo/CustomerCircuitInfo/Quality\":\"Standard\",\"/InrDomCktResponse/Body/DomesticEthernetIOCInventory/CustomerAccountInfo/CustomerSubAccountInfo/CustomerCircuitInfo/BillerID\":\"DCS-CR60\",\"/InrDomCktResponse/Body/DomesticEthernetIOCInventory/CustomerAccountInfo/CustomerSubAccountInfo/CustomerCircuitInfo/FALLOUTMATCHINGID\":\"0000000010/InrDomCktResponse/Body/DomesticEthernetIOCInventory/CustomerAccountInfo/CustomerSubAccountInfo/CustomerCircuitInfo/FALLOUTMATCHINGID\",\"/InrDomCktResponse/Body/DomesticEthernetIOCInventory/CustomerAccountInfo/CustomerSubAccountInfo/CustomerCircuitInfo/USOCInfo/CLLIZEnd\":\"HRFRCT03\",\"/InrDomCktResponse/Body/DomesticEthernetIOCInventory/CustomerAccountInfo/CustomerSubAccountInfo/CustomerCircuitInfo/USOCInfo/IOCMileage\":\"0303\",\"/InrDomCktResponse/Body/DomesticEthernetIOCInventory/CustomerAccountInfo/CustomerSubAccountInfo/CustomerCircuitInfo/USOCInfo/DisplaySpeed\":\"10 Gig\",\"/InrDomCktResponse/Body/DomesticEthernetIOCInventory/CustomerAccountInfo/CustomerSubAccountInfo/CustomerCircuitInfo/USOCInfo/USOCDescription\":\"INTEROFFICE CHANNEL\",\"/InrDomCktResponse/Body/DomesticEthernetIOCInventory/CustomerAccountInfo/CustomerSubAccountInfo/CustomerCircuitInfo/USOCInfo/Rate\":\"3559.80\",\"/InrDomCktResponse/Body/DomesticEthernetIOCInventory/CustomerAccountInfo/CustomerSubAccountInfo/CustomerCircuitInfo/USOCInfo/SecondaryCOS\":\"HAPEE\",\"/InrDomCktResponse/Body/DomesticEthernetIOCInventory/CustomerAccountInfo/CustomerSubAccountInfo/CustomerCircuitInfo/USOCInfo/RJA\":\"FTXX\",\"/InrDomCktResponse/Body/DomesticEthernetIOCInventory/CustomerAccountInfo/CustomerSubAccountInfo/CustomerCircuitInfo/USOCInfo/Quantity\":\"1\",\"/InrDomCktResponse/Body/DomesticEthernetIOCInventory/CustomerAccountInfo/CustomerSubAccountInfo/CustomerCircuitInfo/USOCInfo/USOC\":\"1LNVX\",\"/InrDomCktResponse/Body/DomesticEthernetIOCInventory/CustomerAccountInfo/CustomerSubAccountInfo/CustomerCircuitInfo/USOCInfo/NetRate\":\"3559.80\",\"/InrDomCktResponse/Body/DomesticEthernetIOCInventory/CustomerAccountInfo/CustomerSubAccountInfo/CustomerCircuitInfo/USOCInfo/AccessSpeed\":\"10 GBPS BASIC EPL-WAN\",\"/InrDomCktResponse/Body/DomesticEthernetIOCInventory/CustomerAccountInfo/CustomerSubAccountInfo/CustomerCircuitInfo/USOCInfo/CLLIAEnd\":\"WASHDCDT\",\"/InrDomCktResponse/Body/DomesticEthernetIOCInventory/CustomerAccountInfo/CustomerSubAccountInfo/CustomerCircuitInfo/USOCInfo/BandedRate\":\"0.00\",\"sequence\":1,\"FALLOUTMATCHINGID\":\"0000000010/InrDomCktResponse/Body/DomesticEthernetIOCInventory/CustomerAccountInfo/CustomerSubAccountInfo/CustomerCircuitInfo/FALLOUTMATCHINGID\"},{\"rootTag\":\"InrDomCktResponse\",\"/InrDomCktResponse/v1:Header/v1:CustomerName\":\"NA\",\"/InrDomCktResponse/v1:Header/v1:DUNSNumber\":\"945155190\",\"/InrDomCktResponse/v1:Header/v1:BillMonth\":\"September 2019\",\"/InrDomCktResponse/v1:Header/v1:ProprietaryStatement\":\"AT&T Proprietary (Restricted) Only for use by authorized individuals or any above-designated team(s) within the AT&T companies and not for general distribution.\",\"/InrDomCktResponse/v1:Header/xmlns:v1\":\"http://edb.att.com/inr/v1\",\"/InrDomCktResponse/v1:Header/v1:Product\":\"DOMESTIC PL IOC\",\"/InrDomCktResponse/v1:Header/v1:CorrelationID\":\"263749957\",\"/InrDomCktResponse/Body/DomesticEthernetIOCInventory/CustomerAccountInfo/SOC\":\"S1\",\"/InrDomCktResponse/Body/DomesticEthernetIOCInventory/CustomerAccountInfo/MCN\":\"SK9699\",\"/InrDomCktResponse/Body/DomesticEthernetIOCInventory/CustomerAccountInfo/GRC\":\"PL1\",\"/InrDomCktResponse/Body/DomesticEthernetIOCInventory/CustomerAccountInfo/CustomerName\":\"AETNA\",\"/InrDomCktResponse/Body/DomesticEthernetIOCInventory/CustomerAccountInfo/DunsNumber\":\"945155190\",\"/InrDomCktResponse/Body/DomesticEthernetIOCInventory/CustomerAccountInfo/CustomerSubAccountInfo/CustomerCircuitInfo/PrimClsOfSvcCd\":\"HAPDE\",\"/InrDomCktResponse/Body/DomesticEthernetIOCInventory/CustomerAccountInfo/CustomerSubAccountInfo/CustomerCircuitInfo/ServiceIndicator\":\"PL\",\"/InrDomCktResponse/Body/DomesticEthernetIOCInventory/CustomerAccountInfo/CustomerSubAccountInfo/CustomerCircuitInfo/CktId\":\"IZEC579038   ATI\",\"/InrDomCktResponse/Body/DomesticEthernetIOCInventory/CustomerAccountInfo/CustomerSubAccountInfo/CustomerCircuitInfo/CatOfSvcCd\":\"OC48\",\"/InrDomCktResponse/Body/DomesticEthernetIOCInventory/CustomerAccountInfo/CustomerSubAccountInfo/CustomerCircuitInfo/Quality\":\"Standard\",\"/InrDomCktResponse/Body/DomesticEthernetIOCInventory/CustomerAccountInfo/CustomerSubAccountInfo/CustomerCircuitInfo/BillerID\":\"DCS-CR60\",\"/InrDomCktResponse/Body/DomesticEthernetIOCInventory/CustomerAccountInfo/CustomerSubAccountInfo/CustomerCircuitInfo/FALLOUTMATCHINGID\":\"0000000009/InrDomCktResponse/Body/DomesticEthernetIOCInventory/CustomerAccountInfo/CustomerSubAccountInfo/CustomerCircuitInfo/FALLOUTMATCHINGID\",\"/InrDomCktResponse/Body/DomesticEthernetIOCInventory/CustomerAccountInfo/CustomerSubAccountInfo/CustomerCircuitInfo/USOCInfo/CLLIZEnd\":\"NYCMNY54\",\"/InrDomCktResponse/Body/DomesticEthernetIOCInventory/CustomerAccountInfo/CustomerSubAccountInfo/CustomerCircuitInfo/USOCInfo/IOCMileage\":\"0075\",\"/InrDomCktResponse/Body/DomesticEthernetIOCInventory/CustomerAccountInfo/CustomerSubAccountInfo/CustomerCircuitInfo/USOCInfo/DisplaySpeed\":\"1 Gig\",\"/InrDomCktResponse/Body/DomesticEthernetIOCInventory/CustomerAccountInfo/CustomerSubAccountInfo/CustomerCircuitInfo/USOCInfo/USOCDescription\":\"INTEROFFICE CHANNEL\",\"/InrDomCktResponse/Body/DomesticEthernetIOCInventory/CustomerAccountInfo/CustomerSubAccountInfo/CustomerCircuitInfo/USOCInfo/Rate\":\"3580.00\",\"/InrDomCktResponse/Body/DomesticEthernetIOCInventory/CustomerAccountInfo/CustomerSubAccountInfo/CustomerCircuitInfo/USOCInfo/SecondaryCOS\":\"HAPDE\",\"/InrDomCktResponse/Body/DomesticEthernetIOCInventory/CustomerAccountInfo/CustomerSubAccountInfo/CustomerCircuitInfo/USOCInfo/RJA\":\"FTXX\",\"/InrDomCktResponse/Body/DomesticEthernetIOCInventory/CustomerAccountInfo/CustomerSubAccountInfo/CustomerCircuitInfo/USOCInfo/Quantity\":\"1\",\"/InrDomCktResponse/Body/DomesticEthernetIOCInventory/CustomerAccountInfo/CustomerSubAccountInfo/CustomerCircuitInfo/USOCInfo/USOC\":\"1LNVX\",\"/InrDomCktResponse/Body/DomesticEthernetIOCInventory/CustomerAccountInfo/CustomerSubAccountInfo/CustomerCircuitInfo/USOCInfo/NetRate\":\"3580.00\",\"/InrDomCktResponse/Body/DomesticEthernetIOCInventory/CustomerAccountInfo/CustomerSubAccountInfo/CustomerCircuitInfo/USOCInfo/AccessSpeed\":\"1 GBPS BASIC EPL-WAN\",\"/InrDomCktResponse/Body/DomesticEthernetIOCInventory/CustomerAccountInfo/CustomerSubAccountInfo/CustomerCircuitInfo/USOCInfo/CLLIAEnd\":\"CHSHCT02\",\"/InrDomCktResponse/Body/DomesticEthernetIOCInventory/CustomerAccountInfo/CustomerSubAccountInfo/CustomerCircuitInfo/USOCInfo/BandedRate\":\"0.00\",\"sequence\":2,\"FALLOUTMATCHINGID\":\"0000000009/InrDomCktResponse/Body/DomesticEthernetIOCInventory/CustomerAccountInfo/CustomerSubAccountInfo/CustomerCircuitInfo/FALLOUTMATCHINGID\"},{\"rootTag\":\"InrDomCktResponse1\",\"/InrDomCktResponse/v1:Header/v1:CustomerName\":\"NA\",\"/InrDomCktResponse/v1:Header/v1:DUNSNumber\":\"945155190\",\"/InrDomCktResponse/v1:Header/v1:BillMonth\":\"September 2019\",\"/InrDomCktResponse/v1:Header/v1:ProprietaryStatement\":\"AT&T Proprietary (Restricted) Only for use by authorized individuals or any above-designated team(s) within the AT&T companies and not for general distribution.\",\"/InrDomCktResponse/v1:Header/xmlns:v1\":\"http://edb.att.com/inr/v1\",\"/InrDomCktResponse/v1:Header/v1:Product\":\"DOMESTIC PL IOC\",\"/InrDomCktResponse/v1:Header/v1:CorrelationID\":\"263749957\",\"/InrDomCktResponse/Body/DomesticIOCInventory/CustomerAccountInfo/SOC\":\"S1\",\"/InrDomCktResponse/Body/DomesticIOCInventory/CustomerAccountInfo/MCN\":\"824080\",\"/InrDomCktResponse/Body/DomesticIOCInventory/CustomerAccountInfo/GRC\":\"CTI\",\"/InrDomCktResponse/Body/DomesticIOCInventory/CustomerAccountInfo/CustomerName\":\"AETNA LIFE INSURANCE\",\"/InrDomCktResponse/Body/DomesticIOCInventory/CustomerAccountInfo/DunsNumber\":\"945155190\",\"/InrDomCktResponse/Body/DomesticIOCInventory/CustomerAccountInfo/CustomerSubAccountInfo/CustomerCircuitInfo/PrimClsOfSvcCd\":\"AGA5E\",\"/InrDomCktResponse/Body/DomesticIOCInventory/CustomerAccountInfo/CustomerSubAccountInfo/CustomerCircuitInfo/ServiceIndicator\":\"PL\",\"/InrDomCktResponse/Body/DomesticIOCInventory/CustomerAccountInfo/CustomerSubAccountInfo/CustomerCircuitInfo/CktId\":\"AREC552524   ATI\",\"/InrDomCktResponse/Body/DomesticIOCInventory/CustomerAccountInfo/CustomerSubAccountInfo/CustomerCircuitInfo/CatOfSvcCd\":\"ASDS\",\"/InrDomCktResponse/Body/DomesticIOCInventory/CustomerAccountInfo/CustomerSubAccountInfo/CustomerCircuitInfo/BillerID\":\"VTNS-BC\",\"/InrDomCktResponse/Body/DomesticIOCInventory/CustomerAccountInfo/CustomerSubAccountInfo/CustomerCircuitInfo/FALLOUTMATCHINGID\":\"0000000008/InrDomCktResponse/Body/DomesticIOCInventory/CustomerAccountInfo/CustomerSubAccountInfo/CustomerCircuitInfo/FALLOUTMATCHINGID\",\"/InrDomCktResponse/Body/DomesticIOCInventory/CustomerAccountInfo/CustomerSubAccountInfo/CustomerCircuitInfo/USOCInfo/CLLIZEnd\":\"JCSNMSPS\",\"/InrDomCktResponse/Body/DomesticIOCInventory/CustomerAccountInfo/CustomerSubAccountInfo/CustomerCircuitInfo/USOCInfo/IOCMileage\":\"1141.00\",\"/InrDomCktResponse/Body/DomesticIOCInventory/CustomerAccountInfo/CustomerSubAccountInfo/CustomerCircuitInfo/USOCInfo/Rate\":\"618.71\",\"/InrDomCktResponse/Body/DomesticIOCInventory/CustomerAccountInfo/CustomerSubAccountInfo/CustomerCircuitInfo/USOCInfo/SecondaryCOS\":\"AGA5E\",\"/InrDomCktResponse/Body/DomesticIOCInventory/CustomerAccountInfo/CustomerSubAccountInfo/CustomerCircuitInfo/USOCInfo/Quantity\":\"1\",\"/InrDomCktResponse/Body/DomesticIOCInventory/CustomerAccountInfo/CustomerSubAccountInfo/CustomerCircuitInfo/USOCInfo/USOC\":\"1LNVX\",\"/InrDomCktResponse/Body/DomesticIOCInventory/CustomerAccountInfo/CustomerSubAccountInfo/CustomerCircuitInfo/USOCInfo/NetRate\":\"618.71\",\"/InrDomCktResponse/Body/DomesticIOCInventory/CustomerAccountInfo/CustomerSubAccountInfo/CustomerCircuitInfo/USOCInfo/AccessSpeed\":\"DTC 56 MBR KBPS\",\"/InrDomCktResponse/Body/DomesticIOCInventory/CustomerAccountInfo/CustomerSubAccountInfo/CustomerCircuitInfo/USOCInfo/CLLIAEnd\":\"CHSHCT02\",\"/InrDomCktResponse/Body/DomesticIOCInventory/CustomerAccountInfo/CustomerSubAccountInfo/CustomerCircuitInfo/USOCInfo/BandedRate\":\"0.31\",\"/InrDomCktResponse/Body/DomesticIOCInventory/CustomerAccountInfo/CustomerSubAccountInfo/CustomerCircuitInfo/USOCInfo/CustomerDiscountPlanInfo/VolumeDiscountFigure\":\"00.00\",\"/InrDomCktResponse/Body/DomesticIOCInventory/CustomerAccountInfo/CustomerSubAccountInfo/CustomerCircuitInfo/USOCInfo/CustomerDiscountPlanInfo/DiscountPlanIdentifier\":\"DTCVTC TC    DOM022\",\"sequence\":1,\"FALLOUTMATCHINGID\":\"0000000008/InrDomCktResponse/Body/DomesticIOCInventory/CustomerAccountInfo/CustomerSubAccountInfo/CustomerCircuitInfo/FALLOUTMATCHINGID\"},{\"rootTag\":\"InrDomCktResponse1\",\"/InrDomCktResponse/v1:Header/v1:CustomerName\":\"NA\",\"/InrDomCktResponse/v1:Header/v1:DUNSNumber\":\"945155190\",\"/InrDomCktResponse/v1:Header/v1:BillMonth\":\"September 2019\",\"/InrDomCktResponse/v1:Header/v1:ProprietaryStatement\":\"AT&T Proprietary (Restricted) Only for use by authorized individuals or any above-designated team(s) within the AT&T companies and not for general distribution.\",\"/InrDomCktResponse/v1:Header/xmlns:v1\":\"http://edb.att.com/inr/v1\",\"/InrDomCktResponse/v1:Header/v1:Product\":\"DOMESTIC PL IOC\",\"/InrDomCktResponse/v1:Header/v1:CorrelationID\":\"263749957\",\"/InrDomCktResponse/Body/DomesticIOCInventory/CustomerAccountInfo/SOC\":\"S1\",\"/InrDomCktResponse/Body/DomesticIOCInventory/CustomerAccountInfo/MCN\":\"824080\",\"/InrDomCktResponse/Body/DomesticIOCInventory/CustomerAccountInfo/GRC\":\"CTI\",\"/InrDomCktResponse/Body/DomesticIOCInventory/CustomerAccountInfo/CustomerName\":\"AETNA LIFE INSURANCE\",\"/InrDomCktResponse/Body/DomesticIOCInventory/CustomerAccountInfo/DunsNumber\":\"945155190\",\"/InrDomCktResponse/Body/DomesticIOCInventory/CustomerAccountInfo/CustomerSubAccountInfo/CustomerCircuitInfo/PrimClsOfSvcCd\":\"AGA5E\",\"/InrDomCktResponse/Body/DomesticIOCInventory/CustomerAccountInfo/CustomerSubAccountInfo/CustomerCircuitInfo/ServiceIndicator\":\"PL\",\"/InrDomCktResponse/Body/DomesticIOCInventory/CustomerAccountInfo/CustomerSubAccountInfo/CustomerCircuitInfo/CktId\":\"AREC552544   ATI\",\"/InrDomCktResponse/Body/DomesticIOCInventory/CustomerAccountInfo/CustomerSubAccountInfo/CustomerCircuitInfo/CatOfSvcCd\":\"ASDS\",\"/InrDomCktResponse/Body/DomesticIOCInventory/CustomerAccountInfo/CustomerSubAccountInfo/CustomerCircuitInfo/BillerID\":\"VTNS-BC\",\"/InrDomCktResponse/Body/DomesticIOCInventory/CustomerAccountInfo/CustomerSubAccountInfo/CustomerCircuitInfo/FALLOUTMATCHINGID\":\"0000000007/InrDomCktResponse/Body/DomesticIOCInventory/CustomerAccountInfo/CustomerSubAccountInfo/CustomerCircuitInfo/FALLOUTMATCHINGID\",\"/InrDomCktResponse/Body/DomesticIOCInventory/CustomerAccountInfo/CustomerSubAccountInfo/CustomerCircuitInfo/USOCInfo/CLLIZEnd\":\"HRFRCT03\",\"/InrDomCktResponse/Body/DomesticIOCInventory/CustomerAccountInfo/CustomerSubAccountInfo/CustomerCircuitInfo/USOCInfo/IOCMileage\":\"0769.00\",\"/InrDomCktResponse/Body/DomesticIOCInventory/CustomerAccountInfo/CustomerSubAccountInfo/CustomerCircuitInfo/USOCInfo/Rate\":\"503.39\",\"/InrDomCktResponse/Body/DomesticIOCInventory/CustomerAccountInfo/CustomerSubAccountInfo/CustomerCircuitInfo/USOCInfo/SecondaryCOS\":\"AGA5E\",\"/InrDomCktResponse/Body/DomesticIOCInventory/CustomerAccountInfo/CustomerSubAccountInfo/CustomerCircuitInfo/USOCInfo/Quantity\":\"1\",\"/InrDomCktResponse/Body/DomesticIOCInventory/CustomerAccountInfo/CustomerSubAccountInfo/CustomerCircuitInfo/USOCInfo/USOC\":\"1LNVX\",\"/InrDomCktResponse/Body/DomesticIOCInventory/CustomerAccountInfo/CustomerSubAccountInfo/CustomerCircuitInfo/USOCInfo/NetRate\":\"503.39\",\"/InrDomCktResponse/Body/DomesticIOCInventory/CustomerAccountInfo/CustomerSubAccountInfo/CustomerCircuitInfo/USOCInfo/AccessSpeed\":\"DTC 56 MBR KBPS\",\"/InrDomCktResponse/Body/DomesticIOCInventory/CustomerAccountInfo/CustomerSubAccountInfo/CustomerCircuitInfo/USOCInfo/CLLIAEnd\":\"CHCGILCL\",\"/InrDomCktResponse/Body/DomesticIOCInventory/CustomerAccountInfo/CustomerSubAccountInfo/CustomerCircuitInfo/USOCInfo/BandedRate\":\"0.31\",\"/InrDomCktResponse/Body/DomesticIOCInventory/CustomerAccountInfo/CustomerSubAccountInfo/CustomerCircuitInfo/USOCInfo/CustomerDiscountPlanInfo/VolumeDiscountFigure\":\"00.00\",\"/InrDomCktResponse/Body/DomesticIOCInventory/CustomerAccountInfo/CustomerSubAccountInfo/CustomerCircuitInfo/USOCInfo/CustomerDiscountPlanInfo/DiscountPlanIdentifier\":\"DTCVTC TC    DOM022\",\"sequence\":2,\"FALLOUTMATCHINGID\":\"0000000007/InrDomCktResponse/Body/DomesticIOCInventory/CustomerAccountInfo/CustomerSubAccountInfo/CustomerCircuitInfo/FALLOUTMATCHINGID\"},{\"rootTag\":\"InrDomCktResponse1\",\"/InrDomCktResponse/v1:Header/v1:CustomerName\":\"NA\",\"/InrDomCktResponse/v1:Header/v1:DUNSNumber\":\"945155190\",\"/InrDomCktResponse/v1:Header/v1:BillMonth\":\"September 2019\",\"/InrDomCktResponse/v1:Header/v1:ProprietaryStatement\":\"AT&T Proprietary (Restricted) Only for use by authorized individuals or any above-designated team(s) within the AT&T companies and not for general distribution.\",\"/InrDomCktResponse/v1:Header/xmlns:v1\":\"http://edb.att.com/inr/v1\",\"/InrDomCktResponse/v1:Header/v1:Product\":\"DOMESTIC PL IOC\",\"/InrDomCktResponse/v1:Header/v1:CorrelationID\":\"263749957\",\"/InrDomCktResponse/Body/DomesticIOCInventory/CustomerAccountInfo/SOC\":\"S1\",\"/InrDomCktResponse/Body/DomesticIOCInventory/CustomerAccountInfo/MCN\":\"824080\",\"/InrDomCktResponse/Body/DomesticIOCInventory/CustomerAccountInfo/GRC\":\"CTI\",\"/InrDomCktResponse/Body/DomesticIOCInventory/CustomerAccountInfo/CustomerName\":\"AETNA LIFE INSURANCE\",\"/InrDomCktResponse/Body/DomesticIOCInventory/CustomerAccountInfo/DunsNumber\":\"945155190\",\"/InrDomCktResponse/Body/DomesticIOCInventory/CustomerAccountInfo/CustomerSubAccountInfo/CustomerCircuitInfo/PrimClsOfSvcCd\":\"AGA5E\",\"/InrDomCktResponse/Body/DomesticIOCInventory/CustomerAccountInfo/CustomerSubAccountInfo/CustomerCircuitInfo/ServiceIndicator\":\"PL\",\"/InrDomCktResponse/Body/DomesticIOCInventory/CustomerAccountInfo/CustomerSubAccountInfo/CustomerCircuitInfo/CktId\":\"AREC581439   ATI\",\"/InrDomCktResponse/Body/DomesticIOCInventory/CustomerAccountInfo/CustomerSubAccountInfo/CustomerCircuitInfo/CatOfSvcCd\":\"ASDS\",\"/InrDomCktResponse/Body/DomesticIOCInventory/CustomerAccountInfo/CustomerSubAccountInfo/CustomerCircuitInfo/BillerID\":\"VTNS-BC\",\"/InrDomCktResponse/Body/DomesticIOCInventory/CustomerAccountInfo/CustomerSubAccountInfo/CustomerCircuitInfo/FALLOUTMATCHINGID\":\"0000000006/InrDomCktResponse/Body/DomesticIOCInventory/CustomerAccountInfo/CustomerSubAccountInfo/CustomerCircuitInfo/FALLOUTMATCHINGID\",\"/InrDomCktResponse/Body/DomesticIOCInventory/CustomerAccountInfo/CustomerSubAccountInfo/CustomerCircuitInfo/USOCInfo/CLLIZEnd\":\"HRFRCT03\",\"/InrDomCktResponse/Body/DomesticIOCInventory/CustomerAccountInfo/CustomerSubAccountInfo/CustomerCircuitInfo/USOCInfo/IOCMileage\":\"0465.00\",\"/InrDomCktResponse/Body/DomesticIOCInventory/CustomerAccountInfo/CustomerSubAccountInfo/CustomerCircuitInfo/USOCInfo/Rate\":\"409.15\",\"/InrDomCktResponse/Body/DomesticIOCInventory/CustomerAccountInfo/CustomerSubAccountInfo/CustomerCircuitInfo/USOCInfo/SecondaryCOS\":\"AGA5E\",\"/InrDomCktResponse/Body/DomesticIOCInventory/CustomerAccountInfo/CustomerSubAccountInfo/CustomerCircuitInfo/USOCInfo/Quantity\":\"1\",\"/InrDomCktResponse/Body/DomesticIOCInventory/CustomerAccountInfo/CustomerSubAccountInfo/CustomerCircuitInfo/USOCInfo/USOC\":\"1LNVX\",\"/InrDomCktResponse/Body/DomesticIOCInventory/CustomerAccountInfo/CustomerSubAccountInfo/CustomerCircuitInfo/USOCInfo/NetRate\":\"409.15\",\"/InrDomCktResponse/Body/DomesticIOCInventory/CustomerAccountInfo/CustomerSubAccountInfo/CustomerCircuitInfo/USOCInfo/AccessSpeed\":\"DTC 56 MBR KBPS\",\"/InrDomCktResponse/Body/DomesticIOCInventory/CustomerAccountInfo/CustomerSubAccountInfo/CustomerCircuitInfo/USOCInfo/CLLIAEnd\":\"CLEVOH02\",\"/InrDomCktResponse/Body/DomesticIOCInventory/CustomerAccountInfo/CustomerSubAccountInfo/CustomerCircuitInfo/USOCInfo/BandedRate\":\"0.31\",\"/InrDomCktResponse/Body/DomesticIOCInventory/CustomerAccountInfo/CustomerSubAccountInfo/CustomerCircuitInfo/USOCInfo/CustomerDiscountPlanInfo/VolumeDiscountFigure\":\"00.00\",\"/InrDomCktResponse/Body/DomesticIOCInventory/CustomerAccountInfo/CustomerSubAccountInfo/CustomerCircuitInfo/USOCInfo/CustomerDiscountPlanInfo/DiscountPlanIdentifier\":\"DTCVTC TC    DOM022\",\"sequence\":3,\"FALLOUTMATCHINGID\":\"0000000006/InrDomCktResponse/Body/DomesticIOCInventory/CustomerAccountInfo/CustomerSubAccountInfo/CustomerCircuitInfo/FALLOUTMATCHINGID\"},{\"rootTag\":\"InrDomCktResponse1\",\"/InrDomCktResponse/v1:Header/v1:CustomerName\":\"NA\",\"/InrDomCktResponse/v1:Header/v1:DUNSNumber\":\"945155190\",\"/InrDomCktResponse/v1:Header/v1:BillMonth\":\"September 2019\",\"/InrDomCktResponse/v1:Header/v1:ProprietaryStatement\":\"AT&T Proprietary (Restricted) Only for use by authorized individuals or any above-designated team(s) within the AT&T companies and not for general distribution.\",\"/InrDomCktResponse/v1:Header/xmlns:v1\":\"http://edb.att.com/inr/v1\",\"/InrDomCktResponse/v1:Header/v1:Product\":\"DOMESTIC PL IOC\",\"/InrDomCktResponse/v1:Header/v1:CorrelationID\":\"263749957\",\"/InrDomCktResponse/Body/DomesticIOCInventory/CustomerAccountInfo/SOC\":\"S1\",\"/InrDomCktResponse/Body/DomesticIOCInventory/CustomerAccountInfo/MCN\":\"824080\",\"/InrDomCktResponse/Body/DomesticIOCInventory/CustomerAccountInfo/GRC\":\"CTI\",\"/InrDomCktResponse/Body/DomesticIOCInventory/CustomerAccountInfo/CustomerName\":\"AETNA LIFE INSURANCE\",\"/InrDomCktResponse/Body/DomesticIOCInventory/CustomerAccountInfo/DunsNumber\":\"945155190\",\"/InrDomCktResponse/Body/DomesticIOCInventory/CustomerAccountInfo/CustomerSubAccountInfo/CustomerCircuitInfo/PrimClsOfSvcCd\":\"AGA5E\",\"/InrDomCktResponse/Body/DomesticIOCInventory/CustomerAccountInfo/CustomerSubAccountInfo/CustomerCircuitInfo/ServiceIndicator\":\"PL\",\"/InrDomCktResponse/Body/DomesticIOCInventory/CustomerAccountInfo/CustomerSubAccountInfo/CustomerCircuitInfo/CktId\":\"AREC919547   ATI\",\"/InrDomCktResponse/Body/DomesticIOCInventory/CustomerAccountInfo/CustomerSubAccountInfo/CustomerCircuitInfo/CatOfSvcCd\":\"ASDS\",\"/InrDomCktResponse/Body/DomesticIOCInventory/CustomerAccountInfo/CustomerSubAccountInfo/CustomerCircuitInfo/BillerID\":\"VTNS-BC\",\"/InrDomCktResponse/Body/DomesticIOCInventory/CustomerAccountInfo/CustomerSubAccountInfo/CustomerCircuitInfo/FALLOUTMATCHINGID\":\"0000000005/InrDomCktResponse/Body/DomesticIOCInventory/CustomerAccountInfo/CustomerSubAccountInfo/CustomerCircuitInfo/FALLOUTMATCHINGID\",\"/InrDomCktResponse/Body/DomesticIOCInventory/CustomerAccountInfo/CustomerSubAccountInfo/CustomerCircuitInfo/USOCInfo/CLLIZEnd\":\"PITBPADG\",\"/InrDomCktResponse/Body/DomesticIOCInventory/CustomerAccountInfo/CustomerSubAccountInfo/CustomerCircuitInfo/USOCInfo/IOCMileage\":\"0375.00\",\"/InrDomCktResponse/Body/DomesticIOCInventory/CustomerAccountInfo/CustomerSubAccountInfo/CustomerCircuitInfo/USOCInfo/Rate\":\"381.25\",\"/InrDomCktResponse/Body/DomesticIOCInventory/CustomerAccountInfo/CustomerSubAccountInfo/CustomerCircuitInfo/USOCInfo/SecondaryCOS\":\"AGA5E\",\"/InrDomCktResponse/Body/DomesticIOCInventory/CustomerAccountInfo/CustomerSubAccountInfo/CustomerCircuitInfo/USOCInfo/Quantity\":\"1\",\"/InrDomCktResponse/Body/DomesticIOCInventory/CustomerAccountInfo/CustomerSubAccountInfo/CustomerCircuitInfo/USOCInfo/USOC\":\"1LNVX\",\"/InrDomCktResponse/Body/DomesticIOCInventory/CustomerAccountInfo/CustomerSubAccountInfo/CustomerCircuitInfo/USOCInfo/NetRate\":\"381.25\",\"/InrDomCktResponse/Body/DomesticIOCInventory/CustomerAccountInfo/CustomerSubAccountInfo/CustomerCircuitInfo/USOCInfo/AccessSpeed\":\"DTC 56 MBR KBPS\",\"/InrDomCktResponse/Body/DomesticIOCInventory/CustomerAccountInfo/CustomerSubAccountInfo/CustomerCircuitInfo/USOCInfo/CLLIAEnd\":\"CHSHCT02\",\"/InrDomCktResponse/Body/DomesticIOCInventory/CustomerAccountInfo/CustomerSubAccountInfo/CustomerCircuitInfo/USOCInfo/BandedRate\":\"0.31\",\"/InrDomCktResponse/Body/DomesticIOCInventory/CustomerAccountInfo/CustomerSubAccountInfo/CustomerCircuitInfo/USOCInfo/CustomerDiscountPlanInfo/VolumeDiscountFigure\":\"00.00\",\"/InrDomCktResponse/Body/DomesticIOCInventory/CustomerAccountInfo/CustomerSubAccountInfo/CustomerCircuitInfo/USOCInfo/CustomerDiscountPlanInfo/DiscountPlanIdentifier\":\"DTCVTC TC    DOM022\",\"sequence\":4,\"FALLOUTMATCHINGID\":\"0000000005/InrDomCktResponse/Body/DomesticIOCInventory/CustomerAccountInfo/CustomerSubAccountInfo/CustomerCircuitInfo/FALLOUTMATCHINGID\"},{\"rootTag\":\"InrDomCktResponse1\",\"/InrDomCktResponse/v1:Header/v1:CustomerName\":\"NA\",\"/InrDomCktResponse/v1:Header/v1:DUNSNumber\":\"945155190\",\"/InrDomCktResponse/v1:Header/v1:BillMonth\":\"September 2019\",\"/InrDomCktResponse/v1:Header/v1:ProprietaryStatement\":\"AT&T Proprietary (Restricted) Only for use by authorized individuals or any above-designated team(s) within the AT&T companies and not for general distribution.\",\"/InrDomCktResponse/v1:Header/xmlns:v1\":\"http://edb.att.com/inr/v1\",\"/InrDomCktResponse/v1:Header/v1:Product\":\"DOMESTIC PL IOC\",\"/InrDomCktResponse/v1:Header/v1:CorrelationID\":\"263749957\",\"/InrDomCktResponse/Body/DomesticIOCInventory/CustomerAccountInfo/SOC\":\"S1\",\"/InrDomCktResponse/Body/DomesticIOCInventory/CustomerAccountInfo/MCN\":\"824080\",\"/InrDomCktResponse/Body/DomesticIOCInventory/CustomerAccountInfo/GRC\":\"CTI\",\"/InrDomCktResponse/Body/DomesticIOCInventory/CustomerAccountInfo/CustomerName\":\"AETNA LIFE INSURANCE\",\"/InrDomCktResponse/Body/DomesticIOCInventory/CustomerAccountInfo/DunsNumber\":\"945155190\",\"/InrDomCktResponse/Body/DomesticIOCInventory/CustomerAccountInfo/CustomerSubAccountInfo/CustomerCircuitInfo/PrimClsOfSvcCd\":\"AGA5E\",\"/InrDomCktResponse/Body/DomesticIOCInventory/CustomerAccountInfo/CustomerSubAccountInfo/CustomerCircuitInfo/ServiceIndicator\":\"PL\",\"/InrDomCktResponse/Body/DomesticIOCInventory/CustomerAccountInfo/CustomerSubAccountInfo/CustomerCircuitInfo/CktId\":\"AREC919584   ATI\",\"/InrDomCktResponse/Body/DomesticIOCInventory/CustomerAccountInfo/CustomerSubAccountInfo/CustomerCircuitInfo/CatOfSvcCd\":\"ASDS\",\"/InrDomCktResponse/Body/DomesticIOCInventory/CustomerAccountInfo/CustomerSubAccountInfo/CustomerCircuitInfo/BillerID\":\"VTNS-BC\",\"/InrDomCktResponse/Body/DomesticIOCInventory/CustomerAccountInfo/CustomerSubAccountInfo/CustomerCircuitInfo/FALLOUTMATCHINGID\":\"0000000004/InrDomCktResponse/Body/DomesticIOCInventory/CustomerAccountInfo/CustomerSubAccountInfo/CustomerCircuitInfo/FALLOUTMATCHINGID\",\"/InrDomCktResponse/Body/DomesticIOCInventory/CustomerAccountInfo/CustomerSubAccountInfo/CustomerCircuitInfo/USOCInfo/CLLIZEnd\":\"PHLAPASL\",\"/InrDomCktResponse/Body/DomesticIOCInventory/CustomerAccountInfo/CustomerSubAccountInfo/CustomerCircuitInfo/USOCInfo/IOCMileage\":\"0160.00\",\"/InrDomCktResponse/Body/DomesticIOCInventory/CustomerAccountInfo/CustomerSubAccountInfo/CustomerCircuitInfo/USOCInfo/Rate\":\"314.60\",\"/InrDomCktResponse/Body/DomesticIOCInventory/CustomerAccountInfo/CustomerSubAccountInfo/CustomerCircuitInfo/USOCInfo/SecondaryCOS\":\"AGA5E\",\"/InrDomCktResponse/Body/DomesticIOCInventory/CustomerAccountInfo/CustomerSubAccountInfo/CustomerCircuitInfo/USOCInfo/Quantity\":\"1\",\"/InrDomCktResponse/Body/DomesticIOCInventory/CustomerAccountInfo/CustomerSubAccountInfo/CustomerCircuitInfo/USOCInfo/USOC\":\"1LNVX\",\"/InrDomCktResponse/Body/DomesticIOCInventory/CustomerAccountInfo/CustomerSubAccountInfo/CustomerCircuitInfo/USOCInfo/NetRate\":\"314.60\",\"/InrDomCktResponse/Body/DomesticIOCInventory/CustomerAccountInfo/CustomerSubAccountInfo/CustomerCircuitInfo/USOCInfo/AccessSpeed\":\"DTC 56 MBR KBPS\",\"/InrDomCktResponse/Body/DomesticIOCInventory/CustomerAccountInfo/CustomerSubAccountInfo/CustomerCircuitInfo/USOCInfo/CLLIAEnd\":\"CHSHCT02\",\"/InrDomCktResponse/Body/DomesticIOCInventory/CustomerAccountInfo/CustomerSubAccountInfo/CustomerCircuitInfo/USOCInfo/BandedRate\":\"0.31\",\"/InrDomCktResponse/Body/DomesticIOCInventory/CustomerAccountInfo/CustomerSubAccountInfo/CustomerCircuitInfo/USOCInfo/CustomerDiscountPlanInfo/VolumeDiscountFigure\":\"00.00\",\"/InrDomCktResponse/Body/DomesticIOCInventory/CustomerAccountInfo/CustomerSubAccountInfo/CustomerCircuitInfo/USOCInfo/CustomerDiscountPlanInfo/DiscountPlanIdentifier\":\"DTCVTC TC    DOM022\",\"sequence\":5,\"FALLOUTMATCHINGID\":\"0000000004/InrDomCktResponse/Body/DomesticIOCInventory/CustomerAccountInfo/CustomerSubAccountInfo/CustomerCircuitInfo/FALLOUTMATCHINGID\"},{\"rootTag\":\"InrDomCktResponse1\",\"/InrDomCktResponse/v1:Header/v1:CustomerName\":\"NA\",\"/InrDomCktResponse/v1:Header/v1:DUNSNumber\":\"945155190\",\"/InrDomCktResponse/v1:Header/v1:BillMonth\":\"September 2019\",\"/InrDomCktResponse/v1:Header/v1:ProprietaryStatement\":\"AT&T Proprietary (Restricted) Only for use by authorized individuals or any above-designated team(s) within the AT&T companies and not for general distribution.\",\"/InrDomCktResponse/v1:Header/xmlns:v1\":\"http://edb.att.com/inr/v1\",\"/InrDomCktResponse/v1:Header/v1:Product\":\"DOMESTIC PL IOC\",\"/InrDomCktResponse/v1:Header/v1:CorrelationID\":\"263749957\",\"/InrDomCktResponse/Body/DomesticIOCInventory/CustomerAccountInfo/SOC\":\"S1\",\"/InrDomCktResponse/Body/DomesticIOCInventory/CustomerAccountInfo/MCN\":\"824080\",\"/InrDomCktResponse/Body/DomesticIOCInventory/CustomerAccountInfo/GRC\":\"CTI\",\"/InrDomCktResponse/Body/DomesticIOCInventory/CustomerAccountInfo/CustomerName\":\"AETNA LIFE INSURANCE\",\"/InrDomCktResponse/Body/DomesticIOCInventory/CustomerAccountInfo/DunsNumber\":\"945155190\",\"/InrDomCktResponse/Body/DomesticIOCInventory/CustomerAccountInfo/CustomerSubAccountInfo/CustomerCircuitInfo/PrimClsOfSvcCd\":\"AGA5E\",\"/InrDomCktResponse/Body/DomesticIOCInventory/CustomerAccountInfo/CustomerSubAccountInfo/CustomerCircuitInfo/ServiceIndicator\":\"PL\",\"/InrDomCktResponse/Body/DomesticIOCInventory/CustomerAccountInfo/CustomerSubAccountInfo/CustomerCircuitInfo/CktId\":\"AREC937451   ATI\",\"/InrDomCktResponse/Body/DomesticIOCInventory/CustomerAccountInfo/CustomerSubAccountInfo/CustomerCircuitInfo/CatOfSvcCd\":\"ASDS\",\"/InrDomCktResponse/Body/DomesticIOCInventory/CustomerAccountInfo/CustomerSubAccountInfo/CustomerCircuitInfo/BillerID\":\"VTNS-BC\",\"/InrDomCktResponse/Body/DomesticIOCInventory/CustomerAccountInfo/CustomerSubAccountInfo/CustomerCircuitInfo/FALLOUTMATCHINGID\":\"0000000003/InrDomCktResponse/Body/DomesticIOCInventory/CustomerAccountInfo/CustomerSubAccountInfo/CustomerCircuitInfo/FALLOUTMATCHINGID\",\"/InrDomCktResponse/Body/DomesticIOCInventory/CustomerAccountInfo/CustomerSubAccountInfo/CustomerCircuitInfo/USOCInfo/CLLIZEnd\":\"JCVLFLCL\",\"/InrDomCktResponse/Body/DomesticIOCInventory/CustomerAccountInfo/CustomerSubAccountInfo/CustomerCircuitInfo/USOCInfo/IOCMileage\":\"0914.00\",\"/InrDomCktResponse/Body/DomesticIOCInventory/CustomerAccountInfo/CustomerSubAccountInfo/CustomerCircuitInfo/USOCInfo/Rate\":\"548.34\",\"/InrDomCktResponse/Body/DomesticIOCInventory/CustomerAccountInfo/CustomerSubAccountInfo/CustomerCircuitInfo/USOCInfo/SecondaryCOS\":\"AGA5E\",\"/InrDomCktResponse/Body/DomesticIOCInventory/CustomerAccountInfo/CustomerSubAccountInfo/CustomerCircuitInfo/USOCInfo/Quantity\":\"1\",\"/InrDomCktResponse/Body/DomesticIOCInventory/CustomerAccountInfo/CustomerSubAccountInfo/CustomerCircuitInfo/USOCInfo/USOC\":\"1LNVX\",\"/InrDomCktResponse/Body/DomesticIOCInventory/CustomerAccountInfo/CustomerSubAccountInfo/CustomerCircuitInfo/USOCInfo/NetRate\":\"548.34\",\"/InrDomCktResponse/Body/DomesticIOCInventory/CustomerAccountInfo/CustomerSubAccountInfo/CustomerCircuitInfo/USOCInfo/AccessSpeed\":\"DTC 56 MBR KBPS\",\"/InrDomCktResponse/Body/DomesticIOCInventory/CustomerAccountInfo/CustomerSubAccountInfo/CustomerCircuitInfo/USOCInfo/CLLIAEnd\":\"CHSHCT02\",\"/InrDomCktResponse/Body/DomesticIOCInventory/CustomerAccountInfo/CustomerSubAccountInfo/CustomerCircuitInfo/USOCInfo/BandedRate\":\"0.31\",\"/InrDomCktResponse/Body/DomesticIOCInventory/CustomerAccountInfo/CustomerSubAccountInfo/CustomerCircuitInfo/USOCInfo/CustomerDiscountPlanInfo/VolumeDiscountFigure\":\"00.00\",\"/InrDomCktResponse/Body/DomesticIOCInventory/CustomerAccountInfo/CustomerSubAccountInfo/CustomerCircuitInfo/USOCInfo/CustomerDiscountPlanInfo/DiscountPlanIdentifier\":\"DTCVTC TC    DOM022\",\"sequence\":6,\"FALLOUTMATCHINGID\":\"0000000003/InrDomCktResponse/Body/DomesticIOCInventory/CustomerAccountInfo/CustomerSubAccountInfo/CustomerCircuitInfo/FALLOUTMATCHINGID\"},{\"rootTag\":\"InrDomCktResponse1\",\"/InrDomCktResponse/v1:Header/v1:CustomerName\":\"NA\",\"/InrDomCktResponse/v1:Header/v1:DUNSNumber\":\"945155190\",\"/InrDomCktResponse/v1:Header/v1:BillMonth\":\"September 2019\",\"/InrDomCktResponse/v1:Header/v1:ProprietaryStatement\":\"AT&T Proprietary (Restricted) Only for use by authorized individuals or any above-designated team(s) within the AT&T companies and not for general distribution.\",\"/InrDomCktResponse/v1:Header/xmlns:v1\":\"http://edb.att.com/inr/v1\",\"/InrDomCktResponse/v1:Header/v1:Product\":\"DOMESTIC PL IOC\",\"/InrDomCktResponse/v1:Header/v1:CorrelationID\":\"263749957\",\"/InrDomCktResponse/Body/DomesticIOCInventory/CustomerAccountInfo/SOC\":\"S1\",\"/InrDomCktResponse/Body/DomesticIOCInventory/CustomerAccountInfo/MCN\":\"SK9699\",\"/InrDomCktResponse/Body/DomesticIOCInventory/CustomerAccountInfo/GRC\":\"PL1\",\"/InrDomCktResponse/Body/DomesticIOCInventory/CustomerAccountInfo/CustomerName\":\"AETNA\",\"/InrDomCktResponse/Body/DomesticIOCInventory/CustomerAccountInfo/DunsNumber\":\"945155190\",\"/InrDomCktResponse/Body/DomesticIOCInventory/CustomerAccountInfo/CustomerSubAccountInfo/CustomerCircuitInfo/PrimClsOfSvcCd\":\"HPPKE\",\"/InrDomCktResponse/Body/DomesticIOCInventory/CustomerAccountInfo/CustomerSubAccountInfo/CustomerCircuitInfo/ServiceIndicator\":\"PL\",\"/InrDomCktResponse/Body/DomesticIOCInventory/CustomerAccountInfo/CustomerSubAccountInfo/CustomerCircuitInfo/CktId\":\"IVEC990793   ATI\",\"/InrDomCktResponse/Body/DomesticIOCInventory/CustomerAccountInfo/CustomerSubAccountInfo/CustomerCircuitInfo/CatOfSvcCd\":\"OC12\",\"/InrDomCktResponse/Body/DomesticIOCInventory/CustomerAccountInfo/CustomerSubAccountInfo/CustomerCircuitInfo/BillerID\":\"DCS-CR60\",\"/InrDomCktResponse/Body/DomesticIOCInventory/CustomerAccountInfo/CustomerSubAccountInfo/CustomerCircuitInfo/FALLOUTMATCHINGID\":\"0000000002/InrDomCktResponse/Body/DomesticIOCInventory/CustomerAccountInfo/CustomerSubAccountInfo/CustomerCircuitInfo/FALLOUTMATCHINGID\",\"/InrDomCktResponse/Body/DomesticIOCInventory/CustomerAccountInfo/CustomerSubAccountInfo/CustomerCircuitInfo/USOCInfo/CLLIZEnd\":\"NWRKNJ02\",\"/InrDomCktResponse/Body/DomesticIOCInventory/CustomerAccountInfo/CustomerSubAccountInfo/CustomerCircuitInfo/USOCInfo/IOCMileage\":\"0106\",\"/InrDomCktResponse/Body/DomesticIOCInventory/CustomerAccountInfo/CustomerSubAccountInfo/CustomerCircuitInfo/USOCInfo/USOCDescription\":\"INTEROFFICE CHANNEL\",\"/InrDomCktResponse/Body/DomesticIOCInventory/CustomerAccountInfo/CustomerSubAccountInfo/CustomerCircuitInfo/USOCInfo/Rate\":\"3567.00\",\"/InrDomCktResponse/Body/DomesticIOCInventory/CustomerAccountInfo/CustomerSubAccountInfo/CustomerCircuitInfo/USOCInfo/SecondaryCOS\":\"HPPKE\",\"/InrDomCktResponse/Body/DomesticIOCInventory/CustomerAccountInfo/CustomerSubAccountInfo/CustomerCircuitInfo/USOCInfo/RJA\":\"FCAIXX14000474\",\"/InrDomCktResponse/Body/DomesticIOCInventory/CustomerAccountInfo/CustomerSubAccountInfo/CustomerCircuitInfo/USOCInfo/Quantity\":\"1\",\"/InrDomCktResponse/Body/DomesticIOCInventory/CustomerAccountInfo/CustomerSubAccountInfo/CustomerCircuitInfo/USOCInfo/USOC\":\"1LNVX\",\"/InrDomCktResponse/Body/DomesticIOCInventory/CustomerAccountInfo/CustomerSubAccountInfo/CustomerCircuitInfo/USOCInfo/NetRate\":\"3567.00\",\"/InrDomCktResponse/Body/DomesticIOCInventory/CustomerAccountInfo/CustomerSubAccountInfo/CustomerCircuitInfo/USOCInfo/AccessSpeed\":\"OC12 APLS INTERSTATE\",\"/InrDomCktResponse/Body/DomesticIOCInventory/CustomerAccountInfo/CustomerSubAccountInfo/CustomerCircuitInfo/USOCInfo/CLLIAEnd\":\"HRFRCT03\",\"/InrDomCktResponse/Body/DomesticIOCInventory/CustomerAccountInfo/CustomerSubAccountInfo/CustomerCircuitInfo/USOCInfo/BandedRate\":\"0.00\",\"/InrDomCktResponse/Body/DomesticIOCInventory/CustomerAccountInfo/CustomerSubAccountInfo/CustomerCircuitInfo/USOCInfo/CustomerDiscountPlanInfo/VolumeDiscountFigure\":\"0000000\",\"/InrDomCktResponse/Body/DomesticIOCInventory/CustomerAccountInfo/CustomerSubAccountInfo/CustomerCircuitInfo/USOCInfo/CustomerDiscountPlanInfo/DiscountPlanIdentifier\":\"SK9699,DSVPPCMG801\",\"sequence\":7,\"FALLOUTMATCHINGID\":\"0000000002/InrDomCktResponse/Body/DomesticIOCInventory/CustomerAccountInfo/CustomerSubAccountInfo/CustomerCircuitInfo/FALLOUTMATCHINGID\"},{\"rootTag\":\"InrDomCktResponse1\",\"/InrDomCktResponse/v1:Header/v1:CustomerName\":\"NA\",\"/InrDomCktResponse/v1:Header/v1:DUNSNumber\":\"945155190\",\"/InrDomCktResponse/v1:Header/v1:BillMonth\":\"September 2019\",\"/InrDomCktResponse/v1:Header/v1:ProprietaryStatement\":\"AT&T Proprietary (Restricted) Only for use by authorized individuals or any above-designated team(s) within the AT&T companies and not for general distribution.\",\"/InrDomCktResponse/v1:Header/xmlns:v1\":\"http://edb.att.com/inr/v1\",\"/InrDomCktResponse/v1:Header/v1:Product\":\"DOMESTIC PL IOC\",\"/InrDomCktResponse/v1:Header/v1:CorrelationID\":\"263749957\",\"/InrDomCktResponse/Body/DomesticIOCInventory/CustomerAccountInfo/SOC\":\"3J\",\"/InrDomCktResponse/Body/DomesticIOCInventory/CustomerAccountInfo/MCN\":\"SL5076\",\"/InrDomCktResponse/Body/DomesticIOCInventory/CustomerAccountInfo/GRC\":\"WAV\",\"/InrDomCktResponse/Body/DomesticIOCInventory/CustomerAccountInfo/CustomerName\":\"AETNA, INC\",\"/InrDomCktResponse/Body/DomesticIOCInventory/CustomerAccountInfo/DunsNumber\":\"945155190\",\"/InrDomCktResponse/Body/DomesticIOCInventory/CustomerAccountInfo/CustomerSubAccountInfo/CustomerCircuitInfo/PrimClsOfSvcCd\":\"HED2E\",\"/InrDomCktResponse/Body/DomesticIOCInventory/CustomerAccountInfo/CustomerSubAccountInfo/CustomerCircuitInfo/ServiceIndicator\":\"ETH\",\"/InrDomCktResponse/Body/DomesticIOCInventory/CustomerAccountInfo/CustomerSubAccountInfo/CustomerCircuitInfo/CktId\":\"L4YS989809   ATI\",\"/InrDomCktResponse/Body/DomesticIOCInventory/CustomerAccountInfo/CustomerSubAccountInfo/CustomerCircuitInfo/CatOfSvcCd\":\"OC48\",\"/InrDomCktResponse/Body/DomesticIOCInventory/CustomerAccountInfo/CustomerSubAccountInfo/CustomerCircuitInfo/BillerID\":\"DCS-CR60\",\"/InrDomCktResponse/Body/DomesticIOCInventory/CustomerAccountInfo/CustomerSubAccountInfo/CustomerCircuitInfo/FALLOUTMATCHINGID\":\"0000000001/InrDomCktResponse/Body/DomesticIOCInventory/CustomerAccountInfo/CustomerSubAccountInfo/CustomerCircuitInfo/FALLOUTMATCHINGID\",\"/InrDomCktResponse/Body/DomesticIOCInventory/CustomerAccountInfo/CustomerSubAccountInfo/CustomerCircuitInfo/USOCInfo/CLLIZEnd\":\"HRFRCT03\",\"/InrDomCktResponse/Body/DomesticIOCInventory/CustomerAccountInfo/CustomerSubAccountInfo/CustomerCircuitInfo/USOCInfo/IOCMileage\":\"0092\",\"/InrDomCktResponse/Body/DomesticIOCInventory/CustomerAccountInfo/CustomerSubAccountInfo/CustomerCircuitInfo/USOCInfo/USOC\":\"1LNVX\",\"/InrDomCktResponse/Body/DomesticIOCInventory/CustomerAccountInfo/CustomerSubAccountInfo/CustomerCircuitInfo/USOCInfo/USOCDescription\":\"INTEROFFICE CHANNEL\",\"/InrDomCktResponse/Body/DomesticIOCInventory/CustomerAccountInfo/CustomerSubAccountInfo/CustomerCircuitInfo/USOCInfo/NetRate\":\"0.00\",\"/InrDomCktResponse/Body/DomesticIOCInventory/CustomerAccountInfo/CustomerSubAccountInfo/CustomerCircuitInfo/USOCInfo/AccessSpeed\":\"10 GBPS BASIC SVC\",\"/InrDomCktResponse/Body/DomesticIOCInventory/CustomerAccountInfo/CustomerSubAccountInfo/CustomerCircuitInfo/USOCInfo/Rate\":\"0.00\",\"/InrDomCktResponse/Body/DomesticIOCInventory/CustomerAccountInfo/CustomerSubAccountInfo/CustomerCircuitInfo/USOCInfo/SecondaryCOS\":\"HED2E\",\"/InrDomCktResponse/Body/DomesticIOCInventory/CustomerAccountInfo/CustomerSubAccountInfo/CustomerCircuitInfo/USOCInfo/RJA\":\"FCAIXX03000824\",\"/InrDomCktResponse/Body/DomesticIOCInventory/CustomerAccountInfo/CustomerSubAccountInfo/CustomerCircuitInfo/USOCInfo/Quantity\":\"1\",\"/InrDomCktResponse/Body/DomesticIOCInventory/CustomerAccountInfo/CustomerSubAccountInfo/CustomerCircuitInfo/USOCInfo/CLLIAEnd\":\"CMBRMA01\",\"/InrDomCktResponse/Body/DomesticIOCInventory/CustomerAccountInfo/CustomerSubAccountInfo/CustomerCircuitInfo/USOCInfo/BandedRate\":\"0.00\",\"sequence\":8,\"FALLOUTMATCHINGID\":\"0000000001/InrDomCktResponse/Body/DomesticIOCInventory/CustomerAccountInfo/CustomerSubAccountInfo/CustomerCircuitInfo/FALLOUTMATCHINGID\"}],\"falloutSheet\":[{\"/fallout/PrimClsOfSvcCd\":\"AGA5E\",\"/fallout/AccessSpeed\":\"DTC 56 MBR KBPS\",\"/fallout/productCd\":\"Private Line\",\"/InrDomCktResponse/Body/DomesticIOCInventory/CustomerAccountInfo/CustomerSubAccountInfo/CustomerCircuitInfo/PrimClsOfSvcCd\":\"AGA5E\",\"/InrDomCktResponse/Body/DomesticIOCInventory/CustomerAccountInfo/CustomerSubAccountInfo/CustomerCircuitInfo/FALLOUTMATCHINGID\":\"0000000008/InrDomCktResponse/Body/DomesticIOCInventory/CustomerAccountInfo/CustomerSubAccountInfo/CustomerCircuitInfo/FALLOUTMATCHINGID\",\"/InrDomCktResponse/Body/DomesticIOCInventory/CustomerAccountInfo/CustomerSubAccountInfo/CustomerCircuitInfo/USOCInfo/AccessSpeed\":\"DTC 56 MBR KBPS\",\"/fallout/reason\":\"Unable to find a line item for your data. Please contact IT team to fix the issue.\",\"rootTag\":\"foInrDomCktResponse1\"},{\"/fallout/PrimClsOfSvcCd\":\"AGA5E\",\"/fallout/AccessSpeed\":\"DTC 56 MBR KBPS\",\"/fallout/productCd\":\"Private Line\",\"/InrDomCktResponse/Body/DomesticIOCInventory/CustomerAccountInfo/CustomerSubAccountInfo/CustomerCircuitInfo/PrimClsOfSvcCd\":\"AGA5E\",\"/InrDomCktResponse/Body/DomesticIOCInventory/CustomerAccountInfo/CustomerSubAccountInfo/CustomerCircuitInfo/FALLOUTMATCHINGID\":\"0000000007/InrDomCktResponse/Body/DomesticIOCInventory/CustomerAccountInfo/CustomerSubAccountInfo/CustomerCircuitInfo/FALLOUTMATCHINGID\",\"/InrDomCktResponse/Body/DomesticIOCInventory/CustomerAccountInfo/CustomerSubAccountInfo/CustomerCircuitInfo/USOCInfo/AccessSpeed\":\"DTC 56 MBR KBPS\",\"/fallout/reason\":\"Unable to find a line item for your data. Please contact IT team to fix the issue.\",\"rootTag\":\"foInrDomCktResponse1\"},{\"/fallout/PrimClsOfSvcCd\":\"AGA5E\",\"/fallout/AccessSpeed\":\"DTC 56 MBR KBPS\",\"/fallout/productCd\":\"Private Line\",\"/InrDomCktResponse/Body/DomesticIOCInventory/CustomerAccountInfo/CustomerSubAccountInfo/CustomerCircuitInfo/PrimClsOfSvcCd\":\"AGA5E\",\"/InrDomCktResponse/Body/DomesticIOCInventory/CustomerAccountInfo/CustomerSubAccountInfo/CustomerCircuitInfo/FALLOUTMATCHINGID\":\"0000000006/InrDomCktResponse/Body/DomesticIOCInventory/CustomerAccountInfo/CustomerSubAccountInfo/CustomerCircuitInfo/FALLOUTMATCHINGID\",\"/InrDomCktResponse/Body/DomesticIOCInventory/CustomerAccountInfo/CustomerSubAccountInfo/CustomerCircuitInfo/USOCInfo/AccessSpeed\":\"DTC 56 MBR KBPS\",\"/fallout/reason\":\"Unable to find a line item for your data. Please contact IT team to fix the issue.\",\"rootTag\":\"foInrDomCktResponse1\"},{\"/fallout/PrimClsOfSvcCd\":\"AGA5E\",\"/fallout/AccessSpeed\":\"DTC 56 MBR KBPS\",\"/fallout/productCd\":\"Private Line\",\"/InrDomCktResponse/Body/DomesticIOCInventory/CustomerAccountInfo/CustomerSubAccountInfo/CustomerCircuitInfo/PrimClsOfSvcCd\":\"AGA5E\",\"/InrDomCktResponse/Body/DomesticIOCInventory/CustomerAccountInfo/CustomerSubAccountInfo/CustomerCircuitInfo/FALLOUTMATCHINGID\":\"0000000005/InrDomCktResponse/Body/DomesticIOCInventory/CustomerAccountInfo/CustomerSubAccountInfo/CustomerCircuitInfo/FALLOUTMATCHINGID\",\"/InrDomCktResponse/Body/DomesticIOCInventory/CustomerAccountInfo/CustomerSubAccountInfo/CustomerCircuitInfo/USOCInfo/AccessSpeed\":\"DTC 56 MBR KBPS\",\"/fallout/reason\":\"Unable to find a line item for your data. Please contact IT team to fix the issue.\",\"rootTag\":\"foInrDomCktResponse1\"},{\"/fallout/PrimClsOfSvcCd\":\"AGA5E\",\"/fallout/AccessSpeed\":\"DTC 56 MBR KBPS\",\"/fallout/productCd\":\"Private Line\",\"/InrDomCktResponse/Body/DomesticIOCInventory/CustomerAccountInfo/CustomerSubAccountInfo/CustomerCircuitInfo/PrimClsOfSvcCd\":\"AGA5E\",\"/InrDomCktResponse/Body/DomesticIOCInventory/CustomerAccountInfo/CustomerSubAccountInfo/CustomerCircuitInfo/FALLOUTMATCHINGID\":\"0000000004/InrDomCktResponse/Body/DomesticIOCInventory/CustomerAccountInfo/CustomerSubAccountInfo/CustomerCircuitInfo/FALLOUTMATCHINGID\",\"/InrDomCktResponse/Body/DomesticIOCInventory/CustomerAccountInfo/CustomerSubAccountInfo/CustomerCircuitInfo/USOCInfo/AccessSpeed\":\"DTC 56 MBR KBPS\",\"/fallout/reason\":\"Unable to find a line item for your data. Please contact IT team to fix the issue.\",\"rootTag\":\"foInrDomCktResponse1\"},{\"/fallout/PrimClsOfSvcCd\":\"AGA5E\",\"/fallout/AccessSpeed\":\"DTC 56 MBR KBPS\",\"/fallout/productCd\":\"Private Line\",\"/InrDomCktResponse/Body/DomesticIOCInventory/CustomerAccountInfo/CustomerSubAccountInfo/CustomerCircuitInfo/PrimClsOfSvcCd\":\"AGA5E\",\"/InrDomCktResponse/Body/DomesticIOCInventory/CustomerAccountInfo/CustomerSubAccountInfo/CustomerCircuitInfo/FALLOUTMATCHINGID\":\"0000000003/InrDomCktResponse/Body/DomesticIOCInventory/CustomerAccountInfo/CustomerSubAccountInfo/CustomerCircuitInfo/FALLOUTMATCHINGID\",\"/InrDomCktResponse/Body/DomesticIOCInventory/CustomerAccountInfo/CustomerSubAccountInfo/CustomerCircuitInfo/USOCInfo/AccessSpeed\":\"DTC 56 MBR KBPS\",\"/fallout/reason\":\"Unable to find a line item for your data. Please contact IT team to fix the issue.\",\"rootTag\":\"foInrDomCktResponse1\"},{\"/fallout/PrimClsOfSvcCd\":\"HED2E\",\"/fallout/AccessSpeed\":\"10 GBPS BASIC SVC\",\"/fallout/productCd\":\"Private Line\",\"/InrDomCktResponse/Body/DomesticIOCInventory/CustomerAccountInfo/CustomerSubAccountInfo/CustomerCircuitInfo/PrimClsOfSvcCd\":\"HED2E\",\"/InrDomCktResponse/Body/DomesticIOCInventory/CustomerAccountInfo/CustomerSubAccountInfo/CustomerCircuitInfo/FALLOUTMATCHINGID\":\"0000000001/InrDomCktResponse/Body/DomesticIOCInventory/CustomerAccountInfo/CustomerSubAccountInfo/CustomerCircuitInfo/FALLOUTMATCHINGID\",\"/InrDomCktResponse/Body/DomesticIOCInventory/CustomerAccountInfo/CustomerSubAccountInfo/CustomerCircuitInfo/USOCInfo/AccessSpeed\":\"10 GBPS BASIC SVC\",\"/fallout/reason\":\"Unable to find a line item for your data. Please contact IT team to fix the issue.\",\"rootTag\":\"foInrDomCktResponse1\"}],\"rootTag\":[\"InrDomCktResponse\",\"InrDomCktResponse1\",\"foInrDomCktResponse1\"]}");
		fileModels.add(value);
		newNxRequestDetails.setNxOutputFiles(fileModels);
		ObjectMapper objMap = new ObjectMapper();
		try {
			JsonNode cdirDataNode = objMap.readTree(value.getCdirData());
			Mockito.when(mapper.readTree(anyString())).thenReturn(cdirDataNode);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		NxDesignAudit design = new NxDesignAudit();
		design.setData("[IZEC579038ATI]");
		Mockito.when(nxDesignAuditRepository.findByNxRefIdAndTransaction(anyLong(), anyString())).thenReturn(design);
		falloutDetailsImpl.updateNxOutputFileCdirData(newNxRequestDetails, newNxRequestDetails.getNxReqId());
	}
	
	@Test
	public void testCopyNew() throws SalesBusinessException {
		FalloutDetailsRequest request = new FalloutDetailsRequest();
		request.setAction("copyNew");
		request.setNxSolutionId(12345l);
		request.setNxReqId(1l);
		request.setAttuid("test");
		NxRequestDetails nxRequestDetails = new NxRequestDetails();
		nxRequestDetails.setNxRequestGroupName("ACCESS_GROUP");
		nxRequestDetails.setStatus(100L);
		List<NxOutputFileModel> nxOutputFileModels = new ArrayList<NxOutputFileModel>();
		nxOutputFileModels.add(new NxOutputFileModel());
		nxRequestDetails.setNxOutputFiles(nxOutputFileModels);
		Mockito.when(nxMyPriceRepositoryServce.getItemDescFromLookup(anyString(), anyString())).thenReturn(copyStatusLookup);
		Mockito.when(hybridRepo.getByRequestId(request.getNxReqId())).thenReturn(nxRequestDetails);
		NxSolutionDetail nxsolDetail=new NxSolutionDetail();
		List<NxSolutionDetail> solution = new ArrayList<NxSolutionDetail>();
		solution.add(nxsolDetail);
		Mockito.when(nxSolutionDetailsRepository.saveAndFlush(any())).thenReturn(nxsolDetail);
		Mockito.when(nxSolutionDetailsRepository.findByNxSolutionId(request.getNxSolutionId())).thenReturn(solution.get(0));
		Mockito.when(hybridRepo.getNxSolutionDetailList(anyLong())).thenReturn(solution);
		Mockito.when(nxTeamRepository.saveAndFlush(any())).thenReturn(new NxTeam());
		NxUser nxUser = new NxUser();
		nxUser.setFirstName("firstName");
		nxUser.setLastName("lastName");
		nxUser.setEmail("email");
		Mockito.when(nxUserRepository.findByUserAttId(anyString())).thenReturn(nxUser);
		Mockito.when(userServiceImpl.getUserProfileName(anyString())).thenReturn("Adminacess");
		NxLookupData nxLookupData = new NxLookupData();
		nxLookupData.setItemId("1");
		doNothing().when(nexxusService).updateNxSolution(1l);	
		Mockito.when(nxLookupDataRepository.findByDescriptionAndDatasetName(nxRequestDetails.getProduct(), MyPriceConstants.NX_REQ_GROUP_NAMES)).thenReturn(nxLookupData);
		doNothing().when(nexxusService).updateNxSolution(1l);	
		falloutDetailsImpl = spy(falloutDetailsImpl);
		doNothing().when(falloutDetailsImpl).updateNxOutputFileCdirData(any(), anyLong());
//		falloutDetailsImpl.nexxusRequestActions(request);

		//case1 for create new solution
		List<NxTeam> listOfNxTeam = new ArrayList();
		listOfNxTeam.add(new NxTeam());
		Mockito.when(hybridRepo.getNxTeamList(anyString(),any())).thenReturn(listOfNxTeam);
//		falloutDetailsImpl.nexxusRequestActions(request);

		//case2 for create new solution
		NxSolutionDetail nxsolDetail1=new NxSolutionDetail();
		nxsolDetail1.setOptyId("1optyId");
		List<NxSolutionDetail> solution1 = new ArrayList<NxSolutionDetail>();
		solution1.add(nxsolDetail1);
		Mockito.when(nxSolutionDetailsRepository.findByNxSolutionId(request.getNxSolutionId())).thenReturn(solution1.get(0));
		GetOptyResponse optyResponse = new GetOptyResponse();
		optyResponse.setNxSolutionId(1L);
		Mockito.when(optyInfoServiceImpl.performGetOptyInfo(any())).thenReturn(optyResponse);
//		falloutDetailsImpl.nexxusRequestActions(request);
	}
	
	@Test
	public void testCopyNewInvalidData() throws SalesBusinessException {
		FalloutDetailsRequest request = new FalloutDetailsRequest();
		request.setAction("copyNew");
		falloutDetailsImpl.nexxusRequestActions(request);

		FalloutDetailsRequest request1 = new FalloutDetailsRequest();
		request1.setAction("copyNew");
		request1.setNxSolutionId(12345l);
		request1.setNxReqId(1l);
		request1.setAttuid("test");
		Mockito.when(hybridRepo.getByRequestId(anyLong())).thenReturn(null);
		falloutDetailsImpl.nexxusRequestActions(request1);
		
		FalloutDetailsRequest request2 = new FalloutDetailsRequest();
		request2.setAction("copyNew");
		request2.setNxSolutionId(12345l);
		request2.setNxReqId(1l);
		request2.setAttuid("test");
		Mockito.when(nxSolutionDetailsRepository.findByNxSolutionId(anyLong())).thenReturn(new NxSolutionDetail());
		falloutDetailsImpl.nexxusRequestActions(request2);
	}
}