package com.att.sales.nexxus.userdetails.service;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;

import com.att.sales.framework.model.ServiceResponse;
import com.att.sales.framework.service.BaseServiceImpl;
import com.att.sales.nexxus.common.CommonConstants;
import com.att.sales.nexxus.constant.StringConstants;
import com.att.sales.nexxus.userdetails.model.UserDetailsRequest;
import com.att.sales.nexxus.userdetails.model.UserDetailsResponse;
import com.att.sales.nexxus.util.HttpRestClient;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * The Class UserDetailsServiceImpl.
 */
@Service("UserDetailsServiceImpl")
public class UserDetailsServiceImpl extends BaseServiceImpl implements UserDetailsService{
	
	/** The logger. */
	private static Logger logger = LoggerFactory.getLogger(UserDetailsServiceImpl.class);
	
	/** The env. */
	@Autowired
	private Environment env;
	
	@Autowired
	private HttpRestClient httpRestClient;
	
	@Autowired
	private ObjectMapper mapper;
	
	@Value("${azure.proxy.enabled}")
	private String isProxyEnabled;
	
	/* (non-Javadoc)
	 * @see com.att.sales.nexxus.userdetails.service.UserDetailsService#retreiveUserDetails(com.att.sales.nexxus.userdetails.model.UserDetailsRequest)
	 */
	@Override
	public ServiceResponse retreiveUserDetails(UserDetailsRequest request){
		logger.info("Enetered into retreiveUserDetails Method");
		final String uri = env.getProperty("adopt.user.details.api");
		UserDetailsResponse resp = new UserDetailsResponse();
		request.setLeadDesignId(12324324L);
		try{
			Map<String, String> requestHeaders = new HashMap<>();
			requestHeaders.put(StringConstants.REQUEST_AUTHORIZATION, "Basic "+env.getProperty(CommonConstants.AZURE_MS_AUTHORIZATION));
			com.fasterxml.jackson.databind.ObjectMapper thisMapper = new com.fasterxml.jackson.databind.ObjectMapper();
			thisMapper.configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES, true);
			String requestPayload = mapper.writeValueAsString(request);
			String proxy = null;
			if(StringConstants.CONSTANT_Y.equalsIgnoreCase(isProxyEnabled)) {
				proxy = env.getProperty(CommonConstants.AZURE_HTTP_PROXY);
			}
			String response = httpRestClient.callHttpRestClient(uri,  HttpMethod.POST, null, requestPayload, 
					requestHeaders, proxy);
			
			resp = thisMapper.readValue(response, UserDetailsResponse.class);
			
		}
		catch(Exception e){
			logger.info("Exception" + e);
		}
		
		
		return resp;
		
	}

}
