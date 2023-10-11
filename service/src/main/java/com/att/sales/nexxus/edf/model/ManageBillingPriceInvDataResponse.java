package com.att.sales.nexxus.edf.model;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.map.annotate.JsonSerialize;

import com.att.sales.framework.model.ServiceResponse;

/**
 * The Class ManageBillingPriceInvDataResponse.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class ManageBillingPriceInvDataResponse extends ServiceResponse {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;

	/** The nx solution id. */
	private Long nxSolutionId;
	
	/** The attuid. */
	private String attuid;
	
	/** The opty id. */
	private String optyId;
	
	/** The nx solution desc. */
	private String nxSolutionDesc;
	

	public String getNxSolutionDesc() {
		return nxSolutionDesc;
	}

	public void setNxSolutionDesc(String nxSolutionDesc) {
		this.nxSolutionDesc = nxSolutionDesc;
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
	 * Gets the opty id.
	 *
	 * @return the opty id
	 */
	public String getOptyId() {
		return optyId;
	}

	/**
	 * Sets the opty id.
	 *
	 * @param optyId the new opty id
	 */
	public void setOptyId(String optyId) {
		this.optyId = optyId;
	}

}
