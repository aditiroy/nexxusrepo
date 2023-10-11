package com.att.sales.nexxus.fallout.service;

import java.io.IOException;
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
import java.util.TimeZone;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.apache.commons.collections.CollectionUtils;
import org.json.JSONException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.att.sales.framework.exception.SalesBusinessException;
import com.att.sales.framework.model.Message;
import com.att.sales.framework.model.ServiceMetaData;
import com.att.sales.framework.model.ServiceResponse;
import com.att.sales.framework.model.Status;
import com.att.sales.framework.model.constants.HttpErrorCodes;
import com.att.sales.framework.service.BaseServiceImpl;
import com.att.sales.framework.util.MessageResourcesUtil;
import com.att.sales.nexxus.constant.AuditTrailConstants;
import com.att.sales.nexxus.constant.CommonConstants;
import com.att.sales.nexxus.constant.FmoConstants;
import com.att.sales.nexxus.constant.MyPriceConstants;
import com.att.sales.nexxus.constant.StringConstants;
import com.att.sales.nexxus.dao.model.NxDesignAudit;
import com.att.sales.nexxus.dao.model.NxLookupData;
import com.att.sales.nexxus.dao.model.NxOutputFileModel;
import com.att.sales.nexxus.dao.model.NxRequestDetails;
import com.att.sales.nexxus.dao.model.NxRequestGroup;
import com.att.sales.nexxus.dao.model.NxUser;
import com.att.sales.nexxus.dao.model.solution.NxSolutionDetail;
import com.att.sales.nexxus.dao.model.solution.NxTeam;
import com.att.sales.nexxus.dao.repository.HybridRepositoryService;
import com.att.sales.nexxus.dao.repository.NxDesignAuditRepository;
import com.att.sales.nexxus.dao.repository.NxLookupDataRepository;
import com.att.sales.nexxus.dao.repository.NxRequestDetailsRepository;
import com.att.sales.nexxus.dao.repository.NxRequestGroupRepository;
import com.att.sales.nexxus.dao.repository.NxSolutionDetailsRepository;
import com.att.sales.nexxus.dao.repository.NxTeamRepository;
import com.att.sales.nexxus.dao.repository.NxUserRepository;
import com.att.sales.nexxus.fallout.model.FalloutDetailsRequest;
import com.att.sales.nexxus.fallout.model.FalloutDetailsResponse;
import com.att.sales.nexxus.service.FmoProcessingService;
import com.att.sales.nexxus.service.InrBetaGenerateNxsiteId;
import com.att.sales.nexxus.service.InrProcessingService;
import com.att.sales.nexxus.service.InrQualifyService;
import com.att.sales.nexxus.service.NexxusService;
import com.att.sales.nexxus.service.NxMyPriceRepositoryServce;
import com.att.sales.nexxus.service.SubmitToMyPriceService;
import com.att.sales.nexxus.userdetails.service.UserServiceImpl;
import com.att.sales.nexxus.util.AuditUtil;
import com.att.sales.nexxus.util.ThreadMetaDataUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

@Service
@Transactional
public class ActionServiceImpl extends BaseServiceImpl implements ActionService {

	/** The logger. */
	private static Logger logger = LoggerFactory.getLogger(ActionServiceImpl.class);

	/** The hybrid repo. */
	@Autowired
	private HybridRepositoryService hybridRepo;

	/** The inr processing service. */
	@Autowired
	private InrProcessingService inrProcessingService;

	/** The fmo processing service. */
	@Autowired
	private FmoProcessingService fmoProcessingService;

	/** The mapper. */
	@Autowired
	private ObjectMapper mapper;

	@Autowired
	private NxSolutionDetailsRepository nxSolutionDetailsRepository;

	@Autowired
	private NxLookupDataRepository nxLookupDataRepository;

	@Autowired
	private NxRequestDetailsRepository nxRequestDetailsRepository;

	@Autowired
	private NxRequestGroupRepository nxRequestGroupRepository;

	@Autowired
	private SubmitToMyPriceService submitToMyPriceService;

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
	private AuditUtil auditUtil;
	
	@Autowired
	private InrBetaGenerateNxsiteId inrBetaGenerateNxsiteId;
	
	public FalloutDetailsResponse copyAction(FalloutDetailsRequest request) throws SalesBusinessException {
		Long currentTime = System.currentTimeMillis();
		Long startTime = System.currentTimeMillis() - currentTime;

		FalloutDetailsResponse falloutResp = new FalloutDetailsResponse();
		falloutResp.setNxSolutionId(request.getNxSolutionId());
		falloutResp.setNxReqId(request.getNxReqId());
		if ("copyNew".equalsIgnoreCase(request.getAction())) {
		if (null != request.getNxReqId() && null != request.getNxSolutionId() && null != request.getAttuid()) {
			Long newNxSolutionDetailId = null;
			try {
				logger.info("copy new is called");
				NxRequestDetails existingRequestDetails = hybridRepo.getByRequestId(request.getNxReqId());
				if (null == existingRequestDetails) {
					return (FalloutDetailsResponse) setErrorResponse(new FalloutDetailsResponse(), "M00203");
				}
				NxSolutionDetail solution = nxSolutionDetailsRepository
						.findByNxSolutionId(request.getNxSolutionId());
				NxSolutionDetail existingNxSolutionDetail = null;
				if (solution!=null) {
					existingNxSolutionDetail = solution;
				} else {
					return (FalloutDetailsResponse) setErrorResponse(new FalloutDetailsResponse(), "M00021");
				}
				NxSolutionDetail newNxSolutionDetail = null;
				newNxSolutionDetailId = createNxSolutionDetail(existingNxSolutionDetail, request);
				logger.info("Copy to new inr  new nxSolution Id {}", newNxSolutionDetailId);
				List<NxSolutionDetail> nxSolutiondetailList = hybridRepo
						.getNxSolutionDetailList(newNxSolutionDetailId);
				if (null != nxSolutiondetailList && !nxSolutiondetailList.isEmpty()) {
					newNxSolutionDetail = nxSolutiondetailList.get(0);
				}
				List<NxLookupData> copyStatusLookup = nxMyPriceRepositoryServce
						.getItemDescFromLookup(CommonConstants.NEXXUS_COPY_STATUS, StringConstants.CONSTANT_Y);
				NxRequestDetails newNxRequestDetails = new NxRequestDetails();
				BeanUtils.copyProperties(existingRequestDetails, newNxRequestDetails, "nxReqId", "modifedDate",
						"createdDate", "nxOutputFiles", "nxRequestGroupId", "nxSolutionDetail", "submitReqAddrEditInd");
				updateCopyStatus(copyStatusLookup, newNxRequestDetails);
				NxLookupData nxLookupData = nxLookupDataRepository.findByDescriptionAndDatasetName(
						existingRequestDetails.getProduct(), MyPriceConstants.NX_REQ_GROUP_NAMES);
				// new request group is created
				NxRequestGroup newNxRequestGroup = new NxRequestGroup();
				newNxRequestGroup.setGroupId(Long.parseLong(nxLookupData.getItemId()));
				newNxRequestGroup.setDescription(nxLookupData.getCriteria());
				newNxRequestGroup.setNxSolutionId(newNxSolutionDetailId);
				newNxRequestGroup.setStatus(MyPriceConstants.NOT_APPLICABLE);
				newNxRequestGroup.setActiveYn(StringConstants.CONSTANT_Y);
				hybridRepo.saveNxRequestGroup(newNxRequestGroup);
				newNxRequestDetails.setNxRequestGroupId(newNxRequestGroup.getNxRequestGroupId());
				newNxRequestDetails.setNxRequestGroupName(nxLookupData.getDatasetName());
				newNxRequestDetails.setNxSolutionDetail(newNxSolutionDetail);
				newNxRequestDetails.setUser(request.getAttuid());
				newNxRequestDetails.setSourceSolId(request.getNxSolutionId());
				nxRequestDetailsRepository.saveAndFlush(newNxRequestDetails);
				// call inr qualify check
				CompletableFuture.runAsync(() -> {
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
					updateNxOutputFileCdirData(newNxRequestDetails, request.getNxReqId());
					hybridRepo.saveNxRequestDetails(newNxRequestDetails);
					if(StringConstants.FLOW_TYPE_USRP.equalsIgnoreCase(newNxRequestDetails.getFlowType())) {
						inrBetaGenerateNxsiteId.generateNxsiteidInrBeta(newNxRequestDetails.getNxReqId(), true, null);
					}else {
						inrQualifyService.inrQualifyCheck(newNxRequestDetails.getNxReqId(), true, null);
					}
				});
				nexxusService.updateNxSolution(newNxSolutionDetailId);
				falloutResp.setNxSolutionId(newNxSolutionDetailId);
				falloutResp.setNxReqId(newNxRequestDetails.getNxReqId());
				Long endTime = System.currentTimeMillis() - currentTime;
				Long executionTime = endTime-startTime;
				//for capturing audit trail	
				auditUtil.addActionToNxUiAudit(newNxSolutionDetailId,AuditTrailConstants.COPY_SOLUTION,request.getActionPerformedBy(),AuditTrailConstants.SUCCESS,request.getNxSolutionId(),null,executionTime,null);
				
			} catch (Exception e) {
				logger.info("Exception while copy to new inr for existing solution id : {} ",
						request.getNxSolutionId());
				Long endTime = System.currentTimeMillis() - currentTime;
				Long executionTime = endTime-startTime;
				auditUtil.addActionToNxUiAudit(newNxSolutionDetailId,AuditTrailConstants.COPY_SOLUTION,request.getActionPerformedBy(),AuditTrailConstants.FAIL,request.getNxSolutionId(),null,executionTime,null);
				e.printStackTrace();
				throw new SalesBusinessException(e.getMessage());

			}
		} else {
			return (FalloutDetailsResponse) setErrorResponse(new FalloutDetailsResponse(), "M00203");
		}
	}else if (request.getAction().equalsIgnoreCase("copyAccess") && null != request.getNxReqId()) {

		try {
			logger.info("Copy Access {}", request.getNxSolutionId());
			NxRequestDetails nxRequestDetails = hybridRepo.getByRequestId(request.getNxReqId());
			String sourceGroupName = nxRequestDetails.getNxRequestGroupName();
			List<String> reqNames = new ArrayList<String>();
			NxSolutionDetail nxSolutionDetail = nxSolutionDetailsRepository
					.findByNxSolutionId(request.getNxSolutionId());
			// logger.info("solution picked for copy ");
			if (nxSolutionDetail == null) {
				return (FalloutDetailsResponse) setErrorResponse(new FalloutDetailsResponse(), "M00021");
			}

			List<NxLookupData> copyStatusLookup = nxMyPriceRepositoryServce
					.getItemDescFromLookup(CommonConstants.NEXXUS_COPY_STATUS, StringConstants.CONSTANT_Y);
			if (CollectionUtils.isNotEmpty(request.getDataIds())) {
				List<NxRequestGroup> nxRequestGroups = nxRequestGroupRepository
						.findByNxRequestGroupIdAndActiveYn(request.getDataIds(), StringConstants.CONSTANT_Y);
				for (NxRequestGroup nxRequestGroup : nxRequestGroups) {
					List<NxRequestDetails> nxdetials = new ArrayList<NxRequestDetails>();
					List<NxRequestDetails> nxRequests = nxRequestDetailsRepository
							.findByEdfAckIdAndActiveYnAndNxSolutionDetailAndNxRequestGroupId(
									nxRequestDetails.getEdfAckId(), StringConstants.CONSTANT_Y, nxSolutionDetail,
									nxRequestGroup.getNxRequestGroupId());
					if (CollectionUtils.isNotEmpty(nxRequests)) {
						logger.info("Request is already exist in target group {}",
								nxRequestGroup.getNxRequestGroupId());
						reqNames.add(nxRequests.get(0).getNxReqDesc());
					} else {
						List<NxRequestDetails> nrds = nxRequestDetailsRepository.findRequestsByGroupId(
								nxRequestGroup.getNxRequestGroupId(), StringConstants.CONSTANT_Y);
						String targetGroupName = nrds.get(0).getNxRequestGroupName();
						NxRequestDetails newNxRequestDetails = new NxRequestDetails();
						BeanUtils.copyProperties(nxRequestDetails, newNxRequestDetails, "nxReqId", "modifedDate",
								"createdDate", "nxOutputFiles", "submitReqAddrEditInd");
						newNxRequestDetails.setNxSolutionDetail(nxSolutionDetail);
						newNxRequestDetails.setNxRequestGroupId(nxRequestGroup.getNxRequestGroupId());
						newNxRequestDetails
								.setSourceSolId(nxRequestDetails.getNxSolutionDetail().getNxSolutionId());
						updateCopyStatus(copyStatusLookup, newNxRequestDetails);

						if (MyPriceConstants.SERVICE_ACCESS_GROUP.equalsIgnoreCase(sourceGroupName)
								&& MyPriceConstants.ACCESS_GROUP.equalsIgnoreCase(targetGroupName)) {
							NxLookupData lookupdata = nxLookupDataRepository.findByDatasetNameAndDescription(
									MyPriceConstants.SERVICE_ACCESS_GROUP, newNxRequestDetails.getProduct());
							List<NxRequestGroup> serviceAccessData = nxRequestGroupRepository
									.findByNxSolutionIdAndGroupIdAndActiveYn(request.getNxSolutionId(),
											Long.parseLong(lookupdata.getItemId()), StringConstants.CONSTANT_Y);
							if (CollectionUtils.isEmpty(serviceAccessData)) {
								nxRequestGroup.setGroupId(Long.parseLong(lookupdata.getItemId()));
								nxRequestGroup.setDescription(lookupdata.getCriteria());
								nxRequestGroup.setStatus(MyPriceConstants.IN_PROGRESS);
								nxRequestGroup.setModifiedDate(new Date());
								nxRequestGroupRepository.save(nxRequestGroup);
								nxdetials.add(newNxRequestDetails);
							} else {
								// if service_access present, merge the data
								// delete access_group
								nxRequestGroupRepository.delete(nxRequestGroup);
								NxRequestGroup grp = serviceAccessData.get(0);
								newNxRequestDetails.setNxRequestGroupId(grp.getNxRequestGroupId());
								nxdetials.add(newNxRequestDetails);
								for (NxRequestDetails req : nrds) {
									req.setNxRequestGroupId(grp.getNxRequestGroupId());
									req.setModifedDate(new Date());
									nxdetials.add(req);
								}
								grp.setStatus(MyPriceConstants.IN_PROGRESS);
								grp.setModifiedDate(new Date());
								nxRequestGroupRepository.save(grp);
							}

						} else {
							nxRequestDetailsRepository.saveAndFlush(newNxRequestDetails);
							List<NxRequestDetails> nxReqDetails = nxRequestDetailsRepository.findRequestsByGroupId(
									nxRequestGroup.getNxRequestGroupId(), StringConstants.CONSTANT_Y);
							setNxGroupStatus(nxReqDetails, nxRequestGroup);

						}

						if (CollectionUtils.isNotEmpty(nxdetials)) {
							nxRequestDetailsRepository.saveAll(nxdetials);
							nxRequestDetailsRepository.flush();
						}
						// call inr qualify
						CompletableFuture.runAsync(() -> {

							try {
								TimeUnit.SECONDS.sleep(10);
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
							List<NxOutputFileModel> nxOutputFileModels = new ArrayList<NxOutputFileModel>();
							for (NxOutputFileModel nxOutputFile : nxRequestDetails.getNxOutputFiles()) {
								NxOutputFileModel newNxOutputFile = new NxOutputFileModel();
								BeanUtils.copyProperties(nxOutputFile, newNxOutputFile, "id", "modifedDate",
										"createdDate");
								newNxOutputFile.setNxRequestDetails(newNxRequestDetails);
								newNxOutputFile.setCreatedDate(new Timestamp(System.currentTimeMillis()));
								newNxOutputFile.setNxSiteIdInd(StringConstants.CONSTANT_N);
								nxOutputFileModels.add(newNxOutputFile);
							}
							newNxRequestDetails.setNxOutputFiles(nxOutputFileModels);

							updateNxOutputFileCdirData(newNxRequestDetails, request.getNxReqId());
							// nxOutputFileRepository.save(nxOutputFileModels);
							hybridRepo.saveNxRequestDetails(newNxRequestDetails);
							if(StringConstants.FLOW_TYPE_USRP.equalsIgnoreCase(newNxRequestDetails.getFlowType())) {
								inrBetaGenerateNxsiteId.generateNxsiteidInrBeta(newNxRequestDetails.getNxReqId(), true, null);
							}else {
								inrQualifyService.inrQualifyCheck(newNxRequestDetails.getNxReqId(), true, null);
							}
						});
					}
					if (CollectionUtils.isNotEmpty(reqNames)) {
						return (FalloutDetailsResponse) setSuccessResponse(new FalloutDetailsResponse(), "M00034");
					}
				}
			} else {
				// new group
				logger.info("create new group");
				NxRequestDetails newNxRequestDetails = new NxRequestDetails();
				BeanUtils.copyProperties(nxRequestDetails, newNxRequestDetails, "nxReqId", "modifedDate",
						"createdDate", "nxOutputFiles", "submitReqAddrEditInd");

				newNxRequestDetails.setNxSolutionDetail(nxSolutionDetail);
				newNxRequestDetails.setSourceSolId(nxRequestDetails.getNxSolutionDetail().getNxSolutionId());
				updateCopyStatus(copyStatusLookup, newNxRequestDetails);
				// logger.info("copy done new group");
				NxLookupData nxLookupData = nxLookupDataRepository.findByDescriptionAndDatasetName(
						nxRequestDetails.getProduct(), MyPriceConstants.NX_REQ_GROUP_NAMES);

				// List<NxRequestGroup> requestGroups =
				// nxRequestGroupRepository.findByNxSolutionIdAndGroupId(nxSolutionDetail.getNxSolutionId(),
				// Long.parseLong((nxLookupData.getItemId()));
				// if(CollectionUtils.isEmpty(requestGroups)) {
				NxRequestGroup nxRequestGroup = new NxRequestGroup();
				nxRequestGroup.setGroupId(Long.parseLong(nxLookupData.getItemId()));
				nxRequestGroup.setDescription(nxLookupData.getCriteria());
				nxRequestGroup.setNxSolutionId(nxSolutionDetail.getNxSolutionId());
				nxRequestGroup.setStatus(MyPriceConstants.NOT_APPLICABLE);
				nxRequestGroup.setActiveYn(StringConstants.CONSTANT_Y);
				hybridRepo.saveNxRequestGroup(nxRequestGroup);
				newNxRequestDetails.setNxRequestGroupId(nxRequestGroup.getNxRequestGroupId());
				newNxRequestDetails.setNxRequestGroupName(nxLookupData.getDatasetName());
				// }
				nxRequestDetailsRepository.saveAndFlush(newNxRequestDetails);

				// call inr qualify check
				CompletableFuture.runAsync(() -> {
					try {
						TimeUnit.SECONDS.sleep(10);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					List<NxOutputFileModel> nxOutputFileModels = new ArrayList<NxOutputFileModel>();
					for (NxOutputFileModel nxOutputFile : nxRequestDetails.getNxOutputFiles()) {
						NxOutputFileModel newNxOutputFile = new NxOutputFileModel();
						BeanUtils.copyProperties(nxOutputFile, newNxOutputFile, "id", "modifedDate", "createdDate");
						newNxOutputFile.setNxRequestDetails(newNxRequestDetails);
						newNxOutputFile.setCreatedDate(new Timestamp(System.currentTimeMillis()));
						newNxOutputFile.setNxSiteIdInd(StringConstants.CONSTANT_N);
						nxOutputFileModels.add(newNxOutputFile);
					}
					newNxRequestDetails.setNxOutputFiles(nxOutputFileModels);

					updateNxOutputFileCdirData(newNxRequestDetails, request.getNxReqId());
					hybridRepo.saveNxRequestDetails(newNxRequestDetails);
					if(StringConstants.FLOW_TYPE_USRP.equalsIgnoreCase(newNxRequestDetails.getFlowType())) {
						inrBetaGenerateNxsiteId.generateNxsiteidInrBeta(newNxRequestDetails.getNxReqId(), true, null);
					}else {
						inrQualifyService.inrQualifyCheck(newNxRequestDetails.getNxReqId(), true, null);
					}
				});
			}
			nexxusService.updateNxSolution(request.getNxSolutionId());
			Long endTime = System.currentTimeMillis() - currentTime;
			Long executionTime = endTime-startTime;
			//for capturing audit trail	
			auditUtil.addActionToNxUiAudit(request.getNxSolutionId(),AuditTrailConstants.COPY_SOLUTION,request.getActionPerformedBy(),AuditTrailConstants.SUCCESS,nxRequestDetails.getNxSolutionDetail().getNxSolutionId(),null,executionTime,null);
			
		} catch (Exception e) {
			logger.info("Exception while copy {} ", request.getNxSolutionId());
			e.printStackTrace();
		}
	
	}
		return (FalloutDetailsResponse) setSuccessResponse(falloutResp);
	}
	
	public ServiceResponse submitToMyprice(FalloutDetailsRequest request) throws SalesBusinessException {
		
		Long currentTime = System.currentTimeMillis();
		Long startTime = System.currentTimeMillis() - currentTime;

		FalloutDetailsResponse falloutResp = new FalloutDetailsResponse();
		falloutResp.setNxSolutionId(request.getNxSolutionId());
		falloutResp.setNxReqId(request.getNxReqId());
		
		Map<String, Object> requestMetaDataMap = new HashMap<>();
		if (ServiceMetaData.getRequestMetaData() != null) {
			ServiceMetaData.getRequestMetaData().forEach((key, value) -> requestMetaDataMap.put(key, value));
		}
		List<NxSolutionDetail> solution = (List<NxSolutionDetail>) nxSolutionDetailsRepository
				.findByNxSolutionIdAndActiveYn(request.getNxSolutionId(), StringConstants.CONSTANT_Y);
		if (CollectionUtils.isNotEmpty(solution)) {
			nexxusService.updateNxSolution(solution.get(0).getNxSolutionId());
			if (MyPriceConstants.SOURCE_FMO.equalsIgnoreCase(request.getReqDesc())) {
				NxRequestDetails nxRequestDetails = nxRequestDetailsRepository
						.findByNxReqIdAndActiveYn(request.getNxReqId(), StringConstants.CONSTANT_Y);
				if (nxRequestDetails != null) {
					NxOutputFileModel model = null;
					if (CollectionUtils.isNotEmpty(nxRequestDetails.getNxOutputFiles())) {
						model = nxRequestDetails.getNxOutputFiles().get(0);
					}
					Map<String, Object> requestMap = new HashMap<>();
					requestMap.put(FmoConstants.CALL_OPTYINFO, FmoConstants.NO);
					requestMap.put("optyId", solution.get(0).getOptyId());
					requestMap.put("attuid", solution.get(0).getCreatedUser());
					requestMap.put("nxSolutionId", solution.get(0).getNxSolutionId());
					requestMap.put("action", "updateSolution");
					requestMap.put("flowType", "FMO");
					NxOutputFileModel fileModel = model;
					CompletableFuture.runAsync(() -> {
						try {
							ThreadMetaDataUtil.initThreadMetaData(requestMetaDataMap);
							submitToMyPriceService.submitFMOToMyPrice(solution.get(0), nxRequestDetails, fileModel, requestMap);
						} catch (JsonProcessingException | JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} finally {
							ThreadMetaDataUtil.destroyThreadMetaData();
						}
					});
				} else {
					return (FalloutDetailsResponse) setErrorResponse(new FalloutDetailsResponse(), "M00033");
				}
			} else {
				Map<String, Object> response = submitToMyPriceService.submitToMyPrice(solution.get(0),
						request);
				boolean status = response.containsKey(MyPriceConstants.RESPONSE_STATUS)
						? (Boolean) response.get(MyPriceConstants.RESPONSE_STATUS)
						: false;
				if (!status) {
					String code = response.containsKey("messageCode") ? (String) response.get("messageCode") : null;
					Long endTime = System.currentTimeMillis() - currentTime;
					Long executionTime = endTime-startTime;
					auditUtil.addActionToNxUiAudit(request.getNxSolutionId(),AuditTrailConstants.SUBMIT_TO_MYPRICE,request.getActionPerformedBy(),AuditTrailConstants.FAIL,null,null,executionTime,null);
					if (code != null) {
						return (FalloutDetailsResponse) setErrorResponse(new FalloutDetailsResponse(), code);
					} else {
						return (FalloutDetailsResponse) setErrorResponse(new FalloutDetailsResponse(), response);
					}
				}else {
					Long endTime = System.currentTimeMillis() - currentTime;
					Long executionTime = endTime-startTime;
					auditUtil.addActionToNxUiAudit(request.getNxSolutionId(),AuditTrailConstants.SUBMIT_TO_MYPRICE,request.getActionPerformedBy(),AuditTrailConstants.SUCCESS,null,null,executionTime,null);
				}
			}
		} else {
			return (FalloutDetailsResponse) setErrorResponse(new FalloutDetailsResponse(), "M00021");
		}
		return (FalloutDetailsResponse) setSuccessResponse(falloutResp);
	}
	
	public ServiceResponse retriggerRequest(FalloutDetailsRequest request) throws SalesBusinessException {
		
		Long currentTime = System.currentTimeMillis();
		Long startTime = System.currentTimeMillis() - currentTime;

		FalloutDetailsResponse falloutResp = new FalloutDetailsResponse();
		falloutResp.setNxSolutionId(request.getNxSolutionId());
		falloutResp.setNxReqId(request.getNxReqId());

		NxRequestDetails nxRequestDetails = hybridRepo.getByRequestId(request.getNxReqId());

		if (null != nxRequestDetails && null != nxRequestDetails.getFlowType()) {
			try {
				if (nxRequestDetails.getFlowType().equalsIgnoreCase("INR")) {
					nxRequestDetails.setStatus(CommonConstants.STATUS_CONSTANTS.REGENERATE_LINE_ITEMS.getValue());
					nxRequestDetailsRepository.save(nxRequestDetails);
					nexxusService.updateNxSolution(nxRequestDetails.getNxSolutionDetail().getNxSolutionId());
					CompletableFuture.runAsync(() -> {
						try {
							inrProcessingService.regenerateOutputJson(nxRequestDetails);
						} catch (IOException | SalesBusinessException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					});
					setSuccessResponse(falloutResp);
				}

				else if (nxRequestDetails.getFlowType().equalsIgnoreCase("FMO")) {
					fmoProcessingService.updateNexxusOutput(nxRequestDetails);
					setSuccessResponse(falloutResp);
				}
				Long endTime = System.currentTimeMillis() - currentTime;
				Long executionTime = endTime-startTime;
				//for capturing audit trail	
				auditUtil.addActionToNxUiAudit(nxRequestDetails.getNxSolutionDetail().getNxSolutionId(),AuditTrailConstants.REGENERATE_LINE_ITEMS,request.getActionPerformedBy(),AuditTrailConstants.SUCCESS,null,null,executionTime,null);
			}catch(Exception e) {
				Long endTime = System.currentTimeMillis() - currentTime;
				Long executionTime = endTime-startTime;
				auditUtil.addActionToNxUiAudit(nxRequestDetails.getNxSolutionDetail().getNxSolutionId(),AuditTrailConstants.REGENERATE_LINE_ITEMS,request.getActionPerformedBy(),AuditTrailConstants.FAIL,null,null,executionTime,null);
			}
		}
		return (FalloutDetailsResponse) setSuccessResponse(falloutResp);
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
                BeanUtils.copyProperties(existingNxSolutionDetail, newNxSolutionDetails, "createdUser", "createdDate", "nxSolutionId",
						"modifiedDate","modifiedUser","activeYn","archivedSolInd","isLocked" ,"nxDesign", "nxRequestDetails", "nxTeams", "users");
				newNxSolutionDetails.setCreatedUser(request.getAttuid());
				newNxSolutionDetails.setCreatedDate(new Date());
				newNxSolutionDetails.setModifiedDate(new Date());
				newNxSolutionDetails.setModifiedUser(request.getAttuid());
				newNxSolutionDetails.setActiveYn("Y");
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
	
	private void updateCopyStatus(List<NxLookupData> copyStatusLookup, NxRequestDetails newNxRequestDetails) {
		for (NxLookupData copyStatus : copyStatusLookup) {
			if (Arrays.asList(copyStatus.getCriteria().split(","))
					.contains(String.valueOf(newNxRequestDetails.getStatus()))) {
				newNxRequestDetails.setStatus(Long.valueOf(copyStatus.getItemId()));
				break;
			}
		}
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
}
