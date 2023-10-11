/**
 * 
 */
package com.att.sales.nexxus.myprice.transaction.service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
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
import com.att.sales.nexxus.constant.MyPriceConstants;
import com.att.sales.nexxus.constant.StringConstants;
import com.att.sales.nexxus.dao.model.NxMpDeal;
import com.att.sales.nexxus.dao.model.solution.NxSolutionDetail;
import com.att.sales.nexxus.dao.repository.NxMpDealRepository;
import com.att.sales.nexxus.dao.repository.SalesMsDao;
import com.att.sales.nexxus.myprice.transaction.model.CreateTransactionResponse;
import com.att.sales.nexxus.reteriveicb.model.RetreiveICBPSPRequest;
import com.att.sales.nexxus.reteriveicb.model.Solution;
import com.att.sales.nexxus.util.HttpRestClient;
import com.att.sales.nexxus.util.RestClientUtil;

/**
 * @author ShruthiCJ
 *
 */
@Service("CreateTransactionServiceImpl")
public class CreateTransactionServiceImpl extends BaseServiceImpl implements CreateTransactionService {

	private static Logger logger = LoggerFactory.getLogger(CreateTransactionServiceImpl.class);

	@Autowired
	private RestClientUtil restClient;

	@Autowired
	private NxMpDealRepository nxMpDealRepository;

	@Autowired
	private Environment env;
	
	@Autowired
	private SalesMsDao salesMsDao;

	@Autowired
	private MyPriceTransactionUtil myPriceTransactionUtil;
	
	@Autowired
	private HttpRestClient httpRestClient;
	
	@Value("${myprice.proxy.enabled}")
	private String isProxyEnabled;
	
	
	@Override
	public Map<String, Object> createTransaction(RetreiveICBPSPRequest retreiveICBPSPRequest, NxSolutionDetail nxSolutionDetail, Long nxTxnId, String flowType) {
		logger.info("Entering createTransaction() method");
		CreateTransactionResponse createTransactionRes = null;
		Map<String, Object> response = new HashMap<String, Object>();
		NxMpDeal nxMpDeal = new NxMpDeal();
		if(nxTxnId != null) {
			nxMpDeal.setNxTxnId(nxTxnId);
			nxMpDeal.setModifiedDate(new Date());
		}else {
			nxMpDeal.setCreatedDate(new Date());
		}
			
		try {
			Solution solution = retreiveICBPSPRequest.getSolution();
			if(null != solution) {
				if(!solution.getOffers().isEmpty()) {
					String offerId = solution.getOffers().get(0).getOfferId();
					if (StringUtils.isNotEmpty(offerId)) {
						nxMpDeal.setOfferId(salesMsDao.getOfferNameByOfferId(Integer.parseInt(offerId)));
					}
				}
				nxMpDeal.setPriceScenarioId(solution.getPriceScenarioId());
			}
			nxMpDeal.setSolutionId(nxSolutionDetail.getNxSolutionId());
			nxMpDeal.setActiveYN(CommonConstants.ACTIVE_Y);
			Map<String, String> headers  = new HashMap<>();
			headers.put(StringConstants.REQUEST_AUTHORIZATION, "Basic "+env.getProperty(CommonConstants.MYPRICE_AUTHORIZATION));
			headers.put(StringConstants.REQUEST_CONTENT_TYPE, "application/json");
			String createRequest = "{}";
			String proxy = null;
			if(StringConstants.CONSTANT_Y.equalsIgnoreCase(isProxyEnabled)) {
				proxy = env.getProperty(CommonConstants.CDP_HTTP_PROXY);
			}
			String result = httpRestClient.callHttpRestClient(env.getProperty("myprice.createTransaction"),  HttpMethod.POST, null, createRequest, 
					headers,proxy);
			response.put(MyPriceConstants.RESPONSE_DATA, result);
			response.put(MyPriceConstants.RESPONSE_CODE, 200);
			response.put(MyPriceConstants.RESPONSE_MSG,"OK");
			String createResponse = response.get(MyPriceConstants.RESPONSE_DATA) != null ? (String) response.get(MyPriceConstants.RESPONSE_DATA) : null;
			int code =  (int) response.get(MyPriceConstants.RESPONSE_CODE);
			if(code == CommonConstants.SUCCESS_CODE) {
				if (null != createResponse && !createResponse.isEmpty()) {
					createTransactionRes = (CreateTransactionResponse) restClient.processResult(createResponse,
							CreateTransactionResponse.class);
				}
				if (null != createTransactionRes) {
					nxMpDeal.setDealID(createTransactionRes.getDealID());
					nxMpDeal.setRevision(createTransactionRes.getRevision());
					nxMpDeal.setTransactionId(createTransactionRes.getMyPriceTransacId());
					nxMpDeal.setVersion(createTransactionRes.getVersion());
					nxMpDeal.setDealStatus(CommonConstants.CREATED);
					nxMpDeal.setNxMpStatusInd(StringConstants.CONSTANT_Y);
					nxMpDeal = nxMpDealRepository.save(nxMpDeal);
					createTransactionRes.setNxTransacId(nxMpDeal.getNxTxnId());
					createTransactionRes.setOfferName(nxMpDeal.getOfferId());
					createTransactionRes.setPriceScenarioId(0L);
					if(null != nxMpDeal.getPriceScenarioId()) {
						createTransactionRes.setPriceScenarioId(nxMpDeal.getPriceScenarioId());
					}
					createTransactionRes.setSuccess(true);
					myPriceTransactionUtil.sendDmaapEvents(nxMpDeal, nxSolutionDetail, CommonConstants.CREATED, response);
				}
			} else {
				nxMpDeal.setNxMpStatusInd(StringConstants.CONSTANT_N);
				nxMpDeal = nxMpDealRepository.save(nxMpDeal);
				createTransactionRes = new CreateTransactionResponse();
				createTransactionRes.setNxTransacId(nxMpDeal.getNxTxnId());
				createTransactionRes.setSuccess(false);
				myPriceTransactionUtil.sendDmaapEvents(null, nxSolutionDetail, CommonConstants.FAILED, response);
			}
		} catch (SalesBusinessException e) {
			nxMpDeal.setNxMpStatusInd(StringConstants.CONSTANT_N);
			nxMpDeal = nxMpDealRepository.save(nxMpDeal);
			createTransactionRes = new CreateTransactionResponse();
			createTransactionRes.setNxTransacId(nxMpDeal.getNxTxnId());
			createTransactionRes.setSuccess(false);
			response.put(MyPriceConstants.RESPONSE_CODE, e.getHttpErrorCode());
			response.put(MyPriceConstants.RESPONSE_MSG, e.getMessage());
			logger.error("exception occured in Myprice createTraction call {}", e.getMessage());
			myPriceTransactionUtil.sendDmaapEvents(null, nxSolutionDetail, CommonConstants.FAILED, response);
		}
		logger.info("Existing createTransaction() method");
		response.put("createTransactionRes", createTransactionRes);
		return response;
	}
	
	@Override
	public Map<String, Object> callCreateTrans() throws SalesBusinessException {
		String createRequest = "{}";
		Map<String, String> headers  = new HashMap<>();
		headers.put(StringConstants.REQUEST_AUTHORIZATION, "Basic "+env.getProperty(CommonConstants.MYPRICE_AUTHORIZATION));
		headers.put(StringConstants.REQUEST_CONTENT_TYPE, "application/json");
		Map<String, Object> result = new HashMap<String, Object>();
		String proxy = null;
		if(StringConstants.CONSTANT_Y.equalsIgnoreCase(isProxyEnabled)) {
			proxy = env.getProperty(CommonConstants.CDP_HTTP_PROXY);
		}
		String response = httpRestClient.callHttpRestClient(env.getProperty("myprice.createTransaction"),  HttpMethod.POST, null, createRequest, 
				headers,proxy);
		result.put(MyPriceConstants.RESPONSE_DATA, response);
		result.put(MyPriceConstants.RESPONSE_CODE, 200);
		result.put(MyPriceConstants.RESPONSE_MSG,"OK");
		return result;
	}
}
