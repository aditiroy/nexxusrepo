/**
 * 
 */
package com.att.sales.nexxus.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

/**
 * The Class NexxusAIService.
 *
 * @author rk967c
 */
@Service
public class NexxusAIService extends BaseServiceImpl {

	/** The Constant logger. */
	private static final Logger logger = LoggerFactory.getLogger(NexxusAIService.class);

	/** The env. */
	@Autowired
	private Environment env;
	
	/** The resolution prediction. */
	@Value("${ai.resolutionPrediction.url}")
	private String resolutionPrediction;
	
	/** The model resolution key. */
	@Value("${ai.modelResolutionKey}")
	private String modelResolutionKey;
	
	/** The model resolution version. */
	@Value("${ai.modelResolutionVersion}")
	private String modelResolutionVersion;
	
	/** The enable AI. */
	@Value("${ai.enable.flag}")
	private String enableAI;
	
	@Autowired
	private HttpRestClient httpRestClient;
	
	/**
	 * Gets the nx predictions.
	 *
	 * @param beids the beids
	 * @param product the product
	 * @return the nx predictions
	 */
	public boolean getNxPredictions(Set<String> beids, String product) {
		boolean result = true;
		
		if("N".equalsIgnoreCase(enableAI)) {
			return false;
		}
		
		for(String beid : beids) {
			String request = "beid,product\n".concat(beid).concat("B").concat(",").concat(product);
			Map<String, String> headers  = new HashMap<String, String>();
			headers.put(StringConstants.REQUEST_AUTHORIZATION, "Basic "+env.getProperty(CommonConstants.AZURE_MS_AUTHORIZATION));
			headers.put(StringConstants.REQUEST_CONTENT_TYPE, "text/plain");
			headers.put("ATT-ModelKey", modelResolutionKey);
			headers.put("ATT-ModelVersion", modelResolutionVersion);
					
			String nbaResponse = null;
			try {
				 nbaResponse = httpRestClient.callHttpRestClient(resolutionPrediction, HttpMethod.POST, null, request, 
						 headers, null);
			} catch (SalesBusinessException e) {
				 logger.info("SalesBusinessException occured during getNxPredictions method"+e);
				result = false;
				break;
			}
			if("N".equalsIgnoreCase(parseAndGetBestAction(nbaResponse))) {
				result = false;
				break;
			}
		}
			
		return result;
	}
	
	/**
	 * Gets the nx predictions.
	 *
	 * @param request the request
	 * @return the nx predictions
	 */
	public boolean getNxPredictions(String request) {
		boolean result = true;

		if ("N".equalsIgnoreCase(enableAI)) {
			return false;
		}

		// String request =
		// "beid,product\n".concat(beid).concat("B").concat(",").concat(product);

		Map<String, String> headers = new HashMap<String, String>();
		headers.put(StringConstants.REQUEST_AUTHORIZATION, "Basic "+env.getProperty(CommonConstants.AZURE_MS_AUTHORIZATION));
		headers.put(StringConstants.REQUEST_CONTENT_TYPE, "text/plain");
		headers.put("ATT-ModelKey", modelResolutionKey);
		headers.put("ATT-ModelVersion", modelResolutionVersion);

		String nbaResponse = null;
		try {
			 nbaResponse = httpRestClient.callHttpRestClient(resolutionPrediction, HttpMethod.POST, null, request, 
					 headers, null);
		} catch (SalesBusinessException e) {
			logger.info("SalesBusinessException occured during getNxPredictions method" + e);
			result = false;
		}
		if (result && "N".equalsIgnoreCase(parseAndGetBestAction(nbaResponse))) {
			result = false;
		}

		return result;
	}
	
	/**
	 * Parses the and get best action.
	 *
	 * @param response the response
	 * @return the string
	 */
	private String parseAndGetBestAction(String response) {
		
		response = response.replace("\n","");
        response = response.replace("\r\n","");
                
        String[] tokens = response.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)", -1);
        ArrayList<Double> probabilities = new ArrayList<Double>();
        ArrayList<String> bestActionStrings = new ArrayList<String>();

        Pattern pattern = Pattern.compile("\"?confidence\\((.*?)\\)\"?.*");
        for (int i = 0;i<tokens.length;i++) {
            Matcher matcher = pattern.matcher(tokens[i]);
            if (matcher.matches()) {
                bestActionStrings.add(matcher.group(1));
            }
            else {
                try {
                    double probability = Double.parseDouble(tokens[i]);
                    logger.info("Prediction percentage is"+probability);
                    probabilities.add(probability);
                } catch(NumberFormatException ex) {
                    logger.info("Exception in parseAndGetBestAction");
                }
            }
        }
        
        String bestAction = "";
        if (bestActionStrings.size() != 0 && probabilities.size() != 0) {
        	bestAction = bestActionStrings.get(probabilities.indexOf(Collections.max(probabilities)));
        }
        
        return bestAction;
	}
}
