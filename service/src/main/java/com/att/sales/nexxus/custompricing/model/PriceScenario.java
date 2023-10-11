package com.att.sales.nexxus.custompricing.model;

import java.util.List;

import org.codehaus.jackson.map.annotate.JsonSerialize;

import com.att.sales.nexxus.myprice.transaction.model.AllIncPrices3PA;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class PriceScenario {

	private Long priceScenarioId;
	
	private Integer revisionNumber;
	
	private List<AllIncPrice> allIncPrices;
	
	private AllIncPrices3PA allIncPricesTpa;
	
	private List<ComponentDetail> componentDetails;
	
	private String rlExpirationDate;
	
	private String rlQuoteUrl;
	
	private String rlDiscountApprovalType;
	
	private String dealStatus;
	
	private Long transactionId;
	
	private String rlType;
}
