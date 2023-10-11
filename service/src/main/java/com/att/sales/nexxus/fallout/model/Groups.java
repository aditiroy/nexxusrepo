package com.att.sales.nexxus.fallout.model;

import java.util.List;

import org.codehaus.jackson.map.annotate.JsonSerialize;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class Groups {

	private String groupId;	
	private String groupDesc;	
	private String nxRequestGroupId;	
	private String active;
	private String nXsolutionId;
	private String status;
	private String groupName;
	private String statusName;
	private List<NxRequests> nxRequests;
}
