package com.att.sales.nexxus.service;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyList;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.mock;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import org.junit.Ignore;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.junit.jupiter.MockitoExtension;

import com.att.sales.framework.exception.SalesBusinessException;
import com.att.sales.framework.model.ServiceMetaData;
import com.att.sales.framework.model.ServiceResponse;
import com.att.sales.nexxus.common.CommonConstants;
import com.att.sales.nexxus.constant.StringConstants;
import com.att.sales.nexxus.dao.model.NexxusSolutionDetailUIModel;
import com.att.sales.nexxus.dao.model.NexxusSolutionDetailUIModel.NexxusEdfRequestDetail;
import com.att.sales.nexxus.dao.model.NexxusSolutionDetailUIModel.NexxusSolnsGroups;
import com.att.sales.nexxus.dao.model.NxAdminUserModel;
import com.att.sales.nexxus.dao.model.NxLookupData;
import com.att.sales.nexxus.dao.model.NxRequestDetails;
import com.att.sales.nexxus.dao.model.NxRequestGroup;
import com.att.sales.nexxus.dao.model.solution.NxSolutionDetail;
import com.att.sales.nexxus.dao.repository.NxAdminUserRepository;
import com.att.sales.nexxus.dao.repository.NxLookupDataRepository;
import com.att.sales.nexxus.dao.repository.NxMpDealRepository;
import com.att.sales.nexxus.dao.repository.NxRequestDetailsRepository;
import com.att.sales.nexxus.dao.repository.NxRequestGroupRepository;
import com.att.sales.nexxus.dao.repository.NxSolutionDetailsRepository;
import com.att.sales.nexxus.dao.repository.NxTeamRepository;
import com.att.sales.nexxus.edf.model.ManageBillingPriceInvDataRequest;
import com.att.sales.nexxus.edf.model.ManageBillingPriceInvDataResponse;
import com.att.sales.nexxus.edf.model.ManageBillingPriceInventoryDataResponse;
import com.att.sales.nexxus.edf.model.ManageBillingPriceInventoryDataResponse.InnerManageBillingPriceInventoryDataResponse;
import com.att.sales.nexxus.edf.model.PriceInventoryDataRequest;
import com.att.sales.nexxus.helper.FileReaderHelper;
import com.att.sales.nexxus.model.ProductDataLoadRequest;
import com.att.sales.nexxus.myprice.transaction.model.GetTransactionResponse;
import com.att.sales.nexxus.myprice.transaction.service.GetTransactionServiceImpl;
import com.att.sales.nexxus.reteriveicb.model.NexxusTestRequest;
import com.att.sales.nexxus.reteriveicb.model.NexxusTestResponse;
import com.att.sales.nexxus.rome.model.GetOptyResponse;
import com.att.sales.nexxus.rome.service.GetOptyInfoServiceImpl;
import com.att.sales.nexxus.util.DME2RestClient;
import com.fasterxml.jackson.databind.ObjectMapper;

@ExtendWith(MockitoExtension.class)
public class NexxusServiceImplTest {

	@InjectMocks
	NexxusServiceImpl nexxusServiceImpl;

	@Mock
	private NxSolutionDetailsRepository nxSolutionDetailsRepository;

	@Mock
	private DME2RestClient dme;

	@Mock
	private NxSolutionDetailsRepository solutionRepo;

	@Mock
	private NxRequestDetailsRepository repo;

	@Mock
	private FileReaderHelper fileReaderHelper;

	@Mock
	private GetOptyInfoServiceImpl optyInfoServiceImpl;

	@Mock
	private EntityManager em;
	
	@Mock
	private NxLookupDataRepository nxLookupDataRepository;
	
	@Mock
	private NxRequestGroupRepository nxRequestGroupRepository;

	@Mock
	private NxMpDealRepository nxMpDealRepository;
	
	@Mock
	private GetTransactionServiceImpl getTransactionServiceImpl;
	
	@Mock
	private NxTeamRepository nxTeamRepository;
		
	@Mock
	private NxRequestDetailsRepository nxRequestDetailsRepository;
	
	@Mock
	private NxMyPriceRepositoryServce nxMyPriceRepositoryServce;
	
	@Mock
	private NxAdminUserRepository nxAdminUserRepository;
	
	@Mock
	private MailServiceImpl mailServiceImpl;
	
	@Mock
	private ObjectMapper nexxusObjectMapper ;
	
	@Test
	public void testTransformTestData() {
		NexxusTestRequest request = new NexxusTestRequest();
		request.setSolutionId(1L);
		request.setSolutionName("solutionName");
		ServiceResponse result=nexxusServiceImpl.transformTestData(request);
		assertEquals("solutionName", ((NexxusTestResponse) result).getSolutionName());
	}
	
	@Test
	public void testPutUploadASENexxusFile() throws IOException, SalesBusinessException {
		Map<String, Object> requestParams = new HashMap<>();
 		requestParams.put(CommonConstants.FILENAME, "test_file");
 		requestParams.put("TransactionId","5765");
 		ServiceMetaData.add(requestParams);
 		
		ProductDataLoadRequest productDataLoadRequest = new ProductDataLoadRequest();
		nexxusServiceImpl.putUploadASENexxusFile(productDataLoadRequest);
	}
	
	@Test
	public void testPutUploadASENexxusFileException() throws IOException, SalesBusinessException {
		Map<String, Object> requestParams = new HashMap<>();
 		requestParams.put(CommonConstants.FILENAME, "test");
 		requestParams.put("TransactionId","5765");
 		ServiceMetaData.add(requestParams);
 		ProductDataLoadRequest productDataLoadRequest = new ProductDataLoadRequest();
		nexxusServiceImpl.putUploadASENexxusFile(productDataLoadRequest);
	}
	
	@Test
	public void testfetchNexxusSolutionsByUserIdMPFlow() throws SalesBusinessException {
		TypedQuery<NexxusSolutionDetailUIModel> mockTypedQuery = mock(TypedQuery.class); 
		Mockito.when(em.createNamedQuery(any(),eq(NexxusSolutionDetailUIModel.class))).thenReturn(mockTypedQuery);
		List<NexxusSolutionDetailUIModel> resultList= new ArrayList<>();
		Mockito.when(mockTypedQuery.getResultList()).thenReturn(resultList);
		LinkedHashMap<String, Object> queryParams = new LinkedHashMap<>();
		queryParams.put("fromSystem", "myPrice");
		queryParams.put("dealId", "68768");
		queryParams.put("transactionId", "768696");
		GetTransactionResponse getTransactionResponse= new GetTransactionResponse();
		getTransactionResponse.setVersion("1");
		getTransactionResponse.setRevision("1");
		queryParams.put("userId", "userId");
		nexxusServiceImpl.fetchNexxusSolutionsByUserId(queryParams);

		Mockito.when(getTransactionServiceImpl.getTransaction(anyString())).thenReturn(getTransactionResponse);
		nexxusServiceImpl.fetchNexxusSolutionsByUserId(queryParams);
	}
	
	@Test
	public void testfetchNexxusSolutionsByUserIdFilterdata() throws SalesBusinessException {
		TypedQuery<NexxusSolutionDetailUIModel> mockTypedQuery = mock(TypedQuery.class); 
		Mockito.when(em.createNamedQuery(any(),eq(NexxusSolutionDetailUIModel.class))).thenReturn(mockTypedQuery);
		List<NexxusSolutionDetailUIModel> resultList= new ArrayList<>();
		Mockito.when(mockTypedQuery.getResultList()).thenReturn(resultList);
		LinkedHashMap<String, Object> queryParams = new LinkedHashMap<>();
		queryParams.put("userId", "userId");
		GetTransactionResponse getTransactionResponse= new GetTransactionResponse();
		getTransactionResponse.setVersion("1");
		getTransactionResponse.setRevision("1");
		nexxusServiceImpl.fetchNexxusSolutionsByUserId(queryParams);

		queryParams.put("filter", "2345");
		List<NxAdminUserModel> adminUserList= new ArrayList<>();
		Mockito.when(nxAdminUserRepository.findByAttUid(anyString())).thenReturn(adminUserList);
		nexxusServiceImpl.fetchNexxusSolutionsByUserId(queryParams);
		
		NxAdminUserModel nxAdminUserModel = new NxAdminUserModel();
		adminUserList.add(nxAdminUserModel);
		Mockito.when(nxAdminUserRepository.findByAttUid(anyString())).thenReturn(adminUserList);
		nexxusServiceImpl.fetchNexxusSolutionsByUserId(queryParams);
	}
	
	
	@Test
	public void testfetchNexxusSolutionsByUserIdResultListNotempty() throws SalesBusinessException {
		TypedQuery<NexxusSolutionDetailUIModel> mockTypedQuery = mock(TypedQuery.class); 
		Mockito.when(em.createNamedQuery(any(),eq(NexxusSolutionDetailUIModel.class))).thenReturn(mockTypedQuery);
		List<NexxusSolutionDetailUIModel> resultList= new ArrayList<>();
		NexxusSolutionDetailUIModel nexxusSolutionDetailUIModel = new NexxusSolutionDetailUIModel();
		nexxusSolutionDetailUIModel.setNxSolutionId(1L);
		nexxusSolutionDetailUIModel.setCustomerName("customerName");
		nexxusSolutionDetailUIModel.setFlowType(StringConstants.FLOW_TYPE_INR);
		NexxusSolnsGroups nexxusSolnsGroups= new NexxusSolnsGroups(23L,"ACCESS_GROUP", "ACCESS_GROUP","Active",2L, "Y");
		nexxusSolnsGroups.setNxReqGroupId(23L);
		nexxusSolutionDetailUIModel.setNexxusSolnsGroups(nexxusSolnsGroups);
		resultList.add(nexxusSolutionDetailUIModel);
		
		NexxusEdfRequestDetail accountSearchData = new NexxusEdfRequestDetail(1L, "ACCESS_GROUP", "AVPN", "cpniApprover","Submitted", "testRequest", new Date(),new Date(),"submitted","N", "","",1L,"USRP","Y");
		nexxusSolutionDetailUIModel.setNexxusEdfRequestDetail(accountSearchData);
		resultList.add(nexxusSolutionDetailUIModel);
		Mockito.when(mockTypedQuery.getResultList()).thenReturn(resultList);
		LinkedHashMap<String, Object> queryParams = new LinkedHashMap<>();
		queryParams.put("userId", "userId");
		GetTransactionResponse getTransactionResponse= new GetTransactionResponse();
		getTransactionResponse.setVersion("1");
		getTransactionResponse.setRevision("1");
		nexxusServiceImpl.fetchNexxusSolutionsByUserId(queryParams);

		//empty admilList
		queryParams.put("filter", "2345");
		List<NxAdminUserModel> adminUserList= new ArrayList<>();
		Mockito.when(nxAdminUserRepository.findByAttUid(anyString())).thenReturn(adminUserList);
		nexxusServiceImpl.fetchNexxusSolutionsByUserId(queryParams);
		
		NxAdminUserModel nxAdminUserModel = new NxAdminUserModel();
		adminUserList.add(nxAdminUserModel);
		Mockito.when(nxAdminUserRepository.findByAttUid(anyString())).thenReturn(adminUserList);
		nexxusServiceImpl.fetchNexxusSolutionsByUserId(queryParams);
	}
	
	@SuppressWarnings("unchecked")
	@Disabled
	@Test
	public void testGetBillingPriceInventryData() throws Exception {
		ManageBillingPriceInvDataRequest inventoryrequest = new ManageBillingPriceInvDataRequest();
		List<PriceInventoryDataRequest> reqInventoryList = new ArrayList<>();
		PriceInventoryDataRequest priceInventoryDataRequest = new PriceInventoryDataRequest();
		priceInventoryDataRequest.setDunsNumber("46546");
		priceInventoryDataRequest.setBeginBillMonth("11feb2018");
		priceInventoryDataRequest.setBillMonth("feb");
		priceInventoryDataRequest.setL3SubAcctId("M456");
		priceInventoryDataRequest.setL4AcctId("SC89");
		priceInventoryDataRequest.setL5MasterAcctId("SHJ78");
		priceInventoryDataRequest.setProduct("bvoip");
		priceInventoryDataRequest.setMainAcctNumber("5768");
		Object manageBillingPriceAccountDataRequest = new Object();
		inventoryrequest.setManageBillingPriceAccountDataRequest(manageBillingPriceAccountDataRequest);
		reqInventoryList.add(priceInventoryDataRequest);
		inventoryrequest.setInventoryList(reqInventoryList);
		String manageBillingPriceAccountData="{\r\n" + 
				"    \"ManageBillingPriceAccountDataRequest\": {\r\n" + 
				"    	\"billMonth\": \"082019\",\r\n" + 
				"        \"pagingHeader\": {\r\n" + 
				"            \"startRow\": \"1\",\r\n" + 
				"            \"maxRow\": \"200\"\r\n" + 
				"        },\r\n" + 
				"        \"dunsNumber\": \"006965859\",\r\n" + 
				"        \"product\": \"BVoIP\"\r\n" + 
				"\r\n" + 
				"    }\r\n" + 
				"}";
		Mockito.when(nexxusObjectMapper.writeValueAsString(any())).thenReturn(manageBillingPriceAccountData);
		NxLookupData nxLookupData = new NxLookupData();
		nxLookupData.setDatasetName("dataSetrName");
		nxLookupData.setCriteria("Y");
		nxLookupData.setItemId("1");
		Mockito.when(nxLookupDataRepository.findByDescriptionAndDatasetName(anyString(),anyList())).thenReturn(nxLookupData);
		ManageBillingPriceInventoryDataResponse resp= new ManageBillingPriceInventoryDataResponse();
		InnerManageBillingPriceInventoryDataResponse manageBillingPriceInventoryDataResponse = new InnerManageBillingPriceInventoryDataResponse();
		manageBillingPriceInventoryDataResponse.setRequestId("56");
		resp.setManageBillingPriceInventoryDataResponse(manageBillingPriceInventoryDataResponse);
		Mockito.when(dme.getBillingPriceInventryUri(any())).thenReturn(resp);
		GetOptyResponse optyResponse = new GetOptyResponse();
		optyResponse.setNxSolutionId(1L);
		Mockito.when(optyInfoServiceImpl.performGetOptyInfo(any())).thenReturn(optyResponse);
		List<NxSolutionDetail> solution = new ArrayList<>();
		NxSolutionDetail nxSolutionDetail = new NxSolutionDetail();
		solution.add(nxSolutionDetail);
		Mockito.when(solutionRepo.findByNxSolutionId(anyLong())).thenReturn(solution.get(0));
		Mockito.when(nxLookupDataRepository.findByDatasetNameAndDescription(anyString(),anyString())).thenReturn(nxLookupData);
		List<NxRequestGroup> nxRequestGroups= new ArrayList<>();
		Mockito.when(nxRequestGroupRepository.findByNxSolutionIdAndGroupIdAndActiveYn(anyLong(),any(),anyString())).thenReturn(nxRequestGroups);
		NxRequestGroup nxRequestGroup = new NxRequestGroup();
		Mockito.when(nxRequestGroupRepository.save(any(NxRequestGroup.class))).thenReturn(nxRequestGroup);
//		nexxusServiceImpl.getBillingPriceInventryData(inventoryrequest);
	}
	
	@Test
	public void testUpdateNxSolution() {
		NxSolutionDetail solutionDetail = new NxSolutionDetail();
		Mockito.when(nxSolutionDetailsRepository.findByNxSolutionId(anyLong())).thenReturn(solutionDetail);
		nexxusServiceImpl.updateNxSolution(1L);
	}
	
	
	@Test
	public void testSaveInventoryPricingRequestData() throws SalesBusinessException {
		List<NxRequestDetails> nxRequestDetails= new ArrayList<NxRequestDetails>();
		NxRequestDetails nxRequestDetailObj = new NxRequestDetails();
		nxRequestDetails.add(nxRequestDetailObj);
		NxLookupData nxLookupData = new NxLookupData();
		nxLookupData.setDatasetName("dataSetrName");
		nxLookupData.setCriteria("Y");
		nxLookupData.setItemId("1");
		nxLookupData.setDescription("description");
		Mockito.when(nxLookupDataRepository.findByDatasetNameAndDescription(anyString(),anyString())).thenReturn(nxLookupData);
		List<NxSolutionDetail> solution = new ArrayList<>();
		NxSolutionDetail nxSolutionDetail = new NxSolutionDetail();
		solution.add(nxSolutionDetail);
		Mockito.when(solutionRepo.findByNxSolutionId(anyLong())).thenReturn(solution.get(0));
		GetOptyResponse optyResponse = new GetOptyResponse();
		optyResponse.setNxSolutionId(1L);
		Mockito.when(optyInfoServiceImpl.performGetOptyInfo(any())).thenReturn(optyResponse);
		NxRequestGroup nxRequestGroup =   new NxRequestGroup();
		Mockito.when(nxRequestGroupRepository.findByNxRequestGroupIdAndActiveYn(anyLong(),anyString())).thenReturn(nxRequestGroup);
		Mockito.when(nxRequestDetailsRepository.findRequestsByGroupId(anyLong(), anyString())).thenReturn(nxRequestDetails);
		List<NxLookupData> nxLookupDataList= new ArrayList<>();
		nxLookupDataList.add(nxLookupData);
		Mockito.when(nxLookupDataRepository.findByDatasetName(anyString())).thenReturn(nxLookupDataList);
//		nexxusServiceImpl.saveInventoryPricingRequestData(inventoryrequest, nxRequestDetails, objServiceResponse, isServiceExist);
	}
	
	@Test
	public void testSaveNxRequestGroup() {
		NxRequestDetails nxRequestDetails2 = new NxRequestDetails() ;
		NxLookupData nxLookupData = new NxLookupData();
		nxLookupData.setItemId("1");
		Long nxSolutionId=1L;
		String groupStatus="Submitted";
		List<NxRequestGroup> nxRequestGroups = new ArrayList<>();
	    NxRequestGroup  nxRequestGroupObj= new NxRequestGroup();
	    nxRequestGroupObj.setGroupId(1L);
	    nxRequestGroups.add(nxRequestGroupObj);
		Mockito.when(nxRequestGroupRepository.findByNxSolutionIdAndGroupIdAndActiveYn(anyLong(), anyLong(), 
				anyString())).thenReturn(nxRequestGroups);
		nexxusServiceImpl.saveNxRequestGroup(nxRequestDetails2, nxLookupData, nxSolutionId, groupStatus);

	}
}
