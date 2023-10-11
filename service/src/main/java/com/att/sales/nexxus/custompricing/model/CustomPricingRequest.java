package com.att.sales.nexxus.custompricing.model;

import java.util.List;

import org.codehaus.jackson.map.annotate.JsonSerialize;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class CustomPricingRequest {

	private List<ActionDeterminant> actionDeterminants;
	
	private Solution solution;
	
	private String userId;
	
	private Long externalKey;
	
	private String dealNumber;
	
	private String versionNumber;
	
	private String revisionNumber;
	
	private String productNumber;
	
}
