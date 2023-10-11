package com.att.sales.nexxus.dao.model;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "LEGACY_CO_DETAILS")
public class LegacyCoDetails implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name = "SWCCLLI")
	private String swcclli;
	
	@Column(name = "COCLLI")
	private String coclli;
	
	@Column(name = "SWITCHCLLI")
	private String switchclli;
	
	@Column(name = "STATE")
	private String state;
	
	@Column(name = "OCN")
	private String ocn;
	
	@Column(name = "OPERATING_COMP_NAME")
	private String operatingCompName;
	
	@Column(name = "SWCVCOORDINATE")
	private String swcvcoordinate;
	
	@Column(name = "SWCHCOORDINATE")
	private String swchcoordinate;
	
	@Column(name = "CATEGORY")
	private String category;
	
	@Column(name = "SWITCHTYPE")
	private String switchtype;
	
	@Column(name = "ISDNPRIHOSTTYPE")
	private String isdnprihosttype;
	
	@Column(name = "CENTREX_CAPABLE")
	private String centrexCapable;
	
	@Column(name = "MSA_RELIEF_IND")
	private String msaReliefInd;
	
	@Column(name = "ZONE")
	private String zone;
	
	@Column(name = "FEATURE_CODE")
	private String featureCode;
	
	@Column(name = "CREATED_DATE")
	private Date createdDate;
	
	@Column(name = "MODIFIED_DATE")
	private Date modifiedDate;
	
	@Column(name = "STATUS")
	private String status;

	public String getSwcclli() {
		return swcclli;
	}

	public void setSwcclli(String swcclli) {
		this.swcclli = swcclli;
	}

	public String getCoclli() {
		return coclli;
	}

	public void setCoclli(String coclli) {
		this.coclli = coclli;
	}

	public String getSwitchclli() {
		return switchclli;
	}

	public void setSwitchclli(String switchclli) {
		this.switchclli = switchclli;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getOcn() {
		return ocn;
	}

	public void setOcn(String ocn) {
		this.ocn = ocn;
	}

	public String getOperatingCompName() {
		return operatingCompName;
	}

	public void setOperatingCompName(String operatingCompName) {
		this.operatingCompName = operatingCompName;
	}

	public String getSwcvcoordinate() {
		return swcvcoordinate;
	}

	public void setSwcvcoordinate(String swcvcoordinate) {
		this.swcvcoordinate = swcvcoordinate;
	}

	public String getSwchcoordinate() {
		return swchcoordinate;
	}

	public void setSwchcoordinate(String swchcoordinate) {
		this.swchcoordinate = swchcoordinate;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public String getSwitchtype() {
		return switchtype;
	}

	public void setSwitchtype(String switchtype) {
		this.switchtype = switchtype;
	}

	public String getIsdnprihosttype() {
		return isdnprihosttype;
	}

	public void setIsdnprihosttype(String isdnprihosttype) {
		this.isdnprihosttype = isdnprihosttype;
	}

	public String getCentrexCapable() {
		return centrexCapable;
	}

	public void setCentrexCapable(String centrexCapable) {
		this.centrexCapable = centrexCapable;
	}

	public String getMsaReliefInd() {
		return msaReliefInd;
	}

	public void setMsaReliefInd(String msaReliefInd) {
		this.msaReliefInd = msaReliefInd;
	}

	public String getZone() {
		return zone;
	}

	public void setZone(String zone) {
		this.zone = zone;
	}

	public String getFeatureCode() {
		return featureCode;
	}

	public void setFeatureCode(String featureCode) {
		this.featureCode = featureCode;
	}

	public Date getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}

	public Date getModifiedDate() {
		return modifiedDate;
	}

	public void setModifiedDate(Date modifiedDate) {
		this.modifiedDate = modifiedDate;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append(csvStatus()).append(",").append(csvColumn(swcclli)).append(",").append(csvColumn(coclli))
				.append(",").append(csvColumn(switchclli)).append(",").append(csvColumn(state)).append(",")
				.append(csvColumn(ocn)).append(",").append(csvColumn(operatingCompName)).append(",")
				.append(csvColumn(swcvcoordinate)).append(",").append(csvColumn(swchcoordinate)).append(",")
				.append(csvColumn(category)).append(",").append(csvColumn(switchtype)).append(",")
				.append(csvColumn(isdnprihosttype)).append(",").append(csvColumn(centrexCapable)).append(",")
				.append(csvColumn(msaReliefInd)).append(",").append(csvColumn(zone)).append(",")
				.append(csvColumn(featureCode));
		return builder.toString();
	}
	
	protected String csvStatus() {
		if (status == null) {
			return "";
		} else if (status.endsWith("modify")) {
			return "modify";
		} else if (status.endsWith("add")) {
			return "modify";
		} else {
			return "";
		}
	}
	
	protected String csvColumn(String value) {
		if (value == null) {
			return "";
		} else if (!value.contains(",")) {
			return value;
		} else {
			return "\"" + value + "\"";
		}
	}
}
