package com.att.sales.nexxus.reteriveicb.model;

import java.util.List;

import org.codehaus.jackson.map.annotate.JsonSerialize;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonSerialize(include = JsonSerialize.Inclusion.NON_EMPTY)
public class ContractInvResponse {
	private String contractId;
	private String contractTerm;
	private String contractIcb;
	private String netpricePercentage;
	private long pricePlanId;
	private long ratePlanId;
	private List<ContractInvComponent> component;
	public String getContractId() {
		return contractId;
	}
	public void setContractId(String contractId) {
		this.contractId = contractId;
	}
	public String getContractTerm() {
		return contractTerm;
	}
	public void setContractTerm(String contractTerm) {
		this.contractTerm = contractTerm;
	}
	public String getContractIcb() {
		return contractIcb;
	}
	public void setContractIcb(String contractIcb) {
		this.contractIcb = contractIcb;
	}
	public String getNetpricePercentage() {
		return netpricePercentage;
	}
	public void setNetpricePercentage(String netpricePercentage) {
		this.netpricePercentage = netpricePercentage;
	}
	public long getPricePlanId() {
		return pricePlanId;
	}
	public void setPricePlanId(long pricePlanId) {
		this.pricePlanId = pricePlanId;
	}
	public long getRatePlanId() {
		return ratePlanId;
	}
	public void setRatePlanId(long ratePlanId) {
		this.ratePlanId = ratePlanId;
	}
	public List<ContractInvComponent> getComponent() {
		return component;
	}
	public void setComponent(List<ContractInvComponent> component) {
		this.component = component;
	}
	@Override
	public String toString() {
		return "ContractInvResponse [contractId=" + contractId + ", contractTerm=" + contractTerm + ", contractIcb="
				+ contractIcb + ", netpricePercentage=" + netpricePercentage + ", pricePlanId=" + pricePlanId
				+ ", ratePlanId=" + ratePlanId + ", component=" + component + "]";
	}

}
