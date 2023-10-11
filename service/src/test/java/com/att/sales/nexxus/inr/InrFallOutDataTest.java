package com.att.sales.nexxus.inr;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.junit.jupiter.api.Test;
public class InrFallOutDataTest {
	
	@Test
	public void setGetTest() {
		InrFallOutData inrFallOutData = new InrFallOutData();
		Set<String> beid = new HashSet<>();
		Set<Map<String, String>> queryParameter = new HashSet<>();
		inrFallOutData.setBeid(beid);
		inrFallOutData.setQueryParameter(queryParameter);
		assertSame(beid, inrFallOutData.getBeid());
		assertSame(queryParameter, inrFallOutData.getQueryParameter());
	}
	
	@Test
	public void addTest() {
		InrFallOutData inrFallOutData = new InrFallOutData();
		Map<String, String> parameter = new HashMap<>();
		parameter.put("beid", "beid");
		parameter.put("country", "country");
		parameter.put("aiResponse", "false");
		inrFallOutData.add(parameter);
		assertTrue(inrFallOutData.hasValue());
		assertEquals("false", inrFallOutData.getAiResponse());
		InventoryJsonLookup inventoryJsonLookup = new InventoryJsonLookup();
		inrFallOutData.addInventoryJsonLookups(inventoryJsonLookup);
		assertNotNull(inrFallOutData.getInventoryJsonLookups());
	}
}
