package com.att.sales.nexxus.model;

import java.io.Serializable;
import java.util.List;

import org.codehaus.jackson.map.annotate.JsonSerialize;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;


/**
 * The Class NodeObjectList.
 *
 * @author km017g
 */
@SuppressWarnings("serial")
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonSerialize(include=JsonSerialize.Inclusion.NON_NULL)
public class NodeObjectList implements Serializable{

	/** The node obj. */
	private NodeObj nodeObj;
	
	/** The price. */
	private List<PriceList> price;
	

	/**
	 * Gets the price.
	 *
	 * @return the price
	 */
	public List<PriceList> getPrice() {
		return price;
	}

	/**
	 * Sets the price.
	 *
	 * @param price the new price
	 */
	public void setPrice(List<PriceList> price) {
		this.price = price;
	}

	/**
	 * Gets the node obj.
	 *
	 * @return the node obj
	 */
	public NodeObj getNodeObj() {
		return nodeObj;
	}

	/**
	 * Sets the node obj.
	 *
	 * @param nodeObj the new node obj
	 */
	public void setNodeObj(NodeObj nodeObj) {
		this.nodeObj = nodeObj;
	}
	
	
	
}
