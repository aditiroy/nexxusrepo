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
public class SDNEthernetContractResponse {
	/**
	 * 
	 */
	@JsonProperty("ContractID")
	private String contractID;
	@JsonProperty("ContractTerm")
	private String contractTerm;
	@JsonProperty("ContractICB")
	private String contractICB;
	@JsonProperty("NetPricePercentage")
	private String netPricePercentage;
	@JsonProperty("OfferId")
	private long offerId;
	@JsonProperty("ProductId")
	private long productId;
	@JsonProperty("PricePlanId")
	private long pricePlanId;
	@JsonProperty("RatePlanId")
	private long ratePlanId;
	@JsonProperty("SDNCharges")
	private SDNCharges sdnCharges;
	@JsonProperty("MarketSegment")
	private String marketSegment;

}
