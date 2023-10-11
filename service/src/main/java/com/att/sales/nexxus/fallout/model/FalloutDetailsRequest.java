package com.att.sales.nexxus.fallout.model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author ry217s
 * 
 * File t read request
 *
 */

import org.codehaus.jackson.map.annotate.JsonSerialize;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * The Class FalloutDetailsRequest.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class FalloutDetailsRequest {
    
    /** The nx solution id. */
    private Long nxSolutionId;
    
    /** The nx req id. */
    private Long nxReqId;
    
    /** The action. */
    private String action;
    
    /** The failed beids. */
    private List<String> failedBeids;
    
    /** The product. */
    private String product;
    
    /** The req desc. */
    private String reqDesc;
    
    private Long currentStatus;
    
    private Long nxRequestGroupId;
    
    private List<AdminUserList> adminUserList;
    
    private List<Long> dataIds;
    
    private String dealId;
	
	private String version;
	
	private String revision;
	
	private String actionInd;
	
	private Map<String, Object> map;
	
	public Map<String, Object> getMap() {
		return map;
	}

	public void setMap(Map<String, Object> map) {
		this.map = map;
	}

	public String getDealId() {
		return dealId;
	}

	public void setDealId(String dealId) {
		this.dealId = dealId;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getRevision() {
		return revision;
	}

	public void setRevision(String revision) {
		this.revision = revision;
	}

	public String getActionInd() {
		return actionInd;
	}

	public void setActionInd(String actionInd) {
		this.actionInd = actionInd;
	}

	/** The attuid. */
	private String attuid;
	
	private String actionPerformedBy;

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
	 * Gets the action.
	 *
	 * @return the action
	 */
	public String getAction() {
		return action;
	}
	
	/**
	 * Sets the action.
	 *
	 * @param action the new action
	 */
	public void setAction(String action) {
		this.action = action;
	}
	
	/**
	 * Gets the failed beids.
	 *
	 * @return the failedBeids
	 */
	public List<String> getFailedBeids() {
		return failedBeids;
	}
	
	/**
	 * Sets the failed beids.
	 *
	 * @param failedBeids the failedBeids to set
	 */
	public void setFailedBeids(List<String> failedBeids) {
		this.failedBeids = failedBeids;
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
	 * @param product the product to set
	 */
	public void setProduct(String product) {
		this.product = product;
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

	public List<AdminUserList> getAdminUserList() {
		return adminUserList;
	}

	public void setAdminUserList(List<AdminUserList> adminUserList) {
		this.adminUserList = adminUserList;
	}

	public String getAttuid() {
		return attuid;
	}

	public void setAttuid(String attuid) {
		this.attuid = attuid;
	}

	public String getActionPerformedBy() {
		return actionPerformedBy;
	}

	public void setActionPerformedBy(String actionPerformedBy) {
		this.actionPerformedBy = actionPerformedBy;
	}
	
	
}
