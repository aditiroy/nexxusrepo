package com.att.sales.nexxus.accesspricing.model;
import static org.junit.Assert.assertEquals;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import com.att.sales.nexxus.model.AccessPricingUiRequest;
@ExtendWith(MockitoExtension.class)
public class AccessPricingUiRequestTest {
	
	@Test
	public void testGetterAndSetter() {
	AccessPricingUiRequest request=new AccessPricingUiRequest();
	request.setAction("delete");
	assertEquals("delete", request.getAction());
	
	request.setCountry("US");
	assertEquals("US", request.getCountry());
	
	/*request.setDqId("ETH764");
	assertEquals("ETH764", request.getDqId());*/
	
	request.setQueryType("13");
	assertEquals("13", request.getQueryType());
	
	request.setNxSolutionId(new Long(3l));
	assertEquals(new Long(3l), request.getNxSolutionId());
	
	request.setRequestType("GET");
	assertEquals("GET", request.getRequestType());
	
	request.setUserId("ec006e");
	assertEquals("ec006e", request.getUserId());
	
	}	
}
