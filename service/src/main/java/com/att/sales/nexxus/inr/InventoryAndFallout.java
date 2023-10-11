package com.att.sales.nexxus.inr;

import com.fasterxml.jackson.databind.JsonNode;

public class InventoryAndFallout {
	private JsonNode inventory;
	private InrFallOutData fallout;
	
	public InventoryAndFallout(JsonNode inventory) {
		this.inventory = inventory;
	}

	public JsonNode getInventory() {
		return inventory;
	}

	public void setInventory(JsonNode inventory) {
		this.inventory = inventory;
	}

	public InrFallOutData getFallout() {
		return fallout;
	}

	public void setFallout(InrFallOutData fallout) {
		this.fallout = fallout;
	}
	
}
