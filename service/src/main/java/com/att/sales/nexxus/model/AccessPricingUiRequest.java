package com.att.sales.nexxus.model;

import java.util.List;
import java.util.Set;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.map.annotate.JsonSerialize;

import com.att.sales.framework.validation.APIFieldProperty;
import com.att.sales.nexxus.admin.model.UploadEthTokenRequest;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * The Class AccessPricingUiRequest.
 */
@SuppressWarnings("serial")
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class AccessPricingUiRequest {

	/** The action. */
	@APIFieldProperty(required = true)
	private String action;

	/** The nx solution id. */
	private Long nxSolutionId;

	/** The user id. */
	@APIFieldProperty(required = true)
	private String userId;

	/** The request type. */
	@APIFieldProperty(required = true)
	private String requestType;

	/** The dq id. */
	@APIFieldProperty(required = true)
	private List<String> dqId;

	/** The country. */
	@APIFieldProperty(required = true)
	private String country;

	/** The query type. */
	@APIFieldProperty(required = true)
	private String queryType;

	/** The quote details. */
	@APIFieldProperty(required = true)
	private List<QuoteDetails> quoteDetails;

	private Long siteId;

	private String portId;

	private String siteRefId;

	private boolean bulkupload;

	private List<UploadEthTokenRequest> bulkUploadTokens;
	
	private List<String> iglooQuoteId;
	
	private String actionPerformedBy;
	
	/** The circuit id. */
	@APIFieldProperty(required = true)
	private List<String> circuitId;
	
	public List<String> getCircuitId() {
		return circuitId;
	}

	public void setCircuitId(List<String> circuitId) {
		this.circuitId = circuitId;
	}

	/**
	 * Gets the user id.
	 *
	 * @return the user id
	 */
	public String getUserId() {
		return userId;
	}

	/**
	 * Sets the user id.
	 *
	 * @param userId the new user id
	 */
	public void setUserId(String userId) {
		this.userId = userId;
	}

	/**
	 * Gets the request type.
	 *
	 * @return the request type
	 */
	@JsonProperty("requestType")
	public String getRequestType() {
		return requestType;
	}

	/**
	 * Sets the request type.
	 *
	 * @param requestType the new request type
	 */
	public void setRequestType(String requestType) {
		this.requestType = requestType;
	}

	/**
	 * Gets the dq id.
	 *
	 * @return the dq id
	 */
	@JsonProperty("dqId")
	public List<String> getDqId() {
		return dqId;
	}

	/**
	 * Sets the dq id.
	 *
	 * @param dqId the new dq id
	 */
	public void setDqId(List<String> dqId) {
		this.dqId = dqId;
	}

	/**
	 * Gets the query type.
	 *
	 * @return the query type
	 */
	@JsonProperty("queryType")
	public String getQueryType() {
		return queryType;
	}

	/**
	 * Sets the query type.
	 *
	 * @param queryType the new query type
	 */
	public void setQueryType(String queryType) {
		this.queryType = queryType;
	}

	/**
	 * Gets the country.
	 *
	 * @return the country
	 */
	@JsonProperty("country")
	public String getCountry() {
		return country;
	}

	/**
	 * Sets the country.
	 *
	 * @param country the country to set
	 */
	public void setCountry(String country) {
		this.country = country;
	}

	/**
	 * Gets the action.
	 *
	 * @return the action
	 */
	@JsonProperty("action")
	public String getAction() {
		return action;
	}

	/**
	 * Sets the action.
	 *
	 * @param action the new action
	 */
	public void setAction(String action) {
		this.action = action;
	}

	/**
	 * Gets the nx solution id.
	 *
	 * @return the nx solution id
	 */
	@JsonProperty("nxSolutionId")
	public Long getNxSolutionId() {
		return nxSolutionId;
	}

	/**
	 * Sets the nx solution id.
	 *
	 * @param nxSolutionId the new nx solution id
	 */
	public void setNxSolutionId(Long nxSolutionId) {
		this.nxSolutionId = nxSolutionId;
	}

	/**
	 * Gets the quote details.
	 *
	 * @return the quote details
	 */
	@JsonProperty("quoteDetails")
	public List<QuoteDetails> getQuoteDetails() {
		return quoteDetails;
	}

	/**
	 * Sets the quote details.
	 *
	 * @param quoteDetails the new quote details
	 */
	public void setQuoteDetails(List<QuoteDetails> quoteDetails) {
		this.quoteDetails = quoteDetails;
	}

	public Long getSiteId() {
		return siteId;
	}

	public void setSiteId(Long siteId) {
		this.siteId = siteId;
	}

	public String getPortId() {
		return portId;
	}

	public void setPortId(String portId) {
		this.portId = portId;
	}

	public String getSiteRefId() {
		return siteRefId;
	}

	public void setSiteRefId(String siteRefId) {
		this.siteRefId = siteRefId;
	}

	public boolean isBulkupload() {
		return bulkupload;
	}

	public void setBulkupload(boolean bulkupload) {
		this.bulkupload = bulkupload;
	}

	public List<UploadEthTokenRequest> getBulkUploadTokens() {
		return bulkUploadTokens;
	}

	public void setBulkUploadTokens(List<UploadEthTokenRequest> bulkUploadTokens) {
		this.bulkUploadTokens = bulkUploadTokens;
	}

	public List<String> getIglooQuoteId() {
		return iglooQuoteId;
	}

	public void setIglooQuoteId(List<String> iglooQuoteId) {
		this.iglooQuoteId = iglooQuoteId;
	}

	public String getActionPerformedBy() {
		return actionPerformedBy;
	}

	public void setActionPerformedBy(String actionPerformedBy) {
		this.actionPerformedBy = actionPerformedBy;
	}
	
	
	
}
