package com.att.sales.nexxus.dao.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

/**
 * The Class LittleProductDataEntity.
 *
 * @author RudreshWaladaunki
 */

/**
 * The persistent class for the LITTLE_PRODUCT_DATA database table.
 * 
 */
@Entity
@Table(name = "LITTLE_PRODUCT_DATA")
@NamedQueries({
	@NamedQuery(name="updateLittleProductAdminData", query="update LittleProductDataEntity a set a.littleProductId=:newLittleProductId, a.littleProductName=:littleProductName "
			+ "where a.topProductData.topProductId=:topProductId and a.littleProductId=:oldLittleProductId")
})
public class LittleProductDataEntity implements Serializable {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;

	/** The little id. */
	@Id
	@Column(name = "LITTLE_ID")
	private Long littleId;
	
	/** The little product id. */
	@Column(name = "LITTLE_PRODUCT_ID")
	private Long littleProductId;

	/** The top product data. */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "TOP_PRODUCT_ID")
	private TopProductDataEntity topProductData;

	/** The little product name. */
	@Column(name = "LITTLE_PRODUCT_NAME")
	private String littleProductName;
	

	/** The active yn. */
	@Column(name = "ACTIVE_YN")
	private String activeYn;	
	
	/** The template name. */
	@Column(name = "TEMPLATE_NAME")
	private String templateName;	
	
	/**
	 * Gets the little id.
	 *
	 * @return the little id
	 */
	public Long getLittleId() {
		return littleId;
	}

	/**
	 * Sets the little id.
	 *
	 * @param littleId the new little id
	 */
	public void setLittleId(Long littleId) {
		this.littleId = littleId;
	}

	/**
	 * Gets the little product id.
	 *
	 * @return the littleProductId
	 */
	public Long getLittleProductId() {
		return littleProductId;
	}

	/**
	 * Sets the little product id.
	 *
	 * @param littleProductId the littleProductId to set
	 */
	public void setLittleProductId(Long littleProductId) {
		this.littleProductId = littleProductId;
	}

	/**
	 * Gets the top product data.
	 *
	 * @return the topProductData
	 */
	public TopProductDataEntity getTopProductData() {
		return topProductData;
	}

	/**
	 * Sets the top product data.
	 *
	 * @param topProductData the topProductData to set
	 */
	public void setTopProductData(TopProductDataEntity topProductData) {
		this.topProductData = topProductData;
	}

	/**
	 * Gets the little product name.
	 *
	 * @return the littleProductName
	 */
	public String getLittleProductName() {
		return littleProductName;
	}

	/**
	 * Sets the little product name.
	 *
	 * @param littleProductName the littleProductName to set
	 */
	public void setLittleProductName(String littleProductName) {
		this.littleProductName = littleProductName;
	}

	/**
	 * Gets the active yn.
	 *
	 * @return the active yn
	 */
	public String getActiveYn() {
		return activeYn;
	}

	/**
	 * Sets the active yn.
	 *
	 * @param activeYn the new active yn
	 */
	public void setActiveYn(String activeYn) {
		this.activeYn = activeYn;
	}

	/**
	 * Gets the template name.
	 *
	 * @return the template name
	 */
	public String getTemplateName() {
		return templateName;
	}

	/**
	 * Sets the template name.
	 *
	 * @param templateName the new template name
	 */
	public void setTemplateName(String templateName) {
		this.templateName = templateName;
	}
	
	
	
}
