package com.att.sales.nexxus.model;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.map.annotate.JsonSerialize;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class SubmitFeedbackRequest {
	private String attuid;
	private String feedback;
	
	public String getAttuid() {
		return attuid;
	}
	public void setAttuid(String attuid) {
		this.attuid = attuid;
	}
	public String getFeedback() {
		return feedback;
	}
	public void setFeedback(String feedback) {
		this.feedback = feedback;
	}
	

}
