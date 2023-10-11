package com.att.sales.nexxus.pricing.model;

import java.util.List;

import org.codehaus.jackson.map.annotate.JsonSerialize;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class DesignDetails {
	private String product;
	private Long quantity;
	private String nssEngagementIndicator;
	private String multiGigeIndicator;
	private List<SiteDetails> siteDetails;
	private String alias;
	private String facilityType;
	private String assetInvestmentSheetIndicator;
	private Long imsProductNumber;
	private String macdType;
	private List<String> macdActivity;
	private String circuitId;
	private String jurisdiction;
	private String interWirecenterDiversityIndicator;
	private String diverseFromASRItemId;
	private String diverseFromCircuitId;
	private String specialRouting;
	private String networkChannelCode;
	private Long percentInterlataUsage;
	private String commonLanguageCircuitId;
	private String certification;
	private String certificationReason;
	private String customerNetworkManagementInd;
	private Long committedInformationRate;
	private Double annualRevenue;
	private Double monthlyRevenue;
	private String dateSoldToCustomer;
	private Double specialConstructionCharge;
	private String specialConstructionHandling;
	private String tspInd;
	private String tspAuthorizationCode;
	private String comments;
	private String icsc;
	private String accessCarrierNameAbbreviation;
	private String classOfServiceType;
	private String classOfServiceLevelSchedulingType;
	private String purchaseOrderNumber;
	private String enniFlag;
	private String asrItemId;
	private String result;
	private List<ValidationIssues> validationIssues;

	public String getProduct() {
		return product;
	}

	public void setProduct(String product) {
		this.product = product;
	}

	public Long getQuantity() {
		return quantity;
	}

	public void setQuantity(Long quantity) {
		this.quantity = quantity;
	}

	public String getNssEngagementIndicator() {
		return nssEngagementIndicator;
	}

	public void setNssEngagementIndicator(String nssEngagementIndicator) {
		this.nssEngagementIndicator = nssEngagementIndicator;
	}

	public String getMultiGigeIndicator() {
		return multiGigeIndicator;
	}

	public void setMultiGigeIndicator(String multiGigeIndicator) {
		this.multiGigeIndicator = multiGigeIndicator;
	}

	public List<SiteDetails> getSiteDetails() {
		return siteDetails;
	}

	public void setSiteDetails(List<SiteDetails> siteDetails) {
		this.siteDetails = siteDetails;
	}

	public String getAlias() {
		return alias;
	}

	public void setAlias(String alias) {
		this.alias = alias;
	}

	public String getFacilityType() {
		return facilityType;
	}

	public void setFacilityType(String facilityType) {
		this.facilityType = facilityType;
	}

	public String getAssetInvestmentSheetIndicator() {
		return assetInvestmentSheetIndicator;
	}

	public void setAssetInvestmentSheetIndicator(String assetInvestmentSheetIndicator) {
		this.assetInvestmentSheetIndicator = assetInvestmentSheetIndicator;
	}

	public Long getImsProductNumber() {
		return imsProductNumber;
	}

	public void setImsProductNumber(Long imsProductNumber) {
		this.imsProductNumber = imsProductNumber;
	}

	public String getMacdType() {
		return macdType;
	}

	public void setMacdType(String macdType) {
		this.macdType = macdType;
	}

	public List<String> getMacdActivity() {
		return macdActivity;
	}

	public void setMacdActivity(List<String> macdActivity) {
		this.macdActivity = macdActivity;
	}

	public String getCircuitId() {
		return circuitId;
	}

	public void setCircuitId(String circuitId) {
		this.circuitId = circuitId;
	}

	public String getJurisdiction() {
		return jurisdiction;
	}

	public void setJurisdiction(String jurisdiction) {
		this.jurisdiction = jurisdiction;
	}

	public String getInterWirecenterDiversityIndicator() {
		return interWirecenterDiversityIndicator;
	}

	public void setInterWirecenterDiversityIndicator(String interWirecenterDiversityIndicator) {
		this.interWirecenterDiversityIndicator = interWirecenterDiversityIndicator;
	}

	public String getDiverseFromASRItemId() {
		return diverseFromASRItemId;
	}

	public void setDiverseFromASRItemId(String diverseFromASRItemId) {
		this.diverseFromASRItemId = diverseFromASRItemId;
	}

	public String getDiverseFromCircuitId() {
		return diverseFromCircuitId;
	}

	public void setDiverseFromCircuitId(String diverseFromCircuitId) {
		this.diverseFromCircuitId = diverseFromCircuitId;
	}

	public String getSpecialRouting() {
		return specialRouting;
	}

	public void setSpecialRouting(String specialRouting) {
		this.specialRouting = specialRouting;
	}

	public String getNetworkChannelCode() {
		return networkChannelCode;
	}

	public void setNetworkChannelCode(String networkChannelCode) {
		this.networkChannelCode = networkChannelCode;
	}

	public Long getPercentInterlataUsage() {
		return percentInterlataUsage;
	}

	public void setPercentInterlataUsage(Long percentInterlataUsage) {
		this.percentInterlataUsage = percentInterlataUsage;
	}

	public String getCommonLanguageCircuitId() {
		return commonLanguageCircuitId;
	}

	public void setCommonLanguageCircuitId(String commonLanguageCircuitId) {
		this.commonLanguageCircuitId = commonLanguageCircuitId;
	}

	public String getCertification() {
		return certification;
	}

	public void setCertification(String certification) {
		this.certification = certification;
	}

	public String getCertificationReason() {
		return certificationReason;
	}

	public void setCertificationReason(String certificationReason) {
		this.certificationReason = certificationReason;
	}

	public String getCustomerNetworkManagementInd() {
		return customerNetworkManagementInd;
	}

	public void setCustomerNetworkManagementInd(String customerNetworkManagementInd) {
		this.customerNetworkManagementInd = customerNetworkManagementInd;
	}

	public Long getCommittedInformationRate() {
		return committedInformationRate;
	}

	public void setCommittedInformationRate(Long committedInformationRate) {
		this.committedInformationRate = committedInformationRate;
	}

	public Double getAnnualRevenue() {
		return annualRevenue;
	}

	public void setAnnualRevenue(Double annualRevenue) {
		this.annualRevenue = annualRevenue;
	}

	public Double getMonthlyRevenue() {
		return monthlyRevenue;
	}

	public void setMonthlyRevenue(Double monthlyRevenue) {
		this.monthlyRevenue = monthlyRevenue;
	}

	public String getDateSoldToCustomer() {
		return dateSoldToCustomer;
	}

	public void setDateSoldToCustomer(String dateSoldToCustomer) {
		this.dateSoldToCustomer = dateSoldToCustomer;
	}

	public Double getSpecialConstructionCharge() {
		return specialConstructionCharge;
	}

	public void setSpecialConstructionCharge(Double specialConstructionCharge) {
		this.specialConstructionCharge = specialConstructionCharge;
	}

	public String getSpecialConstructionHandling() {
		return specialConstructionHandling;
	}

	public void setSpecialConstructionHandling(String specialConstructionHandling) {
		this.specialConstructionHandling = specialConstructionHandling;
	}

	public String getTspInd() {
		return tspInd;
	}

	public void setTspInd(String tspInd) {
		this.tspInd = tspInd;
	}

	public String getTspAuthorizationCode() {
		return tspAuthorizationCode;
	}

	public void setTspAuthorizationCode(String tspAuthorizationCode) {
		this.tspAuthorizationCode = tspAuthorizationCode;
	}

	public String getComments() {
		return comments;
	}

	public void setComments(String comments) {
		this.comments = comments;
	}

	public String getIcsc() {
		return icsc;
	}

	public void setIcsc(String icsc) {
		this.icsc = icsc;
	}

	public String getAccessCarrierNameAbbreviation() {
		return accessCarrierNameAbbreviation;
	}

	public void setAccessCarrierNameAbbreviation(String accessCarrierNameAbbreviation) {
		this.accessCarrierNameAbbreviation = accessCarrierNameAbbreviation;
	}

	public String getClassOfServiceType() {
		return classOfServiceType;
	}

	public void setClassOfServiceType(String classOfServiceType) {
		this.classOfServiceType = classOfServiceType;
	}

	public String getClassOfServiceLevelSchedulingType() {
		return classOfServiceLevelSchedulingType;
	}

	public void setClassOfServiceLevelSchedulingType(String classOfServiceLevelSchedulingType) {
		this.classOfServiceLevelSchedulingType = classOfServiceLevelSchedulingType;
	}

	public String getPurchaseOrderNumber() {
		return purchaseOrderNumber;
	}

	public void setPurchaseOrderNumber(String purchaseOrderNumber) {
		this.purchaseOrderNumber = purchaseOrderNumber;
	}

	public String getEnniFlag() {
		return enniFlag;
	}

	public void setEnniFlag(String enniFlag) {
		this.enniFlag = enniFlag;
	}

	public String getAsrItemId() {
		return asrItemId;
	}

	public void setAsrItemId(String asrItemId) {
		this.asrItemId = asrItemId;
	}

	public String getResult() {
		return result;
	}

	public void setResult(String result) {
		this.result = result;
	}

	public List<ValidationIssues> getValidationIssues() {
		return validationIssues;
	}

	public void setValidationIssues(List<ValidationIssues> validationIssues) {
		this.validationIssues = validationIssues;
	}

}
