package com.att.sales.nexxus.ws.utility;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.att.sales.nexxus.constant.MyPriceConstants;
import com.att.sales.nexxus.constant.StringConstants;

@Component("getBillingChargesWSClientUtility")
public class GetBillingChargesWSClientUtility extends SoapWSHandler {

	private static final Logger log = LoggerFactory.getLogger(GetBillingChargesWSClientUtility.class);

	@Value("${dw.getBillingChargesInfo.url}")
	private String endPointUrl;

	@Value("${dw.getBillingChargesInfo.contextPath}")
	private String contextPath;

	/** The username. */
	@Value("${dw.getBillingChargesInfo.userName}")
	private String username;

	/** The password. */
	@Value("${dw.getBillingChargesInfo.userPassword}")
	private String password;
	
	@Value("${azure.proxy.enabled}")
	private String isProxyEnabled;
	
	@Value("${azure.http.proxy.host}")
	private String httpProxyHost;
	
	@Value("${azure.http.proxy.port}")
	private String httpProxyPort;

	@Override
	public void init() {
		log.info("Inside init method for GetBillingChargesInfo WebService Client");
		if(StringConstants.CONSTANT_Y.equalsIgnoreCase(isProxyEnabled)) {
			this.setProxyDetails(httpProxyHost, httpProxyPort);
			this.setProxyRequired(true);
		}
		this.setWsCredentials(endPointUrl, contextPath, username, password);
		this.setWsType(MyPriceConstants.BILL_CHRGS_WS);
	}

}
