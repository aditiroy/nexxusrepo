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
public class SDNMRCPrice {
	@JsonProperty("RateId")
	private String rateId;
	@JsonProperty("RateDescription")
	private String rateDescription;
	@JsonProperty("RateType")
	private String rateType;
	@JsonProperty("RateCurrency")
	private String rateCurrency;
	@JsonProperty("Rate")
	private double rate;
	@JsonProperty("RateCategory")
	private String rateCategory;

}
