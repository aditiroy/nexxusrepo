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
public class Document {

	private Long siteId;
	
	private Long country;
	
	private String state;
	
	private Long asrItemID;
	
	private Long circuitId;
	
	private Long designId;
	
	private String macdType;
	
	private String fromInventory;
	
	private String typeOfInventory;
	
	private List<Component> component;

	
}
