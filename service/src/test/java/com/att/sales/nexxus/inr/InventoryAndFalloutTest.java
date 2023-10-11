package com.att.sales.nexxus.inr;

import static org.junit.Assert.assertSame;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.JsonNode;
public class InventoryAndFalloutTest {
	@Test
	public void test() {
		JsonNode inventory = null;
		InrFallOutData fallout = new InrFallOutData();
		InventoryAndFallout inventoryAndFallout = new InventoryAndFallout(inventory);
		inventoryAndFallout.setFallout(fallout);
		inventoryAndFallout.setInventory(inventory);
		assertSame(fallout, inventoryAndFallout.getFallout());
		assertSame(inventory, inventoryAndFallout.getInventory());
	}
}
