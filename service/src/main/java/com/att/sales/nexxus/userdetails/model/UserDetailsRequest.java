package com.att.sales.nexxus.userdetails.model;
/*
 * 
 * @Authour: Ruchi
 * 
 * File is to accept attuid and leaddesignId field from nexxus UI
 */

import org.codehaus.jackson.map.annotate.JsonSerialize;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * The Class UserDetailsRequest.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class UserDetailsRequest {

	/** The lead design id. */
	private Long leadDesignId;
	
	/** The attuid. */
	private String attuid;
	
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
	 * Gets the attuid.
	 *
	 * @return the attuid
	 */
	public String getAttuid() {
		return attuid;
	}
	
	/**
	 * Sets the attuid.
	 *
	 * @param attuid the new attuid
	 */
	public void setAttuid(String attuid) {
		this.attuid = attuid;
	}
	
	
}
