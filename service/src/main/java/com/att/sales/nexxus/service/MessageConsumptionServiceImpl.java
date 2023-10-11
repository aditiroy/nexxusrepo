package com.att.sales.nexxus.service;

import java.math.BigDecimal;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Component;

import com.att.sales.exampledomainobject.model.EDFMRResponse;
import com.att.sales.framework.exception.SalesBusinessException;
import com.att.sales.framework.model.ServiceMetaData;
import com.att.sales.framework.model.ServiceResponse;
import com.att.sales.nexxus.constant.CommonConstants;
import com.att.sales.nexxus.constant.InrConstants;
import com.att.sales.nexxus.constant.StringConstants;
import com.att.sales.nexxus.dao.model.NxInrActivePods;
import com.att.sales.nexxus.dao.model.NxInrDmaapAudit;
import com.att.sales.nexxus.dao.model.NxOutputFileModel;
import com.att.sales.nexxus.dao.model.NxRequestDetails;
import com.att.sales.nexxus.dao.repository.NxInrActivePodsRepository;
import com.att.sales.nexxus.dao.repository.NxInrDmaapAuditRepository;
import com.att.sales.nexxus.dao.repository.NxRequestDetailsRepository;
import com.att.sales.nexxus.dmaap.mr.util.IDmaapMRSubscriber;
import com.att.sales.nexxus.inr.InrJsonServiceImpl;
import com.att.sales.nexxus.inr.InrJsonServiceRequest;
import com.att.sales.nexxus.inr.InvPriceJsonImpl;
import com.att.sales.nexxus.inr.InvPriceJsonRequest;
import com.att.sales.nexxus.inr.InvPriceJsonResponse;
import com.att.sales.nexxus.model.MailRequest;
import com.att.sales.nexxus.p8.P8Service;
import com.att.sales.nexxus.util.ThreadMetaDataUtil;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * The Class MessageConsumptionServiceImpl.
 *
 * @author(dc650q) 
 */

@Component
public class MessageConsumptionServiceImpl implements MessageCosumptionService {

	/** The logger. */
	private static Logger logger = LoggerFactory.getLogger(MessageCosumptionService.class);

	/** The Constant OLD_FILE_DESC. */
	private static final String OLD_FILE_DESC = "Please check old file : ";

	/** The env. */
	@Autowired
	private Environment env;

	/** The dmaap MR subscriber service. */
	@Autowired
	private IDmaapMRSubscriber dmaapMRSubscriberService;

	/** The nx request details repository. */
	@Autowired
	private NxRequestDetailsRepository nxRequestDetailsRepository;

	/** The mapper. */
	@Autowired
	private ObjectMapper mapper;

	/** The p 8 service. */
	@Autowired
	private P8Service p8Service;

	/** The inr processing service. */
	@Autowired
	private InrProcessingService inrProcessingService;
	
	/** The mail service. */
	@Autowired
	private MailServiceImpl mailService;

	@Value("${edf.dmaap.enabled:N}")
	private String edfDmaapEnabled;
	
	@Value("${inr.dmaap.process.enabled:N}")
	private String inrDmaapProcessEnabled;
	
	@Autowired
	private NxInrDmaapAuditRepository nxInrDmaapAuditRepository;
	
	@Autowired
	private NxInrActivePodsRepository nxInrActivePodsRepository;
		
	@Autowired
	private InrQualifyService inrQualifyService;
	
	@Value("${inr.active.pods.enabled:N}")
	private String inrActivePodsEnabled;
	
	@Value("${inr.bulk.active.pods.enabled:N}")
	private String inrBulkActivePodsEnabled;
	
	@Value("${update.podname.enabled:N}")
	private String updatePodnameEnabled;
	
	@Value("#{new Integer('${pod.heartbeat.time.in.min}')}")
	private int podHeartbeatTimeInMin;
	
	@Value("#{new Integer('${inr.req.stuck.time.in.hour}')}")
	private int inrReqStuckTimeInhour;
	
	@Value("${inr.usrp.active.pods.enabled:N}")
	private String usrpActivePodsEnabled;
	
	@Value("${usrp.dmaap.process.enabled:N}")
	private String usrpDmaapProcessEnabled;
	
	@Autowired
	private InrJsonServiceImpl inrJsonServiceImpl;
	
	@Autowired
	@Qualifier("taskscheduler")
	private ThreadPoolTaskScheduler taskScheduler;
	
	@Value("#{new Integer('${usrp.process.timeout.in.min}')}")
	private int usrpProcessTimeoutTimeInMin;
	
	@Value("${usrp.reassign.pods.enabled:N}")
	private String usrpReassignPodsEnabled;
	
	@Value("#{new Integer('${usrp.req.stuck.time.in.hour}')}")
	private int usrpReqStuckTimeInhour;

	@Value("${python.reassign.pods.enabled:N}")
	private String pythonReassignPodsEnabled;
	
	@Value("#{new Integer('${python.req.stuck.time.in.hour}')}")
	private int pythonsReqStuckTimeInhour;
	
	@Autowired
	private RetrieveBillingProductDetail retrieveBillingProductDetail;
	
	@Autowired
	private InvPriceJsonImpl invPriceJsonImpl;

	@Value("#{new Integer('${usrp.inrbeta.process.timeout.in.min}')}")
	private int usrpInrbetaProcessTimeoutInMin;
	
	@Value("#{new Integer('${usrp.inrbeta.req.stuck.time.in.hour}')}")
	private int usrpInrBetaReqStuckTimeInhour;


	/* (non-Javadoc)
	 * @see com.att.sales.nexxus.service.MessageCosumptionService#getMessage()
	 */
	@Override
	@Scheduled(fixedDelay = 2 * 60 * 1000) // 2 mins
	public void getMessage() {

		List<String> messages = null;
		EDFMRResponse responseObj = null;
		try {
			if("Y".equalsIgnoreCase(edfDmaapEnabled)) {
				messages = dmaapMRSubscriberService.retrieveMessage(env.getProperty("dmaap.subscriber.topic"),
						env.getProperty("dmaap.subscriber.groupname"), env.getProperty("dmaap.subscriber.edf.host"));
	
				if (!messages.isEmpty()) {
					for (int i = 0; i < messages.size(); i++) {
						responseObj = mapper.readValue(messages.get(i), EDFMRResponse.class);
						NxInrDmaapAudit nxInrDmaapAudit = new NxInrDmaapAudit();
						NxRequestDetails nxRequets = nxRequestDetailsRepository.findNxSolutionIdByEdfAckIdAndActiveYn(responseObj.getRequestId(), StringConstants.CONSTANT_Y);
						/*NxInrDmaapAudit nxInrDmaap = nxInrDmaapAuditRepository.findByNxSolutionIdAndStatusAndTransactionType(nxRequets.getNxSolutionDetail().getNxSolutionId(), InrConstants.DMAAP_STATUS, InrConstants.EDF_INR_DMAAP);
						if(nxInrDmaap != null) {
							nxInrDmaapAudit.setNxPodName(nxInrDmaap.getNxPodName());
						}else {
							nxInrDmaapAudit.setNxPodName(getHostName());
						}*/
						nxInrDmaapAudit.setNxMessage(messages.get(i));
						nxInrDmaapAudit.setNxCorrelationId(responseObj.getRequestId());
						nxInrDmaapAudit.setNxProcessStatus("N");
						nxInrDmaapAudit.setNxTransactionType(InrConstants.EDF_INR_DMAAP);
						nxInrDmaapAudit.setNxSolutionId(nxRequets.getNxSolutionDetail().getNxSolutionId());
						
						updatePodName(InrConstants.EDF_INR_DMAAP, nxInrDmaapAudit);
						nxInrDmaapAuditRepository.save(nxInrDmaapAudit);
					}
				}
			}

		} catch (Exception x) {
			logger.error("Exception while getting Dmaap Message Router messages:%s", x);
		}

	}
	
	/**
	 * Send mail notification.
	 *
	 * @param reqDetails the req details
	 */
	public void sendMailNotification(NxRequestDetails reqDetails) {
		logger.info("Inside sendMailNotification method  {}", "");
		if (reqDetails.getNxReqId() != null) {
			Map<String, Object> requestParams = new HashMap<>();
			String conversationId = String.format("NEXXUSMAILREQUEST%s", reqDetails.getNxReqId());
			requestParams.put(ServiceMetaData.XCONVERSATIONID, conversationId);
			requestParams.put(ServiceMetaData.REST_REQUEST_START_TIME, System.currentTimeMillis());
			MailRequest request = new MailRequest();
			request.setNxRequestId(reqDetails.getNxReqId());
			try {
				ThreadMetaDataUtil.initThreadMetaDataIfNull(requestParams);
				mailService.mailNotification(request);
			} catch (SalesBusinessException e) {
				logger.error("EXCEPTION IN CALLING MAIL API", e);
			} finally {
				ThreadMetaDataUtil.destroyThreadMetaData();
			}
		}
	}

	/**
	 * Gets the file name.
	 *
	 * @param eDFMRResponse the e DFMR response
	 * @return the file name
	 */
	protected String getFileName(EDFMRResponse eDFMRResponse) {
		if (eDFMRResponse.getMessage() != null) {
			String message = eDFMRResponse.getMessage();
			int index = message.indexOf(OLD_FILE_DESC);
			if (index >= 0) {
				return message.substring(index + OLD_FILE_DESC.length());
			}
		}
		return eDFMRResponse.getOutputfileName();
	}

	@Scheduled(fixedDelay = 3 * 60 * 1000)
	public void processInrDmaap() {
		if("Y".equalsIgnoreCase(inrDmaapProcessEnabled)) {
			EDFMRResponse responseObj = null;
		//	List<NxInrDmaapAudit> nxInrDmaapAudits = nxInrDmaapAuditRepository.getNewDmaapDetails("N",  InrConstants.EDF_INR_DMAAP);
			List<NxInrDmaapAudit> nxInrDmaapAudits = nxInrDmaapAuditRepository.getNewDmaapDetailsByPodName("N",  InrConstants.EDF_INR_DMAAP, getHostName());
			for(NxInrDmaapAudit nxInrDmaapAudit : nxInrDmaapAudits) {
				try {
					logger.info("dmaap message is: {}", nxInrDmaapAudit.getNxMessage());
					
					//Updating dmaap processing status to In Progress
					nxInrDmaapAuditRepository.updateDmaapProcessStatus("IP", nxInrDmaapAudit.getId());
					
					responseObj = mapper.readValue(nxInrDmaapAudit.getNxMessage(), EDFMRResponse.class);
					List<NxRequestDetails> nxRequestDetail = nxRequestDetailsRepository.findByEdfAckIdAndActiveYn(responseObj.getRequestId(), "Y");
					if (!nxRequestDetail.isEmpty()) {
						NxRequestDetails nxRequestDetailsFrist = nxRequestDetail.get(0);
						String fileName = this.getFileName(responseObj);
						nxRequestDetailsFrist.setFileName(fileName);
						nxRequestDetailsFrist.setDmaapMsg(nxInrDmaapAudit.getNxMessage());
						logger.info("retrieving file <{}> from p8", fileName);
						p8Service.lookupDocumentInP8(fileName);
						logger.info("trigger InrProcessingService");
						inrProcess(nxRequestDetail, nxInrDmaapAudit, fileName);
					}
					if (!nxRequestDetail.isEmpty()) {
						sendMailNotification(nxRequestDetail.get(0));
					}
					//Updating dmaap processing status to Success
					nxInrDmaapAuditRepository.updateDmaapProcessStatus("S", nxInrDmaapAudit.getId());
				} catch(Exception e) {
					//Updating dmaap processing status to Failed
					nxInrDmaapAuditRepository.updateDmaapProcessStatus("F", nxInrDmaapAudit.getId());
					logger.info("INR processing failed for id {} with exception {}", nxInrDmaapAudit.getId(), e.getMessage());
					logger.info("Exception", e);
				}
				try {
					TimeUnit.SECONDS.sleep(20);
				} catch (InterruptedException e) {
					logger.info(e.getMessage());
				}
			}
		}
		
		if("Y".equalsIgnoreCase(usrpDmaapProcessEnabled)) {
			//old usrp processing flow
			//processPriceJson();
			//for the new dw integation approach
			inrBetaJsonProcessing();
		}

	}
	protected void processPriceJson() {
		List<NxInrDmaapAudit> nxUSRPDmaapAudits = nxInrDmaapAuditRepository.getNewDmaapDetailsByPodName("N",  InrConstants.USRP, getHostName());
		if(CollectionUtils.isNotEmpty(nxUSRPDmaapAudits)) {
			for(NxInrDmaapAudit nxInrDmaapAudit : nxUSRPDmaapAudits) {
				ExecutorService service = null;
				try {
					logger.info("nx correlation id for usrp is {}",nxInrDmaapAudit.getNxCorrelationId());
					
					//Updating dmaap processing status to In Progress
					nxInrDmaapAuditRepository.updateDmaapProcessStatus("IP", nxInrDmaapAudit.getId());

					// call usrpDesign, 60 mins timeout
					InrJsonServiceRequest request=new InrJsonServiceRequest();
					request.setId(Long.parseLong(nxInrDmaapAudit.getNxCorrelationId()));
					service = Executors.newSingleThreadExecutor();
					Future<ServiceResponse> res = service.submit(() -> inrJsonServiceImpl.inrJsonProcess(request));
					res.get(usrpProcessTimeoutTimeInMin, TimeUnit.MINUTES);
					
					nxInrDmaapAuditRepository.updateDmaapProcessStatus("S", nxInrDmaapAudit.getId());
				
				} catch(Exception e) {
					//Updating dmaap processing status to Failed
					nxInrDmaapAuditRepository.updateDmaapProcessStatus("F", nxInrDmaapAudit.getId());
					logger.info("USRP processing failed for id {} with exception {}", nxInrDmaapAudit.getId(), e.getMessage());
					logger.info("Exception", e);
				} finally {
					if (service != null) {
						service.shutdown();
					}
				}
				try {
					TimeUnit.SECONDS.sleep(20);
				} catch (InterruptedException e) {
					logger.info(e.getMessage());
				}
			}
		}
	}
	
	protected void inrProcess(NxRequestDetails nxRequestDetail) {
		inrProcess(Arrays.asList(nxRequestDetail), null, null);
	}

	protected void inrProcess(List<NxRequestDetails> nxRequestDetail, NxInrDmaapAudit nxInrDmaapAudit, String fileName) {
		NxRequestDetails nxRequestDetailsFrist = nxRequestDetail.get(0);
		NxOutputFileModel nxOutputFileModel = inrProcessingService.createInrNexusOutput(nxRequestDetailsFrist, null);
		for (int i = 1; i < nxRequestDetail.size(); i++) {
			NxRequestDetails nxRequestDetails = nxRequestDetail.get(i);
			nxRequestDetails.setFileName(fileName);
			nxRequestDetails.setDmaapMsg(nxInrDmaapAudit.getNxMessage());
			nxRequestDetails.setStatus(nxRequestDetailsFrist.getStatus());
			nxRequestDetails.setModifedDate(new Date());
			List<NxOutputFileModel> nxOutputFileModels = nxRequestDetails.getNxOutputFiles();
			NxOutputFileModel copy = null;
			if(CollectionUtils.isNotEmpty(nxOutputFileModels)) {
				copy = nxOutputFileModels.get(0)!=null?nxOutputFileModels.get(0):null;
			}
			if(null == copy) {
				copy = new NxOutputFileModel(nxOutputFileModel);
			}else {
				updateNxOutputFileModel(nxOutputFileModel, copy);
				nxRequestDetails.setNxOutputFiles(null);
			}
			nxRequestDetails.addNxOutputFiles(copy);
			nxRequestDetailsRepository.saveAndFlush(nxRequestDetails);
			inrQualifyService.inrQualifyCheck(nxRequestDetails.getNxReqId(), true, null);
		}
		nxRequestDetailsFrist.addNxOutputFiles(nxOutputFileModel);
		nxRequestDetailsRepository.saveAndFlush(nxRequestDetailsFrist);
		inrQualifyService.inrQualifyCheck(nxRequestDetailsFrist.getNxReqId(), true, null);
	}
	
	public void updateNxOutputFileModel(NxOutputFileModel copy, NxOutputFileModel newModel) {
		newModel.setIntermediateJson(copy.getIntermediateJson());
		newModel.setOutput(copy.getOutput());
		newModel.setOutputFile(copy.getOutputFile());
		newModel.setFileName(copy.getFileName());
		newModel.setFileType(copy.getFileType());
		newModel.setModifiedDate(copy.getModifiedDate());
		newModel.setStatus(copy.getStatus());
		newModel.setInventoryJson(copy.getInventoryJson());
		newModel.setFallOutData(copy.getFallOutData());
		newModel.setMpOutputJson(copy.getMpOutputJson());
		newModel.setNxSiteIdInd(StringConstants.CONSTANT_N);
		newModel.setCdirData(copy.getCdirData());
		newModel.setInventoryFileSize(copy.getInventoryFileSize());
	}
	
	@PreDestroy
	public void cleanUpPodName() {
		logger.info("Start : clearing pod names in dmaap audit table on shutdown");
		int count = nxInrDmaapAuditRepository.updatePodName(getHostName());
		NxInrActivePods nxInrActivePod = nxInrActivePodsRepository.findByPodName(getHostName());
		if(nxInrActivePod != null) {
			nxInrActivePodsRepository.delete(nxInrActivePod);
		}
		logger.info("End : clearing pod names in dmaap audit table on shutdown {}", count);
	}
	
	/*@EventListener(ApplicationReadyEvent.class)
	public void populatePodNameOnStart() {
		
	}*/
	
	//@Scheduled(fixedDelay = 2 * 60 * 1000)
	public void createNxInrActivePod() {
		if(StringConstants.CONSTANT_Y.equalsIgnoreCase(inrActivePodsEnabled) || StringConstants.CONSTANT_Y.equalsIgnoreCase(inrBulkActivePodsEnabled)
				|| (StringConstants.CONSTANT_Y.equalsIgnoreCase(usrpActivePodsEnabled))) {
			logger.info("Start : populating pod names in active pods table");
			NxInrActivePods nxInrActivePod = nxInrActivePodsRepository.findByPodName(getHostName());
			if(nxInrActivePod != null) {
				nxInrActivePod.setLastHeartbeat(new Date());
			}else {
				nxInrActivePod = new NxInrActivePods();
				nxInrActivePod.setPodName(getHostName());
				nxInrActivePod.setLastHeartbeat(new Date());
				if(StringConstants.CONSTANT_Y.equalsIgnoreCase(inrActivePodsEnabled)){
					nxInrActivePod.setPodType( InrConstants.EDF_INR_DMAAP);
				}else if(StringConstants.CONSTANT_Y.equalsIgnoreCase(inrBulkActivePodsEnabled)) {
					nxInrActivePod.setPodType(InrConstants.EDF_BULK_INR_DMAAP);
				}else if(StringConstants.CONSTANT_Y.equalsIgnoreCase(usrpActivePodsEnabled)) {
					//USRP_POD for the new inr beta dw integration flow
					nxInrActivePod.setPodType(InrConstants.USRP_POD);
				}
				/*USRP_INR_DMAAP  was for old dw inr beta flow
				 * else if(StringConstants.CONSTANT_Y.equalsIgnoreCase(usrpActivePodsEnabled)) {
					nxInrActivePod.setPodType(InrConstants.USRP_INR_DMAAP);
				}*/
			}
			nxInrActivePodsRepository.saveAndFlush(nxInrActivePod);
			logger.info("End : populating pod names in active pods table");
		}
	}
	
	//@Scheduled(fixedDelay = 3 * 60 * 1000)
/**	public void updatePodName() {
		try {
			if(StringConstants.CONSTANT_Y.equalsIgnoreCase(updatePodnameEnabled)){
				logger.info("updatePodName : processing for standalone inr request");
				updatePodName(InrConstants.EDF_INR_DMAAP);
				logger.info("updatePodName : processing for bulk inr request");
				updatePodName(InrConstants.EDF_BULK_INR_DMAAP);
				logger.info("End : updatePodName");
			}
		}catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void updatePodName(String transactionType) {
		Date dateThreshold = Date.from(Instant.now().minus(podHeartbeatTimeInMin, ChronoUnit.MINUTES));
		//List<NxInrActivePods> nxInrActivePods = nxInrActivePodsRepository.getNxInrActivePods(dateThreshold);
		Map<String, Long> podCount = new HashMap<String, Long>();
		List<Object[]> nxInrActivePods = nxInrActivePodsRepository.getPods(dateThreshold, transactionType);
		Map<Long, String> solPodName = new HashMap<Long, String>();
		if(nxInrActivePods.size() > 0) {
			for (Object[] row : nxInrActivePods) {
				podCount.put((String) row[0], ((BigDecimal) row[1]).longValue());
			}

			List<NxInrDmaapAudit> nxInrDmaaps = nxInrDmaapAuditRepository.findByTransactionType(transactionType);
			if(CollectionUtils.isNotEmpty(nxInrDmaaps)) {
				for(NxInrDmaapAudit nxInrDmaapAudit : nxInrDmaaps) {
					if(!solPodName.containsKey(nxInrDmaapAudit.getNxSolutionId())) {
						NxInrDmaapAudit nxInrDmaap = nxInrDmaapAuditRepository.findByNxSolutionIdAndStatusAndTransactionType(nxInrDmaapAudit.getNxSolutionId(), InrConstants.DMAAP_STATUS, transactionType);
						if(nxInrDmaap != null) {
							solPodName.put(nxInrDmaapAudit.getNxSolutionId(), nxInrDmaap.getNxPodName());
						}
					}
					if(solPodName.containsKey(nxInrDmaapAudit.getNxSolutionId())) {
						String hostName = solPodName.get(nxInrDmaapAudit.getNxSolutionId());
						nxInrDmaapAudit.setNxPodName(hostName);
						if(podCount.containsKey(hostName)) {
							podCount.put(hostName, podCount.get(hostName).longValue()+1);
						}
					}else {
						String hostName = podCount.entrySet().stream().min(Comparator.comparingLong(Map.Entry::getValue)).get().getKey();
						nxInrDmaapAudit.setNxPodName(hostName);
						if(podCount.containsKey(hostName)) {
							podCount.put(hostName, podCount.get(hostName).longValue()+1);
						}
						solPodName.put(nxInrDmaapAudit.getNxSolutionId(), hostName);
					}
				}
				nxInrDmaapAuditRepository.save(nxInrDmaaps);
			}
			
			// any request where status is IP for around 2 hours  - (this would slove the OOM issue that termiates the job in middle)
			Date dateThresholdforReq = Date.from(Instant.now().minus(inrReqStuckTimeInhour, ChronoUnit.HOURS));
			List<NxInrDmaapAudit> pendingNxInrDmaaps = nxInrDmaapAuditRepository.findByTransactionTypeAndProcessStatus(transactionType, "IP", dateThresholdforReq);
			if(CollectionUtils.isNotEmpty(pendingNxInrDmaaps)) {
				Set<Long> solnIds = pendingNxInrDmaaps.stream().map(p -> p.getNxSolutionId()).filter(s -> s!= null).collect(Collectors.toSet());
				if(CollectionUtils.isNotEmpty(solnIds)) {
					for(Long solnId : solnIds) {
						String hostName = podCount.entrySet().stream().min(Comparator.comparingLong(Map.Entry::getValue)).get().getKey();
						Long count = pendingNxInrDmaaps.stream().filter(p -> p.getNxSolutionId() != null && (p.getNxSolutionId().longValue() == solnId.longValue())).peek( i -> {i.setNxPodName(hostName); i.setNxProcessStatus("N");}).count();
						podCount.put(hostName, podCount.get(hostName).longValue()+count.longValue());
					}
					nxInrDmaapAuditRepository.save(pendingNxInrDmaaps);
				}
			}
		}else {
			logger.info("updatePodName :: no active pods to update podname in dmaap table");
		}
		podCount = null;
		solPodName = null;
	}*/
	
	public void updatePodName(String transactionType, NxInrDmaapAudit nxInrDmaapAudit) {
		Date dateThreshold = Date.from(Instant.now().minus(podHeartbeatTimeInMin, ChronoUnit.MINUTES));
		//List<NxInrActivePods> nxInrActivePods = nxInrActivePodsRepository.getNxInrActivePods(dateThreshold);
		Map<String, Long> podCount = new HashMap<String, Long>();
		List<Object[]> nxInrActivePods = nxInrActivePodsRepository.getPods(dateThreshold, transactionType);
	//	Map<Long, String> solPodName = new HashMap<Long, String>();
		if(nxInrActivePods.size() > 0) {
			for (Object[] row : nxInrActivePods) {
				podCount.put((String) row[0], ((BigDecimal) row[1]).longValue());
			}

			//List<NxInrDmaapAudit> nxInrDmaaps = nxInrDmaapAuditRepository.findByTransactionType(transactionType);
			//if(CollectionUtils.isNotEmpty(nxInrDmaaps)) {
				//for(NxInrDmaapAudit nxInrDmaapAudit : nxInrDmaaps) {
					//if(!solPodName.containsKey(nxInrDmaapAudit.getNxSolutionId())) {
						NxInrDmaapAudit nxInrDmaap = nxInrDmaapAuditRepository.findByNxSolutionIdAndStatusAndTransactionType(nxInrDmaapAudit.getNxSolutionId(), InrConstants.DMAAP_STATUS, transactionType);
						
						if(nxInrDmaap != null && podCount.keySet().contains(nxInrDmaap.getNxPodName())) {
							nxInrDmaapAudit.setNxPodName(nxInrDmaap.getNxPodName());
						}else {
							String hostName = podCount.entrySet().stream().min(Comparator.comparingLong(Map.Entry::getValue)).get().getKey();
							nxInrDmaapAudit.setNxPodName(hostName);
						}
							/**solPodName.put(nxInrDmaapAudit.getNxSolutionId(), nxInrDmaap.getNxPodName());
						}
					}
					if(solPodName.containsKey(nxInrDmaapAudit.getNxSolutionId())) {
						String hostName = solPodName.get(nxInrDmaapAudit.getNxSolutionId());
						nxInrDmaapAudit.setNxPodName(hostName);
						if(podCount.containsKey(hostName)) {
							podCount.put(hostName, podCount.get(hostName).longValue()+1);
						}
					}else {
						String hostName = podCount.entrySet().stream().min(Comparator.comparingLong(Map.Entry::getValue)).get().getKey();
						nxInrDmaapAudit.setNxPodName(hostName);
						if(podCount.containsKey(hostName)) {
							podCount.put(hostName, podCount.get(hostName).longValue()+1);
						}
						solPodName.put(nxInrDmaapAudit.getNxSolutionId(), hostName);
					}**/
				//}
				//nxInrDmaapAuditRepository.save(nxInrDmaaps);
			//}
			
			// any request where status is IP for around 2 hours  - (this would slove the OOM issue that termiates the job in middle)
			Date dateThresholdforReq = Date.from(Instant.now().minus(inrReqStuckTimeInhour, ChronoUnit.HOURS));
			List<NxInrDmaapAudit> pendingNxInrDmaaps = 
					nxInrDmaapAuditRepository.findByTransactionTypeAndProcessStatusAndPodName(transactionType, InrConstants.DMAAP_STATUS, dateThresholdforReq, podCount.keySet());
			if(CollectionUtils.isNotEmpty(pendingNxInrDmaaps)) {
				Set<Long> solnIds = pendingNxInrDmaaps.stream().map(p -> p.getNxSolutionId()).filter(s -> s!= null).collect(Collectors.toSet());
				if(CollectionUtils.isNotEmpty(solnIds)) {
					for(Long solnId : solnIds) {
						String hostName = podCount.entrySet().stream().min(Comparator.comparingLong(Map.Entry::getValue)).get().getKey();
						Long count = pendingNxInrDmaaps.stream().filter(p -> p.getNxSolutionId() != null && (p.getNxSolutionId().longValue() == solnId.longValue())).peek( i -> {i.setNxPodName(hostName); i.setNxProcessStatus("N");}).count();
						podCount.put(hostName, podCount.get(hostName).longValue()+count.longValue());
					}
					nxInrDmaapAuditRepository.saveAll(pendingNxInrDmaaps);
				}
			}
		}else {
			nxInrDmaapAudit.setNxPodName(getHostName());
			logger.info("updatePodName :: no active pods to update podname in dmaap table");
		}
		podCount = null;
		//solPodName = null;
	}
	
	public String getHostName() {
		 try {
			return InetAddress.getLocalHost().getHostName();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	
	@PostConstruct
    public void test() {
		//run after 2 min
		taskScheduler.scheduleAtFixedRate(() -> {   
        	createNxInrActivePod(); 
        	}, 120000L);
    }
	
	// 2 hours
	@Scheduled(fixedRate = 3 * 60 * 60 * 1000) 
	public void reassignUSRPPodName() {
	/*	if (StringConstants.CONSTANT_Y.equalsIgnoreCase(usrpReassignPodsEnabled)) {
			logger.info("Reassign the usrp request, pod scheduler is running");
			reassignPod(usrpReqStuckTimeInhour,InrConstants.USRP_INR_DMAAP,InrConstants.USRP);
	
		}
		if (StringConstants.CONSTANT_Y.equalsIgnoreCase(pythonReassignPodsEnabled)) {
			logger.info("Reassign the python request, pod scheduler is running");
			reassignPod(pythonsReqStuckTimeInhour,InrConstants.PYTHON_POD,InrConstants.DW_DMAAP);
	
		}*/
		//for the new INR beta Dw integration flow
		if (StringConstants.CONSTANT_Y.equalsIgnoreCase(usrpReassignPodsEnabled)) {
			logger.info("Reassign the usrp request, pod scheduler is running");
			Set<String> transaction=new HashSet<>();
			reassignPod(usrpInrBetaReqStuckTimeInhour,InrConstants.USRP_POD,InrConstants.INR_BETA_JSON_CREATION);
		}

	}
	
	public void reassignPod(int requeststuckTimeInHr, String podType,String transactionType ) {
		try {	
		Date dateThresholdActivepods = Date.from(Instant.now().minus(podHeartbeatTimeInMin, ChronoUnit.MINUTES));
		List<Object[]> activePods = nxInrActivePodsRepository.getPods(dateThresholdActivepods, podType);
		Map<String, Long> podCount = new HashMap<String, Long>();
		if (activePods.size() > 0) {
			for (Object[] row : activePods) {
				podCount.put((String) row[0], ((BigDecimal) row[1]).longValue());
			}
			Date dateThresholdforStuckReq = Date.from(Instant.now().minus(requeststuckTimeInHr, ChronoUnit.HOURS));
			List<NxInrDmaapAudit> pendingInProgressNxInrDmaaps = nxInrDmaapAuditRepository
					.findByTransactionTypeAndProcessStatusAndPodName(transactionType,InrConstants.DMAAP_STATUS,dateThresholdforStuckReq,
							 podCount.keySet());
			List<NxInrDmaapAudit> pendingNewNxInrDmaaps = nxInrDmaapAuditRepository
					.getPendingNewDmaapDetails(transactionType,dateThresholdforStuckReq,
							 podCount.keySet());
			List<NxInrDmaapAudit> pendingNxInrDmaaps= new ArrayList<>();
			pendingNxInrDmaaps.addAll(pendingInProgressNxInrDmaaps);
			pendingNxInrDmaaps.addAll(pendingNewNxInrDmaaps);
			
			if (CollectionUtils.isNotEmpty(pendingNxInrDmaaps)) {
				Set<Long> solnIds = pendingNxInrDmaaps.stream().map(p -> p.getNxSolutionId()).filter(s -> s!= null).collect(Collectors.toSet());
				if(CollectionUtils.isNotEmpty(solnIds)) {
					for(Long solnId : solnIds) {
						String hostName = podCount.entrySet().stream().min(Comparator.comparingLong(Map.Entry::getValue)).get().getKey();
						Long count = pendingNxInrDmaaps.stream().filter(p -> p.getNxSolutionId() != null && 
								(p.getNxSolutionId().longValue() == solnId.longValue())).peek( i -> {i.setNxPodName(hostName); i.setNxProcessStatus("N");
								i.setCreatedTime(new Date()); i.setModifiedTime(null);}).count();
						podCount.put(hostName, podCount.get(hostName).longValue()+count.longValue());
					}
					nxInrDmaapAuditRepository.saveAll(pendingNxInrDmaaps);
				}
			}
			pendingNxInrDmaaps=null;
			pendingInProgressNxInrDmaaps=null;
			pendingNewNxInrDmaaps=null;			
		}
		podCount=null;
	}
	catch(Exception e) {
		logger.error("Exception while reassigning the usrp, python pods", e);
	}
}
	
	protected void inrBetaJsonProcessing() {
		List<NxInrDmaapAudit> nxUSRPDmaapAudits = nxInrDmaapAuditRepository.getNewDmaapDetailsByPodName("N",  InrConstants.INR_BETA_JSON_CREATION, getHostName());
		if(CollectionUtils.isNotEmpty(nxUSRPDmaapAudits)) {
			for(NxInrDmaapAudit nxInrDmaapAudit : nxUSRPDmaapAudits) {
				ExecutorService service = null;
				try {
					logger.info("nx request id for processing inr beta request {}",nxInrDmaapAudit.getNxCorrelationId());
					//Updating dmaap processing status to In Progress
					nxInrDmaapAuditRepository.updateDmaapProcessStatus("IP", nxInrDmaapAudit.getId());
					Long nxRequestId=Long.parseLong(nxInrDmaapAudit.getNxCorrelationId());
					
					service = Executors.newSingleThreadExecutor();
					Future<ServiceResponse> res = service.submit(() -> requestProcessing(nxRequestId));
					res.get(usrpInrbetaProcessTimeoutInMin, TimeUnit.MINUTES);
					
					nxInrDmaapAuditRepository.updateDmaapProcessStatus("S", nxInrDmaapAudit.getId());
				
				} catch(Exception e) {
					//Updating dmaap processing status to Failed
					nxInrDmaapAuditRepository.updateDmaapProcessStatus("F", nxInrDmaapAudit.getId());
					logger.info("USRP processing failed for nx inr dmap audit id {} with exception {}", nxInrDmaapAudit.getId(), e.getMessage());
					logger.info("Exception", e);
					Long nxRequestId=Long.parseLong(nxInrDmaapAudit.getNxCorrelationId());
					NxRequestDetails reqDetails = nxRequestDetailsRepository.findByNxReqId(nxRequestId);
					reqDetails.setStatus(CommonConstants.STATUS_CONSTANTS.ERROR.getValue());
					reqDetails.setModifedDate(new Date());
					nxRequestDetailsRepository.save(reqDetails);

				} finally {
					if (service != null) {
						service.shutdown();
					}
				}
				try {
					TimeUnit.SECONDS.sleep(20);
				} catch (InterruptedException e) {
					logger.info(e.getMessage());
				}
			}
		}
	}
	
	protected ServiceResponse requestProcessing(Long nxRequestId) throws SalesBusinessException {
		ServiceResponse response=null;
		InvPriceJsonRequest invRequest=new InvPriceJsonRequest();
		invRequest.setNxReqId(nxRequestId);
		//get the access product details
		response=retrieveBillingProductDetail.getProductdetails(invRequest);
		//create price json
		InvPriceJsonResponse priceResponse=(InvPriceJsonResponse) invPriceJsonImpl.invPriceJson(invRequest);
		response=priceResponse;
		if(priceResponse.getNxDwPriceId()!=null) {
			InrJsonServiceRequest request=new InrJsonServiceRequest();
			request.setId(priceResponse.getNxDwPriceId());
			response=inrJsonServiceImpl.inrJsonProcess(request);
		}
		return response;
	}
	
}