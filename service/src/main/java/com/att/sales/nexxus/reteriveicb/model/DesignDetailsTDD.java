package com.att.sales.nexxus.reteriveicb.model;

import java.util.List;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.map.annotate.JsonSerialize;

import com.att.sales.nexxus.transmitdesigndata.model.ValidationIssues;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class DesignDetailsTDD {
	private String id; 
	private String asrItemId;
	private String message;
	private String result;
	private List<ValidationIssues> validationIssues;
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getAsrItemId() {
		return asrItemId;
	}
	public void setAsrItemId(String asrItemId) {
		this.asrItemId = asrItemId;
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
	public String getResult() {
		return result;
	}
	public void setResult(String result) {
		this.result = result;
	}
	
	
}
