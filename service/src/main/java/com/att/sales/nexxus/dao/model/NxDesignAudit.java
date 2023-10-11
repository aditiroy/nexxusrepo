package com.att.sales.nexxus.dao.model;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

/**
 * The Class NxDesignAudit.
 */
@Entity
@Table(name = "NX_DESIGN_AUDIT")
public class NxDesignAudit implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Id
	@SequenceGenerator(name = "sequence_nx_design_audit", sequenceName = "SEQ_NX_DESIGN_AUDIT_ID", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequence_nx_design_audit")
	@Column(name = "NX_AUDIT_ID")
	private Long nxAuditId;
	
	@Column(name="NX_REF_ID")
	private Long nxRefId;
	
	@Column(name = "TRANSACTION")
	private String transaction;
	
	@Column(name = "STATUS")
	private String status;
	
	@Column(name = "CREATED_DATE")
	private Date createdDate = new Date();
	
	@Column(name = "MODIFIED_DATE")
	private Date modifedDate = new Date();

	@Column(name = "DATA")
	private String data;
	
	@Column(name = "NX_SUB_REF_ID")
	private String nxSubRefId ;
	
	public Long getNxAuditId() {
		return nxAuditId;
	}

	public void setNxAuditId(Long nxAuditId) {
		this.nxAuditId = nxAuditId;
	}

	public Long getNxRefId() {
		return nxRefId;
	}

	public void setNxRefId(Long nxRefId) {
		this.nxRefId = nxRefId;
	}

	public String getTransaction() {
		return transaction;
	}

	public void setTransaction(String transaction) {
		this.transaction = transaction;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Date getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}

	public Date getModifedDate() {
		return modifedDate;
	}

	public void setModifedDate(Date modifedDate) {
		this.modifedDate = modifedDate;
	}

	public String getData() {
		return data;
	}

	public void setData(String data) {
		this.data = data;
	}

	/**
	 * @return the nxSubRefId
	 */
	public String getNxSubRefId() {
		return nxSubRefId;
	}

	/**
	 * @param nxSubRefId the nxSubRefId to set
	 */
	public void setNxSubRefId(String nxSubRefId) {
		this.nxSubRefId = nxSubRefId;
	}
	
	
	
}
