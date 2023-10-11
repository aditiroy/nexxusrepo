package com.att.sales.nexxus.inr;

import com.fasterxml.jackson.databind.JsonNode;

public class JsonNodeWithName {
	private String name;
	private JsonNode node;
	
	public JsonNodeWithName(String name, JsonNode node) {
		this.name = name;
		this.node = node;
	}

	public String getName() {
		return name;
	}

	public JsonNode getNode() {
		return node;
	}
}
