package com.att.sales.nexxus.chatbot.model;

import java.io.Serializable;
import java.util.List;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.map.annotate.JsonSerialize;

import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class ElizaIntents  implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	@JsonProperty("intent")
	private String intent; 
	
	@JsonProperty("displayquestion")
	private String displayquestion; 
	
	@JsonProperty("questions")
	private List<ElizaQuestions> questions;

	public String getIntent() {
		return intent;
	}

	public void setIntent(String intent) {
		this.intent = intent;
	}

	public String getDisplayquestion() {
		return displayquestion;
	}

	public void setDisplayquestion(String displayquestion) {
		this.displayquestion = displayquestion;
	}

	public List<ElizaQuestions> getQuestions() {
		return questions;
	}

	public void setQuestions(List<ElizaQuestions> questions) {
		this.questions = questions;
	}

}