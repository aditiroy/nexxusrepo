package com.att.sales.nexxus.reteriveicb.model;

import java.util.List;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.map.annotate.JsonSerialize;


/**
 * The Class Cpe.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class Cpe {
	
	/** The router id. */
	private Long routerId;
	
	/** The component. */
	private List<Component> component;
	
	/** The cpe validation message. */
	private InterfaceorchMessage cpeValidationMessage;

	/**
	 * Gets the router id.
	 *
	 * @return the router id
	 */
	public Long getRouterId() {
		return routerId;
	}

	/**
	 * Sets the router id.
	 *
	 * @param routerId the new router id
	 */
	public void setRouterId(Long routerId) {
		this.routerId = routerId;
	}

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

	/**
	 * Gets the cpe validation message.
	 *
	 * @return the cpe validation message
	 */
	public InterfaceorchMessage getCpeValidationMessage() {
		return cpeValidationMessage;
	}

	/**
	 * Sets the cpe validation message.
	 *
	 * @param cpeValidationMessage the new cpe validation message
	 */
	public void setCpeValidationMessage(InterfaceorchMessage cpeValidationMessage) {
		this.cpeValidationMessage = cpeValidationMessage;
	}

}
