package com.att.sales.nexxus.reteriveicb.model;

import java.util.List;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.map.annotate.JsonSerialize;


/**
 * The Class LogicalChannel.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class LogicalChannel {
	
	/** The component. */
	private List<Component> component;
	
	/** The lc validationmsg. */
	private InterfaceorchMessage lcValidationmsg;
	
	/** The vpns. */
	private List<MvlVpnObject> vpns;

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
	 * Gets the lc validationmsg.
	 *
	 * @return the lc validationmsg
	 */
	public InterfaceorchMessage getLcValidationmsg() {
		return lcValidationmsg;
	}

	/**
	 * Sets the lc validationmsg.
	 *
	 * @param lcValidationmsg the new lc validationmsg
	 */
	public void setLcValidationmsg(InterfaceorchMessage lcValidationmsg) {
		this.lcValidationmsg = lcValidationmsg;
	}
	
	/**
	 * Gets the vpns.
	 *
	 * @return the vpns
	 */
	public List<MvlVpnObject> getVpns() {
		return vpns;
	}

	/**
	 * Sets the vpns.
	 *
	 * @param vpns the new vpns
	 */
	public void setVpns(List<MvlVpnObject> vpns) {
		this.vpns = vpns;
	}
}
