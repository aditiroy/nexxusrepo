package com.att.sales.nexxus.rome.model;

import org.codehaus.jackson.map.annotate.JsonSerialize;

import com.att.sales.framework.model.ServiceResponse;

/**
 * The Class GetBillingChargesResponse.
 */
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class GetBillingChargesResponse extends ServiceResponse {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/** The keyFieldID. */
	private String keyFieldID;

	/** The responseCode. */
	private String responseCode;

	/** The message. */
	private String message;

	/**
	 * @return the keyFieldID
	 */
	public String getKeyFieldID() {
		return keyFieldID;
	}

	/**
	 * @param keyFieldID the keyFieldID to set
	 */
	public void setKeyFieldID(String keyFieldID) {
		this.keyFieldID = keyFieldID;
	}

	/**
	 * @return the responseCode
	 */
	public String getResponseCode() {
		return responseCode;
	}

	/**
	 * @param responseCode the responseCode to set
	 */
	public void setResponseCode(String responseCode) {
		this.responseCode = responseCode;
	}

	/**
	 * @return the message
	 */
	public String getMessage() {
		return message;
	}

	/**
	 * @param message the message to set
	 */
	public void setMessage(String message) {
		this.message = message;
	}
}
