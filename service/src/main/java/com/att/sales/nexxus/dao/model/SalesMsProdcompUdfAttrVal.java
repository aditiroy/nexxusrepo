package com.att.sales.nexxus.dao.model;

import java.io.Serializable;
import javax.persistence.*;
import java.util.Date;


/**
 * The persistent class for the SALES_MS_PRODCOMP_UDF_ATTR_VAL database table.
 * 
 */
@Entity
@Table(name="SALES_MS_PRODCOMP_UDF_ATTR_VAL")
@NamedQuery(name="SalesMsProdcompUdfAttrVal.findAll", query="SELECT s FROM SalesMsProdcompUdfAttrVal s")
public class SalesMsProdcompUdfAttrVal implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name="PRODCOMP_UDF_ATTR_ID")
	private long prodcompUdfAttrId;

	@Column(name="\"ACTION\"")
	private Long action;

	private String active;

	@Column(name="ATTR_DISPLAY_VALUE")
	private String attrDisplayValue;

	@Column(name="ATTRVAL_ORDER_HAND_OFF")
	private String attrvalOrderHandOff;

	@Column(name="COMPONENT_ID")
	private Long componentId;

	private String default1;

	@Temporal(TemporalType.DATE)
	@Column(name="EFF_DATE")
	private Date effDate;

	@Temporal(TemporalType.DATE)
	@Column(name="END_DATE")
	private Date endDate;

	@Temporal(TemporalType.DATE)
	@Column(name="MOD_DATE")
	private Date modDate;

	@Column(name="OFFER_ID")
	private Long offerId;

	@Column(name="PRODUCT_ID")
	private Long productId;

	@Column(name="RELEASE_CODE_ID")
	private Long releaseCodeId;

	@Column(name="SALES_DATA_YN")
	private String salesDataYn;

	@Column(name="TRANSACTION_ID")
	private String transactionId;

	@Column(name="UDF_ATTRIBUTE_ID")
	private Long udfAttributeId;

	@Column(name="UDF_ATTRIBUTE_VALUE")
	private String udfAttributeValue;

	@Column(name="UDF_ID")
	private Long udfId;

	private String uom;

	public SalesMsProdcompUdfAttrVal() {
	}

	public long getProdcompUdfAttrId() {
		return this.prodcompUdfAttrId;
	}

	public void setProdcompUdfAttrId(long prodcompUdfAttrId) {
		this.prodcompUdfAttrId = prodcompUdfAttrId;
	}

	public Long getAction() {
		return this.action;
	}

	public void setAction(Long action) {
		this.action = action;
	}

	public String getActive() {
		return this.active;
	}

	public void setActive(String active) {
		this.active = active;
	}

	public String getAttrDisplayValue() {
		return this.attrDisplayValue;
	}

	public void setAttrDisplayValue(String attrDisplayValue) {
		this.attrDisplayValue = attrDisplayValue;
	}

	public String getAttrvalOrderHandOff() {
		return this.attrvalOrderHandOff;
	}

	public void setAttrvalOrderHandOff(String attrvalOrderHandOff) {
		this.attrvalOrderHandOff = attrvalOrderHandOff;
	}

	public Long getComponentId() {
		return this.componentId;
	}

	public void setComponentId(Long componentId) {
		this.componentId = componentId;
	}

	public String getDefault1() {
		return this.default1;
	}

	public void setDefault1(String default1) {
		this.default1 = default1;
	}

	public Date getEffDate() {
		return this.effDate;
	}

	public void setEffDate(Date effDate) {
		this.effDate = effDate;
	}

	public Date getEndDate() {
		return this.endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public Date getModDate() {
		return this.modDate;
	}

	public void setModDate(Date modDate) {
		this.modDate = modDate;
	}

	public Long getOfferId() {
		return this.offerId;
	}

	public void setOfferId(Long offerId) {
		this.offerId = offerId;
	}

	public Long getProductId() {
		return this.productId;
	}

	public void setProductId(Long productId) {
		this.productId = productId;
	}

	public Long getReleaseCodeId() {
		return this.releaseCodeId;
	}

	public void setReleaseCodeId(Long releaseCodeId) {
		this.releaseCodeId = releaseCodeId;
	}

	public String getSalesDataYn() {
		return this.salesDataYn;
	}

	public void setSalesDataYn(String salesDataYn) {
		this.salesDataYn = salesDataYn;
	}

	public String getTransactionId() {
		return this.transactionId;
	}

	public void setTransactionId(String transactionId) {
		this.transactionId = transactionId;
	}

	public Long getUdfAttributeId() {
		return this.udfAttributeId;
	}

	public void setUdfAttributeId(Long udfAttributeId) {
		this.udfAttributeId = udfAttributeId;
	}

	public String getUdfAttributeValue() {
		return this.udfAttributeValue;
	}

	public void setUdfAttributeValue(String udfAttributeValue) {
		this.udfAttributeValue = udfAttributeValue;
	}

	public Long getUdfId() {
		return this.udfId;
	}

	public void setUdfId(Long udfId) {
		this.udfId = udfId;
	}

	public String getUom() {
		return this.uom;
	}

	public void setUom(String uom) {
		this.uom = uom;
	}

}