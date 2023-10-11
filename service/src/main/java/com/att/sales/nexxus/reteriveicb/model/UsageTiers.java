package com.att.sales.nexxus.reteriveicb.model;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.map.annotate.JsonSerialize;


/**
 * The Class UsageTiers.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class UsageTiers {
	
	/** The usage tier id. */
	private Long usageTierId;
	
	/** The range from. */
	private Long rangeFrom;
	
	/** The range to. */
	private Long rangeTo;
	
	/** The t 3 rate. */
	private Long t3Rate;
	
	/** The oc 3 rate. */
	private Long oc3Rate;
	
	/** The oc 12 rate. */
	private Long oc12Rate;
	
	/** The oc 48 rate. */
	private Long oc48Rate;
	
	/** The gig rate. */
	private Long gigRate;
	
	/** The ethernet 10 gig. */
	private Long ethernet10Gig;
	
	/**
	 * Gets the usage tier id.
	 *
	 * @return the usage tier id
	 */
	public Long getUsageTierId() {
		return usageTierId;
	}
	
	/**
	 * Sets the usage tier id.
	 *
	 * @param usageTierId the new usage tier id
	 */
	public void setUsageTierId(Long usageTierId) {
		this.usageTierId = usageTierId;
	}
	
	/**
	 * Gets the range from.
	 *
	 * @return the range from
	 */
	public Long getRangeFrom() {
		return rangeFrom;
	}
	
	/**
	 * Sets the range from.
	 *
	 * @param rangeFrom the new range from
	 */
	public void setRangeFrom(Long rangeFrom) {
		this.rangeFrom = rangeFrom;
	}
	
	/**
	 * Gets the range to.
	 *
	 * @return the range to
	 */
	public Long getRangeTo() {
		return rangeTo;
	}
	
	/**
	 * Sets the range to.
	 *
	 * @param rangeTo the new range to
	 */
	public void setRangeTo(Long rangeTo) {
		this.rangeTo = rangeTo;
	}
	
	/**
	 * Gets the t 3 rate.
	 *
	 * @return the t 3 rate
	 */
	public Long getT3Rate() {
		return t3Rate;
	}
	
	/**
	 * Sets the t 3 rate.
	 *
	 * @param t3Rate the new t 3 rate
	 */
	public void setT3Rate(Long t3Rate) {
		this.t3Rate = t3Rate;
	}
	
	/**
	 * Gets the oc 3 rate.
	 *
	 * @return the oc 3 rate
	 */
	public Long getOc3Rate() {
		return oc3Rate;
	}
	
	/**
	 * Sets the oc 3 rate.
	 *
	 * @param oc3Rate the new oc 3 rate
	 */
	public void setOc3Rate(Long oc3Rate) {
		this.oc3Rate = oc3Rate;
	}
	
	/**
	 * Gets the oc 12 rate.
	 *
	 * @return the oc 12 rate
	 */
	public Long getOc12Rate() {
		return oc12Rate;
	}
	
	/**
	 * Sets the oc 12 rate.
	 *
	 * @param oc12Rate the new oc 12 rate
	 */
	public void setOc12Rate(Long oc12Rate) {
		this.oc12Rate = oc12Rate;
	}
	
	/**
	 * Gets the oc 48 rate.
	 *
	 * @return the oc 48 rate
	 */
	public Long getOc48Rate() {
		return oc48Rate;
	}
	
	/**
	 * Sets the oc 48 rate.
	 *
	 * @param oc48Rate the new oc 48 rate
	 */
	public void setOc48Rate(Long oc48Rate) {
		this.oc48Rate = oc48Rate;
	}
	
	/**
	 * Gets the gig rate.
	 *
	 * @return the gig rate
	 */
	public Long getGigRate() {
		return gigRate;
	}
	
	/**
	 * Sets the gig rate.
	 *
	 * @param gigRate the new gig rate
	 */
	public void setGigRate(Long gigRate) {
		this.gigRate = gigRate;
	}
	
	/**
	 * Gets the ethernet 10 gig.
	 *
	 * @return the ethernet 10 gig
	 */
	public Long getEthernet10Gig() {
		return ethernet10Gig;
	}
	
	/**
	 * Sets the ethernet 10 gig.
	 *
	 * @param ethernet10Gig the new ethernet 10 gig
	 */
	public void setEthernet10Gig(Long ethernet10Gig) {
		this.ethernet10Gig = ethernet10Gig;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "UsageTiers [usageTierId:" + usageTierId
				+ ", rangeFrom=" + rangeFrom + ", rangeTo=" + rangeTo
				+ ", t3Rate=" + t3Rate + ", oc3Rate=" + oc3Rate
				+ ", oc12Rate=" + oc12Rate + ", oc48Rate=" + oc48Rate
				+ ", gigRate=" + gigRate + ", ethernet10Gig=" + ethernet10Gig;
	}
}
