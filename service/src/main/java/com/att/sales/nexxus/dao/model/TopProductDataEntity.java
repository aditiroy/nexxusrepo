/**
 * 
 */
package com.att.sales.nexxus.dao.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * The Class TopProductDataEntity.
 *
 * @author RudreshWaladaunki
 */

/**
 * The persistent class for the TOP_PRODUCT_DATA database table.
 * 
 */
@Entity
@Table(name = "TOP_PRODUCT_DATA")
public class TopProductDataEntity implements Serializable {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;

	/** The top product id. */
	@Id
	@Column(name = "TOP_PRODUCT_ID")
	private Long topProductId;

	/** The product. */
	@Column(name = "PRODUCT")
	private String product;

	/** The top product name. */
	@Column(name = "TOP_PRODUCT_NAME")
	private String topProductName;

	/**
	 * Gets the top product id.
	 *
	 * @return the topProductId
	 */
	public Long getTopProductId() {
		return topProductId;
	}

	/**
	 * Sets the top product id.
	 *
	 * @param topProductId the topProductId to set
	 */
	public void setTopProductId(Long topProductId) {
		this.topProductId = topProductId;
	}

	/**
	 * Gets the product.
	 *
	 * @return the product
	 */
	public String getProduct() {
		return product;
	}

	/**
	 * Sets the product.
	 *
	 * @param product the product to set
	 */
	public void setProduct(String product) {
		this.product = product;
	}

	/**
	 * Gets the top product name.
	 *
	 * @return the topProductName
	 */
	public String getTopProductName() {
		return topProductName;
	}

	/**
	 * Sets the top product name.
	 *
	 * @param topProductName the topProductName to set
	 */
	public void setTopProductName(String topProductName) {
		this.topProductName = topProductName;
	}

}
