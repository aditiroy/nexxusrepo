package com.att.sales.nexxus.pricing.model;

import org.codehaus.jackson.map.annotate.JsonSerialize;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class ValidationIssues {
	private String issueDescription;
	private String pedFieldNam;

	public String getIssueDescription() {
		return issueDescription;
	}

	public void setIssueDescription(String issueDescription) {
		this.issueDescription = issueDescription;
	}

	public String getPedFieldNam() {
		return pedFieldNam;
	}

	public void setPedFieldNam(String pedFieldNam) {
		this.pedFieldNam = pedFieldNam;
	}

}
