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
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "NX_MP_DESIGN_DOCUMENT")
public class NxMpDesignDocument implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@SequenceGenerator(name = "NX_MP_DESIGN_DOCUMENT_NXDOCUMENTID_GENERATOR", sequenceName = "SEQ_NX_MP_DESIGN_DOCUMENT", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "NX_MP_DESIGN_DOCUMENT_NXDOCUMENTID_GENERATOR")
	@Column(name = "NX_DOCUMENT_ID")
	private Long nxDocumentId;

	@Column(name = "NX_DESIGN_ID")
	private Long nxDesignId;

	@Column(name = "MP_SOLUTION_ID")
	private String mpSolutionId;

	@Column(name = "MP_PRODUCT_LINE_ID")
	private String mpProductLineId;

	@Column(name = "MP_DOCUMENT_NUMBER")
	private Long mpDocumentNumber;

	@Column(name = "CHARGE_DESC")
	private String chargeDesc;

	@Column(name = "USOC_ID")
	private String usocId;

	@Column(name = "MP_PART_NUMBER")
	private String mpPartNumber;

	@Column(name = "NX_TXN_ID")
	private Long nxTxnId;

	@Column(name = "CREATED_DATE")
	private Date createdDate;

	@Column(name = "MODIFIED_DATE")
	private Date modifiedDate;

	@Column(name = "ACTIVE_YN")
	private String activeYN;

}