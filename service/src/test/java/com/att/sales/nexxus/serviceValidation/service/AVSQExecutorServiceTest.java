package com.att.sales.nexxus.serviceValidation.service;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyMap;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import com.att.sales.nexxus.serviceValidation.model.AddressValidationServiceQualificationRequest;
import com.att.sales.nexxus.util.DME2RestClient;


public class AVSQExecutorServiceTest {

	
	@InjectMocks
	AVSQExecutorService aVSQExecutorService;
	
	@Mock
	DME2RestClient dme2RestClient;
	
	@Test
	public void test() throws Exception {
		Map<String, Object> paramMap = new HashMap<>();
		Map<String, Object> requestParams = new HashMap<>();
		paramMap.put("requestMetaDataMap", requestParams);
		AddressValidationServiceQualificationRequest addressValidationServiceQualificationRequest = new AddressValidationServiceQualificationRequest();
		aVSQExecutorService.setParamMap(paramMap);
		aVSQExecutorService.setAddressValidationServiceQualificationRequest(addressValidationServiceQualificationRequest);
		String response ="success" ;
		Mockito.when(dme2RestClient.callAVSQRequest(any(), anyMap())).thenReturn(response);
		aVSQExecutorService.call();
	}

}
