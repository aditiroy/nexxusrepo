package com.att.sales.nexxus.accesspricing.model;

import java.util.List;

import org.codehaus.jackson.map.annotate.JsonSerialize;

import com.att.sales.framework.model.ServiceResponse;
import com.att.sales.nexxus.model.QuoteResponse;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;


/**
 * The Class AccessPricingResponse.
 */
@SuppressWarnings("serial")
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonSerialize(include=JsonSerialize.Inclusion.NON_NULL)
public class AccessPricingResponse extends ServiceResponse{

/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;
	
	/** The quote response. */
	private List<QuoteResponse> quoteResponse;

/**
 * Gets the quote response.
 *
 * @return the quote response
 */
public List<QuoteResponse> getQuoteResponse() {
	return quoteResponse;
}

/**
 * Sets the quote response.
 *
 * @param quoteResponse the new quote response
 */
public void setQuoteResponse(List<QuoteResponse> quoteResponse) {
	this.quoteResponse = quoteResponse;
}

@Override
public String toString() {
	return "AccessPricingResponse [quoteResponse=" + quoteResponse + "]";
}

}
