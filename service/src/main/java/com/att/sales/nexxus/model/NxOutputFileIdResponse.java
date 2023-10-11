package com.att.sales.nexxus.model;

import java.io.Serializable;

import org.codehaus.jackson.map.annotate.JsonSerialize;

import com.att.sales.framework.model.ServiceResponse;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * The Class NxOutputFileIdResponse.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class NxOutputFileIdResponse extends ServiceResponse implements Serializable{
	
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;
	
	/** The nx output file id. */
	@JsonProperty("nxOutputFileId")
	private Long nxOutputFileId;
	
	/** The nx output file name. */
	@JsonProperty("nxOutputFileName")
	private String nxOutputFileName;

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
	 * Gets the nx output file name.
	 *
	 * @return the nx output file name
	 */
	public String getNxOutputFileName() {
		return nxOutputFileName;
	}

	/**
	 * Sets the nx output file name.
	 *
	 * @param nxOutputFileName the new nx output file name
	 */
	public void setNxOutputFileName(String nxOutputFileName) {
		this.nxOutputFileName = nxOutputFileName;
	}
	
	
	
}
