package com.att.sales.nexxus.ws.utility;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.att.sales.nexxus.constant.MyPriceConstants;
import com.att.sales.nexxus.constant.StringConstants;

@Component("getOptyInfoWSClientUtility")
public class GetOptyInfoWSClientUtility extends SoapWSHandler {
	
	private static final Logger log = LoggerFactory.getLogger(GetOptyInfoWSClientUtility.class);
	
	
	@Value("${rome.getOptyInfo.url}")
	private String endPointUrl;

	
	@Value("${rome.getOptyInfo.contextPath}")
	private String contextPath;
	
	/** The username. */
	@Value("${rome.getOptyInfo.userName}")
	private String username;

	/** The password. */
	@Value("${rome.getOptyInfo.userPassword}")
	private String password;
	
	@Value("${azure.proxy.enabled}")
	private String isProxyEnabled;
	
	@Value("${azure.http.proxy.host}")
	private String httpProxyHost;
	
	@Value("${azure.http.proxy.port}")
	private String httpProxyPort;
	
	@Override
	public void init() {
		log.info("Inside init method for GetoptyInfo WebService Client");
		if(StringConstants.CONSTANT_Y.equalsIgnoreCase(isProxyEnabled)) {
			this.setProxyDetails(httpProxyHost, httpProxyPort);
			this.setProxyRequired(true);
		}
		this.setWsCredentials(endPointUrl, contextPath, username, password);
		this.setWsType(MyPriceConstants.OPTY_INFO_WS);
	}
	
	
	

	
	
}
