package com.att.sales.nexxus.service;

import java.io.IOException;
import java.sql.Blob;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.NumberToTextConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.att.sales.framework.exception.SalesBusinessException;
import com.att.sales.framework.model.Message;
import com.att.sales.framework.model.ServiceResponse;
import com.att.sales.framework.model.Status;
import com.att.sales.framework.model.constants.HttpErrorCodes;
import com.att.sales.framework.service.BaseServiceImpl;
import com.att.sales.framework.util.MessageResourcesUtil;
import com.att.sales.nexxus.constant.AuditTrailConstants;
import com.att.sales.nexxus.constant.CommonConstants;
import com.att.sales.nexxus.constant.DataUploadConstants;
import com.att.sales.nexxus.constant.InrConstants;
import com.att.sales.nexxus.constant.MyPriceConstants;
import com.att.sales.nexxus.constant.StringConstants;
import com.att.sales.nexxus.dao.model.NxDesignAudit;
import com.att.sales.nexxus.dao.model.NxLookupData;
import com.att.sales.nexxus.dao.model.NxOutputFileModel;
import com.att.sales.nexxus.dao.model.NxRequestDetails;
import com.att.sales.nexxus.dao.model.NxRequestGroup;
import com.att.sales.nexxus.dao.repository.NxDesignAuditRepository;
import com.att.sales.nexxus.dao.repository.NxLookupDataRepository;
import com.att.sales.nexxus.dao.repository.NxOutputFileRepository;
import com.att.sales.nexxus.dao.repository.NxRequestDetailsRepository;
import com.att.sales.nexxus.dao.repository.NxRequestGroupRepository;
import com.att.sales.nexxus.inr.CdirEdit;
import com.att.sales.nexxus.inr.CopyOutputToIntermediateJson;
import com.att.sales.nexxus.inr.InrFactory;
import com.att.sales.nexxus.inr.InrIntermediateJsonUpdate;
import com.att.sales.nexxus.inr.OutputJsonFallOutData;
import com.att.sales.nexxus.model.BulkUploadInrUpdateRequest;
import com.att.sales.nexxus.model.BulkUploadInrUpdateResponse;
import com.att.sales.nexxus.output.entity.NxOutputBean;
import com.att.sales.nexxus.util.AuditUtil;
import com.att.sales.nexxus.util.JsonPathUtil;
import com.att.sales.nexxus.util.NexxusJsonUtility;
import com.att.sales.nexxus.util.NxSolutionUserLockUtil;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.pjfanning.xlsx.StreamingReader;
import com.jayway.jsonpath.TypeRef;

/**
 * @author sj0546
 *
 */
@Service("bulkUploadInrUpdateService")
public class BulkUploadInrUpdateService extends BaseServiceImpl {

	/** The log. */
	private static Logger log = LoggerFactory.getLogger(BulkUploadInrUpdateService.class);

	@Autowired
	private NexxusJsonUtility nexxusJsonUtility;

	@Autowired
	private NxLookupDataRepository nxLookupDataRepository;
	
	@Autowired
	private NxRequestDetailsRepository nxRequestDetailsRepository;
	
	@Autowired
	private ObjectMapper mapper;
	
	@Autowired
	private InrProcessingService inrProcessingService;
	
	@Autowired
	private NxDesignAuditRepository nxDesignAuditRepository;
	
	@Autowired
	private InrQualifyService inrQualifyService;
	
	@Autowired
	private CdirEdit cdirEdit;
	
	@Autowired
	private NexxusService nexxusService;
	
	@Autowired
	private InrFactory inrFactory;

	@Autowired
	private NxMyPriceRepositoryServce nxMyPriceRepositoryServce;
	
	@Autowired
	private InrEditCreateExcelService inrEditCreateExcelService;
	
	@Autowired
	private NxRequestGroupRepository nxRequestGroupRepository;
	
	@Autowired
	private JsonPathUtil jsonPathUtil;
	
	@Autowired
	private AuditUtil auditUtil;
	
	@Autowired
	private NxSolutionUserLockUtil nxSolutionUserLockUtil;
	
	@Autowired
	private NxOutputFileRepository nxOutputFileRepository;
	
	@Autowired
	private InrBetaGenerateNxsiteId inrBetaGenerateNxsiteId;
	
	/** The report service. */
	@Autowired
	private ReportService reportService;
	
	/**
	 * Update the inr data
	 *
	 * @param request the request
	 * @return the data upload response
	 */
	public ServiceResponse bulkUploadInrData(BulkUploadInrUpdateRequest request)
			throws SalesBusinessException {
		Long currentTime = System.currentTimeMillis();
		Long startTime = System.currentTimeMillis() - currentTime;		

		log.info("Inside bulkUploadInrData method: "+request.getAction());
		log.info("Inside bulkUploadInrData method, nxSolutionID: {}", request.getNxSolutionId());
		BulkUploadInrUpdateResponse resp = new BulkUploadInrUpdateResponse();
		List<LinkedHashMap<String, Object>> excelData = new ArrayList<LinkedHashMap<String, Object>>();
		Map<String, Object> paramMap = new HashMap<String, Object>();
		String product = request.getProduct();
		if ("ADIG(GMIS)".equalsIgnoreCase(product)) {
			product = "GMIS";
		}
		if(!validateExcel(request, excelData, request.getAction(), paramMap)) {
			setErrorResponse(resp, CommonConstants.INR_ERROR_INVALID_ACTION);
			nxSolutionUserLockUtil.updateSolutionLockStatus(request.getNxSolutionId(), request.getActionPerformedBy());
			return resp;
		}
		if(CollectionUtils.isEmpty(excelData)) {
			setErrorResponse(resp, CommonConstants.INR_ERROR_DATA_NOT_FOUND);
			nxSolutionUserLockUtil.updateSolutionLockStatus(request.getNxSolutionId(), request.getActionPerformedBy());
			return resp;
		}

		if(CommonConstants.BULK_INR_DATA_UPDATE.equalsIgnoreCase(request.getAction())) {
			resp.setNxSolutionId(request.getNxSolutionId());
			nexxusService.updateNxSolution(request.getNxSolutionId());
			Set<String> cktIdforAugmentation = new HashSet<String>();
			List<Long> nxReqIds = nxRequestDetailsRepository.findNxReqIdByNxSolutionIdAndProduct(request.getNxSolutionId(), product);
			statusUpdate(nxReqIds, CommonConstants.PRE_DATA);
			
			boolean isCktAugmentation = paramMap.containsKey("isCktAugmentation") ? Boolean.valueOf(paramMap.get("isCktAugmentation").toString()) : false;
			boolean isExcludeLineItems = paramMap.containsKey("isExcludeLineItems") ? Boolean.valueOf(paramMap.get("isExcludeLineItems").toString()) : false;
			boolean isDataUpdate = paramMap.containsKey("isDataUpdate") ? Boolean.valueOf(paramMap.get("isDataUpdate").toString()) : false;
			List<NxRequestDetails> nxRequestDetailList = nxRequestDetailsRepository.findRequestByNxSolutionIdAndProduct(request.getNxSolutionId(), product);
			Set<Long> cktUpdateReq = new HashSet<Long>();
			if(isCktAugmentation || isExcludeLineItems || isDataUpdate) {
				for(NxRequestDetails nxReq: nxRequestDetailList) {
					NxRequestGroup nxRequestGroup = nxRequestGroupRepository.findByNxRequestGroupIdAndActiveYn(nxReq.getNxRequestGroupId(), StringConstants.CONSTANT_Y);
					if(nxRequestGroup != null && (Long.parseLong(MyPriceConstants.QUALIFIED) == Long.parseLong(nxRequestGroup.getStatus())
							|| Long.parseLong(MyPriceConstants.NOT_QUALIFIED) == Long.parseLong(nxRequestGroup.getStatus())
							|| Long.parseLong(MyPriceConstants.MANUALLY_QUALIFIED) == Long.parseLong(nxRequestGroup.getStatus()))) {
						nxRequestGroup.setStatus(MyPriceConstants.IN_PROGRESS);
						nxRequestGroupRepository.save(nxRequestGroup);
					//	cktUpdateReq.add(nxReq.getNxReqId());
					}
				}
				if(isCktAugmentation) {
					cktIdforAugmentation = nxOutputFileRepository.fetchCircuitId(request.getNxSolutionId());
				}
			}
						
			// req status update
			Set<String> cktIdforAug = cktIdforAugmentation;
			CompletableFuture.runAsync(() -> {
				try {
					Set<Long> excludeReq = new HashSet<Long>();
					Set<String> excludedCktsAtProdLevel = new HashSet<String>();
					String additionalMessage = null;
					for(NxRequestDetails nxRequestDetails : nxRequestDetailList) {
						log.info("Inside bulkUploadInrData async method  {}", nxRequestDetails.getNxReqId());
						Map<String, LinkedHashMap<String, Object>> criteriaJson = new HashMap<String, LinkedHashMap<String, Object>>();
						String datasetName = getDatasetName(nxRequestDetails.getFlowType());
						List<NxLookupData> nxLookupLst=nxLookupDataRepository.findByDatasetNameAndItemIdAndActive(datasetName, nxRequestDetails.getProduct(), StringConstants.CONSTANT_Y);
						 Optional.ofNullable(nxLookupLst).map(List::stream).orElse(Stream.empty()).filter(Objects::nonNull).
						    forEach( data -> {
						    	if(StringUtils.isNotEmpty(data.getCriteria())) {
						    		LinkedHashMap<String, Object> rules = (LinkedHashMap<String, Object>) nexxusJsonUtility
											.convertStringJsonToMap(data.getCriteria());
									criteriaJson.put(data.getDescription(), rules);
						    	}
						 });
						 
					    Set<String> excludedCkts = new HashSet<String>();
					    Set<String> inrBetaexcludeckts = new HashSet<String>();

					    Map<String, LinkedHashMap<String, Object>> cdirInput = new HashMap<String, LinkedHashMap<String, Object>>();
					    Set<String> cktUpdate = new HashSet<String>();
						JsonNode intermediateJson = mapper.readTree(nxRequestDetails.getNxOutputFiles().get(0).getIntermediateJson());
						JsonNode outputJson = mapper.readTree(nxRequestDetails.getNxOutputFiles().get(0).getMpOutputJson());
						
						CopyOutputToIntermediateJson copyOutputToIntermediateJson = inrFactory.getCopyOutputToIntermediateJson(outputJson, intermediateJson);
						copyOutputToIntermediateJson.copyNxSiteId();
						String flowType= nxRequestDetails.getFlowType();
						for(LinkedHashMap<String, Object> dataMap : excelData) {
							String action = dataMap.get(CommonConstants.ACTION_COL_NAME).toString();
							if(criteriaJson.get(action) != null) {
								LinkedHashMap<String, Object> criteriaMap = new  LinkedHashMap<String, Object>();
								criteriaMap = criteriaJson.get(action);
								InrIntermediateJsonUpdate inrIntermediateJsonUpdate = inrFactory.getInrIntermediateJsonUpdate(intermediateJson, criteriaMap, dataMap, action, 
										nxRequestDetails.getProduct(), cktIdforAug, flowType);
								inrIntermediateJsonUpdate.inredits(excludedCkts, cdirInput, cktUpdate,inrBetaexcludeckts);
								action = null;
							}
							
							
						}
						
						if(CollectionUtils.isNotEmpty(cktUpdate)) {
							nxRequestDetails.getNxOutputFiles().get(0).setNxSiteIdInd(CommonConstants.REGENERATE_NXSITEID);
							cktUpdateReq.add(nxRequestDetails.getNxReqId());
						}
						nxRequestDetails.getNxOutputFiles().get(0).setIntermediateJson(intermediateJson.toString());
						
						String flowtype=InrConstants.USRP.equalsIgnoreCase(nxRequestDetails.getFlowType())?InrConstants.INR_BETA:nxRequestDetails.getFlowType();
						OutputJsonFallOutData updateOutputJson = inrProcessingService.generateOutput(intermediateJson.deepCopy(), flowtype);
						nxRequestDetails.getNxOutputFiles().get(0).setMpOutputJson(updateOutputJson.getMpOutputJson().toString());
						nxRequestDetailsRepository.saveAndFlush(nxRequestDetails);

						if(StringConstants.FLOW_TYPE_USRP.equalsIgnoreCase(nxRequestDetails.getFlowType())) {
							inrQualifyService.inrBetaQualifycheck(nxRequestDetails.getNxReqId());
						}
					
						if(CollectionUtils.isNotEmpty(excludedCkts)) {
							excludedCktsAtProdLevel.addAll(excludedCkts);
							excludeReq.add(nxRequestDetails.getNxReqId());
							NxDesignAudit designAudit = nxDesignAuditRepository.findByNxRefIdAndTransaction(nxRequestDetails.getNxReqId(), CommonConstants.INR_EXCLUDE_LINE_ITEMS);
							if(designAudit != null) {
								String[] data = designAudit.getData().substring(1, designAudit.getData().length() - 1).trim().split(",");
								excludedCkts.addAll(new HashSet<>(Arrays.asList(data)));
								designAudit.setData(excludedCkts.toString());
								designAudit.setModifedDate(new Date());
							}else {
								designAudit = new NxDesignAudit();
								designAudit.setData(excludedCkts.toString());
								designAudit.setTransaction(CommonConstants.INR_EXCLUDE_LINE_ITEMS);
								designAudit.setNxRefId(nxRequestDetails.getNxReqId());
								designAudit.setCreatedDate(new Date());
							}
							nxDesignAuditRepository.saveAndFlush(designAudit);
						}
						if(CollectionUtils.isNotEmpty(inrBetaexcludeckts)) {
							excludeReq.add(nxRequestDetails.getNxReqId());
							excludedCktsAtProdLevel.addAll(inrBetaexcludeckts);
						}
						// release memory
						updateOutputJson = null;
						intermediateJson = null;
						outputJson = null;
						excludedCkts = null;
						inrBetaexcludeckts=null;
						cktUpdate = null;
						if (("INR").equalsIgnoreCase(nxRequestDetails.getFlowType())) {
							cdirEdit.updateCdirData(cdirInput, nxRequestDetails.getNxReqId());
						}
						log.info("Inside bulkUploadInrData async method completed {}", nxRequestDetails.getNxReqId());
					}
					log.info("Inside bulkUploadInrData status update {}", request.getNxSolutionId());
					if(isCktAugmentation || isExcludeLineItems || isDataUpdate) {
						for(NxRequestDetails nxRequestDetails : nxRequestDetailList) {
							if(excludeReq.contains(nxRequestDetails.getNxReqId()) || cktUpdateReq.contains(nxRequestDetails.getNxReqId())) {
								if(StringConstants.FLOW_TYPE_USRP.equalsIgnoreCase(nxRequestDetails.getFlowType())) {
									inrBetaGenerateNxsiteId.generateNxsiteidInrBeta(nxRequestDetails.getNxReqId(), true, null);
								}else {
									inrQualifyService.inrQualifyCheck(nxRequestDetails.getNxReqId(), true, null);
								}
							}
						}
					}
					for(NxRequestDetails nxRequestDetails : nxRequestDetailList) {
						if(excludeReq.contains(nxRequestDetails.getNxReqId())) {
							NxRequestDetails nxRequestDetail = nxRequestDetailsRepository.findByNxReqIdAndActiveYn(nxRequestDetails.getNxReqId(), StringConstants.CONSTANT_Y);
							nxRequestDetail.setStatus(CommonConstants.STATUS_CONSTANTS.PARTIAL_FALLOUT_LINE_ITEMS_IGNORED.getValue());
							nxRequestDetailsRepository.saveAndFlush(nxRequestDetail);
						}
					}
					
					statusUpdate(nxReqIds.stream().filter(((Predicate<Long>)excludeReq::contains).negate()).collect(Collectors.toList()), CommonConstants.POST_DATA);
					excludeReq = null;
					log.info("Inside bulkUploadInrData status update completed {}", request.getNxSolutionId());
					Long endTime = System.currentTimeMillis() - currentTime;
			        Long executionTime=endTime-startTime;
			        if(CollectionUtils.isNotEmpty(excludedCktsAtProdLevel)) {
						additionalMessage = "Excluded Circuits:"+excludedCktsAtProdLevel.toString();
					}
					auditUtil.addActionToNxUiAudit(request.getNxSolutionId(),AuditTrailConstants.INR_DATA_UPDATE,request.getActionPerformedBy(),AuditTrailConstants.SUCCESS,null,null,executionTime,additionalMessage);	
					nxSolutionUserLockUtil.updateSolutionLockStatus(request.getNxSolutionId(), request.getActionPerformedBy());
					excludedCktsAtProdLevel = null;
				} catch (SalesBusinessException | IOException e1) {
					e1.printStackTrace();
					nxSolutionUserLockUtil.updateSolutionLockStatus(request.getNxSolutionId(), request.getActionPerformedBy());
				}
			});
			cktIdforAugmentation= null;
		} else if (CommonConstants.BULK_INR_ADDRESS_UPDATE.equalsIgnoreCase(request.getAction())) {
			/*
			 * request is in solution level
			 * check all request, find nxSiteId in each requestId
			 * for each address update request, update mp_output_json and intermediateJson
			 * update NxSolutionSite via InrQualifyService
			 * update CDIR data 
			 */
			List<Long> nxReqIds = nxRequestDetailsRepository.findNxReqIdByNxSolutionId(request.getNxSolutionId());
			nexxusService.updateNxSolution(request.getNxSolutionId());
			resp.setNxSolutionId(request.getNxSolutionId());
			statusUpdate(nxReqIds, CommonConstants.PRE_DATA);
			
			CompletableFuture.runAsync(() -> {
				try {
					log.info("Inr addressUpdate for nxSolutionId: {}", request.getNxSolutionId());
					List<NxRequestDetails> nxRequestDetail = nxRequestDetailsRepository.findByNxSolutionId(request.getNxSolutionId());
					Map<Long, List<LinkedHashMap<String, Object>>> cdirDataMap = new HashMap<>();
					String flowType=null;
					for (NxRequestDetails nxRequestDetails : nxRequestDetail) {
						flowType=nxRequestDetails.getFlowType();
						String datasetName = getAddressDatasetName(nxRequestDetails.getFlowType());
						List<NxLookupData> nxLookupLst=nxLookupDataRepository.findByDatasetNameAndItemIdAndActive(datasetName, nxRequestDetails.getProduct(), StringConstants.CONSTANT_Y);
						LinkedHashMap<String,String> dataConvertRuleMap=new LinkedHashMap<String,String>();
						 Optional.ofNullable(nxLookupLst).map(List::stream).orElse(Stream.empty()).filter(Objects::nonNull).
						    forEach( data -> {
						    	if(StringUtils.isNotEmpty(data.getCriteria())) {
						    		dataConvertRuleMap.put(data.getItemId(), data.getCriteria());
						    	}
						 });
						/*LinkedHashMap<String, String> dataConvertRuleMap = nxMyPriceRepositoryServce
								.getDataFromLookup(datasetName);*/
						String offer = nxRequestDetails.getProduct();
						if (nxRequestDetails.getNxOutputFiles() != null
								&& nxRequestDetails.getNxOutputFiles().size() > 0
								&& nxRequestDetails.getNxOutputFiles().get(0) != null
								&& dataConvertRuleMap.containsKey(offer)) {
							NxOutputFileModel nxOutputFile = nxRequestDetails.getNxOutputFiles().get(0);
							JsonNode mpOutputJsonNode = mapper.readTree(nxOutputFile.getMpOutputJson());
							List<LinkedHashMap<String, Object>> cdirData = new ArrayList<>();
							List<String> nxSiteIdsInMpOutputJson = mpOutputJsonNode.findValuesAsText("nxSiteId");
							nxSiteIdsInMpOutputJson = mpOutputJsonNode.findValuesAsText("nxSiteIdZ", nxSiteIdsInMpOutputJson);
							List<String> nxSiteIdForTdmChk = new ArrayList<String>();
							for (LinkedHashMap<String, Object> excelRow : excelData) {
								if (nxSiteIdsInMpOutputJson.contains(excelRow.get("NX_Site_ID"))) {
									cdirData.add(excelRow);
									nxSiteIdForTdmChk.add(excelRow.get("NX_Site_ID").toString());
								}
							}
							if (!cdirData.isEmpty()) {
								cdirDataMap.put(nxRequestDetails.getNxReqId(), cdirData);
								//modify mpOutputJson and intermediateJson
								inrEditCreateExcelService.uploadExcelData(nxRequestDetails, dataConvertRuleMap.get(offer), cdirData);
								nxRequestDetailsRepository.saveAndFlush(nxRequestDetails);
								// update tdm ckts data in audit table
								if(CommonConstants.DDA_PRODUCT_NAME.equalsIgnoreCase(nxRequestDetails.getProduct()) && 
										(mpOutputJsonNode.has("DomesticDSODS1AccessInventory") 
												|| mpOutputJsonNode.has("DomesticDS3OCXAccessInventory"))) {
									NxDesignAudit designAudit = nxDesignAuditRepository.findByNxRefIdAndTransaction(nxRequestDetails.getNxRequestGroupId(), MyPriceConstants.TDM_NXT1_CIRCUIT_ID);
									if(designAudit != null) {
										processTdmckts(nxRequestDetails.getNxOutputFiles().get(0), designAudit, nxSiteIdForTdmChk);
									}
								}
								nxSiteIdForTdmChk = null;
								//update nx_solution_site
								List<Object> siteAddress = new ArrayList<>();
								Set<String> siteIds = new HashSet<String>();
								inrQualifyService.prepareSiteData(nxRequestDetails.getProduct(), siteAddress, nxRequestDetails.getNxOutputFiles().get(0).getMpOutputJson(), siteIds, nxRequestDetails.getFlowType());
								inrQualifyService.saveNxSolutionSite(siteAddress, nxRequestDetails); 
							}
						}
						if (("INR").equalsIgnoreCase(flowType)) {
							cdirEdit.updateCdirAddressData(cdirDataMap);
						}
					}
					
					statusUpdate(nxReqIds, CommonConstants.POST_DATA);
					nxSolutionUserLockUtil.updateSolutionLockStatus(request.getNxSolutionId(), request.getActionPerformedBy());
				} catch (Exception e) {
					log.info("Inr addressUpdate exception", e);
					nxSolutionUserLockUtil.updateSolutionLockStatus(request.getNxSolutionId(), request.getActionPerformedBy());
				}
			});
			Long endTime = System.currentTimeMillis() - currentTime;
	        Long executionTime=endTime-startTime;
			auditUtil.addActionToNxUiAudit(request.getNxSolutionId(),AuditTrailConstants.INR_ADDRESS_UPDATE,request.getActionPerformedBy(),AuditTrailConstants.SUCCESS,null,null,executionTime,null);
		}
		setSuccessResponse(resp);
		return resp;
	}

	protected String getAddressDatasetName(String flowType) {
		if (("INR").equalsIgnoreCase(flowType)) {
			return CommonConstants.INR_EDIT_EXCEL_DOWNLOAD_MAPPING;
		} else {
			return CommonConstants.INR_BETA_EDIT_EXCEL_DOWNLOAD_MAPPING;
		}
	}

	private String getDatasetName(String flowType) {
		if (("INR").equalsIgnoreCase(flowType)) {
			return CommonConstants.INR_EDITS_DATASET;
		} else {
			return CommonConstants.INR_BETA_EDITS_DATASET;
		}
	}

	/**
	 * Skip header.
	 *
	 * @param r          the r
	 * @param startIndex the start index
	 * @return the boolean
	 */
	protected static Boolean skipHeader(Row r, int startIndex) {
		return r.getRowNum() >= startIndex;
	}

	/**
	 * Sets the error response.
	 *
	 * @param response the response
	 * @param errorCode the error code
	 * @return the service response
	 */
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

	
	/** 
	 * Checks row empty
	 * @param row
	 * @return
	 */
	private boolean isRowEmpty(Row row) {
		boolean isEmpty = true;
		DataFormatter dataFormatter = new DataFormatter();
		if (row != null) {
			for (Cell cell : row) {
				if (dataFormatter.formatCellValue(cell).trim().length() > 0) {
					isEmpty = false;
					break;
				}
			}
		}
		return isEmpty;
	}
	
	/**
	 * Validate the excel data for action 
	 * 
	 * @param request
	 * @param excelData
	 * @param inrUdapteActions 
	 * @return
	 */
	protected boolean validateExcel(BulkUploadInrUpdateRequest request, List<LinkedHashMap<String, Object>> excelData, String action, Map<String, Object> paramMap) {
		List<String> inrUdapteActions = CommonConstants.INR_DATA_UDAPTE_ACTIONS;
		List<String> inrAddrUdapteActions = CommonConstants.INR_ADDRESS_UDAPTE_ACTIONS;
		Workbook workbook = null;
		LinkedHashMap<String, Object> lastDataMap = new  LinkedHashMap<String, Object>();
		boolean lastRowActionChange = false;
		try {
			workbook = StreamingReader.builder().rowCacheSize(100).bufferSize(4096).open(request.getInputStream());
			Sheet sheet = workbook.getSheetAt(0);
			java.util.Iterator<Row> rows = sheet.rowIterator();
			ArrayList<String> headers = new ArrayList<String>();
			while (rows.hasNext()) {
				Row r = rows.next();
				if (isRowEmpty(r)) {
					continue;
				}
				if(r.getRowNum() == 0) {
					java.util.Iterator<Cell> cells = r.cellIterator();
					while (cells.hasNext()) {
						Cell c = cells.next();
						headers.add(c.getStringCellValue().trim());
					}
				}
				
				if (skipHeader(r, DataUploadConstants.ETH_TOKNE_START_INDEX)) {
					LinkedHashMap<String, Object> dataMap = new  LinkedHashMap<String, Object>();
					java.util.Iterator<Cell> cells = r.cellIterator();
					String excelAction = r.getCell(r.getLastCellNum()-1).getStringCellValue();
					if(!CommonConstants.NO_CHANGE.equalsIgnoreCase(excelAction)) {
						while (cells.hasNext()) {
							Cell c = cells.next();
							if(c != null && (c.getCellType() != CellType.BLANK)) {
								if (c.getCellType() == CellType.NUMERIC) {
									dataMap.put(headers.get(c.getColumnIndex()), NumberToTextConverter.toText(c.getNumericCellValue()));
								} else {
									if (CommonConstants.BULK_INR_ADDRESS_UPDATE.equalsIgnoreCase(action) && c.getStringCellValue().startsWith("*")) {
										dataMap.put(headers.get(c.getColumnIndex()), lastDataMap.get(headers.get(c.getColumnIndex())));
									} else {
										dataMap.put(headers.get(c.getColumnIndex()), c.getStringCellValue());
									}
								}
							}
						}
						
						if(CommonConstants.BULK_INR_DATA_UPDATE.equalsIgnoreCase(action)) {
							if(!paramMap.containsKey("isCktAugmentation") && CommonConstants.CIRCUITE_ID_AUGMENTATION.equalsIgnoreCase(excelAction)) {
								paramMap.put("isCktAugmentation", true);
							}
							if(!paramMap.containsKey("isExcludeLineItems") && CommonConstants.EXCLUDE_FROM_MP.equalsIgnoreCase(excelAction)) {
								paramMap.put("isExcludeLineItems", true);
							}
							if(!paramMap.containsKey("isDataUpdate") && CommonConstants.DATA_UPDATE.equalsIgnoreCase(excelAction)) {
								paramMap.put("isDataUpdate", true);
							}
							if(!inrUdapteActions.contains(excelAction)) {
								return false;
							}
						}else {
							if(!inrAddrUdapteActions.contains(excelAction)) {
								return false;
							}
						}
						excelData.add(dataMap);
						lastDataMap = dataMap;
						lastRowActionChange = true;
					} else { //action no change, for inr address edit * started value case
						boolean starFound = false;
						if (CommonConstants.BULK_INR_ADDRESS_UPDATE.equalsIgnoreCase(action) && lastRowActionChange) {
							while (cells.hasNext()) {
								Cell c = cells.next();
								if(c != null && (c.getCellType() != CellType.BLANK)) {
									if (c.getCellType() == CellType.NUMERIC) {
										dataMap.put(headers.get(c.getColumnIndex()), NumberToTextConverter.toText(c.getNumericCellValue()));
									} else {
										if (CommonConstants.BULK_INR_ADDRESS_UPDATE.equalsIgnoreCase(action) && c.getStringCellValue().startsWith("*")) {
											starFound = true;
											dataMap.put(headers.get(c.getColumnIndex()), lastDataMap.get(headers.get(c.getColumnIndex())));
										} else {
											dataMap.put(headers.get(c.getColumnIndex()), c.getStringCellValue());
										}
									}
								}
							}
						}
						if (starFound) {
							excelData.add(dataMap);
						} else {
							lastRowActionChange = false;
						}
					}
				}
			}
			
		} catch (Exception e) {
			log.error("bulkUploadInrData validateExcel : Exception during workbok processing {}", e.getMessage());
			e.printStackTrace();
		}finally{
			try {
				if(workbook != null)
					workbook.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return true;
	}
	
	/** Update request status 
	 * 
	 * @param nxReqId
	 * @param dataUpdate
	 */
	protected void statusUpdate(List<Long> nxReqId, String dataUpdate) {
		if(CollectionUtils.isNotEmpty(nxReqId)) {
			List<NxRequestDetails> nxRequests = nxRequestDetailsRepository.findByNxReqIdAndActiveYn(nxReqId, StringConstants.CONSTANT_Y);
			List<NxLookupData> inrStatusLookup = nxMyPriceRepositoryServce.getItemDescFromLookup(CommonConstants.INR_EDIT_REQUEST_STATUS, StringConstants.CONSTANT_Y);
			for(NxRequestDetails nxRequestDetail : nxRequests) {
				for(NxLookupData inrStatus : inrStatusLookup) {
					if(CommonConstants.PRE_DATA.equalsIgnoreCase(dataUpdate)) {
						if(Arrays.asList(inrStatus.getCriteria().split(",")).contains(String.valueOf(nxRequestDetail.getStatus()))) {
							if(nxRequestDetail.getStatus().longValue() == 90l || nxRequestDetail.getStatus().longValue() == 100l) {
								nxRequestDetail.setSubmitReqAddrEditInd(StringConstants.CONSTANT_Y);
							}
							nxRequestDetail.setStatus(Long.valueOf(inrStatus.getItemId()));
						}
					}else {
						if(Long.valueOf(inrStatus.getItemId()).longValue() == nxRequestDetail.getStatus().longValue()) {
							nxRequestDetail.setStatus(Long.valueOf(inrStatus.getDescription()));
						}
					}
				}
			}
			nxRequestDetailsRepository.saveAll(nxRequests);
			nxRequestDetailsRepository.flush();
		}
	}
	
	/** Updates tdm ckts in audit table
	 * 
	 * @param mpOutputJsonNode
	 * @param designAudit
	 * @throws IOException 
	 */
	protected void processTdmckts(NxOutputFileModel nxOutputFile, NxDesignAudit designAudit, List<String> nxSiteIdForTdmChk) throws IOException {
		JsonNode mpOutputJsonNode = mapper.readTree(nxOutputFile.getMpOutputJson());
		Set<String> updatedTdmNxt1Ckts = new HashSet<String>();
		Set<String> tdmNxt1Ckts = new HashSet<String>(Arrays.asList(designAudit.getData().split("##")));
		boolean dataFound = false;
		String dsods1path = "$..DomesticDSODS1AccessInventory..CustomerSubAccountInfo.CustomerCircuitInfo..CustomerLocationInfo..[?(@.nxSiteId == <REPLACE>)]..['SiteNPANXX', 'CustSrvgWireCtrCLLICd']";
		String ds3path = "$..DomesticDS3OCXAccessInventory..CustomerSubAccountInfo.CustomerCircuitInfo..CustomerLocationInfo..[?(@.nxSiteId == <REPLACE>)]..['SiteNPANXX', 'CustSrvgWireCtrCLLICd']";
		TypeRef<List<Object>> mapType = new TypeRef<List<Object>>() {};
		List<Object> results = null;
		LinkedHashMap<String, Object> map = null;
		for(String tdmCktData : tdmNxt1Ckts) {
			String[] data = tdmCktData.split("\\$");
			if(nxSiteIdForTdmChk.contains(data[0])) {
				results = jsonPathUtil.search(mpOutputJsonNode, dsods1path.replace("<REPLACE>", data[0]), mapType);
				if(CollectionUtils.isEmpty(results)) {
					results = jsonPathUtil.search(mpOutputJsonNode, ds3path.replace("<REPLACE>", data[0]), mapType);
				}
				if(!CollectionUtils.isEmpty(results)) {
					for(Object res : results) {
						dataFound = true;
						if(res instanceof LinkedHashMap) {
							map = (LinkedHashMap<String, Object>) res;
							data[2] = map.get("SiteNPANXX").toString();
							data[3] = map.get("CustSrvgWireCtrCLLICd").toString();
							break;
						}
				
					}
				}
			}
			updatedTdmNxt1Ckts.add(Arrays.asList(data).stream().collect(Collectors.joining("$")));
		}
		if(dataFound) {
			designAudit.setData(updatedTdmNxt1Ckts.stream().collect(Collectors.joining("##")));
			designAudit.setModifedDate(new Date());
			nxDesignAuditRepository.saveAndFlush(designAudit);
		}
	}
}
