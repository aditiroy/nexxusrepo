	
package com.att.sales.nexxus.service;

/**
 * 
 * @author Akash Arya
 *
 * Logic to process the response once request received from Interface Orch
 */
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

import javax.transaction.Transactional;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.assertj.core.util.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.att.sales.framework.model.Message;
import com.att.sales.framework.model.ServiceMetaData;
import com.att.sales.framework.model.ServiceResponse;
import com.att.sales.framework.model.Status;
import com.att.sales.framework.model.constants.HttpErrorCodes;
import com.att.sales.framework.service.BaseServiceImpl;
import com.att.sales.framework.util.MessageResourcesUtil;
import com.att.sales.nexxus.common.CommonConstants;
import com.att.sales.nexxus.constant.FmoConstants;
import com.att.sales.nexxus.constant.InrConstants;
import com.att.sales.nexxus.constant.MyPriceConstants;
import com.att.sales.nexxus.constant.StringConstants;
import com.att.sales.nexxus.dao.model.NxDesignAudit;
import com.att.sales.nexxus.dao.model.NxMpDeal;
import com.att.sales.nexxus.dao.model.NxPdRequestValidation;
import com.att.sales.nexxus.dao.model.NxRequestDetails;
import com.att.sales.nexxus.dao.model.solution.NxSolutionDetail;
import com.att.sales.nexxus.dao.model.solution.NxTeam;
import com.att.sales.nexxus.dao.repository.HybridRepositoryService;
import com.att.sales.nexxus.dao.repository.NxDesignAuditRepository;
import com.att.sales.nexxus.dao.repository.NxMpDealRepository;
import com.att.sales.nexxus.dao.repository.NxPdRequestValidationRepository;
import com.att.sales.nexxus.dao.repository.NxRequestDetailsRepository;
import com.att.sales.nexxus.dao.repository.NxSolutionDetailsRepository;
import com.att.sales.nexxus.dao.repository.SalesMsDao;
import com.att.sales.nexxus.myprice.transaction.model.CreateTransactionResponse;
import com.att.sales.nexxus.myprice.transaction.service.MyPriceTransactionUtil;
import com.att.sales.nexxus.reteriveicb.model.Offer;
import com.att.sales.nexxus.reteriveicb.model.RetreiveICBPSPRequest;
import com.att.sales.nexxus.reteriveicb.model.RetreiveICBPSPResponse;
import com.att.sales.nexxus.reteriveicb.model.Solution;
import com.att.sales.nexxus.userdetails.model.UserDetails;
import com.att.sales.nexxus.userdetails.model.UserDetailsRequest;
import com.att.sales.nexxus.userdetails.model.UserDetailsResponse;
import com.att.sales.nexxus.userdetails.service.UserDetailsServiceImpl;
import com.att.sales.nexxus.util.JsonPathUtil;
import com.att.sales.nexxus.util.NexxusJsonUtility;
import com.att.sales.nexxus.util.ThreadMetaDataUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.TypeRef;

/**
 * The Class RetreiveICBPSPServiceImpl.
 */
@Service
public class RetreiveICBPSPServiceImpl extends BaseServiceImpl implements RetreiveICBPSPService {

	/** The repository. */
	@Autowired
	private NxSolutionDetailsRepository repository;

	/** The repo. */
	@Autowired
	private NxRequestDetailsRepository repo;

	/** The fmo processing service. */
	@Autowired
	private FmoProcessingService fmoProcessingService;

	/** The hybrid repository service. */
	@Autowired
	private HybridRepositoryService hybridRepositoryService;

	/** The logger. */
	private static Logger logger = LoggerFactory.getLogger(RetreiveICBPSPServiceImpl.class);

	@Autowired
	private PedSnsdService pedSnsdService;

	@Autowired
	private MyPriceTransactionUtil myPriceTransactionUtil;

	@Autowired
	private PedSnsdServiceUtil pedSnsdServiceUtil;

	@Autowired
	private NxMpDealRepository nxMpDealRepository;

	@Autowired
	private NxDesignAuditRepository nxDesignAuditRepository;

	@Autowired
	private NxPdRequestValidationRepository nxPdReqValidationRepository;
	
	@Autowired
	private ObjectMapper mapper;

	@Autowired
	private SalesMsDao salesMsDao;

	@Autowired
	private ProcessPDtoMPRestUtil processPDtoMPRestUtil;
	
	@Autowired
	private NexxusJsonUtility nexxusJsonUtility;
	
	@Autowired
	private UserDetailsServiceImpl userDetailsServiceImpl;
	
	@Autowired
	private JsonPathUtil jsonPathUtil;
	
	@Value("${ade.designStatus.path}")
	private String adeDesignStatusPath;
	
	@Value("${ase.designStatus.path}")
	private String aseDesignStatusPath;
	

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.att.sales.nexxus.service.RetreiveICBPSPService#retreiveICBPSP(com.att.
	 * sales.nexxus.reteriveicb.model.RetreiveICBPSPRequest)
	 */
	@Override
	@Transactional
	public ServiceResponse retreiveICBPSP(RetreiveICBPSPRequest request) {

		RetreiveICBPSPResponse resp = new RetreiveICBPSPResponse();
		NxSolutionDetail solnData = null;
		Date date = new Date();
		JsonNode request1 = mapper.valueToTree(request);
		logger.info("RetreiveICBPSPRequest: {}", request1);
	//	JsonNode offerss = request1.at("/solution/offers");
		// starting validation logic
		Long currentTime = System.currentTimeMillis();
		Long startTime = System.currentTimeMillis() - currentTime;
		//JsonPathUtil jsonPathUtil = new JsonPathUtil();
		String product = request.getSolution().getBundleCode();// getJsonValue(request,"$.solution.bundleCode");ipne
		/*String offerId = null;
		if (Optional.ofNullable(request.getSolution()).isPresent()
				&& Optional.ofNullable(request.getSolution().getOffers()).isPresent()) {
			List<Offer> offers=request.getSolution().getOffers();
			
			for (Offer offerElement : offers) {
				offerId = offerElement.getOfferId();
			    if(StringConstants.IPNE.equalsIgnoreCase(product) && StringConstants.OFFERID_EPLS.equalsIgnoreCase(offerId)) {	
				      resp = requestValidation(request,StringConstants.EPLS);
				      if(resp != null && resp.getStatus() != null ) {    
						    return resp;
					   }
			   }
			    
			}			
	    }
		
		for(int i =0; i< offerss.size(); i++) { 
			ArrayNode arrayNode = (ArrayNode) offerss;
			JsonNode value = arrayNode.get(i);
			String offererIdValue= value.get("offerId").asText();
			 if("210".equals(offererIdValue)){
				 arrayNode.remove(i); 
			 }
		}
	  
		RetreiveICBPSPRequest req =  mapper.convertValue(request1, RetreiveICBPSPRequest.class);	*/
		
		   resp = requestValidation(request,product);
		    
		if(resp != null && resp.getStatus() != null ) {    
		    return resp;
		}

		Long endTime = System.currentTimeMillis() - currentTime;
		logger.info("End : of pricerD request validation and total duration {}, for external key {}",
				(endTime - startTime), request.getSolution().getExternalKey());
		// ending validation logic
		Solution solution = request.getSolution();
		String automationInd = null != request.getSolution() ? request.getSolution().getAutomationInd() : null;
		String sourceName = null != request.getSolution() ? request.getSolution().getSourceName() : null;
		if (request != null && ("Y".equalsIgnoreCase(automationInd) || checkOffer(request))) {
			Long pricerDSolnId = request.getSolution().getPricerDSolutionId();
			if (pricerDSolnId != null) {
				List<NxSolutionDetail> nxSolnList = repository
						.findByExternalKey(request.getSolution().getPricerDSolutionId());
				if (!CollectionUtils.isEmpty(nxSolnList)) {
					solnData = nxSolnList.get(0);
				}
				
				/**
				 * check if solution is in progress return
				 */
				if(solnData!=null) {
 					if(StringConstants.SOLUTION_IN_PROGRESS.equalsIgnoreCase(solnData.getPdStatusInd())) {
						setErrorResponse(resp, StringConstants.PREVIOUS_REQUEST_IS_IN_PROGRESS);
						return resp;
					}
				}
			
				boolean automationFLowInd = "Y".equalsIgnoreCase(request.getSolution().getAutomationInd());
				if (solnData == null || solnData.getExternalKey() == null) {
					solnData = new NxSolutionDetail();
					solnData.setOptyId(request.getSolution().getOptyId());
					solnData.setCreatedDate(date);
					solnData.setModifiedDate(date);
					solnData.setActiveYn("Y");
					solnData.setNxsDescription("[AUTOMATION:" + request.getSolution().getPricerDSolutionId() + "]");
					solnData.setCreatedUser(request.getSolution().getUserId());
					solnData.setExternalKey(request.getSolution().getPricerDSolutionId());
					solnData.setStandardPricingInd(request.getSolution().getStandardPricingInd());
					solnData.setFlowType(StringConstants.FLOW_TYPE_AUTO);
					if(request != null && StringConstants.IPNE.equalsIgnoreCase(sourceName)){
						solnData.setFlowType(StringConstants.SALES_IPNE);
					}

					solnData.setAutomationFlowInd(automationFLowInd ? "Y" : "N");
					String restVersion=getRestVersion(request);
					solnData.setRestVersion(restVersion);
					// check if only 3pa
					boolean is3paRequest = false;
					if("ASEoD".equalsIgnoreCase(product)) {
						int noOfSites = request.getSolution().getOffers().get(0).getSite().size();
						int noOf3paSites = (int)request.getSolution().getOffers().get(0).getSite().stream().filter(n->"Y".equalsIgnoreCase(n.getThirdPartyInd())).count();
						if(noOfSites==noOf3paSites) {
							is3paRequest = true;
						}
					}
					if(is3paRequest) {
						solnData.setSolutionVersion(0L);
						solution.setSolutionVersion(0L);
					}else {
						solnData.setSolutionVersion(1L);
						solution.setSolutionVersion(1L);
					}
					solnData.setPdStatusInd(StringConstants.SOLUTION_IN_PROGRESS);
					solnData.setSlcInd(StringConstants.CONSTANT_Y);
					repository.saveAndFlush(solnData);
					request.setSolution(solution);
					List<NxTeam> nxTeamList = new ArrayList<>();
					NxTeam nxTeam = new NxTeam();
					List<NxTeam> listOfNxTeam = hybridRepositoryService.getNxTeamList(request.getSolution().getUserId(),
							solnData);

					if (!listOfNxTeam.isEmpty()) {
						nxTeam = listOfNxTeam.get(0);
					}
					nxTeam.setNxSolutionDetail(solnData);
					nxTeam.setAttuid(request.getSolution().getUserId());
					nxTeam.setfName(request.getSolution().getUserFirstName());
					nxTeam.setlName(request.getSolution().getUserLastName());
					nxTeam.setManagerHrid(request.getSolution().getUserManagerATTUID());
					nxTeam.setIsPryMVG("Y");
					nxTeamList.add(nxTeam);
					hybridRepositoryService.setNxTeamList(nxTeamList);
					
					//adding sales team user details to nx_team table
					if(null != request.getSolution().getOpportunitySalesTeam() && !request.getSolution().getOpportunitySalesTeam().isEmpty()) {
						for(String userId:request.getSolution().getOpportunitySalesTeam())	{
							List<NxTeam> nxTeamListObj = new ArrayList<>();
							NxTeam nxTeamObj = new NxTeam();
							List<NxTeam> listOfNxTeamObj = hybridRepositoryService.getNxTeamList(userId,
									solnData);

							if (!listOfNxTeamObj.isEmpty()) {
								nxTeamObj = listOfNxTeamObj.get(0);
							}
							UserDetailsRequest userDetailsRequest = new UserDetailsRequest();
							userDetailsRequest.setAttuid(userId);
							UserDetailsResponse response = (UserDetailsResponse) userDetailsServiceImpl.retreiveUserDetails(userDetailsRequest);
							List<UserDetails> userDetails = response.getUserDetails();
							if (CollectionUtils.isNotEmpty(userDetails) && null != userDetails.get(0)) {
								nxTeamObj.setNxSolutionDetail(solnData);
								nxTeamObj.setAttuid(userDetails.get(0).getAttuid());
								nxTeamObj.setfName(userDetails.get(0).getFirstName());
								nxTeamObj.setlName(userDetails.get(0).getLastName());
								nxTeamObj.setEmail(userDetails.get(0).getEmailId());
								nxTeamObj.setManagerHrid(userDetails.get(0).getManagerAttuid());							
								nxTeamListObj.add(nxTeamObj);	
							}
							hybridRepositoryService.setNxTeamList(nxTeamListObj);
						}
					}
				}else {
					boolean is3paRequest = false;
					boolean designUpdateForReopen = false;
					boolean isSubmittoPed =false;
					Set<Object> designStatusIndicators = null;
					String solutionStatus = Optional.ofNullable(request.getSolution()).map(Solution::getSolutionStatus)
							.orElse(null);
					List<NxMpDeal> nxMpDealDetails = nxMpDealRepository.getActivePricerDDeals(solnData.getNxSolutionId(),
							CommonConstants.ACTIVE_Y);
					String isReconfigure = null;
					String isPedSuccess = null;
					if(CollectionUtils.isNotEmpty(nxMpDealDetails)){
						 isReconfigure = nxMpDealDetails.get(0).getNxMpStatusInd(); // if Y then reconfigure else retrigger myprice
						 isPedSuccess = nxMpDealDetails.get(0).getNxPedStatusInd(); // if Y then reconfigure else retrigger ped
					}
					
					if(StringConstants.IPNE.equalsIgnoreCase(request.getSolution().getSourceName())) {
						isSubmittoPed = StringConstants.CONSTANT_Y.equalsIgnoreCase(request.getSolution().getSubmitToMyprice());
						designStatusIndicators = getValuesFromRequest(request.getSolution().getOffers(), adeDesignStatusPath);
					}
					else if("ASE".equalsIgnoreCase(product)||"ASEoD".equalsIgnoreCase(product)) {
						designStatusIndicators = getValuesFromRequest(request.getSolution().getOffers().get(0), aseDesignStatusPath);
						// check if only 3pa
						if("ASEoD".equalsIgnoreCase(product)) {
							int noOfSites = request.getSolution().getOffers().get(0).getSite().size();
							int noOf3paSites = (int)request.getSolution().getOffers().get(0).getSite().stream().filter(n->"Y".equalsIgnoreCase(n.getThirdPartyInd())).count();
							if(noOfSites==noOf3paSites) {
								is3paRequest = true;
							}
						}
					}    
					else if("ADE".equalsIgnoreCase(product) ) {
						designStatusIndicators = getValuesFromRequest(request.getSolution().getOffers().get(0), adeDesignStatusPath);
					}
					designUpdateForReopen = checkDesignStatusForR(designStatusIndicators);
					
					long solutionVersion = solnData.getSolutionVersion()==0L?1L:solnData.getSolutionVersion();
					if (is3paRequest) {
						solutionVersion = solnData.getSolutionVersion();
					}
					else if(StringConstants.IPNE.equalsIgnoreCase(request.getSolution().getSourceName()) && !(isSubmittoPed) && 
							!StringConstants.CONSTANT_N.equalsIgnoreCase(isReconfigure) &&  !StringConstants.CONSTANT_N.equalsIgnoreCase(isPedSuccess) 
								&& !("U".equalsIgnoreCase(solutionStatus) && designUpdateForReopen) && !("S".equalsIgnoreCase(solutionStatus))
								&& !("C".equalsIgnoreCase(solutionStatus))) {
						solutionVersion = solutionVersion+1;
					}
					else if(StringConstants.CONSTANT_Y.equalsIgnoreCase(request.getSolution().getStandardPricingInd()) &&
							 !StringConstants.CONSTANT_N.equalsIgnoreCase(isPedSuccess) 
									&& !("U".equalsIgnoreCase(solutionStatus) && designUpdateForReopen) && !("S".equalsIgnoreCase(solutionStatus))
									&& !("C".equalsIgnoreCase(solutionStatus))
									&& !is3paRequest){
						solutionVersion = solutionVersion+1;
					}
					else if(!(StringConstants.IPNE.equalsIgnoreCase(request.getSolution().getSourceName())) && 
							!StringConstants.CONSTANT_N.equalsIgnoreCase(isReconfigure) &&  !StringConstants.CONSTANT_N.equalsIgnoreCase(isPedSuccess) 
									&& !("U".equalsIgnoreCase(solutionStatus) && designUpdateForReopen) && !("S".equalsIgnoreCase(solutionStatus))
									&& !("C".equalsIgnoreCase(solutionStatus))
									&& !is3paRequest) {
						solutionVersion = solutionVersion+1;
					}
					solnData.setSolutionVersion(solutionVersion);
					solution.setSolutionVersion(solutionVersion);
					solnData.setSlcInd(StringConstants.CONSTANT_Y);
					repository.saveAndFlush(solnData);
					request.setSolution(solution);
					//if solution is already there, mark it as in progress
					updateNxSolPdStausInd(StringConstants.SOLUTION_IN_PROGRESS,solnData.getNxSolutionId());
				}
				
				pedSnsdServiceUtil.saveSolutionData(request, solnData.getNxSolutionId());
				resp.setNxSolutionId(solnData.getNxSolutionId());

				Map<String, Object> paramMap = new HashMap<String, Object>();
				paramMap.put(StringConstants.REST_VERSION, solnData.getRestVersion());
				Map<String, Object> requestMetaDataMap = new HashMap<>();
				if (ServiceMetaData.getRequestMetaData() != null) {
					ServiceMetaData.getRequestMetaData().forEach((key, value) -> requestMetaDataMap.put(key, value));
					paramMap.put(InrConstants.REQUEST_META_DATA_KEY, requestMetaDataMap);
				}
				Map<String, Object> createAndUpdateStatus = new HashMap<String, Object>();
				CreateTransactionResponse createTransactionResponse = null;

				paramMap.put(MyPriceConstants.AUTOMATION_IND, automationFLowInd);
				createAndUpdateStatus.put(MyPriceConstants.AUTOMATION_IND, automationFLowInd);
				paramMap.put(MyPriceConstants.SOURCE, MyPriceConstants.SOURCE_PD);
				
				paramMap.put("RequestSolution", request.getSolution());
				/*
				 * Need to check for respective solution any entry is there in NxMpDeal table.
				 */
				List<NxMpDeal> nxMpDeal = nxMpDealRepository.getActivePricerDDeals(solnData.getNxSolutionId(),
						CommonConstants.ACTIVE_Y);

				NxDesignAudit nxDesignAudit = nxDesignAuditRepository
						.findByNxRefIdAndTransaction(solnData.getNxSolutionId(), MyPriceConstants.AUDIT_CREATE);
				Boolean callPedService=StringConstants.IPNE.equalsIgnoreCase(request.getSolution().getSourceName()) && StringConstants.CONSTANT_Y.equalsIgnoreCase(request.getSolution().getSubmitToMyprice());
				/*
				 * String standardPricingInd = null !=
				 * request.getSolution().getStandardPricingInd() ?
				 * request.getSolution().getStandardPricingInd() : null;
				 * paramMap.put(MyPriceConstants.STANDART_PRICING_IND, standardPricingInd);
				 */
				//if (standardPricingInd.equals("Y")) {
				if ((StringConstants.CONSTANT_Y.equalsIgnoreCase(request.getSolution().getStandardPricingInd()))
						|| (StringConstants.IPNE.equalsIgnoreCase(request.getSolution().getSourceName()) 
								&&  !StringConstants.CONSTANT_Y.equalsIgnoreCase(request.getSolution().getSubmitToMyprice()))) { 
					//Log.info("skipping the my price call");
					logger.info("skipping the my price call");
					
					paramMap.put(StringConstants.INVOKE_MYPRICE, false);
					/*
					 * CreateTransactionResponse createTransaction = new
					 * CreateTransactionResponse(); createTransaction.setMyPriceTransacId(null);
					 * createTransaction.setNxTransacId(null);
					 * createTransaction.setPriceScenarioId(request.getSolution().getPriceScenarioId
					 * ());
					 */
					RetreiveICBPSPRequest icbpspRequest = request;
					NxSolutionDetail solData = solnData;
					CompletableFuture.supplyAsync(() -> {
						Boolean result = false;
						try {
							ThreadMetaDataUtil.initThreadMetaData(requestMetaDataMap);
							myPriceTransactionUtil.saveNxDesignAudit(solData.getNxSolutionId(), MyPriceConstants.AUDIT_IPE_TRACE);
							result = pedSnsdServiceUtil.saveDesignData(icbpspRequest, solData, null,
									paramMap);
						} catch(Exception e) {
							logger.info("exception in ipne flow ", e);
						} finally {
							ThreadMetaDataUtil.destroyThreadMetaData();
						}
						return result;
					
				
						
					}).thenAccept(result -> {
						try {
							ThreadMetaDataUtil.initThreadMetaData(requestMetaDataMap);
							if (result  ) {
								paramMap.remove(MyPriceConstants.RESPONSE_STATUS);
								
								pedSnsdService.process(icbpspRequest, solData, paramMap);
							}
							updateNxSolPdStausInd(StringConstants.SOLUTION_COMPLETED,solData.getNxSolutionId());
						}catch(Exception e) {
							logger.info("exception in ipne flow ", e);
						} finally {
							ThreadMetaDataUtil.destroyThreadMetaData();
						}
					});

				} else if (CollectionUtils.isEmpty(nxMpDeal)
						|| (!CollectionUtils.isEmpty(nxMpDeal) && null != nxDesignAudit
								&& MyPriceConstants.AUDIT_CREATE.contains(nxDesignAudit.getTransaction())
								&& CommonConstants.FAILURE.equalsIgnoreCase(nxDesignAudit.getStatus()))) {
					/*
					 * Since its completely new transaction then we will call create and update
					 * transaction.
					 * 
					 * or
					 * 
					 * if retrigger with create transaction failed
					 */
					Long nxTxnId = !CollectionUtils.isEmpty(nxMpDeal) ? nxMpDeal.get(0).getNxTxnId() : null;
					
					// create and update transaction in myprice
					createAndUpdateStatus.put(MyPriceConstants.FLOW_TYPE, "pricerD");

				//	createAndUpdateStatus.put(MyPriceConstants.STANDART_PRICING_IND, standardPricingInd);
					createAndUpdateStatus = myPriceTransactionUtil.createAndUpdateTransc(request, solnData,
							createAndUpdateStatus, nxTxnId);
					paramMap.put(StringConstants.TRANSACTION_TYPE, StringConstants.TRANSACTION_TYPE_NEW);

					Boolean status = (Boolean) createAndUpdateStatus.get("status");
					if (status) {
						createTransactionResponse = (CreateTransactionResponse) createAndUpdateStatus
								.get("createTransactionResponse");
						if (createTransactionResponse != null && createTransactionResponse.getDealID() != null)
							request.getSolution()
									.setImsDealNumber(Long.parseLong(createTransactionResponse.getDealID()));
						if (createTransactionResponse != null && createTransactionResponse.getVersion() != null)
							request.getSolution()
									.setImsVersionNumber(Long.parseLong(createTransactionResponse.getVersion()));
						if (createTransactionResponse != null
								&& createAndUpdateStatus.get("retreiveICBPSPRequest") != null)
							request = (RetreiveICBPSPRequest) createAndUpdateStatus.get("retreiveICBPSPRequest");
						paramMap.put(MyPriceConstants.NX_AUDIT_ID,
								createAndUpdateStatus.get(MyPriceConstants.NX_AUDIT_ID));
						RetreiveICBPSPRequest retreiveICBPSPRequest = request;
						// thread to execute pedsnsd service
						NxSolutionDetail soln = solnData;

						CreateTransactionResponse createTransaction = createTransactionResponse;

						CompletableFuture.supplyAsync(() -> {
							Boolean result = false;
							try {
								ThreadMetaDataUtil.initThreadMetaData(requestMetaDataMap);
								result = pedSnsdServiceUtil.saveDesignData(retreiveICBPSPRequest, soln,
										createTransaction, paramMap);
								if (result && !automationFLowInd) {
									logger.info("Processing for multiple price scenarios");
									paramMap.put(StringConstants.MY_PRICE_TRANS_ID,
											createTransaction.getMyPriceTransacId());
									paramMap.put(StringConstants.NX_TRANS_ID, createTransaction.getNxTransacId());
									myPriceTransactionUtil.processMutliPriceScenario(retreiveICBPSPRequest, soln, paramMap);
									result = paramMap.containsKey(MyPriceConstants.RESPONSE_STATUS)
											? (boolean) paramMap.get(MyPriceConstants.RESPONSE_STATUS)
											: true;
								}
							} catch (Exception e) {
								logger.info("new transaction or retrigger worker threads exception", e);
							} finally {
								ThreadMetaDataUtil.destroyThreadMetaData();
							}
							return result;
						}).thenAccept(result -> {
							try {
								ThreadMetaDataUtil.initThreadMetaData(requestMetaDataMap);
								 if (result && !callPedService) {
									paramMap.remove(MyPriceConstants.RESPONSE_STATUS);
									pedSnsdService.process(retreiveICBPSPRequest, soln, paramMap);
								}
								updateNxSolPdStausInd(StringConstants.SOLUTION_COMPLETED,soln.getNxSolutionId());
							} catch (Exception e) {
								logger.info("new transaction or retrigger worker threads exception", e);
							} finally {
								ThreadMetaDataUtil.destroyThreadMetaData();
							}
						});
					}
					else {
						updateNxSolPdStausInd(StringConstants.SOLUTION_COMPLETED,solnData.getNxSolutionId());
					}
				} else {
					/**
					 * if transaction exist, check if nx_mp_status_ind = 'Y' then its reconfigure
					 * else if nx_mp_status_ind = N check if update is failure call copy and clean
					 * save else execute reconfigure copy override remove transaction config soln ,
					 * design and pricing
					 */
					String isReconfigure = nxMpDeal.get(0).getNxMpStatusInd(); // if Y then reconfigure else retrigger
																				// myprice
					String isPedSuccess = nxMpDeal.get(0).getNxPedStatusInd(); // if Y then reconfigure else retrigger
																				// ped
					/*
					 * if record exist, we need to identify whether its price update or design
					 * update or add site or delete site
					 * 
					 * if its design update then get
					 * 
					 * if its price update then get price scenario list with price update
					 * 
					 * if its add site
					 * 
					 * if its delete site
					 */
					if (StringConstants.CONSTANT_N.equalsIgnoreCase(isReconfigure)) {
						nxDesignAudit = nxDesignAuditRepository.findTopByNxRefIdOrderByNxAuditIdDesc(solnData.getNxSolutionId());
						if (MyPriceConstants.AUDIT_UPDATE_CS.equalsIgnoreCase(nxDesignAudit.getTransaction())) {
							paramMap.put("CALL_CLEAN_SAVE", StringConstants.CONSTANT_Y);
						}
					}
					paramMap.put("IS_RECONFIGURE", isReconfigure);
					paramMap.put("IS_PED_SUCCESS", isPedSuccess);
					paramMap.put(StringConstants.TRANSACTION_TYPE, StringConstants.TRANSACTION_TYPE_EXISTING);
					paramMap.put("retreiveICBPSPRequest", request);

					RetreiveICBPSPRequest retreiveICBPSPReq = request;
					NxSolutionDetail solData = solnData;
					
					// Handle is design status C is coming in IPNE Project
					Boolean isCancelDesignStatus = false;
					if("ipne".equalsIgnoreCase(request.getSolution().getSourceName()) && request.getSolution().getSolutionStatus().equalsIgnoreCase("C")) {
										//Boolean isCancelDesignStatus = false; 
//										for(Offer offr : request.getSolution().getOffers()) {
//											  if(offr.getCircuit().stream().anyMatch(r -> "C".equalsIgnoreCase(r.getDesignStatus()))) {
											    	isCancelDesignStatus = true;
//											    }	
//										}
//									
										paramMap.put("designStatus", isCancelDesignStatus);
					}
					
					Boolean isCancelD = isCancelDesignStatus;
					
					CompletableFuture.supplyAsync(() -> {
						boolean res = false;
						try {
							ThreadMetaDataUtil.initThreadMetaData(requestMetaDataMap);
							myPriceTransactionUtil.reconfigureScenarios(retreiveICBPSPReq, nxMpDeal, paramMap);
							boolean status = paramMap.containsKey(MyPriceConstants.RESPONSE_STATUS)
									? (boolean) paramMap.get(MyPriceConstants.RESPONSE_STATUS)
									: true;
							res = status;
							paramMap.remove(MyPriceConstants.RESPONSE_STATUS);
							if (status) {
								CreateTransactionResponse createTransactionResp = (CreateTransactionResponse) paramMap
										.get("createTransactionResponse");
								if (createTransactionResp != null && createTransactionResp.getDealID() != null)
									retreiveICBPSPReq.getSolution()
											.setImsDealNumber(Long.parseLong(createTransactionResp.getDealID()));
								if (createTransactionResp != null && createTransactionResp.getVersion() != null)
									retreiveICBPSPReq.getSolution()
											.setImsVersionNumber(Long.parseLong(createTransactionResp.getVersion()));
								RetreiveICBPSPRequest retreiveICBPSPRequest = retreiveICBPSPReq;
								NxSolutionDetail soln = solData;
								boolean invokeMyprice = (boolean) paramMap.get(StringConstants.INVOKE_MYPRICE);
								CreateTransactionResponse createTransaction = createTransactionResp;
								Boolean result = pedSnsdServiceUtil.saveDesignData(retreiveICBPSPRequest, soln,
										createTransaction, paramMap);
								
								if (result) {
									if (invokeMyprice && !automationFLowInd) {
										logger.info("Processing for multiple price scenarios");
										paramMap.put(StringConstants.MY_PRICE_TRANS_ID,
												createTransaction.getMyPriceTransacId());
										paramMap.put(StringConstants.NX_TRANS_ID, createTransaction.getNxTransacId());
										myPriceTransactionUtil.processMutliPriceScenario(retreiveICBPSPRequest, soln,
												paramMap);
										result = paramMap.containsKey(MyPriceConstants.RESPONSE_STATUS)
												? (boolean) paramMap.get(MyPriceConstants.RESPONSE_STATUS)
												: true;
									} else {
										logger.info(
												"Design Status is R OR solution status S :: Multiple price scenario is not invoked");
									}
								}
								res = result;
							}
						} catch (Exception e) {
							logger.info("existing transaction worker threads exception", e);
						} finally {
							ThreadMetaDataUtil.destroyThreadMetaData();
						}
						return res;
					}).thenAccept(result -> {
						try {
							ThreadMetaDataUtil.initThreadMetaData(requestMetaDataMap);
							
							if ((result && !callPedService) || (result && isCancelD && callPedService)) {//if the result fail no need to call ped if status c is true also
								boolean callPedForDesignUpdate = (boolean) paramMap
										.get(StringConstants.CALLPEDFORDESIGNUPDATE);
								paramMap.remove(MyPriceConstants.RESPONSE_STATUS);
								if (callPedForDesignUpdate) {
									logger.info("Calling pedSnsdService for processing the solution {} ",
											solData.getNxSolutionId());
									
									pedSnsdService.process(retreiveICBPSPReq, solData, paramMap);
								}
							}
							updateNxSolPdStausInd(StringConstants.SOLUTION_COMPLETED,solData.getNxSolutionId());
						} catch (Exception e) {
							logger.info("existing transaction worker threads exception", e);
						} finally {
							ThreadMetaDataUtil.destroyThreadMetaData();
						}
					});

				}
			}
		} else {
			Long externalId = null != request.getSolution() ? request.getSolution().getSolutionId() : null;
			logger.info("Entered retreiveICBPSP() method for External id: {} ", externalId);

			Map<String, Object> requestMap = new HashMap<>();
			requestMap.put(FmoConstants.CALL_OPTYINFO, FmoConstants.NO);

			/*
			 * if (request.getSolution().getSolutionId() != null) { List<NxSolutionDetail>
			 * nxSolnList =
			 * repository.findByExternalKey(request.getSolution().getSolutionId()); if
			 * (!CollectionUtils.isEmpty(nxSolnList)) { // Pick the first soln as only 1 is
			 * expected solnData = nxSolnList.get(0); } }
			 */

			if (solnData == null || solnData.getExternalKey() == null) {
				requestMap.put(FmoConstants.CALL_OPTYINFO, FmoConstants.YES);
				solnData = new NxSolutionDetail();
				solnData.setOptyId(request.getSolution().getOptyId());
				solnData.setCreatedDate(date);
				solnData.setModifiedDate(date);
				solnData.setActiveYn("Y");
				solnData.setNxsDescription("[ASAP:" + request.getSolution().getSolutionId() + "]");
				solnData.setCreatedUser(request.getSolution().getUserId());
				solnData.setExternalKey(request.getSolution().getSolutionId());
				solnData.setFlowType("FMO");
				repository.save(solnData);
			}

			requestMap.put("optyId", request.getSolution().getOptyId());
			requestMap.put("attuid", request.getSolution().getUserId());
			requestMap.put("nxSolutionId", solnData.getNxSolutionId());
			requestMap.put("action", "updateSolution");
			requestMap.put("flowType", "FMO");
			logger.info("Inside  retreiveICBPSP for  solutionId id: {} ", solnData.getNxSolutionId());

			NxRequestDetails reqDetails = new NxRequestDetails();
			reqDetails.setNxSolutionDetail(solnData);
			reqDetails.setCreatedDate(date);
			reqDetails.setFlowType(StringConstants.FLOW_TYPE_FMO);
			reqDetails.setUser(request.getSolution().getUserId());
			reqDetails.setActiveYn(StringConstants.CONSTANT_Y);
			repo.save(reqDetails);
			logger.info("Inside  retreiveICBPSP  Nexus Request details is created for  request id: {} ",
					reqDetails.getNxReqId());

			resp.setNxSolutionId(solnData.getNxSolutionId());

			Map<String, Object> paramMap = new HashMap<String, Object>();
			Map<String, Object> requestMetaDataMap = new HashMap<>();
			if (ServiceMetaData.getRequestMetaData() != null) {
				ServiceMetaData.getRequestMetaData().forEach((key, value) -> requestMetaDataMap.put(key, value));
				paramMap.put(InrConstants.REQUEST_META_DATA_KEY, requestMetaDataMap);
				requestMap.put(InrConstants.REQUEST_META_DATA_KEY, requestMetaDataMap);
			}
			Map<String, Object> createAndUpdateStatus = new HashMap<String, Object>();
			CreateTransactionResponse createTransactionResponse = null;

			/*
			 * Need to check for respective solution any entry is there in NxMpDeal table.
			 */
			List<NxMpDeal> nxMpDeal = nxMpDealRepository.findBySolutionIdAndActiveYN(solnData.getNxSolutionId(),
					CommonConstants.ACTIVE_Y);

			NxDesignAudit nxDesignAudit = nxDesignAuditRepository
					.findByNxRefIdAndTransaction(solnData.getNxSolutionId(), MyPriceConstants.AUDIT_CREATE);

			if (CollectionUtils.isEmpty(nxMpDeal) || (!CollectionUtils.isEmpty(nxMpDeal) && null != nxDesignAudit
					&& MyPriceConstants.AUDIT_CREATE.contains(nxDesignAudit.getTransaction())
					&& CommonConstants.FAILURE.equalsIgnoreCase(nxDesignAudit.getStatus()))) {
				/*
				 * Since its completely new transaction then we will call create and update
				 * transaction.
				 * 
				 * or
				 * 
				 * if re-trigger with create transaction failed
				 */
				Long nxTxnId = !CollectionUtils.isEmpty(nxMpDeal) ? nxMpDeal.get(0).getNxTxnId() : null;

				// create and update transaction in myprice
				createAndUpdateStatus.put(MyPriceConstants.FLOW_TYPE, MyPriceConstants.SOURCE_FMO);
				createAndUpdateStatus.put("currentNxSolutionId", solnData.getNxSolutionId());

				createAndUpdateStatus = myPriceTransactionUtil.createAndUpdateTransc(request, solnData,
						createAndUpdateStatus, nxTxnId);
				paramMap.put(StringConstants.TRANSACTION_TYPE, StringConstants.TRANSACTION_TYPE_NEW);

				Boolean status = (Boolean) createAndUpdateStatus.get("status");
				if (status) {
					createTransactionResponse = (CreateTransactionResponse) createAndUpdateStatus
							.get("createTransactionResponse");
					if (createTransactionResponse != null && createTransactionResponse.getDealID() != null)
						request.getSolution().setImsDealNumber(Long.parseLong(createTransactionResponse.getDealID()));
					if (createTransactionResponse != null && createTransactionResponse.getVersion() != null)
						request.getSolution()
								.setImsVersionNumber(Long.parseLong(createTransactionResponse.getVersion()));
					if (createTransactionResponse != null && createAndUpdateStatus.get("retreiveICBPSPRequest") != null)
						request = (RetreiveICBPSPRequest) createAndUpdateStatus.get("retreiveICBPSPRequest");
					if (createTransactionResponse != null && createTransactionResponse.getNxTransacId() != null) {
						requestMap.put(MyPriceConstants.NX_TRANSACTION_ID, createTransactionResponse.getNxTransacId());
					}
					requestMap.put(MyPriceConstants.NX_AUDIT_ID,
							createAndUpdateStatus.get(MyPriceConstants.NX_AUDIT_ID));

					RetreiveICBPSPRequest retreiveICBPSPRequest = request;
					paramMap.put(MyPriceConstants.SOURCE, MyPriceConstants.SOURCE_FMO);
					/*
					 * For FMO flow, please find below steps 1. Create Transaction 2. Update clean
					 * save 3. fmo output generation below steps will be performed if fmo output
					 * generation is success for each site / design 4. saving data to db 5. config
					 * solution 6. config design 7. price update
					 */
					CompletableFuture.runAsync(() -> {
						try {
							ThreadMetaDataUtil.initThreadMetaData(requestMetaDataMap);
							fmoProcessingService.createFmoNexxusOutput(reqDetails, retreiveICBPSPRequest, requestMap);
						} catch (Exception e) {
							logger.info("Exception", e);
						} finally {
							ThreadMetaDataUtil.destroyThreadMetaData();
						}
					});

				}
			}
			resp.setNxRequestId(reqDetails.getNxReqId());

		}

		logger.info("Exiting retreiveICBPSP() method");
		setSuccessResponse(resp);
		return resp;
	}

	private RetreiveICBPSPResponse requestValidation(RetreiveICBPSPRequest request, String product) { //akash arya 
		
		RetreiveICBPSPResponse resp = new RetreiveICBPSPResponse();
		JsonPathUtil jsonPathUtil = new JsonPathUtil();
		String offerId = null;
		 List<NxPdRequestValidation> validations = new ArrayList<>();
	        
			if ("ASE".equalsIgnoreCase(product)||"ASEoD".equalsIgnoreCase(product)||"ADE".equalsIgnoreCase(product) ) {
				List<NxPdRequestValidation> Validation1 = nxPdReqValidationRepository.fetchAllValidation(product);
				validations.addAll(Validation1);
				
			}else if ("IPNE".equalsIgnoreCase(product)) {
				
				if (Optional.ofNullable(request.getSolution()).isPresent()
						&& Optional.ofNullable(request.getSolution().getOffers()).isPresent()) {
					List<Offer> offers1=request.getSolution().getOffers();
					  
					for (Offer offerElement : offers1) {
						offerId = offerElement.getOfferId();
						if(StringConstants.OFFERID_ADE.equalsIgnoreCase(offerId)) {
							List<NxPdRequestValidation> Validation1 = nxPdReqValidationRepository.fetchAllValidation(StringConstants.ADE);
							validations.addAll(Validation1);
						}
						else if(StringConstants.OFFERID_EPLS.equalsIgnoreCase(offerId)) {				
							List<NxPdRequestValidation> Validation2 = nxPdReqValidationRepository.fetchAllValidation(StringConstants.EPLS);
							validations.addAll(Validation2);
						}
					}			
					
			}
			}
		

		for (NxPdRequestValidation validation : validations) {
			if (validation.getDataType().equals("String")) {
				String status = getJsonValue(request, validation.getJsonPath());

				if (Strings.isNullOrEmpty(status)) {
					resp = getValidationMsg(validation.getErrorMsg());
					return resp;
				}
			} else if (validation.getDataType().equals("Array")) {
				TypeRef<List<Object>> arrayType = new TypeRef<List<Object>>() {
				};
				List<Object> result = jsonPathUtil.search(request, validation.getJsonPath(), arrayType);
				if (result.size() == 0) {
					resp = getValidationMsg(validation.getErrorMsg());
					return resp;
				}
				if (!Strings.isNullOrEmpty(validation.getSubJsonPath())) {
					LinkedHashMap<String, Object> criteriaMap = (LinkedHashMap<String, Object>) nexxusJsonUtility
							.convertStringJsonToMap(validation.getSubJsonPath());
					for (Map.Entry<String, Object> x : criteriaMap.entrySet()) {
						if (x.getValue() instanceof Map<?, ?>) {
							Map<String, String> mapData = (Map<String, String>) x.getValue();
							if(mapData.containsKey("dataType") && ("Array".equalsIgnoreCase(mapData.get("dataType")))){
								for (Object obj : result) {
									List<Object> arrayResult = jsonPathUtil.search(obj, mapData.get("path"), arrayType);
									if (arrayResult.size() == 0) {
										resp = getValidationMsg(validation.getErrorMsg());
										return resp;
									}
								}
							}else {
								List<Object> subresult = jsonPathUtil.search(request, mapData.get("path").trim(), arrayType);
								if (result.size() != subresult.size()) {
									resp = getValidationMsg(validation.getErrorMsg());
									return resp;
								}
							}
						} 
					}
				}
			}
		}
		
		return resp;
	}
	
	
	public Set<Object> getValuesFromRequest(Object request, String path) {
		if(request != null) {
			TypeRef<List<Object>> mapType = new TypeRef<List<Object>>() {};
			List<Object> results = jsonPathUtil.search(request, path, mapType);
			if(CollectionUtils.isEmpty(results)) {
				return null;
			} else {
				return new HashSet<Object>(results);
			}
		}
		return null;
	}

	private String getJsonValue(RetreiveICBPSPRequest request, String jsonPath) {
		String product = "";
		try {
			product = JsonPath.read(new ObjectMapper().writeValueAsString(request), jsonPath);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}

		return product;
	}
	
	public boolean checkDesignStatusForR(Set<Object> designStatusIndicators) {
		if(CollectionUtils.isNotEmpty(designStatusIndicators) && designStatusIndicators.contains(StringConstants.DESIGN_REOPEN)) {
			return true;
		}
		return false;
	}

	private boolean checkOffer(RetreiveICBPSPRequest request) {
		if (Optional.ofNullable(request.getSolution()).isPresent()
				&& Optional.ofNullable(request.getSolution().getOffers()).isPresent()) {
			for (Offer offer : request.getSolution().getOffers()) {
				if ("103".equalsIgnoreCase(offer.getOfferId()) || "120".equalsIgnoreCase(offer.getOfferId())
						|| "6".equalsIgnoreCase(offer.getOfferId())|| "210".equalsIgnoreCase(offer.getOfferId())) {
					return true;
				}
			}
		}
		return false;
	}

	

	private RetreiveICBPSPResponse getValidationMsg(String message) {
		RetreiveICBPSPResponse response = new RetreiveICBPSPResponse();
		Status s = new Status();
		List<Message> msgList = new ArrayList<>();
		Message msg = new Message();
		msg.setDescription(message);
		msg.setDetailedDescription(message);
		msg.setCode("M00004");
		msgList.add(msg);
		s.setCode("400");
		s.setMessages(msgList);
		response.setStatus(s);
		return response;
	}

	private String getRestVersion(RetreiveICBPSPRequest retreiveICBPSPRequest) {
		if (Optional.ofNullable(retreiveICBPSPRequest.getSolution()).isPresent()
				&& Optional.ofNullable(retreiveICBPSPRequest.getSolution().getOffers()).isPresent()) {
			List<Offer> offers=retreiveICBPSPRequest.getSolution().getOffers();
			String offerName=null;
			for (Offer offerElement : offers) {
				String offerId = offerElement.getOfferId();
				if (StringUtils.isNotEmpty(offerId)) {
					int id = Integer.parseInt(offerId);
					offerName = salesMsDao.getOfferNameByOfferId(id);
				}

			}
			if(processPDtoMPRestUtil.isRESTVersionEnabled(offerName,MyPriceConstants.REST_PRODUCTS_V2, MyPriceConstants.SOURCE_PD)) {
				return StringConstants.VERSION_2;
			}else if(processPDtoMPRestUtil.isRESTVersionEnabled(offerName,MyPriceConstants.REST_PRODUCTS, MyPriceConstants.SOURCE_PD)) {
				return null;
			}
		}
		return null;
	}
	
	public ServiceResponse setErrorResponse(ServiceResponse response, String errorCode) {
		Status status = new Status();
		List<Message> messageList = new ArrayList<>();
		Message msg = MessageResourcesUtil.getMessageMapping().get(errorCode);
		messageList.add(msg);
		status.setCode(HttpErrorCodes.ERROR.toString());
		status.setMessages(messageList);
		response.setStatus(status);
		return response;
	}

	public void updateNxSolPdStausInd(String nxPdstatusInd, Long nxsolutionId) {
		repository.updatePdStatusIndBySolutionId(nxPdstatusInd, new Date(), nxsolutionId);
		repository.flush();
	}
 
}
