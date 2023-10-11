package com.att.sales.nexxus.accesspricing.model;

import org.codehaus.jackson.map.annotate.JsonSerialize;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * The Class TransmitDesignDataResponse.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class TransmitDesignDataResponse {

	/** The nexxus request id. */
	private Long nexxusRequestId;
	
	/** The nx solution id. */
	private Long nxSolutionId;

	/** The attuid. */
	private String attuid;

	/** The nss status. */
	private String nssStatus;

	/** The first name. */
	private String firstName;

	/** The last name. */
	private String lastName;

	/**
	 * Gets the nexxus request id.
	 *
	 * @return the nexxus request id
	 */
	public Long getNexxusRequestId() {
		return nexxusRequestId;
	}

	/**
	 * Sets the nexxus request id.
	 *
	 * @param nexxusRequestId the new nexxus request id
	 */
	public void setNexxusRequestId(Long nexxusRequestId) {
		this.nexxusRequestId = nexxusRequestId;
	}

	/**
	 * Gets the nx solution id.
	 *
	 * @return the nx solution id
	 */
	public Long getNxSolutionId() {
		return nxSolutionId;
	}

	/**
	 * Sets the nx solution id.
	 *
	 * @param nxSolutionId the new nx solution id
	 */
	public void setNxSolutionId(Long nxSolutionId) {
		this.nxSolutionId = nxSolutionId;
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

	/**
	 * Gets the nss status.
	 *
	 * @return the nss status
	 */
	public String getNssStatus() {
		return nssStatus;
	}

	/**
	 * Sets the nss status.
	 *
	 * @param nssStatus the new nss status
	 */
	public void setNssStatus(String nssStatus) {
		this.nssStatus = nssStatus;
	}

	/**
	 * Gets the first name.
	 *
	 * @return the first name
	 */
	public String getFirstName() {
		return firstName;
	}

	/**
	 * Sets the first name.
	 *
	 * @param firstName the new first name
	 */
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	/**
	 * Gets the last name.
	 *
	 * @return the last name
	 */
	public String getLastName() {
		return lastName;
	}

	/**
	 * Sets the last name.
	 *
	 * @param lastName the new last name
	 */
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	
	

}
