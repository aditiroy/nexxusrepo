package com.att.sales.nexxus.serviceValidation.model;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.map.annotate.JsonSerialize;

import lombok.Getter;
import lombok.Setter;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
@Getter
@Setter
public class FTTBData {
	
	private BuildingFiberStatus buildingFiberStatus;
	private String servingWireCenterCLLI;
	 
	@Override
	public String toString() {
		return "FTTBData [buildingFiberStatus=" + buildingFiberStatus + "]";
	}
}
