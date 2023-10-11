package com.att.sales.nexxus.model;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.map.annotate.JsonSerialize;


/**
 * The Class QueoteRequestList.
 */
@SuppressWarnings("serial")
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonSerialize(include=JsonSerialize.Inclusion.NON_NULL)
public class QueoteRequestList {
	
	/** The user id. */
	private String userId;
	
	/** The request type. */
	private String requestType;
	
	/** The dq id. */
	private String dqId;
	
	/** The country. */
	private String country;
	
	/** The query type. */
	private String queryType;
	
	private Long siteId;
	
	private String portId;
	
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
	public String getDqId() {
		return dqId;
	}
	
	/**
	 * Sets the dq id.
	 *
	 * @param dqId the new dq id
	 */
	public void setDqId(String dqId) {
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

	
}
