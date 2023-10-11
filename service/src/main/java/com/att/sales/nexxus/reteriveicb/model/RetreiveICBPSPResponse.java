package com.att.sales.nexxus.reteriveicb.model;

/*
 * @Author: Akash Arya
 * 
 * 
 */
import com.att.sales.framework.model.ServiceResponse;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * The Class RetreiveICBPSPResponse.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RetreiveICBPSPResponse  extends ServiceResponse{
    
	/** The nx request id. */
	private Long nxRequestId;
	
	/** The nx solution id. */
	private Long nxSolutionId;
	
	/** The site id. */
	private Long siteId;
	
	/** The asr item id. */
	private Long asrItemId;

	/** The nexxus message. */
	private NexxusMessage message;
	
	/**
	 * Gets the nx request id.
	 *
	 * @return the nx request id
	 */
	public Long getNxRequestId() {
		return nxRequestId;
	}

	/**
	 * Sets the nx request id.
	 *
	 * @param nxRequestId the new nx request id
	 */
	public void setNxRequestId(Long nxRequestId) {
		this.nxRequestId = nxRequestId;
	}

	/**
	 * Gets the site id.
	 *
	 * @return the site id
	 */
	public Long getSiteId() {
		return siteId;
	}

	/**
	 * Sets the site id.
	 *
	 * @param siteId the new site id
	 */
	public void setSiteId(Long siteId) {
		this.siteId = siteId;
	}

	/**
	 * Gets the asr item id.
	 *
	 * @return the asr item id
	 */
	public Long getAsrItemId() {
		return asrItemId;
	}

	/**
	 * Sets the asr item id.
	 *
	 * @param asrItemId the new asr item id
	 */
	public void setAsrItemId(Long asrItemId) {
		this.asrItemId = asrItemId;
	}

	/**
	 * @return the message
	 */
	public NexxusMessage getMessage() {
		return message;
	}

	/**
	 * @param message the message to set
	 */
	public void setMessage(NexxusMessage message) {
		this.message = message;
	}

	public Long getNxSolutionId() {
		return nxSolutionId;
	}

	public void setNxSolutionId(Long nxSolutionId) {
		this.nxSolutionId = nxSolutionId;
	}
}
