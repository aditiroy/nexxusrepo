/**
 * 
 */
package com.att.sales.nexxus.chatbot.model;

import java.io.Serializable;
import java.util.List;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.map.annotate.JsonSerialize;

import com.att.sales.framework.model.ServiceResponse;
import com.fasterxml.jackson.annotation.JsonProperty;


@JsonIgnoreProperties(ignoreUnknown = true)
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class ElizaResponse extends ServiceResponse implements Serializable {

	private static final long serialVersionUID = 1L;

	@JsonProperty("userId")
	private String userId;

	@JsonProperty("message")
	private String message;
	
	
	@JsonProperty("chatBot")
	private String chatBot;

	@JsonProperty("intents")
	private List<ElizaIntents> intents;

	@JsonProperty("defaultWorkflow")
	private List<DefaultWorkflow> defaultWorkflow;
	
	public List<DefaultWorkflow> getDefaultWorkflow() {
		return defaultWorkflow;
	}

	public void setDefaultWorkflow(List<DefaultWorkflow> defaultWorkflow) {
		this.defaultWorkflow = defaultWorkflow;
	}

	public List<ElizaIntents> getIntents() {
		return intents;
	}

	public void setIntents(List<ElizaIntents> intents) {
		this.intents = intents;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getChatBot() {
		return chatBot;
	}

	public void setChatBot(String chatBot) {
		this.chatBot = chatBot;
	}
	
	

}
