package com.att.sales.nexus.config;

import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPHeader;
import javax.xml.soap.SOAPMessage;
import javax.xml.soap.SOAPPart;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class WSClientSoapHandlerTest {

	@InjectMocks
	WSClientSoapHandler test;

	@Mock
	SOAPMessage soapMessage;
	@Mock
	SOAPEnvelope sOAPEnvelope;
	@Mock
	SOAPPart soapPartValue;
	@Mock
	SOAPHeader header;
	@Mock
	SOAPElement security;
	@Mock
	SOAPElement usernameToken;
	@Mock
	SOAPElement usernameElement;
	@Mock
	SOAPElement passwordElement;

	@Test
	public void test() {
		Boolean boolean1 = true;

		Mockito.when(soapMessage.getSOAPPart()).thenReturn(soapPartValue);
		try {
			Mockito.when(soapPartValue.getEnvelope()).thenReturn(sOAPEnvelope);
			Mockito.when(sOAPEnvelope.getHeader()).thenReturn(header);
			Mockito.when(header.addChildElement("Security", "wsse",
					"http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd"))
					.thenReturn(security);
			Mockito.when(security.addChildElement("UsernameToken", "wsse")).thenReturn(usernameToken);
			Mockito.when(usernameToken.addChildElement("Username", "wsse")).thenReturn(usernameElement);
			Mockito.when(usernameToken.addChildElement("Password", "wsse")).thenReturn(passwordElement);
			Mockito.when(usernameToken.addChildElement("Password", "wsse")).thenReturn(passwordElement);

		} catch (SOAPException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		test.handleMessage(soapMessage, boolean1, "testUser", "testPassword");
	}

	@Test
	public void testExp() {
		Boolean boolean1 = true;
		test.handleMessage(soapMessage, boolean1, "testUser", "testPassword");
	}
}
