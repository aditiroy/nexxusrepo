package com.att.sales.nexxus.dao.model;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "NX_DW_PRICE_DETAILS")
@Getter
@Setter

public class NxDwPriceDetails implements Serializable {
	private static final long serialVersionUID = 1L;
	
	@Id
	@SequenceGenerator(name = "sequence_nx_dw_price_details", sequenceName = "SEQ_NX_DW_PRICE_DETAILS_ID", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequence_nx_dw_price_details")
	@Column(name = "ID")
	private Long id;
	
	@Column(name = "NX_REQ_ID")
	private Long nxReqId;
	
	@Column(name = "PRICE_JSON")
	private String priceJson;
	
	@Column(name = "CIRCUIT_ID")
	private String circuitId;
	
	@Column(name = "MCN")
	private String mcn;
	
	@Column(name = "CREATED_DATE")
	private Date createdDate;
	
	@Column(name = "MODIFIED_DATE")
	private Date modifiedDate;
}
