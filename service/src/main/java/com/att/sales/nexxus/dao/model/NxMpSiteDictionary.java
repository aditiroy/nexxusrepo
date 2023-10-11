package com.att.sales.nexxus.dao.model;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.NamedQuery;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "NX_MP_SITE_DICTIONARY")
@NamedQuery(name = "NxMpSiteDictionary.findAll", query = "SELECT n FROM NxMpSiteDictionary n")
public class NxMpSiteDictionary implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@SequenceGenerator(name = "SITEREFID_GENERATOR", sequenceName = "SEQ_SITEREFID", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SITEREFID_GENERATOR")
	@Column(name = "SITE_REF_ID")
	private Long siteRefId;

	@Column(name = "SOURCE_SYSTEM")
	private String sourceSystem;

	@Column(name = "NX_TXN_ID")
	private Long nxTxnId;

	@Lob
	@Column(name = "SITE_JSON")
	private String siteJson;

	@CreationTimestamp
	@Column(name = "CREATED_DATE")
	private Date createdDate;

	@UpdateTimestamp
	@Column(name = "MODIFIED_DATE")
	private Date modifiedDate;

	@Column(name = "ACTIVE_YN")
	private String activeYN;
	
	@Lob
	@Column(name = "SITE_ADDRESS")
	private String siteAddress;
}