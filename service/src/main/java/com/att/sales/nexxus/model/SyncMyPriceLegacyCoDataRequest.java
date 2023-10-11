package com.att.sales.nexxus.model;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.map.annotate.JsonSerialize;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class SyncMyPriceLegacyCoDataRequest {
	private String updatingSource;

	public String getUpdatingSource() {
		return updatingSource;
	}

	public void setUpdatingSource(String updatingSource) {
		this.updatingSource = updatingSource;
	}
}
