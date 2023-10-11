package com.att.sales.nexxus.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.att.sales.framework.exception.SalesBusinessException;
import com.att.sales.framework.model.ServiceMetaData;
import com.att.sales.framework.model.ServiceResponse;
import com.att.sales.framework.service.BaseServiceImpl;
import com.att.sales.nexxus.dao.repository.NxDesignAuditRepository;
import com.att.sales.nexxus.model.SyncMyPriceLegacyCoDataRequest;
import com.att.sales.nexxus.model.UploadMyPriceLegacyCoDataResponse;
import com.att.sales.nexxus.template.model.NxTemplateUploadRequest;
import com.att.sales.nexxus.util.ThreadMetaDataUtil;

@Service
public class UploadMyPriceLegacyCoDataServiceImpl extends BaseServiceImpl {
	private static Logger logger = LoggerFactory.getLogger(UploadMyPriceLegacyCoDataServiceImpl.class);
	private static final String ERROR_UNKNOWN_FILE = "MP0000";

	@Autowired
	private UploadNexxusLegacyCoDataService uploadNexxusLegacyCoDataService;
	
	@Autowired
	private SyncMyPriceLegacyCoDataServiceImpl syncMyPriceLegacyCoDataServiceImpl;
	
	@Autowired
	private NxDesignAuditRepository nxDesignAuditRepository;
	
	public ServiceResponse uploadMyPriceLegacyCoData(NxTemplateUploadRequest request)
			throws SalesBusinessException {
		logger.info("enter uploadMyPriceLegacyCoData method");
		UploadMyPriceLegacyCoDataResponse response = new UploadMyPriceLegacyCoDataResponse();
		response.setFileName(request.getFileName());
		validateRequest(request);
		
		Map<String, Object> requestMetaDataMap = new HashMap<>();
		if (ServiceMetaData.getRequestMetaData() != null) {
			ServiceMetaData.getRequestMetaData().forEach((key, value) -> requestMetaDataMap.put(key, value));
			requestMetaDataMap.put(ServiceMetaData.XCONVERSATIONID, requestMetaDataMap.get(ServiceMetaData.XCONVERSATIONID));
			requestMetaDataMap.remove(ServiceMetaData.XCONVERSATIONID);
		}
		CompletableFuture.runAsync(() -> {
			try {
				logger.info("UploadMyPriceLegacyCoDataServiceImpl worker thread starts");
				ThreadMetaDataUtil.initThreadMetaData(requestMetaDataMap);
				uploadNexxusLegacyCoDataService.updateNexxusLegacyCoData(request);
				
				List<String> dccLodedFiles = nxDesignAuditRepository.findDccLodedFiles();
				if (isAllDccFileLoaded(dccLodedFiles)) {
					nxDesignAuditRepository.resetDccLodedFiles();
					SyncMyPriceLegacyCoDataRequest syncMyPriceLegacyCoDataRequest = new SyncMyPriceLegacyCoDataRequest();
					syncMyPriceLegacyCoDataRequest.setUpdatingSource("dcc");
					syncMyPriceLegacyCoDataServiceImpl.syncMyPriceLegacyCoData(syncMyPriceLegacyCoDataRequest);
				}
			} catch (Exception e) {
				logger.info("Exception", e);
			} finally {
				ThreadMetaDataUtil.destroyThreadMetaData();
				logger.info("UploadMyPriceLegacyCoDataServiceImpl worker thread ends");
			}
		});
		
		return response;
	}

	

	protected boolean isAllDccFileLoaded(List<String> dccLodedFiles) {
		if (dccLodedFiles.size() < 3) {
			return false;
		}
		if (!dccLodedFiles.contains("QENXXNP.txt")) {
			return false;
		}
		if (!dccLodedFiles.contains("TISDN.txt")) {
			return false;
		}
		for (String fileName : dccLodedFiles) {
			if (fileName.endsWith(".zip")) {
				return true;
			}
		}
		return false;
	}



	protected void validateRequest(NxTemplateUploadRequest request) throws SalesBusinessException {
		if ("QENXXNP.txt".equals(request.getFileName())) {
			return;
		} else if ("TISDN.txt".equals(request.getFileName())) {
			return;
		} else if (request.getFileName().endsWith("zip")) {
			return;
		}
		throw new SalesBusinessException(ERROR_UNKNOWN_FILE);
	}
}
