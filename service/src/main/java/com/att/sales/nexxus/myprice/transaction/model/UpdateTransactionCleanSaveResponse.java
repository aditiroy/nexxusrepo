package com.att.sales.nexxus.myprice.transaction.model;


import org.codehaus.jackson.map.annotate.JsonSerialize;

import com.att.sales.framework.model.ServiceResponse;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Getter;
import lombok.Setter;

/**
 * @author IndraSingh
 */
@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class UpdateTransactionCleanSaveResponse extends ServiceResponse{

	private static final long serialVersionUID = 1L;
	
	private String exceptionCode;
	private String exceptionMessage;
	private String exceptionDescription;

}
