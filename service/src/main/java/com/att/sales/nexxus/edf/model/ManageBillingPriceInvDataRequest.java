package com.att.sales.nexxus.edf.model;

import java.util.List;

import org.codehaus.jackson.map.annotate.JsonSerialize;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * The Class ManageBillingPriceInvDataRequest.
 */
@SuppressWarnings("serial")
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class ManageBillingPriceInvDataRequest {
	
	/** The nx solution id. */
	private String nxSolutionId;
	
	/** The attuid. */
	private String attuid;
	
	/** The opty id. */
	private String optyId;
	private Object manageBillingPriceAccountDataRequest;
	
	public Object getManageBillingPriceAccountDataRequest() {
		return manageBillingPriceAccountDataRequest;
	}
	public void setManageBillingPriceAccountDataRequest(Object manageBillingPriceAccountDataRequest) {
		this.manageBillingPriceAccountDataRequest = manageBillingPriceAccountDataRequest;
	}

	/** The cpni approver. */
	private String cpniApprover;

	/** The inventory list. */
	private List<PriceInventoryDataRequest> inventoryList;
	
	

	/**
	 * Gets the nx solution id.
	 *
	 * @return the nx solution id
	 */
	@JsonProperty("nxSolutionId")
	public String getNxSolutionId() {
		return nxSolutionId;
	}

	

	/**
	 * Sets the nx solution id.
	 *
	 * @param nxSolutionId the new nx solution id
	 */
	public void setNxSolutionId(String nxSolutionId) {
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
	
	/**
	 * Gets the cpni approver.
	 *
	 * @return the cpni approver
	 */
	public String getCpniApprover() {
		return cpniApprover;
	}

	/**
	 * Sets the cpni approver.
	 *
	 * @param cpniApprover the new cpni approver
	 */
	public void setCpniApprover(String cpniApprover) {
		this.cpniApprover = cpniApprover;
	}

	/**
	 * Gets the inventory list.
	 *
	 * @return the inventory list
	 */
	@JsonProperty("inventoryList")
	public List<PriceInventoryDataRequest> getInventoryList() {
		return inventoryList;
	}

	/**
	 * Sets the inventory list.
	 *
	 * @param inventoryList the new inventory list
	 */
	public void setInventoryList(List<PriceInventoryDataRequest> inventoryList) {
		this.inventoryList = inventoryList;
	}

}
