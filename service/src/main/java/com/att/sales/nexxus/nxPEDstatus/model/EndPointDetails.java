package com.att.sales.nexxus.nxPEDstatus.model;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.map.annotate.JsonSerialize;

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
public class EndPointDetails {

	@JsonProperty("endpointType")
	private String endpointType;

	@JsonProperty("edgelessDesignIndicator")
	private String edgelessDesignIndicator;

	@JsonProperty("locationclli")
	private String locationclli;

	@JsonProperty("alternateSWCCLLI")
	private String alternateSWCCLLI;

	@JsonProperty("commonLanguageFacilityId")
	private String commonLanguageFacilityId;

}
