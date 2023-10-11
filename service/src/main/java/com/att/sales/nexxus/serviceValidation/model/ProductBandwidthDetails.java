
package com.att.sales.nexxus.serviceValidation.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;
@Getter
@Setter
public class ProductBandwidthDetails {
	private String  productName;
	@JsonProperty("BandwidthData")
	private List<BandwidthData> bandwidthData;
	
	@Override
	public String toString() {
		return "ProductBandwidthDetails [productName=" + productName + "]" +"[bandwidthData=" +bandwidthData+"]";
	}
	

}
