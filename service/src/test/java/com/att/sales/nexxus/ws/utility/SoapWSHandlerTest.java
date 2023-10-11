package com.att.sales.nexxus.ws.utility;

import static org.mockito.Mockito.anyString;

import java.util.Map;

import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPHeader;
import javax.xml.soap.SOAPMessage;
import javax.xml.soap.SOAPPart;

import org.junit.Before;
import org.junit.jupiter.api.Test;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
public class SoapWSHandlerTest {
	
	@Spy
	SoapWSHandler soapWSHandler;
	
	@Spy
	SOAPMessage soapMessage;
	
	@Mock
	Map<String, Object> map;
	
	@Mock
	SOAPPart soaPPart;
	
	@Mock
	SOAPEnvelope envelope;
	
	@Mock
	SOAPHeader header;
	
	@Mock
	SOAPElement element;
	
	@BeforeEach
	public void initialize() {
		ReflectionTestUtils.setField(soapWSHandler, "endPointUrl", "");
		ReflectionTestUtils.setField(soapWSHandler, "contextPath", "http://test.com");
		ReflectionTestUtils.setField(soapWSHandler, "username", "");
		ReflectionTestUtils.setField(soapWSHandler, "password", "");
		ReflectionTestUtils.setField(soapWSHandler, "httpProxyHost", "");
		ReflectionTestUtils.setField(soapWSHandler, "httpProxyPort", "8080");
		ReflectionTestUtils.setField(soapWSHandler, "httpsProxyHost", "");
		ReflectionTestUtils.setField(soapWSHandler, "httpsProxyPort", "");
		ReflectionTestUtils.setField(soapWSHandler, "httpProxyUser", "");
		ReflectionTestUtils.setField(soapWSHandler, "httpProxyPassword", "");
		ReflectionTestUtils.setField(soapWSHandler, "endPointUrl", "");
		ReflectionTestUtils.setField(soapWSHandler, "httpProxySet", "");
	}
	
	@Test
	public void testSetWsCredentials() {
		soapWSHandler.setWsCredentials(anyString(), anyString(), anyString(), anyString());
	}

	@Test
	public void testSetProxyDetails() {
		soapWSHandler.setProxyDetails(anyString(), anyString(), anyString(), anyString(), anyString(), anyString(), anyString());
	}
	
	@Test
	public void testHandleMessage() throws SOAPException {
		Mockito.when(soapMessage.getSOAPPart()).thenReturn(soaPPart);
		Mockito.when(soaPPart.getEnvelope()).thenReturn(envelope);
		Mockito.when(envelope.getHeader()).thenReturn(header);
		Mockito.when(header.addChildElement(anyString(), anyString())).thenReturn(element);
		soapWSHandler.handleMessage(soapMessage, true, map);
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void testHandleMessageExce() {
		Mockito.when(soapMessage.getSOAPPart()).thenThrow(SOAPException.class);
		soapWSHandler.handleMessage(soapMessage, true, map);
	}
	
	@Test
	public void testGetwebServiceTemplate() {
		ReflectionTestUtils.setField(soapWSHandler, "isProxyRequired", true);
		soapWSHandler.getwebServiceTemplate();
		
	}
}
