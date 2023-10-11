package com.att.sales.nexxus.dao.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;


@Entity
@Table(name="nx_fmo_mp_output_json_mapping")
public class NxFmoMPOutputJsonMapping {
	
	@Id
	@Column(name="ID")
	private Long id;
	
	@Column(name="FIELD_NAME")
	private String fieldName; 
	
	@Column(name="GET_PATH")
	private String getPath;
	
	@Column(name="GET_TYPE")
	private String getType;
	
	@Column(name="DEFAULT_VALUE")
	private String defaultValue;
	
	@Column(name="SET_PATH")
	private String setPath;
	
	@Column(name="SET_TYPE")
	private String setType;

	@Column(name="SET_PATH_CRITERIA")
	private String setPathCriteria;
	
	@Column(name="FILED_TYPE")
	private String filedType;
	
	@Column(name="OFFER")
	private String offer;
	
	@Column(name="ACTIVE")
	private String active;
	
	@Column(name="DATASET")
	private String dataSetName;
	
	@Column(name="GET_PATH_CRITERIA")
	private String getPathCriteria;
	
	@ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "NX_ID")
	private NxLineItemLookUpFieldModel nexusLineItem;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getFieldName() {
		return fieldName;
	}

	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}

	public String getGetPath() {
		return getPath;
	}

	public void setGetPath(String getPath) {
		this.getPath = getPath;
	}

	public String getGetType() {
		return getType;
	}

	public void setGetType(String getType) {
		this.getType = getType;
	}

	public String getDefaultValue() {
		return defaultValue;
	}

	public void setDefaultValue(String defaultValue) {
		this.defaultValue = defaultValue;
	}

	public String getSetPath() {
		return setPath;
	}

	public void setSetPath(String setPath) {
		this.setPath = setPath;
	}

	public String getSetType() {
		return setType;
	}

	public void setSetType(String setType) {
		this.setType = setType;
	}

	public String getSetPathCriteria() {
		return setPathCriteria;
	}

	public void setSetPathCriteria(String setPathCriteria) {
		this.setPathCriteria = setPathCriteria;
	}

	public String getFiledType() {
		return filedType;
	}

	public void setFiledType(String filedType) {
		this.filedType = filedType;
	}

	public String getOffer() {
		return offer;
	}

	public void setOffer(String offer) {
		this.offer = offer;
	}

	public String getActive() {
		return active;
	}

	public void setActive(String active) {
		this.active = active;
	}

	public NxLineItemLookUpFieldModel getNexusLineItem() {
		return nexusLineItem;
	}

	public void setNexusLineItem(NxLineItemLookUpFieldModel nexusLineItem) {
		this.nexusLineItem = nexusLineItem;
	}

	public String getDataSetName() {
		return dataSetName;
	}

	public void setDataSetName(String dataSetName) {
		this.dataSetName = dataSetName;
	}

	public String getGetPathCriteria() {
		return getPathCriteria;
	}

	public void setGetPathCriteria(String getPathCriteria) {
		this.getPathCriteria = getPathCriteria;
	}


}
