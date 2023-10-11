package com.att.sales.nexxus.model;

import java.util.ArrayList;
import java.util.List;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.map.annotate.JsonSerialize;

/**
 * The Class AccessPricingUiRequestList.
 */
@SuppressWarnings("serial")
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonSerialize(include=JsonSerialize.Inclusion.NON_NULL)
public class AccessPricingUiRequestList {
	
	/** The request list. */
	private List<AccessPricingUiRequest> requestList =new ArrayList<>();
	
	/**
	 * Gets the request list.
	 *
	 * @return the request list
	 */
	@JsonProperty("requestList")
	public List<AccessPricingUiRequest> getRequestList() {
		return requestList;
	}
	
	/**
	 * Sets the request list.
	 *
	 * @param requestList the new request list
	 */
	public void setRequestList(List<AccessPricingUiRequest> requestList) {
		this.requestList = requestList;
	}

}
