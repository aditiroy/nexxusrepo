package com.att.sales.nexxus.myprice.transaction.service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.att.sales.framework.exception.SalesBusinessException;
import com.att.sales.framework.model.ServiceResponse;
import com.att.sales.framework.service.BaseServiceImpl;
import com.att.sales.nexxus.common.CommonConstants;
import com.att.sales.nexxus.constant.MyPriceConstants;
import com.att.sales.nexxus.constant.StringConstants;
import com.att.sales.nexxus.dao.model.NxMpDeal;
import com.att.sales.nexxus.dao.model.solution.NxSolutionDetail;
import com.att.sales.nexxus.dao.repository.NxMpDealRepository;
import com.att.sales.nexxus.dao.repository.NxSolutionDetailsRepository;
import com.att.sales.nexxus.myprice.transaction.model.GetTransactionResponse;
import com.att.sales.nexxus.myprice.transaction.model.OriginalClonedTxId;
import com.att.sales.nexxus.util.HttpRestClient;
import com.att.sales.nexxus.util.JacksonUtil;
import com.att.sales.nexxus.util.RestClientUtil;

@Service("getTransactionServiceImpl")
public class GetTransactionServiceImpl extends BaseServiceImpl {

	/** The Constant logger. */
	private static final Logger logger = LoggerFactory.getLogger(GetTransactionServiceImpl.class);

	/** The env. */
	@Autowired
	private Environment env;

	@Autowired
	private RestClientUtil restClient;
	
	@Autowired
	private NxMpDealRepository nxMpDealRepository;
	
	@Autowired
	private NxSolutionDetailsRepository nxSolutionDetailsRepository;
	
	@Autowired
	private HttpRestClient httpRestClient;

	@Value("${myprice.proxy.enabled}")
	private String isProxyEnabled;
	
	public ServiceResponse getTransaction(String transactionId) throws SalesBusinessException {
		logger.info("Entering getTransaction() method {}", org.apache.commons.lang3.StringUtils.normalizeSpace(transactionId));
		GetTransactionResponse response = null;
		try {
			String uri = env.getProperty("myprice.getTransaction");
			uri = uri.replace("{transactionId}", transactionId);			
			Map<String, String> headers  = new HashMap<>();
			headers.put(StringConstants.REQUEST_AUTHORIZATION, "Basic "+env.getProperty(CommonConstants.MYPRICE_AUTHORIZATION));
			headers.put(StringConstants.REQUEST_CONTENT_TYPE, "application/json");
			String proxy = null;
			if(StringConstants.CONSTANT_Y.equalsIgnoreCase(isProxyEnabled)) {
				proxy = env.getProperty(CommonConstants.CDP_HTTP_PROXY);
			}
			String transResponse = httpRestClient.callHttpRestClient(uri, HttpMethod.GET, null, null, 
					headers, proxy);
			
			if (null != transResponse) {
				response = (GetTransactionResponse) restClient.processResult(transResponse,
						GetTransactionResponse.class);
				logger.info("GetTransaction Response : {}", JacksonUtil.toString(response));
				updateRateLetterDetails(response,transactionId);
				setSuccessResponse(response);
			}
		} catch (SalesBusinessException e) {
			logger.error("exception occured in Myprice getTransaction call {}", e.getMessage());
		}
		logger.info("Existing getTransaction() method {}", transactionId);
		return response;
	}

	@Transactional
	public void updateRateLetterDetails(GetTransactionResponse response,String transactionId) {
		logger.info("Entering updateRateLetterDetails() : My Price Transaction-Id : {}", org.apache.commons.lang3.StringUtils.normalizeSpace(transactionId));
		List<NxMpDeal> nxmpdeals = nxMpDealRepository.getByTransactionIdAndNxTxnOrder(transactionId);
		NxMpDeal nxmpdeal = null;
		if(CollectionUtils.isNotEmpty(nxmpdeals)) {
			nxmpdeal = nxmpdeals.get(0);
		}
		if(Optional.ofNullable(nxmpdeal).isPresent()) {
			nxmpdeal.setRateLetterExpiresOn(response.getRateLetterExpiresOn());
			//logger.info("Quoteurl for transaction id {}", LogUtils.logString(transactionId),  "is {}", response.getQuoteUrl());
			nxmpdeal.setQuoteUrl(response.getQuoteUrl());
			if(null != response.getRlType() && null != response.getRlType().getValue()) {
				String rlType = response.getRlType().getValue();
				if(MyPriceConstants.RL_TYPE_WITHOUT_CAVEAT_ARRAY.contains(rlType)) {
					nxmpdeal.setRlType(MyPriceConstants.RL_TYPE_WITHOUT_CAVEAT);
				} else if(MyPriceConstants.RL_TYPE_WITH_CAVEAT_ARRAY.contains(rlType)) {
					nxmpdeal.setRlType(MyPriceConstants.RL_TYPE_WITH_CAVEAT);
				} else {
					nxmpdeal.setRlType(response.getRlType().getValue());
				}
			}
			if(response.isAutoApproval()) {
				nxmpdeal.setAutoApproval("Approved Online");
			} else if(!response.isAutoApproval()) {
				nxmpdeal.setAutoApproval("ICB");
			} else {
				nxmpdeal.setAutoApproval("None");
			}
			
			if(null != response.getContractPricingScope() && null != response.getContractPricingScope().getValue()) {
				nxmpdeal.setContractPricingScope(response.getContractPricingScope().getValue());
			}
			
			if(StringConstants.DEAL_ACTION_PD_CLONE.equalsIgnoreCase(nxmpdeal.getAction())) {
				if(null != response.getOriginalClonedTxId()) {
					OriginalClonedTxId cloneTxnId = (OriginalClonedTxId) restClient.processResult(response.getOriginalClonedTxId(), OriginalClonedTxId.class);
					if("Clone".equalsIgnoreCase(cloneTxnId.getAction()) && null != cloneTxnId.getSourceId()) {
						NxMpDeal oldNxmpdeal = nxMpDealRepository.getByTransactionId(cloneTxnId.getSourceId());
						if(null != oldNxmpdeal && null != oldNxmpdeal.getPriceScenarioId()) {
							nxmpdeal.setPriceScenarioId(oldNxmpdeal.getPriceScenarioId());
						}
					}
				}
			}
			nxmpdeal.setModifiedDate(new Date());
			nxMpDealRepository.save(nxmpdeal);
			if (Optional.ofNullable(nxmpdeal.getSolutionId()).isPresent()) {
				NxSolutionDetail nxSolutionDetail = nxSolutionDetailsRepository.findByNxSolutionId(nxmpdeal.getSolutionId());
				if (nxSolutionDetail.getCustomerName() == null && response.getCustomerCompanyName() != null
						&& !response.getCustomerCompanyName().isEmpty()) {
					nxSolutionDetail.setCustomerName(response.getCustomerCompanyName());
				}
				if (nxSolutionDetail.getL3Value() == null && response.getSaartAccountNumber() != null
						&& !response.getSaartAccountNumber().isEmpty()) {
					nxSolutionDetail.setL3Value(response.getSaartAccountNumber());
				}
				nxSolutionDetailsRepository.save(nxSolutionDetail);
			}
		}
		logger.info("Exit updateRateLetterDetails()");
	}
	
/*	public GetTransactionResponse callGetTransactionApi() throws SalesBusinessException {
		logger.info("Calling MyPrice for callGetTransactionApi .");
		GetTransactionResponse response = null;
		try {
			RestTemplateBuilder restTemplateBuilder = new RestTemplateBuilder();
			RestTemplate restTemplate = restTemplateBuilder
					.basicAuthorization(env.getProperty("myprice.username"), env.getProperty("myprice.password"))
					.build();
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(org.springframework.http.MediaType.APPLICATION_JSON);
			HttpEntity<String> entity = new HttpEntity<>("parameters", headers);
			ResponseEntity<String> respString = restTemplate
					.exchange(new URI(env.getProperty("myprice.getTransaction")), HttpMethod.GET, entity, String.class);

			com.fasterxml.jackson.databind.ObjectMapper thisMapper = new com.fasterxml.jackson.databind.ObjectMapper();
			thisMapper.configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES, true);
			thisMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

			if (null != respString.getBody()) {
				response = thisMapper.readValue(respString.getBody(), GetTransactionResponse.class);
			}
			logger.info("The response is {}", response);
		} catch (Exception e) {
			logger.error("Exception : While processing callGetTransactionApi rest client call {}", e.getMessage());
			throw new SalesBusinessException(MessageConstants.ADDRESS_EXCEPTION);
		}
		return response;
	}*/
	
	public GetTransactionResponse getTransactionSalesOne(String transactionId) throws SalesBusinessException {
		logger.info("Entering getTransactionSalesOne() method {}", transactionId);
		GetTransactionResponse response = null;
		try {
			String uri = env.getProperty("myprice.getTransaction");
			uri = uri.replace("{transactionId}", transactionId);			
			Map<String, String> headers  = new HashMap<>();
			headers.put(StringConstants.REQUEST_AUTHORIZATION, "Basic "+env.getProperty(CommonConstants.MYPRICE_AUTHORIZATION));
			headers.put(StringConstants.REQUEST_CONTENT_TYPE, "application/json");
			String proxy = null;
			if(StringConstants.CONSTANT_Y.equalsIgnoreCase(isProxyEnabled)) {
				proxy = env.getProperty(CommonConstants.CDP_HTTP_PROXY);
			}
			String transResponse = httpRestClient.callHttpRestClient(uri, HttpMethod.GET, null, null, 
					headers, proxy);
			
			if (null != transResponse) {
				response = (GetTransactionResponse) restClient.processResult(transResponse,
						GetTransactionResponse.class);
				logger.info("GetTransaction Response : {}", JacksonUtil.toString(response));
				//updateRateLetterDetails(response,transactionId);
				setSuccessResponse(response);
			}
		} catch (SalesBusinessException e) {
			logger.error("exception occured in Myprice getTransaction call {}", e.getMessage());
		}
		logger.info("Existing getTransaction() method {}", transactionId);
		return response;
	}
	
}
