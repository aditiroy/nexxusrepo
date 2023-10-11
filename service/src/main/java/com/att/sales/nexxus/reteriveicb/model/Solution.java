package com.att.sales.nexxus.reteriveicb.model;

/*
 * @Author: Akash Arya
 * 
 * 
 */
import java.util.List;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;




/**
 * The Class Solution.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Solution {

	private String ppcosUser;

	public String getPpcosUser() {
		return ppcosUser;
	}

	public void setPpcosUser(String ppcosUser) {
		this.ppcosUser = ppcosUser;
	}

	/** The solution determinants. */
	private SolutionDeterminants solutionDeterminants;

	/** The user id. */
	private String userId;

	/** The external key. */
	private Long externalKey;

	/** The price scenario id. */
	private Long priceScenarioId;

	/** The erate ind. */
	private String erateInd;

	/** The bundle code. */
	private String bundleCode;

	/** The promotion code. */
	private String promotionCode;

	/** The before external key. */
	private Long beforeExternalKey;

	/** The net bond. */
	private String netBond;

	/** The bulk ind. */
	private String bulkInd;

	/** The opty id. */
	private String optyId;

	/** The solution id. */
	private Long solutionId;

	/** The icb ind. */
	private String icbInd;

	/** The user manager ATTUID. */
	private String userManagerATTUID;

	/** The user role. */
	private String userRole;

	/** The user first name. */
	private String userFirstName;

	/** The user last name. */
	private String userLastName;

	/** The opportunity name. */
	private String opportunityName;

	/** The account id. */
	private String accountId;

	/** The opty currency. */
	private String optyCurrency;

	/** The offer type. */
	private String offerType;

	/** The opportunity type. */
	private String opportunityType;

	/** The source opty id. */
	private String sourceOptyId;

	/** The acquisition type. */
	private String acquisitionType;

	/** The market segment. */
	private String marketSegment;

	/** The market strata. */
	private String marketStrata;

	/** The rome solution ID. */
	private String romeSolutionID;

	/** The customer name. */
	private String customerName;

	/** The pricer D solution id. */
	@JsonProperty("pricerdSolutionId")
	private Long pricerDSolutionId;

	/** The adopt solution ID. */
	private String adoptSolutionID;

	/** The solution status. */
	private String solutionStatus;

	/** The contract term. */
	private Long contractTerm;

	/** The product fiber sale. */
	private String productFiberSale;

	/** The product tariff type. */
	private String productTariffType;

	/** The accessAutomation. */
	private String accessAutomation;

	/** The external key name. */
	private String externalKeyName;

	/** The sales channel. */
	private String salesChannel;

	/** The crdd. */
	private String crdd;

	/** The design type. */
	private String designType;

	private Long imsDealNumber;
	private Long imsProductNumber;
	private Long imsVersionNumber;
	private Double annualRevenue;
	private Double monthlyRevenue;
	private String dateSoldToCustomer;
	private String endCustomerName;
	private String cancellationReason;
	private String automationInd;
	private Long quantity;

	/** The contact. */
	private List<Contact> contact;

	/** The offers. */
	private List<Offer> offers;

	/** The price plan details. */
	private List<PricePlanDetails> pricePlanDetails;

	/** The vpn. */
	private VpnObject vpn;

	/** The diversity arrangement. */
	private DiversityArrangement diversityArrangement;

	private String sAARTAccountNumber;
	private String solutionName;
	private String sourceName;
	private String layer;
	private String erateFormNumber;
	private String userTitle;
	private String userWorkPhone;
	private String userMobile;
	private String userEmail;
	private List<String> opportunitySalesTeam;
	private String caseType;
	private String presaleExpediteIndicator;
	private String presalesExpediteDate;
	private String presalesExpediteComments;
	private String specialConstructionContractUrl;
	private String contractType;
	private String standardPricingInd;
	private String contractNumber;
	private String submitToMyprice;
	private String mpsIndicator;
	private String priceModifiedInd;
	private String diverseAccessInd;
	private long solutionVersion;
	
	public long getSolutionVersion() {
		return solutionVersion;
	}

	public void setSolutionVersion(long solutionVersion) {
		this.solutionVersion = solutionVersion;
	}

	public String getDiverseAccessInd() {
		return diverseAccessInd;
	}

	public void setDiverseAccessInd(String diverseAccessInd) {
		this.diverseAccessInd = diverseAccessInd;
	}

	public String getPriceModifiedInd() {
		return priceModifiedInd;
	}

	public void setPriceModifiedInd(String priceModifiedInd) {
		this.priceModifiedInd = priceModifiedInd;
	}

	public String getSubmitToMyprice() {
		return submitToMyprice;
	}

	public void setSubmitToMyprice(String submitToMyprice) {
		this.submitToMyprice = submitToMyprice;
	}
	
	public String getContractNumber() {
		return contractNumber;
	}

	public void setContractNumber(String contractNumber) {
		this.contractNumber = contractNumber;
	}

	public String getStandardPricingInd() {
		return standardPricingInd;
	}

	public void setStandardPricingInd(String standardPricingInd) {
		this.standardPricingInd = standardPricingInd;
	}

	/**
	 * Gets the vpn.
	 *
	 * @return the vpn
	 */
	public VpnObject getVpn() {
		return vpn;
	}

	/**
	 * Sets the vpn.
	 *
	 * @param vpn the new vpn
	 */
	public void setVpn(VpnObject vpn) {
		this.vpn = vpn;
	}

	/**
	 * Gets the diversity arrangement.
	 *
	 * @return the diversity arrangement
	 */
	public DiversityArrangement getDiversityArrangement() {
		return diversityArrangement;
	}

	/**
	 * Sets the diversity arrangement.
	 *
	 * @param diversityArrangement the new diversity arrangement
	 */
	public void setDiversityArrangement(DiversityArrangement diversityArrangement) {
		this.diversityArrangement = diversityArrangement;
	}

	/**
	 * Gets the solution determinants.
	 *
	 * @return the solution determinants
	 */
	public SolutionDeterminants getSolutionDeterminants() {
		return solutionDeterminants;
	}

	/**
	 * Sets the solution determinants.
	 *
	 * @param solutionDeterminants the new solution determinants
	 */
	public void setSolutionDeterminants(SolutionDeterminants solutionDeterminants) {
		this.solutionDeterminants = solutionDeterminants;
	}

	/**
	 * Gets the user id.
	 *
	 * @return the user id
	 */
	public String getUserId() {
		return userId;
	}

	/**
	 * Sets the user id.
	 *
	 * @param userId the new user id
	 */
	public void setUserId(String userId) {
		this.userId = userId;
	}

	/**
	 * Gets the external key.
	 *
	 * @return the external key
	 */
	public Long getExternalKey() {
		return externalKey;
	}

	/**
	 * Sets the external key.
	 *
	 * @param externalKey the new external key
	 */
	public void setExternalKey(Long externalKey) {
		this.externalKey = externalKey;
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
	 * Gets the erate ind.
	 *
	 * @return the erate ind
	 */
	public String getErateInd() {
		return erateInd;
	}

	/**
	 * Sets the erate ind.
	 *
	 * @param erateInd the new erate ind
	 */
	public void setErateInd(String erateInd) {
		this.erateInd = erateInd;
	}

	public String getBundleCode() {
		return bundleCode;
	}

	public void setBundleCode(String bundleCode) {
		this.bundleCode = bundleCode;
	}

	/**
	 * Gets the promotion code.
	 *
	 * @return the promotion code
	 */
	public String getPromotionCode() {
		return promotionCode;
	}

	/**
	 * Sets the promotion code.
	 *
	 * @param promotionCode the new promotion code
	 */
	public void setPromotionCode(String promotionCode) {
		this.promotionCode = promotionCode;
	}

	/**
	 * Gets the before external key.
	 *
	 * @return the before external key
	 */
	public Long getBeforeExternalKey() {
		return beforeExternalKey;
	}

	/**
	 * Sets the before external key.
	 *
	 * @param beforeExternalKey the new before external key
	 */
	public void setBeforeExternalKey(Long beforeExternalKey) {
		this.beforeExternalKey = beforeExternalKey;
	}

	/**
	 * Gets the offers.
	 *
	 * @return the offers
	 */
	public List<Offer> getOffers() {
		return offers;
	}

	/**
	 * Sets the offers.
	 *
	 * @param offers the new offers
	 */
	public void setOffers(List<Offer> offers) {
		this.offers = offers;
	}

	/**
	 * Gets the price plan details.
	 *
	 * @return the price plan details
	 */
	public List<PricePlanDetails> getPricePlanDetails() {
		return pricePlanDetails;
	}

	/**
	 * Sets the price plan details.
	 *
	 * @param pricePlanDetails the new price plan details
	 */
	public void setPricePlanDetails(List<PricePlanDetails> pricePlanDetails) {
		this.pricePlanDetails = pricePlanDetails;
	}

	/**
	 * Gets the net bond.
	 *
	 * @return the net bond
	 */
	public String getNetBond() {
		return netBond;
	}

	/**
	 * Sets the net bond.
	 *
	 * @param netBond the new net bond
	 */
	public void setNetBond(String netBond) {
		this.netBond = netBond;
	}

	/**
	 * Gets the bulk ind.
	 *
	 * @return the bulk ind
	 */
	public String getBulkInd() {
		return bulkInd;
	}

	/**
	 * Sets the bulk ind.
	 *
	 * @param bulkInd the new bulk ind
	 */
	public void setBulkInd(String bulkInd) {
		this.bulkInd = bulkInd;
	}

	/**
	 * Gets the opty id.
	 *
	 * @return the opty id
	 */
	public String getOptyId() {
		return optyId;
	}

	/**
	 * Sets the opty id.
	 *
	 * @param optyId the new opty id
	 */
	public void setOptyId(String optyId) {
		this.optyId = optyId;
	}

	/**
	 * Gets the solution id.
	 *
	 * @return the solution id
	 */
	public Long getSolutionId() {
		return solutionId;
	}

	/**
	 * Sets the solution id.
	 *
	 * @param solutionId the new solution id
	 */
	public void setSolutionId(Long solutionId) {
		this.solutionId = solutionId;
	}

	/**
	 * Gets the icb ind.
	 *
	 * @return the icb ind
	 */
	public String getIcbInd() {
		return icbInd;
	}

	/**
	 * Sets the icb ind.
	 *
	 * @param icbInd the new icb ind
	 */
	public void setIcbInd(String icbInd) {
		this.icbInd = icbInd;
	}

	/**
	 * Gets the user manager ATTUID.
	 *
	 * @return the user manager ATTUID
	 */
	public String getUserManagerATTUID() {
		return userManagerATTUID;
	}

	/**
	 * Sets the user manager ATTUID.
	 *
	 * @param userManagerATTUID the new user manager ATTUID
	 */
	public void setUserManagerATTUID(String userManagerATTUID) {
		this.userManagerATTUID = userManagerATTUID;
	}

	/**
	 * Gets the user role.
	 *
	 * @return the user role
	 */
	public String getUserRole() {
		return userRole;
	}

	/**
	 * Sets the user role.
	 *
	 * @param userRole the new user role
	 */
	public void setUserRole(String userRole) {
		this.userRole = userRole;
	}

	/**
	 * Gets the user first name.
	 *
	 * @return the user first name
	 */
	public String getUserFirstName() {
		return userFirstName;
	}

	/**
	 * Sets the user first name.
	 *
	 * @param userFirstName the new user first name
	 */
	public void setUserFirstName(String userFirstName) {
		this.userFirstName = userFirstName;
	}

	/**
	 * Gets the user last name.
	 *
	 * @return the user last name
	 */
	public String getUserLastName() {
		return userLastName;
	}

	/**
	 * Sets the user last name.
	 *
	 * @param userLastName the new user last name
	 */
	public void setUserLastName(String userLastName) {
		this.userLastName = userLastName;
	}

	/**
	 * Gets the opportunity name.
	 *
	 * @return the opportunity name
	 */
	public String getOpportunityName() {
		return opportunityName;
	}

	/**
	 * Sets the opportunity name.
	 *
	 * @param opportunityName the new opportunity name
	 */
	public void setOpportunityName(String opportunityName) {
		this.opportunityName = opportunityName;
	}

	/**
	 * Gets the account id.
	 *
	 * @return the account id
	 */
	public String getAccountId() {
		return accountId;
	}

	/**
	 * Sets the account id.
	 *
	 * @param accountId the new account id
	 */
	public void setAccountId(String accountId) {
		this.accountId = accountId;
	}

	/**
	 * Gets the opty currency.
	 *
	 * @return the opty currency
	 */
	public String getOptyCurrency() {
		return optyCurrency;
	}

	/**
	 * Sets the opty currency.
	 *
	 * @param optyCurrency the new opty currency
	 */
	public void setOptyCurrency(String optyCurrency) {
		this.optyCurrency = optyCurrency;
	}

	/**
	 * Gets the offer type.
	 *
	 * @return the offer type
	 */
	public String getOfferType() {
		return offerType;
	}

	/**
	 * Sets the offer type.
	 *
	 * @param offerType the new offer type
	 */
	public void setOfferType(String offerType) {
		this.offerType = offerType;
	}

	/**
	 * Gets the opportunity type.
	 *
	 * @return the opportunity type
	 */
	public String getOpportunityType() {
		return opportunityType;
	}

	/**
	 * Sets the opportunity type.
	 *
	 * @param opportunityType the new opportunity type
	 */
	public void setOpportunityType(String opportunityType) {
		this.opportunityType = opportunityType;
	}

	/**
	 * Gets the source opty id.
	 *
	 * @return the source opty id
	 */
	public String getSourceOptyId() {
		return sourceOptyId;
	}

	/**
	 * Sets the source opty id.
	 *
	 * @param sourceOptyId the new source opty id
	 */
	public void setSourceOptyId(String sourceOptyId) {
		this.sourceOptyId = sourceOptyId;
	}

	/**
	 * Gets the acquisition type.
	 *
	 * @return the acquisition type
	 */
	public String getAcquisitionType() {
		return acquisitionType;
	}

	/**
	 * Sets the acquisition type.
	 *
	 * @param acquisitionType the new acquisition type
	 */
	public void setAcquisitionType(String acquisitionType) {
		this.acquisitionType = acquisitionType;
	}

	/**
	 * Gets the market segment.
	 *
	 * @return the market segment
	 */
	public String getMarketSegment() {
		return marketSegment;
	}

	/**
	 * Sets the market segment.
	 *
	 * @param marketSegment the new market segment
	 */
	public void setMarketSegment(String marketSegment) {
		this.marketSegment = marketSegment;
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
	 * Gets the rome solution ID.
	 *
	 * @return the rome solution ID
	 */
	public String getRomeSolutionID() {
		return romeSolutionID;
	}

	/**
	 * Sets the rome solution ID.
	 *
	 * @param romeSolutionID the new rome solution ID
	 */
	public void setRomeSolutionID(String romeSolutionID) {
		this.romeSolutionID = romeSolutionID;
	}

	/**
	 * Gets the adopt solution ID.
	 *
	 * @return the adopt solution ID
	 */
	public String getAdoptSolutionID() {
		return adoptSolutionID;
	}

	public Long getPricerDSolutionId() {
		return pricerDSolutionId;
	}

	public void setPricerDSolutionId(Long pricerDSolutionId) {
		this.pricerDSolutionId = pricerDSolutionId;
	}

	/**
	 * Sets the adopt solution ID.
	 *
	 * @param adoptSolutionID the new adopt solution ID
	 */
	public void setAdoptSolutionID(String adoptSolutionID) {
		this.adoptSolutionID = adoptSolutionID;
	}

	/**
	 * Gets the solution status.
	 *
	 * @return the solution status
	 */
	public String getSolutionStatus() {
		return solutionStatus;
	}

	/**
	 * Sets the solution status.
	 *
	 * @param solutionStatus the new solution status
	 */
	public void setSolutionStatus(String solutionStatus) {
		this.solutionStatus = solutionStatus;
	}

	/**
	 * Gets the contract term.
	 *
	 * @return the contract term
	 */
	public Long getContractTerm() {
		return contractTerm;
	}

	/**
	 * Sets the contract term.
	 *
	 * @param contractTerm the new contract term
	 */
	public void setContractTerm(Long contractTerm) {
		this.contractTerm = contractTerm;
	}

	/**
	 * Gets the product fiber sale.
	 *
	 * @return the product fiber sale
	 */
	public String getProductFiberSale() {
		return productFiberSale;
	}

	/**
	 * Sets the product fiber sale.
	 *
	 * @param productFiberSale the new product fiber sale
	 */
	public void setProductFiberSale(String productFiberSale) {
		this.productFiberSale = productFiberSale;
	}

	/**
	 * Gets the product tariff type.
	 *
	 * @return the product tariff type
	 */
	public String getProductTariffType() {
		return productTariffType;
	}

	/**
	 * Sets the product tariff type.
	 *
	 * @param productTariffType the new product tariff type
	 */
	public void setProductTariffType(String productTariffType) {
		this.productTariffType = productTariffType;
	}

	/**
	 * Gets the external key name.
	 *
	 * @return the external key name
	 */
	public String getExternalKeyName() {
		return externalKeyName;
	}

	/**
	 * Sets the external key name.
	 *
	 * @param externalKeyName the new external key name
	 */
	public void setExternalKeyName(String externalKeyName) {
		this.externalKeyName = externalKeyName;
	}

	/**
	 * Gets the sales channel.
	 *
	 * @return the sales channel
	 */
	public String getSalesChannel() {
		return salesChannel;
	}

	/**
	 * Sets the sales channel.
	 *
	 * @param salesChannel the new sales channel
	 */
	public void setSalesChannel(String salesChannel) {
		this.salesChannel = salesChannel;
	}

	/**
	 * Gets the crdd.
	 *
	 * @return the crdd
	 */
	public String getCrdd() {
		return crdd;
	}

	/**
	 * Sets the crdd.
	 *
	 * @param crdd the new crdd
	 */
	public void setCrdd(String crdd) {
		this.crdd = crdd;
	}

	public Long getImsDealNumber() {
		return imsDealNumber;
	}

	public void setImsDealNumber(Long imsDealNumber) {
		this.imsDealNumber = imsDealNumber;
	}

	public Long getImsProductNumber() {
		return imsProductNumber;
	}

	public void setImsProductNumber(Long imsProductNumber) {
		this.imsProductNumber = imsProductNumber;
	}

	public Long getImsVersionNumber() {
		return imsVersionNumber;
	}

	public void setImsVersionNumber(Long imsVersionNumber) {
		this.imsVersionNumber = imsVersionNumber;
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

	/**
	 * Gets the design type.
	 *
	 * @return the design type
	 */
	public String getDesignType() {
		return designType;
	}

	/**
	 * Sets the design type.
	 *
	 * @param designType the new design type
	 */
	public void setDesignType(String designType) {
		this.designType = designType;
	}

	public List<Contact> getContact() {
		return contact;
	}

	public void setContact(List<Contact> contact) {
		this.contact = contact;
	}

	public String getEndCustomerName() {
		return endCustomerName;
	}

	public void setEndCustomerName(String endCustomerName) {
		this.endCustomerName = endCustomerName;
	}

	public String getCancellationReason() {
		return cancellationReason;
	}

	public void setCancellationReason(String cancellationReason) {
		this.cancellationReason = cancellationReason;
	}

	public String getAutomationInd() {
		return automationInd;
	}

	public void setAutomationInd(String automationInd) {
		this.automationInd = automationInd;
	}

	public Long getQuantity() {
		return quantity;
	}

	public void setQuantity(Long quantity) {
		this.quantity = quantity;
	}

	public String getAccessAutomation() {
		return accessAutomation;
	}

	public void setAccessAutomation(String accessAutomation) {
		this.accessAutomation = accessAutomation;
	}

	public String getsAARTAccountNumber() {
		return sAARTAccountNumber;
	}

	public void setsAARTAccountNumber(String sAARTAccountNumber) {
		this.sAARTAccountNumber = sAARTAccountNumber;
	}

	public String getSolutionName() {
		return solutionName;
	}

	public void setSolutionName(String solutionName) {
		this.solutionName = solutionName;
	}

	public String getSourceName() {
		return sourceName;
	}

	public void setSourceName(String sourceName) {
		this.sourceName = sourceName;
	}

	public String getLayer() {
		return layer;
	}

	public void setLayer(String layer) {
		this.layer = layer;
	}

	public String getErateFormNumber() {
		return erateFormNumber;
	}

	public void setErateFormNumber(String erateFormNumber) {
		this.erateFormNumber = erateFormNumber;
	}

	public String getUserTitle() {
		return userTitle;
	}

	public void setUserTitle(String userTitle) {
		this.userTitle = userTitle;
	}

	public String getUserWorkPhone() {
		return userWorkPhone;
	}

	public void setUserWorkPhone(String userWorkPhone) {
		this.userWorkPhone = userWorkPhone;
	}

	public String getUserMobile() {
		return userMobile;
	}

	public void setUserMobile(String userMobile) {
		this.userMobile = userMobile;
	}

	public String getUserEmail() {
		return userEmail;
	}

	public void setUserEmail(String userEmail) {
		this.userEmail = userEmail;
	}

	public List<String> getOpportunitySalesTeam() {
		return opportunitySalesTeam;
	}

	public void setOpportunitySalesTeam(List<String> opportunitySalesTeam) {
		this.opportunitySalesTeam = opportunitySalesTeam;
	}

	public String getCaseType() {
		return caseType;
	}

	public void setCaseType(String caseType) {
		this.caseType = caseType;
	}

	public String getPresaleExpediteIndicator() {
		return presaleExpediteIndicator;
	}

	public void setPresaleExpediteIndicator(String presaleExpediteIndicator) {
		this.presaleExpediteIndicator = presaleExpediteIndicator;
	}

	public String getPresalesExpediteDate() {
		return presalesExpediteDate;
	}

	public void setPresalesExpediteDate(String presalesExpediteDate) {
		this.presalesExpediteDate = presalesExpediteDate;
	}

	public String getPresalesExpediteComments() {
		return presalesExpediteComments;
	}

	public void setPresalesExpediteComments(String presalesExpediteComments) {
		this.presalesExpediteComments = presalesExpediteComments;
	}

	public String getSpecialConstructionContractUrl() {
		return specialConstructionContractUrl;
	}

	public void setSpecialConstructionContractUrl(String specialConstructionContractUrl) {
		this.specialConstructionContractUrl = specialConstructionContractUrl;
	}

	public String getContractType() {
		return contractType;
	}

	public void setContractType(String contractType) {
		this.contractType = contractType;
	}

	public String getMpsIndicator() {
		return mpsIndicator;
	}

	public void setMpsIndicator(String mpsIndicator) {
		this.mpsIndicator = mpsIndicator;
	}

	@Override
	public String toString() {
		return "Solution [solutionDeterminants=" + solutionDeterminants + ", userId=" + userId + ", externalKey="
				+ externalKey + ", priceScenarioId=" + priceScenarioId + ", erateInd=" + erateInd + ", bundleCode="
				+ bundleCode + ", promotionCode=" + promotionCode + ", beforeExternalKey=" + beforeExternalKey
				+ ", netBond=" + netBond + ", bulkInd=" + bulkInd + ", optyId=" + optyId + ", solutionId=" + solutionId
				+ ", icbInd=" + icbInd + ", userManagerATTUID=" + userManagerATTUID + ", userRole=" + userRole
				+ ", userFirstName=" + userFirstName + ", userLastName=" + userLastName + ", opportunityName="
				+ opportunityName + ", accountId=" + accountId + ", optyCurrency=" + optyCurrency + ", offerType="
				+ offerType + ", opportunityType=" + opportunityType + ", sourceOptyId=" + sourceOptyId
				+ ", acquisitionType=" + acquisitionType + ", marketSegment=" + marketSegment + ", marketStrata="
				+ marketStrata + ", romeSolutionID=" + romeSolutionID + ", customerName=" + customerName
				+ ", pricerDSolutionId=" + pricerDSolutionId + ", adoptSolutionID=" + adoptSolutionID
				+ ", solutionStatus=" + solutionStatus + ", contractTerm=" + contractTerm + ", productFiberSale="
				+ productFiberSale + ", productTariffType=" + productTariffType + ", accessAutomation="
				+ accessAutomation + ", externalKeyName=" + externalKeyName + ", salesChannel=" + salesChannel
				+ ", crdd=" + crdd + ", designType=" + designType + ", imsDealNumber=" + imsDealNumber
				+ ", imsProductNumber=" + imsProductNumber + ", imsVersionNumber=" + imsVersionNumber
				+ ", annualRevenue=" + annualRevenue + ", monthlyRevenue=" + monthlyRevenue + ", dateSoldToCustomer="
				+ dateSoldToCustomer + ", endCustomerName=" + endCustomerName + ", cancellationReason="
				+ cancellationReason + ", automationInd=" + automationInd + ", quantity=" + quantity + ", contact="
				+ contact + ", offers=" + offers + ", pricePlanDetails=" + pricePlanDetails + ", vpn=" + vpn
				+ ", diversityArrangement=" + diversityArrangement + ", sAARTAccountNumber=" + sAARTAccountNumber
				+ ", solutionName=" + solutionName + ", sourceName=" + sourceName + ", layer=" + layer
				+ ", erateFormNumber=" + erateFormNumber + ", userTitle=" + userTitle + ", userWorkPhone="
				+ userWorkPhone + ", userMobile=" + userMobile + ", userEmail=" + userEmail + ", opportunitySalesTeam="
				+ opportunitySalesTeam + ", caseType=" + caseType + ", presaleExpediteIndicator="
				+ presaleExpediteIndicator + ", presalesExpediteDate=" + presalesExpediteDate
				+ ", presalesExpediteComments=" + presalesExpediteComments + ", specialConstructionContractUrl="
				+ specialConstructionContractUrl + ", contractType=" + contractType + ", standardPricingInd="
				+ standardPricingInd + "]";
	}
	
}
