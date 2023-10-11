package com.att.sales.nexxus.model;

import java.io.Serializable;
import java.util.List;

import com.att.sales.framework.validation.APIFieldProperty;

/**
 * The Class AccesPricingFailedDqId.
 *
 * @author Rudresh Waladaunki
 */
public class AccesPricingFailedDqId implements Serializable {
	
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;
	
	/** The failed dq id. */
	@APIFieldProperty(required=true)
	private List<String> failedDqId;


	/**
	 * Gets the failed dq id.
	 *
	 * @return the failedDqId
	 */
	public List<String> getFailedDqId() {
		return failedDqId;
	}
	

	/**
	 * Sets the failed dq id.
	 *
	 * @param failedDqId the failedDqId to set
	 */
	public void setFailedDqId(List<String> failedDqId) {
		this.failedDqId = failedDqId;
	}
	

}
