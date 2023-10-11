package com.att.sales.nexxus.model;

import java.util.List;

import org.codehaus.jackson.map.annotate.JsonSerialize;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;


/**
 * The Class AccessBandwidth.
 *
 * @author Yosadhara
 * 
 * Newly Added class (sb808b, yp353m)
 */
@JsonIgnoreProperties(ignoreUnknown = true) 
@JsonSerialize(include=JsonSerialize.Inclusion.NON_NULL)
public class AccessBandwidth {
	
	/** The error message. */
	private String errorMessage;
	
	/** The token ID. */
	private String tokenID;
	
	/** The availability. */
	private String availability;
	
	/** The ethernet IOC. */
	private String ethernetIOC;
	
	/** The service guide eligible indicator. */
	private String serviceGuideEligibleIndicator;
	
	/** The zone. */
	private String zone;
	
	/** The tier. */
	private String tier;
	
	/** The mileage type. */
	private String mileageType;
	
	/** The mileage group. */
	private String mileageGroup;
	
	/** The chargeable mileage. */
	private Double chargeableMileage;
	
	/** The swc to swc mileage. */
	private Double swcToSwcMileage;
	
	/** The discount percentage. */
	private Integer discountPercentage;
	
	/** The quote request date. */
	private String quoteRequestDate;
	
	/** The circuit quantity. */
	private String circuitQuantity;
	
	/** The access supplier list. */
	//Need to clarity to keep AccessSupplier inside  AccessBandwidth or in AccessQuotePricePromoResponse
	private List<AccessSupplier> accessSupplierList;
	
	/** The service guide published date. */
	private String serviceGuidePublishedDate;
	
	/** The customer avoided vendor. */
	private List<AvoidedVendorList> customerAvoidedVendor;
	
	/** The customer pref vendor. */
	private List<PreferredVendorList> customerPrefVendor; 
	
	/** The design details. */
	private List<DesignDetails> designDetails;

	/**
	 * Gets the error message.
	 *
	 * @return the error message
	 */
	public String getErrorMessage() {
		return errorMessage;
	}

	/**
	 * Sets the error message.
	 *
	 * @param errorMessage the new error message
	 */
	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}

	/**
	 * Gets the token ID.
	 *
	 * @return the token ID
	 */
	public String getTokenID() {
		return tokenID;
	}

	/**
	 * Sets the token ID.
	 *
	 * @param tokenID the new token ID
	 */
	public void setTokenID(String tokenID) {
		this.tokenID = tokenID;
	}

	/**
	 * Gets the availability.
	 *
	 * @return the availability
	 */
	public String getAvailability() {
		return availability;
	}

	/**
	 * Sets the availability.
	 *
	 * @param availability the new availability
	 */
	public void setAvailability(String availability) {
		this.availability = availability;
	}

	/**
	 * Gets the ethernet IOC.
	 *
	 * @return the ethernet IOC
	 */
	public String getEthernetIOC() {
		return ethernetIOC;
	}

	/**
	 * Sets the ethernet IOC.
	 *
	 * @param ethernetIOC the new ethernet IOC
	 */
	public void setEthernetIOC(String ethernetIOC) {
		this.ethernetIOC = ethernetIOC;
	}

	/**
	 * Gets the service guide eligible indicator.
	 *
	 * @return the service guide eligible indicator
	 */
	public String getServiceGuideEligibleIndicator() {
		return serviceGuideEligibleIndicator;
	}

	/**
	 * Sets the service guide eligible indicator.
	 *
	 * @param serviceGuideEligibleIndicator the new service guide eligible indicator
	 */
	public void setServiceGuideEligibleIndicator(String serviceGuideEligibleIndicator) {
		this.serviceGuideEligibleIndicator = serviceGuideEligibleIndicator;
	}

	/**
	 * Gets the zone.
	 *
	 * @return the zone
	 */
	public String getZone() {
		return zone;
	}

	/**
	 * Sets the zone.
	 *
	 * @param zone the new zone
	 */
	public void setZone(String zone) {
		this.zone = zone;
	}

	/**
	 * Gets the tier.
	 *
	 * @return the tier
	 */
	public String getTier() {
		return tier;
	}

	/**
	 * Sets the tier.
	 *
	 * @param tier the new tier
	 */
	public void setTier(String tier) {
		this.tier = tier;
	}

	/**
	 * Gets the mileage type.
	 *
	 * @return the mileage type
	 */
	public String getMileageType() {
		return mileageType;
	}

	/**
	 * Sets the mileage type.
	 *
	 * @param mileageType the new mileage type
	 */
	public void setMileageType(String mileageType) {
		this.mileageType = mileageType;
	}

	/**
	 * Gets the mileage group.
	 *
	 * @return the mileage group
	 */
	public String getMileageGroup() {
		return mileageGroup;
	}

	/**
	 * Sets the mileage group.
	 *
	 * @param mileageGroup the new mileage group
	 */
	public void setMileageGroup(String mileageGroup) {
		this.mileageGroup = mileageGroup;
	}

	/**
	 * Gets the chargeable mileage.
	 *
	 * @return the chargeable mileage
	 */
	public Double getChargeableMileage() {
		return chargeableMileage;
	}

	/**
	 * Sets the chargeable mileage.
	 *
	 * @param chargeableMileage the new chargeable mileage
	 */
	public void setChargeableMileage(Double chargeableMileage) {
		this.chargeableMileage = chargeableMileage;
	}

	/**
	 * Gets the swc to swc mileage.
	 *
	 * @return the swc to swc mileage
	 */
	public Double getSwcToSwcMileage() {
		return swcToSwcMileage;
	}

	/**
	 * Sets the swc to swc mileage.
	 *
	 * @param swcToSwcMileage the new swc to swc mileage
	 */
	public void setSwcToSwcMileage(Double swcToSwcMileage) {
		this.swcToSwcMileage = swcToSwcMileage;
	}

	/**
	 * Gets the discount percentage.
	 *
	 * @return the discount percentage
	 */
	public Integer getDiscountPercentage() {
		return discountPercentage;
	}

	/**
	 * Sets the discount percentage.
	 *
	 * @param discountPercentage the new discount percentage
	 */
	public void setDiscountPercentage(Integer discountPercentage) {
		this.discountPercentage = discountPercentage;
	}

	/**
	 * Gets the quote request date.
	 *
	 * @return the quote request date
	 */
	public String getQuoteRequestDate() {
		return quoteRequestDate;
	}

	/**
	 * Sets the quote request date.
	 *
	 * @param quoteRequestDate the new quote request date
	 */
	public void setQuoteRequestDate(String quoteRequestDate) {
		this.quoteRequestDate = quoteRequestDate;
	}

	/**
	 * Gets the circuit quantity.
	 *
	 * @return the circuit quantity
	 */
	public String getCircuitQuantity() {
		return circuitQuantity;
	}

	/**
	 * Sets the circuit quantity.
	 *
	 * @param circuitQuantity the new circuit quantity
	 */
	public void setCircuitQuantity(String circuitQuantity) {
		this.circuitQuantity = circuitQuantity;
	}

	/**
	 * Gets the access supplier list.
	 *
	 * @return the access supplier list
	 */
	public List<AccessSupplier> getAccessSupplierList() {
		return accessSupplierList;
	}

	/**
	 * Sets the access supplier list.
	 *
	 * @param accessSupplierList the new access supplier list
	 */
	public void setAccessSupplierList(List<AccessSupplier> accessSupplierList) {
		this.accessSupplierList = accessSupplierList;
	}

	/**
	 * Gets the service guide published date.
	 *
	 * @return the service guide published date
	 */
	public String getServiceGuidePublishedDate() {
		return serviceGuidePublishedDate;
	}

	/**
	 * Sets the service guide published date.
	 *
	 * @param serviceGuidePublishedDate the new service guide published date
	 */
	public void setServiceGuidePublishedDate(String serviceGuidePublishedDate) {
		this.serviceGuidePublishedDate = serviceGuidePublishedDate;
	}

	/**
	 * Gets the customer avoided vendor.
	 *
	 * @return the customer avoided vendor
	 */
	public List<AvoidedVendorList> getCustomerAvoidedVendor() {
		return customerAvoidedVendor;
	}

	/**
	 * Sets the customer avoided vendor.
	 *
	 * @param customerAvoidedVendor the new customer avoided vendor
	 */
	public void setCustomerAvoidedVendor(List<AvoidedVendorList> customerAvoidedVendor) {
		this.customerAvoidedVendor = customerAvoidedVendor;
	}

	/**
	 * Gets the customer pref vendor.
	 *
	 * @return the customer pref vendor
	 */
	public List<PreferredVendorList> getCustomerPrefVendor() {
		return customerPrefVendor;
	}

	/**
	 * Sets the customer pref vendor.
	 *
	 * @param customerPrefVendor the new customer pref vendor
	 */
	public void setCustomerPrefVendor(List<PreferredVendorList> customerPrefVendor) {
		this.customerPrefVendor = customerPrefVendor;
	}

	/**
	 * Gets the design details.
	 *
	 * @return the design details
	 */
	public List<DesignDetails> getDesignDetails() {
		return designDetails;
	}

	/**
	 * Sets the design details.
	 *
	 * @param designDetails the new design details
	 */
	public void setDesignDetails(List<DesignDetails> designDetails) {
		this.designDetails = designDetails;
	}

	/*public List<DesignDetails> getDesignDetail() {
		return designDetail;
	}

	public void setDesignDetail(List<DesignDetails> designDetail) {
		this.designDetail = designDetail;
	}*/
}
