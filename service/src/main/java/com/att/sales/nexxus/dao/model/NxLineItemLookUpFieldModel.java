package com.att.sales.nexxus.dao.model;

import java.util.Date;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;


/**
 * The Class NxLineItemLookUpFieldModel.
 */
/**
 * @author vt393d
 *
 */
@Entity
@Table(name="NX_LINE_ITEM_LOOKUP_FIELDS")
public class NxLineItemLookUpFieldModel {
	
	
	/** The look up id. */
	@Id
	@Column(name="NX_LOOKUP_ID")
	private Long lookUpId;
	
	/** The offer id. */
	@Column(name="OFFER_ID")
	private Long offerId; 
	
	/** The product id. */
	@Column(name="PRODUCT_ID")
	private Long productId;
	
	/** The offer name. */
	@Column(name="OFFER_NAME")
	private String offerName;
	
	/** The price type. */
	@Column(name="PRICE_TYPE")
	private String priceType;
	
	/** The country cd. */
	@Column(name="COUNTRY_CD")
	private String countryCd;
	
	/** The key field name. */
	@Column(name="KEY_FIELD_NAME")
	private String keyFieldName;
	
	/** The is udf field. */
	@Column(name="IS_UDF_FIELD")
	private String isUdfField;
	
	/** The active. */
	@Column(name="IS_ACTIVE")
	private String active;
	
	/** The updated by. */
	@Column(name="UPDATED_BY")
	private String updatedBy;
	
	/** The updated date. */
	@Column(name="UPDATE_DATE")
	private Date updatedDate;
	
	/** The input type. */
	@Column(name="INPUT_TYPE")
	private String inputType;
	
	/** The key field condition. */
	@Column(name="KEY_FIELD_CONDITION")
	private String keyFieldCondition;
	
	/** The mrc nrc source map. */
	@Column(name="MRC_NRC_SOURCE")
	private String mrcNrcSourceMap;
	
	@Column(name="OUTPUT_JSON_MAPPING")
	private String outputJsonMapping;
	
	@Column(name="SORT_ORDER")
	private String sortOrder;
	
	/** The key field mapping. */
	@OneToMany(targetEntity=NxKeyFieldPathModel.class,mappedBy = "nexusLineItem", 
			cascade = CascadeType.ALL,fetch = FetchType.EAGER)
	private Set<NxKeyFieldPathModel> keyFieldMapping;
	
	/** The key field mapping. */
	@OneToMany(targetEntity=NxFmoMPOutputJsonMapping.class,mappedBy = "nexusLineItem", 
			cascade = CascadeType.ALL,fetch = FetchType.EAGER)
	private Set<NxFmoMPOutputJsonMapping> mpJsonMapping;

	/**
	 * Gets the look up id.
	 *
	 * @return the look up id
	 */
	public Long getLookUpId() {
		return lookUpId;
	}

	/**
	 * Sets the look up id.
	 *
	 * @param lookUpId the new look up id
	 */
	public void setLookUpId(Long lookUpId) {
		this.lookUpId = lookUpId;
	}

	/**
	 * Gets the offer id.
	 *
	 * @return the offer id
	 */
	public Long getOfferId() {
		return offerId;
	}

	/**
	 * Sets the offer id.
	 *
	 * @param offerId the new offer id
	 */
	public void setOfferId(Long offerId) {
		this.offerId = offerId;
	}

	/**
	 * Gets the product id.
	 *
	 * @return the product id
	 */
	public Long getProductId() {
		return productId;
	}

	/**
	 * Sets the product id.
	 *
	 * @param productId the new product id
	 */
	public void setProductId(Long productId) {
		this.productId = productId;
	}

	/**
	 * Gets the offer name.
	 *
	 * @return the offer name
	 */
	public String getOfferName() {
		return offerName;
	}

	/**
	 * Sets the offer name.
	 *
	 * @param offerName the new offer name
	 */
	public void setOfferName(String offerName) {
		this.offerName = offerName;
	}

	/**
	 * Gets the price type.
	 *
	 * @return the price type
	 */
	public String getPriceType() {
		return priceType;
	}

	/**
	 * Sets the price type.
	 *
	 * @param priceType the new price type
	 */
	public void setPriceType(String priceType) {
		this.priceType = priceType;
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
	 * Gets the key field name.
	 *
	 * @return the key field name
	 */
	public String getKeyFieldName() {
		return keyFieldName;
	}

	/**
	 * Sets the key field name.
	 *
	 * @param keyFieldName the new key field name
	 */
	public void setKeyFieldName(String keyFieldName) {
		this.keyFieldName = keyFieldName;
	}


	/**
	 * Gets the checks if is udf field.
	 *
	 * @return the checks if is udf field
	 */
	public String getIsUdfField() {
		return isUdfField;
	}

	/**
	 * Sets the checks if is udf field.
	 *
	 * @param isUdfField the new checks if is udf field
	 */
	public void setIsUdfField(String isUdfField) {
		this.isUdfField = isUdfField;
	}

	/**
	 * Gets the active.
	 *
	 * @return the active
	 */
	public String getActive() {
		return active;
	}

	/**
	 * Sets the active.
	 *
	 * @param active the new active
	 */
	public void setActive(String active) {
		this.active = active;
	}

	/**
	 * Gets the updated by.
	 *
	 * @return the updated by
	 */
	public String getUpdatedBy() {
		return updatedBy;
	}

	/**
	 * Sets the updated by.
	 *
	 * @param updatedBy the new updated by
	 */
	public void setUpdatedBy(String updatedBy) {
		this.updatedBy = updatedBy;
	}

	/**
	 * Gets the updated date.
	 *
	 * @return the updated date
	 */
	public Date getUpdatedDate() {
		return updatedDate;
	}

	/**
	 * Sets the updated date.
	 *
	 * @param updatedDate the new updated date
	 */
	public void setUpdatedDate(Date updatedDate) {
		this.updatedDate = updatedDate;
	}

	/**
	 * Gets the input type.
	 *
	 * @return the input type
	 */
	public String getInputType() {
		return inputType;
	}

	/**
	 * Sets the input type.
	 *
	 * @param inputType the new input type
	 */
	public void setInputType(String inputType) {
		this.inputType = inputType;
	}
	
	/**
	 * Gets the key field mapping.
	 *
	 * @return the key field mapping
	 */
	public Set<NxKeyFieldPathModel> getKeyFieldMapping() {
		return keyFieldMapping;
	}

	/**
	 * Sets the key field mapping.
	 *
	 * @param keyFieldMapping the new key field mapping
	 */
	public void setKeyFieldMapping(Set<NxKeyFieldPathModel> keyFieldMapping) {
		this.keyFieldMapping = keyFieldMapping;
	}

	/**
	 * Gets the key field condition.
	 *
	 * @return the key field condition
	 */
	public String getKeyFieldCondition() {
		return keyFieldCondition;
	}

	/**
	 * Sets the key field condition.
	 *
	 * @param keyFieldCondition the new key field condition
	 */
	public void setKeyFieldCondition(String keyFieldCondition) {
		this.keyFieldCondition = keyFieldCondition;
	}

	/**
	 * Gets the mrc nrc source map.
	 *
	 * @return the mrc nrc source map
	 */
	public String getMrcNrcSourceMap() {
		return mrcNrcSourceMap;
	}

	/**
	 * Sets the mrc nrc source map.
	 *
	 * @param mrcNrcSourceMap the new mrc nrc source map
	 */
	public void setMrcNrcSourceMap(String mrcNrcSourceMap) {
		this.mrcNrcSourceMap = mrcNrcSourceMap;
	}

	public String getOutputJsonMapping() {
		return outputJsonMapping;
	}

	public void setOutputJsonMapping(String outputJsonMapping) {
		this.outputJsonMapping = outputJsonMapping;
	}

	public Set<NxFmoMPOutputJsonMapping> getMpJsonMapping() {
		return mpJsonMapping;
	}

	public void setMpJsonMapping(Set<NxFmoMPOutputJsonMapping> mpJsonMapping) {
		this.mpJsonMapping = mpJsonMapping;
	}

	public String getSortOrder() {
		return sortOrder;
	}

	public void setSortOrder(String sortOrder) {
		this.sortOrder = sortOrder;
	}

	
}
