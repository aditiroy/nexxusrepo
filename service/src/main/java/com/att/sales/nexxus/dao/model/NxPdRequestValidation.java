package com.att.sales.nexxus.dao.model;


import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="NX_PD_REQUEST_VALIDATION")
public class NxPdRequestValidation implements Serializable {
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;

	@Id
//	@SequenceGenerator(name="NX_PD_REQUEST_VALIDATIONID_GENERATOR", sequenceName="NX_PD_REQUEST_VALIDATION_ID", allocationSize=1) 
//	@GeneratedValue(strategy=GenerationType.SEQUENCE, generator="NX_PD_REQUEST_VALIDATIONID_GENERATOR") 
	@Column(name="NX_PD_REQUEST_VALIDATION_ID")
	private long pdReqValidationId;

	/** The field. */
	@Column(name="FIELD")
	private String field;

	/** The data type. */
	@Column(name="DATA_TYPE")
	private String dataType;

	/** The json path. */
	@Column(name="JSON_PATH")
	private String jsonPath;

	/** The sub json path. */
	@Column(name="SUB_JSON_PATH")
	private String subJsonPath;

	/** The product. */
	@Column(name="PRODUCT")
	private String product;

	/** The active. */
	@Column(name="ACTIVE")
	private String active;
	
	/** The error msg. */
	@Column(name="ERROR_MSG")
	private String errorMsg;

	/** The validation Order. */
	@Column(name="VALIDATION_ORDER")
	private int validationOrder;

	public long getPdReqValidationId() {
		return pdReqValidationId;
	}

	public void setPdReqValidationId(long pdReqValidationId) {
		this.pdReqValidationId = pdReqValidationId;
	}

	public String getField() {
		return field;
	}

	public void setField(String field) {
		this.field = field;
	}

	public String getDataType() {
		return dataType;
	}

	public void setDataType(String dataType) {
		this.dataType = dataType;
	}

	public String getJsonPath() {
		return jsonPath;
	}

	public void setJsonPath(String jsonPath) {
		this.jsonPath = jsonPath;
	}

	public String getSubJsonPath() {
		return subJsonPath;
	}

	public void setSubJsonPath(String subJsonPath) {
		this.subJsonPath = subJsonPath;
	}

	public String getProduct() {
		return product;
	}

	public void setProduct(String product) {
		this.product = product;
	}

	public String getActive() {
		return active;
	}

	public void setActive(String active) {
		this.active = active;
	}

	public String getErrorMsg() {
		return errorMsg;
	}

	public void setErrorMsg(String errorMsg) {
		this.errorMsg = errorMsg;
	}

	public int getValidationOrder() {
		return validationOrder;
	}

	public void setValidationOrder(int validationOrder) {
		this.validationOrder = validationOrder;
	}

	@Override
	public String toString() {
		return "PDValidationDetailsBean [pdReqValidationId=" + pdReqValidationId + ", field=" + field + ", dataType="
				+ dataType + ", jsonPath=" + jsonPath + ", subJsonPath=" + subJsonPath + ", product=" + product
				+ ", active=" + active + ", errorMsg=" + errorMsg + ", validationOrder=" + validationOrder + "]";
	}

	
}
