package com.att.sales.nexxus.dao.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;


/**
 * The Class NxKeyFieldPathModel.
 *
 * @author vt393d
 */
@Entity
@Table(name="nx_key_field_path")
public class NxKeyFieldPathModel {
	
	/** The id. */
	@Id
	@Column(name="ID")
	private Long id;
	
	/** The key field name. */
	@Column(name="KEY_FIELD_NAME")
	private String keyFieldName; 
	
	/** The json path. */
	@Column(name="JSON_PATH")
	private String jsonPath;
	
	/** The field name. */
	@Column(name="FIELD_NAME")
	private String fieldName;
	
	/** The type. */
	@Column(name="TYPE")
	private String type;
	
	/** The default value. */
	@Column(name="DEFAULT_VALUE")
	private String defaultValue;
	
	@Column(name="LONG_KEY_NAME")
	private String longKeyName;
	
	/** The nexus line item. */
	@ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "NX_ID")
	private NxLineItemLookUpFieldModel nexusLineItem;

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
	 * Gets the key field name.
	 *
	 * @return the key field name
	 */
	public String getKeyFieldName() {
		return keyFieldName;
	}

	/**
	 * Sets the key field name.
	 *
	 * @param keyFieldName the new key field name
	 */
	public void setKeyFieldName(String keyFieldName) {
		this.keyFieldName = keyFieldName;
	}

	/**
	 * Gets the json path.
	 *
	 * @return the json path
	 */
	public String getJsonPath() {
		return jsonPath;
	}

	/**
	 * Sets the json path.
	 *
	 * @param jsonPath the new json path
	 */
	public void setJsonPath(String jsonPath) {
		this.jsonPath = jsonPath;
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
	 * Gets the nexus line item.
	 *
	 * @return the nexus line item
	 */
	public NxLineItemLookUpFieldModel getNexusLineItem() {
		return nexusLineItem;
	}

	/**
	 * Sets the nexus line item.
	 *
	 * @param nexusLineItem the new nexus line item
	 */
	public void setNexusLineItem(NxLineItemLookUpFieldModel nexusLineItem) {
		this.nexusLineItem = nexusLineItem;
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

	public String getLongKeyName() {
		return longKeyName;
	}

	public void setLongKeyName(String longKeyName) {
		this.longKeyName = longKeyName;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("NxKeyFieldPathModel [id=").append(id).append(", keyFieldName=").append(keyFieldName)
				.append(", jsonPath=").append(jsonPath).append(", fieldName=").append(fieldName).append(", type=")
				.append(type).append(", defaultValue=").append(defaultValue).append(", longKeyName=")
				.append(longKeyName).append("]");
		return builder.toString();
	}

}
