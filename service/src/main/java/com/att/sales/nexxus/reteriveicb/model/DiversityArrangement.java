package com.att.sales.nexxus.reteriveicb.model;

/*
 * @Author: Akash Arya
 * 
 * 
 */
import java.util.List;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.map.annotate.JsonSerialize;


/**
 * The Class DiversityArrangement.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class DiversityArrangement {
	
	/** The component. */
	private List<Component> component;

	/**
	 * Gets the component.
	 *
	 * @return the component
	 */
	public List<Component> getComponent() {
		return component;
	}

	/**
	 * Sets the component.
	 *
	 * @param component the new component
	 */
	public void setComponent(List<Component> component) {
		this.component = component;
	}
	
}
