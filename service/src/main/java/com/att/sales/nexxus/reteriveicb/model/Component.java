package com.att.sales.nexxus.reteriveicb.model;

/*
 * @Author: Akash Arya
 * 
 * 
 */
import java.util.List;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.map.annotate.JsonSerialize;


/**
 * The Class Component.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class Component {
	
	/** The component code id. */
	private Long componentCodeId;
	
	/** The from inv YN. */
	private String fromInvYN;
	
	/** The logical channel pvc ID. */
	private Long logicalChannelPvcID;
	
	/** The ete vpn key. */
	private String eteVpnKey;
	
	/** The diversity group id. */
	private Long diversityGroupId;
	
	/** The component code type. */
	private String componentCodeType;
	
	/** The component id. */
	private Long componentId;
	
	/** The external field. */
	private Long externalField;
	
	/** The parent component id. */
	private Long parentComponentId;
	
	/** The external key ref. */
	private Long externalKeyRef;
	
	/** The mvl ind. */
	private String mvlInd;
	
	/** The user entered vpn. */
	private String userEnteredVpn;
	
	/** The site id. */
	private String siteId;
	
	/** The logical channel id. */
	private Long logicalChannelId;

	/** The design details. */
	private List<UDFBaseData> designDetails;
	
	/** The route targets. */
	private List<RouteTarget> routeTargets;
	
	/** The References */
	private List<References> references;
	
	/** The Site obj**/
	private Site siteObj;
	
	private String circuitType;
	
	private List<CustomAccessPriceList> customAccessPriceList;
	
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
	 * Gets the from inv YN.
	 *
	 * @return the from inv YN
	 */
	public String getFromInvYN() {
		return fromInvYN;
	}


	/**
	 * Sets the from inv YN.
	 *
	 * @param fromInvYN the new from inv YN
	 */
	public void setFromInvYN(String fromInvYN) {
		this.fromInvYN = fromInvYN;
	}


	/**
	 * Gets the logical channel pvc ID.
	 *
	 * @return the logical channel pvc ID
	 */
	public Long getLogicalChannelPvcID() {
		return logicalChannelPvcID;
	}


	/**
	 * Sets the logical channel pvc ID.
	 *
	 * @param logicalChannelPvcID the new logical channel pvc ID
	 */
	public void setLogicalChannelPvcID(Long logicalChannelPvcID) {
		this.logicalChannelPvcID = logicalChannelPvcID;
	}


	/**
	 * Gets the ete vpn key.
	 *
	 * @return the ete vpn key
	 */
	public String getEteVpnKey() {
		return eteVpnKey;
	}


	/**
	 * Sets the ete vpn key.
	 *
	 * @param eteVpnKey the new ete vpn key
	 */
	public void setEteVpnKey(String eteVpnKey) {
		this.eteVpnKey = eteVpnKey;
	}


	/**
	 * Gets the diversity group id.
	 *
	 * @return the diversity group id
	 */
	public Long getDiversityGroupId() {
		return diversityGroupId;
	}


	/**
	 * Sets the diversity group id.
	 *
	 * @param diversityGroupId the new diversity group id
	 */
	public void setDiversityGroupId(Long diversityGroupId) {
		this.diversityGroupId = diversityGroupId;
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
	 * Gets the external field.
	 *
	 * @return the external field
	 */
	public Long getExternalField() {
		return externalField;
	}


	/**
	 * Sets the external field.
	 *
	 * @param externalField the new external field
	 */
	public void setExternalField(Long externalField) {
		this.externalField = externalField;
	}


	/**
	 * Gets the parent component id.
	 *
	 * @return the parent component id
	 */
	public Long getParentComponentId() {
		return parentComponentId;
	}


	/**
	 * Sets the parent component id.
	 *
	 * @param parentComponentId the new parent component id
	 */
	public void setParentComponentId(Long parentComponentId) {
		this.parentComponentId = parentComponentId;
	}


	/**
	 * Gets the external key ref.
	 *
	 * @return the external key ref
	 */
	public Long getExternalKeyRef() {
		return externalKeyRef;
	}


	/**
	 * Sets the external key ref.
	 *
	 * @param externalKeyRef the new external key ref
	 */
	public void setExternalKeyRef(Long externalKeyRef) {
		this.externalKeyRef = externalKeyRef;
	}


	/**
	 * Gets the mvl ind.
	 *
	 * @return the mvl ind
	 */
	public String getMvlInd() {
		return mvlInd;
	}


	/**
	 * Sets the mvl ind.
	 *
	 * @param mvlInd the new mvl ind
	 */
	public void setMvlInd(String mvlInd) {
		this.mvlInd = mvlInd;
	}


	/**
	 * Gets the user entered vpn.
	 *
	 * @return the user entered vpn
	 */
	public String getUserEnteredVpn() {
		return userEnteredVpn;
	}


	/**
	 * Sets the user entered vpn.
	 *
	 * @param userEnteredVpn the new user entered vpn
	 */
	public void setUserEnteredVpn(String userEnteredVpn) {
		this.userEnteredVpn = userEnteredVpn;
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

	/**
	 * Gets the route targets.
	 *
	 * @return the route targets
	 */
	public List<RouteTarget> getRouteTargets() {
		return routeTargets;
	}


	/**
	 * Sets the route targets.
	 *
	 * @param routeTargets the new route targets
	 */
	public void setRouteTargets(List<RouteTarget> routeTargets) {
		this.routeTargets = routeTargets;
	}

	/**
	 * Gets the site id.
	 *
	 * @return the site id
	 */
	public String getSiteId() {
		return siteId;
	}


	/**
	 * Sets the site id.
	 *
	 * @param siteId the new site id
	 */
	public void setSiteId(String siteId) {
		this.siteId = siteId;
	}


	/**
	 * Gets the logical channel id.
	 *
	 * @return the logical channel id
	 */
	public Long getLogicalChannelId() {
		return logicalChannelId;
	}


	/**
	 * Sets the logical channel id.
	 *
	 * @param logicalChannelId the new logical channel id
	 */
	public void setLogicalChannelId(Long logicalChannelId) {
		this.logicalChannelId = logicalChannelId;
	}


	public List<References> getReferences() {
		return references;
	}


	public void setReferences(List<References> references) {
		this.references = references;
	}


	public Site getSiteObj() {
		return siteObj;
	}


	public void setSiteObj(Site siteObj) {
		this.siteObj = siteObj;
	}
	
	public String getCircuitType() {
		return circuitType;
	}


	public void setCircuitType(String circuitType) {
		this.circuitType = circuitType;
	}


	public List<CustomAccessPriceList> getCustomAccessPriceList() {
		return customAccessPriceList;
	}


	public void setCustomAccessPriceList(List<CustomAccessPriceList> customAccessPriceList) {
		this.customAccessPriceList = customAccessPriceList;
	}


	
	
}
