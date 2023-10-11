package com.att.sales.nexxus.dao.model;

import java.io.Serializable;
import java.sql.Clob;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import com.att.sales.nexxus.dao.model.solution.NxSolutionDetail;

/**
 * The Class NxRequestDetails.
 */
@Entity
@Table(name = "NX_REQUEST_DETAILS")
@NamedQueries({ 
	@NamedQuery(name = "NxRequestDetails.findByNxReqId", query = "SELECT s FROM NxRequestDetails s where nxReqId = :nxReqId"), })
public class NxRequestDetails implements Serializable {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;

	
	/** The nx req id. */
	@Id
	@SequenceGenerator(name = "sequence_nx_request_details", sequenceName = "SEQ_NX_REQ_ID", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequence_nx_request_details")
	@Column(name = "NX_REQ_ID")
	private Long nxReqId;

	/** The product. */
	@Column(name = "PRODUCT_CD")
	private String product;

	/** The cpni approver. */
	@Column(name = "CPNI_APPROVER")
	private String cpniApprover;

	/** The acct criteria. */
	@Column(name = "ACCT_CRITERIA")
	private String acctCriteria;

	/** The inv resp addr. */
	@Column(name = "PRICE_INV_RESP_ADDR")
	private Clob invRespAddr;

	/** The edf ack id. */
	@Column(name = "EDF_ACK_ID")
	private String edfAckId;// resp.getREquestId

	/** The user. */
	@Column(name = "CREATED_USER")
	private String user;

	/** The status. */
	@Column(name = "STATUS")
	private Long status;

	/** The created date. */
	@Column(name = "CREATED_DATE")
	private Date createdDate = new Date();

	/** The file name. */
	@Column(name = "FILE_NAME")
	private String fileName;
	
	/** The dmaap msg. */
	@Column(name = "DMAAP_MSG")
	private String dmaapMsg;
	
	/** The nx req desc. */
	@Column(name = "NX_REQ_DESC")
	private String nxReqDesc;
	
	/** The modifed date. */
	@Column(name = "MODIFIED_DATE")
	private Date modifedDate = new Date();

	/** The nx solution detail. */
	// bi-directional many-to-one association to NxSolutionDetail
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "NX_SOLUTION_ID")
	private NxSolutionDetail nxSolutionDetail;

	/** The nx output files. */
	// bi-directional many-to-one association to NxOutputFile
	@OneToMany(cascade = CascadeType.ALL, mappedBy = "nxRequestDetails", fetch = FetchType.EAGER)
	private List<NxOutputFileModel> nxOutputFiles;
	
	@Column(name = "NX_REQUEST_GROUP_NAME")
	private String nxRequestGroupName;
	
	/** The active yn. */
	@Column(name="ACTIVE_YN")
	private String activeYn;

	/** The active yn. */
	@Column(name="BULK_REQ_YN")
	private String bulkReqYn;
	
	@Column(name="MANAGE_BILLING_PRICE_JSON")
	private String manageBillingPriceJson;

	@Column(name="VALIDATE_ACCDATA_REQ_JSON")
	private String validateAccountDataRequestJson;
	
	@Column(name="DMAAP_BULK_STATUS")
	private String dmaapBulkStatus;

	@Column(name="SOURCE_SOL_ID")
	private Long sourceSolId;

	
	/**
	 * Gets the file name.
	 *
	 * @return the file name
	 */
	public String getFileName() {
		return fileName;
	}

	public String getManageBillingPriceJson() {
		return manageBillingPriceJson;
	}

	public void setManageBillingPriceJson(String manageBillingPriceJson) {
		this.manageBillingPriceJson = manageBillingPriceJson;
	}

	/**
	 * Sets the file name.
	 *
	 * @param fileName the new file name
	 */
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	/** The flow type. */
	@Column(name = "FLOW_TYPE")
	private String flowType;
	
	@Column(name = "NX_REQUEST_GROUP_ID")
	private Long nxRequestGroupId;
	
	@Column(name = "SUBMITREQ_ADDR_EDIT_IND")
	private String submitReqAddrEditInd;

	/**
	 * Gets the nx req id.
	 *
	 * @return the nx req id
	 */
	public Long getNxReqId() {
		return nxReqId;
	}

	/**
	 * Sets the nx req id.
	 *
	 * @param nxReqId the new nx req id
	 */
	public void setNxReqId(Long nxReqId) {
		this.nxReqId = nxReqId;
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
	 * Gets the cpni approver.
	 *
	 * @return the cpni approver
	 */
	public String getCpniApprover() {
		return cpniApprover;
	}

	/**
	 * Sets the cpni approver.
	 *
	 * @param cpniApprover the new cpni approver
	 */
	public void setCpniApprover(String cpniApprover) {
		this.cpniApprover = cpniApprover;
	}

	/**
	 * Gets the acct criteria.
	 *
	 * @return the acct criteria
	 */
	public String getAcctCriteria() {
		return acctCriteria;
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
	 * Gets the inv resp addr.
	 *
	 * @return the inv resp addr
	 */
	public Clob getInvRespAddr() {
		return invRespAddr;
	}

	/**
	 * Sets the inv resp addr.
	 *
	 * @param invRespAddr the new inv resp addr
	 */
	public void setInvRespAddr(Clob invRespAddr) {
		this.invRespAddr = invRespAddr;
	}

	

	/**
	 * Gets the user.
	 *
	 * @return the user
	 */
	public String getUser() {
		return user;
	}

	/**
	 * Sets the user.
	 *
	 * @param user the new user
	 */
	public void setUser(String user) {
		this.user = user;
	}

	/**
	 * Gets the status.
	 *
	 * @return the status
	 */
	public Long getStatus() {
		return status;
	}

	/**
	 * Sets the status.
	 *
	 * @param status the new status
	 */
	public void setStatus(Long status) {
		this.status = status;
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
	 * Gets the flow type.
	 *
	 * @return the flow type
	 */
	public String getFlowType() {
		return flowType;
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
	 * Gets the nx solution detail.
	 *
	 * @return the nx solution detail
	 */
	public NxSolutionDetail getNxSolutionDetail() {
		return nxSolutionDetail;
	}

	/**
	 * Sets the nx solution detail.
	 *
	 * @param nxSolutionDetail the new nx solution detail
	 */
	public void setNxSolutionDetail(NxSolutionDetail nxSolutionDetail) {
		this.nxSolutionDetail = nxSolutionDetail;
	}

	/**
	 * Gets the nx output files.
	 *
	 * @return the nx output files
	 */
	public List<NxOutputFileModel> getNxOutputFiles() {
		return nxOutputFiles;
	}

	/**
	 * Sets the nx output files.
	 *
	 * @param nxOutputFiles the new nx output files
	 */
	public void setNxOutputFiles(List<NxOutputFileModel> nxOutputFiles) {
		this.nxOutputFiles = nxOutputFiles;
	}
	
	public void addNxOutputFiles(NxOutputFileModel nxOutputFile) {
		if (nxOutputFiles == null) {
			nxOutputFiles = new ArrayList<>();
		}
		nxOutputFiles.add(nxOutputFile);
		nxOutputFile.setNxRequestDetails(this);
	}

	/**
	 * Gets the edf ack id.
	 *
	 * @return the edf ack id
	 */
	public String getEdfAckId() {
		return edfAckId;
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
	 * Gets the dmaap msg.
	 *
	 * @return the dmaap msg
	 */
	public String getDmaapMsg() {
		return dmaapMsg;
	}

	/**
	 * Sets the dmaap msg.
	 *
	 * @param dmaapMsg the new dmaap msg
	 */
	public void setDmaapMsg(String dmaapMsg) {
		this.dmaapMsg = dmaapMsg;
	}

	/**
	 * Gets the nx req desc.
	 *
	 * @return the nx req desc
	 */
	public String getNxReqDesc() {
		return nxReqDesc;
	}

	/**
	 * Sets the nx req desc.
	 *
	 * @param nxReqDesc the new nx req desc
	 */
	public void setNxReqDesc(String nxReqDesc) {
		this.nxReqDesc = nxReqDesc;
	}
	
	/**
	 * Gets the modifed date.
	 *
	 * @return the modifed date
	 */
	public Date getModifedDate() {
		return modifedDate;
	}

	/**
	 * Sets the modifed date.
	 *
	 * @param modifedDate the new modifed date
	 */
	public void setModifedDate(Date modifedDate) {
		this.modifedDate = modifedDate;
	}

	/**
	 * @return the nxRequestGroupId
	 */
	public Long getNxRequestGroupId() {
		return nxRequestGroupId;
	}

	/**
	 * @param nxRequestGroupId the nxRequestGroupId to set
	 */
	public void setNxRequestGroupId(Long nxRequestGroupId) {
		this.nxRequestGroupId = nxRequestGroupId;
	}

	/**
	 * @return the nxRequestGroupName
	 */
	public String getNxRequestGroupName() {
		return nxRequestGroupName;
	}

	/**
	 * @param nxRequestGroupName the nxRequestGroupName to set
	 */
	public void setNxRequestGroupName(String nxRequestGroupName) {
		this.nxRequestGroupName = nxRequestGroupName;
	}

	public String getActiveYn() {
		return activeYn;
	}

	public void setActiveYn(String activeYn) {
		this.activeYn = activeYn;
	}

	/**
	 * @return the bulkReqYn
	 */
	public String getBulkReqYn() {
		return bulkReqYn;
	}

	/**
	 * @param bulkReqYn the bulkReqYn to set
	 */
	public void setBulkReqYn(String bulkReqYn) {
		this.bulkReqYn = bulkReqYn;
	}

	/**
	 * @return the validateAccountDataRequestJson
	 */
	public String getValidateAccountDataRequestJson() {
		return validateAccountDataRequestJson;
	}

	/**
	 * @param validateAccountDataRequestJson the validateAccountDataRequestJson to set
	 */
	public void setValidateAccountDataRequestJson(String validateAccountDataRequestJson) {
		this.validateAccountDataRequestJson = validateAccountDataRequestJson;
	}
	
	public String getDmaapBulkStatus() {
		return dmaapBulkStatus;
	}

	public void setDmaapBulkStatus(String dmaapBulkStatus) {
		this.dmaapBulkStatus = dmaapBulkStatus;
	}
	
	public Long getSourceSolId() {
		return sourceSolId;
	}

	public void setSourceSolId(Long sourceSolId) {
		this.sourceSolId = sourceSolId;
	}

	public String getSubmitReqAddrEditInd() {
		return submitReqAddrEditInd;
	}

	public void setSubmitReqAddrEditInd(String submitReqAddrEditInd) {
		this.submitReqAddrEditInd = submitReqAddrEditInd;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((nxRequestGroupId == null) ? 0 : nxRequestGroupId.hashCode());
		return result;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		NxRequestDetails other = (NxRequestDetails) obj;
		if (nxRequestGroupId == null) {
			if (other.nxRequestGroupId != null)
				return false;
		} else if (!nxRequestGroupId.equals(other.nxRequestGroupId))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "NxRequestDetails [nxReqId=" + nxReqId + ", product=" + product + ", cpniApprover=" + cpniApprover
				+ ", acctCriteria=" + acctCriteria + ", invRespAddr=" + invRespAddr + ", edfAckId=" + edfAckId
				+ ", user=" + user + ", status=" + status + ", createdDate=" + createdDate + ", fileName=" + fileName
				+ ", dmaapMsg=" + dmaapMsg + ", nxReqDesc=" + nxReqDesc + ", modifedDate=" + modifedDate
				+ ", nxSolutionDetail=" + nxSolutionDetail + ", nxOutputFiles=" + nxOutputFiles
				+ ", nxRequestGroupName=" + nxRequestGroupName + ", activeYn=" + activeYn + ", bulkReqYn=" + bulkReqYn
				+ ", manageBillingPriceJson=" + manageBillingPriceJson + ", validateAccountDataRequestJson="
				+ validateAccountDataRequestJson + ", dmaapBulkStatus=" + dmaapBulkStatus + ", sourceSolId="
				+ sourceSolId + ", flowType=" + flowType + ", nxRequestGroupId=" + nxRequestGroupId
				+ ", submitReqAddrEditInd=" + submitReqAddrEditInd + "]";
	}

}
