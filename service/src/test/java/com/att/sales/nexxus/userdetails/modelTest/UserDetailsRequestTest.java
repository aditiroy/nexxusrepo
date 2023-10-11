package com.att.sales.nexxus.userdetails.modelTest;


import static org.junit.Assert.assertEquals;

import org.junit.jupiter.api.Test;

import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.junit.jupiter.MockitoExtension;

import com.att.sales.nexxus.userdetails.model.UserDetailsRequest;

@ExtendWith(MockitoExtension.class)
public class UserDetailsRequestTest {

	@InjectMocks
	UserDetailsRequest userDetailsRequest;

	@Test
	public void test() {
		userDetailsRequest.setAttuid("attuid");
		assertEquals(new String("attuid"),userDetailsRequest.getAttuid());

		userDetailsRequest.setLeadDesignId(12345L);
		assertEquals(new Long(12345L),userDetailsRequest.getLeadDesignId());

	}

}
