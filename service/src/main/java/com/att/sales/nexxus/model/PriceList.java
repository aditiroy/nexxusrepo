package com.att.sales.nexxus.model;

import java.io.Serializable;
import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

/**
 * The Class PriceList.
 *
 * @author km017g
 */
public class PriceList implements Serializable{

	 /** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;
	
	/** The net price. */
	private BigDecimal netPrice;
	 
 	/** The list price. */
 	private BigDecimal listPrice;
	 
 	/** The rate id. */
 	@JsonInclude(Include.NON_DEFAULT)
	 private Integer rateId;
	 
 	/** The external rate id. */
 	private Integer externalRateId;
	 
 	/** The rate description. */
 	private String rateDescription;
	 
 	/** The frequency. */
 	private String frequency;
	 
 	/** The discount percentage. */
 	private Integer discountPercentage;
	
 	
 	private String priceUnit;
 	
 	private String rdsPriceType;
 	
	/**
	 * Gets the net price.
	 *
	 * @return the net price
	 */
	public BigDecimal getNetPrice() {
		return netPrice;
	}
	
	/**
	 * Sets the net price.
	 *
	 * @param netPrice the new net price
	 */
	public void setNetPrice(BigDecimal netPrice) {
		this.netPrice = netPrice;
	}
	
	/**
	 * Gets the list price.
	 *
	 * @return the list price
	 */
	public BigDecimal getListPrice() {
		return listPrice;
	}
	
	/**
	 * Sets the list price.
	 *
	 * @param listPrice the new list price
	 */
	public void setListPrice(BigDecimal listPrice) {
		this.listPrice = listPrice;
	}
	
	/**
	 * Gets the rate id.
	 *
	 * @return the rate id
	 */
	public Integer getRateId() {
		return rateId;
	}
	
	/**
	 * Sets the rate id.
	 *
	 * @param rateId the new rate id
	 */
	public void setRateId(Integer rateId) {
		this.rateId = rateId;
	}
	
	/**
	 * Gets the external rate id.
	 *
	 * @return the external rate id
	 */
	public Integer getExternalRateId() {
		return externalRateId;
	}
	
	/**
	 * Sets the external rate id.
	 *
	 * @param externalRateId the new external rate id
	 */
	public void setExternalRateId(Integer externalRateId) {
		this.externalRateId = externalRateId;
	}
	
	/**
	 * Gets the rate description.
	 *
	 * @return the rate description
	 */
	public String getRateDescription() {
		return rateDescription;
	}
	
	/**
	 * Sets the rate description.
	 *
	 * @param rateDescription the new rate description
	 */
	public void setRateDescription(String rateDescription) {
		this.rateDescription = rateDescription;
	}
	
	/**
	 * Gets the frequency.
	 *
	 * @return the frequency
	 */
	public String getFrequency() {
		return frequency;
	}
	
	/**
	 * Sets the frequency.
	 *
	 * @param frequency the new frequency
	 */
	public void setFrequency(String frequency) {
		this.frequency = frequency;
	}
	
	/**
	 * Gets the discount percentage.
	 *
	 * @return the discount percentage
	 */
	public Integer getDiscountPercentage() {
		return discountPercentage;
	}
	
	/**
	 * Sets the discount percentage.
	 *
	 * @param discountPercentage the new discount percentage
	 */
	public void setDiscountPercentage(Integer discountPercentage) {
		this.discountPercentage = discountPercentage;
	}

	public String getPriceUnit() {
		return priceUnit;
	}

	public void setPriceUnit(String priceUnit) {
		this.priceUnit = priceUnit;
	}

	public String getRdsPriceType() {
		return rdsPriceType;
	}

	public void setRdsPriceType(String rdsPriceType) {
		this.rdsPriceType = rdsPriceType;
	}
	
	
}
