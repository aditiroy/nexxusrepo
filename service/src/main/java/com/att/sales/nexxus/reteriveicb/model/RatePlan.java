package com.att.sales.nexxus.reteriveicb.model;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.map.annotate.JsonSerialize;


/**
 * The Class RatePlan.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class RatePlan {

	/** The rate plan id. */
	private String ratePlanId;
	
	/** The rate plan eff date. */
	private String ratePlanEffDate;
	
	/** The rate stability date. */
	private String rateStabilityDate;
	
	/** The rate plan version. */
	private Long ratePlanVersion;
	
	/** The rate plan source id. */
	private String ratePlanSourceId;
	
	/** The price plan id external. */
	//New property added
	private Long pricePlanIdExternal;
	
	/** The price plan version. */
	private Long pricePlanVersion;
	
	/** The pricing source id. */
	private Long pricingSourceId;
	
	/** The pricing source name. */
	private String pricingSourceName;
	
	/** The pricing source expired yn. */
	private String pricingSourceExpiredYn;
	
	/** The rate plan end date. */
	private String ratePlanEndDate;
	
	/** The rate plan id external. */
	private String ratePlanIdExternal;
	
	/** The rate plan name. */
	private String ratePlanName;
	
	/** The rate plan status. */
	private String ratePlanStatus;
	

	/**
	 * Gets the rate plan id.
	 *
	 * @return the rate plan id
	 */
	public String getRatePlanId() {
		return ratePlanId;
	}
	
	/**
	 * Sets the rate plan id.
	 *
	 * @param ratePlanId the new rate plan id
	 */
	public void setRatePlanId(String ratePlanId) {
		this.ratePlanId = ratePlanId;
	}
	
	/**
	 * Gets the rate stability date.
	 *
	 * @return the rate stability date
	 */
	public String getRateStabilityDate() {
		return rateStabilityDate;
	}
	
	/**
	 * Sets the rate stability date.
	 *
	 * @param rateStabilityDate the new rate stability date
	 */
	public void setRateStabilityDate(String rateStabilityDate) {
		this.rateStabilityDate = rateStabilityDate;
	}
	
	/**
	 * Gets the rate plan version.
	 *
	 * @return the rate plan version
	 */
	public Long getRatePlanVersion() {
		return ratePlanVersion;
	}
	
	/**
	 * Sets the rate plan version.
	 *
	 * @param ratePlanVersion the new rate plan version
	 */
	public void setRatePlanVersion(Long ratePlanVersion) {
		this.ratePlanVersion = ratePlanVersion;
	}
	
	
	/**
	 * Gets the rate plan source id.
	 *
	 * @return the rate plan source id
	 */
	public String getRatePlanSourceId() {
		return ratePlanSourceId;
	}
	
	/**
	 * Sets the rate plan source id.
	 *
	 * @param ratePlanSourceId the new rate plan source id
	 */
	public void setRatePlanSourceId(String ratePlanSourceId) {
		this.ratePlanSourceId = ratePlanSourceId;
	}
	
	
	/**
	 * Gets the price plan id external.
	 *
	 * @return the price plan id external
	 */
	public Long getPricePlanIdExternal() {
		return pricePlanIdExternal;
	}
	
	/**
	 * Sets the price plan id external.
	 *
	 * @param pricePlanIdExternal the new price plan id external
	 */
	public void setPricePlanIdExternal(Long pricePlanIdExternal) {
		this.pricePlanIdExternal = pricePlanIdExternal;
	}
	
	/**
	 * Gets the price plan version.
	 *
	 * @return the price plan version
	 */
	public Long getPricePlanVersion() {
		return pricePlanVersion;
	}
	
	/**
	 * Sets the price plan version.
	 *
	 * @param pricePlanVersion the new price plan version
	 */
	public void setPricePlanVersion(Long pricePlanVersion) {
		this.pricePlanVersion = pricePlanVersion;
	}
	
	/**
	 * Gets the pricing source id.
	 *
	 * @return the pricing source id
	 */
	public Long getPricingSourceId() {
		return pricingSourceId;
	}
	
	/**
	 * Sets the pricing source id.
	 *
	 * @param pricingSourceId the new pricing source id
	 */
	public void setPricingSourceId(Long pricingSourceId) {
		this.pricingSourceId = pricingSourceId;
	}
	
	/**
	 * Gets the pricing source name.
	 *
	 * @return the pricing source name
	 */
	public String getPricingSourceName() {
		return pricingSourceName;
	}
	
	/**
	 * Sets the pricing source name.
	 *
	 * @param pricingSourceName the new pricing source name
	 */
	public void setPricingSourceName(String pricingSourceName) {
		this.pricingSourceName = pricingSourceName;
	}
	
	/**
	 * Gets the pricing source expired yn.
	 *
	 * @return the pricing source expired yn
	 */
	public String getPricingSourceExpiredYn() {
		return pricingSourceExpiredYn;
	}
	
	/**
	 * Sets the pricing source expired yn.
	 *
	 * @param pricingSourceExpiredYn the new pricing source expired yn
	 */
	public void setPricingSourceExpiredYn(String pricingSourceExpiredYn) {
		this.pricingSourceExpiredYn = pricingSourceExpiredYn;
	}
	
	/**
	 * Gets the rate plan end date.
	 *
	 * @return the rate plan end date
	 */
	public String getRatePlanEndDate() {
		return ratePlanEndDate;
	}
	
	/**
	 * Sets the rate plan end date.
	 *
	 * @param ratePlanEndDate the new rate plan end date
	 */
	public void setRatePlanEndDate(String ratePlanEndDate) {
		this.ratePlanEndDate = ratePlanEndDate;
	}
	
	/**
	 * Gets the rate plan id external.
	 *
	 * @return the rate plan id external
	 */
	public String getRatePlanIdExternal() {
		return ratePlanIdExternal;
	}
	
	/**
	 * Sets the rate plan id external.
	 *
	 * @param ratePlanIdExternal the new rate plan id external
	 */
	public void setRatePlanIdExternal(String ratePlanIdExternal) {
		this.ratePlanIdExternal = ratePlanIdExternal;
	}
	
	/**
	 * Gets the rate plan name.
	 *
	 * @return the rate plan name
	 */
	public String getRatePlanName() {
		return ratePlanName;
	}
	
	/**
	 * Gets the rate plan eff date.
	 *
	 * @return the ratePlanEffDate
	 */
	public String getRatePlanEffDate() {
		return ratePlanEffDate;
	}
	
	/**
	 * Sets the rate plan eff date.
	 *
	 * @param ratePlanEffDate the ratePlanEffDate to set
	 */
	public void setRatePlanEffDate(String ratePlanEffDate) {
		this.ratePlanEffDate = ratePlanEffDate;
	}
	
	/**
	 * Gets the rate plan status.
	 *
	 * @return the rate plan status
	 */
	public String getRatePlanStatus() {
		return ratePlanStatus;
	}
	
	/**
	 * Sets the rate plan status.
	 *
	 * @param ratePlanStatus the new rate plan status
	 */
	public void setRatePlanStatus(String ratePlanStatus) {
		this.ratePlanStatus = ratePlanStatus;
	}
}
