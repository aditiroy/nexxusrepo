package com.att.sales.nexxus.dao.model;

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
@Table(name = "NX_USER_LOCK_DETAILS")
@Getter
@Setter
public class NxUserLockDetails {

	private static final long serialVersionUID = 1L;

	@Id
	@SequenceGenerator(name = "sequence_nx_lock_id", sequenceName = "SEQ_NX_LOCK_ID", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequence_nx_lock_id")
	@Column(name = "NX_LOCK_ID")
	private Long nxLockId;

	@Column(name = "NX_SOLUTION_ID")
	private Long nxSolutionId;

	@Column(name = "LOCKED_BY_USER")
	private String lockedByUser;

	@Column(name = "IS_LOCKED")
	private String isLocked;

	@Column(name = "CREATED_DATE")
	private Date createdDate;

	@Column(name = "MODIFIED_DATE")
	private Date modifiedDate;
}
