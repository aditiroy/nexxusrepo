package com.att.sales.nexxus.output.entity;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

/**
 * The Class NxAvpnIntAccessOutputBean.
 *
 * @author vt393d
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(Include.NON_NULL)	
public class NxAvpnIntAccessOutputBean extends NxBaseOutputBean  implements Serializable{
	
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;
	
	
	/** The site aliase. */
	private String siteAliase;
	
	/** The access bandwidth. */
	private String accessBandwidth;
	
	/** The access primary or backup. */
	private String accessPrimaryOrBackup;
	
	/** The access pop cilli. */
	private String accessPopCilli;
	
	/** The existing telco circuit id. */
	private String existingTelcoCircuitId;
	
	/** The existing telco provider. */
	private String existingTelcoProvider;
	
	/** The igloo id. */
	private String iglooId;
	
	/** The access tail technology. */
	private String accessTailTechnology;
	
	/**
	 * Gets the site aliase.
	 *
	 * @return the site aliase
	 */
	public String getSiteAliase() {
		return siteAliase;
	}
	
	/**
	 * Sets the site aliase.
	 *
	 * @param siteAliase the new site aliase
	 */
	public void setSiteAliase(String siteAliase) {
		this.siteAliase = siteAliase;
	}
	
	/**
	 * Gets the access bandwidth.
	 *
	 * @return the access bandwidth
	 */
	public String getAccessBandwidth() {
		return accessBandwidth;
	}
	
	/**
	 * Sets the access bandwidth.
	 *
	 * @param accessBandwidth the new access bandwidth
	 */
	public void setAccessBandwidth(String accessBandwidth) {
		this.accessBandwidth = accessBandwidth;
	}
	
	/**
	 * Gets the access primary or backup.
	 *
	 * @return the access primary or backup
	 */
	public String getAccessPrimaryOrBackup() {
		return accessPrimaryOrBackup;
	}
	
	/**
	 * Sets the access primary or backup.
	 *
	 * @param accessPrimaryOrBackup the new access primary or backup
	 */
	public void setAccessPrimaryOrBackup(String accessPrimaryOrBackup) {
		this.accessPrimaryOrBackup = accessPrimaryOrBackup;
	}
	
	/**
	 * Gets the access pop cilli.
	 *
	 * @return the access pop cilli
	 */
	public String getAccessPopCilli() {
		return accessPopCilli;
	}
	
	/**
	 * Sets the access pop cilli.
	 *
	 * @param accessPopCilli the new access pop cilli
	 */
	public void setAccessPopCilli(String accessPopCilli) {
		this.accessPopCilli = accessPopCilli;
	}
	
	/**
	 * Gets the existing telco circuit id.
	 *
	 * @return the existing telco circuit id
	 */
	public String getExistingTelcoCircuitId() {
		return existingTelcoCircuitId;
	}
	
	/**
	 * Sets the existing telco circuit id.
	 *
	 * @param existingTelcoCircuitId the new existing telco circuit id
	 */
	public void setExistingTelcoCircuitId(String existingTelcoCircuitId) {
		this.existingTelcoCircuitId = existingTelcoCircuitId;
	}
	
	/**
	 * Gets the existing telco provider.
	 *
	 * @return the existing telco provider
	 */
	public String getExistingTelcoProvider() {
		return existingTelcoProvider;
	}
	
	/**
	 * Sets the existing telco provider.
	 *
	 * @param existingTelcoProvider the new existing telco provider
	 */
	public void setExistingTelcoProvider(String existingTelcoProvider) {
		this.existingTelcoProvider = existingTelcoProvider;
	}
	
	/**
	 * Gets the igloo id.
	 *
	 * @return the igloo id
	 */
	public String getIglooId() {
		return iglooId;
	}
	
	/**
	 * Sets the igloo id.
	 *
	 * @param iglooId the new igloo id
	 */
	public void setIglooId(String iglooId) {
		this.iglooId = iglooId;
	}
	
	/**
	 * Gets the access tail technology.
	 *
	 * @return the access tail technology
	 */
	public String getAccessTailTechnology() {
		return accessTailTechnology;
	}
	
	/**
	 * Sets the access tail technology.
	 *
	 * @param accessTailTechnology the new access tail technology
	 */
	public void setAccessTailTechnology(String accessTailTechnology) {
		this.accessTailTechnology = accessTailTechnology;
	}
	
	
	

}
