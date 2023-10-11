package com.att.sales.nexxus.userdetails.service;

/**
*
*
* @author aa316k
*         
*/

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import com.att.sales.framework.exception.SalesBusinessException;
import com.att.sales.framework.model.ServiceResponse;
import com.att.sales.framework.model.Status;
import com.att.sales.framework.service.BaseServiceImpl;
import com.att.sales.nexxus.constant.AuditTrailConstants;
import com.att.sales.nexxus.constant.MessageConstants;
import com.att.sales.nexxus.constant.StringConstants;
import com.att.sales.nexxus.dao.model.NxLookupData;
import com.att.sales.nexxus.dao.model.NxUser;
import com.att.sales.nexxus.dao.model.solution.NxSolutionDetail;
import com.att.sales.nexxus.dao.model.solution.NxTeam;
import com.att.sales.nexxus.dao.repository.NxLookupDataRepository;
import com.att.sales.nexxus.dao.repository.NxTeamRepository;
import com.att.sales.nexxus.dao.repository.NxUserRepository;
import com.att.sales.nexxus.model.MailResponse;
import com.att.sales.nexxus.model.MailServiceRequest;
import com.att.sales.nexxus.service.NexxusService;
import com.att.sales.nexxus.userdetails.model.ConsumerDetail;
import com.att.sales.nexxus.userdetails.model.ConsumerDetailRequest;
import com.att.sales.nexxus.userdetails.model.ConsumerDetailResponse;
import com.att.sales.nexxus.util.AuditUtil;
import com.att.sales.nexxus.util.DME2RestClient;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * The Class ConsumerDetailServiceImpl.
 */
@Service("ConsumerDetailServiceImpl")
public class ConsumerDetailServiceImpl extends BaseServiceImpl implements ConsumerDetailService {

	/** The logger. */
	private static Logger logger = LoggerFactory.getLogger(ConsumerDetailServiceImpl.class);

	/** The nxteam repo. */
	@Autowired
	private NxTeamRepository nxteamRepo;

	/** The adopt user service. */
	@Autowired
	private UserDetailsServiceImpl adoptUserService;
	
	@Autowired
	private NexxusService nexxusService;
	
	@Autowired
	private UserServiceImpl userServiceImpl;
	
	@Autowired
	private NxUserRepository nxUserRepository;
	
	@Autowired
	private AuditUtil auditUtil;
	
	/** The dme. */
	@Autowired
	private DME2RestClient dme;
	
	/** The mapper. */
	@Autowired
	private ObjectMapper mapper;
	
	/** The environment. */
	@Autowired
	private Environment environment;

	/**
	 * Process the Add,delete and retrieve functionality for Nexxus UI .
	 *
	 * @param request the request
	 * @return the consumer detail response
	 * @throws SalesBusinessException the sales business exception
	 */

	@Override
	public ServiceResponse consumerDetailData(ConsumerDetailRequest request) throws SalesBusinessException {
		Long currentTime = System.currentTimeMillis();
        Long startTime = System.currentTimeMillis() - currentTime;
        StringBuffer printLog = new StringBuffer(request.getAttuid()+"  "+ request.getActionType());
		logger.info("Enetered into consumerDetailData Method  {}", org.apache.commons.lang3.StringUtils.normalizeSpace(printLog.toString()));

		ConsumerDetailResponse consumerResponse = new ConsumerDetailResponse();
		List<ConsumerDetail> consumerDetails = new ArrayList<>();

		// Process the Delete functionality for Nexxus UI .
		if (Optional.ofNullable(request).isPresent() && StringConstants.DELETE_ACTION.equalsIgnoreCase(request.getActionType())) {
			try {
				nexxusService.updateNxSolution(request.getNxSolutionId());
				consumerResponse = deleteUser(request, consumerDetails, consumerResponse);
				Long endTime = System.currentTimeMillis() - currentTime;
				Long executionTime = endTime-startTime;
				auditUtil.addActionToNxUiAudit(request.getNxSolutionId(),AuditTrailConstants.USER_DELETE,request.getActionPerformedBy(),AuditTrailConstants.SUCCESS,null,request.getAttuid(),executionTime,null);			
			}catch(Exception e) {
				Long endTime = System.currentTimeMillis() - currentTime;
				Long executionTime = endTime-startTime;
				auditUtil.addActionToNxUiAudit(request.getNxSolutionId(),AuditTrailConstants.USER_DELETE,request.getActionPerformedBy(),AuditTrailConstants.FAIL,null,request.getAttuid(),executionTime,null);			
			}
		}

		// Process the Add functionality for Nexxus UI .
		if (Optional.ofNullable(request).isPresent()  && StringConstants.ADD_ACTION.equalsIgnoreCase(request.getActionType())) {
			try {
				nexxusService.updateNxSolution(request.getNxSolutionId());
				consumerResponse = addUser(request, consumerDetails, consumerResponse);
				Long endTime = System.currentTimeMillis() - currentTime;
				Long executionTime = endTime-startTime;
				auditUtil.addActionToNxUiAudit(request.getNxSolutionId(),AuditTrailConstants.USER_ADD,request.getActionPerformedBy(),AuditTrailConstants.SUCCESS,null,request.getAttuid(),executionTime,null);			
			}catch(Exception e) {
				Long endTime = System.currentTimeMillis() - currentTime;
				Long executionTime = endTime-startTime;
				auditUtil.addActionToNxUiAudit(request.getNxSolutionId(),AuditTrailConstants.USER_ADD,request.getActionPerformedBy(),AuditTrailConstants.FAIL,null,request.getAttuid(),executionTime,null);			
			}
		}

		// Process the retreive functionality for Nexxus UI
		if (StringConstants.RETREIVE_ACTION.equalsIgnoreCase(request.getActionType())) {
			consumerResponse = retreiveUser(request, consumerDetails, consumerResponse);
		}

		if(consumerResponse.getStatus() == null) {
			setSuccessResponse(consumerResponse);
	     }
		return consumerResponse;

	}

	/**
	 * Delete user.
	 *
	 * @param request the request
	 * @param consumerDetails the consumer details
	 * @param consumerResponse the consumer response
	 * @return the consumer detail response
	 */
	/*
	 * Deletes user from nx team table
	 */
	private ConsumerDetailResponse deleteUser(ConsumerDetailRequest request, List<ConsumerDetail> consumerDetails,
			ConsumerDetailResponse consumerResponse) {
		if (request.getAttuid() != null && request.getNxSolutionId() != null) {
			NxTeam nxTeam = nxteamRepo.getNxTeam(request.getNxSolutionId(), request.getAttuid());
			nxteamRepo.delete(nxTeam);
			
			try {
				sendMailForAddAndDeleteAction(request,nxTeam);
				} catch (Exception e) {
 					logger.error("SERVICE LOG :: Exception During Calling sendMailForAddAndDeleteAction method ", e);
				}

			consumerResponse = UserDetailData(request, consumerDetails, consumerResponse);
			//for capturing audit trail
		}
		return consumerResponse;

	}

	/**
	 * Adds the user.
	 *
	 * @param request the request
	 * @param consumerDetails the consumer details
	 * @param consumerResponse the consumer response
	 * @return the consumer detail response
	 * @throws SalesBusinessException the sales business exception
	 */
	/*
	 * ADD user in nx team table
	 */
	private ConsumerDetailResponse addUser(ConsumerDetailRequest request, List<ConsumerDetail> consumerDetails,
			ConsumerDetailResponse consumerResponse) throws SalesBusinessException {
		NxSolutionDetail nxSolutionDetail = new NxSolutionDetail();
		nxSolutionDetail.setNxSolutionId(request.getNxSolutionId());
		// NxUser table to get user details
		String userProfileName = userServiceImpl.getUserProfileName(request.getAttuid());
		if (!UserServiceImpl.NONE.equals(userProfileName)) {
			NxUser nxUser = nxUserRepository.findByUserAttId(request.getAttuid());
			NxTeam nxTeam = new NxTeam();

			nxTeam.setAttuid(nxUser.getUserAttId());
			nxTeam.setEmail(nxUser.getEmail());
			nxTeam.setfName(nxUser.getFirstName());
			nxTeam.setlName(nxUser.getLastName());
			nxTeam.setNxSolutionDetail(nxSolutionDetail);
			nxteamRepo.save(nxTeam);
			
		try {
				sendMailForAddAndDeleteAction(request,nxTeam);
			} catch (Exception e) {
 					logger.error("SERVICE LOG :: Exception During Calling sendMailForAddAndDeleteAction method ", e);
			}
		} else {
			Status status=	getErrorStatus(MessageConstants.ATTID_INVALID);
			consumerResponse.setStatus(status);
		}

		consumerResponse = UserDetailData(request, consumerDetails, consumerResponse);

		return consumerResponse;

	}

	/*
	 * RETREIVE user from nx team table
	 */

	/**
	 * Retreive user.
	 *
	 * @param request the request
	 * @param consumerDetails the consumer details
	 * @param consumerResponse the consumer response
	 * @return the consumer detail response
	 */
	private ConsumerDetailResponse retreiveUser(ConsumerDetailRequest request, List<ConsumerDetail> consumerDetails,
			ConsumerDetailResponse consumerResponse) {

		consumerResponse = UserDetailData(request, consumerDetails, consumerResponse);

		return consumerResponse;

	}

	/**
	 * User detail data.
	 *
	 * @param request the request
	 * @param consumerDetails the consumer details
	 * @param consumerResponse the consumer response
	 * @return the consumer detail response
	 */
	private ConsumerDetailResponse UserDetailData(ConsumerDetailRequest request, List<ConsumerDetail> consumerDetails,
			ConsumerDetailResponse consumerResponse) {

		List<NxTeam> nxTeamList = nxteamRepo.findByNxSolutionId(request.getNxSolutionId());
		for (int i = 0; i < nxTeamList.size(); i++) {
			ConsumerDetail conumerDetail = new ConsumerDetail();
			conumerDetail.setAttuId(nxTeamList.get(i).getAttuid());
			conumerDetail.setEmailId(nxTeamList.get(i).getEmail());
			conumerDetail.setFirstName(nxTeamList.get(i).getfName());
			conumerDetail.setLastName(nxTeamList.get(i).getlName());
			conumerDetail.setNxId(nxTeamList.get(i).getNxTeamId());
			consumerDetails.add(conumerDetail);
		}
		consumerResponse.setConsumerDetails(consumerDetails);
		return consumerResponse;

	}
	
	
	/**
	 * Mail notification Add/delete the user.
	 * @param request the request
	 * @param MailResponse the mail response
	 * @throws SalesBusinessException the sales business exception
	 */
	public MailResponse sendMailForAddAndDeleteAction(ConsumerDetailRequest request ,NxTeam nxTeam) throws SalesBusinessException {
		
		//nxTeam = nxteamRepo.getNxTeam(request.getNxSolutionId(), request.getAttuid());
		
		String toMail = request.getAttuid()+StringConstants.ATT_EXTN;
		String fromMail = (environment.getProperty("mail.notify.from")); //"aa316k"+StringConstants.ATT_EXTN;
		String env = "ENV";
		String mailSubject =null;
		String mailBody = null;
		if( StringConstants.DELETE_ACTION.equalsIgnoreCase(request.getActionType())) {
			
		    mailSubject= environment.getProperty("delete.user.mail.notify.success.subject");
			mailBody = environment.getProperty("delete.user.mail.notify.success.body");
			
		}else if(StringConstants.ADD_ACTION.equalsIgnoreCase(request.getActionType())) {
			
		    mailSubject= environment.getProperty("add.user.mail.notify.success.subject");
			mailBody = environment.getProperty("add.user.mail.notify.success.body");
		}
		mailBody = mailBody.replace("fName", StringUtils.isNotEmpty(nxTeam.getlName()) ? nxTeam.getfName() : "Nexxus" );
		mailBody = mailBody.replace("lName", StringUtils.isNotEmpty(nxTeam.getlName()) ? nxTeam.getlName():"Team");
		mailBody = mailBody.replace("solutionId", (request.getNxSolutionId()!=null ? request.getNxSolutionId().toString() : "0"));
		MailServiceRequest mailServiceRequest = new MailServiceRequest();
		mailServiceRequest.setMailsource(StringConstants.ADOPT);
		mailServiceRequest.setToMailList(toMail);
		mailServiceRequest.setFromMailList(fromMail);
		mailServiceRequest.setEmailSub(mailSubject);
		mailServiceRequest.setEmailBody(mailBody);
		mailServiceRequest.setCoorelationId(request.getNxSolutionId());
		String jsonPayLoad = null;
		try {
			jsonPayLoad = mapper.writeValueAsString(mailServiceRequest);
		} catch (Exception e) {
			logger.error("SERVICE LOG ::  MAPPING REQUEST PARSE ERROR {} ", e);
		}
		MailResponse mailResponse = dme.callMailNotificationDME2(jsonPayLoad);
		return mailResponse;
	}

}
