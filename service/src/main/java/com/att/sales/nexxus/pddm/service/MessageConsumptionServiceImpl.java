package com.att.sales.nexxus.pddm.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.scheduling.annotation.Scheduled;
import com.att.sales.nexxus.dmaap.mr.util.IDmaapMRSubscriber;

/**
 * The Class MessageConsumptionServiceImpl.
 *
 * @author RudreshWaladaunki
 */
public class MessageConsumptionServiceImpl implements MessageCosumptionService {
	
	/** The logger. */
	private static Logger logger = LoggerFactory.getLogger(MessageConsumptionServiceImpl.class);
	
	/** The details service impl. */
	@Autowired
	private NexxusUDFDetailsServiceImpl detailsServiceImpl;


	/** The dmaap MR subscriber service. */
	@Autowired
	private IDmaapMRSubscriber dmaapMRSubscriberService;
	
	
	/* (non-Javadoc)
	 * @see com.att.sales.nexxus.pddm.service.MessageCosumptionService#getMessage()
	 */
	@Override
	@Scheduled(fixedDelay = 1000) // 2 mins
	public void getMessage() {
		
		List<String> messages;
		String message;
		
		try {	//"TopicName" and "GroupName" needs to be updated
				messages=dmaapMRSubscriberService.retrieveMessage("TopicName", "GroupName", "host");
				
			if(!messages.isEmpty()){
				logger.info("Inside retrieveMessage method Successfully caught Dmaap event from PDDM");
				message = messages.get(messages.size()-1);
				detailsServiceImpl.loadDataFromWorkingToActualTables(message);
			}
			
		} catch (Exception x) {
			logger.error("Exception while getting Dmaap Message Router messages:%s",x);
		}
		
	}
	

}
