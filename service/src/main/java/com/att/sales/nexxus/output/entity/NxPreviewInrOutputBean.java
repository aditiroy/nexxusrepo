package com.att.sales.nexxus.output.entity;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

/**
 * Class NxPreviewInrOutputBean.
 *
 * @author sn973r
 */

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(Include.NON_NULL)
public class NxPreviewInrOutputBean {
	
	/** The customer name. */
	private String customerName;
	
	/** The search criteria. */
	private String searchCriteria;
	
	/** The search criteria value. */
	private String searchCriteriaValue;
	
	/** The site id. */
	private String siteId;
	
	/** The site name. */
	private String siteName;
	
	/** The city. */
	private String city;
	
	/** The country. */
	private String country;
	
	/** The currency. */
	private String currency;
	
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
	
	/** The product. */
	private String product;
	
	
	/**
	 * Gets the product.
	 *
	 * @return the product
	 */
	public String getProduct() {
		return product;
	}
	
	/**
	 * Sets the product.
	 *
	 * @param product the new product
	 */
	public void setProduct(String product) {
		this.product = product;
	}
	
	/**
	 * Gets the customer name.
	 *
	 * @return the customer name
	 */
	public String getCustomerName() {
		return customerName;
	}
	
	/**
	 * Sets the customer name.
	 *
	 * @param customerName the new customer name
	 */
	public void setCustomerName(String customerName) {
		this.customerName = customerName;
	}
	
	/**
	 * Gets the search criteria.
	 *
	 * @return the search criteria
	 */
	public String getSearchCriteria() {
		return searchCriteria;
	}
	
	/**
	 * Sets the search criteria.
	 *
	 * @param searchCriteria the new search criteria
	 */
	public void setSearchCriteria(String searchCriteria) {
		this.searchCriteria = searchCriteria;
	}
	
	/**
	 * Gets the search criteria value.
	 *
	 * @return the search criteria value
	 */
	public String getSearchCriteriaValue() {
		return searchCriteriaValue;
	}
	
	/**
	 * Sets the search criteria value.
	 *
	 * @param searchCriteriaValue the new search criteria value
	 */
	public void setSearchCriteriaValue(String searchCriteriaValue) {
		this.searchCriteriaValue = searchCriteriaValue;
	}
	
	/**
	 * Gets the currency.
	 *
	 * @return the currency
	 */
	public String getCurrency() {
		return currency;
	}
	
	/**
	 * Sets the currency.
	 *
	 * @param currency the new currency
	 */
	public void setCurrency(String currency) {
		this.currency = currency;
	}
	
	
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
	 * Gets the site id.
	 *
	 * @return the site id
	 */
	public String getSiteId() {
		return siteId;
	}
	
	/**
	 * Sets the site id.
	 *
	 * @param siteId the new site id
	 */
	public void setSiteId(String siteId) {
		this.siteId = siteId;
	}
	
	/**
	 * Gets the site name.
	 *
	 * @return the site name
	 */
	public String getSiteName() {
		return siteName;
	}
	
	/**
	 * Sets the site name.
	 *
	 * @param siteName the new site name
	 */
	public void setSiteName(String siteName) {
		this.siteName = siteName;
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
