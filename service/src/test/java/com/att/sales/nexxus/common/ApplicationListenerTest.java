package com.att.sales.nexxus.common;

import javax.servlet.ServletContextEvent;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
@ExtendWith(MockitoExtension.class)
public class ApplicationListenerTest {
	@InjectMocks
	ApplicationListener test;
	
	@Mock
	ServletContextEvent sce;
	
	@Test
	public void test() {
		test.contextDestroyed(sce);
		test.contextInitialized(sce);
	}

}
