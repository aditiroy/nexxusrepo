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

import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "NX_FEEDBACK")
@Getter
@Setter
public class NxFeedback {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Id
	@SequenceGenerator(name = "sequence_nx_feedback", sequenceName = "SEQ_NX_FEEDBACK_ID", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequence_nx_feedback")
	@Column(name = "NX_FEEDBACK_ID")
	private Long nxFeedbackId;

	@Column(name = "ATTID")
	private String attId;
	
	@Column(name = "EMAIL")
	private String email;
	
	@Column(name = "FEEDBACK")
	private String feedback;
	
	@Column(name = "CREATED_DATE")
	private Date createdDate;
}
