package com.att.sales.nexxus.myprice.transaction.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;

import com.att.sales.framework.exception.SalesBusinessException;
import com.att.sales.framework.model.ServiceResponse;
import com.att.sales.framework.service.BaseServiceImpl;
import com.att.sales.nexxus.common.CommonConstants;
import com.att.sales.nexxus.constant.MyPriceConstants;
import com.att.sales.nexxus.constant.StringConstants;
import com.att.sales.nexxus.dao.model.NxMpDeal;
import com.att.sales.nexxus.dao.repository.NxMpDealRepository;
import com.att.sales.nexxus.dao.repository.NxMpDesignDocumentRepository;
import com.att.sales.nexxus.myprice.transaction.model.RemoveTransactionLineResponse;
import com.att.sales.nexxus.util.HttpRestClient;
import com.att.sales.nexxus.util.RestClientUtil;

@Service("removeTransactionServiceLineImpl")
public class RemoveTransactionServiceLineImpl extends BaseServiceImpl {

	/** The Constant logger. */
	private static final Logger logger = LoggerFactory.getLogger(RemoveTransactionServiceLineImpl.class);

	/** The env. */
	@Autowired
	private Environment env;

	@Autowired
	private RestClientUtil restClient;
	
	@Autowired
	private NxMpDealRepository nxMpDealRepository;
	
	@Autowired
	private NxMpDesignDocumentRepository nxMpDesignDocumentRepository;
	
	@Autowired
	private HttpRestClient httpRestClient;
	
	@Value("${myprice.proxy.enabled}")
	private String isProxyEnabled;

	public ServiceResponse removeTransactionLine(Map<String, Object> designMap) throws SalesBusinessException {
		logger.info("Entering removeTransactionLine() method");
		RemoveTransactionLineResponse response = null;
		try {			
			String transactionId = designMap.containsKey(MyPriceConstants.MP_TRANSACTION_ID) ? designMap.get(MyPriceConstants.MP_TRANSACTION_ID).toString() : null;
			Set<Long> documentIds = designMap.containsKey(MyPriceConstants.DOCUMENT_ID) ? (HashSet<Long>) designMap.get(MyPriceConstants.DOCUMENT_ID) : new HashSet<Long>();
			Long nxDesignId = designMap.containsKey(MyPriceConstants.NX_DESIGN_ID) ? (long) designMap.get(MyPriceConstants.NX_DESIGN_ID) : 0L;
			ArrayList<String> transactionLine = new ArrayList<String>();
			for(Long id : documentIds) {
				if(id != null) {
					transactionLine.add("transactionLine/"+id.toString());
				}
			}
			String requestString = "{ \"selections\" : [" + transactionLine.stream().collect(Collectors.joining("\", \"", "\"","\"")) + "],"
					+ "\"criteria\":{ \"limit\":1, \"fields\":[\"_document_number\"] }}";
			logger.info("Request To Remove Transaction API :====>> {}", requestString);
			String uri = env.getProperty("myprice.removeTransactionLine");
			uri = uri.replace("{transactionId}", transactionId);			
			Map<String, String> headers  = new HashMap<String, String>();
			headers.put(StringConstants.REQUEST_AUTHORIZATION, "Basic "+env.getProperty(CommonConstants.MYPRICE_AUTHORIZATION));
			headers.put(StringConstants.REQUEST_CONTENT_TYPE, "application/json");
			String proxy = null;
			if(StringConstants.CONSTANT_Y.equalsIgnoreCase(isProxyEnabled)) {
				proxy = env.getProperty(CommonConstants.CDP_HTTP_PROXY);
			}
			String transResponse = httpRestClient.callHttpRestClient(uri, HttpMethod.POST, null, requestString, 
					headers, proxy);
			if (null != transResponse) {
				response = (RemoveTransactionLineResponse) restClient.processResult(transResponse,
						RemoveTransactionLineResponse.class);
				NxMpDeal nxmpdeal = nxMpDealRepository.findByTransactionId(transactionId);
				if (Optional.ofNullable(nxmpdeal).isPresent() && nxDesignId.longValue() != 0) {
					nxMpDesignDocumentRepository.updateActiveYNByTxnId(new Date(), nxmpdeal.getNxTxnId(), nxDesignId);
				}
				setSuccessResponse(response);
			}

		} catch (Exception e) {
			logger.error("exception occured in Myprice removeTransactionLine call");
			throw new SalesBusinessException();
		}
		logger.info("Existing removeTransactionLine() method");
		return response;
	}



}
