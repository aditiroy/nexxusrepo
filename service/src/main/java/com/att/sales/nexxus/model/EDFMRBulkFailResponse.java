package com.att.sales.nexxus.model;

import java.io.Serializable;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * The Class EDFMRBulkFailResponse.
 *
 * * @author(ar896d) 
 * 
 * The EDFMRBulkFailResponse class 
 *
 */
public class EDFMRBulkFailResponse implements Serializable{

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;
	  @JsonProperty("Request_id")
	  private String requestId = null;

	  @JsonProperty("PGM")
	  private String pgm = null;

	  @JsonProperty("User_id")
	  private String userId = null;

	  @JsonProperty("Application")
	  private String application = null;

	  @JsonProperty("status")
	  private Integer status ;

	  @JsonProperty("failedAccountSet")
	  private List<FailedAccount> failedAccountSet = null;

	public String getRequestId() {
		return requestId;
	}

	public void setRequestId(String requestId) {
		this.requestId = requestId;
	}

	public String getPgm() {
		return pgm;
	}

	public void setPgm(String pgm) {
		this.pgm = pgm;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getApplication() {
		return application;
	}

	public void setApplication(String application) {
		this.application = application;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public List<FailedAccount> getFailedAccountSet() {
		return failedAccountSet;
	}

	public void setFailedAccountSet(List<FailedAccount> failedAccountSet) {
		this.failedAccountSet = failedAccountSet;
	}

}