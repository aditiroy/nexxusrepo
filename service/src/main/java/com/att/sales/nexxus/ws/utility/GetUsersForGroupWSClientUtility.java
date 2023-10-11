package com.att.sales.nexxus.ws.utility;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.att.sales.nexxus.constant.MyPriceConstants;
import com.att.sales.nexxus.constant.StringConstants;

@Component("getUsersForGroupWSClientUtility")
public class GetUsersForGroupWSClientUtility extends SoapWSHandler {

	
	@Value("${myPrice.getUserDetails.url}")
	private String endPointUrl;

	@Value("${myPrice.getUserDetails.contextPath}")
	private String contextPath;

	/** The username. */
	@Value("${myprice.username}")
	private String username;

	/** The password. */
	@Value("${myprice.password}")
	private String password;
	
	@Value("${http.proxyHost}")
	private String httpProxyHost;
	
	@Value("${http.proxyPort}")
	private String httpProxyPort;
	
	@Value("${https.proxyHost}")
	private String httpsProxyHost;
	
	@Value("${https.proxyPort}")
	private String httpsProxyPort;
	
	@Value("${http.proxyUser}")
	private String httpProxyUser;
	
	@Value("${http.proxyPassword}")
	private String httpProxyPassword;
	
	@Value("${http.proxySet}")
	private String httpProxySet;
	
	@Value("${myprice.proxy.enabled}")
	private String isProxyEnabled;

	@Override
	public void init() {
		this.setWsCredentials(endPointUrl, contextPath, username, password);
		if(StringConstants.CONSTANT_Y.equalsIgnoreCase(isProxyEnabled)) {
			this.setProxyDetails(httpProxyHost, httpProxyPort, httpsProxyHost,
					httpsProxyPort, httpProxyUser, httpProxyPassword, httpProxySet);
			this.setProxyRequired(true);
		}
		this.setWsType(MyPriceConstants.CONFIG_WS);
	}

}
