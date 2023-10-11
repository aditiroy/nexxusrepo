package com.att.sales.nexxus.myprice.transaction.model;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.map.annotate.JsonSerialize;

import com.fasterxml.jackson.annotation.JsonProperty;

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
@Getter
@Setter
@NoArgsConstructor
public class GetTransactionLineItem {

	@JsonProperty("lii_uSOC_ql")
	private String usocId;

	@JsonProperty("_document_number")
	private String documentNumber;

	@JsonProperty("_line_bom_id")
	private String bomId;
	
	@JsonProperty("rl_extPriceMRC_ql")
	private String extendedPriceMRC;
	
	@JsonProperty("rl_extPriceNRC_ql")
	private String extendedPriceNRC;
	
	@JsonProperty("_line_bom_part_number")
	private String bomPartNumber;
	
	@JsonProperty("_parent_doc_number")
	private String parentDocNumber;
		
	@JsonProperty("lii_approvedDiscountPctgNRC_ql")
	private String approvedDiscountPctgNRC;

	@JsonProperty("lii_approvedEffectivePriceNRC_ql")
	private GetTransactionLineItemPriceNew approvedEffectivePriceNRC;

	@JsonProperty("lii_approvedNetEffectivePriceNRC_ql")
	private GetTransactionLineItemPriceNew approvedNetEffectivePriceNRC;

	@JsonProperty("lii_approvedDiscountPctgMRC_ql")
	private String approvedDiscountPctgMRC;

	@JsonProperty("lii_approvedEffectivePriceMRC_ql")
	private GetTransactionLineItemPriceNew approvedEffectivePriceMRC;

	@JsonProperty("lii_approvedNetEffectivePriceMRC_ql")
	private GetTransactionLineItemPriceNew approvedNetEffectivePriceMRC;

	@JsonProperty("_model_product_line_name")
	private String modelProductLineName;

	@JsonProperty("_model_variable_name")
	private String modelVariableName;

	@JsonProperty("lii_nxSiteId_ql")
	private String nxSiteId;

	@JsonProperty("lii_countryAbbrev_ql")
	private String nxSiteCountry;
	
	@JsonProperty("lii_country_ql")
	private String nxSiteCountryName;
	
	@JsonProperty("lii_specialConstructionEffectiveCharge_ql")
	private GetTransactionLineItemPrice specialConstructionAppNRC;

	@JsonProperty("lii_asrID_ql")
	private String asrItemId;
	
	@JsonProperty("lii_contractTerm_ql")
	private Long term;
	
	@JsonProperty("lii_jurisdiction_ql")
	private String jurisdiction;
	
	@JsonProperty("productType_l")
	private String productType;
	
	@JsonProperty("lii_requestedEffectivePriceMRC_ql")
	private GetTransactionLineItemPriceNew requestedEffectivePriceMRC;

	@JsonProperty("lii_requestedEffectivePriceNRC_ql")
	private GetTransactionLineItemPriceNew requestedEffectivePriceNRC;

	@JsonProperty("wi_uniqueID_ql")
	private String uniqueIds;

	@JsonProperty("lii_Token_ql")
	private String tokenId;
	
	@JsonProperty("lii_productVariation_ql")
	private String productVariation;
	
	@JsonProperty("lii_sOCDate_ql")
	private String socVersion;
	
	@JsonProperty("rl_isProductRow_ql")
	private String isProductRow;
	
	@JsonProperty("rl_product_ql")
	private String rlProduct;
	
	@JsonProperty("lii_siteName_ql")
	private String nxSiteName;
	
	@JsonProperty("_line_bom_parent_id")
	private String bomParentId;
	
	@JsonProperty("_parent_line_item")
	private String parentLineitem;
	
	@JsonProperty("wl_int_model_asr")
	private String modelAsr;
	
	@JsonProperty("wl_int_model_sites")
	private String modelSites;
	
	@JsonProperty("lii_externalKey_ql")
	private String externalKey;
	
	@JsonProperty("lii_circuitNumber_ql")
	private String circuitNumber;
	
	@JsonProperty("lii_pricePrecision_ql")
    private int pricePrecision;
}
