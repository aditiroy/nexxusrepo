package com.att.sales.nexxus.dao.model;

import java.io.PrintWriter;
import java.io.Serializable;
import java.io.StringWriter;
import java.time.Instant;
import java.util.Date;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "NX_WEB_SERVICE_ERROR")
@Getter
@Setter
public class NxWebServiceError implements Serializable {
	private static final long serialVersionUID = 1L;
	
	@Id
	@SequenceGenerator(name = "SEQUENCE_NX_WEB_SERVICE_ERROR", sequenceName = "SEQ_NX_WEB_SERVICE_ERROR_ID", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQUENCE_NX_WEB_SERVICE_ERROR")
	@Column(name = "NX_WEB_SERVICE_ERROR_ID")
	private Long nxWebServiceErrorId;
	
	@Column(name = "SERVICE_NAME")
	private String serviceName;
	
	@Column(name = "URL")
	private String url;
	
	@Column(name = "METHOD")
	private String method;
	
	@Column(name = "HEADERS")
	private String headers;
	
	@Column(name = "QUERY_PARAMETERS")
	private String queryParameters;
	
	@Column(name = "REQUEST")
	private String request;
	
	@Column(name = "ERROR_MESSAGE")
	private String errorMessage;
	
	@Column(name = "REQUEST_TIME")
	private Date requestTime;
	
	@Column(name = "ALERT_SENT_TIME")
	private Date alertSentTime;
	
	public NxWebServiceError() {
		//empty constructor
	}
	
	public NxWebServiceError(String serviceName, String request, String ingressUrl, String method, Map<String, String> headers,
			Map<String, Object> queryParameters, Exception e, Instant now) {
		this.serviceName = serviceName;
		this.request = request;
		this.url = ingressUrl;
		this.method = method;
		if (headers != null) {
			this.headers = headers.toString();
		}
		if (queryParameters != null) {
			this.queryParameters = queryParameters.toString();
		}
		if (e != null) {
			StringWriter sw = new StringWriter();
			PrintWriter pw = new PrintWriter(sw);
			e.printStackTrace(pw);
			this.errorMessage = sw.toString();
		}
		this.requestTime = Date.from(now);
	}
}
