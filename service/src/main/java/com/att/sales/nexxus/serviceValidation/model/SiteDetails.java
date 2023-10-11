package com.att.sales.nexxus.serviceValidation.model;

import java.util.List;

import org.codehaus.jackson.map.annotate.JsonSerialize;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @author KumariMuktta
 *
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
@Getter
@Setter
@NoArgsConstructor
public class SiteDetails {
	
	private String siteInfoSource;

	private String nxSiteId;
	 
	private String name;
	
	private String globalLocationId;
	
	private String clli;

	private String addressLine;

	private String city;

	private String state;

	private String postalCode;

	private String country;

	private String room;

	private String floor;

	private String building;
	
	private String unitType; 
	 
	private String unitValue; 
 
	private String structureType; 
 
	private String structureValue; 
 
	private String levelType; 
 
	private String levelValue; 

	private List<ConfigurationDetails> configurationDetails;
	
}