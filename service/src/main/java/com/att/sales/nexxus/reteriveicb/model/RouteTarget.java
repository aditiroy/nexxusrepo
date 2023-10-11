package com.att.sales.nexxus.reteriveicb.model;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
/*
 * @Author: Akash Arya
 * 
 * 
 */
import org.codehaus.jackson.map.annotate.JsonSerialize;


/**
 * The Class RouteTarget.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class RouteTarget {
	
	/** The customer route target id. */
	private Long customerRouteTargetId;
	
	/** The customer route target val. */
	private String customerRouteTargetVal;
	
	/** The action. */
	private String action;

	/**
	 * Gets the customer route target id.
	 *
	 * @return the customer route target id
	 */
	public Long getCustomerRouteTargetId() {
		return customerRouteTargetId;
	}

	/**
	 * Sets the customer route target id.
	 *
	 * @param customerRouteTargetId the new customer route target id
	 */
	public void setCustomerRouteTargetId(Long customerRouteTargetId) {
		this.customerRouteTargetId = customerRouteTargetId;
	}

	/**
	 * Gets the customer route target val.
	 *
	 * @return the customer route target val
	 */
	public String getCustomerRouteTargetVal() {
		return customerRouteTargetVal;
	}

	/**
	 * Sets the customer route target val.
	 *
	 * @param customerRouteTargetVal the new customer route target val
	 */
	public void setCustomerRouteTargetVal(String customerRouteTargetVal) {
		this.customerRouteTargetVal = customerRouteTargetVal;
	}

	/**
	 * Gets the action.
	 *
	 * @return the action
	 */
	public String getAction() {
		return action;
	}

	/**
	 * Sets the action.
	 *
	 * @param action the new action
	 */
	public void setAction(String action) {
		this.action = action;
	}
}
