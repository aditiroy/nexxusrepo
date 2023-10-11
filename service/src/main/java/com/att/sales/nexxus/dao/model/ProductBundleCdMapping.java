package com.att.sales.nexxus.dao.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * The Class ProductBundleCdMapping.
 */
@Entity
@Table(name="PRODUCT_BUNDLE_CODE_MAPPING")
public class ProductBundleCdMapping {
	
	/** The parent offer id. */
	@Id
	@Column(name="PARENT_OFFER_ID")
	private Long parentOfferId;
	
	/** The parent product name. */
	@Column(name="PARENT_PRODUCT_NAME")
	private String parentProductName;
	
	/** The product name. */
	@Column(name="PRODUCT_NAME") 
	private String productName;
	
	/** The bundle code. */
	@Column(name="BUNDLE_CODE")    
	private String bundleCode;
	
	/** The bundle offer id. */
	@Column(name="BUNDLE_OFFER_ID") 
	private Long bundleOfferId;
	
	/** The product id. */
	@Column(name="PRODUCT_ID") 
	private Long productId;

	/**
	 * Gets the parent offer id.
	 *
	 * @return the parent offer id
	 */
	public Long getParentOfferId() {
		return parentOfferId;
	}

	/**
	 * Sets the parent offer id.
	 *
	 * @param parentOfferId the new parent offer id
	 */
	public void setParentOfferId(Long parentOfferId) {
		this.parentOfferId = parentOfferId;
	}

	/**
	 * Gets the parent product name.
	 *
	 * @return the parent product name
	 */
	public String getParentProductName() {
		return parentProductName;
	}

	/**
	 * Sets the parent product name.
	 *
	 * @param parentProductName the new parent product name
	 */
	public void setParentProductName(String parentProductName) {
		this.parentProductName = parentProductName;
	}

	/**
	 * Gets the product name.
	 *
	 * @return the product name
	 */
	public String getProductName() {
		return productName;
	}

	/**
	 * Sets the product name.
	 *
	 * @param productName the new product name
	 */
	public void setProductName(String productName) {
		this.productName = productName;
	}

	/**
	 * Gets the bundle code.
	 *
	 * @return the bundle code
	 */
	public String getBundleCode() {
		return bundleCode;
	}

	/**
	 * Sets the bundle code.
	 *
	 * @param bundleCode the new bundle code
	 */
	public void setBundleCode(String bundleCode) {
		this.bundleCode = bundleCode;
	}

	/**
	 * Gets the bundle offer id.
	 *
	 * @return the bundle offer id
	 */
	public Long getBundleOfferId() {
		return bundleOfferId;
	}

	/**
	 * Sets the bundle offer id.
	 *
	 * @param bundleOfferId the new bundle offer id
	 */
	public void setBundleOfferId(Long bundleOfferId) {
		this.bundleOfferId = bundleOfferId;
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
	
	

}
