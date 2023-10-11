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
public class CIRSpeedPrice {
	@JsonProperty("RateId")
	private String rateId;
	@JsonProperty("RateDescription")
	private String rateDescription;
	@JsonProperty("FieldName")
	private String fieldName;
	@JsonProperty("CIRTypeOfRate")
	private String cirTypeOfRate;
	@JsonProperty("CIRCurrency")
	private String cirCurrency;
	@JsonProperty("CIRListRate")
	private double cirListRate;
		
	@JsonProperty("RateCategory")
	private String rateCategory;
	
}
