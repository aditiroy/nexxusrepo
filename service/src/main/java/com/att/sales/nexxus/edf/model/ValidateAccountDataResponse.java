package com.att.sales.nexxus.edf.model;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.map.annotate.JsonSerialize;

import com.att.sales.framework.model.ServiceResponse;

/**
 * @author KRani The Class ValidateAccountDataResponse.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class ValidateAccountDataResponse extends ServiceResponse {

	/** The correlationId. */
	private Integer correlationId;

	/**
	 * @return the correlationId
	 */
	public Integer getCorrelationId() {
		return correlationId;
	}

	/**
	 * @param correlationId the correlationId to set
	 */
	public void setCorrelationId(Integer correlationId) {
		this.correlationId = correlationId;
	}

	@Override
	public String toString() {
		return "ValidateAccountDataResponse [correlationId=" + correlationId + "]";
	}

}