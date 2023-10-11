package com.att.sales.nexxus.datarouter.model;

import org.codehaus.jackson.map.annotate.JsonSerialize;

import com.att.sales.framework.model.ServiceResponse;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonSerialize(include = JsonSerialize.Inclusion.NON_EMPTY)
public class DataRouterResponseBean extends ServiceResponse{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String output;

	public String getOutput() {
		return output;
	}

	public void setOutput(String output) {
		this.output = output;
	}
	

}
