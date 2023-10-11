package com.att.sales.nexxus.reteriveicb.model;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.map.annotate.JsonSerialize;


/**
 * The Class MacdActivityType.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class MacdActivityType {

/** The activity type. */
private String activityType;
	
	/** The component id. */
	private Long componentId;
	
	/** The component code id. */
	private Long componentCodeId;
	
	/** The component code type. */
	private String componentCodeType;
	
	/**
	 * Gets the activity type.
	 *
	 * @return the activity type
	 */
	public String getActivityType() {
		return activityType;
	}

	/**
	 * Sets the activity type.
	 *
	 * @param activityType the new activity type
	 */
	public void setActivityType(String activityType) {
		this.activityType = activityType;
	}

	/**
	 * Gets the component id.
	 *
	 * @return the component id
	 */
	public Long getComponentId() {
		return componentId;
	}

	/**
	 * Sets the component id.
	 *
	 * @param componentId the new component id
	 */
	public void setComponentId(Long componentId) {
		this.componentId = componentId;
	}

	/**
	 * Gets the component code id.
	 *
	 * @return the component code id
	 */
	public Long getComponentCodeId() {
		return componentCodeId;
	}

	/**
	 * Sets the component code id.
	 *
	 * @param componentCodeId the new component code id
	 */
	public void setComponentCodeId(Long componentCodeId) {
		this.componentCodeId = componentCodeId;
	}

	/**
	 * Gets the component code type.
	 *
	 * @return the component code type
	 */
	public String getComponentCodeType() {
		return componentCodeType;
	}

	/**
	 * Sets the component code type.
	 *
	 * @param componentCodeType the new component code type
	 */
	public void setComponentCodeType(String componentCodeType) {
		this.componentCodeType = componentCodeType;
	}
	
	/**
	 * Instantiates a new macd activity type.
	 *
	 * @param activityType the activity type
	 * @param componentId the component id
	 * @param componentCodeId the component code id
	 */
	public MacdActivityType(String activityType, Long componentId, Long componentCodeId) {
		super();
		this.activityType = activityType;
		this.componentId = componentId;
		this.componentCodeId = componentCodeId;
	}
	
	/**
	 * Instantiates a new macd activity type.
	 */
	public MacdActivityType(){
		
	}
}
