package com.att.sales.nexxus.rateletter.model;

/*
 * @author Ruchi Yadav
 * 
 */
import com.att.sales.framework.model.ServiceResponse;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)

public class RateLetterStatusResponse extends ServiceResponse {

	
	private static final long serialVersionUID = 1L;
	
	

}
