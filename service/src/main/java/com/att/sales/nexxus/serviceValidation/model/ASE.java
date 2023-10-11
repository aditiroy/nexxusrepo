/**
 * 
 */
package com.att.sales.nexxus.serviceValidation.model;

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
public class ASE {
	private Boolean aseIndicator;
	private Boolean aseAvailabilityFlag;
	@JsonProperty("AdditionalRequestData")
	private AdditionalRequestData additionalRequestData;
	@JsonProperty("FiberAvailabilityResults")
	private FiberAvailabilityResults fiberAvailabilityResults;

	@Override
	public String toString() {
		return "ASE [aseIndicator=" + aseIndicator + ", additionalRequestData=" + additionalRequestData
				+ ", fiberAvailabilityResults=" + fiberAvailabilityResults + "]";
	}
}
