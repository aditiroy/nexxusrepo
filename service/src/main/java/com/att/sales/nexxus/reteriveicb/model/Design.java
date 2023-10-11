package com.att.sales.nexxus.reteriveicb.model;

import java.util.List;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.map.annotate.JsonSerialize;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class Design {

	private String country;
	private String currencyCd;
	private String portProtocol;
	private Long accessSpeedUdfAttrId;
	private DesignAccessDetails accessDetails;
	private Long portId;
	@JsonProperty("interface")
	private String interfaceName;
	private Long referenceSiteId;
	private String lac;
	private String accessSpeed;
	private String accessType;
	private String accessArchitecture;
	private String portSpeed;
	private List<PriceAttributes> priceDetails;
	private String accessTailTechnology;
	private String mileage;
	private String siteType;
	private String accessTypeUdfAttrId;
	private String categoryLocalAccess;
}
