package com.att.sales.nexxus.reteriveicb.model;

import java.util.List;

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
public class CosPremium {
	@JsonProperty("FieldName")
	private String fieldName;
	@JsonProperty("CosPremium")
	private String cosPremium;
	@JsonProperty("COSonContract")
	private String cosonContract;
	@JsonProperty("CIRSpeed")
	private List<CIRSpeed> cirSpeed;

	
}
