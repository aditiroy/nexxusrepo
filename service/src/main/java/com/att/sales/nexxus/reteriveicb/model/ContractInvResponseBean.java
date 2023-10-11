package com.att.sales.nexxus.reteriveicb.model;

import org.codehaus.jackson.map.annotate.JsonSerialize;

import com.att.sales.framework.model.ServiceResponse;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonSerialize(include = JsonSerialize.Inclusion.NON_EMPTY)
public class ContractInvResponseBean extends ServiceResponse {
		/**
		 * 
		 */
	private static final long serialVersionUID = 1L;
	private ContractInvResponse contractInvResponse;
	private String responseStatus;
	 public ContractInvResponse getContractInvResponse() {
		return contractInvResponse;
	}

	public void setContractInvResponse(ContractInvResponse contractInvResponse) {
		this.contractInvResponse = contractInvResponse;
	}
	

	public String getResponseStatus() {
		return responseStatus;
	}

	public void setResponseStatus(String responseStatus) {
		this.responseStatus = responseStatus;
	}

	@Override
	public String toString() {
		return "ContractInvResponseBean [contractInvResponse=" + contractInvResponse + "]";
	}

}
