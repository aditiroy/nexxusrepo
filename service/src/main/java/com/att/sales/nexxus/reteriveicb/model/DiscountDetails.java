package com.att.sales.nexxus.reteriveicb.model;

import java.util.List;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.map.annotate.JsonSerialize;


/**
 * The Class DiscountDetails.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class DiscountDetails {

	/** The discount id. */
	private String discountId;
	
	/** The discount ref id. */
	private String discountRefId;
	
	/** The beid. */
	private String beid;
	
	/** The from feature ind. */
	private String fromFeatureInd;
	
	/** The category. */
	private String category;
	
	/** The sub category. */
	private String subCategory;
	
	/** The connection. */
	private String connection;
	
	/** The start date. */
	private String startDate;
	
	/** The end date. */
	private String endDate;
	
	/** The intial disc. */
	private String intialDisc;
	
	/** The max disc. */
	private String maxDisc;
	
	/** The inventory discount percentage. */
	private Long inventoryDiscountPercentage;
	
	/** The region id. */
	private String regionId;
	
	/** The frm dsgn. */
	private String frmDsgn;
	
	/** The disc prctg. */
	private String discPrctg;
	
	/** The icb mx disc prctg. */
	private String icbMxDiscPrctg;
	
	/** The status id. */
	private String statusId;
	
	/** The addtnl promo grp cd. */
	private String addtnlPromoGrpCd;
	
	/** The icb apld disc perc. */
	private String icbApldDiscPerc;
	
	/** The isb YN. */
	private String isbYN;
	
	/** The bgn rng. */
	private String bgnRng;
	
	/** The end rng. */
	private String endRng;
	
	/** The mx disc perc. */
	private String mxDiscPerc;
	
	/** The disc cat. */
	private String discCat;
	
	/** The disc rng id. */
	private String discRngId;
	
	/** The rate stable ind. */
	private String rateStableInd;
	
	/** The disc grp id. */
	private String discGrpId;
	
	/** The is cstm disc. */
	private String isCstmDisc;
	
	/** The grp name. */
	private String grpName;
	
	/** The srvc typ. */
	private String srvcTyp;
	
	/** The uc totl ftr disc. */
	private String ucTotlFtrDisc;
	
	/** The cat sbcat disc id. */
	private String catSbcatDiscId;
	
	/** The category dsc. */
	private String categoryDsc;
	
	/** The sub category dsc. */
	private String subCategoryDsc;
	
	/** The gbp YN. */
	private String gbpYN;
	
	/** The discount type. */
	private String discountType;
	
	/** The recurrence desc. */
	private String recurrenceDesc;
	
	/** The max special disc prctg. */
	private Long maxSpecialDiscPrctg;
	
	/** The icb desired disc perc. */
	private Double icbDesiredDiscPerc;
	
	/** The from inventory YN. */
	private String fromInventoryYN;
	
	/** The external disc id. */
	private Long externalDiscId;
	
	/** The external discount id. */
	private Long externalDiscountId;
	
	/** The rate plan version. */
	private Long ratePlanVersion;
	
	/** The price plan version. */
	private Long pricePlanVersion;
	
	/** The disc feat desc. */
	private String discFeatDesc;
	
	/** The is active. */
	private String isActive;
	
	/** The ms PSI id. */
	private String msPSIId;
	
	/** The non disc yn. */
	private String nonDiscYn;
	
	/** The access type. */
	private String accessType;
	
	/** The cpc disc id. */
	private Long cpcDiscId;
	
	/** The sort order. */
	private String sortOrder;
	
	/** The enumerated values. */
	private String enumeratedValues;
	
	/** The sub group. */
	private List<SubGroup> subGroup;
	
	/** The promo list. */
	private List<Promo> promoList;
	
	/**
	 * Gets the discount id.
	 *
	 * @return the discount id
	 */
	public String getDiscountId() {
		return discountId;
	}
	
	/**
	 * Sets the discount id.
	 *
	 * @param discountId the new discount id
	 */
	public void setDiscountId(String discountId) {
		this.discountId = discountId;
	}
	
	/**
	 * Gets the discount ref id.
	 *
	 * @return the discount ref id
	 */
	public String getDiscountRefId() {
		return discountRefId;
	}
	
	/**
	 * Sets the discount ref id.
	 *
	 * @param discountRefId the new discount ref id
	 */
	public void setDiscountRefId(String discountRefId) {
		this.discountRefId = discountRefId;
	}
	
	/**
	 * Gets the beid.
	 *
	 * @return the beid
	 */
	public String getBeid() {
		return beid;
	}
	
	/**
	 * Sets the beid.
	 *
	 * @param beid the new beid
	 */
	public void setBeid(String beid) {
		this.beid = beid;
	}
	
	/**
	 * Gets the from feature ind.
	 *
	 * @return the from feature ind
	 */
	public String getFromFeatureInd() {
		return fromFeatureInd;
	}
	
	/**
	 * Sets the from feature ind.
	 *
	 * @param fromFeatureInd the new from feature ind
	 */
	public void setFromFeatureInd(String fromFeatureInd) {
		this.fromFeatureInd = fromFeatureInd;
	}
	
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
	 * Gets the sub category.
	 *
	 * @return the sub category
	 */
	public String getSubCategory() {
		return subCategory;
	}
	
	/**
	 * Sets the sub category.
	 *
	 * @param subCategory the new sub category
	 */
	public void setSubCategory(String subCategory) {
		this.subCategory = subCategory;
	}
	
	/**
	 * Gets the connection.
	 *
	 * @return the connection
	 */
	public String getConnection() {
		return connection;
	}
	
	/**
	 * Sets the connection.
	 *
	 * @param connection the new connection
	 */
	public void setConnection(String connection) {
		this.connection = connection;
	}
	
	/**
	 * Gets the start date.
	 *
	 * @return the start date
	 */
	public String getStartDate() {
		return startDate;
	}
	
	/**
	 * Sets the start date.
	 *
	 * @param startDate the new start date
	 */
	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}
	
	/**
	 * Gets the end date.
	 *
	 * @return the end date
	 */
	public String getEndDate() {
		return endDate;
	}
	
	/**
	 * Sets the end date.
	 *
	 * @param endDate the new end date
	 */
	public void setEndDate(String endDate) {
		this.endDate = endDate;
	}
	
	/**
	 * Gets the external discount id.
	 *
	 * @return the external discount id
	 */
	public Long getExternalDiscountId() {
		return externalDiscountId;
	}
	
	/**
	 * Sets the external discount id.
	 *
	 * @param externalDiscountId the new external discount id
	 */
	public void setExternalDiscountId(Long externalDiscountId) {
		this.externalDiscountId = externalDiscountId;
	}
	
	/**
	 * Gets the intial disc.
	 *
	 * @return the intial disc
	 */
	public String getIntialDisc() {
		return intialDisc;
	}
	
	/**
	 * Sets the intial disc.
	 *
	 * @param intialDisc the new intial disc
	 */
	public void setIntialDisc(String intialDisc) {
		this.intialDisc = intialDisc;
	}
	
	/**
	 * Gets the max disc.
	 *
	 * @return the max disc
	 */
	public String getMaxDisc() {
		return maxDisc;
	}
	
	/**
	 * Sets the max disc.
	 *
	 * @param maxDisc the new max disc
	 */
	public void setMaxDisc(String maxDisc) {
		this.maxDisc = maxDisc;
	}
	
	/**
	 * Gets the region id.
	 *
	 * @return the region id
	 */
	public String getRegionId() {
		return regionId;
	}
	
	/**
	 * Sets the region id.
	 *
	 * @param regionId the new region id
	 */
	public void setRegionId(String regionId) {
		this.regionId = regionId;
	}
	
	/**
	 * Gets the frm dsgn.
	 *
	 * @return the frm dsgn
	 */
	public String getFrmDsgn() {
		return frmDsgn;
	}
	
	/**
	 * Sets the frm dsgn.
	 *
	 * @param frmDsgn the new frm dsgn
	 */
	public void setFrmDsgn(String frmDsgn) {
		this.frmDsgn = frmDsgn;
	}
	
	/**
	 * Gets the disc prctg.
	 *
	 * @return the disc prctg
	 */
	public String getDiscPrctg() {
		return discPrctg;
	}
	
	/**
	 * Sets the disc prctg.
	 *
	 * @param discPrctg the new disc prctg
	 */
	public void setDiscPrctg(String discPrctg) {
		this.discPrctg = discPrctg;
	}
	
	/**
	 * Gets the icb mx disc prctg.
	 *
	 * @return the icb mx disc prctg
	 */
	public String getIcbMxDiscPrctg() {
		return icbMxDiscPrctg;
	}
	
	/**
	 * Sets the icb mx disc prctg.
	 *
	 * @param icbMxDiscPrctg the new icb mx disc prctg
	 */
	public void setIcbMxDiscPrctg(String icbMxDiscPrctg) {
		this.icbMxDiscPrctg = icbMxDiscPrctg;
	}
	
	/**
	 * Gets the status id.
	 *
	 * @return the status id
	 */
	public String getStatusId() {
		return statusId;
	}
	
	/**
	 * Sets the status id.
	 *
	 * @param statusId the new status id
	 */
	public void setStatusId(String statusId) {
		this.statusId = statusId;
	}
	
	/**
	 * Gets the addtnl promo grp cd.
	 *
	 * @return the addtnl promo grp cd
	 */
	public String getAddtnlPromoGrpCd() {
		return addtnlPromoGrpCd;
	}
	
	/**
	 * Sets the addtnl promo grp cd.
	 *
	 * @param addtnlPromoGrpCd the new addtnl promo grp cd
	 */
	public void setAddtnlPromoGrpCd(String addtnlPromoGrpCd) {
		this.addtnlPromoGrpCd = addtnlPromoGrpCd;
	}
	
	/**
	 * Gets the icb apld disc perc.
	 *
	 * @return the icb apld disc perc
	 */
	public String getIcbApldDiscPerc() {
		return icbApldDiscPerc;
	}
	
	/**
	 * Sets the icb apld disc perc.
	 *
	 * @param icbApldDiscPerc the new icb apld disc perc
	 */
	public void setIcbApldDiscPerc(String icbApldDiscPerc) {
		this.icbApldDiscPerc = icbApldDiscPerc;
	}
	
	/**
	 * Gets the isb YN.
	 *
	 * @return the isb YN
	 */
	public String getIsbYN() {
		return isbYN;
	}
	
	/**
	 * Sets the isb YN.
	 *
	 * @param isbYN the new isb YN
	 */
	public void setIsbYN(String isbYN) {
		this.isbYN = isbYN;
	}
	
	/**
	 * Gets the mx disc perc.
	 *
	 * @return the mx disc perc
	 */
	public String getMxDiscPerc() {
		return mxDiscPerc;
	}
	
	/**
	 * Sets the mx disc perc.
	 *
	 * @param mxDiscPerc the new mx disc perc
	 */
	public void setMxDiscPerc(String mxDiscPerc) {
		this.mxDiscPerc = mxDiscPerc;
	}
	
	/**
	 * Gets the disc cat.
	 *
	 * @return the disc cat
	 */
	public String getDiscCat() {
		return discCat;
	}
	
	/**
	 * Sets the disc cat.
	 *
	 * @param discCat the new disc cat
	 */
	public void setDiscCat(String discCat) {
		this.discCat = discCat;
	}
	
	/**
	 * Gets the disc rng id.
	 *
	 * @return the disc rng id
	 */
	public String getDiscRngId() {
		return discRngId;
	}
	
	/**
	 * Sets the disc rng id.
	 *
	 * @param discRngId the new disc rng id
	 */
	public void setDiscRngId(String discRngId) {
		this.discRngId = discRngId;
	}
	
	/**
	 * Gets the rate stable ind.
	 *
	 * @return the rate stable ind
	 */
	public String getRateStableInd() {
		return rateStableInd;
	}
	
	/**
	 * Sets the rate stable ind.
	 *
	 * @param rateStableInd the new rate stable ind
	 */
	public void setRateStableInd(String rateStableInd) {
		this.rateStableInd = rateStableInd;
	}
	
	/**
	 * Gets the disc grp id.
	 *
	 * @return the disc grp id
	 */
	public String getDiscGrpId() {
		return discGrpId;
	}
	
	/**
	 * Sets the disc grp id.
	 *
	 * @param discGrpId the new disc grp id
	 */
	public void setDiscGrpId(String discGrpId) {
		this.discGrpId = discGrpId;
	}
	
	/**
	 * Gets the checks if is cstm disc.
	 *
	 * @return the checks if is cstm disc
	 */
	public String getIsCstmDisc() {
		return isCstmDisc;
	}
	
	/**
	 * Sets the checks if is cstm disc.
	 *
	 * @param isCstmDisc the new checks if is cstm disc
	 */
	public void setIsCstmDisc(String isCstmDisc) {
		this.isCstmDisc = isCstmDisc;
	}
	
	/**
	 * Gets the grp name.
	 *
	 * @return the grp name
	 */
	public String getGrpName() {
		return grpName;
	}
	
	/**
	 * Sets the grp name.
	 *
	 * @param grpName the new grp name
	 */
	public void setGrpName(String grpName) {
		this.grpName = grpName;
	}
	
	/**
	 * Gets the srvc typ.
	 *
	 * @return the srvc typ
	 */
	public String getSrvcTyp() {
		return srvcTyp;
	}
	
	/**
	 * Sets the srvc typ.
	 *
	 * @param srvcTyp the new srvc typ
	 */
	public void setSrvcTyp(String srvcTyp) {
		this.srvcTyp = srvcTyp;
	}
	
	/**
	 * Gets the uc totl ftr disc.
	 *
	 * @return the uc totl ftr disc
	 */
	public String getUcTotlFtrDisc() {
		return ucTotlFtrDisc;
	}
	
	/**
	 * Sets the uc totl ftr disc.
	 *
	 * @param ucTotlFtrDisc the new uc totl ftr disc
	 */
	public void setUcTotlFtrDisc(String ucTotlFtrDisc) {
		this.ucTotlFtrDisc = ucTotlFtrDisc;
	}
	
	/**
	 * Gets the cat sbcat disc id.
	 *
	 * @return the cat sbcat disc id
	 */
	public String getCatSbcatDiscId() {
		return catSbcatDiscId;
	}
	
	/**
	 * Sets the cat sbcat disc id.
	 *
	 * @param catSbcatDiscId the new cat sbcat disc id
	 */
	public void setCatSbcatDiscId(String catSbcatDiscId) {
		this.catSbcatDiscId = catSbcatDiscId;
	}
	
	/**
	 * Gets the category dsc.
	 *
	 * @return the category dsc
	 */
	public String getCategoryDsc() {
		return categoryDsc;
	}
	
	/**
	 * Sets the category dsc.
	 *
	 * @param categoryDsc the new category dsc
	 */
	public void setCategoryDsc(String categoryDsc) {
		this.categoryDsc = categoryDsc;
	}
	
	/**
	 * Gets the gbp YN.
	 *
	 * @return the gbp YN
	 */
	public String getGbpYN() {
		return gbpYN;
	}
	
	/**
	 * Sets the gbp YN.
	 *
	 * @param gbpYN the new gbp YN
	 */
	public void setGbpYN(String gbpYN) {
		this.gbpYN = gbpYN;
	}
	
	/**
	 * Gets the discount type.
	 *
	 * @return the discount type
	 */
	public String getDiscountType() {
		return discountType;
	}
	
	/**
	 * Sets the discount type.
	 *
	 * @param discountType the new discount type
	 */
	public void setDiscountType(String discountType) {
		this.discountType = discountType;
	}
	
	/**
	 * Gets the recurrence desc.
	 *
	 * @return the recurrence desc
	 */
	public String getRecurrenceDesc() {
		return recurrenceDesc;
	}
	
	/**
	 * Sets the recurrence desc.
	 *
	 * @param recurrenceDesc the new recurrence desc
	 */
	public void setRecurrenceDesc(String recurrenceDesc) {
		this.recurrenceDesc = recurrenceDesc;
	}
	
	/**
	 * Gets the max special disc prctg.
	 *
	 * @return the max special disc prctg
	 */
	public Long getMaxSpecialDiscPrctg() {
		return maxSpecialDiscPrctg;
	}
	
	/**
	 * Sets the max special disc prctg.
	 *
	 * @param maxSpecialDiscPrctg the new max special disc prctg
	 */
	public void setMaxSpecialDiscPrctg(Long maxSpecialDiscPrctg) {
		this.maxSpecialDiscPrctg = maxSpecialDiscPrctg;
	}
	
	/**
	 * Gets the icb desired disc perc.
	 *
	 * @return the icb desired disc perc
	 */
	public Double getIcbDesiredDiscPerc() {
		return icbDesiredDiscPerc;
	}
	
	/**
	 * Sets the icb desired disc perc.
	 *
	 * @param icbDesiredDiscPerc the new icb desired disc perc
	 */
	public void setIcbDesiredDiscPerc(Double icbDesiredDiscPerc) {
		this.icbDesiredDiscPerc = icbDesiredDiscPerc;
	}
	
	/**
	 * Gets the from inventory YN.
	 *
	 * @return the from inventory YN
	 */
	public String getFromInventoryYN() {
		return fromInventoryYN;
	}
	
	/**
	 * Sets the from inventory YN.
	 *
	 * @param fromInventoryYN the new from inventory YN
	 */
	public void setFromInventoryYN(String fromInventoryYN) {
		this.fromInventoryYN = fromInventoryYN;
	}
	
	/**
	 * Gets the external disc id.
	 *
	 * @return the external disc id
	 */
	public Long getExternalDiscId() {
		return externalDiscId;
	}
	
	/**
	 * Sets the external disc id.
	 *
	 * @param externalDiscId the new external disc id
	 */
	public void setExternalDiscId(Long externalDiscId) {
		this.externalDiscId = externalDiscId;
	}
	
	/**
	 * Gets the rate plan version.
	 *
	 * @return the rate plan version
	 */
	public Long getRatePlanVersion() {
		return ratePlanVersion;
	}
	
	/**
	 * Sets the rate plan version.
	 *
	 * @param ratePlanVersion the new rate plan version
	 */
	public void setRatePlanVersion(Long ratePlanVersion) {
		this.ratePlanVersion = ratePlanVersion;
	}
	
	/**
	 * Gets the price plan version.
	 *
	 * @return the price plan version
	 */
	public Long getPricePlanVersion() {
		return pricePlanVersion;
	}
	
	/**
	 * Sets the price plan version.
	 *
	 * @param pricePlanVersion the new price plan version
	 */
	public void setPricePlanVersion(Long pricePlanVersion) {
		this.pricePlanVersion = pricePlanVersion;
	}
		
	/**
	 * Gets the disc feat desc.
	 *
	 * @return the disc feat desc
	 */
	public String getDiscFeatDesc() {
		return discFeatDesc;
	}
	
	/**
	 * Sets the disc feat desc.
	 *
	 * @param discFeatDesc the new disc feat desc
	 */
	public void setDiscFeatDesc(String discFeatDesc) {
		this.discFeatDesc = discFeatDesc;
	}
	
	/**
	 * Gets the checks if is active.
	 *
	 * @return the checks if is active
	 */
	public String getIsActive() {
		return isActive;
	}
	
	/**
	 * Sets the checks if is active.
	 *
	 * @param isActive the new checks if is active
	 */
	public void setIsActive(String isActive) {
		this.isActive = isActive;
	}
	
	/**
	 * Gets the sub group.
	 *
	 * @return the sub group
	 */
	public List<SubGroup> getSubGroup() {
		return subGroup;
	}
	
	/**
	 * Sets the sub group.
	 *
	 * @param subGroup the new sub group
	 */
	public void setSubGroup(List<SubGroup> subGroup) {
		this.subGroup = subGroup;
	}
	
	
	/**
	 * Gets the ms PSI id.
	 *
	 * @return the ms PSI id
	 */
	public String getMsPSIId() {
		return msPSIId;
	}
	
	/**
	 * Sets the ms PSI id.
	 *
	 * @param msPSIId the new ms PSI id
	 */
	public void setMsPSIId(String msPSIId) {
		this.msPSIId = msPSIId;
	}
	
	/**
	 * Gets the non disc yn.
	 *
	 * @return the nonDiscYn
	 */
	public String getNonDiscYn() {
		return nonDiscYn;
	}
	
	/**
	 * Sets the non disc yn.
	 *
	 * @param nonDiscYn the nonDiscYn to set
	 */
	public void setNonDiscYn(String nonDiscYn) {
		this.nonDiscYn = nonDiscYn;
	}
	
	/**
	 * Gets the sub category dsc.
	 *
	 * @return the sub category dsc
	 */
	public String getSubCategoryDsc() {
		return subCategoryDsc;
	}
	
	/**
	 * Sets the sub category dsc.
	 *
	 * @param subCategoryDsc the new sub category dsc
	 */
	public void setSubCategoryDsc(String subCategoryDsc) {
		this.subCategoryDsc = subCategoryDsc;
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
	 * @param accessType the accessType to set
	 */
	public void setAccessType(String accessType) {
		this.accessType = accessType;
	}
	
	/**
	 * Gets the promo list.
	 *
	 * @return the promo list
	 */
	public List<Promo> getPromoList() {
		return promoList;
	}
	
	/**
	 * Sets the promo list.
	 *
	 * @param promoList the new promo list
	 */
	public void setPromoList(List<Promo> promoList) {
		this.promoList = promoList;
	}
	
	/**
	 * Gets the inventory discount percentage.
	 *
	 * @return the inventory discount percentage
	 */
	public Long getInventoryDiscountPercentage() {
		return inventoryDiscountPercentage;
	}
	
	/**
	 * Sets the inventory discount percentage.
	 *
	 * @param inventoryDiscountPercentage the new inventory discount percentage
	 */
	public void setInventoryDiscountPercentage(Long inventoryDiscountPercentage) {
		this.inventoryDiscountPercentage = inventoryDiscountPercentage;
	}
	
	/**
	 * Gets the bgn rng.
	 *
	 * @return the bgn rng
	 */
	public String getBgnRng() {
		return bgnRng;
	}
	
	/**
	 * Sets the bgn rng.
	 *
	 * @param bgnRng the new bgn rng
	 */
	public void setBgnRng(String bgnRng) {
		this.bgnRng = bgnRng;
	}
	
	/**
	 * Gets the end rng.
	 *
	 * @return the end rng
	 */
	public String getEndRng() {
		return endRng;
	}
	
	/**
	 * Sets the end rng.
	 *
	 * @param endRng the new end rng
	 */
	public void setEndRng(String endRng) {
		this.endRng = endRng;
	}
	
	/**
	 * Gets the cpc disc id.
	 *
	 * @return the cpc disc id
	 */
	public Long getCpcDiscId() {
		return cpcDiscId;
	}
	
	/**
	 * Sets the cpc disc id.
	 *
	 * @param cpcDiscId the new cpc disc id
	 */
	public void setCpcDiscId(Long cpcDiscId) {
		this.cpcDiscId = cpcDiscId;
	}
	
	/**
	 * Gets the sort order.
	 *
	 * @return the sort order
	 */
	public String getSortOrder() {
		return sortOrder;
	}
	
	/**
	 * Sets the sort order.
	 *
	 * @param sortOrder the new sort order
	 */
	public void setSortOrder(String sortOrder) {
		this.sortOrder = sortOrder;
	}
	
	/**
	 * Gets the enumerated values.
	 *
	 * @return the enumerated values
	 */
	public String getEnumeratedValues() {
		return enumeratedValues;
	}
	
	/**
	 * Sets the enumerated values.
	 *
	 * @param enumeratedValues the new enumerated values
	 */
	public void setEnumeratedValues(String enumeratedValues) {
		this.enumeratedValues = enumeratedValues;
	}
}
