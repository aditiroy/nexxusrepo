package com.att.sales.nexxus.util;

import static org.junit.Assert.assertEquals;

import org.junit.jupiter.api.Test;


public class NumUtilTest {
	@Test
	public void parseLongTest() {
		String s = "1";
		long def = 0;
		long res = NumUtil.parseLong(s, def);
		assertEquals(1, res);
		
		s = "wrong format";
		res = NumUtil.parseLong(s, def);
		assertEquals(def, res);
	}
	
	@Test
	public void parseLong1Test() {
		Long l = 1L;
		long def = 0;
		long res = NumUtil.parseLong(l, def);
		assertEquals(1, res);
		
		l = null;
		res = NumUtil.parseLong(l, def);
		assertEquals(def, res);
	}
}
