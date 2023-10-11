/**
 * 
 */
package com.att.sales.nexxus.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import javax.transaction.Transactional;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.att.sales.framework.model.Message;
import com.att.sales.framework.model.ServiceMetaData;
import com.att.sales.framework.model.ServiceResponse;
import com.att.sales.framework.model.Status;
import com.att.sales.framework.model.constants.HttpErrorCodes;
import com.att.sales.framework.service.BaseServiceImpl;
import com.att.sales.framework.util.MessageResourcesUtil;
import com.att.sales.nexxus.constant.StringConstants;
import com.att.sales.nexxus.constant.TDDConstants;
import com.att.sales.nexxus.dao.model.solution.NxSolutionDetail;
import com.att.sales.nexxus.dao.repository.NxSolutionDetailsRepository;
import com.att.sales.nexxus.dmaap.mr.util.DmaapPublishEventsServiceImpl;
import com.att.sales.nexxus.myprice.transaction.service.AutomationFlowHelperService;
import com.att.sales.nexxus.reteriveicb.model.SolutionCostIndicatorResponse;
import com.att.sales.nexxus.service.SolutionCostIndicatorService;
import com.att.sales.nexxus.transmitdesigndata.model.NxSolutionStatusDMaap;
import com.att.sales.nexxus.transmitdesigndata.model.SolutionCostRequest;
import com.att.sales.nexxus.util.ThreadMetaDataUtil;

/**
 * @author aa316k
 *
 */
@Service
@Transactional
public class SolutionCostIndicatorServiceimpl extends BaseServiceImpl implements SolutionCostIndicatorService {
	
	@Autowired
	private AutomationFlowHelperService automationFlowHelperService;
	
	@Autowired
	private NxSolutionDetailsRepository nxSolutionDetailsRepository;
	
	@Autowired
	private DmaapPublishEventsServiceImpl dmaapPublishEventsServiceImpl;
	
	private static Logger logger = LoggerFactory.getLogger(SolutionCostIndicatorService.class);
	
	public SolutionCostIndicatorResponse solutionCostIndicator(SolutionCostRequest request) {
		
		SolutionCostIndicatorResponse resp = new SolutionCostIndicatorResponse();
		
		String slcIndicator  = request.getSlcIndicator();
		Long externalKey = request.getSolutionId();
		
		if(slcIndicator == null && externalKey == null ) {			
			setErrorResponse(resp, StringConstants.SOLUTION_INVALID);
		    return resp;
		}			
		nxSolutionDetailsRepository.updateSlcIndBySolutionId(slcIndicator,externalKey);
		List<NxSolutionDetail> nxSolnList = nxSolutionDetailsRepository.findByExternalKey(externalKey);//if we have one record then only it will work
	    Map<String, Object> inputMap = new HashMap<>();
	 	inputMap.put(TDDConstants.SOLUTION_DATA, nxSolnList.get(0));
	 	inputMap.put(TDDConstants.FLOW_TYPE, nxSolnList.get(0).getFlowType());
	 	inputMap.put(TDDConstants.OPTY_ID, nxSolnList.get(0).getOptyId());
 	   
		if("C".equalsIgnoreCase(slcIndicator)) {
			if(CollectionUtils.isNotEmpty(nxSolnList) && "Y".equals(nxSolnList.get(0).getAutomationFlowInd()) && !StringConstants.IPNE.equalsIgnoreCase(nxSolnList.get(0).getFlowType()) ){
				Map<String, Object> requestMetaDataMap = new HashMap<>();
           		
	           	if (ServiceMetaData.getRequestMetaData() != null) {
	           		ServiceMetaData.getRequestMetaData().forEach((key, value) -> requestMetaDataMap.put(key, value));
	           	}
				CompletableFuture.runAsync(() -> {
					try {
							ThreadMetaDataUtil.initThreadMetaData(requestMetaDataMap);
							automationFlowHelperService.process(new LinkedHashMap<String, Object>(inputMap));
					} catch (Exception e) {
							logger.info("Exception", e);
							throw e;
					} finally {
							ThreadMetaDataUtil.destroyThreadMetaData();
					}
				});
             }
								
		}
        callMessageRouter(request,inputMap);
		if(request.getSolutionId()!=null && request.getSlcIndicator()!=null) {
		   setSuccessResponse(resp);  
		}
		return resp;
	}
	
	
	public void callMessageRouter(SolutionCostRequest request, Map<String, Object> inputMap){
		
			String flowType = inputMap.get(TDDConstants.FLOW_TYPE) != null ? (String) inputMap.get(TDDConstants.FLOW_TYPE) : "";
			String optyId = inputMap.get(TDDConstants.OPTY_ID) != null ? (String) inputMap.get(TDDConstants.OPTY_ID) : "";		
			NxSolutionStatusDMaap pedDmaap = new NxSolutionStatusDMaap();
			pedDmaap.setOpportunityId(optyId);
			pedDmaap.setExternalKey(request.getSolutionId());
			pedDmaap.setEventType("solution");
			
				if ("C".equalsIgnoreCase(request.getSlcIndicator())){	
				    pedDmaap.setNxStatus("SLC completed");
				}else if("Y".equalsIgnoreCase(request.getSlcIndicator())) {
					pedDmaap.setNxStatus("SLC in progress"); 	
				}else {
					pedDmaap.setNxStatus("Regular Costing");
				}
				
				if (StringConstants.SALES_IPNE.equalsIgnoreCase(flowType)) {
					pedDmaap.setIpeIndicator(StringConstants.CONSTANT_Y);
				}else {
					pedDmaap.setIpeIndicator("N"); 
				}
				logger.info("SolutionCost is: {}", pedDmaap);
						
			dmaapPublishEventsServiceImpl.triggerDmaapEventForSLC(pedDmaap);
	}

		
	public ServiceResponse setErrorResponse(ServiceResponse response, String errorCode) {
		Status status = new Status();
		List<Message> messageList = new ArrayList<>();
		Message msg = MessageResourcesUtil.getMessageMapping().get(errorCode);
		messageList.add(msg);
		status.setCode(HttpErrorCodes.ERROR.toString());
		status.setMessages(messageList);
		response.setStatus(status);
		return response;
	}

}
