package com.att.sales.nexxus.pricing.model;

import java.util.List;

import org.codehaus.jackson.map.annotate.JsonSerialize;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class SiteDetails {
	private String siteIdentifier;
	private String clientSiteId;
	private String alias;
	private String clli;
	private Address address;
	private String lata;
	private String certification;
	private String portConnectionType;
	private String channelizedIndicator;
	private String portInterfaceType;
	private String serviceLocationType;
	private String customerConfigType;
	private String regionCode;
	private String franchiseCode;
	private String protectionOptions;
	private String edgelessDesignIndicator;
	private String independentCarrierCompanyLATA;
	private String independentCarrierCompanyName;
	private String diversityOptions;
	private String standaloneDiversityIndicator;
	private String newBuildingIndicator;
	private String secondEntranceRequiredIndicator;
	private String networkChannelInterfaceCode;
	private String servingWirecenter;
	private String alternateServingWirecenter;
	private String interDepartmentMeetPointChecklistURL;
	private String diverseFromReferenceInfo;
	private String opticalTerminatingNetworkCarrierFacilityAssignment;
	private String collocationCarrierFacilityAssignment;
	private String npanxx;
	private Double loopLength;
	private Long numberRepeaters;
	private Long numberCopperPairs;
	private Long numberRemoteTerminals;
	private String ta5KClli;
	private String addDropInd;
	private Contact localContactDetails;
	private Contact alternateLocalContactDetails;
	private Contact buildingContactDetails;
	private String entranceFacilityOffer;
	private String carrierFacilityAssignment;
	private String actl;
	private String noShareInd;
	private String endCustomerName;
	private List<String> macdSubActivity;
	private String userProvidedPowerSupplyInd;
	private String comments;

	public String getSiteIdentifier() {
		return siteIdentifier;
	}

	public void setSiteIdentifier(String siteIdentifier) {
		this.siteIdentifier = siteIdentifier;
	}

	public String getClientSiteId() {
		return clientSiteId;
	}

	public void setClientSiteId(String clientSiteId) {
		this.clientSiteId = clientSiteId;
	}

	public String getAlias() {
		return alias;
	}

	public void setAlias(String alias) {
		this.alias = alias;
	}

	public String getClli() {
		return clli;
	}

	public void setClli(String clli) {
		this.clli = clli;
	}

	public Address getAddress() {
		return address;
	}

	public void setAddress(Address address) {
		this.address = address;
	}

	public String getLata() {
		return lata;
	}

	public void setLata(String lata) {
		this.lata = lata;
	}

	public String getCertification() {
		return certification;
	}

	public void setCertification(String certification) {
		this.certification = certification;
	}

	public String getPortConnectionType() {
		return portConnectionType;
	}

	public void setPortConnectionType(String portConnectionType) {
		this.portConnectionType = portConnectionType;
	}

	public String getChannelizedIndicator() {
		return channelizedIndicator;
	}

	public void setChannelizedIndicator(String channelizedIndicator) {
		this.channelizedIndicator = channelizedIndicator;
	}

	public String getPortInterfaceType() {
		return portInterfaceType;
	}

	public void setPortInterfaceType(String portInterfaceType) {
		this.portInterfaceType = portInterfaceType;
	}

	public String getServiceLocationType() {
		return serviceLocationType;
	}

	public void setServiceLocationType(String serviceLocationType) {
		this.serviceLocationType = serviceLocationType;
	}

	public String getCustomerConfigType() {
		return customerConfigType;
	}

	public void setCustomerConfigType(String customerConfigType) {
		this.customerConfigType = customerConfigType;
	}

	public String getRegionCode() {
		return regionCode;
	}

	public void setRegionCode(String regionCode) {
		this.regionCode = regionCode;
	}

	public String getFranchiseCode() {
		return franchiseCode;
	}

	public void setFranchiseCode(String franchiseCode) {
		this.franchiseCode = franchiseCode;
	}

	public String getProtectionOptions() {
		return protectionOptions;
	}

	public void setProtectionOptions(String protectionOptions) {
		this.protectionOptions = protectionOptions;
	}

	public String getEdgelessDesignIndicator() {
		return edgelessDesignIndicator;
	}

	public void setEdgelessDesignIndicator(String edgelessDesignIndicator) {
		this.edgelessDesignIndicator = edgelessDesignIndicator;
	}

	public String getIndependentCarrierCompanyLATA() {
		return independentCarrierCompanyLATA;
	}

	public void setIndependentCarrierCompanyLATA(String independentCarrierCompanyLATA) {
		this.independentCarrierCompanyLATA = independentCarrierCompanyLATA;
	}

	public String getIndependentCarrierCompanyName() {
		return independentCarrierCompanyName;
	}

	public void setIndependentCarrierCompanyName(String independentCarrierCompanyName) {
		this.independentCarrierCompanyName = independentCarrierCompanyName;
	}

	public String getDiversityOptions() {
		return diversityOptions;
	}

	public void setDiversityOptions(String diversityOptions) {
		this.diversityOptions = diversityOptions;
	}

	public String getStandaloneDiversityIndicator() {
		return standaloneDiversityIndicator;
	}

	public void setStandaloneDiversityIndicator(String standaloneDiversityIndicator) {
		this.standaloneDiversityIndicator = standaloneDiversityIndicator;
	}

	public String getNewBuildingIndicator() {
		return newBuildingIndicator;
	}

	public void setNewBuildingIndicator(String newBuildingIndicator) {
		this.newBuildingIndicator = newBuildingIndicator;
	}

	public String getSecondEntranceRequiredIndicator() {
		return secondEntranceRequiredIndicator;
	}

	public void setSecondEntranceRequiredIndicator(String secondEntranceRequiredIndicator) {
		this.secondEntranceRequiredIndicator = secondEntranceRequiredIndicator;
	}

	public String getNetworkChannelInterfaceCode() {
		return networkChannelInterfaceCode;
	}

	public void setNetworkChannelInterfaceCode(String networkChannelInterfaceCode) {
		this.networkChannelInterfaceCode = networkChannelInterfaceCode;
	}

	public String getServingWirecenter() {
		return servingWirecenter;
	}

	public void setServingWirecenter(String servingWirecenter) {
		this.servingWirecenter = servingWirecenter;
	}

	public String getAlternateServingWirecenter() {
		return alternateServingWirecenter;
	}

	public void setAlternateServingWirecenter(String alternateServingWirecenter) {
		this.alternateServingWirecenter = alternateServingWirecenter;
	}

	public String getInterDepartmentMeetPointChecklistURL() {
		return interDepartmentMeetPointChecklistURL;
	}

	public void setInterDepartmentMeetPointChecklistURL(String interDepartmentMeetPointChecklistURL) {
		this.interDepartmentMeetPointChecklistURL = interDepartmentMeetPointChecklistURL;
	}

	public String getDiverseFromReferenceInfo() {
		return diverseFromReferenceInfo;
	}

	public void setDiverseFromReferenceInfo(String diverseFromReferenceInfo) {
		this.diverseFromReferenceInfo = diverseFromReferenceInfo;
	}

	public String getOpticalTerminatingNetworkCarrierFacilityAssignment() {
		return opticalTerminatingNetworkCarrierFacilityAssignment;
	}

	public void setOpticalTerminatingNetworkCarrierFacilityAssignment(
			String opticalTerminatingNetworkCarrierFacilityAssignment) {
		this.opticalTerminatingNetworkCarrierFacilityAssignment = opticalTerminatingNetworkCarrierFacilityAssignment;
	}

	public String getCollocationCarrierFacilityAssignment() {
		return collocationCarrierFacilityAssignment;
	}

	public void setCollocationCarrierFacilityAssignment(String collocationCarrierFacilityAssignment) {
		this.collocationCarrierFacilityAssignment = collocationCarrierFacilityAssignment;
	}

	public String getNpanxx() {
		return npanxx;
	}

	public void setNpanxx(String npanxx) {
		this.npanxx = npanxx;
	}

	public Double getLoopLength() {
		return loopLength;
	}

	public void setLoopLength(Double loopLength) {
		this.loopLength = loopLength;
	}

	public Long getNumberRepeaters() {
		return numberRepeaters;
	}

	public void setNumberRepeaters(Long numberRepeaters) {
		this.numberRepeaters = numberRepeaters;
	}

	public Long getNumberCopperPairs() {
		return numberCopperPairs;
	}

	public void setNumberCopperPairs(Long numberCopperPairs) {
		this.numberCopperPairs = numberCopperPairs;
	}

	public Long getNumberRemoteTerminals() {
		return numberRemoteTerminals;
	}

	public void setNumberRemoteTerminals(Long numberRemoteTerminals) {
		this.numberRemoteTerminals = numberRemoteTerminals;
	}

	public String getTa5KClli() {
		return ta5KClli;
	}

	public void setTa5KClli(String ta5kClli) {
		ta5KClli = ta5kClli;
	}

	public String getAddDropInd() {
		return addDropInd;
	}

	public void setAddDropInd(String addDropInd) {
		this.addDropInd = addDropInd;
	}

	public Contact getLocalContactDetails() {
		return localContactDetails;
	}

	public void setLocalContactDetails(Contact localContactDetails) {
		this.localContactDetails = localContactDetails;
	}

	public Contact getAlternateLocalContactDetails() {
		return alternateLocalContactDetails;
	}

	public void setAlternateLocalContactDetails(Contact alternateLocalContactDetails) {
		this.alternateLocalContactDetails = alternateLocalContactDetails;
	}

	public Contact getBuildingContactDetails() {
		return buildingContactDetails;
	}

	public void setBuildingContactDetails(Contact buildingContactDetails) {
		this.buildingContactDetails = buildingContactDetails;
	}

	public String getEntranceFacilityOffer() {
		return entranceFacilityOffer;
	}

	public void setEntranceFacilityOffer(String entranceFacilityOffer) {
		this.entranceFacilityOffer = entranceFacilityOffer;
	}

	public String getCarrierFacilityAssignment() {
		return carrierFacilityAssignment;
	}

	public void setCarrierFacilityAssignment(String carrierFacilityAssignment) {
		this.carrierFacilityAssignment = carrierFacilityAssignment;
	}

	public String getActl() {
		return actl;
	}

	public void setActl(String actl) {
		this.actl = actl;
	}

	public String getNoShareInd() {
		return noShareInd;
	}

	public void setNoShareInd(String noShareInd) {
		this.noShareInd = noShareInd;
	}

	public String getEndCustomerName() {
		return endCustomerName;
	}

	public void setEndCustomerName(String endCustomerName) {
		this.endCustomerName = endCustomerName;
	}

	public List<String> getMacdSubActivity() {
		return macdSubActivity;
	}

	public void setMacdSubActivity(List<String> macdSubActivity) {
		this.macdSubActivity = macdSubActivity;
	}

	public String getUserProvidedPowerSupplyInd() {
		return userProvidedPowerSupplyInd;
	}

	public void setUserProvidedPowerSupplyInd(String userProvidedPowerSupplyInd) {
		this.userProvidedPowerSupplyInd = userProvidedPowerSupplyInd;
	}

	public String getComments() {
		return comments;
	}

	public void setComments(String comments) {
		this.comments = comments;
	}
}
