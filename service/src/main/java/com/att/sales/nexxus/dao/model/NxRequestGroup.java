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
import javax.persistence.Transient;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name="NX_REQUEST_GROUP")
@Getter
@Setter
public class NxRequestGroup implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Id
	@SequenceGenerator(name = "SEQUENCE_NX_REQUEST_GROUP", sequenceName = "SEQ_NX_REQUEST_GROUP_ID", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQUENCE_NX_REQUEST_GROUP")
	@Column(name = "NX_REQUEST_GROUP_ID")
	private Long nxRequestGroupId;
	
	@Column(name = "GROUP_ID")
	private Long groupId;
	
	@Column(name = "DESCRIPTION")
	private String description;
	
	@Column(name = "STATUS")
	private String status;
	
	@Column(name = "NX_SOLUTION_ID")
	private Long nxSolutionId;
	
	/** The active yn. */
	@Column(name="ACTIVE_YN")
	private String activeYn;
	
	/** The created date. */
	@CreationTimestamp
	@Column(name="CREATED_DATE")
	private Date createdDate = new Date();
	
	/** The modified date. */
	@UpdateTimestamp
	@Column(name="MODIFIED_DATE")
	private Date modifiedDate = new Date();
	
	@Transient
	private String statusName;
	
	@Column(name="GROUPNAME_EDIT_IND")
	private String groupNameEditInd;
}