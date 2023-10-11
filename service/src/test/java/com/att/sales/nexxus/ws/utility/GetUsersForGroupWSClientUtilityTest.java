package com.att.sales.nexxus.ws.utility;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
public class GetUsersForGroupWSClientUtilityTest {
	
	@InjectMocks
	GetUsersForGroupWSClientUtility getUsersForGroupWSClientUtility;
	
	@Test
	public void testInit() {
		ReflectionTestUtils.setField(getUsersForGroupWSClientUtility, "endPointUrl", "");
		ReflectionTestUtils.setField(getUsersForGroupWSClientUtility, "contextPath", "");
		ReflectionTestUtils.setField(getUsersForGroupWSClientUtility, "username", "");
		ReflectionTestUtils.setField(getUsersForGroupWSClientUtility, "password", "");
		getUsersForGroupWSClientUtility.init();
	}
}
