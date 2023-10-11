package com.att.sales.nexxus.reteriveicb.model;


import org.codehaus.jackson.map.annotate.JsonSerialize;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonSerialize(include = JsonSerialize.Inclusion.NON_EMPTY)
@Getter
@Setter
@ToString
public class CIRSpeed {
	@JsonProperty("FieldName")
	private String fieldName;
	@JsonProperty("CIR")
	private String cir;
	@JsonProperty("CIRUnits")
	private String cirUnits;
	@JsonProperty("CIRonContract")
	private String cironContract;
	@JsonProperty("Price")
	private CIRSpeedPrice price;
		
}
