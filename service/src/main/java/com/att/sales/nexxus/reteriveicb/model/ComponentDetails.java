package com.att.sales.nexxus.reteriveicb.model;

import java.util.List;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.map.annotate.JsonSerialize;


/**
 * The Class ComponentDetails.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class ComponentDetails {
	
	/** The component code id. */
	private Long componentCodeId;
	
	/** The component code type. */
	private String componentCodeType;
	
	/** The component id. */
	private Long componentId;
	
	/** The component type. */
	private String componentType;
	
	/** The component parent id. */
	private Long componentParentId;
	
	/** The component attributes. */
	private List<ComponentAttributes> componentAttributes;
	
	/** The price attributes. */
	private List<PriceAttributes> priceAttributes;
	
	/** The scpPriceMessages. */
	private List<SCPPriceMessages> scpPriceMessages;
	
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
	 * Gets the component type.
	 *
	 * @return the component type
	 */
	public String getComponentType() {
		return componentType;
	}
	
	/**
	 * Sets the component type.
	 *
	 * @param componentType the new component type
	 */
	public void setComponentType(String componentType) {
		this.componentType = componentType;
	}
	
	/**
	 * Gets the component parent id.
	 *
	 * @return the component parent id
	 */
	public Long getComponentParentId() {
		return componentParentId;
	}
	
	/**
	 * Sets the component parent id.
	 *
	 * @param componentParentId the new component parent id
	 */
	public void setComponentParentId(Long componentParentId) {
		this.componentParentId = componentParentId;
	}
	
	/**
	 * Gets the component attributes.
	 *
	 * @return the component attributes
	 */
	public List<ComponentAttributes> getComponentAttributes() {
		return componentAttributes;
	}
	
	/**
	 * Sets the component attributes.
	 *
	 * @param componentAttributes the new component attributes
	 */
	public void setComponentAttributes(List<ComponentAttributes> componentAttributes) {
		this.componentAttributes = componentAttributes;
	}
	
	/**
	 * Gets the price attributes.
	 *
	 * @return the price attributes
	 */
	public List<PriceAttributes> getPriceAttributes() {
		return priceAttributes;
	}
	
	/**
	 * Sets the price attributes.
	 *
	 * @param priceAttributes the new price attributes
	 */
	public void setPriceAttributes(List<PriceAttributes> priceAttributes) {
		this.priceAttributes = priceAttributes;
	}

	public List<SCPPriceMessages> getScpPriceMessages() {
		return scpPriceMessages;
	}

	public void setScpPriceMessages(List<SCPPriceMessages> scpPriceMessages) {
		this.scpPriceMessages = scpPriceMessages;
	}
	
	
}
