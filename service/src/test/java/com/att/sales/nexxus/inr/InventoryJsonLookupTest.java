package com.att.sales.nexxus.inr;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.Test;

public class InventoryJsonLookupTest {
	@Test
	public void test() {
		Map<String, String> longForm = new HashMap<>();
		Map<String, String> shortForm = new HashMap<>();
		String fallOutReason = "fallOutReason";
		longForm.put("1", "1");
		shortForm.put("1", "1");
		
		InventoryJsonLookup inventoryJsonLookup = new InventoryJsonLookup();
		inventoryJsonLookup.setFallOutReason(fallOutReason);
		inventoryJsonLookup.addShortForm(shortForm);
		inventoryJsonLookup.addLongForm(longForm);
		assertSame(fallOutReason, inventoryJsonLookup.getFallOutReason());
		assertEquals(shortForm, inventoryJsonLookup.getShortForm());
		assertEquals(longForm, inventoryJsonLookup.getLongForm());
		inventoryJsonLookup.setLongForm(longForm);
		inventoryJsonLookup.setShortForm(shortForm);
		assertSame(shortForm, inventoryJsonLookup.getShortForm());
		assertSame(longForm, inventoryJsonLookup.getLongForm());
	}
}
