package com.att.sales.nexxus.service;


import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.when;

import java.util.HashMap;
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
import com.att.sales.framework.util.MessageResourcesUtil;
import com.att.sales.nexxus.dao.model.NxUser;
import com.att.sales.nexxus.dao.repository.NxFeedbackRepository;
import com.att.sales.nexxus.dao.repository.NxUserRepository;
import com.att.sales.nexxus.model.SubmitFeedbackRequest;


@ExtendWith(MockitoExtension.class)
public class SubmitFeedbackServiceImplTest {
	@InjectMocks
	SubmitFeedbackServiceImpl submitFeedbackServiceImpl;
	
	@Mock
	private Environment env;
	
	@Mock
	private RestTemplate restTemplate;
	
	@Mock
	private NxUserRepository nxUserRepository;
	
	@Mock
	private NxFeedbackRepository nxFeedbackRepository;
	
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
	public void test() throws SalesBusinessException {
		SubmitFeedbackRequest request = new SubmitFeedbackRequest();
		ServiceResponse resp = new ServiceResponse();
		request.setAttuid("attuid");
		request.setFeedback("Test");
		when(restTemplate.postForObject(any(),any(),any())).thenReturn(resp);
		NxUser nxUser = new NxUser();
		nxUser.setEmail("test@mail.com");
		Mockito.when(nxUserRepository.findByUserAttId(anyString())).thenReturn(nxUser);
		submitFeedbackServiceImpl.submitFeedback(request);
	}
	
	
}
