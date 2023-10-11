package com.att.sales.nexxus.reteriveicb.model;

import java.util.List;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.map.annotate.JsonSerialize;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class NexxusTDDMessage {
	
	/** The type of msg. */
	private String typeOfMsg;
	
	/** The status code. */
	private String statusCode;
	
	/** The status message. */
	private String statusMessage;
	
	private NexxusResponse response;
	
	private List<DesignDetailsTDD> designDetails;
	
	
	public String getTypeOfMsg() {
		return typeOfMsg;
	}
	public void setTypeOfMsg(String typeOfMsg) {
		this.typeOfMsg = typeOfMsg;
	}
	public String getStatusCode() {
		return statusCode;
	}
	public void setStatusCode(String statusCode) {
		this.statusCode = statusCode;
	}
	public String getStatusMessage() {
		return statusMessage;
	}
	public void setStatusMessage(String statusMessage) {
		this.statusMessage = statusMessage;
	}
	public NexxusResponse getResponse() {
		return response;
	}
	public void setResponse(NexxusResponse response) {
		this.response = response;
	}
	public List<DesignDetailsTDD> getDesignDetails() {
		return designDetails;
	}
	public void setDesignDetails(List<DesignDetailsTDD> designDetails) {
		this.designDetails = designDetails;
	}
	
	
	
	

}
