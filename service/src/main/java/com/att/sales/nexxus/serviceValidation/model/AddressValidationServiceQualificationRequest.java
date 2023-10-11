/**
 * 
 */
package com.att.sales.nexxus.serviceValidation.model;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.map.annotate.JsonSerialize;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.As;

import lombok.Getter;
import lombok.Setter;

/**
 * @author ShruthiCJ
 *
 */

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
@Getter
@Setter
@JsonTypeInfo(include=As.WRAPPER_OBJECT, use=JsonTypeInfo.Id.NAME)
public class AddressValidationServiceQualificationRequest {
	
	private String fiberFastQualFlag;
	
	private String buildingQualIndicator;
	private String allProductsIndicator;
	private String processIndicator;
	private String globalLocationId;
	private String buildingBashKey;
	private String building;
	private String floor;
	private String unit;
	private String source;
	@JsonProperty("Location")
	private Location location;
	@JsonProperty("RequestedProducts")
	private RequestedProducts requestedProducts;
	private String bypass3PAHSIAEIndicator;
	private String addressValidationOnlyWithFiberIntel;
	private String abfLGQualificationIndicator;
	
	@JsonIgnore
	private String qualConversationId;
	@JsonIgnore
	private String modelName;
}
