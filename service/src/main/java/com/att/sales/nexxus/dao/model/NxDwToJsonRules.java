package com.att.sales.nexxus.dao.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "NX_DW_TO_JSON_RULES")
@Getter
@Setter
public class NxDwToJsonRules implements Serializable {
	private static final long serialVersionUID = 1L;
	
	@Id
	@Column(name = "ID")
	private Long id;
	
	@Column(name = "FIELD_NAME")
	private String fieldName;
	
	@Column(name = "FIELD_TYPE")
	private String fieldType;
	
	@Column(name = "FIELD_PARENT")
	private String fieldParent;
	
	@Column(name = "IDENTIFIER_KEY")
	private String identifierKey;
	
	@Column(name = "DW_KEY")
	private String dwKey;
	
	@Column(name = "OFFER")
	private String offer;
	
	@Column(name = "RULE_NAME")
	private String ruleName;
	
	@Column(name = "ACTIVE")
	private String active;
	
	@Column(name = "TYPE")
	private String type;
	
	@Column(name = "DEFAULT_VALUE")
	private String defaultValue;
	
	@Column(name = "IDENTIFIER_TYPE")
	private String identifierType;
	
	@Column(name = "LOOKUP_DATASET_NAME")
	private String lookupDatasetName;
}
