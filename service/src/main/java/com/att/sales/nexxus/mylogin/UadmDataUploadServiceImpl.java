package com.att.sales.nexxus.mylogin;

import java.util.HashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.att.sales.nexxus.constant.StringConstants;

@Component
public class UadmDataUploadServiceImpl {
	
	private static final Logger log = LoggerFactory.getLogger(UadmDataUploadServiceImpl.class);
	
	@Autowired
	private GetUsersForGroupWSHandler getUsersForGroupWSHandler;
	
	@Autowired
	private NexxusMyloginService nexxusMyloginService;
	
	@Value("${uadm.myprice.dataupload.enabled:N}")
	private String mypriceDataupload;
	
	@Value("${uadm.nexxus.dataupload.enabled:N}")
	private String nexxusDataupload;
	
	@Scheduled(fixedDelayString = "${uadm.dataupload.scheduletime}") // 24 hours
	public void uploadMypriceData() {
		if(StringConstants.CONSTANT_Y.equalsIgnoreCase(mypriceDataupload)) {
			log.info("uadmDataUploadServiceImpl : Start Myprice data upload");
			try {
				getUsersForGroupWSHandler.getUsersWebService(new HashMap<String, Object>());
			} catch (Exception e) {
				e.printStackTrace();
			}
			log.info("uadmDataUploadServiceImpl : End Myprice data upload");
		}
	}
	
	@Scheduled(fixedDelayString = "${uadm.dataupload.scheduletime}") // 24 hours
	public void uploadNexxusData() {
		if(StringConstants.CONSTANT_Y.equalsIgnoreCase(nexxusDataupload)) {
			log.info("uadmDataUploadServiceImpl : Start Nexxus data upload");
			try {
				nexxusMyloginService.getUserProfile();
			} catch (Exception e) {
				e.printStackTrace();
			}
			log.info("uadmDataUploadServiceImpl : End Nexxus data upload");
		}
	}
	

}
