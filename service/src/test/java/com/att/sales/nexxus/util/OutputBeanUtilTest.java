package com.att.sales.nexxus.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.jupiter.api.Test;


public class OutputBeanUtilTest {

	@Test
	public void getLocationTest() {
		assertNull(OutputBeanUtil.getLocation(null));
		assertEquals("2 Alaska", OutputBeanUtil.getLocation("AK"));
		assertEquals("2 Alaska", OutputBeanUtil.getLocation("Alaska"));
		assertEquals("3 Hawaii", OutputBeanUtil.getLocation("HI"));
		assertEquals("3 Hawaii", OutputBeanUtil.getLocation("Hawaii"));
		assertEquals("4 USVI", OutputBeanUtil.getLocation("VI"));
		assertEquals("4 USVI", OutputBeanUtil.getLocation("USVI"));
		assertEquals("4 USVI", OutputBeanUtil.getLocation("US Vergin Islands"));
		assertEquals("5 Puerto Rico", OutputBeanUtil.getLocation("PR"));
		assertEquals("5 Puerto Rico", OutputBeanUtil.getLocation("Puerto Rico"));
		assertEquals("1 Lower 48", OutputBeanUtil.getLocation("NJ"));
	}
}
