package com.att.sales.nexxus.custompricing.model;

import java.util.List;

import org.codehaus.jackson.map.annotate.JsonSerialize;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class CustomerDetail {

	private String firstName;	
	private String lastName;
	private String companyName;
	private String address;
	private String addr1;
	private String addr2;
	private String city;
	private String state;
	private String postalCode;
	private String country;
	private String mobile;
	private String telephone;
	private String email;
	
}
