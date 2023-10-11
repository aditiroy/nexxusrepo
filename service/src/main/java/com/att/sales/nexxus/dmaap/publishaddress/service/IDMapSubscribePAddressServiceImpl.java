package com.att.sales.nexxus.dmaap.publishaddress.service;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.att.sales.nexxus.dmaap.mr.util.DmaapMRSubscriberImpl;
import com.att.sales.nexxus.dmaap.publishaddress.processor.MyPriceInitiatedFlow;

import lombok.extern.slf4j.Slf4j;

/**
 * @author IndraSingh
 * 
 */
@Service
@Slf4j
public class IDMapSubscribePAddressServiceImpl implements IDMapSubscribePAddressService {

	@Autowired
	private Environment env;

	@Autowired
	private DmaapMRSubscriberImpl dMaapMRSubscriberImpl;

	@Autowired
	private MyPriceInitiatedFlow processor;
	
	@Value("${ipne.dmaap.enabled:N}")
	private String ipneDmaapEnabled;

	@Override
	@Scheduled(fixedDelay = 5000)
	public void dMapPublishAddressEvent() throws IOException, InterruptedException, Exception {
		
		String topic = env.getProperty("dmaap.subscriber.publishAddress.topic");

		String groupName = env.getProperty("dmaap.subscriber.publishAddress.groupName");
		
		String host = env.getProperty("dmaap.subscriber.ipne.host");

		if ("Y".equalsIgnoreCase(ipneDmaapEnabled) && !topic.isEmpty() && !groupName.isEmpty()) {
			List<String> messagesList = publishDmaapMRSubscriber(topic, groupName, host);
			if (!messagesList.isEmpty()) {
				log.info("Event found : calling dMapSubscribeAddressProcessor for processing");
				processor.dMapSubscribeAddressProcessorThroughThread(messagesList);
			}

		}

	}

	private List<String> publishDmaapMRSubscriber(String topicc, String groupNamee, String host)
			throws IOException, InterruptedException, Exception {
		log.info("Service Log :: [Nexxus Info] :: publishDmaapMRSubscriber invoked");

		List<String> messagesList;
		messagesList = dMaapMRSubscriberImpl.retrieveMessage(topicc, groupNamee, host);

		log.info("Service Log :: [Nexxus Info] :: publishDmaapMRSubscriber completed");
		return messagesList;
	}

}
