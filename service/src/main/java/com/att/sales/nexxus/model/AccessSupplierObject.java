package com.att.sales.nexxus.model;

import java.io.Serializable;
import java.util.List;

import org.codehaus.jackson.map.annotate.JsonSerialize;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * The Class AccessSupplierObject.
 *
 * @author km017g
 */
@SuppressWarnings("serial")
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonSerialize(include=JsonSerialize.Inclusion.NON_NULL)
public class AccessSupplierObject implements Serializable{
	
	 /** The itu code. */
 	private String ituCode;
	 
 	/** The supplier name. */
 	private String supplierName;
	 
 	/** The supplier tier. */
 	private String supplierTier;
	 
 	/** The lead time. */
 	private Integer leadTime;
	 
 	/** The pmtu. */
 	private String pmtu;
	 
 	/** The mtu support message. */
 	private String mtuSupportMessage;
	 
 	/** The node list. */
 	private List<NodeObjectList> nodeList;
	
	/**
	 * Gets the itu code.
	 *
	 * @return the itu code
	 */
	public String getItuCode() {
		return ituCode;
	}
	
	/**
	 * Sets the itu code.
	 *
	 * @param ituCode the new itu code
	 */
	public void setItuCode(String ituCode) {
		this.ituCode = ituCode;
	}
	
	/**
	 * Gets the supplier name.
	 *
	 * @return the supplier name
	 */
	public String getSupplierName() {
		return supplierName;
	}
	
	/**
	 * Sets the supplier name.
	 *
	 * @param supplierName the new supplier name
	 */
	public void setSupplierName(String supplierName) {
		this.supplierName = supplierName;
	}
	
	/**
	 * Gets the supplier tier.
	 *
	 * @return the supplier tier
	 */
	public String getSupplierTier() {
		return supplierTier;
	}
	
	/**
	 * Sets the supplier tier.
	 *
	 * @param supplierTier the new supplier tier
	 */
	public void setSupplierTier(String supplierTier) {
		this.supplierTier = supplierTier;
	}
	
	/**
	 * Gets the pmtu.
	 *
	 * @return the pmtu
	 */
	public String getPmtu() {
		return pmtu;
	}
	
	/**
	 * Sets the pmtu.
	 *
	 * @param pmtu the new pmtu
	 */
	public void setPmtu(String pmtu) {
		this.pmtu = pmtu;
	}
	
	/**
	 * Gets the mtu support message.
	 *
	 * @return the mtu support message
	 */
	public String getMtuSupportMessage() {
		return mtuSupportMessage;
	}
	
	/**
	 * Sets the mtu support message.
	 *
	 * @param mtuSupportMessage the new mtu support message
	 */
	public void setMtuSupportMessage(String mtuSupportMessage) {
		this.mtuSupportMessage = mtuSupportMessage;
	}
	
	/**
	 * Gets the node list.
	 *
	 * @return the node list
	 */
	public List<NodeObjectList> getNodeList() {
		return nodeList;
	}
	
	/**
	 * Sets the node list.
	 *
	 * @param nodeList the new node list
	 */
	public void setNodeList(List<NodeObjectList> nodeList) {
		this.nodeList = nodeList;
	}
	
	/**
	 * Gets the lead time.
	 *
	 * @return the lead time
	 */
	public Integer getLeadTime() {
		return leadTime;
	}
	
	/**
	 * Sets the lead time.
	 *
	 * @param leadTime the new lead time
	 */
	public void setLeadTime(Integer leadTime) {
		this.leadTime = leadTime;
	}
	
}
