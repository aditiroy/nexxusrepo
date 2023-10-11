package com.att.sales.nexxus.transmitdesigndata.model;

/**
 * The Class PedUdfMapper.
 */
public class PedToUdfMapper {
	
	private Long udfId;
	private Long offerId;
	private Long componentId;
	private String componentType;
	private String udfAttributeVal;
	private String type;
	public Long getUdfId() {
		return udfId;
	}
	public void setUdfId(Long udfId) {
		this.udfId = udfId;
	}
	public Long getOfferId() {
		return offerId;
	}
	public void setOfferId(Long offerId) {
		this.offerId = offerId;
	}
	public Long getComponentId() {
		return componentId;
	}
	public void setComponentId(Long componentId) {
		this.componentId = componentId;
	}
	public String getComponentType() {
		return componentType;
	}
	public void setComponentType(String componentType) {
		this.componentType = componentType;
	}
	public String getUdfAttributeVal() {
		return udfAttributeVal;
	}
	public void setUdfAttributeVal(String udfAttributeVal) {
		this.udfAttributeVal = udfAttributeVal;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	
	

}
