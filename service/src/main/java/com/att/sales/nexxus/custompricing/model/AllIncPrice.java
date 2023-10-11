package com.att.sales.nexxus.custompricing.model;

import org.codehaus.jackson.map.annotate.JsonSerialize;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class AllIncPrice {

	private String usocId;
	
	private String pbi;
	
	private String netRate;
	
	private String cosType;
	
	private String pricingTier;
	
	private String portInd;
}
