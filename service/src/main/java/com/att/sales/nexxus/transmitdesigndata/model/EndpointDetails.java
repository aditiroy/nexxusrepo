package com.att.sales.nexxus.transmitdesigndata.model;

import org.codehaus.jackson.map.annotate.JsonSerialize;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class EndpointDetails {
	
	@JsonProperty("endpointType")
	private String endpointType;
	
	@JsonProperty("edgelessDesignIndicator")
	private String edgelessDesignIndicator;
	
	@JsonProperty("locationCLLI")
	private String locationCLLI;
	
	@JsonProperty("alternateSWCCLLI")
	private String alternateSWCCLLI;
	
	@JsonProperty("commonLanguageFacilityId")
	private String commonLanguageFacilityId;
	
	
	public String getEndpointType() {
		return endpointType;
	}
	public void setEndpointType(String endpointType) {
		this.endpointType = endpointType;
	}
	public String getEdgelessDesignIndicator() {
		return edgelessDesignIndicator;
	}
	public void setEdgelessDesignIndicator(String edgelessDesignIndicator) {
		this.edgelessDesignIndicator = edgelessDesignIndicator;
	}
	public String getLocationCLLI() {
		return locationCLLI;
	}
	public void setLocationCLLI(String locationCLLI) {
		this.locationCLLI = locationCLLI;
	}
	public String getAlternateSWCCLLI() {
		return alternateSWCCLLI;
	}
	public void setAlternateSWCCLLI(String alternateSWCCLLI) {
		this.alternateSWCCLLI = alternateSWCCLLI;
	}
	public String getCommonLanguageFacilityId() {
		return commonLanguageFacilityId;
	}
	public void setCommonLanguageFacilityId(String commonLanguageFacilityId) {
		this.commonLanguageFacilityId = commonLanguageFacilityId;
	}
	
	

}
