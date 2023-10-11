package com.att.sales.nexxus.service;

import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Blob;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.sql.rowset.serial.SerialBlob;
import javax.sql.rowset.serial.SerialException;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.NumberToTextConverter;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.att.sales.framework.exception.SalesBusinessException;
import com.att.sales.framework.model.Message;
import com.att.sales.framework.model.ServiceMetaData;
import com.att.sales.framework.model.ServiceResponse;
import com.att.sales.framework.model.Status;
import com.att.sales.framework.service.BaseServiceImpl;
import com.att.sales.nexxus.accesspricing.service.AccessPricingServiceImpl;
import com.att.sales.nexxus.admin.model.BulkUploadEthTokenRequest;
import com.att.sales.nexxus.admin.model.BulkUploadEthTokenResponse;
import com.att.sales.nexxus.admin.model.FailedEthTokesResponse;
import com.att.sales.nexxus.admin.model.UploadEthTokenRequest;
import com.att.sales.nexxus.common.MessageConstants;
import com.att.sales.nexxus.constant.AuditTrailConstants;
import com.att.sales.nexxus.constant.CustomJsonConstants;
import com.att.sales.nexxus.constant.DataUploadConstants;
import com.att.sales.nexxus.constant.MyPriceConstants;
import com.att.sales.nexxus.constant.StringConstants;
import com.att.sales.nexxus.dao.model.NxAccessPricingData;
import com.att.sales.nexxus.dao.model.NxDataExport;
import com.att.sales.nexxus.dao.model.NxDesignAudit;
import com.att.sales.nexxus.dao.model.NxLookupData;
import com.att.sales.nexxus.dao.model.solution.NxSolutionDetail;
import com.att.sales.nexxus.dao.repository.NxAccessPricingDataRepository;
import com.att.sales.nexxus.dao.repository.NxDataExportRepository;
import com.att.sales.nexxus.dao.repository.NxDesignAuditRepository;
import com.att.sales.nexxus.dao.repository.NxLookupDataRepository;
import com.att.sales.nexxus.dao.repository.NxSolutionDetailsRepository;
import com.att.sales.nexxus.model.AccessPricingUiRequest;
import com.att.sales.nexxus.model.CircuitSiteDetails;
import com.att.sales.nexxus.model.IglooTokenExportResponse;
import com.att.sales.nexxus.model.NexxusOutputRequest;
import com.att.sales.nexxus.util.AuditUtil;
import com.att.sales.nexxus.util.NxSolutionUserLockUtil;
import com.att.sales.nexxus.util.ThreadMetaDataUtil;
import com.github.pjfanning.xlsx.StreamingReader;
import com.google.common.collect.Lists;

@Service("bulkUploadEthTokenService")
public class BulkUploadEthTokenService extends BaseServiceImpl {

	/** The log. */
	private static Logger log = LoggerFactory.getLogger(BulkUploadEthTokenService.class);

	@Autowired
	private AccessPricingServiceImpl accessPricingService;

	@Autowired
	private MailService mailService;

	/** The solution repo. */
	@Autowired
	private NxSolutionDetailsRepository solutionRepo;

	@Autowired
	private NxDesignAuditRepository nxDesignAuditRepository;

	@Autowired
	private NxTemplateProcessingService nxTemplateProcessingService;

	@Autowired
	private NxAccessPricingDataRepository accessPricingDataRepository;

	@Autowired
	private NxDataExportRepository nxDataExportRepository;

	/** The nx outpu template path. */
	@Value("${nx.failed.ethtoken.path}")
	private String failedTokenFilePath;

	@Value("${nx.ethtoken.path}")
	private String iglooTokenFilePath;
	
	@Autowired
	private NxLookupDataRepository nxLookupDataRepository;
	
	@Autowired
	private  InrQualifyService inrQualifyService;
	
	@Autowired
	private AuditUtil auditUtil;
	
	@Autowired
	private NxSolutionUserLockUtil nxSolutionUserLockUtil;

	private static String[] columns = { "Site Ref Id", "Quote ID", "Token ID", "Failure Reason" };

	/**
	 * Upload nexxus data file.
	 *
	 * @param request the request
	 * @return the data upload response
	 */
	public ServiceResponse bulkUploadEthTokens(BulkUploadEthTokenRequest request)
			throws SalesBusinessException {
		Long currentTime = System.currentTimeMillis();
		Long startTime = System.currentTimeMillis() - currentTime;

		log.info("Inside bulkUploadEthTokens method  {}", "");
		BulkUploadEthTokenResponse resp = new BulkUploadEthTokenResponse();
		Workbook workbook = null;
		workbook = StreamingReader.builder().rowCacheSize(100).bufferSize(4096).open(request.getInputStream());
		Sheet sheet = workbook.getSheetAt(0);
		java.util.Iterator<Row> rows = sheet.rowIterator();
		List<UploadEthTokenRequest> tokenList = new ArrayList<>();
		Set<String> excelTokens = new HashSet<String>();
		Set<String> excelDupTokens = new HashSet<String>();
		while (rows.hasNext()) {
			Row r = rows.next();
			if (isRowEmpty(r)) {
				continue;
			}
			if(r.getRowNum()==0 && !isValidHeader(r)) {
				String errormsg="Token import failed as you are using older version of the template."
						+ "Please download latest version and use it for importing your data";
				validationStatus(resp, MessageConstants.INVALID_DATA_MSG, MessageConstants.INVALID_DATA_CODE,
						errormsg);
				nxSolutionUserLockUtil.updateSolutionLockStatus(request.getNxSolutionId(), request.getUserId());	
				return resp;
			}
			if (skipHeader(r, DataUploadConstants.ETH_TOKNE_START_INDEX)) {
				java.util.Iterator<Cell> cells = r.cellIterator();
				UploadEthTokenRequest ethTokenRequest = new UploadEthTokenRequest();
				while (cells.hasNext()) {
					Cell c = cells.next();
					if (c.getColumnIndex() == 0) {
						String siteRefId = c.getStringCellValue();
						if (Optional.ofNullable(siteRefId).isPresent() && !siteRefId.isEmpty()) {
							ethTokenRequest.setSiteRefId(siteRefId);
						}
					}
					if (c.getColumnIndex() == 1) {
						String circuitId = c.getStringCellValue();
						if (Optional.ofNullable(circuitId).isPresent() && !circuitId.isEmpty()) {
							ethTokenRequest.setCircuitId(circuitId);
						}
					}
					if (c.getColumnIndex() == 2) {
						String quoteId = null;
						if (c.getCellType() == CellType.NUMERIC) {
							quoteId = NumberToTextConverter.toText(c.getNumericCellValue());
						} else {
							quoteId = c.getStringCellValue();
						}
						if (Optional.ofNullable(quoteId).isPresent()) {
							ethTokenRequest.setQuoteId(quoteId);
						}
					}
					if (c.getColumnIndex() == 3) {
						String portStatus = c.getStringCellValue();
						if (Optional.ofNullable(portStatus).isPresent() && !portStatus.isEmpty()) {
							ethTokenRequest.setPortStatus(portStatus);
						} else {
							ethTokenRequest.setPortStatus("New");
						}
					}
				}
				if(!excelTokens.contains(ethTokenRequest.getQuoteId())){
					tokenList.add(ethTokenRequest);
					excelTokens.add(ethTokenRequest.getQuoteId());
				}else {
					excelDupTokens.add(ethTokenRequest.getQuoteId());
				}
				
			}
		}
		String siteRefIdsNotThrere = tokenList.stream().filter(o -> o.getSiteRefId() == null).map(o -> o.getQuoteId())
				.collect(Collectors.joining(","));
		log.info("siteRefIdsNotThrere:==>>" + siteRefIdsNotThrere);
		String detailedDescription = "";
		if (Optional.ofNullable(siteRefIdsNotThrere).isPresent() && !siteRefIdsNotThrere.isEmpty()) {
			detailedDescription = detailedDescription + "Missing SiteRefId for token " + siteRefIdsNotThrere;
			validationStatus(resp, MessageConstants.INVALID_DATA_MSG, MessageConstants.INVALID_DATA_CODE,
					detailedDescription);
			nxSolutionUserLockUtil.updateSolutionLockStatus(request.getNxSolutionId(), request.getUserId());
			return resp;
		}
		// Checking duplicate tokens in excel file
		String duplicateTokens = excelDupTokens.stream().collect(Collectors.joining(", "));
		log.info("duplicateTokens:==>>" + duplicateTokens);
		// Checking duplicate tokens in db
	    Set<String> tokensDbSet = new HashSet<>();
		Set<String> quoteTokens = new HashSet<String>();
		quoteTokens.addAll(excelDupTokens);
		quoteTokens.addAll(excelTokens);
		// sending 1000 entries to IN predicate at a time
		int chunkSize = 1000;
		AtomicInteger counter = new AtomicInteger();
		Map<Object, List<String>> subTokenMap = quoteTokens.stream()
				.collect(Collectors.groupingBy(it -> counter.getAndIncrement() / chunkSize));
		for (Map.Entry<Object, List<String>> subtokenVal : subTokenMap.entrySet()) {
			List<NxAccessPricingData> accessPricingdatas = accessPricingDataRepository
					.findByTokensAndQuotes(new HashSet(subtokenVal.getValue()), request.getNxSolutionId());
			Optional.ofNullable(accessPricingdatas).map(List::stream).orElse(Stream.empty()).forEach(item -> {
				String token=item.getEthToken()!=null?item.getEthToken():item.getIglooQuoteId();
				tokensDbSet.add(token);
				log.info("DB duplicateTokens:==>>" + tokensDbSet);
			});
		}
			   
		if (Optional.ofNullable(request.getNxSolutionId()).isPresent() && request.getNxSolutionId().equals(0L)) {
			resp.setNxSolutionId(request.getNxSolutionId());
		}
		if ((Optional.ofNullable(siteRefIdsNotThrere).isPresent() && siteRefIdsNotThrere.isEmpty())) {
			    NxSolutionDetail nxSolutionDetail = null;	
	 			String description;	
			if (Optional.ofNullable(request.getNxSolutionId()).isPresent() && request.getNxSolutionId().equals(0L)) {	
				nxSolutionDetail = createNewSolution(request);	
				description = nxSolutionDetail.getNxsDescription();	
			} else {	
				nxSolutionDetail = solutionRepo.findByNxSolutionId(request.getNxSolutionId());	
				 	
				nxSolutionDetail.setIglooStatusInd("I");	
				solutionRepo.save(nxSolutionDetail);	
				description = nxSolutionDetail.getNxsDescription();	
			}
			
			resp.setNxSolutionId(request.getNxSolutionId());
			Set<String> dupIds = new HashSet<String>();
			dupIds.addAll(excelDupTokens);
			dupIds.addAll(tokensDbSet);
			resp.setDuplicateTokenId(dupIds.stream().collect(Collectors.joining(",")));
			NxSolutionDetail nxSolutionDetailToChildThread = nxSolutionDetail;	
			Map<String, Object> requestParams = new HashMap<>();	
			if (ServiceMetaData.getRequestMetaData() != null) {
       			ServiceMetaData.getRequestMetaData().forEach((key, value) -> requestParams.put(key, value));
       		}
			String conversationId = String.format("NEXXUSBULKTOKEN%d", request.getNxSolutionId());	
			String traceId = String.format("%d%d", request.getNxSolutionId(), System.currentTimeMillis());	
			requestParams.put(ServiceMetaData.XCONVERSATIONID, conversationId);	
			requestParams.put(ServiceMetaData.REST_REQUEST_START_TIME, System.currentTimeMillis());
//			requestParams.put(ServiceMetaData.XTraceId, traceId);	
//			requestParams.put(ServiceMetaData.XSpanId, traceId);	
			CompletableFuture.runAsync(() -> {	
				try {	
					ThreadMetaDataUtil.initThreadMetaData(requestParams);	
					//log.info("nxSolutionDetailToChildThread.....{}", nxSolutionDetailToChildThread);	
					int previousCount = 0;	
					List<NxAccessPricingData> previousAccessPricingDatas = accessPricingDataRepository	
							.findByHasrequiredfeilds(request.getNxSolutionId());	
					if (CollectionUtils.isNotEmpty(previousAccessPricingDatas)) {	
						previousCount = previousAccessPricingDatas.size();	
					}	
					setIglooSiteID(tokenList,request.getNxSolutionId());
					Set<String> finalTokens = excelTokens.stream().filter(((Predicate<String>) tokensDbSet::contains).negate()).collect(Collectors.toSet());
					List<List<UploadEthTokenRequest>> smallerTokensList = Lists.partition(tokenList, 20);	
					for (List<UploadEthTokenRequest> tokens : smallerTokensList) {	
						AccessPricingUiRequest req = new AccessPricingUiRequest();	
						req.setAction(request.getAction());	
						req.setUserId(request.getUserId());	
						req.setNxSolutionId(request.getNxSolutionId());	
						req.setBulkupload(true);	
						List<String> dqIds = new ArrayList<>();	
						List<String> circuitIds = new ArrayList<>();	
						tokens.forEach(token -> {	
							if(finalTokens.contains(token.getQuoteId())) {
								dqIds.add(token.getQuoteId().trim());	
							}
								
						});	
						req.setDqId(dqIds);	
						req.setCircuitId(circuitIds);	
						req.setBulkUploadTokens(tokens);	
						
						try {	
							log.info("req"+req);	
							if(CollectionUtils.isNotEmpty(dqIds)) {
								accessPricingService.getAccessPricing(req);	
							}
						} catch (SalesBusinessException e) {	
							log.error("Exceotion during AP processing {}", e.getMessage());	
						}	
							
					}	
					try {	
						if (Optional.ofNullable(request.getNxSolutionId()).isPresent()	
								&& Optional.ofNullable(request.getUserId()).isPresent()) {	
							List<NxAccessPricingData> accessPricingDatas = accessPricingDataRepository	
									.findByHasrequiredfeilds(request.getNxSolutionId());	
							int status;	
							if (CollectionUtils.isNotEmpty(accessPricingDatas) && CollectionUtils.isNotEmpty(tokenList)	
									&& tokenList.size() == (accessPricingDatas.size() - previousCount)) {	
								status = 1; // success	
							} else if (CollectionUtils.isNotEmpty(accessPricingDatas)	
									&& accessPricingDatas.size() > previousCount) {	
								status = 3; // partial	
							} else {	
								status = 2; // fail	
							}	
							String nxDescription = description + " [ID:" + request.getNxSolutionId() + "] ";	
							mailService.prepareAndSendMailForBulkuploadEthTokensRequest(request.getNxSolutionId(),	
									request.getUserId(), nxDescription, status);	
						}	
					} catch (SalesBusinessException e) {	
						log.error("Error : While sendig email after successful upload ethernet tokens : {}", e);	
					}	
				} finally {	
					ThreadMetaDataUtil.destroyThreadMetaData();	
				}	
			}).thenAccept(action -> {	
				try {	
					ThreadMetaDataUtil.initThreadMetaData(requestParams);	
					nxSolutionDetailToChildThread.setIglooStatusInd("C");	
					solutionRepo.save(nxSolutionDetailToChildThread);	
					log.info("BulkUpload child thread completed........");
					nxSolutionUserLockUtil.updateSolutionLockStatus(request.getNxSolutionId(), request.getUserId());
				} finally {	
					ThreadMetaDataUtil.destroyThreadMetaData();	
				}	
			}).exceptionally(exception -> {	
				try {	
					ThreadMetaDataUtil.initThreadMetaData(requestParams);	
					nxSolutionDetailToChildThread.setIglooStatusInd("I");	
					solutionRepo.save(nxSolutionDetailToChildThread);	
					log.info("BulkUpload child thread completed exceptionally : {}",exception.getMessage());
					nxSolutionUserLockUtil.updateSolutionLockStatus(request.getNxSolutionId(), request.getUserId());	
				} finally {	
					ThreadMetaDataUtil.destroyThreadMetaData();	
				}	
					
				return null;	
			});				
			setSuccessResponse(resp);
		}
		try {
			workbook.close();
		} catch (IOException e) {
			log.error("Exception during workbok processing {}", e.getMessage());
			nxSolutionUserLockUtil.updateSolutionLockStatus(request.getNxSolutionId(), request.getUserId());
		}
		Long endTime = System.currentTimeMillis() - currentTime;
        Long executionTime=endTime-startTime;
		//for capturing audit trail	
		auditUtil.addActionToNxUiAudit(request.getNxSolutionId(),AuditTrailConstants.ADD_BULK_IGLOO_QUOTES,request.getUserId(),AuditTrailConstants.SUCCESS,null,null,executionTime,null);
		return resp;
	}

	public NxSolutionDetail createNewSolution(BulkUploadEthTokenRequest request) throws SalesBusinessException {
		Long currentTime = System.currentTimeMillis();
		Long startTime = System.currentTimeMillis() - currentTime;
		
		Long nxSolutionId = null;
		String description = StringConstants.FLOW_TYPE_IGLOO_QUOTE + "_"
				+ LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
		NxSolutionDetail nxSolutionDetail = new NxSolutionDetail();
		nxSolutionDetail.setFlowType(StringConstants.FLOW_TYPE_IGLOO_QUOTE);
		nxSolutionDetail.setCreatedUser(request.getUserId());
		nxSolutionDetail.setActiveYn("Y");
		nxSolutionDetail.setIglooStatusInd("I");
		nxSolutionDetail.setNxsDescription(description);
		nxSolutionDetail.setArchivedSolInd("N");
		nxSolutionDetail = solutionRepo.save(nxSolutionDetail);
		accessPricingService.createNxTeamEntry(nxSolutionDetail, request.getUserId());
		nxSolutionId = nxSolutionDetail.getNxSolutionId();
		request.setNxSolutionId(nxSolutionId);
		Long endTime = System.currentTimeMillis() - currentTime;
        Long executionTime=endTime-startTime;
		//for capturing audit trail	
		auditUtil.addActionToNxUiAudit(request.getNxSolutionId(),AuditTrailConstants.IGLOO_SOLUTION_CREATE,request.getUserId(),AuditTrailConstants.SUCCESS,null,null,executionTime,null);
		return nxSolutionDetail;
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

	public void validationStatus(ServiceResponse objResponse, String description, String code,
			String detailedDescription) {
		Status status = new Status();
		List<Message> msgList = new ArrayList<>();
		Message successMessage = new Message(code, description, detailedDescription);
		msgList.add(successMessage);
		status.setMessages(msgList);
		status.setCode(code);
		objResponse.setStatus(status);
	}

	public ServiceResponse downloadFailedTokenFile(NexxusOutputRequest request) {

		FailedEthTokesResponse response = new FailedEthTokesResponse();
		// Create a Workbook
		Workbook workbook = new XSSFWorkbook(); // new HSSFWorkbook() for generating `.xls` file

		try {

			// Create a Sheet
			Sheet sheet = workbook.createSheet("FailedTokens");

			// Create a Font for styling header cells
			Font headerFont = workbook.createFont();
			headerFont.setBold(true);
			headerFont.setFontHeightInPoints((short) 14);
			headerFont.setColor(IndexedColors.RED.getIndex());

			// Create a CellStyle with the font
			CellStyle headerCellStyle = workbook.createCellStyle();
			headerCellStyle.setFont(headerFont);

			// Create a Row
			Row headerRow = sheet.createRow(0);

			// Create cells
			for (int i = 0; i < columns.length; i++) {
				Cell cell = headerRow.createCell(i);
				cell.setCellValue(columns[i]);
				cell.setCellStyle(headerCellStyle);
			}

			List<NxDesignAudit> iglooImportFailList = nxDesignAuditRepository
					.findFailedTokensByTransactionAndNxRefId("Ethernet Token Bulkupload", request.getNxSolutionId());
			List<NxAccessPricingData> mpFailTokenList = accessPricingDataRepository
					.findByNxSolIdAndMpStatus(request.getNxSolutionId(), "RF");
			if (CollectionUtils.isNotEmpty(iglooImportFailList) || CollectionUtils.isNotEmpty(mpFailTokenList)) {
				AtomicInteger rowNum = new AtomicInteger(1);
				if (CollectionUtils.isNotEmpty(iglooImportFailList)) {
					try {
						NxDesignAudit audit = iglooImportFailList.get(0);
						String data = audit.getData();
						JSONParser parser = new JSONParser();
						JSONArray array = (JSONArray) parser.parse(data);
						array.forEach(item -> {
							JSONObject jsonObject = (JSONObject) item;
							String quoteId = jsonObject.get("quoteId") != null ? jsonObject.get("quoteId").toString()
									: "";
							String siteRefId = jsonObject.get("siteRefId") != null
									? jsonObject.get("siteRefId").toString()
									: "";
							Row row = sheet.createRow(rowNum.getAndIncrement());
							row.createCell(0).setCellValue(siteRefId);
							row.createCell(1).setCellValue(quoteId);
							row.createCell(2).setCellValue("");
							row.createCell(3).setCellValue(StringConstants.IGLOO_FAIL);
						});
					} catch (ParseException e) {
						e.printStackTrace();
					}
				}
				if (CollectionUtils.isNotEmpty(mpFailTokenList)) {
					for (NxAccessPricingData nxAccessObj : mpFailTokenList) {
						String tokenId = nxAccessObj.getEthToken() != null ? nxAccessObj.getEthToken() : "";
						String siterefId = nxAccessObj.getSiteRefId() != null ? nxAccessObj.getSiteRefId() : "";
						String quoteid = nxAccessObj.getIglooQuoteId() != null ? nxAccessObj.getIglooQuoteId() : "";
						String failreason = "";
						String restResponseError=nxAccessObj.getRestResponseError() != null
								? nxAccessObj.getRestResponseError() : "";
						String requiredFieldError = nxAccessObj.getRequiredFieldError() != null
								? nxAccessObj.getRequiredFieldError() : "";
						
						if(StringUtils.isNotBlank(restResponseError)) {
							List<String> errorDetail= new ArrayList<String>();
							Map<String,String> errorMap = getMapfromString(restResponseError);
							if(errorMap.containsKey(CustomJsonConstants.REST_RESPONSE_ERROR) && 
								null != errorMap.get(CustomJsonConstants.REST_RESPONSE_ERROR) &&
								!"null".equalsIgnoreCase(errorMap.get(CustomJsonConstants.REST_RESPONSE_ERROR))) {
								errorDetail.add(errorMap.get(CustomJsonConstants.REST_RESPONSE_ERROR));
							}
							if(errorMap.containsKey(CustomJsonConstants.CONFIG_BOM_ERROR_DATA) && 
								null != errorMap.get(CustomJsonConstants.CONFIG_BOM_ERROR_DATA) &&
								!"null".equalsIgnoreCase(errorMap.get(CustomJsonConstants.CONFIG_BOM_ERROR_DATA))) {
								errorDetail.add(errorMap.get(CustomJsonConstants.CONFIG_BOM_ERROR_DATA));
							}
							if(errorMap.containsKey(CustomJsonConstants.SITE_CONFIG_ERROR_MAP) &&
								null !=  errorMap.get(CustomJsonConstants.SITE_CONFIG_ERROR_MAP)
								&& !"null".equalsIgnoreCase(errorMap.get(CustomJsonConstants.SITE_CONFIG_ERROR_MAP))){
								errorDetail.add(errorMap.get(CustomJsonConstants.SITE_CONFIG_ERROR_MAP));
								}
							failreason=String.join(" , ",errorDetail);
						} 
						
						if (StringUtils.isNotBlank(requiredFieldError)) {
							failreason = StringUtils.isNotBlank(failreason)? failreason +" and " + requiredFieldError:requiredFieldError;
						}
								
						Row row = sheet.createRow(rowNum.getAndIncrement());
						row.createCell(0).setCellValue(siterefId);
						row.createCell(1).setCellValue(quoteid);
						row.createCell(2).setCellValue(tokenId);
						row.createCell(3).setCellValue(failreason);
					}
				}
				// Resize all columns to fit the content size
				for (int i = 0; i < columns.length; i++) {
					sheet.autoSizeColumn(i);
				}

				// Write the output to a file
				FileOutputStream fileOut = null;
				try {
					DateTimeFormatter format = DateTimeFormatter.ofPattern("ddMMyyyyHHmmss");
					String fileName = request.getNxSolutionId() + format.format(LocalDateTime.now()) + ".xlsx";
					Path path = nxTemplateProcessingService.getFilePath(failedTokenFilePath + fileName);
					log.info("URI:==>>" + path.toUri());
					fileOut = new FileOutputStream(path.toString());
					workbook.write(fileOut);
					byte[] fileBytes = Files.readAllBytes(path);
					Blob blob = null;
					try {
						blob = new SerialBlob(fileBytes);
					} catch (SerialException e) {
						log.error("downloadFailedTokenFile() : Error : While serialization ", e);
					} catch (SQLException e) {
						log.error("downloadFailedTokenFile() : Error : SQLException ", e);
					}
					// String fileString = Base64.getEncoder().encodeToString(fileBytes);
					response.setFile(blob);
					response.setFileName(fileName);
					setSuccessResponse(response);
				} catch (IOException | SalesBusinessException e) {
					e.printStackTrace();
				} finally {
					try {
						fileOut.close();
					} catch (IOException e) {
						log.error("downloadFailedTokenFile() : Error : IOException ", e);
					}
				}
			} else {
				String detailedDescription = "Failed tokens not exist";
				validationStatus(response, MessageConstants.INVALID_DATA_MSG, MessageConstants.INVALID_DATA_CODE,
						detailedDescription);
			}
		} finally {
			try {
				workbook.close();
			} catch (IOException e) {
				log.error("downloadFailedTokenFile() : Error : IOException While closing workbook ", e);
			}
		}
		return response;
	}

	public IglooTokenExportResponse downloadIglooTokenFile(NexxusOutputRequest request) {

		IglooTokenExportResponse response = new IglooTokenExportResponse();
		// Create a Workbook
		Workbook workbook = new XSSFWorkbook(); // new HSSFWorkbook() for generating `.xls` file

		try {

			// Create a Sheet
			Sheet sheet = workbook.createSheet(StringConstants.IGLOO_EXPORT_SHEET_NAME);

			// Create a Font for styling header cells
			Font headerFont = workbook.createFont();
			headerFont.setFontHeightInPoints((short) 14);
			headerFont.setColor(IndexedColors.WHITE.getIndex());

			// Create a CellStyle with the font
			CellStyle headerCellStyle = workbook.createCellStyle();
			headerCellStyle.setFont(headerFont);
			// fill foreground color ...
			headerCellStyle.setFillForegroundColor(IndexedColors.DARK_GREEN.index);
			// and solid fill pattern produces solid grey cell fill
			headerCellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
			// Create a Row
			Row headerRow = sheet.createRow(0);

			List<NxDataExport> nxDataExports = nxDataExportRepository
					.getNxDataExport(StringConstants.FLOW_TYPE_IGLOO.toUpperCase(), StringConstants.CONSTANT_Y);

			for (int i = 0; i < nxDataExports.size(); i++) {
				Cell cell = headerRow.createCell(i);
				cell.setCellValue(nxDataExports.get(i).getDisplayName());
				cell.setCellStyle(headerCellStyle);
			}

			List<NxAccessPricingData> nxAccessPricingDataList = accessPricingDataRepository
					.findByHasrequiredfeilds(request.getNxSolutionId());
			if (CollectionUtils.isNotEmpty(nxAccessPricingDataList)) {
				try {
					AtomicInteger rowNum = new AtomicInteger(1);
					for (NxAccessPricingData nxAccessPricingData : nxAccessPricingDataList) {
						Row row = sheet.createRow(rowNum.getAndIncrement());
						for (int i = 0; i < nxDataExports.size(); i++) {
							String[] vars = nxDataExports.get(i).getVariableName()
									.split(Pattern.quote(MyPriceConstants.OR_CONDITION_SEPERATOR));
							Object data = null;
							for (String var : vars) {
								Field field = NxAccessPricingData.class.getDeclaredField(var);
								boolean accessible = field.isAccessible();
								field.setAccessible(true);
								if ("Long".equalsIgnoreCase(nxDataExports.get(i).getDataType())) {
									data = field.getLong(nxAccessPricingData);
								} else {
									data = field.get(nxAccessPricingData);
								}
								field.setAccessible(accessible);
								if (data != null) {
									break;
								}
							}
							if (data != null && data instanceof Long) {
								row.createCell(i).setCellValue(Long.parseLong(data.toString()));
							} else {
								if (data != null) {
									if (nxDataExports.get(i).getDisplayName().equals("SUBMITTED_TO_MYPRICE")) {
										if (data.toString().equals("RS")) {
											row.createCell(i).setCellValue("Submitted");
										} else if (data.toString().equals("RF")) {
											row.createCell(i).setCellValue("Submission Failed");
										} else {
											row.createCell(i).setCellValue(data.toString());
										}
									} else {
										row.createCell(i).setCellValue(data.toString());
									}
								} else {
									if (nxDataExports.get(i).getDisplayName().equals("SUBMITTED_TO_MYPRICE")) {
										row.createCell(i).setCellValue("Not Submitted");
									} else {
										row.createCell(i).setCellValue(data != null ? data.toString() : null);
									}
								}
							}
						}

					}
				} catch (NoSuchFieldException | IllegalArgumentException | IllegalAccessException
						| SecurityException e) {
					e.printStackTrace();
				}
				// Resize all columns to fit the content size
				for (int i = 0; i < nxDataExports.size(); i++) {
					sheet.autoSizeColumn(i);
				}

				// Write the output to a file
				FileOutputStream fileOut = null;
				try {
					String fileName = StringConstants.IGLOO_EXPORT_FILE_NAME + request.getNxSolutionId() + ".xlsx";
					Path path = nxTemplateProcessingService.getFilePath(iglooTokenFilePath + fileName);
					fileOut = new FileOutputStream(path.toString());
					workbook.write(fileOut);
					byte[] fileBytes = Files.readAllBytes(path);
					Blob blob = null;
					try {
						blob = new SerialBlob(fileBytes);
					} catch (SerialException e) {
						log.error("downloadIglooTokenFile() : Error : While serialization ", e);
					} catch (SQLException e) {
						log.error("downloadIglooTokenFile() : Error : SQLException ", e);
					}
					response.setFile(blob);
					response.setFileName(fileName);
					setSuccessResponse(response);
				} catch (IOException | SalesBusinessException e) {
					e.printStackTrace();
				} finally {
					try {
						fileOut.close();
					} catch (IOException e) {
						log.error("downloadIglooTokenFile() : Error : IOException ", e);
					}
				}
			} else {
				String detailedDescription = "Tokens not exist";
				validationStatus(response, MessageConstants.INVALID_DATA_MSG, MessageConstants.INVALID_DATA_CODE,
						detailedDescription);
			}
		} finally {
			try {
				workbook.close();
			} catch (IOException e) {
				log.error("downloadIglooTokenFile() : Error : IOException While closing workbook ", e);
			}
		}
		return response;
	}

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
	
	public Map<String,String> getMapfromString(String mapAsString){
		mapAsString=mapAsString.substring(1, mapAsString.length()-1);
		Map<String, String> map = Arrays.stream(mapAsString.split(","))
				  .map(entry -> entry.trim())
			      .map(entry -> entry.split("="))
			      .collect(Collectors.toMap(entry -> entry[0], entry -> entry[1]));
		Map<String, String> mapResult= new HashMap<>();
        for (Map.Entry<String,String> entry : map.entrySet()) {
        	String mapdata = entry.getValue();
        	if(mapdata.startsWith("[")) {
        		mapdata=mapdata.substring(1, mapdata.length()-1);
        	}      	
        	else if(mapdata.startsWith("{")) {
        		mapdata=mapdata.substring(1, mapdata.length()-1);
        	}
        	mapResult.put(entry.getKey(), mapdata);
        }
		
		return mapResult;
	}
	
	protected Boolean isValidHeader(Row r) {
		boolean result=true;
			java.util.Iterator<Cell> cells = r.cellIterator();
			List<NxLookupData> lookupData=nxLookupDataRepository.findByDatasetNameAndItemId("BULK_UPLOAD_ETHTOKEN","Header");
			NxLookupData nxLookupData =lookupData.get(0);
			String criteria=nxLookupData.getCriteria();
			String[] excelheader=criteria.split(",");
			if(excelheader.length!=r.getLastCellNum()) {
				result=false;
				return result;
			}
			for(int i=0;cells.hasNext();i++) {
				Cell c = cells.next();
				String columnHeader=c.getStringCellValue().trim();
				if(!excelheader[i].equalsIgnoreCase(columnHeader)) {
					result=false;
					return result;
				}
			}
		
		return result;
	}
	
	protected void setIglooSiteID(List<UploadEthTokenRequest>	bulktokenListrequest,Long nxSolutionId) {
		Map<String, List<CircuitSiteDetails>> cktSiteMap = inrQualifyService.prepareInrCktSiteMap(nxSolutionId);
		if(cktSiteMap!=null && MapUtils.isNotEmpty(cktSiteMap)) {
		for(UploadEthTokenRequest uploadToken: bulktokenListrequest) {
				String circuitId=StringUtils.isNotBlank(uploadToken.getCircuitId())?uploadToken.getCircuitId():null;
				if(circuitId!=null) {
					String circuitIdModified=circuitId.replaceAll("\\s", "").replaceAll("\\.", "");
					List<CircuitSiteDetails> circuits = circuitIdModified != null
							? cktSiteMap.entrySet().stream()
									.filter(c -> c.getKey().trim().equalsIgnoreCase(circuitIdModified.trim()))
									.map(Map.Entry::getValue).findAny().orElse(null)
							: null;
					if(CollectionUtils.isNotEmpty(circuits)) {
						if(circuits.size()>1) {
							for(CircuitSiteDetails c:circuits) {
								if("A".equalsIgnoreCase(c.getEndType())) {
									uploadToken.setNxsiteId(c.getId());
								}
							}
						}else {
							uploadToken.setNxsiteId(circuits.get(0).getId());
						}
					}				
				}
			}
		}
	}


}
