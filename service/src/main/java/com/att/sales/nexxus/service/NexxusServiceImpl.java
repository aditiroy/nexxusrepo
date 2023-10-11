package com.att.sales.nexxus.service;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.transaction.Transactional;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import org.apache.cxf.jaxrs.ext.multipart.MultipartBody;
import org.assertj.core.util.Strings;
import org.hibernate.hql.internal.ast.tree.Node;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.att.aft.dme2.internal.google.common.collect.Lists;
import com.att.sales.framework.exception.SalesBusinessException;
import com.att.sales.framework.model.ServiceMetaData;
import com.att.sales.framework.model.ServiceResponse;
import com.att.sales.framework.model.Status;
import com.att.sales.framework.service.BaseServiceImpl;
import com.att.sales.nexxus.common.CommonConstants;
import com.att.sales.nexxus.constant.AuditTrailConstants;
import com.att.sales.nexxus.constant.MessageConstants;
import com.att.sales.nexxus.constant.MyPriceConstants;
import com.att.sales.nexxus.constant.StringConstants;
import com.att.sales.nexxus.dao.model.NexxusSolutionDetailUIModel;
import com.att.sales.nexxus.dao.model.NexxusSolutionDetailUIModel.NexxusEdfRequestDetail;
import com.att.sales.nexxus.dao.model.NexxusSolutionDetailUIModel.NexxusSolnsGroups;
import com.att.sales.nexxus.dao.model.NexxusSolutionDetailUiModelResponse;
import com.att.sales.nexxus.dao.model.NxAdminUserModel;
import com.att.sales.nexxus.dao.model.NxLookupData;
import com.att.sales.nexxus.dao.model.NxMpDeal;
import com.att.sales.nexxus.dao.model.NxReqRefNumMapping;
import com.att.sales.nexxus.dao.model.NxRequestDetails;
import com.att.sales.nexxus.dao.model.NxRequestGroup;
import com.att.sales.nexxus.dao.model.solution.NxSolutionDetail;
import com.att.sales.nexxus.dao.model.solution.NxTeam;
import com.att.sales.nexxus.dao.repository.NxAdminUserRepository;
import com.att.sales.nexxus.dao.repository.NxLookupDataRepository;
import com.att.sales.nexxus.dao.repository.NxMpDealRepository;
import com.att.sales.nexxus.dao.repository.NxReqRefNumMappingRepository;
import com.att.sales.nexxus.dao.repository.NxRequestDetailsRepository;
import com.att.sales.nexxus.dao.repository.NxRequestGroupRepository;
import com.att.sales.nexxus.dao.repository.NxSolutionDetailsRepository;
import com.att.sales.nexxus.dao.repository.NxTeamRepository;
import com.att.sales.nexxus.edf.model.InputDetailModel;
import com.att.sales.nexxus.edf.model.ManageBillDataInv;
import com.att.sales.nexxus.edf.model.ManageBillingPriceInvDataRequest;
import com.att.sales.nexxus.edf.model.ManageBillingPriceInvDataResponse;
import com.att.sales.nexxus.edf.model.ManageBillingPriceInventoryDataRequest;
import com.att.sales.nexxus.edf.model.ManageBillingPriceInventoryDataResponse;
import com.att.sales.nexxus.edf.model.PriceInventoryDataRequest;
import com.att.sales.nexxus.edf.model.products;
import com.att.sales.nexxus.helper.FileReaderHelper;
import com.att.sales.nexxus.model.ProductDataLoadRequest;
import com.att.sales.nexxus.model.Report_Key;
import com.att.sales.nexxus.myprice.transaction.model.GetTransactionResponse;
import com.att.sales.nexxus.myprice.transaction.service.GetTransactionServiceImpl;
import com.att.sales.nexxus.reteriveicb.model.NexxusTestRequest;
import com.att.sales.nexxus.reteriveicb.model.NexxusTestResponse;
import com.att.sales.nexxus.rome.model.ABSDWGetBillingChargesRequest;
import com.att.sales.nexxus.rome.model.GetBillingChargesRequest;
import com.att.sales.nexxus.rome.model.GetBillingChargesResponse;
import com.att.sales.nexxus.rome.model.GetOptyRequest;
import com.att.sales.nexxus.rome.model.GetOptyResponse;
import com.att.sales.nexxus.rome.service.GetBillingChargesServiceImpl;
import com.att.sales.nexxus.rome.service.GetOptyInfoServiceImpl;
import com.att.sales.nexxus.util.AuditUtil;
import com.att.sales.nexxus.util.DME2RestClient;
import com.att.sales.nexxus.util.JacksonUtil;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import edu.emory.mathcs.backport.java.util.Arrays;;

/**
 * The Class NexxusServiceImpl.
 *
 * @author sw088d
 */
@Service
@Transactional
public class NexxusServiceImpl extends BaseServiceImpl implements NexxusService {
	
	@Autowired
	private NxSolutionDetailsRepository nxSolutionDetailsRepository;

	/** The solution repo. */
	@Autowired
	private NxSolutionDetailsRepository solutionRepo;

	/** The repo. */
	@Autowired
	private NxRequestDetailsRepository repo;

	/** The file reader helper. */
	@Autowired
	private FileReaderHelper fileReaderHelper;

	/** The opty info service impl. */
	@Autowired
	private GetOptyInfoServiceImpl optyInfoServiceImpl;
	
	@Autowired
	private GetBillingChargesServiceImpl getBillingChargesServiceImpl;

	/** The em. */
	@PersistenceContext
	private EntityManager em;
	
	/** The logger. */
	private static Logger logger = LoggerFactory.getLogger(NexxusServiceImpl.class);

	/** The nexxus object mapper. */
	private ObjectMapper nexxusObjectMapper = new ObjectMapper()
			.configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES, true);

	@Autowired
	private NxLookupDataRepository nxLookupDataRepository;
	
	@Autowired
	private NxRequestGroupRepository nxRequestGroupRepository;

	@Autowired
	private NxMpDealRepository nxMpDealRepository;
	
	@Autowired
	private GetTransactionServiceImpl getTransactionServiceImpl;
	
	@Autowired
	private NxTeamRepository nxTeamRepository;
		
	@Autowired
	private NxRequestDetailsRepository nxRequestDetailsRepository;
	
	@Autowired
	private NxMyPriceRepositoryServce nxMyPriceRepositoryServce;
	
	@Autowired
	private NxAdminUserRepository nxAdminUserRepository;
	
	@Autowired
	private MailServiceImpl mailServiceImpl;
	
	@Autowired
	private DME2RestClient dme;

	@Autowired
	private AuditUtil auditUtil;
	
	@Autowired
	private NxReqRefNumMappingRepository nxReqRefNumMappingRepository;
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.att.sales.nexxus.service.NexxusService#transformTestData(com.att.sales.
	 * nexxus.reteriveicb.model.NexxusTestRequest)
	 */
	@Override
	public ServiceResponse transformTestData(NexxusTestRequest request) {
		logger.info("Entered transformTestData() method");
		NexxusTestResponse resp = new NexxusTestResponse();
		resp.setSolutionId(request.getSolutionId());
		resp.setSolutionName(request.getSolutionName());
		logger.info("Exiting transformTestData() method");
		return resp;
	}

	/**
	 * Put upload ASE nexxus file.
	 *
	 * @param productDataLoadRequest the product data load request
	 * @throws IOException            Signals that an I/O exception has occurred.
	 * @throws SalesBusinessException the sales business exception
	 */
	public void putUploadASENexxusFile(ProductDataLoadRequest productDataLoadRequest)
			throws IOException, SalesBusinessException {
	//	MultipartBody multipart = productDataLoadRequest.getMultipartBody();
		String fileName = null;
		String transactionId = null;
		try {
			if (ServiceMetaData.getRequestMetaData().get(CommonConstants.FILENAME) != null) {
				fileName = ServiceMetaData.getRequestMetaData().get(CommonConstants.FILENAME).toString();
			}

			if (ServiceMetaData.getRequestMetaData().get("TransactionId") != null) {
				transactionId = ServiceMetaData.getRequestMetaData().get("TransactionId").toString();
			}
			setDesignDataLoadData(fileName);
			fileReaderHelper.copyToPVAndUnzip(fileName, transactionId);

		} catch (Exception e) {
			logger.error("Exception from NexxusServiceImpl.putProductDataLoad ", e);
			throw new SalesBusinessException(MessageConstants.PROCESS_ERROR_CODE);
		}
	}

	/**
	 * Sets the design data load data.
	 *
	 * @param fileName the new design data load data
	 * @throws SalesBusinessException the sales business exception
	 */
	public void setDesignDataLoadData(String fileName) throws SalesBusinessException {
		if (fileName == null || !fileName.contains("_")) {
			logger.error("Exception while parsing dataload fileName: ");
			throw new SalesBusinessException(MessageConstants.FILE_FORMAT_INVALID_OR_CORRUPTED);
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.att.sales.nexxus.service.NexxusService#fetchNexxusSolutionsByUserId(java.
	 * util.LinkedHashMap)
	 */
	
	@Override
	public ServiceResponse fetchNexxusSolutionsByUserId(LinkedHashMap<String, Object> queryParams) throws SalesBusinessException {
		NexxusSolutionDetailUiModelResponse response = new NexxusSolutionDetailUiModelResponse();
		int numberOfSolutions = 0;
		TypedQuery<NexxusSolutionDetailUIModel> q = null;
		List<NexxusSolutionDetailUIModel> resultList = null;
		//String flowType = queryParams.get("flowType") != null ? queryParams.get("flowType").toString() : null;
		String fetchBatchIndex = queryParams.get("fetchBatchIndex") != null ? queryParams.get("fetchBatchIndex").toString() : "1";
				
		//For MP initiated flow,fromSystem=myPrice
		//https://nexxusui.it.att.com/home?fromSystem=myPrice&d&userId=rk967c
		String fromSystem = queryParams.get("fromSystem") != null ? queryParams.get("fromSystem").toString() : StringUtils.EMPTY;
		if(Optional.ofNullable(fromSystem).isPresent() && fromSystem.equalsIgnoreCase("myPrice")) {
			String dealId = queryParams.get("dealId") != null ? queryParams.get("dealId").toString() : StringUtils.EMPTY;
			String transactionId = queryParams.get("transactionId") != null ? queryParams.get("transactionId").toString() : StringUtils.EMPTY;
			String archivedSolInd =  queryParams.get("archivedSolInd") != null ? queryParams.get("archivedSolInd").toString() : "N";
			q = em.createNamedQuery("getNexxusSolutionsByTxnNDealIdByGroup", NexxusSolutionDetailUIModel.class);
			q.setParameter("transactionId", transactionId);
			q.setParameter("dealId", dealId);
			q.setParameter("archivedSolInd", archivedSolInd);
			setDefaultParameters(q,fetchBatchIndex);
			 if(q.getResultList().isEmpty()) {
				// create a new solution entry in nx_solution_details(flowType = INR),nx_team and subsequently nx_mp_deal with default values 
				GetTransactionResponse getTransactionResponse = null;
				try {
					getTransactionResponse = (GetTransactionResponse) getTransactionServiceImpl.getTransaction(transactionId);
					if(Optional.ofNullable(getTransactionResponse).isPresent()) {
						NxTeam nxTeam = new NxTeam();
						NxSolutionDetail nxSolutionDetail = new NxSolutionDetail();
						nxSolutionDetail.setFlowType(StringConstants.FLOW_TYPE_INR);
						nxSolutionDetail.setActiveYn("Y");
						solutionRepo.save(nxSolutionDetail);
						nxTeam.setNxSolutionDetail(nxSolutionDetail);
						nxTeamRepository.save(nxTeam);
						NxMpDeal nxMpDeal = new NxMpDeal();
						nxMpDeal.setDealID(dealId);
						nxMpDeal.setTransactionId(transactionId);
						nxMpDeal.setVersion(getTransactionResponse.getVersion());
						nxMpDeal.setRevision(getTransactionResponse.getRevision());
						nxMpDeal.setSolutionId(nxTeam.getNxSolutionDetail().getNxSolutionId());
						nxMpDealRepository.save(nxMpDeal);
						q = em.createNamedQuery("getNexxusSolutionsByTxnNDealIdByGroup", NexxusSolutionDetailUIModel.class);
						q.setParameter("transactionId", transactionId);
						q.setParameter("dealId", dealId);
						q.setParameter("archivedSolInd", archivedSolInd);
						setDefaultParameters(q,fetchBatchIndex);
					} else {
						// solutions by logged in user
						String userId = queryParams.get("userId") != null ? queryParams.get("userId").toString() : StringUtils.EMPTY;
						if(Optional.ofNullable(userId).isPresent()) {
							q = em.createNamedQuery("getNexxusSolutionsByUserIdByGroup", NexxusSolutionDetailUIModel.class);
							q.setParameter("userId", userId);
							setIdParameters(q,queryParams);
							q.setParameter("archivedSolInd", archivedSolInd);
							setDefaultParameters(q,fetchBatchIndex);
						} else {
							return null;
						}
					}
				} catch (SalesBusinessException e) {
					e.printStackTrace();
				}

			} 
		} else {
			/*TypedQuery<NexxusSolutionDetailUIModel> q = null;*/
			String userId = queryParams.get("userId") != null ? queryParams.get("userId").toString() : StringUtils.EMPTY;
			String archivedSolInd =  queryParams.get("archivedSolInd") != null ? queryParams.get("archivedSolInd").toString() : "N";
			String filter = (String) queryParams.get("filter");
			if (filter == null) {
				q = em.createNamedQuery("getNexxusSolutionsByUserIdByGroup", NexxusSolutionDetailUIModel.class);
				logger.info("Inside generateReport method: {}", q);
				logger.info("Connecting to DataBase: {}");
				setIdParameters(q,queryParams);
				q.setParameter("userId", userId);
				q.setParameter("archivedSolInd", archivedSolInd);
			} else {
				
				List<NxAdminUserModel> adminUserList = (List<NxAdminUserModel>) nxAdminUserRepository.findByAttUid(userId);
						
				if(adminUserList != null && !adminUserList.isEmpty()) {
					q = em.createNamedQuery("getNexxusSolutionsBySearchCriteriaByGroupAdminUser", NexxusSolutionDetailUIModel.class);
					q.setParameter("filter", filter);
					q.setParameter("archivedSolInd", archivedSolInd);
				}
				
				else {
					q = em.createNamedQuery("getNexxusSolutionsBySearchCriteriaByGroup", NexxusSolutionDetailUIModel.class);
					q.setParameter("filter", filter);
					q.setParameter("userId", userId);
					q.setParameter("archivedSolInd", archivedSolInd);
				}
			}
			setDefaultParameters(q,fetchBatchIndex);
/*			
			String fetchBatchIndex = queryParams.get("fetchBatchIndex") != null
					? queryParams.get("fetchBatchIndex").toString()
					: "1";
			logger.info("fetchBatchIndex received in request is: {}", fetchBatchIndex);
			q.setParameter("fetchBatchIndex", Integer.parseInt(fetchBatchIndex));
			q.setParameter("ROW_FETCH_COUNT",
					com.att.sales.nexxus.constant.CommonConstants.LANDING_PAD_MAX_SOLUTIONS_FETCH_COUNT);
			resultList = q.getResultList();*/
	
/*			if (CollectionUtils.isNotEmpty(resultList)) {
				LinkedHashMap<Long, List<NexxusSolutionDetailUIModel>> groupBySolutionId = resultList.stream()
						.collect(Collectors.groupingBy(NexxusSolutionDetailUIModel::getNxSolutionId, LinkedHashMap::new,
								Collectors.toList()));
	
				Set<Entry<Long, List<NexxusSolutionDetailUIModel>>> keyValue = groupBySolutionId.entrySet();
				List<NexxusSolutionDetailUIModel> responseSolutionList = new ArrayList<>();
	
				for (Entry<Long, List<NexxusSolutionDetailUIModel>> entry : keyValue) {
					List<NexxusSolutionDetailUIModel> solutionList = entry.getValue();
	
					NexxusSolutionDetailUIModel firstSolution = solutionList.get(0);
					responseSolutionList.add(firstSolution);
	
					solutionList.forEach(n -> {
						NexxusEdfRequestDetail priceDetail = n.getNexxusEdfRequestDetail();
						if (priceDetail != null) {
							firstSolution.getRequestDetails().add(n.getNexxusEdfRequestDetail());
						}
					});
				}
	
				response.setNexxusSolutionsDetail(responseSolutionList);
				numberOfSolutions = groupBySolutionId.keySet().size();
			} else {
				setSuccessResponse(response, "M00201");
				return response;
			}*/
		}
		
/*		String fetchBatchIndex = queryParams.get("fetchBatchIndex") != null
				? queryParams.get("fetchBatchIndex").toString()
				: "1";*/
/*		logger.info("fetchBatchIndex received in request is: {}", fetchBatchIndex);
		q.setParameter("fetchBatchIndex", Integer.parseInt(fetchBatchIndex));
		q.setParameter("ROW_FETCH_COUNT",
				com.att.sales.nexxus.constant.CommonConstants.LANDING_PAD_MAX_SOLUTIONS_FETCH_COUNT);
*/		
		resultList = q.getResultList();
		logger.info("Time taken to get the resultList");
		logger.info("check ResultList", resultList);
		if (CollectionUtils.isNotEmpty(resultList)) {
			LinkedHashMap<Long, List<NexxusSolutionDetailUIModel>> groupBySolutionId = resultList.stream()
					.collect(Collectors.groupingBy(NexxusSolutionDetailUIModel::getNxSolutionId, LinkedHashMap::new,
							Collectors.toList()));

			Set<Entry<Long, List<NexxusSolutionDetailUIModel>>> keyValue = groupBySolutionId.entrySet();
			List<NexxusSolutionDetailUIModel> responseSolutionList = new ArrayList<>();

			for (Entry<Long, List<NexxusSolutionDetailUIModel>> entry : keyValue) {
				List<NexxusSolutionDetailUIModel> solutionList = entry.getValue();
					NexxusSolutionDetailUIModel firstSolution = solutionList.get(0);
					responseSolutionList.add(firstSolution);
					if(Optional.ofNullable(firstSolution).isPresent() && Optional.ofNullable(firstSolution.getFlowType()).isPresent() 
							&& (firstSolution.getFlowType().equalsIgnoreCase(StringConstants.FLOW_TYPE_INR) || firstSolution.getFlowType().equalsIgnoreCase(StringConstants.FLOW_TYPE_IGLOO_QUOTE) 
									|| firstSolution.getFlowType().equalsIgnoreCase(StringConstants.FLOW_TYPE_USRP))) {
						Map<Long,NexxusSolnsGroups> map = new HashMap<>();
						solutionList.forEach(n -> {
							
							NexxusEdfRequestDetail priceDetail = n.getNexxusEdfRequestDetail();
							if (priceDetail != null) {
								NexxusSolnsGroups group = n.getNexxusSolnsGroups();
								if(Optional.ofNullable(group).isPresent() && null != group.getNxReqGroupId()) {
									if(map.containsKey(group.getNxReqGroupId())) {
										NexxusSolnsGroups existingGroup = map.get(group.getNxReqGroupId());
										existingGroup.getRequestDetails().add(n.getNexxusEdfRequestDetail());
										map.put(group.getNxReqGroupId(), existingGroup);
									} else {
										group.setNxReqGroupId(group.getNxReqGroupId());
										group.setNxReqGroupDesc(group.getNxReqGroupDesc());
										group.getRequestDetails().add(n.getNexxusEdfRequestDetail());
										map.put(group.getNxReqGroupId(), group);
									}
								} /*else {
									group.setNxReqGroupId(priceDetail.getNxReqGroupId());
									group.setNxReqGroupDesc("N");
									group.getRequestDetails().add(n.getNexxusEdfRequestDetail());
									map.put(priceDetail.getNxReqGroupId(), group);
								}*/
								else {
									firstSolution.getRequestDetails().add(n.getNexxusEdfRequestDetail());
									//group.getRequestDetails().add(n.getNexxusEdfRequestDetail());
								}
	
							}
						});
						List<NexxusSolnsGroups> groups = new ArrayList<NexxusSolnsGroups>(map.values());
						firstSolution.setGroups(groups);
					}
				 else {
					/*NexxusSolutionDetailUIModel firstSolution = solutionList.get(0);
					responseSolutionList.add(firstSolution);*/
					/* HashMap<Long, NexxusEdfRequestDetail> requstDetailsMap = new HashMap<>();
					solutionList.forEach(n -> {
						NexxusEdfRequestDetail priceDetail = n.getNexxusEdfRequestDetail();
						if (priceDetail != null && !requstDetailsMap.containsKey(priceDetail.getNxReqId())) {
							requstDetailsMap.put(priceDetail.getNxReqId(), priceDetail);
						}
					});
					if(!requstDetailsMap.isEmpty()) {
						firstSolution.getRequestDetails().addAll(requstDetailsMap.values());
					}*/
					 solutionList.forEach(n -> {
							NexxusEdfRequestDetail priceDetail = n.getNexxusEdfRequestDetail();
						if (priceDetail != null) {
							firstSolution.getRequestDetails().add(n.getNexxusEdfRequestDetail());
						}
					 });
				}
			}
			logger.info("completion of forloop:");

			response.setNexxusSolutionsDetail(responseSolutionList);
			numberOfSolutions = groupBySolutionId.keySet().size();
		} else {
			setSuccessResponse(response, "M00201");
			return response;
		}
		response.setNumberOfSolutions(numberOfSolutions);
		setSuccessResponse(response);
		return response;
	}

	/**
	 * The Class InvGroupByKey.
	 */
	class InvGroupByKey {

		/** The begin bill month. */
		private String beginBillMonth;

		/** The bill month. */
		private String billMonth;

		/** The product. */
		private String product;

		/**
		 * Instantiates a new inv group by key.
		 *
		 * @param product        the product
		 * @param beginBillMonth the begin bill month
		 * @param billMonth      the bill month
		 */
		public InvGroupByKey(String product, String beginBillMonth, String billMonth) {
			super();
			this.beginBillMonth = beginBillMonth;
			this.billMonth = billMonth;
			this.product = product;
		}

		/**
		 * Gets the begin bill month.
		 *
		 * @return the begin bill month
		 */
		public String getBeginBillMonth() {
			return beginBillMonth;
		}

		/**
		 * Sets the begin bill month.
		 *
		 * @param beginBillMonth the new begin bill month
		 */
		public void setBeginBillMonth(String beginBillMonth) {
			this.beginBillMonth = beginBillMonth;
		}

		/**
		 * Gets the bill month.
		 *
		 * @return the bill month
		 */
		public String getBillMonth() {
			return billMonth;
		}

		/**
		 * Sets the bill month.
		 *
		 * @param billMonth the new bill month
		 */
		public void setBillMonth(String billMonth) {
			this.billMonth = billMonth;
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

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.lang.Object#hashCode()
		 */
		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + getOuterType().hashCode();
			result = prime * result + ((beginBillMonth == null) ? 0 : beginBillMonth.hashCode());
			result = prime * result + ((billMonth == null) ? 0 : billMonth.hashCode());
			result = prime * result + ((product == null) ? 0 : product.hashCode());
			return result;
		}

		/*
		 * (non-Javadoc)
		 * 
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
			InvGroupByKey other = (InvGroupByKey) obj;
			if (!getOuterType().equals(other.getOuterType()))
				return false;
			if (beginBillMonth == null) {
				if (other.beginBillMonth != null)
					return false;
			} else if (!beginBillMonth.equals(other.beginBillMonth))
				return false;
			if (billMonth == null) {
				if (other.billMonth != null)
					return false;
			} else if (!billMonth.equals(other.billMonth))
				return false;
			if (product == null) {
				if (other.product != null)
					return false;
			} else if (!product.equals(other.product))
				return false;
			return true;
		}

		/**
		 * Gets the outer type.
		 *
		 * @return the outer type
		 */
		private NexxusServiceImpl getOuterType() {
			return NexxusServiceImpl.this;
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.att.sales.nexxus.service.NexxusService#getBillingPriceInventryData(com.
	 * att.sales.nexxus.edf.model.ManageBillingPriceInvDataRequest)
	 */
	@SuppressWarnings({ "unused" })
	@Override
	public ServiceResponse getBillingPriceInventryData(ManageBillingPriceInvDataRequest inventoryrequest)
			throws SalesBusinessException {
		Long currentTime = System.currentTimeMillis();
		Long startTime = System.currentTimeMillis() - currentTime;
		
		ManageBillingPriceInvDataResponse objServiceResponse = new ManageBillingPriceInvDataResponse();
		Status status = new Status();
		status.setCode(CommonConstants.SUCCESS_STATUS);
		logger.info("inside getBillingPriceInventryData()");
		try {
			List<PriceInventoryDataRequest> reqInventoryList = inventoryrequest.getInventoryList();
			if (CollectionUtils.isNotEmpty(reqInventoryList)) {

				List<NxRequestDetails> nxRequestDetails = new ArrayList<>();

				List<ManageBillingPriceInventoryDataRequest> edfReqList = new ArrayList<>();

				Map<String, String> productTypeMap = new HashMap<String, String>();
				Map<String, String> groupNameMap = new HashMap<String, String>();
				String requestPricePayLoad = nexxusObjectMapper.writeValueAsString(inventoryrequest.getManageBillingPriceAccountDataRequest());
				InputDetailModel inputDetailModel = getInputDetailModel(requestPricePayLoad);
				
				for (PriceInventoryDataRequest priceInventoryDataRequest : reqInventoryList) {
					ManageBillingPriceInventoryDataRequest manageBillingresq = new ManageBillingPriceInventoryDataRequest();

					List<Report_Key> reportKeyList = new ArrayList<>();
					Report_Key reportKey = new Report_Key();

					reportKey.setDuns_number(priceInventoryDataRequest.getDunsNumber());
					reportKey.setL3_sub_acct_id(priceInventoryDataRequest.getL3SubAcctId());
					reportKey.setL4_acct_id(priceInventoryDataRequest.getL4AcctId());
					reportKey.setL5_master_acct_id(priceInventoryDataRequest.getL5MasterAcctId());
					reportKey.setMain_acct_number(priceInventoryDataRequest.getMainAcctNumber());
					reportKey.setMcn(priceInventoryDataRequest.getMcn());
					reportKey.setCust_acct_nbr(priceInventoryDataRequest.getCustAcctNbr());
					reportKey.setLead_acct_nbr(priceInventoryDataRequest.getLeadAcctNbr());
					reportKey.setSVID(priceInventoryDataRequest.getSVID());
	
					manageBillingresq.setBill_Month(priceInventoryDataRequest.getBillMonth());
					manageBillingresq.setBegin_Bill_Month(priceInventoryDataRequest.getBeginBillMonth());
					manageBillingresq.setCustomer_name(priceInventoryDataRequest.getCustomerName());
					manageBillingresq.setProduct(priceInventoryDataRequest.getProduct());
					if(productTypeMap.containsKey(priceInventoryDataRequest.getProduct())) {
						manageBillingresq.setProductType(productTypeMap.get(priceInventoryDataRequest.getProduct()));
					} else {
						NxLookupData nxLookupData = nxLookupDataRepository.findByDescriptionAndDatasetName(priceInventoryDataRequest.getProduct(), MyPriceConstants.NX_REQ_GROUP_NAMES);
						manageBillingresq.setProductType(nxLookupData.getDatasetName());  
						productTypeMap.put(priceInventoryDataRequest.getProduct(), nxLookupData.getDatasetName());
						groupNameMap.put(priceInventoryDataRequest.getProduct(), nxLookupData.getCriteria());
					}
					reportKeyList.add(reportKey);
					manageBillingresq.setReport_key(reportKeyList);
					manageBillingresq.setInputDetail(inputDetailModel);
					edfReqList.add(manageBillingresq);
				}

				LinkedHashMap<InvGroupByKey, List<ManageBillingPriceInventoryDataRequest>> invGroupedList = edfReqList
						.stream()
						.collect(Collectors.groupingBy(
								p -> new InvGroupByKey(p.getProduct(), p.getBegin_Bill_Month(), p.getBill_Month()),
								LinkedHashMap::new, Collectors.toList()));

				Set<Entry<InvGroupByKey, List<ManageBillingPriceInventoryDataRequest>>> keyValue = invGroupedList
						.entrySet();
				List<ManageBillingPriceInventoryDataRequest> invGroupedRequestList = new ArrayList<>();

				for (Entry<InvGroupByKey, List<ManageBillingPriceInventoryDataRequest>> entry : keyValue) {
					List<ManageBillingPriceInventoryDataRequest> accountList = entry.getValue();

					List<Report_Key> reportKeyList = new ArrayList<>();
					for (int i = 0; i < accountList.size(); i++) {
						reportKeyList.addAll(accountList.get(i).getReport_key());
					}
					List<List<Report_Key>> groupedReportKeyList = Lists.partition(reportKeyList, 200);
					if(!groupedReportKeyList.isEmpty()) {
						for(List<Report_Key> reportKeys : groupedReportKeyList) {
							ManageBillingPriceInventoryDataRequest firstAccountReq = new ManageBillingPriceInventoryDataRequest(accountList.get(0));
							firstAccountReq.getReport_key().addAll(reportKeys);
							invGroupedRequestList.add(firstAccountReq);
						}
					}
				}
				
				boolean isServiceExist = invGroupedRequestList.stream().filter(prod -> prod.getProductType().equalsIgnoreCase("SERVICE_GROUP") || prod.getProductType().equalsIgnoreCase("SERVICE_ACCESS_GROUP")).findAny().isPresent();

				for (ManageBillingPriceInventoryDataRequest manageBillingPriceInventoryDataRequest : invGroupedRequestList) {
					ManageBillDataInv reqForDme2 = new ManageBillDataInv();
					
					if (!("bvoip".equalsIgnoreCase(manageBillingPriceInventoryDataRequest.getProduct().toLowerCase()) ||
							"ABN LD Voice".equalsIgnoreCase(manageBillingPriceInventoryDataRequest.getProduct()) ||
							"SDN/ONENET LD Voice Usage".equalsIgnoreCase(manageBillingPriceInventoryDataRequest.getProduct()) ||
							"VTNS LD Voice Usage".equalsIgnoreCase(manageBillingPriceInventoryDataRequest.getProduct()))) {
						manageBillingPriceInventoryDataRequest.setBegin_Bill_Month(null);
					}
					reqForDme2.setManageBillingPriceInventoryDataRequest(manageBillingPriceInventoryDataRequest);
					ManageBillingPriceInventoryDataResponse resp = dme.getBillingPriceInventryUri(reqForDme2);
					NxRequestDetails details = new NxRequestDetails();
					details.setCreatedDate(new Date());
					details.setStatus(getEdfInvResponseStatus(resp)); 
					details.setProduct(manageBillingPriceInventoryDataRequest.getProduct());
					details.setNxRequestGroupName(manageBillingPriceInventoryDataRequest.getProductType());
					details.setNxReqDesc(manageBillingPriceInventoryDataRequest.getCustomer_name() + "_"
							+ groupNameMap.get(manageBillingPriceInventoryDataRequest.getProduct()));
					details.setCpniApprover(inventoryrequest.getCpniApprover());
					details.setEdfAckId(resp.getManageBillingPriceInventoryDataResponse().getRequestId()); 
					details.setUser(inventoryrequest.getAttuid());

					String requestPayLoad = nexxusObjectMapper.writeValueAsString(reqForDme2);
					details.setAcctCriteria(requestPayLoad);
					details.setFlowType("INR");
					details.setActiveYn(StringConstants.CONSTANT_Y);
					JsonNode accountData = JacksonUtil.toJsonNode(requestPricePayLoad);
					 ObjectNode obj = (ObjectNode) accountData;
					  if(!accountData.has("billMonth") && manageBillingPriceInventoryDataRequest.getBill_Month() != null) {
						  obj.put("billMonth",manageBillingPriceInventoryDataRequest.getBill_Month()); 
					  } 
					  if(!accountData.has("beginBillMonth") && manageBillingPriceInventoryDataRequest.getBegin_Bill_Month() != null) {
						  obj.put("beginBillMonth",manageBillingPriceInventoryDataRequest.getBegin_Bill_Month());
					  } 
					
					details.setManageBillingPriceJson(accountData.toString());
					nxRequestDetails.add(details);
				}

				boolean saveSuccess = saveInventoryPricingRequestData(inventoryrequest, nxRequestDetails,
						objServiceResponse, isServiceExist);
				if (!saveSuccess) {
					status.setCode(CommonConstants.FAILURE_STATUS);
				}
			}
			Long endTime = System.currentTimeMillis() - currentTime;
		    Long executionTime=endTime-startTime;
			auditUtil.addActionToNxUiAudit(null,AuditTrailConstants.REQUEST_BILLIND_PRICE_INVENTORY_DATA,null,AuditTrailConstants.SUCCESS,null,null,executionTime,null);
			return objServiceResponse;
		} catch (Exception e) {
			Long endTime = System.currentTimeMillis() - currentTime;
		    Long executionTime=endTime-startTime;
			auditUtil.addActionToNxUiAudit(null,AuditTrailConstants.REQUEST_BILLIND_PRICE_INVENTORY_DATA,null,AuditTrailConstants.FAIL,null,null,executionTime,null);
			logger.error("Exception from NexxusServiceImpl.getBillingPriceInventryData", e);
			throw new SalesBusinessException(MessageConstants.PROCESS_ERROR_CODE);
		}

	}
	
	public ServiceResponse retrieveBillingCharges(GetBillingChargesRequest inventoryrequest)
			throws SalesBusinessException {
		Long currentTime = System.currentTimeMillis();
		Long startTime = System.currentTimeMillis() - currentTime;

		ManageBillingPriceInvDataResponse objServiceResponse = new ManageBillingPriceInvDataResponse();
		Status status = new Status();
		status.setCode(CommonConstants.SUCCESS_STATUS);
		boolean isSingleAndHasFailure = false;
		List<NxReqRefNumMapping> mappingRecList = new ArrayList<NxReqRefNumMapping>();
		logger.info("Inside retrieveBillingCharges()");
		try {
			List<products> reqProductList = inventoryrequest.getProducts();
			Object manageBillingPriceInventoryDataRequest;
			if (CollectionUtils.isNotEmpty(reqProductList)) {
				List<NxRequestDetails> nxRequestDetails = new ArrayList<>();
				manageBillingPriceInventoryDataRequest = inventoryrequest.getManageBillingPriceInventoryDataRequest();
				String reqAcctCriteria = nexxusObjectMapper.writeValueAsString(manageBillingPriceInventoryDataRequest);
				String searchCriteriaStr = nexxusObjectMapper.writeValueAsString(inventoryrequest);
				logger.info("inventoryrequest : " + searchCriteriaStr);
				JsonNode searchCriteriaJson = JacksonUtil.toJsonNode(searchCriteriaStr);
				JsonNode manageBilliongJson = searchCriteriaJson.get("manageBillingPriceInventoryDataRequest");

				boolean isServiceExist = reqProductList.stream()
						.filter(prod -> prod.getProductType().equalsIgnoreCase("SERVICE_GROUP")
								|| prod.getProductType().equalsIgnoreCase("SERVICE_ACCESS_GROUP"))
						.findAny().isPresent();

				for (products prod : reqProductList) {
					NxRequestDetails details = new NxRequestDetails();
					details.setCreatedDate(new Date());
					details.setStatus(Long.valueOf(10));
					details.setProduct(prod.getProduct());
					details.setNxRequestGroupName(prod.getProductType());
					details.setNxReqDesc(prod.getProductType() + "_" + prod.getProduct());
					details.setCpniApprover(inventoryrequest.getCpniApprover());
					details.setUser(inventoryrequest.getAttuid());
					details.setAcctCriteria(reqAcctCriteria);
					details.setFlowType(StringConstants.FLOW_TYPE_USRP);
					details.setActiveYn(StringConstants.CONSTANT_Y);
					JsonNode accountData = JacksonUtil.toJsonNode(reqAcctCriteria);
					ObjectNode obj = (ObjectNode) accountData;
					if (!accountData.has("billMonth") && inventoryrequest.getBeginBillMonth() != null) {
						obj.put("billMonth", inventoryrequest.getBeginBillMonth());
					}
					if (!accountData.has("beginBillMonth") && inventoryrequest.getBeginBillMonth() != null) {
						obj.put("beginBillMonth", inventoryrequest.getBeginBillMonth());
					}
					details.setManageBillingPriceJson(accountData.toString());
					nxRequestDetails.add(details);

					ManageBillingPriceInvDataRequest req = new ManageBillingPriceInvDataRequest();
					req.setAttuid(inventoryrequest.getAttuid());
					req.setCpniApprover(inventoryrequest.getCpniApprover());
					req.setNxSolutionId(inventoryrequest.getNxSolutionId());

					boolean saveSuccess = false;
					try {
						saveSuccess = saveInventoryPricingRequestData(req, nxRequestDetails, objServiceResponse,
								isServiceExist);
					} catch (Exception ex) {
						saveSuccess = false;
					}

					String[] searchCriteriaValueArray = null;

					if (manageBilliongJson != null && manageBilliongJson.has("mcn")
							&& !Strings.isNullOrEmpty(manageBilliongJson.get("mcn").asText())) {
						searchCriteriaValueArray = manageBilliongJson.get("mcn").asText().split(",");
					} else if (manageBilliongJson != null && manageBilliongJson.has("svid")
							&& !Strings.isNullOrEmpty(manageBilliongJson.get("svid").asText())) {
						searchCriteriaValueArray = manageBilliongJson.get("svid").asText().split(",");
					}

					if (searchCriteriaValueArray != null && !searchCriteriaValueArray[0].isEmpty()) {
						for (String searchCriteria : searchCriteriaValueArray) {
							String nexxusRefNum = String.valueOf(System.currentTimeMillis());
							NxReqRefNumMapping mappingRec = new NxReqRefNumMapping();
							mappingRec.setNxReqId(String.valueOf(nxRequestDetails.get(0).getNxReqId()));
							mappingRec.setUsrpRequestObj(reqAcctCriteria);
							mappingRec.setNexxusRefNum(nexxusRefNum);
							mappingRec.setActiveYn(StringConstants.CONSTANT_Y);
							mappingRec.setCreatedDate(new Timestamp(System.currentTimeMillis()));
							nxReqRefNumMappingRepository.save(mappingRec);
							List<NxReqRefNumMapping> mappingReqList = nxReqRefNumMappingRepository
									.findByNexxusRefNum(nexxusRefNum);
							ABSDWGetBillingChargesRequest absDWReq = new ABSDWGetBillingChargesRequest();
							absDWReq.setBillDate(inventoryrequest.getBeginBillMonth());
							absDWReq.setKeyFieldID(
									getSearchCriteriaMapping(manageBilliongJson.get("searchCriteria").asText()));
							if (manageBilliongJson.has("mcn")
									&& !Strings.isNullOrEmpty(manageBilliongJson.get("mcn").asText())) {
								absDWReq.setMcnNB(searchCriteria);
								absDWReq.setSvID("");
							} else if (manageBilliongJson.has("svid")
									&& !Strings.isNullOrEmpty(manageBilliongJson.get("svid").asText())) {
								absDWReq.setMcnNB("");
								absDWReq.setSvID(searchCriteria);
							}

							absDWReq.setRequestType(prod.getProduct());
							absDWReq.setRefNB("NX_" + String.valueOf(mappingReqList.get(0).getNxReqRefNumMappingId()));

							GetBillingChargesResponse resp = null;
							Boolean isDWCallFailed = false;
							try {
								resp = getBillingChargesServiceImpl
										.performGetBillingCharges(absDWReq);
								mappingRec = mappingReqList.get(0);
								mappingRec.setStatus(resp.getResponseCode());
								mappingRec.setUsrpResponseObj(resp.getMessage());
								mappingRec.setModifiedDate(new Timestamp(System.currentTimeMillis()));
								mappingRec.setNexxusRefNum(
										"NX_" + String.valueOf(mappingReqList.get(0).getNxReqRefNumMappingId()));
								nxReqRefNumMappingRepository.save(mappingRec);

								logger.info("resp :::::::: " + resp.toString());
								logger.info("fl::::" +resp.getResponseCode());
							}catch(Exception e) {
								isDWCallFailed = true;
								logger.info("failure:::::" +resp.getResponseCode());
							}
							
							if (isDWCallFailed == true || (!resp.getResponseCode().equals("0000") && searchCriteriaValueArray.length == 1) ) {
								isSingleAndHasFailure = true;
								NxSolutionDetail solution = solutionRepo.findByNxSolutionId(objServiceResponse.getNxSolutionId());
								if(StringUtils.isEmpty(inventoryrequest.getNxSolutionId()) ) {
									List<NxTeam> nxTeamList = nxTeamRepository.findByNxSolutionId(objServiceResponse.getNxSolutionId());
									nxTeamRepository.deleteAll(nxTeamList);
									List<NxRequestDetails> reqDetailsList = repo.findByNxSolutionId(objServiceResponse.getNxSolutionId());
									repo.deleteAll(reqDetailsList);
									solutionRepo.delete(solution);
								} else {
									for(NxRequestDetails nxReq : nxRequestDetails) {
										repo.inactiveReqDetails(nxReq.getNxReqId());
									}
								}
								
							}

							if (!resp.getResponseCode().equals("0000"))
								continue;

							mappingRecList.add(mappingRec);
						}
					}
					if (!saveSuccess || isSingleAndHasFailure) {
						status.setCode(CommonConstants.FAILURE_STATUS);
						objServiceResponse.setStatus(status);
					}
				}
			}
			Long endTime = System.currentTimeMillis() - currentTime;
			Long executionTime = endTime - startTime;
			auditUtil.addActionToNxUiAudit(null, AuditTrailConstants.RETRIEVE_BILLING_CHARGES, null,
					AuditTrailConstants.SUCCESS, null, null, executionTime,null);
			logger.info("End retrieveBillingCharges()");
			return objServiceResponse;
		} catch (Exception e) {
			Long endTime = System.currentTimeMillis() - currentTime;
			Long executionTime = endTime - startTime;
			auditUtil.addActionToNxUiAudit(null, AuditTrailConstants.RETRIEVE_BILLING_CHARGES, null,
					AuditTrailConstants.FAIL, null, null, executionTime,null);
			logger.error("Exception from NexxusServiceImpl.getBillingPriceInventryData", e);
			throw new SalesBusinessException(MessageConstants.PROCESS_ERROR_CODE);
		}
	}
	
	private String getSearchCriteriaMapping(String searchCriteria) {
		Map<String,String> searchCriteriaAndKeyMapping = new HashMap<>();
		if(searchCriteria.equalsIgnoreCase("MCN")) {
			searchCriteriaAndKeyMapping.put("MCN", "M");	
		} else if(searchCriteria.equalsIgnoreCase("SVID")) {
			searchCriteriaAndKeyMapping.put("SVID", "S");	
		}
		return searchCriteriaAndKeyMapping.get(searchCriteria);
	}

	private InputDetailModel getInputDetailModel(String data) {
		JsonNode accountData = JacksonUtil.toJsonNode(data);
		
		InputDetailModel inputDetailObj = new InputDetailModel();
		if(!accountData.isMissingNode()) {
			if(null != accountData.get("dunsNumber") && accountData.get("dunsNumber").asText() != null) {
				inputDetailObj.setDunsNumber(accountData.get("dunsNumber").asText());
			} 
			if(null != accountData.get("l5MasterAcctId") && accountData.get("l5MasterAcctId").asText() !=null) {
				inputDetailObj.setL5MasterAcctId(accountData.get("l5MasterAcctId").asText());
			} 
			if(null != accountData.get("l4AcctId") && accountData.get("l4AcctId").asText() !=null) {
				inputDetailObj.setL4AcctId(accountData.get("l4AcctId").asText());
			} 
			if(null != accountData.get("l3SubAcctId") && accountData.get("l3SubAcctId").asText() !=null) {
				inputDetailObj.setL3SubAcctId(accountData.get("l3SubAcctId").asText());
			}
		}
		
		return inputDetailObj;
		
	}
	
	/**
	 * This method sets the status code for the request in DB.
	 *
	 * @param resp the resp
	 * @return the edf inv response status
	 */
	private Long getEdfInvResponseStatus(ManageBillingPriceInventoryDataResponse resp) {
		if (resp != null && resp.getManageBillingPriceInventoryDataResponse() != null) {
			String status = resp.getManageBillingPriceInventoryDataResponse().getStatus();
			if ("success".equalsIgnoreCase(status)) {
				return com.att.sales.nexxus.constant.CommonConstants.STATUS_CONSTANTS.IN_PROGRESS.getValue();
			}
		}

		return com.att.sales.nexxus.constant.CommonConstants.STATUS_CONSTANTS.ERROR.getValue();
	}

	/**
	 * Save inventory pricing request data.
	 *
	 * @param inventoryrequest   the inventoryrequest
	 * @param nxRequestDetails   the nx request details
	 * @param objServiceResponse the obj service response
	 * @return true, if successful
	 * @throws SalesBusinessException the sales business exception
	 */
	public boolean saveInventoryPricingRequestData(ManageBillingPriceInvDataRequest inventoryrequest,
			List<NxRequestDetails> nxRequestDetails, ManageBillingPriceInvDataResponse objServiceResponse, boolean isServiceExist)
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
				List<NxRequestDetails> accessProducts = new ArrayList<NxRequestDetails>();
				Set<Long> nxRequestGrpIds = new HashSet<Long>();
				for (NxRequestDetails nxRequestDetails2 : nxRequestDetails) {
					boolean serivceAccessGrp = true;
					nxRequestDetails2.setNxSolutionDetail(details);
					if(!isServiceExist) {
						// only one access product
						NxLookupData nxLookupData = nxLookupDataRepository.findByDatasetNameAndDescription("ACCESS_GROUP", nxRequestDetails2.getProduct());
						saveNxRequestGroup(nxRequestDetails2, nxLookupData, nxSolutionId, MyPriceConstants.NOT_APPLICABLE);
						nxRequests.add(nxRequestDetails2);
					}else {
						NxLookupData nxLookupData = null;
						nxLookupData = nxLookupDataRepository.findByDatasetNameAndDescription("SERVICE_ACCESS_GROUP", nxRequestDetails2.getProduct());
						if(null == nxLookupData) {
							nxLookupData = nxLookupDataRepository.findByDatasetNameAndDescription("SERVICE_GROUP", nxRequestDetails2.getProduct());
							if(nxLookupData != null)
								saveNxRequestGroup(nxRequestDetails2, nxLookupData, nxSolutionId, MyPriceConstants.NOT_APPLICABLE);
							serivceAccessGrp = false;
						}
						if(nxLookupData != null) {
							if(serivceAccessGrp) {
								saveNxRequestGroup(nxRequestDetails2, nxLookupData, nxSolutionId, null);
								nxRequestGrpIds.add(nxRequestDetails2.getNxRequestGroupId());
							}
							nxRequests.add(nxRequestDetails2);
						}else {
							accessProducts.add(nxRequestDetails2);
						}
					}
				}
				repo.saveAll(nxRequests);
				if(nxRequestGrpIds.isEmpty()) {
					for(NxRequestDetails req : accessProducts) {
						NxLookupData nxLookupData = nxLookupDataRepository.findByDatasetNameAndDescription("ACCESS_GROUP", req.getProduct());
						saveNxRequestGroup(req, nxLookupData, nxSolutionId, MyPriceConstants.NOT_APPLICABLE);
					}
					repo.saveAll(accessProducts);
				}else {
					for(Long nxRequestGrpId : nxRequestGrpIds) {
						NxRequestGroup nxRequestGroup = nxRequestGroupRepository.findByNxRequestGroupIdAndActiveYn(nxRequestGrpId, StringConstants.CONSTANT_Y);
						for(NxRequestDetails nxRequestDetail : accessProducts) {
							NxRequestDetails newNxRequestDetails = new NxRequestDetails();
							BeanUtils.copyProperties(nxRequestDetail, newNxRequestDetails, "nxReqId");
							newNxRequestDetails.setNxRequestGroupId(nxRequestGrpId);
							//newNxRequestDetails.setNxRequestGroupName(nxLookupData.get(0).getDatasetName());
							repo.save(newNxRequestDetails);
						}
						// service + access check
						List<NxRequestDetails> nxReqDetails = nxRequestDetailsRepository.findRequestsByGroupId(nxRequestGrpId, StringConstants.CONSTANT_Y);
						if(CollectionUtils.isNotEmpty(nxReqDetails)) {
							List<String> access = nxLookupDataRepository.findByDatasetName("ACCESS_GROUP").stream().map(NxLookupData::getDescription).collect(Collectors.toList());
							List<NxRequestDetails> accessRequest = nxReqDetails.stream().filter(n -> access.contains(n.getProduct())).collect(Collectors.toList());
							if(CollectionUtils.isEmpty(accessRequest)) {
								nxRequestGroup.setStatus(MyPriceConstants.NOT_APPLICABLE);
								nxRequestGroup.setModifiedDate(new Date());
								nxRequestGroupRepository.save(nxRequestGroup);
							}
						}
					}
				}
				
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

	public void saveNxRequestGroup(NxRequestDetails nxRequestDetails2, NxLookupData nxLookupData, Long nxSolutionId, String groupStatus) {
		NxRequestGroup nxRequestGroup = null;
		List<NxRequestGroup> nxRequestGroups = nxRequestGroupRepository.findByNxSolutionIdAndGroupIdAndActiveYn(nxSolutionId, Long.parseLong(nxLookupData.getItemId()), StringConstants.CONSTANT_Y);
		if(CollectionUtils.isEmpty(nxRequestGroups)) {
			nxRequestGroup = new NxRequestGroup();
			nxRequestGroup.setGroupId(Long.parseLong(nxLookupData.getItemId()));
			nxRequestGroup.setDescription(nxLookupData.getCriteria());
			nxRequestGroup.setNxSolutionId(nxSolutionId);
			if(groupStatus != null) {
				nxRequestGroup.setStatus(groupStatus);
			}else {
				nxRequestGroup.setStatus(MyPriceConstants.IN_PROGRESS);
			}
		
			nxRequestGroup.setActiveYn(StringConstants.CONSTANT_Y);
			nxRequestGroupRepository.save(nxRequestGroup);
			nxRequestDetails2.setNxRequestGroupId(nxRequestGroup.getNxRequestGroupId());
			nxRequestDetails2.setNxRequestGroupName(nxLookupData.getDatasetName());
		}else {
			nxRequestDetails2.setNxRequestGroupId(nxRequestGroups.get(0).getNxRequestGroupId());
			nxRequestDetails2.setNxRequestGroupName(nxLookupData.getDatasetName());
			nxRequestGroup = nxRequestGroups.get(0);
			if(groupStatus != null) {
				nxRequestGroup.setStatus(groupStatus);
			}else {
				nxRequestGroup.setStatus(MyPriceConstants.IN_PROGRESS);
			}
			nxRequestGroup.setModifiedDate(new Date());
			nxRequestGroupRepository.save(nxRequestGroup);
		}
	}

	/**
	 * Gets the internal test.
	 *
	 * @return the internal test
	 */
	public ServiceResponse getInternalTest() {
		
		logger.info("Inside getInternalTest()");
		/*List<NxSolutionDetail> nxSolutionDetail = nxSolutionDetailsRepository.findByNxSolutionId(5455L);
				List<Long> nxRequestGrpId = new ArrayList<Long>(){{
					add(2446L);
				}};
		submitToMyPriceService.submitToMyPrice(nxSolutionDetail.get(0), nxRequestGrpId,
				"INR");*/
		NxMpDeal deal = nxMpDealRepository.findByTransactionId("203340995");
		mailServiceImpl.prepareMyPriceDealSubmissionRequest(deal);
		//inrQualifyService.inrQualifyCheck(5325L, true);
		//Map<String, Object> configUpdateResMap = new HashMap<String, Object>();
		
		//Map<String, Object> paramMap = new HashMap<String, Object>();
		//Map<String, Object> configRespMap = new HashMap<String, Object>();
		
	/*	CreateTransactionResponse createTransactionResponse = new CreateTransactionResponse();
		NxMpDeal deal = nxMpDealRepository.findByNxTxnId(9837L);
		createTransactionResponse.setDealID(deal.getDealID());
		createTransactionResponse.setMyPriceTransacId(deal.getTransactionId());
		createTransactionResponse.setNxTransacId(deal.getNxTxnId());
		createTransactionResponse.setOfferName(deal.getOfferId());*/
/*		List<NxAccessPricingData> nxAccessPricingDatas = nxAccessPricingDataRepository.findByNxSolutionId(4093L);
		for(NxAccessPricingData accessPricingData : nxAccessPricingDatas) {
			configRespMap = configAndUpdateProcessingIglooService.callConfigSolutionAndDesign(createTransactionResponse, accessPricingData, paramMap);
		}
		configUpdateResMap.putAll(configRespMap);*/
		
		//CreateTransactionResponse createTransactionResponse  = (CreateTransactionResponse) result.get("createTransactionRes");
	/*	Map<String, Object> configUpdateResMap = new HashMap<String, Object>();
		Map<String, Object> requestMetaDataMap = new HashMap<>();
		if (ServiceMetaData.getRequestMetaData() != null) {
			ServiceMetaData.getRequestMetaData().forEach((key, value) -> requestMetaDataMap.put(key, value));
		}*/
/*		List<NxSolutionDetail> solution = (List<NxSolutionDetail>) solutionRepo.findByNxSolutionIdAndActiveYn(12335L, StringConstants.CONSTANT_Y);
		List<Long> getDataIds = new ArrayList<>();
		getDataIds.add(2679L);
		CompletableFuture.runAsync(() -> {
			processINRtoMP.process(solution.get(0), getDataIds, requestMetaDataMap, configUpdateResMap, "INR", createTransactionResponse,  null,
				 createTransactionResponse.getOfferName());
		});
		
*/		
		/*List<NxInrDesign> nxInrDesigns = nxInrDesignRepository.findByNxSolutionIdAndStatusAndActiveYN(
				12335L, MyPriceConstants.API_FAILED, StringConstants.CONSTANT_Y);
		Map<Long,Set<String>> failedCktsByReqIdMap = new HashMap<>();
		Optional.ofNullable(nxInrDesigns).map(List::stream).orElse(Stream.empty()).forEach(design->{
			if(CollectionUtils.isNotEmpty(design.getNxInrDesignDetails())) {
				for(NxInrDesignDetails designDetail : design.getNxInrDesignDetails()) {
					if(Optional.ofNullable(designDetail.getNxReqId()).isPresent()) {
						if(failedCktsByReqIdMap.containsKey(designDetail.getNxReqId())) {
							Set<String> failedCkts = failedCktsByReqIdMap.get(designDetail.getNxReqId());
							failedCkts.add(design.getCircuitId());
							failedCktsByReqIdMap.put(designDetail.getNxReqId(), failedCkts);
						} else {
							Set<String> failedCkts = new HashSet<>();
							failedCkts.add(design.getCircuitId());
							failedCktsByReqIdMap.put(designDetail.getNxReqId(), failedCkts);
						}
					}
				}
			}
		});
		
		failedCktsByReqIdMap.forEach((reqId,ckts)->{
			NxDesignAudit nxDesignAudit = new NxDesignAudit();
			nxDesignAudit.setTransaction(MyPriceConstants.MYPRICE_FAILED_CIRCUITS);
			nxDesignAudit.setData(ckts.toString());
			nxDesignAudit.setNxRefId(reqId);
			nxDesignAuditRepository.save(nxDesignAudit);
		});
		*/

		/*List<NxRequestDetails> nxRequestDetail = nxRequestDetailsRepository.findByEdfAckIdAndActiveYn("72787720200205022915", "Y");
		
		for(NxRequestDetails nxRequestDetails : nxRequestDetail) {
			if (null != nxRequestDetails && null != nxRequestDetails.getNxReqId()) {
			
				String fileName = "AETNA LIFE INSURANCE CO_120586867_58452720200123073005_20200122_233210.xml";
				nxRequestDetails.setFileName(fileName);
				nxRequestDetails.setDmaapMsg("test");

				logger.info("trigger InrProcessingService");
				long status = inrProcessingService.createInrNexusOutput(nxRequestDetails);
				logger.info("setting NxRequestDetails status={} where nxReqId={}", status, nxRequestDetails.getNxReqId());
				
			}
		}*/
		
		//inrQualifyService.inrQualifyCheck(2420L);
/*		Map<String, Object> designMap = new HashMap<>();
		designMap.put(MyPriceConstants.MP_TRANSACTION_ID, 102027615);
		designMap.put(MyPriceConstants.NX_DESIGN_ID, 1841L);
		designMap.put(MyPriceConstants.NX_TRANSACTION_ID, 2241L);
		designMap.put(StringConstants.PRICE_SCENARIO_ID, 99999941437L);
		designMap.put(MyPriceConstants.OFFER_TYPE, "AVPN");

		try {
			u.updateTransactionPricingRequest(designMap);
		} catch (SalesBusinessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
		
		
		//inrQualifyService.inrQualifyCheck(2774L);
		/*
		 * Map<String, Object> designMap = new HashMap<>();
		 * designMap.put(MyPriceConstants.MP_TRANSACTION_ID, 102027615);
		 * designMap.put(MyPriceConstants.NX_DESIGN_ID, 1841L);
		 * designMap.put(MyPriceConstants.NX_TRANSACTION_ID, 2241L);
		 * designMap.put(StringConstants.PRICE_SCENARIO_ID, 99999941437L);
		 * designMap.put(MyPriceConstants.OFFER_TYPE, "AVPN");
		 * 
		 * try { u.updateTransactionPricingRequest(designMap); } catch
		 * (SalesBusinessException e) { // TODO Auto-generated catch block
		 * e.printStackTrace(); }
		 */
		ServiceResponse response = new ServiceResponse();
		//add testing code here
		
		//myprice endpoint test
//		
//		String createSaveRequest = "{\"documents\":\r\n" + 
//				"	{\"_customer_t_first_name\":\"TEST\",\r\n" + 
//				"		\"_customer_t_last_name\":\"USER\",\r\n" + 
//				"		\"_customer_t_company_name\":\"TEST Company\",\r\n" + 
//				"		\"_customer_t_address\":\"200 S Laurel Ave\",\r\n" + 
//				"		\"_customer_t_address_2\":\"South\",\r\n" + 
//				"		\"_customer_t_city\":\"Middletown\",\r\n" + 
//				"		\"_customer_t_state\":\"NJ\",\r\n" + 
//				"		\"_customer_t_zip\":\"07748\",\r\n" + 
//				"		\"_customer_t_country\":\"US\",\r\n" + 
//				"		\"_customer_t_fax\":\"346-23-111\",\r\n" + 
//				"		\"_customer_t_email\":\"madevire@in.ibm.com\",\r\n" + 
//				"		\"opportunityID_t\":\"1-NISHA\",\r\n" + 
//				"		\"opportunityName_t\":\"TEST Opty One\",\r\n" + 
//				"		\"sAARTAccountNumber\":\"LT_SAART_ACCT_0001\"\r\n" + 
//				"	}\r\n" + 
//				"}";
//		String createSaveUrl = "https://custompricingdev2.att.com/rest/v7/commerceDocumentsOraclecpqo_bmClone_2Transaction/39002093/actions/cleanSave_t";
//		
//		String updateLineItemRequest = "{\"documents\":\r\n" + 
//				"	{\"transactionLine\":\r\n" + 
//				"		{\"items\":\r\n" + 
//				"			[\r\n" + 
//				"				{\"_document_number\":\"4\",\"lii_requestedDiscountPctgNRC_ql\":\"2.0\"},\r\n" + 
//				"				{\"_document_number\":\"5\",\"lii_requestedDiscountPctgMRC_ql\":\"3.0\"}\r\n" + 
//				"			]\r\n" + 
//				"		}\r\n" + 
//				"	}\r\n" + 
//				"}\r\n" + 
//				"";
//		String updateLineItemUrl = "https://custompricingdev2.att.com/rest/v7/commerceDocumentsOraclecpqo_bmClone_2Transaction/39002093/actions/_update_line_items";
//		
/*		String createRequest = "{}";
=======
		/*String createRequest = "{}";
>>>>>>> 8ae29f314ca3dea87ed2be39dfe80285f0199614
		String createUrl = "https://custompricingdev2.att.com/rest/v7/commerceDocumentsOraclecpqo_bmClone_2Transaction/";
		
		Map<String, Object> queryParameters = new HashMap<String, Object>();
		Map<String, String> headers  = new HashMap<String, String>();
		String encoded= Base64.getEncoder().encodeToString(("salesclient" + ":" + "cH4&$A5sSy").getBytes());
		logger.info("encoded ::"+encoded);
		headers.put(StringConstants.REQUEST_AUTHORIZATION, "Basic "+encoded);
		headers.put(StringConstants.REQUEST_CONTENT_TYPE, "application/json");*/

		/*
		 * String createSaveResponse = null; try { createSaveResponse =
		 * restClientUtil.callRestClient(createSaveRequest, createSaveUrl, "POST",
		 * headers, queryParameters); } catch (SalesBusinessException e) {
		 * logger.error("Inside getInternalTest() for createsave error",e);
		 * e.printStackTrace(); }
		 */
//		
//		String updateLineItemResponse = null;
//		try {
//			updateLineItemResponse = restClientUtil.callRestClient(updateLineItemRequest, updateLineItemUrl, "POST", headers, queryParameters);
//		} catch (SalesBusinessException e) {
//			logger.error("Inside getInternalTest() for update error",e);
//			e.printStackTrace();
//		}
//		

		/*		String createResponse = null;
=======
		/*String createResponse = null;
>>>>>>> 8ae29f314ca3dea87ed2be39dfe80285f0199614
		try {
			createResponse = restClientUtil.callRestClient(createRequest, createUrl, "POST", headers, queryParameters);
		} catch (SalesBusinessException e) {
			logger.error("Inside getInternalTest() for save error",e);
			e.printStackTrace();
		}*/

		//		String res = String.format("createSaveResponse:%n%s%nupdateLineItemResponse:%n%s%ncreateResponse:%n%s%n", createSaveResponse, updateLineItemResponse, createResponse);
//		System.out.println("res is:\n" + res);
//		logger.info("save response:{}", createSaveResponse);
//		logger.info("update response:{}", updateLineItemResponse);
		//logger.info("create response:{}", createResponse);
//		response.setTimestamp(res);
		
		
		//snsd endpoint test start
		/*String snsdUrl = "http://zldv0170.vci.att.com:7100/ADEWAR/rest/queue/PedNssEngagementSOAP";
		String snsdRequest = "{\"customerName\":\"AT&T B End User (Affiliate)\",\"caseType\":null,\"opportunityId\":\"1-85J3EUY\",\"erateFlag\":\"FALSE\",\"solutionId\":\"1499957\",\"imsDealNumber\":897141,\"presaleExpediteFlag\":null,\"customerRequestedDueDate\":null,\"specialConstructionContractUrl\":null,\"presalesExpediteComments\":null,\"specialConstructionPaymentUrl\":null,\"presalesExpediteDate\":null,\"postsaleUser\":null,\"imsVersionNumber\":1,\"salesUser\":null,\"solutionStatus\":\"N\",\"salesChannel\":\"Wholesale\",\"design\":[{\"designStatus\":\"N\",\"asrItemId\":\"AD0040990\",\"cancelReason\":null,\"designDetails\":{\"product\":\"ADE\",\"quantity\":1,\"siteDetails\":[{\"siteIdentifier\":\"Z\",\"clientSiteId\":null,\"alias\":\"PRI PREM ECAP-4329619\",\"clli\":\"IRVECARR\",\"address\":{\"addressLine\":[\"17836 GILLETTE AV\",\"LINE 2\",\"Suite 1\",\"FLR 1\",\"RM Telco\"],\"city\":\"IRV\",\"state\":\"CA\",\"postalCode\":\"92614\",\"country\":null},\"lata\":\"730\",\"certification\":\"G\",\"portConnectionType\":\"1 GE Native Ethernet\",\"portInterfaceType\":\"1310 LX\",\"serviceLocationType\":\"IR / IF End-User Customer\",\"customerConfigType\":\"End-User Customer\",\"regionCode\":\"Y\",\"franchiseCode\":\"IF\",\"protectionOptions\":\"Unprotected\",\"edgelessDesignFlag\":\"FALSE\",\"independentCarrierCompanyLATA\":null,\"independentCarrierCompanyName\":\"PACIFIC BELL\",\"standaloneDiversityFlag\":\"FALSE\",\"newBuildingFlag\":\"FALSE\",\"secondEntranceRequiredFlag\":\"FALSE\",\"networkChannelInterfaceCode\":\"02LNF.A02\",\"servingWirecenter\":\"IRVNCA11\",\"alternateServingWirecenter\":null,\"interDepartmentMeetPointChecklistURL\":null,\"diverseFromReferenceInfo\":null,\"opticalTerminatingNetworkCarrierFacilityAssignment\":null,\"collocationCarrierFacilityAssignment\":null,\"npanxx\":null,\"loopLength\":null,\"numberRepeaters\":null,\"numberCopperPairs\":null,\"numberRemoteTerminals\":null,\"ta5KClli\":null,\"addDropFlag\":null,\"localContactDetails\":null,\"alternateLocalContactDetails\":null,\"buildingContactDetails\":null,\"entranceFacilityOffer\":null,\"carrierFacilityAssignment\":null,\"noShareFlag\":null,\"endCustomerName\":\"BCM One\",\"portType\":null,\"macdSubActivity\":[],\"userProvidedPowerSupplyFlag\":null,\"comments\":\"ADE-ATX-BCM One-4329619 4329620 BOT_1\",\"newBuildingIndicator\":false,\"channelizedIndicator\":\"false\"},{\"siteIdentifier\":\"A\",\"clientSiteId\":null,\"alias\":\"PRI POP ECAP-4329619\",\"clli\":\"GRDNCA02\",\"address\":{\"addressLine\":[\"17200 S VERMONT AV\",\"NA\",\"NA\",\"NA\",\"NA\"],\"city\":\"GRD\",\"state\":\"CA\",\"postalCode\":\"90247\",\"country\":null},\"lata\":\"730\",\"certification\":\"G\",\"portConnectionType\":\"1 GE Native Ethernet\",\"portInterfaceType\":\"1310 LX\",\"serviceLocationType\":\"IR / IF IXC PoP\",\"customerConfigType\":\"Access Carrier\",\"regionCode\":\"Y\",\"franchiseCode\":\"IF\",\"protectionOptions\":\"Unprotected\",\"edgelessDesignFlag\":\"FALSE\",\"independentCarrierCompanyLATA\":null,\"independentCarrierCompanyName\":\"PACIFIC BELL\",\"standaloneDiversityFlag\":\"FALSE\",\"newBuildingFlag\":\"FALSE\",\"secondEntranceRequiredFlag\":\"FALSE\",\"networkChannelInterfaceCode\":\"02LNF.A02\",\"servingWirecenter\":\"GRDNCA01\",\"alternateServingWirecenter\":null,\"interDepartmentMeetPointChecklistURL\":null,\"diverseFromReferenceInfo\":null,\"opticalTerminatingNetworkCarrierFacilityAssignment\":null,\"collocationCarrierFacilityAssignment\":null,\"npanxx\":null,\"loopLength\":null,\"numberRepeaters\":null,\"numberCopperPairs\":null,\"numberRemoteTerminals\":null,\"ta5KClli\":null,\"addDropFlag\":null,\"localContactDetails\":null,\"alternateLocalContactDetails\":null,\"buildingContactDetails\":null,\"entranceFacilityOffer\":null,\"carrierFacilityAssignment\":null,\"noShareFlag\":null,\"endCustomerName\":\"BCM One\",\"portType\":null,\"macdSubActivity\":[],\"userProvidedPowerSupplyFlag\":null,\"comments\":\"ADE-ATX-BCM One-4329619 4329620 BOT_1\",\"newBuildingIndicator\":false,\"channelizedIndicator\":\"false\"}],\"alias\":null,\"facilityType\":null,\"ringDetails\":null,\"assetInvestmentSheetFlag\":\"FALSE\",\"imsProductNumber\":1,\"macdType\":\"ADD\",\"macdActivity\":[\"Newstart Circuit\"],\"circuitId\":null,\"jurisdiction\":\"Intrastate Access (Interlata/Intrastate)\",\"interWirecenterDiversityFlag\":\"FALSE\",\"diverseFromASRItemId\":\"AD0040991\",\"diverseFromCircuitId\":null,\"specialRouting\":\"NNN\",\"networkChannelCode\":\"KFL-\",\"percentInterlataUsage\":0,\"commonLanguageCircuitId\":\"KFFS\",\"certification\":\"G\",\"certificationReason\":null,\"customerNetworkManagementFlag\":null,\"committedInformationRate\":null,\"annualRevenue\":null,\"monthlyRevenue\":null,\"dateSoldToCustomer\":null,\"specialConstructionCharge\":null,\"specialConstructionHandling\":null,\"tspFlag\":null,\"tspAuthorizationCode\":null,\"comments\":null,\"icsc\":null,\"ringAsrItemId\":null,\"ringId\":null,\"accessCarrierNameAbbreviation\":\"ATX\",\"classOfServiceTy
		//Map<String, Object> queryParameters = new HashMap<String, Object>();
		//Map<String, String> headers  = new HashMap<String, String>();
		//headers.put(StringConstants.REQUEST_CONTENT_TYPE, "application/json");
		//headers.put("USER-ID", "nexxus");
	//	headers.put("PASSWORD", "nhy$%89ws");
	//	headers.put("Accept", "*");
	//	String snsdResponse = null;
		try {
		//	snsdResponse = restClientUtil.callRestClient(snsdRequest, snsdUrl, "PUT", headers, queryParameters);
		} catch (SalesBusinessException e) {
		//	logger.error("Exception", e);
		}
		//logger.info("snsd response is: {}", snsdResponse);
		//response.setTimestamp(snsdResponse);*/
		//sdsd endpoint test end
		
		return setSuccessResponse(response);
	}

	private void setDefaultParameters(TypedQuery<NexxusSolutionDetailUIModel> q,String fetchBatchIndex) {
		logger.info("fetchBatchIndex received in request is: {}", fetchBatchIndex);
		q.setParameter("fetchBatchIndex", Integer.parseInt(fetchBatchIndex));
		q.setParameter("ROW_FETCH_COUNT",
				com.att.sales.nexxus.constant.CommonConstants.LANDING_PAD_MAX_SOLUTIONS_FETCH_COUNT);
		String[] flowTypes = {"INR","FMO","iglooQuote","USRP"};
		q.setParameter("flowType", Arrays.asList(flowTypes));
	}
	private void setIdParameters(TypedQuery<NexxusSolutionDetailUIModel> q,LinkedHashMap<String, Object> queryParams) {
		String externalId = queryParams.get("externalId") != null ? queryParams.get("externalId").toString() : StringUtils.EMPTY;
		String optyId = queryParams.get("optyId") != null ? queryParams.get("optyId").toString() : StringUtils.EMPTY;
		String nxId = queryParams.get("nxId") != null ? queryParams.get("nxId").toString() : StringUtils.EMPTY;

		q.setParameter("externalId", externalId);
		q.setParameter("optyId", optyId);
		q.setParameter("nxId", nxId);
	}
	
	public ServiceResponse refreshCache() {
		logger.info("Inside refreshCache()");
		ServiceResponse response = new ServiceResponse();
		nxMyPriceRepositoryServce.clearCache();
		return setSuccessResponse(response);
	}
	
	public void updateNxSolution(Long nxSolutionId) {
		if(Optional.ofNullable(nxSolutionId).isPresent()) {
			NxSolutionDetail solutionDetail = nxSolutionDetailsRepository.findByNxSolutionId(nxSolutionId);
			if(Optional.ofNullable(solutionDetail).isPresent()) {
				solutionDetail.setModifiedDate(new Date());
				nxSolutionDetailsRepository.save(solutionDetail);
			}
		}
	}
}