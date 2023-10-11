package com.att.sales.nexxus.ws.utility;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
public class GetOptyInfoWSClientUtilityTest {
	
	@InjectMocks
	GetOptyInfoWSClientUtility getOptyInfoWSClientUtility;
	
	@Test
	public void testInit() {
		ReflectionTestUtils.setField(getOptyInfoWSClientUtility, "endPointUrl", "");
		ReflectionTestUtils.setField(getOptyInfoWSClientUtility, "contextPath", "");
		ReflectionTestUtils.setField(getOptyInfoWSClientUtility, "username", "");
		ReflectionTestUtils.setField(getOptyInfoWSClientUtility, "password", "");
		getOptyInfoWSClientUtility.init();
	}

}
