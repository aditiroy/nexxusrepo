package com.att.sales.nexxus.dao.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.codehaus.jackson.annotate.JsonIgnore;

/**
 * The Class NexxusSolutionDetailUIModel.
 */
public class NexxusSolutionDetailUIModel {

	/** The nx solution id. */
	private Long nxSolutionId;

	private String dealStatus;
	
	private String dealID;

	private String dealRevision;

	private String dealVersion;
	
	private String myPriceTxnId;
	
	/** The created date. */
	private String createdDate;
	
	/** The modified date. */
	private Date modifiedDate;

	/** The created user. */
	private String createdUser;

	/** The customer name. */
	private String customerName;

	/** The duns number. */
	private String dunsNumber;

	/** The gu duns number. */
	private String guDunsNumber;

	/** The l 3 value. */
	private String l3Value;

	/** The l 4 value. */
	private String l4Value;

	/** The nxs description. */
	private String nxsDescription;

	/** The opty id. */
	private String optyId;
	
	/** The flow type. */
	private String flowType;
	
	/** The Archived Solution Indicator. */
	private String archivedSolInd;
	
	/** The ap count. */
	private String failedTokensCount;
	
	/** The ap count. */
	private String apCount;
	
	/** The ap selected count. */
	private String apSelectedCount;
	
	/** The missing field count. */
	private String missingFieldCount;
	
	private String nxReqGroupName;

	private String inrStatusInd;
	
	private String iglooStatusInd; 
	
	private String isLocked;
	
	private String lockedByUserName;
	
	private String reqFlowType;
	
	public String getNxReqGroupName() {
		return nxReqGroupName;
	}

	public void setNxReqGroupName(String nxReqGroupName) {
		this.nxReqGroupName = nxReqGroupName;
	}
	
	public String getFailedTokensCount() {
		return failedTokensCount;
	}

	public void setFailedTokensCount(String failedTokensCount) {
		this.failedTokensCount = failedTokensCount;
	}

	/**
	 * Gets the ap count.
	 *
	 * @return the ap count
	 */
	public String getApCount() {
		return apCount;
	}

	/**
	 * Sets the ap count.
	 *
	 * @param apCount the new ap count
	 */
	public void setApCount(String apCount) {
		this.apCount = apCount;
	}

	/** The nexxus edf request detail. */
	private NexxusEdfRequestDetail nexxusEdfRequestDetail;
	
	private NexxusSolnsGroups nexxusSolnsGroups;

	/** The request details. */
	private List<NexxusEdfRequestDetail> requestDetails = new ArrayList<>();
	
	private List<NexxusSolnsGroups> groups = new ArrayList<>();

	/**
	 * Gets the nexxus edf request detail.
	 *
	 * @return the nexxus edf request detail
	 */
	@JsonIgnore
	public NexxusEdfRequestDetail getNexxusEdfRequestDetail() {
		return nexxusEdfRequestDetail;
	}

	/**
	 * Sets the nexxus edf request detail.
	 *
	 * @param accountSearchData the new nexxus edf request detail
	 */
	public void setNexxusEdfRequestDetail(NexxusEdfRequestDetail accountSearchData) {
		this.nexxusEdfRequestDetail = accountSearchData;
	}

	@JsonIgnore
	public NexxusSolnsGroups getNexxusSolnsGroups() {
		return nexxusSolnsGroups;
	}

	public void setNexxusSolnsGroups(NexxusSolnsGroups nexxusSolnsGroups) {
		this.nexxusSolnsGroups = nexxusSolnsGroups;
	}

	/**
	 * Instantiates a new nexxus solution detail UI model.
	 */
	public NexxusSolutionDetailUIModel() {
		super();
	}


	/**
	 * @return the inrStatusInd
	 */
	public String getInrStatusInd() {
		return inrStatusInd;
	}

	/**
	 * @param inrStatusInd the inrStatusInd to set
	 */
	public void setInrStatusInd(String inrStatusInd) {
		this.inrStatusInd = inrStatusInd;
	}

	/**
	 * @return the iglooStatusInd
	 */
	public String getIglooStatusInd() {
		return iglooStatusInd;
	}

	/**
	 * @param iglooStatusInd the iglooStatusInd to set
	 */
	public void setIglooStatusInd(String iglooStatusInd) {
		this.iglooStatusInd = iglooStatusInd;
	}
	
	/**
	 * Instantiates a new nexxus solution detail UI model.
	 *
	 * @param nxSolutionId the nx solution id
	 * @param nxsDescription the nxs description
	 * @param optyId the opty id
	 * @param dunsNumber the duns number
	 * @param guDunsNumber the gu duns number
	 * @param l3Value the l 3 value
	 * @param l4Value the l 4 value
	 * @param customerName the customer name
	 * @param createdUser the created user
	 * @param createdDate the created date
	 * @param modifiedDate the modified date
	 * @param flowType the flow type
	 * @param archivedSolInd the archived sol index
	 * @param apCount the ap count
	 * @param apSelectedCount the ap selected count
	 * @param nxReqId the nx req id
	 * @param productCd the product cd
	 * @param cpniApprover the cpni approver
	 * @param status the status
	 * @param nxReqDesc the nx req desc
	 * @param edfReqModifiedDate the edf req modified date
	 * @param edfReqCreateDate the edf req create date
	 * @param reqStatus the req status
	 */
	public NexxusSolutionDetailUIModel(Long nxSolutionId, String dealStatus, String dealID, String dealRevision, String dealVersion, String myPriceTxnId, String nxsDescription, String optyId, String dunsNumber,
			String guDunsNumber, String l3Value, String l4Value, String customerName, String createdUser,
			String createdDate, Date modifiedDate, String flowType, String archivedSolInd, String failedTokensCount, String apCount, String apSelectedCount, Long nxReqId, String productCd, String cpniApprover, String status, String nxReqDesc,Long nxReqGroupId,String nxReqGroupName,Long nxLookUpGroupId, String groupName, String nxRequestGroupStatus, String nxRequestGroupStatusId, Date edfReqModifiedDate, Date edfReqCreateDate, String reqStatus,String nxReqGroupDesc,
			String dmaapBulkStatus,String dmappBulkStatusDesc,String bulkRequest, String inrStatusInd, String iglooStatusInd, Long sourceSolId, String isLocked, String lockedByUserName, String reqFlowType,String missingFieldCount) {
		super();
		this.nxSolutionId = nxSolutionId;
		this.dealStatus = dealStatus;
		this.dealID = dealID;
		this.dealRevision = dealRevision;
		this.dealVersion = dealVersion;
		this.myPriceTxnId = myPriceTxnId;
		this.createdDate = createdDate;
		this.modifiedDate = modifiedDate;
		this.flowType = flowType;
		this.archivedSolInd = archivedSolInd;
		this.failedTokensCount = failedTokensCount;
		this.apCount = apCount;
		this.apSelectedCount = apSelectedCount;
		this.createdUser = createdUser;
		this.customerName = customerName;
		this.dunsNumber = dunsNumber;
		this.guDunsNumber = guDunsNumber;
		this.l3Value = l3Value;
		this.l4Value = l4Value;
		this.nxsDescription = nxsDescription;
		this.nxReqGroupName = nxReqGroupName;
		this.optyId = optyId;
		this.inrStatusInd = inrStatusInd;
		this.iglooStatusInd = iglooStatusInd;
		this.isLocked = isLocked;
		this.lockedByUserName = lockedByUserName;
		this.reqFlowType = reqFlowType;
		this.missingFieldCount=missingFieldCount;
		
		if ((nxReqId != null) && (nxReqId != -1l)) {
			if(Optional.ofNullable(nxReqGroupId).isPresent()) {
				this.nexxusSolnsGroups = new NexxusSolnsGroups(nxReqGroupId,groupName, nxReqGroupDesc,nxRequestGroupStatus,nxLookUpGroupId, nxRequestGroupStatusId);
			}
			this.nexxusEdfRequestDetail = new NexxusEdfRequestDetail(nxReqId,nxReqGroupName, productCd, cpniApprover, status, nxReqDesc, edfReqModifiedDate, edfReqCreateDate,reqStatus,
					 dmaapBulkStatus, dmappBulkStatusDesc,bulkRequest,sourceSolId, missingFieldCount,reqFlowType);
		}

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
	 * Gets the created date.
	 *
	 * @return the created date
	 */
	public String getCreatedDate() {
		return createdDate;
	}

	/**
	 * Sets the created date.
	 *
	 * @param createdDate the new created date
	 */
	public void setCreatedDate(String createdDate) {
		this.createdDate = createdDate;
	}
	
	/**
	 * Gets the modified date.
	 *
	 * @return the modified date
	 */
	public Date getModifiedDate() {
		return modifiedDate;
	}

	/**
	 * Sets the modified date.
	 *
	 * @param modifiedDate the new modified date
	 */
	public void setModifiedDate(Date modifiedDate) {
		this.modifiedDate = modifiedDate;
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
	
	public String getIsLocked() {
		return isLocked;
	}

	public void setIsLocked(String isLocked) {
		this.isLocked = isLocked;
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
	 * Gets the customer name.
	 *
	 * @return the customer name
	 */
	public String getCustomerName() {
		return customerName;
	}

	/**
	 * Sets the customer name.
	 *
	 * @param customerName the new customer name
	 */
	public void setCustomerName(String customerName) {
		this.customerName = customerName;
	}

	/**
	 * Gets the duns number.
	 *
	 * @return the duns number
	 */
	public String getDunsNumber() {
		return dunsNumber;
	}

	/**
	 * Sets the duns number.
	 *
	 * @param dunsNumber the new duns number
	 */
	public void setDunsNumber(String dunsNumber) {
		this.dunsNumber = dunsNumber;
	}

	/**
	 * Gets the gu duns number.
	 *
	 * @return the gu duns number
	 */
	public String getGuDunsNumber() {
		return guDunsNumber;
	}

	/**
	 * Sets the gu duns number.
	 *
	 * @param guDunsNumber the new gu duns number
	 */
	public void setGuDunsNumber(String guDunsNumber) {
		this.guDunsNumber = guDunsNumber;
	}

	/**
	 * Gets the l 3 value.
	 *
	 * @return the l 3 value
	 */
	public String getL3Value() {
		return l3Value;
	}

	/**
	 * Sets the l 3 value.
	 *
	 * @param l3Value the new l 3 value
	 */
	public void setL3Value(String l3Value) {
		this.l3Value = l3Value;
	}

	/**
	 * Gets the l 4 value.
	 *
	 * @return the l 4 value
	 */
	public String getL4Value() {
		return l4Value;
	}

	/**
	 * Sets the l 4 value.
	 *
	 * @param l4Value the new l 4 value
	 */
	public void setL4Value(String l4Value) {
		this.l4Value = l4Value;
	}

	/**
	 * Gets the nxs description.
	 *
	 * @return the nxs description
	 */
	public String getNxsDescription() {
		return nxsDescription;
	}

	/**
	 * Sets the nxs description.
	 *
	 * @param nxsDescription the new nxs description
	 */
	public void setNxsDescription(String nxsDescription) {
		this.nxsDescription = nxsDescription;
	}

	/**
	 * Gets the opty id.
	 *
	 * @return the opty id
	 */
	public String getOptyId() {
		return optyId;
	}

	/**
	 * Sets the opty id.
	 *
	 * @param optyId the new opty id
	 */
	public void setOptyId(String optyId) {
		this.optyId = optyId;
	}

	/**
	 * Gets the request details.
	 *
	 * @return the request details
	 */
	public List<NexxusEdfRequestDetail> getRequestDetails() {
		return requestDetails;
	}

	/**
	 * Sets the request details.
	 *
	 * @param requestDetails the new request details
	 */
	public void setRequestDetails(List<NexxusEdfRequestDetail> requestDetails) {
		this.requestDetails = requestDetails;
	}

	
	public List<NexxusSolnsGroups> getGroups() {
		return groups;
	}

	public void setGroups(List<NexxusSolnsGroups> groups) {
		this.groups = groups;
	}

	/**
	 * Gets the ap selected count.
	 *
	 * @return the ap selected count
	 */
	public String getApSelectedCount() {
		return apSelectedCount;
	}

	/**
	 * Sets the ap selected count.
	 *
	 * @param apSelectedCount the new ap selected count
	 */
	public void setApSelectedCount(String apSelectedCount) {
		this.apSelectedCount = apSelectedCount;
	}

	/**
	 * 
	 * The class NexxusSolnsGroups
	 *
	 */
	public static class NexxusSolnsGroups{
		
		
		private Long nxReqGroupId;
		
		private String nxReqGroupDesc;
	
		private String nxRequestGroupStatus;
		
		private Long nxLookUpGroupId;
		
		private String groupName;
		
		private String nxRequestGroupStatusId;
		
		private List<NexxusEdfRequestDetail> requestDetails = new ArrayList<>();

		public NexxusSolnsGroups(Long nxReqGroupId,String groupName, String nxReqGroupDesc,String nxRequestGroupStatus,Long nxLookUpGroupId, String nxRequestGroupStatusId){
			this.nxReqGroupId = nxReqGroupId;
			this.nxReqGroupDesc = nxReqGroupDesc;
			this.nxLookUpGroupId = nxLookUpGroupId;
			this.nxRequestGroupStatus = nxRequestGroupStatus;
			this.groupName = groupName;
			this.setNxRequestGroupStatusId(nxRequestGroupStatusId);
		}
		public Long getNxReqGroupId() {
			return nxReqGroupId;
		}

		public void setNxReqGroupId(Long nxReqGroupId) {
			this.nxReqGroupId = nxReqGroupId;
		}

		public String getNxReqGroupDesc() {
			return nxReqGroupDesc;
		}

		public void setNxReqGroupDesc(String nxReqGroupDesc) {
			this.nxReqGroupDesc = nxReqGroupDesc;
		}

		public List<NexxusEdfRequestDetail> getRequestDetails() {
			return requestDetails;
		}

		public void setRequestDetails(List<NexxusEdfRequestDetail> requestDetails) {
			this.requestDetails = requestDetails;
		}
		public String getNxRequestGroupStatus() {
			return nxRequestGroupStatus;
		}
		public void setNxRequestGroupStatus(String nxRequestGroupStatus) {
			this.nxRequestGroupStatus = nxRequestGroupStatus;
		}
		public Long getNxLookUpGroupId() {
			return nxLookUpGroupId;
		}
		public void setNxLookUpGroupId(Long nxLookUpGroupId) {
			this.nxLookUpGroupId = nxLookUpGroupId;
		}
		public String getGroupName() {
			return groupName;
		}
		public void setGroupName(String groupName) {
			this.groupName = groupName;
		}
		public String getNxRequestGroupStatusId() {
			return nxRequestGroupStatusId;
		}
		public void setNxRequestGroupStatusId(String nxRequestGroupStatusId) {
			this.nxRequestGroupStatusId = nxRequestGroupStatusId;
		}
		
	}
	
	/**
	 * The Class NexxusEdfRequestDetail.
	 */
	public static class NexxusEdfRequestDetail {
		
		/** The nx req id. */
		private Long nxReqId;

		/** The product cd. */
		private String productCd;
		
		/** The cpni approver. */
		private String cpniApprover;

		/** The status. */
		private String status;
		
		/** The nx req desc. */
		private String nxReqDesc;
		
		/** The edf req modified date. */
		private Date edfReqModifiedDate;
		
		/** The edf req create date. */
		private Date edfReqCreateDate;
		
		/** The req status. */
		private String reqStatus;
		
		private String requestGroupName;
		
		private String dmaapBulkStatus;
		
		private String dmappBulkStatusDesc;
		
		private String bulkRequest;
		
		private Long sourceSolId;
		
		/** The flow type. */
		private String flowType;
		
		
		/**
		 * Instantiates a new nexxus edf request detail.
		 *
		 * @param nxReqId the nx req id
		 * @param productCd the product cd
		 * @param cpniApprover the cpni approver
		 * @param status the status
		 * @param nxReqDesc the nx req desc
		 * @param edfReqModifiedDate the edf req modified date
		 * @param edfReqCreateDate the edf req create date
		 * @param reqStatus the req status
		 * @param inrStatusInd inrStatusInd
		 * @param iglooStatusInd iglooStatusInd
		 */
		public NexxusEdfRequestDetail(Long nxReqId, String requestGroupName, String productCd, String cpniApprover, String status, String nxReqDesc, Date edfReqModifiedDate, Date edfReqCreateDate, String reqStatus,String dmaapBulkStatus, String dmappBulkStatusDesc,String bulkRequest,Long sourceSolId, String missingFieldCount,String flowType) {
			super();
			this.nxReqId = nxReqId;
			this.productCd = productCd;
			this.cpniApprover = cpniApprover;
			this.status = status;
			this.nxReqDesc = nxReqDesc;
			this.edfReqModifiedDate = edfReqModifiedDate;
			this.edfReqCreateDate = edfReqCreateDate;
			this.reqStatus = reqStatus;
			this.requestGroupName = requestGroupName;
			this.dmaapBulkStatus= dmaapBulkStatus;
			this.dmappBulkStatusDesc=dmappBulkStatusDesc;
			this.bulkRequest=bulkRequest;
			this.sourceSolId=sourceSolId;
			this.flowType=flowType;
		}

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
		 * Gets the product cd.
		 *
		 * @return the product cd
		 */
		public String getProductCd() {
			return productCd;
		}

		/**
		 * Sets the product cd.
		 *
		 * @param productCd the new product cd
		 */
		public void setProductCd(String productCd) {
			this.productCd = productCd;
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
		 * Gets the edf req create date.
		 *
		 * @return the edf req create date
		 */
		public Date getEdfReqCreateDate() {
			return edfReqCreateDate;
		}

		/**
		 * Sets the edf req create date.
		 *
		 * @param edfReqCreateDate the new edf req create date
		 */
		public void setEdfReqCreateDate(Date edfReqCreateDate) {
			this.edfReqCreateDate = edfReqCreateDate;
		}
		
		/**
		 * Gets the edf req modified date.
		 *
		 * @return the edf req modified date
		 */
		public Date getEdfReqModifiedDate() {
			return edfReqModifiedDate;
		}

		/**
		 * Sets the edf req modified date.
		 *
		 * @param edfReqModifiedDate the new edf req modified date
		 */
		public void setEdfReqModifiedDate(Date edfReqModifiedDate) {
			this.edfReqModifiedDate = edfReqModifiedDate;
		}

		/**
		 * Gets the req status.
		 *
		 * @return the req status
		 */
		public String getReqStatus() {
			return reqStatus;
		}

		/**
		 * Sets the req status.
		 *
		 * @param reqStatus the new req status
		 */
		public void setReqStatus(String reqStatus) {
			this.reqStatus = reqStatus;
		}

		public String getRequestGroupName() {
			return requestGroupName;
		}

		public void setRequestGroupName(String requestGroupName) {
			this.requestGroupName = requestGroupName;
		}

		public String getDmaapBulkStatus() {
			return dmaapBulkStatus;
		}

		public void setDmaapBulkStatus(String dmaapBulkStatus) {
			this.dmaapBulkStatus = dmaapBulkStatus;
		}

		public String getDmappBulkStatusDesc() {
			return dmappBulkStatusDesc;
		}

		public void setDmappBulkStatusDesc(String dmappBulkStatusDesc) {
			this.dmappBulkStatusDesc = dmappBulkStatusDesc;
		}

		public String getBulkRequest() {
			return bulkRequest;
		}

		public void setBulkRequest(String bulkRequest) {
			this.bulkRequest = bulkRequest;
		}

		public Long getSourceSolId() {
			return sourceSolId;
		}

		public void setSourceSolId(Long sourceSolId) {
			this.sourceSolId = sourceSolId;
		}

		public String getFlowType() {
			return flowType;
		}

		public void setFlowType(String flowType) {
			this.flowType = flowType;
		}
					
	}
	public String getDealStatus() {
		return dealStatus;
	}

	public void setDealStatus(String dealStatus) {
		this.dealStatus = dealStatus;
	}

	public String getDealID() {
		return dealID;
	}

	public void setDealID(String dealID) {
		this.dealID = dealID;
	}

	public String getDealRevision() {
		return dealRevision;
	}

	public void setDealRevision(String dealRevision) {
		this.dealRevision = dealRevision;
	}

	public String getDealVersion() {
		return dealVersion;
	}

	public void setDealVersion(String dealVersion) {
		this.dealVersion = dealVersion;
	}

	public String getMyPriceTxnId() {
		return myPriceTxnId;
	}

	public void setMyPriceTxnId(String myPriceTxnId) {
		this.myPriceTxnId = myPriceTxnId;
	}

	public String getArchivedSolInd() {
		return archivedSolInd;
	}

	public void setArchivedSolInd(String archivedSolInd) {
		this.archivedSolInd = archivedSolInd;
	}

	/**
	 * @return the lockedByUserName
	 */
	public String getLockedByUserName() {
		return lockedByUserName;
	}

	/**
	 * @param lockedByUserName the lockedByUserName to set
	 */
	public void setLockedByUserName(String lockedByUserName) {
		this.lockedByUserName = lockedByUserName;
	}

	public String getReqFlowType() {
		return reqFlowType;
	}

	public void setReqFlowType(String reqFlowType) {
		this.reqFlowType = reqFlowType;
	}

	public String getMissingFieldCount() {
		return missingFieldCount;
	}

	public void setMissingFieldCount(String missingFieldCount) {
		this.missingFieldCount = missingFieldCount;
	}
	
}
