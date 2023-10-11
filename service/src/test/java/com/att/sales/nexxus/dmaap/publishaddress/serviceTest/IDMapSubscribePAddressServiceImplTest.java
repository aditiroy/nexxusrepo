package com.att.sales.nexxus.dmaap.publishaddress.serviceTest;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.env.Environment;
import org.springframework.test.util.ReflectionTestUtils;

import com.att.msgrtr.referenceClient.MRClientFactory;
import com.att.msgrtr.referenceClient.MRConsumer;
import com.att.sales.framework.exception.SalesBusinessException;
import com.att.sales.framework.model.ServiceMetaData;
import com.att.sales.nexxus.dmaap.mr.util.DmaapMRSubscriberImpl;
import com.att.sales.nexxus.dmaap.publishaddress.processor.MyPriceInitiatedFlow;
import com.att.sales.nexxus.dmaap.publishaddress.service.IDMapSubscribePAddressServiceImpl;

@ExtendWith(MockitoExtension.class)
public class IDMapSubscribePAddressServiceImplTest {
	
	@InjectMocks
	
	IDMapSubscribePAddressServiceImpl iDMapSubscribePAddressService;
	
	@Mock
	DmaapMRSubscriberImpl dMaapMRSubscriberImpl;
	
	@Mock
	 File file;
	
	@Mock
	 FileReader fileReader;
	
	@Mock 
	MRClientFactory props;	
	
	@Mock
	Environment env;
	
	@Mock
	MRConsumer consumer;
	
	@Mock
	MyPriceInitiatedFlow myPriceInitiatedFlow;
	
	
	@BeforeEach
	public void initializeServiceMetaData() {
	Map<String, Object> requestParams = new HashMap<>();
		requestParams.put(ServiceMetaData.OFFER, "AVPN");
		requestParams.put(ServiceMetaData.REST_REQUEST_START_TIME, System.currentTimeMillis());
		requestParams.put(ServiceMetaData.VERSION, "1.0");
		requestParams.put(ServiceMetaData.METHOD, "TestMethod");
		requestParams.put(ServiceMetaData.URI, "Testuri");
		ReflectionTestUtils.setField(iDMapSubscribePAddressService, "ipneDmaapEnabled", "Y");
		ReflectionTestUtils.setField(dMaapMRSubscriberImpl, "mrConsumerPropFilePath", 
				"opt/att/ajsc/config/dmaap-mr-config/dmaap_mr_consumer.properties"); 
		
		ServiceMetaData.add(requestParams);
	}
	
	@Test
	public void testDmapSubscribedAddressService() throws SalesBusinessException, IOException, InterruptedException, Exception {
			when(this.env.getProperty("dmaap.subscriber.publishAddress.topic")).thenReturn("com.att.salesexpress.test.22787-salesexpress_IPNE-MyPrice-v1");
			when(this.env.getProperty("dmaap.subscriber.publishAddress.groupName")).thenReturn("/TEST");
			String topic = "com.att.salesexpress.test.22787-salesexpress_IPNE-MyPrice-v1";
			String groupName = "/TEST";
			String message = "{\r\n" + 
					"	\"sourceSystem\":\"myPrice\",\r\n" + 
					"	\"motsId\":27226,\r\n" + 
					"	\"uniqueId\":\"69758139\",\r\n" + 
					"	\"status\":\"COMPLETED\"\r\n" + 
					"}\r\n" + 
					"";
			List<String> messagesList = new ArrayList<>();
			messagesList.add(message);
			Mockito.when(dMaapMRSubscriberImpl.retrieveMessage(any(), any(), any())).thenReturn(messagesList);
			iDMapSubscribePAddressService.dMapPublishAddressEvent();			
	}
}
