package com.att.sales.nexus.config;

import javax.xml.namespace.QName;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPHeader;
import javax.xml.soap.SOAPMessage;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * The Class WSClientSoapHandler.
 *
 * @author RudreshWaladaunki
 */
@Component
public class WSClientSoapHandler {

	/** The Constant log. */
	private static final Logger log = LoggerFactory.getLogger(WSClientSoapHandler.class);

	/**
	 * This method facilitates in preparing the SOAPMessage which is present in the
	 * context with certain details required for the web-service call ROME
	 * application.
	 *
	 * @param soapMessage the soap message
	 * @param outboundProperty the outbound property
	 */
	public void handleMessage(SOAPMessage soapMessage, Boolean outboundProperty, String userName, String userPassword) {
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
				SOAPElement usernameToken = security.addChildElement("UsernameToken", "wsse");
				usernameToken.addAttribute(new QName("xmlns:wsu"),
						"http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-utility-1.0.xsd");
				SOAPElement usernameElement = usernameToken.addChildElement("Username", "wsse");
				usernameElement.addTextNode(userName);
				SOAPElement passwordElement = usernameToken.addChildElement("Password", "wsse");
				passwordElement.setAttribute("Type",
						"http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-username-token-profile-1.0#PasswordText");
				passwordElement.addTextNode(userPassword);
				// log.info("SOAPMessage: {}", soapMessage.);

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

}
