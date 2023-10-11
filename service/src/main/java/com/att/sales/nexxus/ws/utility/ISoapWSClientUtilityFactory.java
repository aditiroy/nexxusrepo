/*package com.att.sales.nexxus.ws.utility;

import java.util.Map;

import javax.xml.soap.SOAPMessage;

import org.springframework.ws.client.core.WebServiceTemplate;

import com.att.sales.framework.exception.SalesBusinessException;

public interface ISoapWSClientUtilityFactory {
	
	public <T extends Object> T prepareRequest(Map<String, Object> methodParamMap) throws SalesBusinessException;
	public <T> void processResonse(Map<String, Object> methodParamMap, T inputResponse)throws SalesBusinessException;
	public void handleMessage(SOAPMessage soapMessage,Boolean outboundProperty,Map<String, Object> methodParamMap);
	public WebServiceTemplate getwebServiceTemplate();
	public Boolean setProxyProperty();
	

}
*/