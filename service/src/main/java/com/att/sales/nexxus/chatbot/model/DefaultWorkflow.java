package com.att.sales.nexxus.chatbot.model;

import java.io.Serializable;
import java.util.List;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.map.annotate.JsonSerialize;

import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class DefaultWorkflow  implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	@JsonProperty("option")
	private String option; 
	
	@JsonProperty("eventType")
	private String eventType; 
	
	@JsonProperty("workflow")
	private List<Workflow> workflow;

	public String getOption() {
		return option;
	}

	public void setOption(String option) {
		this.option = option;
	}

	public String getEventType() {
		return eventType;
	}

	public void setEventType(String eventType) {
		this.eventType = eventType;
	}

	public List<Workflow> getWorkflow() {
		return workflow;
	}

	public void setWorkflow(List<Workflow> workflow) {
		this.workflow = workflow;
	}

}