package com.att.sales.nexxus.model;

import java.io.Serializable;
import java.util.List;

import org.codehaus.jackson.map.annotate.JsonSerialize;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * The Class GUIResponse.
 *
 * @author km017g
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonSerialize(include=JsonSerialize.Inclusion.NON_NULL)
public class GUIResponse implements Serializable{
	
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;
	
	/** The quote id. */
	private String quoteId;
	
	/** The quote name. */
	private String quoteName;
    
    /** The error ind. */
    private String errorInd;
	
	/** The bulk request id. */
	private String bulkRequestId;
	
	/** The quote qualification. */
	private String quoteQualification; 
	
	/** The prob build desc. */
	private String probBuildDesc ;
	
	/** The architecture met. */
	private String architectureMet; 
	
	/** The bandwidth met. */
	private String bandwidthMet ;
	
	/** The vendor preference met. */
	private String vendorPreferenceMet;
	
	/** The error message. */
	private String errorMessage ;
	
	/** The customer name. */
	private String customerName ;
	
	/** The site ref ID. */
	private String siteRefID ;
	
	/** The req street address. */
	private String reqStreetAddress ;
	
	/** The req city. */
	private String reqCity ;
	
	/** The req state. */
	private String reqState ;
	
	/** The req zip code. */
	private String reqZipCode; 
	
	/** The req country. */
	private String reqCountry ;
	
	/** The req building. */
	private String reqBuilding ;
	
	/** The req floor. */
	private String reqFloor ;
	
	/** The req room. */
	private String reqRoom ;
	
	/** The match score. */
	private String matchScore ;
	
	/** The street address. */
	private String streetAddress ;
	
	/** The city. */
	private String city ;
	
	/** The state. */
	private String state ;
	
	/** The zip code. */
	private String zipCode; 
	
	/** The country. */
	private String country ;
	
	/** The latitude. */
	@JsonSerialize(include = JsonSerialize.Inclusion.NON_DEFAULT)
	private double latitude ; 
	
	/** The longitude. */
	@JsonSerialize(include = JsonSerialize.Inclusion.NON_DEFAULT)
	private double longitude ; 
	
	/** The v coordinate. */
	private String vCoordinate ;
	
	/** The h coordinate. */
	private String hCoordinate ;
	
	/** The req contract term. */
	private String reqContractTerm ;
	
	/** The req service. */
	private String reqService ;
	
	/** The req access transport. */
	private String reqAccessTransport ;
	
	/** The req access bandwidth. */
	private String reqAccessBandwidth ;
	
	/** The req access arch. */
	private String reqAccessArch ;
	
	/** The req physical interface. */
	private String reqPhysicalInterface ;
	
	/** The req discount percentage. */
	private String reqDiscountPercentage ;
	
	/** The req port level cos. */
	private String reqPortLevelCos ;
	
	/** The req cmtu. */
	private String reqCmtu ;
	
	/** The req att eth pop. */
	private String reqAttEthPop ;
	
	/** The req circuit quantity. */
	private String reqCircuitQuantity ;
	
	/** The vendor preference requested. */
	private String vendorPreferenceRequested ;
	
	/** The req vendor. */
	private String reqVendor ;
	
	/** The req on net building address. */
	private String reqOnNetBuildingAddress ;
	
	/** The req npanxx. */
	private String reqNpanxx ;
	
	/** The req ilec swc. */
	private String reqIlecSwc ;
	
	/** The att eth pop. */
	private String attEthPop;
	
	/** The contract term. */
	private String contractTerm; 
	
	/** The service. */
	private String service ;
	
	/** The access transport. */
	private String accessTransport ;
	
	/** The bandwidth. */
	private String bandwidth ;
	
	/** The access arch. */
	private String accessArch ;
	
	/** The physical interface. */
	private String physicalInterface ;
	
	/** The technology. */
	private String technology ;
	
	/** The mileage used for pricing. */
	private String mileageUsedForPricing ;
	
	/** The base monthly recurring price. */
	private String baseMonthlyRecurringPrice ;
	
	/** The discount percentage. */
	private String discountPercentage ;
	
	/** The discount monthly recurring price. */
	private String discountMonthlyRecurringPrice ;
	
	/** The non recurring charge. */
	private String nonRecurringCharge ;
	
	/** The currency. */
	private String currency ;
	
	/** The seap serial number. */
	private String seapSerialNumber ;
	
	/** The eth token. */
	private String ethToken ;
	
	/** The igloo quote ID. */
	private String iglooQuoteID ;
	
	/** The creation date. */
	private String creationDate ;
	
	/** The days until quote expires. */
	private String daysUntilQuoteExpires ;
	
	/** The expiry date. */
	private String expiryDate ;
	
	/** The cmtu. */
	private String cmtu;
	
	/** The pmtu. */
	private String pmtu ;
	
	/** The mtu support message. */
	private String mtuSupportMessage ;
	
	/** The pref vendors. */
	private String prefVendors ;
	
	/** The avoid vendors. */
	private String avoidVendors ;
	
	/** The rules derived message. */
	private String rulesDerivedMessage ;
	
	/** The quote status. */
	private String quoteStatus ;
	
	/** The user comments. */
	private String userComments;
	
	/** The sub grouploc details. */
	private List<Subgrouplocdetails> subGrouplocDetails;
	
	/** The site id. */
	private String siteId;
	
	/** The swclli. */
	private String swclli;
	
	/** The diversity id. */
	private String diversityId;
	
	/** The diversity options. */
	private String diversityOptions;
	
	/** The diversity grouping. */
	private String diversityGrouping;
	
	/** The diversity order type. */
	private String diversityOrderType;
	
	/** The diversity vendor type. */
	private String diversityVendorType;
	
	/** The diversity change order. */
	private String diversityChangeOrder;
	
	/** The special construction charge. */
	private String splConstructionCharge;
	
	/** The QuoteType. */
	private String quoteType;

	/** The zone. */
	private String zone;

	private String ethernetLcMonthlyRecurringCost; 
	private String ethernetLcPopMonthlyRecurringCost; 
	private String splConstructionCostMRC; 
	private String ethernetLcNonRecurringCost; 
	private String ethernetLcPopNonRecurringCost; 
	private String splConstructionCostNRC; 

	
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

	public String getSplConstructionCharge() {
		return splConstructionCharge;
	}

	public void setSplConstructionCharge(String splConstructionCharge) {
		this.splConstructionCharge = splConstructionCharge;
	}

	public String getQuoteType() {
		return quoteType;
	}

	public void setQuoteType(String quoteType) {
		this.quoteType = quoteType;
	}

	/**
	 * Gets the error ind.
	 *
	 * @return the error ind
	 */
	public String getErrorInd() {
		return errorInd;
	}
	
	/**
	 * Sets the error ind.
	 *
	 * @param errorInd the new error ind
	 */
	public void setErrorInd(String errorInd) {
		this.errorInd = errorInd;
	}
	
	/**
	 * Gets the diversity id.
	 *
	 * @return the diversity id
	 */
	public String getDiversityId() {
		return diversityId;
	}
	
	/**
	 * Sets the diversity id.
	 *
	 * @param diversityId the new diversity id
	 */
	public void setDiversityId(String diversityId) {
		this.diversityId = diversityId;
	}
	
	/**
	 * Gets the diversity options.
	 *
	 * @return the diversity options
	 */
	public String getDiversityOptions() {
		return diversityOptions;
	}
	
	/**
	 * Sets the diversity options.
	 *
	 * @param diversityOptions the new diversity options
	 */
	public void setDiversityOptions(String diversityOptions) {
		this.diversityOptions = diversityOptions;
	}
	
	/**
	 * Gets the diversity grouping.
	 *
	 * @return the diversity grouping
	 */
	public String getDiversityGrouping() {
		return diversityGrouping;
	}
	
	/**
	 * Sets the diversity grouping.
	 *
	 * @param diversityGrouping the new diversity grouping
	 */
	public void setDiversityGrouping(String diversityGrouping) {
		this.diversityGrouping = diversityGrouping;
	}
	
	/**
	 * Gets the diversity order type.
	 *
	 * @return the diversity order type
	 */
	public String getDiversityOrderType() {
		return diversityOrderType;
	}
	
	/**
	 * Sets the diversity order type.
	 *
	 * @param diversityOrderType the new diversity order type
	 */
	public void setDiversityOrderType(String diversityOrderType) {
		this.diversityOrderType = diversityOrderType;
	}
	
	/**
	 * Gets the diversity vendor type.
	 *
	 * @return the diversity vendor type
	 */
	public String getDiversityVendorType() {
		return diversityVendorType;
	}
	
	/**
	 * Sets the diversity vendor type.
	 *
	 * @param diversityVendorType the new diversity vendor type
	 */
	public void setDiversityVendorType(String diversityVendorType) {
		this.diversityVendorType = diversityVendorType;
	}
	
	/**
	 * Gets the diversity change order.
	 *
	 * @return the diversity change order
	 */
	public String getDiversityChangeOrder() {
		return diversityChangeOrder;
	}
	
	/**
	 * Sets the diversity change order.
	 *
	 * @param diversityChangeOrder the new diversity change order
	 */
	public void setDiversityChangeOrder(String diversityChangeOrder) {
		this.diversityChangeOrder = diversityChangeOrder;
	}
	
	/**
	 * Gets the cmtu.
	 *
	 * @return the cmtu
	 */
	public String getCmtu() {
		return cmtu;
	}
	
	/**
	 * Sets the cmtu.
	 *
	 * @param cmtu the new cmtu
	 */
	public void setCmtu(String cmtu) {
		this.cmtu = cmtu;
	}
	
	/**
	 * Gets the quote id.
	 *
	 * @return the quote id
	 */
	public String getQuoteId() {
		return quoteId;
	}
	
	/**
	 * Sets the quote id.
	 *
	 * @param quoteId the new quote id
	 */
	public void setQuoteId(String quoteId) {
		this.quoteId = quoteId;
	}
	
	/**
	 * Gets the quote name.
	 *
	 * @return the quote name
	 */
	public String getQuoteName() {
		return quoteName;
	}
	
	/**
	 * Sets the quote name.
	 *
	 * @param quoteName the new quote name
	 */
	public void setQuoteName(String quoteName) {
		this.quoteName = quoteName;
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
	 * Gets the req on net building address.
	 *
	 * @return the req on net building address
	 */
	public String getReqOnNetBuildingAddress() {
		return reqOnNetBuildingAddress;
	}
	
	/**
	 * Sets the req on net building address.
	 *
	 * @param reqOnNetBuildingAddress the new req on net building address
	 */
	public void setReqOnNetBuildingAddress(String reqOnNetBuildingAddress) {
		this.reqOnNetBuildingAddress = reqOnNetBuildingAddress;
	}
	
	/**
	 * Gets the req npanxx.
	 *
	 * @return the req npanxx
	 */
	public String getReqNpanxx() {
		return reqNpanxx;
	}
	
	/**
	 * Sets the req npanxx.
	 *
	 * @param reqNpanxx the new req npanxx
	 */
	public void setReqNpanxx(String reqNpanxx) {
		this.reqNpanxx = reqNpanxx;
	}
	
	/**
	 * Gets the swclli.
	 *
	 * @return the swclli
	 */
	public String getSwclli() {
		return swclli;
	}
	
	/**
	 * Sets the swclli.
	 *
	 * @param swclli the new swclli
	 */
	public void setSwclli(String swclli) {
		this.swclli = swclli;
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
	 * Gets the sub grouploc details.
	 *
	 * @return the sub grouploc details
	 */
	public List<Subgrouplocdetails> getSubGrouplocDetails() {
		return subGrouplocDetails;
	}
	
	/**
	 * Sets the sub grouploc details.
	 *
	 * @param subGrouplocDetails the new sub grouploc details
	 */
	public void setSubGrouplocDetails(List<Subgrouplocdetails> subGrouplocDetails) {
		this.subGrouplocDetails = subGrouplocDetails;
	}
	
	/**
	 * Gets the bulk request id.
	 *
	 * @return the bulk request id
	 */
	public String getBulkRequestId() {
		return bulkRequestId;
	}
	
	/**
	 * Sets the bulk request id.
	 *
	 * @param bulkRequestId the new bulk request id
	 */
	public void setBulkRequestId(String bulkRequestId) {
		this.bulkRequestId = bulkRequestId;
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
	 * Gets the prob build desc.
	 *
	 * @return the prob build desc
	 */
	public String getProbBuildDesc() {
		return probBuildDesc;
	}
	
	/**
	 * Sets the prob build desc.
	 *
	 * @param probBuildDesc the new prob build desc
	 */
	public void setProbBuildDesc(String probBuildDesc) {
		this.probBuildDesc = probBuildDesc;
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
	 * Gets the customer name.
	 *
	 * @return the customer name
	 */
	public String getCustomerName() {
		return customerName;
	}
	
	/**
	 * Sets the customer name.
	 *
	 * @param customerName the new customer name
	 */
	public void setCustomerName(String customerName) {
		this.customerName = customerName;
	}
	
	/**
	 * Gets the site ref ID.
	 *
	 * @return the site ref ID
	 */
	public String getSiteRefID() {
		return siteRefID;
	}
	
	/**
	 * Sets the site ref ID.
	 *
	 * @param siteRefID the new site ref ID
	 */
	public void setSiteRefID(String siteRefID) {
		this.siteRefID = siteRefID;
	}
	
	/**
	 * Gets the req street address.
	 *
	 * @return the req street address
	 */
	public String getReqStreetAddress() {
		return reqStreetAddress;
	}
	
	/**
	 * Sets the req street address.
	 *
	 * @param reqStreetAddress the new req street address
	 */
	public void setReqStreetAddress(String reqStreetAddress) {
		this.reqStreetAddress = reqStreetAddress;
	}
	
	/**
	 * Gets the req city.
	 *
	 * @return the req city
	 */
	public String getReqCity() {
		return reqCity;
	}
	
	/**
	 * Sets the req city.
	 *
	 * @param reqCity the new req city
	 */
	public void setReqCity(String reqCity) {
		this.reqCity = reqCity;
	}
	
	/**
	 * Gets the req state.
	 *
	 * @return the req state
	 */
	public String getReqState() {
		return reqState;
	}
	
	/**
	 * Sets the req state.
	 *
	 * @param reqState the new req state
	 */
	public void setReqState(String reqState) {
		this.reqState = reqState;
	}
	
	/**
	 * Gets the req zip code.
	 *
	 * @return the req zip code
	 */
	public String getReqZipCode() {
		return reqZipCode;
	}
	
	/**
	 * Sets the req zip code.
	 *
	 * @param reqZipCode the new req zip code
	 */
	public void setReqZipCode(String reqZipCode) {
		this.reqZipCode = reqZipCode;
	}
	
	/**
	 * Gets the req country.
	 *
	 * @return the req country
	 */
	public String getReqCountry() {
		return reqCountry;
	}
	
	/**
	 * Sets the req country.
	 *
	 * @param reqCountry the new req country
	 */
	public void setReqCountry(String reqCountry) {
		this.reqCountry = reqCountry;
	}
	
	/**
	 * Gets the req building.
	 *
	 * @return the req building
	 */
	public String getReqBuilding() {
		return reqBuilding;
	}
	
	/**
	 * Sets the req building.
	 *
	 * @param reqBuilding the new req building
	 */
	public void setReqBuilding(String reqBuilding) {
		this.reqBuilding = reqBuilding;
	}
	
	/**
	 * Gets the req floor.
	 *
	 * @return the req floor
	 */
	public String getReqFloor() {
		return reqFloor;
	}
	
	/**
	 * Sets the req floor.
	 *
	 * @param reqFloor the new req floor
	 */
	public void setReqFloor(String reqFloor) {
		this.reqFloor = reqFloor;
	}
	
	/**
	 * Gets the req room.
	 *
	 * @return the req room
	 */
	public String getReqRoom() {
		return reqRoom;
	}
	
	/**
	 * Sets the req room.
	 *
	 * @param reqRoom the new req room
	 */
	public void setReqRoom(String reqRoom) {
		this.reqRoom = reqRoom;
	}
	
	/**
	 * Gets the match score.
	 *
	 * @return the match score
	 */
	public String getMatchScore() {
		return matchScore;
	}
	
	/**
	 * Sets the match score.
	 *
	 * @param matchScore the new match score
	 */
	public void setMatchScore(String matchScore) {
		this.matchScore = matchScore;
	}
	
	/**
	 * Gets the street address.
	 *
	 * @return the street address
	 */
	public String getStreetAddress() {
		return streetAddress;
	}
	
	/**
	 * Sets the street address.
	 *
	 * @param streetAddress the new street address
	 */
	public void setStreetAddress(String streetAddress) {
		this.streetAddress = streetAddress;
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
	 * Gets the latitude.
	 *
	 * @return the latitude
	 */
	public double getLatitude() {
		return latitude;
	}
	
	/**
	 * Sets the latitude.
	 *
	 * @param latitude the new latitude
	 */
	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}
	
	/**
	 * Gets the longitude.
	 *
	 * @return the longitude
	 */
	public double getLongitude() {
		return longitude;
	}
	
	/**
	 * Sets the longitude.
	 *
	 * @param longitude the new longitude
	 */
	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}
	
	/**
	 * Gets the v coordinate.
	 *
	 * @return the v coordinate
	 */
	public String getvCoordinate() {
		return vCoordinate;
	}
	
	/**
	 * Sets the v coordinate.
	 *
	 * @param vCoordinate the new v coordinate
	 */
	public void setvCoordinate(String vCoordinate) {
		this.vCoordinate = vCoordinate;
	}
	
	/**
	 * Gets the h coordinate.
	 *
	 * @return the h coordinate
	 */
	public String gethCoordinate() {
		return hCoordinate;
	}
	
	/**
	 * Sets the h coordinate.
	 *
	 * @param hCoordinate the new h coordinate
	 */
	public void sethCoordinate(String hCoordinate) {
		this.hCoordinate = hCoordinate;
	}
	
	/**
	 * Gets the req contract term.
	 *
	 * @return the req contract term
	 */
	public String getReqContractTerm() {
		return reqContractTerm;
	}
	
	/**
	 * Sets the req contract term.
	 *
	 * @param reqContractTerm the new req contract term
	 */
	public void setReqContractTerm(String reqContractTerm) {
		this.reqContractTerm = reqContractTerm;
	}
	
	/**
	 * Gets the req service.
	 *
	 * @return the req service
	 */
	public String getReqService() {
		return reqService;
	}
	
	/**
	 * Sets the req service.
	 *
	 * @param reqService the new req service
	 */
	public void setReqService(String reqService) {
		this.reqService = reqService;
	}
	
	/**
	 * Gets the req access transport.
	 *
	 * @return the req access transport
	 */
	public String getReqAccessTransport() {
		return reqAccessTransport;
	}
	
	/**
	 * Sets the req access transport.
	 *
	 * @param reqAccessTransport the new req access transport
	 */
	public void setReqAccessTransport(String reqAccessTransport) {
		this.reqAccessTransport = reqAccessTransport;
	}
	
	/**
	 * Gets the req access bandwidth.
	 *
	 * @return the req access bandwidth
	 */
	public String getReqAccessBandwidth() {
		return reqAccessBandwidth;
	}
	
	/**
	 * Sets the req access bandwidth.
	 *
	 * @param reqAccessBandwidth the new req access bandwidth
	 */
	public void setReqAccessBandwidth(String reqAccessBandwidth) {
		this.reqAccessBandwidth = reqAccessBandwidth;
	}
	
	/**
	 * Gets the req access arch.
	 *
	 * @return the req access arch
	 */
	public String getReqAccessArch() {
		return reqAccessArch;
	}
	
	/**
	 * Sets the req access arch.
	 *
	 * @param reqAccessArch the new req access arch
	 */
	public void setReqAccessArch(String reqAccessArch) {
		this.reqAccessArch = reqAccessArch;
	}
	
	/**
	 * Gets the req physical interface.
	 *
	 * @return the req physical interface
	 */
	public String getReqPhysicalInterface() {
		return reqPhysicalInterface;
	}
	
	/**
	 * Sets the req physical interface.
	 *
	 * @param reqPhysicalInterface the new req physical interface
	 */
	public void setReqPhysicalInterface(String reqPhysicalInterface) {
		this.reqPhysicalInterface = reqPhysicalInterface;
	}
	
	/**
	 * Gets the req discount percentage.
	 *
	 * @return the req discount percentage
	 */
	public String getReqDiscountPercentage() {
		return reqDiscountPercentage;
	}
	
	/**
	 * Sets the req discount percentage.
	 *
	 * @param reqDiscountPercentage the new req discount percentage
	 */
	public void setReqDiscountPercentage(String reqDiscountPercentage) {
		this.reqDiscountPercentage = reqDiscountPercentage;
	}
	
	/**
	 * Gets the req port level cos.
	 *
	 * @return the req port level cos
	 */
	public String getReqPortLevelCos() {
		return reqPortLevelCos;
	}
	
	/**
	 * Sets the req port level cos.
	 *
	 * @param reqPortLevelCos the new req port level cos
	 */
	public void setReqPortLevelCos(String reqPortLevelCos) {
		this.reqPortLevelCos = reqPortLevelCos;
	}
	
	/**
	 * Gets the req cmtu.
	 *
	 * @return the req cmtu
	 */
	public String getReqCmtu() {
		return reqCmtu;
	}
	
	/**
	 * Sets the req cmtu.
	 *
	 * @param reqCmtu the new req cmtu
	 */
	public void setReqCmtu(String reqCmtu) {
		this.reqCmtu = reqCmtu;
	}
	
	/**
	 * Gets the req att eth pop.
	 *
	 * @return the req att eth pop
	 */
	public String getReqAttEthPop() {
		return reqAttEthPop;
	}
	
	/**
	 * Sets the req att eth pop.
	 *
	 * @param reqAttEthPop the new req att eth pop
	 */
	public void setReqAttEthPop(String reqAttEthPop) {
		this.reqAttEthPop = reqAttEthPop;
	}
	
	/**
	 * Gets the req circuit quantity.
	 *
	 * @return the req circuit quantity
	 */
	public String getReqCircuitQuantity() {
		return reqCircuitQuantity;
	}
	
	/**
	 * Sets the req circuit quantity.
	 *
	 * @param reqCircuitQuantity the new req circuit quantity
	 */
	public void setReqCircuitQuantity(String reqCircuitQuantity) {
		this.reqCircuitQuantity = reqCircuitQuantity;
	}
	
	/**
	 * Gets the req vendor.
	 *
	 * @return the req vendor
	 */
	public String getReqVendor() {
		return reqVendor;
	}
	
	/**
	 * Sets the req vendor.
	 *
	 * @param reqVendor the new req vendor
	 */
	public void setReqVendor(String reqVendor) {
		this.reqVendor = reqVendor;
	}
	
	/**
	 * Gets the att eth pop.
	 *
	 * @return the att eth pop
	 */
	public String getAttEthPop() {
		return attEthPop;
	}
	
	/**
	 * Sets the att eth pop.
	 *
	 * @param attEthPop the new att eth pop
	 */
	public void setAttEthPop(String attEthPop) {
		this.attEthPop = attEthPop;
	}
	
	/**
	 * Gets the req ilec swc.
	 *
	 * @return the req ilec swc
	 */
	public String getReqIlecSwc() {
		return reqIlecSwc;
	}
	
	/**
	 * Sets the req ilec swc.
	 *
	 * @param reqIlecSwc the new req ilec swc
	 */
	public void setReqIlecSwc(String reqIlecSwc) {
		this.reqIlecSwc = reqIlecSwc;
	}
	
	/**
	 * Gets the contract term.
	 *
	 * @return the contract term
	 */
	public String getContractTerm() {
		return contractTerm;
	}
	
	/**
	 * Sets the contract term.
	 *
	 * @param contractTerm the new contract term
	 */
	public void setContractTerm(String contractTerm) {
		this.contractTerm = contractTerm;
	}
	
	/**
	 * Gets the service.
	 *
	 * @return the service
	 */
	public String getService() {
		return service;
	}
	
	/**
	 * Sets the service.
	 *
	 * @param service the new service
	 */
	public void setService(String service) {
		this.service = service;
	}
	
	/**
	 * Gets the access transport.
	 *
	 * @return the access transport
	 */
	public String getAccessTransport() {
		return accessTransport;
	}
	
	/**
	 * Sets the access transport.
	 *
	 * @param accessTransport the new access transport
	 */
	public void setAccessTransport(String accessTransport) {
		this.accessTransport = accessTransport;
	}
	
	/**
	 * Gets the bandwidth.
	 *
	 * @return the bandwidth
	 */
	public String getBandwidth() {
		return bandwidth;
	}
	
	/**
	 * Sets the bandwidth.
	 *
	 * @param bandwidth the new bandwidth
	 */
	public void setBandwidth(String bandwidth) {
		this.bandwidth = bandwidth;
	}
	
	/**
	 * Gets the access arch.
	 *
	 * @return the access arch
	 */
	public String getAccessArch() {
		return accessArch;
	}
	
	/**
	 * Sets the access arch.
	 *
	 * @param accessArch the new access arch
	 */
	public void setAccessArch(String accessArch) {
		this.accessArch = accessArch;
	}
	
	/**
	 * Gets the physical interface.
	 *
	 * @return the physical interface
	 */
	public String getPhysicalInterface() {
		return physicalInterface;
	}
	
	/**
	 * Sets the physical interface.
	 *
	 * @param physicalInterface the new physical interface
	 */
	public void setPhysicalInterface(String physicalInterface) {
		this.physicalInterface = physicalInterface;
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
	 * Gets the mileage used for pricing.
	 *
	 * @return the mileage used for pricing
	 */
	public String getMileageUsedForPricing() {
		return mileageUsedForPricing;
	}
	
	/**
	 * Sets the mileage used for pricing.
	 *
	 * @param mileageUsedForPricing the new mileage used for pricing
	 */
	public void setMileageUsedForPricing(String mileageUsedForPricing) {
		this.mileageUsedForPricing = mileageUsedForPricing;
	}
	
	/**
	 * Gets the base monthly recurring price.
	 *
	 * @return the base monthly recurring price
	 */
	public String getBaseMonthlyRecurringPrice() {
		return baseMonthlyRecurringPrice;
	}
	
	/**
	 * Sets the base monthly recurring price.
	 *
	 * @param baseMonthlyRecurringPrice the new base monthly recurring price
	 */
	public void setBaseMonthlyRecurringPrice(String baseMonthlyRecurringPrice) {
		this.baseMonthlyRecurringPrice = baseMonthlyRecurringPrice;
	}
	
	/**
	 * Gets the discount percentage.
	 *
	 * @return the discount percentage
	 */
	public String getDiscountPercentage() {
		return discountPercentage;
	}
	
	/**
	 * Sets the discount percentage.
	 *
	 * @param discountPercentage the new discount percentage
	 */
	public void setDiscountPercentage(String discountPercentage) {
		this.discountPercentage = discountPercentage;
	}
	
	/**
	 * Gets the discount monthly recurring price.
	 *
	 * @return the discount monthly recurring price
	 */
	public String getDiscountMonthlyRecurringPrice() {
		return discountMonthlyRecurringPrice;
	}
	
	/**
	 * Sets the discount monthly recurring price.
	 *
	 * @param discountMonthlyRecurringPrice the new discount monthly recurring price
	 */
	public void setDiscountMonthlyRecurringPrice(String discountMonthlyRecurringPrice) {
		this.discountMonthlyRecurringPrice = discountMonthlyRecurringPrice;
	}
	
	/**
	 * Gets the non recurring charge.
	 *
	 * @return the non recurring charge
	 */
	public String getNonRecurringCharge() {
		return nonRecurringCharge;
	}
	
	/**
	 * Sets the non recurring charge.
	 *
	 * @param nonRecurringCharge the new non recurring charge
	 */
	public void setNonRecurringCharge(String nonRecurringCharge) {
		this.nonRecurringCharge = nonRecurringCharge;
	}
	
	/**
	 * Gets the currency.
	 *
	 * @return the currency
	 */
	public String getCurrency() {
		return currency;
	}
	
	/**
	 * Sets the currency.
	 *
	 * @param currency the new currency
	 */
	public void setCurrency(String currency) {
		this.currency = currency;
	}
	
	/**
	 * Gets the seap serial number.
	 *
	 * @return the seap serial number
	 */
	public String getSeapSerialNumber() {
		return seapSerialNumber;
	}
	
	/**
	 * Sets the seap serial number.
	 *
	 * @param seapSerialNumber the new seap serial number
	 */
	public void setSeapSerialNumber(String seapSerialNumber) {
		this.seapSerialNumber = seapSerialNumber;
	}
	
	/**
	 * Gets the eth token.
	 *
	 * @return the eth token
	 */
	public String getEthToken() {
		return ethToken;
	}
	
	/**
	 * Sets the eth token.
	 *
	 * @param ethToken the new eth token
	 */
	public void setEthToken(String ethToken) {
		this.ethToken = ethToken;
	}
	
	/**
	 * Gets the igloo quote ID.
	 *
	 * @return the igloo quote ID
	 */
	public String getIglooQuoteID() {
		return iglooQuoteID;
	}
	
	/**
	 * Sets the igloo quote ID.
	 *
	 * @param iglooQuoteID the new igloo quote ID
	 */
	public void setIglooQuoteID(String iglooQuoteID) {
		this.iglooQuoteID = iglooQuoteID;
	}
	
	/**
	 * Gets the creation date.
	 *
	 * @return the creation date
	 */
	public String getCreationDate() {
		return creationDate;
	}
	
	/**
	 * Sets the creation date.
	 *
	 * @param creationDate the new creation date
	 */
	public void setCreationDate(String creationDate) {
		this.creationDate = creationDate;
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
	 * Gets the expiry date.
	 *
	 * @return the expiry date
	 */
	public String getExpiryDate() {
		return expiryDate;
	}
	
	/**
	 * Sets the expiry date.
	 *
	 * @param expiryDate the new expiry date
	 */
	public void setExpiryDate(String expiryDate) {
		this.expiryDate = expiryDate;
	}
	
	/**
	 * Gets the serialversionuid.
	 *
	 * @return the serialversionuid
	 */
	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	
	/**
	 * Gets the pmtu.
	 *
	 * @return the pmtu
	 */
	public String getPmtu() {
		return pmtu;
	}
	
	/**
	 * Sets the pmtu.
	 *
	 * @param pmtu the new pmtu
	 */
	public void setPmtu(String pmtu) {
		this.pmtu = pmtu;
	}
	
	/**
	 * Gets the mtu support message.
	 *
	 * @return the mtu support message
	 */
	public String getMtuSupportMessage() {
		return mtuSupportMessage;
	}
	
	/**
	 * Sets the mtu support message.
	 *
	 * @param mtuSupportMessage the new mtu support message
	 */
	public void setMtuSupportMessage(String mtuSupportMessage) {
		this.mtuSupportMessage = mtuSupportMessage;
	}
	
	/**
	 * Gets the pref vendors.
	 *
	 * @return the pref vendors
	 */
	public String getPrefVendors() {
		return prefVendors;
	}
	
	/**
	 * Sets the pref vendors.
	 *
	 * @param prefVendors the new pref vendors
	 */
	public void setPrefVendors(String prefVendors) {
		this.prefVendors = prefVendors;
	}
	
	/**
	 * Gets the avoid vendors.
	 *
	 * @return the avoid vendors
	 */
	public String getAvoidVendors() {
		return avoidVendors;
	}
	
	/**
	 * Sets the avoid vendors.
	 *
	 * @param avoidVendors the new avoid vendors
	 */
	public void setAvoidVendors(String avoidVendors) {
		this.avoidVendors = avoidVendors;
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
	 * Gets the quote status.
	 *
	 * @return the quote status
	 */
	public String getQuoteStatus() {
		return quoteStatus;
	}
	
	/**
	 * Sets the quote status.
	 *
	 * @param quoteStatus the new quote status
	 */
	public void setQuoteStatus(String quoteStatus) {
		this.quoteStatus = quoteStatus;
	}
	
	/**
	 * Gets the user comments.
	 *
	 * @return the user comments
	 */
	public String getUserComments() {
		return userComments;
	}
	
	/**
	 * Sets the user comments.
	 *
	 * @param userComments the new user comments
	 */
	public void setUserComments(String userComments) {
		this.userComments = userComments;
	}

	@Override
	public String toString() {
		return "GUIResponse [quoteId=" + quoteId + ", quoteName=" + quoteName + ", errorInd=" + errorInd
				+ ", bulkRequestId=" + bulkRequestId + ", quoteQualification=" + quoteQualification + ", probBuildDesc="
				+ probBuildDesc + ", architectureMet=" + architectureMet + ", bandwidthMet=" + bandwidthMet
				+ ", vendorPreferenceMet=" + vendorPreferenceMet + ", errorMessage=" + errorMessage + ", customerName="
				+ customerName + ", siteRefID=" + siteRefID + ", reqStreetAddress=" + reqStreetAddress + ", reqCity="
				+ reqCity + ", reqState=" + reqState + ", reqZipCode=" + reqZipCode + ", reqCountry=" + reqCountry
				+ ", reqBuilding=" + reqBuilding + ", reqFloor=" + reqFloor + ", reqRoom=" + reqRoom + ", matchScore="
				+ matchScore + ", streetAddress=" + streetAddress + ", city=" + city + ", state=" + state + ", zipCode="
				+ zipCode + ", country=" + country + ", latitude=" + latitude + ", longitude=" + longitude
				+ ", vCoordinate=" + vCoordinate + ", hCoordinate=" + hCoordinate + ", reqContractTerm="
				+ reqContractTerm + ", reqService=" + reqService + ", reqAccessTransport=" + reqAccessTransport
				+ ", reqAccessBandwidth=" + reqAccessBandwidth + ", reqAccessArch=" + reqAccessArch
				+ ", reqPhysicalInterface=" + reqPhysicalInterface + ", reqDiscountPercentage=" + reqDiscountPercentage
				+ ", reqPortLevelCos=" + reqPortLevelCos + ", reqCmtu=" + reqCmtu + ", reqAttEthPop=" + reqAttEthPop
				+ ", reqCircuitQuantity=" + reqCircuitQuantity + ", vendorPreferenceRequested="
				+ vendorPreferenceRequested + ", reqVendor=" + reqVendor + ", reqOnNetBuildingAddress="
				+ reqOnNetBuildingAddress + ", reqNpanxx=" + reqNpanxx + ", reqIlecSwc=" + reqIlecSwc + ", attEthPop="
				+ attEthPop + ", contractTerm=" + contractTerm + ", service=" + service + ", accessTransport="
				+ accessTransport + ", bandwidth=" + bandwidth + ", accessArch=" + accessArch + ", physicalInterface="
				+ physicalInterface + ", technology=" + technology + ", mileageUsedForPricing=" + mileageUsedForPricing
				+ ", baseMonthlyRecurringPrice=" + baseMonthlyRecurringPrice + ", discountPercentage="
				+ discountPercentage + ", discountMonthlyRecurringPrice=" + discountMonthlyRecurringPrice
				+ ", nonRecurringCharge=" + nonRecurringCharge + ", currency=" + currency + ", seapSerialNumber="
				+ seapSerialNumber + ", ethToken=" + ethToken + ", iglooQuoteID=" + iglooQuoteID + ", creationDate="
				+ creationDate + ", daysUntilQuoteExpires=" + daysUntilQuoteExpires + ", expiryDate=" + expiryDate
				+ ", cmtu=" + cmtu + ", pmtu=" + pmtu + ", mtuSupportMessage=" + mtuSupportMessage + ", prefVendors="
				+ prefVendors + ", avoidVendors=" + avoidVendors + ", rulesDerivedMessage=" + rulesDerivedMessage
				+ ", quoteStatus=" + quoteStatus + ", userComments=" + userComments + ", subGrouplocDetails="
				+ subGrouplocDetails + ", siteId=" + siteId + ", swclli=" + swclli + ", diversityId=" + diversityId
				+ ", diversityOptions=" + diversityOptions + ", diversityGrouping=" + diversityGrouping
				+ ", diversityOrderType=" + diversityOrderType + ", diversityVendorType=" + diversityVendorType
				+ ", diversityChangeOrder=" + diversityChangeOrder + ", splConstructionCharge=" + splConstructionCharge
				+ ", quoteType=" + quoteType + ", zone=" + zone + "]";
	}
	
	
	
}
