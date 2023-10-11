
package com.att.sales.nexxus.dao.model;

import java.util.Date;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class NexxusSolutionDetailUIModelTest {

	private Long nxSolutionId;
	private String nxsDescription;
	private String optyId;
	private String dunsNumber;
	private String guDunsNumber;
	private String l3Value;
	private String l4Value;
	private String customerName;
	private String createdUser;
	private String createdDate;
	private Date modifiedDate;
	private String flowType;
	private String archivedSolInd;
	private String failedTokensCount;
	private String apCount;
	private String apSelectedCount;
	private Long nxReqId = new Long(21);
	private String productCd;
	private String cpniApprover;
	private String status;
	private String nxReqDesc;
	private Long nxReqGroupId;
	private String nxReqGroupName;
	private String nxReqGroupDesc;
	private Long nxLookUpGroupId;
	private String groupName;
	private String nxRequestGroupStatus;
	private Date edfReqModifiedDate;
	private Date edfReqCreateDate;
	private String dealStatus;
	private String dealID;
	private String dealRevision;
	private String dealVersion;
	private String reqStatus;
	private String myPriceTxnId;
	private String nxRequestGroupStatusId;
	private String dmaapBulkStatus;
	private String dmappBulkStatusDesc;
	private String bulkRequest;
	private String inrStatusInd;
	private String iglooStatusInd;
	private Long sourceSolId=new Long(1);;
	private String isLocked;
	private String lockedByUserName;
	private String reqFlowType;
	private String missingFieldCount;
	
	@InjectMocks
	NexxusSolutionDetailUIModel test;

	@Test
	public void test() {
		test = new NexxusSolutionDetailUIModel(nxSolutionId, dealStatus, dealID, dealRevision, dealVersion, myPriceTxnId,
				nxsDescription, optyId, dunsNumber, guDunsNumber, l3Value, l4Value, customerName, createdUser,
				createdDate, modifiedDate, flowType,archivedSolInd, failedTokensCount,apCount, apSelectedCount, nxReqId, productCd, cpniApprover, status,
				nxReqDesc, nxReqGroupId, nxReqGroupName, nxLookUpGroupId, groupName,
				nxRequestGroupStatus, nxRequestGroupStatusId, edfReqModifiedDate, edfReqCreateDate, reqStatus,nxReqGroupDesc,dmaapBulkStatus,dmappBulkStatusDesc,bulkRequest,inrStatusInd,iglooStatusInd,sourceSolId,isLocked,lockedByUserName,reqFlowType,missingFieldCount);
	}

}
