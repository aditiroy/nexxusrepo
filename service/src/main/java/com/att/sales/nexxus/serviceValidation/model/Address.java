package com.att.sales.nexxus.serviceValidation.model;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.map.annotate.JsonSerialize;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Getter;
import lombok.Setter;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
@Getter
@Setter
public class Address {
	private String city;
	private String state;
	private String postalCode;
	private String unitType; 
	private String unitValue; 
	private String structureType; 
	private String structureValue; 
	private String levelType; 
	private String levelValue; 
	@JsonIgnore
	private String nxSiteId;
	@JsonIgnore
	private String documentNumber;
	

}
