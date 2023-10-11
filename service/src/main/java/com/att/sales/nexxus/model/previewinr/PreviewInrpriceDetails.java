package com.att.sales.nexxus.model.previewinr;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

/**
 * Class PreviewInrpriceDetails.
 *
 * @author sn973r
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(Include.NON_NULL)
public class PreviewInrpriceDetails {
	
	/** The beid. */
	private String beid;
	
	/** The price type. */
	private String priceType;
	
	/** The quantity. */
	private String quantity;
	
	/** The actual price. */
	private String actualPrice;	
	
	/** The local list price. */
	private String localListPrice;
	
	/**
	 * Gets the local list price.
	 *
	 * @return the local list price
	 */
	public String getLocalListPrice() {
		return localListPrice;
	}
	
	/**
	 * Sets the local list price.
	 *
	 * @param localListPrice the new local list price
	 */
	public void setLocalListPrice(String localListPrice) {
		this.localListPrice = localListPrice;
	}
	
	/**
	 * Gets the beid.
	 *
	 * @return the beid
	 */
	public String getBeid() {
		return beid;
	}
	
	/**
	 * Sets the beid.
	 *
	 * @param beid the new beid
	 */
	public void setBeid(String beid) {
		this.beid = beid;
	}
	
	/**
	 * Gets the price type.
	 *
	 * @return the price type
	 */
	public String getPriceType() {
		return priceType;
	}
	
	/**
	 * Sets the price type.
	 *
	 * @param priceType the new price type
	 */
	public void setPriceType(String priceType) {
		this.priceType = priceType;
	}
	
	/**
	 * Gets the quantity.
	 *
	 * @return the quantity
	 */
	public String getQuantity() {
		return quantity;
	}
	
	/**
	 * Sets the quantity.
	 *
	 * @param quantity the new quantity
	 */
	public void setQuantity(String quantity) {
		this.quantity = quantity;
	}
	
	/**
	 * Gets the actual price.
	 *
	 * @return the actual price
	 */
	public String getActualPrice() {
		return actualPrice;
	}
	
	/**
	 * Sets the actual price.
	 *
	 * @param actualPrice the new actual price
	 */
	public void setActualPrice(String actualPrice) {
		this.actualPrice = actualPrice;
	}

	

}
