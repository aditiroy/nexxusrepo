package com.att.sales.nexxus.serviceValidation.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SAGLocationAttributes {
	@JsonProperty("SAGProperties")
	private SAGProperties sagProperties;
}
