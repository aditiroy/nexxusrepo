package com.att.sales.nexxus.pricing.model;

import java.util.List;

import org.codehaus.jackson.map.annotate.JsonSerialize;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class Message {
	private Response response;
	private List<DesignDetails> designDetails;

	public Response getResponse() {
		return response;
	}

	public void setResponse(Response response) {
		this.response = response;
	}

	public List<DesignDetails> getDesignDetails() {
		return designDetails;
	}

	public void setDesignDetails(List<DesignDetails> designDetails) {
		this.designDetails = designDetails;
	}

}
