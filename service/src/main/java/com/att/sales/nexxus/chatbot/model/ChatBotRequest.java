/**
 * 
 */
package com.att.sales.nexxus.chatbot.model;

import java.io.Serializable;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author Akash
 *
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class ChatBotRequest implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	/**
	 * Default Constructor
	 */
	public ChatBotRequest() {
		// -- Default Constructor
	}
	
	@JsonProperty("userId")
	private String userId;
	
	@JsonProperty("searchString")
	private String searchString;
	
	
	@JsonProperty("actionType")
	private String actionType;

	public String getUserId() {
		return userId;
	}


	public void setUserId(String userId) {
		this.userId = userId;
	}


	public String getSearchString() {
		return searchString;
	}


	public void setSearchString(String searchString) {
		this.searchString = searchString;
	}


	public String getActionType() {
		return actionType;
	}


	public void setActionType(String actionType) {
		this.actionType = actionType;
	}
	
	
	
	
	}


