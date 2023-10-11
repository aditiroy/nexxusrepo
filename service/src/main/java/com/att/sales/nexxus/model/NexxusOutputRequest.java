package com.att.sales.nexxus.model;

import java.util.List;

import org.codehaus.jackson.map.annotate.JsonSerialize;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * The Class NexxusOutputRequest.
 *
 * @author RudreshWaladaunki
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class NexxusOutputRequest {
	
		/** The request ids. */
	@JsonProperty("requestIds")
	private List<Long> requestIds;
	
	/** The nx solution id. */
	@JsonProperty("nxSolutionId")
	private Long nxSolutionId;
	
	/** The nx output file id. */
	@JsonProperty("nxOutputFileId")
	private Long nxOutputFileId;
	
	/** The little id. */
	@JsonProperty("littleId")
	private Long littleId;
	
	
	/** The nx output action. */
	@JsonProperty("nxOutputAction")
	private String nxOutputAction;
	
	

	@JsonProperty("fileName")
	private String fileName;
	
	@JsonProperty("requestId")
	private Long requestId;
	/**
	 * Gets the nx output action.
	 *
	 * @return the nx output action
	 */
	public String getNxOutputAction() {
		return nxOutputAction;
	}

	/**
	 * Sets the nx output action.
	 *
	 * @param nxOutputAction the new nx output action
	 */
	public void setNxOutputAction(String nxOutputAction) {
		this.nxOutputAction = nxOutputAction;
	}

	/**
	 * Gets the request ids.
	 *
	 * @return the request ids
	 */
	public List<Long> getRequestIds() {
		return requestIds;
	}
	
	/**
	 * Sets the request ids.
	 *
	 * @param requestIds the requestIds to set
	 */
	public void setRequestIds(List<Long> requestIds) {
		this.requestIds = requestIds;
	}

	/**
	 * Gets the nx solution id.
	 *
	 * @return the nxSolutionId
	 */
	public Long getNxSolutionId() {
		return nxSolutionId;
	}

	/**
	 * Sets the nx solution id.
	 *
	 * @param nxSolutionId the nxSolutionId to set
	 */
	public void setNxSolutionId(Long nxSolutionId) {
		this.nxSolutionId = nxSolutionId;
	}

	public Long getRequestId() {
		return requestId;
	}

	public void setRequestId(Long requestId) {
		this.requestId = requestId;
	}
	/**
	 * Gets the nx output file id.
	 *
	 * @return the nxOutputFileId
	 */
	public Long getNxOutputFileId() {
		return nxOutputFileId;
	}

	/**
	 * Sets the nx output file id.
	 *
	 * @param nxOutputFileId the nxOutputFileId to set
	 */
	public void setNxOutputFileId(Long nxOutputFileId) {
		this.nxOutputFileId = nxOutputFileId;
	}

	/**
	 * Gets the little id.
	 *
	 * @return the little id
	 */
	public Long getLittleId() {
		return littleId;
	}

	/**
	 * Sets the little id.
	 *
	 * @param littleId the new little id
	 */
	public void setLittleId(Long littleId) {
		this.littleId = littleId;
	}

	/**
	 * @return the fileName
	 */
	public String getFileName() {
		return fileName;
	}

	/**
	 * @param fileName the fileName to set
	 */
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	
}
