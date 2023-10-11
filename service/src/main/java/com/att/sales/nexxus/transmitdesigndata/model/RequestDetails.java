package com.att.sales.nexxus.transmitdesigndata.model;

import java.io.Serializable;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.map.annotate.JsonSerialize;

/**
 * The Class RequestDetails.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class RequestDetails implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@JsonProperty("accessServiceRequestItemId")
	private String asrItemId;
	private String offer;
	
	public String getAsrItemId() {
		return asrItemId;
	}
	public void setAsrItemId(String asrItemId) {
		this.asrItemId = asrItemId;
	}
	public String getOffer() {
		return offer;
	}
	public void setOffer(String offer) {
		this.offer = offer;
	}
	
	

}
