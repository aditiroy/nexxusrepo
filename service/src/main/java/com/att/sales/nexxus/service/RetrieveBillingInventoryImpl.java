package com.att.sales.nexxus.service;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.att.sales.framework.exception.SalesBusinessException;
import com.att.sales.framework.model.Message;
import com.att.sales.framework.model.ServiceResponse;
import com.att.sales.framework.model.Status;
import com.att.sales.framework.service.BaseServiceImpl;
import com.att.sales.framework.util.MessageResourcesUtil;
import com.att.sales.nexxus.common.CommonConstants;
import com.att.sales.nexxus.constant.AuditTrailConstants;
import com.att.sales.nexxus.constant.InrConstants;
import com.att.sales.nexxus.constant.MessageConstants;
import com.att.sales.nexxus.constant.MyPriceConstants;
import com.att.sales.nexxus.constant.StringConstants;
import com.att.sales.nexxus.dao.model.NxInrDmaapAudit;
import com.att.sales.nexxus.dao.model.NxLookupData;
import com.att.sales.nexxus.dao.model.NxRequestDetails;
import com.att.sales.nexxus.dao.model.NxRequestGroup;
import com.att.sales.nexxus.dao.model.solution.NxSolutionDetail;
import com.att.sales.nexxus.dao.repository.NxDwInventoryDao;
import com.att.sales.nexxus.dao.repository.NxDwUbCallInventoryDataDao;
import com.att.sales.nexxus.dao.repository.NxVtnsLDCallInventoryDataDao;
import com.att.sales.nexxus.dao.repository.NxInrActivePodsRepository;
import com.att.sales.nexxus.dao.repository.NxInrDmaapAuditRepository;
import com.att.sales.nexxus.dao.repository.NxLookupDataRepository;
import com.att.sales.nexxus.dao.repository.NxOneNetInventoryDataDao;
import com.att.sales.nexxus.dao.repository.NxRequestDetailsRepository;
import com.att.sales.nexxus.dao.repository.NxRequestGroupRepository;
import com.att.sales.nexxus.dao.repository.NxSolutionDetailsRepository;
import com.att.sales.nexxus.dao.repository.NxTeamRepository;
import com.att.sales.nexxus.edf.model.ManageBillingPriceInvDataResponse;
import com.att.sales.nexxus.edf.model.products;
import com.att.sales.nexxus.rome.model.GetBillingChargesRequest;
import com.att.sales.nexxus.rome.model.GetOptyRequest;
import com.att.sales.nexxus.rome.model.GetOptyResponse;
import com.att.sales.nexxus.rome.service.GetOptyInfoServiceImpl;
import com.att.sales.nexxus.util.AuditUtil;
import com.att.sales.nexxus.util.JacksonUtil;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;


@Service
public class RetrieveBillingInventoryImpl extends BaseServiceImpl {

	@Autowired
	private NxTeamRepository nxTeamRepository;

	@Autowired
	private ObjectMapper mapper;

	@Autowired
	private AuditUtil auditUtil;

	@Autowired
	private GetOptyInfoServiceImpl optyInfoServiceImpl;

	@Autowired
	private NxSolutionDetailsRepository solutionRepo;

	@Autowired
	private NxRequestGroupRepository nxRequestGroupRepository;
		
	@Autowired
	private NxDwInventoryDao nxDwInventoryDao;
	
	@Autowired
	private NxLookupDataRepository nxLookupDataRepository;

	@Autowired
	private NxRequestDetailsRepository nxRequestDetailsRepository;
	
	@Autowired
	private NxInrDmaapAuditRepository nxInrDmaapAuditRepository;

	@Value("#{new Integer('${pod.heartbeat.time.in.min}')}")
	private int podHeartbeatTimeInMin;
	
	@Autowired
	private NxInrActivePodsRepository nxInrActivePodsRepository;
	
	@Autowired
	private NxDwUbCallInventoryDataDao nxDwUbCallInventoryDataDao;
	
	@Autowired
	private NxOneNetInventoryDataDao nxOneNetInventoryDataDao;

	@Autowired
	private NxVtnsLDCallInventoryDataDao nxVtnsLDCallInventoryDataDao;
	
	private static Logger logger = LoggerFactory.getLogger(RetrieveBillingInventoryImpl.class);

	public ServiceResponse retrieveBillingInventory(GetBillingChargesRequest inventoryrequest)
			throws SalesBusinessException {
		Long currentTime = System.currentTimeMillis();
		Long startTime = System.currentTimeMillis() - currentTime;

		ManageBillingPriceInvDataResponse objServiceResponse = new ManageBillingPriceInvDataResponse();
		Status status = new Status();
		status.setCode(CommonConstants.SUCCESS_STATUS);
		objServiceResponse.setStatus(status);
		objServiceResponse.setAttuid(inventoryrequest.getAttuid());
		objServiceResponse.setOptyId(inventoryrequest.getOptyId());
		List<NxRequestDetails> failRequest= new ArrayList<>();
		boolean isSingleAndHasFailure = false;
		logger.info("Inside retrieveBillingInventory()");
		try {
			List<products> reqProductList = inventoryrequest.getProducts();
			Object manageBillingPriceInventoryDataRequest;
			if (CollectionUtils.isNotEmpty(reqProductList)) {
				List<NxRequestDetails> nxRequestDetails = new ArrayList<>();
				manageBillingPriceInventoryDataRequest = inventoryrequest.getManageBillingPriceInventoryDataRequest();
				String reqAcctCriteria = mapper.writeValueAsString(manageBillingPriceInventoryDataRequest);
				String searchCriteriaStr = mapper.writeValueAsString(inventoryrequest);
				logger.info("inventory request : " + searchCriteriaStr);
				String accountName=""; 
				for (products prod : reqProductList) {
					NxRequestDetails details = new NxRequestDetails();
					details.setCreatedDate(new Date());
					details.setStatus(Long.valueOf(10));
					details.setProduct(prod.getProduct());
					details.setNxRequestGroupName(prod.getProductType());
					//details.setNxReqDesc(prod.getProductType() + "_" + prod.getProduct());
					details.setCpniApprover(inventoryrequest.getCpniApprover());
					details.setUser(inventoryrequest.getAttuid());
					details.setAcctCriteria(reqAcctCriteria);
					details.setFlowType(StringConstants.FLOW_TYPE_USRP);
					details.setActiveYn(StringConstants.CONSTANT_Y);
					JsonNode accountData = JacksonUtil.toJsonNode(reqAcctCriteria);
					ObjectNode obj = (ObjectNode) accountData;
					if (!accountData.has("billMonth") && inventoryrequest.getBillMonth() != null) {
						obj.put("billMonth", inventoryrequest.getBillMonth());
					}
					if (!accountData.has("beginBillMonth") && inventoryrequest.getBeginBillMonth() != null) {
						obj.put("beginBillMonth", inventoryrequest.getBeginBillMonth());
					}
					details.setManageBillingPriceJson(accountData.toString());
					long totalRecords = 0;
					if (MyPriceConstants.DW_INVENTORY_PRODUCTS.contains(prod.getProduct())) {
						totalRecords = nxDwInventoryDao.getTotalCountBySearchCriteria(details.getManageBillingPriceJson(),
								details.getProduct());
					}
					//VTNS LD USAGE AND VTNS LD Feature		
					else if(MyPriceConstants.DW_VTNS_LD.contains(prod.getProduct())) {
						totalRecords = nxVtnsLDCallInventoryDataDao.getTotalCountBySearchCriteria(details.getManageBillingPriceJson(), 
							details.getProduct());				
					}
					//BVoIP
					else if (MyPriceConstants.DW_UB_CALL_PRODUCTS.contains(prod.getProduct())) {
						totalRecords = nxDwUbCallInventoryDataDao.getTotalCountBySearchCriteria(details.getManageBillingPriceJson(), 
								details.getProduct());
					} else if (MyPriceConstants.INR_BETA_ONENET_PRODUCTS.contains(prod.getProduct())) {
						totalRecords = nxOneNetInventoryDataDao.getTotalCountBySearchCriteria(details.getManageBillingPriceJson(), 
								details.getProduct());
					}
					if(totalRecords==0) {
						failRequest.add(details);
						details.setActiveYn(StringConstants.CONSTANT_N);
					}
					if (totalRecords > 0) {
						if (MyPriceConstants.DW_INVENTORY_PRODUCTS.contains(prod.getProduct())) {
						 accountName = nxDwInventoryDao.getAccountNameCountBySearchCriteria(
								details.getManageBillingPriceJson(), details.getProduct());
						}
						else if (MyPriceConstants.DW_UB_CALL_PRODUCTS.contains(prod.getProduct())) {
							accountName = nxDwUbCallInventoryDataDao.getAccountNameCountBySearchCriteria(
									details.getManageBillingPriceJson(), details.getProduct());
						} else if (MyPriceConstants.INR_BETA_ONENET_PRODUCTS.contains(prod.getProduct())) {
							accountName = nxOneNetInventoryDataDao.getAccountNameCountBySearchCriteria(
									details.getManageBillingPriceJson(), details.getProduct());
						} else if (MyPriceConstants.DW_VTNS_LD.contains(prod.getProduct())) {
							accountName = nxVtnsLDCallInventoryDataDao.getAccountNameCountBySearchCriteria(
									details.getManageBillingPriceJson(), details.getProduct());
						}
						details.setNxReqDesc(accountName);
					}
					nxRequestDetails.add(details);

				}
				
				boolean saveSuccess = false;
				try {
					saveSuccess = saveInventoryPricingRequestData(inventoryrequest, nxRequestDetails,
							objServiceResponse);
				} catch (Exception ex) {
					saveSuccess = false;
				}
				if(CollectionUtils.isNotEmpty(failRequest) && (failRequest.size()==reqProductList.size())
						&& (failRequest.size()==1)) {
					isSingleAndHasFailure=true;	
				}
				if(CollectionUtils.isNotEmpty(failRequest)) {
					if(StringUtils.isEmpty(inventoryrequest.getNxSolutionId()) && isSingleAndHasFailure ) {
						nxTeamRepository.deleteNxTeams(objServiceResponse.getNxSolutionId());
						nxRequestDetailsRepository.deleteNxRequestDetails(objServiceResponse.getNxSolutionId());
						nxRequestGroupRepository.deleteNxRequestGroup(objServiceResponse.getNxSolutionId());
						solutionRepo.deleteNxSolutions(objServiceResponse.getNxSolutionId());
					} else {
						for(NxRequestDetails nxReq : failRequest) {
							nxRequestDetailsRepository.inactiveReqDetails(nxReq.getNxReqId());
						}
					}
				}
				
				if(saveSuccess) {
						NxSolutionDetail nxSolutionDetail = solutionRepo
								.findByNxSolutionId(objServiceResponse.getNxSolutionId());
						String solutionDesc=null;
						if (nxSolutionDetail != null) {
							if(nxSolutionDetail.getNxsDescription()==null) {
								nxSolutionDetail.setNxsDescription("My request for " + accountName);
								solutionRepo.saveAndFlush(nxSolutionDetail);
								solutionDesc="My request for " + accountName;
							}else {
								solutionDesc=nxSolutionDetail.getNxsDescription();
							}
							objServiceResponse.setNxSolutionDesc(solutionDesc);
						}
						
					
					//need to add request id added only during this requesttime
					nxRequestDetails.removeAll(failRequest);
					if(CollectionUtils.isNotEmpty(nxRequestDetails)) {
						for(NxRequestDetails nxr: nxRequestDetails) {
							NxInrDmaapAudit nxInrDmaapAudit = new NxInrDmaapAudit();
							nxInrDmaapAudit.setNxCorrelationId(String.valueOf(nxr.getNxReqId()));
							nxInrDmaapAudit.setNxProcessStatus("N");
							nxInrDmaapAudit.setNxTransactionType(InrConstants.INR_BETA_JSON_CREATION);
							nxInrDmaapAudit.setNxSolutionId(nxr.getNxSolutionDetail().getNxSolutionId());
							updatePodName(InrConstants.INR_BETA_JSON_CREATION,nxInrDmaapAudit,InrConstants.USRP_POD);
							nxInrDmaapAuditRepository.saveAndFlush(nxInrDmaapAudit);
						}
					}
				}
				
				if (!saveSuccess || isSingleAndHasFailure) {
					status.setCode(CommonConstants.FAILURE_STATUS);
					List<Message> messageList = new ArrayList<>();
					Message msg = MessageResourcesUtil.getMessageMapping().get("D0001");
					messageList.add(msg);
					status.setMessages(messageList);
					objServiceResponse.setStatus(status);
				}
			}
			Long endTime = System.currentTimeMillis() - currentTime;
			Long executionTime = endTime - startTime;
			auditUtil.addActionToNxUiAudit(null, AuditTrailConstants.RETRIEVE_BILLING_INVENTORY, null,
					AuditTrailConstants.SUCCESS, null, null, executionTime, null);
			logger.info("End retrieveBillingInventoryImpl()");
			return objServiceResponse;
		} catch (Exception e) {
			Long endTime = System.currentTimeMillis() - currentTime;
			Long executionTime = endTime - startTime;
			auditUtil.addActionToNxUiAudit(null, AuditTrailConstants.RETRIEVE_BILLING_INVENTORY, null,
					AuditTrailConstants.FAIL, null, null, executionTime, null);
			logger.error("Exception from RetrieveBillingInventoryImpl ", e);
			throw new SalesBusinessException(MessageConstants.PROCESS_ERROR_CODE);
		}

	}

	protected boolean saveInventoryPricingRequestData(GetBillingChargesRequest inventoryrequest,
			List<NxRequestDetails> nxRequestDetails, ManageBillingPriceInvDataResponse objServiceResponse)
			throws SalesBusinessException {
		Long currentTime = System.currentTimeMillis();
		Long startTime = System.currentTimeMillis() - currentTime;
		logger.info("Saving inventory price request acknowledgements");
		if (CollectionUtils.isNotEmpty(nxRequestDetails)) {
			Long nxSolutionId = -1L;
			if (StringUtils.isEmpty(inventoryrequest.getNxSolutionId())) {
				logger.info("solutionId NOT passed in request. Creating a new solution");
				GetOptyRequest optyRequest = new GetOptyRequest();
				optyRequest.setAction("createSolution");
				optyRequest.setAttuid(inventoryrequest.getAttuid());
				optyRequest.setOptyId(inventoryrequest.getOptyId());
				GetOptyResponse optyResponse = (GetOptyResponse) optyInfoServiceImpl.performGetOptyInfo(optyRequest);
				if (optyResponse.getNxSolutionId() != null) {
					nxSolutionId = optyResponse.getNxSolutionId();
					Long endTime = System.currentTimeMillis() - currentTime;
				    Long executionTime=endTime-startTime;
					auditUtil.addActionToNxUiAudit(nxSolutionId,AuditTrailConstants.INR_SOLUTION_CREATE,inventoryrequest.getAttuid(),AuditTrailConstants.SUCCESS,null,null,executionTime,null);
				} else {
					Long endTime = System.currentTimeMillis() - currentTime;
				    Long executionTime=endTime-startTime;
					auditUtil.addActionToNxUiAudit(nxSolutionId,AuditTrailConstants.INR_SOLUTION_CREATE,inventoryrequest.getAttuid(),AuditTrailConstants.FAIL,null,null,executionTime,null);
						logger.info("The new solution could not be created. Error message received is:",
							optyResponse.getStatus().getMessages());
					return false;
				}
				logger.info("Solution is received from getOptyInfo API is {}", nxSolutionId);
			} else {
				logger.info("Getting solutionId from request");
				nxSolutionId = Long.valueOf(inventoryrequest.getNxSolutionId());
			}
			objServiceResponse.setNxSolutionId(nxSolutionId);

			NxSolutionDetail details = solutionRepo.findByNxSolutionId(nxSolutionId);
			if (null != details) {
				details.setModifiedDate(new Date());
				details.setArchivedSolInd("N");
				solutionRepo.save(details);
				List<NxRequestDetails> nxRequests = new ArrayList<NxRequestDetails>();
				Set<Long> nxRequestGrpIds = new HashSet<Long>();
				for (NxRequestDetails nxRequestDetails2 : nxRequestDetails) {
					nxRequestDetails2.setNxSolutionDetail(details);
					NxLookupData nxLookupData = null;
					nxLookupData = nxLookupDataRepository.findByDescriptionAndDatasetName(nxRequestDetails2.getProduct(), MyPriceConstants.NX_REQ_GROUP_NAMES);
					if(nxLookupData != null) {
						saveNxRequestGroup(nxRequestDetails2, nxLookupData, nxSolutionId);
						nxRequestGrpIds.add(nxRequestDetails2.getNxRequestGroupId());
						nxRequests.add(nxRequestDetails2);
					}
				}
				nxRequestDetailsRepository.saveAll(nxRequests);
				nxRequestDetailsRepository.flush();
			}
			logger.info("Save Flushed inventory price request acknowledgements");
			Long endTime = System.currentTimeMillis() - currentTime;
		    Long executionTime=endTime-startTime;
			//for capturing audit trail	
			auditUtil.addActionToNxUiAudit(nxSolutionId,AuditTrailConstants.ADD_INR_PRODUCTS,inventoryrequest.getAttuid(),AuditTrailConstants.SUCCESS,null,null,executionTime,null);
			return true;
		}
		return false;
	}

	protected void saveNxRequestGroup(NxRequestDetails nxRequestDetails2, NxLookupData nxLookupData, Long nxSolutionId) {
		NxRequestGroup nxRequestGroup = null;
		List<NxRequestGroup> nxRequestGroups = nxRequestGroupRepository.findByNxSolutionIdAndGroupIdAndActiveYn(nxSolutionId, Long.parseLong(nxLookupData.getItemId()), StringConstants.CONSTANT_Y);
		if(CollectionUtils.isEmpty(nxRequestGroups)) {
			nxRequestGroup = new NxRequestGroup();
			nxRequestGroup.setGroupId(Long.parseLong(nxLookupData.getItemId()));
			nxRequestGroup.setDescription(nxLookupData.getCriteria());
			nxRequestGroup.setNxSolutionId(nxSolutionId);
			nxRequestGroup.setStatus(MyPriceConstants.NOT_APPLICABLE);
			nxRequestGroup.setActiveYn(StringConstants.CONSTANT_Y);
			nxRequestGroupRepository.save(nxRequestGroup);
			nxRequestDetails2.setNxRequestGroupId(nxRequestGroup.getNxRequestGroupId());
			nxRequestDetails2.setNxRequestGroupName(nxLookupData.getDatasetName());
		}else {
			nxRequestDetails2.setNxRequestGroupId(nxRequestGroups.get(0).getNxRequestGroupId());
			nxRequestDetails2.setNxRequestGroupName(nxLookupData.getDatasetName());
			nxRequestGroup = nxRequestGroups.get(0);
			nxRequestGroup.setStatus(MyPriceConstants.NOT_APPLICABLE);
			nxRequestGroup.setModifiedDate(new Date());
			nxRequestGroupRepository.save(nxRequestGroup);
		}
	}
	
	
	public void updatePodName(String transactionType, NxInrDmaapAudit nxInrDmaapAudit, String podType) {
		Date dateThreshold = Date.from(Instant.now().minus(podHeartbeatTimeInMin, ChronoUnit.MINUTES));
		Map<String, Long> podCount = new HashMap<String, Long>();
		List<Object[]> nxInrActivePods = nxInrActivePodsRepository.getPods(dateThreshold, podType);
		if (nxInrActivePods.size() > 0) {
			//if usrp active pod
			for (Object[] row : nxInrActivePods) {
				podCount.put((String) row[0], ((BigDecimal) row[1]).longValue());
			}

			NxInrDmaapAudit nxInrDmaap = nxInrDmaapAuditRepository.findByNxSolutionIdAndStatusAndTransactionType(
					nxInrDmaapAudit.getNxSolutionId(), InrConstants.DMAAP_STATUS, transactionType);
			//Assign the existing usrp pod
			if (nxInrDmaap != null && podCount.keySet().contains(nxInrDmaap.getNxPodName())) {
				nxInrDmaapAudit.setNxPodName(nxInrDmaap.getNxPodName());
			} else {
				// Assign the usrp pod with the minimum"
				String hostName = podCount.entrySet().stream().min(Comparator.comparingLong(Map.Entry::getValue)).get()
						.getKey();
				nxInrDmaapAudit.setNxPodName(hostName);
			}
		} else {
			// assign the usrppod with the latest heart beat
			List<Object[]> activePod = nxInrActivePodsRepository.getActivePodByPodType(dateThreshold, podType);
			if (activePod.size() > 0) {
				Object[] ob=activePod.get(0);
				nxInrDmaapAudit.setNxPodName(ob[0].toString());
			}

		}
		podCount = null;
	}

}
