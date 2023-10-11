/**
 * 
 */
package com.att.sales.nexxus.model;

import java.io.Serializable;
import java.util.List;

/**
 * The Class TopProduct.
 *
 * @author RudreshWaladaunki
 */
public class TopProduct implements Serializable{

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;
	
	/** The top product id. */
	private Long topProductId;
	
	/** The product. */
	private String product;
	
	/** The description. */
	private String description;
	
	/** The little product list. */
	private List<LittleProduct> littleProductList;

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
	 * Gets the description.
	 *
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * Sets the description.
	 *
	 * @param description the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * Gets the little product list.
	 *
	 * @return the littleProductList
	 */
	public List<LittleProduct> getLittleProductList() {
		return littleProductList;
	}

	/**
	 * Sets the little product list.
	 *
	 * @param littleProductList the littleProductList to set
	 */
	public void setLittleProductList(List<LittleProduct> littleProductList) {
		this.littleProductList = littleProductList;
	}

}
