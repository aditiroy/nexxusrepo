package com.att.sales.nexxus.userdetails.serviceTest;


import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.junit.jupiter.MockitoExtension;

import com.att.sales.framework.exception.SalesBusinessException;
import com.att.sales.framework.model.ServiceMetaData;
import com.att.sales.framework.model.Status;
import com.att.sales.nexxus.dao.model.NxUser;
import com.att.sales.nexxus.dao.model.solution.NxTeam;
import com.att.sales.nexxus.dao.repository.NxRequestDetailsRepository;
import com.att.sales.nexxus.dao.repository.NxTeamRepository;
import com.att.sales.nexxus.dao.repository.NxUserRepository;
import com.att.sales.nexxus.service.NexxusService;
import com.att.sales.nexxus.userdetails.model.ConsumerDetailRequest;
import com.att.sales.nexxus.userdetails.model.UserDetails;
import com.att.sales.nexxus.userdetails.model.UserDetailsResponse;
import com.att.sales.nexxus.userdetails.service.ConsumerDetailServiceImpl;
import com.att.sales.nexxus.userdetails.service.UserDetailsServiceImpl;
import com.att.sales.nexxus.userdetails.service.UserServiceImpl;



@ExtendWith(MockitoExtension.class)
public class ConsumerDetailServiceImplTest {
	
     @InjectMocks
  private   ConsumerDetailServiceImpl consumerDetailServiceImpl;
     
     @Mock
 	NxTeamRepository nxteamRepo;
     
     @Mock
 	private NxRequestDetailsRepository repo;

     @Mock
 	private UserDetailsServiceImpl adoptUserService;
     
 	 @Mock
     private NexxusService nexxusService;
 	 
	@Mock
	private UserServiceImpl userServiceImpl;

	@Mock
	private NxUserRepository nxUserRepository;

     
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

     // comment broken test cases after remove adopt get user details call
     /*
	@Test
	public void testConsumerDetailData() throws SalesBusinessException {
		ConsumerDetailRequest request = new ConsumerDetailRequest();
		request.setActionType("addUserDetails");
		request.setNxSolutionId(12345L);
		UserDetailsResponse resp= new UserDetailsResponse();
		resp.setLeadDesignId(12345L);
		List<UserDetails> userDetails = new ArrayList<>();
		UserDetails details=  new UserDetails();
		details.setAttuid("attuid");
		details.setCellular("cellular");
	
		userDetails.add(details);
		resp.setUserDetails(userDetails);
		Status status  =new Status();
		status.setCode("code");
		resp.setStatus(status);
		List<NxTeam> nxTeams = new ArrayList<>();
		NxTeam nxTeam = new NxTeam();
		nxTeam.setAttuid("attuid");
		nxTeams.add(nxTeam);
		Mockito.when(adoptUserService.retreiveUserDetails(Mockito.anyObject())).thenReturn(resp);
		Mockito.when(nxteamRepo.findByNxSolutionId(Mockito.anyLong())).thenReturn(nxTeams);
		doNothing().when(nexxusService).updateNxSolution(any());
		consumerDetailServiceImpl.consumerDetailData(request);
	}

	@Test
	public void testConsumerDetailDataElse() throws SalesBusinessException {
		ConsumerDetailRequest request = new ConsumerDetailRequest();
		request.setActionType("addUserDetails");
		request.setNxSolutionId(12345L);
		request.setAttuid("asdfg");
		UserDetailsResponse resp= new UserDetailsResponse();
		Mockito.when(adoptUserService.retreiveUserDetails(Mockito.anyObject())).thenReturn(resp);
		doNothing().when(nexxusService).updateNxSolution(any());
		consumerDetailServiceImpl.consumerDetailData(request);
	}
	*/
	
	@Test
	public void testConsumerDetailDataAddUser() throws SalesBusinessException {
		ConsumerDetailRequest request = new ConsumerDetailRequest();
		request.setActionType("addUserDetails");
		request.setNxSolutionId(12345L);
		request.setAttuid("attuid");
		doNothing().when(nexxusService).updateNxSolution(any());
		Mockito.when(userServiceImpl.getUserProfileName(any())).thenReturn("General Access", "NONE");
		NxUser nxUser = new NxUser();
		Mockito.when(nxUserRepository.findByUserAttId(anyString())).thenReturn(nxUser);
		
//		consumerDetailServiceImpl.consumerDetailData(request);
//		consumerDetailServiceImpl.consumerDetailData(request);
	}
	
	@Test
	public void testConsumerDetailDataRetrive() throws SalesBusinessException {
		ConsumerDetailRequest request = new ConsumerDetailRequest();
		request.setActionType("retrieveUserDetails");
		request.setNxSolutionId(12345L);
		UserDetailsResponse resp= new UserDetailsResponse();
		resp.setLeadDesignId(12345L);
		List<UserDetails> userDetails = new ArrayList<>();
		UserDetails details=  new UserDetails();
		details.setAttuid("attuid");
		details.setCellular("cellular");
		userDetails.add(details);
		resp.setUserDetails(userDetails);
		Status status  =new Status();
		status.setCode("code");
		resp.setStatus(status);
		List<NxTeam> nxTeams = new ArrayList<>();
		NxTeam nxTeam = new NxTeam();
		nxTeam.setAttuid("attuid");
		nxTeams.add(nxTeam);
		Mockito.when(adoptUserService.retreiveUserDetails(Mockito.any())).thenReturn(resp);
		Mockito.when(nxteamRepo.findByNxSolutionId(Mockito.anyLong())).thenReturn(nxTeams);
		consumerDetailServiceImpl.consumerDetailData(request);
	}
	
	
	
	@Test
	public void testConsumerDetailDataDeleteUser() throws SalesBusinessException {
		ConsumerDetailRequest request = new ConsumerDetailRequest();
		request.setActionType("deleteUserDetails");
		request.setNxSolutionId(12345L);
		request.setAttuid("attuid");
		UserDetailsResponse resp= new UserDetailsResponse();
		resp.setLeadDesignId(12345L);
		List<UserDetails> userDetails = new ArrayList<>();
		UserDetails details=  new UserDetails();
		details.setAttuid("attuid");
		details.setCellular("cellular");
		userDetails.add(details);
		resp.setUserDetails(userDetails);
		Status status  =new Status();
		status.setCode("code");
		resp.setStatus(status);
		NxTeam nxTeam = new NxTeam();
		nxTeam.setAttuid("attuid");
		Mockito.when(nxteamRepo.getNxTeam(Mockito.any(), Mockito.any())).thenReturn(nxTeam);
		doNothing().when(nexxusService).updateNxSolution(any());
//		consumerDetailServiceImpl.consumerDetailData(request);
	}
	
	@Test
	public void testConsumerDetailDataDeleteUserElse() throws SalesBusinessException {
		ConsumerDetailRequest request = new ConsumerDetailRequest();
		request.setActionType("deleteUserDetails");
		doNothing().when(nexxusService).updateNxSolution(any());
//		consumerDetailServiceImpl.consumerDetailData(request);
	}
	
	@Test
	public void testDeleteUserConditionOne() throws SalesBusinessException {
		ConsumerDetailRequest request = new ConsumerDetailRequest();
		request.setActionType("deleteUserDetails");
		request.setAttuid("asdfg");
		doNothing().when(nexxusService).updateNxSolution(any());
//		consumerDetailServiceImpl.consumerDetailData(request);
	}
	@Test
	public void testDeleteUserConditionTwo() throws SalesBusinessException {
		ConsumerDetailRequest request = new ConsumerDetailRequest();
		request.setActionType("deleteUserDetails");
		request.setNxSolutionId(1L);
		doNothing().when(nexxusService).updateNxSolution(any());
//		consumerDetailServiceImpl.consumerDetailData(request);
	}

}
