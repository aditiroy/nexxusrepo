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
public class RequestedProducts {	
	private Boolean aniraIndicator;
	private Boolean avtsIndicator;
	private Boolean avpnIndicator;
	private Boolean eplsWANIndicator;
	private Boolean misIndicator;
	private Boolean opticalEthernetWANIndicator;
	private Boolean avpnExpressIndicator;
	private Boolean vvbExpressIndicator;
	private Boolean misExpressIndicator;
	private Boolean networkBasedFirewallIndicator;
	private Boolean officeAtHandIndicator;
	private Boolean cloudWebSecurityServicesIndicator;
	private Boolean ipbbIndicator;
	private Boolean attDSLIndicator;
	private Boolean uverseVoiceIndicator;
	private Boolean ddosDefenseIndicator;
	private Boolean attProxyServicesIndicator;
	private Boolean attIDPHostBasedIndicator;
	private Boolean attIDPServicesIndicator;
	private Boolean attPremisesBasedFirewallIndicator;
	private Boolean attWIFIIndicator;
	private Boolean adiIndicator;
	private Boolean bvoipIndicator;
	private Boolean pFlexLocalIndicator;	
	private Boolean ipFlexLongDistanceIndicator;
	private Boolean ipFlexTollFreeIndicator;
	private Boolean vdnaIndicator;
	@JsonProperty("VoiceOverIPServiceAvailability")
	private VoiceOverIPServiceAvailability voiceOverIPServiceAvailability;
	@JsonProperty("ASE")
	private ASE ase;
	private BVOIP bviop;
	@JsonProperty("ATTCollaborate")
	private ATTCollaborate attCollaborate;
	private HSIAE hsiae;
}
