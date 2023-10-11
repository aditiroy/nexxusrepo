package com.att.sales.nexxus.myprice.transaction.service;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;

import com.att.sales.framework.exception.SalesBusinessException;
import com.att.sales.framework.service.BaseServiceImpl;
import com.att.sales.nexxus.common.CommonConstants;
import com.att.sales.nexxus.constant.StringConstants;
import com.att.sales.nexxus.util.HttpRestClient;

@Service("UpdateTransactionSubmitToApprovalImpl")
public class UpdateTransactionSubmitToApprovalImpl extends BaseServiceImpl
		implements UpdateTransactionSubmitToApproval {

	@Autowired
	private Environment env;
	
	@Autowired
	private HttpRestClient httpRestClient;
	
	@Value("${myprice.proxy.enabled}")
	private String isProxyEnabled;	

	private static Logger logger = LoggerFactory.getLogger(UpdateTransactionSubmitToApprovalImpl.class);

	public boolean updateTransactionSubmitToApproval(LinkedHashMap<String, Object> requestMap) {
		logger.info("Entering updateTransactionSubmitToApproval() method");
		String priceScoreResponse = null;
		String emptyRequest = new JSONObject().toString();
		try {
			String myPriceTransId = (String) requestMap.get(StringConstants.MY_PRICE_TRANS_ID);
			Map<String, String> headers = new HashMap<String, String>();
			headers.put(StringConstants.REQUEST_AUTHORIZATION, "Basic "+env.getProperty(CommonConstants.MYPRICE_AUTHORIZATION));
			headers.put(StringConstants.REQUEST_CONTENT_TYPE, "application/json");
			String proxy = null;
			if(StringConstants.CONSTANT_Y.equalsIgnoreCase(isProxyEnabled)) {
				proxy = env.getProperty(CommonConstants.CDP_HTTP_PROXY);
			}
			priceScoreResponse = httpRestClient.callHttpRestClient(env.getProperty("myprice.updateTransactionSubmitToApproval")
					.replace("{transactionId}", myPriceTransId), HttpMethod.POST, null, emptyRequest, 
					headers, proxy);
		} catch (SalesBusinessException e) {
			logger.error("Exception occured while calling update transaction submit to approval {}", e);
		}
		if (null != priceScoreResponse) {
			try {
				JSONObject jsonObject = new JSONObject(priceScoreResponse);
				if(jsonObject.has("documents")) {
					JSONObject getDocuments = jsonObject.getJSONObject("documents");
					if(getDocuments.has("pricingManager_q") && !getDocuments.isNull("pricingManager_q") && 
							"AutoApproved".equalsIgnoreCase(getDocuments.get("pricingManager_q").toString())) {
						logger.info("Exiting updateTransactionSubmitToApproval() with request status {}", getDocuments.get("pricingManager_q").toString());
						return true;
					}
				}
			}catch(JSONException e){
				e.printStackTrace();
			}
		}
		logger.info("Exiting updateTransactionSubmitToApproval() method");
		return false;
	}
}
