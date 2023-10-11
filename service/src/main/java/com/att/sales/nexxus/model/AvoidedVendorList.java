package com.att.sales.nexxus.model;

import org.codehaus.jackson.map.annotate.JsonSerialize;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;



/**
 * The Class AvoidedVendorList.
 *
 * @author km017g
 */

@JsonIgnoreProperties(ignoreUnknown = true) 
@JsonSerialize(include=JsonSerialize.Inclusion.NON_NULL)
public class AvoidedVendorList {
	
	/** The avoided vendor name. */
	private String avoidedVendorName;
	
	/** The avoided vendor id. */
	@JsonIgnore
	private String avoidedVendorId;
	
	/**
	 * Gets the avoided vendor name.
	 *
	 * @return the avoided vendor name
	 */
	public String getAvoidedVendorName() {
		return avoidedVendorName;
	}
	
	/**
	 * Sets the avoided vendor name.
	 *
	 * @param avoidedVendorName the new avoided vendor name
	 */
	public void setAvoidedVendorName(String avoidedVendorName) {
		this.avoidedVendorName = avoidedVendorName;
	}
	
	/**
	 * Gets the avoided vendor id.
	 *
	 * @return the avoided vendor id
	 */
	public String getAvoidedVendorId() {
		return avoidedVendorId;
	}
	
	/**
	 * Sets the avoided vendor id.
	 *
	 * @param avoidedVendorId the new avoided vendor id
	 */
	public void setAvoidedVendorId(String avoidedVendorId) {
		this.avoidedVendorId = avoidedVendorId;
	}
	
	

}
