package com.att.sales.nexxus.reteriveicb.model;

import org.codehaus.jackson.map.annotate.JsonSerialize;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;


/**
 * The Class NexxusTestRequest.
 */
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class NexxusTestRequest {
	
	/** The solution id. */
	private Long solutionId;
	
	/** The solution name. */
	private String solutionName;

	/**
	 * Gets the solution id.
	 *
	 * @return the solution id
	 */
	public Long getSolutionId() {
		return solutionId;
	}

	/**
	 * Sets the solution id.
	 *
	 * @param solutionId the new solution id
	 */
	public void setSolutionId(Long solutionId) {
		this.solutionId = solutionId;
	}

	/**
	 * Gets the solution name.
	 *
	 * @return the solution name
	 */
	public String getSolutionName() {
		return solutionName;
	}

	/**
	 * Sets the solution name.
	 *
	 * @param solutionName the new solution name
	 */
	public void setSolutionName(String solutionName) {
		this.solutionName = solutionName;
	}

}
