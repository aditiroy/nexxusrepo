package com.att.sales.nexxus.dao.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQuery;
import javax.persistence.Table;


/**
 * The persistent class for the NX_UDF_MAPPING database table.
 * 
 */
@Entity
@Table(name="NX_UDF_MAPPING")
@NamedQuery(name="NxUdfMapping.findAll", query="SELECT n FROM NxUdfMapping n")
public class NxUdfMapping implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name="NX_UDF_MAPPING_ID")
	private long nxUdfMappingId;

	@Column(name="COMPONENT_ID")
	private Long componentId;

	@Column(name="COMPONENT_TYPE")
	private String componentType;

	@Column(name="OFFER_ID")
	private Long offerId;

	@Column(name="OFFER_NAME")
	private String offerName;

	@Column(name="RULE_SET")
	private String ruleSet;

	@Column(name="UDF_ABBR")
	private String udfAbbr;

	@Column(name="UDF_ATTRIBUTE_DATASET_NAME")
	private String udfAttributeDatasetName;

	@Column(name="UDF_ID")
	private Long udfId;

	public NxUdfMapping() {
		//default constructor
	}

	public long getNxUdfMappingId() {
		return this.nxUdfMappingId;
	}

	public void setNxUdfMappingId(long nxUdfMappingId) {
		this.nxUdfMappingId = nxUdfMappingId;
	}

	public Long getComponentId() {
		return this.componentId;
	}

	public void setComponentId(Long componentId) {
		this.componentId = componentId;
	}

	public String getComponentType() {
		return this.componentType;
	}

	public void setComponentType(String componentType) {
		this.componentType = componentType;
	}

	public Long getOfferId() {
		return this.offerId;
	}

	public void setOfferId(Long offerId) {
		this.offerId = offerId;
	}

	public String getOfferName() {
		return this.offerName;
	}

	public void setOfferName(String offerName) {
		this.offerName = offerName;
	}

	public String getRuleSet() {
		return this.ruleSet;
	}

	public void setRuleSet(String ruleSet) {
		this.ruleSet = ruleSet;
	}

	public String getUdfAbbr() {
		return this.udfAbbr;
	}

	public void setUdfAbbr(String udfAbbr) {
		this.udfAbbr = udfAbbr;
	}

	public String getUdfAttributeDatasetName() {
		return this.udfAttributeDatasetName;
	}

	public void setUdfAttributeDatasetName(String udfAttributeDatasetName) {
		this.udfAttributeDatasetName = udfAttributeDatasetName;
	}

	public Long getUdfId() {
		return this.udfId;
	}

	public void setUdfId(Long udfId) {
		this.udfId = udfId;
	}

}