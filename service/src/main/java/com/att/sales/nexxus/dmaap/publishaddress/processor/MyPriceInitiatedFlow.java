package com.att.sales.nexxus.dmaap.publishaddress.processor;



import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.apache.commons.collections.CollectionUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;

import com.att.sales.framework.exception.SalesBusinessException;
import com.att.sales.nexxus.common.CommonConstants;
import com.att.sales.nexxus.constant.StringConstants;
import com.att.sales.nexxus.dao.model.NxMpDeal;
import com.att.sales.nexxus.dao.model.solution.NxSolutionDetail;
import com.att.sales.nexxus.dao.repository.NxMpDealRepository;
import com.att.sales.nexxus.dao.repository.NxSolutionDetailsRepository;
import com.att.sales.nexxus.myprice.transaction.service.UpdateTxnSiteUploadServiceImpl;
import com.att.sales.nexxus.serviceValidation.service.GetQualificationServiceImpl;
import com.att.sales.nexxus.util.HttpRestClient;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import lombok.extern.slf4j.Slf4j;

/**
 * @author IndraSingh
 */

@Component
@Slf4j
public class MyPriceInitiatedFlow {
	
	/** The logger. */
	private static Logger logger = LoggerFactory.getLogger(MyPriceInitiatedFlow.class);

	@Autowired
	private Environment env;

	@Autowired
	private NxSolutionDetailsRepository repository;
	
	@Autowired
	private NxMpDealRepository nxMpDealRepository;

	@Autowired
	private ObjectMapper mapper;
	
	@Autowired
	private GetQualificationServiceImpl getQualificationServiceImpl;
	
	@Autowired
	private UpdateTxnSiteUploadServiceImpl updateTxnSiteUploadServiceImpl;
	
	@Autowired
	private HttpRestClient httpRestClient;
	
	/** The thread size. */
	@Value("${fmo.threadPool.size}")
	private Integer threadSize;
	
	@Value("${ipne.dmaap.mots.id:N}")
	private String ipneDmaapMotsId;
	
	@Value("${myprice.proxy.enabled}")
	private String isProxyEnabled;
	
	/**
	 * Gets the thread size.
	 *
	 * @return the thread size
	 */
	public Integer getThreadSize() {
		return threadSize;
	}
	
	/**
	 * Gets the excutor service.
	 *
	 * @return the excutor service
	 */
	protected ExecutorService getExcutorService() {
        return Executors.newFixedThreadPool(getThreadSize());
    }
	
	/**
	 * D map subscribe address processor through thread.
	 *
	 * @param messagesList the messages list
	 * @throws JsonProcessingException the json processing exception
	 * @throws SalesBusinessException the sales business exception
	 */
	public void dMapSubscribeAddressProcessorThroughThread(List<String> messagesList) throws JSONException, SalesBusinessException, JsonProcessingException{
		log.info("Service Log :: [Nexxus Info] :: dMapSubscribeAddressProcessor invoked");
		if(CollectionUtils.isNotEmpty(messagesList)) {
			Set<String> idSet=new HashSet<>();
			logger.info("Subscribe messages size : "+messagesList.size());
			List<Callable<Object>> taskList = new ArrayList<>();
			for(String message:messagesList) {
				logger.info("Subscribe message : "+message);
				JSONObject object = new JSONObject(message);
				if(object.has("motsId") && null != object.get("motsId") && ipneDmaapMotsId.equalsIgnoreCase(object.get("motsId").toString()) 
						&& object.has("sourceSystem") && null != object.get("sourceSystem") && "myPrice".equalsIgnoreCase(object.get("sourceSystem").toString()) 
						&& object.has("uniqueId") && null!=object.get("uniqueId")) {
					String uniqueId = (String) object.get("uniqueId");
					//to filter duplicate records
					if(idSet.add(uniqueId)) {
						MyPriceExecutorService mpExecutor=new MyPriceExecutorService(object);
						mpExecutor.setGetQualificationServiceImpl(getQualificationServiceImpl);
						mpExecutor.setMyPriceInitiatedFlow(this);
						mpExecutor.setNxMpDealRepository(nxMpDealRepository);
						mpExecutor.setUpdateTxnSiteUploadServiceImpl(updateTxnSiteUploadServiceImpl);
						taskList.add(mpExecutor);
					}
				}
				
			}
			if(CollectionUtils.isNotEmpty(taskList)) {
				this.processDmaapThroughExecutor(taskList);
			}
		}
		
		log.info("Service Log :: [Nexxus Info] :: dMapSubscribeAddressProcessor Destroyed");
	}
	
	/**
	 * Process dmaap through executor.
	 *
	 * @param taskList the task list
	 */
	protected void processDmaapThroughExecutor(List<Callable<Object>> taskList) {
		ExecutorService executor=getExcutorService();
		try {
			executor.invokeAll(taskList);
			executor.shutdown();
			log.error("Executor is shutdown");
			executor.awaitTermination(5, TimeUnit.SECONDS);
		} catch (InterruptedException e) {
			log.error("Exception from callQualDesignExucuterService {}",e);
		    Thread.currentThread().interrupt();
		}finally {
			if (!executor.isTerminated()) {
				log.error("cancel non-finished tasks");
		    }
		    executor.shutdownNow();
		    log.error("shutdown finished");
		}
		
	}

	

	protected String processSiteStatusUpdate(String transactionId, String tRequestJson) throws SalesBusinessException {
		log.info("Service Log :: [Nexxus Info] :: processSiteStatusUpdate Invoked");

		String proxy = null;
		if(StringConstants.CONSTANT_Y.equalsIgnoreCase(isProxyEnabled)) {
			proxy = env.getProperty(CommonConstants.CDP_HTTP_PROXY);
		}
		String siteStatusUpdateRequestURL = env.getProperty("myPrice.siteStatusUpdate").replace("{TransactionId}",
				String.valueOf(transactionId));
		Map<String, String> requestHeaders = new HashMap<String, String>();
		requestHeaders.put(StringConstants.REQUEST_AUTHORIZATION, "Basic "+env.getProperty(com.att.sales.nexxus.common.CommonConstants.MYPRICE_AUTHORIZATION));

		log.info("Created Request For Site Transfer Initiated ready to do Handshake: " + "\n" + tRequestJson);
		String serviceResponse = httpRestClient.callHttpRestClient(siteStatusUpdateRequestURL, HttpMethod.POST, null, tRequestJson, 
				requestHeaders, proxy);
		
		log.info("Service Log :: [Nexxus Info] :: processSiteStatusUpdate Destroyed");

		return serviceResponse;
	}

	protected String getupdateTransactionJSON(Map<Object, Object> triggerRequestMap) throws JsonProcessingException {

		mapper.enable(SerializationFeature.INDENT_OUTPUT);
		String documentDJSON;
		documentDJSON = mapper.writeValueAsString(triggerRequestMap);

		return documentDJSON;
	}

	protected NxMpDeal createEntry(String transactionId) {
		Date date = new Date();
		NxSolutionDetail solnData = new NxSolutionDetail();
		solnData.setCreatedDate(date);
		solnData.setModifiedDate(date);
		solnData.setActiveYn(com.att.sales.nexxus.common.CommonConstants.ACTIVE_Y);
		solnData.setNxsDescription("[AUTOMATION:" + StringConstants.FLOW_TYPE_IPnE + "]");
		solnData.setFlowType(StringConstants.FLOW_TYPE_IPnE);
		repository.save(solnData);
		
		log.info("Service Log :: [Nexxus Info] :: createEntryNxMpDeal Invoked");
		NxMpDeal nxMpDeal = new NxMpDeal();
		nxMpDeal.setTransactionId(transactionId);
		nxMpDeal.setCreatedDate(new Date());
		nxMpDeal.setModifiedDate(new Date());
		nxMpDeal.setDealStatus(com.att.sales.nexxus.common.CommonConstants.CREATED);
		nxMpDeal.setActiveYN(com.att.sales.nexxus.common.CommonConstants.ACTIVE_Y);
		nxMpDeal.setSolutionId(solnData.getNxSolutionId());
		nxMpDeal.setNxMpStatusInd(StringConstants.CONSTANT_Y);
		nxMpDealRepository.save(nxMpDeal);
		log.info("Service Log :: [Nexxus Info] :: createEntryNxMpDeal Destroyed");
		return nxMpDeal;
	}

}
