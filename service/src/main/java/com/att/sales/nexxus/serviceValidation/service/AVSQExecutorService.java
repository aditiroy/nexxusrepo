/**
 * 
 */
package com.att.sales.nexxus.serviceValidation.service;

import java.util.Map;
import java.util.concurrent.Callable;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.map.annotate.JsonSerialize;

import com.att.sales.framework.model.ServiceMetaData;
import com.att.sales.nexxus.serviceValidation.model.AddressValidationServiceQualificationRequest;
import com.att.sales.nexxus.serviceValidation.model.SiteDetails;
import com.att.sales.nexxus.util.DME2RestClient;
import com.att.sales.nexxus.util.ThreadMetaDataUtil;

import lombok.Getter;
import lombok.Setter;

/**
 * @author ShruthiCJ
 *
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
@Getter
@Setter
public class AVSQExecutorService implements Callable<Object>{

	private AddressValidationServiceQualificationRequest addressValidationServiceQualificationRequest;

	private Map<String, Object> paramMap;
	
	private DME2RestClient dme2RestClient;
	
	private SiteDetails siteDetails;
	
	@Override
	public Object call() throws Exception {
		try {
			Map<String, Object> requestParams = (Map<String, Object>) paramMap.get("requestMetaDataMap");
			requestParams.put(ServiceMetaData.XCONVERSATIONID, addressValidationServiceQualificationRequest.getQualConversationId());
			ThreadMetaDataUtil.initThreadMetaData(requestParams);
			return dme2RestClient.callAVSQRequest(addressValidationServiceQualificationRequest, paramMap);					
		} finally {
			ThreadMetaDataUtil.destroyThreadMetaData();
		}
	}
}
