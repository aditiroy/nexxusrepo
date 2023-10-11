package com.att.sales.nexxus.myprice.transaction.model;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.map.annotate.JsonSerialize;

import com.att.sales.framework.model.ServiceResponse;
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
public class GetTransactionResponse extends ServiceResponse {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@JsonProperty("rl_wirelineRateLetterExpirationDate_q")
	private String rateLetterExpiresOn;
	
	@JsonProperty("rl_approvedWirelineRLURL_q")
	private String quoteUrl;
	
	@JsonProperty("tsc_wirelineAutoApproval_q")
	private boolean autoApproval;
	
	@JsonProperty("rl_serviceQualification_q")
	private Values rlType;
	
	@JsonProperty("_customer_t_first_name")
	private String customerFirstName;
	
	@JsonProperty("_customer_t_last_name")
	private String customerLastName;
	
	@JsonProperty("_customer_t_company_name")
	private String customerCompanyName;
	
	@JsonProperty("_customer_t_company_name_2")
	private String customerCompanyName2;
	
	@JsonProperty("_customer_t_address")
	private String customerAddres;
	
	@JsonProperty("_customer_t_address_2")
	private String customerAddres2;
	
	@JsonProperty("_customer_t_city")
	private String customerCity;
	
	@JsonProperty("_customer_t_state")
	private Values customerState;
	
	@JsonProperty("_customer_t_zip")
	private String customerPostalCode;
	
	@JsonProperty("_customer_t_country")
	private Values customerCountry;
	
	@JsonProperty("_customer_t_phone")
	private String customerPhone;
	
	@JsonProperty("_customer_t_email")
	private String customerEmail;
	
	@JsonProperty("_customer_t_fax")
	private String customerFax;
	
	//@JsonProperty("REVISION")
	private String revision;

	//@JsonProperty("VERSION")
	private String version;
	
	@JsonProperty("wi_OriginalClonedTxId")
	private String originalClonedTxId;
	
	@JsonProperty("contractPricingScope_q")
	private Values contractPricingScope;
	
	@JsonProperty("sAARTAccountNumber")
	private String saartAccountNumber;
	
	@JsonProperty("wi_isErate_q")
	private boolean wiIsErate; 

}
