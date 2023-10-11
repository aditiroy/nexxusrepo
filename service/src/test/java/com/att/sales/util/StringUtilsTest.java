package com.att.sales.util;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class StringUtilsTest {
	
	StringUtils test;
	@Test
	public void test() {
		StringUtils.createBasicEncoding("rd","rd");
	}

}
