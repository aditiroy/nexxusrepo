package com.att.sales.nexxus.service;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

/**
 *
 *
 * @author aa316k
 *         
 */

import java.util.ArrayList;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import com.att.sales.framework.exception.SalesBusinessException;
import com.att.sales.framework.model.ServiceResponse;
import com.att.sales.framework.service.BaseServiceImpl;
import com.att.sales.nexxus.constant.CommonConstants.STATUS_CONSTANTS;
import com.att.sales.nexxus.common.CommonConstants;
import com.att.sales.nexxus.constant.DataUploadConstants;
import com.att.sales.nexxus.constant.StringConstants;
import com.att.sales.nexxus.dao.model.NxMpDeal;
import com.att.sales.nexxus.dao.model.NxRequestDetails;
import com.att.sales.nexxus.dao.model.solution.NxSolutionDetail;
import com.att.sales.nexxus.dao.model.solution.NxTeam;
import com.att.sales.nexxus.dao.repository.NxMpSolutionDetailsRepository;
import com.att.sales.nexxus.dao.repository.NxRequestDetailsRepository;
import com.att.sales.nexxus.dao.repository.NxSolutionDetailsRepository;
import com.att.sales.nexxus.dao.repository.NxTeamRepository;
import com.att.sales.nexxus.dmaap.mr.util.DmaapPublishEventsService;
import com.att.sales.nexxus.edf.model.ManageBillDataInv;
import com.att.sales.nexxus.model.MailRequest;
import com.att.sales.nexxus.model.SendMailRequest;
import com.att.sales.nexxus.model.MailResponse;
import com.att.sales.nexxus.model.MailServiceRequest;
import com.att.sales.nexxus.util.DME2RestClient;
import com.fasterxml.jackson.databind.ObjectMapper;


/**
 * The Class MailServiceImpl.
 */
@Service
public class MailServiceImpl extends BaseServiceImpl implements MailService {

	/** The logger. */
	private static Logger logger = LoggerFactory.getLogger(MailServiceImpl.class);
	
	@Autowired
	private DmaapPublishEventsService dmaapPublishEventsService;

	/** The environment. */
	@Autowired
	private Environment environment;

	/** The dme. */
	@Autowired
	private DME2RestClient dme;

	/** The repo. */
	@Autowired
	private NxRequestDetailsRepository repo;

	/** The repository. */
	@Autowired
	private NxTeamRepository repository;
	
	/** The mapper. */
	@Autowired
	private ObjectMapper mapper;
	
	@Autowired
	private NxSolutionDetailsRepository nxSolutionDetailsRepository;
	
	@Autowired
	private NxMyPriceRepositoryServce nxMyPriceRepositoryServce;

	/* (non-Javadoc)
	 * @see com.att.sales.nexxus.service.MailService#mailNotification(com.att.sales.nexxus.model.MailRequest)
	 */
	@Override
	public ServiceResponse mailNotification(MailRequest request) throws SalesBusinessException {
		NxRequestDetails nxRequest = repo.findByNxReqId(request.getNxRequestId());
		NxTeam nxTeam = null;
		ObjectMapper mapper = new ObjectMapper();
		String jsonPayLoad = null;
		nxTeam = getToMailId(nxRequest.getNxSolutionDetail());

		try {
			jsonPayLoad = mapper.writeValueAsString(prepareMailRequest(nxTeam, request, nxRequest));
		} catch (Exception e) {
			logger.error("SERVICE LOG ::  MAPPING REQUEST PARSE ERROR {} ", e);

		}

		MailResponse mailResp = dme.callMailNotificationDME2(jsonPayLoad);
		
		return mailResp;

	}
	
	
	@Override
	public MailResponse mailNotificationFMO(MailRequest request) throws SalesBusinessException {
		NxRequestDetails nxRequest = repo.findByNxReqId(request.getNxRequestId());
		NxTeam nxTeam = null;
		ObjectMapper mapper = new ObjectMapper();
		String jsonPayLoad = null;
		nxTeam = getToMailId(nxRequest.getNxSolutionDetail());

		try {
			jsonPayLoad = mapper.writeValueAsString(prepareMailRequestFMO(nxTeam, request, nxRequest));
		} catch (Exception e) {
			logger.error("SERVICE LOG ::  MAPPING REQUEST PARSE ERROR {} ", e);

		}

		MailResponse mailResp = dme.callMailNotificationDME2(jsonPayLoad);
		
		return mailResp;

	}


	/**
	 * Gets the to mail id.
	 *
	 * @param nxSolution the nx solution
	 * @return the to mail id
	 */
	public NxTeam getToMailId(NxSolutionDetail nxSolution) {
		NxTeam nxTeam = null;

		if (nxSolution != null && 0L != nxSolution.getNxSolutionId()) {
			nxTeam = repository.getNxTeam(nxSolution.getNxSolutionId(), nxSolution.getCreatedUser());
		}
		return nxTeam;
	}
	
	
	/* (non-Javadoc)
	 * @see com.att.sales.nexxus.service.MailService#prepareMailRequest(com.att.sales.nexxus.dao.model.solution.NxTeam, com.att.sales.nexxus.model.MailRequest, com.att.sales.nexxus.dao.model.NxRequestDetails)
	 */
	public <T> MailServiceRequest prepareMailRequest(NxTeam nxTeam, MailRequest request,
			NxRequestDetails nxRequestDetail) {
		String email = null;
		List<String> allIds=  new ArrayList<String>();
		NxSolutionDetail nxSolutionDetail = nxRequestDetail.getNxSolutionDetail();
		Long solnId = nxSolutionDetail.getNxSolutionId();
		Long corrId = request.getNxRequestId();
		
		List<NxTeam> team = repository.findByNxSolutionId(solnId);
		
		
		allIds = team.stream().filter(nx -> nx.getAttuid() != null).map(nx -> nx.getAttuid())
			.collect(Collectors.toList());
		
		email = allIds.stream().filter(m -> m != null).map(m -> m + StringConstants.ATT_EXTN)
			.collect(Collectors.joining(","));
		
		
	    MailServiceRequest mailServiceRequest = new MailServiceRequest();
		mailServiceRequest.setMailsource(StringConstants.ADOPT);
		mailServiceRequest.setToMailList(email);
		mailServiceRequest.setFromMailList(environment.getProperty("mail.notify.from"));
		
		String customerName = getCustomerName(nxRequestDetail, nxSolutionDetail);
		
		
		if (nxRequestDetail.getStatus() != null && nxRequestDetail.getStatus() == 30) {
			String emailSuccess =  environment.getProperty("mail.notify.success.body");
			emailSuccess =  emailSuccess.replaceAll("fName", null != nxTeam && null != nxTeam.getfName() ? nxTeam.getfName() : "Nexxus" );
			emailSuccess =  emailSuccess.replaceAll("lName", null != nxTeam &&  null != nxTeam.getlName() ? nxTeam.getlName() : "Team");
			emailSuccess =	emailSuccess.replaceAll(StringConstants.SOLN_ID, solnId.toString());	
			mailServiceRequest.setEmailSub(environment.getProperty("mail.notify.success.subject").replaceAll(StringConstants.SOLN_ID, solnId.toString()).replaceAll(StringConstants.PRODUCT, nxRequestDetail.getProduct()).replaceAll(StringConstants.CUSTOMER_NAME, customerName));
			mailServiceRequest.setEmailBody(emailSuccess);
			
		}
		
		else if(nxRequestDetail.getStatus() != null && nxRequestDetail.getStatus() == 20) {
			
			String emailPartiaFallout = environment.getProperty("mail.notify.partialfallout.body");
			emailPartiaFallout = emailPartiaFallout.replaceAll("fName", null != nxTeam && null != nxTeam.getfName() ? nxTeam.getfName() : "Nexxus" );
			emailPartiaFallout = emailPartiaFallout.replaceAll("lName", null != nxTeam &&  null != nxTeam.getlName() ? nxTeam.getlName() : "Team");
			emailPartiaFallout = emailPartiaFallout.replaceAll(StringConstants.SOLN_ID, solnId.toString());
			mailServiceRequest.setEmailSub(environment.getProperty("mail.notify.partialfallout.subject").replaceAll(StringConstants.SOLN_ID, solnId.toString()).replaceAll(StringConstants.PRODUCT, nxRequestDetail.getProduct()).replaceAll(StringConstants.CUSTOMER_NAME, customerName));
			mailServiceRequest.setEmailBody(emailPartiaFallout);
		}
		
		else {
			
			String emailFail = environment.getProperty("mail.notify.failed.body");
			emailFail = emailFail.replaceAll("fName", null != nxTeam && null != nxTeam.getfName() ? nxTeam.getfName() : "Nexxus" );
			emailFail = emailFail.replaceAll("lName", null != nxTeam &&  null != nxTeam.getlName() ? nxTeam.getlName() : "Team");
			emailFail =	emailFail.replaceAll(StringConstants.SOLN_ID, solnId.toString());
			mailServiceRequest.setEmailSub(environment.getProperty("mail.notify.failed.subject").replaceAll(StringConstants.SOLN_ID, solnId.toString()).replaceAll(StringConstants.PRODUCT, nxRequestDetail.getProduct()).replaceAll(StringConstants.CUSTOMER_NAME, customerName));
			mailServiceRequest.setEmailBody(emailFail);
		}
		
		mailServiceRequest.setCoorelationId(corrId);
		return mailServiceRequest;
	}
	
		
	public void prepareMyPriceDealSubmissionRequest(NxMpDeal nxMpDeal) {
		
		MailServiceRequest mailServiceRequest = new MailServiceRequest();
			
		NxSolutionDetail nxSolutionDetail = nxSolutionDetailsRepository.findByNxSolutionId(nxMpDeal.getSolutionId());
				
		String email = null;
		String customerName = null;
		String fname = null;
		String lname = null;
		List<String> allIds=  new ArrayList<String>();
		
		
		if(nxSolutionDetail != null) {
			List<NxTeam> team = repository.findByNxSolutionId(nxMpDeal.getSolutionId());
			
			allIds = team.stream().filter(nx -> nx.getAttuid() != null).map(nx -> nx.getAttuid())
				.collect(Collectors.toList());
			
			email = allIds.stream().filter(m -> m != null).map(m -> m + StringConstants.ATT_EXTN)
				.collect(Collectors.joining(","));
			
			if(team != null){
				for(NxTeam member : team){
					if(member.getAttuid().equalsIgnoreCase(nxSolutionDetail.getCreatedUser())) {
						fname = member.getfName();
						lname = member.getlName();
					}
				}
			}
			
			
			Long corrId = nxMpDeal.getNxTxnId();
			mailServiceRequest.setCoorelationId(corrId);			
			mailServiceRequest.setFromMailList(environment.getProperty("mail.notify.from"));				
			mailServiceRequest.setToMailList(email);
			
			if(nxSolutionDetail.getCustomerName() != null) {
				customerName = nxSolutionDetail.getCustomerName();
			} else {
				customerName = nxSolutionDetail.getNxsDescription();
			}
			
			String emailBody = "", emailSubject = "";
			if ("SUBMITTED".equalsIgnoreCase(nxMpDeal.getDealStatus())) {
				emailBody =  environment.getProperty("mail.notify.dealsubmissionsuccess.body");
				emailSubject = environment.getProperty("mail.notify.dealsubmissionsuccess.subject");
			}
			else if("FAILED".equalsIgnoreCase(nxMpDeal.getDealStatus())) {
				emailBody =  environment.getProperty("mail.notify.dealsubmissionfailure.body");
				emailSubject = environment.getProperty("mail.notify.dealsubmissionfailure.subject");
			}
			else if(CommonConstants.PARTIAL.equalsIgnoreCase(nxMpDeal.getDealStatus())) {
				emailBody =  environment.getProperty("mail.notify.dealsubmissionpartial.body");
				emailSubject = environment.getProperty("mail.notify.dealsubmissionpartial.subject");
			}
			
			mailServiceRequest.setMailsource(StringConstants.ADOPT);
						
			mailServiceRequest.setEmailBody(emailBody.replaceAll("fName", null != fname ? fname : "Nexxus" )
					.replaceAll("lName", null != lname ? lname : "Team" )
					.replaceAll(StringConstants.SOLN_ID, nxMpDeal.getSolutionId().toString())
					.replaceAll("transactionId", nxMpDeal.getTransactionId())
					.replaceAll("dealId", nxMpDeal.getDealID())
					.replaceAll("verId", nxMpDeal.getVersion())
					.replaceAll("revId", nxMpDeal.getRevision()));
			
			mailServiceRequest.setEmailSub(emailSubject.
					replaceAll(StringConstants.SOLN_ID, nxMpDeal.getSolutionId().toString()).
					replaceAll(StringConstants.CUSTOMER_NAME, customerName).
					replaceAll("dealId", nxMpDeal.getDealID()).
					replaceAll("verId", nxMpDeal.getVersion()).
					replaceAll("revId", nxMpDeal.getRevision()));
		}
		
		logger.info("Mail response object : " + mailServiceRequest.toString());
		
		String jsonPayLoad = null;
		
			try {
				jsonPayLoad = mapper.writeValueAsString(mailServiceRequest);
				dme.callMailNotificationDME2(jsonPayLoad);
			} catch (Exception e) {
				logger.error("SERVICE LOG ::  MAPPING REQUEST PARSE ERROR {} ", e);
			}		
				
	}
	
	@Override
	public  MailServiceRequest prepareMailRequestFMO(NxTeam nxTeam, MailRequest request,
			NxRequestDetails nxRequestDetail) {
		String email = null;
		NxSolutionDetail nxSolutionDetail = nxRequestDetail.getNxSolutionDetail();
		Long solnId = nxSolutionDetail.getNxSolutionId();
		Long corrId = request.getNxRequestId();
		
		email = null != nxTeam &&  null != nxTeam.getAttuid() ? nxTeam.getAttuid() + StringConstants.ATT_EXTN :
			nxSolutionDetail.getCreatedUser() + StringConstants.ATT_EXTN;
	    if(email.equalsIgnoreCase("null@att.com")) {
	    	 email =   nxSolutionDetail.getCreatedUser() + StringConstants.ATT_EXTN;
	    }
		
	    MailServiceRequest mailServiceRequest = new MailServiceRequest();
		mailServiceRequest.setMailsource(StringConstants.ADOPT);
		mailServiceRequest.setToMailList(email);
		mailServiceRequest.setFromMailList(environment.getProperty("mail.notify.from"));
		
		String customerName = getCustomerName(nxRequestDetail, nxSolutionDetail);
		if (!(nxRequestDetail.getStatus() != null && STATUS_CONSTANTS.SUCCESS.getValue()==nxRequestDetail.getStatus())) {
			String emailFail = environment.getProperty("mail.notify.failed.body.fmo");
			emailFail = emailFail.replaceAll("fName", null != nxTeam && null != nxTeam.getfName() ? nxTeam.getfName() : "Nexxus" );
			emailFail = emailFail.replaceAll("lName", null != nxTeam &&  null != nxTeam.getlName() ? nxTeam.getlName() : "Team");
			emailFail =	emailFail.replaceAll(StringConstants.SOLN_ID, solnId.toString());
			mailServiceRequest.setEmailSub(environment.getProperty("mail.notify.failed.subject.fmo").
					replaceAll(StringConstants.SOLN_ID, solnId.toString()).replaceAll(StringConstants.CUSTOMER_NAME, customerName));
			mailServiceRequest.setEmailBody(emailFail);
		}
		mailServiceRequest.setCoorelationId(corrId);
		return mailServiceRequest;
	}
	
	/**
	 * Gets the customer name.
	 *
	 * @param nxRequestDetail the nx request detail
	 * @param nxSolutionDetail the nx solution detail
	 * @return the customer name
	 */
	private String getCustomerName(NxRequestDetails nxRequestDetail, NxSolutionDetail nxSolutionDetail) {
		String customerName = "NA";
		
		try {
			ManageBillDataInv invRequest = mapper.readValue(nxRequestDetail.getAcctCriteria(), ManageBillDataInv.class);
			
			if (null != invRequest.getManageBillingPriceInventoryDataRequest().getCustomer_name()) {
				customerName = invRequest.getManageBillingPriceInventoryDataRequest().getCustomer_name();
			}
			
		} catch (Exception e) {
			logger.error("Exception occured while getting acctCriteria" + e);
		}
		if((customerName == null || "NA".equalsIgnoreCase(customerName)) && null != nxSolutionDetail.getCustomerName()) {
			customerName = nxSolutionDetail.getCustomerName();
		}
		if(customerName == null || "NA".equalsIgnoreCase(customerName)) {
			customerName = nxSolutionDetail.getNxsDescription();
		}
		return customerName;
	}


	/* (non-Javadoc)
	 * @see com.att.sales.nexxus.service.MailService#dataUploadMailNotification(java.util.Map, java.lang.String)
	 */
	@Override
	public MailResponse dataUploadMailNotification(Map<String,Object> inputmap,String userId) throws SalesBusinessException {
		String jsonPayLoad = null;
		try {
			jsonPayLoad = mapper.writeValueAsString(prepareDataUploadMailRequest(inputmap,userId));
		} catch (Exception e) {
			logger.error("SERVICE LOG ::  MAPPING REQUEST PARSE ERROR {} ", e);

		}
		return dme.callMailNotificationDME2(jsonPayLoad);
	}


	/* (non-Javadoc)
	 * @see com.att.sales.nexxus.service.MailService#prepareDataUploadMailRequest(java.util.Map, java.lang.String)
	 */
	@Override
	public MailServiceRequest prepareDataUploadMailRequest(Map<String, Object> inputmap, String userId) {
		Long littleProdId=null!=inputmap.get(DataUploadConstants.LITTLE_PROD_ID)?
				Long.valueOf(inputmap.get(DataUploadConstants.LITTLE_PROD_ID).toString()):null;
		String email = null;
		String status=getStatus(inputmap);
		String unMatchedSecondaryKeys=inputmap.containsKey(DataUploadConstants.UNMATCHED_SECONDARY_KES)?
				String.valueOf(DataUploadConstants.UNMATCHED_SECONDARY_KES):null;
		
		email = userId + StringConstants.ATT_EXTN ;
	    MailServiceRequest mailServiceRequest = new MailServiceRequest();
		mailServiceRequest.setMailsource(StringConstants.ADOPT);
		mailServiceRequest.setToMailList(email);
		mailServiceRequest.setFromMailList(environment.getProperty("mail.notify.from"));
		
		
		if (status.equals(DataUploadConstants.SUCCESS)) {
			String emailSuccess =  environment.getProperty("dataUpload.mail.notify.success.body");
			emailSuccess =  emailSuccess.replaceAll("fName", userId );
			emailSuccess =	emailSuccess.replaceAll("littleProdId",String.valueOf(littleProdId));	
			mailServiceRequest.setEmailSub(environment.getProperty("dataUpload.mail.notify.success.subject")
					.replaceAll("littleProdId", String.valueOf(littleProdId)).
					replaceAll(StringConstants.CUSTOMER_NAME, userId));
			mailServiceRequest.setEmailBody(emailSuccess);
			
		} else {
			String emailFail = environment.getProperty("dataUpload.mail.notify.failed.body");
			emailFail = emailFail.replaceAll("fName", userId );
			emailFail = emailFail.replaceAll("littleProdId", String.valueOf(littleProdId) );
			emailFail = emailFail.replaceAll("secondaryKeys",String.valueOf(unMatchedSecondaryKeys));
			mailServiceRequest.setEmailSub(environment.getProperty("dataUpload.mail.notify.failed.subject")
					.replaceAll("littleProdId", String.valueOf(littleProdId)).
					replaceAll(StringConstants.CUSTOMER_NAME, userId));
			mailServiceRequest.setEmailBody(emailFail);
		}
		mailServiceRequest.setCoorelationId(littleProdId);
		return mailServiceRequest;
	}
	
	/**
	 * Gets the status.
	 *
	 * @param inputmap the inputmap
	 * @return the status
	 */
	protected String getStatus(Map<String,Object> inputmap) {
		if(inputmap.containsKey(DataUploadConstants.STATUS) && null!=inputmap.get(DataUploadConstants.STATUS)) {
			String status=(String)inputmap.get(DataUploadConstants.STATUS);
			return status;
		}
		return DataUploadConstants.FAILED;
	}
	
	/* (non-Javadoc)
	 * @see com.att.sales.nexxus.service.MailService#prepareAndSendMailForPEDRequest(com.att.sales.nexxus.accesspricing.model.TransmitDesignDataResponse)
	 */
	@Override
	public ServiceResponse prepareAndSendMailForPEDRequest(Long solutionId,String attId) throws SalesBusinessException {
		
		MailResponse mailResp =null;
		if (null!=solutionId) {
			NxTeam nxTeam = repository.getNxTeam(solutionId, attId);
			if(null!=nxTeam && StringUtils.isNotEmpty(nxTeam.getIsPryMVG()) && nxTeam.getIsPryMVG().equalsIgnoreCase("Y")) {
				String email=nxTeam.getAttuid()+StringConstants.ATT_EXTN;
				MailServiceRequest mailServiceRequest = new MailServiceRequest();
				mailServiceRequest.setMailsource(StringConstants.ADOPT);
				mailServiceRequest.setToMailList(email);
				mailServiceRequest.setFromMailList(environment.getProperty("soe.mail.notify.from"));
				mailServiceRequest.setEmailSub(environment.getProperty("soe.mail.notify.subject"));
				String mailBody = environment.getProperty("soe.mail.notify.body");
				mailBody = mailBody.replace("fName", StringUtils.isNotEmpty(nxTeam.getfName())?nxTeam.getfName():"");
				mailBody = mailBody.replace("lName", StringUtils.isNotEmpty(nxTeam.getlName())?nxTeam.getlName():"");
				mailBody = mailBody.replace("solnId", solutionId.toString());
				mailServiceRequest.setEmailBody(mailBody);
				mailServiceRequest.setCoorelationId(solutionId);
				String jsonPayLoad = null;
				try {
					jsonPayLoad = mapper.writeValueAsString(mailServiceRequest);
				} catch (Exception e) {
					logger.error("SERVICE LOG ::  MAPPING REQUEST PARSE ERROR {} ", e);
				}
				mailResp = dme.callMailNotificationDME2(jsonPayLoad);
			}
		}
		return mailResp;
	}
	
	
	@Override
	public MailResponse prepareAndSendMailForBulkuploadEthTokensRequest(Long solutionId, String attId,String solutionName,int status) throws SalesBusinessException {
		
		MailResponse mailResp =null;
		String email=null;
		List<String> allIds=  new ArrayList<String>();
		if (null!=solutionId) {
			
			NxTeam nxTeam = repository.getNxTeam(solutionId, attId);
			List<NxTeam> team = repository.findByNxSolutionId(solutionId);
			
			allIds = team.stream().filter(nx -> nx.getAttuid() != null).map(nx -> nx.getAttuid())
				.collect(Collectors.toList());
			
			email = allIds.stream().filter(m -> m != null).map(m -> m + StringConstants.ATT_EXTN)
				.collect(Collectors.joining(","));
			
			if(null!=nxTeam && StringUtils.isNotEmpty(nxTeam.getIsPryMVG()) && nxTeam.getIsPryMVG().equalsIgnoreCase("Y")) {
				MailServiceRequest mailServiceRequest = new MailServiceRequest();
				mailServiceRequest.setMailsource(StringConstants.ADOPT);
				mailServiceRequest.setToMailList(email);
				mailServiceRequest.setFromMailList(environment.getProperty("mail.notify.from"));
				String mailBody;
				if(status == 1) {
					mailServiceRequest.setEmailSub(environment.getProperty("bulupload.ethtoken.mail.notify.success.subject"));
					mailBody = environment.getProperty("bulupload.ethtoken.mail.notify.success.body");
				} else if(status == 2) {
					mailServiceRequest.setEmailSub(environment.getProperty("bulupload.ethtoken.mail.notify.failed.subject"));
					mailBody = environment.getProperty("bulupload.ethtoken.mail.notify.failed.body");
				} else {
					mailServiceRequest.setEmailSub(environment.getProperty("bulupload.ethtoken.mail.notify.partial.subject"));
					mailBody = environment.getProperty("bulupload.ethtoken.mail.notify.partial.body");
				}
							
				mailBody = mailBody.replace("fName", StringUtils.isNotEmpty(nxTeam.getfName())?nxTeam.getfName():"Nexxus");
				mailBody = mailBody.replace("lName", StringUtils.isNotEmpty(nxTeam.getlName())?nxTeam.getlName():"Team");
				mailBody = mailBody.replace("solutionName", solutionName);
				mailBody = mailBody.replace("solnId", (solutionId!=null ? solutionId.toString() : "0"));
				mailServiceRequest.setEmailBody(mailBody);
				mailServiceRequest.setCoorelationId(solutionId);
				String jsonPayLoad = null;
				try {
					jsonPayLoad = mapper.writeValueAsString(mailServiceRequest);
				} catch (Exception e) {
					logger.error("SERVICE LOG ::  MAPPING REQUEST PARSE ERROR {} ", e);
				}
				mailResp = dme.callMailNotificationDME2(jsonPayLoad);
			}
		}
		return mailResp;
	}
	
	@Override
	public MailResponse prepareAndSendMailForServiceAlert(String serviceName, long errorCount, int interval, long nxWebServiceErrorId) throws SalesBusinessException {
		Map<String, String> mailProperty = nxMyPriceRepositoryServce.getDescDataFromLookup("SERVICE_ERROR_ALERT_EMAIL");
		String toMail = mailProperty.get("TO_MAIL");
		String fromMail = mailProperty.get("FROM_MAIL");
		String env = mailProperty.get("ENV");
		String sub = mailProperty.get("SUB");
		String body = mailProperty.get("BODY");
		sub = sub.replace("<service>", serviceName).replace("<count>", String.valueOf(errorCount)).replace("<interval>", String.valueOf(interval)).replace("<env>", env);
		MailServiceRequest mailServiceRequest = new MailServiceRequest();
		mailServiceRequest.setMailsource(StringConstants.ADOPT);
		mailServiceRequest.setToMailList(toMail);
		mailServiceRequest.setFromMailList(fromMail);
		mailServiceRequest.setEmailSub(sub);
		mailServiceRequest.setEmailBody(body);
		mailServiceRequest.setCoorelationId(nxWebServiceErrorId);
		String jsonPayLoad = null;
		try {
			jsonPayLoad = mapper.writeValueAsString(mailServiceRequest);
		} catch (Exception e) {
			logger.error("SERVICE LOG ::  MAPPING REQUEST PARSE ERROR {} ", e);
		}
		MailResponse mailResponse = dme.callMailNotificationDME2(jsonPayLoad);
		return mailResponse;
	}

	public ServiceResponse sendMail(SendMailRequest request ) throws SalesBusinessException{
		
		MailServiceRequest mailServiceRequest = new MailServiceRequest();
		mailServiceRequest.setMailsource(StringConstants.ADOPT);
		mailServiceRequest.setToMailList(request.getToId());
		mailServiceRequest.setFromMailList(request.getFromId());
		mailServiceRequest.setEmailSub(request.getSubject());
		mailServiceRequest.setEmailBody(request.getBody());
		mailServiceRequest.setCoorelationId(System.currentTimeMillis());
		
		ObjectMapper mapper = new ObjectMapper();
		String jsonPayLoad = null;
		
		try {
			jsonPayLoad = mapper.writeValueAsString(mailServiceRequest);
		} catch (Exception e) {
			logger.error("SERVICE LOG ::  MAPPING REQUEST PARSE ERROR {} ", e);
		}

		MailResponse mailResp = dme.callMailNotificationDME2(jsonPayLoad);
		
		return mailResp;
	}

}
