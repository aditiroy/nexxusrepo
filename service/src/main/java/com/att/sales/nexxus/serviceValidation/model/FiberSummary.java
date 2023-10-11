package com.att.sales.nexxus.serviceValidation.model;

import java.util.List;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.map.annotate.JsonSerialize;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
@Getter
@Setter
public class FiberSummary {
	private String tier;
	private String pricingTier;
	private String gponLitIndicator;
	private Boolean fapIndicator;
	private Boolean fiberNearbyIndicator;
	@JsonProperty("ProductIntervalDetails")
	private List<ProductIntervalDetails> productIntervalDetails;
}
