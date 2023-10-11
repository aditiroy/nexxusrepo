package com.att.sales.nexxus.dao.model;

import java.io.Serializable;
import javax.persistence.*;


/**
 * The persistent class for the NX_LOOKUP_DATA database table.
 * 
 */
@Entity
@Table(name="NX_LOOKUP_DATA")
@NamedQuery(name="NxLookupData.findAll", query="SELECT n FROM NxLookupData n")
public class NxLookupData implements Serializable {
	
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;

	/** The id. */
	@Id
	private String id;

	/** The dataset name. */
	@Column(name="DATASET_NAME")
	private String datasetName;

	/** The description. */
	@Column(name="DESCRIPTION")
	private String description;

	/** The item id. */
	@Column(name="ITEM_ID")
	private String itemId;
	
	@Column(name="CRITERIA")
	private String criteria;
	
	@Column(name="ACTIVE")
	private String active;
	
	@Column(name="SORT_ORDER")
	private Long sortOrder;

	/**
	 * Instantiates a new nx lookup data.
	 */
	public NxLookupData() {
	}

	/**
	 * Gets the id.
	 *
	 * @return the id
	 */
	public String getId() {
		return this.id;
	}

	/**
	 * Sets the id.
	 *
	 * @param id the new id
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * Gets the dataset name.
	 *
	 * @return the dataset name
	 */
	public String getDatasetName() {
		return this.datasetName;
	}

	/**
	 * Sets the dataset name.
	 *
	 * @param datasetName the new dataset name
	 */
	public void setDatasetName(String datasetName) {
		this.datasetName = datasetName;
	}

	/**
	 * Gets the description.
	 *
	 * @return the description
	 */
	public String getDescription() {
		return this.description;
	}

	/**
	 * Sets the description.
	 *
	 * @param description the new description
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * Gets the item id.
	 *
	 * @return the item id
	 */
	public String getItemId() {
		return this.itemId;
	}

	/**
	 * Sets the item id.
	 *
	 * @param itemId the new item id
	 */
	public void setItemId(String itemId) {
		this.itemId = itemId;
	}

	public String getCriteria() {
		return criteria;
	}

	public void setCriteria(String criteria) {
		this.criteria = criteria;
	}

	public String getActive() {
		return active;
	}

	public void setActive(String active) {
		this.active = active;
	}

	public Long getSortOrder() {
		return sortOrder;
	}

	public void setSortOrder(Long sortOrder) {
		this.sortOrder = sortOrder;
	}

	
}