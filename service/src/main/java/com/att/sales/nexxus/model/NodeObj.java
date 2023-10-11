package com.att.sales.nexxus.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;
import org.codehaus.jackson.map.annotate.JsonSerialize;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * The Class NodeObj.
 *
 * @author km017g
 */
@SuppressWarnings("serial")
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonSerialize(include=JsonSerialize.Inclusion.NON_NULL)
public class NodeObj implements Serializable{
	
	/** The node name. */
	private String nodeName;
	
	/** The clli. */
	private String clli;
	
	/** The address. */
	private String address;
	
	/** The signal type. */
	private Integer signalType;
	
	/** The contract term list. */
	private List<ContractTermList> contractTermList;
	
	/** The monthly cost local. */
	private BigDecimal monthlyCostLocal;
	
	/** The one time cost local. */
	private BigDecimal oneTimeCostLocal;
	
	/** The monthly cost USD. */
	private BigDecimal monthlyCostUSD;
	
	/** The one time cost USD. */
	private BigDecimal oneTimeCostUSD;
	
	/** The local currency. */
	private String localCurrency;
	
	/** The coverage indicator. */
	private String coverageIndicator;
	
	/** The supplier service. */
	private String supplierService;
	
	/** The technology. */
	private String technology;
	
	/** The serial number. */
	private String serialNumber;
	
	/** The expiration date. */
	private String expirationDate;
	
	/** The access interconnect. */
	private Integer accessInterconnect;
	
	/** The provider product name. */
	private String providerProductName;
	
	/** The provider product code. */
	private Integer providerProductCode;
	
	/** The access type. */
	private Integer accessType;
	
	/** The best price. */
	private String bestPrice;
	
	/** The quantity. */
	private String quantity;
	
	/** The quote qualification. */
	//Newly Added fields(sb808b, yp353m)
	private String quoteQualification;
	
	/** The architecture met. */
	private String architectureMet;
	
	/** The bandwidth met. */
	private String bandwidthMet;
	
	/** The vendor preference met. */
	private String vendorPreferenceMet;
	
	/** The vendor preference requested. */
	private String vendorPreferenceRequested;
	
	/** The rules derived message. */
	private String rulesDerivedMessage;
	
	/** The days until quote expires. */
	private String daysUntilQuoteExpires;
	
	/** The eth pseudo npanxx. */
	private String ethPseudoNpanxx;
	
	/** The clec lns sw clli. */
	private String clecLnsSwClli;
	//Newly Added fields(sb808b, yp353m)
	
	
	/**
	 * Gets the quantity.
	 *
	 * @return the quantity
	 */
	public String getQuantity() {
		return quantity;
	}
	
	/**
	 * Sets the quantity.
	 *
	 * @param quantity the new quantity
	 */
	public void setQuantity(String quantity) {
		this.quantity = quantity;
	}
	
	/**
	 * Gets the node name.
	 *
	 * @return the node name
	 */
	public String getNodeName() {
		return nodeName;
	}
	
	/**
	 * Gets the quote qualification.
	 *
	 * @return the quote qualification
	 */
	public String getQuoteQualification() {
		return quoteQualification;
	}
	
	/**
	 * Sets the quote qualification.
	 *
	 * @param quoteQualification the new quote qualification
	 */
	public void setQuoteQualification(String quoteQualification) {
		this.quoteQualification = quoteQualification;
	}
	
	/**
	 * Gets the architecture met.
	 *
	 * @return the architecture met
	 */
	public String getArchitectureMet() {
		return architectureMet;
	}
	
	/**
	 * Sets the architecture met.
	 *
	 * @param architectureMet the new architecture met
	 */
	public void setArchitectureMet(String architectureMet) {
		this.architectureMet = architectureMet;
	}
	
	/**
	 * Gets the bandwidth met.
	 *
	 * @return the bandwidth met
	 */
	public String getBandwidthMet() {
		return bandwidthMet;
	}
	
	/**
	 * Sets the bandwidth met.
	 *
	 * @param bandwidthMet the new bandwidth met
	 */
	public void setBandwidthMet(String bandwidthMet) {
		this.bandwidthMet = bandwidthMet;
	}
	
	/**
	 * Gets the vendor preference met.
	 *
	 * @return the vendor preference met
	 */
	public String getVendorPreferenceMet() {
		return vendorPreferenceMet;
	}
	
	/**
	 * Sets the vendor preference met.
	 *
	 * @param vendorPreferenceMet the new vendor preference met
	 */
	public void setVendorPreferenceMet(String vendorPreferenceMet) {
		this.vendorPreferenceMet = vendorPreferenceMet;
	}
	
	/**
	 * Gets the vendor preference requested.
	 *
	 * @return the vendor preference requested
	 */
	public String getVendorPreferenceRequested() {
		return vendorPreferenceRequested;
	}
	
	/**
	 * Sets the vendor preference requested.
	 *
	 * @param vendorPreferenceRequested the new vendor preference requested
	 */
	public void setVendorPreferenceRequested(String vendorPreferenceRequested) {
		this.vendorPreferenceRequested = vendorPreferenceRequested;
	}
	
	/**
	 * Gets the rules derived message.
	 *
	 * @return the rules derived message
	 */
	public String getRulesDerivedMessage() {
		return rulesDerivedMessage;
	}
	
	/**
	 * Sets the rules derived message.
	 *
	 * @param rulesDerivedMessage the new rules derived message
	 */
	public void setRulesDerivedMessage(String rulesDerivedMessage) {
		this.rulesDerivedMessage = rulesDerivedMessage;
	}
	
	/**
	 * Gets the days until quote expires.
	 *
	 * @return the days until quote expires
	 */
	public String getDaysUntilQuoteExpires() {
		return daysUntilQuoteExpires;
	}
	
	/**
	 * Sets the days until quote expires.
	 *
	 * @param daysUntilQuoteExpires the new days until quote expires
	 */
	public void setDaysUntilQuoteExpires(String daysUntilQuoteExpires) {
		this.daysUntilQuoteExpires = daysUntilQuoteExpires;
	}
	
	/**
	 * Gets the eth pseudo npanxx.
	 *
	 * @return the eth pseudo npanxx
	 */
	public String getEthPseudoNpanxx() {
		return ethPseudoNpanxx;
	}
	
	/**
	 * Sets the eth pseudo npanxx.
	 *
	 * @param ethPseudoNpanxx the new eth pseudo npanxx
	 */
	public void setEthPseudoNpanxx(String ethPseudoNpanxx) {
		this.ethPseudoNpanxx = ethPseudoNpanxx;
	}
	
	/**
	 * Gets the clec lns sw clli.
	 *
	 * @return the clec lns sw clli
	 */
	public String getClecLnsSwClli() {
		return clecLnsSwClli;
	}
	
	/**
	 * Sets the clec lns sw clli.
	 *
	 * @param clecLnsSwClli the new clec lns sw clli
	 */
	public void setClecLnsSwClli(String clecLnsSwClli) {
		this.clecLnsSwClli = clecLnsSwClli;
	}
	
	/**
	 * Sets the node name.
	 *
	 * @param nodeName the new node name
	 */
	public void setNodeName(String nodeName) {
		this.nodeName = nodeName;
	}
	
	/**
	 * Gets the clli.
	 *
	 * @return the clli
	 */
	public String getClli() {
		return clli;
	}
	
	/**
	 * Sets the clli.
	 *
	 * @param clli the new clli
	 */
	public void setClli(String clli) {
		this.clli = clli;
	}
	
	/**
	 * Gets the address.
	 *
	 * @return the address
	 */
	public String getAddress() {
		return address;
	}
	
	/**
	 * Sets the address.
	 *
	 * @param address the new address
	 */
	public void setAddress(String address) {
		this.address = address;
	}
	
	/**
	 * Gets the contract term list.
	 *
	 * @return the contract term list
	 */
	public List<ContractTermList> getContractTermList() {
		return contractTermList;
	}
	
	/**
	 * Sets the contract term list.
	 *
	 * @param contractTermList the new contract term list
	 */
	public void setContractTermList(List<ContractTermList> contractTermList) {
		this.contractTermList = contractTermList;
	}
	
	/**
	 * Gets the local currency.
	 *
	 * @return the local currency
	 */
	public String getLocalCurrency() {
		return localCurrency;
	}
	
	/**
	 * Sets the local currency.
	 *
	 * @param localCurrency the new local currency
	 */
	public void setLocalCurrency(String localCurrency) {
		this.localCurrency = localCurrency;
	}
	
	/**
	 * Gets the coverage indicator.
	 *
	 * @return the coverage indicator
	 */
	public String getCoverageIndicator() {
		return coverageIndicator;
	}
	
	/**
	 * Sets the coverage indicator.
	 *
	 * @param coverageIndicator the new coverage indicator
	 */
	public void setCoverageIndicator(String coverageIndicator) {
		this.coverageIndicator = coverageIndicator;
	}
	
	/**
	 * Gets the supplier service.
	 *
	 * @return the supplier service
	 */
	public String getSupplierService() {
		return supplierService;
	}
	
	/**
	 * Sets the supplier service.
	 *
	 * @param supplierService the new supplier service
	 */
	public void setSupplierService(String supplierService) {
		this.supplierService = supplierService;
	}
	
	/**
	 * Gets the technology.
	 *
	 * @return the technology
	 */
	public String getTechnology() {
		return technology;
	}
	
	/**
	 * Sets the technology.
	 *
	 * @param technology the new technology
	 */
	public void setTechnology(String technology) {
		this.technology = technology;
	}
	
	/**
	 * Gets the serial number.
	 *
	 * @return the serial number
	 */
	public String getSerialNumber() {
		return serialNumber;
	}
	
	/**
	 * Sets the serial number.
	 *
	 * @param serialNumber the new serial number
	 */
	public void setSerialNumber(String serialNumber) {
		this.serialNumber = serialNumber;
	}
	
	/**
	 * Gets the expiration date.
	 *
	 * @return the expiration date
	 */
	public String getExpirationDate() {
		return expirationDate;
	}
	
	/**
	 * Sets the expiration date.
	 *
	 * @param expirationDate the new expiration date
	 */
	public void setExpirationDate(String expirationDate) {
		this.expirationDate = expirationDate;
	}
	
	/**
	 * Gets the provider product name.
	 *
	 * @return the provider product name
	 */
	public String getProviderProductName() {
		return providerProductName;
	}
	
	/**
	 * Sets the provider product name.
	 *
	 * @param providerProductName the new provider product name
	 */
	public void setProviderProductName(String providerProductName) {
		this.providerProductName = providerProductName;
	}
	
	/**
	 * Gets the signal type.
	 *
	 * @return the signal type
	 */
	public Integer getSignalType() {
		return signalType;
	}
	
	/**
	 * Sets the signal type.
	 *
	 * @param signalType the new signal type
	 */
	public void setSignalType(Integer signalType) {
		this.signalType = signalType;
	}
	
	/**
	 * Gets the monthly cost local.
	 *
	 * @return the monthly cost local
	 */
	public BigDecimal getMonthlyCostLocal() {
		return monthlyCostLocal;
	}
	
	/**
	 * Sets the monthly cost local.
	 *
	 * @param monthlyCostLocal the new monthly cost local
	 */
	public void setMonthlyCostLocal(BigDecimal monthlyCostLocal) {
		this.monthlyCostLocal = monthlyCostLocal;
	}
	
	/**
	 * Gets the one time cost local.
	 *
	 * @return the one time cost local
	 */
	public BigDecimal getOneTimeCostLocal() {
		return oneTimeCostLocal;
	}
	
	/**
	 * Sets the one time cost local.
	 *
	 * @param oneTimeCostLocal the new one time cost local
	 */
	public void setOneTimeCostLocal(BigDecimal oneTimeCostLocal) {
		this.oneTimeCostLocal = oneTimeCostLocal;
	}
	
	/**
	 * Gets the monthly cost USD.
	 *
	 * @return the monthly cost USD
	 */
	public BigDecimal getMonthlyCostUSD() {
		return monthlyCostUSD;
	}
	
	/**
	 * Sets the monthly cost USD.
	 *
	 * @param monthlyCostUSD the new monthly cost USD
	 */
	public void setMonthlyCostUSD(BigDecimal monthlyCostUSD) {
		this.monthlyCostUSD = monthlyCostUSD;
	}
	
	/**
	 * Gets the one time cost USD.
	 *
	 * @return the one time cost USD
	 */
	public BigDecimal getOneTimeCostUSD() {
		return oneTimeCostUSD;
	}
	
	/**
	 * Sets the one time cost USD.
	 *
	 * @param oneTimeCostUSD the new one time cost USD
	 */
	public void setOneTimeCostUSD(BigDecimal oneTimeCostUSD) {
		this.oneTimeCostUSD = oneTimeCostUSD;
	}
	
	/**
	 * Gets the access interconnect.
	 *
	 * @return the access interconnect
	 */
	public Integer getAccessInterconnect() {
		return accessInterconnect;
	}
	
	/**
	 * Sets the access interconnect.
	 *
	 * @param accessInterconnect the new access interconnect
	 */
	public void setAccessInterconnect(Integer accessInterconnect) {
		this.accessInterconnect = accessInterconnect;
	}
	
	/**
	 * Gets the provider product code.
	 *
	 * @return the provider product code
	 */
	public Integer getProviderProductCode() {
		return providerProductCode;
	}
	
	/**
	 * Sets the provider product code.
	 *
	 * @param providerProductCode the new provider product code
	 */
	public void setProviderProductCode(Integer providerProductCode) {
		this.providerProductCode = providerProductCode;
	}
	
	/**
	 * Gets the access type.
	 *
	 * @return the access type
	 */
	public Integer getAccessType() {
		return accessType;
	}
	
	/**
	 * Sets the access type.
	 *
	 * @param accessType the new access type
	 */
	public void setAccessType(Integer accessType) {
		this.accessType = accessType;
	}
	
	/**
	 * Gets the best price.
	 *
	 * @return the best price
	 */
	public String getBestPrice() {
		return bestPrice;
	}
	
	/**
	 * Sets the best price.
	 *
	 * @param bestPrice the new best price
	 */
	public void setBestPrice(String bestPrice) {
		this.bestPrice = bestPrice;
	}
	
}
