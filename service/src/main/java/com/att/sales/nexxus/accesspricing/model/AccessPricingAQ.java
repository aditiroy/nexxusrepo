package com.att.sales.nexxus.accesspricing.model;

import java.util.List;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.map.annotate.JsonSerialize;



/**
 * The Class AccessPricingAQ.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class AccessPricingAQ {
	
	/** The access price UI details. */
	private List<AccessPriceUIdetails> accessPriceUIDetails;

	/**
	 * Gets the access price UI details.
	 *
	 * @return the access price UI details
	 */
	public List<AccessPriceUIdetails> getAccessPriceUIDetails() {
		return accessPriceUIDetails;
	}

	/**
	 * Sets the access price UI details.
	 *
	 * @param accessPriceUIDetails the new access price UI details
	 */
	public void setAccessPriceUIDetails(List<AccessPriceUIdetails> accessPriceUIDetails) {
		this.accessPriceUIDetails = accessPriceUIDetails;
	}	
}
