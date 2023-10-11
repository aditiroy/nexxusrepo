/**
 * 
 */
package com.att.sales.nexxus.reteriveicb.model;

import com.att.sales.framework.model.ServiceResponse;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * @author aa316k
 *
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SolutionCostIndicatorResponse extends ServiceResponse {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

}
