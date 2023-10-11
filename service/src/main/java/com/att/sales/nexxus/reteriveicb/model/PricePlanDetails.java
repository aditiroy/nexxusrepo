package com.att.sales.nexxus.reteriveicb.model;
/*
 * @Author: Akash Arya
 *  
 */
import java.util.List;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.map.annotate.JsonSerialize;


/**
 * The Class PricePlanDetails.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class PricePlanDetails {
	
	/** The external price plan id. */
	private String externalPricePlanId;
	
	/** The term. */
	//private String discountApplied;
	private Long term;
	
	/** The marc. */
	private Long marc;
	
	/** The extended term id. */
	private Long extendedTermId;
	
	/** The rate plan id. */
	private String ratePlanId;
	
	/** The auth id. */
	private String authId;
	
	/** The auth val. */
	private String authVal;
	
	/** The country cd. */
	private String countryCd;
	
	/** The offer id. */
	private String offerId;
	
	/** The region CD. */
	private String regionCD;
	
	/** The Segment. */
	private String Segment;
	
	/** The min applicable term. */
	private Long minApplicableTerm;
	
	/** The rate letter exp date. */
	private String rateLetterExpDate;
	
	/** The access price exp date. */
	private String accessPriceExpDate;
	
	/** The terms conditions YN. */
	private String termsConditionsYN;
	
	/** The terms conditions note. */
	private String termsConditionsNote;
	
	/** The stare compare YN. */
	private String stareCompareYN;
	
	/** The pricing source id. */
	private String pricingSourceId;
	
	/** The access source id. */
	private Long accessSourceId;
	
	/** The gcp contract id. */
	private Long gcpContractId;
	
	/** The contract nego action. */
	private String contractNegoAction;
	
	/** The custom price plan YN. */
	private String customPricePlanYN;
	
	/** The customer signed YN. */
	private String customerSignedYN;
	
	/** The contract impacting YN. */
	private String contractImpactingYN;
	
	/** The sales exec pricing level. */
	private String salesExecPricingLevel;
	
	/** The target currency. */
	private String targetCurrency;
	
	/** The fx version id. */
	private Long fxVersionId;
	
	/** The fx version date. */
	private String fxVersionDate;
	
	/** The expired SOC appov YN. */
	private String expiredSOCAppovYN;
	
	/** The currency ver approved. */
	private String currencyVerApproved;
	
	/** The mdgf attached YN. */
	private String mdgfAttachedYN;
	
	/** The isb billed. */
	private String isbBilled;
	
	/** The icb disc appliedby gcs YN. */
	private String icbDiscAppliedbyGcsYN;
	
	/** The sales pricing level. */
	private String salesPricingLevel;
	
	/** The promo applied YN. */
	private String promoAppliedYN;
	
	/** The abn save promo. */
	private String abnSavePromo;
	
	/** The has aggregate billing. */
	private String hasAggregateBilling;
	
	/** The has revenue discount. */
	private String hasRevenueDiscount;
	
	/** The coterminous indicator. */
	private String coterminousIndicator;
	
	/** The service coverage type. */
	private String serviceCoverageType;
	
	/** The bundled service name. */
	private String bundledServiceName;
	
	/** The bundled marc YN. */
	private String bundledMarcYN;
	
	/** The coterminous exp date. */
	private String coterminousExpDate;
	
	/** The is country agnostic YN. */
	private String isCountryAgnosticYN;
	
	/** The override YN. */
	private String overrideYN;
	
	/** The type of inventory. */
	//New property added 
	private String typeOfInventory;
	
	/** The access pricing offer name. */
	private String accessPricingOfferName;

	
	/** The discount details. */
	private List<DiscountDetails> discountDetails;

	/**
	 * Gets the external price plan id.
	 *
	 * @return the external price plan id
	 */
	public String getExternalPricePlanId() {
		return externalPricePlanId;
	}

	/**
	 * Sets the external price plan id.
	 *
	 * @param externalPricePlanId the new external price plan id
	 */
	public void setExternalPricePlanId(String externalPricePlanId) {
		this.externalPricePlanId = externalPricePlanId;
	}

	
/*	public String getDiscountApplied() {
		return discountApplied;
	}

	public void setDiscountApplied(String discountApplied) {
		this.discountApplied = discountApplied;
	}*/

	/**
 * Gets the term.
 *
 * @return the term
 */
public Long getTerm() {
		return term;
	}

	/**
	 * Sets the term.
	 *
	 * @param term the new term
	 */
	public void setTerm(Long term) {
		this.term = term;
	}

	/**
	 * Gets the marc.
	 *
	 * @return the marc
	 */
	public Long getMarc() {
		return marc;
	}

	/**
	 * Sets the marc.
	 *
	 * @param marc the new marc
	 */
	public void setMarc(Long marc) {
		this.marc = marc;
	}

	/**
	 * Gets the extended term id.
	 *
	 * @return the extended term id
	 */
	public Long getExtendedTermId() {
		return extendedTermId;
	}

	/**
	 * Sets the extended term id.
	 *
	 * @param extendedTermId the new extended term id
	 */
	public void setExtendedTermId(Long extendedTermId) {
		this.extendedTermId = extendedTermId;
	}

	/**
	 * Gets the rate plan id.
	 *
	 * @return the rate plan id
	 */
	public String getRatePlanId() {
		return ratePlanId;
	}

	/**
	 * Sets the rate plan id.
	 *
	 * @param ratePlanId the new rate plan id
	 */
	public void setRatePlanId(String ratePlanId) {
		this.ratePlanId = ratePlanId;
	}

	/**
	 * Gets the auth id.
	 *
	 * @return the auth id
	 */
	public String getAuthId() {
		return authId;
	}

	/**
	 * Sets the auth id.
	 *
	 * @param authId the new auth id
	 */
	public void setAuthId(String authId) {
		this.authId = authId;
	}

	/**
	 * Gets the auth val.
	 *
	 * @return the auth val
	 */
	public String getAuthVal() {
		return authVal;
	}

	/**
	 * Sets the auth val.
	 *
	 * @param authVal the new auth val
	 */
	public void setAuthVal(String authVal) {
		this.authVal = authVal;
	}

	/**
	 * Gets the country cd.
	 *
	 * @return the country cd
	 */
	public String getCountryCd() {
		return countryCd;
	}

	/**
	 * Sets the country cd.
	 *
	 * @param countryCd the new country cd
	 */
	public void setCountryCd(String countryCd) {
		this.countryCd = countryCd;
	}

	/**
	 * Gets the offer id.
	 *
	 * @return the offer id
	 */
	public String getOfferId() {
		return offerId;
	}

	/**
	 * Sets the offer id.
	 *
	 * @param offerId the new offer id
	 */
	public void setOfferId(String offerId) {
		this.offerId = offerId;
	}

	/**
	 * Gets the region CD.
	 *
	 * @return the region CD
	 */
	public String getRegionCD() {
		return regionCD;
	}

	/**
	 * Sets the region CD.
	 *
	 * @param regionCD the new region CD
	 */
	public void setRegionCD(String regionCD) {
		this.regionCD = regionCD;
	}


	/**
	 * Gets the segment.
	 *
	 * @return the segment
	 */
	public String getSegment() {
		return Segment;
	}

	/**
	 * Sets the segment.
	 *
	 * @param segment the new segment
	 */
	public void setSegment(String segment) {
		Segment = segment;
	}

	/**
	 * Gets the min applicable term.
	 *
	 * @return the min applicable term
	 */
	public Long getMinApplicableTerm() {
		return minApplicableTerm;
	}

	/**
	 * Sets the min applicable term.
	 *
	 * @param minApplicableTerm the new min applicable term
	 */
	public void setMinApplicableTerm(Long minApplicableTerm) {
		this.minApplicableTerm = minApplicableTerm;
	}

	/**
	 * Gets the rate letter exp date.
	 *
	 * @return the rate letter exp date
	 */
	public String getRateLetterExpDate() {
		return rateLetterExpDate;
	}

	/**
	 * Sets the rate letter exp date.
	 *
	 * @param rateLetterExpDate the new rate letter exp date
	 */
	public void setRateLetterExpDate(String rateLetterExpDate) {
		this.rateLetterExpDate = rateLetterExpDate;
	}

	/**
	 * Gets the access price exp date.
	 *
	 * @return the access price exp date
	 */
	public String getAccessPriceExpDate() {
		return accessPriceExpDate;
	}

	/**
	 * Sets the access price exp date.
	 *
	 * @param accessPriceExpDate the new access price exp date
	 */
	public void setAccessPriceExpDate(String accessPriceExpDate) {
		this.accessPriceExpDate = accessPriceExpDate;
	}

	/**
	 * Gets the terms conditions YN.
	 *
	 * @return the terms conditions YN
	 */
	public String getTermsConditionsYN() {
		return termsConditionsYN;
	}

	/**
	 * Sets the terms conditions YN.
	 *
	 * @param termsConditionsYN the new terms conditions YN
	 */
	public void setTermsConditionsYN(String termsConditionsYN) {
		this.termsConditionsYN = termsConditionsYN;
	}
	

	/**
	 * Gets the terms conditions note.
	 *
	 * @return the terms conditions note
	 */
	public String getTermsConditionsNote() {
		return termsConditionsNote;
	}

	/**
	 * Sets the terms conditions note.
	 *
	 * @param termsConditionsNote the new terms conditions note
	 */
	public void setTermsConditionsNote(String termsConditionsNote) {
		this.termsConditionsNote = termsConditionsNote;
	}

	/**
	 * Gets the stare compare YN.
	 *
	 * @return the stare compare YN
	 */
	public String getStareCompareYN() {
		return stareCompareYN;
	}

	/**
	 * Sets the stare compare YN.
	 *
	 * @param stareCompareYN the new stare compare YN
	 */
	public void setStareCompareYN(String stareCompareYN) {
		this.stareCompareYN = stareCompareYN;
	}

	/**
	 * Gets the pricing source id.
	 *
	 * @return the pricing source id
	 */
	public String getPricingSourceId() {
		return pricingSourceId;
	}

	/**
	 * Sets the pricing source id.
	 *
	 * @param pricingSourceId the new pricing source id
	 */
	public void setPricingSourceId(String pricingSourceId) {
		this.pricingSourceId = pricingSourceId;
	}

	/**
	 * Gets the access source id.
	 *
	 * @return the access source id
	 */
	public Long getAccessSourceId() {
		return accessSourceId;
	}

	/**
	 * Sets the access source id.
	 *
	 * @param accessSourceId the new access source id
	 */
	public void setAccessSourceId(Long accessSourceId) {
		this.accessSourceId = accessSourceId;
	}

	/**
	 * Gets the gcp contract id.
	 *
	 * @return the gcp contract id
	 */
	public Long getGcpContractId() {
		return gcpContractId;
	}

	/**
	 * Sets the gcp contract id.
	 *
	 * @param gcpContractId the new gcp contract id
	 */
	public void setGcpContractId(Long gcpContractId) {
		this.gcpContractId = gcpContractId;
	}

	/**
	 * Gets the contract nego action.
	 *
	 * @return the contract nego action
	 */
	public String getContractNegoAction() {
		return contractNegoAction;
	}

	/**
	 * Sets the contract nego action.
	 *
	 * @param contractNegoAction the new contract nego action
	 */
	public void setContractNegoAction(String contractNegoAction) {
		this.contractNegoAction = contractNegoAction;
	}

	/**
	 * Gets the custom price plan YN.
	 *
	 * @return the custom price plan YN
	 */
	public String getCustomPricePlanYN() {
		return customPricePlanYN;
	}

	/**
	 * Sets the custom price plan YN.
	 *
	 * @param customPricePlanYN the new custom price plan YN
	 */
	public void setCustomPricePlanYN(String customPricePlanYN) {
		this.customPricePlanYN = customPricePlanYN;
	}

	/**
	 * Gets the customer signed YN.
	 *
	 * @return the customer signed YN
	 */
	public String getCustomerSignedYN() {
		return customerSignedYN;
	}

	/**
	 * Sets the customer signed YN.
	 *
	 * @param customerSignedYN the new customer signed YN
	 */
	public void setCustomerSignedYN(String customerSignedYN) {
		this.customerSignedYN = customerSignedYN;
	}

	/**
	 * Gets the contract impacting YN.
	 *
	 * @return the contract impacting YN
	 */
	public String getContractImpactingYN() {
		return contractImpactingYN;
	}

	/**
	 * Sets the contract impacting YN.
	 *
	 * @param contractImpactingYN the new contract impacting YN
	 */
	public void setContractImpactingYN(String contractImpactingYN) {
		this.contractImpactingYN = contractImpactingYN;
	}

	/**
	 * Gets the sales exec pricing level.
	 *
	 * @return the sales exec pricing level
	 */
	public String getSalesExecPricingLevel() {
		return salesExecPricingLevel;
	}

	/**
	 * Sets the sales exec pricing level.
	 *
	 * @param salesExecPricingLevel the new sales exec pricing level
	 */
	public void setSalesExecPricingLevel(String salesExecPricingLevel) {
		this.salesExecPricingLevel = salesExecPricingLevel;
	}

	/**
	 * Gets the target currency.
	 *
	 * @return the target currency
	 */
	public String getTargetCurrency() {
		return targetCurrency;
	}

	/**
	 * Sets the target currency.
	 *
	 * @param targetCurrency the new target currency
	 */
	public void setTargetCurrency(String targetCurrency) {
		this.targetCurrency = targetCurrency;
	}

	/**
	 * Gets the fx version id.
	 *
	 * @return the fx version id
	 */
	public Long getFxVersionId() {
		return fxVersionId;
	}

	/**
	 * Sets the fx version id.
	 *
	 * @param fxVersionId the new fx version id
	 */
	public void setFxVersionId(Long fxVersionId) {
		this.fxVersionId = fxVersionId;
	}

	/**
	 * Gets the fx version date.
	 *
	 * @return the fx version date
	 */
	public String getFxVersionDate() {
		return fxVersionDate;
	}

	/**
	 * Sets the fx version date.
	 *
	 * @param fxVersionDate the new fx version date
	 */
	public void setFxVersionDate(String fxVersionDate) {
		this.fxVersionDate = fxVersionDate;
	}

	/**
	 * Gets the expired SOC appov YN.
	 *
	 * @return the expired SOC appov YN
	 */
	public String getExpiredSOCAppovYN() {
		return expiredSOCAppovYN;
	}

	/**
	 * Sets the expired SOC appov YN.
	 *
	 * @param expiredSOCAppovYN the new expired SOC appov YN
	 */
	public void setExpiredSOCAppovYN(String expiredSOCAppovYN) {
		this.expiredSOCAppovYN = expiredSOCAppovYN;
	}

	/**
	 * Gets the currency ver approved.
	 *
	 * @return the currency ver approved
	 */
	public String getCurrencyVerApproved() {
		return currencyVerApproved;
	}

	/**
	 * Sets the currency ver approved.
	 *
	 * @param currencyVerApproved the new currency ver approved
	 */
	public void setCurrencyVerApproved(String currencyVerApproved) {
		this.currencyVerApproved = currencyVerApproved;
	}

	/**
	 * Gets the mdgf attached YN.
	 *
	 * @return the mdgf attached YN
	 */
	public String getMdgfAttachedYN() {
		return mdgfAttachedYN;
	}

	/**
	 * Sets the mdgf attached YN.
	 *
	 * @param mdgfAttachedYN the new mdgf attached YN
	 */
	public void setMdgfAttachedYN(String mdgfAttachedYN) {
		this.mdgfAttachedYN = mdgfAttachedYN;
	}

	/**
	 * Gets the isb billed.
	 *
	 * @return the isb billed
	 */
	public String getIsbBilled() {
		return isbBilled;
	}

	/**
	 * Sets the isb billed.
	 *
	 * @param isbBilled the new isb billed
	 */
	public void setIsbBilled(String isbBilled) {
		this.isbBilled = isbBilled;
	}

	/**
	 * Gets the icb disc appliedby gcs YN.
	 *
	 * @return the icb disc appliedby gcs YN
	 */
	public String getIcbDiscAppliedbyGcsYN() {
		return icbDiscAppliedbyGcsYN;
	}

	/**
	 * Sets the icb disc appliedby gcs YN.
	 *
	 * @param icbDiscAppliedbyGcsYN the new icb disc appliedby gcs YN
	 */
	public void setIcbDiscAppliedbyGcsYN(String icbDiscAppliedbyGcsYN) {
		this.icbDiscAppliedbyGcsYN = icbDiscAppliedbyGcsYN;
	}

	/**
	 * Gets the sales pricing level.
	 *
	 * @return the sales pricing level
	 */
	public String getSalesPricingLevel() {
		return salesPricingLevel;
	}

	/**
	 * Sets the sales pricing level.
	 *
	 * @param salesPricingLevel the new sales pricing level
	 */
	public void setSalesPricingLevel(String salesPricingLevel) {
		this.salesPricingLevel = salesPricingLevel;
	}

	/**
	 * Gets the promo applied YN.
	 *
	 * @return the promo applied YN
	 */
	public String getPromoAppliedYN() {
		return promoAppliedYN;
	}

	/**
	 * Sets the promo applied YN.
	 *
	 * @param promoAppliedYN the new promo applied YN
	 */
	public void setPromoAppliedYN(String promoAppliedYN) {
		this.promoAppliedYN = promoAppliedYN;
	}

	/**
	 * Gets the abn save promo.
	 *
	 * @return the abn save promo
	 */
	public String getAbnSavePromo() {
		return abnSavePromo;
	}

	/**
	 * Sets the abn save promo.
	 *
	 * @param abnSavePromo the new abn save promo
	 */
	public void setAbnSavePromo(String abnSavePromo) {
		this.abnSavePromo = abnSavePromo;
	}

	/**
	 * Gets the checks for aggregate billing.
	 *
	 * @return the checks for aggregate billing
	 */
	public String getHasAggregateBilling() {
		return hasAggregateBilling;
	}

	/**
	 * Sets the checks for aggregate billing.
	 *
	 * @param hasAggregateBilling the new checks for aggregate billing
	 */
	public void setHasAggregateBilling(String hasAggregateBilling) {
		this.hasAggregateBilling = hasAggregateBilling;
	}

	/**
	 * Gets the checks for revenue discount.
	 *
	 * @return the checks for revenue discount
	 */
	public String getHasRevenueDiscount() {
		return hasRevenueDiscount;
	}

	/**
	 * Sets the checks for revenue discount.
	 *
	 * @param hasRevenueDiscount the new checks for revenue discount
	 */
	public void setHasRevenueDiscount(String hasRevenueDiscount) {
		this.hasRevenueDiscount = hasRevenueDiscount;
	}

	/**
	 * Gets the coterminous indicator.
	 *
	 * @return the coterminous indicator
	 */
	public String getCoterminousIndicator() {
		return coterminousIndicator;
	}

	/**
	 * Sets the coterminous indicator.
	 *
	 * @param coterminousIndicator the new coterminous indicator
	 */
	public void setCoterminousIndicator(String coterminousIndicator) {
		this.coterminousIndicator = coterminousIndicator;
	}

	/**
	 * Gets the service coverage type.
	 *
	 * @return the service coverage type
	 */
	public String getServiceCoverageType() {
		return serviceCoverageType;
	}

	/**
	 * Sets the service coverage type.
	 *
	 * @param serviceCoverageType the new service coverage type
	 */
	public void setServiceCoverageType(String serviceCoverageType) {
		this.serviceCoverageType = serviceCoverageType;
	}

	/**
	 * Gets the bundled service name.
	 *
	 * @return the bundled service name
	 */
	public String getBundledServiceName() {
		return bundledServiceName;
	}

	/**
	 * Sets the bundled service name.
	 *
	 * @param bundledServiceName the new bundled service name
	 */
	public void setBundledServiceName(String bundledServiceName) {
		this.bundledServiceName = bundledServiceName;
	}

	/**
	 * Gets the bundled marc YN.
	 *
	 * @return the bundled marc YN
	 */
	public String getBundledMarcYN() {
		return bundledMarcYN;
	}

	/**
	 * Sets the bundled marc YN.
	 *
	 * @param bundledMarcYN the new bundled marc YN
	 */
	public void setBundledMarcYN(String bundledMarcYN) {
		this.bundledMarcYN = bundledMarcYN;
	}

	/**
	 * Gets the coterminous exp date.
	 *
	 * @return the coterminous exp date
	 */
	public String getCoterminousExpDate() {
		return coterminousExpDate;
	}

	/**
	 * Sets the coterminous exp date.
	 *
	 * @param coterminousExpDate the new coterminous exp date
	 */
	public void setCoterminousExpDate(String coterminousExpDate) {
		this.coterminousExpDate = coterminousExpDate;
	}

	/**
	 * Gets the checks if is country agnostic YN.
	 *
	 * @return the checks if is country agnostic YN
	 */
	public String getIsCountryAgnosticYN() {
		return isCountryAgnosticYN;
	}

	/**
	 * Sets the checks if is country agnostic YN.
	 *
	 * @param isCountryAgnosticYN the new checks if is country agnostic YN
	 */
	public void setIsCountryAgnosticYN(String isCountryAgnosticYN) {
		this.isCountryAgnosticYN = isCountryAgnosticYN;
	}

	/**
	 * Gets the override YN.
	 *
	 * @return the override YN
	 */
	public String getOverrideYN() {
		return overrideYN;
	}

	/**
	 * Sets the override YN.
	 *
	 * @param overrideYN the new override YN
	 */
	public void setOverrideYN(String overrideYN) {
		this.overrideYN = overrideYN;
	}

	/**
	 * Gets the discount details.
	 *
	 * @return the discount details
	 */
	public List<DiscountDetails> getDiscountDetails() {
		return discountDetails;
	}

	/**
	 * Sets the discount details.
	 *
	 * @param discountDetails the new discount details
	 */
	public void setDiscountDetails(List<DiscountDetails> discountDetails) {
		this.discountDetails = discountDetails;
	}
	
	/**
	 * Gets the type of inventory.
	 *
	 * @return the type of inventory
	 */
	public String getTypeOfInventory() {
		return typeOfInventory;
	}

	/**
	 * Sets the type of inventory.
	 *
	 * @param typeOfInventory the new type of inventory
	 */
	public void setTypeOfInventory(String typeOfInventory) {
		this.typeOfInventory = typeOfInventory;
	}

	/**
	 * Gets the access pricing offer name.
	 *
	 * @return the access pricing offer name
	 */
	public String getAccessPricingOfferName() {
		return accessPricingOfferName;
	}

	/**
	 * Sets the access pricing offer name.
	 *
	 * @param accessPricingOfferName the new access pricing offer name
	 */
	public void setAccessPricingOfferName(String accessPricingOfferName) {
		this.accessPricingOfferName = accessPricingOfferName;
	}

}
