package com.att.sales.nexxus.dao.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;


/**
 * The Class LookupDataMapping.
 *
 * @author vt393d
 */
@Entity
@Table(name="Lookup_Data_Mapping")
public class LookupDataMapping {
	
	/** The id. */
	@Id
	@Column(name="ID")
	private Long id;
	
	/** The littel id. */
	@Column(name="LITTLE_ID")
	private Long littelId;
	
	/** The table col name. */
	@Column(name="TABLE_COLUMN_NAME")
	private String tableColName;
	
	/** The input cell. */
	@Column(name="INPUT_CELL")
	private String inputCell; 
	
	/** The required flag. */
	@Column(name="REQUIRED_YN")
	private String requiredFlag;
	
	/** The default flag. */
	@Column(name="DEFAULT_YN")
	private String defaultFlag;
	
	/** The default value. */
	@Column(name="DEFAULT_VALUE")
	private String defaultValue;
	
	/** The flow type. */
	@Column(name="FLOW_TYPE")
	private String flowType;
	
	/** The field name. */
	@Column(name="FIELD_NAME")
	private String fieldName;

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
	 * Gets the littel id.
	 *
	 * @return the littel id
	 */
	public Long getLittelId() {
		return littelId;
	}

	/**
	 * Sets the littel id.
	 *
	 * @param littelId the new littel id
	 */
	public void setLittelId(Long littelId) {
		this.littelId = littelId;
	}

	/**
	 * Gets the table col name.
	 *
	 * @return the table col name
	 */
	public String getTableColName() {
		return tableColName;
	}

	/**
	 * Sets the table col name.
	 *
	 * @param tableColName the new table col name
	 */
	public void setTableColName(String tableColName) {
		this.tableColName = tableColName;
	}

	/**
	 * Gets the input cell.
	 *
	 * @return the input cell
	 */
	public String getInputCell() {
		return inputCell;
	}

	/**
	 * Sets the input cell.
	 *
	 * @param inputCell the new input cell
	 */
	public void setInputCell(String inputCell) {
		this.inputCell = inputCell;
	}

	/**
	 * Gets the required flag.
	 *
	 * @return the required flag
	 */
	public String getRequiredFlag() {
		return requiredFlag;
	}

	/**
	 * Sets the required flag.
	 *
	 * @param requiredFlag the new required flag
	 */
	public void setRequiredFlag(String requiredFlag) {
		this.requiredFlag = requiredFlag;
	}

	/**
	 * Gets the default flag.
	 *
	 * @return the default flag
	 */
	public String getDefaultFlag() {
		return defaultFlag;
	}

	/**
	 * Sets the default flag.
	 *
	 * @param defaultFlag the new default flag
	 */
	public void setDefaultFlag(String defaultFlag) {
		this.defaultFlag = defaultFlag;
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
	 * Gets the flow type.
	 *
	 * @return the flow type
	 */
	public String getFlowType() {
		return flowType;
	}

	/**
	 * Sets the flow type.
	 *
	 * @param flowType the new flow type
	 */
	public void setFlowType(String flowType) {
		this.flowType = flowType;
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
	
	
}
