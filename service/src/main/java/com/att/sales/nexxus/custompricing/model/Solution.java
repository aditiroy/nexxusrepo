package com.att.sales.nexxus.custompricing.model;

import java.util.ArrayList;
import java.util.List;

import org.codehaus.jackson.map.annotate.JsonSerialize;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class Solution {

	private SolutionDeterminant solutionDeterminants; 
	
	private String userId;
	
	private String externalKey;
	
	private String dealId;
	
	@JsonProperty("versionNumber")
	private String versionNumber;
	
	private String revisionNumber;
	
	private String productNumber;

	private Long priceScenarioId;

	private String erateInd;

	private String bundleCode;

	private String promoCode;
	
	private String rlExpirationDate;
	
	private String rlQuoteUrl;
	
	private String rlDiscountApprovalType;
	
	private String dealStatus;
	
	private List<Offer> offers = new ArrayList<Offer>();
	
	private String rlType;
	
	private String saartAccountNumber;
	
	private String customerName;
}
