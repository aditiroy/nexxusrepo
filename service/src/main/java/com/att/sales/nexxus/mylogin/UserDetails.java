package com.att.sales.nexxus.mylogin;

import java.util.Date;

import org.codehaus.jackson.map.annotate.JsonSerialize;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Getter;
import lombok.Setter;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
@Getter
@Setter
public class UserDetails {
	
	private String firstName;
	private String lastName;
	private String login;
	private String email;
	private String status;
	private String profileName;
	private String externalSsoId;
	private String middleInitial;
	private Date lastLogonDate;
	private String approvalDate;
}
