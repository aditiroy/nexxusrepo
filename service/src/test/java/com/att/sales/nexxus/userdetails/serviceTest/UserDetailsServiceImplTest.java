package com.att.sales.nexxus.userdetails.serviceTest;


import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.env.Environment;
import org.springframework.web.client.RestTemplate;

import com.att.sales.framework.model.ServiceMetaData;
import com.att.sales.nexxus.userdetails.model.UserDetailsRequest;
import com.att.sales.nexxus.userdetails.model.UserDetailsResponse;
import com.att.sales.nexxus.userdetails.service.UserDetailsServiceImpl;

@ExtendWith(MockitoExtension.class)
public class UserDetailsServiceImplTest {

	@InjectMocks
	UserDetailsServiceImpl userDetailsServiceImpl;
	
	@Mock
	private Environment env;
	
	@Mock
	private RestTemplate restTemplate;
	
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
		UserDetailsRequest request = new UserDetailsRequest();
		UserDetailsResponse resp = new UserDetailsResponse();
		request.setAttuid("attuid");
		when(restTemplate.postForObject(any(),any(),any())).thenReturn(resp);
		userDetailsServiceImpl.retreiveUserDetails(request);
	}

}
