package com.att.sales.nexxus.ws.utility;

import static org.junit.Assert.assertEquals;


import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;


@ExtendWith(MockitoExtension.class)
public class WSClientExceptionTest {
	
	@Test
	public void test() {
		WSClientException ws = new WSClientException("1234" , "fault",  "fault string", new Throwable());
		assertEquals("fault", ws.getFaultCode());
		
		WSClientException ws1 = new WSClientException("1234" , "fault string", new Throwable());
		assertEquals("fault string", ws1.getFaultString());
		
		ws1.toString();
		
	}
	

}
