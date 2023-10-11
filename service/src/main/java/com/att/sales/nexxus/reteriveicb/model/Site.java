package com.att.sales.nexxus.reteriveicb.model;

import java.util.List;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.map.annotate.JsonSerialize;

/**
 * The Class Site.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class Site {

	private Long nxSiteId;

	/** The site id. */
	private Long siteId;

	/** The npanxx. */
	private String npanxx;

	/** The address 1. */
	private String address1;

	/** The city. */
	private String city;

	/** The state. */
	private String state;

	/** The sa lec name. */
	private String saLecName;

	/** The speed id. */
	private Long speedId;

	/** The sa lec sw clli. */
	private String saLecSwClli;

	/** The pop clli. */
	private String popClli;

	/** The country. */
	private String country;

	/** The macd type. */
	private String macdType;

	/** The macd activity. */
	private String macdActivity;

	/** The from inventory. */
	private String fromInventory;

	/** The dual site id. */
	private Long dualSiteId;

	/** The on net check. */
	private String onNetCheck;

	/** The ethernet vendor. */
	private String ethernetVendor;

	/** The design site offer port. */
	private List<Port> designSiteOfferPort;

	/** The price details. */
	private PriceDetails priceDetails;

	/** The address. */
	private String address;

	/** The postal code. */
	private String postalCode;

	/** The site name. */
	private String siteName;

	/** The swc clli. */
	private String swcClli;

	/** The customer location clli. */
	private String customerLocationClli;

	/** The Active. */
	private String active;

	/** The emc. */
	private String emc;

	/** The carrier hotel. */
	private String carrierHotel;

	/** The att comments. */
	private String attComments;

	/** The new building. */
	private String newBuilding;

	/** The customer reference. */
	private String customerReference;

	/** The asr item id. */
	private String asrItemId;

	/** The lata code. */
	private String lataCode;

	/** The zip code. */
	private String zipCode;

	/** The address 2. */
	private String address2;

	/** The room. */
	private String room;

	/** The floor. */
	private String floor;

	/** The building. */
	private String building;

	/** The site comment. */
	private String siteComment;

	/** The lcon first name. */
	private String lconFirstName;

	/** The lcon last name. */
	private String lconLastName;

	/** The lcon phone. */
	private String lconPhone;

	/** The lcon email. */
	private String lconEmail;

	private String endPointSiteIdentifier;
	private String buildingClli;
	private String regionCode;
	private String activityType;
	private String cancellationReason;
	private String product;
	private String quantity;
	private String nssEngagement;
	private String designStatus;
	private String multiGigeIndicator;
	private String alias;
	private List<String> macdActionType;

	private List<LconDetails> lconDetails;

	private String globalLocationId;

	private String jurisdiction;

	private String certificationStatus;

	private String designModifiedInd;
	private String independentCarrierCompanyLATA;
	private String independentCarrierCompanyName;
	private String interDepartmentMeetPointChecklistURL;
	private String diverseFromReferenceInfo;
	private String opticalTerminatingNetworkCarrierFacilityAssignment;
	private String collocationCarrierFacilityAssignment;
	private String assetInvestmentSheetIndicator;
	private String swcCertification;
	private String designVersion;
	private String specialConstructionPaymentUrl;
	private String thirdPartyInd;
	private String accessCarrierNameAbbreviation;

	private List<Design> design;

	private String loopLength;

	@JsonProperty("NumOfCopperRepeaters")
	private String numOfCopperRepeaters;

	private String inventoryNumOfPairs;

	@JsonProperty("ta5kClli")
	private String taskClli;

	private String numberRemoteTerminals;
	
	private Long referenceOfferId;
	
	private String siteNpanxx;
	
	private Double specialConstructionCharge;
	private String specialConstructionHandling;
	private String specialConstructionHandlingNotes;
	private Long _endPointRef;
	private String popCollectorAsr;
	private Long circuitCertification;
	
	/**
	 * @return the nxSiteId
	 */
	public Long getNxSiteId() {
		return nxSiteId;
	}

	/**
	 * @param nxSiteId the nxSiteId to set
	 */
	public void setNxSiteId(Long nxSiteId) {
		this.nxSiteId = nxSiteId;
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
	 * Gets the postal code.
	 *
	 * @return the postal code
	 */
	public String getPostalCode() {
		return postalCode;
	}

	/**
	 * Sets the postal code.
	 *
	 * @param postalCode the new postal code
	 */
	public void setPostalCode(String postalCode) {
		this.postalCode = postalCode;
	}

	/**
	 * Gets the site name.
	 *
	 * @return the site name
	 */
	public String getSiteName() {
		return siteName;
	}

	/**
	 * Sets the site name.
	 *
	 * @param siteName the new site name
	 */
	public void setSiteName(String siteName) {
		this.siteName = siteName;
	}

	/**
	 * Gets the site id.
	 *
	 * @return the site id
	 */
	public Long getSiteId() {
		return siteId;
	}

	/**
	 * Sets the site id.
	 *
	 * @param siteId the new site id
	 */
	public void setSiteId(Long siteId) {
		this.siteId = siteId;
	}

	/**
	 * Gets the npanxx.
	 *
	 * @return the npanxx
	 */
	public String getNpanxx() {
		return npanxx;
	}

	/**
	 * Sets the npanxx.
	 *
	 * @param npanxx the new npanxx
	 */
	public void setNpanxx(String npanxx) {
		this.npanxx = npanxx;
	}

	/**
	 * Gets the address 1.
	 *
	 * @return the address 1
	 */
	public String getAddress1() {
		return address1;
	}

	/**
	 * Sets the address 1.
	 *
	 * @param address1 the new address 1
	 */
	public void setAddress1(String address1) {
		this.address1 = address1;
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
	 * Gets the sa lec name.
	 *
	 * @return the sa lec name
	 */
	public String getSaLecName() {
		return saLecName;
	}

	/**
	 * Sets the sa lec name.
	 *
	 * @param saLecName the new sa lec name
	 */
	public void setSaLecName(String saLecName) {
		this.saLecName = saLecName;
	}

	/**
	 * Gets the speed id.
	 *
	 * @return the speed id
	 */
	public Long getSpeedId() {
		return speedId;
	}

	/**
	 * Sets the speed id.
	 *
	 * @param speedId the new speed id
	 */
	public void setSpeedId(Long speedId) {
		this.speedId = speedId;
	}

	/**
	 * Gets the sa lec sw clli.
	 *
	 * @return the sa lec sw clli
	 */
	public String getSaLecSwClli() {
		return saLecSwClli;
	}

	/**
	 * Sets the sa lec xw clli.
	 *
	 * @param saLecSwClli the new sa lec xw clli
	 */
	public void setSaLecXwClli(String saLecSwClli) {
		this.saLecSwClli = saLecSwClli;
	}

	/**
	 * Gets the pop clli.
	 *
	 * @return the pop clli
	 */
	public String getPopClli() {
		return popClli;
	}

	/**
	 * Sets the pop clli.
	 *
	 * @param popClli the new pop clli
	 */
	public void setPopClli(String popClli) {
		this.popClli = popClli;
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
	 * Gets the macd type.
	 *
	 * @return the macd type
	 */
	public String getMacdType() {
		return macdType;
	}

	/**
	 * Sets the macd type.
	 *
	 * @param macdType the new macd type
	 */
	public void setMacdType(String macdType) {
		this.macdType = macdType;
	}

	/**
	 * Gets the macd activity.
	 *
	 * @return the macd activity
	 */
	public String getMacdActivity() {
		return macdActivity;
	}

	/**
	 * Sets the macd activity.
	 *
	 * @param macdActivity the new macd activity
	 */
	public void setMacdActivity(String macdActivity) {
		this.macdActivity = macdActivity;
	}

	/**
	 * Gets the from inventory.
	 *
	 * @return the from inventory
	 */
	public String getFromInventory() {
		return fromInventory;
	}

	/**
	 * Sets the from inventory.
	 *
	 * @param fromInventory the new from inventory
	 */
	public void setFromInventory(String fromInventory) {
		this.fromInventory = fromInventory;
	}

	/**
	 * Gets the dual site id.
	 *
	 * @return the dual site id
	 */
	public Long getDualSiteId() {
		return dualSiteId;
	}

	/**
	 * Sets the dual site id.
	 *
	 * @param dualSiteId the new dual site id
	 */
	public void setDualSiteId(Long dualSiteId) {
		this.dualSiteId = dualSiteId;
	}

	/**
	 * Gets the on net check.
	 *
	 * @return the on net check
	 */
	public String getOnNetCheck() {
		return onNetCheck;
	}

	/**
	 * Sets the on net check.
	 *
	 * @param onNetCheck the new on net check
	 */
	public void setOnNetCheck(String onNetCheck) {
		this.onNetCheck = onNetCheck;
	}

	/**
	 * Sets the ethernet vendor.
	 *
	 * @param ethernetVendor the new ethernet vendor
	 */
	public void setEthernetVendor(String ethernetVendor) {
		this.ethernetVendor = ethernetVendor;
	}

	/**
	 * Gets the design site offer port.
	 *
	 * @return the design site offer port
	 */
	public List<Port> getDesignSiteOfferPort() {
		return designSiteOfferPort;
	}

	/**
	 * Sets the design site offer port.
	 *
	 * @param designSiteOfferPort the new design site offer port
	 */
	public void setDesignSiteOfferPort(List<Port> designSiteOfferPort) {
		this.designSiteOfferPort = designSiteOfferPort;
	}

	/**
	 * Gets the price details.
	 *
	 * @return the price details
	 */
	public PriceDetails getPriceDetails() {
		return priceDetails;
	}

	/**
	 * Sets the price details.
	 *
	 * @param priceDetails the new price details
	 */
	public void setPriceDetails(PriceDetails priceDetails) {
		this.priceDetails = priceDetails;
	}

	/**
	 * Gets the swc clli.
	 *
	 * @return the swc clli
	 */
	public String getSwcClli() {
		return swcClli;
	}

	/**
	 * Sets the swc clli.
	 *
	 * @param swcClli the new swc clli
	 */
	public void setSwcClli(String swcClli) {
		this.swcClli = swcClli;
	}

	/**
	 * Gets the customer location clli.
	 *
	 * @return the customer location clli
	 */
	public String getCustomerLocationClli() {
		return customerLocationClli;
	}

	/**
	 * Sets the customer location clli.
	 *
	 * @param customerLocationClli the new customer location clli
	 */
	public void setCustomerLocationClli(String customerLocationClli) {
		this.customerLocationClli = customerLocationClli;
	}

	public String getActive() {
		return active;
	}

	public void setActive(String active) {
		this.active = active;
	}

	/**
	 * Gets the emc.
	 *
	 * @return the emc
	 */
	public String getEmc() {
		return emc;
	}

	/**
	 * Sets the emc.
	 *
	 * @param emc the new emc
	 */
	public void setEmc(String emc) {
		this.emc = emc;
	}

	/**
	 * Gets the carrier hotel.
	 *
	 * @return the carrier hotel
	 */
	public String getCarrierHotel() {
		return carrierHotel;
	}

	/**
	 * Sets the carrier hotel.
	 *
	 * @param carrierHotel the new carrier hotel
	 */
	public void setCarrierHotel(String carrierHotel) {
		this.carrierHotel = carrierHotel;
	}

	/**
	 * Gets the att comments.
	 *
	 * @return the att comments
	 */
	public String getAttComments() {
		return attComments;
	}

	/**
	 * Sets the att comments.
	 *
	 * @param attComments the new att comments
	 */
	public void setAttComments(String attComments) {
		this.attComments = attComments;
	}

	/**
	 * Gets the new building.
	 *
	 * @return the new building
	 */
	public String getNewBuilding() {
		return newBuilding;
	}

	/**
	 * Sets the new building.
	 *
	 * @param newBuilding the new new building
	 */
	public void setNewBuilding(String newBuilding) {
		this.newBuilding = newBuilding;
	}

	/**
	 * Gets the customer reference.
	 *
	 * @return the customer reference
	 */
	public String getCustomerReference() {
		return customerReference;
	}

	/**
	 * Sets the customer reference.
	 *
	 * @param customerReference the new customer reference
	 */
	public void setCustomerReference(String customerReference) {
		this.customerReference = customerReference;
	}

	/**
	 * Gets the asr item id.
	 *
	 * @return the asr item id
	 */
	public String getAsrItemId() {
		return asrItemId;
	}

	/**
	 * Sets the asr item id.
	 *
	 * @param asrItemId the new asr item id
	 */
	public void setAsrItemId(String asrItemId) {
		this.asrItemId = asrItemId;
	}

	/**
	 * Gets the lata code.
	 *
	 * @return the lata code
	 */
	public String getLataCode() {
		return lataCode;
	}

	/**
	 * Sets the lata code.
	 *
	 * @param lataCode the new lata code
	 */
	public void setLataCode(String lataCode) {
		this.lataCode = lataCode;
	}

	/**
	 * Gets the zip code.
	 *
	 * @return the zip code
	 */
	public String getZipCode() {
		return zipCode;
	}

	/**
	 * Sets the zip code.
	 *
	 * @param zipCode the new zip code
	 */
	public void setZipCode(String zipCode) {
		this.zipCode = zipCode;
	}

	/**
	 * Gets the address 2.
	 *
	 * @return the address 2
	 */
	public String getAddress2() {
		return address2;
	}

	/**
	 * Sets the address 2.
	 *
	 * @param address2 the new address 2
	 */
	public void setAddress2(String address2) {
		this.address2 = address2;
	}

	/**
	 * Gets the room.
	 *
	 * @return the room
	 */
	public String getRoom() {
		return room;
	}

	/**
	 * Sets the room.
	 *
	 * @param room the new room
	 */
	public void setRoom(String room) {
		this.room = room;
	}

	/**
	 * Gets the floor.
	 *
	 * @return the floor
	 */
	public String getFloor() {
		return floor;
	}

	/**
	 * Sets the floor.
	 *
	 * @param floor the new floor
	 */
	public void setFloor(String floor) {
		this.floor = floor;
	}

	/**
	 * Gets the building.
	 *
	 * @return the building
	 */
	public String getBuilding() {
		return building;
	}

	/**
	 * Sets the building.
	 *
	 * @param building the new building
	 */
	public void setBuilding(String building) {
		this.building = building;
	}

	/**
	 * Gets the site comment.
	 *
	 * @return the site comment
	 */
	public String getSiteComment() {
		return siteComment;
	}

	/**
	 * Sets the site comment.
	 *
	 * @param siteComment the new site comment
	 */
	public void setSiteComment(String siteComment) {
		this.siteComment = siteComment;
	}

	/**
	 * Gets the ethernet vendor.
	 *
	 * @return the ethernet vendor
	 */
	public String getEthernetVendor() {
		return ethernetVendor;
	}

	/**
	 * Sets the sa lec sw clli.
	 *
	 * @param saLecSwClli the new sa lec sw clli
	 */
	public void setSaLecSwClli(String saLecSwClli) {
		this.saLecSwClli = saLecSwClli;
	}

	/**
	 * Gets the lcon first name.
	 *
	 * @return the lcon first name
	 */
	public String getLconFirstName() {
		return lconFirstName;
	}

	/**
	 * Sets the lcon first name.
	 *
	 * @param lconFirstName the new lcon first name
	 */
	public void setLconFirstName(String lconFirstName) {
		this.lconFirstName = lconFirstName;
	}

	/**
	 * Gets the lcon last name.
	 *
	 * @return the lcon last name
	 */
	public String getLconLastName() {
		return lconLastName;
	}

	/**
	 * Sets the lcon last name.
	 *
	 * @param lconLastName the new lcon last name
	 */
	public void setLconLastName(String lconLastName) {
		this.lconLastName = lconLastName;
	}

	/**
	 * Gets the lcon phone.
	 *
	 * @return the lcon phone
	 */
	public String getLconPhone() {
		return lconPhone;
	}

	/**
	 * Sets the lcon phone.
	 *
	 * @param lconPhone the new lcon phone
	 */
	public void setLconPhone(String lconPhone) {
		this.lconPhone = lconPhone;
	}

	/**
	 * Gets the lcon email.
	 *
	 * @return the lcon email
	 */
	public String getLconEmail() {
		return lconEmail;
	}

	/**
	 * Sets the lcon email.
	 *
	 * @param lconEmail the new lcon email
	 */
	public void setLconEmail(String lconEmail) {
		this.lconEmail = lconEmail;
	}

	public String getEndPointSiteIdentifier() {
		return endPointSiteIdentifier;
	}

	public void setEndPointSiteIdentifier(String endPointSiteIdentifier) {
		this.endPointSiteIdentifier = endPointSiteIdentifier;
	}

	public String getBuildingClli() {
		return buildingClli;
	}

	public void setBuildingClli(String buildingClli) {
		this.buildingClli = buildingClli;
	}

	public String getRegionCode() {
		return regionCode;
	}

	public void setRegionCode(String regionCode) {
		this.regionCode = regionCode;
	}

	public String getActivityType() {
		return activityType;
	}

	public void setActivityType(String activityType) {
		this.activityType = activityType;
	}

	public String getCancellationReason() {
		return cancellationReason;
	}

	public void setCancellationReason(String cancellationReason) {
		this.cancellationReason = cancellationReason;
	}

	public String getProduct() {
		return product;
	}

	public void setProduct(String product) {
		this.product = product;
	}

	public String getQuantity() {
		return quantity;
	}

	public void setQuantity(String quantity) {
		this.quantity = quantity;
	}

	public String getNssEngagement() {
		return nssEngagement;
	}

	public void setNssEngagement(String nssEngagement) {
		this.nssEngagement = nssEngagement;
	}

	public String getDesignStatus() {
		return designStatus;
	}

	public void setDesignStatus(String designStatus) {
		this.designStatus = designStatus;
	}

	public List<LconDetails> getLconDetails() {
		return lconDetails;
	}

	public void setLconDetails(List<LconDetails> lconDetails) {
		this.lconDetails = lconDetails;
	}

	public String getMultiGigeIndicator() {
		return multiGigeIndicator;
	}

	public void setMultiGigeIndicator(String multiGigeIndicator) {
		this.multiGigeIndicator = multiGigeIndicator;
	}

	public String getAlias() {
		return alias;
	}

	public void setAlias(String alias) {
		this.alias = alias;
	}

	public List<String> getMacdActionType() {
		return macdActionType;
	}

	public void setMacdActionType(List<String> macdActionType) {
		this.macdActionType = macdActionType;
	}

	public String getGlobalLocationId() {
		return globalLocationId;
	}

	public void setGlobalLocationId(String globalLocationId) {
		this.globalLocationId = globalLocationId;
	}

	public String getJurisdiction() {
		return jurisdiction;
	}

	public void setJurisdiction(String jurisdiction) {
		this.jurisdiction = jurisdiction;
	}

	public String getCertificationStatus() {
		return certificationStatus;
	}

	public void setCertificationStatus(String certificationStatus) {
		this.certificationStatus = certificationStatus;
	}

	public String getDesignModifiedInd() {
		return designModifiedInd;
	}

	public void setDesignModifiedInd(String designModifiedInd) {
		this.designModifiedInd = designModifiedInd;
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

	public String getAssetInvestmentSheetIndicator() {
		return assetInvestmentSheetIndicator;
	}

	public void setAssetInvestmentSheetIndicator(String assetInvestmentSheetIndicator) {
		this.assetInvestmentSheetIndicator = assetInvestmentSheetIndicator;
	}

	public String getSwcCertification() {
		return swcCertification;
	}

	public void setSwcCertification(String swcCertification) {
		this.swcCertification = swcCertification;
	}

	public String getDesignVersion() {
		return designVersion;
	}

	public void setDesignVersion(String designVersion) {
		this.designVersion = designVersion;
	}

	public String getSpecialConstructionPaymentUrl() {
		return specialConstructionPaymentUrl;
	}

	public void setSpecialConstructionPaymentUrl(String specialConstructionPaymentUrl) {
		this.specialConstructionPaymentUrl = specialConstructionPaymentUrl;
	}

	public String getThirdPartyInd() {
		return thirdPartyInd;
	}

	public void setThirdPartyInd(String thirdPartyInd) {
		this.thirdPartyInd = thirdPartyInd;
	}

	public String getAccessCarrierNameAbbreviation() {
		return accessCarrierNameAbbreviation;
	}

	public void setAccessCarrierNameAbbreviation(String accessCarrierNameAbbreviation) {
		this.accessCarrierNameAbbreviation = accessCarrierNameAbbreviation;
	}

	public List<Design> getDesign() {
		return design;
	}

	public void setDesign(List<Design> design) {
		this.design = design;
	}

	/**
	 * @return the loopLength
	 */
	public String getLoopLength() {
		return loopLength;
	}

	/**
	 * @param loopLength the loopLength to set
	 */
	public void setLoopLength(String loopLength) {
		this.loopLength = loopLength;
	}

	/**
	 * @return the numOfCopperRepeaters
	 */
	public String getNumOfCopperRepeaters() {
		return numOfCopperRepeaters;
	}

	/**
	 * @param numOfCopperRepeaters the numOfCopperRepeaters to set
	 */
	public void setNumOfCopperRepeaters(String numOfCopperRepeaters) {
		this.numOfCopperRepeaters = numOfCopperRepeaters;
	}

	/**
	 * @return the inventoryNumOfPairs
	 */
	public String getInventoryNumOfPairs() {
		return inventoryNumOfPairs;
	}

	/**
	 * @param inventoryNumOfPairs the inventoryNumOfPairs to set
	 */
	public void setInventoryNumOfPairs(String inventoryNumOfPairs) {
		this.inventoryNumOfPairs = inventoryNumOfPairs;
	}

	/**
	 * @return the taskClli
	 */
	public String getTaskClli() {
		return taskClli;
	}

	/**
	 * @param taskClli the taskClli to set
	 */
	public void setTaskClli(String taskClli) {
		this.taskClli = taskClli;
	}

	/**
	 * @return the numberRemoteTerminals
	 */
	public String getNumberRemoteTerminals() {
		return numberRemoteTerminals;
	}

	/**
	 * @param numberRemoteTerminals the numberRemoteTerminals to set
	 */
	public void setNumberRemoteTerminals(String numberRemoteTerminals) {
		this.numberRemoteTerminals = numberRemoteTerminals;
	}

	public Long getReferenceOfferId() {
		return referenceOfferId;
	}

	public void setReferenceOfferId(Long referenceOfferId) {
		this.referenceOfferId = referenceOfferId;
	}

	public String getSiteNpanxx() {
		return siteNpanxx;
	}

	public void setSiteNpanxx(String siteNpanxx) {
		this.siteNpanxx = siteNpanxx;
	}

	/**
	 * @return the specialConstructionCharge
	 */
	public Double getSpecialConstructionCharge() {
		return specialConstructionCharge;
	}

	/**
	 * @param specialConstructionCharge the specialConstructionCharge to set
	 */
	public void setSpecialConstructionCharge(Double specialConstructionCharge) {
		this.specialConstructionCharge = specialConstructionCharge;
	}

	/**
	 * @return the specialConstructionHandling
	 */
	public String getSpecialConstructionHandling() {
		return specialConstructionHandling;
	}

	/**
	 * @param specialConstructionHandling the specialConstructionHandling to set
	 */
	public void setSpecialConstructionHandling(String specialConstructionHandling) {
		this.specialConstructionHandling = specialConstructionHandling;
	}

	/**
	 * @return the specialConstructionHandlingNotes
	 */
	public String getSpecialConstructionHandlingNotes() {
		return specialConstructionHandlingNotes;
	}

	/**
	 * @param specialConstructionHandlingNotes the specialConstructionHandlingNotes to set
	 */
	public void setSpecialConstructionHandlingNotes(String specialConstructionHandlingNotes) {
		this.specialConstructionHandlingNotes = specialConstructionHandlingNotes;
	}

	public Long get_endPointRef() {
		return _endPointRef;
	}

	public void set_endPointRef(Long _endPointRef) {
		this._endPointRef = _endPointRef;
	}

	public String getPopCollectorAsr() {
		return popCollectorAsr;
	}

	public void setPopCollectorAsr(String popCollectorAsr) {
		this.popCollectorAsr = popCollectorAsr;
	}

	public Long getCircuitCertification() {
		return circuitCertification;
	}

	public void setCircuitCertification(Long circuitCertification) {
		this.circuitCertification = circuitCertification;
	}
	
}
