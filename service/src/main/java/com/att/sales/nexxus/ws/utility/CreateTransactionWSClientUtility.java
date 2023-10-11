/*package com.att.sales.nexxus.ws.utility;

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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.stereotype.Component;
import org.springframework.ws.client.core.WebServiceTemplate;
import org.springframework.ws.transport.http.HttpComponentsMessageSender;

import com.att.sales.framework.exception.SalesBusinessException;
import com.oracle.xmlns.cpqcloud.commerce.oraclecpqo_bmclone_2.CreateTransaction;
import com.oracle.xmlns.cpqcloud.commerce.oraclecpqo_bmclone_2.CreateTransactionResponse;
import com.oracle.xmlns.cpqcloud.commerce.oraclecpqo_bmclone_2.CreateTransactionType;
import com.oracle.xmlns.cpqcloud.commerce.oraclecpqo_bmclone_2.ReturnSpecificAttributesType;
import com.oracle.xmlns.cpqcloud.commerce.oraclecpqo_bmclone_2.SimpleAttributesType;
import com.oracle.xmlns.cpqcloud.commerce.oraclecpqo_bmclone_2.SimpleDocumentType;
import com.oracle.xmlns.cpqcloud.commerce.oraclecpqo_bmclone_2.SimpleDocumentsType;

@Component("createTransactionWSClientUtility")
public class CreateTransactionWSClientUtility implements ISoapWSClientUtilityFactory {
	
	private static final Logger log = LoggerFactory.getLogger(CreateTransactionWSClientUtility.class);
	
	
	@Value("${createTransaction.url}")
	private String endPointUrl;

	
	@Value("${createTransaction.contextPath}")
	private String contextPath;
	
	*//** The username. *//*
	@Value("${createTransaction.userName}")
	private String username;

	*//** The password. *//*
	@Value("${createTransaction.userPassword}")
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
	
	private WebServiceTemplate webServiceTemplate;
	
	@Override
	public synchronized WebServiceTemplate getwebServiceTemplate() {
        if (webServiceTemplate == null) {
        	
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
	     	webServiceTemplate = new WebServiceTemplate();
	     	webServiceTemplate.setMessageSender(messageSender);
	        webServiceTemplate.setMarshaller(jaxb2Marshaller());
			webServiceTemplate.setUnmarshaller(jaxb2Marshaller());
			webServiceTemplate.setDefaultUri(endPointUrl);
        }
        return webServiceTemplate;
    }
	*//**
	 * creating the consumer {@inheritDoc}
	 * 
	 * @return jaxb2Marshaller
	 *//*
	public Jaxb2Marshaller jaxb2Marshaller() {
		Jaxb2Marshaller jaxb2Marshaller = new Jaxb2Marshaller();
		jaxb2Marshaller.setContextPath(contextPath);
		return jaxb2Marshaller;
	}


	*//**
	 * This method facilitates in preparing the SOAPMessage which is present in the
	 * context with certain details required for the web-service call.
	 *
	 * @param soapMessage the soap message
	 * @param outboundProperty the outbound property
	 *//*
	public void handleMessage(SOAPMessage soapMessage,Boolean outboundProperty,Map<String, Object> methodParamMap) {
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

	@SuppressWarnings("unchecked")
	@Override
	public <T> T prepareRequest(Map<String, Object> methodParamMap) throws SalesBusinessException{
		T t = null;
		CreateTransaction createTransaction =new CreateTransaction();
		
		SimpleAttributesType attributes = new SimpleAttributesType();
		attributes.getAttribute().add("_document_number");
		
		SimpleDocumentType document = new SimpleDocumentType();
		document.setVarName("transaction");
		document.setAttributes(attributes);
		
		SimpleDocumentsType documents = new SimpleDocumentsType();
		documents.getDocument().add(document);
		
		ReturnSpecificAttributesType attributesType = new ReturnSpecificAttributesType();
		attributesType.setDocuments(documents);
		
		CreateTransactionType transactionType = new CreateTransactionType();
		transactionType.setProcessVarName("oraclecpqo_bmClone_2");
		transactionType.setReturnSpecificAttributes(attributesType);
		
		createTransaction.setTransaction(transactionType);
		t=(T) createTransaction;
		return t;
	}


	@Override
	public <T> void processResonse(Map<String, Object> methodParamMap, T inputResponse)
			 throws SalesBusinessException {
		CreateTransactionResponse response=(CreateTransactionResponse) inputResponse;
	}



	



	@Override
	public Boolean setProxyProperty() {
		return true;
	}

	
}
*/