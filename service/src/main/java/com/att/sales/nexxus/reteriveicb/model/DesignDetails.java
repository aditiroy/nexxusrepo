package com.att.sales.nexxus.reteriveicb.model;

import java.util.List;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.map.annotate.JsonSerialize;

import lombok.Getter;
import lombok.Setter;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
@Getter
@Setter
public class DesignDetails {
	private String id; //Pass Nexxus site ID in this field. In future this can be any Id that will uniquely identify a site or a port or circuit.
	private String asrItemId;
	private List<ValidationIssues> validationIssues;
	
}
