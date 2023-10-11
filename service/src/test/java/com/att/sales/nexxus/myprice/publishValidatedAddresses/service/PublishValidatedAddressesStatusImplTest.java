package com.att.sales.nexxus.myprice.publishValidatedAddresses.service;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import com.att.sales.framework.exception.SalesBusinessException;
import com.att.sales.framework.model.ServiceMetaData;
import com.att.sales.nexxus.myprice.publishValidatedAddresses.model.PublishValidatedAddressesStatusRequest;

@ExtendWith(MockitoExtension.class)
public class PublishValidatedAddressesStatusImplTest {

	@InjectMocks
	PublishValidatedAddressesStatusImpl publishValidatedAddressesStatusImpl;
	
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
	public void publishValidatedAddressesStatusTest() throws SalesBusinessException{
		
		PublishValidatedAddressesStatusRequest request = new PublishValidatedAddressesStatusRequest();
		
		request.setId("");
		
		request.setAction("Publish");
		
		publishValidatedAddressesStatusImpl.publishValidatedAddressesStatus(request);
		
	}
	
}
