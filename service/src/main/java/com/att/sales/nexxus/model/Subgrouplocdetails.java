package com.att.sales.nexxus.model;


import java.io.Serializable;

import org.codehaus.jackson.map.annotate.JsonSerialize;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;


/**
 * The Class Subgrouplocdetails.
 *
 * @author km017g
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonSerialize(include=JsonSerialize.Inclusion.NON_NULL)
public class Subgrouplocdetails implements Serializable{
	
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;
	
	/** The sub grouploc id. */
	private int subGrouplocId;
	
	/** The street address. */
	private String streetAddress;
	
	/** The house number. */
	private String houseNumber;
	
	/** The street direction. */
	private String streetDirection;
	
	/** The street name. */
	private String streetName;
	
	/** The city. */
	private String city;
	
	/** The state. */
	private String state;
	
	/** The postal code. */
	private String postalCode;
	
	/** The county. */
	private String county;
	
	/** The country. */
	private String country;
	
	/** The unit type. */
	private String unitType;
	
	/** The unit value. */
	private String unitValue;
	
	/** The building. */
	private String building;
	
	/** The floor. */
	private String floor;
	
	/** The room. */
	private String room;
	
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
	 * Gets the street address.
	 *
	 * @return the street address
	 */
	public String getStreetAddress() {
		return streetAddress;
	}
	
	/**
	 * Sets the street address.
	 *
	 * @param streetAddress the new street address
	 */
	public void setStreetAddress(String streetAddress) {
		this.streetAddress = streetAddress;
	}
	
	/**
	 * Gets the sub grouploc id.
	 *
	 * @return the sub grouploc id
	 */
	public int getSubGrouplocId() {
		return subGrouplocId;
	}
	
	/**
	 * Sets the sub grouploc id.
	 *
	 * @param subGrouplocId the new sub grouploc id
	 */
	public void setSubGrouplocId(int subGrouplocId) {
		this.subGrouplocId = subGrouplocId;
	}
	
	/**
	 * Gets the house number.
	 *
	 * @return the house number
	 */
	public String getHouseNumber() {
		return houseNumber;
	}
	
	/**
	 * Sets the house number.
	 *
	 * @param houseNumber the new house number
	 */
	public void setHouseNumber(String houseNumber) {
		this.houseNumber = houseNumber;
	}
	
	/**
	 * Gets the street direction.
	 *
	 * @return the street direction
	 */
	public String getStreetDirection() {
		return streetDirection;
	}
	
	/**
	 * Sets the street direction.
	 *
	 * @param streetDirection the new street direction
	 */
	public void setStreetDirection(String streetDirection) {
		this.streetDirection = streetDirection;
	}
	
	/**
	 * Gets the street name.
	 *
	 * @return the street name
	 */
	public String getStreetName() {
		return streetName;
	}
	
	/**
	 * Sets the street name.
	 *
	 * @param streetName the new street name
	 */
	public void setStreetName(String streetName) {
		this.streetName = streetName;
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
	 * Gets the postal code.
	 *
	 * @return the postal code
	 */
	public String getPostalCode() {
		return postalCode;
	}
	
	/**
	 * Sets the postal code.
	 *
	 * @param postalCode the new postal code
	 */
	public void setPostalCode(String postalCode) {
		this.postalCode = postalCode;
	}
	
	/**
	 * Gets the county.
	 *
	 * @return the county
	 */
	public String getCounty() {
		return county;
	}
	
	/**
	 * Sets the county.
	 *
	 * @param county the new county
	 */
	public void setCounty(String county) {
		this.county = county;
	}
	
	/**
	 * Gets the country.
	 *
	 * @return the country
	 */
	public String getCountry() {
		return country;
	}
	
	/**
	 * Sets the country.
	 *
	 * @param country the new country
	 */
	public void setCountry(String country) {
		this.country = country;
	}
	
	/**
	 * Gets the unit type.
	 *
	 * @return the unit type
	 */
	public String getUnitType() {
		return unitType;
	}
	
	/**
	 * Sets the unit type.
	 *
	 * @param unitType the new unit type
	 */
	public void setUnitType(String unitType) {
		this.unitType = unitType;
	}
	
	/**
	 * Gets the unit value.
	 *
	 * @return the unit value
	 */
	public String getUnitValue() {
		return unitValue;
	}
	
	/**
	 * Sets the unit value.
	 *
	 * @param unitValue the new unit value
	 */
	public void setUnitValue(String unitValue) {
		this.unitValue = unitValue;
	}
	
	
}
