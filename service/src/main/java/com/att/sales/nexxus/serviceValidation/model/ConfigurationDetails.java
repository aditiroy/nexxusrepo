package com.att.sales.nexxus.serviceValidation.model;

import java.util.List;

import org.codehaus.jackson.map.annotate.JsonSerialize;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @author KumariMuktta
 *
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
@Getter
@Setter
@NoArgsConstructor
public class ConfigurationDetails {

	private String documentNumber;

	private String modelName;

	private List<DesignConfiguration> designConfiguration;
	
	private AddressValidationServiceQualificationResponse avsqResponse;
	
	private String qualConversationId;

}