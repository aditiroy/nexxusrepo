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
@Table(name="NX_INR_DMAAP_AUDIT")
@Getter
@Setter
public class NxInrDmaapAudit implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name="NX_DMAAP_AUDIT_ID")
	@SequenceGenerator(name="sequence_nx_inr_dmaap_audit",sequenceName="SEQ_NX_DMAAP_AUDIT_ID", allocationSize=1)
	@GeneratedValue(strategy=GenerationType.SEQUENCE, generator="sequence_nx_inr_dmaap_audit")
	private Long id;
	
	@Column(name = "NX_TRANSACTION_TYPE")
	private String nxTransactionType;
	
	@Column(name = "NX_CORRELATION_ID")
	private String nxCorrelationId;
	
	@Column(name = "NX_MESSAGE")
	private String nxMessage;
	
	@Column(name = "NX_PROCESS_STATUS")
	private String nxProcessStatus;
	
	@Column(name = "CREATED_TIME")
	private Date createdTime = new Date();
	
	@Column(name = "MODIFIED_TIME")
	private Date modifiedTime = new Date();
	
	@Column(name = "NX_POD_NAME")
	private String nxPodName;
	
	@Column(name = "NX_SOLUTION_ID")
	private Long nxSolutionId;
	
}
