package com.att.sales.nexxus.service;

import java.util.concurrent.CompletableFuture;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.att.sales.framework.service.BaseServiceImpl;
import com.att.sales.nexxus.admin.model.DataUploadRequest;
import com.att.sales.nexxus.admin.model.DataUploadResponse;

/**
 * The Class NxDataUploadService.
 */
/**
 * @author vt393d
 *
 */
@Service
public class NxDataUploadService  extends BaseServiceImpl{
	
	/** The log. */
	private static Logger log = LoggerFactory.getLogger(NxDataUploadService.class);
	
	/** The nx data upload helper service. */
	@Autowired
	private NxDataUploadHelperService nxDataUploadHelperService;
	
	
	
	/**
	 * Upload nexxus data file.
	 *
	 * @param request the request
	 * @return the data upload response
	 */
	public DataUploadResponse uploadNexxusDataFile(DataUploadRequest request) {
		log.info("Inside uploadNexxusDataFile method  {}","");
		DataUploadResponse resp=new DataUploadResponse();
		CompletableFuture.runAsync(() -> nxDataUploadHelperService.uploadDataFile(request));
		setSuccessResponse(resp);
		return resp;
	}



	
	
}
