package com.att.sales.nexxus.fallout.model;

import org.codehaus.jackson.map.annotate.JsonSerialize;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Getter;
import lombok.Setter;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
@Getter
@Setter
public class NxRequests {
	
	private Long nxReqId;

	private String product;

	private Long status;

	private String nxReqDesc;
	
	private String statusName;
	
	private String flowType;
	
	private Long nxGroupRequestId;
	
	private String submitted;
	

}
