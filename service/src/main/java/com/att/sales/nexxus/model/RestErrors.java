package com.att.sales.nexxus.model;

import org.codehaus.jackson.map.annotate.JsonSerialize;

import lombok.Getter;
import lombok.Setter;

@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
@Getter
@Setter
public class RestErrors {
	private String circuitId;
	private String messages;
	private String nxsiteMatchingId;
}
