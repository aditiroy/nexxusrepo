package com.att.sales.nexxus.accesspricing.model;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.map.annotate.JsonSerialize;


/**
 * The Class AccessPriceUIdetails.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class AccessPriceUIdetails {

	/** The port id. */
	private Long portId;
	
	/** The eth rates id. */
	private Long ethRatesId;
	
	/** The token id. */
	private String tokenId;
	
	/** The mrc. */
	private BigDecimal mrc;
	
	/** The nrc. */
	private BigDecimal nrc;
	
	/** The speed. */
	private String speed;
	
	/** The contract term. */
	private String contractTerm;
	
	/** The ethernet quote ind. */
	private String ethernetQuoteInd;
	
	/** The expiration date. */
	private Date expirationDate;
	
	/** The max mrc discount. */
	private Double maxMrcDiscount;
	
	/** The max nrc discount. */
	private Double maxNrcDiscount;
	
	/** The mrc list rate. */
	private Double mrcListRate;
	
	/** The nrc list rate. */
	private Double nrcListRate;
	
	/** The service guide. */
	private String serviceGuide;
	
	/** The igloo max mrc discount. */
	private Double iglooMaxMrcDiscount;
	
	/** The igloo mrc list rate. */
	private Double iglooMrcListRate;
	
	/** The igloo mrc net rate. */
	private Double iglooMrcNetRate;
	
	/** The igloo service guide. */
	private String iglooServiceGuide;
	
	/** The aq status ind. */
	private String aqStatusInd;
	
	/** The quote id. */
	private String quoteId;
	
	/** The gse pricing. */
	private String gsePricing;
	
	/** The current ps. */
	private String currentPs;
	
	/** The icb appr mrc dsc. */
	private BigDecimal icbApprMrcDsc;
	
	/** The icb appr nrc dsc. */
	private BigDecimal icbApprNrcDsc;
	
	/** The icb appliedyn. */
	private String icbAppliedyn;
	
	/** The icb mrc floor rate. */
	private BigDecimal icbMrcFloorRate;
	
	/** The icb nrc floorr rte. */
	private BigDecimal icbNrcFloorrRte;
	
	/** The igloo sse access expiry date. */
	private Date iglooSseAccessExpiryDate;
	
	/** The is savedyn. */
	private String isSavedyn;
	
	/** The extended date YN. */
	private String extendedDateYN;
	
	/** The quote req date. */
	private Date quoteReqDate;
	
	/** The eth zone value. */
	private String ethZoneValue;
	
	/** The is contract impactyn. */
	private String isContractImpactyn;
	
	/** The changedyn. */
	private String changedyn;
	
	/** The nrc list igloo. */
	private Double nrcListIgloo;
	
	/** The mow access quote info id. */
	private Long mowAccessQuoteInfoId;
	
	/** The request id. */
	private Long requestId;
	
	/** The location id. */
	private Long locationId;
	
	/** The dqid. */
	private String dqid;
	
	/** The resp pop clli. */
	private String respPopClli;
	
	/** The resp pop address. */
	private String respPopAddress;
	
	/** The resp speed. */
	private Long respSpeed;
	
	/** The service availability date. */
	private Date serviceAvailabilityDate;
	
	/** The caveat ids. */
	private String caveatIds;
	
	/** The resp itu carrier id. */
	private String respItuCarrierId;
	
	/** The resp supplier name. */
	private String respSupplierName;
	
	/** The resp supplier service. */
	private String respSupplierService;
	
	/** The resp pop node name. */
	private String respPopNodeName;
	
	/** The mrc local. */
	private BigDecimal mrcLocal;
	
	/** The nrc local. */
	private BigDecimal nrcLocal;
	
	/** The local currency code. */
	private String localCurrencyCode;
	
	/** The mrc usd. */
	private BigDecimal mrcUsd;
	
	/** The nrc usd. */
	private BigDecimal nrcUsd;
	
	/** The supplier tier. */
	private String supplierTier;
	
	/** The coverage ind. */
	private String coverageInd;
	
	/** The serial number. */
	private String serialNumber;
	
	/** The dq expiration date. */
	private Date dqExpirationDate;
	
	/** The feasibility reference number. */
	private String feasibilityReferenceNumber;
	
	/** The feasibility expirationdate. */
	private Date feasibilityExpirationdate;
	
	/** The resp access interconnect. */
	private Long respAccessInterconnect;
	
	/** The resp tail technology. */
	private Long respTailTechnology;
	
	/** The resp provider product code. */
	private String respProviderProductCode;
	
	/** The resp provider product name. */
	private String respProviderProductName;
	
	/** The resp upstream speed. */
	private Long respUpstreamSpeed;
	
	/** The resp downstream speed. */
	private Long respDownstreamSpeed;
	
	/** The resp ordering code. */
	private String respOrderingCode;
	
	/** The resp egr flag. */
	private String respEgrFlag;
	
	/** The resp lead time. */
	private Long respLeadTime;
	
	/** The resp best price. */
	private String respBestPrice;
	
	/** The user selectedyn. */
	private String userSelectedyn;
	
	/** The coverage check status. */
	private String coverageCheckStatus;
	
	/** The att comments. */
	private String attComments;
	
	/** The igloo quote call status. */
	private String iglooQuoteCallStatus;
	
	/** The selected interface id. */
	private BigDecimal selectedInterfaceId;
	
	/** The quote type. */
	private String quoteType;
	
	/** The aq quoteinfo id. */
	private BigDecimal aqQuoteinfoId;
	
	/** The service delivery type. */
	private String serviceDeliveryType;
	
	/** The service delivery speed. */
	private Long serviceDeliverySpeed;
	
	/** The circuit protection. */
	private Long circuitProtection;
	
	/** The t 3 framing. */
	private Long t3Framing;
	
	/** The soryn. */
	private String soryn;
	
	/** The resp access type. */
	private Long respAccessType;
	
	/** The resp notes. */
	private String respNotes;
	
	/** The carrier type. */
	private String carrierType;
	
	/** The customer tagging. */
	private BigDecimal customerTagging;
	
	/** The mow popclli id. */
	private String mowPopclliId;
	
	/** The pop address. */
	private String popAddress;
	
	/** The node name. */
	private String nodeName;
	
	/** The access architecture. */
	private Long accessArchitecture;
	
	/** The access provider yn. */
	private String accessProviderYn;
	
	/** The access type. */
	private String accessType;
	
	/** The aq term upd status ind. */
	private String aqTermUpdStatusInd;
	
	/** The ddq status ind. */
	private String ddqStatusInd;
	
	/** The desired discount. */
	private Double desiredDiscount;
	
	/** The dff status ind. */
	private String dffStatusInd;
	
	/** The eth design update ind. */
	private String ethDesignUpdateInd;
	
	/** The global location id. */
	private String globalLocationId;
	
	/** The igloo term id. */
	private Long iglooTermId;
	
	/** The lns clec str id. */
	private String lnsClecStrId;
	
	/** The interconnect technology. */
	private Long interconnectTechnology;
	
	/** The isc base. */
	private String iscBase;
	
	/** The market strata. */
	private String marketStrata;
	
	/** The mis type. */
	private Long misType;
	
	/** The mtu supp msgs. */
	private String mtuSuppMsgs;
	
	/** The npanxx. */
	private String npanxx;
	
	/** The on net check. */
	private String onNetCheck;
	
	/** The physical interface. */
	private String physicalInterface;
	
	/** The pop clli. */
	private String popClli;
	
	/** The provider product name. */
	private String providerProductName;
	
	/** The soc. */
	private String soc;
	
	/** The spec code. */
	private String specCode;
	
	/** The speed token id. */
	private String speedTokenId;
	
	/** The supplier name. */
	private String supplierName;
	
	/** The tail technology. */
	private Long tailTechnology;
	
	/** The uso number. */
	private String usoNumber;
	
	/** The price scenario id. */
	private Long priceScenarioId;
	
	/** The access carrier note. */
	private String accessCarrierNote;
	
	/** The access diversity. */
	private String accessDiversity;
	
	/** The country config. */
	private String countryConfig;
	
	/** The dsl ordering code. */
	private String dslOrderingCode;
	
	/** The has multicast yn. */
	private String hasMulticastYn;
	
	/** The has vlan stacking. */
	private String hasVlanStacking;
	
	/** The ig electrical interface. */
	private Long igElectricalInterface;
	
	/** The ig physical connector. */
	private Long igPhysicalConnector;
	
	/** The igloo error msg. */
	private String iglooErrorMsg;
	
	/** The itu map. */
	private String ituMap;
	
	/** The mow AQ status ind. */
	private String mowAQStatusInd;
	
	/** The mow CC status ind. */
	private String mowCCStatusInd;
	
	/** The mow DQ status ind. */
	private String mowDQStatusInd;
	
	/** The pop error msg. */
	private String popErrorMsg;
	
	/** The pop ret YN. */
	private String popRetYN;
	
	/** The ppn base. */
	private String ppnBase;
	
	/** The site id. */
	private Long siteId;
	
	/** The technology. */
	private BigDecimal technology;
	
	/** The total num vlan. */
	private BigDecimal totalNumVlan;
	
	/** The vlan AQDQ status. */
	private String vlanAQDQStatus;
	
	/** The vlan locked yn. */
	private String vlanLockedYn;
	
	/** The vlan tag control. */
	private String vlanTagControl;
	
	/** The selected quote info id. */
	private Long selectedQuoteInfoId;
	
	/** The selected pop. */
	private Long selectedPop;
	
	/** The external key id. */
	private String externalKeyId;
	
	/** The kafka event type. */
	private String kafkaEventType;
	
	/** The target currency code. */
	private String targetCurrencyCode;
	
	/** The check aq dq status. */
	private String checkAqDqStatus;
	
	/** The zone desc. */
	private String zoneDesc;
	
	/** The category. */
	private String category;
	
	/** The interface list. */
	private List<Object> interfaceList;
	
	/** The pricing status msg. */
	private String pricingStatusMsg;
	
	/** The offer. */
	private String offer;

	/**
	 * Gets the category.
	 *
	 * @return the category
	 */
	public String getCategory() {
		return category;
	}

	/**
	 * Sets the category.
	 *
	 * @param category the new category
	 */
	public void setCategory(String category) {
		this.category = category;
	}

	/**
	 * Gets the zone desc.
	 *
	 * @return the zone desc
	 */
	public String getZoneDesc() {
		return zoneDesc;
	}

	/**
	 * Sets the zone desc.
	 *
	 * @param zoneDesc the new zone desc
	 */
	public void setZoneDesc(String zoneDesc) {
		this.zoneDesc = zoneDesc;
	}

	/**
	 * Gets the check aq dq status.
	 *
	 * @return the check aq dq status
	 */
	public String getCheckAqDqStatus() {
		return checkAqDqStatus;
	}

	/**
	 * Sets the check aq dq status.
	 *
	 * @param checkAqDqStatus the new check aq dq status
	 */
	public void setCheckAqDqStatus(String checkAqDqStatus) {
		this.checkAqDqStatus = checkAqDqStatus;
	}

	/**
	 * Gets the target currency code.
	 *
	 * @return the target currency code
	 */
	public String getTargetCurrencyCode() {
		return targetCurrencyCode;
	}

	/**
	 * Sets the target currency code.
	 *
	 * @param targetCurrencyCode the new target currency code
	 */
	public void setTargetCurrencyCode(String targetCurrencyCode) {
		this.targetCurrencyCode = targetCurrencyCode;
	}

	/**
	 * Gets the kafka event type.
	 *
	 * @return the kafka event type
	 */
	public String getKafkaEventType() {
		return kafkaEventType;
	}

	/**
	 * Sets the kafka event type.
	 *
	 * @param kafkaEventType the new kafka event type
	 */
	public void setKafkaEventType(String kafkaEventType) {
		this.kafkaEventType = kafkaEventType;
	}

	/**
	 * Gets the access carrier note.
	 *
	 * @return the access carrier note
	 */
	public String getAccessCarrierNote() {
		return accessCarrierNote;
	}

	/**
	 * Sets the access carrier note.
	 *
	 * @param accessCarrierNote the new access carrier note
	 */
	public void setAccessCarrierNote(String accessCarrierNote) {
		this.accessCarrierNote = accessCarrierNote;
	}

	/**
	 * Gets the access diversity.
	 *
	 * @return the access diversity
	 */
	public String getAccessDiversity() {
		return accessDiversity;
	}

	/**
	 * Sets the access diversity.
	 *
	 * @param accessDiversity the new access diversity
	 */
	public void setAccessDiversity(String accessDiversity) {
		this.accessDiversity = accessDiversity;
	}

	/**
	 * Gets the country config.
	 *
	 * @return the country config
	 */
	public String getCountryConfig() {
		return countryConfig;
	}

	/**
	 * Sets the country config.
	 *
	 * @param countryConfig the new country config
	 */
	public void setCountryConfig(String countryConfig) {
		this.countryConfig = countryConfig;
	}

	/**
	 * Gets the dsl ordering code.
	 *
	 * @return the dsl ordering code
	 */
	public String getDslOrderingCode() {
		return dslOrderingCode;
	}

	/**
	 * Sets the dsl ordering code.
	 *
	 * @param dslOrderingCode the new dsl ordering code
	 */
	public void setDslOrderingCode(String dslOrderingCode) {
		this.dslOrderingCode = dslOrderingCode;
	}

	/**
	 * Gets the checks for multicast yn.
	 *
	 * @return the checks for multicast yn
	 */
	public String getHasMulticastYn() {
		return hasMulticastYn;
	}

	/**
	 * Sets the checks for multicast yn.
	 *
	 * @param hasMulticastYn the new checks for multicast yn
	 */
	public void setHasMulticastYn(String hasMulticastYn) {
		this.hasMulticastYn = hasMulticastYn;
	}

	/**
	 * Gets the checks for vlan stacking.
	 *
	 * @return the checks for vlan stacking
	 */
	public String getHasVlanStacking() {
		return hasVlanStacking;
	}

	/**
	 * Sets the checks for vlan stacking.
	 *
	 * @param hasVlanStacking the new checks for vlan stacking
	 */
	public void setHasVlanStacking(String hasVlanStacking) {
		this.hasVlanStacking = hasVlanStacking;
	}

	/**
	 * Gets the ig electrical interface.
	 *
	 * @return the ig electrical interface
	 */
	public Long getIgElectricalInterface() {
		return igElectricalInterface;
	}

	/**
	 * Sets the ig electrical interface.
	 *
	 * @param igElectricalInterface the new ig electrical interface
	 */
	public void setIgElectricalInterface(Long igElectricalInterface) {
		this.igElectricalInterface = igElectricalInterface;
	}

	/**
	 * Gets the ig physical connector.
	 *
	 * @return the ig physical connector
	 */
	public Long getIgPhysicalConnector() {
		return igPhysicalConnector;
	}

	/**
	 * Sets the ig physical connector.
	 *
	 * @param igPhysicalConnector the new ig physical connector
	 */
	public void setIgPhysicalConnector(Long igPhysicalConnector) {
		this.igPhysicalConnector = igPhysicalConnector;
	}

	/**
	 * Gets the itu map.
	 *
	 * @return the itu map
	 */
	public String getItuMap() {
		return ituMap;
	}

	/**
	 * Sets the itu map.
	 *
	 * @param ituMap the new itu map
	 */
	public void setItuMap(String ituMap) {
		this.ituMap = ituMap;
	}

	/**
	 * Gets the pop error msg.
	 *
	 * @return the pop error msg
	 */
	public String getPopErrorMsg() {
		return popErrorMsg;
	}

	/**
	 * Sets the pop error msg.
	 *
	 * @param popErrorMsg the new pop error msg
	 */
	public void setPopErrorMsg(String popErrorMsg) {
		this.popErrorMsg = popErrorMsg;
	}

	/**
	 * Gets the ppn base.
	 *
	 * @return the ppn base
	 */
	public String getPpnBase() {
		return ppnBase;
	}

	/**
	 * Sets the ppn base.
	 *
	 * @param ppnBase the new ppn base
	 */
	public void setPpnBase(String ppnBase) {
		this.ppnBase = ppnBase;
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
	 * Gets the technology.
	 *
	 * @return the technology
	 */
	public BigDecimal getTechnology() {
		return technology;
	}

	/**
	 * Sets the technology.
	 *
	 * @param technology the new technology
	 */
	public void setTechnology(BigDecimal technology) {
		this.technology = technology;
	}

	/**
	 * Gets the total num vlan.
	 *
	 * @return the total num vlan
	 */
	public BigDecimal getTotalNumVlan() {
		return totalNumVlan;
	}

	/**
	 * Sets the total num vlan.
	 *
	 * @param totalNumVlan the new total num vlan
	 */
	public void setTotalNumVlan(BigDecimal totalNumVlan) {
		this.totalNumVlan = totalNumVlan;
	}

	/**
	 * Gets the mow AQ status ind.
	 *
	 * @return the mow AQ status ind
	 */
	public String getMowAQStatusInd() {
		return mowAQStatusInd;
	}

	/**
	 * Sets the mow AQ status ind.
	 *
	 * @param mowAQStatusInd the new mow AQ status ind
	 */
	public void setMowAQStatusInd(String mowAQStatusInd) {
		this.mowAQStatusInd = mowAQStatusInd;
	}

	/**
	 * Gets the mow CC status ind.
	 *
	 * @return the mow CC status ind
	 */
	public String getMowCCStatusInd() {
		return mowCCStatusInd;
	}

	/**
	 * Sets the mow CC status ind.
	 *
	 * @param mowCCStatusInd the new mow CC status ind
	 */
	public void setMowCCStatusInd(String mowCCStatusInd) {
		this.mowCCStatusInd = mowCCStatusInd;
	}

	/**
	 * Gets the mow DQ status ind.
	 *
	 * @return the mow DQ status ind
	 */
	public String getMowDQStatusInd() {
		return mowDQStatusInd;
	}

	/**
	 * Sets the mow DQ status ind.
	 *
	 * @param mowDQStatusInd the new mow DQ status ind
	 */
	public void setMowDQStatusInd(String mowDQStatusInd) {
		this.mowDQStatusInd = mowDQStatusInd;
	}

	/**
	 * Gets the vlan AQDQ status.
	 *
	 * @return the vlan AQDQ status
	 */
	public String getVlanAQDQStatus() {
		return vlanAQDQStatus;
	}

	/**
	 * Sets the vlan AQDQ status.
	 *
	 * @param vlanAQDQStatus the new vlan AQDQ status
	 */
	public void setVlanAQDQStatus(String vlanAQDQStatus) {
		this.vlanAQDQStatus = vlanAQDQStatus;
	}

	/**
	 * Gets the vlan locked yn.
	 *
	 * @return the vlan locked yn
	 */
	public String getVlanLockedYn() {
		return vlanLockedYn;
	}

	/**
	 * Sets the vlan locked yn.
	 *
	 * @param vlanLockedYn the new vlan locked yn
	 */
	public void setVlanLockedYn(String vlanLockedYn) {
		this.vlanLockedYn = vlanLockedYn;
	}

	/**
	 * Gets the vlan tag control.
	 *
	 * @return the vlan tag control
	 */
	public String getVlanTagControl() {
		return vlanTagControl;
	}

	/**
	 * Sets the vlan tag control.
	 *
	 * @param vlanTagControl the new vlan tag control
	 */
	public void setVlanTagControl(String vlanTagControl) {
		this.vlanTagControl = vlanTagControl;
	}

	/**
	 * Gets the external key id.
	 *
	 * @return the external key id
	 */
	public String getExternalKeyId() {
		return externalKeyId;
	}

	/**
	 * Sets the external key id.
	 *
	 * @param externalKeyId the new external key id
	 */
	public void setExternalKeyId(String externalKeyId) {
		this.externalKeyId = externalKeyId;
	}

	/**
	 * Gets the port id.
	 *
	 * @return the port id
	 */
	public Long getPortId() {
		return portId;
	}

	/**
	 * Sets the port id.
	 *
	 * @param portId the new port id
	 */
	public void setPortId(Long portId) {
		this.portId = portId;
	}

	/**
	 * Gets the eth rates id.
	 *
	 * @return the eth rates id
	 */
	public Long getEthRatesId() {
		return ethRatesId;
	}

	/**
	 * Sets the eth rates id.
	 *
	 * @param ethRatesId the new eth rates id
	 */
	public void setEthRatesId(Long ethRatesId) {
		this.ethRatesId = ethRatesId;
	}

	/**
	 * Gets the token id.
	 *
	 * @return the token id
	 */
	public String getTokenId() {
		return tokenId;
	}

	/**
	 * Sets the token id.
	 *
	 * @param tokenId the new token id
	 */
	public void setTokenId(String tokenId) {
		this.tokenId = tokenId;
	}

	/**
	 * Gets the mrc.
	 *
	 * @return the mrc
	 */
	public BigDecimal getMrc() {
		return mrc;
	}

	/**
	 * Sets the mrc.
	 *
	 * @param mrc the new mrc
	 */
	public void setMrc(BigDecimal mrc) {
		this.mrc = mrc;
	}

	/**
	 * Gets the nrc.
	 *
	 * @return the nrc
	 */
	public BigDecimal getNrc() {
		return nrc;
	}

	/**
	 * Sets the nrc.
	 *
	 * @param nrc the new nrc
	 */
	public void setNrc(BigDecimal nrc) {
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
	 * Gets the ethernet quote ind.
	 *
	 * @return the ethernet quote ind
	 */
	public String getEthernetQuoteInd() {
		return ethernetQuoteInd;
	}

	/**
	 * Sets the ethernet quote ind.
	 *
	 * @param ethernetQuoteInd the new ethernet quote ind
	 */
	public void setEthernetQuoteInd(String ethernetQuoteInd) {
		this.ethernetQuoteInd = ethernetQuoteInd;
	}

	/**
	 * Gets the expiration date.
	 *
	 * @return the expiration date
	 */
	public Date getExpirationDate() {
		return expirationDate;
	}

	/**
	 * Sets the expiration date.
	 *
	 * @param expirationDate the new expiration date
	 */
	public void setExpirationDate(Date expirationDate) {
		this.expirationDate = expirationDate;
	}

	/**
	 * Gets the max mrc discount.
	 *
	 * @return the max mrc discount
	 */
	public Double getMaxMrcDiscount() {
		return maxMrcDiscount;
	}

	/**
	 * Sets the max mrc discount.
	 *
	 * @param maxMrcDiscount the new max mrc discount
	 */
	public void setMaxMrcDiscount(Double maxMrcDiscount) {
		this.maxMrcDiscount = maxMrcDiscount;
	}

	/**
	 * Gets the max nrc discount.
	 *
	 * @return the max nrc discount
	 */
	public Double getMaxNrcDiscount() {
		return maxNrcDiscount;
	}

	/**
	 * Sets the max nrc discount.
	 *
	 * @param maxNrcDiscount the new max nrc discount
	 */
	public void setMaxNrcDiscount(Double maxNrcDiscount) {
		this.maxNrcDiscount = maxNrcDiscount;
	}

	/**
	 * Gets the mrc list rate.
	 *
	 * @return the mrc list rate
	 */
	public Double getMrcListRate() {
		return mrcListRate;
	}

	/**
	 * Sets the mrc list rate.
	 *
	 * @param mrcListRate the new mrc list rate
	 */
	public void setMrcListRate(Double mrcListRate) {
		this.mrcListRate = mrcListRate;
	}

	/**
	 * Gets the nrc list rate.
	 *
	 * @return the nrc list rate
	 */
	public Double getNrcListRate() {
		return nrcListRate;
	}

	/**
	 * Sets the nrc list rate.
	 *
	 * @param nrcListRate the new nrc list rate
	 */
	public void setNrcListRate(Double nrcListRate) {
		this.nrcListRate = nrcListRate;
	}

	/**
	 * Gets the service guide.
	 *
	 * @return the service guide
	 */
	public String getServiceGuide() {
		return serviceGuide;
	}

	/**
	 * Sets the service guide.
	 *
	 * @param serviceGuide the new service guide
	 */
	public void setServiceGuide(String serviceGuide) {
		this.serviceGuide = serviceGuide;
	}

	/**
	 * Gets the igloo max mrc discount.
	 *
	 * @return the igloo max mrc discount
	 */
	public Double getIglooMaxMrcDiscount() {
		return iglooMaxMrcDiscount;
	}

	/**
	 * Sets the igloo max mrc discount.
	 *
	 * @param iglooMaxMrcDiscount the new igloo max mrc discount
	 */
	public void setIglooMaxMrcDiscount(Double iglooMaxMrcDiscount) {
		this.iglooMaxMrcDiscount = iglooMaxMrcDiscount;
	}

	/**
	 * Gets the igloo mrc list rate.
	 *
	 * @return the igloo mrc list rate
	 */
	public Double getIglooMrcListRate() {
		return iglooMrcListRate;
	}

	/**
	 * Sets the igloo mrc list rate.
	 *
	 * @param iglooMrcListRate the new igloo mrc list rate
	 */
	public void setIglooMrcListRate(Double iglooMrcListRate) {
		this.iglooMrcListRate = iglooMrcListRate;
	}

	/**
	 * Gets the igloo mrc net rate.
	 *
	 * @return the igloo mrc net rate
	 */
	public Double getIglooMrcNetRate() {
		return iglooMrcNetRate;
	}

	/**
	 * Sets the igloo mrc net rate.
	 *
	 * @param iglooMrcNetRate the new igloo mrc net rate
	 */
	public void setIglooMrcNetRate(Double iglooMrcNetRate) {
		this.iglooMrcNetRate = iglooMrcNetRate;
	}

	/**
	 * Gets the igloo service guide.
	 *
	 * @return the igloo service guide
	 */
	public String getIglooServiceGuide() {
		return iglooServiceGuide;
	}

	/**
	 * Sets the igloo service guide.
	 *
	 * @param iglooServiceGuide the new igloo service guide
	 */
	public void setIglooServiceGuide(String iglooServiceGuide) {
		this.iglooServiceGuide = iglooServiceGuide;
	}

	/**
	 * Gets the aq status ind.
	 *
	 * @return the aq status ind
	 */
	public String getAqStatusInd() {
		return aqStatusInd;
	}

	/**
	 * Sets the aq status ind.
	 *
	 * @param aqStatusInd the new aq status ind
	 */
	public void setAqStatusInd(String aqStatusInd) {
		this.aqStatusInd = aqStatusInd;
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
	 * Gets the current ps.
	 *
	 * @return the current ps
	 */
	public String getCurrentPs() {
		return currentPs;
	}

	/**
	 * Sets the current ps.
	 *
	 * @param currentPs the new current ps
	 */
	public void setCurrentPs(String currentPs) {
		this.currentPs = currentPs;
	}

	/**
	 * Gets the icb appr mrc dsc.
	 *
	 * @return the icb appr mrc dsc
	 */
	public BigDecimal getIcbApprMrcDsc() {
		return icbApprMrcDsc;
	}

	/**
	 * Sets the icb appr mrc dsc.
	 *
	 * @param icbApprMrcDsc the new icb appr mrc dsc
	 */
	public void setIcbApprMrcDsc(BigDecimal icbApprMrcDsc) {
		this.icbApprMrcDsc = icbApprMrcDsc;
	}

	/**
	 * Gets the icb appr nrc dsc.
	 *
	 * @return the icb appr nrc dsc
	 */
	public BigDecimal getIcbApprNrcDsc() {
		return icbApprNrcDsc;
	}

	/**
	 * Sets the icb appr nrc dsc.
	 *
	 * @param icbApprNrcDsc the new icb appr nrc dsc
	 */
	public void setIcbApprNrcDsc(BigDecimal icbApprNrcDsc) {
		this.icbApprNrcDsc = icbApprNrcDsc;
	}

	/**
	 * Gets the icb appliedyn.
	 *
	 * @return the icb appliedyn
	 */
	public String getIcbAppliedyn() {
		return icbAppliedyn;
	}

	/**
	 * Sets the icb appliedyn.
	 *
	 * @param icbAppliedyn the new icb appliedyn
	 */
	public void setIcbAppliedyn(String icbAppliedyn) {
		this.icbAppliedyn = icbAppliedyn;
	}

	/**
	 * Gets the icb mrc floor rate.
	 *
	 * @return the icb mrc floor rate
	 */
	public BigDecimal getIcbMrcFloorRate() {
		return icbMrcFloorRate;
	}

	/**
	 * Sets the icb mrc floor rate.
	 *
	 * @param icbMrcFloorRate the new icb mrc floor rate
	 */
	public void setIcbMrcFloorRate(BigDecimal icbMrcFloorRate) {
		this.icbMrcFloorRate = icbMrcFloorRate;
	}

	/**
	 * Gets the icb nrc floorr rte.
	 *
	 * @return the icb nrc floorr rte
	 */
	public BigDecimal getIcbNrcFloorrRte() {
		return icbNrcFloorrRte;
	}

	/**
	 * Sets the icb nrc floorr rte.
	 *
	 * @param icbNrcFloorrRte the new icb nrc floorr rte
	 */
	public void setIcbNrcFloorrRte(BigDecimal icbNrcFloorrRte) {
		this.icbNrcFloorrRte = icbNrcFloorrRte;
	}

	/**
	 * Gets the quote req date.
	 *
	 * @return the quote req date
	 */
	public Date getQuoteReqDate() {
		return quoteReqDate;
	}

	/**
	 * Sets the quote req date.
	 *
	 * @param quoteReqDate the new quote req date
	 */
	public void setQuoteReqDate(Date quoteReqDate) {
		this.quoteReqDate = quoteReqDate;
	}

	/**
	 * Gets the igloo error msg.
	 *
	 * @return the igloo error msg
	 */
	public String getIglooErrorMsg() {
		return iglooErrorMsg;
	}

	/**
	 * Sets the igloo error msg.
	 *
	 * @param iglooErrorMsg the new igloo error msg
	 */
	public void setIglooErrorMsg(String iglooErrorMsg) {
		this.iglooErrorMsg = iglooErrorMsg;
	}

	/**
	 * Gets the eth zone value.
	 *
	 * @return the eth zone value
	 */
	public String getEthZoneValue() {
		return ethZoneValue;
	}

	/**
	 * Sets the eth zone value.
	 *
	 * @param ethZoneValue the new eth zone value
	 */
	public void setEthZoneValue(String ethZoneValue) {
		this.ethZoneValue = ethZoneValue;
	}

	/**
	 * Gets the checks if is contract impactyn.
	 *
	 * @return the checks if is contract impactyn
	 */
	public String getIsContractImpactyn() {
		return isContractImpactyn;
	}

	/**
	 * Sets the checks if is contract impactyn.
	 *
	 * @param isContractImpactyn the new checks if is contract impactyn
	 */
	public void setIsContractImpactyn(String isContractImpactyn) {
		this.isContractImpactyn = isContractImpactyn;
	}

	/**
	 * Gets the changedyn.
	 *
	 * @return the changedyn
	 */
	public String getChangedyn() {
		return changedyn;
	}

	/**
	 * Sets the changedyn.
	 *
	 * @param changedyn the new changedyn
	 */
	public void setChangedyn(String changedyn) {
		this.changedyn = changedyn;
	}

	/**
	 * Gets the nrc list igloo.
	 *
	 * @return the nrc list igloo
	 */
	public Double getNrcListIgloo() {
		return nrcListIgloo;
	}

	/**
	 * Sets the nrc list igloo.
	 *
	 * @param nrcListIgloo the new nrc list igloo
	 */
	public void setNrcListIgloo(Double nrcListIgloo) {
		this.nrcListIgloo = nrcListIgloo;
	}

	/**
	 * Gets the mow access quote info id.
	 *
	 * @return the mow access quote info id
	 */
	public Long getMowAccessQuoteInfoId() {
		return mowAccessQuoteInfoId;
	}

	/**
	 * Sets the mow access quote info id.
	 *
	 * @param mowAccessQuoteInfoId the new mow access quote info id
	 */
	public void setMowAccessQuoteInfoId(Long mowAccessQuoteInfoId) {
		this.mowAccessQuoteInfoId = mowAccessQuoteInfoId;
	}

	/**
	 * Gets the request id.
	 *
	 * @return the request id
	 */
	public Long getRequestId() {
		return requestId;
	}

	/**
	 * Sets the request id.
	 *
	 * @param requestId the new request id
	 */
	public void setRequestId(Long requestId) {
		this.requestId = requestId;
	}

	/**
	 * Gets the location id.
	 *
	 * @return the location id
	 */
	public Long getLocationId() {
		return locationId;
	}

	/**
	 * Sets the location id.
	 *
	 * @param locationId the new location id
	 */
	public void setLocationId(Long locationId) {
		this.locationId = locationId;
	}

	/**
	 * Gets the resp pop clli.
	 *
	 * @return the resp pop clli
	 */
	public String getRespPopClli() {
		return respPopClli;
	}

	/**
	 * Sets the resp pop clli.
	 *
	 * @param respPopClli the new resp pop clli
	 */
	public void setRespPopClli(String respPopClli) {
		this.respPopClli = respPopClli;
	}

	/**
	 * Gets the resp pop address.
	 *
	 * @return the resp pop address
	 */
	public String getRespPopAddress() {
		return respPopAddress;
	}

	/**
	 * Sets the resp pop address.
	 *
	 * @param respPopAddress the new resp pop address
	 */
	public void setRespPopAddress(String respPopAddress) {
		this.respPopAddress = respPopAddress;
	}

	/**
	 * Gets the resp speed.
	 *
	 * @return the resp speed
	 */
	public Long getRespSpeed() {
		return respSpeed;
	}

	/**
	 * Sets the resp speed.
	 *
	 * @param respSpeed the new resp speed
	 */
	public void setRespSpeed(Long respSpeed) {
		this.respSpeed = respSpeed;
	}

	/**
	 * Gets the service availability date.
	 *
	 * @return the service availability date
	 */
	public Date getServiceAvailabilityDate() {
		return serviceAvailabilityDate;
	}

	/**
	 * Sets the service availability date.
	 *
	 * @param serviceAvailabilityDate the new service availability date
	 */
	public void setServiceAvailabilityDate(Date serviceAvailabilityDate) {
		this.serviceAvailabilityDate = serviceAvailabilityDate;
	}

	/**
	 * Gets the caveat ids.
	 *
	 * @return the caveat ids
	 */
	public String getCaveatIds() {
		return caveatIds;
	}

	/**
	 * Sets the caveat ids.
	 *
	 * @param caveatIds the new caveat ids
	 */
	public void setCaveatIds(String caveatIds) {
		this.caveatIds = caveatIds;
	}

	/**
	 * Gets the resp itu carrier id.
	 *
	 * @return the resp itu carrier id
	 */
	public String getRespItuCarrierId() {
		return respItuCarrierId;
	}

	/**
	 * Sets the resp itu carrier id.
	 *
	 * @param respItuCarrierId the new resp itu carrier id
	 */
	public void setRespItuCarrierId(String respItuCarrierId) {
		this.respItuCarrierId = respItuCarrierId;
	}

	/**
	 * Gets the resp supplier name.
	 *
	 * @return the resp supplier name
	 */
	public String getRespSupplierName() {
		return respSupplierName;
	}

	/**
	 * Sets the resp supplier name.
	 *
	 * @param respSupplierName the new resp supplier name
	 */
	public void setRespSupplierName(String respSupplierName) {
		this.respSupplierName = respSupplierName;
	}

	/**
	 * Gets the resp supplier service.
	 *
	 * @return the resp supplier service
	 */
	public String getRespSupplierService() {
		return respSupplierService;
	}

	/**
	 * Sets the resp supplier service.
	 *
	 * @param respSupplierService the new resp supplier service
	 */
	public void setRespSupplierService(String respSupplierService) {
		this.respSupplierService = respSupplierService;
	}

	/**
	 * Gets the resp pop node name.
	 *
	 * @return the resp pop node name
	 */
	public String getRespPopNodeName() {
		return respPopNodeName;
	}

	/**
	 * Sets the resp pop node name.
	 *
	 * @param respPopNodeName the new resp pop node name
	 */
	public void setRespPopNodeName(String respPopNodeName) {
		this.respPopNodeName = respPopNodeName;
	}

	/**
	 * Gets the mrc local.
	 *
	 * @return the mrc local
	 */
	public BigDecimal getMrcLocal() {
		return mrcLocal;
	}

	/**
	 * Sets the mrc local.
	 *
	 * @param mrcLocal the new mrc local
	 */
	public void setMrcLocal(BigDecimal mrcLocal) {
		this.mrcLocal = mrcLocal;
	}

	/**
	 * Gets the nrc local.
	 *
	 * @return the nrc local
	 */
	public BigDecimal getNrcLocal() {
		return nrcLocal;
	}

	/**
	 * Sets the nrc local.
	 *
	 * @param nrcLocal the new nrc local
	 */
	public void setNrcLocal(BigDecimal nrcLocal) {
		this.nrcLocal = nrcLocal;
	}

	/**
	 * Gets the local currency code.
	 *
	 * @return the local currency code
	 */
	public String getLocalCurrencyCode() {
		return localCurrencyCode;
	}

	/**
	 * Sets the local currency code.
	 *
	 * @param localCurrencyCode the new local currency code
	 */
	public void setLocalCurrencyCode(String localCurrencyCode) {
		this.localCurrencyCode = localCurrencyCode;
	}

	/**
	 * Gets the mrc usd.
	 *
	 * @return the mrc usd
	 */
	public BigDecimal getMrcUsd() {
		return mrcUsd;
	}

	/**
	 * Sets the mrc usd.
	 *
	 * @param mrcUsd the new mrc usd
	 */
	public void setMrcUsd(BigDecimal mrcUsd) {
		this.mrcUsd = mrcUsd;
	}

	/**
	 * Gets the nrc usd.
	 *
	 * @return the nrc usd
	 */
	public BigDecimal getNrcUsd() {
		return nrcUsd;
	}

	/**
	 * Sets the nrc usd.
	 *
	 * @param nrcUsd the new nrc usd
	 */
	public void setNrcUsd(BigDecimal nrcUsd) {
		this.nrcUsd = nrcUsd;
	}

	/**
	 * Gets the supplier tier.
	 *
	 * @return the supplier tier
	 */
	public String getSupplierTier() {
		return supplierTier;
	}

	/**
	 * Sets the supplier tier.
	 *
	 * @param supplierTier the new supplier tier
	 */
	public void setSupplierTier(String supplierTier) {
		this.supplierTier = supplierTier;
	}

	/**
	 * Gets the coverage ind.
	 *
	 * @return the coverage ind
	 */
	public String getCoverageInd() {
		return coverageInd;
	}

	/**
	 * Sets the coverage ind.
	 *
	 * @param coverageInd the new coverage ind
	 */
	public void setCoverageInd(String coverageInd) {
		this.coverageInd = coverageInd;
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
	 * Gets the dq expiration date.
	 *
	 * @return the dq expiration date
	 */
	public Date getDqExpirationDate() {
		return dqExpirationDate;
	}

	/**
	 * Sets the dq expiration date.
	 *
	 * @param dqExpirationDate the new dq expiration date
	 */
	public void setDqExpirationDate(Date dqExpirationDate) {
		this.dqExpirationDate = dqExpirationDate;
	}

	/**
	 * Gets the feasibility reference number.
	 *
	 * @return the feasibility reference number
	 */
	public String getFeasibilityReferenceNumber() {
		return feasibilityReferenceNumber;
	}

	/**
	 * Sets the feasibility reference number.
	 *
	 * @param feasibilityReferenceNumber the new feasibility reference number
	 */
	public void setFeasibilityReferenceNumber(String feasibilityReferenceNumber) {
		this.feasibilityReferenceNumber = feasibilityReferenceNumber;
	}

	/**
	 * Gets the resp access interconnect.
	 *
	 * @return the resp access interconnect
	 */
	public Long getRespAccessInterconnect() {
		return respAccessInterconnect;
	}

	/**
	 * Sets the resp access interconnect.
	 *
	 * @param respAccessInterconnect the new resp access interconnect
	 */
	public void setRespAccessInterconnect(Long respAccessInterconnect) {
		this.respAccessInterconnect = respAccessInterconnect;
	}

	/**
	 * Gets the resp tail technology.
	 *
	 * @return the resp tail technology
	 */
	public Long getRespTailTechnology() {
		return respTailTechnology;
	}

	/**
	 * Sets the resp tail technology.
	 *
	 * @param respTailTechnology the new resp tail technology
	 */
	public void setRespTailTechnology(Long respTailTechnology) {
		this.respTailTechnology = respTailTechnology;
	}

	/**
	 * Gets the resp provider product code.
	 *
	 * @return the resp provider product code
	 */
	public String getRespProviderProductCode() {
		return respProviderProductCode;
	}

	/**
	 * Sets the resp provider product code.
	 *
	 * @param respProviderProductCode the new resp provider product code
	 */
	public void setRespProviderProductCode(String respProviderProductCode) {
		this.respProviderProductCode = respProviderProductCode;
	}

	/**
	 * Gets the resp provider product name.
	 *
	 * @return the resp provider product name
	 */
	public String getRespProviderProductName() {
		return respProviderProductName;
	}

	/**
	 * Sets the resp provider product name.
	 *
	 * @param respProviderProductName the new resp provider product name
	 */
	public void setRespProviderProductName(String respProviderProductName) {
		this.respProviderProductName = respProviderProductName;
	}

	/**
	 * Gets the resp upstream speed.
	 *
	 * @return the resp upstream speed
	 */
	public Long getRespUpstreamSpeed() {
		return respUpstreamSpeed;
	}

	/**
	 * Sets the resp upstream speed.
	 *
	 * @param respUpstreamSpeed the new resp upstream speed
	 */
	public void setRespUpstreamSpeed(Long respUpstreamSpeed) {
		this.respUpstreamSpeed = respUpstreamSpeed;
	}

	/**
	 * Gets the resp downstream speed.
	 *
	 * @return the resp downstream speed
	 */
	public Long getRespDownstreamSpeed() {
		return respDownstreamSpeed;
	}

	/**
	 * Sets the resp downstream speed.
	 *
	 * @param respDownstreamSpeed the new resp downstream speed
	 */
	public void setRespDownstreamSpeed(Long respDownstreamSpeed) {
		this.respDownstreamSpeed = respDownstreamSpeed;
	}

	/**
	 * Gets the resp ordering code.
	 *
	 * @return the resp ordering code
	 */
	public String getRespOrderingCode() {
		return respOrderingCode;
	}

	/**
	 * Sets the resp ordering code.
	 *
	 * @param respOrderingCode the new resp ordering code
	 */
	public void setRespOrderingCode(String respOrderingCode) {
		this.respOrderingCode = respOrderingCode;
	}

	/**
	 * Gets the resp egr flag.
	 *
	 * @return the resp egr flag
	 */
	public String getRespEgrFlag() {
		return respEgrFlag;
	}

	/**
	 * Sets the resp egr flag.
	 *
	 * @param respEgrFlag the new resp egr flag
	 */
	public void setRespEgrFlag(String respEgrFlag) {
		this.respEgrFlag = respEgrFlag;
	}

	/**
	 * Gets the resp lead time.
	 *
	 * @return the resp lead time
	 */
	public Long getRespLeadTime() {
		return respLeadTime;
	}

	/**
	 * Sets the resp lead time.
	 *
	 * @param respLeadTime the new resp lead time
	 */
	public void setRespLeadTime(Long respLeadTime) {
		this.respLeadTime = respLeadTime;
	}

	/**
	 * Gets the resp best price.
	 *
	 * @return the resp best price
	 */
	public String getRespBestPrice() {
		return respBestPrice;
	}

	/**
	 * Sets the resp best price.
	 *
	 * @param respBestPrice the new resp best price
	 */
	public void setRespBestPrice(String respBestPrice) {
		this.respBestPrice = respBestPrice;
	}

	/**
	 * Gets the coverage check status.
	 *
	 * @return the coverage check status
	 */
	public String getCoverageCheckStatus() {
		return coverageCheckStatus;
	}

	/**
	 * Sets the coverage check status.
	 *
	 * @param coverageCheckStatus the new coverage check status
	 */
	public void setCoverageCheckStatus(String coverageCheckStatus) {
		this.coverageCheckStatus = coverageCheckStatus;
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
	 * Gets the igloo quote call status.
	 *
	 * @return the igloo quote call status
	 */
	public String getIglooQuoteCallStatus() {
		return iglooQuoteCallStatus;
	}

	/**
	 * Sets the igloo quote call status.
	 *
	 * @param iglooQuoteCallStatus the new igloo quote call status
	 */
	public void setIglooQuoteCallStatus(String iglooQuoteCallStatus) {
		this.iglooQuoteCallStatus = iglooQuoteCallStatus;
	}

	/**
	 * Gets the selected interface id.
	 *
	 * @return the selected interface id
	 */
	public BigDecimal getSelectedInterfaceId() {
		return selectedInterfaceId;
	}

	/**
	 * Sets the selected interface id.
	 *
	 * @param selectedInterfaceId the new selected interface id
	 */
	public void setSelectedInterfaceId(BigDecimal selectedInterfaceId) {
		this.selectedInterfaceId = selectedInterfaceId;
	}

	/**
	 * Gets the quote type.
	 *
	 * @return the quote type
	 */
	public String getQuoteType() {
		return quoteType;
	}

	/**
	 * Sets the quote type.
	 *
	 * @param quoteType the new quote type
	 */
	public void setQuoteType(String quoteType) {
		this.quoteType = quoteType;
	}

	/**
	 * Gets the service delivery type.
	 *
	 * @return the service delivery type
	 */
	public String getServiceDeliveryType() {
		return serviceDeliveryType;
	}

	/**
	 * Sets the service delivery type.
	 *
	 * @param serviceDeliveryType the new service delivery type
	 */
	public void setServiceDeliveryType(String serviceDeliveryType) {
		this.serviceDeliveryType = serviceDeliveryType;
	}

	/**
	 * Gets the service delivery speed.
	 *
	 * @return the service delivery speed
	 */
	public Long getServiceDeliverySpeed() {
		return serviceDeliverySpeed;
	}

	/**
	 * Sets the service delivery speed.
	 *
	 * @param serviceDeliverySpeed the new service delivery speed
	 */
	public void setServiceDeliverySpeed(Long serviceDeliverySpeed) {
		this.serviceDeliverySpeed = serviceDeliverySpeed;
	}

	/**
	 * Gets the circuit protection.
	 *
	 * @return the circuit protection
	 */
	public Long getCircuitProtection() {
		return circuitProtection;
	}

	/**
	 * Sets the circuit protection.
	 *
	 * @param circuitProtection the new circuit protection
	 */
	public void setCircuitProtection(Long circuitProtection) {
		this.circuitProtection = circuitProtection;
	}

	/**
	 * Gets the t 3 framing.
	 *
	 * @return the t 3 framing
	 */
	public Long getT3Framing() {
		return t3Framing;
	}

	/**
	 * Sets the t 3 framing.
	 *
	 * @param t3Framing the new t 3 framing
	 */
	public void setT3Framing(Long t3Framing) {
		this.t3Framing = t3Framing;
	}

	/**
	 * Gets the resp access type.
	 *
	 * @return the resp access type
	 */
	public Long getRespAccessType() {
		return respAccessType;
	}

	/**
	 * Sets the resp access type.
	 *
	 * @param respAccessType the new resp access type
	 */
	public void setRespAccessType(Long respAccessType) {
		this.respAccessType = respAccessType;
	}

	/**
	 * Gets the resp notes.
	 *
	 * @return the resp notes
	 */
	public String getRespNotes() {
		return respNotes;
	}

	/**
	 * Sets the resp notes.
	 *
	 * @param respNotes the new resp notes
	 */
	public void setRespNotes(String respNotes) {
		this.respNotes = respNotes;
	}

	/**
	 * Gets the carrier type.
	 *
	 * @return the carrier type
	 */
	public String getCarrierType() {
		return carrierType;
	}

	/**
	 * Sets the carrier type.
	 *
	 * @param carrierType the new carrier type
	 */
	public void setCarrierType(String carrierType) {
		this.carrierType = carrierType;
	}

	/**
	 * Gets the customer tagging.
	 *
	 * @return the customer tagging
	 */
	public BigDecimal getCustomerTagging() {
		return customerTagging;
	}

	/**
	 * Sets the customer tagging.
	 *
	 * @param customerTagging the new customer tagging
	 */
	public void setCustomerTagging(BigDecimal customerTagging) {
		this.customerTagging = customerTagging;
	}

	/**
	 * Gets the mow popclli id.
	 *
	 * @return the mow popclli id
	 */
	public String getMowPopclliId() {
		return mowPopclliId;
	}

	/**
	 * Sets the mow popclli id.
	 *
	 * @param mowPopclliId the new mow popclli id
	 */
	public void setMowPopclliId(String mowPopclliId) {
		this.mowPopclliId = mowPopclliId;
	}

	/**
	 * Gets the pop address.
	 *
	 * @return the pop address
	 */
	public String getPopAddress() {
		return popAddress;
	}

	/**
	 * Sets the pop address.
	 *
	 * @param popAddress the new pop address
	 */
	public void setPopAddress(String popAddress) {
		this.popAddress = popAddress;
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
	 * Sets the node name.
	 *
	 * @param nodeName the new node name
	 */
	public void setNodeName(String nodeName) {
		this.nodeName = nodeName;
	}

	/**
	 * Gets the access architecture.
	 *
	 * @return the access architecture
	 */
	public Long getAccessArchitecture() {
		return accessArchitecture;
	}

	/**
	 * Sets the access architecture.
	 *
	 * @param accessArchitecture the new access architecture
	 */
	public void setAccessArchitecture(Long accessArchitecture) {
		this.accessArchitecture = accessArchitecture;
	}

	/**
	 * Gets the access provider yn.
	 *
	 * @return the access provider yn
	 */
	public String getAccessProviderYn() {
		return accessProviderYn;
	}

	/**
	 * Sets the access provider yn.
	 *
	 * @param accessProviderYn the new access provider yn
	 */
	public void setAccessProviderYn(String accessProviderYn) {
		this.accessProviderYn = accessProviderYn;
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
	 * Gets the aq term upd status ind.
	 *
	 * @return the aq term upd status ind
	 */
	public String getAqTermUpdStatusInd() {
		return aqTermUpdStatusInd;
	}

	/**
	 * Sets the aq term upd status ind.
	 *
	 * @param aqTermUpdStatusInd the new aq term upd status ind
	 */
	public void setAqTermUpdStatusInd(String aqTermUpdStatusInd) {
		this.aqTermUpdStatusInd = aqTermUpdStatusInd;
	}

	/**
	 * Gets the ddq status ind.
	 *
	 * @return the ddq status ind
	 */
	public String getDdqStatusInd() {
		return ddqStatusInd;
	}

	/**
	 * Sets the ddq status ind.
	 *
	 * @param ddqStatusInd the new ddq status ind
	 */
	public void setDdqStatusInd(String ddqStatusInd) {
		this.ddqStatusInd = ddqStatusInd;
	}

	/**
	 * Gets the desired discount.
	 *
	 * @return the desired discount
	 */
	public Double getDesiredDiscount() {
		return desiredDiscount;
	}

	/**
	 * Sets the desired discount.
	 *
	 * @param desiredDiscount the new desired discount
	 */
	public void setDesiredDiscount(Double desiredDiscount) {
		this.desiredDiscount = desiredDiscount;
	}

	/**
	 * Gets the dff status ind.
	 *
	 * @return the dff status ind
	 */
	public String getDffStatusInd() {
		return dffStatusInd;
	}

	/**
	 * Sets the dff status ind.
	 *
	 * @param dffStatusInd the new dff status ind
	 */
	public void setDffStatusInd(String dffStatusInd) {
		this.dffStatusInd = dffStatusInd;
	}

	/**
	 * Gets the eth design update ind.
	 *
	 * @return the eth design update ind
	 */
	public String getEthDesignUpdateInd() {
		return ethDesignUpdateInd;
	}

	/**
	 * Sets the eth design update ind.
	 *
	 * @param ethDesignUpdateInd the new eth design update ind
	 */
	public void setEthDesignUpdateInd(String ethDesignUpdateInd) {
		this.ethDesignUpdateInd = ethDesignUpdateInd;
	}

	/**
	 * Gets the global location id.
	 *
	 * @return the global location id
	 */
	public String getGlobalLocationId() {
		return globalLocationId;
	}

	/**
	 * Sets the global location id.
	 *
	 * @param globalLocationId the new global location id
	 */
	public void setGlobalLocationId(String globalLocationId) {
		this.globalLocationId = globalLocationId;
	}

	/**
	 * Gets the igloo term id.
	 *
	 * @return the igloo term id
	 */
	public Long getIglooTermId() {
		return iglooTermId;
	}

	/**
	 * Sets the igloo term id.
	 *
	 * @param iglooTermId the new igloo term id
	 */
	public void setIglooTermId(Long iglooTermId) {
		this.iglooTermId = iglooTermId;
	}

	/**
	 * Gets the interconnect technology.
	 *
	 * @return the interconnect technology
	 */
	public Long getInterconnectTechnology() {
		return interconnectTechnology;
	}

	/**
	 * Sets the interconnect technology.
	 *
	 * @param interconnectTechnology the new interconnect technology
	 */
	public void setInterconnectTechnology(Long interconnectTechnology) {
		this.interconnectTechnology = interconnectTechnology;
	}

	/**
	 * Gets the isc base.
	 *
	 * @return the isc base
	 */
	public String getIscBase() {
		return iscBase;
	}

	/**
	 * Sets the isc base.
	 *
	 * @param iscBase the new isc base
	 */
	public void setIscBase(String iscBase) {
		this.iscBase = iscBase;
	}

	/**
	 * Gets the market strata.
	 *
	 * @return the market strata
	 */
	public String getMarketStrata() {
		return marketStrata;
	}

	/**
	 * Sets the market strata.
	 *
	 * @param marketStrata the new market strata
	 */
	public void setMarketStrata(String marketStrata) {
		this.marketStrata = marketStrata;
	}

	/**
	 * Gets the mis type.
	 *
	 * @return the mis type
	 */
	public Long getMisType() {
		return misType;
	}

	/**
	 * Sets the mis type.
	 *
	 * @param misType the new mis type
	 */
	public void setMisType(Long misType) {
		this.misType = misType;
	}

	/**
	 * Gets the mtu supp msgs.
	 *
	 * @return the mtu supp msgs
	 */
	public String getMtuSuppMsgs() {
		return mtuSuppMsgs;
	}

	/**
	 * Sets the mtu supp msgs.
	 *
	 * @param mtuSuppMsgs the new mtu supp msgs
	 */
	public void setMtuSuppMsgs(String mtuSuppMsgs) {
		this.mtuSuppMsgs = mtuSuppMsgs;
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
	 * Gets the soc.
	 *
	 * @return the soc
	 */
	public String getSoc() {
		return soc;
	}

	/**
	 * Sets the soc.
	 *
	 * @param soc the new soc
	 */
	public void setSoc(String soc) {
		this.soc = soc;
	}

	/**
	 * Gets the spec code.
	 *
	 * @return the spec code
	 */
	public String getSpecCode() {
		return specCode;
	}

	/**
	 * Sets the spec code.
	 *
	 * @param specCode the new spec code
	 */
	public void setSpecCode(String specCode) {
		this.specCode = specCode;
	}

	/**
	 * Gets the speed token id.
	 *
	 * @return the speed token id
	 */
	public String getSpeedTokenId() {
		return speedTokenId;
	}

	/**
	 * Sets the speed token id.
	 *
	 * @param speedTokenId the new speed token id
	 */
	public void setSpeedTokenId(String speedTokenId) {
		this.speedTokenId = speedTokenId;
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
	 * Gets the tail technology.
	 *
	 * @return the tail technology
	 */
	public Long getTailTechnology() {
		return tailTechnology;
	}

	/**
	 * Sets the tail technology.
	 *
	 * @param tailTechnology the new tail technology
	 */
	public void setTailTechnology(Long tailTechnology) {
		this.tailTechnology = tailTechnology;
	}

	/**
	 * Gets the uso number.
	 *
	 * @return the uso number
	 */
	public String getUsoNumber() {
		return usoNumber;
	}

	/**
	 * Sets the uso number.
	 *
	 * @param usoNumber the new uso number
	 */
	public void setUsoNumber(String usoNumber) {
		this.usoNumber = usoNumber;
	}

	/**
	 * Gets the price scenario id.
	 *
	 * @return the price scenario id
	 */
	public Long getPriceScenarioId() {
		return priceScenarioId;
	}

	/**
	 * Sets the price scenario id.
	 *
	 * @param priceScenarioId the new price scenario id
	 */
	public void setPriceScenarioId(Long priceScenarioId) {
		this.priceScenarioId = priceScenarioId;
	}

	/**
	 * Gets the selected quote info id.
	 *
	 * @return the selected quote info id
	 */
	public Long getSelectedQuoteInfoId() {
		return selectedQuoteInfoId;
	}

	/**
	 * Sets the selected quote info id.
	 *
	 * @param selectedQuoteInfoId the new selected quote info id
	 */
	public void setSelectedQuoteInfoId(Long selectedQuoteInfoId) {
		this.selectedQuoteInfoId = selectedQuoteInfoId;
	}

	/**
	 * Gets the selected pop.
	 *
	 * @return the selected pop
	 */
	public Long getSelectedPop() {
		return selectedPop;
	}

	/**
	 * Sets the selected pop.
	 *
	 * @param selectedPop the new selected pop
	 */
	public void setSelectedPop(Long selectedPop) {
		this.selectedPop = selectedPop;
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
	 * Gets the checks if is savedyn.
	 *
	 * @return the checks if is savedyn
	 */
	public String getIsSavedyn() {
		return isSavedyn;
	}

	/**
	 * Sets the checks if is savedyn.
	 *
	 * @param isSavedyn the new checks if is savedyn
	 */
	public void setIsSavedyn(String isSavedyn) {
		this.isSavedyn = isSavedyn;
	}

	/**
	 * Gets the user selectedyn.
	 *
	 * @return the user selectedyn
	 */
	public String getUserSelectedyn() {
		return userSelectedyn;
	}

	/**
	 * Sets the user selectedyn.
	 *
	 * @param userSelectedyn the new user selectedyn
	 */
	public void setUserSelectedyn(String userSelectedyn) {
		this.userSelectedyn = userSelectedyn;
	}

	/**
	 * Gets the soryn.
	 *
	 * @return the soryn
	 */
	public String getSoryn() {
		return soryn;
	}

	/**
	 * Sets the soryn.
	 *
	 * @param soryn the new soryn
	 */
	public void setSoryn(String soryn) {
		this.soryn = soryn;
	}

	/**
	 * Gets the pop ret YN.
	 *
	 * @return the pop ret YN
	 */
	public String getPopRetYN() {
		return popRetYN;
	}

	/**
	 * Sets the pop ret YN.
	 *
	 * @param popRetYN the new pop ret YN
	 */
	public void setPopRetYN(String popRetYN) {
		this.popRetYN = popRetYN;
	}

	/**
	 * Gets the aq quoteinfo id.
	 *
	 * @return the aq quoteinfo id
	 */
	public BigDecimal getAqQuoteinfoId() {
		return aqQuoteinfoId;
	}

	/**
	 * Sets the aq quoteinfo id.
	 *
	 * @param aqQuoteinfoId the new aq quoteinfo id
	 */
	public void setAqQuoteinfoId(BigDecimal aqQuoteinfoId) {
		this.aqQuoteinfoId = aqQuoteinfoId;
	}

	/**
	 * Gets the dqid.
	 *
	 * @return the dqid
	 */
	public String getDqid() {
		return dqid;
	}

	/**
	 * Sets the dqid.
	 *
	 * @param dqid the new dqid
	 */
	public void setDqid(String dqid) {
		this.dqid = dqid;
	}

	/**
	 * Gets the feasibility expirationdate.
	 *
	 * @return the feasibility expirationdate
	 */
	public Date getFeasibilityExpirationdate() {
		return feasibilityExpirationdate;
	}

	/**
	 * Sets the feasibility expirationdate.
	 *
	 * @param feasibilityExpirationdate the new feasibility expirationdate
	 */
	public void setFeasibilityExpirationdate(Date feasibilityExpirationdate) {
		this.feasibilityExpirationdate = feasibilityExpirationdate;
	}

	/**
	 * Gets the lns clec str id.
	 *
	 * @return the lns clec str id
	 */
	public String getLnsClecStrId() {
		return lnsClecStrId;
	}

	/**
	 * Sets the lns clec str id.
	 *
	 * @param lnsClecStrId the new lns clec str id
	 */
	public void setLnsClecStrId(String lnsClecStrId) {
		this.lnsClecStrId = lnsClecStrId;
	}

	/**
	 * Gets the igloo sse access expiry date.
	 *
	 * @return the igloo sse access expiry date
	 */
	public Date getIglooSseAccessExpiryDate() {
		return iglooSseAccessExpiryDate;
	}

	/**
	 * Sets the igloo sse access expiry date.
	 *
	 * @param iglooSseAccessExpiryDate the new igloo sse access expiry date
	 */
	public void setIglooSseAccessExpiryDate(Date iglooSseAccessExpiryDate) {
		this.iglooSseAccessExpiryDate = iglooSseAccessExpiryDate;
	}

	/**
	 * Gets the extended date YN.
	 *
	 * @return the extended date YN
	 */
	public String getExtendedDateYN() {
		return extendedDateYN;
	}

	/**
	 * Sets the extended date YN.
	 *
	 * @param extendedDateYN the new extended date YN
	 */
	public void setExtendedDateYN(String extendedDateYN) {
		this.extendedDateYN = extendedDateYN;
	}

	/**
	 * Gets the interface list.
	 *
	 * @return the interface list
	 */
	public List<Object> getInterfaceList() {
		return interfaceList;
	}

	/**
	 * Sets the interface list.
	 *
	 * @param interfaceList the new interface list
	 */
	public void setInterfaceList(List<Object> interfaceList) {
		this.interfaceList = interfaceList;
	}

	/**
	 * Gets the pricing status msg.
	 *
	 * @return the pricing status msg
	 */
	public String getPricingStatusMsg() {
		return pricingStatusMsg;
	}

	/**
	 * Sets the pricing status msg.
	 *
	 * @param pricingStatusMsg the new pricing status msg
	 */
	public void setPricingStatusMsg(String pricingStatusMsg) {
		this.pricingStatusMsg = pricingStatusMsg;
	}

	/**
	 * Gets the offer.
	 *
	 * @return the offer
	 */
	public String getOffer() {
		return offer;
	}

	/**
	 * Sets the offer.
	 *
	 * @param offer the new offer
	 */
	public void setOffer(String offer) {
		this.offer = offer;
	}
	

}
