package com.att.sales.nexxus.dao.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQuery;
import javax.persistence.Table;


/**
 * The persistent class for the INR_XML_TO_JSON_RULES database table.
 * 
 */
@Entity
@Table(name="INR_XML_TO_JSON_RULES")
@NamedQuery(name="InrXmlToJsonRule.findAll", query="SELECT i FROM InrXmlToJsonRule i")
public class InrXmlToJsonRule implements Serializable {
	
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;

	/** The id. */
	@Id
	private long id;

	/** The array element name. */
	@Column(name="ARRAY_ELEMENT_NAME")
	private String arrayElementName;

	/** The array name. */
	@Column(name="ARRAY_NAME")
	private String arrayName;

	/** The array parent. */
	@Column(name="ARRAY_PARENT")
	private String arrayParent;

	/** The field name. */
	@Column(name="FIELD_NAME")
	private String fieldName;

	/** The field name for tag. */
	@Column(name="FIELD_NAME_FOR_TAG")
	private String fieldNameForTag;

	/** The field parent. */
	@Column(name="FIELD_PARENT")
	private String fieldParent;

	/** The json type. */
	@Column(name="JSON_TYPE")
	private String jsonType;

	/** The min size. */
	@Column(name="MIN_SIZE")
	private Long minSize;

	/** The object name. */
	@Column(name="OBJECT_NAME")
	private String objectName;

	/** The object parent. */
	@Column(name="OBJECT_PARENT")
	private String objectParent;
	
	/** The required fields. */
	@Column(name="REQUIRED_FIELDS")
	private String requiredFields;

	/** The root tag. */
	@Column(name="ROOT_TAG")
	private String rootTag;

	/** The xml end tag. */
	@Column(name="XML_END_TAG")
	private String xmlEndTag;

	/** The xml start tag. */
	@Column(name="XML_START_TAG")
	private String xmlStartTag;
	
	/** The lookup dataset name. */
	@Column(name="LOOKUP_DATASET_NAME")
	private String lookupDatasetName;
	
	/** The field null yn. */
	@Column(name="FIELD_NULL_YN")
	private String fieldNullYn;
	
	@Column(name="UDF_RULE_SET")
	private String udfRuleSet;
	
	@Column(name="OPERATIONS")
	private String operations;

	/**
	 * Instantiates a new inr xml to json rule.
	 */
	public InrXmlToJsonRule() {
		//default constructor
	}

	/**
	 * Gets the id.
	 *
	 * @return the id
	 */
	public long getId() {
		return this.id;
	}

	/**
	 * Sets the id.
	 *
	 * @param id the new id
	 */
	public void setId(long id) {
		this.id = id;
	}

	/**
	 * Gets the array element name.
	 *
	 * @return the array element name
	 */
	public String getArrayElementName() {
		return this.arrayElementName;
	}

	/**
	 * Sets the array element name.
	 *
	 * @param arrayElementName the new array element name
	 */
	public void setArrayElementName(String arrayElementName) {
		this.arrayElementName = arrayElementName;
	}

	/**
	 * Gets the array name.
	 *
	 * @return the array name
	 */
	public String getArrayName() {
		return this.arrayName;
	}

	/**
	 * Sets the array name.
	 *
	 * @param arrayName the new array name
	 */
	public void setArrayName(String arrayName) {
		this.arrayName = arrayName;
	}

	/**
	 * Gets the array parent.
	 *
	 * @return the array parent
	 */
	public String getArrayParent() {
		return this.arrayParent;
	}

	/**
	 * Sets the array parent.
	 *
	 * @param arrayParent the new array parent
	 */
	public void setArrayParent(String arrayParent) {
		this.arrayParent = arrayParent;
	}

	/**
	 * Gets the field name.
	 *
	 * @return the field name
	 */
	public String getFieldName() {
		return this.fieldName;
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
	 * Gets the field name for tag.
	 *
	 * @return the field name for tag
	 */
	public String getFieldNameForTag() {
		return this.fieldNameForTag;
	}

	/**
	 * Sets the field name for tag.
	 *
	 * @param fieldNameForTag the new field name for tag
	 */
	public void setFieldNameForTag(String fieldNameForTag) {
		this.fieldNameForTag = fieldNameForTag;
	}

	/**
	 * Gets the field parent.
	 *
	 * @return the field parent
	 */
	public String getFieldParent() {
		return this.fieldParent;
	}

	/**
	 * Sets the field parent.
	 *
	 * @param fieldParent the new field parent
	 */
	public void setFieldParent(String fieldParent) {
		this.fieldParent = fieldParent;
	}

	/**
	 * Gets the json type.
	 *
	 * @return the json type
	 */
	public String getJsonType() {
		return this.jsonType;
	}

	/**
	 * Sets the json type.
	 *
	 * @param jsonType the new json type
	 */
	public void setJsonType(String jsonType) {
		this.jsonType = jsonType;
	}

	/**
	 * Gets the min size.
	 *
	 * @return the min size
	 */
	public Long getMinSize() {
		return this.minSize;
	}

	/**
	 * Sets the min size.
	 *
	 * @param minSize the new min size
	 */
	public void setMinSize(Long minSize) {
		this.minSize = minSize;
	}

	/**
	 * Gets the object name.
	 *
	 * @return the object name
	 */
	public String getObjectName() {
		return this.objectName;
	}

	/**
	 * Sets the object name.
	 *
	 * @param objectName the new object name
	 */
	public void setObjectName(String objectName) {
		this.objectName = objectName;
	}

	/**
	 * Gets the object parent.
	 *
	 * @return the object parent
	 */
	public String getObjectParent() {
		return this.objectParent;
	}

	/**
	 * Sets the object parent.
	 *
	 * @param objectParent the new object parent
	 */
	public void setObjectParent(String objectParent) {
		this.objectParent = objectParent;
	}
	
	/**
	 * Gets the required fields.
	 *
	 * @return the required fields
	 */
	public String getRequiredFields() {
		return this.requiredFields;
	}

	/**
	 * Sets the required fields.
	 *
	 * @param requiredFields the new required fields
	 */
	public void setRequiredFields(String requiredFields) {
		this.requiredFields = requiredFields;
	}

	/**
	 * Gets the root tag.
	 *
	 * @return the root tag
	 */
	public String getRootTag() {
		return this.rootTag;
	}

	/**
	 * Sets the root tag.
	 *
	 * @param rootTag the new root tag
	 */
	public void setRootTag(String rootTag) {
		this.rootTag = rootTag;
	}

	/**
	 * Gets the xml end tag.
	 *
	 * @return the xml end tag
	 */
	public String getXmlEndTag() {
		return this.xmlEndTag;
	}

	/**
	 * Sets the xml end tag.
	 *
	 * @param xmlEndTag the new xml end tag
	 */
	public void setXmlEndTag(String xmlEndTag) {
		this.xmlEndTag = xmlEndTag;
	}

	/**
	 * Gets the xml start tag.
	 *
	 * @return the xml start tag
	 */
	public String getXmlStartTag() {
		return this.xmlStartTag;
	}

	/**
	 * Sets the xml start tag.
	 *
	 * @param xmlStartTag the new xml start tag
	 */
	public void setXmlStartTag(String xmlStartTag) {
		this.xmlStartTag = xmlStartTag;
	}

	/**
	 * Gets the lookup dataset name.
	 *
	 * @return the lookup dataset name
	 */
	public String getLookupDatasetName() {
		return lookupDatasetName;
	}

	/**
	 * Sets the lookup dataset name.
	 *
	 * @param lookupDatasetName the new lookup dataset name
	 */
	public void setLookupDatasetName(String lookupDatasetName) {
		this.lookupDatasetName = lookupDatasetName;
	}

	/**
	 * Gets the field null yn.
	 *
	 * @return the field null yn
	 */
	public String getFieldNullYn() {
		return fieldNullYn;
	}

	/**
	 * Sets the field null yn.
	 *
	 * @param fieldNullYn the new field null yn
	 */
	public void setFieldNullYn(String fieldNullYn) {
		this.fieldNullYn = fieldNullYn;
	}

	public String getUdfRuleSet() {
		return udfRuleSet;
	}

	public void setUdfRuleSet(String udfRuleSet) {
		this.udfRuleSet = udfRuleSet;
	}

	public String getOperations() {
		return operations;
	}

	public void setOperations(String operations) {
		this.operations = operations;
	}

}