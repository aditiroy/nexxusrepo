package com.att.sales.nexxus.dao.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import com.att.sales.nexxus.dao.repository.NxOutputBeanJsonType;
import com.att.sales.nexxus.output.entity.NxOutputBean;

/**
 * The Class NxAccessPricingData.
 */
@Entity
@Table(name = "NX_ACCESS_PRICING_DATA")
public class NxAccessPricingData {

	/** The nx access price id. */
	@Id
	@SequenceGenerator(name = "sequence_nx_access_pricing_data", sequenceName = "SEQ_NX_ACCESS_PRICE_ID", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequence_nx_access_pricing_data")

	@Column(name = "NX_ACCESS_PRICE_ID")
	private Long nxAccessPriceId;

	// private String iglooQuoteID;

	/** The eth token. */
	@Column(name = "TOKEN_ID")
	private String ethToken;

	/** The igloo quote id. */
	@Column(name = "QUOTE_ID")
	private String iglooQuoteId;

	/** The req city. */
	@Column(name = "CITY")
	private String reqCity;

	/** The req state. */
	@Column(name = "STATE")
	private String reqState;

	/** The postal code. */
	@Column(name = "POSTALCODE")
	private String postalCode;

 	/** The cust postalcode. */
	@Column(name = "REQZIPCODE")
 	private String reqZipCode;
	
	/** The req country. */
	@Column(name = "COUNTRY")
	private String reqCountry;

	/** The street address. */
	@Column(name = "ADDRESS1")
	private String streetAddress;

	/** The street address. */
	@Column(name = "REQSTREETADDRESS")
	private String reqStreetAddress;
	
	/** The req floor. */
	@Column(name = "FLOOR")
	private String reqFloor;

	/** The req room. */
	@Column(name = "ROOM")
	private String reqRoom;

	/** The req building. */
	@Column(name = "BUILDING")
	private String reqBuilding;

	/** The mrc. */
	@Column(name = "MRC")
	private String mrc;

	/** The nrc. */
	@Column(name = "NRC")
	private String nrc;

	/** The speed. */
	@Column(name = "SPEED")
	private String speed;

	/** The nx solution id. */
	@Column(name = "NX_SOLUTION_ID")
	private Long nxSolutionId;

	/** The req contract term. */
	@Column(name = "CONTRACT_TERM")
	private String reqContractTerm;

	/** The req access arch. */
	@Column(name = "ACCESS_ARCHITECTURE")
	private String reqAccessArch;

	/** The req physical interface. */
	@Column(name = "PHYSICAL_INTERFACE")
	private String reqPhysicalInterface;

	/** The att eth pop. */
	@Column(name = "POP_CLLI")
	private String attEthPop;

	/** The mow pop. */
	@Column(name = "MOW_POP_CLLI_ID")
	private String mowPop;

	/** The ethernet. */
	@Column(name = "ETHERNET_QUOTE_IND")
	private String ethernet;

	/** The technology. */
	@Column(name = "INTERCONNECT_TECHNOLOGY")
	private String technology;

	/** The tail technology. */
	@Column(name = "TAIL_TECHNOLOGY")
	private String tailTechnology;

	/** The supplier name. */
	@Column(name = "SUPPLIER_NAME")
	private String supplierName;

	/** The access type. */
	@Column(name = "ACCESS_TYPE")
	private String accessType;

	/** The max mrc discount. */
	@Column(name = "MAX_MRC_DISCOUNT")
	private String maxMrcDiscount;

	/** The max nrc discount. */
	@Column(name = "MAX_NRC_DISCOUNT")
	private String maxNrcDiscount;

	/** The mrc list rate. */
	@Column(name = "MRC_LIST_RATE")
	private String mrcListRate;

	/** The gse pricing. */
	@Column(name = "GSE_PRICING")
	private String gsePricing;

	/** The nrc list rate. */
	@Column(name = "NRC_LIST_RATE")
	private String nrcListRate;

	/** The rate id. */
	@Column(name = "RATE_ID")
	private String rateId;

	/** The ext rate id. */
	@Column(name = "EXT_RATE_ID")
	private String extRateId;

	/** The price scenario id. */
	@Column(name = "PRICE_SCENARIO_ID")
	private String priceScenarioId;

	/** The aqstatus ind. */
	@Column(name = "AQ_STATUS_IND")
	private String aqstatusInd;

	/** The icb appr mrc dsc. */
	@Column(name = "ICB_APPR_MRC_DSC")
	private String icbApprMrcDsc;

	/** The icb appr nrc dsc. */
	@Column(name = "ICB_APPR_NRC_DSC")
	private String icbApprNrcDsc;

	/** The icb applied yn. */
	@Column(name = "ICB_APPLIED_YN")
	private String icbAppliedYn;

	/** The icb mrcfloor rate. */
	@Column(name = "ICB_MRC_FLOOR_RATE")
	private String icbMrcfloorRate;

	/** The icb nrcfloor rate. */
	@Column(name = "ICB_NRC_FLOOR_RATE")
	private String icbNrcfloorRate;

	/** The frequency. */
	@Column(name = "FREQUENCY")
	private String frequency;

	/** The strata. */
	@Column(name = "MARKET_STRATA")
	private String strata;

	/** The location yn. */
	@Column(name = "LOCATION_YN")
	private String locationYn;

	/** The include yn. */
	@Column(name = "INCLUDE_YN")
	private String includeYn;

	/** The intermediate json. */
	@Column(name = "INTERMEDIATE_JSON")
	private String intermediateJson;

	/** The output json. */
	@Column(name = "OUTPUT_JSON")
	@Convert(converter = NxOutputBeanJsonType.class)
	private NxOutputBean outputJson = new NxOutputBean();

 	/** The cust addr 1. */
	@Column(name = "CUSTADDR1")
 	private String custAddr1;
	  
 	/** The cust city. */
	@Column(name = "CUSTCITY")
 	private String custCity;
	 
	/** The cust state. */
	@Column(name = "CUSTSTATE")
 	private String custState;
	 
 	/** The cust country. */
	@Column(name = "CUSTCOUNTRY")
 	private String custCountry;
	
 	/** The cust postalcode. */
	@Column(name = "CUSTPOSTALCODE")
 	private String custPostalcode;
	 
	/** The clli. */
	@Column(name = "CLLI")
	private String clli;
	
	/** The node name. */
	@Column(name = "NODENAME")
	private String nodeName;
	
	/** The service. */
	@Column(name = "SERVICE")
 	private String service;
	
 	/** The access bandwidth. */
	@Column(name = "ACCESSBANDWIDTH")
 	private Integer accessBandwidth;
	
	/** The currency. */
	@Column(name = "CURRENCY")
	private String currency;
	
	/** The alternate currency. */
	@Column(name = "ALT_CURRENCY")
	private String alternateCurrency;
 	
	/** The currency. */
	@Column(name = "SITE_REF_ID")
	private String siteRefId;
	
	@Column(name = "MP_STATUS")
	private String mpStatus;
	
	/** The consolidation criteria */
	@Column(name = "CONSOLIDATION_CRITERIA")
	private String consolidation_criteria;
	
	@Column(name="REST_RESPONSE_ERROR")
	private String restResponseError;

	/** The vendor zone code */
	@Column(name="VENDOR_ZONE_CODE")
	private String vendorZoneCode;
	
	/** The special construction charge */
	@Column(name="SPL_CONSTRUCTION_CHARGES")
	private String splConstructionCharges;

	/** The quote type */
	@Column(name="QUOTE_TYPE")
	private String quoteType;

	/** The circuit id */
	@Column(name="CIRCUIT_ID")
	private String circuitId;

	@Column(name="NX_SITE_ID")
	private Long nxSiteId;
	
	/** The created date. */
	@Column(name="CREATED_DATE")
	private Date createdDate = new Date();

	@Column(name="Ethernet_LC_MRC_Cost")
	private String ethernetLcMonthlyRecurringCost; 
	@Column(name="Ethernet_LC_MRC_POP_Cost")
	private String ethernetLcPopMonthlyRecurringCost; 
	@Column(name="Sp_Construction_MRC_Cost")
	private String splConstructionCostMRC; 
	@Column(name="Ethernet_LC_NRC_Cost")
	private String ethernetLcNonRecurringCost; 
	@Column(name="Ethernet_LC_POP_NRC_Cost")
	private String ethernetLcPopNonRecurringCost; 
	@Column(name="Sp_Construction_NRC_Cost")
	private String splConstructionCostNRC; 
	
	@Column(name="HAS_REQUIRED_FIELDS")
	private String hasRequiredFields;

	@Column(name="REQUIRED_FIELD_ERROR")
	private String requiredFieldError;
	
	
	
	public Date getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}

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

	public String getVendorZoneCode() {
		return vendorZoneCode;
	}

	public void setVendorZoneCode(String vendorZoneCode) {
		this.vendorZoneCode = vendorZoneCode;
	}

	public String getSplConstructionCharges() {
		return splConstructionCharges;
	}

	public void setSplConstructionCharges(String splConstructionCharges) {
		this.splConstructionCharges = splConstructionCharges;
	}

	public String getQuoteType() {
		return quoteType;
	}

	public void setQuoteType(String quoteType) {
		this.quoteType = quoteType;
	}

	public String getCircuitId() {
		return circuitId;
	}

	public void setCircuitId(String circuitId) {
		this.circuitId = circuitId;
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
	 * Gets the req city.
	 *
	 * @return the req city
	 */
	public String getReqCity() {
		return reqCity;
	}

	/**
	 * Gets the nx access price id.
	 *
	 * @return the nx access price id
	 */
	public Long getNxAccessPriceId() {
		return nxAccessPriceId;
	}

	/**
	 * Sets the nx access price id.
	 *
	 * @param nxAccessPriceId the new nx access price id
	 */
	public void setNxAccessPriceId(Long nxAccessPriceId) {
		this.nxAccessPriceId = nxAccessPriceId;
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
	 * Gets the speed.
	 *
	 * @return the speed
	 */
	public String getSpeed() {
		return speed;
	}

	/**
	 * Sets the speed.
	 *
	 * @param speed the new speed
	 */
	public void setSpeed(String speed) {
		this.speed = speed;
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
	 * Gets the mow pop.
	 *
	 * @return the mow pop
	 */
	public String getMowPop() {
		return mowPop;
	}

	/**
	 * Sets the mow pop.
	 *
	 * @param mowPop the new mow pop
	 */
	public void setMowPop(String mowPop) {
		this.mowPop = mowPop;
	}

	/**
	 * Gets the ethernet.
	 *
	 * @return the ethernet
	 */
	public String getEthernet() {
		return ethernet;
	}

	/**
	 * Sets the ethernet.
	 *
	 * @param ethernet the new ethernet
	 */
	public void setEthernet(String ethernet) {
		this.ethernet = ethernet;
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
	 * Gets the tail technology.
	 *
	 * @return the tail technology
	 */
	public String getTailTechnology() {
		return tailTechnology;
	}

	/**
	 * Sets the tail technology.
	 *
	 * @param tailTechnology the new tail technology
	 */
	public void setTailTechnology(String tailTechnology) {
		this.tailTechnology = tailTechnology;
	}

	/**
	 * Gets the supplier name.
	 *
	 * @return the supplier name
	 */
	public String getSupplierName() {
		return supplierName;
	}

	/**
	 * Sets the supplier name.
	 *
	 * @param supplierName the new supplier name
	 */
	public void setSupplierName(String supplierName) {
		this.supplierName = supplierName;
	}

	/**
	 * Gets the access type.
	 *
	 * @return the access type
	 */
	public String getAccessType() {
		return accessType;
	}

	/**
	 * Sets the access type.
	 *
	 * @param accessType the new access type
	 */
	public void setAccessType(String accessType) {
		this.accessType = accessType;
	}

	/**
	 * Gets the max mrc discount.
	 *
	 * @return the max mrc discount
	 */
	public String getMaxMrcDiscount() {
		return maxMrcDiscount;
	}

	/**
	 * Sets the max mrc discount.
	 *
	 * @param maxMrcDiscount the new max mrc discount
	 */
	public void setMaxMrcDiscount(String maxMrcDiscount) {
		this.maxMrcDiscount = maxMrcDiscount;
	}

	/**
	 * Gets the max nrc discount.
	 *
	 * @return the max nrc discount
	 */
	public String getMaxNrcDiscount() {
		return maxNrcDiscount;
	}

	/**
	 * Sets the max nrc discount.
	 *
	 * @param maxNrcDiscount the new max nrc discount
	 */
	public void setMaxNrcDiscount(String maxNrcDiscount) {
		this.maxNrcDiscount = maxNrcDiscount;
	}

	/**
	 * Gets the mrc list rate.
	 *
	 * @return the mrc list rate
	 */
	public String getMrcListRate() {
		return mrcListRate;
	}

	/**
	 * Sets the mrc list rate.
	 *
	 * @param mrcListRate the new mrc list rate
	 */
	public void setMrcListRate(String mrcListRate) {
		this.mrcListRate = mrcListRate;
	}

	/**
	 * Gets the gse pricing.
	 *
	 * @return the gse pricing
	 */
	public String getGsePricing() {
		return gsePricing;
	}

	/**
	 * Sets the gse pricing.
	 *
	 * @param gsePricing the new gse pricing
	 */
	public void setGsePricing(String gsePricing) {
		this.gsePricing = gsePricing;
	}

	/**
	 * Gets the nrc list rate.
	 *
	 * @return the nrc list rate
	 */
	public String getNrcListRate() {
		return nrcListRate;
	}

	/**
	 * Sets the nrc list rate.
	 *
	 * @param nrcListRate the new nrc list rate
	 */
	public void setNrcListRate(String nrcListRate) {
		this.nrcListRate = nrcListRate;
	}

	/**
	 * Gets the rate id.
	 *
	 * @return the rate id
	 */
	public String getRateId() {
		return rateId;
	}

	/**
	 * Sets the rate id.
	 *
	 * @param rateId the new rate id
	 */
	public void setRateId(String rateId) {
		this.rateId = rateId;
	}

	/**
	 * Gets the ext rate id.
	 *
	 * @return the ext rate id
	 */
	public String getExtRateId() {
		return extRateId;
	}

	/**
	 * Sets the ext rate id.
	 *
	 * @param extRateId the new ext rate id
	 */
	public void setExtRateId(String extRateId) {
		this.extRateId = extRateId;
	}

	/**
	 * Gets the price scenario id.
	 *
	 * @return the price scenario id
	 */
	public String getPriceScenarioId() {
		return priceScenarioId;
	}

	/**
	 * Sets the price scenario id.
	 *
	 * @param priceScenarioId the new price scenario id
	 */
	public void setPriceScenarioId(String priceScenarioId) {
		this.priceScenarioId = priceScenarioId;
	}

	/**
	 * Gets the aqstatus ind.
	 *
	 * @return the aqstatus ind
	 */
	public String getAqstatusInd() {
		return aqstatusInd;
	}

	/**
	 * Sets the aqstatus ind.
	 *
	 * @param aqstatusInd the new aqstatus ind
	 */
	public void setAqstatusInd(String aqstatusInd) {
		this.aqstatusInd = aqstatusInd;
	}

	/**
	 * Gets the icb appr mrc dsc.
	 *
	 * @return the icb appr mrc dsc
	 */
	public String getIcbApprMrcDsc() {
		return icbApprMrcDsc;
	}

	/**
	 * Sets the icb appr mrc dsc.
	 *
	 * @param icbApprMrcDsc the new icb appr mrc dsc
	 */
	public void setIcbApprMrcDsc(String icbApprMrcDsc) {
		this.icbApprMrcDsc = icbApprMrcDsc;
	}

	/**
	 * Gets the icb appr nrc dsc.
	 *
	 * @return the icb appr nrc dsc
	 */
	public String getIcbApprNrcDsc() {
		return icbApprNrcDsc;
	}

	/**
	 * Sets the icb appr nrc dsc.
	 *
	 * @param icbApprNrcDsc the new icb appr nrc dsc
	 */
	public void setIcbApprNrcDsc(String icbApprNrcDsc) {
		this.icbApprNrcDsc = icbApprNrcDsc;
	}

	/**
	 * Gets the icb applied yn.
	 *
	 * @return the icb applied yn
	 */
	public String getIcbAppliedYn() {
		return icbAppliedYn;
	}

	/**
	 * Sets the icb applied yn.
	 *
	 * @param icbAppliedYn the new icb applied yn
	 */
	public void setIcbAppliedYn(String icbAppliedYn) {
		this.icbAppliedYn = icbAppliedYn;
	}

	/**
	 * Gets the icb mrcfloor rate.
	 *
	 * @return the icb mrcfloor rate
	 */
	public String getIcbMrcfloorRate() {
		return icbMrcfloorRate;
	}

	/**
	 * Sets the icb mrcfloor rate.
	 *
	 * @param icbMrcfloorRate the new icb mrcfloor rate
	 */
	public void setIcbMrcfloorRate(String icbMrcfloorRate) {
		this.icbMrcfloorRate = icbMrcfloorRate;
	}

	/**
	 * Gets the icb nrcfloor rate.
	 *
	 * @return the icb nrcfloor rate
	 */
	public String getIcbNrcfloorRate() {
		return icbNrcfloorRate;
	}

	/**
	 * Sets the icb nrcfloor rate.
	 *
	 * @param icbNrcfloorRate the new icb nrcfloor rate
	 */
	public void setIcbNrcfloorRate(String icbNrcfloorRate) {
		this.icbNrcfloorRate = icbNrcfloorRate;
	}

	/**
	 * Gets the frequency.
	 *
	 * @return the frequency
	 */
	public String getFrequency() {
		return frequency;
	}

	/**
	 * Sets the frequency.
	 *
	 * @param frequency the new frequency
	 */
	public void setFrequency(String frequency) {
		this.frequency = frequency;
	}

	/**
	 * Gets the strata.
	 *
	 * @return the strata
	 */
	public String getStrata() {
		return strata;
	}

	/**
	 * Sets the strata.
	 *
	 * @param strata the new strata
	 */
	public void setStrata(String strata) {
		this.strata = strata;
	}

	/**
	 * Gets the igloo quote id.
	 *
	 * @return the igloo quote id
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
	 * Gets the nx solution id.
	 *
	 * @return the nx solution id
	 */
	public Long getNxSolutionId() {
		return nxSolutionId;
	}

	/**
	 * Sets the nx solution id.
	 *
	 * @param nxSolutionId the new nx solution id
	 */
	public void setNxSolutionId(Long nxSolutionId) {
		this.nxSolutionId = nxSolutionId;
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

	/**
	 * Gets the intermediate json.
	 *
	 * @return the intermediate json
	 */
	public String getIntermediateJson() {
		return intermediateJson;
	}

	/**
	 * Sets the intermediate json.
	 *
	 * @param intermediateJson the new intermediate json
	 */
	public void setIntermediateJson(String intermediateJson) {
		this.intermediateJson = intermediateJson;
	}

	/**
	 * Gets the output json.
	 *
	 * @return the output json
	 */
	public NxOutputBean getOutputJson() {
		return outputJson;
	}

	/**
	 * Sets the output json.
	 *
	 * @param outputJson the new output json
	 */
	public void setOutputJson(NxOutputBean outputJson) {
		this.outputJson = outputJson;
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

	public String getReqZipCode() {
		return reqZipCode;
	}

	public void setReqZipCode(String reqZipCode) {
		this.reqZipCode = reqZipCode;
	}

	public String getReqStreetAddress() {
		return reqStreetAddress;
	}

	public void setReqStreetAddress(String reqStreetAddress) {
		this.reqStreetAddress = reqStreetAddress;
	}

	public String getCurrency() {
		return currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}

	public String getSiteRefId() {
		return siteRefId;
	}

	public void setSiteRefId(String siteRefId) {
		this.siteRefId = siteRefId;
	}

	/**
	 * @return the mpStatus
	 */
	public String getMpStatus() {
		return mpStatus;
	}

	/**
	 * @param mpStatus the mpStatus to set
	 */
	public void setMpStatus(String mpStatus) {
		this.mpStatus = mpStatus;
	}

	public String getAlternateCurrency() {
		return alternateCurrency;
	}

	public void setAlternateCurrency(String alternateCurrency) {
		this.alternateCurrency = alternateCurrency;
	}
	/**
	 * Gets the consolidation criteria .
	 *
	 * @return the consolidation criteria
	 */

	public String getConsolidation_criteria() {
		return consolidation_criteria;
	}

	public void setConsolidation_criteria(String consolidation_criteria) {
		this.consolidation_criteria = consolidation_criteria;
	}

	public String getRestResponseError() {
		return restResponseError;
	}

	public void setRestResponseError(String restResponseError) {
		this.restResponseError = restResponseError;
	}

	public Long getNxSiteId() {
		return nxSiteId;
	}

	public void setNxSiteId(Long nxSiteId) {
		this.nxSiteId = nxSiteId;
	}
	
	
	public String getHasRequiredFields() {
		return hasRequiredFields;
	}

	public void setHasRequiredFields(String hasRequiredFields) {
		this.hasRequiredFields = hasRequiredFields;
	}
	
	
	public String getRequiredFieldError() {
		return requiredFieldError;
	}

	public void setRequiredFieldError(String requiredFieldError) {
		this.requiredFieldError = requiredFieldError;
	}
	
}