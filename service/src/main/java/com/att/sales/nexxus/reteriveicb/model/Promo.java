package com.att.sales.nexxus.reteriveicb.model;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.map.annotate.JsonSerialize;

/**
 * The Class Promo.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class Promo {

	/** The promo CD. */
	private String promoCD;
	
	/** The discount percentage. */
	private String discountPercentage;
	
	/** The discount type. */
	private String discountType;
	
	/** The additional promo grp code. */
	private String additionalPromoGrpCode;
	
	/** The promo desc. */
	private String promoDesc;
	
	/**
	 * Gets the promo CD.
	 *
	 * @return the promo CD
	 */
	public String getPromoCD() {
		return promoCD;
	}
	
	/**
	 * Sets the promo CD.
	 *
	 * @param promoCD the new promo CD
	 */
	public void setPromoCD(String promoCD) {
		this.promoCD = promoCD;
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
	 * Gets the additional promo grp code.
	 *
	 * @return the additional promo grp code
	 */
	public String getAdditionalPromoGrpCode() {
		return additionalPromoGrpCode;
	}
	
	/**
	 * Sets the additional promo grp code.
	 *
	 * @param additionalPromoGrpCode the new additional promo grp code
	 */
	public void setAdditionalPromoGrpCode(String additionalPromoGrpCode) {
		this.additionalPromoGrpCode = additionalPromoGrpCode;
	}
	
	/**
	 * Gets the promo desc.
	 *
	 * @return the promo desc
	 */
	public String getPromoDesc() {
		return promoDesc;
	}
	
	/**
	 * Sets the promo desc.
	 *
	 * @param promoDesc the new promo desc
	 */
	public void setPromoDesc(String promoDesc) {
		this.promoDesc = promoDesc;
	}
	
}
