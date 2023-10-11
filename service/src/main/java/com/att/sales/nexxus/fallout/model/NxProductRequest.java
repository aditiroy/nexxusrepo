package com.att.sales.nexxus.fallout.model;

import java.util.List;

import org.codehaus.jackson.map.annotate.JsonSerialize;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Getter;
import lombok.Setter;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
@Getter
@Setter
public class NxProductRequest {

	private String productName;
	private String inrEditEnabled;
	private List<NxRequests> nxRequests;
}
