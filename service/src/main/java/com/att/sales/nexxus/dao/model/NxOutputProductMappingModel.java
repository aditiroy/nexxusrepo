package com.att.sales.nexxus.dao.model;

import java.util.LinkedHashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;


/**
 * The Class NxOutputProductMappingModel.
 *
 * @author vt393d
 * 
 * 	Model class for NX_OUTPUT_PRODUCT_MAPPING table
 * 	which maintaining relationship between top product id and report tab from output file
 */
@Entity
@Table(name="NX_OUTPUT_PRODUCT_MAPPING")
public class NxOutputProductMappingModel {
	
	
	
	/** The top prod id. */
	@Id
	@Column(name="TOP_PROD_ID")
	private Long topProdId;
	
	/** The tab name. */
	@Column(name="TAB_NAME")
	private String tabName;
	
	/** The nx line item. */
	@OneToMany(targetEntity=NxLineItemLookUpDataModel.class,
			mappedBy = "nexusOutputMapping",cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	private Set<NxLineItemLookUpDataModel> nxLineItem = new LinkedHashSet<>(0);

	/**
	 * Instantiates a new nx output product mapping model.
	 */
	public NxOutputProductMappingModel() {
		
	}
	
	/**
	 * Instantiates a new nx output product mapping model.
	 *
	 * @param topProdId the top prod id
	 * @param tabName the tab name
	 */
	public NxOutputProductMappingModel(Long topProdId,String tabName) {
		this.topProdId=topProdId;
		this.tabName=tabName;
	}
	

	/**
	 * Gets the top prod id.
	 *
	 * @return the top prod id
	 */
	public Long getTopProdId() {
		return topProdId;
	}

	/**
	 * Sets the top prod id.
	 *
	 * @param topProdId the new top prod id
	 */
	public void setTopProdId(Long topProdId) {
		this.topProdId = topProdId;
	}

	/**
	 * Gets the tab name.
	 *
	 * @return the tab name
	 */
	public String getTabName() {
		return tabName;
	}

	/**
	 * Sets the tab name.
	 *
	 * @param tabName the new tab name
	 */
	public void setTabName(String tabName) {
		this.tabName = tabName;
	}
	
	

}
