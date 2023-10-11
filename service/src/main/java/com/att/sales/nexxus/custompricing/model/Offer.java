package com.att.sales.nexxus.custompricing.model;

import java.util.List;

import org.codehaus.jackson.map.annotate.JsonSerialize;

import com.att.sales.nexxus.serviceValidation.model.Locations;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class Offer {

	private Long offerId;
	
	private String bundleCode;
	
	private List<Credit> credits;
	
	private Document document;
	
	private Price prices;
	
	private List<Locations> locations;
	
}
