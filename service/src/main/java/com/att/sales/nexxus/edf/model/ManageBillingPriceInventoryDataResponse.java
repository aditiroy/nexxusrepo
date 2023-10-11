package com.att.sales.nexxus.edf.model;

import java.io.Serializable;

import org.codehaus.jackson.map.annotate.JsonSerialize;

import com.att.sales.framework.model.ServiceResponse;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
/*
 * @Author chandan
 */

/**
 * The Class ManageBillingPriceInventoryDataResponse.
 */
@SuppressWarnings("serial")
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class ManageBillingPriceInventoryDataResponse extends ServiceResponse {

	/** The manage billing price inventory data response. */
	private InnerManageBillingPriceInventoryDataResponse manageBillingPriceInventoryDataResponse;

	/**
	 * Gets the manage billing price inventory data response.
	 *
	 * @return the manage billing price inventory data response
	 */
	@JsonProperty("manageBillingPriceInventoryDataResponse")
	public InnerManageBillingPriceInventoryDataResponse getManageBillingPriceInventoryDataResponse() {
		return manageBillingPriceInventoryDataResponse;
	}

	/**
	 * Sets the manage billing price inventory data response.
	 *
	 * @param manageBillingPriceInventoryDataResponse the new manage billing price inventory data response
	 */
	@JsonProperty("manageBillingPriceInventoryDataResponse")
	public void setManageBillingPriceInventoryDataResponse(InnerManageBillingPriceInventoryDataResponse manageBillingPriceInventoryDataResponse) {
		this.manageBillingPriceInventoryDataResponse = manageBillingPriceInventoryDataResponse;
	}

	/**
	 * The Class InnerManageBillingPriceInventoryDataResponse.
	 */
	@JsonIgnoreProperties(ignoreUnknown = true)
	@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
	public static class InnerManageBillingPriceInventoryDataResponse implements Serializable {
		
		/** The request id. */
		private String requestId;
		
		/** The status. */
		private String status;

		/**
		 * Gets the request id.
		 *
		 * @return the request id
		 */
		@JsonProperty("request_ID")
		public String getRequestId() {
			return requestId;
		}

		/**
		 * Sets the request id.
		 *
		 * @param requestId the new request id
		 */
		@JsonProperty("request_ID")
		public void setRequestId(String requestId) {
			this.requestId = requestId;
		}

		/**
		 * Gets the status.
		 *
		 * @return the status
		 */
		public String getStatus() {
			return status;
		}

		/**
		 * Sets the status.
		 *
		 * @param status the new status
		 */
		public void setStatus(String status) {
			this.status = status;
		}

		/* (non-Javadoc)
		 * @see java.lang.Object#toString()
		 */
		@Override
		public String toString() {
			return "ManageBillingPriceInventoryDataResponse [requestId=" + requestId + ", status=" + status + "]";
		}
	}
}
