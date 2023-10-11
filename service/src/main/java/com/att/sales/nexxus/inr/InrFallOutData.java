package com.att.sales.nexxus.inr;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

/**
 * The Class InrFallOutData.
 */
public class InrFallOutData {
	
	/** The beid. */
	private Set<String> beid;
	
	/** The query parameter. */
	private Set<Map<String, String>> queryParameter;
	
	private List<InventoryJsonLookup> inventoryJsonLookups;
	
	/** The ai response. */
	private String aiResponse = "true";

	/**
	 * Gets the beid.
	 *
	 * @return the beid
	 */
	public Set<String> getBeid() {
		return beid;
	}

	/**
	 * Sets the beid.
	 *
	 * @param beid the new beid
	 */
	public void setBeid(Set<String> beid) {
		this.beid = beid;
	}

	/**
	 * Gets the query parameter.
	 *
	 * @return the query parameter
	 */
	public Set<Map<String, String>> getQueryParameter() {
		return queryParameter;
	}

	/**
	 * Sets the query parameter.
	 *
	 * @param queryParameter the query parameter
	 */
	public void setQueryParameter(Set<Map<String, String>> queryParameter) {
		this.queryParameter = queryParameter;
	}
	
	public List<InventoryJsonLookup> getInventoryJsonLookups() {
		return inventoryJsonLookups;
	}

	/**
	 * Adds the.
	 *
	 * @param parameter the parameter
	 */
	public void add(Map<String, String> parameter) {
		for (Entry<String, String> e : parameter.entrySet()) {
			if ("beid".equals(e.getKey())) {
				addBeid(e.getValue());
			}
			if ("aiResponse".equals(e.getKey()) && "false".equals(e.getValue())) {
				aiResponse = "false";
			}
		}
		addQueryParameter(parameter);
	}
	
	/**
	 * Checks for value.
	 *
	 * @return true, if successful
	 */
	public boolean hasValue() {
		return queryParameter != null || beid != null || inventoryJsonLookups != null;
	}

	/**
	 * Adds the query parameter.
	 *
	 * @param parameter the parameter
	 */
	protected void addQueryParameter(Map<String, String> parameter) {
		if (queryParameter == null) {
			queryParameter = new HashSet<>();
		}
		queryParameter.add(parameter);
	}
	
	protected void addInventoryJsonLookups(InventoryJsonLookup parameter) {
		if (inventoryJsonLookups == null) {
			inventoryJsonLookups = new LinkedList<>();
		}
		inventoryJsonLookups.add(parameter);
	}

	/**
	 * Adds the beid.
	 *
	 * @param value the value
	 */
	protected void addBeid(String value) {
		if (beid == null) {
			beid = new HashSet<>();
		}
		beid.add(value);
	}

	/**
	 * Gets the ai response.
	 *
	 * @return the ai response
	 */
	public String getAiResponse() {
		return aiResponse;
	}
}
