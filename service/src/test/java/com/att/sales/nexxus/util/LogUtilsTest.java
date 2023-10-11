package com.att.sales.nexxus.util;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.junit.jupiter.api.Test;


public class LogUtilsTest {
	
	@Test
	public void logStringTest() {
		String message = "message";
		String res = LogUtils.logString(message);
		assertNotNull(res);
		
		message = null;
		res = LogUtils.logString(message);
		assertNull(res);
	}
	
	@Test
	public void logStringWithAdditionalMessageTest() {
		String message = "message";
		String res = LogUtils.logStringWithAdditionalMessage(message);
		assertNotNull(res);
		
		message = null;
		res = LogUtils.logStringWithAdditionalMessage(message);
		assertNull(res);
	}
}
