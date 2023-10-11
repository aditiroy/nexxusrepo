package com.att.sales.nexxus.transmitdesigndata.model;

import java.util.List;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.codehaus.jackson.map.annotate.JsonSerialize;

import com.fasterxml.jackson.annotation.JsonInclude;


@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class PortDetails {
	
	@JsonProperty("accessServiceRequestItemId")
	private String asrItemId;
	
	@JsonProperty("statusCode")
	private String statusCode;
	
	@JsonProperty("statusDescription")
	private String statusDescription;
	
	@JsonProperty("estimatedInterval")
	private Integer estimatedInterval;
	
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
	
	@JsonProperty("failureInd")
	private String failureInd;
	
	@JsonProperty("result")
	private String result;
	
	@JsonProperty("message")
	private String message;
	
	@JsonProperty("validationIssues")
	@JsonInclude(JsonInclude.Include.NON_EMPTY)
	private List<ValidationIssues> validationIssues;
	
	@JsonProperty("kmzMapLink")
	private String kmzMapLink;

	public String getKmzMapLink() {
		return kmzMapLink;
	}

	public void setKmzMapLink(String kmzMapLink) {
		this.kmzMapLink = kmzMapLink;
	}

	public String getAsrItemId() {
		return asrItemId;
	}

	public void setAsrItemId(String asrItemId) {
		this.asrItemId = asrItemId;
	}

	public String getStatusCode() {
		return statusCode;
	}

	public void setStatusCode(String statusCode) {
		this.statusCode = statusCode;
	}

	public String getStatusDescription() {
		return statusDescription;
	}

	public void setStatusDescription(String statusDescription) {
		this.statusDescription = statusDescription;
	}
	
	public Integer getEstimatedInterval() {
		return estimatedInterval;
	}

	public void setEstimatedInterval(Integer estimatedInterval) {
		this.estimatedInterval = estimatedInterval;
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

	public String getFailureInd() {
		return failureInd;
	}

	public void setFailureInd(String failureInd) {
		this.failureInd = failureInd;
	}

	public String getResult() {
		return result;
	}

	public void setResult(String result) {
		this.result = result;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public List<ValidationIssues> getValidationIssues() {
		return validationIssues;
	}

	public void setValidationIssues(List<ValidationIssues> validationIssues) {
		this.validationIssues = validationIssues;
	}
	
	

}
