package com.att.sales.nexxus.dao.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name="NX_SSDF_SPEED_MAPPING")
@Getter
@Setter
public class NxSsdfSpeedMapping implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@Id
	@Column(name="ID")
	private Long id;
	@Column(name="OFFER")
	private String offer;
	@Column(name="COMPONENT")
	private String component;
	@Column(name="CONNECTION_TYPE")
	private String connectionType;
	@Column(name="PRICE_GROUP")
	private String priceGroup;
	@Column(name="PRICE_TYPE")
	private String priceType;
	@Column(name="SPEED_RANGE")
	private String speedRange;
	@Column(name="FORMULA")
	private String formula;
	@Column(name="ACTIVE")
	private String active;
	
	@Override
	public String toString() {
		return "NxSsdfSpeedMapping [id=" + id + ", offer=" + offer + ", component=" + component + ", connectionType="
				+ connectionType + ", priceGroup=" + priceGroup + ", priceType=" + priceType + ", speedRange=" + speedRange
				+ ", formula=" + formula + ", active=" + active + "]";
	}
	

}
