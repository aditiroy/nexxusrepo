package com.att.sales.nexxus.chatbot.model;

import java.io.Serializable;
import java.util.List;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.map.annotate.JsonSerialize;

import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class ElizaSyncResponse  implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	@JsonProperty("text")
	private String text; 
	
	@JsonProperty("entities")
	private String[] entities; 
	
	@JsonProperty("intent_ranking")
	private List<ElizaSyncIntent> intent_ranking;

	@JsonProperty("intent")
	private ElizaSyncIntent intent;

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public String[] getEntities() {
		return entities;
	}

	public void setEntities(String[] entities) {
		this.entities = entities;
	}

	public List<ElizaSyncIntent> getIntent_ranking() {
		return intent_ranking;
	}

	public void setIntent_ranking(List<ElizaSyncIntent> intent_ranking) {
		this.intent_ranking = intent_ranking;
	}

	public ElizaSyncIntent getIntent() {
		return intent;
	}

	public void setIntent(ElizaSyncIntent intent) {
		this.intent = intent;
	}

}