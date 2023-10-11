package com.att.sales.nexxus.reteriveicb.model;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.map.annotate.JsonSerialize;


/**
 * The Class MilesResult.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class MilesResult {
	
	/** The att co. */
	private String attCo;
	
	/** The miles. */
	private Long miles;
	
	/** The specified pop diff miles. */
	private Long specifiedPopDiffMiles;
	
	/**
	 * Gets the att co.
	 *
	 * @return the att co
	 */
	public String getAttCo() {
		return attCo;
	}
	
	/**
	 * Sets the att co.
	 *
	 * @param attCo the new att co
	 */
	public void setAttCo(String attCo) {
		this.attCo = attCo;
	}
	
	/**
	 * Gets the miles.
	 *
	 * @return the miles
	 */
	public Long getMiles() {
		return miles;
	}
	
	/**
	 * Sets the miles.
	 *
	 * @param miles the new miles
	 */
	public void setMiles(Long miles) {
		this.miles = miles;
	}
	
	/**
	 * Gets the specified pop diff miles.
	 *
	 * @return the specified pop diff miles
	 */
	public Long getSpecifiedPopDiffMiles() {
		return specifiedPopDiffMiles;
	}
	
	/**
	 * Sets the specified pop diff miles.
	 *
	 * @param specifiedPopDiffMiles the new specified pop diff miles
	 */
	public void setSpecifiedPopDiffMiles(Long specifiedPopDiffMiles) {
		this.specifiedPopDiffMiles = specifiedPopDiffMiles;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "MilesResult [attCo:"
				+ attCo + ", miles=" + miles + ", specifiedPopDiffMiles="+specifiedPopDiffMiles;
	}
}
