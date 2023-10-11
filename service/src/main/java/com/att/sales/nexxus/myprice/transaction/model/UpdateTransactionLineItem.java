package com.att.sales.nexxus.myprice.transaction.model;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.map.annotate.JsonSerialize;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 
 * @author Laxman Honawad
 *
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
@JsonInclude(Include.NON_NULL)
@Getter
@Setter
@NoArgsConstructor
public class UpdateTransactionLineItem {
	
	@JsonProperty("_document_number")
	private String documentNumber;
	
/*	@JsonProperty("lii_requestedDiscountPctgNRC_ql")
	private String requestedDiscountPctgNRC;
	
	@JsonProperty("lii_requestedDiscountPctgMRC_ql")
	private String requestedDiscountPctgMRC;*/
	
	@JsonProperty("lii_requestedEffectivePriceMRC_ql")
	private GetTransactionLineItemPrice requestedEffectivePriceMRC;
	
	@JsonProperty("lii_requestedEffectivePriceNRC_ql")
	private GetTransactionLineItemPrice requestedEffectivePriceNRC;
	
	@JsonProperty("lii_contractTerm_ql")
	private Long term;
	
	@JsonProperty("wi_customPriceList_q")
	private String customPriceList;
	
	/*
	 * @JsonProperty("wi_isErate_q") private boolean erateInd;
	 */
}
