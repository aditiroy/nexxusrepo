package com.att.salesmarketing.soma.pric.nexxus.unittest.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.env.Environment;

import com.att.sales.framework.exception.SalesBusinessException;
import com.att.sales.framework.model.ServiceMetaData;
import com.att.sales.nexxus.common.CommonConstants;
import com.att.sales.nexxus.constant.DataUploadConstants;
import com.att.sales.nexxus.constant.StringConstants;
import com.att.sales.nexxus.dao.model.NxMpDeal;
import com.att.sales.nexxus.dao.model.NxRequestDetails;
import com.att.sales.nexxus.dao.model.solution.NxSolutionDetail;
import com.att.sales.nexxus.dao.model.solution.NxTeam;
import com.att.sales.nexxus.dao.repository.NxRequestDetailsRepository;
import com.att.sales.nexxus.dao.repository.NxSolutionDetailsRepository;
import com.att.sales.nexxus.dao.repository.NxTeamRepository;
import com.att.sales.nexxus.model.MailRequest;
import com.att.sales.nexxus.model.MailResponse;
import com.att.sales.nexxus.model.MailServiceRequest;
import com.att.sales.nexxus.service.MailServiceImpl;
import com.att.sales.nexxus.util.DME2RestClient;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@ExtendWith(MockitoExtension.class)
public class MailServiceTest {

	@Mock
	private NxRequestDetailsRepository repo;
	@Mock
	ObjectMapper mapper;
	@Mock
	DME2RestClient dme;
	@Mock
	Environment environment;
	@InjectMocks
	MailServiceImpl service;

	@Mock
	private NxTeamRepository repository;
	
	@Mock
	private NxSolutionDetailsRepository nxSolutionDetailsRepository;

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
	public void testGetToMailId() {
		NxSolutionDetail nxSol = new NxSolutionDetail();
		MailRequest request = new MailRequest();
		request.setNxRequestId(new Long(37687));
		service.getToMailId(nxSol);
	}

	@Test
	public void testPreapareMailRequestForSuccess() {
		NxSolutionDetail nxSolutionDetail = createDummyNxSolution();
		MailRequest mailReq = new MailRequest();
		mailReq.setNxRequestId(63L);
		MailServiceRequest mailServiceRequest = new MailServiceRequest();
		mailServiceRequest.setMailsource(StringConstants.ADOPT);
		mailServiceRequest.setToMailList("aa316k");
		mailServiceRequest.setFromMailList("fromMailList");
		mailServiceRequest.setEmailBody("Hi fName lName for solnId");
		mailServiceRequest.setEmailSub("TEST subject");
		mailServiceRequest.setCoorelationId(new Long(3l));
		Mockito.when(environment.getProperty("mail.notify.success.body")).thenReturn("Hi fName lName for solnId");
		Mockito.when(environment.getProperty("mail.notify.failed.body")).thenReturn("solnId for failure mail");
		Mockito.when(environment.getProperty("mail.notify.success.subject")).thenReturn("solnId for success mail");
		Mockito.when(environment.getProperty("mail.notify.failed.subject")).thenReturn("solnId for failure mail");
		mailServiceRequest = service.prepareMailRequest(nxSolutionDetail.getNxTeams().iterator().next(), mailReq,
				nxSolutionDetail.getNxRequestDetails().iterator().next());
	
		nxSolutionDetail.getNxRequestDetails().iterator().next().setStatus(20L);
		Mockito.when(environment.getProperty(Mockito.anyString())).thenReturn("Hi fName lName for solnId with transaction transactionId deal dealId verId revId");
		mailServiceRequest = service.prepareMailRequest(nxSolutionDetail.getNxTeams().iterator().next(), mailReq,
				nxSolutionDetail.getNxRequestDetails().iterator().next());
	}
	
	@Test
	public void testPreapareMailRequestForFail() {
		NxSolutionDetail nxSolutionDetail = createDummyNxSolutionForStatus();
		MailRequest mailReq = new MailRequest();
		mailReq.setNxRequestId(63L);
		MailServiceRequest mailServiceRequest = new MailServiceRequest();
		mailServiceRequest.setMailsource(StringConstants.ADOPT);
		mailServiceRequest.setToMailList("aa316k");
		mailServiceRequest.setFromMailList("fromMailList");
		mailServiceRequest.setEmailBody("solnId for failure mail");
		mailServiceRequest.setEmailSub("TEST subject");
		mailServiceRequest.setCoorelationId(new Long(3l));
		Mockito.when(environment.getProperty("mail.notify.success.body")).thenReturn("Hi fName lName for solnId");
		Mockito.when(environment.getProperty("mail.notify.failed.body")).thenReturn("solnId for failure mail");
		Mockito.when(environment.getProperty("mail.notify.success.subject")).thenReturn("solnId for success mail");
		Mockito.when(environment.getProperty("mail.notify.failed.subject")).thenReturn("solnId for failure mail");
		mailServiceRequest = service.prepareMailRequest(nxSolutionDetail.getNxTeams().iterator().next(), mailReq,
				nxSolutionDetail.getNxRequestDetails().iterator().next());
		
		
	}

	@Test
	public void testmailNotification() throws SalesBusinessException {
		MailRequest request = new MailRequest();
		request.setNxRequestId(new Long(63));
		NxSolutionDetail nxSolutionDetail = createDummyNxSolution();
		Mockito.when(repo.findByNxReqId(Mockito.anyLong()))
				.thenReturn(nxSolutionDetail.getNxRequestDetails().iterator().next());
		Mockito.when(repository.getNxTeam(Mockito.anyLong(), Mockito.anyString()))
				.thenReturn(nxSolutionDetail.getNxTeams().iterator().next());
		Mockito.when(environment.getProperty(Mockito.anyString())).
		thenReturn("Hi fName lName for solnId with transaction transactionId deal dealId verId revId");

		service.mailNotification(request);
	}
    
	@Test
	public void testmailNotificationForFail() throws SalesBusinessException {
		MailRequest request = new MailRequest();
		request.setNxRequestId(new Long(63));
		NxSolutionDetail nxSolutionDetail = createDummyNxSolution();
		Mockito.when(repo.findByNxReqId(Mockito.anyLong()))
				.thenReturn(nxSolutionDetail.getNxRequestDetails().iterator().next());
		Mockito.when(repository.getNxTeam(Mockito.anyLong(), Mockito.anyString()))
				.thenReturn(null);

		service.mailNotification(request);   
	}

	private NxSolutionDetail createDummyNxSolution() {
		NxSolutionDetail nxSolutionDetail = new NxSolutionDetail();
		nxSolutionDetail.setNxSolutionId(12345L);
		nxSolutionDetail.setCreatedUser("createdUser");
		NxRequestDetails nxRequest = new NxRequestDetails();
		nxRequest.setNxSolutionDetail(nxSolutionDetail);
		nxRequest.setStatus(30L);
		nxSolutionDetail.addNxRequestDetail(nxRequest);
		NxTeam nxTeam = new NxTeam();
		nxTeam.setEmail("test@test.com");
		nxTeam.setIsPryMVG("Y");
		Set<NxTeam> nxTeamSet = new HashSet<>();
		nxTeamSet.add(nxTeam);
		nxSolutionDetail.setNxTeams(nxTeamSet);
		return nxSolutionDetail;
	}
	private NxSolutionDetail createDummyNxSolutionForStatus() {
		NxSolutionDetail nxSolutionDetail = new NxSolutionDetail();
		nxSolutionDetail.setNxSolutionId(12345L);
		NxRequestDetails nxRequest = new NxRequestDetails();
		nxRequest.setNxSolutionDetail(nxSolutionDetail);
		nxSolutionDetail.addNxRequestDetail(nxRequest);
		NxTeam nxTeam = new NxTeam();
		nxTeam.setEmail("test@test.com");
		nxTeam.setIsPryMVG("Y");
		Set<NxTeam> nxTeamSet = new HashSet<>();
		nxTeamSet.add(nxTeam);
		nxSolutionDetail.setNxTeams(nxTeamSet);
		return nxSolutionDetail;
	}
	
	@Test
	public void testMailNotificationFMO() throws SalesBusinessException {
		MailRequest request = new MailRequest();
		request.setNxRequestId(new Long(63));
		NxSolutionDetail nxSolutionDetail = createDummyNxSolution();
		Mockito.when(repo.findByNxReqId(Mockito.anyLong()))
				.thenReturn(nxSolutionDetail.getNxRequestDetails().iterator().next());
		Mockito.when(repository.getNxTeam(Mockito.anyLong(), Mockito.anyString()))
				.thenReturn(nxSolutionDetail.getNxTeams().iterator().next());
		MailResponse mailResp = new MailResponse();
		Mockito.when(dme.callMailNotificationDME2(Mockito.anyString())).thenReturn(mailResp);
		MailResponse result=service.mailNotificationFMO(request);
		assertSame(mailResp, result);
	}
	
	@Test
	public void testPrepareMyPriceDealSubmissionRequest() {
		NxMpDeal nxMpDeal = new NxMpDeal();
		nxMpDeal.setSolutionId(1L);
		nxMpDeal.setNxTxnId(1L);
		nxMpDeal.setDealStatus("SUBMITTED");
		nxMpDeal.setTransactionId("45676");
		nxMpDeal.setRevision("1");
		nxMpDeal.setDealID("1");
		nxMpDeal.setVersion("1");
		List<NxSolutionDetail> nxSolutionDetailList  = new ArrayList<>();
		NxSolutionDetail nxSolutionDetail = createDummyNxSolution();
		nxSolutionDetailList.add(nxSolutionDetail);
		Mockito.when(nxSolutionDetailsRepository.findByNxSolutionId(Mockito.anyLong())).thenReturn(nxSolutionDetailList.get(0));
		List<NxTeam> teamList = new ArrayList<>();
		NxTeam nxTeam = new NxTeam();
		nxTeam.setAttuid("createdUser");
		nxTeam.setfName("fName");
		nxTeam.setlName("lName");
		teamList.add(nxTeam);
		Mockito.when(repository.findByNxSolutionId(Mockito.anyLong())).thenReturn(teamList);
		Mockito.when(environment.getProperty(Mockito.anyString())).thenReturn("Hi fName lName for solnId with transaction transactionId deal dealId verId revId");
		service.prepareMyPriceDealSubmissionRequest(nxMpDeal);

		nxMpDeal.setDealStatus("FAILED");
		service.prepareMyPriceDealSubmissionRequest(nxMpDeal);

		nxMpDeal.setDealStatus(CommonConstants.PARTIAL);
		service.prepareMyPriceDealSubmissionRequest(nxMpDeal);
	}
	
	@Test
	public void testPrepareMailRequestFMO() {
		NxTeam nxTeam = new NxTeam();
		nxTeam.setAttuid("aasd");
		MailRequest request= new MailRequest();
		request.setNxRequestId(1L);
		NxRequestDetails nxRequestDetail = new NxRequestDetails();
		nxRequestDetail.setStatus(40L);
		NxSolutionDetail nxSolutionDetail = createDummyNxSolution();
		nxRequestDetail.setNxSolutionDetail(nxSolutionDetail);
		Mockito.when(environment.getProperty(Mockito.anyString())).thenReturn("Hi fName lName for solnId with transaction transactionId deal dealId verId revId");
		
		MailServiceRequest result=service.prepareMailRequestFMO(nxTeam,request,nxRequestDetail);
		assertEquals(StringConstants.ADOPT, result.getMailsource());
		assertEquals("aasd@att.com", result.getToMailList());

	}
	
	@Test
	public void testDataUploadMailNotification() throws SalesBusinessException, JsonProcessingException {
		Map<String,Object> inputmap=new HashMap<>();
		String userId="userID";
		MailResponse mailResp = new MailResponse();
		Mockito.when(dme.callMailNotificationDME2(Mockito.anyString())).thenReturn(mailResp);
		Mockito.when(environment.getProperty(Mockito.anyString())).thenReturn("Hi fName lName for solnId with transaction transactionId deal dealId verId revId");
		Mockito.when(mapper.writeValueAsString(Mockito.any())).thenReturn("json");
		MailResponse result=service.dataUploadMailNotification(inputmap, userId);
		assertSame(mailResp, result);
	}
	
	@Test
	public void testPrepareDataUploadMailRequest() {
		Map<String,Object> inputmap=new HashMap<>();
		inputmap.put(DataUploadConstants.STATUS,DataUploadConstants.SUCCESS);
		String userId="userID";
		Mockito.when(environment.getProperty(Mockito.anyString())).thenReturn("Hi fName lName for solnId with transaction transactionId deal dealId verId revId");
		MailServiceRequest result=service.prepareDataUploadMailRequest(inputmap, userId);
		assertEquals(userId + StringConstants.ATT_EXTN , result.getToMailList());
	}
	
	@Test
	public void testPrepareAndSendMailForPEDRequest() throws SalesBusinessException, JsonProcessingException {
		Long solutionId=1l;
		String attId="aassd";
		NxTeam nxTeam =  new NxTeam();
		nxTeam.setIsPryMVG("Y");
		nxTeam.setAttuid("assd");
		nxTeam.setfName("fName");
		nxTeam.setlName("lName");		
		Mockito.when(repository.getNxTeam(Mockito.anyLong(),Mockito.anyString())).thenReturn(nxTeam);
		Mockito.when(environment.getProperty(Mockito.anyString())).thenReturn("Hi fName lName for solnId with transaction transactionId deal dealId verId revId");
		Mockito.when(mapper.writeValueAsString(Mockito.any())).thenReturn("json");
		MailResponse mailResp = new MailResponse();
		Mockito.when(dme.callMailNotificationDME2(Mockito.anyString())).thenReturn(mailResp);
		MailResponse result=(MailResponse) service.prepareAndSendMailForPEDRequest(solutionId,attId);
		assertSame(mailResp, result);
	}
	
	@Test
	public void testPrepareAndSendMailForBulkuploadEthTokensRequest() throws SalesBusinessException, JsonProcessingException {
		NxTeam nxTeam =  new NxTeam();
		nxTeam.setAttuid("assd");
		nxTeam.setfName("fName");
		nxTeam.setlName("lName");		
		nxTeam.setIsPryMVG("Y");
		Mockito.when(repository.getNxTeam(Mockito.anyLong(),Mockito.anyString())).thenReturn(nxTeam);
		Mockito.when(environment.getProperty(Mockito.anyString())).thenReturn("Hi fName lName for solnId with transaction transactionId deal dealId verId revId");
		Mockito.when(mapper.writeValueAsString(Mockito.any())).thenReturn("json");
		MailResponse mailResp = new MailResponse();
		Mockito.when(dme.callMailNotificationDME2(Mockito.anyString())).thenReturn(mailResp);
		MailResponse result=service.prepareAndSendMailForBulkuploadEthTokensRequest(1L,"attId","solutionName",1);
		assertSame(mailResp, result);
		
		service.prepareAndSendMailForBulkuploadEthTokensRequest(1L,"attId","solutionName",2);
		service.prepareAndSendMailForBulkuploadEthTokensRequest(1L,"attId","solutionName",3);
	}
}
