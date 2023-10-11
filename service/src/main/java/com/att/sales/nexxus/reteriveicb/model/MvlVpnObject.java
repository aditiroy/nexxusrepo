package com.att.sales.nexxus.reteriveicb.model;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.map.annotate.JsonSerialize;


/**
 * The Class MvlVpnObject.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class MvlVpnObject {
	
	/** The component. */
	private Component component;

	/**
	 * Gets the component.
	 *
	 * @return the component
	 */
	public Component getComponent() {
		return component;
	}

	/**
	 * Sets the component.
	 *
	 * @param component the new component
	 */
	public void setComponent(Component component) {
		this.component = component;
	}
}
