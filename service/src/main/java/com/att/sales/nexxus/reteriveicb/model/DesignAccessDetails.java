package com.att.sales.nexxus.reteriveicb.model;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.map.annotate.JsonSerialize;

import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class DesignAccessDetails {

	private String supplierName;
	private String npanxx;
	private String serialNumber;
	private Long dqid;
	private String popClli;
	private String currencyCode;
	private Long speed;
	private String iglooMaxMrcDiscount;
	private Float nrcListRate;
	private String tokenId;
	private Float mrcListRate;
	private String respSpeed;
	private String respSupplierName;
	private Long portId;
	private String quoteId;
	private String respAccessInterconnect;
	private String respPopClli;
}
