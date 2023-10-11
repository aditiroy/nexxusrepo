package com.att.sales.nexxus.model;

import org.codehaus.jackson.map.annotate.JsonSerialize;

import com.att.sales.framework.model.ServiceResponse;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonSerialize(include = JsonSerialize.Inclusion.NON_EMPTY)
public class UploadMyPriceLegacyCoDataResponse extends ServiceResponse {
	private static final long serialVersionUID = 1L;
	private String fileName;
	private SyncMyPriceLegacyCoDataResponse syncMyPriceLegacyCoDataResponse;

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public SyncMyPriceLegacyCoDataResponse getSyncMyPriceLegacyCoDataResponse() {
		return syncMyPriceLegacyCoDataResponse;
	}

	public void setSyncMyPriceLegacyCoDataResponse(SyncMyPriceLegacyCoDataResponse syncMyPriceLegacyCoDataResponse) {
		this.syncMyPriceLegacyCoDataResponse = syncMyPriceLegacyCoDataResponse;
	}

}
