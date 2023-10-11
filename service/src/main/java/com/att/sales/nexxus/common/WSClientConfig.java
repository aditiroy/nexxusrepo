/**
 * 
 *//*
package com.att.sales.nexxus.common;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.ws.client.core.WebServiceTemplate;

*//**
 * The Class WSClientConfig.
 *//*
@Configuration
public class WSClientConfig {

	*//** The opty info end point url. *//*
	@Value("${rome.getOptyInfo.url}")
	private String optyInfoEndPointUrl;

	*//** The opty info context path. *//*
	@Value("${rome.getOptyInfo.contextPath}")
	private String optyInfoContextPath;

	*//**
	 * creating the consumer {@inheritDoc}
	 * 
	 * @return jaxb2Marshaller
	 *//*
	@Bean
	public Jaxb2Marshaller jaxb2Marshaller() {
		Jaxb2Marshaller jaxb2Marshaller = new Jaxb2Marshaller();
		jaxb2Marshaller.setContextPath(optyInfoContextPath);
		return jaxb2Marshaller;
	}

	*//**
	 * Creating webservice template for ROME call.
	 * 
	 * {@inheritDoc}
	 * 
	 * @return webServiceTemplate
	 *//*
	@Bean
	public WebServiceTemplate webServiceTemplate() {
		WebServiceTemplate webServiceTemplate = new WebServiceTemplate();
		webServiceTemplate.setMarshaller(jaxb2Marshaller());
		webServiceTemplate.setUnmarshaller(jaxb2Marshaller());
		webServiceTemplate.setDefaultUri(optyInfoEndPointUrl);
		return webServiceTemplate;
	}

	*//**
	 * Gets the opty info context path.
	 *
	 * @return the opty info context path
	 *//*
	public String getOptyInfoContextPath() {
		return optyInfoContextPath;
	}

	*//**
	 * Sets the opty info context path.
	 *
	 * @param optyInfoContextPath the new opty info context path
	 *//*
	public void setOptyInfoContextPath(String optyInfoContextPath) {
		this.optyInfoContextPath = optyInfoContextPath;
	}

	
}
*/