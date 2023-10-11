package com.att.sales.nexxus.transmitdesigndata.model;

import java.util.List;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.codehaus.jackson.map.annotate.JsonSerialize;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CircuitDetails {
	
	@JsonProperty("accessServiceRequestItemId")
	private String asrItemId;
	
	@JsonProperty("estimatedInterval")
	private Integer estimatedInterval;
	
	@JsonProperty("confirmedInterval")
	private Integer confirmedInterval;
	
	@JsonProperty("circuitCancellationReason")
	private String circuitCancellationReason;
	
	@JsonProperty("preliminaryServingPlanURL")
	private String preliminaryServingPlanURL;
	
	@JsonProperty("notes")
	private String notes;
	
	@JsonProperty("nssManagerFirstName")
	private String nssManagerFirstName;
	
	@JsonProperty("nssManagerLastName")
	private String nssManagerLastName;
	
	@JsonProperty("nssManagerATTUID")
	private String nssManagerATTUID;
	
	@JsonProperty("endpointDetails")
	@JsonInclude(JsonInclude.Include.NON_EMPTY)
	private List<EndpointDetails> endpointDetails;
	
	@JsonProperty("kmzMapLink")
	private String kmzMapLink;
	
	
	public String getAsrItemId() {
		return asrItemId;
	}
	public void setAsrItemId(String asrItemId) {
		this.asrItemId = asrItemId;
	}
	public Integer getEstimatedInterval() {
		return estimatedInterval;
	}
	public void setEstimatedInterval(Integer estimatedInterval) {
		this.estimatedInterval = estimatedInterval;
	}
	public Integer getConfirmedInterval() {
		return confirmedInterval;
	}
	public void setConfirmedInterval(Integer confirmedInterval) {
		this.confirmedInterval = confirmedInterval;
	}
	public String getCircuitCancellationReason() {
		return circuitCancellationReason;
	}
	public void setCircuitCancellationReason(String circuitCancellationReason) {
		this.circuitCancellationReason = circuitCancellationReason;
	}
	public String getPreliminaryServingPlanURL() {
		return preliminaryServingPlanURL;
	}
	public void setPreliminaryServingPlanURL(String preliminaryServingPlanURL) {
		this.preliminaryServingPlanURL = preliminaryServingPlanURL;
	}
	public String getNotes() {
		return notes;
	}
	public void setNotes(String notes) {
		this.notes = notes;
	}
	public String getNssManagerFirstName() {
		return nssManagerFirstName;
	}
	public void setNssManagerFirstName(String nssManagerFirstName) {
		this.nssManagerFirstName = nssManagerFirstName;
	}
	public String getNssManagerLastName() {
		return nssManagerLastName;
	}
	public void setNssManagerLastName(String nssManagerLastName) {
		this.nssManagerLastName = nssManagerLastName;
	}
	public String getNssManagerATTUID() {
		return nssManagerATTUID;
	}
	public void setNssManagerATTUID(String nssManagerATTUID) {
		this.nssManagerATTUID = nssManagerATTUID;
	}
	public List<EndpointDetails> getEndpointDetails() {
		return endpointDetails;
	}
	public void setEndpointDetails(List<EndpointDetails> endpointDetails) {
		this.endpointDetails = endpointDetails;
	}
	public String getKmzMapLink() {
		return kmzMapLink;
	}
	public void setKmzMapLink(String kmzMapLink) {
		this.kmzMapLink = kmzMapLink;
	}
	
	
	

}
