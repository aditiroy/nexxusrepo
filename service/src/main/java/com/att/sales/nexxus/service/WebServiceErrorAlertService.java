package com.att.sales.nexxus.service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.att.sales.framework.exception.SalesBusinessException;
import com.att.sales.nexxus.dao.model.NxWebServiceError;
import com.att.sales.nexxus.dao.repository.NxWebServiceErrorRepository;

@Component
public class WebServiceErrorAlertService {
	private static Logger logger = LoggerFactory.getLogger(WebServiceErrorAlertService.class);
	
	@Autowired
	private NxMyPriceRepositoryServce nxMyPriceRepositoryServce;
	
	@Autowired
	private NxWebServiceErrorRepository nxWebServiceErrorRepository;
	
	@Autowired
	private MailService mailService;
	
	public void serviceErrorAlert(String request, String ingressUrl, String method, Map<String, String> headers,
			Map<String, Object> queryParameters, Exception e) {
		String serviceName = translateUrlToServiceName(ingressUrl);
		if (serviceName == null) {
			return;
		}
		Instant now = Instant.now();
		NxWebServiceError nxWebServiceError = new NxWebServiceError(serviceName, request, ingressUrl, method, headers, queryParameters, e, now);
		nxWebServiceErrorRepository.save(nxWebServiceError);
		
		Map<String, String> serviceErrorAlertProperty = nxMyPriceRepositoryServce.getDescDataFromLookup("SERVICE_ERROR_ALERT_PROPERTY");
		int serviceErrorCount = Integer.parseInt(serviceErrorAlertProperty.get("SERVICE_ERROR_COUNT"));
		int serviceCountInterval = Integer
				.parseInt(serviceErrorAlertProperty.get("SERVICE_ERROR_COUNT_INTERVAL_IN_MINUTES"));
		long errorCount = errorCount(serviceName, now);
		if (errorCount >= serviceErrorCount) {
			try {
				mailService.prepareAndSendMailForServiceAlert(serviceName, errorCount, serviceCountInterval, nxWebServiceError.getNxWebServiceErrorId());
				nxWebServiceError.setAlertSentTime(Date.from(now));
			} catch (SalesBusinessException e1) {
				logger.info("mailService exception", e1);
			}
		}
	}
	
	protected String translateUrlToServiceName(String url) {
		Map<String, String> serviceTranslation = nxMyPriceRepositoryServce.getDescDataFromLookup("SERVICE_ERROR_ALERT_SERVICE_TRANSLATION");
		for (Entry<String, String> entry : serviceTranslation.entrySet()) {
			if (url.startsWith(entry.getKey())) {
				return entry.getValue();
			}
		}
		return null;
	}
	
	protected long errorCount(String serviceName, Instant now) {
		Map<String, String> serviceErrorAlertProperty = nxMyPriceRepositoryServce
				.getDescDataFromLookup("SERVICE_ERROR_ALERT_PROPERTY");
		int emailTriggerInterval = Integer.parseInt(serviceErrorAlertProperty.get("EMAIL_TRIGGER_INTERVAL_IN_MINUTES"));
		int serviceCountInterval = Integer
				.parseInt(serviceErrorAlertProperty.get("SERVICE_ERROR_COUNT_INTERVAL_IN_MINUTES"));
		long errorCount = nxWebServiceErrorRepository.countError(serviceName,
				Date.from(now.minus(serviceCountInterval, ChronoUnit.MINUTES)),
				Date.from(now.minus(emailTriggerInterval, ChronoUnit.MINUTES)));
		logger.info("errorCount for {} is {}", serviceName, errorCount);
		return errorCount;
	}
}
