package com.att.sales.nexxus.userdetails.model;


import org.codehaus.jackson.map.annotate.JsonSerialize;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;


/**
 * The Class ConsumerDetailRequest.
 *
 * @author aa316k
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class ConsumerDetailRequest {
	
	/** The action type. */
	private String actionType;
	
	/** The attuid. */
	private String attuid;
	
	/** The nx solution id. */
	private Long nxSolutionId;
	
	/** The nx user id. */
	private String actionPerformedBy;
	
	/**
	 * Gets the action type.
	 *
	 * @return the action type
	 */
	public String getActionType() {
		return actionType;
	}
	
	/**
	 * Sets the action type.
	 *
	 * @param actionType the new action type
	 */
	public void setActionType(String actionType) {
		this.actionType = actionType;
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

	public String getActionPerformedBy() {
		return actionPerformedBy;
	}

	public void setActionPerformedBy(String actionPerformedBy) {
		this.actionPerformedBy = actionPerformedBy;
	}



}
