package com.att.sales.nexxus.dao.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "NX_MP_CONFIGURE_JSON_MAPPING")
public class NxMpConfigJsonMapping implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@Id
	@Column(name = "ID")
	private Long id;
	
	@Column(name = "RULE_NAME")
	private String ruleName;
	
	@Column(name = "KEY")
	private String key;
	
	@Column(name = "FIELD_NAME")
	private String fieldName;
	
	@Column(name = "FIELD_TYPE")
	private String fieldType;
	
	@Column(name = "FIELD_PARENT_NAME")
	private String fieldParent;
	
	@Column(name = "ARRAY_ELEMENT_NAME")
	private String arrayElementName;
	
	@Column(name = "DEFAULT_VALUE")
	private String defaultValue;
	
	@Column(name = "INPUT_PATH")
	private String inputPath;
	
	@Column(name = "DATASET_NAME")
	private String datasetName;
	
	@Column(name = "DELIM")
	private String delim;
	
	@Column(name = "TYPE")
	private String type;
	
	@Column(name = "OFFER")
	private String offer;
	
	@Column(name = "SUB_OFFER")
	private String subOffer;
	
	@Column(name = "PRODUCT_TYPE")
	private String productType;
	
	@Column(name = "ACTIVE")
	private String activeYN;
	
	@Column(name="COMPONENT_ID")
	private Long componentId;
	
	@Column(name="UDF_ID")
	private Long udfId;
	
	@Column(name="ORDER_SEQ")
	private Integer orderSeq;
	
}
