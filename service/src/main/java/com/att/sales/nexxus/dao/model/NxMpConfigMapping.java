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
@Table(name = "NX_MP_CONFIGURE_MAPPING")
public class NxMpConfigMapping implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name = "NX_MAPPING_ID")
	private Long nxMappingId;

	@Column(name = "RULE_NAME")
	private String ruleName;

	@Column(name = "VARIABLE_NAME")
	private String variableName;

	@Column(name = "PATH")
	private String path;

	@Column(name = "OFFER")
	private String offer;

	@Column(name = "ACTIVE_YN")
	private String activeYN;

	@Column(name = "DEFAULT_VALUE")
	private String defaultValue;

	@Column(name = "DATASET_NAME")
	private String dataSetName;
	
	@Column(name="COMPONENT_ID")
	private Long componentId;
	
	@Column(name="UDF_ID")
	private Long udfId;
	
	@Column(name = "DELIMITER")
	private String delimiter;
	
	@Column(name = "TYPE")
	private String type;
	
	@Column(name = "PRODUCT_TYPE")
	private String productType;

}
