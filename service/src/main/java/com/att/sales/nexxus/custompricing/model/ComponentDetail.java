package com.att.sales.nexxus.custompricing.model;

import java.util.ArrayList;
import java.util.List;

import org.codehaus.jackson.map.annotate.JsonSerialize;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class ComponentDetail {

	private ComponentAttributes componentAttributes;
	
	private Long componentId;
	
	private Long componentParentId;
	
	private String componentType;
	
	private String asrItemID;
	
	private String specialConstructionNRCCharge;
	
	private Long term;
	
	private String jurisdiction;
	
	private List<PriceAttribute> priceAttributes = new ArrayList<PriceAttribute>();
	
	private String socVersion;
	
	private String siteId;
	
	private String siteCountry;
	
	private String ethTokenId;

	private String isAccess;
	
	private Long ratePlanId;
	
	private String socDate;
	
	private Long externalRatePlanId;

}
