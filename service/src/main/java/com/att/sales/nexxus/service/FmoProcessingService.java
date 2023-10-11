package com.att.sales.nexxus.service;

import java.sql.Blob;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.TimeZone;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.IntRange;
import org.json.JSONException;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.att.sales.framework.exception.SalesBusinessException;
import com.att.sales.nexxus.accesspricing.model.AccessPriceUIdetails;
import com.att.sales.nexxus.accesspricing.model.AccessPricingAQ;
import com.att.sales.nexxus.common.MessageConstants;
import com.att.sales.nexxus.constant.CommonConstants;
import com.att.sales.nexxus.constant.CommonConstants.STATUS_CONSTANTS;
import com.att.sales.nexxus.constant.FmoConstants;
import com.att.sales.nexxus.constant.MyPriceConstants;
import com.att.sales.nexxus.constant.StringConstants;
import com.att.sales.nexxus.dao.model.FmoJsonRulesModel;
import com.att.sales.nexxus.dao.model.FmoOfferJsonRulesMapping;
import com.att.sales.nexxus.dao.model.NxDesignAudit;
import com.att.sales.nexxus.dao.model.NxMpDeal;
import com.att.sales.nexxus.dao.model.NxOutputFileModel;
import com.att.sales.nexxus.dao.model.NxRequestDetails;
import com.att.sales.nexxus.dao.model.NxSolutionSite;
import com.att.sales.nexxus.dao.model.solution.NxSolutionDetail;
import com.att.sales.nexxus.dao.repository.NxDesignAuditRepository;
import com.att.sales.nexxus.dao.repository.NxMpDealRepository;
import com.att.sales.nexxus.dao.repository.NxRequestDetailsRepository;
import com.att.sales.nexxus.dao.repository.NxRequestGroupRepository;
import com.att.sales.nexxus.dao.repository.NxSolutionSiteRepository;
import com.att.sales.nexxus.handlers.GetOptyInfoWSHandler;
import com.att.sales.nexxus.helper.FmoLookUpDataHelper;
import com.att.sales.nexxus.model.CircuitSiteDetails;
import com.att.sales.nexxus.model.MailRequest;
import com.att.sales.nexxus.myprice.transaction.service.MyPriceTransactionUtil;
import com.att.sales.nexxus.output.entity.NxOutputBean;
import com.att.sales.nexxus.reteriveicb.model.ComponentDetails;
import com.att.sales.nexxus.reteriveicb.model.Offer;
import com.att.sales.nexxus.reteriveicb.model.Port;
import com.att.sales.nexxus.reteriveicb.model.PriceAttributes;
import com.att.sales.nexxus.reteriveicb.model.PriceDetails;
import com.att.sales.nexxus.reteriveicb.model.PricePlanDetails;
import com.att.sales.nexxus.reteriveicb.model.RetreiveICBPSPRequest;
import com.att.sales.nexxus.reteriveicb.model.Site;
import com.att.sales.nexxus.reteriveicb.model.Solution;
import com.att.sales.nexxus.util.JacksonUtil;
import com.att.sales.nexxus.util.JsonPathExpressionBuilder;
import com.att.sales.nexxus.util.JsonPathUtil;
import com.att.sales.nexxus.util.NexxusJsonUtility;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.jayway.jsonpath.TypeRef;



/**
 * The Class FmoProcessingService.
 *
 * @author vt393d
 */
@Component
public class FmoProcessingService {
	
	/** The logger. */
	private static Logger logger = LoggerFactory.getLogger(FmoProcessingService.class);
	
	@Autowired
	private NxSolutionSiteRepository nxSolutionSiteRepository;
	
	@Autowired
	private InrQualifyService inrQualifyService;
	
	@Autowired
	private NxRequestGroupRepository nxRequestGroupRepository;
	
	@Autowired
	private NxRequestDetailsRepository nxRequestDetailsRepository;
	
	/** The json path util. */
	@Autowired
	private JsonPathUtil jsonPathUtil;
	
	/** The line item service. */
	@Autowired
	private LineItemProcessingService lineItemService;
	
	/** The nexus json utility. */
	@Autowired
	private NexxusJsonUtility nexusJsonUtility;
	
	/** The report service. */
	@Autowired
	private ReportService reportService;
	
	/** The get opty info WS handler. */
	@Autowired 
	private GetOptyInfoWSHandler getOptyInfoWSHandler;
	
	/** The mail service. */
	@Autowired
	private MailServiceImpl mailService;
	
	/** The repository service. */
	@Autowired
	private FmoProcessingRepoService repositoryService;
	
	/** The fmo look up data helper. */
	@Autowired
	private FmoLookUpDataHelper  fmoLookUpDataHelper;
	
	/** The price type data map. */
	private ConcurrentMap<String,String> priceTypeDataMap=null;
	
	@Autowired
	private SubmitToMyPriceService submitToMyPriceService;
	
	@Autowired
	private MyPriceTransactionUtil myPriceTransactionUtil;
	
	@Autowired
	private NxMpDealRepository nxMpDealRepository;
	
	@Autowired
	private NxDesignAuditRepository nxDesignAuditRepository;
	
	/** The thread size. */
	@Value("${fmo.threadPool.size}")
	private Integer threadSize;
	
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
	 * Gets the price type data map.
	 *
	 * @return the price type data map
	 */
	protected ConcurrentMap<String, String> getPriceTypeDataMap() {
		return priceTypeDataMap;
	}


	/** The Constant COMPONENT_ID_PATH. */
	private  static final String COMPONENT_ID_PATH="$..component.[?(@.componentCodeId==10 || "
			+ "@.componentCodeType=='Connection')].componentId";
	
	private  static final String BVOIP_COMPONENT_ID_PATH="$..component.[?(@.componentCodeId==1320)].componentId";
	
	
	
	
	/**
	 * Creates the fmo nexxus output.
	 *
	 * @param reqDetails the req details
	 * @param inputJson the input json
	 * @param requestMap the request map
	 */
	@SuppressWarnings("unchecked")
	public void createFmoNexxusOutput(NxRequestDetails reqDetails,RetreiveICBPSPRequest inputJson,
			Map<String, Object> requestMap)  {
		if(null!=reqDetails && null!=inputJson) {
			logger.info("Inside createFmoNexusOutput method for request id: {}",reqDetails.getNxReqId());
			//this.callOptyInfo(requestMap);
			JSONObject intermediateJson=null;
			NxOutputBean nxOutputJson=null;
			Map<String,Set<String>> falloutDataMap=null;
			Blob outputFile=null;
			NxOutputFileModel model = new NxOutputFileModel();
			model.setNxRequestDetails(reqDetails);
			model.setCreatedDate(new Timestamp(System.currentTimeMillis()));
			model.setStatus(CommonConstants.INPROGRESS);
			Map<String, Object> paramMap=new HashMap<String, Object>();
			try {
				Map<Long,Map<String,List<FmoJsonRulesModel>>> dataMap=this.getFmoRules(inputJson.getSolution());
				if(MapUtils.isNotEmpty(dataMap)) {
					//load priceType data from cache
					this.loadPriceTypeData();
					//create intermediate JSON
					intermediateJson =this.createResultJson(inputJson,dataMap);
					JacksonUtil.cleanJSON(intermediateJson);
					model.setIntermediateJson(null!=intermediateJson?intermediateJson.toJSONString():null);
					
					//create outputFile JSon
					nxOutputJson=lineItemService.getLineItemData(intermediateJson,paramMap);
					model.setOutput(nxOutputJson);
					
					model.setMpOutputJson(null!=intermediateJson?intermediateJson.toJSONString().replace("\\/", "/"):null);
					
					falloutDataMap=(Map<String, Set<String>>) paramMap.get(FmoConstants.FALLOUT_MAP);
					String fallOutData=nexusJsonUtility.convertMapToJson(falloutDataMap);
					model.setFallOutData(fallOutData);
					
					//create output file BLOG object
					outputFile = reportService.generateReport(nxOutputJson);
					//model.setOutputFile(outputFile);
					//setting fileName and fileType
					if(null!=outputFile) {
						model.setOutputFile(outputFile.getBytes(1,(int) outputFile.length()));
						model.setFileName(this.createFileName(reqDetails.getNxReqId()));
						model.setFileType(FmoConstants.FILE_TYPE);
					}
					//setting status
					this.setStatus(model,intermediateJson,nxOutputJson,outputFile,paramMap);
				}
			} catch (Exception  e) {
				model.setStatus(CommonConstants.FAIL);
				logger.error("createFmoNexusOutput Exception {}", e);
			}
			//set status in nexxus request details
			setStatusForNxRequestDetails(reqDetails, model.getStatus());
			repositoryService.saveNxOutputFile(model);
          
          //commenting below line inorder to restrict mail functionality
			if(CommonConstants.SUCCESS_WITH_FALLOUT.equalsIgnoreCase(model.getStatus()) ||
					CommonConstants.FALLOUT.equalsIgnoreCase(model.getStatus()) ||
					CommonConstants.FAIL.equalsIgnoreCase(model.getStatus())){
				this.sendMailNotification(reqDetails);
				this.sendDmaapForFailed(requestMap, reqDetails);
			}
			
			requestMap.put(FmoConstants.ERATE_IND, inputJson.getSolution().getErateInd());
			try {
				submitToMyPriceService.submitFMOToMyPrice(reqDetails.getNxSolutionDetail(), reqDetails, model, requestMap);
			} catch (JsonProcessingException | JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	


	/**
	 * Call opty info.
	 *
	 * @param requestMap the request map
	 */
	private void callOptyInfo(Map<String, Object> requestMap) {
		logger.info("Inside callOptyInfo method  {}","");
		if(requestMap.containsKey(FmoConstants.CALL_OPTYINFO) && 
				FmoConstants.YES.equalsIgnoreCase(requestMap.get(FmoConstants.CALL_OPTYINFO).toString())) {
			try {
				getOptyInfoWSHandler.initiateGetOptyInfoWebService(requestMap);
			} catch (SalesBusinessException e) {
				logger.error("Exception during initiateGetOptyInfoWebService in createFmoNexusOutput.",e);
			}
		}
	}
	

	/**
	 * Send mail notification.
	 *
	 * @param reqDetails the req details
	 */
	private void sendMailNotification(NxRequestDetails reqDetails) {
		logger.info("Inside sendMailNotification method  {}", "");
		if (reqDetails.getNxReqId() != null) {
			MailRequest request = new MailRequest();
			request.setNxRequestId(reqDetails.getNxReqId());
			try {
				mailService.mailNotificationFMO(request);
			} catch (SalesBusinessException e) {
				logger.error("EXCEPTION IN CALLING MAIL API >>" + e);
			}
		}
	}
	
	protected void sendDmaapForFailed(Map<String, Object> requestMap,NxRequestDetails reqDetails) {
		if (!(reqDetails.getStatus() != null && STATUS_CONSTANTS.SUCCESS.getValue()==reqDetails.getStatus())) {
			if(requestMap.containsKey(MyPriceConstants.NX_TRANSACTION_ID) && requestMap.get(MyPriceConstants.NX_TRANSACTION_ID)!=null) {
				Long nxTxnId=(Long)requestMap.get(MyPriceConstants.NX_TRANSACTION_ID);
				NxMpDeal deal = nxMpDealRepository.findByNxTxnId(nxTxnId);
				Map<String, Object> map = new HashMap<String, Object>();
				map.put(MyPriceConstants.RESPONSE_STATUS, false);
				this.setStausinNxAuditTbl(requestMap, reqDetails);
				if(reqDetails.getStatus().equals(STATUS_CONSTANTS.SUCCESS_WITH_FALLOUT.getValue()) 
						|| reqDetails.getStatus().equals(STATUS_CONSTANTS.FALLOUT.getValue())){
					map.put(MyPriceConstants.RESPONSE_CODE, FmoConstants.FMO_LINE_ITEM_PROCESSING_ERROR_CD.get(reqDetails.getStatus()));
					map.put(MyPriceConstants.RESPONSE_MSG, FmoConstants.FMO_LINE_ITEM_PROCESSING_ERROR_MSG.get(reqDetails.getStatus()));
					myPriceTransactionUtil.sendDmaapEvents(deal, reqDetails.getNxSolutionDetail(), 
							com.att.sales.nexxus.common.CommonConstants.FAILED,map);
				}else {
					myPriceTransactionUtil.sendDmaapEvents(deal, reqDetails.getNxSolutionDetail(),
							com.att.sales.nexxus.common.CommonConstants.FAILED);
				}
				
			}
		}
		
		
	}
	
	protected void setStausinNxAuditTbl(Map<String, Object> requestMap,NxRequestDetails reqDetails) {
		if(requestMap.containsKey(MyPriceConstants.NX_AUDIT_ID) && requestMap.get(MyPriceConstants.NX_AUDIT_ID)!=null) {
			Long nxAuditId=(Long)requestMap.get(MyPriceConstants.NX_AUDIT_ID);
			NxDesignAudit nxDesignAudit = nxDesignAuditRepository.findByNxAuditId(nxAuditId);
			if(null!=nxDesignAudit) {
				nxDesignAuditRepository.updateStatusByNxAuditId(FmoConstants.FMO_LINE_ITEM_PROCESSING_ERROR_MSG.get(reqDetails.getStatus()), 
						MyPriceConstants.LINE_ITEM_PROCESSING, new Date(), nxAuditId);
			}
		}
	}
	
	/**
	 * Sets the status for nx request details.
	 *
	 * @param reqDetails the req details
	 * @param status the status
	 */
	protected void setStatusForNxRequestDetails(NxRequestDetails reqDetails,String status) {
		if(status.equals(CommonConstants.SUCCESS)) {
			reqDetails.setStatus(STATUS_CONSTANTS.SUCCESS.getValue());
		}else if(status.equals(CommonConstants.SUCCESS_WITH_FALLOUT)) {
			reqDetails.setStatus(STATUS_CONSTANTS.SUCCESS_WITH_FALLOUT.getValue());
		}else if(status.equals(CommonConstants.FALLOUT)) {
			reqDetails.setStatus(STATUS_CONSTANTS.FALLOUT.getValue());
		}else if(status.equals(CommonConstants.FAIL)) {
			reqDetails.setStatus(STATUS_CONSTANTS.ERROR.getValue());
		}
		
		reqDetails.setModifedDate(new Date());
		
		logger.info("Inside setStatusForNxRequestDetails method for setting "
				+ "status code {} for request id: {}",reqDetails.getStatus(),reqDetails.getNxReqId());
		repositoryService.saveNxRequestDetails(reqDetails);
	}
	
	/**
	 * Creates the file name.
	 *
	 * @param id the id
	 * @return the string
	 */
	protected String createFileName(Long id) {
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss'.xlsx'");
		dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
		return  id+CommonConstants.OUTPUT_FILE_NAME+ dateFormat
				.format(new Date());
	}
	
	/**
	 * Sets the success status.
	 *
	 * @param model the model
	 * @param intermediateJson the intermediate json
	 * @param nxOutputBean the nx output bean
	 * @param outputFile the output file
	 */
	/*protected void setStatus(NxOutputFileModel model,JSONObject intermediateJson,
			NxOutputBean nxOutputBean,Blob outputFile) {
		model.setStatus(CommonConstants.SUCCESS);
		if(intermediateJson==null || StringUtils.isEmpty(intermediateJson.toJSONString()) || 
				(null!=nxOutputBean && null==outputFile) ||
				null==nxOutputBean && (StringUtils.isEmpty(model.getFallOutData()))) {
			model.setStatus(CommonConstants.FAIL);
		}else if(null==nxOutputBean && StringUtils.isNotEmpty(model.getFallOutData())){
			model.setStatus(CommonConstants.FALLOUT);
		}else if(StringUtils.isNotEmpty(model.getFallOutData()) && model.getStatus().equals(CommonConstants.SUCCESS)){
			model.setStatus(CommonConstants.SUCCESS_WITH_FALLOUT);
		}
	}*/
	
	protected void setStatus(NxOutputFileModel model,JSONObject intermediateJson,
			NxOutputBean nxOutputBean,Blob outputFile,Map<String, Object> paramMap) {
		model.setStatus(CommonConstants.SUCCESS);
		if((intermediateJson==null || StringUtils.isEmpty(intermediateJson.toJSONString())) || StringUtils.isEmpty(model.getMpOutputJson())) {
			model.setStatus(CommonConstants.FAIL);
		}else if(paramMap.get(FmoConstants.IS_COMPLETE_FALLOUT)!=null && (Boolean)paramMap.get(FmoConstants.IS_COMPLETE_FALLOUT)){
			model.setStatus(CommonConstants.FALLOUT);
		}else if(StringUtils.isNotEmpty(model.getFallOutData()) && model.getStatus().equals(CommonConstants.SUCCESS)){
			model.setStatus(CommonConstants.SUCCESS_WITH_FALLOUT);
		}
	}
	
	/**
	 * Gets the fmo rules.
	 *
	 * @param solution the solution
	 * @return Map of FMO JSON rules
	 */
	protected Map<Long,Map<String,List<FmoJsonRulesModel>>> getFmoRules(Solution solution){
		Set<Long> allOfferIds=this.getAllOfferIds(solution.getOffers());
		Map<Long,Map<String,List<FmoJsonRulesModel>>> dataMap=new HashMap<>();
		if(CollectionUtils.isNotEmpty(allOfferIds)) {
			List<FmoOfferJsonRulesMapping> resultLst=repositoryService.getFmoRulesFromTbl(allOfferIds);
			if(CollectionUtils.isNotEmpty(resultLst)) {
				Optional.ofNullable(resultLst).map(List::stream).orElse(Stream.empty()).filter(Objects::nonNull).
				forEach( data -> {
					if(!dataMap.containsKey(data.getOfferId())) {
						dataMap.put(data.getOfferId(), new HashMap<String,List<FmoJsonRulesModel>>());
					}
					Map<String,List<FmoJsonRulesModel>> itemWiseMap=dataMap.get(data.getOfferId());
					if(!itemWiseMap.containsKey(data.getFmoRules().getItemName())) {
						itemWiseMap.put(data.getFmoRules().getItemName(), new ArrayList<FmoJsonRulesModel>());
					}
					itemWiseMap.get(data.getFmoRules().getItemName()).add(data.getFmoRules());
					dataMap.put(data.getOfferId(), itemWiseMap);
				});
			}
		}
		
		return dataMap;
	}
	
	
	/**
	 * Creates the result json.
	 *
	 * @param inputJson the input json
	 * @param dataMap the data map
	 * @return JSONObject
	 * 
	 * This method use to create  filter Json from main base request json
	 */
	@SuppressWarnings("unchecked")
	protected  JSONObject createResultJson(RetreiveICBPSPRequest inputJson,Map<Long,Map<String,
			List<FmoJsonRulesModel>>> dataMap) {
		JSONObject jsonObject = createNewJSONObject();
		jsonObject.put(FmoConstants.SOLUTION_ATR, createSolution(inputJson.getSolution(),dataMap));
		return jsonObject;
	}
	
	/**
	 * Creates the solution.
	 *
	 * @param solution the solution
	 * @param dataMap the data map
	 * @return JSONObject
	 * 
	 * This method use to create Solution block of  filter JSON from main base request JSON
	 */
	@SuppressWarnings("unchecked")
	protected  JSONObject createSolution(Solution solution,Map<Long,Map<String,List<FmoJsonRulesModel>>> dataMap) {
		JSONObject jsonObject = createNewJSONObject();
		List<FmoJsonRulesModel> solutionLst=dataMap.get(FmoConstants.TEMP_OFFER_ID)!=null?
				dataMap.get(FmoConstants.TEMP_OFFER_ID).get(FmoConstants.SOLUTION_TAG):new ArrayList<>();
		Optional.ofNullable(solutionLst).map(List::stream).orElse(Stream.empty()).
		filter(item-> item!=null && StringUtils.isNotEmpty(item.getUdfQuery())).forEach( item -> 
			jsonObject.put(item.getFieldNameJson(), this.getData(nexusJsonUtility.getValue(solution, 
					item.getUdfQuery()), null,item.getUdfId(),item.getComponentId(),item.getDataSetname(),
					item.getDefaultValue()))
		);
		jsonObject.put(FmoConstants.OFFER_ATR, createOffers(solution.getOffers(),dataMap,
				solution.getPricePlanDetails(),solution.getExternalKey()));
		return jsonObject;
	}
	
	
	
	/**
	 * Creates the offers.
	 *
	 * @param offers the offers
	 * @param dataMap the data map
	 * @param pricePlanDetails the price plan details
	 * @param externalKey the external key
	 * @return JSONArray
	 * 
	 * This method use to create offer block of  filter JSON from main base request JSON
	 */
	@SuppressWarnings("unchecked")
	protected  JSONArray  createOffers(List<Offer> offers,Map<Long,Map<String,List<FmoJsonRulesModel>>> dataMap,
			List<PricePlanDetails> pricePlanDetails,Long externalKey) {
		JSONArray offer = new JSONArray();
		Map<String,Object> methodInputMap=new HashMap<>();
		Optional.ofNullable(offers).map(List::stream).orElse(Stream.empty()).filter(Objects::nonNull).
		forEach( data -> {
			Long offerId=Long.valueOf(data.getOfferId());
			methodInputMap.put(FmoConstants.OFFER_ID, offerId);
			Map<String,List<FmoJsonRulesModel>> itemMap=dataMap.get(offerId);
			if(null!=itemMap) {
				List<FmoJsonRulesModel> offerFields=itemMap.get(FmoConstants.OFFER_TAG);
				JSONObject jsonObject = createNewJSONObject();
				Optional.ofNullable(offerFields).map(List::stream).orElse(Stream.empty()).
				filter(item-> item!=null && StringUtils.isNotEmpty(item.getUdfQuery())).forEach( item -> 
					jsonObject.put(item.getFieldNameJson(), this.getData(nexusJsonUtility.getValue(data, 
							item.getUdfQuery()), offerId, item.getUdfId(),item.getComponentId(),item.getDataSetname(),
							item.getDefaultValue()))
				);
				jsonObject.put(FmoConstants.EXTERNAL_KEY, this.getData(externalKey,offerId,null,null,FmoConstants.NOT_AVAILABLE,null));
				jsonObject.put(FmoConstants.SITE_ATR, createSite(data.getSite(),itemMap,pricePlanDetails,methodInputMap,offerId));
				offer.add(jsonObject);
			}
			
		});
		return offer;
	}
	
	
	
	/**
	 * Creates the site.
	 *
	 * @param sites the sites
	 * @param itemMap the item map
	 * @param pricePlanDetails the price plan details
	 * @param methodInputMap the method input map
	 * @param offerId the offer id
	 * @return JSONArray
	 * 
	 * This method use to create Site block of  filter JSON from main base request JSON
	 */
	
	protected  JSONArray createSite(List<Site> sites,Map<String,List<FmoJsonRulesModel>> itemMap,
			List<PricePlanDetails> pricePlanDetails,Map<String,Object> methodInputMap,Long offerId) {
		JSONArray site = new JSONArray();
		List<Callable<Object>> taskList = new ArrayList<>();
		Optional.ofNullable(sites).map(List::stream).orElse(Stream.empty()).filter(Objects::nonNull).
		forEach( data -> {
			FmoExecutorService executorObj=new FmoExecutorService(data);
			executorObj.setFmoProcessingService(this);
			executorObj.setItemMap(itemMap);
			executorObj.setMethodInputMap(methodInputMap);
			executorObj.setNexusJsonUtility(nexusJsonUtility);
			executorObj.setOfferId(offerId);
			executorObj.setPricePlanDetails(pricePlanDetails);
			taskList.add(executorObj);
		});
		if(CollectionUtils.isNotEmpty(taskList)) {
			site=this.getSiteDataThroughExecutor(taskList);
		}
		return site;
	}
	
	/**
	 * Gets the site data through executor.
	 *
	 * @param taskList the task list
	 * @return the site data through executor
	 */
	@SuppressWarnings("unchecked")
	protected JSONArray getSiteDataThroughExecutor(List<Callable<Object>> taskList) {
		JSONArray siteArray = new JSONArray();
		ExecutorService executor=getExcutorService();
		try {
			List<Future<Object>> resultLst = executor.invokeAll(taskList);
			for (Future<Object> sitedata : resultLst) {
				if(null!=sitedata && null!=sitedata.get()){
					JSONObject site = (JSONObject)sitedata.get();
					 siteArray.add(site);
				} 
			}
			executor.shutdown();
			logger.error("Executor is shutdown");
			executor.awaitTermination(5, TimeUnit.SECONDS);
		} catch (InterruptedException |ExecutionException e) {
			logger.error("Exception from callQualDesignExucuterService {}",e);
		    Thread.currentThread().interrupt();
		}finally {
			if (!executor.isTerminated()) {
				logger.error("cancel non-finished tasks");
		    }
		    executor.shutdownNow();
		    logger.error("shutdown finished");
		}
		
		return siteArray;
		
	}
	
	/**
	 * Creates the design.
	 *
	 * @param designSiteOfferPort the design site offer port
	 * @param priceDetails the price details
	 * @param itemMap the item map
	 * @param pricePlanDetailsObj the price plan details obj
	 * @param siteId the site id
	 * @param offerId the offer id
	 * @return JSONArray
	 * 
	 * This method use to create Design block of  filter JSON from main base request JSON
	 */
	@SuppressWarnings("unchecked")
	protected  JSONArray createDesign(List<Port> designSiteOfferPort,PriceDetails priceDetails,Map<String,
			List<FmoJsonRulesModel>> itemMap,PricePlanDetails pricePlanDetailsObj,Long siteId,Long offerId) {
		JSONArray design = new JSONArray();
		Optional.ofNullable(designSiteOfferPort).map(List::stream).orElse(Stream.empty()).
		filter(Objects::nonNull).forEach( data -> {
			List<FmoJsonRulesModel> designFields=itemMap.get(FmoConstants.DESIGN_TAG);
			Long componentId=null;
			if(FmoConstants.BVOIP_OFFER_ID==offerId) {
				componentId=getCompoenetId(data,BVOIP_COMPONENT_ID_PATH);
			}else {
				componentId=getCompoenetId(data,COMPONENT_ID_PATH);
			}
			
			JSONObject jsonObject = createNewJSONObject();
			Optional.ofNullable(designFields).map(List::stream).orElse(Stream.empty()).
			filter(item-> item!=null && StringUtils.isNotEmpty(item.getUdfQuery())).forEach( item -> 
				jsonObject.put(item.getFieldNameJson(), this.getData(nexusJsonUtility.getValue(data, 
						item.getUdfQuery()), offerId, item.getUdfId(), item.getComponentId(),item.getDataSetname(),
						item.getDefaultValue()))
			);
			//setting accessDetails tag
			if(CollectionUtils.isNotEmpty(itemMap.get(FmoConstants.ACCESS_DETAILS_TAG))) {
				String accessDetailsPath=new JsonPathExpressionBuilder().arraynode(FmoConstants.ACCESS_PRICE_UI_DETAILS).
						select().where(FmoConstants.PORT_ID).is(componentId).build();
				AccessPriceUIdetails baseAccessPriceBlock=this.accessPricingByPortId(data.getAccessPricingAQ(),accessDetailsPath);
				jsonObject.put(FmoConstants.ACCESS_DETAILS_ATR, createAccessDetails(baseAccessPriceBlock,itemMap,offerId));
			}
			
			jsonObject.put(FmoConstants.REF_SITE_ID, this.getData(siteId, null, null, null,FmoConstants.NOT_AVAILABLE,null));
			ComponentDetails componentDetails=getComponentDetailByCompId(priceDetails, componentId);
			/*String priceDetailsPath=new JsonPathExpressionBuilder().arraynode().select().where(FmoConstants.COMPONENT_ID).
					is(componentId).arraynode(FmoConstants.PRICE_ATTRIBUTE).select(FmoConstants.ALL).build();
			List<PriceAttributes> priceDetailsObj=getPriceAttributes(priceDetails.getComponentDetails(), priceDetailsPath);*/
			jsonObject.put(FmoConstants.PRICE_DETAILS_ATR, 
					createPriceDetails(componentDetails,itemMap,pricePlanDetailsObj,componentId,offerId));
			design.add(jsonObject);
		});
		return design;
	}
	
	protected ComponentDetails getComponentDetailByCompId(PriceDetails priceDetails,Long componenetId) {
		String path="$..componentDetails.[?(@.componentId=="+componenetId+" )]";
		ComponentDetails result=new ComponentDetails();
		TypeRef<List<ComponentDetails>> mapType = new TypeRef<List<ComponentDetails>>() {};
		List<ComponentDetails> data=jsonPathUtil.search(priceDetails, path,mapType);
		if(CollectionUtils.isNotEmpty(data)) {
			return data.get(0);
		}
		return result;
	}
	
	
	/**
	 * Access pricing by port id.
	 *
	 * @param accessPricingAQ the access pricing AQ
	 * @param path the path
	 * @return AccessPriceUIdetails
	 * 
	 * This method use to get AccessPriceUIdetails block  from main base request JSON on basis on portId
	 */
	protected AccessPriceUIdetails accessPricingByPortId(AccessPricingAQ accessPricingAQ,String path) {
		AccessPriceUIdetails result=new AccessPriceUIdetails();
		TypeRef<List<AccessPriceUIdetails>> mapType = new TypeRef<List<AccessPriceUIdetails>>() {};
		List<AccessPriceUIdetails> data=jsonPathUtil.search(accessPricingAQ, path,mapType);
		if(CollectionUtils.isNotEmpty(data)) {
			return data.get(0);
		}
		return result;
	}
	
	/**
	 * Creates the access details.
	 *
	 * @param accessPricing the access pricing
	 * @param itemMap the item map
	 * @param offerId the offer id
	 * @return JSONObject
	 * 
	 * This method use to create Access Design block of  filter JSON from main base request JSON
	 */
	@SuppressWarnings("unchecked")
	protected JSONObject createAccessDetails(AccessPriceUIdetails accessPricing,
			Map<String,List<FmoJsonRulesModel>> itemMap,Long offerId) {
		JSONObject jsonObject = createNewJSONObject();
		List<FmoJsonRulesModel> accessDtlsLst=itemMap.get(FmoConstants.ACCESS_DETAILS_TAG);
		Optional.ofNullable(accessDtlsLst).map(List::stream).orElse(Stream.empty()).
		filter(item-> item!=null && StringUtils.isNotEmpty(item.getUdfQuery())).forEach( item -> 
			jsonObject.put(item.getFieldNameJson(),this.getData(nexusJsonUtility.getValue(accessPricing, 
					item.getUdfQuery()), offerId,item.getUdfId(),item.getComponentId(),item.getDataSetname(),
					item.getDefaultValue()))
		);
		return jsonObject;
	}
	
	
	
	
	/**
	 * Creates the price details.
	 *
	 * @param priceDetailsLst the price details lst
	 * @param itemMap the item map
	 * @param pricePlanDetailsObj the price plan details obj
	 * @param portId the port id
	 * @param offerId the offer id
	 * @return JSONArray
	 * 
	 * This method use to create PriceDetails block of  filter JSON from main base request JSON
	 */
	@SuppressWarnings("unchecked")
	protected  JSONArray createPriceDetails(ComponentDetails componentDetails,
			Map<String,List<FmoJsonRulesModel>> itemMap,PricePlanDetails pricePlanDetailsObj,
			Long portId,Long offerId) {
		JSONArray priceDetails = new JSONArray();
		List<PriceAttributes> priceDetailsLst=componentDetails.getPriceAttributes();
		Optional.ofNullable(priceDetailsLst).map(List::stream).orElse(Stream.empty()).
		filter(Objects::nonNull).forEach( data -> {
			List<FmoJsonRulesModel> priceDetailsFields=itemMap.get(FmoConstants.PRICE_DETAIL_TAGS);
			JSONObject jsonObject = createNewJSONObject();
			Optional.ofNullable(priceDetailsFields).map(List::stream).orElse(Stream.empty()).
			filter(item-> item!=null && StringUtils.isNotEmpty(item.getUdfQuery())).forEach( item -> 
				jsonObject.put(item.getFieldNameJson(), this.getData(nexusJsonUtility.getValue(data, 
						item.getUdfQuery()),offerId,item.getUdfId(),item.getComponentId(),item.getDataSetname(),item.getDefaultValue()))
			);
			jsonObject.put(FmoConstants.COUNTRY_CD, this.getData(pricePlanDetailsObj.getCountryCd(), 
					null, null, null,FmoConstants.NOT_AVAILABLE,null));
			jsonObject.put(FmoConstants.TERM, this.getData(pricePlanDetailsObj.getTerm(), null,
					null, null,FmoConstants.NOT_AVAILABLE,null));
			Double icbDesiredDisc= getIcbDesiredDiscount(jsonObject, pricePlanDetailsObj);
			jsonObject.put(FmoConstants.ICB_DESIRED_DISCOUNT,this.getData(icbDesiredDisc, null,
					null, null,FmoConstants.NOT_AVAILABLE,null));
			jsonObject.put(FmoConstants.REF_PORT_ID,portId);
			jsonObject.put(FmoConstants.COMP_PARENT_ID,componentDetails.getComponentParentId());
			jsonObject.put(FmoConstants.COMP_TYPE,componentDetails.getComponentType());
			priceDetails.add(jsonObject);
		});
		return priceDetails;
	}
	
	
	
	/**
	 * Creates the new JSON object.
	 *
	 * @return the JSON object
	 */
	protected JSONObject createNewJSONObject() {
		return new JSONObject();
	}
	
	
	/**
	 * Gets the country code.
	 *
	 * @param jsonObject the json object
	 * @return the country code
	 */
	protected String getCountryCode(JSONObject jsonObject) {
		if(null!=jsonObject && jsonObject.containsKey(FmoConstants.COUNTRY_CD)) {
			return (String)jsonObject.get(FmoConstants.COUNTRY_CD);
		}
		return null;
	}
	
	/**
	 * Gets the price plan details.
	 *
	 * @param pricePlanDetailsLst the price plan details lst
	 * @param methodInputMap the method input map
	 * @return the price plan details
	 */
	protected PricePlanDetails getPricePlanDetails(List<PricePlanDetails> pricePlanDetailsLst,
			Map<String,Object> methodInputMap) {
		PricePlanDetails result=new PricePlanDetails();
		String offerId=methodInputMap.get(FmoConstants.OFFER_ID).toString();
		String countryCd=(String)methodInputMap.get(FmoConstants.COUNTRY_CD);
		for(PricePlanDetails pricePlanDetails:pricePlanDetailsLst) {
			if(pricePlanDetails.getOfferId().equals(offerId) 
					&& pricePlanDetails.getCountryCd().equals(countryCd)) {
				return pricePlanDetails;
			}
		}
		return result;
	}
	
	
	
	/**
	 * Gets the icb desired discount.
	 *
	 * @param jsonObject the json object
	 * @param pricePlanDetailsObj the price plan details obj
	 * @return the icb desired discount
	 */
	protected Double getIcbDesiredDiscount(JSONObject jsonObject,PricePlanDetails pricePlanDetailsObj) {
		if(null!=jsonObject && null!=jsonObject.get(FmoConstants.PRODUCT_RATE_ID)) {
			Object productRateId=jsonObject.get(FmoConstants.PRODUCT_RATE_ID);
			String path=new JsonPathExpressionBuilder().arraynode(FmoConstants.DISCOUNT_DETAILS).select().
					where(FmoConstants.BEID).is(productRateId).objectnode(FmoConstants.ICB_DESIRED_DISCOUNT).build();
			TypeRef<List<Double>> mapType = new TypeRef<List<Double>>() {};
			List<Double> data=jsonPathUtil.search(pricePlanDetailsObj,path,mapType);
			if(CollectionUtils.isNotEmpty(data) && null!=data.get(0)) {
				return data.get(0);
			}
		}
		return 0.0d;
	}
	
	/**
	 * Gets the all offer ids.
	 *
	 * @param offers the offers
	 * @return This method use to get all offerIds from main base request JSON
	 */
	protected Set<Long> getAllOfferIds(List<Offer> offers){
		String path=new JsonPathExpressionBuilder().objectnode().objectnode(FmoConstants.OFFER_ID).build();
		Set<Long> allOfferIdSet=null;
		TypeRef<List<Long>> mapType = new TypeRef<List<Long>>() {};
		List<Long> allOfferId=jsonPathUtil.search(offers, path,mapType);
		if(CollectionUtils.isNotEmpty(allOfferId)) {
			allOfferId.removeIf(Objects::isNull);
			//adding 0 as Temp offerId for accessing solution type data
			allOfferId.add(FmoConstants.TEMP_OFFER_ID);
			allOfferIdSet=new HashSet<>(allOfferId);
		}
		return allOfferIdSet;
	}
	
	
	
	
	
	/**
	 * Gets the compoenet id.
	 *
	 * @param jsonObject the json object
	 * @param path the path
	 * @return component Id
	 */
	protected Long getCompoenetId(Object jsonObject,String path) {
		TypeRef<List<Long>> mapType = new TypeRef<List<Long>>() {};
		List<Long> data=jsonPathUtil.search(jsonObject, path,mapType);
		if(CollectionUtils.isNotEmpty(data)) {
			return data.get(0);
		}
		return 0l;
	}
	
	/**
	 * Gets the price attributes.
	 *
	 * @param jsonObject the json object
	 * @param path the path
	 * @return List<PriceAttributes>
	 */
	protected List<PriceAttributes> getPriceAttributes(Object jsonObject,String path){
		if(null!=jsonObject && StringUtils.isNotEmpty(path)) {
			TypeRef<List<PriceAttributes>> mapType = new TypeRef<List<PriceAttributes>>() {};
			return jsonPathUtil.search(jsonObject, path,mapType);
		}
		return new ArrayList<>();
		
	}
	
	/**
	 * Gets the data.
	 *
	 * @param inputData the input data
	 * @param offerId the offer id
	 * @param udfId the udf id
	 * @param componentId the component id
	 * @param datasetName the dataset name
	 * @param defaultValue the default value
	 * @return Object
	 */
	protected Object getData(Object inputData,Long offerId,Long udfId,Long componentId,String datasetName,String defaultValue) {
		if(StringUtils.isNotEmpty(datasetName) &&  null!=inputData) {
				if(datasetName.equals(FmoConstants.SALES_UDF_LOOKUP_SOURCE)) {
					Object result= repositoryService.getDataFromSalesLookUpTbl(inputData,offerId,udfId,componentId);
					if(result==null && StringUtils.isNotEmpty(defaultValue)){
						return defaultValue;
					}else {
						return result;
					}
				}if(datasetName.equals(FmoConstants.IMS2_LOOKUP_SOURCE)) {
					Object result= repositoryService.getDataFromIms2LookUpTbl(inputData,offerId,udfId,componentId);
					if(result==null && StringUtils.isNotEmpty(defaultValue)){
						return defaultValue;
					}else {
						return result;
					}
				}else if(datasetName.equals(FmoConstants.PRICE_TYPE_SOURCE)) {
					String key=inputData.toString();
					if(getPriceTypeDataMap()!=null && getPriceTypeDataMap().containsKey(key)) {
						return getPriceTypeDataMap().get(key);
					}else {
						return null;
					}
				}else if(datasetName.equals(FmoConstants.CONCURRENT_CALL_TYPE)) {
					//convert request maxConcurrentCallType in nexxus format to query  lineItem
					String reqConcurrentCallType=inputData.toString();
					return this.convertConcurrentCallType(reqConcurrentCallType);
				}else if(datasetName.contains(MyPriceConstants.NX_LOOKUP_SOURCE)) {
					String looupDataSet = datasetName.substring(datasetName.indexOf('|')+1, datasetName.length()); 
					String result= repositoryService.getDataFromNxLookUp(String.valueOf(inputData),looupDataSet);
					if(result==null && StringUtils.isNotEmpty(defaultValue)){
						return defaultValue;
					}else {
						return result;
					}
				}
		}
		return inputData;
	}
	
	/**
	 * Gets the fmo look data from cache.
	 *
	 * @return the fmo look data from cache
	 */
	protected void loadPriceTypeData(){
		if(null!=priceTypeDataMap) {
			priceTypeDataMap.clear();
		}
		priceTypeDataMap=MapUtils.isNotEmpty(fmoLookUpDataHelper.getFmoLookDataFromCache())?
				fmoLookUpDataHelper.getFmoLookDataFromCache():new ConcurrentHashMap<>();
	}
	
	
	

	
	/**
	 * This method is use in ReTrigger scenario
	 * Update nexxus output.
	 *
	 * @param reqDetails the req details
	 */
	@SuppressWarnings("unchecked")
	public void updateNexxusOutput(NxRequestDetails reqDetails) {
		if(null!=reqDetails && null!=reqDetails.getNxReqId()) {
			NxOutputFileModel model=repositoryService.getNxOutputFileModel(reqDetails.getNxReqId());
			NxOutputBean nxOutputJson=null;
			Map<String,Set<String>> falloutDataMap=null;
			Blob outputFile=null;
			Map<String, Object> paramMap=new HashMap<String, Object>();
			if(null!=model) {
				try {
					JSONObject intermediateJson=this.getIntermediateJson(model);
					if(null!=intermediateJson) {
						//create outputFile JSon
						nxOutputJson=lineItemService.getLineItemData(intermediateJson,paramMap);
						model.setOutput(nxOutputJson);
						model.setMpOutputJson(null!=intermediateJson?intermediateJson.toJSONString().replace("\\/", "/"):null);
						
						falloutDataMap=(Map<String, Set<String>>) paramMap.get(FmoConstants.FALLOUT_MAP);
						String fallOutData=nexusJsonUtility.convertMapToJson(falloutDataMap);
						model.setFallOutData(fallOutData);
						
						//create output file BLOG object
						outputFile = reportService.generateReport(nxOutputJson);
						//model.setOutputFile(outputFile);
						//setting fileName and fileType
						if(null!=outputFile) {
							model.setOutputFile(outputFile.getBytes(1,(int) outputFile.length()));
							model.setFileName(this.createFileName(reqDetails.getNxReqId()));
							model.setFileType(FmoConstants.FILE_TYPE);
						}
						//setting status
						this.setStatus(model,intermediateJson,nxOutputJson,outputFile,paramMap);
					}
				}catch (Exception  e) {
					model.setStatus(CommonConstants.FAIL);
					logger.error("createFmoNexusOutput Exception {}", e);
				}
				model.setModifiedDate(new Timestamp(System.currentTimeMillis()));
				
				repositoryService.saveNxOutputFile(model);
				
				//set status in nexxus request details
				setStatusForNxRequestDetails(reqDetails, model.getStatus());
				NxSolutionDetail nxSolutionDetail = reqDetails.getNxSolutionDetail();
				nxSolutionDetail.setModifiedDate(new Timestamp(System.currentTimeMillis()));
				repositoryService.saveSolutionDetails(nxSolutionDetail);
			}
			
		}
		
	}
	
	/**
	 * Gets the intermediate json.
	 *
	 * @param model the model
	 * @return the intermediate json
	 * @throws SalesBusinessException the sales business exception
	 */
	protected JSONObject getIntermediateJson(NxOutputFileModel model) throws SalesBusinessException {
		JSONParser parser = new JSONParser();
		try {
			String intermediateJsonString=model.getIntermediateJson();
			if(StringUtils.isNotEmpty(intermediateJsonString)) {
				return (JSONObject) parser.parse(intermediateJsonString);
			}
			return null;
		} catch (ParseException e) {
			logger.info("Exception in getIntermediateJson for retrigger scenario {}", e.getMessage(), e);
			throw new SalesBusinessException(MessageConstants.PROCESS_ERROR_CODE);
		}
	}
	
	
	/**
	 * Convert concurrent call type.
	 *
	 * @param concurrentCallType the concurrent call type
	 * @return the string
	 */
	protected String convertConcurrentCallType(String concurrentCallType ) {
		Long conCallType=convertStringToNumber(concurrentCallType);
		if(null!=conCallType) {
			if(new IntRange(0,12).containsLong(conCallType)) {
				return "VoMIS12";
			}else if(new IntRange(13,24).containsLong(conCallType)) {
				return "VoMIS24";
			}else if(new IntRange(25,48).containsLong(conCallType)) {
				return "VoMIS48";
			}else {
				return "T3";
			}
		}
		
		
		return null;
	}
	
	/**
	 * Convert string to number.
	 *
	 * @param input the input
	 * @return the long
	 */
	protected Long convertStringToNumber(String input) {
		boolean isNumeric = Stream.of(input)
	            .filter(s -> s != null && !s.isEmpty())
	            .filter(Pattern.compile("\\D").asPredicate().negate())
	            .mapToLong(Long::valueOf)
	            .boxed()
	            .findAny()
	            .isPresent();
		if(isNumeric) {
			return Long.parseLong(input);
		}
		return null;
	}
	
	
	/*public Map<String, Object> generateSiteId(Long nxReqId) {
		logger.info("Start : FMO site id generation and site Address preparation block");
		Map<String, Object> result = new HashMap<String, Object>();
		
		NxRequestDetails nxRequestDetail = nxRequestDetailsRepository.findByNxReqIdAndActiveYn(nxReqId, StringConstants.CONSTANT_Y);
	
		if(nxRequestDetail.getNxOutputFiles().get(0).getMpOutputJson() == null) {
			logger.info("Site Id is not generated as output json is not populated for these requests :: {}", nxReqId);
			return result;
		}
		List<Object> siteAddress = new ArrayList<>();
		Set<String> siteIds = new HashSet<String>();
		Long solutionId = nxRequestDetail.getNxSolutionDetail().getNxSolutionId();
		Map<String, List<CircuitSiteDetails>> cktSiteMap = new HashMap<String, List<CircuitSiteDetails>>();
		List<NxRequestDetails> nxRequestDetails = new ArrayList<NxRequestDetails>();
		nxRequestDetails.add(nxRequestDetail);
			// This call is to collect ckt id
		inrQualifyService.generateNxSiteId(nxRequestDetails, cktSiteMap, true, "SERVICE", null, null, null, MyPriceConstants.SOURCE_FMO, true);
			// This call generates the nxSiteid				
		inrQualifyService.generateNxSiteId(nxRequestDetails, cktSiteMap, false, "SERVICE", null, siteAddress, siteIds, MyPriceConstants.SOURCE_FMO, true);

		if(CollectionUtils.isNotEmpty(siteAddress)) {
			String siteAddressJson = inrQualifyService.getSiteAddressJson(siteAddress);
			NxSolutionSite nxSolutionSite = nxSolutionSiteRepository.findByNxSolutionIdAndActiveYN(solutionId, StringConstants.CONSTANT_Y);
			if(nxSolutionSite == null) {
				nxSolutionSite = new NxSolutionSite();
				nxSolutionSite.setNxSolutionId(solutionId);
				nxSolutionSite.setActiveYN(StringConstants.CONSTANT_Y);
			}
			nxSolutionSite.setSiteAddress(siteAddressJson);		
			nxSolutionSiteRepository.save(nxSolutionSite);
		}
		logger.info("End : FMO site id generation and site Address preparation block");
		return result;
	}*/
	
}
