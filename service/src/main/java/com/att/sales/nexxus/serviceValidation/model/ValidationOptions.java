/**
 * 
 */
package com.att.sales.nexxus.serviceValidation.model;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.map.annotate.JsonSerialize;

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
public class ValidationOptions {
	
	private Boolean returnSupplementalsIndicator;
	private String glidLookup;
	private String maxAlternativeReturn;
	private String maxSupplementalReturn;
	private String maxRangeReturn;
	private Boolean lecValidationIndicator;
	private Boolean bypassFiberIntegerelIndicator;
	private Boolean bypassFTTBDataIndicator;
	private String addressValidationOnlyWithFiberIntegerel;
}
