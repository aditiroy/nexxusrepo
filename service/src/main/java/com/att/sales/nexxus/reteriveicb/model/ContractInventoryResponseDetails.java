package com.att.sales.nexxus.reteriveicb.model;

import org.codehaus.jackson.map.annotate.JsonSerialize;

import com.att.sales.framework.model.ServiceResponse;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonSerialize(include = JsonSerialize.Inclusion.NON_EMPTY)
public class ContractInventoryResponseDetails extends ServiceResponse{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String contractNumber;
	private String txnId;
	private String ssdfResponse;
	
	public String getSsdfResponse() {
		return ssdfResponse;
	}
	public void setSsdfResponse(String ssdfResponse) {
		this.ssdfResponse = ssdfResponse;
	}
	public String getTxnId() {
		return txnId;
	}
	public void setTxnId(String txnId) {
		this.txnId = txnId;
	}
	public String getContractNumber() {
		return contractNumber;
	}
	public void setContractNumber(String contractNumber) {
		this.contractNumber = contractNumber;
	}
	
}
