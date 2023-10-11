package com.att.sales.nexxus.userdetails.model;

import java.io.Serializable;

/**
*
*
* @author aa316k
*         
*/

import org.codehaus.jackson.map.annotate.JsonSerialize;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * The Class ConsumerDetail.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonSerialize(include=JsonSerialize.Inclusion.NON_NULL)
public class ConsumerDetail implements Serializable {
	
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;
	
	/** The attu id. */
	private String attuId;
	
	/** The first name. */
	private String firstName;
	
	/** The last name. */
	private String lastName;
	
	/** The manager attuid. */
	private String managerAttuid;
	
	/** The email id. */
	private String emailId;
	
	/** The nx id. */
	private Long nxId;

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
	
	/**
	 * Gets the manager attuid.
	 *
	 * @return the manager attuid
	 */
	public String getManagerAttuid() {
		return managerAttuid;
	}
	
	/**
	 * Sets the manager attuid.
	 *
	 * @param managerAttuid the new manager attuid
	 */
	public void setManagerAttuid(String managerAttuid) {
		this.managerAttuid = managerAttuid;
	}
	
	/**
	 * Gets the email id.
	 *
	 * @return the email id
	 */
	public String getEmailId() {
		return emailId;
	}
	
	/**
	 * Sets the email id.
	 *
	 * @param emailId the new email id
	 */
	public void setEmailId(String emailId) {
		this.emailId = emailId;
	}
	
	/**
	 * Gets the attu id.
	 *
	 * @return the attu id
	 */
	public String getAttuId() {
		return attuId;
	}
	
	/**
	 * Sets the attu id.
	 *
	 * @param attuId the new attu id
	 */
	public void setAttuId(String attuId) {
		this.attuId = attuId;
	}
	
	/**
	 * Gets the nx id.
	 *
	 * @return the nx id
	 */
	public long getNxId() {
		return nxId;
	}
	
	/**
	 * Sets the nx id.
	 *
	 * @param nxId the new nx id
	 */
	public void setNxId(long nxId) {
		this.nxId = nxId;
	}
}
