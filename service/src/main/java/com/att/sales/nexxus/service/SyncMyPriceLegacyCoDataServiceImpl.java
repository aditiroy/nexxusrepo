package com.att.sales.nexxus.service;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;

import com.att.sales.framework.exception.SalesBusinessException;
import com.att.sales.framework.model.ServiceResponse;
import com.att.sales.framework.service.BaseServiceImpl;
import com.att.sales.nexxus.common.CommonConstants;
import com.att.sales.nexxus.constant.MyPriceConstants;
import com.att.sales.nexxus.constant.StringConstants;
import com.att.sales.nexxus.dao.model.LegacyCoDetails;
import com.att.sales.nexxus.dao.model.LegacyCoPercentage;
import com.att.sales.nexxus.dao.model.NxDesignAudit;
import com.att.sales.nexxus.dao.model.LegacyCoPercentage.Pk;
import com.att.sales.nexxus.dao.repository.LegacyCoDetailsDao;
import com.att.sales.nexxus.dao.repository.LegacyCoPercentageDao;
import com.att.sales.nexxus.dao.repository.NxDesignAuditRepository;
import com.att.sales.nexxus.model.SyncMyPriceLegacyCoDataRequest;
import com.att.sales.nexxus.model.SyncMyPriceLegacyCoDataResponse;
import com.att.sales.nexxus.myprice.transaction.service.MyPriceTransactionUtil;
import com.att.sales.nexxus.util.ExceptionUtil;
import com.att.sales.nexxus.util.HttpRestClient;
import com.att.sales.nexxus.util.RestClientUtil;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class SyncMyPriceLegacyCoDataServiceImpl extends BaseServiceImpl {
	private static Logger logger = LoggerFactory.getLogger(SyncMyPriceLegacyCoDataServiceImpl.class);
	private static final String ERROR_UNKNOWN_UPDATING_SOURCE = "MP0001";
	private static final String ERROR_PROCESS_ERROR = "M00003";
	
	@Value("${p8.local.destPath}")
	private String p8dLocalPath;
	
	@Value("${myprice.import.data.table}")
	private String mpImportData;
	
	@Value("${myprice.import.task.details}")
	private String mpTaskDetails;
	
	@Value("${myprice.deploy.data.table}")
	private String mpDeployData;
	
	@Autowired
	private LegacyCoDetailsDao legacyCoDetailsDao;
	
	@Autowired
	private LegacyCoPercentageDao legacyCoPercentageDao;
	
	@Autowired
	private RestClientUtil restClientUtil;
	
	@Autowired
	private MyPriceTransactionUtil myPriceTransactionUtil;
	
	@Autowired
	private ObjectMapper mapper;
	
	@Autowired
	private NxDesignAuditRepository nxDesignAuditRepository;
	
	@Autowired
	private HttpRestClient httpRestClient;
	
	@Value("${myprice.proxy.enabled}")
	private String isProxyEnabled;
	
	@Autowired
	private Environment env;
	
	private int mpDeployRetry = 3;
	
	private int deployCallWaitTimeInSec = 120;

	@Transactional(rollbackOn = Exception.class)
	public ServiceResponse syncMyPriceLegacyCoData(SyncMyPriceLegacyCoDataRequest request)
			throws SalesBusinessException {
		logger.info("enter syncMyPriceLegacyCoData method");
		SyncMyPriceLegacyCoDataResponse response = new SyncMyPriceLegacyCoDataResponse();
		validateRequest(request);
		String savedCsvFolder = "uploadMPCsv_" + Thread.currentThread().getName() + "_" + System.currentTimeMillis();
		Path savedCsvFolderPath = Paths.get(p8dLocalPath).resolve(savedCsvFolder);
		try {
			Files.createDirectory(savedCsvFolderPath);
			syncLegacyCoDetails(request.getUpdatingSource(), savedCsvFolderPath, response);
			Thread.sleep(deployCallWaitTimeInSec * 1000);
			syncLegacyCoPercentage(request.getUpdatingSource(), savedCsvFolderPath, response);
		} catch (IOException | InterruptedException e) {
			logger.error("Exception", e);
			throw new SalesBusinessException(ERROR_PROCESS_ERROR);
		}
		return response;
	}
	
	protected void syncLegacyCoPercentage(String source, Path bashFolder, SyncMyPriceLegacyCoDataResponse response) {
		List<LegacyCoPercentage> legacyCoPercentageSyncRecords = legacyCoPercentageDao.findSyncRecords(source);
		if (!legacyCoPercentageSyncRecords.isEmpty()) {
			Path legacyCoPercentageCsvPath = bashFolder.resolve("LegacyCOPercentage");
			try (PrintWriter writer = new PrintWriter(Files.newBufferedWriter(legacyCoPercentageCsvPath))) {
				writer.println(String.format(
						"_start meta data%n_update_action,CO_CLLI1,CO_CLLI2,OCN,COMPANY_NAME,PERCENTAGE%nString,String,String,String,String,String%n,,,,,%n_end meta data"));
				legacyCoPercentageSyncRecords.forEach(record -> writer.println(record));
				Integer importTaskId = callMpImportData(legacyCoPercentageCsvPath);
				if (importTaskId != null) {
					response.setLegacyCoPercentageImportTaskId(importTaskId);
					TaskDetails importTaskDetails = callMpTaskDetails(importTaskId);
					if (importTaskDetails != null) {
						response.setLegacyCoPercentageName(importTaskDetails.getName());
						Thread.sleep(deployCallWaitTimeInSec * 1000);
						Integer deployTaskId = callMpDeployData(importTaskDetails.getName());
						if (deployTaskId != null) {
							response.setLegacyCoPercentageDeployTaskId(deployTaskId);
							TaskDetails deployTaskDetails = null;
							for (int i = 0, t = deployCallWaitTimeInSec * 1000; i < mpDeployRetry; i++, t *= 2) {
								Thread.sleep(t);
								deployTaskDetails = callMpTaskDetails(deployTaskId);
								if ("Completed".equalsIgnoreCase(deployTaskDetails.getStatus())) {
									break; 
								}
							}
							if ("Completed".equalsIgnoreCase(deployTaskDetails.getStatus())) {
								response.setLegacyCoPercentageMpDeployStatus(deployTaskDetails.getStatus());
								List<Pk> pks = legacyCoPercentageSyncRecords.stream().map(LegacyCoPercentage::getPk).collect(Collectors.toList());
								int updateCount = legacyCoPercentageDao.updateCompleteStatus(pks, source + "_complete");
								logger.info("{} LegacyCoPercentage status updated to complete", updateCount);
								saveDesignAudit(source + "_" + "LegacyCoPercentage", mapper.writeValueAsString(response), "SUCCESS");
								return;
							} else if (deployTaskDetails != null) {
								response.setLegacyCoPercentageMpDeployStatus(deployTaskDetails.getStatus());
							}
						}
					}
				}
				saveDesignAudit(source + "_" + "LegacyCoPercentage", mapper.writeValueAsString(response), "FAILURE");
			} catch (IOException | InterruptedException e) {
				logger.error("Exception", e);
				saveDesignAudit(source + "_" + "LegacyCoPercentage", ExceptionUtil.toString(e), "FAILURE");
			}
			
		}
	}
	
	protected void syncLegacyCoDetails(String source, Path bashFolder, SyncMyPriceLegacyCoDataResponse response) {
		List<LegacyCoDetails> legacyCoDetailsSyncRecords = legacyCoDetailsDao.findSyncRecords(source);
		if (!legacyCoDetailsSyncRecords.isEmpty()) {
			Path legacyCoDetailsCsvPath = bashFolder.resolve("LegacyCODetails");
			try (PrintWriter writer = new PrintWriter(Files.newBufferedWriter(legacyCoDetailsCsvPath))) {
				writer.println(String.format(
						"_start meta data%n_update_action,SWCCLLI,COCLLI,SWITCHCLLI,STATE,OCN,OPERATING_COMP_NAME,SWCVCoordinate,SWCHCoordinate,CATEGORY,SWITCHTYPE,ISDNPRIHOSTTYPE,CENTREX_CAPABLE,MSA_RELIEF_IND,ZONE,FEATURE_CODE%nString,String,String,String,String,String,String,String,String,String,String,String,String,String,String,String%n,,,,,,,,,,,,,,,%n_end meta data"));
				legacyCoDetailsSyncRecords.forEach(record -> writer.println(record));
				Integer importTaskId = callMpImportData(legacyCoDetailsCsvPath);
				if (importTaskId != null) {
					response.setLegacyCoDetailsImportTaskId(importTaskId);
					TaskDetails importTaskDetails = callMpTaskDetails(importTaskId);
					if (importTaskDetails != null) {
						response.setLegacyCoDetailsName(importTaskDetails.getName());
						Thread.sleep(deployCallWaitTimeInSec * 1000);
						Integer deployTaskId = callMpDeployData(importTaskDetails.getName());
						if (deployTaskId != null) {
							response.setLegacyCoDetailsDeployTaskId(deployTaskId);
							TaskDetails deployTaskDetails = null;
							for (int i = 0, t = deployCallWaitTimeInSec * 1000; i < mpDeployRetry; i++, t *= 2) {
								Thread.sleep(t);
								deployTaskDetails = callMpTaskDetails(deployTaskId);
								if ("Completed".equalsIgnoreCase(deployTaskDetails.getStatus())) {
									break; 
								}
							}
							if ("Completed".equalsIgnoreCase(deployTaskDetails.getStatus())) {
								response.setLegacyCoDetailsMpDeployStatus(deployTaskDetails.getStatus());
								List<String> ids = legacyCoDetailsSyncRecords.stream().map(LegacyCoDetails::getSwcclli).collect(Collectors.toList());
								int updateCount = legacyCoDetailsDao.updateCompleteStatus(ids, source + "_complete");
								logger.info("{} LegacyCoDetails status updated to complete", updateCount);
								saveDesignAudit(source + "_" + "LegacyCoDetails", mapper.writeValueAsString(response), "SUCCESS");
								return;
							} else if (deployTaskDetails != null) {
								response.setLegacyCoDetailsMpDeployStatus(deployTaskDetails.getStatus());
							}
						}
					}
				}
				saveDesignAudit(source + "_" + "LegacyCoDetails", mapper.writeValueAsString(response), "FAILURE");
			} catch (IOException | InterruptedException e) {
				logger.error("Exception", e);
				saveDesignAudit(source + "_" + "LegacyCoDetails", ExceptionUtil.toString(e), "FAILURE");
			}
			
		}
	}
	
	protected void saveDesignAudit(String source, String data, String status) {
		NxDesignAudit nxDesignAudit = new NxDesignAudit();		
		nxDesignAudit.setTransaction("MP_LEGACY_SYNC");
		nxDesignAudit.setNxSubRefId(source);
		nxDesignAudit.setData(data);
		nxDesignAudit.setStatus(status);
		nxDesignAuditRepository.save(nxDesignAudit);
	}

	protected Integer callMpDeployData(String name) {
		Integer res = null;
		String request = String.format("{\"selections\":[\"%s\"]}", name);
		try {
			Map<String, String> headers  = new HashMap<>();
			headers.put(StringConstants.REQUEST_AUTHORIZATION, "Basic "+env.getProperty(CommonConstants.MYPRICE_AUTHORIZATION));
			headers.put(StringConstants.REQUEST_CONTENT_TYPE, "application/json");
			String proxy = null;
			if(StringConstants.CONSTANT_Y.equalsIgnoreCase(isProxyEnabled)) {
				proxy = env.getProperty(CommonConstants.CDP_HTTP_PROXY);
			}
			String response = httpRestClient.callHttpRestClient(mpDeployData, HttpMethod.POST, null, request, 
					headers, proxy);
			//if (mpResponse.get(MyPriceConstants.RESPONSE_CODE).toString().startsWith("2")) {
			if(response != null) {
			//	String response = (String) mpResponse.get(MyPriceConstants.RESPONSE_DATA);
				JsonNode responseNode = mapper.readTree(response);
				res = responseNode.at("/taskIds/0/id").asInt();
			}
		} catch (SalesBusinessException | IOException e) {
			logger.info("Exception", e);
		}
		return res;
	}

	protected TaskDetails callMpTaskDetails(int importTaskId) {
		TaskDetails res = null;
		String url = mpTaskDetails.replace("{taskId}", String.valueOf(importTaskId));
		try {
			Map<String, String> headers  = new HashMap<>();
			headers.put(StringConstants.REQUEST_AUTHORIZATION, "Basic "+env.getProperty(CommonConstants.MYPRICE_AUTHORIZATION));
			headers.put(StringConstants.REQUEST_CONTENT_TYPE, "application/json");
			String proxy = null;
			if(StringConstants.CONSTANT_Y.equalsIgnoreCase(isProxyEnabled)) {
				proxy = env.getProperty(CommonConstants.CDP_HTTP_PROXY);
			}
			String response = httpRestClient.callHttpRestClient(url, HttpMethod.GET, null, null, 
					headers, proxy);
		//	if (mpResponse.get(MyPriceConstants.RESPONSE_CODE).toString().startsWith("2")) {
			//	String response = (String) mpResponse.get(MyPriceConstants.RESPONSE_DATA);
			if(response != null) {
				JsonNode responseNode = mapper.readTree(response);
				res = new TaskDetails(responseNode.path("name").asText(), responseNode.path("status").asText());
			}
		} catch (SalesBusinessException | IOException e) {
			logger.error("Exception", e);
		}
		return res;
	}

	protected Integer callMpImportData(Path csvPath) {
		Integer res = null;
		Map<String, String> headers  = new HashMap<>();
		headers.put(StringConstants.REQUEST_AUTHORIZATION, "Basic "+env.getProperty(CommonConstants.MYPRICE_AUTHORIZATION));
		Map<String, String> formField = new HashMap<>();
		formField.put("columnDelimiter", ",");
		formField.put("rowDelimiter", "");
		formField.put("dataHasDelimiter", "true");
		formField.put("folderVariableName", "a328750");
		Map<String, Path> filePart = new HashMap<>();
		filePart.put("file", csvPath);
		try {
			Map<String, Object> mpResponse = restClientUtil.initiateFormDataWebService(mpImportData, CommonConstants.POST_METHOD,
					headers, null, formField, filePart);
			if (mpResponse.get(MyPriceConstants.RESPONSE_CODE).toString().startsWith("2")) {
				String response = (String) mpResponse.get(MyPriceConstants.RESPONSE_DATA);
				JsonNode responseNode = mapper.readTree(response);
				res = responseNode.at("/tasks/0/taskId").asInt();
			}
		} catch (SalesBusinessException | IOException e) {
			logger.error("Exception", e);
		}
		return res;
	}

	protected void validateRequest(SyncMyPriceLegacyCoDataRequest request) throws SalesBusinessException {
		if ("dcc".equalsIgnoreCase(request.getUpdatingSource()) || "edw".equalsIgnoreCase(request.getUpdatingSource())) {
			return;
		}
		throw new SalesBusinessException(ERROR_UNKNOWN_UPDATING_SOURCE);
	}
	
	private static class TaskDetails {
		private String name;
		private String status;

		public TaskDetails(String name, String status) {
			super();
			this.name = name;
			this.status = status;
		}

		public String getName() {
			return name;
		}

		public String getStatus() {
			return status;
		}
	}
}
