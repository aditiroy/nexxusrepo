package com.att.sales.nexxus.dao.model;


import org.junit.jupiter.api.Test;

import com.att.sales.nexxus.accesspricing.model.AccessPricingResponseWrapper;

public class AccessPricingResponseWrapperTest {

	@Test
	public void testGetterAndSetter() {
		AccessPricingResponseWrapper wrapper=new AccessPricingResponseWrapper();
		wrapper.getAccessPricingResponse();
		wrapper.getTree();
	}

}
