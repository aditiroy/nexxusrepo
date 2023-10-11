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
public class FiberDataSummary {
	private Boolean fiberLitIndicator;
	@JsonProperty("FiberAerialLoopDistance")
	private FiberAerialLoopDistance fiberAerialLoopDistance;
	@JsonProperty("FiberAerialIOFDistance")
	private FiberAerialIOFDistance fiberAerialIOFDistance;
	@JsonProperty("ProductIntervalDetails")
	private List<ProductIntervalDetails> productIntervalDetails;
	private String pricingTier;
}
