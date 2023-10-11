package com.att.sales.nexxus.reteriveicb.model;

import java.util.List;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.map.annotate.JsonSerialize;

import com.att.sales.nexxus.accesspricing.model.AccessPricingAQ;

/**
 * The Class Port.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class Port {

	private String designStatus;
	/** The type of inventory. */
	private String typeOfInventory;
	//private DesignDeterminants designDeterminants;
	/** The miles result. */
	//private Long portId;
	private List<MilesResult> milesResult;
	
	/** The security design details. */
	private List<Component> securityDesignDetails;
	
	/** The macd activity type. */
	private List<MacdActivityType> macdActivityType;
	
	/** The component. */
	private List<Component> component;
	
	/** The router details. */
	private List<Cpe> routerDetails;
	
	/** The logical channel detail. */
	private List<LogicalChannel> logicalChannelDetail;
	
	/** The custom design. */
	private CustomDesign customDesign;
	
	/** The access pricing AQ. */
	private AccessPricingAQ accessPricingAQ;
	
	/** The aggregate billing. */
	private List<AggregateBilling> aggregateBilling;

	/*private String serviceGuideEligibleIndicator;
	private Date serviceGuidePublishedDate;*/
	
	/** The port validation message. */
	private InterfaceorchMessage portValidationMessage;
	
	/** The voice feature detail. */
	private List<VoiceFeatureDetail> voiceFeatureDetail;
	
	/**
	 * Gets the port validation message.
	 *
	 * @return the port validation message
	 */
	public InterfaceorchMessage getPortValidationMessage() {
		return portValidationMessage;
	}
	
	/**
	 * Sets the port validation message.
	 *
	 * @param portValidationMessage the new port validation message
	 */
	public void setPortValidationMessage(InterfaceorchMessage portValidationMessage) {
		this.portValidationMessage = portValidationMessage;
	}
	
	/**
	 * Gets the type of inventory.
	 *
	 * @return the type of inventory
	 */
	public String getTypeOfInventory() {
		return typeOfInventory;
	}
	
	/**
	 * Sets the type of inventory.
	 *
	 * @param typeOfInventory the new type of inventory
	 */
	public void setTypeOfInventory(String typeOfInventory) {
		this.typeOfInventory = typeOfInventory;
	}
	
	/**
	 * Gets the miles result.
	 *
	 * @return the miles result
	 */
	public List<MilesResult> getMilesResult() {
		return milesResult;
	}
	
	/**
	 * Sets the miles result.
	 *
	 * @param milesResult the new miles result
	 */
	public void setMilesResult(List<MilesResult> milesResult) {
		this.milesResult = milesResult;
	}
	
	
	/**
	 * Gets the macd activity type.
	 *
	 * @return the macd activity type
	 */
	public List<MacdActivityType> getMacdActivityType() {
		return macdActivityType;
	}
	
	/**
	 * Sets the macd activity type.
	 *
	 * @param macdActivityType the new macd activity type
	 */
	public void setMacdActivityType(List<MacdActivityType> macdActivityType) {
		this.macdActivityType = macdActivityType;
	}
	
	/**
	 * Gets the component.
	 *
	 * @return the component
	 */
	/*public Long getPortId() {
		return portId;
	}
	public void setPortId(Long portId) {
		this.portId = portId;
	}*/
	public List<Component> getComponent() {
		return component;
	}
	
	/**
	 * Sets the component.
	 *
	 * @param component the new component
	 */
	public void setComponent(List<Component> component) {
		this.component = component;
	}
	
	/**
	 * Gets the router details.
	 *
	 * @return the router details
	 */
	public List<Cpe> getRouterDetails() {
		return routerDetails;
	}
	
	/**
	 * Sets the router details.
	 *
	 * @param routerDetails the new router details
	 */
	public void setRouterDetails(List<Cpe> routerDetails) {
		this.routerDetails = routerDetails;
	}

	/**
	 * Gets the logical channel detail.
	 *
	 * @return the logical channel detail
	 */
	public List<LogicalChannel> getLogicalChannelDetail() {
		return logicalChannelDetail;
	}
	
	/**
	 * Sets the logical channel detail.
	 *
	 * @param logicalChannelDetail the new logical channel detail
	 */
	public void setLogicalChannelDetail(List<LogicalChannel> logicalChannelDetail) {
		this.logicalChannelDetail = logicalChannelDetail;
	}
	
	/**
	 * Gets the custom design.
	 *
	 * @return the custom design
	 */
	public CustomDesign getCustomDesign() {
		return customDesign;
	}
	
	/**
	 * Sets the custom design.
	 *
	 * @param customDesign the new custom design
	 */
	public void setCustomDesign(CustomDesign customDesign) {
		this.customDesign = customDesign;
	}
	
	/**
	 * Gets the security design details.
	 *
	 * @return the security design details
	 */
	public List<Component> getSecurityDesignDetails() {
		return securityDesignDetails;
	}
	
	/**
	 * Sets the security design details.
	 *
	 * @param securityDesignDetails the new security design details
	 */
	public void setSecurityDesignDetails(List<Component> securityDesignDetails) {
		this.securityDesignDetails = securityDesignDetails;
	}
	
	/**
	 * Gets the access pricing AQ.
	 *
	 * @return the access pricing AQ
	 */
	public AccessPricingAQ getAccessPricingAQ() {
		return accessPricingAQ;
	}
	
	/**
	 * Sets the access pricing AQ.
	 *
	 * @param accessPricingAQ the new access pricing AQ
	 */
	public void setAccessPricingAQ(AccessPricingAQ accessPricingAQ) {
		this.accessPricingAQ = accessPricingAQ;
	}
	
	/**
	 * Gets the aggregate billing.
	 *
	 * @return the aggregate billing
	 */
	public List<AggregateBilling> getAggregateBilling() {
		return aggregateBilling;
	}
	
	/**
	 * Sets the aggregate billing.
	 *
	 * @param aggregateBilling the new aggregate billing
	 */
	public void setAggregateBilling(List<AggregateBilling> aggregateBilling) {
		this.aggregateBilling = aggregateBilling;
	}
	
	/**
	 * Gets the voice feature detail.
	 *
	 * @return the voice feature detail
	 */
	/*public String getServiceGuideEligibleIndicator() {
		return serviceGuideEligibleIndicator;
	}
	public void setServiceGuideEligibleIndicator(String serviceGuideEligibleIndicator) {
		this.serviceGuideEligibleIndicator = serviceGuideEligibleIndicator;
	}
	public Date getServiceGuidePublishedDate() {
		return serviceGuidePublishedDate;
	}
	public void setServiceGuidePublishedDate(Date serviceGuidePublishedDate) {
		this.serviceGuidePublishedDate = serviceGuidePublishedDate;
	}*/
	public List<VoiceFeatureDetail> getVoiceFeatureDetail() {
		return voiceFeatureDetail;
	}
	
	/**
	 * Sets the voice feature detail.
	 *
	 * @param voiceFeatureDetail the new voice feature detail
	 */
	public void setVoiceFeatureDetail(List<VoiceFeatureDetail> voiceFeatureDetail) {
		this.voiceFeatureDetail = voiceFeatureDetail;
	}

	public String getDesignStatus() {
		return designStatus;
	}

	public void setDesignStatus(String designStatus) {
		this.designStatus = designStatus;
	}
	
	
}
