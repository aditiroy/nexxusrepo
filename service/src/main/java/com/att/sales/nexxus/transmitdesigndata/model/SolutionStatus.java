package com.att.sales.nexxus.transmitdesigndata.model;

import java.util.List;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
/**
 * The Class SolutionStatus.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class SolutionStatus {

	@JsonProperty("opportunityId")
	private String opportunityId;
	
	@JsonProperty("solutionId")
	private Integer solutionId;
	
	@JsonProperty("nxSolutionId")
	private String nxSolutionId;
	
	@JsonProperty("statusCode")
	private String statusCode;
	
	@JsonProperty("responseType")
	private String responseType;
	
	@JsonProperty("statusDescription")
	private String statusDescription;
	
	@JsonProperty("solutionCancellationReason")
	private String solutionCancellationReason;
	
	@JsonProperty("circuitDetails")
	@JsonInclude(JsonInclude.Include.NON_EMPTY)
	private List<CircuitDetails> circuitDetails;
	

	@JsonProperty("portDetails")
	@JsonInclude(JsonInclude.Include.NON_EMPTY)
	private List<PortDetails> portDetails;
	
	
	public String getOpportunityId() {
		return opportunityId;
	}
	public void setOpportunityId(String opportunityId) {
		this.opportunityId = opportunityId;
	}
	
	public Integer getSolutionId() {
		return solutionId;
	}
	public void setSolutionId(Integer solutionId) {
		this.solutionId = solutionId;
	}
	public String getNxSolutionId() {
		return nxSolutionId;
	}
	public void setNxSolutionId(String nxSolutionId) {
		this.nxSolutionId = nxSolutionId;
	}
	public String getStatusCode() {
		return statusCode;
	}
	public void setStatusCode(String statusCode) {
		this.statusCode = statusCode;
	}
	public String getResponseType() {
		return responseType;
	}
	public void setResponseType(String responseType) {
		this.responseType = responseType;
	}
	public String getStatusDescription() {
		return statusDescription;
	}
	public void setStatusDescription(String statusDescription) {
		this.statusDescription = statusDescription;
	}
	public String getSolutionCancellationReason() {
		return solutionCancellationReason;
	}
	public void setSolutionCancellationReason(String solutionCancellationReason) {
		this.solutionCancellationReason = solutionCancellationReason;
	}
	public List<CircuitDetails> getCircuitDetails() {
		return circuitDetails;
	}
	public void setCircuitDetails(List<CircuitDetails> circuitDetails) {
		this.circuitDetails = circuitDetails;
	}
	public List<PortDetails> getPortDetails() {
		return portDetails;
	}
	public void setPortDetails(List<PortDetails> portDetails) {
		this.portDetails = portDetails;
	}
	
	
}
