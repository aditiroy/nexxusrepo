package com.att.sales.nexxus.reteriveicb.model;

/*
 * @Author: Akash Arya
 * 
 * 
 */


import java.util.List;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * The Class RetreiveICBPSPRequest.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RetreiveICBPSPRequest {
   
	/** The action determinants. */
	private List<ActionDeterminants> actionDeterminants;
	
	/** The solution. */
	private Solution solution;

	/**
	 * Gets the solution.
	 *
	 * @return the solution
	 */
	public Solution getSolution() {
		return solution;
	}

	/**
	 * Sets the solution.
	 *
	 * @param solution the new solution
	 */
	public void setSolution(Solution solution) {
		this.solution = solution;
	}

	public List<ActionDeterminants> getActionDeterminants() {
		return actionDeterminants;
	}

	public void setActionDeterminants(List<ActionDeterminants> actionDeterminants) {
		this.actionDeterminants = actionDeterminants;
	}

	
}
