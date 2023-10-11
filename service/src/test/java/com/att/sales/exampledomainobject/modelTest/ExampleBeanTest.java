package com.att.sales.exampledomainobject.modelTest;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import com.att.sales.framework.model.ServiceMetaData;
import com.att.sales.nexxus.model.ExampleBean;


@ExtendWith(MockitoExtension.class)
public class ExampleBeanTest {

	
	//@InjectMocks
	ExampleBean exampleBean;
	
	@BeforeEach
	public void setup() {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put(ServiceMetaData.VERSION, "1.0");
		map.put(ServiceMetaData.METHOD, "GET");
		map.put(ServiceMetaData.URI, "/services/hello1");
		map.put(ServiceMetaData.REST_REQUEST_START_TIME, System.currentTimeMillis());
		map.put(ServiceMetaData.REST_REQUEST_START_TIME, System.currentTimeMillis());
		map.put(ServiceMetaData.OFFER, "AVPN");
		map.put(ServiceMetaData.XCLIENTID, "ADOPT");

		ServiceMetaData.add(map);

	}
	
	@Test
	public void test() {
		//fail("Not yet implemented");
		
	}

}
