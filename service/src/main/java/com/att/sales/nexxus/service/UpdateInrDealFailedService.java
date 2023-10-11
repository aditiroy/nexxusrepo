package com.att.sales.nexxus.service;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.transaction.Transactional;

import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.att.sales.framework.model.ServiceMetaData;
import com.att.sales.nexxus.dao.model.NxMpDeal;
import com.att.sales.nexxus.dao.repository.NxMpDealRepository;
import com.att.sales.nexxus.util.ThreadMetaDataUtil;

@Component
public class UpdateInrDealFailedService {
	
	/** The log. */
	private static Logger log = LoggerFactory.getLogger(UpdateInrDealFailedService.class);

	/** The enable scheduler. */
	@Value("${enable.scheduler}")
	private String enableScheduler;

	@Value("#{new Integer('${mpdeal.fail.time.in.hours}')}")
	private int mpDealFailTimeInHours;
	
	@Autowired
	private NxMpDealRepository nxMpDealRepository;
	
	@Autowired
	private MailServiceImpl mailServiceImpl;
	
	/**
	 * scheduled job to update STATUS in NX_MP_DEAL when the mp processing is happening for more than 48 hrs
	 * 
	 */
	@Transactional
	@Scheduled(fixedDelay = 6 * 60 * 60 * 1000) // 6 hours
	public void updateMpDealFailedStatus() {
		log.info("updateMpDealFailedStatus starts {}", enableScheduler);
		if ("Y".equalsIgnoreCase(enableScheduler)) {
			Date dateThreshold = Date.from(Instant.now().minus(mpDealFailTimeInHours, ChronoUnit.HOURS));
			List<NxMpDeal> nxMpDeals = nxMpDealRepository.getNxMpDeals(dateThreshold);
			if(CollectionUtils.isNotEmpty(nxMpDeals)) {
				Map<String, Object> requestParams = new HashMap<>();
				for (NxMpDeal deal : nxMpDeals) {
					try {
						deal.setDealStatus("FAILED");
						deal.setModifiedDate(new Timestamp(System.currentTimeMillis()));
						nxMpDealRepository.saveAndFlush(deal);
						String conversationId = String.format("NEXXUSMPDEALFAILEDSTATUS%s", deal.getNxTxnId());
						requestParams.put(ServiceMetaData.XCONVERSATIONID, conversationId);
						requestParams.put(ServiceMetaData.REST_REQUEST_START_TIME, System.currentTimeMillis());
						ThreadMetaDataUtil.initThreadMetaData(requestParams);
						mailServiceImpl.prepareMyPriceDealSubmissionRequest(deal);
					}catch(Exception e){
						log.error("Exception in updateMpDealFailedStatus {}", e.getMessage());
					} finally {
						ThreadMetaDataUtil.destroyThreadMetaData();
					}
				}
				log.info("Total deals updated to failed are : {}", nxMpDeals.size());
			}
		}
	}


}
