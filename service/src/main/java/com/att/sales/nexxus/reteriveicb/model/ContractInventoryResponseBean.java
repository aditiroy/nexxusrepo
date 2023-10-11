package com.att.sales.nexxus.reteriveicb.model;

import org.codehaus.jackson.map.annotate.JsonSerialize;

import com.att.sales.framework.model.ServiceResponse;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonSerialize(include = JsonSerialize.Inclusion.NON_EMPTY)
public class ContractInventoryResponseBean extends ServiceResponse{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private ContractInventoryResponseDetails contractInventoryResponse;

	public ContractInventoryResponseDetails getContractInventoryResponse() {
		return contractInventoryResponse;
	}

	public void setContractInventoryResponse(ContractInventoryResponseDetails contractInventoryResponse) {
		this.contractInventoryResponse = contractInventoryResponse;
	}

}
