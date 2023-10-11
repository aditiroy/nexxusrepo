package com.att.sales.nexxus.model;

import java.util.List;

import com.att.sales.framework.model.ServiceResponse;
import com.att.sales.framework.validation.APIFieldProperty;

/**
 * The Class APUiResponse.
 */
public class APUiResponse extends ServiceResponse{
	
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;
	
	private Long nxSolutionId;
	
	private String description;
	
	/** The access pricing ui response. */
	@APIFieldProperty(required=true)
	private List<AccessPricingUiResponse> accessPricingUiResponse;
	
	/** The Acces pricing failed dq id. */
	@APIFieldProperty(required=true, allowableValues="List of failed Quotes Type String")
	private AccesPricingFailedDqId AccesPricingFailedDqId;
	
	@APIFieldProperty(required=true, allowableValues="List of failed Quotes Type String")
	private AccesPricingDuplicatetokenid AccesPricingDuplicatetokenid;

	/**
	 * Gets the access pricing ui response.
	 *
	 * @return the access pricing ui response
	 */
	public List<AccessPricingUiResponse> getAccessPricingUiResponse() {
		return accessPricingUiResponse;
	}

	/**
	 * Sets the access pricing ui response.
	 *
	 * @param accessPricingUiResponse the new access pricing ui response
	 */
	public void setAccessPricingUiResponse(List<AccessPricingUiResponse> accessPricingUiResponse) {
		this.accessPricingUiResponse = accessPricingUiResponse;
	}

	/**
	 * Gets the acces pricing failed dq id.
	 *
	 * @return the accesPricingFailedDqId
	 */
	public AccesPricingFailedDqId getAccesPricingFailedDqId() {
		return AccesPricingFailedDqId;
	}

	public AccesPricingDuplicatetokenid getAccesPricingDuplicatetokenid() {
		return AccesPricingDuplicatetokenid;
	}

	/**
	 * Sets the acces pricing failed dq id.
	 *
	 * @param accesPricingFailedDqId the accesPricingFailedDqId to set
	 */
	public void setAccesPricingFailedDqId(AccesPricingFailedDqId accesPricingFailedDqId) {
		AccesPricingFailedDqId = accesPricingFailedDqId;
	}
	
	public void setAccesPricingDuplicatetokenid(AccesPricingDuplicatetokenid accesPricingDuplicatetokenid) {
		AccesPricingDuplicatetokenid = accesPricingDuplicatetokenid;
	}

	public Long getNxSolutionId() {
		return nxSolutionId;
	}

	public void setNxSolutionId(Long nxSolutionId) {
		this.nxSolutionId = nxSolutionId;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
	
}
