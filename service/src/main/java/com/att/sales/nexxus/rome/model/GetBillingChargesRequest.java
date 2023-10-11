package com.att.sales.nexxus.rome.model;

import java.util.List;

import org.codehaus.jackson.map.annotate.JsonSerialize;

import com.att.sales.nexxus.edf.model.products;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class GetBillingChargesRequest {
	private String nxSolutionId;
	private String attuid;
	private String optyId;
	private String billMonth;
	private String beginBillMonth;
	private String cpniApprover;
	private List<products> Products;
	private Object manageBillingPriceInventoryDataRequest;

	public Object getManageBillingPriceInventoryDataRequest() {
		return manageBillingPriceInventoryDataRequest;
	}

	public void setManageBillingPriceInventoryDataRequest(Object manageBillingPriceInventoryDataRequest) {
		this.manageBillingPriceInventoryDataRequest = manageBillingPriceInventoryDataRequest;
	}
}