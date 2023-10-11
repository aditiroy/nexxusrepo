package com.att.sales.nexxus.output.entity;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

/**
 * The Class NxBaseOutputBean.
 *
 * @author vt393d
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(Include.NON_NULL)
public class NxBaseOutputBean  implements Serializable{
	
	/** The Constant serialVersionUID. */
	
	private static final long serialVersionUID = 1L;
	
	/**
	 * Instantiates a new nx base output bean.
	 */
	public NxBaseOutputBean() {}
	
	
	/** The site id. */
	private String siteId;
	
	/** The line item id. */
	private String lineItemId;
	
	/** The secondary key. */
	private String secondaryKey;
	
	/** The little prod id. */
	private String littleProdId;
	
	/** The top prod id. */
	private String topProdId;
	
	/** The product type. */
	private String productType;
	
	/** The site name. */
	private String siteName;
	
	/** The state. */
	private String state;
	
	/** The city. */
	private String city;
	
	/** The postal cd. */
	private String postalCd;
	
	/** The address. */
	private String address;
	
	/** The currency. */
	private String currency;
	
	/** The fmo mrc quantity. */
	private String fmoMrcQuantity;
	
	/** The inr mrc quantity. */
	private String inrMrcQuantity;
	
	/** The fmo nrc quantity. */
	private String fmoNrcQuantity;
	
	/** The inr nrc quantity. */
	private String inrNrcQuantity;
	
	/** The term. */
	private String term;
	
	/** The current mrc. */
	private String currentMrc;
	
	/** The current nrc. */
	private String currentNrc;
	
	/** The type. */
	private String type;
	
	/** The country. */
	private String country;
	
	/** The mrc discount. */
	private String mrcDiscount;
	
	/** The nrc discount. */
	private String nrcDiscount;
	
	/** The mrc be id. */
	private String mrcBeId;
	
	/** The nrc be id. */
	private String nrcBeId;
	
	/** The related product. */
	private String relatedProduct;
	
	/** The quantity. */
	private String quantity;
	
	
	/**
	 * Gets the little prod id.
	 *
	 * @return the little prod id
	 */
	public String getLittleProdId() {
		return littleProdId;
	}
	
	/**
	 * Sets the little prod id.
	 *
	 * @param littleProdId the new little prod id
	 */
	public void setLittleProdId(String littleProdId) {
		this.littleProdId = littleProdId;
	}
	
	/**
	 * Gets the product type.
	 *
	 * @return the product type
	 */
	public String getProductType() {
		return productType;
	}
	
	/**
	 * Sets the product type.
	 *
	 * @param productType the new product type
	 */
	public void setProductType(String productType) {
		this.productType = productType;
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
	 * Gets the term.
	 *
	 * @return the term
	 */
	public String getTerm() {
		return term;
	}
	
	/**
	 * Sets the term.
	 *
	 * @param term the new term
	 */
	public void setTerm(String term) {
		this.term = term;
	}
	
	/**
	 * Gets the current mrc.
	 *
	 * @return the current mrc
	 */
	public String getCurrentMrc() {
		return currentMrc;
	}
	
	/**
	 * Sets the current mrc.
	 *
	 * @param currentMrc the new current mrc
	 */
	public void setCurrentMrc(String currentMrc) {
		this.currentMrc = currentMrc;
	}
	
	/**
	 * Gets the current nrc.
	 *
	 * @return the current nrc
	 */
	public String getCurrentNrc() {
		return currentNrc;
	}
	
	/**
	 * Sets the current nrc.
	 *
	 * @param currentNrc the new current nrc
	 */
	public void setCurrentNrc(String currentNrc) {
		this.currentNrc = currentNrc;
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
	 * Gets the fmo mrc quantity.
	 *
	 * @return the fmo mrc quantity
	 */
	public String getFmoMrcQuantity() {
		return fmoMrcQuantity;
	}
	
	/**
	 * Sets the fmo mrc quantity.
	 *
	 * @param fmoMrcQuantity the new fmo mrc quantity
	 */
	public void setFmoMrcQuantity(String fmoMrcQuantity) {
		this.fmoMrcQuantity = fmoMrcQuantity;
	}
	
	/**
	 * Gets the inr mrc quantity.
	 *
	 * @return the inr mrc quantity
	 */
	public String getInrMrcQuantity() {
		return inrMrcQuantity;
	}
	
	/**
	 * Sets the inr mrc quantity.
	 *
	 * @param inrMrcQuantity the new inr mrc quantity
	 */
	public void setInrMrcQuantity(String inrMrcQuantity) {
		this.inrMrcQuantity = inrMrcQuantity;
	}
	
	/**
	 * Gets the fmo nrc quantity.
	 *
	 * @return the fmo nrc quantity
	 */
	public String getFmoNrcQuantity() {
		return fmoNrcQuantity;
	}
	
	/**
	 * Sets the fmo nrc quantity.
	 *
	 * @param fmoNrcQuantity the new fmo nrc quantity
	 */
	public void setFmoNrcQuantity(String fmoNrcQuantity) {
		this.fmoNrcQuantity = fmoNrcQuantity;
	}
	
	/**
	 * Gets the inr nrc quantity.
	 *
	 * @return the inr nrc quantity
	 */
	public String getInrNrcQuantity() {
		return inrNrcQuantity;
	}
	
	/**
	 * Sets the inr nrc quantity.
	 *
	 * @param inrNrcQuantity the new inr nrc quantity
	 */
	public void setInrNrcQuantity(String inrNrcQuantity) {
		this.inrNrcQuantity = inrNrcQuantity;
	}
	
	/**
	 * Gets the postal cd.
	 *
	 * @return the postal cd
	 */
	public String getPostalCd() {
		return postalCd;
	}
	
	/**
	 * Sets the postal cd.
	 *
	 * @param postalCd the new postal cd
	 */
	public void setPostalCd(String postalCd) {
		this.postalCd = postalCd;
	}
	
	/**
	 * Gets the address.
	 *
	 * @return the address
	 */
	public String getAddress() {
		return address;
	}
	
	/**
	 * Sets the address.
	 *
	 * @param address the new address
	 */
	public void setAddress(String address) {
		this.address = address;
	}
	
	/**
	 * Gets the type.
	 *
	 * @return the type
	 */
	public String getType() {
		return type;
	}
	
	/**
	 * Sets the type.
	 *
	 * @param type the new type
	 */
	public void setType(String type) {
		this.type = type;
	}
	
	/**
	 * Gets the top prod id.
	 *
	 * @return the top prod id
	 */
	public String getTopProdId() {
		return topProdId;
	}
	
	/**
	 * Sets the top prod id.
	 *
	 * @param topProdId the new top prod id
	 */
	public void setTopProdId(String topProdId) {
		this.topProdId = topProdId;
	}
	
	/**
	 * Gets the line item id.
	 *
	 * @return the line item id
	 */
	public String getLineItemId() {
		return lineItemId;
	}
	
	/**
	 * Sets the line item id.
	 *
	 * @param lineItemId the new line item id
	 */
	public void setLineItemId(String lineItemId) {
		this.lineItemId = lineItemId;
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
	 * Gets the mrc discount.
	 *
	 * @return the mrc discount
	 */
	public String getMrcDiscount() {
		return mrcDiscount;
	}

	/**
	 * Sets the mrc discount.
	 *
	 * @param mrcDiscount the new mrc discount
	 */
	public void setMrcDiscount(String mrcDiscount) {
		this.mrcDiscount = mrcDiscount;
	}

	/**
	 * Gets the nrc discount.
	 *
	 * @return the nrc discount
	 */
	public String getNrcDiscount() {
		return nrcDiscount;
	}

	/**
	 * Sets the nrc discount.
	 *
	 * @param nrcDiscount the new nrc discount
	 */
	public void setNrcDiscount(String nrcDiscount) {
		this.nrcDiscount = nrcDiscount;
	}

	/**
	 * Gets the mrc be id.
	 *
	 * @return the mrc be id
	 */
	public String getMrcBeId() {
		return mrcBeId;
	}
	
	/**
	 * Sets the mrc be id.
	 *
	 * @param mrcBeId the new mrc be id
	 */
	public void setMrcBeId(String mrcBeId) {
		this.mrcBeId = mrcBeId;
	}
	
	/**
	 * Gets the nrc be id.
	 *
	 * @return the nrc be id
	 */
	public String getNrcBeId() {
		return nrcBeId;
	}
	
	/**
	 * Sets the nrc be id.
	 *
	 * @param nrcBeId the new nrc be id
	 */
	public void setNrcBeId(String nrcBeId) {
		this.nrcBeId = nrcBeId;
	}
	
	/**
	 * Gets the secondary key.
	 *
	 * @return the secondary key
	 */
	public String getSecondaryKey() {
		return secondaryKey;
	}
	
	/**
	 * Sets the secondary key.
	 *
	 * @param secondaryKey the new secondary key
	 */
	public void setSecondaryKey(String secondaryKey) {
		this.secondaryKey = secondaryKey;
	}
	
	/**
	 * Gets the related product.
	 *
	 * @return the related product
	 */
	public String getRelatedProduct() {
		return relatedProduct;
	}
	
	/**
	 * Sets the related product.
	 *
	 * @param relatedProduct the new related product
	 */
	public void setRelatedProduct(String relatedProduct) {
		this.relatedProduct = relatedProduct;
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
	
	
}
