package com.att.sales.nexxus.myprice.transaction.model;

import java.io.Serializable;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.map.annotate.JsonSerialize;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * 
 * @author Laxman Honawad
 *
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
@Data
@NoArgsConstructor
@ToString
public class UpdateTxnSiteUploadLocations implements Serializable {
	/**
	* 
	*/
	private static final long serialVersionUID = 1L;

	private Long id;
	
	private Long nxSiteId;
	
	private String siteInfoSource;
	
	@JsonProperty("Name")
	private String name;
	
	private String validationStatus;
	
	@JsonProperty("AddressValidationServiceQualificationResponse")
	private UpdateTxnSiteUploadAddrsValidationSerQualResp addressValidationSerQualResponse;
	
}
