package com.att.sales.nexxus.dmaap.mr.utilTest;


import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.env.Environment;

import com.att.sales.framework.model.ServiceMetaData;
import com.att.sales.nexxus.dmaap.mr.util.DmaapMRSubscriberImpl;

@ExtendWith(MockitoExtension.class)
public class DmaapMRSubscriberImplTest {
	
	
	@Mock
	private Environment env;
	
	@InjectMocks 
private 	DmaapMRSubscriberImpl dmaapMRSubscriberImpl;
	
	@Mock
	FileReader reader;
	
	@Mock
	File file;
	
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
	public void testRetrieveMessage() throws Exception {
		String topic = "com.att.edf.test.ms.InvAndRevDataResponse";
		String groupName = "edfTest";
		String message = "{\"Offer\":\"AVPN\",\"TypeofRule\": \"PORT\",\"Region\": \"US\",\"DeltaID\": 123}";
		List<String> messagesList = new ArrayList<>();
		messagesList.add(message);
String 	 mrConsumerPropFilePath="opt/att/ajsc/config/dmaap-mr-config/dmaap_mr_consumer.properties";
		
		try {
			dmaapMRSubscriberImpl.retrieveMessage(topic, groupName, "host");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

}

