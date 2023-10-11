package com.att.sales.nexxus.reteriveicb.model;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.map.annotate.JsonSerialize;


/**
 * The Class SolutionDeterminants.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class SolutionDeterminants {
	
	/** The solution type. */
	private String solutionType;
	
	/** The icb ind. */
	private String icbInd;
	
	/** The mvl ind. */
	private String mvlInd;

	/**
	 * Gets the solution type.
	 *
	 * @return the solution type
	 */
	public String getSolutionType() {
		return solutionType;
	}

	/**
	 * Sets the solution type.
	 *
	 * @param solutionType the new solution type
	 */
	public void setSolutionType(String solutionType) {
		this.solutionType = solutionType;
	}

	/**
	 * Gets the icb ind.
	 *
	 * @return the icb ind
	 */
	public String getIcbInd() {
		return icbInd;
	}

	/**
	 * Sets the icb ind.
	 *
	 * @param icbInd the new icb ind
	 */
	public void setIcbInd(String icbInd) {
		this.icbInd = icbInd;
	}
	
	/**
	 * Gets the mvl ind.
	 *
	 * @return the mvl ind
	 */
	public String getMvlInd() {
		return mvlInd;
	}

	/**
	 * Sets the mvl ind.
	 *
	 * @param mvlInd the new mvl ind
	 */
	public void setMvlInd(String mvlInd) {
		this.mvlInd = mvlInd;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "SolutionDeterminants [solutionType=" + solutionType + ", icbInd=" + icbInd + "]";
	}

}
