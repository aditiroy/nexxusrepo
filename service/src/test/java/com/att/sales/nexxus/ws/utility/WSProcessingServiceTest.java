package com.att.sales.nexxus.ws.utility;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyBoolean;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.anyMap;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.doNothing;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;
import javax.xml.transform.Source;


import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.oxm.Unmarshaller;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.ws.client.core.WebServiceTemplate;
import org.springframework.ws.soap.SoapBody;
import org.springframework.ws.soap.SoapFault;
import org.springframework.ws.soap.SoapFaultDetail;
import org.springframework.ws.soap.SoapFaultDetailElement;
import org.springframework.ws.soap.SoapMessage;
import org.springframework.ws.soap.client.SoapFaultClientException;

import com.att.sales.nexxus.constant.MyPriceConstants;
import com.oracle.xmlns.cpqcloud.configuration.wireline.solution.solution.Configure;
import com.oracle.xmlns.cpqcloud.configuration.wireline.solution.solution.ConfigureResponse;
import com.oracle.xmlns.cpqcloud.configuration.wireline.solution.solution.Fault;
import com.oracle.xmlns.cpqcloud.configuration.wireline.solution.solution.ObjectFactory;

@ExtendWith(MockitoExtension.class)
public class WSProcessingServiceTest {

	@InjectMocks
	@Spy
	private WSProcessingService wsProcessingService;
	
	@Mock
	private SoapWSHandler configureWSClientUtility;
	
	@Mock
	private SoapWSHandler wsHandler;
	
	@Mock
	private WebServiceTemplate serviceTemplate;
	
	@Mock
	private SoapMessage msg;
	
	@Mock
	private SoapFault fault;
	
	@Mock
	private SoapFaultDetail faultDetail;
	
	@Mock
	private SoapBody soapBody;
	
	@Mock
	private SoapFaultClientException ex;
	
	@Mock
	private Iterator<SoapFaultDetailElement> details;
	
	@Mock
	private SoapFaultDetailElement ele;
	
	@Mock
	private Source src;
	
	@Mock
	private Object obj;
	
	@Mock
	private Unmarshaller unmarshaller;
	
	@Mock
	private QName qName;

	@Mock
	private JAXBElement<Fault> detail;
	
	@Mock
	private Fault value;
	
	
	@Test
	public void test() throws WSClientException {
		ReflectionTestUtils.setField(wsProcessingService, "logEnabled", "Y");
		ObjectFactory objectFactory = new ObjectFactory();
		Configure configureRequest = objectFactory.createConfigure();
		Map<String, Object> requestMap = new HashMap<String, Object>();
		requestMap.put(MyPriceConstants.MP_TRANSACTION_ID, "12324");
		requestMap.put("optyId", "1OPTY");
		Mockito.when(configureWSClientUtility.getwebServiceTemplate()).thenReturn(serviceTemplate);
		wsProcessingService.initiateWebService(configureRequest,
				configureWSClientUtility, requestMap, ConfigureResponse.class);
		
	}
	
	
	@Test
	public void testException() {
		try {
			ReflectionTestUtils.setField(wsProcessingService, "logEnabled", "Y");
			ObjectFactory objectFactory = new ObjectFactory();
			Configure configureRequest = objectFactory.createConfigure();
			Map<String, Object> requestMap = new HashMap<String, Object>();
			requestMap.put("optyId", "1OPTY");
			Mockito.when(msg.getSoapBody()).thenReturn(soapBody);
			Mockito.when(ex.getSoapFault()).thenReturn(fault);
			Mockito.when(ex.getFaultCode()).thenReturn(qName);
			Mockito.when(configureWSClientUtility.getwebServiceTemplate()).thenThrow(ex);
			wsProcessingService.initiateWebService(configureRequest,
					configureWSClientUtility, requestMap, ConfigureResponse.class);
		}catch(Exception e) {
			
		}
	}
	
	@Test
	public void testException1() {
		try {
			ReflectionTestUtils.setField(wsProcessingService, "logEnabled", "Y");
			ObjectFactory objectFactory = new ObjectFactory();
			Configure configureRequest = objectFactory.createConfigure();
			Map<String, Object> requestMap = new HashMap<String, Object>();
			requestMap.put("optyId", "1OPTY");
			Mockito.when(msg.getSoapBody()).thenReturn(soapBody);
			Mockito.when(ex.getSoapFault()).thenReturn(fault);
			Mockito.when(ex.getFaultCode()).thenReturn(qName);
			Mockito.when(fault.getFaultDetail()).thenReturn(faultDetail);
			Mockito.when(faultDetail.getDetailEntries()).thenReturn(details);
			Mockito.when(details.next()).thenReturn(ele);
			Mockito.when(ele.getSource()).thenReturn(src);
			Mockito.when(configureWSClientUtility.getwebServiceTemplate()).thenReturn(serviceTemplate);
			Mockito.when(serviceTemplate.getUnmarshaller()).thenReturn(unmarshaller);
			Mockito.when(unmarshaller.unmarshal(src)).thenReturn(detail);
			Mockito.doThrow(ex).when(wsProcessingService).setBackServiceMetaData(anyMap());
			Mockito.when(configureWSClientUtility.getWsType()).thenReturn("Config");
			wsProcessingService.initiateWebService(configureRequest,
					configureWSClientUtility, requestMap, ConfigureResponse.class);
		}catch(Exception e) {
			
		}
	}
	
	@Test
	public void testExceptionConfigWs() {
		try {
			ReflectionTestUtils.setField(wsProcessingService, "logEnabled", "Y");
			ObjectFactory objectFactory = new ObjectFactory();
			Configure configureRequest = objectFactory.createConfigure();
			Map<String, Object> requestMap = new HashMap<String, Object>();
			requestMap.put("optyId", "1OPTY");
			Mockito.when(msg.getSoapBody()).thenReturn(soapBody);
			Mockito.when(ex.getSoapFault()).thenReturn(fault);
			Mockito.when(ex.getFaultCode()).thenReturn(qName);
			Mockito.when(fault.getFaultDetail()).thenReturn(faultDetail);
			Mockito.when(faultDetail.getDetailEntries()).thenReturn(details);
			Mockito.when(details.next()).thenReturn(ele);
			Mockito.when(ele.getSource()).thenReturn(src);
			Mockito.when(configureWSClientUtility.getwebServiceTemplate()).thenReturn(serviceTemplate);
			Mockito.when(serviceTemplate.getUnmarshaller()).thenReturn(unmarshaller);
			Mockito.when(unmarshaller.unmarshal(src)).thenReturn(detail);
			Mockito.doThrow(ex).when(wsProcessingService).setBackServiceMetaData(anyMap());
			Mockito.when(configureWSClientUtility.getWsType()).thenReturn(MyPriceConstants.CONFIG_WS);
			wsProcessingService.initiateWebService(configureRequest,
					configureWSClientUtility, requestMap, ConfigureResponse.class);
		}catch(Exception e) {
			
		}
	}
	
	@Test
	public void testExceptionOptyInfoWs() {
		try {
			ReflectionTestUtils.setField(wsProcessingService, "logEnabled", "Y");
			ObjectFactory objectFactory = new ObjectFactory();
			Configure configureRequest = objectFactory.createConfigure();
			Map<String, Object> requestMap = new HashMap<String, Object>();
			requestMap.put("optyId", "1OPTY");
			Mockito.when(msg.getSoapBody()).thenReturn(soapBody);
			Mockito.when(ex.getSoapFault()).thenReturn(fault);
			Mockito.when(ex.getFaultCode()).thenReturn(qName);
			Mockito.when(fault.getFaultDetail()).thenReturn(faultDetail);
			Mockito.when(faultDetail.getDetailEntries()).thenReturn(details);
			Mockito.when(details.next()).thenReturn(ele);
			Mockito.when(ele.getSource()).thenReturn(src);
			Mockito.when(configureWSClientUtility.getwebServiceTemplate()).thenReturn(serviceTemplate);
			Mockito.when(serviceTemplate.getUnmarshaller()).thenReturn(unmarshaller);
			Mockito.when(unmarshaller.unmarshal(src)).thenReturn(detail);
			doNothing().when(wsProcessingService).printStartEndLogs(anyBoolean(), anyLong() , anyLong(), any(),anyMap(), anyString());
			Mockito.doThrow(ex).when(wsProcessingService).setBackServiceMetaData(anyMap());
			Mockito.when(configureWSClientUtility.getWsType()).thenReturn(MyPriceConstants.OPTY_INFO_WS);
			wsProcessingService.initiateWebService(configureRequest,
					configureWSClientUtility, requestMap, ConfigureResponse.class);
		}catch(Exception e) {
			
		}
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void testExceptionIO() {
		try {
			ReflectionTestUtils.setField(wsProcessingService, "logEnabled", "Y");
			ReflectionTestUtils.setField(wsProcessingService, "zipkinTraceEnabled", "Y");
			ObjectFactory objectFactory = new ObjectFactory();
			Configure configureRequest = objectFactory.createConfigure();
			Map<String, Object> requestMap = new HashMap<String, Object>();
			requestMap.put("optyId", "1OPTY");
			Mockito.when(msg.getSoapBody()).thenReturn(soapBody);
			Mockito.when(ex.getSoapFault()).thenReturn(fault);
			Mockito.when(ex.getFaultCode()).thenReturn(qName);
			Mockito.when(fault.getFaultDetail()).thenReturn(faultDetail);
			Mockito.when(faultDetail.getDetailEntries()).thenReturn(details);
			Mockito.when(details.next()).thenReturn(ele);
			Mockito.when(ele.getSource()).thenReturn(src);
			Mockito.when(configureWSClientUtility.getwebServiceTemplate()).thenReturn(serviceTemplate);
			Mockito.when(serviceTemplate.getUnmarshaller()).thenThrow(IOException.class);
			Mockito.doThrow(ex).when(wsProcessingService).setBackServiceMetaData(anyMap());
			Mockito.when(configureWSClientUtility.getWsType()).thenReturn(MyPriceConstants.OPTY_INFO_WS);
			wsProcessingService.initiateWebService(configureRequest,
					configureWSClientUtility, requestMap, ConfigureResponse.class);
		}catch(Exception e) {
			
		}
	}
	
	@Test
	public void testException2() {
		try {
			ReflectionTestUtils.setField(wsProcessingService, "logEnabled", "Y");
			ObjectFactory objectFactory = new ObjectFactory();
			Configure configureRequest = objectFactory.createConfigure();
			Map<String, Object> requestMap = new HashMap<String, Object>();
			requestMap.put("optyId", "1OPTY");
			Mockito.when(configureWSClientUtility.getwebServiceTemplate()).thenThrow(Exception.class);
			wsProcessingService.initiateWebService(configureRequest,
					configureWSClientUtility, requestMap, ConfigureResponse.class);
		}catch(Exception e) {
			
		}
	}
	
	@Test
	public void testIsRetriggered(){
		String errorCd = "500";
		Map<String, Object> methodParamMap = new HashMap<String, Object>();
		String wsName = MyPriceConstants.CONFIG_SOL_PRODUCT_WS;
		ReflectionTestUtils.setField(wsProcessingService, "retriggerFlag", "Y");
		ReflectionTestUtils.setField(wsProcessingService, "mpRetriggerErrorCd", "500");
		ReflectionTestUtils.setField(wsProcessingService, "maxRetriggerCount", 1);
		wsProcessingService.isRetriggered(errorCd, methodParamMap, wsName);
		
		wsProcessingService.isRetriggered(errorCd, methodParamMap, MyPriceConstants.CONFIG_DESIGN_WS);
	}
	
	@Test
	public void testPrintLogs() {
		wsProcessingService.printLogs(null, obj);
	}
	
	
}
