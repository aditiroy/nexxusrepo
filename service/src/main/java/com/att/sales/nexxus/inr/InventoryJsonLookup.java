package com.att.sales.nexxus.inr;

import java.util.HashMap;
import java.util.Map;

public class InventoryJsonLookup {
	private Map<String, String> longForm;
	private Map<String, String> shortForm;
	private String fallOutReason;
	
	public String getFallOutReason() {
		return fallOutReason;
	}
	public void setFallOutReason(String fallOutReason) {
		this.fallOutReason = fallOutReason;
	}
	public Map<String, String> getLongForm() {
		return longForm;
	}
	public void setLongForm(Map<String, String> longForm) {
		this.longForm = longForm;
	}
	public Map<String, String> getShortForm() {
		return shortForm;
	}
	public void setShortForm(Map<String, String> shortForm) {
		this.shortForm = shortForm;
	}
	public void addShortForm(Map<String, String> shortForm) {
		if (this.shortForm == null) {
			this.shortForm = new HashMap<>();
		}
		shortForm.entrySet().forEach(e -> this.shortForm.put(e.getKey(), e.getValue()));
	}
	public void addLongForm(Map<String, String> longForm) {
		if (this.longForm == null) {
			this.longForm = new HashMap<>();
		}
		longForm.entrySet().forEach(e -> this.longForm.put(e.getKey(), e.getValue()));
	}
}
