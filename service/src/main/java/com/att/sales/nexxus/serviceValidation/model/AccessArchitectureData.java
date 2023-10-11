package com.att.sales.nexxus.serviceValidation.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;
@Getter
@Setter
public class AccessArchitectureData {
	private String accessArchitecture;
	@JsonProperty("PhysicalInterfaceData")
	private List<PhysicalInterfaceData> physicalInterfaceData;
}
