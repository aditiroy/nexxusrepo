package com.att.sales.nexxus.dao.model.solution;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQuery;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

/**
 * The persistent class for the NX_UI_AUDIT database table.
 * 
 */
@Entity
@Table(name="NX_UI_AUDIT")
public class NxUiAudit implements Serializable {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;
	
	@Id
	@SequenceGenerator(name="NX_UI_AUDIT_ID_GENERATOR", sequenceName="SEQ_NX_UI_AUDIT_ID", allocationSize=1) 
	@GeneratedValue(strategy=GenerationType.SEQUENCE, generator="NX_UI_AUDIT_ID_GENERATOR") 
	@Column(name="ID")
	private long id;
	
	/** The NX_SOLUTION_ID. */
	@Column(name="NX_SOLUTION_ID")
	private long nxSolutionId;

	/** The created date. */
	@Column(name="CREATED_DATE")
	private Date createdDate = new Date();

	/** The action. */
	@Column(name="ACTION")
	private String action;
	
	/** The status. */
	@Column(name="STATUS")
	private String status;
	
	/** The message. */
	@Column(name="MESSAGE")
	private String message;
	
	/** The attid. */
	@Column(name="ATT_ID")
	private String attId;

	@Column(name="EXECUTION_TIME")
	private Long executionTime;

	@Column(name="ACTION_TYPE")
	private String actionType;
	
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public long getNxSolutionId() {
		return nxSolutionId;
	}

	public void setNxSolutionId(long nxSolutionId) {
		this.nxSolutionId = nxSolutionId;
	}

	public Date getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getAttId() {
		return attId;
	}

	public void setAttId(String attId) {
		this.attId = attId;
	}
	
	public Long getExecutionTime(Long executionTime) {
		return executionTime;
	}
	
	public void setExecutionTime(Long executionTime) {
		this.executionTime = executionTime;
	}

	public String getActionType(String actionType) {
		return actionType;
	}
	
	public void setActionType(String actionType) {
		this.actionType = actionType;
	}
	
}
