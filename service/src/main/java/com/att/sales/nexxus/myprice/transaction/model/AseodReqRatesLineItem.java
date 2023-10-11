package com.att.sales.nexxus.myprice.transaction.model;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.map.annotate.JsonSerialize;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 
 * @author Laxman Honawad
 *
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
@Getter
@Setter
@NoArgsConstructor
public class AseodReqRatesLineItem {

	@JsonProperty("usoc")
	private String usoc;

	@JsonProperty("price")
	private String price;

	@JsonProperty("cosType")
	private String cosType;
	
	@JsonProperty("pricingTier")
	private String pricingTier;
	
	@JsonProperty("portInd")
	private String portInd;
	
}
