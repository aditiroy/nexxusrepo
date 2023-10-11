package com.att.sales.nexxus.model;

import java.util.List;
import java.util.Map;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.map.annotate.JsonSerialize;

import lombok.Getter;
import lombok.Setter;
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
@Getter
@Setter
public class SiteAddress {
	
	List<Map<String,Object>> siteAddress;

	public List<Map<String, Object>> getSiteAddress() {
		return siteAddress;
	}

	public void setSiteAddress(List<Map<String, Object>> siteAddress) {
		this.siteAddress = siteAddress;
	}

	
	

}
