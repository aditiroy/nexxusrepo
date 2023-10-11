package com.att.sales.nexxus.reteriveicb.model;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.map.annotate.JsonSerialize;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class CustomAccessPriceList {
	
	private Long term;
	private Double mrc;
	private Double nrc;
	private String accessSupplier;
	private String token;
	private String endpointIndicator;
	
	public Long getTerm() {
		return term;
	}
	public void setTerm(Long term) {
		this.term = term;
	}

	public Double getMrc() {
		return mrc;
	}
	public void setMrc(Double mrc) {
		this.mrc = mrc;
	}
	public Double getNrc() {
		return nrc;
	}
	public void setNrc(Double nrc) {
		this.nrc = nrc;
	}
	public String getAccessSupplier() {
		return accessSupplier;
	}
	public void setAccessSupplier(String accessSupplier) {
		this.accessSupplier = accessSupplier;
	}
	public String getToken() {
		return token;
	}
	public void setToken(String token) {
		this.token = token;
	}
	public String getEndpointIndicator() {
		return endpointIndicator;
	}
	public void setEndpointIndicator(String endpointIndicator) {
		this.endpointIndicator = endpointIndicator;
	}
  
	

}
