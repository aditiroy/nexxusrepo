package com.att.sales.nexxus.dao.model;

import java.io.Serializable;
import java.sql.Clob;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

/**
 * The Class NxAcctSearchDetails.
 */
@Entity
@Table(name="NX_ACCT_SEARCH_DETAILS")
public class NxAcctSearchDetails implements Serializable{
	
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;
	
	/** The nx act search id. */
	@Id
	@SequenceGenerator(name="sequence_nx_acct_search_details",sequenceName="SEQ_NX_ACT_SEARCH_ID", allocationSize=1)
	@GeneratedValue(strategy=GenerationType.SEQUENCE, generator="sequence_nx_acct_search_details")
	
	@Column(name="NX_ACT_SEARCH_ID")
	private Long nxActSearchId; 
	
	/** The nx solution id. */
	@Column(name="NX_SOLUTION_ID")
	private Long nxSolutionId;
	
	/** The acct search criteria. */
	@Column(name="ACCT_SEARCH_CRITERIA")
	private Clob acctSearchCriteria;
	
	
	/** The acct resp json. */
	@Column(name="ACCT_RESP_JSON")
	private Clob acctRespJson;
	
	/** The created user. */
	@Column(name="CREATED_USER")
	private String createdUser;
	
	/** The created date. */
	@Column(name="CREATED_DATE")
	private Date createdDate;
	
	/** The product. */
	@Column(name="PRODUCT")
	private String product;
	
	/** The status. */
	@Column(name="STATUS")
	private String status;

	/**
	 * Gets the nx act search id.
	 *
	 * @return the nx act search id
	 */
	public Long getNxActSearchId() {
		return nxActSearchId;
	}

	/**
	 * Sets the nx act search id.
	 *
	 * @param nxActSearchId the new nx act search id
	 */
	public void setNxActSearchId(Long nxActSearchId) {
		this.nxActSearchId = nxActSearchId;
	}

	/**
	 * Gets the nx solution id.
	 *
	 * @return the nx solution id
	 */
	public Long getNxSolutionId() {
		return nxSolutionId;
	}

	/**
	 * Sets the nx solution id.
	 *
	 * @param nxSolutionId the new nx solution id
	 */
	public void setNxSolutionId(Long nxSolutionId) {
		this.nxSolutionId = nxSolutionId;
	}

	/**
	 * Gets the acct search criteria.
	 *
	 * @return the acct search criteria
	 */
	public Clob getAcctSearchCriteria() {
		return acctSearchCriteria;
	}

	/**
	 * Sets the acct search criteria.
	 *
	 * @param acctSearchCriteria the new acct search criteria
	 */
	public void setAcctSearchCriteria(Clob acctSearchCriteria) {
		this.acctSearchCriteria = acctSearchCriteria;
	}

	/**
	 * Gets the acct resp json.
	 *
	 * @return the acct resp json
	 */
	public Clob getAcctRespJson() {
		return acctRespJson;
	}

	/**
	 * Sets the acct resp json.
	 *
	 * @param acctRespJson the new acct resp json
	 */
	public void setAcctRespJson(Clob acctRespJson) {
		this.acctRespJson = acctRespJson;
	}

	/**
	 * Gets the created user.
	 *
	 * @return the created user
	 */
	public String getCreatedUser() {
		return createdUser;
	}

	/**
	 * Sets the created user.
	 *
	 * @param createdUser the new created user
	 */
	public void setCreatedUser(String createdUser) {
		this.createdUser = createdUser;
	}

	/**
	 * Gets the created date.
	 *
	 * @return the created date
	 */
	public Date getCreatedDate() {
		return createdDate;
	}

	/**
	 * Sets the created date.
	 *
	 * @param createdDate the new created date
	 */
	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}

	/**
	 * Gets the product.
	 *
	 * @return the product
	 */
	public String getProduct() {
		return product;
	}

	/**
	 * Sets the product.
	 *
	 * @param product the new product
	 */
	public void setProduct(String product) {
		this.product = product;
	}

	/**
	 * Gets the status.
	 *
	 * @return the status
	 */
	public String getStatus() {
		return status;
	}

	/**
	 * Sets the status.
	 *
	 * @param status the new status
	 */
	public void setStatus(String status) {
		this.status = status;
	}
	
	

}
