package com.att.sales.nexxus.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CircuitSiteDetails {
	
	private Long id;
	private String clli;
	private String endType;
	private String siteNpanxx; // for handling nxT1
	private String custSrvgWireCtrCLLICd; // for handling nxT1
	private String nxKeyId; // for handling nxT1
	private int nxT1Qty; // for handling nxT1

}
