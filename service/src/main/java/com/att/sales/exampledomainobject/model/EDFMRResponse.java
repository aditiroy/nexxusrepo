package com.att.sales.exampledomainobject.model;
import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Response for EDF DMaap Message Router.
 */
public class EDFMRResponse implements Serializable {
	
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;

	/** The user id. */
	@JsonProperty("User_id")
	private String userId;
	
	/** The application. */
	@JsonProperty("Application")
	private String application;
	
	/** The pgm. */
	@JsonProperty("PGM")
	private String pgm;
	
	/** The request id. */
	@JsonProperty("Request_id")
	private String requestId;
	
	/** The outputfile name. */
	@JsonProperty("Outputfile_name")
	private String outputfileName;
	
	/** The start run time. */
	@JsonProperty("Start_run_time")
	private String startRunTime;
	
	/** The end run time. */
	@JsonProperty("End_run_time")
	private String endRunTime;
	
	/** The message. */
	@JsonProperty("Message")
	private String message;

	/**
	 * Gets the user id.
	 *
	 * @return the user id
	 */
	public String getUserId() {
		return userId;
	}

	/**
	 * Sets the user id.
	 *
	 * @param userId the new user id
	 */
	public void setUserId(String userId) {
		this.userId = userId;
	}

	/**
	 * Gets the application.
	 *
	 * @return the application
	 */
	public String getApplication() {
		return application;
	}

	/**
	 * Sets the application.
	 *
	 * @param application the new application
	 */
	public void setApplication(String application) {
		this.application = application;
	}

	/**
	 * Gets the pgm.
	 *
	 * @return the pgm
	 */
	public String getPgm() {
		return pgm;
	}

	/**
	 * Sets the pgm.
	 *
	 * @param pgm the new pgm
	 */
	public void setPgm(String pgm) {
		this.pgm = pgm;
	}

	/**
	 * Gets the request id.
	 *
	 * @return the request id
	 */
	public String getRequestId() {
		return requestId;
	}

	/**
	 * Sets the request id.
	 *
	 * @param requestId the new request id
	 */
	public void setRequestId(String requestId) {
		this.requestId = requestId;
	}

	/**
	 * Gets the outputfile name.
	 *
	 * @return the outputfile name
	 */
	public String getOutputfileName() {
		return outputfileName;
	}

	/**
	 * Sets the outputfile name.
	 *
	 * @param outputfileName the new outputfile name
	 */
	public void setOutputfileName(String outputfileName) {
		this.outputfileName = outputfileName;
	}

	/**
	 * Gets the start run time.
	 *
	 * @return the start run time
	 */
	public String getStartRunTime() {
		return startRunTime;
	}

	/**
	 * Sets the start run time.
	 *
	 * @param startRunTime the new start run time
	 */
	public void setStartRunTime(String startRunTime) {
		this.startRunTime = startRunTime;
	}

	/**
	 * Gets the end run time.
	 *
	 * @return the end run time
	 */
	public String getEndRunTime() {
		return endRunTime;
	}

	/**
	 * Sets the end run time.
	 *
	 * @param endRunTime the new end run time
	 */
	public void setEndRunTime(String endRunTime) {
		this.endRunTime = endRunTime;
	}

	/**
	 * Gets the message.
	 *
	 * @return the message
	 */
	public String getMessage() {
		return message;
	}

	/**
	 * Sets the message.
	 *
	 * @param message the new message
	 */
	public void setMessage(String message) {
		this.message = message;
	}
}
