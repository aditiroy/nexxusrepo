package com.att.sales.nexxus.dao.model;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "NX_MP_SOLUTION_DETAILS")
@Getter
@Setter
@NoArgsConstructor
public class NxMpSolutionDetails implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@SequenceGenerator(name = "NX_MP_SOLUTION_DETAILS_NXMPSOLUTIONID_GENERATOR", sequenceName = "SEQ_NX_MP_SOLUTION_DETAILS", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "NX_MP_SOLUTION_DETAILS_NXMPSOLUTIONID_GENERATOR")
	@Column(name = "NX_MP_SOLUTION_ID")
	private Long nxMpSolutionId;

	@Column(name = "NX_TXN_ID")
	private Long nxTxnId;

	@Lob
	@Column(name = "SOLUTION_DATA")
	private String solutionData;

	@Column(name = "SOURCE_SYSTEM")
	private String sourceSystem;

	@Column(name = "SITE_REF_ID")
	private Long siteRefId;

	@CreationTimestamp
	@Column(name = "CREATED_DATE")
	private Date createdDate;

	@UpdateTimestamp
	@Column(name = "MODIFIED_DATE")
	private Date modifiedDate;

	@Column(name = "ACTIVE_YN")
	private String activeYN;

}