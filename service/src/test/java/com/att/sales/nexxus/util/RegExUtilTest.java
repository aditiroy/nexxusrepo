package com.att.sales.nexxus.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.junit.jupiter.api.Test;


public class RegExUtilTest {
	
	@Test
	public void firstMatchYYYY_MM_DDTest() {
		String in = "1111-11-11";
		String res = RegExUtil.firstMatchYYYY_MM_DD(in);
		assertEquals(in, res);
		
		res = RegExUtil.firstMatchYYYY_MM_DD("");
		assertNull(res);
	}
	
	@Test
	public void convertDateToYYYY_MM_DDTest() {
		String in = "Jan 1, 2000 5:00:00 AM";
		String res = RegExUtil.convertDateToYYYY_MM_DD(in);
		assertNotNull(res);
		
		res = RegExUtil.convertDateToYYYY_MM_DD("");
		assertNull(res);
	}
}
