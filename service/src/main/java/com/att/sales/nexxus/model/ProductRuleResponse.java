package com.att.sales.nexxus.model;

import java.io.Serializable;
import com.att.sales.framework.model.ServiceResponse;
import com.att.sales.framework.model.Status;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;



/**
 * The Class ProductRuleResponse.
 *
 * @author 
 */
public class ProductRuleResponse extends ServiceResponse implements Serializable  {
	
	/** The Constant serialVersionUID. */
	@JsonInclude(Include.NON_NULL)

	
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1015399252230439816L;

	/** The correlation id. */
	private String correlationId;
	
	
	
	/**
	 * Instantiates a new service response.
	 *
	 * @inheritDoc Default constructor.
	 */
	public ProductRuleResponse() {
		super();

	}
	
	
	/**
	 * Instantiates a new service response.
	 *
	 * @param status
	 *            the status
	 * @inheritDoc constructor.
	 */
	public ProductRuleResponse(Status status) {
		super();
		this.setStatus(status);

	}


	/**
	 * Sets the request id.
	 *
	 * @return the correlation id
	 */
	

		

		
	


	/**
	 * @return the correlationId
	 */
	public String getCorrelationId() {
		return correlationId;
	}


	/**
	 * Sets the correlation id.
	 *
	 * @param correlationId the new correlation id
	 */
	public void setCorrelationId(String correlationId) {
		this.correlationId = correlationId;
	}





	

	
}
