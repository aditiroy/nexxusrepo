package com.att.sales.nexxus.admin.model;

import java.io.InputStream;

import org.codehaus.jackson.map.annotate.JsonSerialize;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * The Class DataUploadRequest.
 *
 * @author vt393d
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class DataUploadRequest{

	
	/** The id. */
	private String  id;
	
	/** The action. */
	private String action;
	
	/** The activity. */
	private String activity;
	
	/** The input stream. */
	private InputStream inputStream;
	
	/** The user id. */
	private String userId;
	
	
	/**
	 * Gets the id.
	 *
	 * @return the id
	 */
	public String getId() {
		return id;
	}
	
	/**
	 * Sets the id.
	 *
	 * @param id the new id
	 */
	public void setId(String id) {
		this.id = id;
	}
	
	/**
	 * Gets the action.
	 *
	 * @return the action
	 */
	public String getAction() {
		return action;
	}
	
	/**
	 * Sets the action.
	 *
	 * @param action the new action
	 */
	public void setAction(String action) {
		this.action = action;
	}
	
	/**
	 * Gets the activity.
	 *
	 * @return the activity
	 */
	public String getActivity() {
		return activity;
	}
	
	/**
	 * Sets the activity.
	 *
	 * @param activity the new activity
	 */
	public void setActivity(String activity) {
		this.activity = activity;
	}
	
	/**
	 * Gets the input stream.
	 *
	 * @return the input stream
	 */
	public InputStream getInputStream() {
		return inputStream;
	}
	
	/**
	 * Sets the input stream.
	 *
	 * @param inputStream the new input stream
	 */
	public void setInputStream(InputStream inputStream) {
		this.inputStream = inputStream;
	}
	
	/**
	 * Gets the user id.
	 *
	 * @return the user id
	 */
	public String getUserId() {
		return userId;
	}
	
	/**
	 * Sets the user id.
	 *
	 * @param userId the new user id
	 */
	public void setUserId(String userId) {
		this.userId = userId;
	}
	
	
	
	
	
}
