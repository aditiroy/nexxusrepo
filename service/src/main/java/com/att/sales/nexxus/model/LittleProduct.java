/**
 * 
 */
package com.att.sales.nexxus.model;

import java.io.Serializable;

/**
 * The Class LittleProduct.
 *
 * @author RudreshWaladaunki
 */
public class LittleProduct implements Serializable{

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;
	
	/** The little product id. */
	private Long littleProductId;
	
	/** The description. */
	private String description;
	
	/** The active yn. */
	private String activeYn;
	
	/** The little id. */
	private Long littleId;
	
	

	/**
	 * Gets the little id.
	 *
	 * @return the little id
	 */
	public Long getLittleId() {
		return littleId;
	}

	/**
	 * Sets the little id.
	 *
	 * @param littleId the new little id
	 */
	public void setLittleId(Long littleId) {
		this.littleId = littleId;
	}

	/**
	 * Gets the little product id.
	 *
	 * @return the littleProductId
	 */
	public Long getLittleProductId() {
		return littleProductId;
	}

	/**
	 * Sets the little product id.
	 *
	 * @param littleProductId the littleProductId to set
	 */
	public void setLittleProductId(Long littleProductId) {
		this.littleProductId = littleProductId;
	}

	/**
	 * Gets the description.
	 *
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * Sets the description.
	 *
	 * @param description the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * Gets the active yn.
	 *
	 * @return the active yn
	 */
	public String getActiveYn() {
		return activeYn;
	}

	/**
	 * Sets the active yn.
	 *
	 * @param activeYn the new active yn
	 */
	public void setActiveYn(String activeYn) {
		this.activeYn = activeYn;
	}

	
}
