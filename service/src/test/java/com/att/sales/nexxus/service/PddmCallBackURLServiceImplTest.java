package com.att.sales.nexxus.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.env.Environment;

import com.att.sales.nexxus.model.ProductRuleRequest;
import com.att.sales.nexxus.util.DME2RestClient;
import com.fasterxml.jackson.databind.ObjectMapper;

@ExtendWith(MockitoExtension.class)
public class PddmCallBackURLServiceImplTest {
	
	@InjectMocks
	PddmCallBackURLServiceImpl test;
	
	@Mock
	private DME2RestClient dmeClient;
	
	@Mock
	private Environment env;
	
	@Mock
	ObjectMapper mapper;

	@Mock
	private ProductRuleRequest request;
	
	@Test
	public void test() {
		
		try {
			test.getPRoductRulesFromPddm(request);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

}
