package com.att.sales.nexxus.model;


import java.io.Serializable;

import org.codehaus.jackson.map.annotate.JsonSerialize;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;


/**
 * The Class AccessSupplierList.
 *
 * @author km017g
 */
@SuppressWarnings("serial")
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonSerialize(include=JsonSerialize.Inclusion.NON_NULL)
public class AccessSupplierList implements Serializable{
	
	/** The access supplier. */
	private AccessSupplierObject accessSupplier;

	/**
	 * Gets the access supplier.
	 *
	 * @return the access supplier
	 */
	public AccessSupplierObject getAccessSupplier() {
		return accessSupplier;
	}

	/**
	 * Sets the access supplier.
	 *
	 * @param accessSupplier the new access supplier
	 */
	public void setAccessSupplier(AccessSupplierObject accessSupplier) {
		this.accessSupplier = accessSupplier;
	}

}
