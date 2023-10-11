package com.att.sales.nexxus.userdetails.model;

import java.io.Serializable;
import java.util.List;

import org.codehaus.jackson.map.annotate.JsonSerialize;

import com.att.sales.framework.model.ServiceResponse;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/*
 * 
 * @Authour: Ruchi
 * 
 * File is to accept attuid and leaddesignId field from nexxus UI
 */


/**
 * The Class UserDetailsResponse.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonSerialize(include=JsonSerialize.Inclusion.NON_NULL)
public class UserDetailsResponse extends ServiceResponse{

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;
	
	/** The lead design id. */
	private Long leadDesignId;
	
	/** The user details. */
	private List<UserDetails> userDetails; 
	
	/** The status message. */
	private String statusMessage;
	
	/** The status code. */
	private String statusCode;
	
	/**
	 * Gets the lead design id.
	 *
	 * @return the lead design id
	 */
	public Long getLeadDesignId() {
		return leadDesignId;
	}
	
	/**
	 * Sets the lead design id.
	 *
	 * @param leadDesignId the new lead design id
	 */
	public void setLeadDesignId(Long leadDesignId) {
		this.leadDesignId = leadDesignId;
	}
	
	/**
	 * Gets the user details.
	 *
	 * @return the user details
	 */
	public List<UserDetails> getUserDetails() {
		return userDetails;
	}
	
	/**
	 * Sets the user details.
	 *
	 * @param userDetails the new user details
	 */
	public void setUserDetails(List<UserDetails> userDetails) {
		this.userDetails = userDetails;
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
}
