package com.att.sales.nexxus.transmitdesigndata.model;

import java.util.List;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;
import org.codehaus.jackson.map.annotate.JsonSerialize;


/**
 * The Class TransmitDesignDataRequest.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonTypeName("transmitDesignDataRequest")                                                                                         
@JsonTypeInfo(include = JsonTypeInfo.As.WRAPPER_OBJECT ,use = JsonTypeInfo.Id.NAME)
public class TransmitDesignDataRequest {
	
	@JsonProperty("solutionStatus")
	@JsonInclude(JsonInclude.Include.NON_EMPTY)
	private List<SolutionStatus> solutionStatus;

	public List<SolutionStatus> getSolutionStatus() {
		return solutionStatus;
	}

	public void setSolutionStatus(List<SolutionStatus> solutionStatus) {
		this.solutionStatus = solutionStatus;
	}

	
}
