/**
 * 
 */
package com.att.sales.nexxus.rome.model;

import org.codehaus.jackson.map.annotate.JsonSerialize;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;


/**
 * The Class GetOptyRequest.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class GetOptyRequest {
	
	/** The hr id. */
	@JsonProperty("hrId")
	private String hrId;
	
	/** The opty id. */
	@JsonProperty("optyId")
	private String optyId;
		
	/** The solution id. */
	@JsonProperty("solutionId")
	private Long solutionId;
	
	/** The action. */
	@JsonProperty("action")
	private String action;
	
	/** The attuid. */
	@JsonProperty("attuid")
	private String attuid;
	
	/** The nx solution id. */
	@JsonProperty("nxSolutionId")
	private Long nxSolutionId;
	
	/** The solution description. */
	@JsonProperty("solutionDescription")
	private String solutionDescription;
	
	/** The solution description. */
	@JsonProperty("actionPerformedBy")
	private String actionPerformedBy;
	
	/**
	 * Gets the hr id.
	 *
	 * @return the hrId
	 */
	public String getHrId() {
		return hrId;
	}

	/**
	 * Sets the hr id.
	 *
	 * @param hrId the hrId to set
	 */
	public void setHrId(String hrId) {
		this.hrId = hrId;
	}

	/**
	 * Gets the opty id.
	 *
	 * @return the optyId
	 */
	public String getOptyId() {
		return optyId;
	}

	/**
	 * Sets the opty id.
	 *
	 * @param optyId the optyId to set
	 */
	public void setOptyId(String optyId) {
		this.optyId = optyId;
	}

	/**
	 * Gets the solution id.
	 *
	 * @return the solutionId
	 */
	public Long getSolutionId() {
		return solutionId;
	}

	/**
	 * Sets the solution id.
	 *
	 * @param solutionId the solutionId to set
	 */
	public void setSolutionId(Long solutionId) {
		this.solutionId = solutionId;
	}

	/**
	 * Gets the action.
	 *
	 * @return the action
	 */
	public String getAction() {
		return action;
	}

	/**
	 * Sets the action.
	 *
	 * @param action the new action
	 */
	public void setAction(String action) {
		this.action = action;
	}

	/**
	 * Gets the attuid.
	 *
	 * @return the attuid
	 */
	public String getAttuid() {
		return attuid;
	}

	/**
	 * Sets the attuid.
	 *
	 * @param attuid the new attuid
	 */
	public void setAttuid(String attuid) {
		this.attuid = attuid;
	}

	/**
	 * Gets the nx solution id.
	 *
	 * @return the nx solution id
	 */
	public Long getNxSolutionId() {
		return nxSolutionId;
	}

	/**
	 * Sets the nx solution id.
	 *
	 * @param nxSolutionId the new nx solution id
	 */
	public void setNxSolutionId(Long nxSolutionId) {
		this.nxSolutionId = nxSolutionId;
	}

	/**
	 * Gets the solution description.
	 *
	 * @return the solution description
	 */
	public String getSolutionDescription() {
		return solutionDescription;
	}

	/**
	 * Sets the solution description.
	 *
	 * @param solutionDescription the new solution description
	 */
	public void setSolutionDescription(String solutionDescription) {
		this.solutionDescription = solutionDescription;
	}

	public String getActionPerformedBy() {
		return actionPerformedBy;
	}

	public void setActionPerformedBy(String actionPerformedBy) {
		this.actionPerformedBy = actionPerformedBy;
	}
	
	
		
}
