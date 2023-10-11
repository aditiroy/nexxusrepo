package com.att.sales.nexxus.accesspricing.model;

import com.fasterxml.jackson.databind.JsonNode;

/**
 * The Class AccessPricingResponseWrapper.
 */
public class AccessPricingResponseWrapper {
	
	/** The access pricing response. */
	private AccessPricingResponse accessPricingResponse;
	
	/** The tree. */
	private JsonNode tree;
	
	/**
	 * Instantiates a new access pricing response wrapper.
	 */
	public AccessPricingResponseWrapper() {
		//default constructor is added to pass pojo validate
	}

	/**
	 * Instantiates a new access pricing response wrapper.
	 *
	 * @param accessPricingResponse the access pricing response
	 * @param tree the tree
	 */
	public AccessPricingResponseWrapper(AccessPricingResponse accessPricingResponse, JsonNode tree) {
		super();
		this.accessPricingResponse = accessPricingResponse;
		this.tree = tree;
	}

	/**
	 * Gets the access pricing response.
	 *
	 * @return the access pricing response
	 */
	public AccessPricingResponse getAccessPricingResponse() {
		return accessPricingResponse;
	}

	/**
	 * Gets the tree.
	 *
	 * @return the tree
	 */
	public JsonNode getTree() {
		return tree;
	}
}
