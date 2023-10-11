package com.att.sales.exampledomainobject.modelTest;
import static org.junit.Assert.assertEquals;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import com.att.sales.nexxus.model.QueoteRequestList;

/*
 * @Author chandan
 */
@ExtendWith(MockitoExtension.class)
public class QueoteRequestListTest {
	@Test
public void testGetterAndSetter() {
		QueoteRequestList requestList=new QueoteRequestList();
		requestList.setCountry("US");
		assertEquals("US", requestList.getCountry());
		requestList.setDqId("ETH7851372");
		assertEquals("ETH7851372", requestList.getDqId());
		requestList.setQueryType("GET");
		assertEquals("GET", requestList.getQueryType());
		requestList.setUserId("ec006e");
		assertEquals("ec006e", requestList.getUserId());
		requestList.setRequestType("13");
		assertEquals("13", requestList.getRequestType());
			
}
}
