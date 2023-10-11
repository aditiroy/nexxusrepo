package com.att.sales.nexxus.model;

import org.codehaus.jackson.map.annotate.JsonSerialize;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

/**
 * The Class UdfAttributes.
 *
 * @author km017g
 */
@JsonInclude(Include.NON_EMPTY)
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonSerialize(include=JsonSerialize.Inclusion.NON_NULL)
public class UdfAttributes {
	
	/** The udf attribute id. */
	private String udfAttributeId;
	
	/** The udf attribute value. */
	private String udfAttributeValue;
	
	/** The map id. */
	private String mapId;
	
	/** The map value. */
	private String mapValue;
	
	/** The udf attribute text. */
	private String udfAttributeText;
	
	/** The udf attribute number. */
	private String udfAttributeNumber;
	
	/**
	 * Gets the udf attribute id.
	 *
	 * @return the udf attribute id
	 */
	public String getUdfAttributeId() {
		return udfAttributeId;
	}
	
	/**
	 * Sets the udf attribute id.
	 *
	 * @param udfAttributeId the new udf attribute id
	 */
	public void setUdfAttributeId(String udfAttributeId) {
		this.udfAttributeId = udfAttributeId;
	}
	
	/**
	 * Gets the udf attribute value.
	 *
	 * @return the udf attribute value
	 */
	public String getUdfAttributeValue() {
		return udfAttributeValue;
	}
	
	/**
	 * Sets the udf attribute value.
	 *
	 * @param udfAttributeValue the new udf attribute value
	 */
	public void setUdfAttributeValue(String udfAttributeValue) {
		this.udfAttributeValue = udfAttributeValue;
	}
	
	/**
	 * Gets the map id.
	 *
	 * @return the map id
	 */
	public String getMapId() {
		return mapId;
	}
	
	/**
	 * Sets the map id.
	 *
	 * @param mapId the new map id
	 */
	public void setMapId(String mapId) {
		this.mapId = mapId;
	}
	
	/**
	 * Gets the map value.
	 *
	 * @return the map value
	 */
	public String getMapValue() {
		return mapValue;
	}
	
	/**
	 * Sets the map value.
	 *
	 * @param mapValue the new map value
	 */
	public void setMapValue(String mapValue) {
		this.mapValue = mapValue;
	}
	
	/**
	 * Gets the udf attribute text.
	 *
	 * @return the udf attribute text
	 */
	public String getUdfAttributeText() {
		return udfAttributeText;
	}
	
	/**
	 * Sets the udf attribute text.
	 *
	 * @param udfAttributeText the new udf attribute text
	 */
	public void setUdfAttributeText(String udfAttributeText) {
		this.udfAttributeText = udfAttributeText;
	}
	
	/**
	 * Gets the udf attribute number.
	 *
	 * @return the udf attribute number
	 */
	public String getUdfAttributeNumber() {
		return udfAttributeNumber;
	}
	
	/**
	 * Sets the udf attribute number.
	 *
	 * @param udfAttributeNumber the new udf attribute number
	 */
	public void setUdfAttributeNumber(String udfAttributeNumber) {
		this.udfAttributeNumber = udfAttributeNumber;
	}
	
	
}
