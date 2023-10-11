package com.att.sales.nexxus.fallout.service;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;
import java.util.TimeZone;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import com.att.sales.framework.exception.SalesBusinessException;
import com.att.sales.framework.model.Message;
import com.att.sales.framework.model.ServiceResponse;
import com.att.sales.framework.model.Status;
import com.att.sales.framework.model.constants.HttpErrorCodes;
import com.att.sales.framework.service.BaseServiceImpl;
import com.att.sales.framework.util.MessageResourcesUtil;
import com.att.sales.nexxus.accesspricing.service.AccessPricingServiceImpl;
import com.att.sales.nexxus.common.MessageConstants;
import com.att.sales.nexxus.constant.AuditTrailConstants;
import com.att.sales.nexxus.constant.CommonConstants;
import com.att.sales.nexxus.constant.InrConstants;
import com.att.sales.nexxus.constant.MyPriceConstants;
import com.att.sales.nexxus.constant.StringConstants;
import com.att.sales.nexxus.dao.model.FailedBeidDetails;
import com.att.sales.nexxus.dao.model.NxAccessPricingData;
import com.att.sales.nexxus.dao.model.NxAdminUserModel;
import com.att.sales.nexxus.dao.model.NxDesignAudit;
import com.att.sales.nexxus.dao.model.NxInrDmaapAudit;
import com.att.sales.nexxus.dao.model.NxLookupData;
import com.att.sales.nexxus.dao.model.NxOutputFileModel;
import com.att.sales.nexxus.dao.model.NxRequestDetails;
import com.att.sales.nexxus.dao.model.NxRequestGroup;
import com.att.sales.nexxus.dao.model.NxSolutionSite;
import com.att.sales.nexxus.dao.model.NxUser;
import com.att.sales.nexxus.dao.model.solution.NxSolutionDetail;
import com.att.sales.nexxus.dao.model.solution.NxTeam;
import com.att.sales.nexxus.dao.repository.HybridRepositoryService;
import com.att.sales.nexxus.dao.repository.NxAccessPricingDataRepository;
import com.att.sales.nexxus.dao.repository.NxAdminUserRepository;
import com.att.sales.nexxus.dao.repository.NxDesignAuditRepository;
import com.att.sales.nexxus.dao.repository.NxDwPriceDetailsRepository;
import com.att.sales.nexxus.dao.repository.NxInrDmaapAuditRepository;
import com.att.sales.nexxus.dao.repository.NxLookupDataRepository;
import com.att.sales.nexxus.dao.repository.NxOutputFileRepository;
import com.att.sales.nexxus.dao.repository.NxRequestDetailsRepository;
import com.att.sales.nexxus.dao.repository.NxRequestGroupRepository;
import com.att.sales.nexxus.dao.repository.NxSolutionDetailsRepository;
import com.att.sales.nexxus.dao.repository.NxSolutionSiteRepository;
import com.att.sales.nexxus.dao.repository.NxTeamRepository;
import com.att.sales.nexxus.dao.repository.NxUserRepository;
import com.att.sales.nexxus.edf.model.ManageBillDataInv;
import com.att.sales.nexxus.edf.model.ManageBillingPriceInventoryDataResponse;
import com.att.sales.nexxus.edf.model.ValidateAccountDataRequest;
import com.att.sales.nexxus.edf.model.ValidateAccountDataResponse;
import com.att.sales.nexxus.fallout.model.AdminUserList;
import com.att.sales.nexxus.fallout.model.FalloutDetailsRequest;
import com.att.sales.nexxus.fallout.model.FalloutDetailsResponse;
import com.att.sales.nexxus.fallout.model.Groups;
import com.att.sales.nexxus.fallout.model.NxProductRequest;
import com.att.sales.nexxus.fallout.model.NxRequests;
import com.att.sales.nexxus.inr.InrFallOutData;
import com.att.sales.nexxus.service.InrBetaGenerateNxsiteId;
import com.att.sales.nexxus.service.InrQualifyService;
import com.att.sales.nexxus.service.NexxusService;
import com.att.sales.nexxus.service.NxMyPriceRepositoryServce;
import com.att.sales.nexxus.service.RetrieveBillingInventoryImpl;
import com.att.sales.nexxus.userdetails.service.UserServiceImpl;
import com.att.sales.nexxus.util.AuditUtil;
import com.att.sales.nexxus.util.DME2RestClient;
import com.att.sales.nexxus.util.HttpRestClient;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.collect.Lists;

/**
 * The Class FalloutDetailsImpl.
 */
@Service
@Transactional
public class FalloutDetailsImpl extends BaseServiceImpl implements FalloutDetails {

	/** The logger. */
	private static Logger logger = LoggerFactory.getLogger(FalloutDetailsImpl.class);

	@Autowired
	private NxSolutionSiteRepository nxSolutionSiteRepository;

	@Autowired
	private NxAccessPricingDataRepository nxAccessPricingDataRepository;

	/** The hybrid repo. */
	@Autowired
	private HybridRepositoryService hybridRepo;
	
	@Autowired
	private RetrieveBillingInventoryImpl retrieveBillingInventoryImpl;
	
	@Autowired
	private NxInrDmaapAuditRepository nxInrDmaapAuditRepository;
	
	/** The mapper. */
	@Autowired
	private ObjectMapper mapper;

	/** The dme. */
	@Autowired
	private DME2RestClient dme;

	@Autowired
	private NxSolutionDetailsRepository nxSolutionDetailsRepository;

	@Autowired
	private NxAdminUserRepository nxAdminUserRepository;

	@Autowired
	private NxLookupDataRepository nxLookupDataRepository;

	@Autowired
	private NxRequestDetailsRepository nxRequestDetailsRepository;

	@Autowired
	private NxRequestGroupRepository nxRequestGroupRepository;

	@Autowired
	private InrQualifyService inrQualifyService;

	@Autowired
	private NexxusService nexxusService;

	@Autowired
	private NxMyPriceRepositoryServce nxMyPriceRepositoryServce;

	@Autowired
	private NxTeamRepository nxTeamRepository;

	@Autowired
	private UserServiceImpl userServiceImpl;

	@Autowired
	private NxUserRepository nxUserRepository;

	@Autowired
	private NxDesignAuditRepository nxDesignAuditRepository;
	
	@Autowired
	private HttpRestClient httpRestClient;
	
	@Autowired
	private AuditUtil auditUtil;
	
	@Autowired
	private AccessPricingServiceImpl accessPricingServiceImpl;
	
	@Autowired
	private InrBetaGenerateNxsiteId inrBetaGenerateNxsiteId;

	@Autowired
	private NxOutputFileRepository nxOutputFileRepository;
	
	@Autowired
	private NxDwPriceDetailsRepository nxDwPriceDetailsRepository;
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.att.sales.nexxus.fallout.service.FalloutDetails#nexxusRequestActions(com.
	 * att.sales.nexxus.fallout.model.FalloutDetailsRequest)
	 */
	@Override
	public ServiceResponse nexxusRequestActions(FalloutDetailsRequest request) throws SalesBusinessException {
		 Long currentTime = System.currentTimeMillis();
	     Long startTime = System.currentTimeMillis() - currentTime;
		
		FalloutDetailsResponse falloutResp = new FalloutDetailsResponse();
		falloutResp.setNxSolutionId(request.getNxSolutionId());
		falloutResp.setNxReqId(request.getNxReqId());

		// List<NxOutputFileModel> nxOutputFileModel =
		// hybridRepo.getNxOutputFileRepository(request.getNxReqId());

		if (request.getAction().equalsIgnoreCase("activateDeActivateAdminUser")) {

			List<AdminUserList> adminUserLists = request.getAdminUserList();
			adminUserLists.forEach((adminUser) -> {
				nxAdminUserRepository.updateActiveYn(adminUser.getActiveYn(), adminUser.getRowId());
			});

			falloutResp.setAdminUserList(getAllAdminUsers());

		} else if (request.getAction().equalsIgnoreCase("addAdminUser")) {

			List<AdminUserList> adminUserLists = request.getAdminUserList();

			adminUserLists.forEach((adminUser) -> {

				NxAdminUserModel nxAdminUserModel = new NxAdminUserModel();

				nxAdminUserModel.setAttuid(adminUser.getAdminUserId());
				nxAdminUserModel.setFname(adminUser.getFname());
				nxAdminUserModel.setMname(adminUser.getMname());
				nxAdminUserModel.setLname(adminUser.getLname());
				nxAdminUserModel.setPhone(adminUser.getTelephone());
				nxAdminUserModel.setEmail(adminUser.getEmail());
				nxAdminUserModel.setActiveYn(adminUser.getActiveYn());
				nxAdminUserModel.setRole(adminUser.getUserrole());

				nxAdminUserRepository.save(nxAdminUserModel);
			});

			falloutResp.setAdminUserList(getAllAdminUsers());

		} else if (request.getAction().equalsIgnoreCase("adminUserListRetrieve")) {
			falloutResp.setAdminUserList(getAllAdminUsers());

		} else if (request.getAction().equalsIgnoreCase("retrieveFalloutDetails")) {
			List<NxOutputFileModel> nxOutputFileModel = hybridRepo.getNxOutputFileRepository(request.getNxReqId());
			NxDesignAudit designAudit = nxDesignAuditRepository.findByNxRefIdAndTransaction(request.getNxReqId(),
					CommonConstants.INR_EXCLUDE_LINE_ITEMS);
			String value = nxOutputFileModel.get(0).getFallOutData();
			if (null == value && designAudit == null) {
				throw new SalesBusinessException("M00201");
			}
			try {
				if (value != null) {
					InrFallOutData fallOutData = mapper.readValue(value, InrFallOutData.class);
					falloutResp.setFallOutBeid(fallOutData.getBeid());
					// if(null != fallOutData && null != fallOutData.getBeid() &&
					// !fallOutData.getBeid().isEmpty()) {
					// falloutResp.setAiResult(nexxusAIService.getNxPredictions(fallOutData.getBeid(),
					// request.getProduct() != null ? request.getProduct() : "AVPN"));
					// }
					falloutResp.setAiResult(true);
					if (null != fallOutData && "false".equals(fallOutData.getAiResponse())) {
						falloutResp.setAiResult(false);
					}
				}
			} catch (IOException e) {
				logger.error("Exception occured during retrieveFalloutDetails", e);
			}

		}else if (request.getAction().equalsIgnoreCase("retriggerInrRequest") && null != request.getNxReqId()) {
			NxRequestDetails nxRequestDetails = hybridRepo.getByRequestId(request.getNxReqId());

			if (null != nxRequestDetails && null != nxRequestDetails.getAcctCriteria()) {
				ManageBillDataInv invRequest = null;
				try {
					invRequest = mapper.readValue(nxRequestDetails.getAcctCriteria(), ManageBillDataInv.class);
				} catch (IOException e) {
					logger.error("IOException occured while getting acctCriteria" + e);
					setSuccessResponse(falloutResp, MessageConstants.RETRIGGER_FAILURE_INTERNAL); // pass constant
				}

				logger.error("Account search criteria:", invRequest);

				if (null != invRequest) {
					ManageBillingPriceInventoryDataResponse response = null;
					try {
						response = dme.getBillingPriceInventryUri(invRequest);
					} catch (Exception e) {
						logger.error("Exception occured during dme call" + e);
						setErrorResponse(falloutResp, MessageConstants.RETRIGGER_FAILURE_EXTERNAL);
					}
					NxSolutionDetail currentSolution = nxRequestDetails.getNxSolutionDetail();

					if (null != response) {

						nxRequestDetails.setStatus(getEdfInvResponseStatus(response));
						nxRequestDetails.setDmaapMsg("");
						nxRequestDetails
								.setEdfAckId(response.getManageBillingPriceInventoryDataResponse().getRequestId());
						nxRequestDetails.setFileName("");
						nxRequestDetails.setModifedDate(new Date());
						nxRequestDetails.setCreatedDate(new Date());

						currentSolution.setModifiedDate(new Date());

						hybridRepo.setNxSolutionDetailList(currentSolution);
						hybridRepo.deleteNxOutputFileRepository(nxRequestDetails);
						hybridRepo.saveNxRequestDetails(nxRequestDetails);

						logger.error("Retrigger INR process completed");

						// update group status

						List<NxRequestDetails> nxRequests = nxRequestDetailsRepository
								.findByNxRequestGroupIdAndActiveYn(nxRequestDetails.getNxRequestGroupId(),
										StringConstants.CONSTANT_Y);
						NxRequestGroup nxRequestGroup = nxRequestGroupRepository.findByNxRequestGroupIdAndActiveYn(
								nxRequestDetails.getNxRequestGroupId(), StringConstants.CONSTANT_Y);
						setNxGroupStatus(nxRequests, nxRequestGroup);
						setSuccessResponse(falloutResp);
						Long endTime = System.currentTimeMillis() - currentTime;
						Long executionTime = endTime-startTime;
						//for capturing audit trail	
						auditUtil.addActionToNxUiAudit(currentSolution.getNxSolutionId(),AuditTrailConstants.RETRIGGER_PRICING_INR,request.getActionPerformedBy(),AuditTrailConstants.SUCCESS,null,null,executionTime,null);
						
					} else {
						setErrorResponse(falloutResp, MessageConstants.RETRIGGER_FAILURE_INTERNAL);
						logger.error("Error occured while processing updates");
						Long endTime = System.currentTimeMillis() - currentTime;
						Long executionTime = endTime-startTime;
						auditUtil.addActionToNxUiAudit(currentSolution.getNxSolutionId(),AuditTrailConstants.RETRIGGER_PRICING_INR,request.getActionPerformedBy(),AuditTrailConstants.FAIL,null,null,executionTime,null);
					}
				}

			} else {
				setErrorResponse(falloutResp, MessageConstants.RETRIGGER_FAILURE_INTERNAL);
				logger.error("Error expected near Account search criteria");
			}
		}else if (request.getAction().equalsIgnoreCase("retriggerInrBetaRequest") && null != request.getNxReqId()) {
			NxRequestDetails nxRequestDetails = hybridRepo.getByRequestId(request.getNxReqId());
			NxSolutionDetail currentSolution = nxRequestDetails.getNxSolutionDetail();
			if("USRP".equals(nxRequestDetails.getFlowType())) {
				nxInrDmaapAuditRepository.deleteByNxReqId(String.valueOf(nxRequestDetails.getNxReqId()));
				nxDwPriceDetailsRepository.deleteByNxReqId(nxRequestDetails.getNxReqId());
				nxOutputFileRepository.deleteByNxReqId(nxRequestDetails.getNxReqId());
				
				nxRequestDetails.setCreatedDate(new Date());
				nxRequestDetails.setModifedDate(new Date());
				nxRequestDetails.setStatus(Long.valueOf(10));
				currentSolution.setModifiedDate(new Date());

				hybridRepo.setNxSolutionDetailList(currentSolution);
				hybridRepo.saveNxRequestDetails(nxRequestDetails);
				
				NxInrDmaapAudit nxInrDmaapAudit = new NxInrDmaapAudit();
				nxInrDmaapAudit.setNxCorrelationId(String.valueOf(nxRequestDetails.getNxReqId()));
				nxInrDmaapAudit.setNxProcessStatus("N");
				nxInrDmaapAudit.setNxTransactionType(InrConstants.INR_BETA_JSON_CREATION);
				nxInrDmaapAudit.setNxSolutionId(nxRequestDetails.getNxSolutionDetail().getNxSolutionId());
				retrieveBillingInventoryImpl.updatePodName(InrConstants.INR_BETA_JSON_CREATION,nxInrDmaapAudit,InrConstants.USRP_POD);
				nxInrDmaapAuditRepository.saveAndFlush(nxInrDmaapAudit);

				Long endTime = System.currentTimeMillis() - currentTime;
				Long executionTime = endTime-startTime;
				auditUtil.addActionToNxUiAudit(currentSolution.getNxSolutionId(),AuditTrailConstants.RETRIGGER_PRICING_INR_BETA,request.getActionPerformedBy(),AuditTrailConstants.SUCCESS,null,null,executionTime,null);
				
			}else {
				setErrorResponse(falloutResp, MessageConstants.RETRIGGER_FAILURE_INTERNAL);
				logger.error("Error expected inr beta product");
				Long endTime = System.currentTimeMillis() - currentTime;
				Long executionTime = endTime-startTime;
				auditUtil.addActionToNxUiAudit(currentSolution.getNxSolutionId(),AuditTrailConstants.RETRIGGER_PRICING_INR_BETA,request.getActionPerformedBy(),AuditTrailConstants.FAIL,null,null,executionTime,null);
			}
		} else if (request.getAction().equalsIgnoreCase("retriggerBulkUploadIndividualRequest")
				&& null != request.getNxReqId()) {
			NxRequestDetails nxRequestDetails = hybridRepo.getByRequestId(request.getNxReqId());
			if (null != nxRequestDetails && null != nxRequestDetails.getValidateAccountDataRequestJson()) {
				ValidateAccountDataRequest validateAccountDataRequest = null;
				try {
					validateAccountDataRequest = mapper.readValue(nxRequestDetails.getValidateAccountDataRequestJson(),
							ValidateAccountDataRequest.class);
				} catch (IOException e) {
					logger.error("IOException occured while getting ValidateAccountDataRequest" + e);
					setSuccessResponse(falloutResp, MessageConstants.RETRIGGER_FAILURE_INTERNAL); // pass constant
				}
				logger.info("validateAccountDataRequest :", validateAccountDataRequest);
				if (null != validateAccountDataRequest) {
					ValidateAccountDataResponse edfResponse = null;
					try {
						edfResponse = httpRestClient.getValidateAccontDataUri(validateAccountDataRequest);
						logger.info("edfResponse during retrigger ::::::::::::::::::: {}",edfResponse);
					} catch (Exception e) {
						logger.error("Exception occured during dme call" + e);
						setErrorResponse(falloutResp, MessageConstants.RETRIGGER_FAILURE_EXTERNAL);
					}
					if (!ObjectUtils.isEmpty(edfResponse)) {
						Integer edfResponseId = null;
						edfResponseId = edfResponse.getCorrelationId();
						logger.info("edfResponseId...........{}", edfResponseId);
						nxRequestDetails.setStatus(getBulkUploadEdfResponseStatus(edfResponse));
						nxRequestDetails.setEdfAckId(String.valueOf(edfResponseId));
						nxRequestDetails.setModifedDate(new Date());
						hybridRepo.saveNxRequestDetails(nxRequestDetails);
						logger.info("Retrigger Bulk upload process completed, nxRequestDetails.......{}",
								nxRequestDetails);
						setSuccessResponse(falloutResp);
					} else {
						setErrorResponse(falloutResp, MessageConstants.RETRIGGER_FAILURE_INTERNAL);
						logger.error("Error occured while processing Bulk upload excel");
					}
				}
			} else {
				setErrorResponse(falloutResp, MessageConstants.RETRIGGER_FAILURE_INTERNAL);
				logger.error("Error expected while Bulk upload process");
			}
		} else if (request.getAction().equalsIgnoreCase("updateFalloutList") && null != request.getNxReqId()) {
			if (CollectionUtils.isNotEmpty(request.getFailedBeids())) {
				Map<String, List<String>> falloutMap = new HashMap<>();
				falloutMap.put("failesBeids", request.getFailedBeids());
				try {
					String falloutData = mapper.writeValueAsString(falloutMap);
					FailedBeidDetails failedBeidDetails = new FailedBeidDetails();
					failedBeidDetails.setNxSolutionId(request.getNxSolutionId());
					failedBeidDetails.setNxReqId(request.getNxReqId());
					failedBeidDetails.setFalloutData(falloutData);
					hybridRepo.saveFailedBeidDetails(failedBeidDetails);
				} catch (JsonProcessingException e) {
					logger.error("JsonProcessingException occured during getting fallout data" + e);
				}
			}
			/*
			 * NxRequestDetails nxRequestDetails1 =
			 * hybridRepo.getByRequestId(request.getNxReqId());
			 * nxRequestDetails1.setStatus(80L); nxRequestDetails1.setModifedDate(new
			 * Date()); hybridRepo.saveNxRequestDetails(nxRequestDetails1);
			 */
			List<NxSolutionDetail> nxSolDetailList = hybridRepo.getNxSolutionDetailList(request.getNxSolutionId());
			NxSolutionDetail nxSolutionDetail;
			if (!nxSolDetailList.isEmpty()) {
				nxSolutionDetail = nxSolDetailList.get(0);
				nxSolutionDetail.setModifiedDate(new Date());
				NxRequestDetails nxRequestDetails1 = hybridRepo.getByRequestId(request.getNxReqId());
				nxRequestDetails1.setStatus(80L);
				nxRequestDetails1.setModifedDate(new Date());
				nxSolutionDetail.addNxRequestDetail(nxRequestDetails1);
				hybridRepo.setNxSolutionDetailList(nxSolutionDetail);
			}
			Long endTime = System.currentTimeMillis() - currentTime;
			Long executionTime = endTime-startTime;
			auditUtil.addActionToNxUiAudit(request.getNxSolutionId(),AuditTrailConstants.IGNORE_FALLOUT,request.getActionPerformedBy(),AuditTrailConstants.SUCCESS,null,null,executionTime,null);		
		} else if (request.getAction().equalsIgnoreCase("updateRequestDescription") && null != request.getNxReqId()) {
			NxRequestDetails nxRequestDetails1 = null;
			try{
				nxRequestDetails1 = hybridRepo.getByRequestId(request.getNxReqId());
				nxRequestDetails1.setNxReqDesc(request.getReqDesc());
				nxRequestDetails1.setModifedDate(new Date());
				hybridRepo.saveNxRequestDetails(nxRequestDetails1);
				falloutResp.setReqDesc(request.getReqDesc());
				nexxusService.updateNxSolution(nxRequestDetails1.getNxSolutionDetail().getNxSolutionId());
				Long endTime = System.currentTimeMillis() - currentTime;
				Long executionTime = endTime-startTime;
				auditUtil.addActionToNxUiAudit(nxRequestDetails1.getNxSolutionDetail().getNxSolutionId(),AuditTrailConstants.REQUEST_NAME_EDIT,request.getActionPerformedBy(),AuditTrailConstants.SUCCESS,null,null,executionTime,null);			
			}catch(Exception e) {
				Long endTime = System.currentTimeMillis() - currentTime;
				Long executionTime = endTime-startTime;
				auditUtil.addActionToNxUiAudit(nxRequestDetails1.getNxSolutionDetail().getNxSolutionId(),AuditTrailConstants.REQUEST_NAME_EDIT,request.getActionPerformedBy(),AuditTrailConstants.FAIL,null,null,executionTime,null);
			}
		} else if (request.getAction().equalsIgnoreCase("updateCustomerName") && null != request.getNxSolutionId()) {
			// logger.info("inside updateCustomerName Action {}",request.getReqDesc());
			if (request.getNxSolutionId() != null) {
//				List<NxSolutionDetail> solutionDetails = nxSolutionDetailsRepository.findByNxSolutionId(request.getNxSolutionId());
//				for(NxSolutionDetail solutionDetail:solutionDetails) {
//					solutionDetail.setCustomerName(request.getReqDesc());
//					solutionDetail.setModifiedDate(new Date());
//					nxSolutionDetailsRepository.save(solutionDetail);
//				}
				try {
					nxSolutionDetailsRepository.updateCustomerNameSolutionId(request.getReqDesc(),
							request.getNxSolutionId());
					falloutResp.setReqDesc(request.getReqDesc());
					//for capturing audit trail
					Long endTime = System.currentTimeMillis() - currentTime;
					Long executionTime = endTime-startTime;
					auditUtil.addActionToNxUiAudit(request.getNxSolutionId(),AuditTrailConstants.CUSTOMER_NAME_EDIT,request.getActionPerformedBy(),AuditTrailConstants.SUCCESS,null,null,executionTime,null);
				}catch(Exception e) {
					Long endTime = System.currentTimeMillis() - currentTime;
					Long executionTime = endTime-startTime;
					auditUtil.addActionToNxUiAudit(request.getNxSolutionId(),AuditTrailConstants.CUSTOMER_NAME_EDIT,request.getActionPerformedBy(),AuditTrailConstants.FAIL,null,null,executionTime,null);
				}
				
			}
		} else if (request.getAction().equalsIgnoreCase("getStatus") && null != request.getNxReqId()) {
			List<Object[]> result = hybridRepo.findByGetStatusAction(request);

			for (Object[] row : result) {
				falloutResp.setCurrentReqStatus((String) row[0]);
				falloutResp.setNxReqId(((BigDecimal) row[1]).longValue());
				falloutResp.setNxSolutionId(((BigDecimal) row[2]).longValue());
				falloutResp.setCurrentStatus(((BigDecimal) row[3]).longValue());
				falloutResp.setStatusChanged(((Character) row[4]).toString());
				falloutResp.setGroupStatus((String) row[5]);
			}
		} else if (request.getAction().equalsIgnoreCase("searchCriteria") && null != request.getNxReqId()) {
			NxRequestDetails nxRequestDetails = hybridRepo.getByRequestId(request.getNxReqId());
			falloutResp.setManageBillingPriceInventoryDataRequest(nxRequestDetails.getManageBillingPriceJson());
		} else if (request.getAction().equalsIgnoreCase("updateGroupDescription")
				&& null != request.getNxRequestGroupId()) {
			NxRequestGroup nxRequestGroup=null;
			try {
				nxRequestGroup = hybridRepo.findByNxRequestGroupId(request.getNxRequestGroupId());
				nxRequestGroup.setModifiedDate(new Date());
				nxRequestGroup.setDescription(request.getReqDesc());
				nxRequestGroup.setGroupNameEditInd(StringConstants.CONSTANT_Y);
				hybridRepo.saveNxRequestGroup(nxRequestGroup);
				falloutResp.setReqDesc(request.getReqDesc());
				nexxusService.updateNxSolution(nxRequestGroup.getNxSolutionId());
				Long endTime = System.currentTimeMillis() - currentTime;
				Long executionTime = endTime-startTime;
			    //for capturing audit trail
     			auditUtil.addActionToNxUiAudit(nxRequestGroup.getNxSolutionId(),AuditTrailConstants.REQUEST_GROUP_NAME_EDIT,request.getActionPerformedBy(),AuditTrailConstants.SUCCESS,null,null,executionTime,null);
			}catch(Exception e) {
				Long endTime = System.currentTimeMillis() - currentTime;
				Long executionTime = endTime-startTime;
				auditUtil.addActionToNxUiAudit(nxRequestGroup.getNxSolutionId(),AuditTrailConstants.REQUEST_GROUP_NAME_EDIT,request.getActionPerformedBy(),AuditTrailConstants.FAIL,null,null,executionTime,null);
			}
		} else if (request.getAction().equalsIgnoreCase("getGroupIdForSoln") && null != request.getNxSolutionId()) {
			List<NxSolutionDetail> solution = (List<NxSolutionDetail>) nxSolutionDetailsRepository
					.findByNxSolutionIdAndActiveYn(request.getNxSolutionId(), StringConstants.CONSTANT_Y);
			if (CollectionUtils.isNotEmpty(solution)) {
				NxSolutionDetail nxSolutionDetail = new NxSolutionDetail();
				nxSolutionDetail.setNxSolutionId(request.getNxSolutionId());
				List<NxRequestGroup> nxRequestGroups = hybridRepo.findByNxSolutionId(request.getNxSolutionId());

				if (CollectionUtils.isNotEmpty(nxRequestGroups)) {
					List<Groups> groups = new ArrayList<Groups>();
					nxRequestGroups.forEach((nxRequestGroup) -> {

						Groups group = new Groups();

						group.setActive(nxRequestGroup.getActiveYn());
						group.setGroupDesc(nxRequestGroup.getDescription());
						group.setGroupId(nxRequestGroup.getGroupId().toString());
						group.setNxRequestGroupId(nxRequestGroup.getNxRequestGroupId().toString());
						group.setNXsolutionId(nxRequestGroup.getNxSolutionId().toString());
						group.setStatus(nxRequestGroup.getStatus());
						List<NxLookupData> nxLookupData = nxLookupDataRepository.findByItemIdAndDatasetName(
								nxRequestGroup.getGroupId().toString(), MyPriceConstants.NX_REQ_GROUP_NAMES);
						if (CollectionUtils.isNotEmpty(nxLookupData)) {
							group.setGroupName(nxLookupData.get(0).getDatasetName());
						}
						groups.add(group);

					});
					falloutResp.setGroups(groups);
				}
				// falloutResp.setNxRequestGroups(nxRequestGroup);
			} else {
				return (FalloutDetailsResponse) setErrorResponse(new FalloutDetailsResponse(), "M00021");
			}
		} else if (request.getAction().equalsIgnoreCase("getExsistingGroupList") && null != request.getNxSolutionId()) {
			List<NxRequestGroup> nxRequestGroups = hybridRepo.findByNxSolutionId(request.getNxSolutionId());
			if (CollectionUtils.isNotEmpty(nxRequestGroups)) {
				List<Groups> groups = new ArrayList<Groups>();
				nxRequestGroups.forEach((nxRequestGroup) -> {
					Groups group = new Groups();
					group.setActive(nxRequestGroup.getActiveYn());
					group.setGroupDesc(nxRequestGroup.getDescription());
					group.setGroupId(nxRequestGroup.getGroupId().toString());
					group.setNxRequestGroupId(nxRequestGroup.getNxRequestGroupId().toString());
					group.setNXsolutionId(nxRequestGroup.getNxSolutionId().toString());
					groups.add(group);
				});
				falloutResp.setGroups(groups);
			} else {
				return (FalloutDetailsResponse) setErrorResponse(new FalloutDetailsResponse(), "M00021");
			}
		} else if (request.getAction().equalsIgnoreCase("qualifyGroup") && null != request.getNxSolutionId()) {
			List<NxSolutionDetail> solution = (List<NxSolutionDetail>) nxSolutionDetailsRepository
					.findByNxSolutionIdAndActiveYn(request.getNxSolutionId(), StringConstants.CONSTANT_Y);
			if (CollectionUtils.isNotEmpty(solution)) {
				nexxusService.updateNxSolution(request.getNxSolutionId());
				List<NxRequestGroup> nxRequestGroups = new ArrayList<NxRequestGroup>();
				for (Long id : request.getDataIds()) {
					NxRequestGroup nxRequestGroup = hybridRepo.findByNxRequestGroupId(id);
					nxRequestGroup.setStatus(MyPriceConstants.MANUALLY_QUALIFIED);
					nxRequestGroups.add(nxRequestGroup);
				}
				hybridRepo.saveNxRequestGroups(nxRequestGroups);
				List<NxRequestGroup> groups = new ArrayList<NxRequestGroup>();
				List<Object[]> result = nxRequestGroupRepository.findByNxSolutionIdAndNxRequestGroupIdAndActive(
						request.getNxSolutionId(), request.getDataIds(), StringConstants.CONSTANT_Y);
				if (result != null && !result.isEmpty()) {
					for (Object[] row : result) {
						NxRequestGroup nxRequestGroup = new NxRequestGroup();
						nxRequestGroup.setStatusName((String) row[0]);
						nxRequestGroup.setNxRequestGroupId(((BigDecimal) row[1]).longValue());
						nxRequestGroup.setNxSolutionId(((BigDecimal) row[2]).longValue());
						nxRequestGroup.setGroupId(((BigDecimal) row[3]).longValue());
						nxRequestGroup.setStatus(((String) row[4]).toString());
						nxRequestGroup.setDescription((String) row[5]);
						groups.add(nxRequestGroup);
					}
					falloutResp.setNxRequestGroups(groups);
				}
				Long endTime = System.currentTimeMillis() - currentTime;
				Long executionTime = endTime-startTime;
				auditUtil.addActionToNxUiAudit(request.getNxSolutionId(),AuditTrailConstants.QUALIFIED_SERVICE,request.getActionPerformedBy(),AuditTrailConstants.SUCCESS,null,null,executionTime,null);
			} else {
				Long endTime = System.currentTimeMillis() - currentTime;
				Long executionTime = endTime-startTime;
				auditUtil.addActionToNxUiAudit(request.getNxSolutionId(),AuditTrailConstants.QUALIFIED_SERVICE,request.getActionPerformedBy(),AuditTrailConstants.FAIL,null,null,executionTime,null);
				return (FalloutDetailsResponse) setErrorResponse(new FalloutDetailsResponse(), "M00021");
			}
		} else if (request.getAction().equalsIgnoreCase("getNotQualifiedGrp") && null != request.getNxSolutionId()) {
			List<NxSolutionDetail> solution = (List<NxSolutionDetail>) nxSolutionDetailsRepository
					.findByNxSolutionIdAndActiveYn(request.getNxSolutionId(), StringConstants.CONSTANT_Y);
			if (CollectionUtils.isNotEmpty(solution)) {
				List<NxRequestGroup> nxRequestGroups = hybridRepo.findByNxSolutionIdAndStatus(request.getNxSolutionId(),
						MyPriceConstants.NOT_QUALIFIED);
				falloutResp.setNxRequestGroups(nxRequestGroups);
			} else {
				return (FalloutDetailsResponse) setErrorResponse(new FalloutDetailsResponse(), "M00021");
			}
		} else if (request.getAction().equalsIgnoreCase("removePricingINR") && null != request.getNxReqId()) {
			NxRequestDetails nxRequestDetails = nxRequestDetailsRepository.findByNxReqId(request.getNxReqId());
			if (java.util.Optional.ofNullable(nxRequestDetails).isPresent()) {
				// nxRequestDetails.setActiveYn("N");
				nxRequestDetailsRepository.inactiveReqDetails(nxRequestDetails.getNxReqId());
				List<NxRequestDetails> nxRequestDetailList = nxRequestDetailsRepository
						.findRequestsByGroupId(nxRequestDetails.getNxRequestGroupId(), StringConstants.CONSTANT_Y);
				String groupId = null, groupName = null;
				if (CollectionUtils.isNotEmpty(nxRequestDetailList)) {
					NxLookupData lookupData = null;
					Optional<NxRequestDetails> nxRequest = nxRequestDetailList.stream().filter(prod -> prod
							.getNxRequestGroupName().equalsIgnoreCase(MyPriceConstants.SERVICE_ACCESS_GROUP))
							.findFirst();
					if (nxRequest.isPresent()) {
						lookupData = nxLookupDataRepository.findByDatasetNameAndDescription(
								MyPriceConstants.SERVICE_ACCESS_GROUP, nxRequest.get().getProduct());
					} else {
						lookupData = nxLookupDataRepository.findByDescriptionAndDatasetName(
								nxRequestDetailList.get(0).getProduct(), MyPriceConstants.ACCESS_SERVICE_GROUP_NAMES);
					}
					if (Optional.ofNullable(lookupData).isPresent()) {
						groupId = lookupData.getItemId();
						groupName = lookupData.getCriteria();
					}
					/*
					 * List<String> products =
					 * nxRequestDetailList.stream().map(rd->rd.getProduct()).collect(Collectors.
					 * toList()); if(CollectionUtils.isNotEmpty(products) && products.size() == 1) {
					 * String productDescription = products.get(0); lookupData =
					 * nxLookupDataRepository.findServiceAccessGroupByDescription(productDescription
					 * ); if(Optional.ofNullable(lookupData).isPresent()) { groupId =
					 * lookupData.getItemId(); groupName = lookupData.getCriteria(); } } else {
					 * lookupData = nxLookupDataRepository.findServiceGroupByDescription(products);
					 * if(Optional.ofNullable(lookupData).isPresent()) { groupId =
					 * lookupData.getItemId(); groupName = lookupData.getCriteria(); } }
					 */
				}

				NxRequestGroup nxRequestGroup = nxRequestGroupRepository.getOne(nxRequestDetails.getNxRequestGroupId());
				if (CollectionUtils.isEmpty(nxRequestDetailList)) {
					nxRequestGroup.setActiveYn("N");
					nxRequestGroupRepository.saveAndFlush(nxRequestGroup);
				} else {
					if (Optional.ofNullable(groupId).isPresent()) {
						if (Long.parseLong(groupId) != nxRequestGroup.getGroupId().longValue()) {
							List<NxRequestGroup> nxRequestGroups = nxRequestGroupRepository
									.findByNxSolutionIdAndGroupIdAndActiveYn(
											nxRequestDetails.getNxSolutionDetail().getNxSolutionId(),
											Long.parseLong(groupId), StringConstants.CONSTANT_Y);
							if (CollectionUtils.isEmpty(nxRequestGroups)) {
								// if group not exist then update existing group
								nxRequestGroup.setGroupId(Long.parseLong(groupId));
								if (!StringConstants.CONSTANT_Y
										.equalsIgnoreCase(nxRequestGroup.getGroupNameEditInd())) {
									nxRequestGroup.setDescription(groupName);
								}
								nxRequestGroupRepository.saveAndFlush(nxRequestGroup);
							} else {
								// if group exist then update request details
								for (NxRequestDetails req : nxRequestDetailList) {
									req.setNxRequestGroupId(nxRequestGroups.get(0).getNxRequestGroupId());
								}
								nxRequestDetailsRepository.saveAll(nxRequestDetailList);
								nxRequestGroup.setActiveYn(StringConstants.CONSTANT_N);
								nxRequestGroupRepository.saveAndFlush(nxRequestGroup);
							}
						}
					}
					// update group status
					List<NxRequestDetails> nxRequestDetail = nxRequestDetailList.stream()
							.filter(r -> r.getNxReqId().longValue() != request.getNxReqId().longValue())
							.collect(Collectors.toList());
					setNxGroupStatus(nxRequestDetail, nxRequestGroup);
				}
				// delete if any disqualified ckts present for the deleted request in audit
				// table
				List<Long> nxReqId= new ArrayList(); 
				nxReqId.add(nxRequestDetails.getNxReqId());
				//inrQualifyService.deleteDisQualifiedCkts(nxRequestDetails.getNxReqId());
				inrQualifyService.deleteDisQualifiedCkts(nxReqId);
				
				//added to delete disqualified and qualified ckts for all the request of that grp
				//so that cdir takes the latest qualified circuits value
				if(CollectionUtils.isNotEmpty(nxRequestDetailList)) {
					List<Long> nxReqIdList=nxRequestDetailList.stream().map(r->r.getNxReqId()).collect(Collectors.toList());
					inrQualifyService.deleteDisQualifiedCkts(nxReqIdList);
				}
				
				NxSolutionSite nxSolutionSite = nxSolutionSiteRepository
						.findByNxSolutionIdAndNxRequestGroupIdAndActiveYNAndNxReqId(
								nxRequestDetails.getNxSolutionDetail().getNxSolutionId(),
								nxRequestDetails.getNxRequestGroupId(), StringConstants.CONSTANT_Y,
								nxRequestDetails.getNxReqId());
				if (nxSolutionSite != null)
					nxSolutionSiteRepository.delete(nxSolutionSite);

				falloutResp.setAiResult(true);
				if (CollectionUtils.isNotEmpty(nxRequestDetailList)) {
					//removing this condition so that generate site id happens always on remove
					//if (isServiceAccessGroup(nxRequestDetailList)) {
						CompletableFuture.runAsync(() -> {
							try {
								TimeUnit.SECONDS.sleep(5);
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
							boolean isNxSiteToGenerate=false;
							List<NxOutputFileModel> nxOutputFileModelList=nxOutputFileRepository.findByNxRequestGrpId(nxRequestDetailList.get(0).getNxRequestGroupId());
							for(NxOutputFileModel nxOutputFileModel:nxOutputFileModelList) {
								if("N".equalsIgnoreCase(nxOutputFileModel.getNxSiteIdInd())) {
									isNxSiteToGenerate=true;
									break;
								}
							}
							if(StringConstants.FLOW_TYPE_USRP.equalsIgnoreCase(nxRequestDetailList.get(0).getFlowType())) {
								inrBetaGenerateNxsiteId.generateNxsiteidInrBeta(nxRequestDetailList.get(0).getNxReqId(), isNxSiteToGenerate, null);
							}else {
								inrQualifyService.inrQualifyCheck(nxRequestDetailList.get(0).getNxReqId(), isNxSiteToGenerate, null);
							}
						});
					//}
				}
			} else {
				return (FalloutDetailsResponse) setErrorResponse(new FalloutDetailsResponse(), "M00021");
			}
			nexxusService.updateNxSolution(nxRequestDetails.getNxSolutionDetail().getNxSolutionId());
			Long endTime = System.currentTimeMillis() - currentTime;
			Long executionTime = endTime-startTime;
			//for capturing audit trail	
			auditUtil.addActionToNxUiAudit(nxRequestDetails.getNxSolutionDetail().getNxSolutionId(),AuditTrailConstants.REMOVE_PRICING_INR,request.getActionPerformedBy(),AuditTrailConstants.SUCCESS,null,null,executionTime,null);
		} else if (request.getAction().equalsIgnoreCase("getGroupsForSoln") && null != request.getNxSolutionId()) {
			logger.info("Start : GetGroupsForSoln");
			List<NxSolutionDetail> solution = (List<NxSolutionDetail>) nxSolutionDetailsRepository
					.findByNxSolutionIdAndActiveYn(request.getNxSolutionId(), StringConstants.CONSTANT_Y);
			if (CollectionUtils.isNotEmpty(solution)) {
				List<NxRequestDetails> nxRequestDetails = nxRequestDetailsRepository
						.findByNxSolutionDetailAndActiveYnAndFlowType(solution.get(0), StringConstants.CONSTANT_Y,
								MyPriceConstants.FLOW_TYPES);
				List<NxRequests> nxRequests = new ArrayList<NxRequests>();
				for (NxRequestDetails nx : nxRequestDetails) {
					NxRequests nxRequest = new NxRequests();
					nxRequest.setNxReqId(nx.getNxReqId());
					nxRequest.setNxReqDesc(nx.getNxReqDesc());
					nxRequest.setProduct(nx.getProduct());
					nxRequest.setStatus(nx.getStatus());
					nxRequest.setFlowType(nx.getFlowType());
					nxRequest.setNxGroupRequestId(nx.getNxRequestGroupId());
					if (nx.getStatus() != null) {
						NxLookupData lookupData = nxLookupDataRepository.findTopByDatasetNameAndItemId(
								CommonConstants.SOLUTION_REQUEST_STATUS, String.valueOf(nx.getStatus()));
						nxRequest.setStatusName(lookupData.getDescription());
					}
					nxRequests.add(nxRequest);
				}
				falloutResp.setNxRequests(nxRequests);
				falloutResp.setNxsDescription(solution.get(0).getNxsDescription());
				falloutResp.setFlowType(solution.get(0).getFlowType());
				NxSolutionDetail nxSolutionDetail = new NxSolutionDetail();
				nxSolutionDetail.setNxSolutionId(request.getNxSolutionId());
				List<Groups> groups = new ArrayList<Groups>();
				List<Object[]> result = nxRequestGroupRepository.findByNxSolutionIdAndActive(request.getNxSolutionId(),
						StringConstants.CONSTANT_Y);
				boolean groupfound = false, iglooFound = false;
				if (result != null && !result.isEmpty()) {
					groupfound = true;
					for (Object[] row : result) {
						Groups group = new Groups();
						group.setStatusName((String) row[0]);
						group.setNxRequestGroupId(String.valueOf(((BigDecimal) row[1]).longValue()));
						group.setNXsolutionId(String.valueOf(((BigDecimal) row[2]).longValue()));
						group.setGroupId(String.valueOf(((BigDecimal) row[3]).longValue()));
						group.setStatus(((String) row[4]).toString());
						group.setGroupDesc((String) row[5]);
						group.setNxRequests(nxRequests.stream().filter(
								n -> n.getNxGroupRequestId().longValue() == Long.parseLong(group.getNxRequestGroupId()))
								.collect(Collectors.toList()));
						groups.add(group);
					}
					falloutResp.setNxRequests(new ArrayList<NxRequests>());
				}
				falloutResp.setGroups(groups);
				List<Object[]> iglooData = nxAccessPricingDataRepository.findByNxSolId(request.getNxSolutionId());
				if (iglooData != null && !iglooData.isEmpty()) {
					iglooFound = true;
					for (Object[] row : iglooData) {
						falloutResp.setApSelectedCount(((BigDecimal) row[0]).longValue());
						falloutResp.setApCount(((BigDecimal) row[1]).longValue());
					}
				}
				if (!iglooFound && !groupfound) {
					return (FalloutDetailsResponse) setErrorResponse(new FalloutDetailsResponse(), "M00031");
				}
			} else {
				return (FalloutDetailsResponse) setErrorResponse(new FalloutDetailsResponse(), "M00021");
			}
			logger.info("End : GetGroupsForSoln");
		} else if (request.getAction().equalsIgnoreCase("getInrRequest") && null != request.getNxSolutionId()) {
			logger.info("Start : getInrRequest");
			List<NxRequestDetails> nxRequestDetails = nxRequestDetailsRepository
					.findByNxSolutionId(request.getNxSolutionId());
			if (CollectionUtils.isNotEmpty(nxRequestDetails)) {
				List<NxLookupData> nxLookupDatas = nxMyPriceRepositoryServce.getItemDescFromLookup("INR_EDIT_PRODUCTS",
						StringConstants.CONSTANT_Y);
				List<NxProductRequest> nxProducts = new ArrayList<>();
				
				Set<String> products = nxRequestDetails.stream().map(n-> n.getProduct()).collect(Collectors.toSet());
				for (String product : products) {
					NxLookupData lookupData = nxLookupDatas.stream()
							.filter(n -> n.getItemId().equalsIgnoreCase(product)).findFirst()
							.orElse(null);
					if(lookupData != null) {
						if (!nxProducts.stream()
								.filter(n -> n.getProductName().equalsIgnoreCase(lookupData.getDescription())).findAny()
								.isPresent()) {
							List<NxRequests> nxRequests = new ArrayList<NxRequests>();
							NxProductRequest nxProduct = new NxProductRequest();
							nxProduct.setProductName(lookupData.getDescription());
							if(StringConstants.CONSTANT_N.equalsIgnoreCase(lookupData.getCriteria())) {
								nxProduct.setInrEditEnabled(lookupData.getCriteria());
							}else {
								if(nxRequestDetails.stream().filter(n-> product.equalsIgnoreCase(n.getProduct()) && !MyPriceConstants.INR_EDIT_REQUEST_STATUS
								.contains(n.getStatus().longValue())).findFirst().isPresent()){
									nxProduct.setInrEditEnabled(StringConstants.CONSTANT_N);
								}else {
									nxProduct.setInrEditEnabled(StringConstants.CONSTANT_Y);
								}
							}
							nxProduct.setNxRequests(nxRequests);
							nxProducts.add(nxProduct);
						}
					}
				}
				/*for (NxRequestDetails nxRequestDetail : nxRequestDetails) {
					if (lookupData != null) {
						NxRequests nxRequest = new NxRequests();
						nxRequest.setNxReqId(nxRequestDetail.getNxReqId());
						nxRequest.setNxReqDesc(nxRequestDetail.getNxReqDesc());
						if (MyPriceConstants.INR_EDIT_REQUEST_STATUS
								.contains(nxRequestDetail.getStatus().longValue())) {
							nxRequest.setSubmitted(StringConstants.CONSTANT_N);
						} else {
							nxRequest.setSubmitted(StringConstants.CONSTANT_Y);
						}

						if (nxProducts.stream()
								.filter(n -> n.getProductName().equalsIgnoreCase(lookupData.getDescription())).findAny()
								.isPresent()) {
							nxProducts.stream()
									.filter(n -> n.getProductName().equalsIgnoreCase(lookupData.getDescription()))
									.findAny().get().getNxRequests().add(nxRequest);
						} else {
							List<NxRequests> nxRequests = new ArrayList<NxRequests>();
							NxProductRequest nxProduct = new NxProductRequest();
							nxProduct.setProductName(lookupData.getDescription());
							nxProduct.setInrEditEnabled(lookupData.getCriteria());
							nxRequests.add(nxRequest);
							nxProduct.setNxRequests(nxRequests);
							nxProducts.add(nxProduct);
						}
					}
				}*/
				if (CollectionUtils.isNotEmpty(nxProducts)) {
					falloutResp.setProducts(nxProducts);
				} else {
					return (FalloutDetailsResponse) setErrorResponse(new FalloutDetailsResponse(), "M00035");
				}

			} else {
				return (FalloutDetailsResponse) setErrorResponse(new FalloutDetailsResponse(), "M00035");
			}
			logger.info("End : getInrRequest");
		}
		
		//Ruchita copy solution
		else if ("copySolution".equalsIgnoreCase(request.getAction())) {
			if (null != request.getNxSolutionId()) {
				NxSolutionDetail existingNxSolutionDetail  = nxSolutionDetailsRepository.findByNxSolutionId(request.getNxSolutionId());	
				if (existingNxSolutionDetail == null) {
					return (FalloutDetailsResponse) setErrorResponse(new FalloutDetailsResponse(), "M00021");
				}
				Long newNxSolutionDetailId = createNxSolutionDetail(existingNxSolutionDetail, request);
				NxSolutionDetail newNxSolutionDetail = null;
				List<NxSolutionDetail> nxSolutiondetailList = hybridRepo.getNxSolutionDetailList(newNxSolutionDetailId);
				if (null != nxSolutiondetailList && !nxSolutiondetailList.isEmpty()) {
					newNxSolutionDetail = nxSolutiondetailList.get(0);
				}
				logger.info("Copy to new inr  new nxSolution Id {}", newNxSolutionDetailId);
				falloutResp.setNxSolutionId(newNxSolutionDetailId);
				if(CollectionUtils.isNotEmpty(request.getDataIds())) {
					for(Long l : request.getDataIds()) {
						List<NxRequestDetails> nxRequestDetails = nxRequestDetailsRepository.findByNxRequestGroupIdAndActiveYn(l, StringConstants.CONSTANT_Y);
						for(NxRequestDetails existingRequestDetails: nxRequestDetails) {
							try {
								logger.info("copy product is called");
								List<NxLookupData> copyStatusLookup = nxMyPriceRepositoryServce
										.getItemDescFromLookup(CommonConstants.NEXXUS_COPY_STATUS, StringConstants.CONSTANT_Y);
								NxRequestDetails newNxRequestDetails = new NxRequestDetails();
								BeanUtils.copyProperties(existingRequestDetails, newNxRequestDetails, "nxReqId", "modifedDate",
										"createdDate", "nxOutputFiles", "nxRequestGroupId", "nxSolutionDetail", "submitReqAddrEditInd");
								updateCopyStatus(copyStatusLookup, newNxRequestDetails);
								NxLookupData nxLookupData = nxLookupDataRepository.findByDescriptionAndDatasetName(
										existingRequestDetails.getProduct(), MyPriceConstants.NX_REQ_GROUP_NAMES);
								// new request group is created
								NxRequestGroup reqgrp = nxRequestGroupRepository.findByNxRequestGroupIdAndActiveYn(existingRequestDetails.getNxRequestGroupId(), StringConstants.CONSTANT_Y);
								List<NxRequestGroup> grps = nxRequestGroupRepository.findByNxSolutionIdAndGroupIdAndActiveYn(newNxSolutionDetailId, reqgrp.getGroupId(), StringConstants.CONSTANT_Y);
								if(CollectionUtils.isEmpty(grps)) {
									NxRequestGroup newNxRequestGroup = new NxRequestGroup();
									newNxRequestGroup.setGroupId(Long.parseLong(nxLookupData.getItemId()));
									newNxRequestGroup.setDescription(nxLookupData.getCriteria());
									newNxRequestGroup.setNxSolutionId(newNxSolutionDetailId);
									newNxRequestGroup.setStatus(MyPriceConstants.NOT_APPLICABLE);
									newNxRequestGroup.setActiveYn(StringConstants.CONSTANT_Y);
									hybridRepo.saveNxRequestGroup(newNxRequestGroup);
									newNxRequestDetails.setNxRequestGroupId(newNxRequestGroup.getNxRequestGroupId());
								}else {
									newNxRequestDetails.setNxRequestGroupId(grps.get(0).getNxRequestGroupId());
								}
								
								newNxRequestDetails.setNxRequestGroupName(nxLookupData.getDatasetName());
								newNxRequestDetails.setNxSolutionDetail(newNxSolutionDetail);
								//newNxRequestDetails.setUser(request.getAttuid());
								newNxRequestDetails.setSourceSolId(request.getNxSolutionId());
								nxRequestDetailsRepository.saveAndFlush(newNxRequestDetails);
								// call inr qualify check
								try {
									TimeUnit.SECONDS.sleep(10);
								} catch (InterruptedException e) {
									e.printStackTrace();
								}
								List<NxOutputFileModel> nxOutputFileModels = new ArrayList<NxOutputFileModel>();
								for (NxOutputFileModel existingNxOutputFile : existingRequestDetails.getNxOutputFiles()) {
									NxOutputFileModel newNxOutputFile = new NxOutputFileModel();
									BeanUtils.copyProperties(existingNxOutputFile, newNxOutputFile, "id", "modifedDate",
											"createdDate", "nxRequestDetails");
									newNxOutputFile.setNxRequestDetails(newNxRequestDetails);
									newNxOutputFile.setCreatedDate(new Timestamp(System.currentTimeMillis()));
									newNxOutputFile.setNxSiteIdInd(StringConstants.CONSTANT_N);
									nxOutputFileModels.add(newNxOutputFile);
								}
								newNxRequestDetails.setNxOutputFiles(nxOutputFileModels);
								updateNxOutputFileCdirData(newNxRequestDetails, existingRequestDetails.getNxReqId());
								hybridRepo.saveNxRequestDetails(newNxRequestDetails);
								if(StringConstants.FLOW_TYPE_USRP.equalsIgnoreCase(newNxRequestDetails.getFlowType())) {
									inrBetaGenerateNxsiteId.generateNxsiteidInrBeta(newNxRequestDetails.getNxReqId(), true, request.getMap());
								}else {
									inrQualifyService.inrQualifyCheck(newNxRequestDetails.getNxReqId(), true, request.getMap());
								}
								nexxusService.updateNxSolution(newNxSolutionDetailId);
								
								falloutResp.setNxReqId(newNxRequestDetails.getNxReqId());
								//for capturing audit trail	
							} catch (Exception e) {
								logger.info("Exception while copy to new inr for existing solution id : {} ",
										request.getNxSolutionId());
								//auditUtil.addActionToNxUiAudit(newNxSolutionDetailId,AuditTrailConstants.COPY_SOLUTION,request.getActionPerformedBy(),AuditTrailConstants.FAIL,request.getNxSolutionId(),null);
								e.printStackTrace();
								throw new SalesBusinessException(e.getMessage());
		
							}
						}
					}
				}
				List<NxAccessPricingData> newAccessPricingDatas = new ArrayList<NxAccessPricingData>();
				List<NxAccessPricingData> nxAccessPricingDatas = nxAccessPricingDataRepository.findByNxSolIdAndMpStatusNdIncludeInd(request.getNxSolutionId());
				if (CollectionUtils.isNotEmpty(nxAccessPricingDatas)) {
					for(NxAccessPricingData priceData: nxAccessPricingDatas) {
						NxAccessPricingData newPriceData = new NxAccessPricingData();
						BeanUtils.copyProperties(priceData, newPriceData, "nxAccessPriceId", "nxSolutionId", "nxSiteId");
						newAccessPricingDatas.add(newPriceData);
					}
					newAccessPricingDatas.forEach(n->n.setNxSolutionId(newNxSolutionDetailId));
					accessPricingServiceImpl.storeNxSiteID(newAccessPricingDatas, newNxSolutionDetailId,request.getMap());
					List<List<NxAccessPricingData>> smallerTokensList = Lists.partition(newAccessPricingDatas, 900);	
					for (List<NxAccessPricingData> tokens : smallerTokensList) {
						nxAccessPricingDataRepository.saveAll(tokens);
					}
				}
				nxAccessPricingDatas = null;
				newAccessPricingDatas = null;
				Long endTime = System.currentTimeMillis() - currentTime;
				Long executionTime = endTime-startTime;
				auditUtil.addActionToNxUiAudit(newNxSolutionDetailId,AuditTrailConstants.COPY_SOLUTION,request.getActionPerformedBy(),AuditTrailConstants.SUCCESS,request.getNxSolutionId(),null,executionTime,null);
			}
		}
		return (FalloutDetailsResponse) setSuccessResponse(falloutResp);
	}

	public void updateNxOutputFileCdirData(NxRequestDetails newNxRequestDetails, Long sourceReqId) {
		List<NxOutputFileModel> nxOutputFiles = newNxRequestDetails.getNxOutputFiles();
		if (nxOutputFiles != null) {
			for (NxOutputFileModel nxOutputFile : nxOutputFiles) {
				String fileName = createFileName(newNxRequestDetails.getNxReqId());
				nxOutputFile.setFileName(fileName);
				try {
					String cdirData = nxOutputFile.getCdirData();
					JsonNode cdirDataNode = mapper.readTree(cdirData);
					JsonNode header = cdirDataNode.path("header");
					for (JsonNode headerElement : header) {
						ObjectNode headerElementObj = (ObjectNode) headerElement;
						headerElementObj.put("nexxusSolutionId",newNxRequestDetails.getNxSolutionDetail().getNxSolutionId());
						headerElementObj.put("nexxusRequestId", newNxRequestDetails.getNxReqId());
					}
					List<String> falloutmachingIds=new ArrayList<>();
					NxDesignAudit designAudit = nxDesignAuditRepository.findByNxRefIdAndTransaction(sourceReqId,CommonConstants.INR_EXCLUDE_LINE_ITEMS);
					if (designAudit != null) {
						String[] cktNames = designAudit.getData().substring(1, designAudit.getData().length() - 1).trim().split("\\s*,\\s*");
						ArrayNode arrayNode = (ArrayNode) cdirDataNode.path("mainSheet");
						next: 
						for (String cktName : cktNames) {
							for (int i = arrayNode.size() - 1; i >= 0; i--) {
								Iterator<Entry<String, JsonNode>> elements = arrayNode.get(i).fields();
								boolean flag=false;
								while (elements.hasNext()) {
									Entry<String, JsonNode> e = elements.next();
									if (e.getValue() != null && cktName.equalsIgnoreCase(e.getValue().asText().replaceAll("\\W", ""))) {
										flag=true;
									}
									if(flag && e.getKey().equalsIgnoreCase("FALLOUTMATCHINGID")) {
										falloutmachingIds.add(e.getValue().asText());
										arrayNode.remove(i);
										continue next;
									}
								}
							}
						}
						if(cdirDataNode.has("falloutSheet")) {
							ArrayNode falloutArrayNode = (ArrayNode) cdirDataNode.path("falloutSheet");
							if (CollectionUtils.isNotEmpty(falloutmachingIds)) {
									next: 
										for (int i = falloutArrayNode.size() - 1; i >= 0; i--) {
											Iterator<Entry<String, JsonNode>> elements = falloutArrayNode.get(i).fields();
												while (elements.hasNext()) {
												Entry<String, JsonNode> e = elements.next();
												if(falloutmachingIds.contains(e.getValue().asText())) {
													falloutArrayNode.remove(i);
													continue next;
												}
											}
										}
							}
						}

					}
					nxOutputFile.setCdirData(cdirDataNode.toString());
				
				} catch (IOException e) {
					logger.info("exception in converting cdid_data to JsonNode", e);
				}
			}
		}
	}

	protected String createFileName(Long id) {
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss'.xlsx'");
		dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
		return id + CommonConstants.OUTPUT_FILE_NAME + dateFormat.format(new Date());
	}

	/**
	 * Sets the error response.
	 *
	 * @param response  the response
	 * @param errorCode the error code
	 * @return the service response
	 */
	public ServiceResponse setErrorResponse(ServiceResponse response, String errorCode) {
		Status status = new Status();
		List<Message> messageList = new ArrayList<>();
		Message msg = MessageResourcesUtil.getMessageMapping().get(errorCode);
		messageList.add(msg);
		status.setCode(HttpErrorCodes.SERVER_ERROR.toString());
		status.setMessages(messageList);
		response.setStatus(status);
		return response;
	}

	/**
	 * Sets the error response.
	 *
	 * @param response  the response
	 * @param errorCode the error code
	 * @return the service response
	 */
	public ServiceResponse setErrorResponse(ServiceResponse response, Map<String, Object> result) {

		Status status = new Status();
		List<Message> messageList = new ArrayList<>();
		String errMsg = (result.containsKey(MyPriceConstants.RESPONSE_DATA)
				&& null != result.get(MyPriceConstants.RESPONSE_DATA))
						? (String) result.get(MyPriceConstants.RESPONSE_DATA)
						: new String("MyPrice api got failed");

		// int code = null!=result.get(MyPriceConstants.RESPONSE_CODE)?(int)
		// result.get(MyPriceConstants.RESPONSE_CODE):0;
		Object code = result.containsKey(MyPriceConstants.RESPONSE_DATA) ? result.get(MyPriceConstants.RESPONSE_CODE)
				: null;
		if (code != null) {
			Message msg1 = MessageResourcesUtil.getMessageMapping().get(String.valueOf(code));
			if (msg1 != null)
				errMsg += " " + msg1.getDetailedDescription();
			Message msg = new Message(String.valueOf(code), errMsg, null);
			messageList.add(msg);
			status.setCode(String.valueOf(code));
		} else {
			Message msg = new Message(null, errMsg, null);
			messageList.add(msg);
		}
		status.setMessages(messageList);
		response.setStatus(status);
		return response;
	}

	/**
	 * Gets the edf inv response status.
	 *
	 * @param response the response
	 * @return the edf inv response status
	 */
	private Long getEdfInvResponseStatus(ManageBillingPriceInventoryDataResponse response) {
		if (response != null && response.getManageBillingPriceInventoryDataResponse() != null) {
			String status = response.getManageBillingPriceInventoryDataResponse().getStatus();
			if ("success".equalsIgnoreCase(status)) {
				return com.att.sales.nexxus.constant.CommonConstants.STATUS_CONSTANTS.IN_PROGRESS.getValue();
			}
		}

		return com.att.sales.nexxus.constant.CommonConstants.STATUS_CONSTANTS.ERROR.getValue();
	}

	/**
	 * Gets the edf bulkupload response status.
	 * 
	 * @param response
	 * @return
	 */
	private Long getBulkUploadEdfResponseStatus(ValidateAccountDataResponse response) {
		if (response != null && response.getCorrelationId() != null) {
			return com.att.sales.nexxus.constant.CommonConstants.STATUS_CONSTANTS.IN_PROGRESS.getValue();
		}
		return com.att.sales.nexxus.constant.CommonConstants.STATUS_CONSTANTS.ERROR.getValue();
	}

	public void setNxGroupStatus(List<NxRequestDetails> nxReqDetails, NxRequestGroup nxRequestGroup) {
		boolean isServiceExist = nxReqDetails.stream()
				.filter(prod -> prod.getNxRequestGroupName().equalsIgnoreCase(MyPriceConstants.SERVICE_GROUP)
						|| prod.getNxRequestGroupName().equalsIgnoreCase(MyPriceConstants.SERVICE_ACCESS_GROUP))
				.findAny().isPresent();
		if (isServiceExist) {
			// service + access check
			if (CollectionUtils.isNotEmpty(nxReqDetails)) {
				// List<String> access =
				// nxLookupDataRepository.findByDatasetName("ACCESS_GROUP").stream().map(NxLookupData::getDescription).collect(Collectors.toList());
				List<NxRequestDetails> accessRequest = nxReqDetails.stream()
						.filter(n -> MyPriceConstants.ACCESS_GROUP.equalsIgnoreCase(n.getNxRequestGroupName()))
						.collect(Collectors.toList());
				if (CollectionUtils.isEmpty(accessRequest)) {
					nxRequestGroup.setStatus(MyPriceConstants.NOT_APPLICABLE);
					nxRequestGroup.setModifiedDate(new Date());
					nxRequestGroupRepository.save(nxRequestGroup);
				} else {
					// Long falloutCoount = nxReqDetails.stream().filter(prod ->
					// prod.getStatus().longValue() ==
					// CommonConstants.STATUS_CONSTANTS.FALLOUT_DMAAP_RECEIVED.getValue()).count();
					boolean isFailedRequest = nxReqDetails.stream().filter(
							prod -> prod.getStatus().longValue() == CommonConstants.STATUS_CONSTANTS.ERROR.getValue()
									|| prod.getStatus().longValue() == CommonConstants.STATUS_CONSTANTS.SYSTEM_FAILURE
											.getValue()
					// || prod.getStatus().longValue() ==
					// CommonConstants.STATUS_CONSTANTS.FALLOUT_DMAAP_RECEIVED.getValue()
					).findAny().isPresent();
					boolean noDmaap = nxReqDetails.stream().filter(prod -> prod.getStatus()
							.longValue() == CommonConstants.STATUS_CONSTANTS.NO_DMAAP_NOTIFICATION_RECEIVED.getValue())
							.findAny().isPresent();
					if (isFailedRequest) {
						nxRequestGroup.setStatus(MyPriceConstants.SYSTEM_FAILURE);
						nxRequestGroup.setModifiedDate(new Date());
						nxRequestGroupRepository.save(nxRequestGroup);
					} else if (noDmaap) {
						nxRequestGroup.setStatus(MyPriceConstants.NO_DMAAP_NOTIFICATION);
						nxRequestGroup.setModifiedDate(new Date());
						nxRequestGroupRepository.save(nxRequestGroup);
					} else {
						nxRequestGroup.setStatus(MyPriceConstants.IN_PROGRESS);
						nxRequestGroup.setModifiedDate(new Date());
						nxRequestGroupRepository.save(nxRequestGroup);
					}
				}
			}
		} else {
			nxRequestGroup.setStatus(MyPriceConstants.NOT_APPLICABLE);
			nxRequestGroup.setModifiedDate(new Date());
			nxRequestGroupRepository.save(nxRequestGroup);
		}
	}

	public boolean findReqWithNxSiteId(List<NxRequestDetails> nxRequestDetail) {
		return nxRequestDetail.stream().anyMatch(
				r -> StringConstants.CONSTANT_N.equalsIgnoreCase(r.getNxOutputFiles().get(0).getNxSiteIdInd()));
	}

	public boolean isServiceAccessGroup(List<NxRequestDetails> nxReqDetails) {
		boolean isServiceExist = nxReqDetails.stream()
				.filter(prod -> prod.getNxRequestGroupName().equalsIgnoreCase(MyPriceConstants.SERVICE_GROUP)
						|| prod.getNxRequestGroupName().equalsIgnoreCase(MyPriceConstants.SERVICE_ACCESS_GROUP))
				.findAny().isPresent();
		if (isServiceExist) {
			if (CollectionUtils.isNotEmpty(nxReqDetails)) {
				List<NxRequestDetails> accessRequest = nxReqDetails.stream()
						.filter(n -> MyPriceConstants.ACCESS_GROUP.equalsIgnoreCase(n.getNxRequestGroupName()))
						.collect(Collectors.toList());
				if (CollectionUtils.isNotEmpty(accessRequest)) {
					return true;
				}
			}
		}
		return false;
	}

	public List<AdminUserList> getAllAdminUsers() {
		List<NxAdminUserModel> adminUserList = (List<NxAdminUserModel>) nxAdminUserRepository.findAll();
		List<AdminUserList> adminUserLists = new ArrayList<AdminUserList>();

		if (CollectionUtils.isNotEmpty(adminUserList)) {

			adminUserList.forEach((adminUser) -> {

				AdminUserList adminUserListResponse = new AdminUserList();

				adminUserListResponse.setRowId(adminUser.getId());
				adminUserListResponse.setAdminUserId(adminUser.getAttuid());
				adminUserListResponse.setFname(adminUser.getFname());
				adminUserListResponse.setMname(adminUser.getMname());
				adminUserListResponse.setLname(adminUser.getLname());
				adminUserListResponse.setTelephone(adminUser.getPhone());
				adminUserListResponse.setEmail(adminUser.getEmail());
				adminUserListResponse.setUserrole(adminUser.getRole());
				adminUserListResponse.setActiveYn(adminUser.getActiveYn());

				adminUserLists.add(adminUserListResponse);

			});
		}
		return adminUserLists;
	}

	private void updateCopyStatus(List<NxLookupData> copyStatusLookup, NxRequestDetails newNxRequestDetails) {
		for (NxLookupData copyStatus : copyStatusLookup) {
			if (Arrays.asList(copyStatus.getCriteria().split(","))
					.contains(String.valueOf(newNxRequestDetails.getStatus()))) {
				newNxRequestDetails.setStatus(Long.valueOf(copyStatus.getItemId()));
				break;
			}
		}
	}

	private Long createNxSolutionDetail(NxSolutionDetail existingNxSolutionDetail, FalloutDetailsRequest request)
			throws SalesBusinessException {
		Long nxSolutionId = -1L;
		NxSolutionDetail newNxSolutionDetails = new NxSolutionDetail();
		if (null != existingNxSolutionDetail) {
			/**
			 * if no opty is associated with the solution copy the existing properties of
			 * solution to the new one
			 */
			//if (null == existingNxSolutionDetail.getOptyId() || "" == existingNxSolutionDetail.getOptyId()) {
			newNxSolutionDetails.setCreatedUser(request.getAttuid());
			newNxSolutionDetails.setCreatedDate(new Date());
			newNxSolutionDetails.setModifiedDate(new Date());
			newNxSolutionDetails.setModifiedUser(request.getAttuid());
			newNxSolutionDetails.setActiveYn("Y");
			newNxSolutionDetails.setFlowType(existingNxSolutionDetail.getFlowType());
			newNxSolutionDetails.setNxsDescription(existingNxSolutionDetail.getNxsDescription());
			newNxSolutionDetails.setArchivedSolInd(StringConstants.CONSTANT_N);
			newNxSolutionDetails.setIsLocked(StringConstants.CONSTANT_N);
			newNxSolutionDetails = nxSolutionDetailsRepository.saveAndFlush(newNxSolutionDetails);
			nxSolutionId = newNxSolutionDetails.getNxSolutionId();
			NxTeam existingnxTeam = null;
			List<NxTeam> listOfNxTeam = hybridRepo.getNxTeamList(request.getAttuid(), existingNxSolutionDetail);
			NxTeam newNxTeam = new NxTeam();
			/**
			 * if team details assosciated with the solution and attuid exist copy that else
			 * create a new one
			 */
			if (null != listOfNxTeam && !listOfNxTeam.isEmpty()) {
				existingnxTeam = listOfNxTeam.get(0);
				if (null != existingnxTeam) {
					BeanUtils.copyProperties(existingnxTeam, newNxTeam, "nxTeamId", "nxSolutionDetail");
					newNxTeam.setNxSolutionDetail(newNxSolutionDetails);
					nxTeamRepository.saveAndFlush(newNxTeam);
				}
			} else {
				// NxUser table to get user details
				String userProfileName = userServiceImpl.getUserProfileName(request.getAttuid());
				if (!UserServiceImpl.NONE.equals(userProfileName)) {
					NxUser nxUser = nxUserRepository.findByUserAttId(request.getAttuid());
					newNxTeam.setEmail(nxUser.getEmail());
					newNxTeam.setfName(nxUser.getFirstName());
					newNxTeam.setlName(nxUser.getLastName());
					newNxTeam.setNxSolutionDetail(newNxSolutionDetails);
					newNxTeam.setAttuid(request.getAttuid());
					newNxTeam.setIsPryMVG("Y");
					nxTeamRepository.saveAndFlush(newNxTeam);
				} else {
					newNxTeam.setNxSolutionDetail(newNxSolutionDetails);
					newNxTeam.setAttuid(request.getAttuid());
					newNxTeam.setIsPryMVG("Y");
					nxTeamRepository.saveAndFlush(newNxTeam);
				}
			}
		}
		
	logger.info("new nxsolution id created is {}", nxSolutionId);
	
	return nxSolutionId;

}


}