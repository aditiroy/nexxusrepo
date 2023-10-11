package com.att.sales.nexxus.service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Blob;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringUtils;
import org.json.JSONException;
import org.json.XML;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.att.sales.framework.exception.SalesBusinessException;
import com.att.sales.nexxus.constant.CommonConstants;
import com.att.sales.nexxus.constant.FmoConstants;
import com.att.sales.nexxus.constant.InrConstants;
import com.att.sales.nexxus.constant.MyPriceConstants;
import com.att.sales.nexxus.constant.StringConstants;
import com.att.sales.nexxus.dao.model.NxDesignAudit;
import com.att.sales.nexxus.dao.model.NxLookupData;
import com.att.sales.nexxus.dao.model.NxOutputFileModel;
import com.att.sales.nexxus.dao.model.NxRequestDetails;
import com.att.sales.nexxus.dao.model.NxRequestGroup;
import com.att.sales.nexxus.dao.model.solution.NxSolutionDetail;
import com.att.sales.nexxus.dao.repository.InrXmlToJsonRuleDao.InrXmlToJsonRuleDaoResult;
import com.att.sales.nexxus.dao.repository.NxDesignAuditRepository;
import com.att.sales.nexxus.dao.repository.NxLookupDataRepository;
import com.att.sales.nexxus.dao.repository.NxOutputFileRepository;
import com.att.sales.nexxus.dao.repository.NxRequestDetailsRepository;
import com.att.sales.nexxus.dao.repository.NxRequestGroupRepository;
import com.att.sales.nexxus.dao.repository.NxSolutionDetailsRepository;
import com.att.sales.nexxus.inr.InrFactory;
import com.att.sales.nexxus.inr.InrIntermediateJsonUpdate;
import com.att.sales.nexxus.inr.InrInventoryJsonToIntermediateJson;
import com.att.sales.nexxus.inr.InrJsonServiceImpl;
import com.att.sales.nexxus.inr.InvPriceJsonImpl;
import com.att.sales.nexxus.inr.OutputJsonFallOutData;
import com.att.sales.nexxus.inr.OutputJsonGenerator;
import com.att.sales.nexxus.inr.OutputJsonService;
import com.att.sales.nexxus.inr.PreviewDataService;
import com.att.sales.nexxus.output.entity.NxOutputBean;
import com.att.sales.nexxus.util.UnmockableWrapper;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * The Class InrProcessingService.
 *
 * @author xy3208
 */
@Component
public class InrProcessingService {

	/** The log. */
	private static Logger log = LoggerFactory.getLogger(InrProcessingService.class);

	/** The dmaap fail time in hours. */
	@Value("${dmaap.message.fail.time.in.hours}")
	private String dmaapFailTimeInHours;

	/** The p 8 d local path. */
	@Value("${p8.local.destPath}")
	private String p8dLocalPath;

	/** The nexus output file repository. */
	@Autowired
	private NxOutputFileRepository nexusOutputFileRepository;

	/** The nx request details repository. */
	@Autowired
	private NxRequestDetailsRepository nxRequestDetailsRepository;

	/** The nx solution details repository. */
	@Autowired
	private NxSolutionDetailsRepository nxSolutionDetailsRepository;

	/** The report service. */
	@Autowired
	private ReportService reportService;

	/** The inr factory. */
	@Autowired
	private InrFactory inrFactory;

	/** The mapper. */
	@Autowired
	private ObjectMapper mapper;

	/** The message consumption service impl. */
	@Autowired
	private MessageConsumptionServiceImpl messageConsumptionServiceImpl;
	
	/** The InrQualifyingService service impl. */
	@Autowired
	private InrQualifyService inrQualifyService;

	/** The enable scheduler. */
	@Value("${enable.scheduler}")
	private String enableScheduler;

	/** The unmockable wrapper. */
	@Autowired
	private UnmockableWrapper unmockableWrapper;

	/** The output json service. */
	@Autowired
	private OutputJsonService outputJsonService;
	
	@Autowired
	private NxRequestGroupRepository nxRequestGroupRepository;
	
	@Autowired
	private NxOutputFileRepository nxOutputFileRepository;
	
	@Autowired
	private NxLookupDataRepository nxLookupDataRepository;
	
	@Autowired
	private PreviewDataService previewDataService;
	
	@Autowired
	private NxDesignAuditRepository nxDesignAuditRepository;
	
	@Value("#{new Integer('${mpdeal.fail.time.in.hours}')}")
	private int mpDealFailTimeInHours;
	
	@Autowired
	private InrBetaGenerateNxsiteId inrBetaGenerateNxsiteId;
	

	/**
	 * Creates the inr nexus output.
	 *
	 * @param reqDetails the req details
	 * @return the long
	 */
	@Transactional
	public NxOutputFileModel createInrNexusOutput(NxRequestDetails reqDetails, JsonNode inventoryJson) {
		log.info("adding little product id query change");
		log.info("createInrNexusOutput requestId: {}, xmlFileName: {}", reqDetails.getNxReqId(),
				reqDetails.getFileName());
		long nxRequestDetailsStatus = CommonConstants.STATUS_CONSTANTS.SUCCESS.getValue();
		List<NxOutputFileModel> nxOutputFileModels=
				nxOutputFileRepository.findByNxReqId(reqDetails.getNxReqId());
		NxOutputFileModel model=null;
		if(CollectionUtils.isNotEmpty(nxOutputFileModels)) {
			model= nxOutputFileModels.get(0)!=null?nxOutputFileModels.get(0):null;
		}
		if(null == model) {
			model=new NxOutputFileModel();

		}else {
			reqDetails.setNxOutputFiles(null);
		}
		//NxOutputFileModel model = new NxOutputFileModel();
		Timestamp currentDate = new Timestamp(System.currentTimeMillis());
		model.setCreatedDate(currentDate);
		model.setModifiedDate(currentDate);
		model.setStatus(CommonConstants.INPROGRESS);
		JsonNode node;
		try {
			if (inventoryJson == null) {
				log.info("saving inventory json to DB");
				Path xmlPath = Paths.get(p8dLocalPath).resolve(FilenameUtils.getName(reqDetails.getFileName())); // NOSONAR
				node = mapper.readTree(
						XML.toJSONObject(unmockableWrapper.readFile(xmlPath.toString(), StandardCharsets.UTF_8), true)
						.toString());
				//to set inventory file size in kb
				long fileSize=Files.size(xmlPath)/1024;
				String fileSizedetail=String.valueOf(fileSize)+" kb";
				model.setInventoryFileSize(fileSizedetail);
			} else {
				node = inventoryJson;
			}
			InrXmlToJsonRuleDaoResult inrXmlToJsonRuleDaoResult = outputJsonService.trimInventoryJson(node);
			outputJsonService.preprocessInventoryJson(node);
			model.setInventoryJson(node.toString());
			
			outputJsonService.modifyInventoryJsonForIntermediateJsonGeneration(node);
			JsonNode intermediateJson = generateIntermediateJson(node, inrXmlToJsonRuleDaoResult);
			node = null; // release memory
			model.setIntermediateJson(intermediateJson.toString());
			// expecting flowType INR or INR_BETA
			//from request details flowtype will be INR or USRP
			String flowtype=InrConstants.USRP.equalsIgnoreCase(reqDetails.getFlowType())?InrConstants.INR_BETA:reqDetails.getFlowType();
			OutputJsonFallOutData outputJsonFallOutData = generateOutput(intermediateJson.deepCopy(), flowtype);
			NxOutputBean nxOutputBean = outputJsonFallOutData.getNxOutputBean();
			if (outputJsonFallOutData.isBeanOutput()) {
				model.setOutput(nxOutputBean);
			}
			model.setMpOutputJson(outputJsonFallOutData.getMpOutputJson().toString());
			model.setFallOutData(outputJsonFallOutData.getFallOutData());
			

			Blob outputFile = reportService.generateReport(nxOutputBean);
			//model.setOutputFile(outputFile);
			if(null != outputFile) {
				model.setOutputFile(outputFile.getBytes(1,(int) outputFile.length()));
			}
			String fileName = createFileName(reqDetails.getNxReqId());
			model.setFileName(fileName);
			model.setFileType(FmoConstants.FILE_TYPE);
			model.setNxSiteIdInd(StringConstants.CONSTANT_N);
			nxRequestDetailsStatus = setSuccessStatus(model, intermediateJson, outputJsonFallOutData, outputFile);
		} catch (SalesBusinessException e) {
			model.setStatus(CommonConstants.FAIL);
			model.setFallOutData(e.getMessage());
			log.error("createInrNexusOutput Exception", e);
			nxRequestDetailsStatus = CommonConstants.STATUS_CONSTANTS.ERROR.getValue();
			if (InrConstants.XML_FILE_NOT_FOUND_EXCEPTION.equals(e.getMsgCode())) {
				nxRequestDetailsStatus = CommonConstants.STATUS_CONSTANTS.FILE_NOT_FOUND.getValue();
			}
		} catch (IOException | JSONException e) {
			log.error("Exception in saving inventoryJson", e);
			model.setStatus(CommonConstants.FAIL);
			model.setFallOutData(e.getMessage());
			nxRequestDetailsStatus = CommonConstants.STATUS_CONSTANTS.ERROR.getValue();
		} catch (Exception e) {
			log.error("General Exception in saving inventoryJson", e);
			log.error("General Exception cause in saving inventoryJson", e.getCause());
			model.setFallOutData(e.getMessage());
			nxRequestDetailsStatus = CommonConstants.STATUS_CONSTANTS.ERROR.getValue();
		}
		model.setNxRequestDetails(reqDetails);

		reqDetails.setStatus(nxRequestDetailsStatus);
		reqDetails.setModifedDate(new Date());

		try {
			JsonNode cdirData = previewDataService.generateCdirData(model);
			model.setCdirData(cdirData.toString());
		} catch (IOException e) {
			log.info("exception in presave cdir data in inr flow", e);
		}
		nxRequestDetailsRepository.save(reqDetails);
		log.info("setting NxRequestDetails status={} where nxReqId={}", nxRequestDetailsStatus, reqDetails.getNxReqId());
		return model;
	}

	/**
	 * Regenerate output json.
	 *
	 * @param reqDetails the req details
	 * @throws IOException            Signals that an I/O exception has occurred.
	 * @throws SalesBusinessException the sales business exception
	 */
	public void regenerateOutputJson(NxRequestDetails reqDetails) throws IOException, SalesBusinessException {
		List<NxOutputFileModel> nxOutputFiles = reqDetails.getNxOutputFiles();
		NxDesignAudit designAudit = nxDesignAuditRepository.findByNxRefIdAndTransaction(reqDetails.getNxReqId(), CommonConstants.INR_EXCLUDE_LINE_ITEMS);
		if(designAudit != null) {
			nxDesignAuditRepository.delete(designAudit);
		}
		if (!nxOutputFiles.isEmpty()) {
			NxOutputFileModel nxOutputFileModel = nxOutputFiles.get(0);
			String inventoryJsonString = nxOutputFileModel.getInventoryJson(); 
			JsonNode node = mapper.readTree(inventoryJsonString);
			InrXmlToJsonRuleDaoResult inrXmlToJsonRuleDaoResult = outputJsonService.trimInventoryJson(node);
			outputJsonService.preprocessInventoryJson(node);
			nxOutputFileModel.setInventoryJson(node.toString());
			outputJsonService.modifyInventoryJsonForIntermediateJsonGeneration(node);
			JsonNode intermediateJson = generateIntermediateJson(node, inrXmlToJsonRuleDaoResult);
			node = null; 
			nxOutputFileModel.setIntermediateJson(intermediateJson.toString());
			nxOutputFileModel.setMpOutputJson(intermediateJson.toString());
			OutputJsonFallOutData outputJsonFallOutData = generateOutput(intermediateJson.deepCopy(), reqDetails.getFlowType());
			NxOutputBean nxOutputBean = outputJsonFallOutData.getNxOutputBean();
			if (outputJsonFallOutData.isBeanOutput()) {
				nxOutputFileModel.setOutput(nxOutputBean);
			}
			nxOutputFileModel.setMpOutputJson(outputJsonFallOutData.getMpOutputJson().toString());
			nxOutputFileModel.setFallOutData(outputJsonFallOutData.getFallOutData());
			nxOutputFileModel.setNxSiteIdInd(StringConstants.CONSTANT_N);
			Blob outputFile = reportService.generateReport(nxOutputBean);
			//nxOutputFileModel.setOutputFile(outputFile);
			try {
				if(null != outputFile) {
					nxOutputFileModel.setOutputFile(outputFile.getBytes(1,(int) outputFile.length()));
				}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				log.info(e.getMessage());
			}catch (Exception e) {
				// TODO Auto-generated catch block
				log.info(e.getMessage());
			}
			String fileName = createFileName(reqDetails.getNxReqId());
			nxOutputFileModel.setFileName(fileName);
			nxOutputFileModel.setFileType(FmoConstants.FILE_TYPE);
			nxOutputFileModel.setModifiedDate(new Timestamp(System.currentTimeMillis()));
			JsonNode cdirData = previewDataService.generateCdirData(nxOutputFileModel);
			nxOutputFileModel.setCdirData(cdirData.toString());
			long reqDetailsStatus = setSuccessStatus(nxOutputFileModel, intermediateJson, outputJsonFallOutData,
					outputFile);
			nexusOutputFileRepository.saveAndFlush(nxOutputFileModel);
			if(StringConstants.FLOW_TYPE_USRP.equalsIgnoreCase(reqDetails.getFlowType())) {
				inrBetaGenerateNxsiteId.generateNxsiteidInrBeta(reqDetails.getNxReqId(), true, null);
			}else {
				inrQualifyService.inrQualifyCheck(reqDetails.getNxReqId(), true, null);
			}
			NxRequestDetails nxRequestDetails =	nxRequestDetailsRepository.findByNxReqId(reqDetails.getNxReqId());
			nxRequestDetails.setStatus(reqDetailsStatus);
			nxRequestDetailsRepository.save(nxRequestDetails);
		}
	}
	
	protected JsonNode generateIntermediateJson(JsonNode inventoryJson, InrXmlToJsonRuleDaoResult inrXmlToJsonRuleDaoResult) throws SalesBusinessException {
		InrInventoryJsonToIntermediateJson inrInventoryJsonToIntermediateJson = inrFactory.getInrInventoryJsonToIntermediateJson(inventoryJson, inrXmlToJsonRuleDaoResult);
		return inrInventoryJsonToIntermediateJson.generate();
	}
	
	

	/**
	 * Generate output.
	 *
	 * @param intermediateJson the intermediate json
	 * @return the output json fall out data
	 * @throws SalesBusinessException the sales business exception
	 */
	protected OutputJsonFallOutData generateOutput(JsonNode intermediateJson, String flowType) throws SalesBusinessException {
		return outputJsonService.getOutputData(intermediateJson, flowType);
	}

	/**
	 * Creates the file name.
	 *
	 * @param id the id
	 * @return the string
	 */
	// duplicated code with FmoProcessingService
	protected String createFileName(Long id) {
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss'.xlsx'");
		dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
		return id + CommonConstants.OUTPUT_FILE_NAME + dateFormat.format(new Date());
	}

	/**
	 * Sets the success status.
	 *
	 * @param model            the model
	 * @param intermediateJson the intermediate json
	 * @param nxOutputBean     the nx output bean
	 * @param outputFile       the output file
	 * @return the long
	 */
	// duplicated code with FmoProcessingService
	protected long setSuccessStatus(NxOutputFileModel model, JsonNode intermediateJson,
			OutputJsonFallOutData outputJsonFallOutData, Blob outputFile) {
		log.info("Inside setSuccessStatus method");
		if (!intermediateJson.iterator().hasNext()
				|| (!outputJsonFallOutData.hasValue() && StringUtils.isEmpty(model.getFallOutData()))
				|| (outputJsonFallOutData.isBeanOutput() && outputFile == null)) {
			if (intermediateJson.path("service").asText().equals("DOMESTIC DEDICATED ACCESS")
					&& intermediateJson.hasNonNull("DomesticEthernetAccessInventory")) {
				model.setStatus(CommonConstants.SUCCESS);
				return CommonConstants.STATUS_CONSTANTS.SUCCESS.getValue();
			} else {
				model.setStatus(CommonConstants.FAIL);
				return CommonConstants.STATUS_CONSTANTS.ERROR.getValue();
			}
		}
		if (!intermediateJson.path("source").asText().equals("USRP")) {
			if (!outputJsonFallOutData.hasValue() && StringUtils.isNotEmpty(model.getFallOutData())) {
				if (intermediateJson.path("service").asText().equals("DOMESTIC DEDICATED ACCESS")
						&& intermediateJson.hasNonNull("DomesticEthernetAccessInventory")) {
					model.setStatus(CommonConstants.SUCCESS_WITH_FALLOUT);
					return CommonConstants.STATUS_CONSTANTS.SUCCESS_WITH_FALLOUT.getValue();
				} else {
					model.setStatus(CommonConstants.FALLOUT);
					return CommonConstants.STATUS_CONSTANTS.FALLOUT.getValue();
				}
			}
			if (outputJsonFallOutData.hasValue() && StringUtils.isNotEmpty(model.getFallOutData())) {
				model.setStatus(CommonConstants.SUCCESS_WITH_FALLOUT);
				return CommonConstants.STATUS_CONSTANTS.SUCCESS_WITH_FALLOUT.getValue();
			} 
		} else {
			if (!outputJsonFallOutData.hasValue() && StringUtils.isNotEmpty(model.getFallOutData())) {
				model.setStatus(CommonConstants.FALLOUT);
				return CommonConstants.STATUS_CONSTANTS.FALLOUT.getValue();
			}
			if (model.getMpOutputJson() != null && (model.getMpOutputJson()
					.contains(InrIntermediateJsonUpdate.EXCLUDE_LINE_ITEMS_FALLOUT_REASON))) {
				model.setStatus(CommonConstants.PARTIAL_FALLOUT_LINE_ITEMS_IGNORED);
				return CommonConstants.STATUS_CONSTANTS.PARTIAL_FALLOUT_LINE_ITEMS_IGNORED.getValue();
			}
			if (model.getMpOutputJson() != null && (model.getMpOutputJson()
					.contains(OutputJsonGenerator.LINEITEM_NOT_FOUND_DESC)
					|| model.getMpOutputJson().contains(OutputJsonGenerator.KEY_MISSING_DESC)
					|| model.getMpOutputJson().contains(InrJsonServiceImpl.TDM_ACCESS_SPEED_MISSING)
					|| model.getMpOutputJson().contains(InrJsonServiceImpl.SECONDARY_CIRCUIT_OF)
					|| model.getMpOutputJson().contains(InrJsonServiceImpl.BILL_IS_NOT_GENERATED)
					|| model.getMpOutputJson().contains(InrJsonServiceImpl.ORDERING_DETAILS_MISSING)
					|| model.getMpOutputJson().contains(InrConstants.NRC_CHARGES_ARE_NOT_NEEDED)
					|| model.getMpOutputJson().contains(InrJsonServiceImpl.CIRCUIT_DISCONNECTED)
					|| model.getMpOutputJson().contains(OutputJsonGenerator.MP_LINE_ITEM_INACTIVE)
					)) {
				model.setStatus(CommonConstants.SUCCESS_WITH_FALLOUT);
				return CommonConstants.STATUS_CONSTANTS.SUCCESS_WITH_FALLOUT.getValue();
			}
			
		}
		model.setStatus(CommonConstants.SUCCESS);
		return CommonConstants.STATUS_CONSTANTS.SUCCESS.getValue();
	}

	/**
	 * scheduled job to update STATUS in NX_REQUEST_DETAILS when the dmaap message
	 * is not received within dmaapFailTimeInHours hours.
	 */
	@Transactional
	@Scheduled(fixedDelay = 6 * 60 * 60 * 1000) // 6 hours
	public void updateNoDmaapNotificationStatus() {
		log.info("updateNoDmaapNotificationStatus starts {}", enableScheduler);
		if ("Y".equalsIgnoreCase(enableScheduler)) {
			Date dateThreshold = Date.from(Instant.now().minus(new Integer(dmaapFailTimeInHours), ChronoUnit.HOURS));
			List<NxRequestDetails> pendingRequests = nxRequestDetailsRepository
					.getNxRequests(CommonConstants.STATUS_CONSTANTS.IN_PROGRESS.getValue(), dateThreshold);

			if (null != pendingRequests && !pendingRequests.isEmpty()) {

				for (NxRequestDetails pendingRequest : pendingRequests) {
					messageConsumptionServiceImpl.sendMailNotification(pendingRequest);

					pendingRequest.setModifedDate(new Timestamp(System.currentTimeMillis()));
					pendingRequest
							.setStatus(CommonConstants.STATUS_CONSTANTS.NO_DMAAP_NOTIFICATION_RECEIVED.getValue());
					nxRequestDetailsRepository.save(pendingRequest);

					NxSolutionDetail nxSolutionDetail = pendingRequest.getNxSolutionDetail();
					nxSolutionDetail.setModifiedDate(new Timestamp(System.currentTimeMillis()));
					nxSolutionDetailsRepository.save(nxSolutionDetail);
				}
				
				// update group status
				List<Long> groupIds = pendingRequests.stream().map(NxRequestDetails::getNxRequestGroupId).distinct().collect(Collectors.toList());
				for(Long id : groupIds) {
					NxRequestGroup nxRequestGroup = nxRequestGroupRepository.findByNxRequestGroupIdAndActiveYn(id, StringConstants.CONSTANT_Y);
					List<NxRequestDetails> nxReqDetails = nxRequestDetailsRepository.findRequestsByGroupId(id, StringConstants.CONSTANT_Y);
					if(CollectionUtils.isNotEmpty(nxReqDetails)) {
						boolean isServiceExist = nxReqDetails.stream().filter(prod -> prod.getNxRequestGroupName().equalsIgnoreCase("SERVICE_GROUP") 
								|| prod.getNxRequestGroupName().equalsIgnoreCase("SERVICE_ACCESS_GROUP")).findAny().isPresent();
						if(isServiceExist) {
							// service + access check
							List<String> access = nxLookupDataRepository.findByDatasetName("ACCESS_GROUP").stream().map(NxLookupData::getDescription).collect(Collectors.toList());
							List<NxRequestDetails> accessRequest = nxReqDetails.stream().filter(n -> access.contains(n.getProduct())).collect(Collectors.toList());
							if(CollectionUtils.isNotEmpty(accessRequest)) {
								nxRequestGroup.setStatus(MyPriceConstants.NO_DMAAP_NOTIFICATION);
								nxRequestGroup.setModifiedDate(new Date());
								nxRequestGroupRepository.save(nxRequestGroup);
							}
						}
					}
				}
			}
		}
	}
	
}
