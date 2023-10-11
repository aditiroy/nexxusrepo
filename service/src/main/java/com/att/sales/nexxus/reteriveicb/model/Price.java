package com.att.sales.nexxus.reteriveicb.model;


import org.codehaus.jackson.map.annotate.JsonSerialize;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonSerialize(include = JsonSerialize.Inclusion.NON_EMPTY)
@Setter
@Getter
@ToString
public class Price {
	@JsonProperty("RateId")
	private long rateId;
	@JsonProperty("RateDescription")
	private String rateDescription;
	@JsonProperty("FieldName")
	private String fieldName;
	@JsonProperty("PortCharge")
	private String portCharge;
	@JsonProperty("TypeOfRate")
	private String typeOfRate;
	@JsonProperty("Currency")
	private String currency;
	@JsonProperty("Rate")
	private double rate;

	
}
