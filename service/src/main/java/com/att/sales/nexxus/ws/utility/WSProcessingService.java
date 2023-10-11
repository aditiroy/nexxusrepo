package com.att.sales.nexxus.ws.utility;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;

import javax.xml.bind.JAXBElement;
import javax.xml.transform.Source;
import javax.xml.transform.TransformerException;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.text.StringEscapeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.oxm.XmlMappingException;
import org.springframework.stereotype.Component;
import org.springframework.ws.WebServiceMessage;
import org.springframework.ws.client.core.WebServiceMessageCallback;
import org.springframework.ws.soap.SoapFaultDetail;
import org.springframework.ws.soap.SoapFaultDetailElement;
import org.springframework.ws.soap.client.SoapFaultClientException;
import org.springframework.ws.soap.saaj.SaajSoapMessage;

import com.att.abs.ecrm.commonheader.v3.WSException;
import com.att.sales.framework.exception.SalesBusinessException;
import com.att.sales.framework.model.ServiceMetaData;
import com.att.sales.nexxus.constant.MessageConstants;
import com.att.sales.nexxus.constant.MyPriceConstants;
import com.att.sales.nexxus.constant.StringConstants;
import com.att.sales.nexxus.util.JacksonUtil;
import com.oracle.xmlns.cpqcloud.configuration.wireline.solution.solution.Fault;



/**
 * The Class WebServiceClient.
 */
@Component
public class WSProcessingService {
	private static final Logger log = LoggerFactory.getLogger(WSProcessingService.class);
	
	//@Autowired
	//private ZipkinWrapper zipkinWrapper;
	
	@Value("${zipkin.enabled:N}")
	private String zipkinTraceEnabled;
	
	@Value("${log.enabled:N}")
	private String logEnabled;
	
	@Value("${soapWS.retrigger.enabled:N}")
	private String retriggerFlag;
	
	@Value("${soapWS.retrigger.mp.errorCd:N}")
	private String mpRetriggerErrorCd;
	
	@Value("${soapWS.retrigger.max.count:0}")
	private int maxRetriggerCount;
	
	@Value("${azure.proxy.enabled}")
	private String isAzureProxy;
	
	
	
	@SuppressWarnings("unchecked")
	public <T> T  initiateWebService(Object request,SoapWSHandler wsHandler,Map<String, Object> methodParamMap,
			Class<T> responseType) throws WSClientException{
		
		T response=null;
		Long currentTime = System.currentTimeMillis();
		Long startTime=System.currentTimeMillis() - currentTime;
		boolean printStartEndLogger=false;
		
		try {
			Map<String, Object> metaDataBackUp = ServiceMetaData.getRequestMetaData();
			updateServiceMetaData(System.currentTimeMillis());
			//this method to used initialize input credentials for WS
			wsHandler.init();
			if(StringConstants.CONSTANT_Y.equalsIgnoreCase(isAzureProxy)) {
				response=responseType.cast(wsHandler.getwebServiceTemplateOnlyProxy().marshalSendAndReceive(request,new WebServiceMessageCallback() {
					@Override
					public void doWithMessage(WebServiceMessage message) throws IOException, TransformerException {
						wsHandler.handleMessage(((SaajSoapMessage) message).getSaajMessage(),Boolean.valueOf(true),methodParamMap);
					}
				}));
			}else {
				response=responseType.cast(wsHandler.getwebServiceTemplate().marshalSendAndReceive(request,new WebServiceMessageCallback() {
					@Override
					public void doWithMessage(WebServiceMessage message) throws IOException, TransformerException {
						wsHandler.handleMessage(((SaajSoapMessage) message).getSaajMessage(),Boolean.valueOf(true),methodParamMap);
					}
				}));
			}
			this.printStartEndLogs(printStartEndLogger,currentTime,startTime, wsHandler, methodParamMap, null);
			this.sendToZipkin(request,response, null, true,false);
			setBackServiceMetaData(metaDataBackUp);
		}catch (SoapFaultClientException se) {
			//printLogs(request,response);
			log.error("SoapFaultClientException during SOAP WS call: {}", se.getSoapFault().getFaultDetail());
			SoapFaultDetail soapFaultDetail = se.getSoapFault().getFaultDetail(); // <soapFaultDetail> node
		    // if there is no fault soapFaultDetail ...
		    if (soapFaultDetail == null) {
		    	this.printStartEndLogs(printStartEndLogger, currentTime,startTime, wsHandler, methodParamMap, se.getFaultStringOrReason());
		    	this.sendToZipkin(request,response, se, true,true);
		    	throw new WSClientException(MessageConstants.SOAP_CLIENT_PROCESSING_ERROR,se.getFaultCode().toString(), se.getFaultStringOrReason(), se);
		    }else {
		    	this.sendToZipkin(request,response, se, true,true);
		    	SoapFaultDetailElement soapFaultDetailElement = soapFaultDetail.getDetailEntries().next();
		    	Source detailSource = soapFaultDetailElement.getSource(); 
		    	Object detail=null;
				try {
					detail = wsHandler.getwebServiceTemplate().getUnmarshaller().unmarshal(detailSource);
					if(wsHandler.getWsType().equals(MyPriceConstants.CONFIG_WS)) {
						JAXBElement<Fault> source = (JAXBElement<Fault>)detail;
						this.printStartEndLogs(printStartEndLogger,currentTime, startTime, wsHandler, methodParamMap, source.getValue().getExceptionMessage());
				    	throw new WSClientException(MessageConstants.SOAP_CLIENT_PROCESSING_ERROR,source.getValue().getExceptionCode(),source.getValue().getExceptionMessage(), se);
					}else if(wsHandler.getWsType().equals(MyPriceConstants.OPTY_INFO_WS)) {
						JAXBElement<WSException> source = (JAXBElement<WSException>)detail;
						this.printStartEndLogs(printStartEndLogger,currentTime, startTime, wsHandler, methodParamMap, source.getValue().getMessage());
				    	throw new WSClientException(MessageConstants.SOAP_CLIENT_PROCESSING_ERROR,source.getValue().getErrorCode(),source.getValue().getMessage(), se);
					}else {
						this.printStartEndLogs(printStartEndLogger,currentTime, startTime, wsHandler, methodParamMap, se.getFaultStringOrReason());
						throw new WSClientException(MessageConstants.SOAP_CLIENT_PROCESSING_ERROR,se.getFaultCode().toString(), se.getFaultStringOrReason(), se);
					}
					
				} catch (XmlMappingException | IOException e) {
					this.printStartEndLogs(printStartEndLogger,currentTime, startTime, wsHandler, methodParamMap,e.getMessage());
					throw new WSClientException(MessageConstants.SOAP_CLIENT_PROCESSING_ERROR,e.getMessage(), e);
				}
		    	
		    }
		}catch(Exception  e) {
			this.printStartEndLogs(printStartEndLogger,currentTime, startTime, wsHandler, methodParamMap,e.getMessage());
			log.error("Exception during SOAP WS call: {}", e.getMessage());
			if(isRetriggered(e.getMessage(), methodParamMap,wsHandler.getWsName())){
				response = initiateWebService(request, wsHandler, methodParamMap, responseType);
			}else {
				this.sendToZipkin(request,response, e, true,true);
				throw new WSClientException(MessageConstants.SOAP_CLIENT_PROCESSING_ERROR,e.getMessage(), e);
			}
			
		}finally{
			if("Y".equalsIgnoreCase(logEnabled)) {
				printLogs(request,response);
			}
		}
		methodParamMap.remove(MyPriceConstants.SOAP_WS_RETRIGGER_COUNT);
		return response;
	}
	
	protected void printStartEndLogs(boolean printStartEndLogger,Long currentTime,Long startTime, 
			SoapWSHandler wsHandler,Map<String, Object> methodParamMap,String error) {
		Long endTime=0l;
		String idForLogger=getIdForLogger(methodParamMap);
		if(!printStartEndLogger) {
			endTime=System.currentTimeMillis() - currentTime;
			methodParamMap.put("soapWs_ProcessTime_"+wsHandler.getWsName(), endTime - startTime);
			printStartEndLogger=true;
			String totalDuration="";
			if(StringUtils.isNotBlank(error)) {
				totalDuration=new StringBuilder().append("InitiateWebService->> Error : "+error+" >>> "+ wsHandler.getWsName()+ " method took").append(" for Id "+idForLogger+" is : ")
						.append((endTime - startTime)).append(" ").append(MyPriceConstants.MILLISEC).toString();
			}else {
				totalDuration=new StringBuilder().append("InitiateWebService->>Success >>> "+ wsHandler.getWsName()+ " method took").append(" for Id "+idForLogger+" is : ")
						.append((endTime - startTime)).append(" ").append(MyPriceConstants.MILLISEC).toString();
			}
			StringBuffer logTotalDuration = new StringBuffer(totalDuration);
			log.info("totalDuration...........{}",org.apache.commons.lang3.StringUtils.normalizeSpace(logTotalDuration.toString()));
			//			log.info("totalDuration...........{}",totalDuration);

		}
	}
	
	protected boolean isRetriggered(String errorCd,Map<String, Object> methodParamMap,String wsName) {
		if(StringUtils.isNotEmpty(wsName) && (MyPriceConstants.CONFIG_SOL_PRODUCT_WS.equals(wsName)|| MyPriceConstants.CONFIG_DESIGN_WS.equals(wsName)) &&
				"Y".equalsIgnoreCase(retriggerFlag) && (StringUtils.isNotEmpty(errorCd) &&
				(errorCd.contains(mpRetriggerErrorCd) || errorCd.equalsIgnoreCase(mpRetriggerErrorCd)))){
			if(!methodParamMap.containsKey(MyPriceConstants.SOAP_WS_RETRIGGER_COUNT) 
					|| null==methodParamMap.get(MyPriceConstants.SOAP_WS_RETRIGGER_COUNT) ){
				methodParamMap.put(MyPriceConstants.SOAP_WS_RETRIGGER_COUNT, 1);
			}
			int count=(int)methodParamMap.get(MyPriceConstants.SOAP_WS_RETRIGGER_COUNT);
			if(count<=maxRetriggerCount) {
				log.info("SOAP WS retrigger call: {}",count+" "+errorCd);
				count++;
				methodParamMap.put(MyPriceConstants.SOAP_WS_RETRIGGER_COUNT, count);
				return true;
			}
		}	
		return false;
		
	}
	
	
	protected void updateServiceMetaData(long currentTimeMillis) {
		Map<String, Object> newMetaData = new HashMap<>();
		newMetaData.put(ServiceMetaData.MS_REQUEST_START_TIME, Long.valueOf(currentTimeMillis));
		ServiceMetaData.add(newMetaData);
	}
	
	protected void setBackServiceMetaData(Map<String, Object> metaDataBackUp) {
		if (metaDataBackUp != null) {
			ServiceMetaData.add(metaDataBackUp);
		}
	}

	protected void sendToZipkin(Object request,Object response, Throwable e, boolean requestFlag, boolean error) {
		Executors.newCachedThreadPool().execute(new Runnable() {
		    @Override
		    public void run() {
	    		try {
	    			/*if("Y".equalsIgnoreCase(logEnabled)) {
	    				printLogs(request,response);
	    			}*/
					String requestString = getXmlStringForZipkin(request);
					String responseString = getXmlStringForZipkin(response);
					if (!StringUtils.isEmpty(zipkinTraceEnabled) && "Y".equalsIgnoreCase(zipkinTraceEnabled)) {
						if(error) {
						//	zipkinWrapper.sendToZipkin(requestString, e, requestFlag);
						} else {
							//zipkinWrapper.sendToZipkin(requestString, responseString, requestFlag);
						}
					}
				} catch (Exception  e1) {
					log.error("Exception during calling Zipkin log: {}", e.getMessage());
				}
		    }
		});
	}
	
	
	protected String getXmlStringForZipkin(Object input) throws SalesBusinessException {
		String xml=JacksonUtil.toXmlString(input);
		if(StringUtils.isNotEmpty(xml)) {
			return StringEscapeUtils.escapeHtml4(xml);
		}
		return "";
	}
	
	
	protected void printLogs(Object request,Object response) {

		 try {
			 if(null!=request) {
					log.info(JacksonUtil.toXmlString(request));
			 }
			 if(null!=response) {
					log.info(JacksonUtil.toXmlString(response));
			 }
			} catch (SalesBusinessException e) {
				log.error("Exception during printLogs: {}", e.getMessage());
			}
		
	
	}
	
	protected String getIdForLogger(Map<String, Object> methodParamMap) {
		if(methodParamMap.containsKey(MyPriceConstants.MP_TRANSACTION_ID)  
				&& null!=methodParamMap.get(MyPriceConstants.MP_TRANSACTION_ID)) {
			return methodParamMap.get(MyPriceConstants.MP_TRANSACTION_ID).toString();
		}else if(methodParamMap.containsKey("optyId") && null!=methodParamMap.get("optyId")) {
			return methodParamMap.get("optyId").toString();
		}
		return "";
	}
}
