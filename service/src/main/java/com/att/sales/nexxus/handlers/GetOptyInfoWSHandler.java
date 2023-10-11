/**
 * rw161p
 */
package com.att.sales.nexxus.handlers;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.att.abs.ecrm.opty.v2.ObjectFactory;
import com.att.abs.ecrm.opty.v2.OptyInfoRequest;
import com.att.abs.ecrm.opty.v2.OptyInfoRequestType;
import com.att.abs.ecrm.opty.v2.OptyInfoResponse;
import com.att.abs.ecrm.opty.v2.OptyInfoResponseType.ListOfAbsAccount.AbsAccount;
import com.att.abs.ecrm.opty.v2.OptyInfoResponseType.ListOfOpportunityPosition.OpportunityPosition;
import com.att.cio.commonheader.v3.WSEndUserToken;
import com.att.cio.commonheader.v3.WSEnterpriseLogging;
import com.att.cio.commonheader.v3.WSHeader;
import com.att.sales.framework.exception.SalesBusinessException;
import com.att.sales.framework.model.ServiceResponse;
import com.att.sales.nexxus.common.CommonConstants;
import com.att.sales.nexxus.common.MessageConstants;
import com.att.sales.nexxus.constant.MyPriceConstants;
import com.att.sales.nexxus.constant.StringConstants;
import com.att.sales.nexxus.dao.model.NxUser;
import com.att.sales.nexxus.dao.model.solution.NxSolutionDetail;
import com.att.sales.nexxus.dao.model.solution.NxTeam;
import com.att.sales.nexxus.dao.repository.HybridRepositoryService;
import com.att.sales.nexxus.dao.repository.NxUserRepository;
import com.att.sales.nexxus.rome.model.GetOptyResponse;
import com.att.sales.nexxus.userdetails.model.UserDetails;
import com.att.sales.nexxus.userdetails.model.UserDetailsRequest;
import com.att.sales.nexxus.userdetails.model.UserDetailsResponse;
import com.att.sales.nexxus.userdetails.service.UserDetailsServiceImpl;
import com.att.sales.nexxus.userdetails.service.UserServiceImpl;
import com.att.sales.nexxus.ws.utility.SoapWSHandler;
import com.att.sales.nexxus.ws.utility.WSProcessingService;

/**
 * The Class GetOptyInfoWSHandler.
 */
@Component
public class GetOptyInfoWSHandler implements MessageConstants {

	/** The Constant log. */
	private static final Logger log = LoggerFactory.getLogger(GetOptyInfoWSHandler.class);

	/** The hybrid repository service. */
	@Autowired
	private HybridRepositoryService hybridRepositoryService;
	
	

	/** The logging key. */
	@Value("${rome.getOptyInfo.loggingKey}")
	private String loggingKey;

	

	/** The token. */
	@Value("${rome.getOptyInfo.token}")
	private String token;

	/** The token type. */
	@Value("${rome.getOptyInfo.tokenType}")
	private String tokenType;

	/** The application id. */
	@Value("${rome.getOptyInfo.applicationID}")
	private String applicationId;

	/** The adopt user service. */
	@Autowired
	private UserDetailsServiceImpl adoptUserService;
	
	@Autowired
	@Qualifier("getOptyInfoWSClientUtility")
	private SoapWSHandler getOptyInfoWSClientUtility;
	
	@Autowired
	private WSProcessingService wsProcessingService;
	
	@Autowired
	private UserServiceImpl userServiceImpl;

	@Autowired
	private NxUserRepository nxUserRepository;
	
	/**
	 * To call GetOptyInfo web-service published at ROME application.
	 *
	 * @param requestMap the request map
	 * @return Map<String, Object>
	 * @throws SalesBusinessException the sales business exception
	 */
	public ServiceResponse initiateGetOptyInfoWebService(Map<String, Object> requestMap)
			throws SalesBusinessException {
		ObjectFactory objectFactory = new ObjectFactory();
		OptyInfoResponse optyInfoResponse = null;
		OptyInfoRequest optyInfoRequest = objectFactory.createOptyInfoRequest();

		log.info("Creating Header details...");
		createHeaderDetails(optyInfoRequest);

		prepareRequestBody(requestMap, optyInfoRequest);

		try {
			// -- Calling to ROME to get OptyInfo details.
			if((null!= requestMap.get("optyId") && ""!= requestMap.get("optyId")) &&  null != requestMap.get("attuid")){
				getOptyInfoWSClientUtility.setWsName(MyPriceConstants.GET_OPTY_WS);
			optyInfoResponse = wsProcessingService.initiateWebService(optyInfoRequest, 
					getOptyInfoWSClientUtility, requestMap,OptyInfoResponse.class);
			}

		} catch (Exception e) {
			e.printStackTrace();
			if (e.getMessage() != null && e.getMessage().equalsIgnoreCase(CommonConstants.INVALID_OPTY)) {
				log.error("Invalid OptyId or HrId ", e);
				throw new SalesBusinessException(MessageConstants.INVALID_OPTYID_OR_HRID);
			} else {
				log.error("Exception during Opty call", e);
				throw new SalesBusinessException(MessageConstants.OPTYID_CALL_FAILLED);
			}
		}
		return processResponse(optyInfoResponse, requestMap);
	}

	/**
	 * To prepare the request body for the web-service call.
	 *
	 * @param requestMap the request map
	 * @param optyInfoRequest the opty info request
	 */
	protected void prepareRequestBody(Map<String, Object> requestMap, OptyInfoRequest optyInfoRequest) {
		OptyInfoRequestType requestParams = new OptyInfoRequestType();
		requestParams.setId((String) requestMap.get("optyId"));
		requestParams.setHrid((String) requestMap.get("attuid"));			
		optyInfoRequest.setRequestParams(requestParams);
	}

	/**
	 * To process the response received from web-service call.
	 *
	 * @param optyInfoResponse the opty info response
	 * @param requestMap the request map
	 * @return Map<String, Object>
	 * @throws SalesBusinessException the sales business exception
	 */
	public GetOptyResponse processResponse(OptyInfoResponse optyInfoResponse, Map<String, Object> requestMap)
			throws SalesBusinessException {
		log.info("Inside processResponse...");

		NxSolutionDetail nxSolutionDetail = new NxSolutionDetail();
		nxSolutionDetail.setArchivedSolInd(StringConstants.CONSTANT_N);
		GetOptyResponse resp = new GetOptyResponse();
		Long solutionId = null!= requestMap.get("nxSolutionId")? (Long) requestMap.get("nxSolutionId"):null ;
		String nxSolDesc = (null !=requestMap.get("solutionDescription") && ""!=requestMap.get("solutionDescription"))? requestMap.get("solutionDescription").toString():null;
		String flowType = requestMap.containsKey("flowType") ? (String) requestMap.get("flowType") : "INR";
		
		if (optyInfoResponse != null) {
			List<OpportunityPosition> listOfEcrmOpportunityPosition = optyInfoResponse.getResponseParams()
					.getListOfOpportunityPosition().getOpportunityPosition();			
			
			List<AbsAccount> listOfEcrmAbsAccount = optyInfoResponse.getResponseParams().getListOfAbsAccount()
					.getAbsAccount();
			List<NxTeam> nxTeamList = new ArrayList<>();			
						
			if(null !=requestMap.get("action") &&( requestMap.get("action").toString().equalsIgnoreCase("createSolution") ||  requestMap.get("action").toString().equalsIgnoreCase("updateSolution")
					|| ("myPriceFlow".equalsIgnoreCase(requestMap.get("action").toString()) && (requestMap.containsKey("subAction") && "updateSolutionMyPriceFlow".equalsIgnoreCase(requestMap.get("subAction").toString())) ) )) {
										
			if (null != solutionId) {
				List<NxSolutionDetail> listOpportunityTeam = hybridRepositoryService.getNxSolutionDetailList(solutionId);
				if (!listOpportunityTeam.isEmpty()) {
					nxSolutionDetail = listOpportunityTeam.get(0);
				} else {
					// put valid msg code for INVALID SOLUTION_ID
					log.info("Invalid SolutinId");
					throw new SalesBusinessException(MessageConstants.INVALID_SOLUTION_ID);
				}
			}

			nxSolutionDetail.setOptyId((String) requestMap.get("optyId"));
			nxSolutionDetail.setOptyLinkedBy((String) requestMap.get("attuid"));
			resp.setOptyId((String) requestMap.get("optyId"));
			if(null!= requestMap.get("solutionDescription")) {
				nxSolutionDetail.setNxsDescription(requestMap.get("solutionDescription").toString());
				resp.setSolutionDescription(requestMap.get("solutionDescription").toString());
			}
			nxSolutionDetail.setL3Value(listOfEcrmAbsAccount.get(0).getSubAccountID());
			nxSolutionDetail.setDunsNumber(listOfEcrmAbsAccount.get(0).getDUNSNumber());
			nxSolutionDetail.setGuDunsNumber(listOfEcrmAbsAccount.get(0).getGlobalUltimateDUNS());
			nxSolutionDetail.setCustomerName(optyInfoResponse.getResponseParams().getCustomerName());

			if (null == solutionId) {
				nxSolutionDetail.setCreatedUser((String) requestMap.get("attuid"));
				nxSolutionDetail.setCreatedDate(new Date());
			}

			nxSolutionDetail.setModifiedDate(new Date());
			nxSolutionDetail.setModifiedUser((String) requestMap.get("attuid"));
			nxSolutionDetail.setActiveYn("Y");
			nxSolutionDetail.setFlowType(flowType);
			
			hybridRepositoryService.setNxSolutionDetailList(nxSolutionDetail);
			resp.setNxSolutionId(nxSolutionDetail.getNxSolutionId());			

			List<String> customList = new ArrayList<>();
			for (OpportunityPosition opportunityPosition : listOfEcrmOpportunityPosition) {
				if(null != opportunityPosition.getSalesRep() && !customList.contains(opportunityPosition.getSalesRep())) {
					NxTeam nxTeam = new NxTeam();
					List<NxTeam> listOfNxTeam = hybridRepositoryService.getNxTeamList(opportunityPosition.getSalesRep(),
							nxSolutionDetail);
					
					if(!listOfNxTeam.isEmpty()) {
						nxTeam = listOfNxTeam.get(0);
					}
					nxTeam.setNxSolutionDetail(nxSolutionDetail);
					nxTeam.setAttuid(opportunityPosition.getSalesRep());
					nxTeam.setfName(opportunityPosition.getFirstName());
					nxTeam.setlName(opportunityPosition.getLastName());
					nxTeam.setSalesRepFullName(opportunityPosition.getSalesRepFullName());
					nxTeam.setEmail(opportunityPosition.getEmail());
					nxTeam.setManagerName(opportunityPosition.getManagerName());
					nxTeam.setManagerHrid(opportunityPosition.getManagerHRID());
					nxTeam.setIsPryMVG(opportunityPosition.getIsPrimaryMVG());
					nxTeamList.add(nxTeam);
					customList.add(opportunityPosition.getSalesRep());
				}
			}
			hybridRepositoryService.setNxTeamList(nxTeamList);
		}else if(null != requestMap.get("action") && requestMap.get("action").toString().equalsIgnoreCase("retrieveOpty") ) {
			
			resp.setOptyId((String) requestMap.get("optyId"));
			resp.setCustomerName(optyInfoResponse.getResponseParams().getCustomerName());
			resp.setDunsNumber(listOfEcrmAbsAccount.get(0).getDUNSNumber());
			resp.setGuDunsNumber(listOfEcrmAbsAccount.get(0).getGlobalUltimateDUNS());
			resp.setL3SubAcctId(listOfEcrmAbsAccount.get(0).getSubAccountID());
			resp.setL4AcctId(optyInfoResponse.getResponseParams().getAccountId());
			
		}
			
		/** Added the below code by M V C VAMSI KRISHNA (vk553x) in develop/1906 branch.
		 * If marketStrata missing in ICBPricingRequest, call getOpty and set the fields necessary (missing ones)
			for UT_cleanSave back to the same ICBPricingRequest using response of getOpty. Starts*/
			
		if(null !=requestMap.get("action") && (requestMap.get("action").toString().equalsIgnoreCase("myPriceFlow"))){
			List<String> salesRepList = new ArrayList<>();
			for (OpportunityPosition opportunityPosition : listOfEcrmOpportunityPosition) {				 
				if("N".equalsIgnoreCase(opportunityPosition.getIsPrimaryMVG())){						
					salesRepList.add(opportunityPosition.getSalesRep());	//Body.OptyInfoResponse.ResponseParams.ListOfOpportunityPosition.OpportunityPosition.SalesRep where IsPrimaryMVG is 'N'. 								
				}
			}
			resp.setOptyId((String) requestMap.get("optyId"));
			resp.setSalesRep(salesRepList);
			resp.setName(optyInfoResponse.getResponseParams().getName());
			resp.setAccountId(optyInfoResponse.getResponseParams().getAccountId());			
			resp.setSubAccountID(listOfEcrmAbsAccount.get(0).getSubAccountID());
			resp.setCurrencyCode(optyInfoResponse.getResponseParams().getCurrencyCode());
			resp.setOffer(optyInfoResponse.getResponseParams().getOffer());
			resp.setType(optyInfoResponse.getResponseParams().getType());
			resp.setMarketStrataValue(optyInfoResponse.getResponseParams().getMarketStrataValue());		
			resp.setAbsCreatedByName(optyInfoResponse.getResponseParams().getABSCreatedByName());
			resp.setAddress1(optyInfoResponse.getResponseParams().getAddress1());
			resp.setAddress2(optyInfoResponse.getResponseParams().getAddress2());
			resp.setCity(optyInfoResponse.getResponseParams().getCity());
			resp.setState(optyInfoResponse.getResponseParams().getState());
			resp.setCountry(optyInfoResponse.getResponseParams().getCountry());
			resp.setPostalCode(optyInfoResponse.getResponseParams().getPostalCode());
			resp.setPrimaryATTUID(optyInfoResponse.getResponseParams().getPrimaryATTUID());
			resp.setPrimaryManager(optyInfoResponse.getResponseParams().getPrimaryManager());
			resp.setPrimaryManagersManager(optyInfoResponse.getResponseParams().getPrimaryManagersManager());
			resp.setCustomerName(optyInfoResponse.getResponseParams().getCustomerName());
			resp.setMainPhone(optyInfoResponse.getResponseParams().getMainPhone());	
			resp.setForm470(optyInfoResponse.getResponseParams().getForm470());
			
		} //Ends
 }else if((null == requestMap.get("optyId") || "" == requestMap.get("optyId")) ||  null == requestMap.get("attuid")){ 
			
			if(null !=requestMap.get("action") &&( requestMap.get("action").toString().equalsIgnoreCase("createSolution") ||  requestMap.get("action").toString().equalsIgnoreCase("updateSolution"))) {
				
				if (null != solutionId) {
					List<NxSolutionDetail> listOpportunityTeam = hybridRepositoryService.getNxSolutionDetailList(solutionId);
					if (!listOpportunityTeam.isEmpty()) {
						nxSolutionDetail = listOpportunityTeam.get(0);
					} else {
						// put valid msg code for INVALID SOLUTION_ID
						log.info("Invalid SolutinId");
						throw new SalesBusinessException(MessageConstants.INVALID_SOLUTION_ID);
					}
				}

				if(null!= requestMap.get("optyId") && ""!= requestMap.get("optyId")) {				
				nxSolutionDetail.setOptyId((String) requestMap.get("optyId"));
				resp.setOptyId((String) requestMap.get("optyId"));
				}
				if(null!= requestMap.get("solutionDescription")) {
					nxSolutionDetail.setNxsDescription(requestMap.get("solutionDescription").toString());
					resp.setSolutionDescription(requestMap.get("solutionDescription").toString());
				}
				if (null == solutionId) {
					if(null!= requestMap.get("attuid")) {
					nxSolutionDetail.setCreatedUser((String) requestMap.get("attuid"));
					}					
					nxSolutionDetail.setCreatedDate(new Date());
				}

				nxSolutionDetail.setModifiedDate(new Date());
				 if(null!= requestMap.get("attuid")) {
				nxSolutionDetail.setModifiedUser((String) requestMap.get("attuid"));
				 }
				nxSolutionDetail.setActiveYn("Y");
				nxSolutionDetail.setFlowType(flowType);
				hybridRepositoryService.setNxSolutionDetailList(nxSolutionDetail);
				resp.setNxSolutionId(nxSolutionDetail.getNxSolutionId());	
				
				addDefaultTeam(nxSolutionDetail);
		}
		}				
		else {
			log.error("Rome getOpty sending null as response");
			//put valid error message for Rome getOpty sending null as response
			throw new SalesBusinessException();
		}
		return resp;
	}
	
	/**
	 * Adds the default team.
	 *
	 * @param nxSolutionDetail the nx solution detail
	 */
	private void addDefaultTeam(NxSolutionDetail nxSolutionDetail) {
		if(null != nxSolutionDetail && null != nxSolutionDetail.getCreatedUser() && !nxSolutionDetail.getCreatedUser().isEmpty()) {
			List<NxTeam> nxTeamList = hybridRepositoryService.getNxTeamList(nxSolutionDetail.getCreatedUser(),
					nxSolutionDetail);
			if(null == nxTeamList || nxTeamList.isEmpty()) {
				NxTeam nxTeam = new NxTeam();
				nxTeam.setNxSolutionDetail(nxSolutionDetail);
				nxTeam.setAttuid(nxSolutionDetail.getCreatedUser());
				nxTeam.setIsPryMVG("Y");
//				log.info("Flow type while creating nxSolutiondetail "+nxSolutionDetail.getFlowType());
				if("INR".equalsIgnoreCase(nxSolutionDetail.getFlowType()) || "iglooQuote".equalsIgnoreCase(nxSolutionDetail.getFlowType()) ) {
					// NxUser table to get user details
					String userProfileName = userServiceImpl.getUserProfileName(nxSolutionDetail.getCreatedUser());
					if (!UserServiceImpl.NONE.equals(userProfileName)) {
						NxUser nxUser = nxUserRepository.findByUserAttId(nxSolutionDetail.getCreatedUser());
						nxTeam.setEmail(nxUser.getEmail());
						nxTeam.setfName(nxUser.getFirstName());
						nxTeam.setlName(nxUser.getLastName());
					} else {
						log.error("No user details for attuid " + nxSolutionDetail.getCreatedUser() + 
								"for nexxus solution " + nxSolutionDetail.getNxSolutionId());
					}
				}else {
					UserDetailsRequest req = new UserDetailsRequest();
					req.setAttuid(nxSolutionDetail.getCreatedUser());
					try {
						// Call ADOPT to get user details like name, email
						UserDetailsResponse resp = (UserDetailsResponse) adoptUserService.retreiveUserDetails(req);
						List<UserDetails> userDetails = resp.getUserDetails();
						if (CollectionUtils.isNotEmpty(userDetails) && null!=userDetails.get(0)) {
							nxTeam.setEmail(userDetails.get(0).getEmailId());
							nxTeam.setfName(userDetails.get(0).getFirstName());
							nxTeam.setlName(userDetails.get(0).getLastName());
						}
					} catch (Exception e) {
						log.error("Exception in addDefaultTeam for attuid " + nxSolutionDetail.getCreatedUser() + 
							" for nexxus solution " + nxSolutionDetail.getNxSolutionId());
					}
				}
				hybridRepositoryService.setNxTeam(nxTeam);
			}
		}
	}

	/**
	 * Method to create header details for change request.
	 *
	 * @param optyInfoRequest the opty info request
	 * @throws SalesBusinessException the sales business exception
	 */
	private void createHeaderDetails(OptyInfoRequest optyInfoRequest) throws SalesBusinessException {
		WSHeader wsHeader = new WSHeader();
		try {
			wsHeader.setWSResponseMessageExpiration(30000L);

			WSEndUserToken wsEndUserToken = new WSEndUserToken();
			wsEndUserToken.setToken(token);
			wsEndUserToken.setTokenType(tokenType);
			wsHeader.setWSEndUserToken(wsEndUserToken);

			WSEnterpriseLogging wsEnterpriseLogging = new WSEnterpriseLogging();
			wsEnterpriseLogging.setApplicationID(applicationId);
			wsEnterpriseLogging.setLoggingKey(loggingKey);
			wsHeader.setWSEnterpriseLogging(wsEnterpriseLogging);

			optyInfoRequest.setWSHeader(wsHeader);
		} catch (RuntimeException e) {
			log.error("Exception during header preparation", e);
			throw new SalesBusinessException(e.getMessage());
		}
	}

}
