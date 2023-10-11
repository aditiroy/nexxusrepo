package com.att.sales.nexxus.serviceValidation.model;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.map.annotate.JsonSerialize;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
@Getter
@Setter
@NoArgsConstructor
public class UpdateTransSitesServiceUpdateLocations {

	private String nxSiteId;

	@JsonProperty("Name")
	private String name;

	private String documentNumber;

	@JsonProperty("AddressValidationServiceQualificationResponse")
	private UpdateTransSitesServiceUpdateAVSQResponse addressValidationServiceQualificationResponse;
	
	private String qualConversationId;
}
