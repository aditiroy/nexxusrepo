package com.att.sales.nexxus.dao.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "NX_VALIDATION_RULES")
@Getter
@Setter
public class NxValidationRules implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@Id
	private Long id;
	@Column(name = "VALIDATION_GROUP")
	private String validationGroup;
	@Column(name = "NAME")
	private String name;
	@Column(name = "VALUE")
	private String value;
	@Column(name = "DATA_PATH")
	private String dataPath;
	@Column(name = "OFFER")
	private String offer;
	@Column(name = "SUB_OFFER")
	private String subOffer;
	@Column(name = "DESCRIPTION")
	private String description;
	@Column(name = "ACTIVE")
	private String active;
	@Column(name = "SUB_DATA_PATH")
	private String subDataPath;
	@Column(name = "SUB_DATA")
	private String subData;
	@Column(name = "FLOW_TYPE")
	private String flowType;
	

}
