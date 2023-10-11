package com.att.sales.nexxus.accesspricing.model;

import static org.junit.Assert.assertEquals;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import com.att.sales.nexxus.model.QuoteDetails;
@ExtendWith(MockitoExtension.class)
public class QuoteDetailsTest {
	
@Test	
public void testGetterAndSetter() {
	QuoteDetails details=new QuoteDetails();
	details.setApId(new Long(2l));
	assertEquals(new Long(2l), details.getApId());
	details.setIncludeYn("Y");
	assertEquals("Y", details.getIncludeYn());
	details.setLocationYn("Y");
	assertEquals("Y", details.getLocationYn());
}
}
