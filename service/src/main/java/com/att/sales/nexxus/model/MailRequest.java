package com.att.sales.nexxus.model;

import com.att.sales.framework.validation.APIFieldProperty;

/**
 * The Class MailRequest.
 */
public class MailRequest {
	
	/** The nx request id. */
	@APIFieldProperty(required=true)
	private Long nxRequestId;
	
	/**
	 * Gets the nx request id.
	 *
	 * @return the nx request id
	 */
	public Long getNxRequestId() {
		return nxRequestId;
	}

	/**
	 * Sets the nx request id.
	 *
	 * @param nxRequestId the new nx request id
	 */
	public void setNxRequestId(Long nxRequestId) {
		this.nxRequestId = nxRequestId;
	}

}
