package com.att.sales.nexxus.dao.model.solution;

import java.io.Serializable;
import javax.persistence.*;
import java.util.Date;


/**
 * The persistent class for the NX_ACCT_SEARCH_DETAILS database table.
 * 
 */
@Entity
@Table(name="NX_ACCT_SEARCH_DETAILS")
@NamedQuery(name="NxAcctSearchDetail.findAll", query="SELECT n FROM NxAcctSearchDetail n")
public class NxAcctSearchDetail implements Serializable {
	
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;

	/** The nx act search id. */
	@Id
//	@SequenceGenerator(name="NX_ACCT_SEARCH_DETAILS_NXACTSEARCHID_GENERATOR", sequenceName="SEQ_NX_ACT_SEARCH_ID")  //Uncomment this line after duplicate entity is deleted
//	@GeneratedValue(strategy=GenerationType.SEQUENCE, generator="NX_ACCT_SEARCH_DETAILS_NXACTSEARCHID_GENERATOR")  //Uncomment this line after duplicate entity is deleted
	@Column(name="NX_ACT_SEARCH_ID")
	private long nxActSearchId;

	/** The acct resp json. */
	@Lob
	@Column(name="ACCT_RESP_JSON")
	private String acctRespJson;

	/** The acct search criteria. */
	@Lob
	@Column(name="ACCT_SEARCH_CRITERIA")
	private String acctSearchCriteria;

	/** The created date. */
	@Temporal(TemporalType.DATE)
	@Column(name="CREATED_DATE")
	private Date createdDate;

	/** The created user. */
	@Column(name="CREATED_USER")
	private String createdUser;

	/** The product. */
	private String product;

	/** The status. */
	private String status;

	/** The nx solution detail. */
	//bi-directional many-to-one association to NxSolutionDetail
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="NX_SOLUTION_ID")
	private NxSolutionDetail nxSolutionDetail;

	/**
	 * Instantiates a new nx acct search detail.
	 */
	public NxAcctSearchDetail() {
		//
	}

	/**
	 * Gets the nx act search id.
	 *
	 * @return the nx act search id
	 */
	public long getNxActSearchId() {
		return this.nxActSearchId;
	}

	/**
	 * Sets the nx act search id.
	 *
	 * @param nxActSearchId the new nx act search id
	 */
	public void setNxActSearchId(long nxActSearchId) {
		this.nxActSearchId = nxActSearchId;
	}

	/**
	 * Gets the acct resp json.
	 *
	 * @return the acct resp json
	 */
	public String getAcctRespJson() {
		return this.acctRespJson;
	}

	/**
	 * Sets the acct resp json.
	 *
	 * @param acctRespJson the new acct resp json
	 */
	public void setAcctRespJson(String acctRespJson) {
		this.acctRespJson = acctRespJson;
	}

	/**
	 * Gets the acct search criteria.
	 *
	 * @return the acct search criteria
	 */
	public String getAcctSearchCriteria() {
		return this.acctSearchCriteria;
	}

	/**
	 * Sets the acct search criteria.
	 *
	 * @param acctSearchCriteria the new acct search criteria
	 */
	public void setAcctSearchCriteria(String acctSearchCriteria) {
		this.acctSearchCriteria = acctSearchCriteria;
	}

	/**
	 * Gets the created date.
	 *
	 * @return the created date
	 */
	public Date getCreatedDate() {
		return this.createdDate;
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
	 * Gets the created user.
	 *
	 * @return the created user
	 */
	public String getCreatedUser() {
		return this.createdUser;
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
	 * Gets the product.
	 *
	 * @return the product
	 */
	public String getProduct() {
		return this.product;
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
		return this.status;
	}

	/**
	 * Sets the status.
	 *
	 * @param status the new status
	 */
	public void setStatus(String status) {
		this.status = status;
	}

	/**
	 * Gets the nx solution detail.
	 *
	 * @return the nx solution detail
	 */
	public NxSolutionDetail getNxSolutionDetail() {
		return this.nxSolutionDetail;
	}

	/**
	 * Sets the nx solution detail.
	 *
	 * @param nxSolutionDetail the new nx solution detail
	 */
	public void setNxSolutionDetail(NxSolutionDetail nxSolutionDetail) {
		this.nxSolutionDetail = nxSolutionDetail;
	}

}