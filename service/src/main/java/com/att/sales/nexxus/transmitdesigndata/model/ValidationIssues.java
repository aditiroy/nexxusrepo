package com.att.sales.nexxus.transmitdesigndata.model;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.codehaus.jackson.map.annotate.JsonSerialize;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class ValidationIssues {

	@JsonProperty("issueDescription")
	private String issueDescription;
	
	@JsonProperty("pedFieldName")
	private String pedFieldName;

	public String getIssueDescription() {
		return issueDescription;
	}

	public void setIssueDescription(String issueDescription) {
		this.issueDescription = issueDescription;
	}

	public String getPedFieldName() {
		return pedFieldName;
	}

	public void setPedFieldName(String pedFieldName) {
		this.pedFieldName = pedFieldName;
	}
	
	
}
