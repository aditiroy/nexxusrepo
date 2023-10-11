/**
 * 
 */
package com.att.sales.nexxus.serviceValidation.model;

import java.util.List;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.map.annotate.JsonSerialize;

import com.fasterxml.jackson.annotation.JsonProperty;

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
public class FiberAvailabilityResults {
	private Boolean fiberAvailabilityFlag;
	@JsonProperty("ProductFTTB")
	private ProductFTTB productFTTB;
	@JsonProperty("ProductASE")
	private List<ProductASE> productASE;
	@JsonProperty("FiberDataSummary")
	private FiberDataSummary fiberDataSummary;
}