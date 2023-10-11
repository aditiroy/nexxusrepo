
package com.att.sales.nexxus.service;

import java.util.HashMap;
import java.util.Map;

import javax.transaction.Transactional;

import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;

import com.att.sales.framework.exception.SalesBusinessException;
import com.att.sales.framework.model.ServiceResponse;
import com.att.sales.framework.model.Status;
import com.att.sales.framework.service.BaseServiceImpl;
import com.att.sales.nexxus.common.CommonConstants;
import com.att.sales.nexxus.constant.MyPriceConstants;
import com.att.sales.nexxus.constant.StringConstants;
import com.att.sales.nexxus.dao.model.NxMpDeal;
import com.att.sales.nexxus.dao.repository.NxMpDealRepository;
import com.att.sales.nexxus.model.UpdateTransactionOverrideRequest;
import com.att.sales.nexxus.myprice.transaction.service.MyPriceTransactionUtil;
import com.att.sales.nexxus.util.HttpRestClient;
import com.att.sales.nexxus.util.JacksonUtil;
import com.att.sales.nexxus.util.RestClientUtil;

@Service("UpdateTransactionOverrideImpl")
public class UpdateTransactionOverrideImpl extends BaseServiceImpl implements UpdateTransactionOverride {

	private static Logger logger = LoggerFactory.getLogger(UpdateTransactionOverrideImpl.class);

	@Value("${myPrice.updateTransactionCleanSaveRequest}")
	private String uri;

	@Autowired
	private RestClientUtil restClient;
	
	@Autowired
	private MyPriceTransactionUtil myPriceTransactionUtil;

	@Autowired
	private Environment env;
	
	@Autowired
	private NxMpDealRepository nxMpDealRepository;
	
	@Autowired
	private HttpRestClient httpRestClient;
	
	@Value("${myprice.proxy.enabled}")
	private String isProxyEnabled;
	
	@Override
	@Transactional
	public ServiceResponse updateTransactionOverride(UpdateTransactionOverrideRequest request) {

		logger.info("Entering updateTransactionOverride() method");
		ServiceResponse response = new ServiceResponse();
		Map<String, Object> result = new HashMap<String, Object>();
		String requestString = JacksonUtil.toStringForCodeHaus(request);
		JSONObject jsonObj = (JSONObject) JacksonUtil.toJsonObject(requestString);
		jsonObj.remove("myPriceTransId");
		requestString = jsonObj.toJSONString();
		try {
//			result = restClient.initiateWebService(requestString, env.getProperty("myPrice.updateTransactionCleanSaveRequest").replace("{TransactionId}", request.getMyPriceTransId()), "POST", headers, queryParameters);
			Map<String, String> headers  = new HashMap<>();
			headers.put(StringConstants.REQUEST_AUTHORIZATION, "Basic "+env.getProperty(CommonConstants.MYPRICE_AUTHORIZATION));
			headers.put(StringConstants.REQUEST_CONTENT_TYPE, "application/json");
			String proxy = null;
			if(StringConstants.CONSTANT_Y.equalsIgnoreCase(isProxyEnabled)) {
				proxy = env.getProperty(CommonConstants.CDP_HTTP_PROXY);
			}
			String res = httpRestClient.callHttpRestClient(env.getProperty("myPrice.updateTransactionCleanSaveRequest").replace("{TransactionId}", request.getMyPriceTransId()),  HttpMethod.POST, null, requestString, 
					headers, proxy);
			result.put(MyPriceConstants.RESPONSE_DATA, res);
			result.put(MyPriceConstants.RESPONSE_CODE, 200);
			result.put(MyPriceConstants.RESPONSE_MSG,"OK");
		} catch (SalesBusinessException e) {
			response = new ServiceResponse();
			Status status = new Status();
			status.setCode("M00003");
			response.setStatus(status);
			result.put(MyPriceConstants.RESPONSE_CODE, e.getHttpErrorCode());
			result.put(MyPriceConstants.RESPONSE_MSG, e.getMessage());
			NxMpDeal deal = nxMpDealRepository.findByTransactionId(request.getMyPriceTransId());
			myPriceTransactionUtil.updateNxMpDealMpInd(StringConstants.CONSTANT_N, deal.getNxTxnId());
			myPriceTransactionUtil.updateNxDesignAuditStatus(CommonConstants.FAILURE, request.getTransType(), request.getNxAuditId());
			myPriceTransactionUtil.sendDmaapEvents(deal, myPriceTransactionUtil.getNxSolutionDetails(deal.getNxTxnId()), CommonConstants.FAILED, result);
			logger.error("exception occured in Myprice updateTransactionOverride call");
		}
		String transResponse = (result.containsKey(MyPriceConstants.RESPONSE_DATA) && result.get(MyPriceConstants.RESPONSE_DATA) != null) ? (String) result.get(MyPriceConstants.RESPONSE_DATA) : null;
		int code = (int) result.get(MyPriceConstants.RESPONSE_CODE);
		
		if (code == CommonConstants.SUCCESS_CODE  && null != transResponse) {
			response = (ServiceResponse) restClient.processResult(transResponse, ServiceResponse.class);
			myPriceTransactionUtil.updateNxDesignAuditStatus(CommonConstants.SUCCESS, request.getTransType(), request.getNxAuditId());
			setSuccessResponse(response);
		}else {
			NxMpDeal deal = nxMpDealRepository.findByTransactionId(request.getMyPriceTransId());
			myPriceTransactionUtil.updateNxMpDealMpInd(StringConstants.CONSTANT_N, deal.getNxTxnId());
			myPriceTransactionUtil.updateNxDesignAuditStatus(CommonConstants.FAILURE, request.getTransType(), request.getNxAuditId());
			myPriceTransactionUtil.sendDmaapEvents(deal, myPriceTransactionUtil.getNxSolutionDetails(deal.getNxTxnId()), CommonConstants.FAILED, result);
			response = new ServiceResponse();
			Status status = new Status();
			status.setCode("M00003");
			response.setStatus(status);
		}
		logger.info("Exiting updateTransactionOverride() method");
		return response;
	}

}