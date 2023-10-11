package com.att.sales.nexxus.service;

import java.io.File;
import java.io.IOException;
import java.sql.Blob;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.TimeZone;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.sql.rowset.serial.SerialBlob;
import javax.sql.rowset.serial.SerialException;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import com.att.sales.framework.exception.SalesBusinessException;
import com.att.sales.framework.model.ServiceResponse;
import com.att.sales.framework.service.BaseServiceImpl;
import com.att.sales.framework.util.swagger.APIConstants;
import com.att.sales.nexxus.admin.model.EdfDownloadRequestDetailsResponse;
import com.att.sales.nexxus.admin.model.FailedDmaapMessageResponse;
import com.att.sales.nexxus.admin.model.FailedEthTokesResponse;
import com.att.sales.nexxus.common.MessageConstants;
import com.att.sales.nexxus.constant.CommonConstants;
import com.att.sales.nexxus.dao.model.LittleProductDataEntity;
import com.att.sales.nexxus.dao.model.NxAccessPricingData;
import com.att.sales.nexxus.dao.model.NxOutputFileAuditModel;
import com.att.sales.nexxus.dao.model.NxOutputFileModel;
import com.att.sales.nexxus.dao.model.NxRequestDetails;
import com.att.sales.nexxus.dao.model.solution.NxSolutionDetail;
import com.att.sales.nexxus.dao.repository.HybridRepositoryService;
import com.att.sales.nexxus.dao.repository.LittleProductRepo;
import com.att.sales.nexxus.dao.repository.NxAccessPricingDataRepository;
import com.att.sales.nexxus.dao.repository.NxOutputFileRepository;
import com.att.sales.nexxus.model.IglooTokenExportResponse;
import com.att.sales.nexxus.model.NexxusOutputRequest;
import com.att.sales.nexxus.model.NxOutputFileIdResponse;
import com.att.sales.nexxus.model.ZipFileResponse;
import com.att.sales.nexxus.output.entity.NxOutputBean;
import com.att.sales.util.CSVFileWriter;
import com.att.sales.nexxus.util.AuditUtil;
import com.att.sales.nexxus.constant.AuditTrailConstants;

/**
 * The Class NexxusDownloadServiceImpl.
 *
 * @author RudreshWaladaunki
 */
@Service
public class NexxusDownloadServiceImpl extends BaseServiceImpl implements APIConstants {

	/** The log. */
	private static Logger log = LoggerFactory.getLogger(NexxusDownloadServiceImpl.class);

	/** The env. */
	@Autowired
	private Environment env;

	/** The hybrid repo. */
	@Autowired
	private HybridRepositoryService hybridRepo;

	/** The nexus output file repository. */
	@Autowired
	private NxOutputFileRepository nexusOutputFileRepository;

	/** The report service. */
	@Autowired
	private ReportService reportService;

	@Autowired
	private BulkUploadEdfService edfbulkUploadService;
	/** The pricing data repository. */
	@Autowired
	private NxAccessPricingDataRepository pricingDataRepository;

	/** The little product repo. */
	@Autowired
	private LittleProductRepo littleProductRepo;

	@Autowired
	private BulkUploadEthTokenService bulkUploadEthTokenService;
	
	@Autowired
	private InrEditCreateExcelService inrEditCreateExcelService;

	@Autowired
	private AuditUtil auditUtil;

	/** The nx inr edit template path. */
	@Value("${nx.inr.edit.template.path}")
	private String inrEditTemplatePath;
	
	/**
	 * Gets the nexxus output.
	 *
	 * @param request the request
	 * @return the nexxus output
	 * @throws SalesBusinessException the sales business exception
	 */
	public ServiceResponse getNexxusOutput(NexxusOutputRequest request) throws SalesBusinessException {
		String statusMessage = null;
		ZipFileResponse zipFileResponse = new ZipFileResponse();
		if (null != request && null != request.getRequestIds() && !request.getRequestIds().contains(null)) {

			String cdtfile = env.getProperty("pv.directory.cdtfile");
			List<Long> inputReqIds = request.getRequestIds();
			Blob blob = null;
			String fileName = null;
			statusMessage = validateNxRequestDetailsStatus(inputReqIds);
			Map<String, Blob> resultMap = getOutputBlogObjByReqIds(inputReqIds);

			for (Map.Entry<String, Blob> entry : resultMap.entrySet()) {
				fileName = entry.getKey();
				blob = entry.getValue();
			}

			if (fileName != null && blob != null) {

				fileName = fileName.replaceAll(":", "-");
				String filePath = env.getProperty("pv.directory.nexxusoutput");
				File dir = new File(filePath); // NOSONAR
				// create output directory if it doesn't exist
				if (!dir.exists())
					dir.mkdirs();
				CSVFileWriter.convertBlobToFile(blob, filePath + fileName);

				/*
				 * ArrayList<String> inputFiles = new ArrayList<>(); inputFiles.add(filePath +
				 * fileName); inputFiles.add(cdtfile);
				 * 
				 * String zipFileName = FilenameUtils.removeExtension(fileName) + ".zip"; String
				 * zipFileNamePath = filePath + zipFileName;
				 */

				/*
				 * Path path = Paths.get(filePath); Path pathWhole = path.resolve(zipFileName);
				 * String zipFileNamePath = pathWhole.toString();
				 */

				/* doZip(zipFileNamePath, inputFiles); */

				/* File zipFile = new File(zipFileNamePath); */
				String zipFileString;
				try {
					zipFileString = Base64.getEncoder().encodeToString(blob.getBytes(1, (int) blob.length()));
				} catch (Exception e) {
					log.error("Error occured while encoding file byte[]", e);
					throw new SalesBusinessException(e.getMessage());
				}

				zipFileResponse.setZipFileName(fileName);
				zipFileResponse.setZipFile(zipFileString);
				setSuccessResponse(zipFileResponse, statusMessage);

			} else {
				// Put valid message for Invalid input
				throw new SalesBusinessException(MessageConstants.FILE_DOWNLOAD_FAILED);
			}
		} else {
			// Put valid message for Invalid input
			throw new SalesBusinessException(MessageConstants.INVALID_REQUEST);
		}

		return zipFileResponse;
	}

	/**
	 * Validate nx request details status.
	 *
	 * @param inputReqIds the input req ids
	 * @return the string
	 * @throws SalesBusinessException the sales business exception
	 */
	public String validateNxRequestDetailsStatus(List<Long> inputReqIds) throws SalesBusinessException {
		NxRequestDetails nxRequestDetails = hybridRepo.getByRequestId(inputReqIds.get(0));
		if (nxRequestDetails.getStatus() == 20) {
			return MessageConstants.SUCCESS_WITH_FALLOUT;
		} else if (nxRequestDetails.getStatus() == 30) {
			return MessageConstants.SUCCESS;
		} else if (nxRequestDetails.getStatus() == 10) {
			throw new SalesBusinessException(MessageConstants.INPROGRESS);
		} else if (nxRequestDetails.getStatus() == 40) {
			throw new SalesBusinessException(MessageConstants.FAILED);
		} else if (nxRequestDetails.getStatus() == 70) {
			throw new SalesBusinessException(MessageConstants.FALLOUT);
		} else {
			throw new SalesBusinessException();
		}
	}

	/**
	 * Creates the zipfile out of inputFiles.
	 *
	 * @param filePath   the file path
	 * @param inputFiles the input files
	 * @throws SalesBusinessException the sales business exception
	 */

	public void doZip(String filePath, List<String> inputFiles) throws SalesBusinessException {

		try {
			// commented below code as it is not used and causing vulnerability 17/2/21
			// CSVFileWriter.generateZipfile(filePath, inputFiles.toArray(new String[0]));
		} catch (Exception e) {
			log.error("Exception occured while creating zip file", e);
			throw new SalesBusinessException(e.getMessage());
		}

	}

	/**
	 * Gets the output blog obj by req ids.
	 *
	 * @param inputReqIds the input req ids
	 * @return the output blog obj by req ids
	 * @throws SalesBusinessException the sales business exception
	 * 
	 *                                This method is used to get output file using
	 *                                request ids
	 */
	public Map<String, Blob> getOutputBlogObjByReqIds(List<Long> inputReqIds) throws SalesBusinessException {
		log.info("Inside getOutputBlogObjForMultipleReqIds method for request ids: {}", inputReqIds);
		Map<String, Blob> resultMap = new HashMap<>();
		inputReqIds.removeIf(Objects::isNull);
		Set<Long> requestIds = new HashSet<>(inputReqIds);
		List<NxOutputFileModel> nxOutputLst = nexusOutputFileRepository.findByMultipleRequestIds(requestIds);
		if (inputReqIds.size() == 1 && nxOutputLst.get(0) != null) {
			NxOutputFileModel outputFileObj = nxOutputLst.get(0);
			if (outputFileObj.getStatus().equalsIgnoreCase(SUCCEED)
					|| outputFileObj.getStatus().equalsIgnoreCase(CommonConstants.SUCCESS_WITH_FALLOUT)) {
				// resultMap.put(outputFileObj.getFileName(), outputFileObj.getOutputFile());
				try {
					resultMap.put(outputFileObj.getFileName(), new SerialBlob(outputFileObj.getOutputFile()));
				} catch (SerialException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} else {
				throw new SalesBusinessException(MessageConstants.STATUS_NOT_SUCCESS);
			}
		} else {
			NxOutputBean combinedOutput = new NxOutputBean();
			Optional.ofNullable(nxOutputLst).map(List::stream).orElse(Stream.empty()).filter(Objects::nonNull)
					.filter(x -> x.getOutput() != null).forEach(data -> {
						NxOutputBean outputFromDb = data.getOutput();
						if (CollectionUtils.isNotEmpty(outputFromDb.getNxAvpnIntlOutputBean())) {
							combinedOutput.getNxAvpnIntlOutputBean().addAll(outputFromDb.getNxAvpnIntlOutputBean());
						}
						if (CollectionUtils.isNotEmpty(outputFromDb.getNxAvpnOutput())) {
							combinedOutput.getNxAvpnOutput().addAll(outputFromDb.getNxAvpnOutput());
						}
						if (CollectionUtils.isNotEmpty(outputFromDb.getNxEthernetAccOutputBean())) {
							combinedOutput.getNxEthernetAccOutputBean()
									.addAll(outputFromDb.getNxEthernetAccOutputBean());
						}
					});
			String reqIdsString = inputReqIds.stream().map(Object::toString).collect(Collectors.joining(","));
			Blob combinedOutputFile = reportService.generateReport(combinedOutput);
			resultMap.put(this.createFileName(reqIdsString), combinedOutputFile);
		}

		return resultMap;

	}

	/**
	 * Gets the output files blog object.
	 *
	 * @param request the request
	 * @return the output files blog object
	 * @throws SalesBusinessException the sales business exception
	 */
	public Map<String, Blob> getOutputFilesByReqIdAndSolId(NexxusOutputRequest request) throws SalesBusinessException {
		log.info("Inside getOutputFilesBlogObject method : {}", "");
		Map<String, Blob> resultMap = new HashMap<>();
		List<NxOutputBean> allOutputdataLst = new ArrayList<>();
		String solIdsString = "ASAP Solution ";
		if (CollectionUtils.isNotEmpty(request.getRequestIds())) {
			List<NxOutputFileModel> nxOutputLst = nexusOutputFileRepository
					.findByMultipleRequestIds(new HashSet<>(request.getRequestIds()));
			allOutputdataLst.addAll(Optional.ofNullable(nxOutputLst).map(List::stream).orElse(Stream.empty())
					.filter(Objects::nonNull).filter(data -> data.getOutput() != null).map(NxOutputFileModel::getOutput)
					.collect(Collectors.toList()));
		}

		if (null != request.getNxSolutionId()) {
			List<NxAccessPricingData> nxAccessPricingData = pricingDataRepository
					.findByNxSolIdAndIncludeInd(request.getNxSolutionId());
			allOutputdataLst.addAll(Optional.ofNullable(nxAccessPricingData).map(List::stream).orElse(Stream.empty())
					.filter(Objects::nonNull).filter(data -> data.getOutputJson() != null)
					.map(NxAccessPricingData::getOutputJson).collect(Collectors.toList()));
			solIdsString = solIdsString + request.getNxSolutionId();
		}

		NxOutputBean combinedOutput = new NxOutputBean();
		Optional.ofNullable(allOutputdataLst).map(List::stream).orElse(Stream.empty()).filter(Objects::nonNull)
				.forEach(data -> {
					if (CollectionUtils.isNotEmpty(data.getNxAvpnIntlOutputBean())) {
						combinedOutput.getNxAvpnIntlOutputBean().addAll(data.getNxAvpnIntlOutputBean());
					}
					if (CollectionUtils.isNotEmpty(data.getNxAvpnOutput())) {
						combinedOutput.getNxAvpnOutput().addAll(data.getNxAvpnOutput());
					}
					if (CollectionUtils.isNotEmpty(data.getNxEthernetAccOutputBean())) {
						combinedOutput.getNxEthernetAccOutputBean().addAll(data.getNxEthernetAccOutputBean());
					}
					if (CollectionUtils.isNotEmpty(data.getNxDs3AccessBean())) {
						combinedOutput.getNxDs3AccessBean().addAll(data.getNxDs3AccessBean());
					}
					if (CollectionUtils.isNotEmpty(data.getNxMisDS1AccessBean())) {
						combinedOutput.getNxMisDS1AccessBean().addAll(data.getNxMisDS1AccessBean());
					}
					if (CollectionUtils.isNotEmpty(data.getNxAvpnDS0DS1AccessBean())) {
						combinedOutput.getNxAvpnDS0DS1AccessBean().addAll(data.getNxAvpnDS0DS1AccessBean());
					}
					if (CollectionUtils.isNotEmpty(data.getNxAvpnDS1FlatRateAccessBean())) {
						combinedOutput.getNxAvpnDS1FlatRateAccessBean().addAll(data.getNxAvpnDS1FlatRateAccessBean());
					}
					if (CollectionUtils.isNotEmpty(data.getNxAdiMisBean())) {
						combinedOutput.getNxAdiMisBean().addAll(data.getNxAdiMisBean());
					}
					if (CollectionUtils.isNotEmpty(data.getNxBvoipOutputBean())) {
						combinedOutput.getNxBvoipOutputBean().addAll(data.getNxBvoipOutputBean());
					}
				});

		Blob combinedOutputFile = reportService.generateReport(combinedOutput);
		resultMap.put(this.getFileName(solIdsString), combinedOutputFile);
		return resultMap;

	}

	/**
	 * Creates the file name.
	 *
	 * @param id the id
	 * @return the string
	 * 
	 */
	protected String createFileName(String id) {
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss'.xlsx'");
		dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
		return id + CommonConstants.OUTPUT_FILE_NAME + dateFormat.format(new Date());
	}

	/**
	 * Gets the file name.
	 *
	 * @param id the id
	 * @return the file name
	 */
	protected String getFileName(String id) {
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
		return id + "_NexxusLanding Pad (" + dateFormat.format(new Date()) + ").xlsx";
	}

	/**
	 * Creates ZipFileResponse.
	 *
	 * @param request the request
	 * @return the ZipFileResponse
	 * @throws SalesBusinessException the sales business exception
	 */
	public ServiceResponse getNexxusOutputZipFile(NexxusOutputRequest request) throws SalesBusinessException {
		Long currentTime = System.currentTimeMillis();
		Long startTime = System.currentTimeMillis() - currentTime;
		log.info("Inside getNexxusOutputZipFile START");
		ZipFileResponse zipFileResponse = new ZipFileResponse();

		if (Optional.ofNullable(request.getNxSolutionId()).isPresent()
				&& Optional.ofNullable(request.getNxOutputAction()).isPresent()
				&& request.getNxOutputAction().equalsIgnoreCase("failedTokens")) {
			FailedEthTokesResponse failedEthTokesResponse = (FailedEthTokesResponse) bulkUploadEthTokenService.downloadFailedTokenFile(request);
			if (failedEthTokesResponse.getFileName() != null && failedEthTokesResponse.getFile() != null) {
				generateOutpuFile(zipFileResponse, failedEthTokesResponse.getFile(),
						failedEthTokesResponse.getFileName());
				Long endTime = System.currentTimeMillis() - currentTime;
				Long executionTime=endTime-startTime;
				auditUtil.addActionToNxUiAudit(request.getNxSolutionId(),AuditTrailConstants.DOWNLOAD_FAILED_TOKEN,null,AuditTrailConstants.SUCCESS,null,null,executionTime,null);
				return zipFileResponse;
			} else {
				Long endTime = System.currentTimeMillis() - currentTime;
				Long executionTime=endTime-startTime;
				auditUtil.addActionToNxUiAudit(request.getNxSolutionId(),AuditTrailConstants.DOWNLOAD_FAILED_TOKEN,null,AuditTrailConstants.FAIL,null,null,executionTime,null);
				throw new SalesBusinessException(MessageConstants.FILE_DOWNLOAD_FAILED);
			}
		}
		if (Optional.ofNullable(request.getNxSolutionId()).isPresent()
				&& Optional.ofNullable(request.getNxOutputAction()).isPresent() && request.getNxOutputAction()
						.equalsIgnoreCase(com.att.sales.nexxus.constant.StringConstants.IGLOO_EXPORT_ACTION)) {
			IglooTokenExportResponse iglooTokenExportResponse = bulkUploadEthTokenService
					.downloadIglooTokenFile(request);
			if (iglooTokenExportResponse.getFileName() != null && iglooTokenExportResponse.getFile() != null) {
				generateOutpuFile(zipFileResponse, iglooTokenExportResponse.getFile(),
						iglooTokenExportResponse.getFileName());
				Long endTime = System.currentTimeMillis() - currentTime;
				Long executionTime=endTime-startTime;
				auditUtil.addActionToNxUiAudit(request.getNxSolutionId(),AuditTrailConstants.DOWNLOAD_IGLOO_EXPORT_ACTION,null,AuditTrailConstants.SUCCESS,null,null,executionTime,null);		
				return zipFileResponse;
			} else {
				Long endTime = System.currentTimeMillis() - currentTime;
				Long executionTime=endTime-startTime;
				auditUtil.addActionToNxUiAudit(request.getNxSolutionId(),AuditTrailConstants.DOWNLOAD_IGLOO_EXPORT_ACTION,null,AuditTrailConstants.FAIL,null,null,executionTime,null);		
				throw new SalesBusinessException(MessageConstants.FILE_DOWNLOAD_FAILED);
			}
		}
		if(Optional.ofNullable(request.getNxSolutionId()).isPresent() && Optional.ofNullable(request.getNxOutputAction()).isPresent() && request.getNxOutputAction().equalsIgnoreCase("inrEditDownload")) {
			//xy3208 inr edit download interface test, no actual logic
			log.info("inrEditDownload for nxSolutionId {}", request.getNxSolutionId());
			File file = new File("src/main/resources/nexxusTemplate/address_edit_template.xlsx");
			zipFileResponse.setZipFileName("address_edit_template.xlsx");
			zipFileResponse.setFileZip(file);
			setSuccessResponse(zipFileResponse);
			Long endTime = System.currentTimeMillis() - currentTime;
			Long executionTime=endTime-startTime;
			auditUtil.addActionToNxUiAudit(request.getNxSolutionId(),AuditTrailConstants.INR_ADDRESS_EDIT_DOWNLOAD,null,AuditTrailConstants.SUCCESS,null,null,executionTime,null);		
			return zipFileResponse;
		}
		if (null != request && null != request.getRequestIds() && !request.getRequestIds().contains(null)
				&& null != request.getNxOutputAction()) {
			List<Long> inputReqIds = request.getRequestIds();
			if (request.getNxOutputAction().equalsIgnoreCase("previewInr")) {
				try {
					log.info("Inside getNexxusOutputZipFile START" + request.getNxOutputAction());
					zipFileResponse.setZipFileName("previewInrSheet.xlsx");
					zipFileResponse.setFileZip(reportService.generatePreviewInrSheet(inputReqIds));
					setSuccessResponse(zipFileResponse);
					Long endTime = System.currentTimeMillis() - currentTime;
					Long executionTime=endTime-startTime;
					auditUtil.addActionToNxUiAudit(request.getNxSolutionId(),AuditTrailConstants.DOWNLOAD_ZIP_CUSTOMER_INVENTORY,null,AuditTrailConstants.SUCCESS,null,null,executionTime,null);		
					return zipFileResponse;

				} catch (IOException e) {
					Long endTime = System.currentTimeMillis() - currentTime;
					Long executionTime=endTime-startTime;
					auditUtil.addActionToNxUiAudit(request.getNxSolutionId(),AuditTrailConstants.DOWNLOAD_ZIP_CUSTOMER_INVENTORY,null,AuditTrailConstants.FAIL,null,null,executionTime,null);		
					log.error("Exception occured during previewInr process", e);
					throw new SalesBusinessException(MessageConstants.FILE_DOWNLOAD_FAILED);
				} catch (Exception e) {
					Long endTime = System.currentTimeMillis() - currentTime;
					Long executionTime=endTime-startTime;
					auditUtil.addActionToNxUiAudit(request.getNxSolutionId(),AuditTrailConstants.DOWNLOAD_ZIP_CUSTOMER_INVENTORY,null,AuditTrailConstants.FAIL,null,null,executionTime,null);		
					log.error("Main exception occured during previewInr process", e);
				}

			} else if (request.getNxOutputAction().equalsIgnoreCase("fmoNxOutput")
					&& null != request.getNxSolutionId()) {
				validateAndGetNxSolutionDetails(request);
				inputReqIds = request.getRequestIds();
				String cdtfile = env.getProperty("pv.directory.cdtfile");
				Blob blob = null;
				String fileName = null;

				Map<String, Blob> resultMap = getOutputBlogObjByReqIds(inputReqIds);

				for (Map.Entry<String, Blob> entry : resultMap.entrySet()) {
					fileName = entry.getKey();
					blob = entry.getValue();
				}

				if (fileName != null && blob != null) {
					// getZipFileResponse(zipFileResponse, cdtfile, blob, fileName);
					generateOutpuFile(zipFileResponse, blob, fileName);
					return zipFileResponse;

				} else {
					// Put valid message for Invalid input
					throw new SalesBusinessException(MessageConstants.FILE_DOWNLOAD_FAILED);
				}

			}

		}

		String cdtfile = env.getProperty("pv.directory.cdtfile");
		NxSolutionDetail nxSolutionDetail;
		if (request != null && request.getLittleId() != null
				&& request.getNxOutputAction().equalsIgnoreCase("dataUpLoadTemplate")) {
			// code for download template file for data upload
			File basePathfile = new File("src/main/resources/dataUploadTemplate/");
			LittleProductDataEntity littleProductData = littleProductRepo.findByLittleId(request.getLittleId());
			if (null != littleProductData && StringUtils.isNotEmpty(littleProductData.getTemplateName())) {
				File file = new File(basePathfile, littleProductData.getTemplateName()); // NOSONAR
				zipFileResponse.setFileZip(file);
				zipFileResponse.setZipFileName(littleProductData.getTemplateName());
			} else {
				Long endTime = System.currentTimeMillis() - currentTime;
				Long executionTime=endTime-startTime;
				auditUtil.addActionToNxUiAudit(request.getNxSolutionId(),AuditTrailConstants.DOWNLOAD_DATA_UPLOAD_TEMPLATE,null,AuditTrailConstants.FAIL,null,null,executionTime,null);		
				throw new SalesBusinessException(MessageConstants.INVALID_REQUEST);
			}

		} else if (request != null && request.getNxOutputFileId() != null && request.getNxSolutionId() != null) {

			nxSolutionDetail = validateAndGetNxSolutionDetails(request);
			Blob blob = null;
			String fileName = null;

			Long nxOutputFileId = request.getNxOutputFileId();
			List<NxOutputFileAuditModel> nxOutputFileAuditList = hybridRepo.getNxOutputFileAuditDetails(nxOutputFileId,
					nxSolutionDetail);
			if (!nxOutputFileAuditList.isEmpty()) {
				fileName = nxOutputFileAuditList.get(0).getFileName();
				blob = nxOutputFileAuditList.get(0).getOutputFile();

				if (fileName != null && blob != null) {

					// getZipFileResponse(zipFileResponse, cdtfile, blob, fileName);
					generateOutpuFile(zipFileResponse, blob, fileName);

				} else {
					Long endTime = System.currentTimeMillis() - currentTime;
					Long executionTime=endTime-startTime;
					auditUtil.addActionToNxUiAudit(request.getNxSolutionId(),AuditTrailConstants.DOWNLOAD_ZIP_FILE,null,AuditTrailConstants.FAIL,null,null,executionTime,null);
					// Put valid message for Invalid input
					throw new SalesBusinessException(MessageConstants.FILE_DOWNLOAD_FAILED);
				}
			} else {
				Long endTime = System.currentTimeMillis() - currentTime;
				Long executionTime=endTime-startTime;
				auditUtil.addActionToNxUiAudit(request.getNxSolutionId(),AuditTrailConstants.DOWNLOAD_ZIP_FILE,null,AuditTrailConstants.FAIL,null,null,executionTime,null);
				// Put valid message for Invalid Input
				throw new SalesBusinessException(MessageConstants.INVALID_REQUEST);
			}
		} else {
			Long endTime = System.currentTimeMillis() - currentTime;
			Long executionTime=endTime-startTime;
			auditUtil.addActionToNxUiAudit(request.getNxSolutionId(),AuditTrailConstants.DOWNLOAD_ZIP_FILE,null,AuditTrailConstants.FAIL,null,null,executionTime,null);
			// Put valid message for Invalid input
			throw new SalesBusinessException(MessageConstants.INVALID_REQUEST);
		}
		Long endTime = System.currentTimeMillis() - currentTime;
		Long executionTime=endTime-startTime;
		auditUtil.addActionToNxUiAudit(request.getNxSolutionId(),AuditTrailConstants.DOWNLOAD_ZIP_FILE,null,AuditTrailConstants.SUCCESS,null,null,executionTime,null);
		return zipFileResponse;
	}

	/**
	 * Creates ZipFileResponse.
	 *
	 * @param request the request
	 * @return the ZipFileResponse
	 * @throws SalesBusinessException the sales business exception
	 */
	public ServiceResponse getEncodedBinaryFile(NexxusOutputRequest request) throws SalesBusinessException {
		Long currentTime = System.currentTimeMillis();
		Long startTime = System.currentTimeMillis() - currentTime;
		
		log.info("Inside getNexxusEncodedBinaryFile START");
		ZipFileResponse zipFileResponse = new ZipFileResponse();
		if (null != request && null != request.getRequestIds() && !request.getRequestIds().contains(null)
				&& null != request.getNxOutputAction()) {
			List<Long> inputReqIds = request.getRequestIds();
			if (request.getNxOutputAction().equalsIgnoreCase("previewInr")) {
				try {
					zipFileResponse.setZipFileName("previewInrSheet.xlsx");
					zipFileResponse.setZipFile(getEncodedFile(reportService.generatePreviewInrSheet(inputReqIds)));
					setSuccessResponse(zipFileResponse);
					Long endTime = System.currentTimeMillis() - currentTime;
				    Long executionTime=endTime-startTime;
				    auditUtil.addActionToNxUiAudit(request.getNxSolutionId(),AuditTrailConstants.DOWNLOAD_CUSTOMER_INVENTORY,null,AuditTrailConstants.SUCCESS,null,null,executionTime,null);
					return zipFileResponse;
				} catch (IOException e) {
					Long endTime = System.currentTimeMillis() - currentTime;
				    Long executionTime=endTime-startTime;
					auditUtil.addActionToNxUiAudit(request.getNxSolutionId(),AuditTrailConstants.DOWNLOAD_CUSTOMER_INVENTORY,null,AuditTrailConstants.FAIL,null,null,executionTime,null);
					log.error("Exception occured during previewInr process", e);
					throw new SalesBusinessException(MessageConstants.FILE_DOWNLOAD_FAILED);
				} catch (Exception e) {
					Long endTime = System.currentTimeMillis() - currentTime;
				    Long executionTime=endTime-startTime;
					auditUtil.addActionToNxUiAudit(request.getNxSolutionId(),AuditTrailConstants.DOWNLOAD_CUSTOMER_INVENTORY,null,AuditTrailConstants.FAIL,null,null,executionTime,null);
					log.error("Main exception occured during previewInr process", e);
				}
			}
		} else if ("downloadFile".equalsIgnoreCase(request.getNxOutputAction()) && null != request.getFileName()) {

			if (request.getFileName().equalsIgnoreCase("AVPN")) {
				request.setFileName("INR_EDIT_Template_AVPN.xlsx");
				zipFileResponse=generateZipFileResponse(request);
			} else if (request.getFileName().equalsIgnoreCase("ADI(MIS)")) {
				request.setFileName("INR_EDIT_Template_ADI(MIS).xlsx");
				zipFileResponse=generateZipFileResponse(request);
			} else if (request.getFileName().equalsIgnoreCase("ADIG(GMIS)")) {
				request.setFileName("INR_EDIT_Template_ADIG(GMIS).xlsx");
				zipFileResponse=generateZipFileResponse(request);
			} else if (request.getFileName().equalsIgnoreCase("DOMESTIC DEDICATED ACCESS")) {
				request.setFileName("INR_EDIT_Template_DOMESTIC DEDICATED ACCESS.xlsx");
				zipFileResponse=generateZipFileResponse(request);
			} else if (request.getFileName().equalsIgnoreCase("DOMESTIC PL IOC")) {
				request.setFileName("INR_EDIT_Template_DOMESTIC PL IOC.xlsx");
				zipFileResponse=generateZipFileResponse(request);
			} else {
				zipFileResponse=generateZipFileResponse(request);
			}
			setSuccessResponse(zipFileResponse);
			Long endTime = System.currentTimeMillis() - currentTime;
		    Long executionTime=endTime-startTime;
			auditUtil.addActionToNxUiAudit(request.getNxSolutionId(),AuditTrailConstants.INR_EDIT_TEMPLATE_DOWNLOAD,null,AuditTrailConstants.SUCCESS,null,null,executionTime,null);
			return zipFileResponse;
		} else if (Optional.ofNullable(request.getNxSolutionId()).isPresent()
				&& Optional.ofNullable(request.getNxOutputAction()).isPresent()
				&& request.getNxOutputAction().equalsIgnoreCase("failedDmapMsg")) {
			try {
				FailedDmaapMessageResponse failedDmaapMessageResponse = edfbulkUploadService
						.generateFailedDmapMsgReport(request);
				if (failedDmaapMessageResponse.getFileName() != null && failedDmaapMessageResponse.getFile() != null) {
					zipFileResponse.setZipFileName(failedDmaapMessageResponse.getFileName());
					zipFileResponse.setZipFile(getEncodedFile(genrateEdfOutputFile(failedDmaapMessageResponse.getFile(),
							failedDmaapMessageResponse.getFileName())));
					Long endTime = System.currentTimeMillis() - currentTime;
				    Long executionTime=endTime-startTime;
					auditUtil.addActionToNxUiAudit(request.getNxSolutionId(),AuditTrailConstants.FAILED_DMAP_MSG_DOWNLOAD,null,AuditTrailConstants.SUCCESS,null,null,executionTime,null);
					return zipFileResponse;
				} else {
					Long endTime = System.currentTimeMillis() - currentTime;
				    Long executionTime=endTime-startTime;
					auditUtil.addActionToNxUiAudit(request.getNxSolutionId(),AuditTrailConstants.FAILED_DMAP_MSG_DOWNLOAD,null,AuditTrailConstants.FAIL,null,null,executionTime,null);
					throw new SalesBusinessException(MessageConstants.FILE_DOWNLOAD_FAILED);
				}
			} catch (IOException e) {
				Long endTime = System.currentTimeMillis() - currentTime;
			    Long executionTime=endTime-startTime;
				auditUtil.addActionToNxUiAudit(request.getNxSolutionId(),AuditTrailConstants.FAILED_DMAP_MSG_DOWNLOAD,null,AuditTrailConstants.FAIL,null,null,executionTime,null);
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else if (Optional.ofNullable(request.getNxSolutionId()).isPresent()
				&& Optional.ofNullable(request.getNxOutputAction()).isPresent()
				&& request.getNxOutputAction().equalsIgnoreCase("inrEditDownload")) {
			log.info("inrEditDownload for nxSolutionId {}", request.getNxSolutionId());
			try {
				File file = inrEditCreateExcelService.generateInrAddressEditSheet(request.getNxSolutionId());
				zipFileResponse.setZipFileName("address_edit_template.xlsx");
				zipFileResponse.setZipFile(getEncodedFile(file));
				setSuccessResponse(zipFileResponse);
				Long endTime = System.currentTimeMillis() - currentTime;
			    Long executionTime=endTime-startTime;
				auditUtil.addActionToNxUiAudit(request.getNxSolutionId(),AuditTrailConstants.INR_EDIT_DOWNLOAD,null,AuditTrailConstants.SUCCESS,null,null,executionTime,null);
				return zipFileResponse;
			} catch (IOException e) {
				Long endTime = System.currentTimeMillis() - currentTime;
			    Long executionTime=endTime-startTime;
				auditUtil.addActionToNxUiAudit(request.getNxSolutionId(),AuditTrailConstants.INR_EDIT_DOWNLOAD,null,AuditTrailConstants.FAIL,null,null,executionTime,null);
				log.error("Exception occured during inrEditDownload process", e);
				throw new SalesBusinessException(MessageConstants.FILE_DOWNLOAD_FAILED);
			}
		} else if (Optional.ofNullable(request.getNxSolutionId()).isPresent()
				&& Optional.ofNullable(request.getNxOutputAction()).isPresent()
				&& request.getNxOutputAction().equalsIgnoreCase("edfBulkUpload")) {
			try {
				Long requestId = request.getRequestId();
				long nxSolutionId = request.getNxSolutionId();
				EdfDownloadRequestDetailsResponse edfDownloadReqResponse = edfbulkUploadService
						.generatePreviewEdfBulkuploadSheet(requestId, nxSolutionId);
				if (edfDownloadReqResponse.getFileName() != null && edfDownloadReqResponse.getFile() != null) {
					zipFileResponse.setZipFileName(edfDownloadReqResponse.getFileName());
					zipFileResponse.setZipFile(getEncodedFile(genrateEdfOutputFile(edfDownloadReqResponse.getFile(),
							edfDownloadReqResponse.getFileName())));
					Long endTime = System.currentTimeMillis() - currentTime;
				    Long executionTime=endTime-startTime;
					auditUtil.addActionToNxUiAudit(request.getNxSolutionId(),AuditTrailConstants.EDF_BULK_UPLOAD_DOWNLOAD,null,AuditTrailConstants.SUCCESS,null,null,executionTime,null);
					return zipFileResponse;
				}
			} catch (Exception e) {
				Long endTime = System.currentTimeMillis() - currentTime;
			    Long executionTime=endTime-startTime;
				auditUtil.addActionToNxUiAudit(request.getNxSolutionId(),AuditTrailConstants.EDF_BULK_UPLOAD_DOWNLOAD,null,AuditTrailConstants.FAIL,null,null,executionTime,null);
				log.error("Main exception occured during downloadEdfbulkUpload process", e);
				throw new SalesBusinessException(MessageConstants.FILE_DOWNLOAD_FAILED);
			}
		}
		Long endTime = System.currentTimeMillis() - currentTime;
	    Long executionTime=endTime-startTime;
		auditUtil.addActionToNxUiAudit(request.getNxSolutionId(),AuditTrailConstants.DOWNLOAD_CUSTOMER_INVENTORY,null,AuditTrailConstants.SUCCESS,null,null,executionTime,null);
		return zipFileResponse;
	}
	private ZipFileResponse generateZipFileResponse(NexxusOutputRequest request) throws SalesBusinessException {
		ZipFileResponse zipFileResponse=new ZipFileResponse();
		zipFileResponse.setZipFileName(request.getFileName());
		try {
			zipFileResponse.setZipFile(
					getEncodedFile(new File(inrEditTemplatePath + request.getFileName())));
		} catch (Exception e) {
			log.error("Exception occured during downloadFile process", e);
			throw new SalesBusinessException(MessageConstants.FILE_DOWNLOAD_FAILED);
		}
		return zipFileResponse;
	}
	private File genrateEdfOutputFile(Blob blob, String fileName) throws SalesBusinessException {

		fileName = fileName.replaceAll(":", "-");
		String filePath = env.getProperty("pv.directory.nexxusoutput");
		File dir = new File(filePath); // NOSONAR
		// create output directory if it doesn't exist
		if (!dir.exists())
			dir.mkdirs();
		File file = CSVFileWriter.convertBlobToFile(blob, filePath + fileName);

		return file;

	}

	public String getEncodedFile(File file) {
		try {
			return Base64.getEncoder().encodeToString(FileUtils.readFileToByteArray(file));
		} catch (IOException e) {
			log.info("Exception during getEncodedFile {}", e.getCause());
		}
		return null;
	}

	/**
	 * Generate outpu file.
	 *
	 * @param zipFileResponse the zip file response
	 * @param blob            the blob
	 * @param fileName        the file name
	 * @throws SalesBusinessException the sales business exception
	 */
	protected void generateOutpuFile(ZipFileResponse zipFileResponse, Blob blob, String fileName)
			throws SalesBusinessException {
		fileName = fileName.replaceAll(":", "-");
		String filePath = env.getProperty("pv.directory.nexxusoutput");
		File dir = new File(filePath); // NOSONAR
		// create output directory if it doesn't exist
		if (!dir.exists())
			dir.mkdirs();
		File file = CSVFileWriter.convertBlobToFile(blob, filePath + fileName);
		zipFileResponse.setFileZip(file);
		zipFileResponse.setZipFileName(fileName);
	}

	/**
	 * Gets the zip file response.
	 *
	 * @param zipFileResponse the zip file response
	 * @param cdtfile         the cdtfile
	 * @param blob            the blob
	 * @param fileName        the file name
	 * @return the zip file response
	 * @throws SalesBusinessException the sales business exception
	 */
	public void getZipFileResponse(ZipFileResponse zipFileResponse, String cdtfile, Blob blob, String fileName)
			throws SalesBusinessException {
		fileName = fileName.replaceAll(":", "-");
		String filePath = env.getProperty("pv.directory.nexxusoutput");
		File dir = new File(filePath); // NOSONAR
		// create output directory if it doesn't exist
		if (!dir.exists())
			dir.mkdirs();
		CSVFileWriter.convertBlobToFile(blob, filePath + fileName);

		ArrayList<String> inputFiles = new ArrayList<>();
		inputFiles.add(filePath + fileName);
		inputFiles.add(cdtfile);

		String zipFileName = FilenameUtils.removeExtension(fileName) + ".zip";
		String zipFileNamePath = filePath + zipFileName;

		/*
		 * Path path = Paths.get(filePath); Path pathWhole = path.resolve(zipFileName);
		 * String zipFileNamePath = pathWhole.toString();
		 */

		doZip(zipFileNamePath, inputFiles);

		File zipFile = new File(zipFileNamePath); // NOSONAR
		zipFileResponse.setZipFileName(zipFileName);
		zipFileResponse.setFileZip(zipFile);
	}

	/**
	 * Validate and get nx solution details.
	 *
	 * @param request the request
	 * @return nxSolutionDetail
	 * @throws SalesBusinessException the sales business exception
	 */
	public NxSolutionDetail validateAndGetNxSolutionDetails(NexxusOutputRequest request) throws SalesBusinessException {
		List<NxSolutionDetail> nxSolutionDetailList = hybridRepo.getNxSolutionDetailList(request.getNxSolutionId());
		NxSolutionDetail nxSolutionDetail;
		if (!nxSolutionDetailList.isEmpty()) {
			nxSolutionDetail = nxSolutionDetailList.get(0);
		} else {
			log.info("Invalid SolutinId");
			throw new SalesBusinessException(MessageConstants.INVALID_SOLUTION_ID);
		}
		return nxSolutionDetail;
	}

	/**
	 * Creates NxOutputFileIdResponse.
	 *
	 * @param request the request
	 * @return the NxOutputFileIdResponse
	 * @throws SalesBusinessException the sales business exception
	 */
	public ServiceResponse getNxOutputFileId(NexxusOutputRequest request) throws SalesBusinessException {

		NxOutputFileAuditModel nxOutputFileAuditModel = new NxOutputFileAuditModel();
		NxOutputFileIdResponse fileIdResponse = new NxOutputFileIdResponse();
		NxSolutionDetail nxSolutionDetail;
		if (request != null && request.getRequestIds() != null && request.getNxSolutionId() != null) {

			nxSolutionDetail = validateAndGetNxSolutionDetails(request);
			Blob blob = null;
			String fileName = null;

			Map<String, Blob> resultMap = getOutputFilesByReqIdAndSolId(request);

			for (Map.Entry<String, Blob> entry : resultMap.entrySet()) {
				fileName = entry.getKey();
				blob = entry.getValue();
			}

			if (fileName != null && blob != null) {
				nxOutputFileAuditModel.setNxSolutionDetail(nxSolutionDetail);
				nxOutputFileAuditModel.setOutputFile(blob);
				nxOutputFileAuditModel.setFileName(fileName);
				nxOutputFileAuditModel.setCreatedDate(new Date());
				hybridRepo.setNxOutputFileAudit(nxOutputFileAuditModel);
			} else {
				// Put valid message for Invalid input
				throw new SalesBusinessException(MessageConstants.FILE_DOWNLOAD_FAILED);
			}

		} else {
			// Put valid message for Invalid Input
			throw new SalesBusinessException(MessageConstants.INVALID_REQUEST);
		}
		fileIdResponse.setNxOutputFileId(nxOutputFileAuditModel.getNxOutputFileId());
		fileIdResponse.setNxOutputFileName(nxOutputFileAuditModel.getFileName());
		setSuccessResponse(fileIdResponse);
		return fileIdResponse;
	}
}