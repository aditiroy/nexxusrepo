package com.att.sales.nexxus.ws.utility;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
public class ConfigureSolnAndProductWSClientUtilityTest {
	
	@InjectMocks
	private ConfigureSolnAndProductWSClientUtility configureSolnAndProductWSClientUtility;
	
	@Test
	public void testInit() {
		ReflectionTestUtils.setField(configureSolnAndProductWSClientUtility, "endPointUrl", "");
		ReflectionTestUtils.setField(configureSolnAndProductWSClientUtility, "contextPath", "");
		ReflectionTestUtils.setField(configureSolnAndProductWSClientUtility, "username", "");
		ReflectionTestUtils.setField(configureSolnAndProductWSClientUtility, "password", "");
		ReflectionTestUtils.setField(configureSolnAndProductWSClientUtility, "httpProxyHost", "");
		ReflectionTestUtils.setField(configureSolnAndProductWSClientUtility, "httpProxyPort", "");
		ReflectionTestUtils.setField(configureSolnAndProductWSClientUtility, "httpsProxyHost", "");
		ReflectionTestUtils.setField(configureSolnAndProductWSClientUtility, "httpsProxyPort", "");
		ReflectionTestUtils.setField(configureSolnAndProductWSClientUtility, "httpProxyUser", "");
		ReflectionTestUtils.setField(configureSolnAndProductWSClientUtility, "httpProxyPassword", "");
		ReflectionTestUtils.setField(configureSolnAndProductWSClientUtility, "endPointUrl", "");
		ReflectionTestUtils.setField(configureSolnAndProductWSClientUtility, "httpProxySet", "");
		
		configureSolnAndProductWSClientUtility.init();
		
	}

	
}
