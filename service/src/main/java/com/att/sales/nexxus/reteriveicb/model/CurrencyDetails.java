package com.att.sales.nexxus.reteriveicb.model;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.map.annotate.JsonSerialize;


/**
 * The Class CurrencyDetails.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class CurrencyDetails {

	/** The currency code. */
	private String currencyCode;
	
	/** The currency description. */
	private String currencyDescription;
	
	/** The currency symbol. */
	private String currencySymbol;
	
	/**
	 * Gets the currency code.
	 *
	 * @return the currency code
	 */
	public String getCurrencyCode() {
		return currencyCode;
	}
	
	/**
	 * Sets the currency code.
	 *
	 * @param currencyCode the new currency code
	 */
	public void setCurrencyCode(String currencyCode) {
		this.currencyCode = currencyCode;
	}
	
	/**
	 * Gets the currency description.
	 *
	 * @return the currency description
	 */
	public String getCurrencyDescription() {
		return currencyDescription;
	}
	
	/**
	 * Sets the currency description.
	 *
	 * @param currencyDescription the new currency description
	 */
	public void setCurrencyDescription(String currencyDescription) {
		this.currencyDescription = currencyDescription;
	}
	
	/**
	 * Gets the currency symbol.
	 *
	 * @return the currency symbol
	 */
	public String getCurrencySymbol() {
		return currencySymbol;
	}
	
	/**
	 * Sets the currency symbol.
	 *
	 * @param currencySymbol the new currency symbol
	 */
	public void setCurrencySymbol(String currencySymbol) {
		this.currencySymbol = currencySymbol;
	}
	
}
