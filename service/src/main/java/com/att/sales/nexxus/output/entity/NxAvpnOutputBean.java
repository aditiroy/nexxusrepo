package com.att.sales.nexxus.output.entity;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

/**
 * The Class NxAvpnOutputBean.
 *
 * @author vt393d
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(Include.NON_NULL)
public class NxAvpnOutputBean extends NxBaseOutputBean  implements Serializable{
	
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;
	
	/** The port speed. */
	private String portSpeed;
	
	/** The port protocol. */
	private String portProtocol; 
	
	
	
	/**
	 * Gets the port speed.
	 *
	 * @return the port speed
	 */
	public String getPortSpeed() {
		return portSpeed;
	}
	
	/**
	 * Sets the port speed.
	 *
	 * @param portSpeed the new port speed
	 */
	public void setPortSpeed(String portSpeed) {
		this.portSpeed = portSpeed;
	}
	
	/**
	 * Gets the port protocol.
	 *
	 * @return the port protocol
	 */
	public String getPortProtocol() {
		return portProtocol;
	}
	
	/**
	 * Sets the port protocol.
	 *
	 * @param portProtocol the new port protocol
	 */
	public void setPortProtocol(String portProtocol) {
		this.portProtocol = portProtocol;
	}
	

}
