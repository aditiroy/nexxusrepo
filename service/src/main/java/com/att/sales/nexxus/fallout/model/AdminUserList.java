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
public class AdminUserList {

	private Long rowId;	
	private String adminUserId;	
	private String fname;	
	private String mname;
	private String lname;
	private String telephone;
	private String email;
	private String userrole;
	private String activeYn;
}


