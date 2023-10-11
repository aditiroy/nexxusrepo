package com.att.sales.nexxus.userdetails.model;

import java.io.Serializable;
import java.util.List;

import org.codehaus.jackson.map.annotate.JsonSerialize;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;


/*
 * 
 * @Authour: Ruchi
 * 
 * 
 */


/**
 * The Class UserDetails.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonSerialize(include=JsonSerialize.Inclusion.NON_NULL)
public class UserDetails implements Serializable {

	
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;
	
	/** The attuid. */
	//private Long userId; //attuid
	private String attuid;
	//private List<UserRole> userRole;
	/** The first name. */
	//private String jobTitle;
	private String firstName;
	
	/** The user role. */
	private List<UserRole> userRole;
	
	/** The middle name. */
	private String middleName;
	
	/** The last name. */
	private String lastName;
	
	/** The phone. */
	private String phone;
	
	/** The cellular. */
	private String cellular;
	//private String addressLine1;
	//private String addressLine2;
	//private String city;
	//private String stateCode;
	//private String zipCode;
	/** The manager attuid. */
	//private String countryCd;
	private String managerAttuid;
	//private String company;
	/** The email id. */
	//private String hrid;
	private String emailId;
	 
	
	 
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
	 * Gets the middle name.
	 *
	 * @return the middle name
	 */
	public String getMiddleName() {
		return middleName;
	}
	
	/**
	 * Sets the middle name.
	 *
	 * @param middleName the new middle name
	 */
	public void setMiddleName(String middleName) {
		this.middleName = middleName;
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
	 * Gets the phone.
	 *
	 * @return the phone
	 */
	public String getPhone() {
		return phone;
	}
	
	/**
	 * Sets the phone.
	 *
	 * @param phone the new phone
	 */
	public void setPhone(String phone) {
		this.phone = phone;
	}
	
	/**
	 * Gets the cellular.
	 *
	 * @return the cellular
	 */
	public String getCellular() {
		return cellular;
	}
	
	/**
	 * Sets the cellular.
	 *
	 * @param cellular the new cellular
	 */
	public void setCellular(String cellular) {
		this.cellular = cellular;
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
	 * Gets the user role.
	 *
	 * @return the user role
	 */
	public List<UserRole> getUserRole() {
		return userRole;
	}
	
	/**
	 * Sets the user role.
	 *
	 * @param userRole the new user role
	 */
	public void setUserRole(List<UserRole> userRole) {
		this.userRole = userRole;
	}
	
	
}
