package com.att.sales.nexxus.handlers;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyMap;
import static org.mockito.Mockito.doNothing;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.ws.client.core.WebServiceTemplate;

import com.att.abs.ecrm.opty.v2.OptyInfoRequest;
import com.att.abs.ecrm.opty.v2.OptyInfoResponse;
import com.att.abs.ecrm.opty.v2.OptyInfoResponseType;
import com.att.abs.ecrm.opty.v2.OptyInfoResponseType.ListOfAbsAccount;
import com.att.abs.ecrm.opty.v2.OptyInfoResponseType.ListOfAbsAccount.AbsAccount;
import com.att.abs.ecrm.opty.v2.OptyInfoResponseType.ListOfOpportunityPosition;
import com.att.abs.ecrm.opty.v2.OptyInfoResponseType.ListOfOpportunityPosition.OpportunityPosition;
import com.att.sales.framework.model.ServiceMetaData;
import com.att.sales.nexus.config.WSClientSoapHandler;
import com.att.sales.nexxus.constant.FmoConstants;
import com.att.sales.nexxus.constant.MyPriceConstants;
import com.att.sales.nexxus.dao.model.solution.NxSolutionDetail;
import com.att.sales.nexxus.dao.model.solution.NxTeam;
import com.att.sales.nexxus.dao.repository.HybridRepositoryService;
import com.att.sales.nexxus.dao.repository.NxSolutionDetailsRepository;
import com.att.sales.nexxus.dao.repository.NxTeamRepository;
import com.att.sales.nexxus.userdetails.model.UserDetails;
import com.att.sales.nexxus.userdetails.model.UserDetailsRequest;
import com.att.sales.nexxus.userdetails.model.UserDetailsResponse;
import com.att.sales.nexxus.userdetails.service.UserDetailsServiceImpl;
import com.att.sales.nexxus.ws.utility.GetOptyInfoWSClientUtility;
import com.att.sales.nexxus.ws.utility.WSProcessingService;

@ExtendWith(MockitoExtension.class)
public class GetOptyInfoWSHandlerTest {
	@InjectMocks
	@Spy
	GetOptyInfoWSHandler test;

	@Mock
	private NxSolutionDetailsRepository nxSolutionDetailsRepository;

	@Mock
	private NxTeamRepository nxTeamRepository;

	@Mock
	private WebServiceTemplate webServiceTemplate;

	@Mock
	private WSClientSoapHandler wsClientSoapHandler;

	@Value("${rome.getOptyInfo.loggingKey}")
	private String loggingKey;

	@Value("${rome.getOptyInfo.userName}")
	private String userName;

	@Value("${rome.getOptyInfo.userPassword}")
	private String userPassword;

	@Value("${rome.getOptyInfo.token}")
	private String token;

	@Value("${rome.getOptyInfo.tokenType}")
	private String tokenType;

	@Value("${rome.getOptyInfo.applicationID}")
	private String applicationId;

	@Spy
	OptyInfoResponse optyInfoResponse;

	@Mock
	Map<String, Object> requestMap;

	@Mock
	OptyInfoResponseType value;

	@Mock
	ListOfOpportunityPosition value1;

	@Mock
	List<OpportunityPosition> value3;

	@Mock
	ListOfAbsAccount value4;

	@Mock
	List<AbsAccount> value5;

	@Mock
	List<NxSolutionDetail> value6;

	@Mock
	private UserDetailsServiceImpl adoptUserService;

	@Mock
	AbsAccount value7;

	@Mock
	OpportunityPosition e;
	@Mock
	private HybridRepositoryService hybridRepositoryService;
	
	@Mock
	private WSProcessingService wsProcessingService;
	
	@Mock
	GetOptyInfoWSClientUtility getOptyInfoWSClientUtility;

	@Mock
	UserDetailsResponse resp;
	@BeforeEach
	public void initializeServiceMetaData() {
		Map<String, Object> requestParams = new HashMap<>();
		requestParams.put(ServiceMetaData.OFFER, "AVPN");
		requestParams.put(ServiceMetaData.REST_REQUEST_START_TIME, System.currentTimeMillis());
		requestParams.put(ServiceMetaData.VERSION, "1.0");
		requestParams.put(ServiceMetaData.METHOD, "TestMethod");
		requestParams.put(ServiceMetaData.URI, "Testuri");
		ServiceMetaData.add(requestParams);
	}

	@Test
	public void test() {

		try {
			Map<String, Object> map = new HashMap<String, Object>();

			map.put("optyId", "1-5PZRHSF");
			map.put("attuid", "TA2987");
			
			Mockito.when(wsProcessingService.initiateWebService(any(OptyInfoRequest.class),
					any(GetOptyInfoWSClientUtility.class), anyMap(), any(Class.class)))
					.thenReturn(new OptyInfoResponse());
			doNothing().when(getOptyInfoWSClientUtility).setWsName(MyPriceConstants.GET_OPTY_WS);

			test.initiateGetOptyInfoWebService(map);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
	@Test
	public void test1() {
		try {
			Map<String, Object> requestMap = new HashMap<>();
			requestMap.put("action", "createSolution");
			requestMap.put("nxSolutionId", new Long(111));
			requestMap.put("optyId", "1-5PZRHSF");
			requestMap.put("attuid", "TA2987");
			requestMap.put(FmoConstants.CALL_OPTYINFO, FmoConstants.YES);
			NxSolutionDetail nxSolutionDetail = new NxSolutionDetail();

			Mockito.when(optyInfoResponse.getResponseParams()).thenReturn(value);
			Mockito.when(value.getListOfOpportunityPosition()).thenReturn(value1);
		//	Mockito.when(value1.getOpportunityPosition()).thenReturn(value3);
			Mockito.when(optyInfoResponse.getResponseParams()).thenReturn(value);
			Mockito.when(value.getListOfAbsAccount()).thenReturn(value4);
			Mockito.when(value4.getAbsAccount()).thenReturn(value5);
			Mockito.when(value5.get(0)).thenReturn(value7);
			Mockito.when(value7.getSubAccountID()).thenReturn(null);
			Mockito.when(nxSolutionDetailsRepository.findByNxSolutionId((Long) requestMap.get("nxSolutionId")))
					.thenReturn(value6.get(0));
			value3.add(e);
			nxSolutionDetail.setNxSolutionId(new Long(111));
			List<NxSolutionDetail> sol = new ArrayList<NxSolutionDetail>() {{add(nxSolutionDetail);}};
			
			Mockito.when(hybridRepositoryService.getNxSolutionDetailList((Long) requestMap.get("nxSolutionId")))
					.thenReturn(sol);
			test.processResponse(optyInfoResponse, requestMap);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Test
	public void test1CheckNull() {
		try {
			Map<String, Object> requestMap = new HashMap<>();
			requestMap.put("action", "createSolution");
			requestMap.put("nxSolutionId", null);
			requestMap.put(FmoConstants.CALL_OPTYINFO, FmoConstants.YES);
			NxSolutionDetail nxSolutionDetail = new NxSolutionDetail();
			nxSolutionDetail.setActiveYn("Y");
			nxSolutionDetail.setCreatedUser("createdUser");
			nxSolutionDetail.setCustomerName("STARK");
			nxSolutionDetail.setFlowType("flowType");
			List<OpportunityPosition> listOfEcrmOpportunityPosition = new ArrayList<>();
			OpportunityPosition opportunityPosition = new OpportunityPosition();
			opportunityPosition.setSalesRep("rep");
			opportunityPosition.setEmail("email");
			opportunityPosition.setFirstName("first");
			opportunityPosition.setLastName("last");
			opportunityPosition.setSalesRepFullName("FullRep");
			opportunityPosition.setManagerHRID("HRID");
			opportunityPosition.setManagerName("name");
			opportunityPosition.setIsPrimaryMVG("Primary");
			listOfEcrmOpportunityPosition.add(opportunityPosition);
			NxTeam nxTeam = new NxTeam();

			nxTeam.setNxSolutionDetail(nxSolutionDetail);
			nxTeam.setAttuid(opportunityPosition.getSalesRep());
			nxTeam.setfName(opportunityPosition.getFirstName());
			nxTeam.setlName(opportunityPosition.getLastName());
			nxTeam.setSalesRepFullName(opportunityPosition.getSalesRepFullName());
			nxTeam.setEmail(opportunityPosition.getEmail());
			nxTeam.setManagerName(opportunityPosition.getManagerName());
			nxTeam.setManagerHrid(opportunityPosition.getManagerHRID());
			nxTeam.setIsPryMVG(opportunityPosition.getIsPrimaryMVG());
			List<NxTeam> listOfNxTeam = new ArrayList<>();
			listOfNxTeam.add(nxTeam);
			// doReturn(listOfNxTeam).when(hybridRepositoryService.getNxTeamList(Mockito.anyString(),
			// nxSolutionDetail));
			Mockito.when(optyInfoResponse.getResponseParams()).thenReturn(value);
			Mockito.when(value.getListOfOpportunityPosition()).thenReturn(value1);
			Mockito.when(value1.getOpportunityPosition()).thenReturn(value3);
			Mockito.when(optyInfoResponse.getResponseParams()).thenReturn(value);
			Mockito.when(value.getListOfAbsAccount()).thenReturn(value4);
			Mockito.when(value4.getAbsAccount()).thenReturn(value5);
			Mockito.when(value5.get(0)).thenReturn(value7);
			Mockito.when(value7.getSubAccountID()).thenReturn(null);
			Mockito.when(nxSolutionDetailsRepository.findByNxSolutionId((Long) requestMap.get("nxSolutionId")))
					.thenReturn(value6.get(0));
			//value3.add(e);
			Mockito.when(hybridRepositoryService.getNxSolutionDetailList((Long) requestMap.get("nxSolutionId")))
					.thenReturn(value6);
			test.processResponse(optyInfoResponse, requestMap);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Test
	public void testretrieveOpty() {
		try {
			Map<String, Object> requestMap = new HashMap<>();
			requestMap.put("action", "retrieveOpty");
			requestMap.put("nxSolutionId", new Long(111));
			requestMap.put(FmoConstants.CALL_OPTYINFO, FmoConstants.YES);

			Mockito.when(optyInfoResponse.getResponseParams()).thenReturn(value);
			Mockito.when(value.getListOfOpportunityPosition()).thenReturn(value1);
			Mockito.when(value1.getOpportunityPosition()).thenReturn(value3);
			Mockito.when(optyInfoResponse.getResponseParams()).thenReturn(value);
			Mockito.when(value.getListOfAbsAccount()).thenReturn(value4);
			Mockito.when(value4.getAbsAccount()).thenReturn(value5);
			Mockito.when(value5.get(0)).thenReturn(value7);
			Mockito.when(value7.getSubAccountID()).thenReturn(null);
			Mockito.when(nxSolutionDetailsRepository.findByNxSolutionId((Long) requestMap.get("nxSolutionId")))
					.thenReturn(value6.get(0));
			value3.add(e);
			Mockito.when(hybridRepositoryService.getNxSolutionDetailList((Long) requestMap.get("nxSolutionId")))
					.thenReturn(value6);
			test.processResponse(optyInfoResponse, requestMap);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Test
	public void testmyPriceFlow() {
		try {
			Map<String, Object> requestMap = new HashMap<>();
			requestMap.put("action", "myPriceFlow");
			requestMap.put("nxSolutionId", new Long(111));
			requestMap.put(FmoConstants.CALL_OPTYINFO, FmoConstants.YES);

			Mockito.when(optyInfoResponse.getResponseParams()).thenReturn(value);
			Mockito.when(value.getListOfOpportunityPosition()).thenReturn(value1);
			Mockito.when(optyInfoResponse.getResponseParams()).thenReturn(value);
			Mockito.when(value.getListOfAbsAccount()).thenReturn(value4);
			Mockito.when(value4.getAbsAccount()).thenReturn(value5);
			Mockito.when(value5.get(0)).thenReturn(value7);
			Mockito.when(value7.getSubAccountID()).thenReturn(null);
			Mockito.when(nxSolutionDetailsRepository.findByNxSolutionId((Long) requestMap.get("nxSolutionId")))
					.thenReturn(value6.get(0));
			Mockito.when(hybridRepositoryService.getNxSolutionDetailList((Long) requestMap.get("nxSolutionId")))
					.thenReturn(value6);
			test.processResponse(optyInfoResponse, requestMap);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Test
	public void testOptyIdNull() {
		try {

			Map<String, Object> requestMap = new HashMap<>();
			requestMap.put("nxSolutionId", new Long(111));
			requestMap.put("attuid", "rw161p");
			requestMap.put("action", "createSolution");
			List<NxSolutionDetail> listOpportunityTeam = new LinkedList<>();
			NxSolutionDetail nxSolutionDetail = new NxSolutionDetail();
			String createdUser = "rw161p";
			List<NxTeam> team = new LinkedList<>();
			NxTeam nxTeam = new NxTeam();
			team.add(nxTeam);
			Mockito.when(hybridRepositoryService.getNxTeamList(nxSolutionDetail.getCreatedUser(), nxSolutionDetail))
					.thenReturn(team);
			nxSolutionDetail.setCreatedUser(createdUser);
			listOpportunityTeam.add(nxSolutionDetail);
			UserDetailsRequest req = new UserDetailsRequest();
			req.setAttuid(nxSolutionDetail.getCreatedUser());
			
			List<UserDetails> userDetails = new LinkedList<>();
			UserDetails udetails = new UserDetails();
			userDetails.add(udetails );
			resp.setUserDetails(userDetails);
			Mockito.when(resp.getUserDetails()).thenReturn(userDetails);
			Mockito.when(adoptUserService.retreiveUserDetails(req)).thenReturn(resp );
			Mockito.when(hybridRepositoryService.getNxSolutionDetailList(new Long(111)))
					.thenReturn(listOpportunityTeam);
			test.processResponse(null, requestMap);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Test
	public void testSolutionIdNull() {
		try {
			Map<String, Object> requestMap = new HashMap<>();
			requestMap.put("action", "createSolution");
			requestMap.put("optyId", "createSolution");
			requestMap.put("solutionDescription", "createSolution");
			List<NxSolutionDetail> listOpportunityTeam = new LinkedList<>();
			NxSolutionDetail nxSolutionDetail = new NxSolutionDetail();
			listOpportunityTeam.add(nxSolutionDetail);
			Mockito.when(hybridRepositoryService.getNxSolutionDetailList(new Long(111)))
					.thenReturn(listOpportunityTeam);
			test.processResponse(null, requestMap);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
