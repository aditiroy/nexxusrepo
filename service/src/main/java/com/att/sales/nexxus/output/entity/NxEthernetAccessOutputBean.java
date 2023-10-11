package com.att.sales.nexxus.output.entity;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

/**
 * The Class NxEthernetAccessOutputBean.
 *
 * @author vt393d
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(Include.NON_NULL)	
public class NxEthernetAccessOutputBean extends NxBaseOutputBean implements Serializable{
	
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;
	
	
	/** The access architecture. */
	private String accessArchitecture;
	
	/** The ilec SWC cilli. */
	private String ilecSWCCilli;
	
	/** The accociated service. */
	private String accociatedService;
	
	/** The speed. */
	private String speed;
	
	/** The ethernet pop cilli. */
	private String ethernetPopCilli;
	
	/** The interface type. */
	private String interfaceType;
	
	/** The alternate provider. */
	private String alternateProvider;
	
	/** The premises code. */
	private String premisesCode;
	
	/**
	 * Gets the access architecture.
	 *
	 * @return the access architecture
	 */
	public String getAccessArchitecture() {
		return accessArchitecture;
	}
	
	/**
	 * Sets the access architecture.
	 *
	 * @param accessArchitecture the new access architecture
	 */
	public void setAccessArchitecture(String accessArchitecture) {
		this.accessArchitecture = accessArchitecture;
	}
	
	/**
	 * Gets the ilec SWC cilli.
	 *
	 * @return the ilec SWC cilli
	 */
	public String getIlecSWCCilli() {
		return ilecSWCCilli;
	}
	
	/**
	 * Sets the ilec SWC cilli.
	 *
	 * @param ilecSWCCilli the new ilec SWC cilli
	 */
	public void setIlecSWCCilli(String ilecSWCCilli) {
		this.ilecSWCCilli = ilecSWCCilli;
	}
	
	/**
	 * Gets the accociated service.
	 *
	 * @return the accociated service
	 */
	public String getAccociatedService() {
		return accociatedService;
	}
	
	/**
	 * Sets the accociated service.
	 *
	 * @param accociatedService the new accociated service
	 */
	public void setAccociatedService(String accociatedService) {
		this.accociatedService = accociatedService;
	}
	
	/**
	 * Gets the speed.
	 *
	 * @return the speed
	 */
	public String getSpeed() {
		return speed;
	}
	
	/**
	 * Sets the speed.
	 *
	 * @param speed the new speed
	 */
	public void setSpeed(String speed) {
		this.speed = speed;
	}
	
	/**
	 * Gets the ethernet pop cilli.
	 *
	 * @return the ethernet pop cilli
	 */
	public String getEthernetPopCilli() {
		return ethernetPopCilli;
	}

	/**
	 * Sets the ethernet pop cilli.
	 *
	 * @param ethernetPopCilli the new ethernet pop cilli
	 */
	public void setEthernetPopCilli(String ethernetPopCilli) {
		this.ethernetPopCilli = ethernetPopCilli;
	}

	/**
	 * Gets the interface type.
	 *
	 * @return the interface type
	 */
	public String getInterfaceType() {
		return interfaceType;
	}
	
	/**
	 * Sets the interface type.
	 *
	 * @param interfaceType the new interface type
	 */
	public void setInterfaceType(String interfaceType) {
		this.interfaceType = interfaceType;
	}
	
	/**
	 * Gets the alternate provider.
	 *
	 * @return the alternate provider
	 */
	public String getAlternateProvider() {
		return alternateProvider;
	}
	
	/**
	 * Sets the alternate provider.
	 *
	 * @param alternateProvider the new alternate provider
	 */
	public void setAlternateProvider(String alternateProvider) {
		this.alternateProvider = alternateProvider;
	}
	
	/**
	 * Gets the premises code.
	 *
	 * @return the premises code
	 */
	public String getPremisesCode() {
		return premisesCode;
	}
	
	/**
	 * Sets the premises code.
	 *
	 * @param premisesCode the new premises code
	 */
	public void setPremisesCode(String premisesCode) {
		this.premisesCode = premisesCode;
	}
	
}
