package com.att.sales.nexxus.model;

import java.math.BigDecimal;
import java.util.Date;

import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.codehaus.jackson.map.annotate.JsonSerialize.Inclusion;
import org.springframework.stereotype.Component;

/**
 * The Class PrdmSubscriptionStatusRequest.
 *
 * @author DevChouhan
 */
@Component
@JsonSerialize(include = Inclusion.NON_NULL)
public class PrdmSubscriptionStatusRequest {
	
	
	/** The transaction id. */
	private String transactionId;
	
	/** The status type. */
	private String statusType;
	
	/** The status. */
	private String status;
	
	/** The client. */
	private String client;
	
	/** The time stamp. */
	private  Date  timeStamp;
	
	/** The reason code. */
	private String reasonCode;
	
	/** The reason. */
	private String reason;
	
	/** The ms application. */
	private String msApplication;
	
	/** The domain object. */
	private String domainObject;
	
	/** The sales pddm id. */
	private BigDecimal salesPddmId;
	
	/**
	 * Gets the transaction id.
	 *
	 * @return the transaction id
	 */
	public String getTransactionId() {
		return transactionId;
	}
	
	/**
	 * Sets the transaction id.
	 *
	 * @param transactionId the new transaction id
	 */
	public void setTransactionId(String transactionId) {
		this.transactionId = transactionId;
	}
	
	/**
	 * Gets the status type.
	 *
	 * @return the status type
	 */
	public String getStatusType() {
		return statusType;
	}
	
	/**
	 * Sets the status type.
	 *
	 * @param statusType the new status type
	 */
	public void setStatusType(String statusType) {
		this.statusType = statusType;
	}
	
	/**
	 * Gets the status.
	 *
	 * @return the status
	 */
	public String getStatus() {
		return status;
	}
	
	/**
	 * Sets the status.
	 *
	 * @param status the new status
	 */
	public void setStatus(String status) {
		this.status = status;
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
	 * @param client the new client
	 */
	public void setClient(String client) {
		this.client = client;
	}
	
	/**
	 * Gets the time stamp.
	 *
	 * @return the time stamp
	 */
	public Date getTimeStamp() {
		return timeStamp;
	}
	
	/**
	 * Sets the time stamp.
	 *
	 * @param timeStamp the new time stamp
	 */
	public void setTimeStamp(Date timeStamp) {
		this.timeStamp = timeStamp;
	}
	
	/**
	 * Gets the reason code.
	 *
	 * @return the reason code
	 */
	public String getReasonCode() {
		return reasonCode;
	}
	
	/**
	 * Sets the reason code.
	 *
	 * @param reasonCode the new reason code
	 */
	public void setReasonCode(String reasonCode) {
		this.reasonCode = reasonCode;
	}
	
	/**
	 * Gets the reason.
	 *
	 * @return the reason
	 */
	public String getReason() {
		return reason;
	}
	
	/**
	 * Sets the reason.
	 *
	 * @param reason the new reason
	 */
	public void setReason(String reason) {
		this.reason = reason;
	}
	
	/**
	 * Gets the ms application.
	 *
	 * @return the ms application
	 */
	public String getMsApplication() {
		return msApplication;
	}
	
	/**
	 * Sets the ms application.
	 *
	 * @param msApplication the new ms application
	 */
	public void setMsApplication(String msApplication) {
		this.msApplication = msApplication;
	}
	
	/**
	 * Gets the domain object.
	 *
	 * @return the domain object
	 */
	public String getDomainObject() {
		return domainObject;
	}
	
	/**
	 * Sets the domain object.
	 *
	 * @param domainObject the new domain object
	 */
	public void setDomainObject(String domainObject) {
		this.domainObject = domainObject;
	}
	
	/**
	 * Gets the sales pddm id.
	 *
	 * @return the sales pddm id
	 */
	public BigDecimal getSalesPddmId() {
		return salesPddmId;
	}
	
	/**
	 * Sets the sales pddm id.
	 *
	 * @param salesPddmId the new sales pddm id
	 */
	public void setSalesPddmId(BigDecimal salesPddmId) {
		this.salesPddmId = salesPddmId;
	}
	

}
