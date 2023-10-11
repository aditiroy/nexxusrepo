package com.att.sales.nexxus.ws.utility;

import java.util.Map;

import javax.xml.namespace.QName;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPHeader;
import javax.xml.soap.SOAPMessage;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.stereotype.Component;
import org.springframework.ws.client.core.WebServiceTemplate;
import org.springframework.ws.transport.http.HttpComponentsMessageSender;

import lombok.Getter;
import lombok.Setter;

/**
 * The Class SoapWSHandler.
 */
@Getter
@Setter
@Component
public abstract class SoapWSHandler {
	private static final Logger log = LoggerFactory.getLogger(SoapWSHandler.class);
	private String endPointUrl;
	private String contextPath;
	private String username;
	private String password;
	private String httpProxyHost;
	private String httpProxyPort;
	private String httpsProxyHost;
	private String httpsProxyPort;
	private String httpProxyUser;
	private String httpProxyPassword;
	private String httpProxySet;
	private String wsType;
	
	private boolean isProxyRequired=false;
	private String wsName;
	
	private WebServiceTemplate webServiceTemplate;
	
	public void setWsCredentials(String endPointUrl,String contextPath,String username,
			String password) {
		this.endPointUrl=endPointUrl;
		this.contextPath=contextPath;
		this.username=username;
		this.password=password;
	}
	
	public void setProxyDetails(String httpProxyHost,String httpProxyPort,String httpsProxyHost,
			String httpsProxyPort,String httpProxyUser,String httpProxyPassword,String httpProxySet) {
		this.httpProxyHost=httpProxyHost;
		this.httpProxyPort=httpProxyPort;
		this.httpsProxyHost=httpsProxyHost;
		this.httpsProxyPort=httpsProxyPort;
		this.httpProxyUser=httpProxyUser;
		this.httpProxyPassword=httpProxyPassword;
		this.httpProxySet=httpProxySet;
	}
	
	public void setProxyDetails(String httpProxyHost,String httpProxyPort) {
		this.httpProxyHost=httpProxyHost;
		this.httpProxyPort=httpProxyPort;
	}
	
	//this method to used initialize input credentials for WS
	//its used to call setWsCredentials() and setProxyDetails() from child class
	public abstract void init();

	
	public void handleMessage(SOAPMessage soapMessage, Boolean outboundProperty, Map<String, Object> methodParamMap) {

		SOAPEnvelope envelope = null;
		SOAPHeader header = null;
		try {
			if (outboundProperty.booleanValue()) {
				envelope = soapMessage.getSOAPPart().getEnvelope();
				header = envelope.getHeader();
				if (header == null) {
					envelope.addHeader();
				}

				SOAPElement security = header.addChildElement("Security", "wsse",
						"http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd");
				security.addAttribute(new QName("xmlns:wsu"), "UsernameToken-2");
				SOAPElement usernameToken = security.addChildElement("UsernameToken", "wsse");
				usernameToken.addAttribute(new QName("wsu:Id"),
						"UsernameToken-2");
				SOAPElement usernameElement = usernameToken.addChildElement("Username", "wsse");
				usernameElement.addTextNode(username);
				SOAPElement passwordElement = usernameToken.addChildElement("Password", "wsse");
				passwordElement.setAttribute("Type",
						"http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-username-token-profile-1.0#PasswordText");
				passwordElement.addTextNode(password);

			}
		} catch (SOAPException ex) {
			log.error("Exception occurred during SOAPMessage modification", ex);
		} catch (Exception ex) {
			log.error("Exception occurred during SOAPMessage modification", ex);
			try {
				if (envelope != null) {
					envelope.getHeader().detachNode();
					envelope.addHeader();
				}
			} catch (SOAPException se) {
				log.error("Exception occurred during addition of SOAPHeader", ex);
				ExceptionUtils.getStackTrace(se);
			}
		}
	

	}

	
	public synchronized WebServiceTemplate getwebServiceTemplate() {
        if (webServiceTemplate == null) {
        	webServiceTemplate = new WebServiceTemplate();
            webServiceTemplate.setMarshaller(jaxb2Marshaller());
    		webServiceTemplate.setUnmarshaller(jaxb2Marshaller());
    		webServiceTemplate.setDefaultUri(endPointUrl);
    		
    		if(isProxyRequired) {
    			RequestConfig config = RequestConfig
            	        .custom()
            	        .setProxy(new HttpHost(httpProxyHost,Integer.parseInt(httpProxyPort)))
            	        .build();
            	CredentialsProvider credsProvider = new BasicCredentialsProvider();
    	        credsProvider.setCredentials(
    	                new AuthScope(httpProxyHost,Integer.parseInt(httpProxyPort)),
    	                new UsernamePasswordCredentials(httpProxyUser,httpProxyPassword));
    	        CloseableHttpClient httpclient = HttpClients.custom()
    	                .setDefaultCredentialsProvider(credsProvider).addInterceptorFirst(new HttpComponentsMessageSender.RemoveSoapHeadersInterceptor())
    	                .setDefaultRequestConfig(config).build();
    	    	HttpComponentsMessageSender messageSender = new HttpComponentsMessageSender(httpclient);
    	    	webServiceTemplate.setMessageSender(messageSender);
    		}
        }
        return webServiceTemplate;
    }
	
	public synchronized WebServiceTemplate getwebServiceTemplateOnlyProxy() {
        if (webServiceTemplate == null) {
        	webServiceTemplate = new WebServiceTemplate();
            webServiceTemplate.setMarshaller(jaxb2Marshaller());
    		webServiceTemplate.setUnmarshaller(jaxb2Marshaller());
    		webServiceTemplate.setDefaultUri(endPointUrl);
    		
    		if(isProxyRequired) {
    			RequestConfig config = RequestConfig
            	        .custom()
            	        .setProxy(new HttpHost(httpProxyHost,Integer.parseInt(httpProxyPort)))
            	        .build();
    	        CloseableHttpClient httpclient = HttpClients.custom().addInterceptorFirst(new HttpComponentsMessageSender.RemoveSoapHeadersInterceptor())
    	                .setDefaultRequestConfig(config).build();
    	    	HttpComponentsMessageSender messageSender = new HttpComponentsMessageSender(httpclient);
    	    	webServiceTemplate.setMessageSender(messageSender);
    		}
        }
        return webServiceTemplate;
    }
	
	/**
	 * creating the consumer {@inheritDoc}
	 * 
	 * @return jaxb2Marshaller
	 */
	public Jaxb2Marshaller jaxb2Marshaller() {
		Jaxb2Marshaller jaxb2Marshaller = new Jaxb2Marshaller();
		jaxb2Marshaller.setContextPath(contextPath);
		return jaxb2Marshaller;
	}
	
	
}
