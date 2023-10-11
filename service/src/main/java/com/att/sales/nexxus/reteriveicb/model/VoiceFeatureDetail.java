package com.att.sales.nexxus.reteriveicb.model;

import java.util.List;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.map.annotate.JsonSerialize;


/**
 * The Class VoiceFeatureDetail.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class VoiceFeatureDetail {
	
	/** The component code id. */
	private Long componentCodeId;
	
	/** The component code type. */
	private String componentCodeType;
	
	/** The component id. */
	private Long componentId;
	
	/** The design details. */
	private List<UDFBaseData> designDetails;
	
	/**
	 * Gets the component code id.
	 *
	 * @return the component code id
	 */
	public Long getComponentCodeId() {
		return componentCodeId;
	}
	
	/**
	 * Sets the component code id.
	 *
	 * @param componentCodeId the new component code id
	 */
	public void setComponentCodeId(Long componentCodeId) {
		this.componentCodeId = componentCodeId;
	}
	
	/**
	 * Gets the component code type.
	 *
	 * @return the component code type
	 */
	public String getComponentCodeType() {
		return componentCodeType;
	}
	
	/**
	 * Sets the component code type.
	 *
	 * @param componentCodeType the new component code type
	 */
	public void setComponentCodeType(String componentCodeType) {
		this.componentCodeType = componentCodeType;
	}
	
	/**
	 * Gets the component id.
	 *
	 * @return the component id
	 */
	public Long getComponentId() {
		return componentId;
	}
	
	/**
	 * Sets the component id.
	 *
	 * @param componentId the new component id
	 */
	public void setComponentId(Long componentId) {
		this.componentId = componentId;
	}
	
	/**
	 * Gets the design details.
	 *
	 * @return the design details
	 */
	public List<UDFBaseData> getDesignDetails() {
		return designDetails;
	}
	
	/**
	 * Sets the design details.
	 *
	 * @param designDetails the new design details
	 */
	public void setDesignDetails(List<UDFBaseData> designDetails) {
		this.designDetails = designDetails;
	}
	
	

	
}
