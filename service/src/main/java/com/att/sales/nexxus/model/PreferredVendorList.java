package com.att.sales.nexxus.model;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.map.annotate.JsonSerialize;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;



/**
 * The Class PreferredVendorList.
 *
 * @author km017g
 */
@JsonIgnoreProperties(ignoreUnknown = true) 
@JsonSerialize(include=JsonSerialize.Inclusion.NON_NULL)
public class PreferredVendorList {

	/** The pref vendor name. */
	private String prefVendorName;
	
	/** The pref vendor id. */
	@JsonIgnore
	private String prefVendorId;
	
	/**
	 * Gets the pref vendor name.
	 *
	 * @return the pref vendor name
	 */
	public String getPrefVendorName() {
		return prefVendorName;
	}
	
	/**
	 * Sets the pref vendor name.
	 *
	 * @param prefVendorName the new pref vendor name
	 */
	public void setPrefVendorName(String prefVendorName) {
		this.prefVendorName = prefVendorName;
	}
	
	/**
	 * Gets the pref vendor id.
	 *
	 * @return the pref vendor id
	 */
	public String getPrefVendorId() {
		return prefVendorId;
	}
	
	/**
	 * Sets the pref vendor id.
	 *
	 * @param prefVendorId the new pref vendor id
	 */
	public void setPrefVendorId(String prefVendorId) {
		this.prefVendorId = prefVendorId;
	}
	
	

}
