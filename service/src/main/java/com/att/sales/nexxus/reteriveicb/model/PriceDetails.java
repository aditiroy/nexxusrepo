package com.att.sales.nexxus.reteriveicb.model;

import java.util.List;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.map.annotate.JsonSerialize;


/**
 * The Class PriceDetails.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class PriceDetails {
	
	/** The component details. */
	private List<ComponentDetails> componentDetails;	
	
	/** The price message. */
	private InterfaceorchMessage priceMessage;
	
	/**
	 * Gets the component details.
	 *
	 * @return the component details
	 */
	public List<ComponentDetails> getComponentDetails() {
		return componentDetails;
	}
	
	/**
	 * Sets the component details.
	 *
	 * @param componentDetails the new component details
	 */
	public void setComponentDetails(List<ComponentDetails> componentDetails) {
		this.componentDetails = componentDetails;
	}
	
	/**
	 * Gets the price message.
	 *
	 * @return the price message
	 */
	public InterfaceorchMessage getPriceMessage() {
		return priceMessage;
	}
	
	/**
	 * Sets the price message.
	 *
	 * @param priceMessage the new price message
	 */
	public void setPriceMessage(InterfaceorchMessage priceMessage) {
		this.priceMessage = priceMessage;
	}
}
