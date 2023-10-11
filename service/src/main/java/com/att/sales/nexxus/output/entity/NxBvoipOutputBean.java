package com.att.sales.nexxus.output.entity;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

/**
 * The Class NxBvoipOutputBean.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(Include.NON_NULL)
public class NxBvoipOutputBean extends NxBaseOutputBean implements Serializable {
	
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;
	
	/** The pbi. */
	private String pbi;
	
	/** The concurrent call qty. */
	private String concurrentCallQty;
	
	/** The concurrent call type. */
	private String concurrentCallType;
	
	/** The module card qty. */
	private String moduleCardQty;
	
	/** The initial period definition. */
	private String initialPeriodDefinition;
	
	/** The additional period definition. */
	private String additionalPeriodDefinition;
	
	/** The initial period rate. */
	private String initialPeriodRate;
	
	/** The additional period rate. */
	private String additionalPeriodRate;
	
	/** The unit rate. */
	private String unitRate;
	
	/** The discount. */
	private String discount;
	
	/** The terminating state country name. */
	private String terminatingStateCountryName;
	
	/** The to country. */
	private String toCountry;
	
	/** The free mins qty. */
	private String freeMinsQty;
	
	/** The billed mins qty. */
	private String billedMinsQty;
	
	/** The lptn type. */
	private String lptnType;
	
	/** The iobmt indicator. */
	private String iobmtIndicator;

	/**
	 * Gets the pbi.
	 *
	 * @return the pbi
	 */
	public String getPbi() {
		return pbi;

	}

	/**
	 * Sets the pbi.
	 *
	 * @param pbi the new pbi
	 */
	public void setPbi(String pbi) {
		this.pbi = pbi;
	}

	/**
	 * Gets the concurrent call qty.
	 *
	 * @return the concurrent call qty
	 */
	public String getConcurrentCallQty() {
		return concurrentCallQty;
	}

	/**
	 * Sets the concurrent call qty.
	 *
	 * @param concurrentCallQty the new concurrent call qty
	 */
	public void setConcurrentCallQty(String concurrentCallQty) {
		this.concurrentCallQty = concurrentCallQty;
	}

	/**
	 * Gets the concurrent call type.
	 *
	 * @return the concurrent call type
	 */
	public String getConcurrentCallType() {
		return concurrentCallType;
	}

	/**
	 * Sets the concurrent call type.
	 *
	 * @param concurrentCallType the new concurrent call type
	 */
	public void setConcurrentCallType(String concurrentCallType) {
		this.concurrentCallType = concurrentCallType;
	}

	/**
	 * Gets the module card qty.
	 *
	 * @return the module card qty
	 */
	public String getModuleCardQty() {
		return moduleCardQty;
	}

	/**
	 * Sets the module card qty.
	 *
	 * @param moduleCardQty the new module card qty
	 */
	public void setModuleCardQty(String moduleCardQty) {
		this.moduleCardQty = moduleCardQty;
	}

	/**
	 * Gets the initial period definition.
	 *
	 * @return the initial period definition
	 */
	public String getInitialPeriodDefinition() {
		return initialPeriodDefinition;
	}

	/**
	 * Sets the initial period definition.
	 *
	 * @param initialPeriodDefinition the new initial period definition
	 */
	public void setInitialPeriodDefinition(String initialPeriodDefinition) {
		this.initialPeriodDefinition = initialPeriodDefinition;
	}

	/**
	 * Gets the additional period definition.
	 *
	 * @return the additional period definition
	 */
	public String getAdditionalPeriodDefinition() {
		return additionalPeriodDefinition;
	}

	/**
	 * Sets the additional period definition.
	 *
	 * @param additionalPeriodDefinition the new additional period definition
	 */
	public void setAdditionalPeriodDefinition(String additionalPeriodDefinition) {
		this.additionalPeriodDefinition = additionalPeriodDefinition;
	}

	/**
	 * Gets the initial period rate.
	 *
	 * @return the initial period rate
	 */
	public String getInitialPeriodRate() {
		return initialPeriodRate;
	}

	/**
	 * Sets the initial period rate.
	 *
	 * @param initialPeriodRate the new initial period rate
	 */
	public void setInitialPeriodRate(String initialPeriodRate) {
		this.initialPeriodRate = initialPeriodRate;
	}

	/**
	 * Gets the additional period rate.
	 *
	 * @return the additional period rate
	 */
	public String getAdditionalPeriodRate() {
		return additionalPeriodRate;
	}

	/**
	 * Sets the additional period rate.
	 *
	 * @param additionalPeriodRate the new additional period rate
	 */
	public void setAdditionalPeriodRate(String additionalPeriodRate) {
		this.additionalPeriodRate = additionalPeriodRate;
	}

	/**
	 * Gets the unit rate.
	 *
	 * @return the unit rate
	 */
	public String getUnitRate() {
		return unitRate;
	}

	/**
	 * Sets the unit rate.
	 *
	 * @param unitRate the new unit rate
	 */
	public void setUnitRate(String unitRate) {
		this.unitRate = unitRate;
	}

	/**
	 * Gets the discount.
	 *
	 * @return the discount
	 */
	public String getDiscount() {
		return discount;
	}

	/**
	 * Sets the discount.
	 *
	 * @param discount the new discount
	 */
	public void setDiscount(String discount) {
		this.discount = discount;
	}

	/**
	 * Gets the terminating state country name.
	 *
	 * @return the terminating state country name
	 */
	public String getTerminatingStateCountryName() {
		return terminatingStateCountryName;
	}

	/**
	 * Sets the terminating state country name.
	 *
	 * @param terminatingStateCountryName the new terminating state country name
	 */
	public void setTerminatingStateCountryName(String terminatingStateCountryName) {
		this.terminatingStateCountryName = terminatingStateCountryName;
	}

	/**
	 * Gets the to country.
	 *
	 * @return the to country
	 */
	public String getToCountry() {
		return toCountry;
	}

	/**
	 * Sets the to country.
	 *
	 * @param toCountry the new to country
	 */
	public void setToCountry(String toCountry) {
		this.toCountry = toCountry;
	}

	/**
	 * Gets the free mins qty.
	 *
	 * @return the free mins qty
	 */
	public String getFreeMinsQty() {
		return freeMinsQty;
	}

	/**
	 * Sets the free mins qty.
	 *
	 * @param freeMinsQty the new free mins qty
	 */
	public void setFreeMinsQty(String freeMinsQty) {
		this.freeMinsQty = freeMinsQty;
	}

	/**
	 * Gets the billed mins qty.
	 *
	 * @return the billed mins qty
	 */
	public String getBilledMinsQty() {
		return billedMinsQty;
	}

	/**
	 * Sets the billed mins qty.
	 *
	 * @param billedMinsQty the new billed mins qty
	 */
	public void setBilledMinsQty(String billedMinsQty) {
		this.billedMinsQty = billedMinsQty;
	}

	/**
	 * Gets the lptn type.
	 *
	 * @return the lptn type
	 */
	public String getLptnType() {
		return lptnType;
	}

	/**
	 * Sets the lptn type.
	 *
	 * @param lptnType the new lptn type
	 */
	public void setLptnType(String lptnType) {
		this.lptnType = lptnType;
	}

	/**
	 * Gets the iobmt indicator.
	 *
	 * @return the iobmt indicator
	 */
	public String getIobmtIndicator() {
		return iobmtIndicator;
	}

	/**
	 * Sets the iobmt indicator.
	 *
	 * @param iobmtIndicator the new iobmt indicator
	 */
	public void setIobmtIndicator(String iobmtIndicator) {
		this.iobmtIndicator = iobmtIndicator;
	}

}
