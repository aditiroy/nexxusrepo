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
public class DataProducts {
	private Boolean eplsWANAvailabilityFlag;
	@JsonProperty("FTTBData")
	private FTTBData fttbData;
	@JsonProperty("ProductBandwidthDetails")
	private List<ProductBandwidthDetails> productBandwidthDetails;
	private  String retrievedATTEthernetPOP;
	private String attDSLAvailabilityFlag;
	@Override
	public String toString() {
		return "DataProducts [eplsWANAvailabilityFlag=" + eplsWANAvailabilityFlag + "]";
	}
}
