package com.att.sales.nexxus.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Authenticator;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.PasswordAuthentication;
import java.net.Proxy;
import java.net.Proxy.Type;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.att.sales.framework.exception.SalesBusinessException;
import com.att.sales.framework.model.ServiceMetaData;
import com.att.sales.nexxus.constant.MessageConstants;
import com.att.sales.nexxus.constant.MyPriceConstants;
import com.att.sales.nexxus.constant.StringConstants;
import com.att.sales.nexxus.service.WebServiceErrorAlertService;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;

/**
 * The Class RestClientUtil.
 *
 * @author SalesRestClient will be used to call BackEnd API using Rest Client utility
 * @param <T>
 */
@Component
public class RestClientUtil {
	
	/** The Constant log. */
	private static final Logger log = LoggerFactory.getLogger(RestClientUtil.class);
	
	//@Autowired
	//private ZipkinWrapper zipkinWrapper;
	
	@Value("${zipkin.enabled:N}")
	private String zipkinTraceEnabled;
	
	@Autowired
	private WebServiceErrorAlertService webServiceErrorAlertService;
	
	
	protected HttpURLConnection openConnection(URL url) throws IOException {
		return (HttpURLConnection) url.openConnection();
	}
	
	protected HttpURLConnection openConnection(URL url, Proxy proxy) throws IOException {
		return (HttpURLConnection) url.openConnection(proxy);
	}
	
	protected BufferedReader getBufferedReaderFromInputStream(InputStream inputStream) {
		BufferedReader br=new BufferedReader(new InputStreamReader((inputStream)));
		return br;
	}
	
	public Object processResult(String response, Class<?> className) {
		com.fasterxml.jackson.databind.ObjectMapper thisMapper = new com.fasterxml.jackson.databind.ObjectMapper();
		thisMapper.configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES, true);
		thisMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		Object object = null;
		try {
			object = thisMapper.readValue(response,
					className);
		} catch (IOException e) {
			log.info("processResult : Error while processing json {}", e.getMessage());
		}
		return object;
	}
	
	public Proxy getProxy(String httpProxyHost,String httpProxyPort,String httpProxyUser,String httpProxyPassword) { 
		Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(httpProxyHost, Integer.parseInt(httpProxyPort)));
		Authenticator authenticator = new Authenticator() {

		    public PasswordAuthentication getPasswordAuthentication() {
			return (new PasswordAuthentication(httpProxyUser, httpProxyPassword.toCharArray()));
		    }
		};
		Authenticator.setDefault(authenticator);
		return proxy; 
	}
	
	
	
	public Map<String, Object> initiateFormDataWebService(String ingressUrl, String method, Map<String, String> headers,
			Map<String, Object> queryParameters, Map<String, String> formField, Map<String, Path> filePart) throws SalesBusinessException {
		final String LINE_FEED = "\r\n";
		final String CHARSET = "UTF-8";
		final String BOUNDARY = "-----" + System.currentTimeMillis();
		log.info("Inside initiateFormDataWebService");
		String tempIngressUrl = ingressUrl;
		log.info("ingressUrl  {}", tempIngressUrl);
		log.info("method {}", method);
		Map<String, Object> result = new HashMap<String, Object>();
		String response = "";
		StringBuilder queryParametersString = new StringBuilder();
		Map<String, Object> metaDataBackUp = ServiceMetaData.getRequestMetaData();
		Proxy proxy=null;
		BufferedReader br = null;
		try {
			String timeoutString = null;
			if (ingressUrl.contains("@")) {
				timeoutString = ingressUrl.substring(ingressUrl.indexOf("@") + 1);
				ingressUrl = ingressUrl.substring(0, ingressUrl.indexOf("@"));
				if (timeoutString != null && timeoutString.contains("=")) {
					timeoutString = timeoutString.substring(timeoutString.indexOf("=") + 1);
				}
			}
			if(queryParameters != null && queryParameters.containsKey(MyPriceConstants.PROXY) && 
					null!=queryParameters.get(MyPriceConstants.PROXY)) { 
				proxy =(Proxy) queryParameters.get(MyPriceConstants.PROXY);
				queryParameters.remove(MyPriceConstants.PROXY);
			}
			if (queryParameters != null && queryParameters.size() > 0) {
					queryParametersString.append('?');
				queryParameters.entrySet().forEach(entry -> {
					queryParametersString.append(entry.getKey());
					queryParametersString.append('=');
					queryParametersString.append(entry.getValue());
					queryParametersString.append('&');
					log.info("queryParameters Key={}, Value={} ", entry.getKey(), entry.getValue());
				});

				queryParametersString.delete(queryParametersString.length() - 1, queryParametersString.length());
				ingressUrl = ingressUrl.concat(queryParametersString.toString());
				String logIngressUrl = ingressUrl;
				log.info("final ingressUrl {}", logIngressUrl);
			}
			updateServiceMetaData(ingressUrl, method, System.currentTimeMillis());
			sendToZipkin("", "", null, true, false);
			
			URL url = new URL(ingressUrl);
			HttpURLConnection conn = null; 
			
			if(null!=proxy) { 
				conn = openConnection(url, proxy); 
			}else { 
				conn = openConnection(url); 
			}
			
			conn.setDoOutput(true);
			conn.setDoInput(true);
			conn.setRequestProperty("Content-Type",
	                "multipart/form-data; boundary=" + BOUNDARY);
			if (timeoutString != null) {
				int timeout = Integer.parseInt(timeoutString);
				conn.setConnectTimeout(timeout);
				conn.setReadTimeout(timeout);
			}
			for(Map.Entry<String, String> entry : headers.entrySet()) {
				if (!StringConstants.REQUEST_CONTENT_TYPE.equals(entry.getKey())) {
					conn.setRequestProperty(entry.getKey(), entry.getValue());
				}
			}
			conn.setRequestMethod(method);
			
			try (OutputStream outputStream = conn.getOutputStream();
					PrintWriter writer = new PrintWriter(new OutputStreamWriter(outputStream, CHARSET), true)) {
				for (Map.Entry<String, String> entry : formField.entrySet()) {
					writer.append("--" + BOUNDARY).append(LINE_FEED);
			        writer.append("Content-Disposition: form-data; name=\"" + entry.getKey() + "\"")
			                .append(LINE_FEED);
			        writer.append("Content-Type: text/plain; charset=" + CHARSET).append(
			                LINE_FEED);
			        writer.append(LINE_FEED);
			        writer.append(entry.getValue()).append(LINE_FEED);
			        writer.flush();
				}
				for(Map.Entry<String, Path> entry : filePart.entrySet()) {
					String fileName = entry.getValue().getFileName().toString();
			        writer.append("--" + BOUNDARY).append(LINE_FEED);
			        writer.append(
			                "Content-Disposition: form-data; name=\"" + entry.getKey()
			                        + "\"; filename=\"" + fileName + "\"")
			                .append(LINE_FEED);
			        writer.append(
			                "Content-Type: "
			                        + URLConnection.guessContentTypeFromName(fileName))
			                .append(LINE_FEED);
			        writer.append(LINE_FEED);
			        writer.flush();
			 
			        try (InputStream inputStream = Files.newInputStream(entry.getValue())) {
			        	byte[] buffer = new byte[4096];
				        int bytesRead = -1;
				        while ((bytesRead = inputStream.read(buffer)) != -1) {
				            outputStream.write(buffer, 0, bytesRead);
				        }
			        }
			        outputStream.flush();
			        writer.append(LINE_FEED);
			        writer.flush();
				}
		        writer.append("--" + BOUNDARY + "--").append(LINE_FEED);
			}

			log.info("RestClientUtil:======>>callMPRestClient():=======>>Response Code :=======>> {}", conn.getResponseCode());
			result.put(MyPriceConstants.RESPONSE_CODE, conn.getResponseCode());
			result.put(MyPriceConstants.RESPONSE_MSG, conn.getResponseMessage());
			String responseMessage = conn.getResponseMessage();
			if (conn.getResponseMessage() != null && !"OK".equalsIgnoreCase(conn.getResponseMessage())
					&& !"Accepted".equalsIgnoreCase(conn.getResponseMessage())) {
				br = getBufferedReaderFromInputStream(conn.getErrorStream());
			} else {
				br = getBufferedReaderFromInputStream(conn.getInputStream());
			}
			String output;
			StringBuilder responseBuilder = new StringBuilder();
			log.debug("Output from Server .... \n");
			while ((output = br.readLine()) != null) {
				//response+= output;
				responseBuilder.append(output);
			}
			response = responseBuilder.toString();

			conn.disconnect();
			if (conn.getResponseMessage() != null && !("OK").equalsIgnoreCase(conn.getResponseMessage())) {
				sendToZipkin("", "", new Exception(conn.getResponseMessage()), false, true);
				//throw new SalesBusinessException(MessageConstants.HTTP_URL_EXCEPTION);
			} else {
				sendToZipkin("", response, null, false, false);
			}

		} catch (MalformedURLException e) {
			log.error("malformed_url_error {}", e);
			sendToZipkin("", "", e, false, true);
			webServiceErrorAlertService.serviceErrorAlert(null, ingressUrl, method, headers, queryParameters, e);
			throw new SalesBusinessException(MessageConstants.HTTP_URL_EXCEPTION);

		} catch (IOException e) {
			log.error("IO_Error {}", e);
			sendToZipkin("", "", e, false, true);
			webServiceErrorAlertService.serviceErrorAlert(null, ingressUrl, method, headers, queryParameters, e);
			throw new SalesBusinessException(MessageConstants.PROCESS_ERROR_CODE);
		} catch (Exception e) {
			log.error("Error while caling Rest End point", e);
			sendToZipkin("", "", e, false, true);
			webServiceErrorAlertService.serviceErrorAlert(null, ingressUrl, method, headers, queryParameters, e);
			throw new SalesBusinessException(MessageConstants.PROCESS_ERROR_CODE);
		}finally{
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		
		log.info("end initiateFormDataWebService");
		setBackServiceMetaData(metaDataBackUp);
		
		result.put(MyPriceConstants.RESPONSE_DATA, response);
		return result;
	}
	
	
	
	protected void updateServiceMetaData(String url, String method, long currentTime) {
		Map<String, Object> newMetaData = new HashMap<>();
		int urlLength = 22;
		if (url.length() > urlLength) {
			url = url.substring(url.length() - urlLength);
		}
		newMetaData.put(ServiceMetaData.METHOD, method);
		newMetaData.put(ServiceMetaData.MS_REQUEST_START_TIME, Long.valueOf(currentTime));
		newMetaData.put(ServiceMetaData.RESTURI, url);
		ServiceMetaData.add(newMetaData);
	}
	
	protected void setBackServiceMetaData(Map<String, Object> metaDataBackUp) {
		if (metaDataBackUp != null) {
			ServiceMetaData.add(metaDataBackUp);
		}
	}

	public void sendToZipkin(Object request, String response, Throwable e, boolean requestFlag, boolean error) {
	//	if (!StringUtils.isEmpty(zipkinTraceEnabled) && "Y".equalsIgnoreCase(zipkinTraceEnabled)) {
			if (error) {
				//zipkinWrapper.sendToZipkin(request, e, requestFlag);
			} else {
				//zipkinWrapper.sendToZipkin(request, response, requestFlag);
			}
		//}
	}
	
	/**
	 * USE THIS METHOD FOR GET CALL WHEN QUERY PARAM HAS JSON STRUCTURE
	 * @param request
	 * @param ingressUrl
	 * @param method
	 * @param headers
	 * @param queryParameters
	 * @param proxyAddress
	 * @return
	 * @throws SalesBusinessException
	 */
	public String callRestApi(String request, String ingressUrl, String method, Map<String, String> headers,
			Map<String, Object> queryParameters, String proxyAddress) throws SalesBusinessException {
		log.info("Inside callRestApi");
		String tempIngressUrl = ingressUrl;
		log.info("ingressUrl {}", org.apache.commons.lang3.StringUtils.normalizeSpace(tempIngressUrl));
		log.info("method {}", method);
		String response = "";
		StringBuilder queryParametersString = new StringBuilder();
		Map<String, Object> metaDataBackUp = ServiceMetaData.getRequestMetaData();
		OutputStream os=null;
		BufferedReader br = null;
		try {
			String timeoutString = null;
			if (ingressUrl.contains("@")) {
				timeoutString = ingressUrl.substring(ingressUrl.indexOf("@") + 1);
				ingressUrl = ingressUrl.substring(0, ingressUrl.indexOf("@"));
				if (timeoutString != null && timeoutString.contains("=")) {
					timeoutString = timeoutString.substring(timeoutString.indexOf("=") + 1);
				}
			}
			
			if (queryParameters != null && queryParameters.size() > 0) {
					queryParametersString.append('?');
				queryParameters.entrySet().forEach(entry -> {
					queryParametersString.append(entry.getKey());
					queryParametersString.append('=');
					queryParametersString.append(entry.getValue());
					queryParametersString.append('&');
					log.info("queryParameters Key={}, Value={} ", entry.getKey(), entry.getValue());
				});

				queryParametersString.delete(queryParametersString.length() - 1, queryParametersString.length());
				ingressUrl = ingressUrl.concat(queryParametersString.toString());
				String logIngressUrl = ingressUrl;
				log.info("final ingressUrl {}", org.apache.commons.lang3.StringUtils.normalizeSpace(logIngressUrl));
			}
			updateServiceMetaData(ingressUrl, method, System.currentTimeMillis());
			sendToZipkin(request, "", null, true, false);
			
			URL url = new URL(ingressUrl);

			HttpURLConnection conn = null; 
			if(proxyAddress != null) {
				String port=proxyAddress.substring(proxyAddress.indexOf(":")+1,proxyAddress.length());
				proxyAddress=proxyAddress.substring(0,proxyAddress.indexOf(":"));
				Proxy proxy = new Proxy(Type.HTTP, new InetSocketAddress(proxyAddress, Integer.valueOf(port))); 
				conn = openConnection(url, proxy); 
			}else {
				conn = openConnection(url); 
			}
			conn.setDoOutput(true);
			conn.setRequestMethod(method);
			if (timeoutString != null) {
				int timeout = Integer.parseInt(timeoutString);
				conn.setConnectTimeout(timeout);
				conn.setReadTimeout(timeout);
			} else {
				conn.setConnectTimeout(180000);
				conn.setReadTimeout(180000);
			}
			for(Map.Entry<String, String> entry : headers.entrySet()) {
					conn.setRequestProperty(entry.getKey(), entry.getValue());
					log.info("headers Key={}, Value={} ", entry.getKey(), entry.getValue());
			}

			if (request != null && request.length() > 0) {
				os = conn.getOutputStream();
				os.write(request.getBytes());
			}
			log.info("RestClientUtil:======>>callMPRestClient():=======>>Response Code :=======>> {}", conn.getResponseCode());
			br = getBufferedReaderFromInputStream(conn.getInputStream());

			String output;
			StringBuilder responseBuilder = new StringBuilder();
			log.debug("Output from Server .... \n");
			while ((output = br.readLine()) != null) {
				//response+= output;
				responseBuilder.append(output);
			}
			response = responseBuilder.toString();
			conn.disconnect();
			if (conn.getResponseMessage() != null && !("OK").equalsIgnoreCase(conn.getResponseMessage())) {
				log.info("code :: {}", org.apache.commons.lang3.StringUtils.normalizeSpace(String.valueOf(conn.getResponseCode())));
				log.info("conn.getResponseMessage {}", org.apache.commons.lang3.StringUtils.normalizeSpace(conn.getResponseMessage()));
				
				sendToZipkin(request, conn.getResponseMessage(), null, false, false);
				throw new SalesBusinessException(MessageConstants.HTTP_URL_EXCEPTION);
			} else {
				sendToZipkin(request, response, null, false, false);
			}

		} catch (MalformedURLException e) {
			log.error("malformed_url_error {}", e);
			sendToZipkin(request, "", e, false, true);
			webServiceErrorAlertService.serviceErrorAlert(request, ingressUrl, method, headers, queryParameters, e);
			throw new SalesBusinessException(MessageConstants.HTTP_URL_EXCEPTION);

		} catch (IOException e) {
			log.error("IO_Error {}", e);
			sendToZipkin(request, "", e, false, true);
			webServiceErrorAlertService.serviceErrorAlert(request, ingressUrl, method, headers, queryParameters, e);
			throw new SalesBusinessException(MessageConstants.PROCESS_ERROR_CODE);
		}finally{
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
			if(null!=os){
				try {
					os.flush();
					os.close();
				} catch (IOException e) {
					log.info("Exception in closing OutputStream {}", e.getMessage(), e);
				}
			}
		}
		log.info("end callRestApi");
		setBackServiceMetaData(metaDataBackUp);
		return response;
	}
}