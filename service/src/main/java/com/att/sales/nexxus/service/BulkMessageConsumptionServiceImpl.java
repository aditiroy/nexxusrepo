
package com.att.sales.nexxus.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.att.sales.framework.exception.SalesBusinessException;
import com.att.sales.framework.model.ServiceMetaData;
import com.att.sales.nexxus.constant.CommonConstants;
import com.att.sales.nexxus.constant.InrConstants;
import com.att.sales.nexxus.constant.MyPriceConstants;
import com.att.sales.nexxus.constant.StringConstants;
import com.att.sales.nexxus.dao.model.NxInrDmaapAudit;
import com.att.sales.nexxus.dao.model.NxLookupData;
import com.att.sales.nexxus.dao.model.NxOutputFileModel;
import com.att.sales.nexxus.dao.model.NxRequestDetails;
import com.att.sales.nexxus.dao.model.NxRequestGroup;
import com.att.sales.nexxus.dao.repository.NxInrDmaapAuditRepository;
import com.att.sales.nexxus.dao.repository.NxLookupDataRepository;
import com.att.sales.nexxus.dao.repository.NxOutputFileRepository;
import com.att.sales.nexxus.dao.repository.NxRequestDetailsRepository;
import com.att.sales.nexxus.dao.repository.NxRequestGroupRepository;
import com.att.sales.nexxus.dmaap.mr.util.IDmaapMRSubscriber;
import com.att.sales.nexxus.fallout.service.FalloutDetailsImpl;
import com.att.sales.nexxus.model.EDFMRBulkFailResponse;
import com.att.sales.nexxus.model.EDFMRBulkSuccessResponse;
import com.att.sales.nexxus.model.Inventoryfiles;
import com.att.sales.nexxus.model.MailRequest;
import com.att.sales.nexxus.p8.P8Service;
import com.att.sales.nexxus.util.NexxusJsonUtility;
import com.att.sales.nexxus.util.ThreadMetaDataUtil;
import com.att.sales.nexxus.util.xmlMerge.XmlMergeUtil;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * The Class BulkMessageConsumptionServiceImpl.
 *
 * * @author(ar896d) 
 * 
 * The BulkMessage scheduler to subscribe
 * the bulk edf DMAAP message
 *
 */

@Component
public class BulkMessageConsumptionServiceImpl implements BulkMessageCosumptionService {

	/** The logger. */

	private static Logger logger = LoggerFactory.getLogger(BulkMessageConsumptionServiceImpl.class);

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

	@Value("${edf.bulk.fail.dmaap.enabled:N}")
	private String edfDmaapFailEnabled;

	@Value("${edf.bulk.success.dmaap.enabled:N}")
	private String edfDmaapSuccessEnabled;

	/** The p 8 d local path. */
	@Value("${p8.local.destPath}")
	private String p8dLocalPath;
	
	/** The  Json utility  */
	@Autowired
	private NexxusJsonUtility nexxusJsonUtility;
	
	/** The  XML Merge utility  */
	@Autowired
	private XmlMergeUtil xmlMergeUtil;
	
	/** The  NX Lookup Data Repository  */
	@Autowired
	private NxLookupDataRepository nxLookupDataRepository;
	

	@Autowired
	private NxOutputFileRepository nxOutputFileRepository;
	
	/** The mail service. */
	@Autowired
	private MailServiceImpl mailService;
	
	@Autowired
	private NxInrDmaapAuditRepository nxInrDmaapAuditRepository;
	
	@Autowired
	private NxRequestGroupRepository nxRequestGroupRepository;
	
	@Autowired
	private FalloutDetailsImpl falloutDetailsImpl;
	
	@Value("${inr.bulk.dmaap.process.enabled:N}")
	private String inrBulkDmaapProcessEnabled;
	
	@Autowired
	private MessageConsumptionServiceImpl messageConsumptionServiceImpl;

	@Override
	@Scheduled(fixedDelay = 2 * 60 * 1000) // 2 mins
	public void getMessage() {

		List<String> messages = null;
		EDFMRBulkFailResponse failueResponseObj = null;
		EDFMRBulkSuccessResponse successResponseObj = null;

		try {
			if(StringConstants.CONSTANT_Y.equalsIgnoreCase(edfDmaapFailEnabled)) {
				messages = dmaapMRSubscriberService.retrieveMessage(
						env.getProperty("dmaap.subscriber.bulkupload.fail.topic"),
						env.getProperty("dmaap.subscriber.bulkupload.fail.groupname"),
						env.getProperty("dmaap.subscriber.edf.host"));
			if (!messages.isEmpty()) {
					for (String msg : messages) {
						Map<String, String> dmaapmsgMap = (Map<String, String>) nexxusJsonUtility
								.convertStringJsonToMap(msg);
						String PGMValue = null;
						if (dmaapmsgMap.containsKey(StringConstants.PGM)) {
							PGMValue = dmaapmsgMap.get(StringConstants.PGM);
						}
						if (StringConstants.ACCT_RPT.equalsIgnoreCase(PGMValue)) {
							failueResponseObj = mapper.readValue(msg, EDFMRBulkFailResponse.class);
						}
						
						NxRequestDetails nxRequets = nxRequestDetailsRepository.findNxSolutionIdByEdfAckIdAndActiveYn(failueResponseObj.getRequestId(), StringConstants.CONSTANT_Y);
						NxInrDmaapAudit nxInrDmaapAudit = new NxInrDmaapAudit();
						nxInrDmaapAudit.setNxMessage(msg);
						nxInrDmaapAudit.setNxCorrelationId(failueResponseObj.getRequestId());
						nxInrDmaapAudit.setNxProcessStatus("N");
						nxInrDmaapAudit.setNxTransactionType(InrConstants.EDF_BULK_INR_DMAAP);
						nxInrDmaapAudit.setNxSolutionId(nxRequets.getNxSolutionDetail().getNxSolutionId());
						messageConsumptionServiceImpl.updatePodName(InrConstants.EDF_BULK_INR_DMAAP, nxInrDmaapAudit);
						nxInrDmaapAuditRepository.save(nxInrDmaapAudit);
						
					}
				}
			}
			
			if(StringConstants.CONSTANT_Y.equalsIgnoreCase(edfDmaapSuccessEnabled)) {
				messages = dmaapMRSubscriberService.retrieveMessage(
						env.getProperty("dmaap.subscriber.bulkupload.success.topic"),
						env.getProperty("dmaap.subscriber.bulkupload.success.groupname"),
						env.getProperty("dmaap.subscriber.edf.host"));
	
				if (!messages.isEmpty()) {
					for (String msg : messages) {
						Map<String, String> dmaapmsgMap = (Map<String, String>) nexxusJsonUtility
								.convertStringJsonToMap(msg);
						String PGMValue = null;
						if (dmaapmsgMap.containsKey(StringConstants.PGM)) {
							PGMValue = dmaapmsgMap.get(StringConstants.PGM);
						}
						if (StringConstants.BULK_INV.equalsIgnoreCase(PGMValue)) {
							successResponseObj = mapper.readValue(msg, EDFMRBulkSuccessResponse.class);
						}
						NxRequestDetails nxRequets = nxRequestDetailsRepository.findNxSolutionIdByEdfAckIdAndActiveYn(successResponseObj.getRequestId(), StringConstants.CONSTANT_Y);
						NxInrDmaapAudit nxInrDmaapAudit = new NxInrDmaapAudit();
						nxInrDmaapAudit.setNxMessage(msg);
						nxInrDmaapAudit.setNxCorrelationId(successResponseObj.getRequestId());
						nxInrDmaapAudit.setNxProcessStatus("N");
						nxInrDmaapAudit.setNxTransactionType(InrConstants.EDF_BULK_INR_DMAAP);
						nxInrDmaapAudit.setNxSolutionId(nxRequets.getNxSolutionDetail().getNxSolutionId());
						messageConsumptionServiceImpl.updatePodName(InrConstants.EDF_BULK_INR_DMAAP, nxInrDmaapAudit);
						nxInrDmaapAuditRepository.save(nxInrDmaapAudit);
					}
				}
			}
			
		} catch (Exception x) {
			logger.error("Exception while getting Dmaap Message Router messages:%s", x);
		}
	}
	
	
	
	@Scheduled(fixedDelay = 3 * 60 * 1000)
	public void processInrDmaap() {
		if("Y".equalsIgnoreCase(inrBulkDmaapProcessEnabled)) {
			EDFMRBulkFailResponse failueResponseObj = null;
			EDFMRBulkSuccessResponse successResponseObj = null;
			//List<NxInrDmaapAudit> nxInrDmaapAudits = nxInrDmaapAuditRepository.getNewDmaapDetails("N", InrConstants.EDF_BULK_INR_DMAAP);
			List<NxInrDmaapAudit> nxInrDmaapAudits = nxInrDmaapAuditRepository.getNewDmaapDetailsByPodName("N",  InrConstants.EDF_BULK_INR_DMAAP, messageConsumptionServiceImpl.getHostName());
			for(NxInrDmaapAudit nxInrDmaapAudit : nxInrDmaapAudits) {
				try {
					logger.info("dmaap message is: {}", nxInrDmaapAudit.getNxMessage());
					//Updating dmaap processing status to In Progress
					nxInrDmaapAuditRepository.updateDmaapProcessStatus("IP", nxInrDmaapAudit.getId());

					Map<String, String> dmaapmsgMap = (Map<String, String>) nexxusJsonUtility
							.convertStringJsonToMap(nxInrDmaapAudit.getNxMessage());
					String PGMValue = null;
					if (dmaapmsgMap.containsKey(StringConstants.PGM)) {
						PGMValue = dmaapmsgMap.get(StringConstants.PGM);
					}
					if (StringConstants.ACCT_RPT.equalsIgnoreCase(PGMValue)) {
						failueResponseObj = mapper.readValue(nxInrDmaapAudit.getNxMessage(), EDFMRBulkFailResponse.class);
						getFailureMessage(failueResponseObj,nxInrDmaapAudit.getNxMessage());
					}
					
					if (StringConstants.BULK_INV.equalsIgnoreCase(PGMValue)) {
						successResponseObj = mapper.readValue(nxInrDmaapAudit.getNxMessage(), EDFMRBulkSuccessResponse.class);
						getSuccessMessage(successResponseObj,nxInrDmaapAudit.getNxMessage());
					}

					//Updating dmaap processing status to Success
					nxInrDmaapAuditRepository.updateDmaapProcessStatus("S", nxInrDmaapAudit.getId());
				} catch(Exception e) {
					//Updating dmaap processing status to Failed
					nxInrDmaapAuditRepository.updateDmaapProcessStatus("F", nxInrDmaapAudit.getId());
					logger.info("INR processing failed for id {} with exception {}", nxInrDmaapAudit.getId(), e.getMessage());
				}
			}
		}
	}

	protected void getFailureMessage(EDFMRBulkFailResponse failueResponseObj, String dmaapMsg) {
		try {
			if (failueResponseObj != null && dmaapMsg!=null) {
				List<NxRequestDetails> nxRequestDetails = nxRequestDetailsRepository
						.findByEdfAckIdAndActiveYnAndBulkReqYn(failueResponseObj.getRequestId(), StringConstants.CONSTANT_Y,
								StringConstants.CONSTANT_Y);
				if (CollectionUtils.isNotEmpty(nxRequestDetails)) {
					for (NxRequestDetails nxRequestDetail : nxRequestDetails) {
						if (null != nxRequestDetail && null != nxRequestDetail.getNxReqId()) {
							String nxRequestDetailDmapMsg = nxRequestDetail.getDmaapMsg();
							if (failueResponseObj != null) {
								if (StringUtils.isEmpty(nxRequestDetailDmapMsg)) {
									nxRequestDetailDmapMsg = dmaapMsg;
								} else {
									nxRequestDetailDmapMsg = nxRequestDetailDmapMsg + "," + dmaapMsg;
								}
								Timestamp currentDate = new Timestamp(System.currentTimeMillis());

								List<NxOutputFileModel> nxOutputFileModels = nxOutputFileRepository
										.findByNxReqId(nxRequestDetail.getNxReqId());

								NxOutputFileModel nxOutputFileModel = null;
								if (CollectionUtils.isNotEmpty(nxOutputFileModels)) {
									nxOutputFileModel = nxOutputFileModels.get(0) != null ? nxOutputFileModels.get(0)
											: null;
								}
								if (null == nxOutputFileModel) {
									nxOutputFileModel = new NxOutputFileModel();

								} else {
									nxRequestDetail.setNxOutputFiles(null);
								}

								int dmaapStatus = failueResponseObj.getStatus().intValue();
								if (dmaapStatus == 1) {
									nxRequestDetail
											.setStatus(CommonConstants.STATUS_CONSTANTS.SYSTEM_FAILURE.getValue());
								}
								if (dmaapStatus == 2) {
									if(nxRequestDetail.getStatus().equals(10L)) {
										nxRequestDetail
											.setStatus(CommonConstants.STATUS_CONSTANTS.FALLOUT_DMAAP_RECEIVED.getValue());
									}
								}
								nxRequestDetail.setDmaapMsg(nxRequestDetailDmapMsg);
								nxOutputFileModel.setCreatedDate(currentDate);
								nxOutputFileModel.setModifiedDate(currentDate);
								nxOutputFileModel.setDmaapFailureJson(dmaapMsg);
								nxRequestDetail.addNxOutputFiles(nxOutputFileModel);
								nxRequestDetail.setDmaapBulkStatus(String.valueOf(failueResponseObj.getStatus()));
								nxRequestDetailsRepository.saveAndFlush(nxRequestDetail);
								
								// update group status

								List<NxRequestDetails> nxRequests = nxRequestDetailsRepository
										.findByNxRequestGroupIdAndActiveYn(nxRequestDetail.getNxRequestGroupId(),
												StringConstants.CONSTANT_Y);
								NxRequestGroup nxRequestGroup = nxRequestGroupRepository
										.findByNxRequestGroupIdAndActiveYn(nxRequestDetail.getNxRequestGroupId(),
												StringConstants.CONSTANT_Y);
								falloutDetailsImpl.setNxGroupStatus(nxRequests, nxRequestGroup);
								if(nxRequestDetail.getStatus().longValue() == CommonConstants.STATUS_CONSTANTS.SYSTEM_FAILURE.getValue()) {
									sendMailNotification(nxRequestDetail);
								}
							}
						}
					}
				}
			}

		} catch (Exception x) {
			logger.error("Exception while processing Fallout DMAAP", x);
		}

	}

	protected void getSuccessMessage(EDFMRBulkSuccessResponse successResponseObj, String dmaapMsg) {
		Long createInrStartTime;
		Long currentTime = System.currentTimeMillis();
		boolean isfail=false;
		try {
			Map<String, String> dmaapmsgMap = (Map<String, String>) nexxusJsonUtility.convertStringJsonToMap(dmaapMsg);
			if (dmaapMsg != null && successResponseObj != null) {
				List<NxRequestDetails> nxRequestDetails = nxRequestDetailsRepository
						.findByEdfAckIdAndActiveYnAndBulkReqYn(successResponseObj.getRequestId(),
								StringConstants.CONSTANT_Y, StringConstants.CONSTANT_Y);
				if (CollectionUtils.isNotEmpty(nxRequestDetails)) {
					for (NxRequestDetails nxRequestDetail : nxRequestDetails) {
						if (null != nxRequestDetail && null != nxRequestDetail.getNxReqId()) {
							String nxRequestDetailFilename = nxRequestDetail.getFileName();
							String nxRequestDetailDmapMsg = nxRequestDetail.getDmaapMsg();
							if (successResponseObj != null) {
								String filenames = this.getFileName(successResponseObj);

								if (StringUtils.isNotEmpty(filenames)) {
									nxRequestDetailFilename = successResponseObj.getRequestId()
											.concat(StringConstants.XML_EXT);
								}
								if (StringUtils.isEmpty(nxRequestDetailDmapMsg)) {
									nxRequestDetailDmapMsg = dmaapMsg;
								} else {
									nxRequestDetailDmapMsg = nxRequestDetailDmapMsg + "," + dmaapMsg;
								}
								nxRequestDetail.setFileName(nxRequestDetailFilename);
								nxRequestDetail.setDmaapMsg(nxRequestDetailDmapMsg);
								nxRequestDetail.setDmaapBulkStatus(String.valueOf(successResponseObj.getStatus()));
								int dmaapStatus = successResponseObj.getStatus().intValue();
								if (dmaapStatus == 0) {
									nxRequestDetail
											.setStatus(CommonConstants.STATUS_CONSTANTS.SYSTEM_FAILURE.getValue());
									isfail=true;
								}
								if (dmaapStatus == 2) {
									nxRequestDetail
									.setStatus(CommonConstants.STATUS_CONSTANTS.IN_PROGRESS.getValue());
						
								}
								nxRequestDetailsRepository.saveAndFlush(nxRequestDetail);
								if (StringUtils.isNotEmpty(filenames)) {
									logger.info("retrieving file <{}> from p8", filenames);
									List<String> p8FileList = new ArrayList<>(
											Arrays.asList(filenames.split(MyPriceConstants.COMMA_SEPERATOR)));
									if (p8FileList.size() > 1) {
										for (String file : p8FileList) {
											p8Service.lookupDocumentInP8(file);
										}
										List<Path> pathList = new ArrayList<Path>();

										for (String file : p8FileList) {
											Path xmlPath = Paths.get(p8dLocalPath).resolve(FilenameUtils.getName(file));
											pathList.add(xmlPath);
										}

										StringBuilder mergeFilePath = new StringBuilder();
										mergeFilePath.append(p8dLocalPath);
										mergeFilePath.append(nxRequestDetailFilename);

										List<NxLookupData> xmlPathData = nxLookupDataRepository
												.findByDatasetNameAndItemIdAndDescription(
														MyPriceConstants.INR_BULK_MERGE, nxRequestDetail.getProduct(),
														MyPriceConstants.PARENT_PATH);
										if (xmlPathData != null) {
											try {
												List<String> xpathExpressionList = xmlPathData.stream()
														.map(nxLookupData -> nxLookupData.getCriteria())
														.collect(Collectors.toList());
												Long xmlMergeStartTime = System.currentTimeMillis() - currentTime;
												xmlMergeUtil.process(pathList, xpathExpressionList,
														mergeFilePath.toString());
												printTotalDuration(currentTime, xmlMergeStartTime,
														StringConstants.XML_MERGE_FILE);
												logger.info("trigger InrProcessingService");
												createInrStartTime = System.currentTimeMillis() - currentTime;
												messageConsumptionServiceImpl.inrProcess(nxRequestDetail);
												printTotalDuration(currentTime, createInrStartTime,
														StringConstants.INR_OUT_FILE);
											} catch (SalesBusinessException e) {
												long nxRequestDetailsStatus = CommonConstants.STATUS_CONSTANTS.ERROR
														.getValue();
												if (InrConstants.XML_FILE_NOT_FOUND_EXCEPTION.equals(e.getMsgCode())) {
													nxRequestDetailsStatus = CommonConstants.STATUS_CONSTANTS.FILE_NOT_FOUND
															.getValue();
												}
												isfail=true;
												nxRequestDetail.setStatus(nxRequestDetailsStatus);
												nxRequestDetailsRepository.saveAndFlush(nxRequestDetail);
											}
										}

									}
									if (p8FileList.size() == 1) {
										p8Service.lookupDocumentInP8(p8FileList.get(0));
										logger.info("processing only single file {}", p8FileList.get(0));

										/*
										 * rename the file
										 */
										try {
											Path f = Paths.get(p8dLocalPath)
													.resolve(FilenameUtils.getName(p8FileList.get(0)));
											Path rf = Paths.get(p8dLocalPath).resolve(nxRequestDetailFilename);
											Files.move(f, rf, StandardCopyOption.REPLACE_EXISTING);

											logger.info("trigger InrProcessingService");
											createInrStartTime = System.currentTimeMillis() - currentTime;
											messageConsumptionServiceImpl.inrProcess(nxRequestDetail);
											printTotalDuration(currentTime, createInrStartTime,
													StringConstants.INR_OUT_FILE);
										} catch (IOException e) {
											isfail=true;
											nxRequestDetail.setStatus(
													CommonConstants.STATUS_CONSTANTS.FILE_NOT_FOUND.getValue());
											nxRequestDetailsRepository.saveAndFlush(nxRequestDetail);
										}
									}
								}
								
								sendMailNotification(nxRequestDetail);
								// update group status
								if(isfail) {
									List<NxRequestDetails> nxRequests = nxRequestDetailsRepository
											.findByNxRequestGroupIdAndActiveYn(nxRequestDetail.getNxRequestGroupId(),
													StringConstants.CONSTANT_Y);
									NxRequestGroup nxRequestGroup = nxRequestGroupRepository
											.findByNxRequestGroupIdAndActiveYn(nxRequestDetail.getNxRequestGroupId(),
													StringConstants.CONSTANT_Y);
									falloutDetailsImpl.setNxGroupStatus(nxRequests, nxRequestGroup);
								}
							}
						}
					}
				}
			}

		} catch (Exception x) {
			logger.error("Exception while processing Success DMAAP:%s", x);
		}
	}

	
	/**
	 * Gets the file name.
	 *
	 * @param eDFMRResponse the eDFMR response
	 * @return the file name
	 */
	protected String getFileName(EDFMRBulkSuccessResponse eDFMRResponse) {
		String filenames = null;
		Inventoryfiles inventoryFiles = eDFMRResponse.getInventoryFiles();
		if (Optional.ofNullable(inventoryFiles).isPresent() && Optional.ofNullable(inventoryFiles.getFileNames()).isPresent()) {
			filenames = inventoryFiles.getFileNames().stream().map(String::valueOf).collect(Collectors.joining(","));
			return filenames;
		}
		return filenames;
	}

	/**
	 * Prints total duration of the operation
	 * 
	 * @param currentTime
	 * @param startTime
	 * @param operation
	 */
	protected void printTotalDuration(Long currentTime,Long startTime,String operation) {
		Long endTime=System.currentTimeMillis() - currentTime;
		String totalDuration=new StringBuilder().append(operation +" took :: ").append((endTime - startTime)).append(" ").append(MyPriceConstants.MILLISEC).toString();
		logger.info(totalDuration);
	}
	
	protected void sendMailNotification(NxRequestDetails reqDetails) {
		logger.info("Inside sendMailNotification method  {}", "");
		if (reqDetails.getNxReqId() != null) {
			Map<String, Object> requestParams = new HashMap<>();
			//UUID randId = UUID.randomUUID();
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

}
