package com.att.sales.nexxus.pricing.model;

import org.codehaus.jackson.map.annotate.JsonSerialize;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class Design {
	private String designStatus;
	private String asrItemId;
	private String cancelReason;
	private DesignDetails designDetails;

	public String getDesignStatus() {
		return designStatus;
	}

	public void setDesignStatus(String designStatus) {
		this.designStatus = designStatus;
	}

	public String getAsrItemId() {
		return asrItemId;
	}

	public void setAsrItemId(String asrItemId) {
		this.asrItemId = asrItemId;
	}

	public String getCancelReason() {
		return cancelReason;
	}

	public void setCancelReason(String cancelReason) {
		this.cancelReason = cancelReason;
	}

	public DesignDetails getDesignDetails() {
		return designDetails;
	}

	public void setDesignDetails(DesignDetails designDetails) {
		this.designDetails = designDetails;
	}

}
