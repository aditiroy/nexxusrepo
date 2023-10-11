package com.att.sales.nexxus.service;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.when;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
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
import org.springframework.core.env.Environment;
import org.springframework.web.client.RestTemplate;

import com.att.sales.framework.exception.SalesBusinessException;
import com.att.sales.framework.model.Message;
import com.att.sales.framework.model.ServiceMetaData;
import com.att.sales.framework.model.ServiceResponse;
import com.att.sales.nexxus.dao.model.NxUser;
import com.att.sales.nexxus.dao.model.NxUserLockDetails;
import com.att.sales.nexxus.dao.repository.NxSolutionDetailsRepository;
import com.att.sales.nexxus.dao.repository.NxSolutionUserLockRepository;
import com.att.sales.nexxus.dao.repository.NxUserRepository;
import com.att.sales.nexxus.model.SolutionLockRequest;
import com.att.sales.nexxus.util.NxSolutionUserLockUtil;
@ExtendWith(MockitoExtension.class)
public class SolutionLockServiceImplTest {

	@Mock
	private NxSolutionUserLockRepository nxSolutionUserLockRepository;

	@Mock
	private NxSolutionDetailsRepository nxSolutionDetailsRepository;

	@Mock
	private NxSolutionUserLockUtil nxSolutionUserLockUtil;

	@InjectMocks
	SolutionLockServiceImpl solutionLockServiceImpl;

	@Mock
	private Environment env;

	@Mock
	private RestTemplate restTemplate;

	@Mock
	private NxUserRepository nxUserRepository;

	@Mock
	private Message message;

	@Mock
	private SalesBusinessException salesBusinessException;

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
	public void testSolutionLock() throws SalesBusinessException {
		Date modifiedDate = new Timestamp(System.currentTimeMillis());
		String userId = "user1";
		String isLocked = "Y";
		String nxSolutionId = "12345";
		SolutionLockRequest request = new SolutionLockRequest();
		ServiceResponse resp = new ServiceResponse();
		request.setAttuid(userId);
		request.setIsLocked(isLocked);
		request.setNxSolutionId(nxSolutionId);
		when(restTemplate.postForObject(any(), any(), any())).thenReturn(resp);
		NxUser nxUser = new NxUser();
		nxUser.setEmail("test@mail.com");
		Mockito.when(nxUserRepository.findByUserAttId(anyString())).thenReturn(nxUser);
		List<NxUserLockDetails> existingLocks = new ArrayList<NxUserLockDetails>();
		NxUserLockDetails lock = new NxUserLockDetails();
		lock.setIsLocked("N");
		Mockito.when(nxSolutionUserLockRepository.findByNxSolutionIdAndIsLocked(anyLong(), anyString()))
				.thenReturn(existingLocks);
		Mockito.when(nxSolutionDetailsRepository.updateLockStatusIndBySolutionId(anyString(), anyString(), anyLong()))
				.thenReturn(1);
		Mockito.when(nxSolutionUserLockRepository.updateLockStatusIndBySolutionId(isLocked, modifiedDate,
				Long.valueOf(nxSolutionId))).thenReturn(1);
		solutionLockServiceImpl.solutionLockCheck(request);
	}

	@Test
	public void testSolutionUnlock() throws SalesBusinessException {
		Date modifiedDate = new Timestamp(System.currentTimeMillis());
		String userId = "user1";
		String isLocked = "N";
		String nxSolutionId = "12345";
		SolutionLockRequest request = new SolutionLockRequest();
		ServiceResponse resp = new ServiceResponse();
		request.setAttuid(userId);
		request.setIsLocked(isLocked);
		request.setNxSolutionId(nxSolutionId);
		when(restTemplate.postForObject(any(), any(), any())).thenReturn(resp);
		NxUser nxUser = new NxUser();
		nxUser.setEmail("test@mail.com");
		Mockito.when(nxUserRepository.findByUserAttId(anyString())).thenReturn(nxUser);
		List<NxUserLockDetails> existingLocks = new ArrayList<NxUserLockDetails>();
		NxUserLockDetails lock = new NxUserLockDetails();
		lock.setIsLocked("Y");
		existingLocks.add(lock);
		Mockito.when(nxSolutionUserLockRepository.findByNxSolutionIdAndIsLocked(anyLong(), anyString()))
				.thenReturn(existingLocks);
		Mockito.when(nxSolutionDetailsRepository.updateLockStatusIndBySolutionId(anyString(), anyString(), anyLong()))
				.thenReturn(1);
		Mockito.when(nxSolutionUserLockRepository.updateLockStatusIndBySolutionId(isLocked, modifiedDate,
				Long.valueOf(nxSolutionId))).thenReturn(1);
		solutionLockServiceImpl.solutionLockCheck(request);
	}
}
