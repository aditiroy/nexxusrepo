package com.att.sales.nexxus.chatbot.model;

import java.io.Serializable;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.map.annotate.JsonSerialize;

import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class Workflow  implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	@JsonProperty("option")
	private String option; 
	
	@JsonProperty("eventType")
	private String eventType; 
	
	@JsonProperty("text")
	private String text; 
	
	@JsonProperty("actionType")
	private String actionType;

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

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public String getActionType() {
		return actionType;
	}

	public void setActionType(String actionType) {
		this.actionType = actionType;
	} 
	
}