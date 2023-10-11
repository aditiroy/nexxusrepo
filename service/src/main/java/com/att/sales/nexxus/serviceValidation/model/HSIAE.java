/**
 * 
 */
package com.att.sales.nexxus.serviceValidation.model;

import java.util.Map;

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
public class HSIAE {
	
	private Boolean hsiaEIndicator;
	private SpeedOptions speedOptions;
	private String telephoneNumber;
	private String bypass3PAHSIAEIndicator;
}
