package com.att.sales.nexxus.ped.dmaap.model;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.map.annotate.JsonSerialize;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * The Class EndPointDetails.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
@Getter
@Setter
@NoArgsConstructor
public class EndPointDetails {
	
	private Long siteId;
	private String endpointType;
	private String edgelessDesignIndicator;
	private String locationclli;
	private String alternateSWCCLLI;
	private String commonLanguageFacilityId;
}
