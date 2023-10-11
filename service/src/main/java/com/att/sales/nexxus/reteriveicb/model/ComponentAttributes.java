package com.att.sales.nexxus.reteriveicb.model;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.map.annotate.JsonSerialize;


/**
 * The Class ComponentAttributes.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class ComponentAttributes {
	
	/** The component field name. */
	private String componentFieldName;
	
	/** The component field value. */
	private String componentFieldValue;
	
	/**
	 * Gets the component field name.
	 *
	 * @return the component field name
	 */
	public String getComponentFieldName() {
		return componentFieldName;
	}
	
	/**
	 * Sets the component field name.
	 *
	 * @param componentFieldName the new component field name
	 */
	public void setComponentFieldName(String componentFieldName) {
		this.componentFieldName = componentFieldName;
	}
	
	/**
	 * Gets the component field value.
	 *
	 * @return the component field value
	 */
	public String getComponentFieldValue() {
		return componentFieldValue;
	}
	
	/**
	 * Sets the component field value.
	 *
	 * @param componentFieldValue the new component field value
	 */
	public void setComponentFieldValue(String componentFieldValue) {
		this.componentFieldValue = componentFieldValue;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "ComponentAttributes [componentFieldName=" + componentFieldName + ", componentFieldValue=" + componentFieldValue+"]";
	}
}
