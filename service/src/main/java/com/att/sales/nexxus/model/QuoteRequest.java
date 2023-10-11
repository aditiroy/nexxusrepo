package com.att.sales.nexxus.model;

import java.util.List;

import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.map.annotate.JsonSerialize;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;


/**
 * The Class QuoteRequest.
 */
/*
 * autor chandan(ck218y)
 */
@SuppressWarnings("serial")
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonSerialize(include=JsonSerialize.Inclusion.NON_NULL)
public class QuoteRequest {
	
	
	/** The quote request. */
	private List<QueoteRequestList> quoteRequest;
	
	/**
	 * Gets the quote request.
	 *
	 * @return the quote request
	 */
	@JsonProperty("quoteRequest")
	public List<QueoteRequestList> getQuoteRequest() {
		return quoteRequest;
	}

	/**
	 * Sets the quote request.
	 *
	 * @param quoteRequest the new quote request
	 */
	public void setQuoteRequest(List<QueoteRequestList> quoteRequest) {
		this.quoteRequest = quoteRequest;
	}


	

	
	
}
