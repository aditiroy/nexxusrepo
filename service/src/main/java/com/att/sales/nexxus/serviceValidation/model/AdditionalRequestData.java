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
public class AdditionalRequestData {
	
	// ASE additional requested data
	private Boolean aicDetailsRequiredIndicator;
	private String productType;
	private Boolean fiberCheckIndicator;
	private Boolean aseLSCheckIndicator;
	private Boolean aseCopperCheckIndicator;
	private Boolean fiberLitCheckIndicator;
	
	private Boolean enniCheckIndicator;
	private Integer inServiceCopperPairs;
	private Integer inServiceCopperRepeaterHousings;
	private Map<String, Object> requestedBandwidth;
	private Integer floorNumber;
	private String interOfficeFacilitySWCCLLI;
	
	// BVOIP & ATTCollaborate additional requested data
	private Boolean e911IntradoIndicator;
	private String npanxx;
	private TelephoneNumber telephoneNumber;
	private Boolean dslRemoteIndicator;
}
