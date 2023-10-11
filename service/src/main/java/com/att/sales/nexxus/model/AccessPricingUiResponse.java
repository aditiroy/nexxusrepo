package com.att.sales.nexxus.model;

import java.io.Serializable;

import javax.persistence.Column;

import org.codehaus.jackson.map.annotate.JsonSerialize;

import com.att.sales.framework.validation.APIFieldProperty;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * The Class AccessPricingUiResponse.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class AccessPricingUiResponse implements Serializable {
	
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;
	
	/** The igloo quote id. */
	@APIFieldProperty(required=true, allowableValues="IGLOO Quote Id Type String")
	private String iglooQuoteId;
	
	/** The city. */
	@APIFieldProperty(required=true, allowableValues="City Type String")
	private String city;
	
	/** The state. */
	@APIFieldProperty(required=true, allowableValues="State Type String")
	private String state;
	
	/** The country. */
	@APIFieldProperty(required=true, allowableValues="Country Type String")
	private String country;
	
 	/** The cust addr 1. */
 	private String custAddr1;
	  
	private String reqCountry;
	
	private String reqState;
	
	private String reqCity;
	
 	private String reqStreetAddress;

 	private String reqZipCode;
 	
 	/** The cust city. */
 	private String custCity;
	 
	/** The cust state. */
 	private String custState;
	 
 	/** The cust country. */
 	private String custCountry;
	
 	/** The cust postalcode. */
 	private String custPostalcode;
	 
	/** The supplier name. */
 	private String supplierName; 

	/** The clli. */
	private String clli;
	
	/** The node name. */
	private String nodeName;
	
	/** The service. */
 	private String service;
	
 	/** The access bandwidth. */
 	private Integer accessBandwidth;
	
	/** The dq id. */
	@APIFieldProperty(required=true, allowableValues="Direct Quote Id Type String")
	private String dqId;
	
	/** The site id. */
	@APIFieldProperty(required=true, allowableValues="Site Id Type String")
	private String siteId;
	
	/** The mrc. */
	@APIFieldProperty(required=true, allowableValues="MRC Type Number")
	private String mrc;
	
	/** The nrc. */
	@APIFieldProperty(required=true, allowableValues="NRC Type Number")
	private String nrc;
	
	/** The ap id. */
	@APIFieldProperty(required=true, allowableValues="API Id Type Number")
	private Long apId;
	
	/** The location yn. */
	@APIFieldProperty(required=true, allowableValues="Existing or New Location Type String")
	private String locationYn;
	
	/** The include yn. */
	@APIFieldProperty(required=true, allowableValues="Indicates to include in Nexxus LP output when true. Type String")
	private String includeYn;
	
	private String currency;
	
	private String alternateCurrency;
	
	private String siteName;
	
	private String mpStatus;
	
	private String speed;

	private String zone;
	
	private String ethernetLcMonthlyRecurringCost; 
	private String ethernetLcPopMonthlyRecurringCost; 
	private String splConstructionCostMRC; 
	private String ethernetLcNonRecurringCost; 
	private String ethernetLcPopNonRecurringCost; 
	private String splConstructionCostNRC; 
	private String hasRequiredFields;

	
	public String getEthernetLcMonthlyRecurringCost() {
		return ethernetLcMonthlyRecurringCost;
	}

	public void setEthernetLcMonthlyRecurringCost(String ethernetLcMonthlyRecurringCost) {
		this.ethernetLcMonthlyRecurringCost = ethernetLcMonthlyRecurringCost;
	}

	public String getEthernetLcPopMonthlyRecurringCost() {
		return ethernetLcPopMonthlyRecurringCost;
	}

	public void setEthernetLcPopMonthlyRecurringCost(String ethernetLcPopMonthlyRecurringCost) {
		this.ethernetLcPopMonthlyRecurringCost = ethernetLcPopMonthlyRecurringCost;
	}

	public String getSplConstructionCostMRC() {
		return splConstructionCostMRC;
	}

	public void setSplConstructionCostMRC(String splConstructionCostMRC) {
		this.splConstructionCostMRC = splConstructionCostMRC;
	}

	public String getEthernetLcNonRecurringCost() {
		return ethernetLcNonRecurringCost;
	}

	public void setEthernetLcNonRecurringCost(String ethernetLcNonRecurringCost) {
		this.ethernetLcNonRecurringCost = ethernetLcNonRecurringCost;
	}

	public String getEthernetLcPopNonRecurringCost() {
		return ethernetLcPopNonRecurringCost;
	}

	public void setEthernetLcPopNonRecurringCost(String ethernetLcPopNonRecurringCost) {
		this.ethernetLcPopNonRecurringCost = ethernetLcPopNonRecurringCost;
	}

	public String getSplConstructionCostNRC() {
		return splConstructionCostNRC;
	}

	public void setSplConstructionCostNRC(String splConstructionCostNRC) {
		this.splConstructionCostNRC = splConstructionCostNRC;
	}

	public String getZone() {
		return zone;
	}

	public void setZone(String zone) {
		this.zone = zone;
	}
	
	/**
	 * Gets the igloo quote id.
	 *
	 * @return the resp
	 */
	
	public String getIglooQuoteId() {
		return iglooQuoteId;
	}

	/**
	 * Sets the igloo quote id.
	 *
	 * @param iglooQuoteId the new igloo quote id
	 */
	public void setIglooQuoteId(String iglooQuoteId) {
		this.iglooQuoteId = iglooQuoteId;
	}

	/**
	 * Gets the city.
	 *
	 * @return the city
	 */
	public String getCity() {
		return city;
	}

	/**
	 * Sets the city.
	 *
	 * @param city the new city
	 */
	public void setCity(String city) {
		this.city = city;
	}

	/**
	 * Gets the state.
	 *
	 * @return the state
	 */
	public String getState() {
		return state;
	}

	/**
	 * Sets the state.
	 *
	 * @param state the new state
	 */
	public void setState(String state) {
		this.state = state;
	}

	/**
	 * Gets the dq id.
	 *
	 * @return the dq id
	 */
	public String getDqId() {
		return dqId;
	}

	/**
	 * Sets the dq id.
	 *
	 * @param dqId the new dq id
	 */
	public void setDqId(String dqId) {
		this.dqId = dqId;
	}

	/**
	 * Gets the country.
	 *
	 * @return the country
	 */
	public String getCountry() {
		return country;
	}

	/**
	 * Sets the country.
	 *
	 * @param country the new country
	 */
	public void setCountry(String country) {
		this.country = country;
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
	 * Gets the mrc.
	 *
	 * @return the mrc
	 */
	public String getMrc() {
		return mrc;
	}

	/**
	 * Sets the mrc.
	 *
	 * @param mrc the new mrc
	 */
	public void setMrc(String mrc) {
		this.mrc = mrc;
	}

	/**
	 * Gets the nrc.
	 *
	 * @return the nrc
	 */
	public String getNrc() {
		return nrc;
	}

	/**
	 * Sets the nrc.
	 *
	 * @param nrc the new nrc
	 */
	public void setNrc(String nrc) {
		this.nrc = nrc;
	}

	/**
	 * Gets the ap id.
	 *
	 * @return the ap id
	 */
	public Long getApId() {
		return apId;
	}

	/**
	 * Sets the ap id.
	 *
	 * @param apId the new ap id
	 */
	public void setApId(Long apId) {
		this.apId = apId;
	}

	/**
	 * Gets the location yn.
	 *
	 * @return the location yn
	 */
	public String getLocationYn() {
		return locationYn;
	}

	/**
	 * Sets the location yn.
	 *
	 * @param locationYn the new location yn
	 */
	public void setLocationYn(String locationYn) {
		this.locationYn = locationYn;
	}

	/**
	 * Gets the include yn.
	 *
	 * @return the include yn
	 */
	public String getIncludeYn() {
		return includeYn;
	}

	/**
	 * Sets the include yn.
	 *
	 * @param includeYn the new include yn
	 */
	public void setIncludeYn(String includeYn) {
		this.includeYn = includeYn;
	}

	public String getCustAddr1() {
		return custAddr1;
	}

	public void setCustAddr1(String custAddr1) {
		this.custAddr1 = custAddr1;
	}

	public String getCustCity() {
		return custCity;
	}

	public void setCustCity(String custCity) {
		this.custCity = custCity;
	}

	public String getCustState() {
		return custState;
	}

	public void setCustState(String custState) {
		this.custState = custState;
	}

	public String getCustCountry() {
		return custCountry;
	}

	public void setCustCountry(String custCountry) {
		this.custCountry = custCountry;
	}

	public String getCustPostalcode() {
		return custPostalcode;
	}

	public void setCustPostalcode(String custPostalcode) {
		this.custPostalcode = custPostalcode;
	}

	public String getSupplierName() {
		return supplierName;
	}

	public void setSupplierName(String supplierName) {
		this.supplierName = supplierName;
	}

	public String getClli() {
		return clli;
	}

	public void setClli(String clli) {
		this.clli = clli;
	}

	public String getNodeName() {
		return nodeName;
	}

	public void setNodeName(String nodeName) {
		this.nodeName = nodeName;
	}

	public String getService() {
		return service;
	}

	public void setService(String service) {
		this.service = service;
	}

	public Integer getAccessBandwidth() {
		return accessBandwidth;
	}

	public void setAccessBandwidth(Integer accessBandwidth) {
		this.accessBandwidth = accessBandwidth;
	}

	public String getReqCountry() {
		return reqCountry;
	}

	public void setReqCountry(String reqCountry) {
		this.reqCountry = reqCountry;
	}

	public String getReqState() {
		return reqState;
	}

	public void setReqState(String reqState) {
		this.reqState = reqState;
	}

	public String getReqCity() {
		return reqCity;
	}

	public void setReqCity(String reqCity) {
		this.reqCity = reqCity;
	}

	public String getReqStreetAddress() {
		return reqStreetAddress;
	}

	public void setReqStreetAddress(String reqStreetAddress) {
		this.reqStreetAddress = reqStreetAddress;
	}

	public String getReqZipCode() {
		return reqZipCode;
	}

	public void setReqZipCode(String reqZipCode) {
		this.reqZipCode = reqZipCode;
	}

	public String getCurrency() {
		return currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}

	public String getSiteName() {
		return siteName;
	}

	public void setSiteName(String siteName) {
		this.siteName = siteName;
	}

	public String getAlternateCurrency() {
		return alternateCurrency;
	}

	public void setAlternateCurrency(String alternateCurrency) {
		this.alternateCurrency = alternateCurrency;
	}

	public String getMpStatus() {
		return mpStatus;
	}

	public void setMpStatus(String mpStatus) {
		this.mpStatus = mpStatus;
	}

	/**
	 * @return the speed
	 */
	public String getSpeed() {
		return speed;
	}

	/**
	 * @param speed the speed to set
	 */
	public void setSpeed(String speed) {
		this.speed = speed;
	}
	
	public String getHasRequiredFields() {
		return hasRequiredFields;
	}

	public void setHasRequiredFields(String hasRequiredFields) {
		this.hasRequiredFields = hasRequiredFields;
	}
	

	
}
