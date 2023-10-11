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
public class GISLocationAttributes {
	
	private String globalLocationId;
	
	@JsonProperty("FieldedAddress")
	private FieldedAddress fieldedAddress;
	
	@JsonProperty("LocationProperties")
	private LocationProperties locationProperties;

}
