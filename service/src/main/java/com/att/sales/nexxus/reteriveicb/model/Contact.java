package com.att.sales.nexxus.reteriveicb.model;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
/**
 * @author Ruchi
 */
import org.codehaus.jackson.map.annotate.JsonSerialize;




/**
 * The Class Contact.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class Contact {

	/** The contact type. */
	private String contactType;

	/** The first name. */
	private String firstName;

	/** The last name. */
	private String lastName;

	/** The work phone. */
	private String workPhone;

	/** The email. */
	private String email;

	/** The cell phone number. */
	private String cellPhoneNumber;

	/** The address 1. */
	private String address1;

	/** The address 2. */
	private String address2;

	/** The room. */
	private String room;

	/** The floor. */
	private String floor;

	/** The building. */
	private String building;

	/** The city. */
	private String city;

	/** The state. */
	private String state;

	/** The zip code. */
	private String zipCode;

	/** The iso country code. */
	private String isoCountryCode;

	private String company;

	private String faxNumber;

	private String country;

	/**
	 * Gets the contact type.
	 *
	 * @return the contact type
	 */
	public String getContactType() {
		return contactType;
	}

	/**
	 * Sets the contact type.
	 *
	 * @param contactType the new contact type
	 */
	public void setContactType(String contactType) {
		this.contactType = contactType;
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

	/**
	 * Gets the work phone.
	 *
	 * @return the work phone
	 */
	public String getWorkPhone() {
		return workPhone;
	}

	/**
	 * Sets the work phone.
	 *
	 * @param workPhone the new work phone
	 */
	public void setWorkPhone(String workPhone) {
		this.workPhone = workPhone;
	}

	/**
	 * Gets the email.
	 *
	 * @return the email
	 */
	public String getEmail() {
		return email;
	}

	/**
	 * Sets the email.
	 *
	 * @param email the new email
	 */
	public void setEmail(String email) {
		this.email = email;
	}

	/**
	 * Gets the cell phone number.
	 *
	 * @return the cell phone number
	 */
	public String getCellPhoneNumber() {
		return cellPhoneNumber;
	}

	/**
	 * Sets the cell phone number.
	 *
	 * @param cellPhoneNumber the new cell phone number
	 */
	public void setCellPhoneNumber(String cellPhoneNumber) {
		this.cellPhoneNumber = cellPhoneNumber;
	}

	/**
	 * Gets the address 1.
	 *
	 * @return the address 1
	 */
	public String getAddress1() {
		return address1;
	}

	/**
	 * Sets the address 1.
	 *
	 * @param address1 the new address 1
	 */
	public void setAddress1(String address1) {
		this.address1 = address1;
	}

	/**
	 * Gets the address 2.
	 *
	 * @return the address 2
	 */
	public String getAddress2() {
		return address2;
	}

	/**
	 * Sets the address 2.
	 *
	 * @param address2 the new address 2
	 */
	public void setAddress2(String address2) {
		this.address2 = address2;
	}

	/**
	 * Gets the room.
	 *
	 * @return the room
	 */
	public String getRoom() {
		return room;
	}

	/**
	 * Sets the room.
	 *
	 * @param room the new room
	 */
	public void setRoom(String room) {
		this.room = room;
	}

	/**
	 * Gets the floor.
	 *
	 * @return the floor
	 */
	public String getFloor() {
		return floor;
	}

	/**
	 * Sets the floor.
	 *
	 * @param floor the new floor
	 */
	public void setFloor(String floor) {
		this.floor = floor;
	}

	/**
	 * Gets the building.
	 *
	 * @return the building
	 */
	public String getBuilding() {
		return building;
	}

	/**
	 * Sets the building.
	 *
	 * @param building the new building
	 */
	public void setBuilding(String building) {
		this.building = building;
	}

	/**
	 * Gets the city.
	 *
	 * @return the city
	 */
	public String getCity() {
		return city;
	}

	/**
	 * Sets the city.
	 *
	 * @param city the new city
	 */
	public void setCity(String city) {
		this.city = city;
	}

	/**
	 * Gets the state.
	 *
	 * @return the state
	 */
	public String getState() {
		return state;
	}

	/**
	 * Sets the state.
	 *
	 * @param state the new state
	 */
	public void setState(String state) {
		this.state = state;
	}

	/**
	 * Gets the zip code.
	 *
	 * @return the zip code
	 */
	public String getZipCode() {
		return zipCode;
	}

	/**
	 * Sets the zip code.
	 *
	 * @param zipCode the new zip code
	 */
	public void setZipCode(String zipCode) {
		this.zipCode = zipCode;
	}

	/**
	 * Gets the iso country code.
	 *
	 * @return the iso country code
	 */
	public String getIsoCountryCode() {
		return isoCountryCode;
	}

	/**
	 * Sets the iso country code.
	 *
	 * @param isoCountryCode the new iso country code
	 */
	public void setIsoCountryCode(String isoCountryCode) {
		this.isoCountryCode = isoCountryCode;
	}

	public String getCompany() {
		return company;
	}

	public void setCompany(String company) {
		this.company = company;
	}

	public String getFaxNumber() {
		return faxNumber;
	}

	public void setFaxNumber(String faxNumber) {
		this.faxNumber = faxNumber;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

}
