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
 * The Class VpnObject.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class VpnObject {
	
	/** The component. */
	private List<Component> component;
	
	/** The vpn validationmsg. */
	private InterfaceorchMessage vpnValidationmsg;
	
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
	 * Gets the vpn validationmsg.
	 *
	 * @return the vpn validationmsg
	 */
	public InterfaceorchMessage getVpnValidationmsg() {
		return vpnValidationmsg;
	}
	
	/**
	 * Sets the vpn validationmsg.
	 *
	 * @param vpnValidationmsg the new vpn validationmsg
	 */
	public void setVpnValidationmsg(InterfaceorchMessage vpnValidationmsg) {
		this.vpnValidationmsg = vpnValidationmsg;
	}
}
