package com.att.sales.nexxus.model;


/**
 * The Class PrdmSubscriptionStatusResponse.
 *
 * @author DevChouhan
 */
public class PrdmSubscriptionStatusResponse {
	
/** The Constant serialVersionUID. */
private static final long serialVersionUID = 1L;
	
	/** The correlation id. */
	private String correlationId;
	
	/** The design status. */
	private String designStatus;
	
	/**
	 * Gets the correlation id.
	 *
	 * @return the correlation id
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
	
	/**
	 * Gets the design status.
	 *
	 * @return the design status
	 */
	public String getDesignStatus() {
		return designStatus;
	}
	
	/**
	 * Sets the design status.
	 *
	 * @param designStatus the new design status
	 */
	public void setDesignStatus(String designStatus) {
		this.designStatus = designStatus;
	}
	
	
	
	

}
