package com.att.sales.nexxus.myprice.transaction.model;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.map.annotate.JsonSerialize;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

/**
 * @author IndraSingh
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
@JsonInclude(Include.NON_NULL)
@Getter
@Setter
public class Document {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@JsonProperty("_customer_t_first_name")
	private String customerTFirstName;

	@JsonProperty("_customer_t_last_name")
	private String customerTLastName;

	@JsonProperty("_customer_t_company_name")
	private String customerTCompanyName;

	@JsonProperty("_customer_t_address")
	private String customerTAddress;

	@JsonProperty("_customer_t_address_2")
	private String customerTAddress2;

	@JsonProperty("_customer_t_city")
	private String customerTCity;

	@JsonProperty("_customer_t_state")
	private String customerTState;

	@JsonProperty("_customer_t_zip")
	private String customerTZip;

	@JsonProperty("_customer_t_country")
	private String customerTCountry;

	@JsonProperty("_customer_t_fax")
	private String customerTFax;

	@JsonProperty("_customer_t_email")
	private String customerTEmail;

	@JsonProperty("opportunityID_t")
	private String opportunityIDT;

	@JsonProperty("opportunityName_t")
	private String opportunityNameT;

	@JsonProperty("sAARTAccountNumber")
	private String saartAccountNumber;

	@JsonProperty("rd_description_q")
	private String rdDescriptionQ;

	@JsonProperty("rOMEMarketSegment_q")
	private String romeMarketSegmentQ;

	@JsonProperty("sourceFromNexxus_q")
	private String sourceFromNexxusQ;

	@JsonProperty("wi_layer_q")
	private String wiLayerQ;

	@JsonProperty("wi_wireline470_q")
	private String wiWireline470Q;

	@JsonProperty("wi_opportunityType_q")
	private String wiOpportunityTypeQ;

	@JsonProperty("wi_budgetaryFirm_q")
	private String wiBudgetaryFirmQ;
	
	@JsonProperty("wi_contractNumber_q")
	private String wiContractNumber;

	@JsonProperty("integrationSiteDict1")
	private String siteAddress;

	@JsonProperty("rd_attuid_q")
	private String rdAttuidQ;

	@JsonProperty("rd_name_q")
	private String rdNameQ;

	@JsonProperty("rd_title_q")
	private String rdTitleQ;

	@JsonProperty("rd_office_q")
	private String rdOfficeQ;

	@JsonProperty("rd_mobile_q")
	private String rdMobileQ;

	@JsonProperty("rd_email_q")
	private String rdEmailQ;

	@JsonProperty("rd_opportunitySalesTeam_q")
	private String rdOpportunitySalesTeamQ;

	@JsonProperty("wi_contractTerm_q")
	private Long contractTerm;
	
	@JsonProperty("wi_subLayer_q")
	private String wiSubLayerQ;
	
	@JsonProperty("rd_segment_q")
	private String rdSegmentQ;
	
	@JsonProperty("rd_salesChannels_q")
	private String rdSalesChannelsQ;
	
	@JsonProperty("rd_bU_q")
	private String rdBUQ;
	
	@JsonProperty("rd_aVP_q")
	private String rdAVPQ;
	
	@JsonProperty("dealType_t")
	private String dealType;
	
	@JsonProperty("ri_competitors_q")
	private String riCompetitorsQ;
	
	@JsonProperty("priceScenarioId_q")
	private Long priceScenarioId;

	@JsonProperty("siteAddress")
	private String siteAddressJson;
	
	@JsonProperty("wi_updateOverride_q")
	private boolean wiUpdateOverrideQ;
	
	@JsonProperty("wi_customPriceList_q")
	private String wiCustomPriceListQ;
	
	@JsonProperty("wi_isErate_q")
	private Boolean wiIsErateQ;
	
	@JsonProperty("contractPricingScope_q")
	private String contractPricingScopeQ;
	
	@JsonProperty("ppcosUser_q")
	private String ppcosUserQ;

	@JsonProperty("externalSolutionId")
	private long externalSolutionId;
	
	@JsonProperty("rd_opportunityTeam_q")
	private String rdOpportunityTeamQ;
	
	@JsonProperty("wl_dAIndicator_q")
	private String wlDAIndicatorQ;
	
	@JsonProperty("Wi_solutionVersion_q")
	private long wiSolutionVersionQ;
}
