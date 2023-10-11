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
public class SDNMRC {
	@JsonProperty("MRCCharge")
	private String mrcCharge;
	@JsonProperty("FieldName")
	private String fieldName;
	@JsonProperty("PortConMRConContract")
	private String portConMRConContract;
	@JsonProperty("Price")
	private SDNMRCPrice price;
		
}
