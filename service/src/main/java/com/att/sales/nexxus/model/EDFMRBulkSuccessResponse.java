package com.att.sales.nexxus.model;

import java.io.Serializable;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * The Class EDFMRBulkSuccessResponse.
 *
 * * @author(ar896d) 
 * 
 * The EDFMRBulkSuccessResponse class 
 *
 */
public class EDFMRBulkSuccessResponse implements Serializable {

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
	  private Integer status = null;

	  @JsonProperty("inventoryFiles")
	  private Inventoryfiles inventoryFiles = null;

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

	public Inventoryfiles getInventoryFiles() {
		return inventoryFiles;
	}

	public void setInventoryFiles(Inventoryfiles inventoryFiles) {
		this.inventoryFiles = inventoryFiles;
	}

}
