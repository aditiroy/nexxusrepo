package com.att.sales.nexxus.common;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.att.sales.nexxus.helper.FmoLookUpDataHelper;

@ExtendWith(MockitoExtension.class)
public class ApplicationStartupRunnerTest {
	@InjectMocks
	ApplicationStartupRunner test;
	
	@Mock
	FmoLookUpDataHelper fmoLookUpDataHelper;
	
	@Test
	public void test() {
		try {
			test.run("args");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
