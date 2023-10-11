package com.att.sales.nexxus.dao.model;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

/**
 * The Class FmoJsonRulesModel.
 *
 * @author vt393d
 * 
 * Model class for maintaining the FMO JSON processing rules in FMO_JSON_RULES
 */
@Entity
@Table(name="FMO_JSON_RULES")
public class FmoJsonRulesModel {

	
	/** The id. */
	@Id
	@Column(name="ID")
	private Long id;
	
	/** The item name. */
	@Column(name="ITEM_NAME")
	private String itemName;
	
	/** The field name. */
	@Column(name="FIELD_NAME")
	private String fieldName;
	
	/** The udf query. */
	@Column(name="UDF_QUERY")
	private String udfQuery;
	
	/** The field name json. */
	@Column(name="FIELD_NAME_JSON")
	private String fieldNameJson;
	
	
	/** The rules mapping. */
	@OneToMany(targetEntity=FmoOfferJsonRulesMapping.class,
	mappedBy = "fmoRules",cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	private List<FmoOfferJsonRulesMapping> rulesMapping;
	
	
	/** The data setname. */
	@Column(name="DATASET_NAME")
	private String dataSetname;
	
	/** The type. */
	@Column(name="TYPE")
	private String type;
	
	/** The udf id. */
	@Column(name="UDF_ID")
	private Long udfId;
	
	/** The component id. */
	@Column(name="COMPONENT_ID")
	private Long componentId;
	
	/** The default value. */
	@Column(name="default_value")
	private String defaultValue;

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
	 * Gets the item name.
	 *
	 * @return the item name
	 */
	public String getItemName() {
		return itemName;
	}

	/**
	 * Sets the item name.
	 *
	 * @param itemName the new item name
	 */
	public void setItemName(String itemName) {
		this.itemName = itemName;
	}

	/**
	 * Gets the field name.
	 *
	 * @return the field name
	 */
	public String getFieldName() {
		return fieldName;
	}

	/**
	 * Sets the field name.
	 *
	 * @param fieldName the new field name
	 */
	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}

	/**
	 * Gets the udf query.
	 *
	 * @return the udf query
	 */
	public String getUdfQuery() {
		return udfQuery;
	}

	/**
	 * Sets the udf query.
	 *
	 * @param udfQuery the new udf query
	 */
	public void setUdfQuery(String udfQuery) {
		this.udfQuery = udfQuery;
	}

	/**
	 * Gets the field name json.
	 *
	 * @return the field name json
	 */
	public String getFieldNameJson() {
		return fieldNameJson;
	}

	/**
	 * Sets the field name json.
	 *
	 * @param fieldNameJson the new field name json
	 */
	public void setFieldNameJson(String fieldNameJson) {
		this.fieldNameJson = fieldNameJson;
	}
	
	/**
	 * Gets the data setname.
	 *
	 * @return the data setname
	 */
	public String getDataSetname() {
		return dataSetname;
	}

	/**
	 * Sets the data setname.
	 *
	 * @param dataSetname the new data setname
	 */
	public void setDataSetname(String dataSetname) {
		this.dataSetname = dataSetname;
	}

	/**
	 * Gets the type.
	 *
	 * @return the type
	 */
	public String getType() {
		return type;
	}

	/**
	 * Sets the type.
	 *
	 * @param type the new type
	 */
	public void setType(String type) {
		this.type = type;
	}

	/**
	 * Gets the udf id.
	 *
	 * @return the udf id
	 */
	public Long getUdfId() {
		return udfId;
	}

	/**
	 * Sets the udf id.
	 *
	 * @param udfId the new udf id
	 */
	public void setUdfId(Long udfId) {
		this.udfId = udfId;
	}

	/**
	 * Gets the component id.
	 *
	 * @return the component id
	 */
	public Long getComponentId() {
		return componentId;
	}

	/**
	 * Sets the component id.
	 *
	 * @param componentId the new component id
	 */
	public void setComponentId(Long componentId) {
		this.componentId = componentId;
	}

	/**
	 * Gets the default value.
	 *
	 * @return the default value
	 */
	public String getDefaultValue() {
		return defaultValue;
	}

	/**
	 * Sets the default value.
	 *
	 * @param defaultValue the new default value
	 */
	public void setDefaultValue(String defaultValue) {
		this.defaultValue = defaultValue;
	}

	/**
	 * Gets the rules mapping.
	 *
	 * @return the rules mapping
	 */
	public List<FmoOfferJsonRulesMapping> getRulesMapping() {
		return rulesMapping;
	}

	/**
	 * Sets the rules mapping.
	 *
	 * @param rulesMapping the new rules mapping
	 */
	public void setRulesMapping(List<FmoOfferJsonRulesMapping> rulesMapping) {
		this.rulesMapping = rulesMapping;
	}

	
	
}
