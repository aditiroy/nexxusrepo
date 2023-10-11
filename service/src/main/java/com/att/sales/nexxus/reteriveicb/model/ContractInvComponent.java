package com.att.sales.nexxus.reteriveicb.model;

import java.util.List;

import org.codehaus.jackson.map.annotate.JsonSerialize;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonSerialize(include = JsonSerialize.Inclusion.NON_EMPTY)
public class ContractInvComponent {
	private String componentCodeType;
	private List<ContractRates> contractRates;

	public String getComponentCodeType() {
		return componentCodeType;
	}

	public void setComponentCodeType(String componentCodeType) {
		this.componentCodeType = componentCodeType;
	}

	public List<ContractRates> getContractRates() {
		return contractRates;
	}

	public void setContractRates(List<ContractRates> contractRates) {
		this.contractRates = contractRates;
	}

	@Override
	public String toString() {
		return "ContractInvComponent [componentCodeType=" + componentCodeType + ", contractRates=" + contractRates
				+ "]";
	}

}
