package com.att.sales.nexxus.common;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
@ExtendWith(MockitoExtension.class)
public class AppConfigTest {
	@InjectMocks
	AppConfig test;
	@Test
	public void test() {
		test.applicationListener();
	}

}
