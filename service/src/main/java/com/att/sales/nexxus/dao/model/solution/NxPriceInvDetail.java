package com.att.sales.nexxus.dao.model.solution;

import java.io.Serializable;
import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;


/**
 * The persistent class for the NX_PRICE_INV_DETAILS database table.
 * 
 */
@Entity
@Table(name="NX_PRICE_INV_DETAILS")
@NamedQuery(name="NxPriceInvDetail.findAll", query="SELECT n FROM NxPriceInvDetail n")
public class NxPriceInvDetail implements Serializable {
	
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;

	/** The nx price inv id. */
	@Id
	@Column(name="NX_PRICE_INV_ID")
	private long nxPriceInvId;

	/** The acct criteria. */
	@Lob
	@Column(name="ACCT_CRITERIA")
	private String acctCriteria;

	/** The created date. */
	@Temporal(TemporalType.DATE)
	@Column(name="CREATED_DATE")
	private Date createdDate;

	/** The created user. */
	@Column(name="CREATED_USER")
	private String createdUser;

	/** The edf ack id. */
	@Column(name="EDF_ACK_ID")
	private String edfAckId;

	/** The flow type. */
	@Column(name="FLOW_TYPE")
	private String flowType;

	/** The price inv resp addr. */
	@Lob
	@Column(name="PRICE_INV_RESP_ADDR")
	private String priceInvRespAddr;

	/** The status. */
	private BigDecimal status;

	/** The url. */
	private String url;

	/** The nx solution detail. */
	//bi-directional many-to-one association to NxSolutionDetail
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="NX_SOLUTION_ID")
	private NxSolutionDetail nxSolutionDetail;

	/**
	 * Instantiates a new nx price inv detail.
	 */
	public NxPriceInvDetail() {
		//
	}

	/**
	 * Gets the nx price inv id.
	 *
	 * @return the nx price inv id
	 */
	public long getNxPriceInvId() {
		return this.nxPriceInvId;
	}

	/**
	 * Sets the nx price inv id.
	 *
	 * @param nxPriceInvId the new nx price inv id
	 */
	public void setNxPriceInvId(long nxPriceInvId) {
		this.nxPriceInvId = nxPriceInvId;
	}

	/**
	 * Gets the acct criteria.
	 *
	 * @return the acct criteria
	 */
	public String getAcctCriteria() {
		return this.acctCriteria;
	}

	/**
	 * Sets the acct criteria.
	 *
	 * @param acctCriteria the new acct criteria
	 */
	public void setAcctCriteria(String acctCriteria) {
		this.acctCriteria = acctCriteria;
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
	 * Gets the edf ack id.
	 *
	 * @return the edf ack id
	 */
	public String getEdfAckId() {
		return this.edfAckId;
	}

	/**
	 * Sets the edf ack id.
	 *
	 * @param edfAckId the new edf ack id
	 */
	public void setEdfAckId(String edfAckId) {
		this.edfAckId = edfAckId;
	}

	/**
	 * Gets the flow type.
	 *
	 * @return the flow type
	 */
	public String getFlowType() {
		return this.flowType;
	}

	/**
	 * Sets the flow type.
	 *
	 * @param flowType the new flow type
	 */
	public void setFlowType(String flowType) {
		this.flowType = flowType;
	}

	/**
	 * Gets the price inv resp addr.
	 *
	 * @return the price inv resp addr
	 */
	public String getPriceInvRespAddr() {
		return this.priceInvRespAddr;
	}

	/**
	 * Sets the price inv resp addr.
	 *
	 * @param priceInvRespAddr the new price inv resp addr
	 */
	public void setPriceInvRespAddr(String priceInvRespAddr) {
		this.priceInvRespAddr = priceInvRespAddr;
	}

	/**
	 * Gets the status.
	 *
	 * @return the status
	 */
	public BigDecimal getStatus() {
		return this.status;
	}

	/**
	 * Sets the status.
	 *
	 * @param status the new status
	 */
	public void setStatus(BigDecimal status) {
		this.status = status;
	}

	/**
	 * Gets the url.
	 *
	 * @return the url
	 */
	public String getUrl() {
		return this.url;
	}

	/**
	 * Sets the url.
	 *
	 * @param url the new url
	 */
	public void setUrl(String url) {
		this.url = url;
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