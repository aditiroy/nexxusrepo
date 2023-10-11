package com.att.sales.nexxus.reteriveicb.model;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
/*
 * @Author: Akash Arya
 * 
 * 
 */
import org.codehaus.jackson.map.annotate.JsonSerialize;


/**
 * The Class XchangeDetails.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class XchangeDetails {

	/** The xchange version id. */
	private Long xchangeVersionId;
	
	/** The xchange description. */
	private String xchangeDescription;
	
	/** The xchange version date. */
	private String xchangeVersionDate;
	
	/**
	 * Gets the xchange version id.
	 *
	 * @return the xchange version id
	 */
	public Long getXchangeVersionId() {
		return xchangeVersionId;
	}
	
	/**
	 * Sets the xchange version id.
	 *
	 * @param xchangeVersionId the new xchange version id
	 */
	public void setXchangeVersionId(Long xchangeVersionId) {
		this.xchangeVersionId = xchangeVersionId;
	}
	
	/**
	 * Gets the xchange description.
	 *
	 * @return the xchange description
	 */
	public String getXchangeDescription() {
		return xchangeDescription;
	}
	
	/**
	 * Sets the xchange description.
	 *
	 * @param xchangeDescription the new xchange description
	 */
	public void setXchangeDescription(String xchangeDescription) {
		this.xchangeDescription = xchangeDescription;
	}
	
	/**
	 * Gets the xchange version date.
	 *
	 * @return the xchange version date
	 */
	public String getXchangeVersionDate() {
		return xchangeVersionDate;
	}
	
	/**
	 * Sets the xchange version date.
	 *
	 * @param xchangeVersionDate the new xchange version date
	 */
	public void setXchangeVersionDate(String xchangeVersionDate) {
		this.xchangeVersionDate = xchangeVersionDate;
	}
}
