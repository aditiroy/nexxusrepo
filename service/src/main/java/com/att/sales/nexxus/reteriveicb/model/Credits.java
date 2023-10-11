package com.att.sales.nexxus.reteriveicb.model;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.map.annotate.JsonSerialize;


/**
 * The Class Credits.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class Credits {
	
	/** The credit id. */
	private Long creditId;
	
	/** The product id. */
	private Long productId;
	
	/** The country. */
	private String country;
	
	/** The credit type. */
	private String creditType;
	
	/** The credit sub type. */
	private String creditSubType;
	
	/** The month offset. */
	private Long monthOffset;
	
	/** The credit amount. */
	private Long creditAmount;
	
	/** The credit conditions. */
	private String creditConditions;
	
	/** The max credit offeset. */
	private Long maxCreditOffeset;
	
	/** The credit app date. */
	private String creditAppDate;
	
	/** The related to. */
	private String relatedTo;
	
	
	/** The credits msg. */
	private InterfaceorchMessage creditsMsg;

	/**
	 * Gets the credit id.
	 *
	 * @return the credit id
	 */
	public Long getCreditId() {
		return creditId;
	}

	/**
	 * Sets the credit id.
	 *
	 * @param creditId the new credit id
	 */
	public void setCreditId(Long creditId) {
		this.creditId = creditId;
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
	 * Gets the credit type.
	 *
	 * @return the credit type
	 */
	public String getCreditType() {
		return creditType;
	}

	/**
	 * Sets the credit type.
	 *
	 * @param creditType the new credit type
	 */
	public void setCreditType(String creditType) {
		this.creditType = creditType;
	}

	/**
	 * Gets the credit sub type.
	 *
	 * @return the credit sub type
	 */
	public String getCreditSubType() {
		return creditSubType;
	}

	/**
	 * Sets the credit sub type.
	 *
	 * @param creditSubType the new credit sub type
	 */
	public void setCreditSubType(String creditSubType) {
		this.creditSubType = creditSubType;
	}

	/**
	 * Gets the month offset.
	 *
	 * @return the month offset
	 */
	public Long getMonthOffset() {
		return monthOffset;
	}

	/**
	 * Sets the month offset.
	 *
	 * @param monthOffset the new month offset
	 */
	public void setMonthOffset(Long monthOffset) {
		this.monthOffset = monthOffset;
	}

	/**
	 * Gets the credit amount.
	 *
	 * @return the credit amount
	 */
	public Long getCreditAmount() {
		return creditAmount;
	}

	/**
	 * Sets the credit amount.
	 *
	 * @param creditAmount the new credit amount
	 */
	public void setCreditAmount(Long creditAmount) {
		this.creditAmount = creditAmount;
	}

	/**
	 * Gets the credits msg.
	 *
	 * @return the credits msg
	 */
	public InterfaceorchMessage getCreditsMsg() {
		return creditsMsg;
	}

	/**
	 * Sets the credits msg.
	 *
	 * @param creditsMsg the new credits msg
	 */
	public void setCreditsMsg(InterfaceorchMessage creditsMsg) {
		this.creditsMsg = creditsMsg;
	}

	/**
	 * Gets the product id.
	 *
	 * @return the product id
	 */
	public Long getProductId() {
		return productId;
	}

	/**
	 * Sets the product id.
	 *
	 * @param productId the new product id
	 */
	public void setProductId(Long productId) {
		this.productId = productId;
	}

	/**
	 * Gets the credit conditions.
	 *
	 * @return the credit conditions
	 */
	public String getCreditConditions() {
		return creditConditions;
	}

	/**
	 * Sets the credit conditions.
	 *
	 * @param creditConditions the new credit conditions
	 */
	public void setCreditConditions(String creditConditions) {
		this.creditConditions = creditConditions;
	}

	/**
	 * Gets the max credit offeset.
	 *
	 * @return the max credit offeset
	 */
	public Long getMaxCreditOffeset() {
		return maxCreditOffeset;
	}

	/**
	 * Sets the max credit offeset.
	 *
	 * @param maxCreditOffeset the new max credit offeset
	 */
	public void setMaxCreditOffeset(Long maxCreditOffeset) {
		this.maxCreditOffeset = maxCreditOffeset;
	}

	/**
	 * Gets the credit app date.
	 *
	 * @return the credit app date
	 */
	public String getCreditAppDate() {
		return creditAppDate;
	}

	/**
	 * Sets the credit app date.
	 *
	 * @param creditAppDate the new credit app date
	 */
	public void setCreditAppDate(String creditAppDate) {
		this.creditAppDate = creditAppDate;
	}

	/**
	 * Gets the related to.
	 *
	 * @return the related to
	 */
	public String getRelatedTo() {
		return relatedTo;
	}

	/**
	 * Sets the related to.
	 *
	 * @param relatedTo the new related to
	 */
	public void setRelatedTo(String relatedTo) {
		this.relatedTo = relatedTo;
	}
}
