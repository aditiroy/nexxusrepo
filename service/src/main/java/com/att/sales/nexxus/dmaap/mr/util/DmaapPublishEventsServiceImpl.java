package com.att.sales.nexxus.dmaap.mr.util;

import java.io.IOException;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;

import com.att.sales.nexxus.constant.CommonConstants;
import com.att.sales.nexxus.ped.dmaap.model.NxPEDStatusDMaap;
import com.att.sales.nexxus.rateletter.model.RateLetterStatusRequest;
import com.att.sales.nexxus.service.TransmitDesignDataService;
import com.att.sales.nexxus.transmitdesigndata.model.NxSolutionStatusDMaap;

@Service
public class DmaapPublishEventsServiceImpl implements DmaapPublishEventsService {

	private static final Logger logger = LoggerFactory.getLogger(DmaapPublishEventsServiceImpl.class);

	private static final String classEx = "DmaapPublishEventsServiceImpl >>>>>>> ";

	private static final String triggerDmaapEventForPEDRequestEx = "triggerDmaapEventForPEDRequest >>>>>>> ";

	private static final String triggerDmaapEventForMypriceEx = "triggerDmaapEventForMyprice >>>>>>> ";
	
	private static final String triggerDmaapEventForSLCEx = "triggerDmaapEventForSLC >>>>>>> ";

	@Autowired
	private Environment env;

	@Autowired
	private IDmaapMRPublisher dmaapMrPublisherService;
	
	@Autowired
	private KafkaTemplate<Integer, String> template;
	
	@Autowired
	private TransmitDesignDataService transmitDesignDataService;

	private ObjectMapper mapper = new ObjectMapper();
	
	@Value("${publish.kafka.message.MP:N}")
	private String isPublishKafkaMsgForMP;
	
	@Value("${publish.kafka.message.PED:N}")
	private String isPublishKafkaMsgForPED;

	@Override
	public void triggerDmaapEventForPEDRequest(NxPEDStatusDMaap request,Map<String,Object> inputmap) {
		try {
			String topicName = env.getProperty("dmaap.publisher.topic.name");
			String groupName = env.getProperty("dmaap.publisher.group.name.ped");
			String host = env.getProperty("dmaap.producer.host");
			String jsonString = mapper.writeValueAsString(request);
			if("Y".equalsIgnoreCase(isPublishKafkaMsgForPED)){
				publishKafkaMessage(topicName, jsonString,inputmap);
			}
			publishMessage(topicName, groupName, jsonString, triggerDmaapEventForPEDRequestEx, host);
		} catch (JsonGenerationException | JsonMappingException e) {
			logger.error(classEx + triggerDmaapEventForPEDRequestEx + "Error : While JSON generate OR Mapping  " + e);
		} catch (IOException e) {
			logger.error(classEx + triggerDmaapEventForPEDRequestEx + "Error : While performing I/O operations " + e);
		}
	}
	
	
	
	public void triggerDmaapEventForSLC(NxSolutionStatusDMaap pedDmaap) {
		try {
			String topicName = env.getProperty("dmaap.publisher.topic.name");
			String groupName = env.getProperty("dmaap.publisher.group.name.ped");
			String host = env.getProperty("dmaap.producer.host");
			String jsonString = mapper.writeValueAsString(pedDmaap);
			publishMessage(topicName, groupName, jsonString, triggerDmaapEventForSLCEx, host);
		} catch (JsonGenerationException | JsonMappingException e) {
			logger.error(classEx + triggerDmaapEventForSLCEx + "Error : While JSON generate OR Mapping  " + e);
		} catch (IOException e) {
			logger.error(classEx + triggerDmaapEventForSLCEx + "Error : While performing I/O operations " + e);
		}
	}

	@Override
	public void triggerDmaapEventForMyprice(RateLetterStatusRequest request,Map<String,Object> inputmap) {
		try {
			String topicName = env.getProperty("dmaap.publisher.rateLetter.topic.name");
			String groupName = env.getProperty("dmaap.publisher.rateLetter.group.name");
			String host = env.getProperty("dmaap.producer.host");
			String jsonString = mapper.writeValueAsString(request);
			if("Y".equalsIgnoreCase(isPublishKafkaMsgForMP)){
				publishKafkaMessage(topicName, jsonString,inputmap);
			}
			publishMessage(topicName, groupName, jsonString, triggerDmaapEventForMypriceEx, host);
		} catch (JsonGenerationException | JsonMappingException e) {
			logger.error(classEx + triggerDmaapEventForMypriceEx + "Error : While JSON generate OR Mapping  " + e);
		} catch (IOException e) {
			logger.error(classEx + triggerDmaapEventForMypriceEx + "Error : While performing I/O operations " + e);
		}
	}

	public Boolean publishMessage(String topicName, String groupName, String jsonString, String methodName, String host) {
		String responseCode = null;
		boolean success = false;
		try {
			logger.info("trigger dmaap event with message: {}", jsonString);
			if(StringUtils.isNotEmpty(jsonString)) {
				responseCode = dmaapMrPublisherService.publishMessage(topicName, groupName, jsonString, host);
			}
		
			if (StringUtils.isNotEmpty(responseCode) && responseCode.equals("200")) {
				success = true;
			}
			logger.info("Dmaap data sent successfully");
		} catch (Exception e) {
			logger.error(classEx + methodName + "Error : While publishing message " + e);
		}
		return success;
	}
	
	public void publishKafkaMessage(String topicName, String request,Map<String,Object> inputmap) {
		
		ListenableFuture<SendResult<Integer, String>> result = template.send(topicName, request);
		result.addCallback(new ListenableFutureCallback<SendResult<Integer, String>> () {

			@Override
			public void onSuccess(SendResult<Integer, String> result) {
				logger.info("successs");
				Long id=inputmap.get(CommonConstants.AUDIT_ID)!=null?
						(Long)inputmap.get(CommonConstants.AUDIT_ID):null;
				String transaction=inputmap.get(CommonConstants.AUDIT_TRANSACTION)!=null?
						(String)inputmap.get(CommonConstants.AUDIT_TRANSACTION):null;
				if(id!=null && StringUtils.isNotEmpty(transaction)) {
					transmitDesignDataService.saveDataInAuditTbl(id, CommonConstants.SUCCESS_STATUS, transaction);
				}
				
			}

			@Override
			public void onFailure(Throwable ex) {
				logger.info("failed");
				Long id=inputmap.get(CommonConstants.AUDIT_ID)!=null?
						(Long)inputmap.get(CommonConstants.AUDIT_ID):null;
				String transaction=inputmap.get(CommonConstants.AUDIT_TRANSACTION)!=null?
						(String)inputmap.get(CommonConstants.AUDIT_TRANSACTION):null;
				if(id!=null && StringUtils.isNotEmpty(transaction)) {
					transmitDesignDataService.saveDataInAuditTbl(id, CommonConstants.FAIL_STATUS, transaction);
				}		
				
			}});
		
	}
}
