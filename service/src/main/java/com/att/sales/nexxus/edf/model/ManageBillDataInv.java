package com.att.sales.nexxus.edf.model;

import java.util.Objects;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.map.annotate.JsonSerialize;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

/**
 * The Class ManageBillDataInv.
 */
/*
 * @Author chandan
 */
@SuppressWarnings("serial")
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
@JsonInclude(Include.NON_NULL)
public class ManageBillDataInv {

	/** The manage billing price inventory data request. */
	private ManageBillingPriceInventoryDataRequest manageBillingPriceInventoryDataRequest;

	/**
	 * Gets the manage billing price inventory data request.
	 *
	 * @return the manage billing price inventory data request
	 */
	@JsonProperty("getManageBillingPriceInventoryDataRequest")
	public ManageBillingPriceInventoryDataRequest getManageBillingPriceInventoryDataRequest() {

		if (Objects.isNull(manageBillingPriceInventoryDataRequest)) {
			manageBillingPriceInventoryDataRequest = new ManageBillingPriceInventoryDataRequest();
		}
		return manageBillingPriceInventoryDataRequest;

	}

	/**
	 * Sets the manage billing price inventory data request.
	 *
	 * @param manageBillingPriceInventoryDataRequest the new manage billing price inventory data request
	 */
	public void setManageBillingPriceInventoryDataRequest(
			ManageBillingPriceInventoryDataRequest manageBillingPriceInventoryDataRequest) {
		this.manageBillingPriceInventoryDataRequest = manageBillingPriceInventoryDataRequest;
	}

}
