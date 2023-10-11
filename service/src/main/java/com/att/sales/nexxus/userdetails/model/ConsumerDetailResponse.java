package com.att.sales.nexxus.userdetails.model;

/**
*
*
* @author aa316k
*         
*/
import java.util.List;

import org.codehaus.jackson.map.annotate.JsonSerialize;

import com.att.sales.framework.model.ServiceResponse;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * The Class ConsumerDetailResponse.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonSerialize(include=JsonSerialize.Inclusion.NON_NULL)
public class ConsumerDetailResponse extends ServiceResponse {

	
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;
	
	/** The consumer details. */
	private List<ConsumerDetail> consumerDetails; 
	
	/** The status message. */
	private String statusMessage;
	
	/** The status code. */
	private String statusCode;
	
	
	/**
	 * Gets the status message.
	 *
	 * @return the status message
	 */
	public String getStatusMessage() {
		return statusMessage;
	}
	
	/**
	 * Sets the status message.
	 *
	 * @param statusMessage the new status message
	 */
	public void setStatusMessage(String statusMessage) {
		this.statusMessage = statusMessage;
	}
	
	/**
	 * Gets the status code.
	 *
	 * @return the status code
	 */
	public String getStatusCode() {
		return statusCode;
	}
	
	/**
	 * Sets the status code.
	 *
	 * @param statusCode the new status code
	 */
	public void setStatusCode(String statusCode) {
		this.statusCode = statusCode;
	}
	
	/**
	 * Gets the consumer details.
	 *
	 * @return the consumer details
	 */
	public List<ConsumerDetail> getConsumerDetails() {
		return consumerDetails;
	}
	
	/**
	 * Sets the consumer details.
	 *
	 * @param consumerDetails the new consumer details
	 */
	public void setConsumerDetails(List<ConsumerDetail> consumerDetails) {
		this.consumerDetails = consumerDetails;
	}

}
