package com.att.sales.nexxus.service;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Blob;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import javax.sql.rowset.serial.SerialBlob;
import javax.sql.rowset.serial.SerialException;
import javax.transaction.Transactional;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import com.att.aft.dme2.internal.google.common.collect.Lists;
import com.att.sales.framework.exception.SalesBusinessException;
import com.att.sales.framework.model.Status;
import com.att.sales.framework.service.BaseServiceImpl;
import com.att.sales.nexxus.admin.model.EdfDownloadRequestDetailsResponse;
import com.att.sales.nexxus.admin.model.FailedDmaapMessageResponse;
import com.att.sales.nexxus.common.CommonConstants;
import com.att.sales.nexxus.constant.AuditTrailConstants;
import com.att.sales.nexxus.constant.DataUploadConstants;
import com.att.sales.nexxus.constant.MessageConstants;
import com.att.sales.nexxus.constant.MyPriceConstants;
import com.att.sales.nexxus.constant.StringConstants;
import com.att.sales.nexxus.dao.model.NxLookupData;
import com.att.sales.nexxus.dao.model.NxOutputFileModel;
import com.att.sales.nexxus.dao.model.NxRequestDetails;
import com.att.sales.nexxus.dao.model.NxRequestGroup;
import com.att.sales.nexxus.dao.model.NxUser;
import com.att.sales.nexxus.dao.model.solution.NxSolutionDetail;
import com.att.sales.nexxus.dao.model.solution.NxTeam;
import com.att.sales.nexxus.dao.repository.NxLookupDataRepository;
import com.att.sales.nexxus.dao.repository.NxOutputFileRepository;
import com.att.sales.nexxus.dao.repository.NxRequestDetailsRepository;
import com.att.sales.nexxus.dao.repository.NxRequestGroupRepository;
import com.att.sales.nexxus.dao.repository.NxSolutionDetailsRepository;
import com.att.sales.nexxus.dao.repository.NxTeamRepository;
import com.att.sales.nexxus.dao.repository.NxUserRepository;
import com.att.sales.nexxus.edf.model.ValidateAccountDataRequest;
import com.att.sales.nexxus.edf.model.ValidateAccountDataRequestDetails;
import com.att.sales.nexxus.edf.model.ValidateAccountDataResponse;
import com.att.sales.nexxus.edfbulkupload.EdfManBulkUploadRequest;
import com.att.sales.nexxus.edfbulkupload.EdfbulkUploadResponse;
import com.att.sales.nexxus.edfbulkupload.UploadMANbulkRequest;
import com.att.sales.nexxus.model.NexxusOutputRequest;
import com.att.sales.nexxus.userdetails.service.UserServiceImpl;
import com.att.sales.nexxus.util.HttpRestClient;
import com.att.sales.nexxus.util.AuditUtil;
import com.att.sales.nexxus.util.DME2RestClient;
import com.att.sales.nexxus.util.NxSolutionUserLockUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @author KRani
 *
 */
@Service("edfBulkUploadService")
@Transactional
public class BulkUploadEdfService extends BaseServiceImpl {

	/** edf each request chunk size. */
	@Value("${edf.request.chunk.size}")
	private String requestsToEdfChunkSize;
	
	@Value("${nx.edfbulkuploadrequest.template.path}")
	private String downloadEdfBulkuploadRequest;
	
	private static String[] maxcolumns = {"MAN /MCN", "Type","Product","Bill Month","Correlation ID","Reason Code","Reason Description"};
	
	@Value("${nx.faileddmaapmsg.template.path}")
	private String failedDmaapMsgRequest;
	 
	@Autowired
	private NxSolutionDetailsRepository nxSolutionDetailsRepo;
	
	@Autowired
	private NxTeamRepository nxTeamRepository;

	@Autowired
	private NxRequestGroupRepository nxRequestGroupRepository;
	
	@Autowired
	private ObjectMapper mapper;
	
	@Autowired
	private NxTemplateProcessingService nxTemplateProcessingService;
	
	@Autowired
	private NxRequestDetailsRepository nxRequestDetailsRepository;
	
	@Autowired
	private NxLookupDataRepository nxLookupDataRepository;
	
	private static String[] columns = {"MAN/MCN", "Type","Product","Usage or Non Usage Indicator","Begin Bill Month",
			"Bill Month","CPNI Approver","Customer Name"};
	
	@Autowired
	 private NxOutputFileRepository nxOutputFileRepository;
	
	@Autowired
	private UserServiceImpl userServiceImpl;

	@Autowired
	private NxUserRepository nxUserRepository;
	
	@Autowired
	private HttpRestClient httpRestClient;

	@Autowired
	private AuditUtil auditUtil;
	
	@Autowired
	private NxSolutionUserLockUtil nxSolutionUserLockUtil;
	
	/** The nexxus object mapper. */
	private ObjectMapper nexxusObjectMapper = new ObjectMapper().configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES, true);
	
	/** The logger. */
	private static Logger logger = LoggerFactory.getLogger(BulkUploadEdfService.class);

	public EdfbulkUploadResponse manBulkUploadToEDF(EdfManBulkUploadRequest request) throws SalesBusinessException {
		Long currentTime = System.currentTimeMillis();
		Long startTime = System.currentTimeMillis() - currentTime;

		EdfbulkUploadResponse response = new EdfbulkUploadResponse();
		Status status = new Status();
		status.setCode(CommonConstants.SUCCESS_STATUS);
		Workbook edfWorkBook = null;
		String manOrMcnNumber = null;
		String description = null;
		NxSolutionDetail nxSolutionDetail = null;
		if(Optional.ofNullable(request.getNxSolutionId()).isPresent() && request.getNxSolutionId().equals(0L)){
			nxSolutionDetail = createNxSolutionId(request);	
			response.setNxSolutionDesc(nxSolutionDetail.getNxsDescription());
			logger.info("New Solution description.....{} ",nxSolutionDetail.getNxsDescription());
			response.setInrStatusInd(nxSolutionDetail.getInrStatusInd());
		} else {
			logger.info("nxSolutionId from UI request...... {}",request.getNxSolutionId());
			List<NxSolutionDetail> nxSolutionDetailList = nxSolutionDetailsRepo.findByNxSolutionIdAndActiveYn(request.getNxSolutionId(), "Y");
			if(CollectionUtils.isNotEmpty(nxSolutionDetailList)) {
				nxSolutionDetail = nxSolutionDetailList.get(0);
				logger.info("nxSolutionDetail.getNxsDescription() from Database......",nxSolutionDetail.getNxsDescription());
				nxSolutionDetail.setInrStatusInd("I");
				nxSolutionDetailsRepo.save(nxSolutionDetail);
				response.setNxSolutionDesc(nxSolutionDetail.getNxsDescription());
				response.setInrStatusInd(nxSolutionDetail.getInrStatusInd());
			}
		}
		edfWorkBook = com.github.pjfanning.xlsx.StreamingReader.builder().rowCacheSize(100).bufferSize(4096).open(request.getInputStream());
		Sheet sheet = edfWorkBook.getSheetAt(0);
		java.util.Iterator<Row> rows = sheet.rowIterator();
		List<UploadMANbulkRequest> edfBulkUploadList = new ArrayList<>();
		while (rows.hasNext()) {
			Row row = rows.next();
			if (checkIfRowEmpty(row)) {
				continue;
			}
			if (skipHeader(row, DataUploadConstants.EDF_BULK_UPLOAD_START_INDEX)) {
				java.util.Iterator<Cell> cells = row.cellIterator();
				UploadMANbulkRequest edfbulkuploadRequest = new UploadMANbulkRequest();
				while (cells.hasNext()) {
					Cell cell = cells.next();
					if (cell.getColumnIndex() == 0) {
						manOrMcnNumber = cell.getStringCellValue().trim();
					}
					if (cell.getColumnIndex() == 1) {
						String type = cell.getStringCellValue();
						if (Optional.ofNullable(type).isPresent()) {
							edfbulkuploadRequest.setType(type);
							if (type.trim().equalsIgnoreCase("MAN")) {
								edfbulkuploadRequest.setManAccountNumber(manOrMcnNumber);
							} else if (type.trim().equalsIgnoreCase("MCN")) {
								edfbulkuploadRequest.setMcnNumber(manOrMcnNumber);
							}
						}
					}
					if (cell.getColumnIndex() == 2) {
						String product = cell.getStringCellValue().trim();
						if (Optional.ofNullable(product).isPresent() && !product.isEmpty()) {
							edfbulkuploadRequest.setProduct(product);
						}
					}
					if (cell.getColumnIndex() == 3) {
						String usageorNonUsageIndicator = cell.getStringCellValue().trim();
						if (Optional.ofNullable(usageorNonUsageIndicator).isPresent()&& !usageorNonUsageIndicator.isEmpty()) {
							edfbulkuploadRequest.setUsageOrNonUsageIndicator(usageorNonUsageIndicator);
						}

					}
					if (cell.getColumnIndex() == 4) {
						String beginBillMonth = cell.getStringCellValue().trim();
						if (Optional.ofNullable(beginBillMonth).isPresent() && !beginBillMonth.isEmpty()) {
							edfbulkuploadRequest.setBeginBillMonth(beginBillMonth);
						}

					}

					if (cell.getColumnIndex() == 5) {
						String billMonth = cell.getStringCellValue().trim();
						if (Optional.ofNullable(billMonth).isPresent() && !billMonth.isEmpty()) {
							edfbulkuploadRequest.setBillMonth(billMonth);
						}

					}

					if (cell.getColumnIndex() == 6) {
						String cpniApprover = cell.getStringCellValue().trim();
						if (Optional.ofNullable(cpniApprover).isPresent() && !cpniApprover.isEmpty()) {
							edfbulkuploadRequest.setCpniApprover(cpniApprover);
						}

					}
					if (cell.getColumnIndex() == 7) {
						String customerName = cell.getStringCellValue().trim();
						if (Optional.ofNullable(customerName).isPresent() && !customerName.isEmpty()) {
							edfbulkuploadRequest.setCustomerName(customerName);
						}

					}
				
				}
				edfBulkUploadList.add(edfbulkuploadRequest);
			 }
		}
		logger.debug("edfBulkUploadList .....................{} ",edfBulkUploadList);
		logger.info("requestsToEdfChunkSize.................{}",requestsToEdfChunkSize);
		if(CollectionUtils.isNotEmpty(edfBulkUploadList)) {
			response.setNxSolutionId(request.getNxSolutionId());
			String nxUserId = request.getUserId();
			Long nxSolutionId = request.getNxSolutionId();
			NxSolutionDetail nxSolutionDetailToChildThread =  nxSolutionDetail;
			CompletableFuture.runAsync(() -> {
				try {
					TimeUnit.SECONDS.sleep(3);
					processAndVadidateAccountData(edfBulkUploadList, nxUserId, nxSolutionDetailToChildThread);
				} catch (SalesBusinessException | InterruptedException exp) {
					logger.error("Exception from BulkUploadEdfService.manBulkUploadToEDF", exp);
					nxSolutionUserLockUtil.updateSolutionLockStatus(request.getNxSolutionId(), request.getUserId());
				}
			}).thenAccept(action ->  {
				nxSolutionDetailToChildThread.setInrStatusInd("C");
				nxSolutionDetailsRepo.save(nxSolutionDetailToChildThread);
				logger.info("BulkUpload child thread completed........");
				nxSolutionUserLockUtil.updateSolutionLockStatus(request.getNxSolutionId(), request.getUserId());
			}).exceptionally(exception -> {
				nxSolutionDetailToChildThread.setInrStatusInd("I");
				nxSolutionDetailsRepo.save(nxSolutionDetailToChildThread);
				logger.info("BulkUpload child thread completed exceptionally........");
				nxSolutionUserLockUtil.updateSolutionLockStatus(request.getNxSolutionId(), request.getUserId());
				return null;
			});
			nxSolutionUserLockUtil.updateSolutionLockStatus(request.getNxSolutionId(), request.getUserId());		
			setSuccessResponse(response);
			Long endTime = System.currentTimeMillis() - currentTime;
            Long executionTime=endTime-startTime;
			//for capturing audit trail	
			auditUtil.addActionToNxUiAudit(request.getNxSolutionId(),AuditTrailConstants.ADD_BULK_INR_PRODUCTS,request.getUserId(),AuditTrailConstants.SUCCESS,null,null,executionTime,null);
			return response;

		} else {
			status.setCode(CommonConstants.FAILURE_STATUS);
			logger.error("Exception while parsing bulkupload excel BulkUploadEdfService.manBulkUploadToEDF");
			nxSolutionUserLockUtil.updateSolutionLockStatus(request.getNxSolutionId(), request.getUserId());	
			Long endTime = System.currentTimeMillis() - currentTime;
            Long executionTime=endTime-startTime;	
			auditUtil.addActionToNxUiAudit(request.getNxSolutionId(),AuditTrailConstants.ADD_BULK_INR_PRODUCTS,request.getUserId(),AuditTrailConstants.FAIL,null,null,executionTime,null);
			return response;
		}
	}

	private boolean checkIfRowEmpty(Row row) {	
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

	private boolean skipHeader(Row row, int edfBulkUploadStartIndex) {
		return row.getRowNum() >= edfBulkUploadStartIndex;
	}

	private NxSolutionDetail createNxSolutionId(EdfManBulkUploadRequest request) {
		Long currentTime = System.currentTimeMillis();
        Long startTime = System.currentTimeMillis() - currentTime;
		
		String description =StringConstants.INR_MAN_BULK_UPLOAD +"_" + LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
		Long nxSolutionId = null;
		NxSolutionDetail nxSolutionDetails = new NxSolutionDetail();
		nxSolutionDetails.setFlowType(StringConstants.FLOW_TYPE_INR);
		nxSolutionDetails.setCreatedUser(request.getUserId());
		nxSolutionDetails.setActiveYn("Y");
		nxSolutionDetails.setOptyId(request.getOptyId());
		nxSolutionDetails.setNxsDescription(description);
		nxSolutionDetails.setInrStatusInd("I");
		nxSolutionDetails.setArchivedSolInd("N");
		nxSolutionDetails = nxSolutionDetailsRepo.save(nxSolutionDetails);
		NxTeam nxTeam = new NxTeam();
		String userProfileName = userServiceImpl.getUserProfileName(request.getUserId());
		if (!UserServiceImpl.NONE.equals(userProfileName)) {
			NxUser nxUser = nxUserRepository.findByUserAttId(request.getUserId());
			nxTeam.setAttuid(nxUser.getUserAttId());
			nxTeam.setEmail(nxUser.getEmail());
			nxTeam.setfName(nxUser.getFirstName());
			nxTeam.setlName(nxUser.getLastName());
			nxTeam.setIsPryMVG("y");
			nxTeam.setNxSolutionDetail(nxSolutionDetails);
		}else {
			nxTeam.setAttuid(request.getUserId());
			nxTeam.setIsPryMVG("Y");
			nxTeam.setNxSolutionDetail(nxSolutionDetails);
		}
		nxTeamRepository.save(nxTeam);

		nxSolutionId = nxSolutionDetails.getNxSolutionId();
		nxSolutionDetails.setNxsDescription(description);
		logger.info("description............{}",nxSolutionDetails.getNxsDescription());
		logger.info("NxSolutionId is:" +" "+ nxSolutionId);
		request.setNxSolutionId(nxSolutionId);
		Long endTime = System.currentTimeMillis() - currentTime;
        Long executionTime=endTime-startTime;
		//for capturing audit trail	
		auditUtil.addActionToNxUiAudit(request.getNxSolutionId(),AuditTrailConstants.INR_SOLUTION_CREATE,request.getUserId(),AuditTrailConstants.SUCCESS,null,null,executionTime,null);
		return nxSolutionDetails;
	}
		
	public void processAndVadidateAccountData(List<UploadMANbulkRequest> bulkRequestsListToUpload, String userId, NxSolutionDetail nxSolutionDetail) throws SalesBusinessException {

		if (CollectionUtils.isNotEmpty(bulkRequestsListToUpload)) {
			LinkedHashMap<BulkInvGroupByKey, List<UploadMANbulkRequest>> intermediateBulkRequests = bulkRequestsListToUpload
					.stream()
					.collect(Collectors.groupingBy(
							p -> new BulkInvGroupByKey(p.getProduct(), p.getBeginBillMonth(), p.getBillMonth(),p.getUsageOrNonUsageIndicator()),
							LinkedHashMap::new, Collectors.toList()));
			 logger.debug("intermediateBulkRequests ....................... {}",intermediateBulkRequests);
			 Set<Entry<BulkInvGroupByKey, List<UploadMANbulkRequest>>> groupBulkRequestsKayValue = intermediateBulkRequests.entrySet();
			 List<ValidateAccountDataRequestDetails> validateAccountDataRequestList = new ArrayList<>();
			 for(Entry<BulkInvGroupByKey, List<UploadMANbulkRequest>> key : groupBulkRequestsKayValue) {
				List<List<UploadMANbulkRequest>> partitionedGroupRequestList = Lists.partition(key.getValue(), Integer.valueOf(requestsToEdfChunkSize));
				for (List<UploadMANbulkRequest> partitionedGroupRequest : partitionedGroupRequestList) {
					UploadMANbulkRequest uploadMANbulkRequest = partitionedGroupRequest.get(0);
					ValidateAccountDataRequest validateAccountDataRequest = new ValidateAccountDataRequest();
					ValidateAccountDataRequestDetails validateAccountDataRequestDetails = new ValidateAccountDataRequestDetails();
					validateAccountDataRequest.setBeginBillMonth(uploadMANbulkRequest.getBeginBillMonth());
					validateAccountDataRequest.setBillMonth(uploadMANbulkRequest.getBillMonth());
					validateAccountDataRequest.setProduct(uploadMANbulkRequest.getProduct());
					validateAccountDataRequestDetails.setBillMonth(uploadMANbulkRequest.getBillMonth());
					validateAccountDataRequestDetails.setBeginBillMonth(uploadMANbulkRequest.getBeginBillMonth());
					validateAccountDataRequestDetails.setProduct(uploadMANbulkRequest.getProduct());
					validateAccountDataRequestDetails.setUserId(userId);
					List<String> mainAccountNumberList = new ArrayList<>();
					List<String> mcnList = new ArrayList<>();
					String cpniApprover = null;
					String customerName = null;
					for (UploadMANbulkRequest eachEntryInFile : partitionedGroupRequest) {
						if (StringUtils.isNotBlank(eachEntryInFile.getType()) && "MAN".equalsIgnoreCase(eachEntryInFile.getType())) {
							mainAccountNumberList.add(eachEntryInFile.getManAccountNumber());
						} else if (StringUtils.isNotBlank(eachEntryInFile.getType()) && "MCN".equalsIgnoreCase(eachEntryInFile.getType())) {
							mcnList.add(eachEntryInFile.getMcnNumber());
						}
						if(StringUtils.isEmpty(cpniApprover) && StringUtils.isNotEmpty(eachEntryInFile.getCpniApprover())) {
							cpniApprover = eachEntryInFile.getCpniApprover();
						}
						if(StringUtils.isEmpty(customerName) && StringUtils.isNotEmpty(eachEntryInFile.getCustomerName())) {
							customerName = eachEntryInFile.getCustomerName();
						}
					}
					validateAccountDataRequest.setAccountNumberSet(mainAccountNumberList);
					validateAccountDataRequest.setMcnSet(mcnList);
					validateAccountDataRequestDetails.setPartitionedGroupRequest(partitionedGroupRequest);
					validateAccountDataRequestDetails.setValidateAccountDataRequest(validateAccountDataRequest);
					validateAccountDataRequestDetails.setCpniApprover(cpniApprover);
					validateAccountDataRequestDetails.setCustomerName(customerName);
					validateAccountDataRequestList.add(validateAccountDataRequestDetails);
				}
			 }
			 logger.info("validateAccountDataRequestList --------------- {} ",validateAccountDataRequestList);
			 if(CollectionUtils.isNotEmpty(validateAccountDataRequestList)) {
				 callEdfValidateAPIandPersist(validateAccountDataRequestList, nxSolutionDetail);
			 }
		}
	}
	
	/**
	 * 
	 * @param validateAccountDataRequestList
	 * @throws SalesBusinessException 
	 */
	private void callEdfValidateAPIandPersist(List<ValidateAccountDataRequestDetails> validateAccountDataRequestList,
			NxSolutionDetail nxSolutionDetail) throws SalesBusinessException {
		logger.debug(":: validateAccountDataRequestList :::::::: {}", validateAccountDataRequestList);
		long nxSolutionId = nxSolutionDetail.getNxSolutionId();

		try {
			
			Map<String, String> productTypeMap = new HashMap<String, String>();
			Map<String, String> groupNameMap = new HashMap<String, String>();
			for(ValidateAccountDataRequestDetails validateAccountDataItem : validateAccountDataRequestList) {
				logger.debug("::validateAccountDataItem********************** {} ",validateAccountDataItem);
				if(productTypeMap.containsKey(validateAccountDataItem.getProduct())) {
					validateAccountDataItem.setProductType(productTypeMap.get(validateAccountDataItem.getProduct()));
				} else {
					NxLookupData nxLookupData = nxLookupDataRepository.findByDescriptionAndDatasetName(validateAccountDataItem.getProduct(), MyPriceConstants.NX_REQ_GROUP_NAMES);
					validateAccountDataItem.setProductType(nxLookupData.getDatasetName());
					productTypeMap.put(validateAccountDataItem.getProduct(), nxLookupData.getDatasetName());
					groupNameMap.put(validateAccountDataItem.getProduct(), nxLookupData.getCriteria());
				}	
			}
			logger.info(":: validateAccountDataRequestList after setting productType :::::::: {}", validateAccountDataRequestList);
			
			boolean isServiceExist = validateAccountDataRequestList.stream().filter(prod -> prod.getProductType().equalsIgnoreCase("SERVICE_GROUP") || prod.getProductType().equalsIgnoreCase("SERVICE_ACCESS_GROUP")).findAny().isPresent();
			List<NxRequestDetails> originalNxRequests = new ArrayList<NxRequestDetails>();	
			
			/*
			 * NxSolutionDetail nxSolutionDetail = null; List<NxSolutionDetail> solution =
			 * nxSolutionDetailsRepo.findByNxSolutionId(nxSolutionId);
			 * if(CollectionUtils.isNotEmpty(solution)) { nxSolutionDetail =
			 * solution.get(0); }
			 */
			for (ValidateAccountDataRequestDetails validateAccountDataItem : validateAccountDataRequestList) {
				Integer edfResponseId = null;
				// call to EDF
				ValidateAccountDataResponse edfResponse = httpRestClient.getValidateAccontDataUri(validateAccountDataItem.getValidateAccountDataRequest());
				if(!ObjectUtils.isEmpty(edfResponse)) {
					edfResponseId = edfResponse.getCorrelationId();
				}
				logger.info("edfResponseId...........{}",edfResponseId);

				// persist into the nx_request_details table
				NxRequestDetails nxRequestDetails = new NxRequestDetails();
				nxRequestDetails.setCreatedDate(new Date());
				nxRequestDetails.setNxSolutionDetail(nxSolutionDetail);
				nxRequestDetails.setStatus(new Long(10));
				nxRequestDetails.setBulkReqYn("Y");
				nxRequestDetails.setActiveYn("Y");
				nxRequestDetails.setFlowType("INR");
				nxRequestDetails.setProduct(validateAccountDataItem.getProduct());
				nxRequestDetails.setNxReqDesc(validateAccountDataItem.getCustomerName() + "_" 
				+ groupNameMap.get(validateAccountDataItem.getProduct()));
				nxRequestDetails.setNxRequestGroupName(validateAccountDataItem.getProductType());
				nxRequestDetails.setEdfAckId(String.valueOf(edfResponseId));
				nxRequestDetails.setUser(validateAccountDataItem.getUserId());
				nxRequestDetails.setCpniApprover(validateAccountDataItem.getCpniApprover());
				
				String requestPayLoad = null;
				try {
					requestPayLoad = nexxusObjectMapper.writeValueAsString(validateAccountDataItem.getValidateAccountDataRequest());
					nxRequestDetails.setValidateAccountDataRequestJson(requestPayLoad);
				} catch (JsonProcessingException e) {
					logger.error("JsonProcessingException : While processing ValidateAccountDataRequest {}", e.getMessage());
				}
				String eachRequestEntries = null;
				try {
					eachRequestEntries = nexxusObjectMapper.writeValueAsString(validateAccountDataItem.getPartitionedGroupRequest());
					nxRequestDetails.setManageBillingPriceJson(eachRequestEntries);
				} catch (JsonProcessingException e) {
					logger.error("JsonProcessingException : While processing PartitionedGroupRequest {}", e.getMessage());
				}
				//nxRequestDetailsRepository.save(details);
				originalNxRequests.add(nxRequestDetails);
				logger.debug("nxRequestDetails----- {} ", nxRequestDetails);
			}
			logger.info("originalNxRequests .......... {}",originalNxRequests);

			List<NxRequestDetails> nxRequests = new ArrayList<NxRequestDetails>();
			List<NxRequestDetails> accessProducts = new ArrayList<NxRequestDetails>();
			Set<Long> nxRequestGrpIds = new HashSet<Long>();
			for (NxRequestDetails nxRequestDetails2 : originalNxRequests) {
				boolean serivceAccessGrp = true;
				nxRequestDetails2.setNxSolutionDetail(nxSolutionDetail);
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
			nxRequestDetailsRepository.saveAll(nxRequests);
			if(nxRequestGrpIds.isEmpty()) {
				for(NxRequestDetails req : accessProducts) {
					NxLookupData nxLookupData = nxLookupDataRepository.findByDatasetNameAndDescription("ACCESS_GROUP", req.getProduct());
					saveNxRequestGroup(req, nxLookupData, nxSolutionId, MyPriceConstants.NOT_APPLICABLE);
				}
				nxRequestDetailsRepository.saveAll(accessProducts);
			} else {
				for(Long nxRequestGrpId : nxRequestGrpIds) { 
					NxRequestGroup nxRequestGroup = nxRequestGroupRepository.findByNxRequestGroupIdAndActiveYn(nxRequestGrpId, StringConstants.CONSTANT_Y);
					List<NxLookupData> nxLookupData = nxLookupDataRepository.findByItemIdAndDatasetName(String.valueOf(nxRequestGroup.getGroupId()), MyPriceConstants.NX_REQ_GROUP_NAMES);
					for(NxRequestDetails nxRequestDetail : accessProducts) {
						NxRequestDetails newNxRequestDetails = new NxRequestDetails();
						BeanUtils.copyProperties(nxRequestDetail, newNxRequestDetails, "nxReqId");
						newNxRequestDetails.setNxRequestGroupId(nxRequestGrpId);
						//newNxRequestDetails.setNxRequestGroupName(nxLookupData.get(0).getDatasetName());
	
						nxRequestDetailsRepository.save(newNxRequestDetails);
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
				}//for 
			}
		} catch (Exception exp) {
			logger.error("Exception from NexxusServiceImpl.getBillingPriceInventryData", exp);
			throw new SalesBusinessException(MessageConstants.PROCESS_ERROR_CODE);
		}
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
	 * The Class InvGroupByKey.
	 */
	class BulkInvGroupByKey {

		/** The begin bill month. */
		private String beginBillMonth;

		/** The bill month. */
		private String billMonth;

		/** The product. */
		private String product;

		/** Usage Indicator */
		private String usageIndicator;
		
		/**
		 * Instantiates a new inv group by key.
		 *
		 * @param product        the product
		 * @param beginBillMonth the begin bill month
		 * @param billMonth      the bill month
		 */
		public BulkInvGroupByKey(String product, String beginBillMonth, String billMonth, String usageIndicator) {
			super();
			this.beginBillMonth = beginBillMonth;
			this.billMonth = billMonth;
			this.product = product;
			this.usageIndicator = usageIndicator;
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

		/**
		 * @return the usageIndicator
		 */
		public String getUsageIndicator() {
			return usageIndicator;
		}

		/**
		 * @param usageIndicator the usageIndicator to set
		 */
		public void setUsageIndicator(String usageIndicator) {
			this.usageIndicator = usageIndicator;
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
			BulkInvGroupByKey other = (BulkInvGroupByKey) obj;
			if (!getOuterType().equals(other.getOuterType()))
				return false;
			if(this.usageIndicator != null && other.usageIndicator != null && 
					"Y".equalsIgnoreCase(this.usageIndicator) && "Y".equalsIgnoreCase(other.usageIndicator)) {
				if (beginBillMonth == null) {
					if (other.beginBillMonth != null)
						return false;
				} else if (!beginBillMonth.equals(other.beginBillMonth))
					return false;
			}
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
		private BulkUploadEdfService getOuterType() {
			return BulkUploadEdfService.this;
		}

		@Override
		public String toString() {
			return "BulkInvGroupByKey [beginBillMonth=" + beginBillMonth + ", billMonth=" + billMonth + ", product="
					+ product + ", usageIndicator=" + usageIndicator + "]";
		}
	}
	
	public EdfDownloadRequestDetailsResponse generatePreviewEdfBulkuploadSheet(Long requestId, long nxSolutionId) throws IOException {
		logger.info("Inside generateEdfBulkuploadInrSheet method: {}", requestId);
		// Create a Workbook
		Workbook workbook = new XSSFWorkbook(); // new HSSFWorkbook() for generating `.xls` file
		EdfDownloadRequestDetailsResponse edfDownloadfileResponse = new EdfDownloadRequestDetailsResponse();
		try {

			// Create a Sheet
			Sheet sheet = workbook.createSheet("Bulk Import Template");

			// Create a Font for styling header cells
			Font headerFont = workbook.createFont();
			headerFont.setBold(true);
			headerFont.setFontHeightInPoints((short) 12);
			headerFont.setColor(IndexedColors.BLACK.getIndex());

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
		NxRequestDetails nxRequestDetails = nxRequestDetailsRepository.findByNxReqId(requestId);
			JsonNode manageBillingPriceJsonNode = mapper.createObjectNode();
			if (nxRequestDetails.getManageBillingPriceJson() != null) {
					manageBillingPriceJsonNode = mapper.readTree(nxRequestDetails.getManageBillingPriceJson());
					if(null != manageBillingPriceJsonNode) {
						int rowNum = 1;
						for(JsonNode manageBillingData : manageBillingPriceJsonNode) {
							Row row = sheet.createRow(rowNum++);
							String type = manageBillingData.get("type").asText();
							if(Optional.ofNullable(type).isPresent() && type.equalsIgnoreCase("MAN")) {
							row.createCell(0).setCellValue(manageBillingData.get("manAccountNumber").asText().trim());
							}else if(Optional.ofNullable(type).isPresent() && type.equalsIgnoreCase("MCN")) {
							row.createCell(0).setCellValue(manageBillingData.get("mcnNumber").asText().trim());
							}
							row.createCell(1).setCellValue(manageBillingData.get("type").asText().trim());
							row.createCell(2).setCellValue(manageBillingData.get("product").asText().trim());
							row.createCell(3).setCellValue(manageBillingData.get("usageOrNonUsageIndicator").asText().trim());
							row.createCell(4).setCellValue(manageBillingData.get("beginBillMonth").asText().trim());
							row.createCell(5).setCellValue(manageBillingData.get("billMonth").asText().trim());
							row.createCell(6).setCellValue(manageBillingData.get("cpniApprover").asText().trim());
							row.createCell(7).setCellValue(manageBillingData.get("customerName").asText().trim());
							logger.info("Added manageBillingPriceJsonNode to sheet: {}", manageBillingData);
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
					String fileName = nxSolutionId+"_"+"Preview_Edf_Bulk_Request_Details" + format.format(LocalDateTime.now())+".xlsx";
					Path path = nxTemplateProcessingService.getFilePath(downloadEdfBulkuploadRequest+fileName);
					logger.info("URI:==>>"+path.toUri());
					fileOut = new FileOutputStream(path.toString());
					try {
						workbook.write(fileOut);
					} catch (IOException e) {
						e.printStackTrace();
					}
					
						byte[] fileBytes = Files.readAllBytes(path);
						Blob blob = null;
						try {
						blob = new SerialBlob(fileBytes);
						} catch (SerialException e) {
							e.printStackTrace();
						} catch (SQLException e) {
							e.printStackTrace();
						}
						edfDownloadfileResponse.setFile(blob);
						edfDownloadfileResponse.setFileName(fileName);
						setSuccessResponse(edfDownloadfileResponse);
					} catch (IOException | SalesBusinessException e) {
						e.printStackTrace();
					}
					try {
						fileOut.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
			}
				} finally {
					try {
						workbook.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
		return edfDownloadfileResponse;
	}
	
 public FailedDmaapMessageResponse generateFailedDmapMsgReport(NexxusOutputRequest request) throws IOException {
		
		logger.info("inside generateFailedDmaapMsgReport method");
		Workbook workbook = new XSSFWorkbook();
		
		FailedDmaapMessageResponse  failedDmaapMessageResponse = new FailedDmaapMessageResponse();
		try {
		
			Sheet sheet = workbook.createSheet("Bulk Failed Report");

		
		Font headerFont = workbook.createFont();
		headerFont.setBold(true);
		headerFont.setFontHeightInPoints((short) 12);
		headerFont.setColor(IndexedColors.BLACK.getIndex());

		
		CellStyle headerCellStyle = workbook.createCellStyle();
		headerCellStyle.setFont(headerFont);

		
		Row headerRow = sheet.createRow(0);

		
		for (int i = 0; i < maxcolumns.length; i++) {
			Cell cell = headerRow.createCell(i);
			cell.setCellValue(maxcolumns[i]);
			cell.setCellStyle(headerCellStyle);
		}
		Long nxReqId = request.getRequestId();
		NxRequestDetails nxRequestDetails = nxRequestDetailsRepository.findByNxReqId(nxReqId);
		String product = nxRequestDetails.getProduct();
		List<NxOutputFileModel> outPutFileData = nxOutputFileRepository.findByNxReqId(nxReqId);
		JsonNode dmaapFailureJsonNode = mapper.createObjectNode();
		for(NxOutputFileModel nxOutPutFileModel : outPutFileData) {		
			int rowNum = 1;
			try {
				if(nxOutPutFileModel.getDmaapFailureJson() != null) {
					logger.info("Failure Dmaap Data:" +" " + nxOutPutFileModel.getDmaapFailureJson());
					dmaapFailureJsonNode = mapper.readTree(nxOutPutFileModel.getDmaapFailureJson());
					Object object = mapper.readValue(dmaapFailureJsonNode.toString(), Object.class);
					String jsonData = mapper.writeValueAsString(object);
					JSONObject jsonObject = new JSONObject(jsonData);
					JSONArray failedAccoutArray = jsonObject.getJSONArray("failedAccountSet");
					if(null  != failedAccoutArray && failedAccoutArray.length() >= 1) {
						for(int i = 0 ; i < failedAccoutArray.length(); i++) {
							Row row = sheet.createRow(rowNum++);
							org.json.JSONObject failedJsonObject = (org.json.JSONObject) failedAccoutArray.get(i);
							if(failedJsonObject.has("accountNumber")) {
							String mainAccountNumber = failedJsonObject.getString("accountNumber");
							row.createCell(0).setCellValue(mainAccountNumber);	
							row.createCell(1).setCellValue("MAN");	
							}
							else if(failedJsonObject.has("mcn")) {
								String mainCustomerNumber = failedJsonObject.getString("mcn");
								row.createCell(0).setCellValue(mainCustomerNumber);	
								row.createCell(1).setCellValue("MCN");	
							}
						
							if(Optional.ofNullable(product).isPresent()) {
								row.createCell(2).setCellValue(product);
							}
							List<String> billMonthList = new ArrayList<>();
							org.json.JSONArray billMonthArray = failedJsonObject.getJSONArray("billMonth");
							if(null != billMonthArray && billMonthArray.length() >= 1) {
								for(int index =0 ; index < billMonthArray.length(); index++) {
									String billMonths = billMonthArray.getString(index);
									billMonthList.add(billMonths);	
									
								}
								String billMonth = billMonthList.toString().replace("[", "").replace("]", "");
								logger.info("Billmoth Details:"+" " +billMonth);
								row.createCell(3).setCellValue(billMonth);
							}
							if(jsonObject.has("Request_id")) {
								String correlationId = jsonObject.get("Request_id").toString();
								row.createCell(4).setCellValue(correlationId);
							}
							if(failedJsonObject.getString("reasonCode") != null) {
								String reasonCode = failedJsonObject.getString("reasonCode") ;
								List<NxLookupData> nxLookUpdataDetails = nxLookupDataRepository.findByDatasetNameAndItemId(MyPriceConstants.FAILED_DMAAP_DESCRIPTION, reasonCode);
								for(NxLookupData nxLookupData :nxLookUpdataDetails ) {
									String reasonDescription = nxLookupData.getDescription();
									row.createCell(5).setCellValue(reasonCode);
									if(Optional.ofNullable(reasonDescription).isPresent()) {
										row.createCell(6).setCellValue(reasonDescription);
									}
								}
							}
						}
					}
				}
			}catch(org.json.JSONException e) {
				e.printStackTrace();
			}
			}
			for (int i = 0; i < maxcolumns.length; i++) {
				sheet.autoSizeColumn(i);
			}
			FileOutputStream fileOut = null;
			try {
			DateTimeFormatter format = DateTimeFormatter.ofPattern("ddMMyyyyHHmmss");
			String fileName = "Failed_Dmaap_MSG_Report" + format.format(LocalDateTime.now())+".xlsx";
			Path path = nxTemplateProcessingService.getFilePath(failedDmaapMsgRequest+fileName);
			logger.info("URI:==>>"+path.toUri());
			fileOut = new FileOutputStream(path.toString());
			try {
				workbook.write(fileOut);
			} catch (IOException e) {
				e.printStackTrace();
			}
			
				byte[] fileBytes = Files.readAllBytes(path);
				Blob blob = null;
				try {
				blob = new SerialBlob(fileBytes);
				} catch (SerialException e) {
					e.printStackTrace();
				} catch (SQLException e) {
					e.printStackTrace();
				}
				failedDmaapMessageResponse.setFile(blob);
				failedDmaapMessageResponse.setFileName(fileName);
			} catch (IOException | SalesBusinessException e) {
				e.printStackTrace();
			}
			try {
				fileOut.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		
		} finally {
			try {
				workbook.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return failedDmaapMessageResponse;
	}

}
