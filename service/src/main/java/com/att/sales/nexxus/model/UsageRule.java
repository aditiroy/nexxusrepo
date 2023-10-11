package com.att.sales.nexxus.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class UsageRule {
	
	@JsonProperty("ruleName")
	private String ruleName;
	
	@JsonProperty("jsonValidationPath")
	private String jsonValidationPath;

	@JsonProperty("jsonValidationKey")
	private String jsonValidationKey;

	@JsonProperty("jsonDataPath")
	private String jsonDataPath;
	
	@JsonProperty("jsonarray")
	private String jsonarray;
	
	@JsonProperty("jsonParentDataPath")
	private String jsonParentDataPath;
	
	@JsonProperty("multiConfigCountPath")
	private String multiConfigCountPath;
	
	@JsonProperty("multiConfigDataPath")
	private String multiConfigDataPath;
	
	
	public String getRuleName() {
		return ruleName;
	}

	public void setRuleName(String ruleName) {
		this.ruleName = ruleName;
	}

	public String getJsonValidationPath() {
		return jsonValidationPath;
	}

	public void setJsonValidationPath(String jsonValidationPath) {
		this.jsonValidationPath = jsonValidationPath;
	}

	public String getJsonValidationKey() {
		return jsonValidationKey;
	}

	public void setJsonValidationKey(String jsonValidationKey) {
		this.jsonValidationKey = jsonValidationKey;
	}

	public String getJsonDataPath() {
		return jsonDataPath;
	}

	public void setJsonDataPath(String jsonDataPath) {
		this.jsonDataPath = jsonDataPath;
	}

	public String getJsonarray() {
		return jsonarray;
	}

	public void setJsonarray(String jsonarray) {
		this.jsonarray = jsonarray;
	}
	
	/**
	 * @return the jsonParentDataPath
	 */
	public String getJsonParentDataPath() {
		return jsonParentDataPath;
	}

	/**
	 * @param jsonParentDataPath the jsonParentDataPath to set
	 */
	public void setJsonParentDataPath(String jsonParentDataPath) {
		this.jsonParentDataPath = jsonParentDataPath;
	}

	/**
	 * @return the multiConfigCountPath
	 */
	public String getMultiConfigCountPath() {
		return multiConfigCountPath;
	}

	/**
	 * @param multiConfigCountPath the multiConfigCountPath to set
	 */
	public void setMultiConfigCountPath(String multiConfigCountPath) {
		this.multiConfigCountPath = multiConfigCountPath;
	}

	/**
	 * @return the multiConfigDataPath
	 */
	public String getMultiConfigDataPath() {
		return multiConfigDataPath;
	}

	/**
	 * @param multiConfigDataPath the multiConfigDataPath to set
	 */
	public void setMultiConfigDataPath(String multiConfigDataPath) {
		this.multiConfigDataPath = multiConfigDataPath;
	}
	
	
}
