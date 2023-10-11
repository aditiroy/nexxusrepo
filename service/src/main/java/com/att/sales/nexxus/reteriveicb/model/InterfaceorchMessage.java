package com.att.sales.nexxus.reteriveicb.model;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.map.annotate.JsonSerialize;


/**
 * The Class InterfaceorchMessage.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class InterfaceorchMessage {
	
	/** The type of msg. */
	private String typeOfMsg;
	
	/** The status code. */
	private String statusCode;
	
	/** The status message. */
	private String statusMessage;
	
	/**
	 * Gets the type of msg.
	 *
	 * @return the type of msg
	 */
	public String getTypeOfMsg() {
		return typeOfMsg;
	}
	
	/**
	 * Sets the type of msg.
	 *
	 * @param typeOfMsg the new type of msg
	 */
	public void setTypeOfMsg(String typeOfMsg) {
		this.typeOfMsg = typeOfMsg;
	}
	
	/**
	 * Gets the status code.
	 *
	 * @return the status code
	 */
	public String getStatusCode() {
		return statusCode;
	}
	
	/**
	 * Sets the status code.
	 *
	 * @param statusCode the new status code
	 */
	public void setStatusCode(String statusCode) {
		this.statusCode = statusCode;
	}
	
	/**
	 * Gets the status message.
	 *
	 * @return the status message
	 */
	public String getStatusMessage() {
		return statusMessage;
	}
	
	/**
	 * Sets the status message.
	 *
	 * @param statusMessage the new status message
	 */
	public void setStatusMessage(String statusMessage) {
		this.statusMessage = statusMessage;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "InterfaceorchMessage [typeOfMsg:"
				+ typeOfMsg + ", statusCode=" + statusCode + ", statusMessage="+statusMessage;
	}
}
