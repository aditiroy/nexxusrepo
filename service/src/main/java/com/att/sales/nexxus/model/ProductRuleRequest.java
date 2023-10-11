package com.att.sales.nexxus.model;

import java.io.InputStream;

import com.att.sales.framework.model.ServiceRequest;

/**
 * The Class ProductRuleRequest.
 *
 * @author 
 */
public class ProductRuleRequest extends ServiceRequest{

	/** The type of rule. */
	private String typeOfRule;
	
	/** The client. */
	private String client;
	
	/** The correlation ID. */
	private String correlationID;
	
	/** The call back url. */
	private String callBackUrl;
	
	/** The uploaded input stream. */
	private InputStream uploadedInputStream;
	
	/**
	 * Instantiates a new product rule request.
	 */
	public ProductRuleRequest(){
		//empty default constructor
	}
	
	
	/**
	 * Instantiates a new product rule request.
	 *
	 * @param typeOfRule the type of rule
	 * @param client the client
	 * @param correlationID the correlation ID
	 * @param callBackUrl the call back url
	 */
	public ProductRuleRequest(String typeOfRule, String client, /*String fullDumpYN,*/
			String correlationID, String callBackUrl) {
		super();
		this.typeOfRule = typeOfRule;
		this.client = client;
		this.correlationID = correlationID;
		this.callBackUrl = callBackUrl;
	}
	
	
	
	/**
	 * Gets the type of rule.
	 *
	 * @return the typeOfRule
	 */
	public String getTypeOfRule() {
		return typeOfRule;
	}
	
	/**
	 * Sets the type of rule.
	 *
	 * @param typeOfRule the typeOfRule to set
	 */
	public void setTypeOfRule(String typeOfRule) {
		this.typeOfRule = typeOfRule;
	}
	
	/**
	 * Gets the client.
	 *
	 * @return the client
	 */
	public String getClient() {
		return client;
	}
	
	/**
	 * Sets the client.
	 *
	 * @param client the client to set
	 */
	public void setClient(String client) {
		this.client = client;
	}
	
	/**
	 * Gets the correlation ID.
	 *
	 * @return the correlationID
	 */
	public String getCorrelationID() {
		return correlationID;
	}
	
	/**
	 * Sets the correlation ID.
	 *
	 * @param correlationID the correlationID to set
	 */
	public void setCorrelationID(String correlationID) {
		this.correlationID = correlationID;
	}


	/**
	 * Gets the call back url.
	 *
	 * @return the callBackUrl
	 */
	public String getCallBackUrl() {
		return callBackUrl;
	}


	/**
	 * Sets the call back url.
	 *
	 * @param callBackUrl the callBackUrl to set
	 */
	public void setCallBackUrl(String callBackUrl) {
		this.callBackUrl = callBackUrl;
	}


	/**
	 * Gets the uploaded input stream.
	 *
	 * @return the uploadedInputStream
	 */
	public InputStream getUploadedInputStream() {
		return uploadedInputStream;
	}


	/**
	 * Sets the uploaded input stream.
	 *
	 * @param uploadedInputStream the uploadedInputStream to set
	 */
	public void setUploadedInputStream(InputStream uploadedInputStream) {
		this.uploadedInputStream = uploadedInputStream;
	}

}