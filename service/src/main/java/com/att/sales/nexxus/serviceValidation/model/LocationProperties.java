package com.att.sales.nexxus.serviceValidation.model;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.map.annotate.JsonSerialize;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
@Getter
@Setter
public class LocationProperties {
	private String matchStatus;
	private String buildingClli;
	private String regionFranchiseStatus;
	private String addressMatchCode;
	private String swcCLLI;
	@JsonProperty("primaryNpaNxx")
	private PrimaryNpaNxx primaryNpaNxx ;
	private String localProviderName;
	private String lataCode;
}
