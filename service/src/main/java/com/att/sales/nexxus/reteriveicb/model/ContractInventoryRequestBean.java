package com.att.sales.nexxus.reteriveicb.model;

import org.codehaus.jackson.map.annotate.JsonSerialize;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonSerialize(include = JsonSerialize.Inclusion.NON_EMPTY)
public class ContractInventoryRequestBean {
	private ContractInventoryRequestDetails contractInventoryRequest;

	public ContractInventoryRequestDetails getContractInventoryRequest() {
		return contractInventoryRequest;
	}

	public void setContractInventoryRequest(ContractInventoryRequestDetails contractInventoryRequest) {
		this.contractInventoryRequest = contractInventoryRequest;
	}

}
