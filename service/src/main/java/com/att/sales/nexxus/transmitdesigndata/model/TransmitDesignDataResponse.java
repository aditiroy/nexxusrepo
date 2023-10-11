package com.att.sales.nexxus.transmitdesigndata.model;

import java.util.List;

import org.codehaus.jackson.map.annotate.JsonSerialize;

import com.att.sales.framework.model.ServiceResponse;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * The Class TransmitDesignDataResponse.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class TransmitDesignDataResponse  extends ServiceResponse{
	
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;
	
	@JsonSerialize(include = JsonSerialize.Inclusion.NON_EMPTY)
	private List<RequestDetails> requestDetails;

	public List<RequestDetails> getRequestDetails() {
		return requestDetails;
	}

	public void setRequestDetails(List<RequestDetails> requestDetails) {
		this.requestDetails = requestDetails;
	}

	

}
