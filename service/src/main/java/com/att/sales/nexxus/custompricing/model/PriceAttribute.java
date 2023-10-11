package com.att.sales.nexxus.custompricing.model;

import org.codehaus.jackson.map.annotate.JsonSerialize;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class PriceAttribute {

	private Long productRateId;
	
	private String beid;
	
	private Long priceCatalog;
	
	private Long localListPrice;
	
	private Long targetListPrice;
	
	private Long priceUnit;
	
	private String frequency;
	
	private Long discount;
	
	private Long quantity;
	
	private Long localNetPrice;
	
	private Long targetNetPrice;
	
	private Long localTotalPrice;
	
	private Long localCurrency;
	
	private Long targetCurrency;
	
	private Long priceInUSD;
	
	private String priceName;
	
	private String priceType;
	
	private String rdsPriceType;
	
	private Float requestedDiscount; 
	 
	private Float approvedDiscount; 
 
	private Double approvedNetRate;
	
	private Float netRate;
	
	private String isAccess;
	
}
