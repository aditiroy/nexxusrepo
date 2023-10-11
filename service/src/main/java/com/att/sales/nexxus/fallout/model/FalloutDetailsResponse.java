package com.att.sales.nexxus.fallout.model;

import java.util.List;
import java.util.Set;

import com.att.sales.framework.model.ServiceResponse;
import com.att.sales.nexxus.dao.model.NxRequestGroup;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * The Class FalloutDetailsResponse.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FalloutDetailsResponse extends ServiceResponse {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;
	
	/** The nx solution id. */
	@JsonProperty("nxSolutionId")
	private Long nxSolutionId;
	
	/** The nx req id. */
	@JsonProperty("nxReqId")
	private Long nxReqId;
	
	/** The fall out beids. */
	@JsonProperty("beid")
	private Set<String> fallOutBeids;

	/** The ai result. */
	@JsonProperty("aiResult")
	private boolean aiResult;
	
	/** The req desc. */
	@JsonProperty("reqDesc")
	private String reqDesc;
	
	private String statusChanged;
	
	private Long currentStatus;
	
	private String currentReqStatus;
	
	private String groupStatus;
	
	private Object manageBillingPriceInventoryDataRequest;
	
	private List<String> groupName;
	
	private List<Groups> groups;
	
	private List<AdminUserList> adminUserList;
	
	private List<Long> dataIds;
	
	private List<NxRequestGroup> nxRequestGroups;
	
	private Long apCount;
	
	private Long apSelectedCount;
	
	private String nxsDescription;
	
	private String flowType;
	
	private List<NxRequests> nxRequests;
	
	private List<NxProductRequest> products;
	
	
	public List<NxProductRequest> getProducts() {
		return products;
	}

	public void setProducts(List<NxProductRequest> products) {
		this.products = products;
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
	 * Gets the fall out beid.
	 *
	 * @return the fallOutBeid
	 */
	public Set<String> getFallOutBeid() {
		return fallOutBeids;
	}

	/**
	 * Sets the fall out beid.
	 *
	 * @param fallOutBeids the new fall out beid
	 */
	public void setFallOutBeid(Set<String> fallOutBeids) {
		this.fallOutBeids = fallOutBeids;
	}

	/**
	 * Gets the ai result.
	 *
	 * @return the aiResult
	 */
	public boolean getAiResult() {
		return aiResult;
	}

	/**
	 * Sets the ai result.
	 *
	 * @param aiResult the aiResult to set
	 */
	public void setAiResult(boolean aiResult) {
		this.aiResult = aiResult;
	}

	/**
	 * Gets the req desc.
	 *
	 * @return the req desc
	 */
	public String getReqDesc() {
		return reqDesc;
	}

	/**
	 * Sets the req desc.
	 *
	 * @param reqDesc the new req desc
	 */
	public void setReqDesc(String reqDesc) {
		this.reqDesc = reqDesc;
	}

	/**
	 * @return the statusChanged
	 */
	public String getStatusChanged() {
		return statusChanged;
	}

	/**
	 * @param statusChanged the statusChanged to set
	 */
	public void setStatusChanged(String statusChanged) {
		this.statusChanged = statusChanged;
	}

	/**
	 * @return the currentStatus
	 */
	public Long getCurrentStatus() {
		return currentStatus;
	}

	/**
	 * @param currentStatus the currentStatus to set
	 */
	public void setCurrentStatus(Long currentStatus) {
		this.currentStatus = currentStatus;
	}

	/**
	 * @return the currentReqStatus
	 */
	public String getCurrentReqStatus() {
		return currentReqStatus;
	}

	/**
	 * @param currentReqStatus the currentReqStatus to set
	 */
	public void setCurrentReqStatus(String currentReqStatus) {
		this.currentReqStatus = currentReqStatus;
	}

	/**
	 * @return the manageBillingPriceInventoryDataRequest
	 */
	public Object getManageBillingPriceInventoryDataRequest() {
		return manageBillingPriceInventoryDataRequest;
	}

	/**
	 * @param manageBillingPriceInventoryDataRequest the manageBillingPriceInventoryDataRequest to set
	 */
	public void setManageBillingPriceInventoryDataRequest(
			Object manageBillingPriceInventoryDataRequest) {
		this.manageBillingPriceInventoryDataRequest = manageBillingPriceInventoryDataRequest;
	}

	/**
	 * @return the dataIds
	 */
	public List<Long> getDataIds() {
		return dataIds;
	}

	/**
	 * @param dataIds the dataIds to set
	 */
	public void setDataIds(List<Long> dataIds) {
		this.dataIds = dataIds;
	}

	/**
	 * @return the nxRequestGroups
	 */
	public List<NxRequestGroup> getNxRequestGroups() {
		return nxRequestGroups;
	}

	/**
	 * @param nxRequestGroups the nxRequestGroups to set
	 */
	public void setNxRequestGroups(List<NxRequestGroup> nxRequestGroups) {
		this.nxRequestGroups = nxRequestGroups;
	}
	
	public List<String> getGroupName() {
		return groupName;
	}

	public void setGroupName(List<String> groupName) {
		this.groupName = groupName;
	}

	public List<Groups> getGroups() {
		return groups;
	}

	public void setGroups(List<Groups> groups) {
		this.groups = groups;
	}

	/**
	 * @return the apCount
	 */
	public Long getApCount() {
		return apCount;
	}

	/**
	 * @param apCount the apCount to set
	 */
	public void setApCount(Long apCount) {
		this.apCount = apCount;
	}

	/**
	 * @return the apSelectedCount
	 */
	public Long getApSelectedCount() {
		return apSelectedCount;
	}

	/**
	 * @param apSelectedCount the apSelectedCount to set
	 */
	public void setApSelectedCount(Long apSelectedCount) {
		this.apSelectedCount = apSelectedCount;
	}

	/**
	 * @return the nxsDescription
	 */
	public String getNxsDescription() {
		return nxsDescription;
	}

	/**
	 * @param nxsDescription the nxsDescription to set
	 */
	public void setNxsDescription(String nxsDescription) {
		this.nxsDescription = nxsDescription;
	}

	/**
	 * @return the flowType
	 */
	public String getFlowType() {
		return flowType;
	}

	/**
	 * @param flowType the flowType to set
	 */
	public void setFlowType(String flowType) {
		this.flowType = flowType;
	}

	/**
	 * @return the nxRequests
	 */
	public List<NxRequests> getNxRequests() {
		return nxRequests;
	}

	/**
	 * @param nxRequests the nxRequests to set
	 */
	public void setNxRequests(List<NxRequests> nxRequests) {
		this.nxRequests = nxRequests;
	}

	/**
	 * @return the groupStatus
	 */
	public String getGroupStatus() {
		return groupStatus;
	}

	/**
	 * @param groupStatus the groupStatus to set
	 */
	public void setGroupStatus(String groupStatus) {
		this.groupStatus = groupStatus;
	}

	public List<AdminUserList> getAdminUserList() {
		return adminUserList;
	}

	public void setAdminUserList(List<AdminUserList> adminUserList) {
		this.adminUserList = adminUserList;
	}

	
}
