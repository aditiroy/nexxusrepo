package com.att.sales.nexxus.dmaap.publishaddress.processor;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import org.json.JSONException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.env.Environment;
import org.springframework.test.util.ReflectionTestUtils;

import com.att.sales.framework.exception.SalesBusinessException;
import com.att.sales.framework.model.ServiceMetaData;
import com.att.sales.nexxus.dao.repository.NxMpDealRepository;
import com.att.sales.nexxus.dao.repository.NxSolutionDetailsRepository;
import com.att.sales.nexxus.dmaap.publishaddress.service.IDMapSubscribePAddressServiceImpl;
import com.att.sales.nexxus.myprice.transaction.service.MyPriceTransactionUtil;
import com.att.sales.nexxus.myprice.transaction.service.UpdateTxnSiteUploadServiceImpl;
import com.att.sales.nexxus.serviceValidation.service.AVSQUtil;
import com.att.sales.nexxus.serviceValidation.service.GetQualificationServiceImpl;
import com.att.sales.nexxus.util.DME2RestClient;
import com.att.sales.nexxus.util.RestClientUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@ExtendWith(MockitoExtension.class)
public class MyPriceInitiatedFlowTest {

	@Spy
	@InjectMocks
	MyPriceInitiatedFlow myPriceInitiatedFlow;
	
	@Mock
	NxMpDealRepository nxMpDealRepository;
	
	@Mock
	NxSolutionDetailsRepository repository;
	
	@Mock
	ObjectMapper mapper;
	
	@Mock
	Environment env;
	
	@Mock
	RestClientUtil restClient;
	
	@Mock
	MyPriceTransactionUtil myPriceTransactionUtil;
	
	@Mock
	java.net.Proxy proxy;
	
	@Mock
	GetQualificationServiceImpl getQualificationServiceImpl;
	
	@Mock
	DME2RestClient dme2RestClient;
	
	@Mock
	AVSQUtil avsqUtil;
	
	@Mock
	private ExecutorService executors;
	
	Integer threadSize=5;
	
     @InjectMocks
	
	IDMapSubscribePAddressServiceImpl iDMapSubscribePAddressService;
     
     @Mock
     Map <String , Object> map;
     
     @Mock
     UpdateTxnSiteUploadServiceImpl updateTxnSiteUploadServiceImpl;
	
	@BeforeEach
	public void initializeServiceMetaData() {
	Map<String, Object> requestParams = new HashMap<>();
		requestParams.put(ServiceMetaData.OFFER, "AVPN");
		requestParams.put(ServiceMetaData.REST_REQUEST_START_TIME, System.currentTimeMillis());
		requestParams.put(ServiceMetaData.VERSION, "1.0");
		requestParams.put(ServiceMetaData.METHOD, "TestMethod");
		requestParams.put(ServiceMetaData.URI, "Testuri");
		ReflectionTestUtils.setField(myPriceInitiatedFlow, "ipneDmaapMotsId", "27226");
		ServiceMetaData.add(requestParams);
	}

	@Test
	public void dMapSubscribeAddressProcessor() throws JsonProcessingException, SalesBusinessException, InterruptedException, JSONException {
		String dmapMsg ="{\r\n" + 
				"	\"sourceSystem\":\"myPrice\",\r\n" + 
				"	\"motsId\":27226,\r\n" + 
				"	\"uniqueId\":\"69758139\",\r\n" + 
				"	\"status\":\"COMPLETED\"\r\n" + 
				"}\r\n" + 
				"";
		List<String> messagesList = new ArrayList<>();
		messagesList.add(dmapMsg);
		List<Future<Object>> resultLst =new ArrayList<>();
		Future<Object> fo=null;
		resultLst.add(fo);
		when(executors.invokeAll(any())).thenReturn(resultLst);
		when(myPriceInitiatedFlow.getThreadSize()).thenReturn(threadSize);
		when(myPriceInitiatedFlow.getExcutorService()).thenReturn(executors);
		myPriceInitiatedFlow.dMapSubscribeAddressProcessorThroughThread(messagesList);
	}
	
	

}
