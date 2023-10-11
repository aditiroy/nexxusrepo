package com.att.sales.nexxus.reteriveicb.model;

/*
 * @Author: Akash Arya
 * 
 * 
 */
import java.util.List;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.map.annotate.JsonSerialize;


/**
 * The Class Offer.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class Offer {

	/** The offer id. */
	private String offerId;
	
	/** The bundle code. */
	private String bundleCode;
	
	/** The offer id external. */
	private String offerIdExternal;
	
	/** The offer version. */
	private Long offerVersion;
	
	/** The price scenario name. */
	private String priceScenarioName;
	
	/** The total MRC. */
	private Long totalMRC;
	
	/** The total NRC. */
	private Long totalNRC;

	/** The site id. */
	private List<Long> siteId;
	
	/** The retrieve details. */
	private List<Long> retrieveDetails;
	
	/** The circuit **/
	private List<Circuit> circuit;
	
	private List<Products> products;
	
	/**
	 * Gets the site id.
	 *
	 * @return the site id
	 */
	public List<Long> getSiteId() {
		return siteId;
	}
	
	/**
	 * Sets the site id.
	 *
	 * @param siteId the new site id
	 */
	public void setSiteId(List<Long> siteId) {
		this.siteId = siteId;
	}
	
	/**
	 * Gets the retrieve details.
	 *
	 * @return the retrieve details
	 */
	public List<Long> getRetrieveDetails() {
		return retrieveDetails;
	}
	
	/**
	 * Sets the retrieve details.
	 *
	 * @param retrieveDetails the new retrieve details
	 */
	public void setRetrieveDetails(List<Long> retrieveDetails) {
		this.retrieveDetails = retrieveDetails;
	}
	
	/** The xchange details. */
	private List<XchangeDetails> xchangeDetails;
	
	/** The currency details. */
	private List<CurrencyDetails> currencyDetails;
	
	/** The credits. */
	private List<Credits> credits;
	
	/** The site. */
	private List<Site> site;
	
	/** The rate plan. */
	private List<RatePlan> ratePlan;
	
	
	/**whether price has to update or not */
	private String priceUpdate;
	
	/**
	 * Gets the offer id.
	 *
	 * @return the offer id
	 */
	public String getOfferId() {
		return offerId;
	}
	
	/**
	 * Sets the offer id.
	 *
	 * @param offerId the new offer id
	 */
	public void setOfferId(String offerId) {
		this.offerId = offerId;
	}
	
	
	/**
	 * Gets the bundle code.
	 *
	 * @return the bundle code
	 */
	public String getBundleCode() {
		return bundleCode;
	}
	
	/**
	 * Sets the bundle code.
	 *
	 * @param bundleCode the new bundle code
	 */
	public void setBundleCode(String bundleCode) {
		this.bundleCode = bundleCode;
	}
	
		
	/**
	 * Gets the offer id external.
	 *
	 * @return the offer id external
	 */
	public String getOfferIdExternal() {
		return offerIdExternal;
	}
	
	/**
	 * Sets the offer id external.
	 *
	 * @param offerIdExternal the new offer id external
	 */
	public void setOfferIdExternal(String offerIdExternal) {
		this.offerIdExternal = offerIdExternal;
	}
	
	/**
	 * Gets the offer version.
	 *
	 * @return the offer version
	 */
	public Long getOfferVersion() {
		return offerVersion;
	}
	
	/**
	 * Sets the offer version.
	 *
	 * @param offerVersion the new offer version
	 */
	public void setOfferVersion(Long offerVersion) {
		this.offerVersion = offerVersion;
	}
	
	/**
	 * Gets the xchange details.
	 *
	 * @return the xchange details
	 */
	public List<XchangeDetails> getXchangeDetails() {
		return xchangeDetails;
	}
	
	/**
	 * Sets the xchange details.
	 *
	 * @param xchangeDetails the new xchange details
	 */
	public void setXchangeDetails(List<XchangeDetails> xchangeDetails) {
		this.xchangeDetails = xchangeDetails;
	}
	
	/**
	 * Gets the currency details.
	 *
	 * @return the currency details
	 */
	public List<CurrencyDetails> getCurrencyDetails() {
		return currencyDetails;
	}
	
	/**
	 * Sets the currency details.
	 *
	 * @param currencyDetails the new currency details
	 */
	public void setCurrencyDetails(List<CurrencyDetails> currencyDetails) {
		this.currencyDetails = currencyDetails;
	}
	
	/**
	 * Gets the credits.
	 *
	 * @return the credits
	 */
	public List<Credits> getCredits() {
		return credits;
	}
	
	/**
	 * Sets the credits.
	 *
	 * @param credits the new credits
	 */
	public void setCredits(List<Credits> credits) {
		this.credits = credits;
	}
		
	
	/**
	 * Gets the site.
	 *
	 * @return the site
	 */
	public List<Site> getSite() {
		return site;
	}
	
	/**
	 * Sets the site.
	 *
	 * @param site the new site
	 */
	public void setSite(List<Site> site) {
		this.site = site;
	}
	
	/**
	 * Gets the rate plan.
	 *
	 * @return the rate plan
	 */
	public List<RatePlan> getRatePlan() {
		return ratePlan;
	}
	
	/**
	 * Sets the rate plan.
	 *
	 * @param ratePlan the new rate plan
	 */
	public void setRatePlan(List<RatePlan> ratePlan) {
		this.ratePlan = ratePlan;
	}
	
		

	/**
	 * Gets the price scenario name.
	 *
	 * @return the price scenario name
	 */
	public String getPriceScenarioName() {
		return priceScenarioName;
	}
	
	/**
	 * Sets the price scenario name.
	 *
	 * @param priceScenarioName the new price scenario name
	 */
	public void setPriceScenarioName(String priceScenarioName) {
		this.priceScenarioName = priceScenarioName;
	}
	
	/**
	 * Gets the total MRC.
	 *
	 * @return the total MRC
	 */
	public Long getTotalMRC() {
		return totalMRC;
	}
	
	/**
	 * Sets the total MRC.
	 *
	 * @param totalMRC the new total MRC
	 */
	public void setTotalMRC(Long totalMRC) {
		this.totalMRC = totalMRC;
	}
	
	/**
	 * Gets the total NRC.
	 *
	 * @return the total NRC
	 */
	public Long getTotalNRC() {
		return totalNRC;
	}
	
	/**
	 * Sets the total NRC.
	 *
	 * @param totalNRC the new total NRC
	 */
	public void setTotalNRC(Long totalNRC) {
		this.totalNRC = totalNRC;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Offer [offerId=" + offerId + ", bundleCode=" + bundleCode+ ", site=" + site +"]";
	}

	public List<Circuit> getCircuit() {
		return circuit;
	}

	public void setCircuit(List<Circuit> circuit) {
		this.circuit = circuit;
	}

	public List<Products> getProducts() {
		return products;
	}

	public void setProducts(List<Products> products) {
		this.products = products;
	}

	public String getPriceUpdate() {
		return priceUpdate;
	}

	public void setPriceUpdate(String priceUpdate) {
		this.priceUpdate = priceUpdate;
	}
	
	
}
