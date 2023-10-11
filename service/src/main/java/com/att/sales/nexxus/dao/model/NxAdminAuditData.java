package com.att.sales.nexxus.dao.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

/**
 * The Class NxAdminAuditData.
 *
 * @author vt393d
 */
@Entity
@Table(name="NX_ADMIN_AUDIT_DATA")
public class NxAdminAuditData {
	
	/** The id. */
	@Id
	@Column(name="ID")
	@SequenceGenerator(name="sequence_nx_admin_audit_data",sequenceName="SEQ_ADMIN_AUDIT_ID", allocationSize=1)
	@GeneratedValue(strategy=GenerationType.SEQUENCE, generator="sequence_nx_admin_audit_data")
	private Long id;
	
	/** The user id. */
	@Column(name="USER_ID")
	private String userId;
	
	/** The little product id. */
	@Column(name="LITTLE_PRODUCT_ID")
	private Long littleProductId; 
	
	/** The top productid. */
	@Column(name="TOP_PRODUCT_ID")
	private Long topProductid; 
	
	/** The nx item ids. */
	@Column(name="NX_ITEM_IDS")
	private String nxItemIds;
	
	/** The created date. */
	@Column(name="CREATED_DATE")
	private Date createdDate;
	

	/** The discription. */
	@Column(name="DISCRIPTION")
	private String discription;

	/**
	 * Gets the id.
	 *
	 * @return the id
	 */
	public Long getId() {
		return id;
	}

	/**
	 * Sets the id.
	 *
	 * @param id the new id
	 */
	public void setId(Long id) {
		this.id = id;
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
	 * Gets the little product id.
	 *
	 * @return the little product id
	 */
	public Long getLittleProductId() {
		return littleProductId;
	}

	/**
	 * Sets the little product id.
	 *
	 * @param littleProductId the new little product id
	 */
	public void setLittleProductId(Long littleProductId) {
		this.littleProductId = littleProductId;
	}

	/**
	 * Gets the top productid.
	 *
	 * @return the top productid
	 */
	public Long getTopProductid() {
		return topProductid;
	}

	/**
	 * Sets the top productid.
	 *
	 * @param topProductid the new top productid
	 */
	public void setTopProductid(Long topProductid) {
		this.topProductid = topProductid;
	}

	/**
	 * Gets the nx item ids.
	 *
	 * @return the nx item ids
	 */
	public String getNxItemIds() {
		return nxItemIds;
	}

	/**
	 * Sets the nx item ids.
	 *
	 * @param nxItemIds the new nx item ids
	 */
	public void setNxItemIds(String nxItemIds) {
		this.nxItemIds = nxItemIds;
	}

	/**
	 * Gets the created date.
	 *
	 * @return the created date
	 */
	public Date getCreatedDate() {
		return createdDate;
	}

	/**
	 * Sets the created date.
	 *
	 * @param createdDate the new created date
	 */
	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}

	/**
	 * Gets the discription.
	 *
	 * @return the discription
	 */
	public String getDiscription() {
		return discription;
	}

	/**
	 * Sets the discription.
	 *
	 * @param discription the new discription
	 */
	public void setDiscription(String discription) {
		this.discription = discription;
	}

	
	
}
