package com.att.sales.nexxus.model;

public class AuditDetails {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;
	
	private String message;
	
	private String status;
	
	private Long createdDate;
	
	public AuditDetails(String message, String status, Long createdDate) {
		super();
		this.message = message;
		this.status = status;
		this.createdDate = createdDate; 
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Long getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(Long createdDate) {
		this.createdDate = createdDate;
	}
	
	
}
