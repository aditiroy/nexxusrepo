package com.att.sales.nexxus.reteriveicb.model;

import java.util.List;

import org.codehaus.jackson.map.annotate.JsonSerialize;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonSerialize(include = JsonSerialize.Inclusion.NON_EMPTY)
@Getter
@Setter
@ToString
public class SDNCharges {
	@JsonProperty("PortFees")
	private PortFees portFees;
	@JsonProperty("CosPremium")
	private List<CosPremium> cosPremium;
	@JsonProperty("AseThirdPartyDetail")
	private List<AseThirdPartyDetail> aseThirdPartyDetail;
	@JsonProperty("SDNMRC")
	private List<SDNMRC> sdnMRC;

}
