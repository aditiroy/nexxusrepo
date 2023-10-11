package com.att.sales.nexxus.serviceValidation.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;
@Getter
@Setter
public class BandwidthData {
	private String serviceType;
	private String productCategoryName;
	private List<String> transportType;
	private String productType;
	private String downStreamSpeed;
	private String upStreamSpeed;
	@JsonProperty("AccessArchitectureData")
	private List<AccessArchitectureData> accessArchitectureData;
	

}
